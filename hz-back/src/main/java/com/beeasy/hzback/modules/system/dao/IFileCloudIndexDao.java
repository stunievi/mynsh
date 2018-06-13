package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.FileCloudIndex;
import com.beeasy.hzback.modules.system.service.ICloudDiskService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IFileCloudIndexDao extends JpaRepository<FileCloudIndex,Long>{
    int countByTypeAndLinkIdAndFileId(ICloudDiskService.DirType type, long uid, long id);
    void deleteByTypeAndLinkIdAndFileId(ICloudDiskService.DirType type, long uid, long id);
    void deleteByTypeAndLinkIdAndFileIdIn(ICloudDiskService.DirType type, long uid, List<Long> ids);
}
