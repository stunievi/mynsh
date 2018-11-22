package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.modules.cloud.CloudApi;
import com.beeasy.hzback.modules.cloud.CloudService;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.entity.GP;
import com.beeasy.hzback.entity.Org;
import com.beeasy.hzback.entity.User;
import com.beeasy.mscommon.RestException;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

//import com.beeasy.hzback.modules.system.entity_kt.UserAddRequeest;

@Slf4j
@Service
@Transactional
public class UserService {


    @Value("${filecloud.userDefaultPassword}")
    String cloudUserPassword;
    @Value("${filecloud.commonUsername}")
    String cloudCommonUsername;
    @Value("${filecloud.commonPassword}")
    String cloudCommonPassword;

    @Autowired
    SQLManager sqlManager;

    @Autowired
    FileService   fileService;

//    IUserTokenDao userTokenDao;
//    final
//    IUserAllowApiDao userAllowApiDao;
//    final
//    IGlobalPermissionDao globalPermissionDao;

    //    @Autowired
//    SystemTextLogService logService;
//    final
//    IRoleDao roleDao;
    //    @Autowired
    @Autowired
    CloudApi cloudApi;
    //    @Autowired
//    IUserExternalPermissionDao externalPermissionDao;
//    final
//    ISystemFileDao systemFileDao;
//    final
//    IQuartersDao quartersDao;
//    final
//    IDepartmentDao departmentDao;
//    final
//    IUserDao userDao;
    //    @Autowired
//    IRolePermissionDao rolePermissionDao;
//    final
//    IUserProfileDao userProfileDao;

    @Autowired
    SystemConfigCache cache;


    //    final
//    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
    @Autowired
    CloudService cloudService;


    /**
     * create a user
     * <p>
     * it's calling lfs'api to create a same user on lfs-system
     * if admin set default ids of role, the new user will get them
     */
//    public User createUser(User u) {
//        //save face images
////        SystemFile face = new SystemFile();
////        SystemFile face = null;
//
////        User u = new User();
//        u.setPassword(DigestUtils.md5DigestAsHex(u.getPassword().getBytes()));
//        u.setSu(false);
//        u.setBaned(false);
////        u.setTrueName(add.getTrueName());
////        u.setPhone(add.getPhone());
////        u.setEmail(add.getEmail());
////        u.setBaned(add.isBaned());
//
//        //save profile
////        UserProfile profile = new UserProfile();
////        profile.setUser(u);
////        profile.setFaceId(face.getId());
//        //创建文件云账号
//        Result r = cloudService.createUser(u.getUsername());
//        if (!r.isSuccess()) {
//            throw new RestException("创建文件云账号失败");
//        }
//        u.setCloudUsername(u.getUsername());
//        u.setCloudPassword(cloudUserPassword);
//
//        initLetter(u);
//        sqlManager.insert(u, true);
//
//        ClassPathResource resource = new ClassPathResource("static/default_face.jpg");
//        //upload image
//        try (
//            InputStream is = resource.getInputStream()
//        ) {
//            MockMultipartFile file = new MockMultipartFile(resource.getFilename(), is);
//            fileService.uploadFace(u.getId(), file);
//        } catch (IOException e) {
//            throw new RestException("保存头像失败");
//        }
//
//        //岗位
////        for (Long aLong : add.getQids()) {
////            addUsersToQuarters(Collections.singleton(ret.getId()), aLong);
////        }
////
////        //角色
////        //默认角色
////        Map<String, String> configs = systemService.get("sys_default_role_ids");
////        List<Long> rids = Utils.convertIdsToList(configs.getOrDefault("sys_default_role_ids", ""));
////        add.getRids().addAll(rids);
////
////        for (Long aLong : add.getRids()) {
////            roleAddUsers(aLong, Collections.singleton(ret.getId()));
////        }
//
//        return u;
//    }

