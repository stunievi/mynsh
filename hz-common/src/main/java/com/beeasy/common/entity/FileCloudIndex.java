//package com.beeasy.common.entity;
//
//import com.beeasy.common.helper.AbstractBaseEntity;
//import com.beeasy.hzback.modules.system.service.ICloudDiskService;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.Entity;
//import javax.persistence.Table;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "t_file_cloud_index")
//public class FileCloudIndex extends AbstractBaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    @Enumerated
//    ICloudDiskService.DirType type;
//    Long linkId;
//    Long fileId;
//
//    boolean dir;
//}
