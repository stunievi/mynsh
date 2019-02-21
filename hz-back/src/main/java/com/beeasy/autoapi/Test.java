package com.beeasy.autoapi;

import com.eclipsesource.v8.V8;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

@RequestMapping("/fuck")
@RestController
public class Test {


//    public static class MemoryClassLoader extends URLClassLoader {
//        public MemoryClassLoader() {
//            super(new URL[0], null);
//        }
//        @Override
//        protected Class<?> findClass(String name) throws ClassNotFoundException {
//            Class clz = findLoadedClass(name);
//            if(!name.startsWith("com.beeasy")){
//                clz = getSystemClassLoader().loadClass(name);
//            }
//            if (clz != null) {
//                return clz;
//            }
//            if(name.endsWith("T") || name.endsWith("R")){
//                File file = new File("E:\\work\\hz\\hznsh\\hz-back\\src\\main\\java\\" + name.replaceAll("\\.", "\\\\") + ".java");
//                String string = IO.readContentAsString(file);
//
//                Map map = compile(file.getName(), string);
//                byte[] buf = compile(file.getName(), string).get(name);
////            IO.readContent(file);//.get(name);
//
//                return defineClass(name, buf, 0, buf.length);
//            }
//            else{
//                return Thread.currentThread().getContextClassLoader().loadClass(name);
//            }
//        }
//
//        @Override
//        public Class<?> loadClass(String name) throws ClassNotFoundException {
//            return findClass(name);
//        }
//
//    }

   static String classPath = System.getProperty("user.dir") + "/temp/class";
    static  String sourcePath = System.getProperty("user.dir")+"/temp/source";

    public static class CustomizationClassLoader extends ClassLoader{

        public CustomizationClassLoader(){
            super(null);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] data = loadByte(name);
                return defineClass(name, data, 0, data.length);
            } catch (Exception e) {
                return CustomizationClassLoader.class.getClassLoader().loadClass(name);
            }
        }

        /**
         * 加载class文件，转换为字节数组
         */
        private byte[] loadByte(String name) throws Exception {
            name = name.replaceAll("\\.", "/");
            FileInputStream fis = new FileInputStream(classPath + "/" + name
                    + ".class");
            int len = fis.available();
            byte[] data = new byte[len];
            fis.read(data);
            fis.close();
            return data;
        }
    }





    public static void compilerJava(File file) throws Exception{
        // 取得当前系统的编译器
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        //获取一个文件管理器
        StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(null, null, null);
        try {
            //文件管理器与文件连接起来
            Iterable it = javaFileManager.getJavaFileObjects(file);
            File dir = new File(classPath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            //创建编译任务
            JavaCompiler.CompilationTask  task = javaCompiler.getTask(null, javaFileManager, null, Arrays.asList("-d", classPath), null, it);
            //执行编译
            task.call();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            javaFileManager.close();
        }
    }

public static int aaa = 1;
    public int bbb = 2;
    public static void main(String[] args) throws Exception{

        V8 runtime = V8.createV8Runtime();
        int result = runtime.executeIntegerScript(""
                + "let hello = 'hello, ';\n"
                + "let world = 'world!';\n"
                + "hello.concat(world).length;\n");
        System.out.println(result);
        runtime.release();

        while(1>0){

            Thread thread = new Thread(() -> {
                aaa = 100;
                Test test = new Test();
                while(true){

                    Thread.currentThread().setContextClassLoader(new CustomizationClassLoader());
                    System.out.println(aaa);
                    try{
                        File file = new File("E:\\work\\hz\\hznsh\\hz-back\\src\\main\\java\\com\\beeasy\\autoapi\\T.java");//+ ".java");
                        compilerJava(file);
                        Class myClass = Thread.currentThread().getContextClassLoader().loadClass("com.beeasy.autoapi.T");
                        Object object = myClass.newInstance();
                        for (Method declaredMethod : myClass.getDeclaredMethods()) {
                            if(declaredMethod.getName().equalsIgnoreCase("gougou")){
                                Class[] clzs = declaredMethod.getParameterTypes();
                                Object[] ags = new Object[clzs.length];
                                for (Class clz : clzs) {
                                    if(clz.getName().equals(Test.class.getName())){
                                        ags[0] = test;
                                    }
                                }
                                System.out.println(declaredMethod.invoke(object, ags));
                            }
                        }
//                        Method method = myClass.getMethod("gougou", Thread.currentThread().getContextClassLoader().loadClass());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
            Thread.sleep(1000000);
        }

//连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
        //ServerAddress()两个参数分别为 服务器地址 和 端口
//        ServerAddress serverAddress = new ServerAddress("47.94.97.138",27017);
//        List<ServerAddress> addrs = new ArrayList<ServerAddress>();
//        addrs.add(serverAddress);
//
//        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
//        MongoCredential credential = MongoCredential.createScramSha1Credential("username", "databaseName", "password".toCharArray());
//        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
//        credentials.add(credential);
//
//        //通过连接认证获取MongoDB连接
//        MongoClient mongoClient = new MongoClient(addrs);
//
//        //连接到数据库
//        MongoDatabase mongoDatabase = mongoClient.getDatabase("databaseName");


//        MongoCollection<Document> collection = mongoDatabase.getCollection("test");
//        Document document = new Document();
//        document.put("key", "val");
//        collection.insertOne(document);

//        collection.find();
//        System.out.println("Connect to database successfully");
    }
}
