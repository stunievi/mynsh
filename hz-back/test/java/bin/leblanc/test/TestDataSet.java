package bin.leblanc.test;

import bin.leblanc.dataset.*;
import bin.leblanc.dataset.exception.NullParamValueException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
                .addExtern("rs",Role.class,model -> {
                    model
                        .setLinkField("roles");
                })
                .addExtern("ds",Department.class,model -> {
                    model
                        .setLinkField("department")
                        .setPath("roles");
                })
                .addExtern("menus",SystemMenu.class,model -> {
                    model.setLinkField("systemMenus");
                });

        DataSetResult result = dataSet.newSearch();
        JSONObject ret = result
                .clearCondition()
                .addCondition("id",1)
                .addCondition("ds","id",9)
                .search();

        log.info(JSON.toJSONString(ret));


    }

    @Test
    public void testNativeDataset() throws NullParamValueException {
        NativeDataSet nativeDataSet = dataSetFactory.createNativeDataSet();
        nativeDataSet.setBaseSql("select * from t_user where id = $id and username = $name")
                .setParamType("id","int",null)
                .setParamType("name","string",null);

        NativeDataSetResult result = nativeDataSet.newSearch();
        result
                .setParam("id",1)
                .setParam("name","2")
                .search();


    }
}
