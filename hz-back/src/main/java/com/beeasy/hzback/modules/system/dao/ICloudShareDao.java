package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.CloudFileIndex;
import com.beeasy.hzback.modules.system.entity.CloudShare;
import com.beeasy.hzback.modules.system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ICloudShareDao extends JpaRepository<CloudShare,Long>{

    Optional<CloudShare> findFirstByToUserAndFileIndex(User toUser, CloudFileIndex fileIndex);
    Page<CloudShare> findAllByToUser(User toUser, Pageable pageable);

    void deleteAllByToUserAndIdIn(User user, Set<Long> ids);
    List<CloudShare> findAllByToUserIdAndIdIn(long uid, Set<Long> ids);


}
