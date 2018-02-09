package com.beeasy.hzback.modules.setting.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.config.AppConfig;
import com.beeasy.hzback.core.helper.BaseEntity;
import com.beeasy.hzback.modules.setting.work_engine.BaseWorkNode;
import com.beeasy.hzback.modules.setting.work_engine.ShenheNode;
import com.beeasy.hzback.modules.setting.work_engine.ZiliaoNode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_work")
public class Work extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotEmpty(message = "流程名不能为空")
    private String name;

    @NotEmpty(message = "流程说明不能为空")
    private String info;


//    @Column(columnDefinition = "long varchar")
//    @Lob
    @Column(columnDefinition = "BLOB")
    @Convert(converter = ByteConverter.class)
    private List<BaseWorkNode> nodeList;

    public static class ByteConverter implements AttributeConverter<List<BaseWorkNode>,byte[]>{

        @Override
        public byte[] convertToDatabaseColumn(List<BaseWorkNode> nodes) {
            return JSON.toJSONString(nodes).getBytes();
        }


        @Override
        public List<BaseWorkNode> convertToEntityAttribute(byte[] s) {
            return JSON.parseObject(s,List.class);

//            if(jsonObject == null){
//                return null;
//            }
//            if(jsonObject.getString("type") == "ziliao"){
//                return JSON.parseObject(s, ZiliaoNode.class);
//            }
//            else{
//                return JSON.parseObject(s, ShenheNode.class);
//            }
        }
    }

    public static class NodeConverter implements AttributeConverter<List<BaseWorkNode>,String>{

        @Override
        public String convertToDatabaseColumn(List<BaseWorkNode> nodes) {
            return JSON.toJSONString(nodes);
        }


        @Override
        public List<BaseWorkNode> convertToEntityAttribute(String s) {
            return JSON.parseObject(s,List.class);

//            if(jsonObject == null){
//                return null;
//            }
//            if(jsonObject.getString("type") == "ziliao"){
//                return JSON.parseObject(s, ZiliaoNode.class);
//            }
//            else{
//                return JSON.parseObject(s, ShenheNode.class);
//            }
        }
    }

    }
