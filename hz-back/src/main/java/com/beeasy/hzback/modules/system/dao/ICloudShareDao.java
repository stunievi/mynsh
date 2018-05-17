package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.CloudShare;
import com.beeasy.hzback.modules.system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ICloudShareDao extends JpaRepository<CloudShare,Long>{

    Optional<CloudShare> findFirstByToUserAndFileIndex(User toUser, CloudDirectoryIndex fileIndex);
    Page<CloudShare> findAllByToUser(User toUser, Pageable pageable);
    Page<CloudShare> findAllByToUser_IdOrderByAddTimeDesc(Long toUid, Pageable pageable);

    void deleteAllByToUserAndIdIn(User user, Set<Long> ids);
    List<CloudShare> findAllByToUserIdAndIdIn(long uid, Set<Long> ids);

}
