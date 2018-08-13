//package com.beeasy.hzback.modules.system.dao;
//
//import com.beeasy.common.entity.CloudDirectoryIndex;
//import com.beeasy.common.entity.CloudFileIndex;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface ICloudFileIndexDao extends JpaRepository<CloudFileIndex,Long>{
////    Optional<CloudFileIndex> findFirstByUserAndFileName(User user, String fileName);
//    Optional<CloudFileIndex> findFirstByDirectoryIndexAndFileName(CloudDirectoryIndex directoryIndex, String fileName);
//    List<CloudFileIndex> findAllByDirectoryIndex(CloudDirectoryIndex directoryIndex);
////    List<CloudFileIndex> findAllByUserAndFileNameLike(User user,String fileName);
////    void deleteAllByUserAndFileNameLike(User user, String fileName);
//
////    @Query("update CloudFileIndex set virtualPath = :newPath where user = :user and virtualPath like :path")
//}
