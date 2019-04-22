package com.beeasy.easyshop.core;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;

import static com.beeasy.easyshop.core.Config.config;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyClassLoadader extends ClassLoader {
    private static ClassLoader defaultClassLoader = MyClassLoadader.class.getClassLoader();
    /**
     * @param filename
     * @return Byte[]
     * @throws IOException
     */
    private byte[] getBytes(String filename) throws IOException {
        File file = new File(filename);
        byte raw[] = new byte[(int) file.length()];
        FileInputStream fin = new FileInputStream(file);
        fin.read(raw);
        fin.close();
        return raw;
    }

    public Object FindNewClass(String className) {
        try {
            byte[] b = getBytes(config.watch + "/" + className.replaceAll("\\.", "/") + ".class");
            return defineClass(null, b, 0, b.length);
        } catch (Exception e) {
//			e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return findClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        boolean hotswap = false;
        if (config.hotswap != null) {
            for (String s : config.hotswap) {
                if(name.startsWith(s)) {
                    hotswap = true;
                    break;
                }
            }
        }

        if(!hotswap){
            return defaultClassLoader.loadClass(name);
        }
        ClassPathResource resource = new ClassPathResource(name.replaceAll("\\.", "/") + ".class");
        try (
            InputStream is = resource.getStream();
            ){
            byte[] b = IoUtil.readBytes(is);
            return defineClass(null, b, 0, b.length);
        } catch (Exception e) {
            return super.findClass(name);
        }
    }
}