    /**
     * edit an user by user's id
     *
     * @param
     */
//    public void editUser(Long uid, User user) {
//        //只有被赋予权限的和自己能修改用户资料
//        if(!Objects.equals(uid, null) && !Objects.equals(AuthFilter.getUid(), uid)){
//            throw new RestException("你没有修改用户资料的权限!");
//        }
//        //禁止修改用户名
//        User u = $.map(user).filter("+password,+phone,+email,+baned,+trueName,+accCode").to(User.class);
//        if(S.notEmpty(u.getPassword())){
////            isValidPassword(u.getPassword());
//            u.setPassword(DigestUtils.md5DigestAsHex(u.getPassword().getBytes()));
//        }
//        if(S.notEmpty(u.getTrueName())){
//            initLetter(u);
//        }
//        sqlManager.lambdaQuery(User.class)
//            .andEq(User::getId, u.getId())
//            .updateSelective(u);

//
//        User user = findUser(edit.getId());
//        if (!edit.getPhone().isEmpty()) {
//            user.setPhone(edit.getPhone());
//        }
//        if (!edit.getEmail().isEmpty()) {
//            user.setEmail(edit.getEmail());
//        }
//        //modify password
//        if (!edit.getPassword().isEmpty()) {
//            isValidPassword(edit.getPassword());
//            user.setPassword(DigestUtils.md5DigestAsHex(edit.getPassword().getBytes()));
//        }
//        //是否禁用
//        if (edit.getBaned() != null) {
//            user.setBaned(edit.getBaned());
//        }
//        //真实姓名
//        if (!edit.getTrueName().isEmpty()) {
//            user.setTrueName(edit.getTrueName());
//        }
//        if (!edit.getAccCode().isEmpty()) {
//            user.setAccCode(edit.getAccCode());
//        }
//        initLetter(user);
        //岗位设置
//        if (null != edit.getQuarters()) {
//            setQuarters(user, edit.getQuarters());
//        }
//        return saveUser(user);
//    }

    /**
     * delete list of user
     * notice that try not to delete a user as much as possible when this user has some works
     * it may lead to some strange problems
     *
     * @param uids collection of user's id
     * @return collection of which user has been deleted
     */
    public void deleteUser(Collection<Long> uids) {
        sqlManager.lambdaQuery(User.class)
            .andIn(User::getId, uids)
            //管理员禁止删除
            .andEq(User::getSu, false)
            .delete();
        //删除组织架构
        String ids =  uids.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","));
        sqlManager.executeUpdate(
            new SQLReady(S.fmt("delete from t_user_org where uid in (%s)",ids))
        );
    }

    /**
     * permission-start
     */
