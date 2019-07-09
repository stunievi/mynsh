package com.beeasy.hzback.modules.system.service;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.entity.WfIns;
import com.beeasy.hzback.entity.WfInsAttr;
import com.beeasy.hzback.entity.WfNodeDealer;
import org.apache.activemq.command.ActiveMQTopic;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Async
@Service
@Transactional
public class TaskSyncService {

    @Autowired
    SQLManager sqlManager;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 同步任务
     *
     * @param wfIns
     */
    public void sync(WfIns wfIns) {
        //查询余下的贷款
        List<JSONObject> list = sqlManager.execute(new SQLReady(String.format("SELECT loan_account, bill_no from RPT_M_RPT_SLS_ACCT where cont_no = (select cont_no from RPT_M_RPT_SLS_ACCT where loan_account = '%s')", wfIns.getLoanAccount())), JSONObject.class);
        long id = wfIns.getId();
        for (JSONObject jsonObject : list) {
            String la = jsonObject.getString("loanAccount");
            String bo = jsonObject.getString("billNo");
            if (la.equalsIgnoreCase(wfIns.getLoanAccount())) {
                continue;
            }
            wfIns.setId(null);
            wfIns.setLoanAccount(la);
            wfIns.setBillNo(bo);
            //主体
            sqlManager.insert(wfIns, true);
            long nid = wfIns.getId();
            //属性
            List<WfInsAttr> attrs = sqlManager.lambdaQuery(WfInsAttr.class)
                    .andEq(WfInsAttr::getInsId, id)
                    .select()
                    .stream()
                    .peek(e -> {
                        e.setId(null);
                        e.setInsId(nid);
                        if (e.getAttrKey().equalsIgnoreCase("LOAN_ACCOUNT")) {
                            e.setAttrValue(la);
                        }
                        if (e.getAttrKey().equalsIgnoreCase("BILL_NO")) {
                            e.setAttrValue(bo);
                        }
                    })
                    .collect(Collectors.toList());
            sqlManager.insertBatch(WfInsAttr.class, attrs);
            //处理人
            List<WfNodeDealer> dealers = sqlManager.lambdaQuery(WfNodeDealer.class)
                    .andEq(WfNodeDealer::getInsId, id)
                    .select()
                    .stream()
                    .peek(e -> {
                        e.setId(null);
                        e.setInsId(nid);
                    })
                    .collect(Collectors.toList());
            sqlManager.insertBatch(WfNodeDealer.class, dealers);
        }

    }


    /**
     * 将数据同步到数据池
     */
    public void syncDataToDataPool(String table) throws IOException {
//        String[] tables = {"T_RELATED_PARTY_LIST", "T_GROUP_CUS_LIST", "T_SHARE_HOLDER_LIST", "T_LOAN_RELEATED_SEARCH"};
//        for (String table : tables) {
        int size = 200;
        String sql = "SELECT * FROM (Select rownumber() over(ORDER BY id ASC) AS rn, t.* from %s t) AS a1 WHERE a1.rn between %d and %d";
        int start = 0;
        int end = 0;
        List<File> files = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            File temp = File.createTempFile(RandomUtil.randomString(3), RandomUtil.randomString(3));
            start = (i) * size + 1;
            end = (i + 1) * size;
            List<JSONObject> list = sqlManager.execute(new SQLReady(String.format(sql, table ,start, end)), JSONObject.class);
            if(list.size() == 0){
                break;
            }
            try (
                    RandomAccessFile raf = new RandomAccessFile(temp, "rw");
            ) {
                raf.write(ObjectUtil.serialize(list));
            }
            files.add(temp);
        }
        String[] paths = new String[files.size()];
        InputStream[] iss = new InputStream[files.size()];
        File zfile = File.createTempFile(RandomUtil.randomString(3), RandomUtil.randomString(3));
        try {
            int i = 0;
            for (File file : files) {
                paths[i] = file.getName();
                iss[i] = new FileInputStream(file);
                i++;
            }
            ZipUtil.zip(zfile, paths, iss);

            ActiveMQTopic topic = new ActiveMQTopic("data-sync");
            Properties properties = new Properties();
            properties.setProperty("table", table);
            topic.setProperties(properties);
            jmsMessagingTemplate.convertAndSend(topic, zfile);

        } finally {
            for (InputStream inputStream : iss) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (File file : files) {
                file.delete();
            }
            if (zfile != null) {
                zfile.delete();
            }
        }


    }

}
