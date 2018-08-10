package com.beeasy.hzback.modules.system.service_kt

import bin.leblanc.classtranslate.Transformer
import com.alibaba.fastjson.JSONArray
import com.beeasy.hzback.core.exception.RestException
import com.beeasy.hzback.core.helper.Result
import com.beeasy.hzback.core.helper.Utils
import com.beeasy.hzback.core.util.CrUtils
import com.beeasy.hzback.core.util.SpringSupport
import com.beeasy.hzback.core.util.then
import com.beeasy.hzback.core.util.toLongList
import com.beeasy.hzback.modules.system.dao.*
import com.beeasy.hzback.modules.system.entity_kt.*
import com.beeasy.hzback.modules.system.service.SystemService
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.DigestUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import javax.annotation.Resource
import javax.persistence.EntityManager
import javax.persistence.criteria.*


@SpringSupport
@Service
@Transactional
class UserService(
        val fileDao: ISystemFileDao,
        val userDao: IUserDao,
        val entityManager: EntityManager,
        val quartersDao: IQuartersDao,
        val roleDao: IRoleDao,
        val globalPermissionDao: IGlobalPermissionDao
) {

//    @Autowired
//    lateinit var fileDao: ISystemFileDao
//    @Autowired
//    lateinit var entityManager: EntityManager
    //    @Autowired
//    lateinit var  : ISystemFileDao
//    @Autowired
//    lateinit var quartersDao: IQuartersDao
//    @Autowired
//    lateinit var roleDao: IRoleDao
//    @Autowired
//    lateinit var userDao: IUserDao
    @Autowired
    lateinit var systemService: SystemService
//    @Autowired
//    lateinit var globalPermissionDao: IGlobalPermissionDao
    @Autowired
    lateinit var departmentDao: IDepartmentDao
    @Autowired
    lateinit var userTokenDao: IUserTokenDao
    @Autowired
    lateinit var userProfileDao: IUserProfileDao



    /**
     * create a user
     *
     * it's calling lfs'api to create a same user on lfs-system
     * if admin set default ids of role, the new user will get them
     */
    open fun createUser(userAdd: UserAddRequeest): User {
        //save face image
        val face = SystemFile().run {
            try {
                val resource = ClassPathResource("static/default_face.jpg")
                    ext = "jpg"
                    type = com.beeasy.hzback.modules.system.entity_kt.SystemFile.Type.FACE
                    bytes = org.apache.commons.io.IOUtils.toByteArray(resource.inputStream)
                    fileName = "default_face.jpg"
                fileDao.save(this)
            } catch (e: Exception) {
                throw RestException("保存头像失败")
            }
        }
        //create user body
        val u = User().run {
            username = userAdd.username
            accCode = userAdd.accCode
            password = CrUtils.md5(userAdd.password!!.toByteArray())
            trueName = userAdd.trueName
            phone = userAdd.phone
            email = userAdd.email
            baned = userAdd.baned
            userDao.save(this)
        }
        u.profile = UserProfile().run {
            user = u
            faceId = face.id
            //create filecloud account

            userProfileDao.save(this)
        }
        u.id?.let{
            //quarters
            for (qid in userAdd.qids) {
                addUserToQuarters(listOf(it), qid)
            }
            //roles
            userAdd.rids.addAll(systemService.getSingle("sys_default_role_ids").toLongList())
            for (rid in userAdd.rids) {
                roleAddUsers(rid, mutableListOf(it))
            }
        }
        return u
    }



    /**
     * edit an user by user's id
     * @param
     */
    open fun editUser(edit: UserEditRequest): User {
        val user = findUser(edit.id)
        edit.phone then {
            if (userDao.countByPhoneAndIdNot(edit.phone, user.id!!) > 0) {
                throw RestException("已经有相同的手机号码")
            }
            user.phone = edit.phone
        }

        edit.email then { user.email = edit.email }

        //修改密码
        edit.password then {
            isValidPassword(edit.password!!)
            user.password = DigestUtils.md5DigestAsHex(edit.password?.toByteArray()!!)
        }
        //是否禁用
        if (edit.baned != null) {
            user.baned = edit.baned ?: false
        }
        //真实姓名
        edit.trueName then {
            user.trueName = edit.trueName
        }
        //信贷机构代码
        edit.accCode then {
            user.accCode = edit.accCode
        }
        setQuarters(user, edit.quarters)
        return userDao.save(user)
    }

    /**
     * delete list of user
     * notice that try not to delete a user as much as possible when this user has some works
     * it may lead to some strange problems
     *
     * @param uids collection of user's id
     * @return collection of which user has been deleted
     */
    open fun deleteUser(uids: Collection<Long>): List<Long> {
        return uids.filter {
            //解除关联
            userDao.deleteLinksByUid(it)
            //删除用户
            userDao.deleteById(it)
            true
        }
    }


    /**
     * add list of quarters to a user
     * @param uids list of user's id
     * @param qid quarters's id which want to add
     */
    open fun addUserToQuarters(uids: List<Long>, qid: Long): Boolean {
        if (quartersDao.countById(qid) == 0) {
            return false
        }
        uids.forEach {
            userDao.countById(it) > 0 && userDao.hasQuarters(it, qid) > 0 || userDao.userAddQuarters(it, qid) > 0
        }
        return true
    }

    open fun addUsersToQuarters(uids: Collection<Long>, qid: Long): List<Long>{
        if (quartersDao.countById(qid) == 0) {
            return listOf()
        }
        return uids.filter { uid ->
            userDao.countById(uid) > 0 && (userDao.hasQuarters(uid,qid) > 0 || userDao.userAddQuarters(uid,qid) > 0)
        }
    }

    /**
     * delete list of quarters
     * at first, to remove the relationship of each user and quarters
     * then, call hard delete
     */
    open fun deleteQuarters(qids: Collection<Long>): Boolean {
        userDao.deleteLinksByQids(qids)
        quartersDao.deleteAllByIdIn(qids)
        return true
    }

    open fun getUidsFromQuarters(vararg qids: Long): List<Long> {
        return entityManager.createQuery("select u.id from User u join u.quarters q where q.id in :qids")
                .setParameter("qids", qids.toList())
                .getResultList() as List<Long>
    }

    /**
     * search list of user
     * paramaters:
     *  username: like
     *  truename: like
     *  baned: equal
     */
    open fun searchUser(search: UserSearchRequest, pageable: Pageable): Page<*> {
        val query = Specification<Any> { root, query, cb ->
            val predicates = ArrayList<Predicate>()
            search.name?.isNotEmpty() then {
                predicates.add(
                        cb.or(
                                cb.like(root.get("username"), "%${search.name}%"),
                                cb.like(root.get("trueName"), "%${search.name}%")
                        )
                )
            }
            search.baned then {
                predicates.add(cb.equal(
                        root.get<Any>("baned"), it
                ))
                predicates.add(cb.equal(root.get<User>("baned"), search.baned))
            }
            search.quarters then {
                predicates.add(root.join<User, List<Quarters>>("quarters").`in`(search.quarters))
            }

            cb.and(*(predicates.toTypedArray()))
        }
        return userDao.findAll(query, pageable)
    }

    open fun setQuarters(user: User, qids: Collection<Long>) {
        userDao.deleteLinksByUid(user.id!!)
        for (qid in qids) {
            addUserToQuarters(arrayListOf(user.id!!), qid)
        }
    }

    /**
     * permission-start
     *
     *
     */
    open fun addGlobalPermission(pType: GlobalPermission.Type, objId: Long, uType: GlobalPermission.UserType, linkIds: Collection<Long>, info: Any): List<Long> {
        return linkIds.map { it ->
            var globalPermission = globalPermissionDao.findTopByTypeAndObjectIdAndUserTypeAndLinkId(pType, objId, uType, it).orElse(GlobalPermission())
            with(globalPermission) {
                type = pType
                objectId = objId
                userType = uType
                linkId = it
                description = info
            }
            globalPermissionDao.save(globalPermission).id!!
        }
    }

    open fun getGlobalPermissions(types: Collection<GlobalPermission.Type>, objectId: Long): List<GlobalPermission> {
        return globalPermissionDao.findAllByTypeInAndObjectId(types, objectId)
    }

    open fun getGlobalPermission(type: GlobalPermission.Type, oid: Long, userType: GlobalPermission.UserType, lid: Long): Optional<GlobalPermission> {
        return globalPermissionDao.findTopByTypeAndObjectIdAndUserTypeAndLinkId(type, oid, userType, lid)
    }

    open fun deleteGlobalPermission(gpids: Collection<Long>): Boolean {
        val count = globalPermissionDao.deleteAllByIdIn(gpids)
        return count > 0
    }

    open fun deleteGlobalPermissionByObjectId(id: Long): Boolean {
        val count = globalPermissionDao.deleteAllByObjectId(id)
        return count > 0
    }

    open fun deleteGlobalPermissionByTypeAndObjectId(type: GlobalPermission.Type, id: Long): Boolean {
        return globalPermissionDao.deleteAllByTypeAndObjectId(type, id) > 0
    }

    /**
     * get user's methods with white list
     * when a method menu is unchecked, the apis which from it cannot be calling again
     * @param uid user's id
     */
    open fun getUserMethods(uid: Long): List<*> {
        return globalPermissionDao.getPermissionsByUser(listOf(GlobalPermission.Type.USER_METHOD), listOf(GlobalPermission.UserType.USER, GlobalPermission.UserType.ROLE), uid)
                .map { it.description }
                .filter { it is JSONArray }
                .flatMap { it as JSONArray }
                .distinct()
                .toList()
    }
    /**
     * permission-end
     */


    /**
     * role-start
     */

    /**
     * create a role
     */
    open fun createRole(request: RoleRequest): Role {
        val role = Role().apply {
            name = request.name
            info = request.info
            sort = request.sort
            canDelete = request.canDelete
        }
        return roleDao.save(role)
    }

    open fun editRole(request: RoleRequest): Role? {
        return findRole(request.id)?.run {
            name = request.name
            info = request.info
            sort = request.sort
            canDelete = request.canDelete
            return roleDao.save(this)
        }
    }

    /**
     * add list of user to a role
     * @param rid role's id
     * @param uids collection of user's id
     * @return boolean if the result is succeed
     */
    open fun roleAddUsers(rid: Long, uids: MutableCollection<Long>): Boolean {
        if (roleDao.countById(rid) == 0) {
            return false
        }
        uids.forEach {
            userDao.countById(it) > 0 &&
                    roleDao.hasPair(it, rid) > 0 ||
                    roleDao.addUserRole(it, rid) > 0
        }
        return true
    }

    open fun deleteRoles(ids: Collection<Long>): List<Long> {
        return ids.map { roleDao.findById(it).orElse(null) }
                .filter { it != null && it.canDelete }
                .map {
                    roleDao.deleteById(it.id!!)
                    it.id!!
                }
    }

    open fun roleDeleteUsers(rid: Long, uids: Collection<Long>): List<Long> {
        if (roleDao.countById(rid) == 0) {
            return listOf()
        }
        return uids.filter { roleDao.deleteUserRole(it, rid) > 0 }
    }

    /**
     * set roles to a user
     * @param
     *
     * @return list of role which is succeed
     */
    open fun userSetRoles(uid: Long, rids: Collection<Long>): List<Long> {
        if (!userDao.existsById(uid)) {
            return listOf()
        }
        //删除所有角色
        roleDao.deleteUserRoles(uid)
        return rids.filter {
            roleDao.hasPair(uid, it) > 0 || roleDao.addUserRole(uid, it) > 0
        }
    }

    /**
     *
     */
    open fun userDeleteRoles(uid: Long, rids: Collection<Long>): List<Long> {
        return rids.filter { roleDao.deleteUserRole(uid, it) > 0 }
    }

    open fun searchRoles(request: RoleSearchRequest, pageable: Pageable): Page<*> {
        return roleDao.findAll(Specification<Any> { root, criteriaQuery, cb ->
            val predicates = ArrayList<Predicate>()
            request.name then {
                predicates.add(
                        cb.like(root.get("name"), "%${request.name}%")
                )
            }
            cb.and(*predicates.toTypedArray())
        }, pageable)
    }
    /**
     * role-end
     */

    /**
     * department-start
     */
    open fun createDepartment(add: DepartmentAdd): Department? {
        val parent: Department? = when (add.parentId) {
            null -> null
            0.toLong() -> null
            else -> null
        }
        val department = Department().apply {
            name = add.name
            parentId = parent?.id
            info = add.info
            sort = add.sort
            accCode = add.accCode
        }

//
////部门编号
        val objs = when (department.parentId) {
            null -> departmentDao.findTopLastCode()
            else -> departmentDao.findLastCode(department.id)
        }
        var code = if (objs.size == 0) {
            if (null == department.parentId) {
                "001"
            } else {
                val codes = departmentDao.getDepartmentCode(department.parentId!!)
                codes.get(0).toString() + "001"
            }
        } else {
            val co = objs.get(0).toString()
            //替换后三位
            co.replaceRange(co.length - 3, co.length, String.format("%03d", co.substring(co.length - 3, co.length).toInt() + 1))
        }
        department.code = code
        return departmentDao.save(department)
    }

    open fun editDepartment(edit: DepartmentEdit): Department? {
        return findDepartment(edit.id)?.run {
            name = edit.name.takeIf { it.isNotEmpty() } ?: name
            info = edit.info.takeIf { it.isNotEmpty() } ?: info
            accCode = edit.accCode.takeIf { it.isNotEmpty() } ?: accCode
            sort = edit.sort
            departmentDao.save(this)
        }
    }

    /**
     * delete a department
     *
     * usually, we suggest that do not delete it
     * but if you really want, remember this department must be an empty department
     *
     */
    open fun deleteDepartment(id: Long): Boolean {
        val department = findDepartment(id)
        //如果还要岗位 不能删除
        if (department.quarters.size > 0) {
            throw RestException("该部门还有岗位, 无法删除")
        }

        if (department.children.size > 0) {
            throw RestException("该部门还有子部门, 无法删除")
        }
        departmentDao.deleteById(id)
        return true
    }
    /**
     * department-end
     */


    /**
     * quarters-start
     */
    open fun createQuarters(add: QuartersAddRequest): Quarters? {
        val department = findDepartment(add.departmentId)
//同部门不能有同名的岗位
        var quarters = Quarters().apply {
            departmentId = add.departmentId
            dName = department.name
            sort = add.sort
            manager = add.manager
        }

//        quarters!!.departmentId = department.id
//        quarters.dName = department.name
//        quarters.sort = add.sort
//        quarters.manager = add.manager

        //查找最上层的id
        val objs = quartersDao.getQuartersCodeFromDepartment(department.id!!)
        if (objs.size == 0) {
            quarters.code = department.code!! + "_001"
        } else {
            val code = objs[0].toString()
            var codeValue = Integer.valueOf(code.substring(code.length - 3, code.length))
            codeValue++
            var newCode = codeValue.toString() + ""
            for (i in newCode.length..2) {
                newCode = "0$newCode"
            }
            quarters.code = department.code + "_" + newCode
        }
        return quartersDao.save(quarters)
    }

    open fun updateQuarters(edit: QuartersEditRequest): Quarters? {
        //禁止编辑同名岗位
        return findQuarters(edit.id)?.run {
           manager = edit.manager
            sort = edit.sort
            name = edit.name
            info = edit.info
            quartersDao.save(this)
        }
    }

    /**
     * quarters-end
     */

    /**
     * this is an async method
     * update the token
     */
    @Async
    open fun updateToken(token: String) {
        userTokenDao.updateToken(token, Date(System.currentTimeMillis() + 30 * 1000 * 60), Date())
    }

    open fun modifyPassword(uid: Long, oldPassword: String, newPassword: String): Boolean {
        var oldPassword = oldPassword
        var newPassword = newPassword
        isValidPassword(newPassword)
        val user = findUser(uid) ?: return false
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.toByteArray())
        newPassword = DigestUtils.md5DigestAsHex(newPassword.toByteArray())
        if (user.password != oldPassword) {
            throw RestException("旧密码不正确")
        }
        user.newUser = false
        user.password = newPassword
        userDao.save(user)
        return true
    }

    open fun modifyProfile(uid: Long, request: ProfileEditRequest): User? {
        return findUser(uid)?.run {
            trueName = request.trueName.takeIf { it.isNotEmpty()} ?: trueName

            request.phone then {
                if(userDao.countByPhoneAndIdNot(it, uid) > 0){
                    throw RestException("已经有相同的手机号")
                }
                phone = it
            }
            request.email then {
                if (userDao.countByEmailAndIdNot(it, uid) > 0) {
                    throw RestException("已经有相同的邮箱")
                }
                email = it
            }
            userDao.save(this)
        }
    }

    open fun updateUserFace(uid: Long, file: MultipartFile): Long {
        val user = findUser(uid) ?: throw RestException()
        val systemFile = fileDao.findById(user.profile.faceId).orElse(SystemFile())
        val ext = FilenameUtils.getExtension(file.originalFilename)
        if (ext.equals("jpg", ignoreCase = true) || ext.equals("jpeg", ignoreCase = true) || ext.equals("png", ignoreCase = true)) {
            if (null != systemFile.id) {
                fileDao.delete(systemFile)
            }
            var face = SystemFile()
            face.type = SystemFile.Type.FACE
            face.ext = Utils.getExt(file.originalFilename)
            try {
                face.bytes = file.bytes
                face = fileDao.save(face)
                user.profile.faceId = face.id
                userProfileDao.save(user.profile)
                return face.id ?: 0
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return 0
    }

    /**
     * check if the password is valid
     * here are rules in error messages
     */
    open fun isValidPassword(newPassword: String) {
        //1 and 8
        if (!(newPassword.matches("^.*[a-zA-Z]+.*$".toRegex()) && newPassword.matches("^.*[0-9]+.*$".toRegex())
                        && newPassword.matches("^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}]+.*$".toRegex()))) {
            throw RestException("密码必须包含字母, 数字, 特殊字符")
        }
        //2
        if (!newPassword.matches("^.{8,}$".toRegex())) {
            throw RestException("密码长度至少8位")
        }
        //3
        if (newPassword.matches("^.*(.)\\1{2,}+.*$".toRegex())) {
            throw RestException("密码不能包含3位及以上相同字符的重复")
        }
        //4
        if (newPassword.matches("^.*(.{3})(.*)\\1+.*$".toRegex())) {
            throw RestException("密码不能包含3位及以上字符组合的重复")
        }
        //6
        if (newPassword.matches("^.*[\\s]+.*$".toRegex())) {
            throw RestException("密码不能包含空格、制表符、换页符等空白字符")
        }
    }

    /**
     * find a user by user's id
     * @param id user
     *
     * if cannot find, throw a RestException
     */
    open fun findUser(id: Long): User {
        return userDao.findById(id).orElseThrow(RestException("找不到ID为" + id + "的用户")) as User
    }

    open fun findUser(ids: Collection<Long>): List<User>{
        return userDao.findAllByIdIn(ids)
    }

    open fun findUserByAccCode(accCode: String): User {
        return userDao.findTopByAccCode(accCode)
                .orElseThrow(RestException(String.format("找不到代号为%s的客户经理", accCode)))
    }

    open fun findDepartment(id: Long): Department {
        return departmentDao.findById(id).orElseThrow(RestException("找不到ID为" + id + "的部门"))
    }

    open fun findQuarters(id: Long): Quarters {
        return quartersDao.findById(id).orElseThrow(RestException("找不到ID为${id}的岗位"))
    }
    open fun findQuarters(ids : Collection<Long>) : List<Quarters>{
        return quartersDao.findAllByIdIn(ids)
    }
    open fun hasQuarters(uid: Long, qid: Long): Boolean{
        return userDao.hasQuarters(uid, qid) > 0
    }

    open fun findRole(id: Long): Role {
        return this.roleDao.findById(id).orElseThrow(RestException(String.format("找不到ID为%d的角色", id)))
    }

    open fun isSu(uid: Long): Boolean {
        return userDao.countByIdAndSuIsTrue(uid) > 0
    }

    /**
     * get child departments of target department
     * the result includes itself
     * @param did top department's id which you want to search
     * @return list of department's id
     */
    open fun getDidsFromDepartment(did: Long): List<Long> {
        return departmentDao.getChildDepIds(did)
    }

    open fun findDepartmentsByParent_Id(pid: Long): List<Department> {
        val allDeps: MutableList<Department> = mutableListOf()
        allDeps.addAll(
                when{
                    pid == 0L || pid == null  -> departmentDao.findAll()
                    else ->
                        departmentDao.findById(pid).orElse(null)?.let{
                            return departmentDao.getChildDeps(it.id!!)
                        } ?: listOf<Department>()
                }
        )
        //整理数据
        val map = mutableMapOf<Long,Department>()
        allDeps.forEach {
            map.put(it.id!!, it)
            it.children = mutableListOf<Department>()
            it.quarters = mutableListOf()
        }
        //所有岗位
        val allQs = quartersDao.findAll()
        allQs.forEach { q ->
            map.get(q.departmentId!!)?.quarters?.add(q)
        }
        return allDeps.filter {dep ->
            when{
                dep.parentId == null || dep.parentId == pid -> true
                else -> {
                    map.get(dep.parentId!!)?.children?.add(dep)
                    false
                }
            }
        }


    }

}
