//package test;
//
//import bin.leblanc.faker.Faker;
//import com.beeasy.hzback.core.exception.RestException;
//import com.beeasy.hzback.modules.setting.entity.User;
//import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
//import com.beeasy.hzback.modules.system.form.UserAdd;
//import com.beeasy.hzback.modules.system.service.CloudDiskService;
//import com.beeasy.hzback.modules.system.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.Assert.*;
//@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class TestCloudDisk {
//    @Autowired
//    UserService userService;
//    @Autowired
//    CloudDiskService cloudDiskService;
//    @Test
//    public void test() throws RestException {
//        UserAdd userAdd = new UserAdd();
//        userAdd.setUsername(Faker.getName());
//        userAdd.setPassword(Faker.getName());
//        userAdd.setPhone(Faker.getPhone());
//        User user =  userService.createUser(userAdd);
//        userAdd.setUsername(Faker.getName());
//        userAdd.setPassword(Faker.getName());
//        userAdd.setPhone(Faker.getPhone());
//        User user2 =  userService.createUser(userAdd);
//
//        //创建文件夹
//        Optional<CloudDirectoryIndex> optional = cloudDiskService.createDirectory(user.getId(),"/fuck/123");
//        assertTrue(optional.isPresent());
//
//        //禁止创建同名
//        optional = cloudDiskService.createDirectory(user.getId(),"/fuck/123");
//        assertFalse(optional.isPresent());
//
//        //检索
//        List<CloudDirectoryIndex> folders = cloudDiskService.getDirectories(user.getId(),"/");
//        assertTrue(folders.size() > 0);
//        folders = cloudDiskService.getDirectories(user.getId(),"/fuck");
//        assertTrue(folders.size() > 0);
//
//        //重命名
//        boolean success = cloudDiskService.renameDirectory(user.getId(),"/fuck/123","/fuck/456");
//        assertFalse(cloudDiskService.findDirectory(user.getId(),"/fuck/123").isPresent());
//        assertTrue(cloudDiskService.findDirectory(user.getId(),"/fuck/456").isPresent());
//
//
//        userService.deleteUser(user.getId());
//    }
//
//
//    @Autowired
//    EntityManager entityManager;
//    @Test
//    public void test1(){
//        log.info("f");
////        CriteriaBuilder cb = entityManager.getCriteriaBuilder();;
////        CriteriaQuery<CloudDirectoryIndex> query = cb.createQuery(CloudDirectoryIndex.class);
////        TypedQuery<CloudDirectoryIndex> q = entityManager.createQuery(query);
////        List<CloudDirectoryIndex> result = q.getResultList();
//
//        int b = 1;
//
//    }
//}
