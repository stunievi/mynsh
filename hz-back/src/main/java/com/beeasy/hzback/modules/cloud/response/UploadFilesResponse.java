package com.beeasy.hzback.modules.cloud.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFilesResponse extends CloudBaseResponse {
    long id;
    String filename;
}
