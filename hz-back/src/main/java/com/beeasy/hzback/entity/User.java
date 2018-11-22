package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.beeasy.hzback.core.helper.ChineseToEnglish;
import com.beeasy.hzback.view.DManager;
import com.beeasy.hzback.view.GPC;
import com.beeasy.mscommon.Result;
import com.beeasy.hzback.modules.cloud.CloudService;
import com.beeasy.hzback.modules.system.service.FileService;
import com.beeasy.mscommon.RestException;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.Unique;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.json.simple.JsonObject;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.query.LambdaQuery;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.Crypto;
import org.osgl.util.S;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.DigestUtils;

import javax.validation.constraints.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import static com.beeasy.mscommon.valid.ValidGroup.*;
import static java.util.stream.Collectors.toSet;

@Table(name = "T_USER")
@Getter
@Setter
public class User extends TailBean implements ValidGroup {
    @Null(groups = Add.class)
    @NotNull(groups = Edit.class)
    @AssignID("simple")
    Long id;

    @NotBlank(message = "用户名不能为空", groups = {Add.class})
    @Unique(value = "用户名")
    String username;

    @JSONField(serialize = false)
    @NotBlank(message = "密码不能为空", groups = {Add.class})
    String password;

    @Digits(integer = 11, fraction = 0, message = "联系电话格式错误", groups = {Add.class, Edit.class})
    String phone;

    @NotBlank(message = "真实姓名不能为空", groups = {Add.class, Edit.class})
    String trueName;
    Boolean su;
    Boolean newUser;

    @Unique(value = "信贷系统代码")
    String  accCode;

    String  avatar;
    String  letter;
    @Email(message = "邮箱格式错误", groups = {Add.class, Edit.class})
    String email;
    Boolean baned;

    String cloudUsername;
    String cloudPassword;

    Date addTime;


//    @AssertTrue(message = "已经有同名用户", groups = {Add.class, Edit.class})
//    protected boolean getZValidName() {
//        username = username.trim();
//        LambdaQuery<User> query = U.getSQLManager().lambdaQuery(User.class)
//            .andEq(User::getUsername, username);
//        if (null == id) {
//            //fix
//            addTime = new Date();
//            password = DigestUtils.md5DigestAsHex(password.getBytes());
//            su = false;
//            baned = false;
//            newUser = false;
//            initLetter(this);
//            return query.count() == 0 ;
//        } else {
//            return query.andNotEq(User::getId, id).count() == 0;
//        }
//    }
//
//    @AssertTrue(message = "已经有相同的组织机构代码", groups = {Add.class,Edit.class})
//    protected boolean getZValidAccCode(){
//        LambdaQuery<User> query = U.getSQLManager().lambdaQuery(User.class)
//            .andEq(User::getAccCode, accCode);
//        if(null == id){
//            return query.count() == 0;
//        }
//        else {
//            return query.andNotEq(User::getId, id).count() == 0;
//        }
//    }

    @Override
    public String onGetListSql(Map<String,Object> params) {
        return "user.查询用户列表";
    }

    @Override
    public Object onAdd(SQLManager sqlManager) {
        //init
        addTime = new Date();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        su = false;
        baned = false;
        newUser = false;
        initLetter(this);
        //save
        sqlManager.insert(this,true);

        //插入私有云账号
        CloudService cloudService = U.getBean(CloudService.class);
        FileService fileService = U.getBean(FileService.class);
        Environment env = U.getBean(Environment.class);
        cloudService.createUser(username);
        //无论成功与否, 都保存
        sqlManager.lambdaQuery(User.class)
            .andEq(User::getId, id)
            .updateSelective(C.newMap(
                "cloudUsername", username
                , "cloudPassword", env.getProperty("filecloud.userDefaultPassword")
            ));

        //保存头像
        ClassPathResource resource = new ClassPathResource("static/default_face.jpg");
        //upload image
        try (
            InputStream is = resource.getInputStream()
        ) {
            MockMultipartFile file = new MockMultipartFile(resource.getFilename(), is);
            fileService.uploadFace(id, file);
        } catch (IOException e) {
            Assert(false, "保存头像失败");
        }

        //更新岗位
        List<String> oids = (List<String>) get("oids");
        //默认的角色
        Long oid = sqlManager.lambdaQuery(Org.class)
            .andEq(Org::getName,"基础角色")
            .select(Org::getId)
            .stream()
            .map(Org::getId)
            .findFirst()
            .orElse(null);
        if(!oids.contains(oid)){
            oids.add(oid + "");
        }
        if(C.notEmpty(oids)){
            setO(id, oids);
        }


        return this;
    }


