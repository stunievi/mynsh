
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class DevClassLoader extends ClassLoader{

    public static void scanClasses(String packageName){
        Thread t = new Thread() {
            @Override
            public void run() {
                List<Class<?>> classes = ClassUtil.getClasses(packageName);
                HttpServerHandler.classList = classes;
            }
        };
        t.setContextClassLoader(new DevClassLoader());
        t.start();
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        String classPath = "/Users/bin/work/hzcp/bin";
        String classPath = DevClassLoader.class.getResource("/").getPath(); //得到classpath
        String fileName = name.replace(".", "/") + ".class" ;
        File classFile = new File(classPath , fileName);
        if(!classFile.exists()){
            throw new ClassNotFoundException(classFile.getPath() + " 不存在") ;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        ByteBuffer bf = ByteBuffer.allocate(1024) ;
        try {
            FileInputStream fis = new FileInputStream(classFile);
            FileChannel fc = fis.getChannel() ;
            while(fc.read(bf) > 0){
                bf.flip() ;
                bos.write(bf.array(),0 , bf.limit()) ;
                bf.clear() ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = bos.toByteArray();
        return defineClass(bytes, 0, bytes.length);
    }

}
