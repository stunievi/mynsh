package com.beeasy.hzdata;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "http://localhost/api/fuck")
public interface Test {


}
