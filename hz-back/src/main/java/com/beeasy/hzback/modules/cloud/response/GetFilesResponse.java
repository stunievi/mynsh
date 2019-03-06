package com.beeasy.hzback.modules.cloud.response;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetFilesResponse extends CloudBaseResponse {
    JSONArray rows;
    Date data;
    long total;
}
