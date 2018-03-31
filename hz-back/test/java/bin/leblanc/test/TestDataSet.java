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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

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
                        .setPath("roles");
                })
                .addExtern("ds",Department.class,model -> {
                    model
                        .setPath("roles","department");
                })
                .addExtern("menus",SystemMenu.class,model -> {
                    model.setPath("systemMenus");
                });

        DataSetResult result = dataSet.newSearch();
        JSONObject ret = result
                .clearCondition()
                .addCondition("id",1)
                .search();

        log.info(JSON.toJSONString(ret));


    }

    @Test
    public void testNativeDataset() throws NullParamValueException {
        NativeDataSet nativeDataSet = dataSetFactory.createNativeDataSet();
        nativeDataSet.setBaseSql("select * from t_user where id > $id and username = $nodeName")
                .setParamType("id","int",null)
                .setParamType("name","string",null)
                .setResultFileter(item -> {
                    item.remove("baned");
                    return item;
                });

        NativeDataSetResult result = nativeDataSet.newSearch();
        List list = result
                .setParam("id",0)
                .setParam("name","1")
                .search();

        Assert.assertTrue(list.size() > 0);
        Assert.assertTrue(!((Map)list.get(0)).containsKey("baned"));

        log.info(list.toString());


    }
}
