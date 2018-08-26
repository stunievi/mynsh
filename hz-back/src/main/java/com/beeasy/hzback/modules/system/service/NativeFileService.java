package com.beeasy.hzback.modules.system.service;

import com.beeasy.common.entity.SystemFile;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.system.dao.ISystemFileDao;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class NativeFileService {

    @Value("${uploads.type}")
    String UPLOAD_TYPE;

    @Value("${uploads.path}")
    private String UPLOAD_PATH;
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ISystemFileDao systemFileDao;

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

    public SystemFile uploadFile(SystemFile.Type type, MultipartFile file) {
        String finalPath = "";
        SystemFile.StorageDriver driver = SystemFile.StorageDriver.valueOf(UPLOAD_TYPE.toUpperCase());
        switch (driver) {
            case NATIVE:
                finalPath = uploadNativeFile(file);
            case FASTDFS:
                finalPath = uploadFastdfsFile(file);
        }
        if (finalPath.isEmpty()) {
            throw new RestException("文件保存失败");
        }
        SystemFile systemFile = new SystemFile();
        systemFile.setExt(FilenameUtils.getExtension(file.getOriginalFilename()));
        systemFile.setFilePath(finalPath);
        systemFile.setStorageDriver(driver);
        systemFile.setFileName(file.getOriginalFilename());
        return systemFileDao.save(systemFile);
    }

    public byte[] downloadFile(final SystemFile.StorageDriver driver, final String path) {
        switch (driver) {
            case NATIVE:
                Path p = Paths.get(path);
                if (!Files.exists(p)) {
                    break;
                }
                if (Files.isDirectory(p)) {
                    break;
                }
                try {
                    return Files.readAllBytes(p);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            case FASTDFS:
                if (!path.startsWith("/group")) {
                    break;
                }
                Pattern pattern = Pattern.compile("/(group\\d+)/(\\.+)$");
                Matcher matcher = pattern.matcher(path);
                if (!matcher.find()) {
                    break;
                }
                return storageClient.downloadFile(matcher.group(0), matcher.group(1), inputStream -> {
                    try {
                        return IOUtils.toByteArray(inputStream);
                    } catch (IOException e) {
                        return new byte[0];
                    }
                });
        }
        return new byte[0];
    }

    public void deleteFile(final SystemFile.StorageDriver driver, final String path) {
        switch (driver) {
            case NATIVE:
                Path p = Paths.get(path);
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case FASTDFS:
                storageClient.deleteFile(path);
                break;
        }
    }

    public void delete(final SystemFile systemFile) {
        if (null != systemFile.getId()) {
            systemFileDao.delete(systemFile);
            deleteFile(systemFile.getStorageDriver(), systemFile.getFilePath());
        }
    }


    /**
     * upload a native file
     * needs glusterfs support
     *
     * @param file
     * @return
     */
    private String uploadNativeFile(MultipartFile file) {
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

    private String uploadFastdfsFile(MultipartFile file) {
        String fileType = FilenameUtils.getExtension(file.getOriginalFilename().toLowerCase());
        StorePath path = null;

        try {
            path = storageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, null);
//            storageClient.downloadFile(path.getGroup(), path.getPath(), inputStream -> {
//                List<String> lines = IOUtils.readLines(inputStream);
//                System.out.print(StringUtils.join(lines.toArray(), ""));
//                return lines;
//            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(path)
                .map(StorePath::getFullPath)
                .orElse("");
    }


}