    @Override
    public void onBeforeAdd(SQLManager sqlManager) {
       User.AssertMethod("系统设置.组织架构.用户列表");
    }

    @Override
    public void onBeforeEdit(SQLManager sqlManager) {
        //只有拥有权限和我自己才可以编辑
        Assert(Objects.equals(id, AuthFilter.getUid()) || User.hasMethod("系统设置.组织架构.用户列表"), "权限验证失败");
    }

    @Override
    public Object onEdit(SQLManager sqlManager) {
        //禁止修改用户名
        User u = $.map(this).filter("+id,+password,+phone,+email,+baned,+trueName,+accCode").to(User.class);
        if (S.notBlank(u.getPassword())) {
            isValidPassword(u.getPassword());
            u.setPassword(DigestUtils.md5DigestAsHex(u.getPassword().getBytes()));
        }
        initLetter(u);
        sqlManager.lambdaQuery(User.class)
            .andEq(User::getId, u.getId())
            .updateSelective(u);
        return this;
    }

    @Override
    public Object onAfterEdit(SQLManager sqlManager, Object object) {
        if(C.notEmpty((Collection<?>) get("oids"))){
            setO(id, (List) get("oids"));
        }
        return this;
    }

    @Override
    public JSONObject onAfterGetOne(SQLManager sqlManager, JSONObject object) {
        //补充岗位信息
        List<JSONObject> objects = sqlManager.select("user.查询组织机构列表(通过用户ID)", JSONObject.class, C.newMap("uid", object.getLong("id")));
        object.put("os", objects);
        return object;
    }

