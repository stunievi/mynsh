package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.modules.system.service.LinkSeachService;
import com.beeasy.mscommon.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/")
public class LinkController {
    @Autowired
    LinkSeachService linkSeachService;

    /**
     * 股东清单
     */
    @RequestMapping(value = "search/link/{no}", method = RequestMethod.GET)
    public Result linkSeach(@PathVariable String no,
                            @RequestParam Map<String, Object> params){


        return Result.ok(linkSeachService.gdSeach(no,params));
    }

    /**
     * 关联方清单
      */
//    @RequestMapping(value = "search/link/rParty/{no}", method = RequestMethod.GET)
//    public Result partySeach(@PathVariable String no,
//                            @RequestParam Map<String, Object> params){
//
//
//        return Result.ok(linkSeachService.gdSeach(no,params));
//    }
}
