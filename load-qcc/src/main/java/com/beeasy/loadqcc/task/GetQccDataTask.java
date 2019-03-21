package com.beeasy.loadqcc.task;

import com.beeasy.loadqcc.service.GetQccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetQccDataTask {

    @Autowired
    GetQccService getQccService;

    @Scheduled(cron="0 0 0 */14 * ?")
    public void getData(){

//        Map companyInfo = new HashMap<String, String>();
//        companyInfo.put("companyName", "11111111");
//        List<Map<String, String>> companyList = new ArrayList<Map<String, String>>();
//        companyList.add(companyInfo);
//
//        for(Map<String, String> company : companyList){
//            String companyName = company.get("companyName");
//            getQccService.ECI_GetBasicDetailsByName(companyName);
//        }

    }

}
