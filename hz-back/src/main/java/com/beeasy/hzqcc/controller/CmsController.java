package com.beeasy.hzqcc.controller;

import com.beeasy.mscommon.RestException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/api/qcc/cms")
public class CmsController {

    /**
     * 下载图片（兼容企查查图谱图片下载）
     * @param filename 图片名
     * @param img {img: "base64字符码"}
     * @param response
     * @throws IOException
     */
    @RequestMapping("/downloadimg")
    public void downLoadBase64Image(
        @RequestParam("filename") String filename,
        String img,
        HttpServletResponse response
    ) throws IOException {
        String str = "data:image/jpeg;base64";
        String pattern = "image/(jpeg|png|jpe|gif);";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        if(m.find()){
            String mimeType = m.group(1);
            switch (mimeType.toLowerCase()) {
                case "jpg":
                case "jpeg":
                case "bmp":
                    response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                    break;
                case "png":
                    response.setContentType(MediaType.IMAGE_PNG_VALUE);
                    break;
                case "gif":
                    response.setContentType(MediaType.IMAGE_GIF_VALUE);
                    break;
                default:
                    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            }
            try {
                filename = URLEncoder.encode(filename, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RestException("下载失败");
            }
            img = img.substring(img.indexOf(",") + 1, img.length());
            byte[] bytes = Base64.getDecoder().decode(img);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"; filename*=utf-8''" + filename);
            try(OutputStream os = response.getOutputStream()){
                os.write(bytes);
                os.flush();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
