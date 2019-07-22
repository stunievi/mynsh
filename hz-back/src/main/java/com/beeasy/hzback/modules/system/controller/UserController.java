package com.beeasy.hzback.modules.system.controller;

//import bin.leblanc.zed.Zed;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.util.Log;
import com.beeasy.hzback.entity.Org;
import com.beeasy.hzback.modules.system.cache.SystemConfigCache;
import com.beeasy.hzback.modules.system.service.UserService;
import com.beeasy.mscommon.Result;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

//import com.beeasy.hzback.modules.system.service.UserService;


@Api(tags = "用户API", description = "后台管理用户相关接口，需要有管理员权限")
@RestController
@Transactional
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    @Autowired
    SQLManager sqlManager;

    @Autowired
    public UserController( SystemConfigCache cache,  UserService userService) {
        this.userService = userService;
//        this.storageClient = storageClient;
    }

//    @ApiOperation(value = "添加用户", notes = "")
//    @RequestMapping(value = "/add")
//    public Result add(
//            @Validated @RequestBody User edit
//    ) throws RestException {
//
//        return Result.ok(userService.createUser(edit));
//    }
//
//    @ApiOperation(value = "修改用户", notes = "")
//    @RequestMapping(value = "/edit", method = RequestMethod.POST)
//    public Result edit(
//            @Valid @RequestBody User edit
//    ){
//        userService.editUser(null,edit);
//        return Result.ok();
//    }

    @ApiOperation(value = "批量删除用户")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Result deleteUser(
            @RequestParam Long[] id
    ) {
        userService.deleteUser(Arrays.asList(id));
        return Result.ok();
    }

//    @ApiOperation(value = "批量添加岗位")
//    @RequestMapping(value = "/addUsersToQuarters", method = RequestMethod.GET)
//    public Result userAddQuarter(
//            @RequestParam String uids,
//            @RequestParam long qid
//    ) {
//        return Result.ok(userService.addUsersToQuarters(Utils.convertIdsToList(uids), qid));
//    }

//    @RequestMapping(value = "/getList", method = RequestMethod.GET)
//    public Result getList(
//        BeetlPager beetlPager
//        , @RequestParam Map<String,Object> jsonObject
//    ){
//        return Result.ok(
//                Utils.beetlPageQuery("user.查询用户列表", JSONObject.class, jsonObject, beetlPager)
//        );
//    }




//    @RequestMapping(value = "/ids", method = RequestMethod.GET)
//    public String getList(
//            @RequestParam String ids) {
//        List<User> users = userService.findUser(Utils.convertIdsToList(ids));
//        return Result.okJson(users,
//                new Result.DisallowEntry(Department.class, "children"),
//                new Result.DisallowEntry(Quarters.class, "department", "users")
//        );
//    }

//    @ApiOperation(value = "禁用/启用", notes = "批量更新状态")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userIds", value = "用户主键", required = true, allowMultiple = true),
//            @ApiImplicitParam(name = "isBaned", value = "是否禁用", required = true)
//    })
//    @PutMapping("/updateBaned")
//    public Object updateBaned(
//            long[] userIds,
//            boolean isBaned
//    ) {
////        userDao.updateBanedByIds(userIds, isBaned);
//        return Result.ok();
//    }


//    @ApiOperation(value = "用户功能模块授权")
//    @RequestMapping(value = "/permission/set", method = RequestMethod.POST)
//    public Result setUserMethodPermission(
//            @RequestParam long uid,
//            @Valid @RequestBody JSONArray array
//    ) {
//        return Result.ok(userService.addGlobalPermission(GlobalPermission.Type.USER_METHOD, 0, GlobalPermission.UserType.USER, Collections.singleton(uid), array));
//    }
//
//    @ApiOperation(value = "查看某个人的功能模块授权")
//    @RequestMapping(value = "/permission/get", method = RequestMethod.GET)
//    public Result getUserMethodPermissions(@RequestParam long uid) {
//        return Result.ok(userService.getGlobalPermission(GlobalPermission.Type.USER_METHOD, 0, GlobalPermission.UserType.USER, uid));
//    }
//

//    @RequestMapping(value = "/per/set")
//    public Result setPermissions(
//        @RequestBody GP[] gps
//    ){
//        userService.setGP(gps);
//        return Result.ok();
//    }

    @RequestMapping(value = "/per/getList")
    public Result getPermissions(
        @RequestParam Map<String,Object> params
    ){
//        if(!params.containsKey("type")){
//            params.put("key", C.newMap());
//        }
        return Result.ok(sqlManager.select("user.查询授权列表", JSONObject.class, params));
    }



//    @ApiOperation(value = "修改密码")
//    @RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
//    public Result modifyPassword(
//            @Valid @RequestBody ModifyPasswordRequest request
//    ) {
//        userService.modifyPassword(Utils.getCurrentUserId(), request.getOldPassword(), request.getNewPassword());
//        return Result.ok();
//    }

