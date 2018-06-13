package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Quarters;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.entity.UserExternalPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IUserDao extends JpaRepository<User,Long> ,JpaSpecificationExecutor {
//    User findFirstByName(String userName);
    User findByUsername(String userName);
    Optional<User> findFirstByUsername(String userName);
    User findFirstByUsernameOrPhone(String userName, String phone);
    void deleteAllByIdIsGreaterThan(long id);
    Page<User> findAllByUsername(String userName, Pageable pageable);
    List<User> findAllByIdIn(Set<Long> ids);

    Optional<User> findFirstByPhone(String phone);

    List<User> findAllByIdIn(List<Long> ids);


    Optional<User> findFirstByUsernameAndPassword(String username, String password);

    //查找用户ID
    @Query(value = "select user.id from User user where user.username = :username and user.password = :password")
    List getUserId(@Param("username") String username, @Param("password") String password);

    @Query(value = "select user.privateKey from User user where user.id = :id")
    List getPrivateKey(@Param("id") long id);

    @Query(value = "select user.privateKey from User user where user.id = :id")
    List getPublicKey(@Param("id") long id);

    int countById(Long id);
    @Query(value = "select count(user.id) from User user join user.quarters q where q.id = :qid and user.id = :uid")
    int countUidAndQid(@Param("uid") long uid, @Param("qid") long qid);

    //查找私有云账号密码
    @Query(value = "select user.profile.cloudUsername, user.profile.cloudPassword from User user where user.id = :uid")
    List getUserCloudProfile(@Param("uid") long uid);

    //检查是否有某个权限
    @Query(value = "select count(user) from User user join user.externalPermissions per where per.permission = :permission and user.id = :id")
    int checkPermission(@Param("id") long uid, @Param("permission") UserExternalPermission.Permission permission);

    @Modifying
    @Transactional
    @Query(value = "update User u set u.privateKey = :privateKey, u.publicKey = :publicKey where u.username = :username")
    public void updateUserKeys(@Param("privateKey") String privateKey, @Param("publicKey") String publicKey, @Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "delete from t_user_quarters WHERE user_id = :uid",nativeQuery = true)
    void deleteUserQuarters(@Param("uid") long uid);


    @Modifying
    @Query("update User set baned = :baned where id in :ids")
    void updateBanedByIds(
            @Param(value = "ids") long[] ids,
            @Param(value = "baned") boolean baned);

    @Modifying
    @Query("update User set quarters = :quarters where id = 1 ")
    void test(@Param("quarters") List<Quarters> quarters);

    @Transactional
    @Modifying
    @Query(value = "delete from t_user_quarters where user_id = :id", nativeQuery = true)
    void test2(@Param("id") int id);

    @Transactional
    @Modifying
    @Query(value = "delete from User where username <> :username")
    void clearUsers(@Param("username") String username);


    @Query(value = "select u.id,u.trueName,u.phone,u.profile.faceId,q.id,u.letter,u.username from User u join u.quarters q where q.id > 0 and u.baned = false ")
    List getNormalUsers();

    @Query(value = "select u.id,u.trueName,u.phone,u.profile.faceId from User u join u.quarters q join q.department d where d.id = :id")
    List getSimpleUsersFromDepartment(@Param("id") long id);

//    User findByUserNameOrEmail(String username, String email);

//    @Transactional
//    default boolean updateRoles(Integer userId,Set<Role> roles) throws RuntimeException{
//        if(userId == null){
//            return false;
//        }
//        User user = this.findOne(userId);
//        if(user == null){
//            return false;
//        }
//        IRoleDao roleDao = (IRoleDao) SpringContextUtils.getBean(IRoleDao.class);
//
//        //注意，这里需要双向解除关联，才会生效
//        //解除role和user的关联
//        for(Role r : user.getRoles()){
//            if(r.getUsers().contains(user)){
//                r.getUsers().remove(user);
//                roleDao.save(r);
//            }
//        }
//        //解除和角色的关联
//        user.setRoles(new HashSet<>());
//        this.save(user);
//
//        Set<Role> updateRoles = new HashSet<>();
//        //验证每一个roles是否存在
//        for(Role role : roles){
//            if(role.getId() == null){
//                continue;
//            }
//            role = roleDao.findOne(role.getId());
//            if(role == null){
//                continue;
//            }
//            updateRoles.add(role);
//            role.getUsers().add(user);
//            roleDao.save(role);
//        }
//        if(updateRoles.size() == 0){
//            throw new RuntimeException();
//        }
//
//        user.setRoles(updateRoles);
//        User result = this.save(user);
//        return result != null && result.getRoles().size() > 0;
//    }
//
//    /**
//     * 得到一个用户所有的工作流
//     * 被序列化的用户无法使用这个懒加载，所以在这里重新取出一次，否则会丢失当时的session
//     * @return
//     */
////    default List<WorkFlow> getUserWorkFlows(User user){
//////        IDepartmentDao departmentDao = (IDepartmentDao) SpringContextUtils.getBean(IDepartmentDao.class);
////        IRoleDao roleDao = (IRoleDao) SpringContextUtils.getBean(IRoleDao.class);
////        List<Role> roles = roleDao.findAllByUsers(Arrays.asList(new User[]{user}));
////        return roles.stream()
////                .map(role -> role.getDepartment().getWorkFlows())
////                .flatMap(Set::stream)
////                .distinct().collect(Collectors.toList());
//////        Set<Role> roles = user.getRoles();
//////        List<WorkFlow> workFlows = new ArrayList<>();
//////        for(Role role : roles){
//////            Department department = role.getDepartment();
////////            Department department = departmentDao.findOne();
//////            workFlows.addAll(department.getWorkFlows());
//////        }
//////        return workFlows;
////    }
//
//




}