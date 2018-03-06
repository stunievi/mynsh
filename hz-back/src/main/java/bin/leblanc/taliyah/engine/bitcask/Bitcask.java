package bin.leblanc.taliyah.engine.bitcask;

import bin.leblanc.taliyah.util.Serializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class Bitcask {
    private long activeFileId = -1;

    @Getter
    private BitcaskOptions options;
    private Map<String, BitcaskIndex> indexMap = new ConcurrentHashMap<>();
    private static Map<Long, BitcaskDataFile> dataFiles = new ConcurrentHashMap<>();
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
                    "glob:**" + options.getDataExtension());

            Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path path,
                                                 BasicFileAttributes attrs) throws IOException {
                    if (pathMatcher.matches(path)) {
                        String name = path.getFileName().toString().replace(getOptions().getDataExtension(), "");
                        long fileId = Long.valueOf(name);
                        //做成单例，保证一个文件永远只有一个相同的引用
                        if (!dataFiles.containsKey(fileId)) {
                            BitcaskDataFile file = new BitcaskDataFile(Bitcask.this, false, fileId);
                            dataFiles.put(file.getFileId(), file);
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

        if (dataFiles.size() == 0) {
            BitcaskDataFile file = new BitcaskDataFile(this, true);
            dataFiles.put(file.getFileId(), file);
            activeFileId = file.getFileId();
        } else {
            //获得编号最大的
            List<Long> fileIds = dataFiles.keySet().stream().collect(Collectors.toList());
            fileIds.sort(new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return (int) (o2 - o1);
                }
            });
            activeFileId = fileIds.get(0);
            getActiveFile().setActive(true);
        }
    }

    private BitcaskDataFile getActiveFile() {
        BitcaskDataFile file = dataFiles.get(activeFileId);
        if (file.getFileSize() < options.getMaxFileSize()) {
            return file;
        }

        //如果超出了文件限制
        //锁定旧文件
        file.setActive(false);
        //生成新的活动文件
        file = new BitcaskDataFile(this, true);
        activeFileId = file.getFileId();
        dataFiles.put(file.getFileId(), file);
        return file;
    }

    public void put(String key, Object value) {
        byte[] valueBytes = Serializer.serialize(new BitcaskValue(value));
        BitcaskIndex index = getActiveFile().write(key.getBytes(), valueBytes);
        if (index != null) {
            indexMap.put(key, index);
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
        paths.remove(options.getDirPath());
    }

}