//    @ApiOperation(value = "修改个人资料")
//    @RequestMapping(value = "/modifyProfile", method = RequestMethod.POST)
//    public Result modifyProfile(
//            @Valid @RequestBody User user
//    ) {
//        userService.editUser(AuthFilter.getUid(), user);
//        return Result.ok();
//    }

//    @ApiOperation(value = "得到我的功能授权")
//    @RequestMapping(value = "/myMethods", method = RequestMethod.GET)
//    public Result getMyMethods() {
//        boolean su = sqlManager.lambdaQuery(User.class)
//            .andEq(User::getId,AuthFilter.getUid())
//            .andEq(User::getSu,true)
//            .count() > 0;
//        if(su){
//            return Result.ok(C.newList("_all_"));
//        }
//        Object methods = sqlManager.lambdaQuery(GPC.class)
//            .andEq(GPC::getUid, AuthFilter.getUid())
//            .andEq(GPC::getType, GP.Type.USER_METHOD)
//            .select()
//            .stream()
//            .map(GPC::getK1)
//            .collect(toSet());
//        return Result.ok(methods);
//    }



//    @ApiOperation(value = "设置用户岗位", notes = "岗位设置, 需一次性传递所有岗位的ID, 无效的岗位会被略过")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户ID", required = true),
//            @ApiImplicitParam(name = "quartersIds", value = "岗位ID", required = true)
//    })
//    @PutMapping("/setQuarters")
//    public Result setQuarters(
//            long userId,
//            long[] quartersIds
//    ) throws CannotFindEntityException {
//        userService.setQuarters(userId,quartersIds);
//        return Result.ok();
////
////        User user = userDao.findOne(userId);
////        if(user == null) return Result.error("没有找到这个用户");
////        List<Quarters> list = Arrays.asList(quartersIds)
////                .stream()
////                .map(id -> quartersDao.findOne(id))
////                .filter(item -> item != null)
////                .collect(Collectors.toList());
//////        user.setQuarters(list);
////        userDao.save(user);
////        return Result.ok();
//    }