//    public List<Long> addGlobalPermission(GlobalPermission.Type pType, long objectId, GlobalPermission.UserType uType, Collection<Long> linkIds, Object info) {
//        return linkIds.stream().map(linkId -> {
//            //检查是否已经有相同的授权
//            GlobalPermission globalPermission = globalPermissionDao.findTopByTypeAndObjectIdAndUserTypeAndLinkId(pType, objectId, uType, linkId).orElse(new GlobalPermission());
//            globalPermission.setType(pType);
//            globalPermission.setObjectId(objectId);
//            globalPermission.setUserType(uType);
//            globalPermission.setLinkId(linkId);
//            //更新授权内容
//            globalPermission.setDescription(info);
//
////            //更新用户授权表
////            if (pType.equals(GlobalPermission.Type.USER_METHOD)) {
////                cacheUserMethods(globalPermission);
////            }
//
//            return globalPermissionDao.save(globalPermission).getId();
//        }).collect(toList());
//    }
//
//    public List<GlobalPermission> getGlobalPermissions(Collection<GlobalPermission.Type> types, long objectId) {
//        return globalPermissionDao.findAllByTypeInAndObjectId(types, objectId);
//    }
//
//    public Optional<GlobalPermission> getGlobalPermission(GlobalPermission.Type type, long oid, GlobalPermission.UserType userType, long lid) {
//        return globalPermissionDao.findTopByTypeAndObjectIdAndUserTypeAndLinkId(type, oid, userType, lid);
//    }
//
//    public boolean deleteGlobalPermission(Collection<Long> gpids) {
//        return globalPermissionDao.deleteAllByIdIn((gpids)) > 0;
//    }
//
//    public boolean deleteGlobalPermissionByObjectId(long id) {
//        return globalPermissionDao.deleteAllByObjectId(id) > 0;
//    }
//
//    public boolean deleteGlobalPermissionByTypeAndObjectId(GlobalPermission.Type type, long id) {
//        return globalPermissionDao.deleteAllByTypeAndObjectId(type, id) > 0;
//    }
//
//    public List getUserMethods(long uid) {
//        if(isSu(uid)){
//            return C.newList("_all_");
//        }
//        return sqlManager.lambdaQuery(GPC.class)
//            .andEq(GPC::getUid, uid)
//            .andEq(GPC::getType, GP.Type.USER_METHOD)
//            .select()
//            .stream()
//            .map(GPC::getDescription)
//            .map(str -> JSON.parseArray(str))
//            .flatMap(arr -> ((JSONArray) arr).stream())
//            .distinct()
//            .collect(toList());
//    }
    /**
     * permission-end
     */




    /**
     * org-start
     */
    public Org createOrg(Org org){
        //edit
        org.setId(null);
        sqlManager.insert(org,true);
        return org;
    }

    public Org editOrg(Org org){
        sqlManager.updateById(org);
        return org;
    }

    public void deleteOrg(long id){
        List<JSONObject> items = sqlManager.select("user.查询子机构ID", JSONObject.class, C.newMap("oid",id));
        List<Long> ids = items.stream()
            .map(item -> item.getLong("id"))
            .collect(toList());
        sqlManager.lambdaQuery(Org.class)
            .andIn(Org::getId, ids)
            .delete();

        //删除授权表中的相关内容
        sqlManager.lambdaQuery(GP.class)
            .andIn(GP::getOid, ids)
            .delete();
    }

    public void setGP(GP[] gps){
        if(gps.length == 0){
            return;
        }
        if(null != gps[0].getType() && !S.empty(gps[0].getK1()) && null != gps[0].getObjectId()){
            sqlManager.lambdaQuery(GP.class)
                .andEq(GP::getType, gps[0].getType())
                .andEq(GP::getObjectId, gps[0].getObjectId())
                .andEq(GP::getK1, gps[0].getK1())
                .delete();
        }
        sqlManager.insertBatch(GP.class, Arrays.asList(gps));
    }

    /**
     *
     * @param chan
     *             0:user -> org
     *             1: org -> user;  -> means "add to"
     *             2: org ->
     *             3: user ->
     * @param uids
     * @param qids
     * @param clean if true, clear all items which selected througn channel
     */
    public void setUserQuarters(int chan, long[] uids, long[] oids, boolean clear){
        switch (chan){
            //user -> quarters
            case 0:
                if(clear){
                    sqlManager.update("user.解除用户组织机构(通过组织机构ID)",C.newMap("oids", oids));
                }
                for (long oid: oids) {
                    for (long uid : uids) {
                        sqlManager.update("user.解除用户组织机构(通过用户和组织机构ID)",C.newMap("oid", oid, "uid", uid));
                        sqlManager.update("user.添加用户组织机构", C.newMap("oid",oid,"uid",uid));
                    }
                }
                break;

            //quarters -> user
            case 1:
                //todo:
//                if(clear){
//                    quartersDao.deleteFromManyUser(uids);
//                }
//                for (long uid : uids) {
//                    for (long oid : oids) {
////                        quartersDao.deleteFromSingleUser(uid, qid);
////                        quartersDao.insertUsersQuarters(uid,qid);
//                    }
//                }
                break;

            //quarters ->
            case 2:
                //ignore clear
                for (long oid : oids) {
                    for (long uid : uids) {
                        sqlManager.update("user.解除用户组织机构(通过用户和组织机构ID)",C.newMap("oid", oid, "uid", uid));
                    }
                }
                break;
            //user ->
            case 3:
                //ignore clear
                for (long uid : uids) {
                    for (long oid : oids) {
                        sqlManager.update("user.解除用户组织机构(通过用户和组织机构ID)",C.newMap("oid", oid, "uid", uid));
                    }
                }
                break;

        }

    }


    /**
     * quarters-end
     */


    /**
     * 批量删除岗位
     *
     * @param qids
     * @return
     */

    /**
     * 修改密码, 修改规则如下
     * 1. 必须包含数字、字母、特殊字符 三种
     * 2. 长度至少8位
     * 3. 不能包含3位及以上相同字符的重复【eg：x111@q& xxxx@q&1】
     * 4 不能包含3位及以上字符组合的重复【eg：ab!23ab!】
     * 该规则不生效 5. 不能包含3位及以上的正序及逆序连续字符【eg：123%@#aop %@#321ao efg3%@#47 3%@#47gfe】
     * 6. 不能包含空格、制表符、换页符等空白字符
     * 该规则不生效 7. 键盘123456789数字对应的正序逆序特殊字符：eg：12#$%pwtcp(#$%(345对应的特殊字符#$%，仍视作连续))
     * 8. 支持的特殊字符范围：^$./,;:’!@#%&*|?+()[]{}
     *
     * @param uid
     * @param oldPassword
     * @param newPassword
     * @return
     */
