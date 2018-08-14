package com.beeasy.hzback.modules.system.service;

import bin.leblanc.classtranslate.Transformer;
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
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.common.entity.*;
import com.beeasy.common.entity.*;
//import com.beeasy.hzback.modules.system.entity_kt.UserAddRequeest;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.request.*;
import com.beeasy.hzback.modules.system.rpc.FileUploadService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
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
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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

    final
    FileUploadService fileUploadService;
    final
    SystemService systemService;
    final
    IUserTokenDao userTokenDao;
    final
    IUserAllowApiDao userAllowApiDao;
    final
    IGlobalPermissionDao globalPermissionDao;

    //    @Autowired
//    SystemTextLogService logService;
    final
    IRoleDao roleDao;
    final
    GlobalPermissionService globalPermissionService;
    //    @Autowired
    CloudApi cloudApi;
    //    @Autowired
//    IUserExternalPermissionDao externalPermissionDao;
    final
    ISystemFileDao systemFileDao;
    final
    IQuartersDao quartersDao;
    final
    IDepartmentDao departmentDao;
    final
    IUserDao userDao;
    //    @Autowired
//    IRolePermissionDao rolePermissionDao;
    final
    IUserProfileDao userProfileDao;

    final
    SystemConfigCache cache;

    final
    EntityManager entityManager;

    //    final