//    @ApiOperation(value = "修改用户密码", notes = "只要登录都可以调用")
//    @PutMapping("/changePassword")
//    public Result changePassword(
//            @Valid ChangePassword changePassword,
//            BindingResult bindingResult
//            ){
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if(!user.getPassword().equals(CrUtils.md5(changePassword.getOldPassword().getBytes()))){
//            return Result.error("旧密码不正确");
//        }
//        User u = userDao.findOne(user.getId());
//        u.setPassword(CrUtils.md5(changePassword.getNewPassword().getBytes()));
//        userDao.save(u);
//        return Result.ok();
//    }


//    @ApiOperation(value = "设置用户的功能角色", notes = "采用黑名单模式, 只需要传入禁用的菜单名, 层级使用.分割, 例如菜单1.子菜单2.子菜单3")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "menus", value = "菜单名", allowMultiple = true)
//    })
//    @PutMapping("/setUnbindMenus")
//    public Object setUnbindMenus(String[] menus){
//        return userService.setUnbindMenus(Utils.getCurrentUserId(),menus);
//    }
//



//    @NotSaveLog
//    @RequestMapping(value = "/face/edit", method = RequestMethod.POST)
//    public String uploadFace(@RequestParam long fileId) {
//        return Result.ok(userService.updateUserFace(Utils.getCurrentUserId(),fileId)).toJson();
//    }


    /************ 角色相关 ************/
//    @ApiOperation(value = "创建角色")
//    @RequestMapping(value = "/role/create", method = RequestMethod.POST)
//    public Result createRole(
//            @Validated(RoleRequest.Add.class) @RequestBody RoleRequest request
//    ) {
//        return Result.ok(userService.createRole(request));
//    }
//
//    @ApiOperation(value = "编辑角色")
//    @RequestMapping(value = "/role/edit", method = RequestMethod.POST)
//    public Result editRole(
//            @Validated(value = RoleRequest.Edit.class) @RequestBody RoleRequest request
//    ) {
//        return Result.ok(userService.editRole(request));
//    }

//    @ApiOperation(value = "得到单独角色的信息")
//    @RequestMapping(value = "/role/getOne", method = RequestMethod.GET)
//    public Result getRoleById(
//            @RequestParam long id
//    ) {
//        return Result.ok(userService.findRole(id));
//    }

//    @ApiOperation(value = "编辑角色")
//    @RequestMapping(value = "/role/delete", method = RequestMethod.GET)
//    public Result deleteRoles(
//            @RequestParam String id
//    ) {
//        return Result.ok(userService.deleteRoles(Utils.convertIdsToList(id)));
//    }

//    @RequestMapping(value = "/role/addUsers", method = RequestMethod.GET)
//    @ApiOperation(value = "角色批量添加用户")
//    public Result roleAddUsers(
//            @RequestParam long rid,
//            @RequestParam String uid
//    ) {
//        return Result.ok(userService.roleAddUsers(rid, Utils.convertIdsToList(uid)));
//    }
//
//    @RequestMapping(value = "/role/deleteUsers", method = RequestMethod.GET)
//    @ApiOperation(value = "角色批量删除用户")
//    public Result roleDeleteUsers(
//            @RequestParam long rid,
//            @RequestParam String uid
//    ) {
//        return Result.ok(userService.roleDeleteUsers(rid, Utils.convertIdsToList(uid)));
//    }
//
//    @ApiOperation(value = "用户批量设置角色")
//    @RequestMapping(value = "/setRoles", method = RequestMethod.GET)
//    public Result userSetRoles(
//            @RequestParam long uid,
//            @RequestParam String rid
//    ) {
//        return Result.ok(userService.userSetRoles(uid, Utils.convertIdsToList(rid)));
//    }
//
//    @ApiOperation(value = "用户批量删除角色")
//    @RequestMapping(value = "/deleteRoles", method = RequestMethod.GET)
//    public Result userDeleteRoles(
//            @RequestParam String id
//    ) {
//        return Result.ok(userService.userDeleteRoles(Utils.getCurrentUserId(), Utils.convertIdsToList(id)));
//    }

//    @ApiOperation(value = "查询角色")
//    @RequestMapping(value = "/role/getList", method = RequestMethod.GET)
//    public Result getAllRoles(
//            RoleSearchRequest request,
//            Pager pager,
//            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
//    ) {
//        return Result.ok(userService.searchRoles(request, pageable));
//    }


//    @ApiOperation(value = "增加角色功能模块授权")
//    @RequestMapping(value = "/role/permission/method/set", method = RequestMethod.POST)
//    public Result setRoleMethodPermissions(
//            @RequestParam long rid,
//            @Validated @RequestBody JSONArray array
//    ) {
//        return Result.ok(userService.addGlobalPermission(GlobalPermission.Type.USER_METHOD, 0, GlobalPermission.UserType.ROLE, Collections.singleton(rid), array));
//    }
//
//    @ApiOperation(value = "得到指定角色的功能模块授权")
//    @RequestMapping(value = "/role/permission/method/get", method = RequestMethod.GET)
//    public Result getRoleMethodPermissions(
//            @RequestParam long rid
//    ) {
//        return Result.ok(userService.getGlobalPermission(GlobalPermission.Type.USER_METHOD, 0, GlobalPermission.UserType.ROLE, rid).orElse(null));
//    }
//
//
//    @RequestMapping(value = "/getCloudFileAccount", method = RequestMethod.GET)
//    public Result getCloudFileAccount() {
//        User user = userService.findUser(Utils.getCurrentUserId());
//        if (null == user) {
//            return Result.error();
//        }
//        return Result.ok(new Object[]{user.getProfile().getCloudUsername(), user.getProfile().getCloudPassword()});
//    }


//    @ApiOperation(value = "部门列表", notes = "获得所有部门列表，开放API")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", value = "部门名称, 如果传递了这个属性, 那么会无视parentId进行查找"),
//            @ApiImplicitParam(name = "parentId", value = "父部门ID,如果想获得顶层,请传0")
//    })
//    @GetMapping("/department/getList")
//    public Result list(
//            String name,
//            Long parentId
//    ) {
//        //name比parent优先
//        return Result.ok(userService.findDepartmentsByParent_Id(0));
//    }

    //部门列表
    @RequestMapping(value = "/org/getList")
    public String getList(){
        List<JSONObject> deps = sqlManager.select("user.查询部门列表",JSONObject.class);
        Map<Long,JSONObject> map = C.newMap();
        for (JSONObject dep : deps) {
            map.put(dep.getLong("id"), dep);
            dep.put("children", new JSONArray());
        }
        JSONArray arr = new JSONArray();
        for (JSONObject dep : deps) {
            Long pid = dep.getLong("parentId");
            if(null == pid || 0 == pid){
                arr.add(dep);
                continue;
            }
            map.get(pid).getJSONArray("children").add(dep);
        }
        return Result.ok(
            arr
        ).toString();
    }

    @RequestMapping(value = "/org/getRQDList")
    public String getRQDList(
    ){
        return Result.ok(
                sqlManager.execute(new SQLReady("select id, full_name, type from t_org_ext where type in ('ROLE','QUARTERS','DEPARTMENT') "),JSONObject.class)
        ).toJson();
    }

    @RequestMapping(value = "/org/getDQList")
    public String getDQList(
    ){
        return Result.ok(
            sqlManager.execute(new SQLReady("select id, full_name, type from t_org_ext where type in ('ROLE','QUARTERS') "),JSONObject.class)
        ).toJson();
//        List<JSONObject> deps = sqlManager.select("user.查询部门和岗位列表",JSONObject.class);
//        Map<Long,JSONObject> map = C.newMap();
//        for (JSONObject dep : deps) {
//            map.put(dep.getLong("id"), dep);
//            dep.put("children", new JSONArray());
//        }
//        JSONArray arr = new JSONArray();
//        for (JSONObject dep : deps) {
//            Long pid = dep.getLong("parentId");
//            if(null == pid || 0 == pid){
//                arr.add(dep);
//                continue;
//            }
//            map.get(pid).getJSONArray("children").add(dep);
//        }
//        return Result.ok(
//            arr
//        ).toString();
    }

    //岗位列表
    @RequestMapping(value = "/org/getQList")
    public Result getQList(
        @RequestParam  long pid
    ){
        return Result.ok(
            sqlManager.lambdaQuery(Org.class)
                .andEq(Org::getParentId, pid)
                .andEq(Org::getType, Org.Type.QUARTERS)
                .orderBy("sort asc")
                .select()
        );
    }

    @RequestMapping(value = "/org/getRList")
    public Result getRList(
//        BeetlPager beetlPager
        @RequestParam Map<String,Object> params
    ){
        return Result.ok(
            U.beetlPageQuery("user.查询角色列表", JSONObject.class, params)
        );
    }

    @RequestMapping(value = "/org/getUList")
    public Result getUList(
        @RequestParam long id
    ){
        return Result.ok(
            sqlManager.select("user.查询用户(通过组织机构)", JSONObject.class, C.newMap("oid",id))
        );
    }

    private static LinkedList<JSONObject> toSort(List<JSONObject> list,
                                          LinkedList<JSONObject> result, Long father, int level) {
        List<JSONObject> temp = new ArrayList<>();
        // 最高层,临时存放
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i).getLong("parentId"),father)) {
                temp.add(list.get(i));
            }
        }

        if (temp.size() < 1) {
            return result;
        } else {
            // 删除最高层
            for (int j = 0; j < list.size(); j++) {
                if (Objects.equals(list.get(j).getLong("parentId"), father)) {
                    list.remove(j);
                }
            }
            // 对最高层排序
            for (int i = 0; i < temp.size() - 1; i++) {
                for (int j = i + 1; j < temp.size(); j++) {
                    Integer s1 = temp.get(i).getInteger("sort");
                    if(null == s1) s1 = 0;
                    Integer s2 = temp.get(j).getInteger("sort");
                    if(null == s2) s2 = 0;
                    if (s1 > s2) {
                        JSONObject myTestTree = temp.get(i);
                        temp.set(i, temp.get(j));
                        temp.set(j, myTestTree);
                    }
                }
            }
            // 递归
            for (int i = 0; i < temp.size(); i++) {
                temp.get(i).put("level",level + 1);
                result.add(temp.get(i));
                toSort(list, result, temp.get(i).getLong("id"), level + 1);
            }
            return result;
        }
    }




    @ApiOperation(value = "添加部门", notes = "添加一个新部门,需要管理员权限")
    @RequestMapping(value = "/org/add", method = RequestMethod.GET)
    public Result add(
            @Validated(value = ValidGroup.Add.class) Org org
    ) {
        return Result.ok(userService.createOrg(org));
    }

    @RequestMapping(value = "/org/edit", method = RequestMethod.GET)
    public Result editOrg(
        @Validated(value = ValidGroup.Edit.class) Org org
    ){
        return Result.ok(userService.editOrg(org));
    }

    @RequestMapping(value = "/org/delete", method = RequestMethod.GET)
    public Result deleteOrg(
        @RequestParam long id
    ){
        sqlManager.lambdaQuery(Org.class)
            .andEq(Org::getId, (id))
            .select(Org::getName, Org::getType).forEach(o -> {
            Log.log("删除%s %s", Org.getTypeName(o.getType()), o.getName());
        });
        userService.deleteOrg(id);
        return Result.ok();
    }


    @RequestMapping(value = "/org/getOne", method = RequestMethod.GET)
    public Result getOrgById(
        @RequestParam long id
    ){
        return Result.ok(
            sqlManager.selectSingle("user.查询组织机构", C.newMap("oid", id), JSONObject.class)
        );
    }



    @RequestMapping("/quarters/adjust")
    public Result adjustUQ(
            @RequestParam int chan,
            @RequestParam long[] uids,
            @RequestParam long[] oids,
            @RequestParam int clear
    ){
        userService.setUserQuarters(chan,uids,oids, clear > 0);
        return Result.ok();
    }

}
