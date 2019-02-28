package com.beeasy.hzcloud.control;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzcloud.entity.CFile;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.tool.Document;
import com.beeasy.tool.ESClient;
import com.beeasy.tool.FileUtil;
import com.beeasy.tool.SearchParameter;
import org.beetl.sql.core.SQLManager;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.IO;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RequestMapping("/api/cfile")
@Controller
@Transactional
public class CFileController {

    @Value("${uploads.path}")
    String UPLOAD_PATH;

    @Autowired
    SQLManager sqlManager;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final Executor executor = Executors.newFixedThreadPool(10);

    ESClient es = new ESClient("http://47.94.97.138/es", "aaa", "document");


    @RequestMapping("/upload")
    @ResponseBody
    public Result upload(
        @RequestParam MultipartFile file,
        @RequestParam long pid,
        Long uid
    ) {
        // FIXME: 2019/2/28 此处为安全隐患，不可以把UID当做参数
        if (uid == null) {
            uid = AuthFilter.getUid();
        }
//        long uid = AuthFilter.getUid();
        String err = "上传失败";
        File dir = new File(UPLOAD_PATH, sdf.format(new Date()));
        if(!dir.exists()) dir.mkdirs();
        String ext = S.fileExtension(file.getOriginalFilename());
        if(S.notEmpty(ext)) ext = "." + ext;
        else ext = "";
        File target = new File(dir, S.uuid() + ext);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestException(err);
        }
        CFile cfile = new CFile();
        cfile.setUid(uid);
        cfile.setCreator(uid);
        cfile.setType(CFile.Type.FILE);
        cfile.setName(file.getOriginalFilename());
        cfile.setPid(pid);
        cfile.setLastModify(new Date());
        cfile.setSize(target.length());
        cfile.setPath(target.getAbsolutePath());
        ext = ext.replace(".", "");
        cfile.setExt(ext.toLowerCase());
        sqlManager.insert(cfile, true);

        if($.isNotNull(cfile.getId())){
            //es检索
            Long finalUid = uid;
            executor.execute(() -> {
                List<String> mayBeDocs = C.newList("txt","pdf","doc","xls","docx","xlsx");
                String content = "";
                if(mayBeDocs.contains(cfile.getExt())){
                    content = FileUtil.readFile(cfile.getPath());
                }
                Document document = new Document();
                document.setFid(cfile.getId());
                document.setUid(finalUid);
                document.setTitle(cfile.getName());
                document.setContent(content);
                document.setCreateTime(new Date());
                document.setModifyTime(new Date());

//                try{
//                    //删除文档
//                    es.deleteDocument(document.getFid());
//                }
//                catch (IOException e){
//                    e.printStackTrace();
//                }
                try{
                    es.updateDocument(document);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            });
        }

        return Result.ok(cfile);
    }


    @RequestMapping("/download")
    public void download(
        @RequestParam long id,
        HttpServletRequest request,
        HttpServletResponse response
    ){
        CFile file = sqlManager.single(CFile.class, id);
        if (file == null) {
            return;
        }
        ServletContext context = request.getServletContext();
        String mimeType = context.getMimeType(file.getPath());
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
            System.out.println("context getMimeType is null");
        }
        response.setContentType(mimeType);
        response.setContentLength((int) file.getSize());
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
            file.getName());
        response.setHeader(headerKey, headerValue);
        try(
            InputStream is = new FileInputStream(file.getPath());
            OutputStream os = response.getOutputStream();
            ) {
            IO.copy(is, os);
            response.flushBuffer();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/search")
    @ResponseBody
    public Result search(
        String keyword,
        Integer page,
        Integer size
    ) throws IOException {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        if(S.empty(keyword)){
            keyword = "";
        }
        SearchParameter parameter = new SearchParameter();
        parameter.setKeyword(keyword);
        parameter.setUid(AuthFilter.getUid());
        parameter.setPage(page);
        parameter.setSize(size);
        JSONObject ret = es.search(parameter);
        return Result.ok(ret);
    }

}
