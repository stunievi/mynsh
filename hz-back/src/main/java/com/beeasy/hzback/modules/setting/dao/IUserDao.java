package com.beeasy.hzback.modules.setting.dao;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.Quarters;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IUserDao extends JpaRepository<User,Long> {
//    User findByName(String userName);
    User findByUsername(String userName);
    User findFirstByUsernameOrPhone(String userName, String phone);
    void deleteAllByIdIsGreaterThan(long id);
    Page<User> findAllByUsername(String userName, Pageable pageable);


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