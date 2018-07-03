package com.beeasy.hzback;

import bin.leblanc.faker.Faker;
import com.beeasy.hzback.core.exception.RestException;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.cloud.CloudAdminApi;
import com.beeasy.hzback.modules.cloud.CloudApi;
import com.beeasy.hzback.modules.cloud.CloudService;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.*;
import com.beeasy.hzback.modules.system.service.*;
import jdk.nashorn.internal.objects.Global;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
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
    @Autowired
    WorkflowService workflowService;
    @Autowired
    IWorkflowModelDao workflowModelDao;
    @Autowired
    IDepartmentDao departmentDao;
    @Autowired
    QuartersService quartersService;


    @Test
    public void init() throws RestException {
        createDepartments();
        createWorkflows();
//        give();
    }

    @Test
    public void give() {

        List<Quarters> qs = quartersDao.findAll();
        List<WorkflowModel> models = workflowModelDao.findAll();
        for (WorkflowModel model : models) {
//            userService.addGlobalPermission(GlobalPermission.Type.WORKFLOW_POINTER, model.getId(), GlobalPermission.UserType.QUARTER, qs.stream().map(Quarters::getId).collect(Collectors.toList()), null);
        }
    }


    @Test
    public void createWorkflows() {
        workflowModelDao.deleteAll();
        String[] modelNames = {"菜单权限申请", "资料收集", "不良资产登记", "利息减免", "抵债资产接收", "资产处置", "不良资产管理流程", "贷后跟踪-小微部公司类", "贷后跟踪-小微部个人类",};
        for (String modelName : modelNames) {
            WorkflowModelAdd edit = new WorkflowModelAdd();
            edit.setName(modelName);
            edit.setVersion(BigDecimal.valueOf(1.0 + 1 * 0.01));
            Result<WorkflowModel> result = workflowService.createWorkflow(modelName, edit);
            Assert.assertTrue(result.isSuccess());
        }
        if(true) return;

        //列出2个版本
        for (int i = 0; i < 2; i++) {
            for (String modelName : modelNames) {
                WorkflowModelAdd edit = new WorkflowModelAdd();
                edit.setName(modelName);
                edit.setVersion(BigDecimal.valueOf(1.0 + i * 0.01));
                Result<WorkflowModel> result = workflowService.createWorkflow(modelName, edit);

                if(null == result.getData()){
                    int d = 1;
                }
                WorkflowModel workflowModel = result.getData();
                //附加人员
                List<Department> departments = departmentDao.findAllByName("部门1");
                Assert.assertTrue(departments.size() > 0);
                Department department = departments.get(0);

                String[] qnames = {"岗位1", "岗位2", "岗位3", "岗位4"};

                List<WorkflowQuartersEdit> list = new ArrayList<>();
                workflowModel.getNodeModels().forEach((v) -> {
                    WorkflowQuartersEdit qedit = new WorkflowQuartersEdit();
                    qedit.setNodeId(v.getId());
                    qedit.setName(v.getName());
                    for (String qname : qnames) {
                        Quarters quarters = quartersDao.findFirstByDepartmentAndName(department, qname);
                        qedit.getMainQuarters().add(quarters.getId());
                    }
                    list.add(qedit);
                });
                WorkflowQuartersEdit[] edits = new WorkflowQuartersEdit[list.size()];
                edits = list.toArray(edits);
//                workflowService.setPersons(edits);

                //打开工作流
                workflowService.editWorkflowModel(new WorkflowModelEdit(){{
                    setId(workflowModel.getId());
                    setOpen(true);
                }});
            }
        }
    }


    @Autowired
    DepartmentService departmentService;
    @Autowired
    IQuartersDao quartersDao;
    @Autowired
    ISystemFileDao systemFileDao;


    @Test
    public void createAdmin() throws RestException {
        User user = userDao.findByUsername("1");
        if (user != null) {
            userDao.delete(user);
        }
        UserAdd add = new UserAdd();
        add.setPhone(Faker.getPhone());
        add.setUsername("1");
        add.setPassword("2");
        add.setTrueName("管理员");
//        add.setTrueName(Faker);
        add.setBaned(false);
        userService.createUser(add);


    }

    @Test
    public void createDepartments() throws RestException {
        systemFileDao.deleteAll();
        userDao.clearUsers("1");
        createAdmin();
        departmentDao.deleteAll();
        quartersDao.deleteAll();

        if(true){
            return;
        }
        int qcount = 0;
        //设立4个部门
        for (int i = 0; i < 4; i++) {
            String dName = "部门" + (i + 1);
            DepartmentAdd departmentAdd = new DepartmentAdd();
            departmentAdd.setName(dName);
            Result<Department> result = departmentService.createDepartment(departmentAdd);
            Assert.assertTrue(result.isSuccess());

            //每个部门4个角色
            for (int j = 0; j < 4; j++) {
                qcount++;
                String qName = "岗位" + qcount;
                QuartersAdd quartersAdd = new QuartersAdd();
                quartersAdd.setDepartmentId(result.getData().getId());
                quartersAdd.setName(qName);
                Result<Quarters> ret = userService.createQuarters(quartersAdd);
                Assert.assertTrue(ret.isSuccess());
                for (int n = 0; n < 5; n++) {
                    UserAdd userAdd = new UserAdd();
                    userAdd.setPhone(Faker.getPhone());
                    String userName = Faker.getTrueName();
                    userAdd.setTrueName(userName);
                    userAdd.setUsername(userName);
                    userAdd.setPassword("2");
                    userAdd.setBaned(false);
                    Result<User> r = userService.createUser(userAdd);
                    if (!r.isSuccess()) {
                        continue;
                    }
                    UserEdit edit = new UserEdit();
                    Set<Long> qs = new HashSet<>();
                    qs.add(ret.getData().getId());
                    edit.setQuarters(qs);
                    edit.setId(r.getData().getId());
                    userService.editUser(edit);
                }
            }
        }

    }


    @Test
    public void test2() {
        List list = workflowModelDao.getAllWorkflows();
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void test() {
        log.info("f");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CloudDirectoryIndex> query = cb.createQuery(CloudDirectoryIndex.class);
        Root root = query.from(CloudDirectoryIndex.class);
        query.select(root);
//        query.where(cb.equal(null,null));
        TypedQuery<CloudDirectoryIndex> q = entityManager.createQuery(query);
        List<CloudDirectoryIndex> result = q.getResultList();

        Query query2 = entityManager.createNativeQuery("SELECT * FROM t_user WHERE username = :a");
        query2.setParameter("a", "1");
        query2.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query2.setMaxResults(10);
        List list = query2.getResultList();
        int c = 1;

    }


    @Autowired
    IWorkflowInstanceDao instanceDao;
    @Autowired
    IWorkflowNodeDao workflowNodeDao;

    @Autowired
    CloudService cloudService;

    @Autowired
    CloudDiskService cloudDiskService;
    @Autowired
    SystemConfigCache systemConfigCache;

    @Autowired
    CloudApi cloudApi;
    @Autowired
    CloudAdminApi cloudAdminApi;
    @Autowired
    IUserProfileDao profileDao;

    @Value("${filecloud.userDefaultPassword}")
    String cloudUserPassword;

    @Autowired
    IWorkflowNodeFileDao nodeFileDao;

    @Test
    public void testtags() {
        Query q = entityManager.createQuery("select user.quarters from User user where user.id = 2613");
        q.setMaxResults(10);
        q.setFirstResult(0);
        List list = q.getResultList();
        int c = 1;
//        nodeFileDao.updateNodeFileTags(559,14, "cubi 123");
//        int d = 0;
    }

    @Test
    public void testUserSelect(){
//        URL urls = new URL(url);
//        HttpURLConnection connection = null;
//        OutputStream outputStream = null;
//        String rs = null;
//        try {
//            connection = (HttpURLConnection) urls.openConnection();
//            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----footfoodapplicationrequestnetwork");
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
//            connection.setRequestProperty("Accept", "*/*");
//            connection.setRequestProperty("Range", "bytes="+"");
//            connection.setConnectTimeout(8000);
//            connection.setReadTimeout(20000);
//            connection.setRequestMethod("POST");
//
//            StringBuffer buffer = new StringBuffer();
//            int len = 0;
//            if(parameter != null)
//                len = parameter.size();
//
//            for(int i = 0; i < len; i++) {
//                buffer.append("------footfoodapplicationrequestnetwork\r\n");
//                buffer.append("Content-Disposition: form-data; name=\"");
//                buffer.append(parameter.getKey(i));
//                buffer.append("\"\r\n\r\n");
//                buffer.append(parameter.getValue(i));
//                buffer.append("\r\n");
//            }
//            if(parameter != null)
//                buffer.append("------footfoodapplicationrequestnetwork--\r\n");
//            outputStream = connection.getOutputStream();
//            outputStream.write(buffer.toString().getBytes());
//            try {
//                connection.connect();
//                if(connection.getResponseCode() == 200) {
//                    rs = getWebSource(connection.getInputStream());
//                }
//            }
//            catch (Exception e) {
//                rs = null;
//            }
//
//            return rs;
//        }
//        finally {
//            try {
//                outputStream.close();
//            }
//            catch (Exception e) {
//            }
//            outputStream = null;
//
//            if(connection != null)
//                connection.disconnect();
//            connection = null;
//        }

        List<User> users = userDao.findAll();
        users.forEach(user -> {
            user.getProfile().setCloudUsername(user.getUsername());
            user.getProfile().setCloudPassword(cloudUserPassword);
            profileDao.save(user.getProfile());
//
//            cloudService.adminCreateUser(user.getUsername());
            cloudService.createUser(user.getUsername());
        });
//        try {
//            LoginResponse loginResponse = cloudApi.login("admin","admin");
//            CloudAdminApi.Config.setCookie(loginResponse.getResponseCookies().get(0));
//            Map map = systemConfigCache.getCreateUserString();
//            map.put("user.username","fuckfei234");
//            Object result = cloudAdminApi.adminCreateUser(map);
//            int c = 1;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        boolean flag = cloudService.login("llyb120","1q2w3e4r");
//        Assert.assertTrue(flag);
//
//        flag = cloudService.checkOnline();
//        long dirId = cloudDiskService.prepareFileCloud(2189);
//        Assert.assertTrue(dirId > 0);
//        List<WorkflowNode> workflowNodes = workflowNodeDao.findAll();
//        for (WorkflowNode workflowNode : workflowNodes) {
//            if(workflowNode.getType().equals("check")){
//                CheckNode checkNode = (CheckNode) workflowNode.getNode();
//                if(checkNode != null){
//                    checkNode.setKey("key");
//                    workflowNodeDao.save(workflowNode);
//                }
//            }
//        }
//        List<User> users = userDao.findAll();
//        for (User user : users) {
//            userService.initLetter(user);
//            userDao.save(user);
//        }
//        List s = instanceDao.findCommonWorks(Collections.singletonList(2613L),Long.MAX_VALUE,new PageRequest(0,20));
//        Result.ok(s).toJson(new Result.Entry[]{
//                new Result.Entry(WorkflowInstance.class,"nodeList","simpleChildInstances")
//        });
//        instanceDao.
//        List b = workflowModelDao.findModelId("资料收集");
//        int c = 1;
//        WorkflowExtPermissionEdit edit = new WorkflowExtPermissionEdit();
//        edit.setModelId(97L);
//        edit.setQids(Collections.singletonList(100L));
//        edit.setType(WorkflowExtPermission.Type.POINTER);
//        workflowService.setExtPermissions(edit);
//        userService.findUser(559).ifPresent(user -> {
//            userService.addExternalPermission(559L,UserExternalPermission.Permission.COMMON_CLOUD_DISK);
//        });

//        List s = instanceDao.findDealedWorks(Collections.singletonList(559L),new PageRequest(0,200));
//       List s = instanceDao.findTest();
//       int c = 1;
//        List s = instanceDao.findObserveredWorks(Collections.singletonList(559L),Long.MAX_VALUE);
//        int c = 1;
    }

    @Autowired
    IGlobalPermissionDao globalPermissionDao;
    @Autowired
    IWorkflowNodeInstanceDao nodeInstanceDao;
    @Autowired
    DataSource dataSource;
    @Transactional
    @Test
    public void testtt() throws InterruptedException, SQLException {

        ResultSet rs = dataSource.getConnection().createStatement().executeQuery("SELECT * from ACC_LOAN limit 10");
        List s=  new ArrayList();
        while(rs.next()){
            Map<String, String> hm = new HashMap<String, String>();
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String key = rsmd.getColumnLabel(i);
                String value = rs.getString(i);
                hm.put(key, value);
            }
            s.add(hm);
        }
        List lll =  entityManager.createNativeQuery("SELECT * FROM ACC_LOAN limit 1")
        .getResultList();

        nodeInstanceDao.updateNodeInstanceDealer(1,Collections.singleton(1l));

//        boolean flag = userService.isChildDepartment(38,29);
//        Assert.assertTrue(flag);
//        flag = userService.isChildDepartment(29,38);
//        Assert.assertFalse(flag);

//        userDao.userAddQuarters(155,33);

//        userService.addUsersToQuarters(Collections.singleton(155l),33);w
        int ccc = workflowModelDao.isManagerForWorkflow(155l, 8);
        Assert.assertTrue(ccc > 0);
//        int ccc = globalPermissionDao.hasPermission(74l,Collections.singleton(GlobalPermission.Type.WORKFLOW_PUB),29);

//    Object o =         instanceDao.findObserveredWorks(Collections.singletonList(GlobalPermission.Type.WORKFLOW_OBSERVER), Collections.singleton(414l), Long.MAX_VALUE, new PageRequest(0,100));
    int c = 1;
//    Assert.assertTrue(s.size() > 0);
//        WorkflowModel model = workflowService.findModel(61).orElse(null);
//        WorkflowModelEdit edit = new WorkflowModelEdit();
//        edit.setId(model.getId());
//        edit.setDepartmentIds(Collections.singletonList(43L));
//        workflowService.editWorkflowModel(edit);

//        List s = instanceDao.findNeedToDealWorks(Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), Collections.singletonList(414L), Long.MAX_VALUE, new PageRequest(0,10));

        if(true) return;

        userService.addGlobalPermission(GlobalPermission.Type.COMMON_CLOUD_DISK,0, GlobalPermission.UserType.DEPARTMENT, Collections.singletonList(41L), null);
//        Assert.assertTrue(id > 0);

        List list = globalPermissionDao.getUids(Collections.singleton(GlobalPermission.Type.COMMON_CLOUD_DISK),0);
        Assert.assertTrue(list.size() > 0);

//        list = globalPermissionDao.getUids()

//        boolean count = userService.deleteGlobalPermission(id);
//        Assert.assertTrue(count);

        Thread.sleep(5000);
        if(true) return;

        Assert.assertTrue(departmentDao.departmentHasChild(41,45) > 0);
        Assert.assertTrue(departmentDao.departmentHasQuarters(42,98) > 0) ;

        Assert.assertTrue(userDao.hasDepartment(414,41) > 0);
        Assert.assertFalse(userDao.hasDepartment(414,42) > 0);

        List obj = departmentDao.getChildQuartersIds(41);
        int ddddc = 1;

//        obj = userDao.getUidsFromDepartment(Collections.singleton(41l));
//        int d = 1;

        Assert.assertTrue(obj.size() > 0);
//        Assert.assertTrue(obj.contains(414));

//        Assert.assertTrue(userService.hasQuarter(414,91));
//        Assert.assertFalse(userService.hasQuarter(94,414));
//
//        Assert.assertTrue(userService.isChildQuarter(98,42));
//        Assert.assertTrue(userService.isChildQuarter(98,29));
    }

    @Test
    public void fixFace() throws IOException {
        for (SystemFile systemFile : systemFileDao.findAll()) {
            if(systemFile.getType().equals(SystemFile.Type.FACE)){
                ClassPathResource resource = new ClassPathResource("static/default_face.jpg");
                byte[] bytes = IOUtils.toByteArray(resource.getInputStream());
                systemFile.setBytes(bytes);
                systemFileDao.save(systemFile);
            }
        }
    }

    @Test
    public void testwork222(){
        int a = globalPermissionDao.hasPermission(178,Collections.singleton(GlobalPermission.Type.WORKFLOW_MAIN_QUARTER), 259);
        int c = 1;

    }

    @Test
    public void updateWorkflows(){
//        List<WorkflowModel> models = Workflow
    }
}
