package bin.leblanc.test;

import bin.leblanc.dataset.DataSet;
import bin.leblanc.dataset.DataSetFactory;
import bin.leblanc.dataset.DataSetResult;
import com.beeasy.hzback.Application;
import com.beeasy.hzback.modules.setting.entity.Department;
import com.beeasy.hzback.modules.setting.entity.Role;
import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.entity.SystemMenu;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestDataSet {

    @Autowired
    DataSetFactory dataSetFactory;

    @Test
    public void test(){
        DataSet dataSet = dataSetFactory.createDataSet();
        dataSet
                .addMain(User.class,model -> {
                })
                .addExtern(Role.class,model -> {
                    model
                        .setLinkField("roles");
                })
                .addExtern(Department.class,model -> {
                    model
                        .setLinkField("department")
                        .setPath("roles");
                })
                .addExtern(SystemMenu.class,model -> {
                    model.setLinkField("systemMenus")
                        .setAlias("menus");
                });

        DataSetResult result = dataSet.newSearch();
        result
                .addCondition("id",1)
                .search();


    }

}
