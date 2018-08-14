//package com.beeasy.hzback.modules.system.dao;
//
//import com.beeasy.common.entity.CloudDirectoryIndex;
//import com.beeasy.common.entity.CloudFileTag;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface ICloudFileTagDao extends JpaRepository<CloudFileTag,Long> {
//
//    void deleteAllByIndex(CloudDirectoryIndex index);
//    void deleteByIndexAndTag(CloudDirectoryIndex index, String tag);
//    List<CloudFileTag> findAllByIndex_Id(Long id);
//    Optional<CloudFileTag> findFirstByIndexAndTag(CloudDirectoryIndex index, String tag);
//
//}