//    ICloudDirectoryIndexDao cloudDirectoryIndexDao;
    final
    CloudService cloudService;

    @Autowired
    public UserService(IRoleDao roleDao, FileUploadService fileUploadService, SystemService systemService, IUserTokenDao userTokenDao, IUserProfileDao userProfileDao, EntityManager entityManager, IUserAllowApiDao userAllowApiDao, CloudService cloudService, IGlobalPermissionDao globalPermissionDao, GlobalPermissionService globalPermissionService, ISystemFileDao systemFileDao, IQuartersDao quartersDao, IDepartmentDao departmentDao, SystemConfigCache cache, IUserDao userDao) {
        this.roleDao = roleDao;
        this.fileUploadService = fileUploadService;
        this.systemService = systemService;
        this.userTokenDao = userTokenDao;
        this.userProfileDao = userProfileDao;
        this.entityManager = entityManager;
        this.userAllowApiDao = userAllowApiDao;
        this.cloudService = cloudService;
        this.globalPermissionDao = globalPermissionDao;
        this.globalPermissionService = globalPermissionService;
        this.systemFileDao = systemFileDao;
        this.quartersDao = quartersDao;
        this.departmentDao = departmentDao;
        this.cache = cache;
        this.userDao = userDao;
    }

    /**
     * create a user
     * <p>
     * it's calling lfs'api to create a same user on lfs-system
     * if admin set default ids of role, the new user will get them
     */
    public User createUser(UserAddRequeest add) {
        //save face images
        SystemFile face = new SystemFile();
            ClassPathResource resource = new ClassPathResource("static/default_face.jpg");
            face.setFileName("default_face.jpg");
            face.setExt("jpg");
            face.setType(SystemFile.Type.FACE);
            String filePath = "";
            //upload image
            try (
                    InputStream is = resource.getInputStream()
            ) {
                MockMultipartFile file = new MockMultipartFile(resource.getFilename(), is);
                filePath = fileUploadService.uploadFiles(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (filePath.isEmpty()) {
                throw new RestException("保存头像失败");
            }
        face = systemFileDao.save(face);
        User u = new User();
        u.setUsername(add.getUsername());
        u.setAccCode(add.getAccCode());
        u.setPassword(DigestUtils.md5DigestAsHex(add.getPassword().getBytes()));
        u.setTrueName(add.getTrueName());
        u.setPhone(add.getPhone());
        u.setEmail(add.getEmail());
        u.setBaned(add.isBaned());

        //save profile
        UserProfile profile = new UserProfile();
        profile.setUser(u);
        profile.setFaceId(face.getId());
        //创建文件云账号
        Result r = cloudService.createUser(u.getUsername());
        if (!r.isSuccess()) {
            throw new RestException("创建文件云账号失败");
        }
        profile.setCloudUsername(u.getUsername());
        profile.setCloudPassword(cloudUserPassword);
        profile = userProfileDao.save(profile);
        u.setProfile(profile);

        initLetter(u);
        User ret = userDao.save(u);

        //岗位
        for (Long aLong : add.getQids()) {
            addUsersToQuarters(Collections.singleton(ret.getId()), aLong);
        }
        //角色
        //默认角色
        Map<String, String> configs = systemService.get("sys_default_role_ids");
        List<Long> rids = Utils.convertIdsToList(configs.getOrDefault("sys_default_role_ids", ""));
        add.getRids().addAll(rids);

        for (Long aLong : add.getRids()) {
            roleAddUsers(aLong, Collections.singleton(ret.getId()));
        }

        return ret;
    }

    /**
     * edit an user by user's id
     *
     * @param
     */
    public User editUser(UserEditRequest edit) throws RestException {
        User user = findUser(edit.getId());
        if (!edit.getPhone().isEmpty()) {
            user.setPhone(edit.getPhone());
        }
        if (!edit.getEmail().isEmpty()) {
            user.setEmail(edit.getEmail());
        }
        //modify password
        if (!edit.getPassword().isEmpty()) {
            isValidPassword(edit.getPassword());
            user.setPassword(DigestUtils.md5DigestAsHex(edit.getPassword().getBytes()));
        }
        //是否禁用
        if (edit.getBaned() != null) {
            user.setBaned(edit.getBaned());
        }
        //真实姓名
        if (!edit.getTrueName().isEmpty()) {
            user.setTrueName(edit.getTrueName());
        }
        if (!edit.getAccCode().isEmpty()) {
            user.setAccCode(edit.getAccCode());
        }
        //岗位设置
        if (null != edit.getQuarters()) {
            setQuarters(user, edit.getQuarters());
        }
        return saveUser(user);
    }

    /**
     * delete list of user
     * notice that try not to delete a user as much as possible when this user has some works
     * it may lead to some strange problems
     *
     * @param uids collection of user's id
     * @return collection of which user has been deleted
     */
    public List<Long> deleteUser(Collection<Long> uids) {
        return uids.stream().filter(uid -> {
            //解除关联
            userDao.deleteLinksByUid(uid);
            //删除用户
            userDao.deleteById(uid);
            return true;
        }).collect(toList());
    }

    /**
     * search list of user
     * paramaters:
     * username: like
     * truename: like
     * baned: equal
     */
    public Page<User> searchUser(UserSearchRequest search, Pageable pageable) {
        Specification query = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!search.getName().isEmpty()) {
                predicates.add(
                        cb.or(
                                cb.like(root.get("username"), "%" + search.getName() + "%"),
                                cb.like(root.get("trueName"), "%" + search.getName() + "%")
                        )
                );
            }
            if (null != search.getBaned()) {
                predicates.add(
                        cb.equal(root.get("baned"), search.getBaned())
                );
            }
            if (search.getQuarters().size() > 0) {
                predicates.add(
                        root.join("quarters").in(search.getQuarters())
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return userDao.findAll(query, pageable);
    }


    /**
     * permission-start
     */
    public List<Long> addGlobalPermission(GlobalPermission.Type pType, long objectId, GlobalPermission.UserType uType, Collection<Long> linkIds, Object info) {
        return linkIds.stream().map(linkId -> {
            //检查是否已经有相同的授权
            GlobalPermission globalPermission = globalPermissionDao.findTopByTypeAndObjectIdAndUserTypeAndLinkId(pType, objectId, uType, linkId).orElse(new GlobalPermission());
            globalPermission.setType(pType);
            globalPermission.setObjectId(objectId);
            globalPermission.setUserType(uType);
            globalPermission.setLinkId(linkId);
            //更新授权内容
            globalPermission.setDescription(info);

//            //更新用户授权表
//            if (pType.equals(GlobalPermission.Type.USER_METHOD)) {
//                cacheUserMethods(globalPermission);
//            }

            return globalPermissionDao.save(globalPermission).getId();
        }).collect(toList());
    }

    public List<GlobalPermission> getGlobalPermissions(Collection<GlobalPermission.Type> types, long objectId) {
        return globalPermissionDao.findAllByTypeInAndObjectId(types, objectId);
    }

    public Optional<GlobalPermission> getGlobalPermission(GlobalPermission.Type type, long oid, GlobalPermission.UserType userType, long lid) {
        return globalPermissionDao.findTopByTypeAndObjectIdAndUserTypeAndLinkId(type, oid, userType, lid);
    }

    public boolean deleteGlobalPermission(Collection<Long> gpids) {
        return globalPermissionDao.deleteAllByIdIn((gpids)) > 0;
    }

    public boolean deleteGlobalPermissionByObjectId(long id) {
        return globalPermissionDao.deleteAllByObjectId(id) > 0;
    }

    public boolean deleteGlobalPermissionByTypeAndObjectId(GlobalPermission.Type type, long id) {
        return globalPermissionDao.deleteAllByTypeAndObjectId(type, id) > 0;
    }

    public List getUserMethods(long uid) {
        return globalPermissionDao.getPermissionsByUser(
                Collections.singleton(GlobalPermission.Type.USER_METHOD),
                Arrays.asList(GlobalPermission.UserType.USER, GlobalPermission.UserType.ROLE),
                uid
        ).stream()
                .map(GlobalPermission::getDescription)
                .filter(obj -> obj instanceof JSONArray)
                .flatMap(obj -> ((JSONArray) obj).stream())
                .distinct()
                .collect(toList());
    }
    /**
     * permission-end
     */

    /**
     * role-start
     */

    /**
     * create a role
     *
     * @param request
     * @return
     */
    public Role createRole(RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setInfo(request.getInfo());
        role.setSort(request.getSort());
        role.setCanDelete(request.isCanDelete());
        return roleDao.save(role);
    }

    /**
     * edit a role
     *
     * @param request
     * @return
     */
    public Role editRole(RoleRequest request) {
        Role role = findRole(request.getId());
        role.setName(request.getName());
        role.setInfo(request.getInfo());
        role.setSort(request.getSort());
        role.setCanDelete(request.isCanDelete());
        return roleDao.save(role);
    }


    /**
     * delete list of roles
     *
     * @param ids
     * @return role's id which has been deleted
     */
    public List<Long> deleteRoles(Collection<Long> ids) {
        return ids.stream()
                .map(id -> roleDao.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(role -> {
                    roleDao.deleteById(role.getId());
                    return role.getId();
                })
                .collect(toList());
    }

    /**
     * add list of user to a role
     *
     * @param rid  role's id
     * @param uids collection of user's id
     * @return boolean if the result is succeed
     */
    public boolean roleAddUsers(long rid, Collection<Long> uids) {
        if (roleDao.countById(rid) == 0) {
            return false;
        }
        for (Long uid : uids) {
            if (userDao.countById(uid) == 0) {
                continue;
            }
            if (roleDao.hasPair(uid, rid) > 0) {
                continue;
            }
            roleDao.addUserRole(uid, rid);
        }
        return true;
    }

    public List<Long> roleDeleteUsers(long rid, Collection<Long> uids) {
        if (roleDao.countById(rid) == 0) {
            return new ArrayList<>();
        }
        return uids.stream()
                .filter(uid -> roleDao.deleteUserRole(uid, rid) > 0)
                .collect(toList());
    }

    public List<Long> userSetRoles(long uid, Collection<Long> rids) {
        if (!userDao.existsById(uid)) {
            return new ArrayList<>();
        }
        //删除所有角色
        roleDao.deleteUserRoles(uid);
        return rids.stream().filter(rid -> {
            return roleDao.hasPair(uid, rid) > 0 || roleDao.addUserRole(uid, rid) > 0;
        }).collect(toList());
    }

    /**
     * set roles to a user
     *
     * @param
     * @return list of role which is succeed
     */
    public List<Long> userDeleteRoles(long uid, Collection<Long> rids) {
        return rids.stream()
                .filter(rid -> roleDao.deleteUserRole(uid, rid) > 0)
                .collect(toList());
    }


    /**
     * search roles
     * name: like
     *
     * @param request
     * @param pageable
     * @return
     */
    public Page searchRoles(RoleSearchRequest request, Pageable pageable) {
        return roleDao.findAll(((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!request.getName().isEmpty()) {
                predicates.add(
                        cb.like(root.get("name"), "%" + request.getName() + "%")
                );
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }), pageable);
    }

    /**
     * role-end
     */

    /**
     * department-start
     */
    public Department createDepartment(DepartmentAddRequest add) {
        Optional<Department> same;
        Department parent = 0 == add.getParentId() ? null : findDepartment(add.getParentId());
        Department department = new Department();
        department.setName(add.getName());
        department.setParentId(null == parent ? null : parent.getId());
        department.setInfo(add.getInfo());
        department.setSort(add.getSort());
        department.setAccCode(add.getAccCode());

        //部门编号
        List objs;
        if (null == department.getParentId()) {
            objs = departmentDao.findTopLastCode();
        } else {
            objs = departmentDao.findLastCode(department.getParentId());
        }
        System.out.println(objs);
        //没找到的时候,使用父编码+001
        if (objs.size() == 0) {
            if (null == department.getParentId()) {
                department.setCode("001");
            } else {
                List codes = departmentDao.getDepartmentCode(department.getParentId());
                department.setCode(codes.get(0) + "001");
            }
        } else {
            //取后三位+1
            String code = (String) objs.get(0);
            String newCode = code.replaceFirst(".{3}$",
                    String.format("%03d",
                            Integer.parseInt(
                                    code.substring(code.length() - 3, code.length())
                            ) + 1
                    )
            );
            department.setCode(newCode);
        }
        return departmentDao.save(department);
    }

    public Department editDepartment(DepartmentEditRequest edit) {
        Department department = findDepartment(edit.getId());
        if (!edit.getName().isEmpty()) {
            department.setName(edit.getName());
        }
        if (!edit.getInfo().isEmpty()) {
            department.setInfo(edit.getInfo());
        }
        if (!edit.getAccCode().isEmpty()) {
            department.setAccCode(edit.getAccCode());
        }
        department.setSort(edit.getSort());
        return departmentDao.save(department);
    }

    /**
     * delete a department
     * <p>
     * usually, we suggest that do not delete it
     * but if you really want, remember this department must be an empty department
     */
    public boolean deleteDepartment(final long id) {
        Department department = findDepartment(id);
        //如果还要岗位 不能删除
        if (department.getQuarters().size() > 0) {
            throw new RestException("该部门还有岗位, 无法删除");
        }

        if (department.getChildren().size() > 0) {
            throw new RestException("该部门还有子部门, 无法删除");
        }

        departmentDao.deleteById(id);
        return true;
    }
    /**
     * department-end
     */


    /**
     * quarters-start
     */

    /**
     * create a quarters
     *
     * @param add
     * @return
     */
    public Quarters createQuarters(QuartersAddRequest add) {
        Department department = findDepartment(add.getDepartmentId());

        Quarters quarters = new Quarters();
        quarters.setDepartmentId(department.getId());
        quarters.setDName(department.getName());
        quarters.setSort(add.getSort());
        quarters.setManager(add.isManager());

        //查找最上层的id
        List objs = quartersDao.getQuartersCodeFromDepartment(department.getId());
        if (objs.size() == 0) {
            quarters.setCode(department.getCode() + "_001");
        } else {
            String code = objs.get(0).toString();
            int codeValue = Integer.valueOf(code.substring(code.length() - 3, code.length()));
            codeValue++;
            String newCode = codeValue + "";
            for (int i = newCode.length(); i < 3; i++) {
                newCode = "0" + newCode;
            }
            quarters.setCode(department.getCode() + "_" + newCode);
        }
        return quartersDao.save(quarters);
    }

    /**
     * edit a quarters
     *
     * @param edit
     * @return
     */
    public Quarters editQuarters(QuartersEditRequest edit) {
        Quarters quarters = findQuarters(edit.getId());
        quarters.setManager(edit.isManager());
        quarters.setSort(edit.getSort());
        quarters.setName(edit.getName());
        quarters.setInfo(edit.getInfo());
        return quartersDao.save(quarters);
    }


    /**
     * delete list of quarters
     * at first, to remove the relationship of each user and quarters
     * then, call hard delete
     */
    public boolean deleteQuarters(Collection<Long> qids) {
        userDao.deleteLinksByQids(qids);
        quartersDao.deleteAllByIdIn(qids);
        return true;
    }

    private boolean setQuarters(User user, Collection<Long> qids) {
        userDao.deleteLinksByUid(user.getId());
        for (Long qid : qids) {
            addUsersToQuarters(Arrays.asList(user.getId()), qid);
        }
        return true;
    }


    public List<Long> addUsersToQuarters(Collection<Long> uids, long qid) {
        if (quartersDao.countById(qid) == 0) {
            return new ArrayList<>();
        }
        return uids.stream()
                .filter(uid -> {
                    return userDao.countById(uid) > 0 && (
                            userDao.hasQuarters(uid, qid) > 0 || userDao.userAddQuarters(uid, qid) > 0
                    );
                }).collect(toList());
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
     * 更新用户头像
     *
     * @param uid
     * @param file
     * @return
     */
    public long updateUserFace(long uid, MultipartFile file) {
        User user = findUser(uid);
        SystemFile systemFile = findFile(user.getProfile().getFaceId()).orElse(new SystemFile());
        String ext = Utils.getExt(file.getOriginalFilename());
        List<String> filters = Arrays.asList("jpeg", "jpg", "png");
        if (!filters.contains(ext.toLowerCase())) {
            return 0;
        }
        if (null != systemFile.getId()) {
            systemFileDao.delete(systemFile);
        }
        SystemFile face = new SystemFile();
        face.setType(SystemFile.Type.FACE);
        face.setExt(Utils.getExt(file.getOriginalFilename()));
        String filePath = "";
        filePath = fileUploadService.uploadFiles(file);
        if (filePath.isEmpty()) {
            return 0;
        }
        face.setFilePath(filePath);
        face = systemFileDao.save(face);
        user.getProfile().setFaceId(face.getId());
        userProfileDao.save(user.getProfile());
        return face.getId();
    }

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
    public boolean modifyPassword(final long uid, String oldPassword, String newPassword) {
        isValidPassword(newPassword);
        User user = findUser(uid);
        if (null == user) {
            return false;
        }
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        if (!user.getPassword().equals(oldPassword)) {
            throw new RestException("旧密码不正确");
        }
        user.setNewUser(false);
        user.setPassword(newPassword);
        userDao.save(user);
        return true;
    }

    public void isValidPassword(String newPassword) {
        //1 and 8
        if (!(newPassword.matches("^.*[a-zA-Z]+.*$") && newPassword.matches("^.*[0-9]+.*$")
                && newPassword.matches("^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}]+.*$"))) {
            throw new RestException("密码必须包含字母, 数字, 特殊字符");
        }
        //2
        if (!newPassword.matches("^.{8,}$")) {
            throw new RestException("密码长度至少8位");
        }
        //3
        if (newPassword.matches("^.*(.)\\1{2,}+.*$")) {
            throw new RestException("密码不能包含3位及以上相同字符的重复");
        }
        //4
        if (newPassword.matches("^.*(.{3})(.*)\\1+.*$")) {
            throw new RestException("密码不能包含3位及以上字符组合的重复");
        }
        //6
        if (newPassword.matches("^.*[\\s]+.*$")) {
            throw new RestException("密码不能包含空格、制表符、换页符等空白字符");
        }
    }

    /**
     * 修改用户资料
     *
     * @param request
     * @return
     */
    public User modifyProfile(long uid, ProfileEditRequest request) {
        User user = findUser(uid);
        if (!StringUtils.isEmpty(request.getTrueName())) {
            user.setTrueName(request.getTrueName());
        }
        if (!StringUtils.isEmpty(request.getPhone())) {
            //查找是否有相同的手机号
            if (userDao.countByPhoneAndIdNot(request.getPhone(), uid) > 0) {
                throw new RestException("已经有相同的手机号");
            }
            user.setPhone(request.getPhone());
        }
        if (!StringUtils.isEmpty(request.getEmail())) {
            if (userDao.countByEmailAndIdNot(request.getEmail(), uid) > 0) {
                throw new RestException("已经有相同的邮箱");
            }
            user.setEmail(request.getEmail());
        }
        return userDao.save(user);
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


    /**
     * 创建岗位
     *
     * @param add
     * @return
     */


    @Async
    public void updateToken(String token) {
        userTokenDao.updateToken(token, new Date(System.currentTimeMillis() + 30 * 1000 * 60), new Date());
    }


    /**
     * 登录用户对应的私有云账号
     *
     * @param uid
     * @return
     */
    public Result loginFileCloudSystem(long uid) {
        List<Object[]> list = userDao.getUserCloudProfile(uid);
        if (list.size() > 0) {
            Object[] objects = list.get(0);
            //登录私有云
            LoginResponse loginResponse = cloudApi.login(String.valueOf(objects[0]), String.valueOf(objects[1]));
            if (null != loginResponse && loginResponse.getStatus().equals("SUCCESS")) {
                return Result.ok(loginResponse.getResponseCookies().get(0));
            }
        }
        return Result.error();
    }


    /**
     * 登录可操作公共文件柜的私有云账号, 如果没有权限, 那么依然登录原本的账号
     *
     * @param uid
     * @return
     */
    public Result loginFileCloudCommonSystem(long uid) {
        //检查是否有共享文件云权限
//        if (!globalPermissionService.checkPermission(GlobalPermission.Type.COMMON_CLOUD_DISK, 0, uid)) {
//            return loginFileCloudCommonSystem(uid);
//        }
//        if(userDao.checkPermission(Utils.getCurrentUserId(), UserExternalPermission.Permission.COMMON_CLOUD_DISK) == 0){
//            return loginFileCloudSystem(uid);
//        }
        LoginResponse loginResponse = cloudApi.login(cloudCommonUsername, cloudCommonPassword);
        if (null != loginResponse && loginResponse.getStatus().equals("SUCCESS")) {
            return Result.ok(loginResponse.getResponseCookies().get(0));
        }
        return Result.error();
    }


    /**
     * 得到用户的私有云账号
     *
     * @param uid
     * @return
     */
    public String[] getPrivateCloudUsername(long uid) {
        List<Object[]> list = userDao.getUserCloudProfile(uid);
        if (list.size() > 0) {
            String[] result = new String[2];
            int count = 0;
            for (Object o : list.get(0)) {
                result[count++] = String.valueOf(o);
            }
            return result;
        }
        return new String[]{"", ""};
    }

    /**
     * 得到公共私有云账号
     *
     * @param uid
     * @return
     */
    public String[] getCommonCloudUsername(long uid) {
        User user = findUser(uid);
        if (null == user || !user.isSu()) {
            return new String[]{"", ""};
        }
        return new String[]{cloudCommonUsername, cloudCommonPassword};

    }

    public List<Department> findDepartmentsByParent_Id(long pid) {
        List<Department> allDeps;
        if (0 == pid) {
            allDeps = departmentDao.findAll();
        } else {
            allDeps = departmentDao.getChildDeps(pid);
        }

        Map<Long, Department> map = (Map<Long, Department>) allDeps.stream()
                .peek(d -> {
                    d.setChildren(new ArrayList());
                    d.setQuarters(new ArrayList());
                })
                .collect(toMap(Department::getId, d -> d));

        List<Quarters> allQs = quartersDao.findAll();
        for (Quarters q : allQs) {
            Optional.ofNullable(map.get(q.getDepartmentId()))
                    .map(Department::getQuarters)
                    .ifPresent(list -> list.add(q));
        }
        return allDeps.stream()
                .filter(dep -> {
                    if (null == dep.getParentId() || 0 == dep.getParentId()) {
                        return true;
                    } else {
                        Optional.ofNullable(map.get(dep.getParentId()))
                                .map(Department::getChildren)
                                .ifPresent(list -> list.add(dep));
                        return false;
                    }
                }).collect(toList());

    }

    public boolean hasUser(long uid) {
        return userDao.countById(uid) > 0;
    }

    public boolean userHasQuarter(long uid, long qid) {
        return userDao.countUidAndQid(uid, qid) > 0;
    }

    public List<User> findUser(Collection<Long> ids) {
        return userDao.findAllByIdIn(ids);
    }

    public User findUser(final long id) {
        return userDao.findById(id).orElseThrow(new RestException("找不到ID为" + id + "的用户"));
    }

    public User findUserByAccCode(String accCode) {
        return userDao.findTopByAccCode(accCode)
                .orElseThrow(new RestException(String.format("找不到代号为%s的客户经理", accCode)));
    }

    public Department findDepartment(final long id) {
        return departmentDao.findById(id).orElseThrow(new RestException("找不到ID为" + id + "的部门"));
    }


    /**
     * 是否拥有这个用户
     *
     * @param id 用户ID
     * @return boolean
     */
    public boolean exists(final long id) {
        return userDao.existsById(id);
    }

    public Role findRole(long id) {
        return roleDao.findById(id)
                .orElseThrow(new RestException(String.format("找不到ID为%d的角色", id)));
    }

    public Quarters findQuarters(long id) {
        return quartersDao.findById(id)
                .orElseThrow(new RestException(String.format("找不到ID为%d的岗位", id)));
    }

    public Optional<SystemFile> findFile(long id) {
        return systemFileDao.findById(id);
    }


    /**
     * 初始化用户首字母
     *
     * @param user
     */
    public void initLetter(User user) {
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
     *
     * @param pType    授权类型
     * @param objectId 授权关联对象ID
     * @param uType    授权方式
     * @param linkIds  授权方式ID
     * @return
     * @Param info 授权详情, 没有为null
     */


    /**
     * 按类型和对象得到授权列表
     *
     * @param types
     * @param objectId
     * @return
     */


    /**
     * 按详细参数得到授权对象
     *
     * @param type
     * @param oid
     * @param userType
     * @param lid
     * @return
     */


    /**
     * 删除某个对象的所有授权
     *
     * @param id
     * @return
     */

    /**
     * 得到叠加过后的最终授权结果
     *
     * @param uid
     * @return
     */


    /********** 角色相关 *************/

    /**
     * 创建角色
     *
     * @param request
     * @return
     */


    /**
     * 编辑角色
     *
     * @param request
     * @return
     */


    /**
     * 删除角色
     *
     * @param ids
     * @return
     */

    /**
     * 角色添加用户
     *
     * @param rid
     * @param uids
     * @return
     */


    /**
     * 角色删除用户
     *
     * @param rid
     * @param uids
     * @return
     */


    /**
     * 用户批量设置角色
     *
     * @param uid
     * @param rids
     * @return
     */


    /**
     * 用户批量删除角色
     *
     * @param uid
     * @param rids
     * @return
     */


    /**************** 部门相关 ******************/


    /**
     * create new department
     *
     * @param add
     * @return
     */

    /**
     * delete department
     * if error, throws RestException and roolback
     *
     * @param id
     * @return
     */


    /**
     * check if user is SuperUser
     *
     * @param uid
     * @return
     */
    public boolean isSu(long uid) {
        return userDao.countByIdAndSuIsTrue(uid) > 0;
    }


    /*********8 工具类函数 *************/


    /**
     * 检查一个部门是不是另一个部门的子部门
     *
     * @param pid
     * @param cid
     * @return
     */
    public boolean isChildDepartment(long cid, long pid) {
        List pobj = departmentDao.getDepartmentCode(pid);
        List cobj = departmentDao.getDepartmentCode(cid);
        return cobj.get(0).toString().startsWith(pobj.get(0).toString());
    }

    /**
     * 检查一个岗位是否隶属某个部门
     *
     * @param qid
     * @param did
     * @return
     */
    public boolean isChildQuarter(long qid, long did) {
        List pobj = departmentDao.getDepartmentCode(did);
        List qobj = quartersDao.getQuartersCode(qid);
        return qobj.get(0).toString().startsWith(pobj.get(0).toString());
    }


    /**
     * 检查用户是否隶属于某个部门
     *
     * @param uid
     * @param did
     * @return
     */
    public boolean isUserFromDepartment(long uid, long did) {
        List<Object[]> qids = userDao.getQids(uid);
        return qids.stream().anyMatch(qid -> isChildDepartment((Long) qid[0], did));
    }

    public boolean hasQuarters(long uid, long qid) {
        return userDao.hasQuarters(uid, qid) > 0;
    }

    public boolean departmentHasQuarters(long did, long qid) {
        return departmentDao.departmentHasQuarters(did, qid) > 0;
//        return departmentDao.departmentHasQuarters(did,qid) > 0;
    }


    /**
     * 得到某个部门的所有用户ID
     *
     * @param dids
     * @return
     */
    public List<Long> getUidsFromDepartment(Long... dids) {
        return entityManager.createQuery("select u.id from User u join u.quarters q where (select count(d) from Department d where q.code like concat(d.code,'%') and d.id in :dids) > 0")
                .setParameter("dids", Arrays.asList(dids))
                .getResultList();
    }

    public List<Long> getDidsFromDepartment(long did) {
        return departmentDao.getChildDepIds(did);
    }

    /**
     * 得到某个岗位的所有用户ID
     *
     * @param qids
     * @return
     */
    public List<Long> getUidsFromQuarters(Long... qids) {
        return entityManager.createQuery("select u.id from User u join u.quarters q where q.id in :qids")
                .setParameter("qids", Arrays.asList(qids))
                .getResultList();
    }


    /**
     * 得到某个岗位的所有用户ID
     *
     * @param uid
     * @return
     */
    public List<Long> getQidsFromUser(long uid) {
        return entityManager.createQuery("select q.id from User u join u.quarters q where u.id = :uid")
                .setParameter("uid", uid)
                .getResultList();
    }

    private JSONObject getChildItemByIndex(JSONObject item, String path) {
        String[] ps = path.trim().split("\\.");
        JSONObject result = item;
        for (String p : ps) {
            Object obj = result.get(p);
            if (null == obj || !(obj instanceof JSONObject)) {
                return new JSONObject();
            }
            result = (JSONObject) obj;
        }
        return result;
    }


}
