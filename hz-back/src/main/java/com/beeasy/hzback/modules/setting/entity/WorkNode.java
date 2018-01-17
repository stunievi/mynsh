package com.beeasy.hzback.modules.setting.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.beetl.ext.fn.Json;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@Entity
@Table(name = "t_work_node")
public class WorkNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @NotEmpty(message = "节点设置错误")
//    @Lob
    @Column(columnDefinition = "TEXT")
    @Convert(converter = NodeConverter.class)
    private List<Node> node;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    @NotNull(message = "节点位置不能为空")
    private Integer position;


    static ObjectMapper objectMapper = new ObjectMapper();

    public static class Node{
        public String test;
    }

    public static class NodeConverter implements AttributeConverter<List<Node>,String>{

        @Override
        public String convertToDatabaseColumn(List<Node> nodes) {
            try {
                return objectMapper.writeValueAsString(nodes);
            } catch (JsonProcessingException e) {
                return "";
//                e.printStackTrace();
            }
        }

        @Override
        public List<Node> convertToEntityAttribute(String s) {
            try {
                return objectMapper.readValue(s,List.class);
            } catch (IOException e) {
                return null;
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Node> getNode() {
        return node;
    }

    public void setNode(List<Node> node) {
        this.node = node;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
