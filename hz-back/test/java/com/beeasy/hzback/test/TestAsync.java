package com.beeasy.hzback.test;

import bin.leblanc.faker.Faker;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.entity.CloudDirectoryIndex;
import com.beeasy.hzback.modules.system.entity.User;
import com.beeasy.hzback.modules.system.form.UserAdd;
import com.beeasy.hzback.modules.system.service.MessageService;
import com.beeasy.hzback.modules.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAsync {

    @Autowired
    EntityManager entityManager;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Autowired
    IUserDao userDao;

    @Test
    public void test2() throws RestException {

        UserAdd userAdd = new UserAdd();
        userAdd.setPassword("f");
        userAdd.setUsername(Faker.getName());
        userAdd.setPhone(Faker.getPhone());
        User user = userService.createUser(userAdd);

        messageService.sendMessage(1, Collections.singleton(user.getId()),"fuck","you",null);
        Page<?> page = messageService.getMessages(1,null,new PageRequest(0,100));
        int b = 1;

    }

    @Test
    public void test(){
        log.info("f");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CloudDirectoryIndex> query = cb.createQuery(CloudDirectoryIndex.class);
        Root root = query.from(CloudDirectoryIndex.class);
        query.select(root);
//        query.where(cb.equal(null,null));
        TypedQuery<CloudDirectoryIndex> q = entityManager.createQuery(query);
        List<CloudDirectoryIndex> result = q.getResultList();

        Query query2 = entityManager.createNativeQuery("SELECT * FROM t_user WHERE username = :a");
        query2.setParameter("a","1");
        query2.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query2.setMaxResults(10);
        List list = query2.getResultList();
        int c = 1;

    }

}
