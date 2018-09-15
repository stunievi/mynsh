import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * 文件变化监听器
 *
 * 在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下：
 * 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
 * 如果有文件的变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）
 *
 *
 */
public class DevFileWatcher extends FileAlterationListenerAdaptor {
//    private Logger log = Logger.getLogger(FileListener.class);
    /**
     * 文件创建执行
     */
    public void onFileCreate(File file) {
        try {
            System.out.println(IOUtils.readLines(new FileReader(file)).stream().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        reload();
//        log.info("[新建]:" + file.getAbsolutePath());
    }

    /**
     * 文件创建修改
     */
    public void onFileChange(File file) {
        try {
            System.out.println(IOUtils.readLines(new FileReader(file)).stream().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        reload();
//        log.info("[修改]:" + file.getAbsolutePath());
    }

    /**
     * 文件删除
     */
    public void onFileDelete(File file) {
        reload();
//        log.info("[删除]:" + file.getAbsolutePath());
    }

    /**
     * 目录创建
     */
    public void onDirectoryCreate(File directory) {
        reload();
//        log.info("[新建]:" + directory.getAbsolutePath());
    }

    /**
     * 目录修改
     */
    public void onDirectoryChange(File directory) {
        reload();
//        log.info("[修改]:" + directory.getAbsolutePath());
    }

    /**
     * 目录删除
     */
    public void onDirectoryDelete(File directory) {
        reload();
//        log.info("[删除]:" + directory.getAbsolutePath());
    }

//    public void onStart(FileAlterationObserver observer) {
//        // TODO Auto-generated method stub
//        super.onStart(observer);
//    }
//
//    public void onStop(FileAlterationObserver observer) {
//        // TODO Auto-generated method stub
//        super.onStop(observer);
//    }


    private static Timer timer;

    public static void start(String dir) throws Exception {
        final Path path = Paths.get(dir);

        FileAlterationMonitor monitor = new FileAlterationMonitor(50);
        FileAlterationObserver observer = new FileAlterationObserver(new File(dir));
        observer.addListener(new DevFileWatcher());
        monitor.addObserver(observer);
        monitor.start();

        if(true){
            return;
        }
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            //给path路径加上文件观察服务
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            while (true) {
                final WatchKey key = watchService.take();

                for (WatchEvent<?> watchEvent : key.pollEvents()) {

                    final WatchEvent.Kind<?> kind = watchEvent.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    //创建事件
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("[新建]");
                        reload();
                    }
                    //修改事件
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("修改]");
                        reload();
                    }
                    //删除事件
                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("[删除]");
                        reload();
                    }
                    // get the filename for the event
                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                    final Path filename = watchEventPath.context();
                    // print it out
                    System.out.println(kind + " ->333 " + filename);

                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }

        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
        }
    }

    public static void reload(){
        if(null != timer){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("reload");
            }
        },50);
    }
}
