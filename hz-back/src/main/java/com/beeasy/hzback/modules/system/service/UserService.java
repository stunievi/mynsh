package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.IMenuPermissionDao;
import com.beeasy.hzback.modules.system.entity.MenuPermission;
import com.beeasy.hzback.modules.system.form.UserAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements IUserService {
    @Autowired
    IUserDao userDao;
    @Autowired
    IMenuPermissionDao menuPermissionDao;

    @Autowired
    SystemConfigCache cache;

    @Autowired
    EntityManager entityManager;

    @Override
    public boolean bindMenus(User user, List<String> menus) {
        user.getMenuPermission().getUnbinds().removeAll(menus);
        menuPermissionDao.save(user.getMenuPermission());
        return true;
    }

    @Override
    public boolean unbindMenus(User user, List<String> menus) {
        user.getMenuPermission().getUnbinds().addAll(menus);
        menuPermissionDao.save(user.getMenuPermission());
        return true;
    }

    @Override
    public JSONArray getMenus(User user) {
        try {
            JSONArray arr = cache.getFullMenu();
            user.getMenuPermission().getUnbinds().forEach(str -> {
                String[] args = str.split("\\.");
                JSONArray obj = arr;
                try{
                    for(int i = 0; i < args.length - 1; i++){
                        String arg = args[i];
                        Optional<Object> target = obj
                                .stream()
                                .filter(item -> {
                                    JSONObject o = (JSONObject) item;
                                    return o.get("name").equals(arg);
                                }).findFirst();
                        obj = (JSONArray) ((JSONObject) target.get()).get("children");
                    }
                    String last = args[args.length - 1];
                    obj.remove(obj.stream().filter(item -> ((JSONObject)item).get("name").equals(last)).findFirst().get());
                }
                catch (Exception e){
                    return;
                }

            });
            return arr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User createUser(UserAdd add) throws RestException {
        User users = userDao.findFirstByUsernameOrPhone(add.getUsername(),add.getPhone());
        if(users != null){
            throw new RestException("已有相同的用户名或手机号");
        }
        User u = Transformer.transform(add,User.class);
        User ret = userDao.save(u);
        //创建menu permission
        MenuPermission menuPermission = new MenuPermission();
        menuPermission.setUser(ret);
        menuPermissionDao.save(menuPermission);
        return ret;
    }

    @Override
    public boolean deleteUser(User user){
        userDao.delete(user);
        return true;
    }



}