    @Override
    public void onDelete(SQLManager sqlManager, Long[] id) {
        sqlManager.lambdaQuery(User.class)
            .andIn(User::getId, Arrays.asList(id))
            //管理员禁止删除
            .andEq(User::getSu, false)
            .delete();
        String ids = Arrays.stream(id).map(i -> "'" + i + "'").collect(Collectors.joining(","));
        sqlManager.executeUpdate(
            new SQLReady(S.fmt("delete from t_user_org where uid in (%s)", ids))
        );
    }

    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        switch (action) {
            //修改密码
            case "modifyPass":
                String oldPass = object.getString("oldPass");
                String newPass = object.getString("newPass");
                oldPass = DigestUtils.md5DigestAsHex(oldPass.getBytes());
                newPass = DigestUtils.md5DigestAsHex(newPass.getBytes());
                int count = sqlManager.lambdaQuery(User.class)
                    .andEq(User::getId, AuthFilter.getUid())
                    .andEq(User::getPassword, oldPass)
                    .updateSelective(C.newMap("password", newPass, "newUser", 0));
                Assert(count > 0, "旧密码不正确或旧密码同新密码相同!");
                break;

            //设置岗位
            case "setO":
                setO(object.getLong("uid"), object.getJSONArray("oids"));
                break;


            //角色人员列表
//            case "getRUList":
//                PageQuery<JSONObject> page = U.beetlPageQuery("user.查询用户列表",JSONObject.class,C.newMap());
//                LinkedList list = new LinkedList(page.getList());
//                list.addAll(0,sqlManager.execute(new SQLReady("select uid as id, uname as username, utname as true_name, phone from t_org_user where oid = " + object.getLong("oid")),JSONObject.class));
//                page.setList(list);
//                return page;

            case "setBaned":
                sqlManager.lambdaQuery(User.class)
                    .andIn(User::getId, object.getJSONArray("uids"))
                    .updateSelective(C.newMap("baned", object.getBoolean("baned")));
                break;

            case "getPList":
                return sqlManager.select("user.查询授权列表", JSONObject.class, object);

            case "getMPList":
                return sqlManager.lambdaQuery(GP.class)
                    .andEq(GP::getType, GP.Type.USER_METHOD)
                    .andEq(GP::getOid, object.getLong("oid"))
                    .select(GP::getK1)
                    .stream()
                    .map(GP::getK1)
                    .collect(toSet());

            case "setMP":
                sqlManager.lambdaQuery(GP.class)
                    .andEq(GP::getObjectId, 0)
                    .andEq(GP::getOid, object.getLong("oid"))
                    .andEq(GP::getType, GP.Type.USER_METHOD)
                    .delete();
                List<GP> gps = object.getJSONArray("methods").stream()
                    .map(m -> {
                        GP gp = new GP();
                        gp.setObjectId(0L);
                        gp.setOid(object.getLong("oid"));
                        gp.setType(GP.Type.USER_METHOD);
                        gp.setK1((String)m);
                        return gp;
                    })
                    .collect(Collectors.toList());
                sqlManager.insertBatch(GP.class,gps);
                break;

            /**
             * 得到我被授权的功能
             * 如果是管理员, 那么默认获取所有的授权
             */
            case "getMyMethods":
                boolean su = sqlManager.lambdaQuery(User.class)
                    .andEq(User::getId,AuthFilter.getUid())
                    .andEq(User::getSu,true)
                    .count() > 0;
                if(su){
                    return (C.newList("_all_"));
                }
                Object methods = sqlManager.lambdaQuery(GPC.class)
                    .andEq(GPC::getUid, AuthFilter.getUid())
                    .andEq(GPC::getType, GP.Type.USER_METHOD)
                    .select()
                    .stream()
                    .map(GPC::getK1)
                    .collect(toSet());
                return (methods);


            /**
             * 检查是否为主管
             * 开放所有权限
             */
            case "isManager":
                return sqlManager.lambdaQuery(DManager.class)
                    .andEq(DManager::getUid, AuthFilter.getUid())
                    .count() > 0;

        }
        return null;
    }

    private void initLetter(User user) {
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

    private void setO(long uid, List<String> oids){
        SQLManager sqlManager = U.getSQLManager();
        sqlManager.executeUpdate(new SQLReady(S.fmt("delete from t_user_org where uid = %d", uid)));
        for (String oid : oids) {
            sqlManager.executeUpdate(new SQLReady(
                S.fmt("insert into t_user_org(uid,oid)values(%d,%s)", uid, oid)
            ));
        }
    }
    private void setO(long uid, JSONArray oids){
        SQLManager sqlManager = U.getSQLManager();
        sqlManager.executeUpdate(new SQLReady(S.fmt("delete from t_user_org where uid = %d", uid)));
        for (Object oid : oids) {
            sqlManager.executeUpdate(new SQLReady(
                S.fmt("insert into t_user_org(uid,oid)values(%d,%s)", uid, oid)
            ));
        }
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
     */
    private void isValidPassword(String newPassword) {
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
     * 是否拥有某些权限
     * @param methodNames
     */
    public static boolean hasMethod(long uid, String ...methodNames){
        return U.assertFromSql("user.是否拥有权限", C.newMap("uid",uid,"methods", methodNames));
    }

    public static boolean hasMethod(String ...methods){
        return hasMethod(AuthFilter.getUid(), methods);
    }

    public static void AssertMethod(long uid, String ...methodNames){
        if(!hasMethod(uid, methodNames)){
            throw new RestException("权限验证失败");
        }
    }

    public static void AssertMethod(String ...methodNames){
        AssertMethod(AuthFilter.getUid(), methodNames);
    }
}
