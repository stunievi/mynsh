package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
import bin.leblanc.maho.RPCMapping;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.ChineseToEnglish;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.core.util.CrUtils;
import com.beeasy.hzback.modules.cloud.CloudApi;
import com.beeasy.hzback.modules.cloud.CloudService;
import com.beeasy.hzback.modules.cloud.response.LoginResponse;
import com.beeasy.hzback.modules.exception.CannotFindEntityException;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.*;
import jdk.nashorn.internal.objects.Global;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserService implements IUserService {

    @Value("${filecloud.userDefaultPassword}")
    String cloudUserPassword;

    @Value("${filecloud.commonUsername}")
    String cloudCommonUsername;
    @Value("${filecloud.commonPassword}")
    String cloudCommonPassword;

    @Autowired
    IUserAllowApiDao userAllowApiDao;
    @Autowired
    IGlobalPermissionDao globalPermissionDao;
    @Autowired
    IGlobalPermissionCenterDao centerDao;

    @Autowired
    GlobalPermissionService globalPermissionService;
    @Autowired
    CloudApi cloudApi;
    @Autowired
    IUserExternalPermissionDao externalPermissionDao;
    @Autowired
    ISystemFileDao systemFileDao;
    @Autowired
    IQuartersDao quartersDao;
    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    IUserDao userDao;
    @Autowired
    IRolePermissionDao rolePermissionDao;
    @Autowired
    IUserProfileDao userProfileDao;

    @Autowired
    SystemConfigCache cache;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
    @Autowired
    CloudService cloudService;
//    
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
//    
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


//    
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

    
    public Result<User> createUser(UserAdd add){
        Result result = Utils.validate(add);
        if(!result.isSuccess()){
            return result;
        }
        User users = userDao.findFirstByUsernameOrPhone(add.getUsername(), add.getPhone());
        if (users != null) {
            return Result.error("已有相同的用户名或手机号");
        }

        add.setPassword(CrUtils.md5(add.getPassword().getBytes()));
        User u = Transformer.transform(add, User.class);

        //profile
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(u);
        SystemFile systemFile = new SystemFile();
        try {
            ClassPathResource resource = new ClassPathResource("static/default_face.jpg");
            byte[] bytes = IOUtils.toByteArray(resource.getInputStream());
            systemFile.setBytes((bytes));
            systemFile.setType(SystemFile.Type.FACE);
            systemFile.setExt("jpg");
            systemFile.setFileName("default_face.jpg");
            systemFile = systemFileDao.save(systemFile);
            if(systemFile.getId() == null){
                throw new IOException();
            }
            userProfile.setFaceId(systemFile.getId());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.error("保存头像失败");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //创建文件云账号
        Result r = cloudService.createUser(u.getUsername());
        if(r.isSuccess()){
            userProfile.setCloudUsername(u.getUsername());
            userProfile.setCloudPassword(cloudUserPassword);
        }

        userProfileDao.save(userProfile);

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

        initLetter(u);
        User ret = userDao.save(u);

        //添加文件夹
//        CloudDirectoryIndex cloudDirectoryIndex = new CloudDirectoryIndex();
//        cloudDirectoryIndex.setDirName("/");
//        cloudDirectoryIndex.setType(ICloudDiskService.DirType.USER);
//        cloudDirectoryIndex.setLinkId(u.getId());
//        cloudDirectoryIndexDao.save(cloudDirectoryIndex);
//        u.getFolders().add(cloudDirectoryIndex);

        return Result.ok(ret);
    }


    /**
     * 删除用户
     * @param uid
     * @return
     */
    public boolean deleteUser(long uid) {
        return findUser(uid)
                .filter(user -> {
                    user.getQuarters().forEach(q -> q.getUsers().remove(user));
                    userDao.delete(user);
                    return true;
                }).isPresent();
    }

    /**
     * 批量删除部门(软删除)
     * @param dids
     * @return
     */
    public Result deleteDepartments(Long ...dids){
        //如果部门下还有岗位, 那么不能删除
        for (Long did : dids) {
            if(quartersDao.countByDepartment_Id(did) > 0){
                return Result.error("该部门下仍有岗位, 无法删除");
            }
        }
        return Result.finish(departmentDao.deleteAllByIdIn(Arrays.asList(dids)) > 0);
    }


    /**
     * 批量删除岗位
     * @param qids
     * @return
     */
    public Result deleteQuarters(Long ...qids){
        for (Long qid : qids) {
            if(getUidsFromQuarters(qid).size() > 0){
                return Result.error("该岗位下仍有任职人员, 无法删除");
            }
        }
        return Result.finish(quartersDao.deleteAllByIdIn(Arrays.asList(qids)) > 0) ;
    }

    /**
     * 更新用户头像
     * @param uid
     * @param file
     * @return
     */
    public Optional<Long> updateUserFace(long uid, MultipartFile file){
        AtomicReference<User> userAtomicReference = new AtomicReference<>();
        return findUser(uid)
            .map(user -> {
                userAtomicReference.set(user);
                return user;
            })
            .flatMap(user -> findFile(user.getProfile().getFaceId()))
            .map(systemFile -> {
                systemFileDao.delete(systemFile);
//                systemFile.setRemoved(true);
//                systemFileDao.save(systemFile);
                SystemFile face = new SystemFile();
                face.setType(SystemFile.Type.FACE);
                face.setExt(Utils.getExt(file.getOriginalFilename()));
                try {
                    face.setBytes(file.getBytes());
                    face = systemFileDao.save(face);
                    userAtomicReference.get().getProfile().setFaceId(face.getId());
                    userProfileDao.save(userAtomicReference.get().getProfile());
                    if(face.getId() != null) return face.getId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });
    }

    /**
     * 修改密码, 修改规则如下
     * 1. 必须包含数字、字母、特殊字符 三种
     2. 长度至少8位
     3. 不能包含3位及以上相同字符的重复【eg：x111@q& xxxx@q&1】
     4 不能包含3位及以上字符组合的重复【eg：ab!23ab!】
     该规则不生效 5. 不能包含3位及以上的正序及逆序连续字符【eg：123%@#aop %@#321ao efg3%@#47 3%@#47gfe】
     6. 不能包含空格、制表符、换页符等空白字符
     该规则不生效 7. 键盘123456789数字对应的正序逆序特殊字符：eg：12#$%pwtcp(#$%(345对应的特殊字符#$%，仍视作连续))
     8. 支持的特殊字符范围：^$./,;:’!@#%&*|?+()[]{}
     * @param uid
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public Result modifyPassword(long uid, String oldPassword, String newPassword){
        //1 and 8
        if(!(newPassword.matches("^.*[a-zA-Z]+.*$") && newPassword.matches("^.*[0-9]+.*$")
                && newPassword.matches("^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}]+.*$"))){
            return Result.error("密码必须包含字母, 数字, 特殊字符");
        }
        //2
        if(!newPassword.matches("^.{8,}$")){
            return Result.error("密码长度至少8位");
        }
        //3
        if(newPassword.matches("^.*(.)\\1{2,}+.*$")){
            return Result.error("密码不能包含3位及以上相同字符的重复");
        }
        //4
        if(newPassword.matches("^.*(.{3})(.*)\\1+.*$")){
            return Result.error("密码不能包含3位及以上字符组合的重复");
        }
        //6
        if(newPassword.matches("^.*[\\s]+.*$")){
            return Result.error("密码不能包含空格、制表符、换页符等空白字符");
        }

        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        return Result.finish(userDao.modifyPassword(uid, oldPassword, newPassword) > 0);
    }

    /**
     * 修改用户资料
     * @param request
     * @return
     */
    public Result modifyProfile(long uid, ProfileEditRequest request){
        User user = findUser(uid).orElse(null);
        if(null == user){
            return Result.error();
        }
        if(!StringUtils.isEmpty(request.getTrueName())){
            user.setTrueName(request.getTrueName());
        }
        if(!StringUtils.isEmpty(request.getPhone())){
            //查找是否有相同的手机号
            if(userDao.hasThisPhone(uid, request.getPhone()) > 1){
                return Result.error("已经有相同的手机号");
            }
            user.setPhone(request.getPhone());
        }
        if(!StringUtils.isEmpty(request.getEmail())){
            if(userDao.hasThisEmail(uid, request.getEmail()) > 1){
                return Result.error("已经有相同的邮箱");
            }
            user.setEmail(request.getEmail());
        }
        userDao.save(user);
        return Result.ok();
    }



    
    public User saveUser(User user) {
        return userDao.save(user);
    }

//    
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
//    
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
//    
//    public User setQuarters(long uid, long... qids) throws CannotFindEntityException {
//        User user = findUserE(uid);
//        List<Quarters> qs = quartersDao.findAllByIdIn(qids);
//        qs.forEach(q -> q.getUsers().add(user));
//        return saveUser(user);
//    }


    private boolean setQuarters(User user, Collection<Long> qids) {
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

    public Result<User> editUser(UserEdit edit) throws RestException {
        Result ret = Utils.validate(edit);
        if(!ret.isSuccess()) return ret;
        return findUser(edit.getId())
                .map(user -> {
                    if (!StringUtils.isEmpty(edit.getPhone())) {
                        Optional<User> sameUser = userDao.findFirstByPhone(edit.getPhone());
                        if (sameUser.isPresent() && !sameUser.get().getId().equals(user.getId())) {
                            return Result.error("已经有相同的手机号");
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

                    List<Long> oldIds = null;
                    //岗位设置
                    if (null != edit.getQuarters()) {
                        //
                        oldIds = user.getQuarters().stream().map(item -> item.getId()).collect(Collectors.toList());
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
                    user = saveUser(user);
                    if(null != oldIds){
                        globalPermissionService.syncGlobalPermissionCenterQuartersChanged(oldIds,user);
                    }
                    return Result.ok(user);
//                    return Result.ok(saveUser(user));
                }).orElse(Result.error());
    }

    
    public Result<Quarters> createQuarters(QuartersAdd add) {
        Department department = departmentDao.findOne(add.getDepartmentId());
        if (department == null) return Result.error("没有该部门");
        //同部门不能有同名的岗位
        Quarters same = quartersDao.findFirstByDepartmentAndName(department, add.getName());
        if (same != null) return Result.error("已经有同名的岗位");

        Quarters quarters = Transformer.transform(add, Quarters.class);
        quarters.setDepartmentId(department.getId());
        quarters.setDName(department.getName());
        //查找最上层的id
        List objs = quartersDao.getQuartersCodeFromDepartment(department.getId());
        if(objs.size() == 0){
            quarters.setCode(department.getCode() + "_001");
        }
        else{
            String code = objs.get(0).toString();
            int codeValue = Integer.valueOf(code.substring(code.length() - 3, code.length()));
            codeValue++;
            String newCode = codeValue + "";
            for(int i = newCode.length(); i < 3; i++){
                newCode = "0" + newCode;
            }
            quarters.setCode(department.getCode() + "_" + newCode);
        }
        Quarters ret = quartersDao.save(quarters);
        return Result.ok(ret);
    }

    public Result<Quarters> updateQuarters(QuartersEdit edit){
        //禁止编辑同名岗位
        if(quartersDao.countSameNameFromDepartment(edit.getId(),edit.getName()) > 0){
            return Result.error("已经有同名的岗位");
        }
        Quarters quarters = findQuarters(edit.getId()).orElse(null);
        if(null == quarters){
            return Result.error("编辑的岗位不存在");
        }
        quarters.setName(edit.getName());
        quarters.setInfo(edit.getInfo());
        return Result.ok(quartersDao.save(quarters));
    }

    /**
     * 更新岗位主管
     * @param qids
     * @param state
     * @return
     */
    public boolean setManager(Collection<Long> qids, boolean state){
        return quartersDao.updateManager(qids,state) > 0;
    }

    /**
     * 增加用户特殊权限
     * @param uid
     * @param permissions
     * @return
     */
    @Deprecated
    public boolean addExternalPermission(Long uid, UserExternalPermission.Permission ...permissions){
        //防止冲突
        User user = findUser(uid).orElse(null);
        if(null == user) return false;
        removeExternalPermission(uid,permissions);
        for (UserExternalPermission.Permission permission : permissions) {
            UserExternalPermission userExternalPermission = new UserExternalPermission();
            userExternalPermission.setUser(user);
            userExternalPermission.setPermission(permission);
            externalPermissionDao.save(userExternalPermission);
        }
        return true;
    }


    /**
     * 删除用户特殊权限
     * @param uid
     * @param permissions
     * @return
     */
    @Deprecated
    public boolean removeExternalPermission(Long uid, UserExternalPermission.Permission ...permissions){
        for (UserExternalPermission.Permission permission : permissions) {
            externalPermissionDao.deleteAllByUser_IdAndPermission(uid,permission);
        }
        return true;
    }


    /**
     * 登录用户对应的私有云账号
     * @param uid
     * @return
     */
    public Result loginFileCloudSystem(long uid){
        List<Object[]> list = userDao.getUserCloudProfile(uid);
        if(list.size() > 0){
            Object[] objects = list.get(0);
            //登录私有云
            LoginResponse loginResponse = cloudApi.login(String.valueOf(objects[0]),String.valueOf(objects[1]));
            if(null != loginResponse &&  loginResponse.getStatus().equals("SUCCESS")){
                return Result.ok(loginResponse.getResponseCookies().get(0));
            }
        }
        return Result.error();
    }


    /**
     * 登录可操作公共文件柜的私有云账号, 如果没有权限, 那么依然登录原本的账号
     * @param uid
     * @return
     */
    public Result loginFileCloudCommonSystem(long uid){
        //检查是否有共享文件云权限
        if(!globalPermissionService.checkPermission(GlobalPermission.Type.COMMON_CLOUD_DISK,0,uid)){
            return loginFileCloudCommonSystem(uid);
        }
//        if(userDao.checkPermission(Utils.getCurrentUserId(), UserExternalPermission.Permission.COMMON_CLOUD_DISK) == 0){
//            return loginFileCloudSystem(uid);
//        }
        LoginResponse loginResponse = cloudApi.login(cloudCommonUsername,cloudCommonPassword);
        if(null != loginResponse &&  loginResponse.getStatus().equals("SUCCESS")){
            return Result.ok(loginResponse.getResponseCookies().get(0));
        }
        return Result.error();
    }


    /**
     * 得到用户的私有云账号
     * @param uid
     * @return
     */
    public String[] getPrivateCloudUsername(long uid){
        List<Object[]> list = userDao.getUserCloudProfile(uid);
        if(list.size() > 0){
            String[] result = new String[2];
            int count = 0;
            for (Object o : list.get(0)) {
                result[count++] = String.valueOf(o);
            }
            return result;
        }
        return new String[]{"",""};
    }

    /**
     * 得到公共私有云账号
     * @param uid
     * @return
     */
    public String[] getCommonCloudUsername(long uid){
        if(userDao.checkPermission(uid, UserExternalPermission.Permission.COMMON_CLOUD_DISK) == 0){
            return new String[]{"",""};
        }
        return new String[]{cloudCommonUsername,cloudCommonPassword};

    }

    public List<Department> findDepartmentsByParent_Id(long pid){
        if(0 == pid){
            return departmentDao.findAllByParent(null);
        }
        else{
            return departmentDao.findAllByParent_Id(pid);
        }
    }

    public boolean hasUser(long uid){
        return userDao.countById(uid) > 0;
    }

    public boolean userHasQuarter(long uid, long qid){
        return userDao.countUidAndQid(uid,qid) > 0;
    }

    public List<User> findUser(List<Long> ids){
        return userDao.findAllByIdIn(ids);
    }

    
    public Optional<User> findUser(long id) {
        return Optional.ofNullable(userDao.findOne(id));
    }

    public Optional<Quarters> findQuarters(long id){
        return Optional.ofNullable(quartersDao.findOne(id));
    }

    public Optional<SystemFile> findFile(long id){
        return Optional.ofNullable(systemFileDao.findOne(id));
    }

    public List<User> findUserByIds(Set<Long> ids){
        return userDao.findAllByIdIn(ids);
    }
    public List<User> findUserByIds(Long ...ids){
        return findUserByIds(new HashSet<Long>(Arrays.asList(ids)));
    }


    /**
     * 初始化用户首字母
     * @param user
     */
    public void initLetter(User user){
        String letter;
        String firstSpell = ChineseToEnglish.getFirstSpell(user.getTrueName());
        String substring = firstSpell.substring(0, 1).toUpperCase();
        if (substring.matches("[A-Z]")) {
            letter = substring;
        } else {
            letter = "#";
        }
        user.setLetter(letter);
    }

    /************ 权限相关 *************/


    /**
     * 添加授权
     * @param pType 授权类型
     * @param objectId 授权关联对象ID
     * @param uType 授权方式
     * @param linkIds 授权方式ID
     * @Param info 授权详情, 没有为null
     * @return
     */
    public List<Long> addGlobalPermission(GlobalPermission.Type pType, long objectId, GlobalPermission.UserType uType, Collection<Long> linkIds, Object info){
        return linkIds.stream().map(linkId -> {
            //检查是否已经有相同的授权
            GlobalPermission globalPermission = globalPermissionDao.findTopByTypeAndObjectIdAndUserTypeAndLinkId(pType,objectId,uType,linkId).orElse(new GlobalPermission());
//            List ids = entityManager.createQuery("select gp.id from GlobalPermission gp where gp.type = :ptype and gp.objectId = :objectId and gp.userType = :uType and gp.linkId = :linkId")
//                    .setParameter("ptype",pType)
//                    .setParameter("objectId",objectId)
//                    .setParameter("uType",uType)
//                    .setParameter("linkId",linkId)
//                    .setMaxResults(1)
//                    .getResultList();
//            if(ids.size() > 0){
//                return (Long)ids.get(0);
//            }
            globalPermission.setType(pType);
            globalPermission.setObjectId(objectId);
            globalPermission.setUserType(uType);
            globalPermission.setLinkId(linkId);
            //更新授权内容
            globalPermission.setDescription(info);

            //更新用户授权表
            if(pType.equals(GlobalPermission.Type.USER_METHOD)){
                cacheUserMethods(globalPermission);
            }

            globalPermission = globalPermissionDao.save(globalPermission);

            //更新授权中间表
//            globalPermissionService.syncGlobalPermissionCenterAdded(globalPermission);
            return globalPermission.getId();
        }).filter(linkId -> linkId > 0).collect(Collectors.toList());
    }

    public boolean deleteGlobalPermission(Long ...gpids){
        int count = globalPermissionDao.deleteAllByIdIn(Arrays.asList(gpids));
//        if(count > 0){
//            globalPermissionService.syncGlobalPermissionCenterDeleted(gpids);
//        }
        return count > 0;
    }


    public JSONArray getUserMethods(long uid){
        GlobalPermission globalPermission = globalPermissionDao.findTopByTypeAndObjectIdAndUserTypeAndLinkId(GlobalPermission.Type.USER_METHOD, 0, GlobalPermission.UserType.USER, uid).orElse(null);
        JSONObject menu = cache.getMenus();
        if(null == globalPermission){
            return new JSONArray();
        }
        return (JSONArray) globalPermission.getDescription();
    }

    public void cacheUserMethods(GlobalPermission globalPermission){
        userAllowApiDao.deleteAllByUserId(globalPermission.getLinkId());
        JSONObject menu = cache.getMenus();
        for (Object o : (JSONArray)globalPermission.getDescription()) {
            if(o instanceof String){
                String str = (String) o;
                JSONObject item = getChildItemByIndex(menu, str);
                if(item.containsKey("api")){
                    JSONArray apis = item.getJSONArray("api");
                    if(null == apis){
                        continue;
                    }
                    for (Object api : apis) {
                        UserAllowApi allowApi = new UserAllowApi();
                        allowApi.setUserId(globalPermission.getLinkId());
                        allowApi.setApi((String) api);
                        userAllowApiDao.save(allowApi);
                    }
                }
            }
        }
    }





    /*********8 工具类函数 *************/


    /**
     * 检查一个部门是不是另一个部门的子部门
     * @param pid
     * @param cid
     * @return
     */
    public boolean isChildDepartment(long cid, long pid){
        List pobj = departmentDao.getDepartmentCode(pid);
        List cobj = departmentDao.getDepartmentCode(cid);
        return cobj.get(0).toString().startsWith(pobj.get(0).toString());
    }

    /**
     * 检查一个岗位是否隶属某个部门
     * @param qid
     * @param did
     * @return
     */
    public boolean isChildQuarter(long qid, long did){
        List pobj = departmentDao.getDepartmentCode(did);
        List qobj = quartersDao.getQuartersCode(qid);
        return qobj.get(0).toString().startsWith(pobj.get(0).toString());
    }


    /**
     * 检查用户是否隶属于某个部门
     * @param uid
     * @param did
     * @return
     */
    public boolean isUserFromDepartment(long uid, long did){
        List<Object[]> qids = userDao.getQids(uid);
        return qids.stream().anyMatch(qid -> isChildDepartment((Long) qid[0],did));
    }

    public boolean hasQuarters(long uid, long qid){
        return userDao.hasQuarters(uid,qid) > 0;
    }


    public boolean departmentHasQuarters(long did, long qid){
        return departmentDao.departmentHasQuarters(did,qid) > 0;
//        return departmentDao.departmentHasQuarters(did,qid) > 0;
    }


    /**
     * 得到某个部门的所有用户ID
     * @param dids
     * @return
     */
    public List<Long> getUidsFromDepartment(Long ...dids){
        return entityManager.createQuery("select u.id from User u join u.quarters q where (select count(d) from Department d where q.code like concat(d.code,'%') and d.id in :dids) > 0")
                .setParameter("dids",Arrays.asList(dids))
                .getResultList();
    }

    /**
     * 得到某个岗位的所有用户ID
     * @param qids
     * @return
     */
    public List<Long> getUidsFromQuarters(Long ...qids){
        return entityManager.createQuery("select u.id from User u join u.quarters q where q.id in :qids")
                .setParameter("qids", Arrays.asList(qids))
                .getResultList();
    }


    /**
     * 得到某个岗位的所有用户ID
     * @param uid
     * @return
     */
    public List<Long> getQidsFromUser(long uid){
        return entityManager.createQuery("select q.id from User u join u.quarters q where u.id = :uid")
                .setParameter("uid",uid)
                .getResultList();
    }

    private JSONObject getChildItemByIndex(JSONObject item, String path){
        String[] ps = path.trim().split("\\.");
        JSONObject result = item;
        for (String p : ps) {
            Object obj = result.get(p);
            if(null == obj || !(obj instanceof JSONObject)){
                return new JSONObject();
            }
            result = (JSONObject) obj;
        }
        return result;
    }



}
