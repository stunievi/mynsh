package com.beeasy.hzback.modules.cloud;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.cloud.config.FeignConfig;
import com.beeasy.hzback.modules.cloud.response.CloudBaseResponse;
import com.beeasy.hzback.modules.cloud.response.CreateDirResponse;
import com.beeasy.hzback.modules.cloud.response.GetFilesResponse;
import com.beeasy.hzback.modules.cloud.response.LoginResponse;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudService {

    @Value("${filecloud.username}")
    private String cloudUserName;
    @Value("${filecloud.password}")
    private String cloudPassword;

    //    @Autowired
    private CloudApi cloudApi;
    @Autowired
    private CloudAdminApi cloudAdminApi;
    @Autowired
    private SystemConfigCache systemConfigCache;

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String ERROR_MESSAGE = "文件云接口请求失败";

    public boolean login(String username, String password) {
        LoginResponse loginResponse = cloudApi.login(username, password);
        if (null != loginResponse) {
            if (loginResponse.getStatus().equals(STATUS_SUCCESS)) {
                FeignConfig.cookies.put(Utils.getCurrentUserId(), loginResponse.getResponseCookies().get(0));
                return true;
            }
        }
        return false;
    }

    public boolean checkOnline() {
        CloudBaseResponse response = cloudApi.checkOnline();
        if (null != response) {
            return response.getStatus().equals(STATUS_SUCCESS);
        }
        return false;
    }

    public Result<CreateDirResponse> createUserDir(long pid, String name) {
        CreateDirResponse response = cloudApi.createUserDir(pid, name);
        return checkResponse(response);
    }

    public Result getFiles(long pid) {
        GetFilesResponse response = cloudApi.getFiles(pid);
        return checkResponse(response);
    }

    public Result deleteFiles(long id) {
        return checkResponse(cloudApi.deleteFiles(id));
    }

    public Result deleteFiles(List<Long> ids) {
        List<String> list = ids.stream()
                .filter(id -> id > 0)
                .map(id -> id + "").collect(Collectors.toList());
        if (ids.size() == 0) return Result.error(ERROR_MESSAGE);
        return checkResponse(cloudApi.deleteFiles(String.join(",", list)));
    }

    public Result renameFile(long id, String name) {
        return checkResponse(cloudApi.renameFile(id, name));
    }

    public Result editTags(long id, List<String> tags) {
        String tag = tags.size() > 0 ? String.join(",", tags) : "";
        return checkResponse(cloudApi.editTags(id, tag));
    }

    public Result<CloudBaseResponse> uploadFiles(long pid, MultipartFile file) {
        //将file名字修复为filedata
//        file.getName()
//        String str = cloudApi.uploadFiles(pid,file);
//        return null;
        return checkResponse(cloudApi.uploadFiles(pid, file));
    }

    public Result fileSearch(String keyword) {
        return checkResponse(cloudApi.fileSearch(keyword));
    }

    private Result checkResponse(CloudBaseResponse response) {
        if (null != response) {
            if (response.getStatus().equals(STATUS_SUCCESS)) {
                return Result.ok(response);
            } else {
                return Result.error(response.getMsg());
            }
        }
        return Result.error(ERROR_MESSAGE);
    }


    public Result loginAdmin() {
        CloudBaseResponse response = cloudAdminApi.checkOnline();
        if (null == response || !response.getStatus().equals(STATUS_SUCCESS)) {
            LoginResponse loginResponse = cloudAdminApi.login(cloudUserName, cloudPassword);
            if (null != loginResponse && loginResponse.getStatus().equals(STATUS_SUCCESS)) {
                CloudAdminApi.Config.setCookie(loginResponse.getResponseCookies().get(0));
            }
            return checkResponse(loginResponse);
        }
        return Result.ok();
    }

//    @Deprecated
//    public Result adminCreateUser(String username){
//        Result result = loginAdmin();
//        if(result.isSuccess()){
//            try {
//                Map map = systemConfigCache.getCreateUserString();
//                map.put("user.username",username);
//                cloudAdminApi.adminCreateUser(map);
//                return Result.ok();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return Result.error();
//    }

    public Result createUser(String username) {
        Result result = loginAdmin();
        if (result.isSuccess()) {
            CloudBaseResponse response = cloudAdminApi.createUser(username);
            return checkResponse(response);
        }
        return Result.error();
    }


//    public CloudService(Decoder decoder, Encoder encoder, Client client){
//        this.cloudApi = Feign.builder().client(client)
//                .encoder(encoder)
//                .decoder(new Decoder() {
//                    @Override
//                    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
//                        return null;
//                    }
//                })
////                .requestInterceptor(new BasicAuthRequestInterceptor("user","password"))
//                .target(CloudApi.class,"http://192.168.31.55");
//    }

}
