package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.util.CrUtils;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.ICloudDirectoryIndexDao;
import com.beeasy.hzback.modules.system.dao.IQuartersDao;
import com.beeasy.hzback.modules.system.dao.IRolePermissionDao;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.RolePermission;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.form.UserAdd;
import com.beeasy.hzback.modules.system.form.UserEdit;
import com.beeasy.hzback.modules.system.form.UserSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
@Transactional
public class UserService implements IUserService {
    @Autowired
    IQuartersDao quartersDao;
    @Autowired
    IUserDao userDao;
    @Autowired
    IRolePermissionDao rolePermissionDao;

    @Autowired
    SystemConfigCache cache;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
//    @Override
//    public boolean bindMenus(long uid, List<String> menus) {
//        return findUser(uid)
//                .flatMap(User::getMenuPermission)
//                .filter(rolePermission -> {
//                    rolePermission.getUnbinds().removeAll(menus);
//                    saveUser(rolePermission.getUser());
//                    return true;
//                }).isPresent();
//    }
//
//    @Override
//    public boolean unbindMenus(long uid, List<String> menus) {
//        return findUser(uid)
//                .flatMap(User::getMenuPermission)
//                .filter(rolePermission -> {
//                    rolePermission.getUnbinds().addAll(menus);
//                    saveUser(rolePermission.getUser());
//                    return true;
//                }).isPresent();
//    }


    private boolean setUnbindMethods(User user, String... menus) {
        return setUnbindMenus(user, new HashSet<>(Arrays.asList(menus)));
    }

    private boolean setUnbindMethods(User user, Set<String> unbindMethods) {
        Map fullMethod = cache.getFullMethodPermission();
        Set<String> unbindItems = new HashSet();
        unbindMethods.forEach(method -> {
            String arr[] = method.split("\\.");
            Map obj = fullMethod;
            try {
                for (String s : arr) {
                    obj = (Map) obj.getOrDefault(s, new HashMap<>());
                }
                if (obj.containsKey("api")) {
                    List<String> list = (List<String>) obj.getOrDefault("api", new ArrayList<>());
                    unbindItems.addAll(list);
                }
            } catch (Exception e) {
            }
        });
        return user.getMethodPermission()
                .filter(menu -> {
                    menu.setUnbinds(unbindMethods);
                    //更新unbindItems
                    menu.setUnbindItems(unbindItems);
                    return true;
                }).isPresent();
    }

    private boolean setUnbindMenus(User user, String... menus) {
        return setUnbindMenus(user, new HashSet<>(Arrays.asList(menus)));
    }

    private boolean setUnbindMenus(User user, Set<String> unbindMenus) {
        return user.getMenuPermission()
                .filter(menu -> {
                    menu.setUnbinds(unbindMenus);
                    return true;
                }).isPresent();
    }


//    @Override
//    public JSONArray getMenus(long uid) {
//        return findUser(uid)
//                .flatMap(user -> rolePermissionDao.findFirstByUserAndType(user,PermissionType.MENU))
//                .map(rolePermission -> {
//                    JSONArray arr = cache.getFullMenu();
//                    rolePermission.getUnbinds().forEach(str -> {
//                        String[] args = str.split("\\.");
//                        JSONArray obj = arr;
//                        try{
//                            for(int i = 0; i < args.length - 1; i++){
//                                String arg = args[i];
//                                Optional<Object> target = obj
//                                        .stream()
//                                        .filter(item -> {
//                                            JSONObject o = (JSONObject) item;
//                                            return o.get("name").equals(arg);
//                                        }).findFirst();
//                                obj = (JSONArray) ((JSONObject) target.get()).get("children");
//                            }
//                            String last = args[args.length - 1];
//                            obj.remove(obj.stream().filter(item -> ((JSONObject)item).get("name").equals(last)).findFirst().get());
//                        }
//                        catch (Exception e){
//                            return;
//                        }
//                    });
//                    return arr;
//                })
//                .orElse(new JSONArray());
//    }

    @Override
    public User createUser(UserAdd add) throws RestException {
        Utils.validate(add);
        User users = userDao.findFirstByUsernameOrPhone(add.getUsername(), add.getPhone());
        if (users != null) {
            throw new RestException("已有相同的用户名或手机号");
        }

        add.setPassword(CrUtils.md5(add.getPassword().getBytes()));
        User u = Transformer.transform(add, User.class);


        //添加permission
        RolePermission rolePermission = new RolePermission();
        rolePermission.setUser(u);
        rolePermission.setType(PermissionType.MENU);
        u.getPermissions().add(rolePermission);

        //添加功能权限
        rolePermission = new RolePermission();
        rolePermission.setUser(u);
        rolePermission.setType(PermissionType.METHOD);
        u.getPermissions().add(rolePermission);

        User ret = userDao.save(u);

        //添加文件夹
        CloudDirectoryIndex cloudDirectoryIndex = new CloudDirectoryIndex();
        cloudDirectoryIndex.setDirName("/");
        cloudDirectoryIndex.setType(ICloudDiskService.DirType.USER);
        cloudDirectoryIndex.setLinkId(u.getId());
        cloudDirectoryIndexDao.save(cloudDirectoryIndex);
//        u.getFolders().add(cloudDirectoryIndex);

        return ret;

    }


