package bin.leblanc.test;

import com.beeasy.hzback.Application;
import com.beeasy.hzback.modules.system.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestJpa {

    @Autowired
    EntityManager entityManager;

    @Test
    public void test(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root root = query.from(User.class);
        query.multiselect(root.joinSet("roles"));

        TypedQuery<Tuple> q = entityManager.createQuery(query);
        List<Tuple> result = q.getResultList();

        query.multiselect(cb.sum(root.get("id")),(cb.count(root)));
        q = entityManager.createQuery(query);
        result = q.getResultList();

//        log.info(result.get(0).get(0).toString());
        log.info(result.get(0).equals(result.get(1)) ? "1" : "0");
    }

    @Test
    public void testSelect(){
        List o = entityManager.createQuery("select user.id, user.quarters from User user").getResultList();
        int c = 1;
    }

}
