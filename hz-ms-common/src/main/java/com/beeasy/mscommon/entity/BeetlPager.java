package com.beeasy.mscommon.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class BeetlPager {
    Integer page = 1;
    Integer size = 10;

    public int getPage() {
        if(null == page){
            page = 1;
        }
        if(page < 1){
            page = 1;
        }
        return page;
    }

    public int getSize() {
        if(null == size){
            size = 10;
        }
        if(size < 1){
            size = 10;
        }
        return size;
    }
}
