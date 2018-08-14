package bin.leblanc.taliyah.engine.bitcask;

import bin.leblanc.taliyah.util.Serializer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class Bitcask {
    @Setter
    private long activeDataFileId = -1;
    @Setter
    private long activeHintFileId = -1;

    @Getter
    private BitcaskOptions options;
    private Map<String, BitcaskIndex> indexMap = new ConcurrentHashMap<>();
    @Getter
    private static Map<Long, BitcaskDataFile> dataFiles = new ConcurrentHashMap<>();
    @Getter
    private static Map<Long, BitcaskHintFile> hintFiles = new ConcurrentHashMap<>();
    private static Set<String> paths = new HashSet<>();

    public Bitcask(BitcaskOptions options) {
        this.options = options;
        open();
    }

    public void open() {
        try {
            //初始化目录
            Path directoryPath = Paths.get(options.getDirPath());
            if (!Files.exists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
            if (!Files.isDirectory(directoryPath)) {
                throw new IOException();
            }
            if (paths.contains(options.getDirPath())) {
                throw new IOException();
            }
            paths.add(options.getDirPath());

            final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(
                    "glob:**");

            Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path path,
                                                 BasicFileAttributes attrs) throws IOException {
                    if (pathMatcher.matches(path)) {
                        String fileName = path.getFileName().toString();
                        //数据文件
                        if (fileName.endsWith(options.getDataExtension())) {
                            String name = fileName.replace(getOptions().getDataExtension(), "");
                            long fileId = Long.valueOf(name);
                            //做成单例，保证一个文件永远只有一个相同的引用
                            if (!dataFiles.containsKey(fileId)) {
                                new BitcaskDataFile(Bitcask.this, false, fileId);
                            }
                        }

                        //hint文件
                        else if (fileName.endsWith(options.getHintExtension())) {
                            String name = fileName.replace(options.getHintExtension(), "");
                            long fileId = Long.valueOf(name);
                            if (!hintFiles.containsKey(fileId)) {
                                new BitcaskHintFile(Bitcask.this, false, fileId);
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc)
                        throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        //处理数据文件
        if (dataFiles.size() == 0) {
            new BitcaskDataFile(this, true);
        } else {
            //获得编号最大的
            List<Long> fileIds = dataFiles.keySet().stream().collect(Collectors.toList());
            fileIds.sort(new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return (int) (o2 - o1);
                }
            });
            dataFiles.get(fileIds.get(0)).setActive(true);
        }

        //处理索引文件
        if (hintFiles.size() == 0) {
            new BitcaskHintFile(this, true);
        } else {
            List<Long> fileIds = hintFiles.keySet().stream().collect(Collectors.toList());
            fileIds.sort(new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return (int) (o2 - o1);
                }
            });
            hintFiles.get(fileIds.get(0)).setActive(true);

            //重建索引
            int len = hintFiles.size();
            BitcaskIndex index = null;
            while (len-- > 0) {
                BitcaskHintFile file = hintFiles.get(fileIds.get(len));
                while ((index = file.readNext()) != null) {
                    String key = new String(index.getKey());
                    if (!indexMap.containsKey(key) || indexMap.get(key).getTimestamp() < index.getTimestamp()) {
                        indexMap.put(key, index);
                    }
                }
            }

        }
    }

    private BitcaskDataFile getActiveDataFile() {
        BitcaskDataFile file = dataFiles.get(activeDataFileId);
        if (file.getFileSize() < options.getMaxFileSize()) {
            return file;
        }

        //如果超出了文件限制
        //锁定旧文件
        file.setActive(false);
        //生成新的活动文件
        return new BitcaskDataFile(this, true);
    }

    private BitcaskHintFile getActiveHintFile() {
        BitcaskHintFile file = hintFiles.get(activeHintFileId);
        if (file.getFileSize() < options.getMaxFileSize()) {
            return file;
        }

        file.setActive(false);
        return new BitcaskHintFile(this, true);
    }

    public void put(String key, Object value) {
        byte[] valueBytes = Serializer.serialize(new BitcaskValue(value));
        BitcaskIndex index = getActiveDataFile().write(key.getBytes(), valueBytes);
        if (index != null) {
            indexMap.put(key, index);
            //写入索引
            getActiveHintFile().write(index);
        }
    }


    public Optional<Object> get(String key) {
        BitcaskIndex index = indexMap.get(key);
        if (index == null) return Optional.empty();
        BitcaskDataFile file = dataFiles.get(index.getFileId());
        if (file == null) return Optional.empty();
        return Optional.ofNullable(file.read(index));
    }


    public void close() {
        indexMap.clear();
        dataFiles.forEach((id, file) -> {
            try {
                file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dataFiles.clear();
        hintFiles.forEach((id, file) -> {
            try {
                file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hintFiles.clear();
        paths.remove(options.getDirPath());
    }

}
