package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICloudDirectoryIndexDao extends JpaRepository<CloudDirectoryIndex,Long>{

//    Optional<CloudDirectoryIndex> findFirstByUserAndParentAndFolderName(User user, CloudDirectoryIndex parent, String folderName);
//    Optional<CloudDirectoryIndex> findFirstByTypeAndLinkIdAndDirPath(ICloudDiskService.DirType type, long linkId, String dirPath);
Optional<CloudDirectoryIndex> findFirstByTypeAndLinkIdAndParent(ICloudDiskService.DirType type, long linkId, CloudDirectoryIndex parent);
    Optional<CloudDirectoryIndex> findFirstByTypeAndLinkIdAndParentAndDirName(ICloudDiskService.DirType type, long linkId, CloudDirectoryIndex parent,String dirName);

    Optional<CloudDirectoryIndex> findFirstByTypeAndLinkIdAndId(ICloudDiskService.DirType type, long linkId, long id);
    Optional<CloudDirectoryIndex> findFirstByTypeAndLinkIdAndDirName(ICloudDiskService.DirType type, long linkId, String dirName);
    List<CloudDirectoryIndex> findAllByTypeAndLinkId(ICloudDiskService.DirType type, long linkId);
    List<CloudDirectoryIndex> findAllByTypeAndLinkIdAndParent(ICloudDiskService.DirType type, long linkId, CloudDirectoryIndex parent);

    List<CloudDirectoryIndex> findAllByParent(CloudDirectoryIndex parent);

    void deleteByTypeAndLinkIdAndId(ICloudDiskService.DirType type, long linkId, long id);
}
