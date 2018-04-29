package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserProfileDao extends JpaRepository<UserProfile,Long>{
}
