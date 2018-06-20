package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.IGlobalPermissionCenterDao;
import com.beeasy.hzback.modules.system.entity.GlobalPermission;
import com.beeasy.hzback.modules.system.entity.GlobalPermissionCenter;
import com.beeasy.hzback.modules.system.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
public class GlobalPermissionService {
    @Autowired
    UserService userService;
    @Autowired
    IGlobalPermissionCenterDao centerDao;

    @Async(value = "center_executor")
    public void syncGlobalPermissionCenterAdded(GlobalPermission globalPermission){
        List<Long> uids = null;
        centerDao.deleteAllByPermissionIdIn(Collections.singletonList(globalPermission.getId()));
        switch (globalPermission.getUserType()){
            //按部门授权
            case DEPARTMENT:
                uids = userService.getUidsFromDepartment(globalPermission.getLinkId());
                break;

            //按用户授权
            case USER:
                GlobalPermissionCenter center = new GlobalPermissionCenter() ;
                center.setUserId(globalPermission.getLinkId());
                center.setPermissionId(globalPermission.getId());
                centerDao.save(center);
                break;

            //按岗位授权
            case QUARTER:
                uids = userService.getUidsFromQuarters(globalPermission.getLinkId());
                break;
        }
        if(null != uids){
            for (Long uid : uids) {
                GlobalPermissionCenter center = new GlobalPermissionCenter();
                center.setUserId(uid);
                center.setPermissionId(globalPermission.getId());
                centerDao.save(center);
            }
        }
    }

    @Async(value = "center_executor")
    public void syncGlobalPermissionCenterDeleted(Long ...gpids){
        centerDao.deleteAllByPermissionIdIn(Arrays.asList(gpids));
    }


    @Async(value = "center_executor")
    public void syncGlobalPermissionCenterQuartersChanged(List<Long> oldqids, User user){
//        List<Long> newQids = userService.getQidsFromUser(user.getId());
        Set<GlobalPermission> globalPermissionSet = new HashSet<>();
        //删除旧关联
        for (GlobalPermissionCenter center : user.getGpCenters()) {
            //如果是个人授权, 不用管
            if(center.getPermission().getUserType().equals(GlobalPermission.UserType.USER)){
                continue;
            }
            globalPermissionSet.add(center.getPermission());
        }
        //重新生成缓存
        for (GlobalPermission globalPermission : globalPermissionSet) {
            syncGlobalPermissionCenterAdded(globalPermission);
        }
    }

    public boolean checkPermission(GlobalPermission.Type type, long objectId, long uid){
        return  centerDao.checkPermission(type,objectId,uid) > 0;
    }
}
