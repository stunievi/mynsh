package com.beeasy.fileupload;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.glassfish.gmbal.ParameterNames;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RequestMapping("/service/upload")
@RestController
public class FileUploadController {
    @Value("${uploads.path}")
    private String UPLOAD_PATH;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private Path prepareDir() throws IOException {
        Path path;
        if (!Files.exists(path = Paths.get(UPLOAD_PATH))) {
            Files.createDirectory(path);
        }
        //create
        if (!Files.exists(path = Paths.get(UPLOAD_PATH + File.separator + sdf.format(new Date())))) {
            Files.createDirectory(path);
        }
        return path;
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public String uploadFile(
            @RequestParam("filedata") MultipartFile file) {
        Path path;
        try {
            path = prepareDir();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        //create working dir
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "." + ext;
        File targetFile = new File(path.toAbsolutePath().toString() + File.separator + fileName);
        try (
                InputStream is = file.getInputStream();
                FileOutputStream fos = new FileOutputStream(targetFile);
        ) {
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = is.read(bytes)) > -1) {
                fos.write(bytes, 0, len);
            }
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/uploadFileJSON", method = RequestMethod.POST)
    public String uploadFile(
            @RequestBody JSONObject object
    ) {
        Path path;
        try {
            path = prepareDir();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        String fileName = UUID.randomUUID().toString() + "." + object.getString("ext");
        File targetFile = new File(path.toAbsolutePath().toString() + File.separator + fileName);
        byte[] bytes = object.getBytes("data");
        try (
                OutputStream fos = new FileOutputStream(targetFile);
        ) {
            IOUtils.write(bytes, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/deleteFile", method = RequestMethod.GET)
    public boolean deleteFile(
            @RequestParam String filePath
    ) {
        File baseDir = new File(UPLOAD_PATH);
        File file = new File(filePath);
        //cannot delete file which not in UPLOAD_PATH
        if (!file.getAbsolutePath().contains(baseDir.getAbsolutePath())) {
            return false;
        }
        try {
            Files.delete(file.toPath());
            return Files.exists(file.toPath());
        } catch (IOException e) {
            return false;
        }
    }
}
