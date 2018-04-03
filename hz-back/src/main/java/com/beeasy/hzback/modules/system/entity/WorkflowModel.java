package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.helper.ObjectConverter;
import com.beeasy.hzback.modules.system.node.BaseNode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "t_workflow_model")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowModel {
    @Id
    @GeneratedValue
    Long id;

    @Column(columnDefinition = "BLOB")
    @Convert(converter = ObjectConverter.class)
    Map<String,BaseNode> model;

    String name;

    BigDecimal version;

    String info;

    boolean open;

    boolean firstOpen;

    @CreatedDate
    Date addTime;


    @OneToMany(mappedBy = "workflowModel",cascade = CascadeType.ALL)
    List<WorkflowModelPersons> persons = new ArrayList<>();



//    @Getter
//    @Setter
//    public static class Persons implements Serializable{
//        private static final long serialVersionUID = 1L;
//        private LinkedHashSet<Integer> mainQuarters = new LinkedHashSet<>();
//        private LinkedHashSet<Integer> mainUser = new LinkedHashSet<>();
//        private LinkedHashSet<Integer> supportQuarters = new LinkedHashSet<>();
//        private LinkedHashSet<Integer> supportUser = new LinkedHashSet<>();
//    }
}
