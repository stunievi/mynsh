package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.CloudDirectoryIndex;
import com.beeasy.common.entity.CloudShare;
import com.beeasy.common.entity.User;
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