    @Override
    public boolean deleteUser(long uid) {
        return findUser(uid)
                .filter(user -> {
                    user.getQuarters().forEach(q -> q.getUsers().remove(user));
                    userDao.delete(user);
                    return true;
                }).isPresent();
    }

    @Override
    public User saveUser(User user) {
        return userDao.save(user);
    }

//    @Override
//    public boolean addQuarters(long uid, long... qids){
//        return findUser(uid)
//                .filter(user -> {
//                    List<Quarters> qs = quartersDao.findAllByIdIn(qids);
//                    qs.forEach(q -> q.getUsers().add(user));
//                    saveUser(user);
//                    return true;
//                }).isPresent();
//    }
//
//    @Override
//    public boolean removeQuarters(long uid, long... qids){
//        return findUser(uid)
//                .filter(user -> {
//                    List<Quarters> qs = quartersDao.findAllByIdIn(qids);
//                    qs.forEach(q -> q.getUsers().remove(user));
//                    saveUser(user);
//                    return true;
//                }).isPresent();
//    }
//
//
//    @Override
//    public User setQuarters(long uid, long... qids) throws CannotFindEntityException {
//        User user = findUserE(uid);
//        List<Quarters> qs = quartersDao.findAllByIdIn(qids);
//        qs.forEach(q -> q.getUsers().add(user));
//        return saveUser(user);
//    }


    private boolean setQuarters(User user, Set<Long> qids) {
//        userDao.deleteUserQuarters(user.getId());
        //解除关联
        user.getQuarters().forEach(q -> {
            q.getUsers().remove(user);
        });
        //重新关联
        quartersDao.findAllByIdIn(qids).forEach(q -> {
            q.getUsers().add(user);
        });
        return true;
    }

    public Page<User> searchUser(UserSearch search, Pageable pageable) {
        Specification query = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(search.getName())) {
                    predicates.add(
                            cb.or(
                                    cb.like(root.get("username"), search.getName()),
                                    cb.like(root.get("trueName"), search.getName())
                            )
                    );
                }
                if (null != search.getBaned()) {
                    predicates.add(cb.equal(root.get("baned"), search.getBaned()));
                }
                if (null != search.getQuarters()) {
                    predicates.add(root.join("quarters").in(search.getQuarters()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        return userDao.findAll(query, pageable);
    }

    public boolean editUser(UserEdit edit) throws RestException {
        Utils.validate(edit);
        String errerMsg = findUser(edit.getId())
                .map(user -> {
                    if (!StringUtils.isEmpty(edit.getPhone())) {
                        Optional<User> sameUser = userDao.findFirstByPhone(edit.getPhone());
                        if (sameUser.isPresent() && !sameUser.get().getId().equals(user.getId())) {
                            return "已经有相同的手机号";
                        }
                        user.setPhone(edit.getPhone());
                    }
                    if (!StringUtils.isEmpty(edit.getEmail())) {
                        user.setEmail(edit.getEmail());
                    }

                    if (!StringUtils.isEmpty(edit.getPassword())) {
                        user.setPassword(edit.getPassword());
                    }
                    //是否禁用
                    if (edit.getBaned() != null) {
                        user.setBaned(edit.getBaned());
                    }
                    //真实姓名
                    if (!StringUtils.isEmpty(edit.getTrueName())) {
                        user.setTrueName(edit.getTrueName());
                    }
                    //岗位设置
                    if (null != edit.getQuarters()) {
                        setQuarters(user, edit.getQuarters());
                    }
                    //菜单权限
                    if (null != edit.getUnbindMenus()) {
                        setUnbindMenus(user, edit.getUnbindMenus());
                    }
                    //禁用功能
                    if (null != edit.getUnbindMethods()) {
                        setUnbindMethods(user, edit.getUnbindMethods());
                    }
                    saveUser(user);
                    return "";
                }).orElse("");
        if (!StringUtils.isEmpty(errerMsg)) {
            throw new RestException(errerMsg);
        }
        return true;
    }

    @Override
    public Optional<User> findUser(long id) {
        return Optional.ofNullable(userDao.findOne(id));
    }

    public Optional<Quarters> findQuarters(long id){
        return Optional.ofNullable(quartersDao.findOne(id));
    }

    public List<User> findUserByIds(Set<Long> ids){
        return userDao.findAllByIdIn(ids);
    }
    public List<User> findUserByIds(Long ...ids){
        return findUserByIds(new HashSet<Long>(Arrays.asList(ids)));
    }

    @Override
    public User findUserE(long id) throws CannotFindEntityException {
        return findUser(id).orElseThrow(() -> new CannotFindEntityException(User.class, id));
    }

}
