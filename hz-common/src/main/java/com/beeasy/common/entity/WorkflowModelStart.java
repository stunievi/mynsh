package com.beeasy.common.entity;//package com.beeasy.hzback.modules.system.entity;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import javax.persistence.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "t_workflow_model_start")
//public class WorkflowModelStart {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    @OneToOne(optional = false)
//    WorkflowModel workflowModel;
//
//    @OneToMany(mappedBy = "start")
//    List<Quarters> quarters = new ArrayList<>();
//
//    @Getter
//    @Setter
//    @Entity
//    @Table(name = "t_workflow_model_start_quarters")
//    public static class Quarters{
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        Long id;
//
//        @ManyToOne(optional = false)
//        WorkflowModelStart start;
//
//        Long qid;
//
//    }
//
//    public interface Dao extends JpaRepository<WorkflowModelStart,Long>{
//
//    }
//
//    public interface QuartersDao extends JpaRepository<Quarters,Long>{
//
//    }
//}