//    public void modifyPassword(final long uid, String oldPassword, String newPassword) {
//        isValidPassword(newPassword);
//        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
//        newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
//        int count = sqlManager.lambdaQuery(User.class)
//            .andEq(User::getId, uid)
//            .andEq(User::getPassword, oldPassword)
//            .updateSelective(C.newMap("password", newPassword, "newUser", 0));
//        User user = sqlManager.unique(User.class, uid);
//        if (0 == count) {
//            throw new RestException("旧密码不正确或旧密码同新密码相同!");
//        }
//    }



    /**
     * 修改用户资料
     *
     * @param request
     * @return
     */
//    public User modifyProfile(long uid, ProfileEditRequest request) {
//        User user = findUser(uid);
//        if (!StringUtils.isEmpty(request.getTrueName())) {
//            user.setTrueName(request.getTrueName());
//        }
//        if (!StringUtils.isEmpty(request.getPhone())) {
//            //查找是否有相同的手机号
//            if (userDao.countByPhoneAndIdNot(request.getPhone(), uid) > 0) {
//                throw new RestException("已经有相同的手机号");
//            }
//            user.setPhone(request.getPhone());
//        }
//        if (!StringUtils.isEmpty(request.getEmail())) {
//            if (userDao.countByEmailAndIdNot(request.getEmail(), uid) > 0) {
//                throw new RestException("已经有相同的邮箱");
//            }
//            user.setEmail(request.getEmail());
//        }
//        return userDao.save(user);
//    }




    /**
     * 登录用户对应的私有云账号
     *
     * @param uid
     * @return
     */
//    public Result loginFileCloudSystem(long uid) {
//        List<Object[]> list = userDao.getUserCloudProfile(uid);
//        if (list.size() > 0) {
//            Object[] objects = list.get(0);
//            //登录私有云
//            LoginResponse loginResponse = cloudApi.login(String.valueOf(objects[0]), String.valueOf(objects[1]));
//            if (null != loginResponse && loginResponse.getStatus().equals("SUCCESS")) {
//                return Result.ok(loginResponse.getResponseCookies().get(0));
//            }
//        }
//        return Result.error();
//    }


    /**
     * 登录可操作公共文件柜的私有云账号, 如果没有权限, 那么依然登录原本的账号
     *
     * @param uid
     * @return
     */
//    public Result loginFileCloudCommonSystem(long uid) {
//        //检查是否有共享文件云权限
////        if (!globalPermissionService.checkPermission(GlobalPermission.Type.COMMON_CLOUD_DISK, 0, uid)) {
////            return loginFileCloudCommonSystem(uid);
////        }
////        if(userDao.checkPermission(Utils.getCurrentUserId(), UserExternalPermission.Permission.COMMON_CLOUD_DISK) == 0){
////            return loginFileCloudSystem(uid);
////        }
//        LoginResponse loginResponse = cloudApi.login(cloudCommonUsername, cloudCommonPassword);
//        if (null != loginResponse && loginResponse.getStatus().equals("SUCCESS")) {
//            return Result.ok(loginResponse.getResponseCookies().get(0));
//        }
//        return Result.error();
//    }


    /**
     * 得到用户的私有云账号
     *
     * @param uid
     * @return
     */
//    public String[] getPrivateCloudUsername(long uid) {
//        List<Object[]> list = userDao.getUserCloudProfile(uid);
//        if (list.size() > 0) {
//            String[] result = new String[2];
//            int count = 0;
//            for (Object o : list.get(0)) {
//                result[count++] = String.valueOf(o);
//            }
//            return result;
//        }
//        return new String[]{"", ""};
//    }

    /**
     * 得到公共私有云账号
     *
     * @param uid
     * @return
     */
//    public String[] getCommonCloudUsername(long uid) {
//        User user = sqlManager.single(User.class, uid);
//        if (null == user || !user.getSu()) {
//            return new String[]{"", ""};
//        }
//        return new String[]{cloudCommonUsername, cloudCommonPassword};
//
//    }



    /**
     * 初始化用户首字母
     *
     * @param user
     */
//    public void initLetter(User user) {
//        String letter;
//        String firstSpell = ChineseToEnglish.getFirstSpell(user.getTrueName());
//        String substring = firstSpell.substring(0, 1).toUpperCase();
//        if (substring.matches("[A-Z]")) {
//            letter = substring;
//        } else {
//            letter = "#";
//        }
//        user.setLetter(letter);
//    }




    /**
     * check if user is SuperUser
     *
     * @param uid
     * @return
     */
    public boolean isSu(long uid) {
        return sqlManager.lambdaQuery(User.class)
            .andEq(User::getId,uid)
            .andEq(User::getSu,true)
            .count() > 0;
    }





}
