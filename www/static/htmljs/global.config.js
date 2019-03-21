window.console = window.console || {
	log: $.noop,
	debug: $.noop,
	info: $.noop,
	warn: $.noop,
	exception: $.noop,
	assert: $.noop,
	dir: $.noop,
	dirxml: $.noop,
	trace: $.noop,
	group: $.noop,
	groupCollapsed: $.noop,
	groupEnd: $.noop,
	profile: $.noop,
	profileEnd: $.noop,
	count: $.noop,
	clear: $.noop,
	time: $.noop,
	timeEnd: $.noop,
	timeStamp: $.noop,
	table: $.noop,
	error: $.noop
};
var pathName = window.location.pathname;
var whitelist = ['/login.html'];
// if(whitelist.indexOf(pathName) < 0){
//     if(typeof $ !== 'undefined'){
//         if(!$.cookie("userId") || !$.cookie("userName") || !$.cookie('authorization')){
//             top.location.href = "/login.html";
//         }
// 	}
// }

//jquery-cookie 设置jQuery Ajax全局的参数 
var isDebuging = false;
var isSuperAdmin = false;
if(typeof $ !== "undefined"){
    jQuery.support.cors = true;
    if($.cookie("userName") == "管理员"){
        isSuperAdmin = true;
    }
}
// api远程服务器地址
//  var remoteOrigin = 'http://localhost'; // hznsh服务器
if(!window.location.origin)
{
    window.location.origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
}

if(typeof $ !== "undefined"){
    $.getJSON("/server.json?v="+Math.random(), function (json) {
        top.SERVER_LIST = json;
    });
}

var getRemoteOrigin = function(server){
	var list = top.SERVER_LIST || [];
	for(var i = 0; i < list.length; i++){
		if(list[i].name == (server || 'main')){
			return list[i].server;
		}
	}
	return null;
}

Object.defineProperty(window, "remoteOrigin", {
	get: function () {
		return getRemoteOrigin(top.SERVER) || getRemoteOrigin() || "";
    }
});
Object.defineProperty(window, "remoteClound", {
    get: function () {
        return  remoteOrigin + "/disk";
    }
});

var matched = location.search.match(/_chain=([^&\/]+)/);
if(matched){
    top.SERVER = matched[1];
}

// var remoteOrigin = location.origin;
// 'http://localhost'; // hznsh服务器
// var remoteOrigin = 'http://192.168.10.186'; // hznsh2服务器
//var remoteOrigin = 'http://192.168.10.187';
// var remoteOrigin = 'http://192.168.2.100:8080';
// var remoteClound = 'http://47.93.54.105'; // 私有云服务器
// var remoteClound = "http://localhost/disk";
// var remoteClound = remoteOrigin + "/disk";
// var remoteCloundToken = ";jsessionid=";

// 远程api
var remoteApi = {
	// 企查查
	qccECISearchFresh: "/qcc/ECI/SearchFresh", // 获取新增公司信息
	qccECIGetDetails: "/qcc/ECI/GetDetails", // 获取公司详细信息
	qccECISearch: "/qcc/ECI/Search", // 企业关键字模糊搜索
	qccECIRelationGetCompanyEquityShareMap: "/qcc/ECIRelation/GetCompanyEquityShareMap", // 股权结构图
	qccECIRelationGenerateMultiDimensionalTreeCompanyMap: "/qcc/ECIRelation/GenerateMultiDimensionalTreeCompanyMap", // 企业图谱
	qccGetChattelMortgage: "/qcc/ChattelMortgage/GetChattelMortgage", // 获取动产抵押信息
	qccECIExceptionGetOpException: "/qcc/ECIException/GetOpException", // 查询企业经营异常信息
	qccCourtAnnoSearchCourtNotice: "/qcc/CourtAnno/SearchCourtNotice", // 开庭公告查询
	qccCourtAnnoGetCourtNoticeInfo: "/qcc/CourtAnno/GetCourtNoticeInfo", // 开庭公告详情查询
	qccJudgeDocSearchJudgmentDoc: "/qcc/JudgeDoc/SearchJudgmentDoc", // 裁判文书查询
	qccJudgeDocGetJudgementDetail: "/qcc/JudgeDoc/GetJudgementDetail", // 裁判文书详情查询
	qccCourtNoticeSearchCourtAnnouncement: "/qcc/CourtNotice/SearchCourtAnnouncement", // 法院公告列表信息
	qccCourtNoticeSearchCourtAnnouncementDetail: "/qcc/CourtNotice/SearchCourtAnnouncementDetail", // 法院公告详情
	qccCourtSearchShiXin: "/qcc/Court/SearchShiXin", // 查询失信列表
	qccCourtGetShiXinDetail: "/qcc/Court/SearchShiXin", // 获取失信详情
	qccGetZhiXingDetail: "/qcc/Court/GetZhiXingDetail", // 获取执行详情
	qccCourtSearchZhiXing: "/qcc/Court/SearchZhiXing", // 查询执行列表
	qccJudicialSaleGetJudicialSaleList: "/qcc/JudicialSale/GetJudicialSaleList", // 司法拍卖列表
	qccJudicialSaleGetJudicialSaleDetail: "/qcc/JudicialSale/GetJudicialSaleDetail", // 司法拍卖详情
	qccLandMortgageGetLandMortgageList: "/qcc/LandMortgage/GetLandMortgageList", // 获取土地抵押列表
	qccLandMortgageGetLandMortgageDetails: "/qcc/LandMortgage/GetLandMortgageDetails", // 获取土地抵押详情
	qccGetEnvPunishmentList: "/qcc/EnvPunishment/GetEnvPunishmentList", // 获取环保处罚列表
	qccGetEnvPunishmentDetails: "/qcc/EnvPunishment/GetEnvPunishmentDetails", // 获取环保处罚详情
	qccHistoryGetHistorytEci: "/qcc/History/GetHistorytEci", // 历史工商信息



	apiGetRejectCollectList: '/api/workflow/getRejectCollectList', // 拒贷记录
	apiSheetSearchYuQYSLX: '/api/data/sheet/searchYuQYSLX', // 报表, 逾期应收利息
	apiSheetSearchYuQYSBJ: '/api/data/sheet/searchYuQYSBJ', // 报表, 逾期应收本金
	apiSheetSearchYQYSLX: '/api/data/sheet/searchYQYSLX', // 报表,预期应收利息
	apiSheetSearchYQYSBJDQ: '/api/data/sheet/searchYQYSBJDQ', // 报表,预期应收本金（到期）
	apiOdsSearchCUS_INDIV: '/api/search/accloan/202', // 对私客户-基本信息 /api/data/searchCUS_INDIV
	apiOdsSearchCUS_COM: '/api/search/accloan/201', // 对公客户-基本信息 /api/data/searchCUS_COM
	apiOdsSearchACC_LOAN: '/api/search/accloan/203', //贷款台账-基本信息 /api/data/searchACC_LOAN
	apiOdsSearchCrtLoan: '/api/search/accloan/220', // 贷款合同列表查询 /api/data/searchCrtLoan
	apiOdsSearchGrtGuar: '/api/search/accloan/03', // 担保合同列表查询 /api/data/searchGrtGuar
	apiOdsSearchGRTGBasicInfo: '/api/search/accloan/04', // 抵押物明细列表查询 /api/data/searchGRTGBasicInfo
	apiOdsSearchCusInDiv: '/api/search/accloan/218', //  osd数据，个人收入查询 /api/data/searchCusInDiv
	apiOdsSearchCusComManager: '/api/search/accloan/215', // osd数据，查询对公客户高管信息 /api/data/searchCusComManager
	apiOdsSearchComAddr: '/api/search/accloan/216', // osd数据，查询对公客户联系信息 /api/data/searchComAddr
	apiOdsSearchAccloanData: '/api/search/accloan/219', // osd数据，贷款资料查询 /api/data/searchAccloanData
	apiOdsSearchAccloan: '/api/search/accloan/219', // osd数据，贷款台账查询 /api/data/searchAccloan
	apiOdsSearchAllClient: '/api/search/accloan/229', // ods数据，所有客户查询
	apiOdsSearchPublicClient: '/api/search/accloan/214', // ods数据，对公客户查询 /api/data/searchPublicClient
	apiOdsSearchPrivateClient: '/api/search/accloan/217', // ods数据，对私客户资料查询 /api/data/searchPrivateClient
		// 手机短信历史
	apiSysShortMessageList: '/api/system/shortMessage/getList', // 手机短信发送历史
		// 消息模板
	apiSysMsgTmplList: '/api/system/msgTmpl/getList', // 获取消息模板列表
    apiSysMsgTmplAdd: '/api/system/msgTmpl/add', // 添加消息模板
	apiSysMsgTmplDel: '/api/system/msgTmpl/delete', // 删除消息模板
    apiSysMsgTmplEdit: '/api/system/msgTmpl/edit', // 编辑消息模板
		// 用户
	apiCheckOnline: '/api/checkOnline', // 检查用户是否在线
	apiUserSetRoles: '/api/user/setRoles', // 用户批量设置角色 
	apiGetRolesByUser: '/api/user/role/getRolesByUser', // 通过用户ID查找用户所持有的角色
	apiRoleAddUsers: '/api/user/role/addUsers', // 
	apiRoleDelUsers: '/api/user/role/deleteUsers', // 移除用户拥有的角色
	apiGetUserListByRoleId: '/api/user/role/getUsersByRole', // 通过角色拿到用户 
		// 角色
	apiRoleEdit: '/api/user/role/edit', // 编辑角色
	apiRoleDel: '/api/user/role/delete', // 删除角色
	apiGetRoleList: '/api/user/role/getList', // 得到角色列表
	apiRoleCreate: '/api/user/role/create', // 创建角色
	apicanAccpetWorks: '/api/workflow/canAccpetWorks', // 得到用户可接受的任务
	apiGetSysVar: '/api/system/var/get', // 获取系统变量
	apiSetSysVar: '/api/system/var/set', // 设置系统变量
	apiModifyPassword: '/api/user/modifyPassword', // 更改密码
	apiModifyProfile: '/api/user/modifyProfile', // 修改个人基本信息
	apiLogin: '/api/login', // 登录
	apiCommon: '/api/',
	apiAlldepartment: '/api/user/department/getList', // 所有部门 /open/department
        // 岗位
	apiQuarters: '/api/quarters/edit',// 编辑岗位
    apiQuartersAdjust: '/api/user/quarters/adjust', // 给用户添加,删除岗位，给岗位添加，删除用户
	apiDepartment: '/api/user/department', // restful部门接口
	apiQuartersDel: '/api/quarters/delete', // 删除部门
	apiConfig: '/open/config', // 配置
	apiNormalUsers: '/api/user/normalUsers', // 获取所有用户简单数据
	apiUserFace: remoteOrigin + '/api/file/download?fileId=', // 获取用户头像
	apiUserFaceEdit: remoteOrigin + '/api/user/face/edit',
	apiUser: '/api/user', // 用户 restful
	apiUserEdit: '/api/user/edit', // 修改用户信息
	apiUserMyMethods: '/api/user/myMethods', // 得到用户功能菜单 
	apiUserIds: '/api/user/ids', // 根据用户id获取用户信息
    apiUserEdit: '/api/user/edit', // 编辑用户
    apiOnlineUser: '/api/onlineUser', // 在线用户
	apiWorkFlowModels: '/api/workflow/models', // 标准工作流
	apiWorkFlowModel: '/api/workflow/model',
	apiWorkFlowModelId: '/api/workflow/model/id', // 获取模型实例信息
	apiWorkFlowModelNodeDel: '/api/workflow/model/node/delete', // restful模型实例节点接口
	apiUpdateBaned: '/api/user/updateBaned', // 用户的禁用，启用
	apiSystemLog: '/api/system/log/getList', // 系统日志
		// 文件管理
	apiFileRetags: '/api/file/retags', // 统一设置文件标签
	apiFileRename: '/api/file/rename', // 统一重命名文件
	apiDownLoadUploadFile: remoteOrigin + '/api/file/download', // 统一下载文件接口
    apiDelUploadFile: '/api/file/delete', // 统一删除文件接口
    apiUploadFile: remoteOrigin + '/api/file/upload', // 统一上传文件接口
	apiCreateDir: '/api/clouddisk/createDir', // 创建个人文件柜文件夹
	apiDeleteDir: '/api/clouddisk/deleteDir', // 删除个人文件柜文件夹
	apiDeleteFiles: '/api/clouddisk/deleteFiles', // 删除个人文件柜文件
	apiDeleteShareFiles: '/api/clouddisk/deleteShareFiles', // 删除个人文件柜分享记录
	apiDirss: '/api/clouddisk/dirs', // 获取个人文件柜文件夹列表
	apiFiles: '/api/clouddisk/files', // 获取个人文件柜文件列表
	//apiRenameFile: '/api/clouddisk/renameFile', // 重命名文件
	//apiRenameDir: '/api/clouddisk/renameDir', // 重命名文件夹
	apiMoveFiles: '/api/clouddisk/moveFiles', // 移动文件
	apiUploadSingle: '/api/clouddisk/file/uploadSingle', // 上传单独文件
	apiSaveShareFiles: '/api/clouddisk/saveShareFiles', // 转存朋友分享文件
        // 功能权限
    apiGetPermissions: '/api/data/getPermissions', // 授权查询
    apiUserPermissionGet: '/api/user/permission/get', // 根据用户ID获取功能菜单
	apiUserPermissionSet: '/api/user/permission/set', // 设置用户功能权限
    apiRolePermissionMethodGet: '/api/user/role/permission/method/get', // 根据角色ID获取功能菜单
    apiRolePermissionMethodSet: '/api/user/role/permission/method/set', // 设置角色功能权限
		// 工作流
	searchInnateAccloanData1: '/api/search/accloan/101?LOAN_ACCOUNT=', // 资料收集流程发起节点
	searchInnateAccloanData2: '/api/search/accloan/102?LOAN_ACCOUNT=', // 菜单权限申请发起节点
    searchInnateAccloanData3: '/api/search/accloan/103?LOAN_ACCOUNT=', // 贷后跟踪-小微部公司类流程发起节点
    searchInnateAccloanData4: '/api/search/accloan/104?LOAN_ACCOUNT=', // 贷后跟踪-小微部个人类流程发起节点
    searchInnateAccloanData5: '/api/search/accloan/105?LOAN_ACCOUNT=', // 贷后跟踪-零售部按揭类流程发起节点 || 贷后跟踪-零售部经营类流程发起节点 || 贷后跟踪-零售部消费类流程发起节点
    searchInnateAccloanData6: '/api/search/accloan/106?LOAN_ACCOUNT=',
    apiDataSearchInnateData: '/api/data/searchInnateData', //  查询固有字段关联的台账信息
	apiWorkflowModelValidList: '/api/workflow/model/validList', // 得到自己可以执行的工作
	apiGetUnreceivedWorks: '/api/workflow/getUnreceivedWorks', // 指派列表
	apiOpenDownload: remoteOrigin + '/api/file/download', // 通过令牌下载文件 /open/download
	apiWorkFlowNodeFileDownloadApply: '/api/workflow/node/file/downloadApply', // 申请下载工作流文件的令牌
	apiWorkflowBinds: '/api/workflow/binds', // 得到台账相关工作流
	apiInfolinkCreateLink: '/api/infolink/createLink', // 资料绑定 
	apigetMyInfoCollect: '/api/infolink/getMyInfoCollect', // 查询我自己的资料收集 
	apiPointTask: '/api/workflow/pointTask', // 任务重指派/移交
	apiGetPreWorks: '/api/workflow/getPlanWorks', // 得到公共任务列表
	apiWorkflowRejectWorks: '/api/workflow/rejectWorks', // 拒绝指派或移交 
	apiWorkflowAcceptWorks: '/api/workflow/acceptWorks', // 接受指派或移交 
	apiWorkflowDealers: '/api/workflow/model/dealers', // 得到一个模型的任务可以指派的对象
	apiWorkflowIinstances: '/api/workflow/instances', // 根据原始模型名获取工作流任务
	apiSetPersons: '/api/workflow/model/setPersons', // 设置节点处理人员
	apiAddCheckNode: '/api/workflow/model/node/addCheck', // 增加审核节点
	apiMyPubWorks: "/api/workflow/myPubWorks", // 我发布的任务 
	apiMyOwnWorks: "/api/workflow/myOwnWorks", // 我执行的任务
	apiMyDealedWorks: '/api/workflow/myDealedWorks', // 我处理过的任务
	apiMyNeedingWorks: '/api/workflow/myNeedingWorks', // 需要我处理的任务
	apiMyObserveredWorks: '/api/workflow/myObserveredWorks', // 我观察的任务
	apiDeptUndealedWorks: '/api/workflow/deptUndealedWorks', // 部门未处理
	apiDeptDealedWorks: '/api/workflow/deptDealedWorks', // 部门已处理
	apiCanAcceptCommonWorks: '/api/workflow/common/getList', // 可接受的公共任务
	apiAcceptCommonWork: '/api/workflow/common/accept?instanceId=', // 接受公共任务
	apiInstanceInfo: "/api/workflow/instance/fetch/", // 查询一个任务实例的所有明细
	apiWorkflowAll: '/api/workflow/all', // 所有工作流
	apiWorkflowGoCancel: '/api/workflow/cancel', // 取消任务
	apiWorkflowRecall: '/api/workflow/recall', // 撤回任务
	apiWorkflowApply: '/api/workflow/apply', // 发布新任务
	apiWorkflowSubmitData: '/api/workflow/submitData', // 暂存节点数据
	apiWorkflowGoNext: '/api/workflow/goNext', // 发起进行下一步请求，不提交数据
	apiWorkflowTransform: '/api/workflow/transform', // 移交任务
	apiWorkflowFileUpload: remoteOrigin + '/api/workflow/file/upload', // 上传节点附件
    //apiWorkflowTempFileUpload: remoteOrigin + '/api/workflow/file/uploadTemp', // 上传节点临时附件
    //apiWorkflowFileDelete: '/api/workflow/file/delete', // 删除节点附件
	apiSetObserverQuarters: '/api/workflow/setObserverQuarters', // 设置观察岗
	apiWorkflowModelEdit: '/api/workflow/model/edit', // 编辑工作流 
	apiWorkFlowModelDel: '/api/workflow/model/delete', // 删除工作流
	apiWorkFlowModelPermissionGet:'/api/workflow/model/permission/get', // 获取工作流节点权限配置
	apiWorkFlowModelPermissionSet: '/api/workflow/model/permission/set', // 设置工作流节点权限
		// 消息
	apiGetNoticeList: '/api/notice/getList', // 得到通知列表
	apiUpdateReadNoticeList: '/api/notice/updateRead', // 更新已读通知列表 
	apiGetUnreadUserList: '/api/message/getUnreadUserList', // 获取未读消息人列表
	apiMsgSendFile: '/api/message/sendFile', // 发送文件
	apiMsgSendString: '/api/message/sendString', // 发送文本短消息 
	apiUserRecentMsg: '/api/message/userRecentMessage', // 获取用户之间相互发送的消息
	apiReadMessages: '/api/message/readMessages',// 更新未读
		// 个人文件柜
	apiAllFilesAndDirs: '/api/clouddisk/user/all', // 某文件夹下所有文件及文件夹
	apiCloudShareFile: '/api/clouddisk/user/shareFile', // 分享文件
	apiClouddiskCreateDir: '/api/clouddisk/user/createDir', // 新建文件柜文件夹
	apiClouddiskDel: '/api/clouddisk/user/removeDir', // 删除文件柜中文件和文件夹
	apiClouddiskRename: '/api/clouddisk/user/renameDir', // 重命名文件柜中文件和文件夹
	apiClouddiskUploadFile: '/api/clouddisk/user/uploadFile', // 文件柜上传文件
	apiClouddiskPutFileTags: '/api/clouddisk/user/putFileTags', // 设置文件标签 
		// 共享文件柜
	apiAllCommonFilesAndDirs: '/api/clouddisk/common/all', // 某文件夹下所有文件及文件夹
	apiShareCommonFile: '/api/clouddisk/common/shareFile', // 分享共享文件
	apiCloudCommondiskCreateDir: '/api/clouddisk/common/createDir', // 新建共享文件柜文件夹
	apiCloudCommondiskDel: '/api/clouddisk/common/removeDir', // 删除共享文件柜中文件和文件夹
	apiCloudCommondiskRename: '/api/clouddisk/common/renameDir', // 重命名共享文件柜中文件和文件夹
	apiCloudCommondiskUploadFile: '/api/clouddisk/common/uploadFile', // 共享文件柜上传文件
    apiWorkflowChildApply: '/api/workflow/child/apply', // 开启不良资产子任务 
	//## 私有云api
		// 封装
	apiCloud2Login: '/api/mobile/user/loginCloud', // 登录私有云
		// 登录
	apiCloudLogin: remoteClound + "/apiLogin.action", // 登录
	apiCloudCheckOnline: "/checkOnline.action", // 在线判断
	  	// 个人
	apiCloudGetFiles: "/disk/getFiles.action", // 获取文件列表
	apiCloudDownload: remoteClound + "/disk/download.action", // 下载
	apiCloudCreateFolder: "/disk/createFolder.action", // 创建文件夹
	apiCloudRenameFile: "/disk/renameFile.action", // 重命名
	apiCloudDeleteFiles: "/disk/deleteFiles.action", // 删除
	apiCloudUploadFiles: remoteClound + "/disk/uploadFiles.action", // 上传
	apiCloudEditTags: "/disk/editTags.action", // 设置标签
	apiCloudGetDirectories: "/disk/getDirectories.action", // 获取目录列表
	apiCloudMoveFiles: "/disk/moveFiles.action", // 移动文件
	apiCloudCopyFile: "/disk/copyFile.action", // 复制文件
	apiCloudCommonFilePut: "/disk/commonFilePut.action", // 移动文件
		// 资料库
	apiCloudCommGetFiles: "/commdisk/getFiles.action", // 获取文件列表
	apiCloudCommDownload: remoteClound + "/commdisk/download.action", // 下载
	apiCloudCommCreateFolder: "/commdisk/createFolder.action", // 创建文件夹
	apiCloudCommRenameFile: "/commdisk/renameFile.action", // 重命名
	apiCloudCommDeleteFiles: "/commdisk/deleteFiles.action", // 删除
	apiCloudCommUploadFiles: remoteClound + "/commdisk/uploadFiles.action", // 上传
	apiCloudCommEditTags: "/commdisk/editTags.action", // 设置标签
	apiCloudCommGetDirectories: "/commdisk/getDirectories.action", // 获取目录列表
	apiCloudCommMoveFiles: "/commdisk/moveFiles.action", // 移动文件
	apiCloudCommCopyFile: "/commdisk/copyFile.action", // 复制文件
	// 回收站
		// 资料库
	apiCloudArchiveFileRecycleJson: "/system/archiveFileRecycleJson.action", // 列表
	apiCloudCommRestoreFiles: "/commdisk/restoreFiles.action", // 还原
	apiCloudCommClearRecycler: "/commdisk/clearRecycler.action", // 删除
		// 文件柜
	apiCloudMyFileTrashListJson: "/disk/myFileTrashListJson.action", // 列表
	apiCloudClearRecycler: "/disk/clearRecycler.action", // 删除
	apiCloudRestoreFiles: "/disk/restoreFiles.action", // 还原
	// 文件共享
		// 我的共享
	apiCloudShareFileTo: "/disk/shareFileTo.action",
	apiCloudShareFileJsonList: "/disk/shareFileJsonList.action",
	apiCloudShareFileDelete: "/disk/shareFileDelete.action",
		// 共享给我的
	apiCloudReceiveFileJsonList: "/disk/receiveFileJsonList.action", // 列表
	apiCloudSaveToNetdisk: "/disk/saveToNetdisk.action", // 转存
	apiCloudReceiveFileDownload: remoteClound +  "/disk/receiveFileDownload.action", // 下载
	// 文件检索
	apiCloudFileSearch: "/disk/fileSearch.action", // 转存
};

var hrefUrl = {
	nodeStates: "/htmlsrc/workFlow/nodeStates.html",
	choiceLoan: "/htmlsrc/htmllayer/choiceLoan.html",
	userInfo: "/htmlsrc/sysManage/userManage/user.show.html?id=",
    indivClientInfo: "/htmlsrc/dataQuery/private/private.show.html?id=",
	comClientInfo: "/htmlsrc/dataQuery/company/company.show.html?id=",
	loanInfo: "/htmlsrc/creditDataManage/ledger/loan.show.html?loanid=",
    loanDataInfo: "/htmlsrc/creditDataManage/ledger/loanData.show.html?loanid=",
	ECIdetail: "/htmlsrc/qichacha/ECI/detailsByName.html?id="
};

var globalData = {
    defaultImg: "/static/htmlImage/face.jpg"
}
var enumClientType = {
	private: 1,
	public: 2
}

// 用户启用状态
var enumUserState = { disabled: '1', enable: '0' }
var enumNoticeType = {
	notice: 1,
	msg: 2,
	task: 3
}
// 标准流程模型
var enumWorkFlowModel = {
	preLendingCollect: '资料收集',
	menuApply: '开放权限申请',
	postLoanTask: "贷后跟踪",
	postLoanTaskMiniPerson: "贷后跟踪-小微部个人类",
	postLoanTaskMiniCompany: "贷后跟踪-小微部公司类",
	postLoanRetailIndivMortgage: "贷后跟踪-零售银行部个人按揭",
	postLoanRetailIndivOperate: "贷后跟踪-零售银行部个人经营",
	postLoanRetailIndivConsume: "贷后跟踪-零售银行部个人消费",
	interestRelief: '利息减免',
	npaRegister: '不良资产登记',
	npaManage: '不良资产主流程',
	debtAssets: '抵债资产接收',
	debtAssetsDeal: '资产处置',
	urge: "催收",
	litigation: "诉讼",
	qiangzhizhixing: "强制执行",
	fangwuchuzu: "房屋出租",
	zichanpaimai: "资产拍卖",
	zichanxieyichushou: "资产协议出售"
}

// 不良任务
var npaWorkFlowArrrr = [
	enumWorkFlowModel.urge,
	enumWorkFlowModel.interestRelief,
	enumWorkFlowModel.litigation,
	enumWorkFlowModel.debtAssets,
	enumWorkFlowModel.debtAssetsDeal
];

// 标准流程列表 isSon: 是否为子任务类型
var enumWorkFlowModelList = [
	{ name: enumWorkFlowModel.preLendingCollect, id: '1' },
	{ name: enumWorkFlowModel.menuApply, id: '2'},
	{ name: enumWorkFlowModel.interestRelief, id: '3' },
	{ name: enumWorkFlowModel.npaRegister, id: '4'},
	{ name: enumWorkFlowModel.npaManage, id: '5'},
	{ name: enumWorkFlowModel.debtAssets, id: '6'},
	{ name: enumWorkFlowModel.debtAssetsDeal, id: '7'},
	{ name: enumWorkFlowModel.postLoanTaskMiniPerson, id: '8'},
	{ name: enumWorkFlowModel.postLoanTaskMiniCompany, id: '9'},
	{ name: enumWorkFlowModel.qiangzhizhixing, id: '10'},
	{ name: enumWorkFlowModel.fangwuchuzu, id: '11'},
	{ name: enumWorkFlowModel.zichanpaimai, id: '12'},
	{ name: enumWorkFlowModel.zichanxieyichushou, id: '13'}
]
/**
 * select 下拉选项配置
 */
var selectData = {
	userState: [ {name: '--请选择--', val:''}, {name: '禁用', val: enumUserState.disabled}, {name: '启用', val: enumUserState.enable} ],  //是否禁用
	userState2: [ {name: '禁用', val: enumUserState.disabled}, {name: '启用', val: enumUserState.enable} ],
	noticeState: [ {name: '--请选择--', val:''}, {name:'已读', val:'1'}, {name:'未读', val:'0'} ], // 消息状态
	noticeType: [{name:'--请选择--', val:''}, {name:'通知', val: enumNoticeType.notice }, {name:'站内信', val: enumNoticeType.msg }, {name:'任务管理', val: enumNoticeType.task }] //消息类型
}

var globalEnumData = {
    year: [1970,1971,1972,1973,1974,1975,1976,1977,1978,1979,1980,1981,1982,1983,1984,1985,1986,1987,1988,1989,1990,1991,1992,1993,1994,1995,1996,1997,1998,1999,2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010,2011,2012,2013,2014,2015,2016,2017,2018,2019,2020,2021,2022,2023,2024,2025,2026,2027,2028,2029,2030,2031,2032,2033,2034,2035,2036,2037,2038,2039,2040,2041,2042],
    month: [1,2,3,4,5,6,7,8,9,10,11,12],
	taskState: { COMMON: "公共任务", UNRECEIVED: "待指派", DEALING: "处理中", CANCELED: "已取消", FINISHED: "已完成", PAUSE: "暂停中" },
	workFlowModel: [ enumWorkFlowModel.preLendingCollect, enumWorkFlowModel.menuApply, enumWorkFlowModel.postLoanTaskMiniPerson, enumWorkFlowModel.postLoanTaskMiniCompany, enumWorkFlowModel.postLoanRetailIndivMortgage, enumWorkFlowModel.postLoanRetailIndivOperate, enumWorkFlowModel.postLoanRetailIndivConsume, enumWorkFlowModel.interestRelief, enumWorkFlowModel.npaRegister, enumWorkFlowModel.npaManage, enumWorkFlowModel.debtAssets, enumWorkFlowModel.debtAssetsDeal, enumWorkFlowModel.urge, enumWorkFlowModel.litigation, enumWorkFlowModel.qiangzhizhixing, enumWorkFlowModel.fangwuchuzu, enumWorkFlowModel.zichanpaimai, enumWorkFlowModel.zichanxieyichushou]
}

var odsEnmuData = {"MORTGAGE_FLG":{"0":"否","1":"是"},"GUARANTY_TYPE":{"10001":"单人担保","10002":"多人联保","10003":"多人分保"},"GUARANTEE_TYPE":{"10001":"连带责任保证","10002":"一般责任保证"},"STATUS_CODE":{"10001":"预登记","10002":"占用待担保","10003":"担保","10004":"解除担保"},"CUR_TYPE":{"CNY":"人民币","EUR":"欧元","GBP":"英镑","HKD":"港币","JPY":"日元","MOP":"澳门币","RUB":"俄国卢布","USD":"美元"},"COM_FIN_REP_TYPE":{"01":"企业客户财务信息","02":"行政事业单位客户","03":"财务信息"},"BIZ_TYPE":{},"PRD_TYPE":{"11":"贴现","21":"承兑","31":"保函","41":"贸易融资","51":"转贴现买入","61":"转贴现卖出/再贴现","71":"信用证","01":"对公贷款","02":"对私贷款","03":"农户贷款"},"PRD_USERDF_TYPE":{"1002":"农户生产经营贷款","1003":"非农个人消费贷款","1004":"非农个人生产经营贷款","2001":"农村单位贷款","2003":"非农单位贷款","2004":"房地产贷款","3001":"贴现","3002":"贸易融资","3003":"银团贷款","3004":"社团贷款","3005":"垫款","3006":"CTA（或有账户）"},"INDUS_ADJ_TYPE":{"1":"鼓励","2":"限制","3":"淘汰"},"COM_CITY_FLG":{},"HOLD_CARD":{},"DEPOT_STATUS":{"10000":"未入库","10005":"入库未记账","10006":"入库已记账","10007":"出库未记账","10008":"出库已销帐"},"COM_MRG_BDAY":{},"CHARGEOFF_IND":{"1":"自助发放","2":"柜面发放"},"INDIV_OCC":{"0":"国家机关、党群组织、企业、事业单位负责人；","1":"专业技术人员；","3":"办事人员和有关人员；","4":"商业、服务业人员；","5":"农、林、牧、渔、水利业生产人员；","6":"生产、运输设备操作人员及有关人员；","X":"军人；","Y":"不便分类的其他从业人员；","Z":"未知。"},"LOAN_CARD_ID":{},"LOAN_CARD_EFF_FLG":{"01":"有效","02":"暂停","03":"注销","04":"吊销"},"LOAN_DIRECTION":{},"LOAN_FORM":{"1":"新增贷款","2":"收回再贷","3":"借新还旧","4":"资产重组","5":"转入","9":"其他"},"LOAN_NATURE":{"01":"自营贷款","02":"委托贷款","03":"特定贷款"},"LOAN_KIND_CA":{"11":"短期流动资金贷款","19":"短期其他贷款","21":"中期流动资金贷款","22":"中期基建贷款","23":"中期技改贷款","29":"中期其他贷款","31":"长期基建贷款","32":"长期技改贷款","39":"长期其他贷款"},"MAIN_GUAR_TYPE":{"10":"抵押","20":"质押","30":"保证","00":"信用"},"GUAR_WAY":{"1000":"抵押-单人","1001":"抵押-多人","2000":"质押-单人","2001":"质押-多人","3001":"保证-单人担保","3002":"保证-多人分保","3003":"保证-多人联保"},"ASSURE_MEANS2":{"10":"抵押","20":"质押","30":"保证","00":"信用"},"ASSURE_MEANS3":{"10":"抵押","20":"质押","30":"保证","00":"信用"},"GUAR_CONT_TYPE":{"1":"一般担保合同","2":"最高额担保合同"},"GUAR_CONT_STATE":{"100":"预登记","101":"生效","102":"担保","103":"解除担保","104":"注销","109":"已校验"},"CUS_TYP":{"10001":"担保公司","10002":"个人","10003":"企业"},"BORROWER_RELATION":{"10010":"自己提供抵（质）押品","10011":"家庭成员","10012":"亲戚","10013":"其他关系","10014":"雇佣关系","10015":"企业关键人","10016":"个人股东","10021":"合作伙伴","10022":"关联分公司","10023":"母子公司","10024":"企业股东","10030":"担保公司担保"},"CER_TYPE":{},"INDIV_COM_FLD":{},"INDIV_COM_TYP":{},"GAGE_TYPE":{"10001":"土地使用权","10002":"在建工程","10003":"房地产","10004":"机动车辆","10005":"机械设备","10006":"船舶","10007":"林权","10008":"海域使用权","10009":"抵押其他类"},"GAGE_TYPE_1":{"10011":"贵金属","10012":"债券","10013":"保单","10014":"票据","10015":"仓单/提单","10016":"股权/股票/基金","10017":"存单","10018":"应收账款","10019":"商铺使用权","10021":"质押其他类","10022":"动产类","10023":"商标使用权","10024":"银行承兑汇票","10025":"商业承兑汇票","10026":"收费权"},"COM_IMPTT_FLG":{"0":"否","1":"是"},"COM_ADDR_TYP":{"01":"营业地址","02":"办公地址","03":"生产地址","04":"其他地址"},"LEGAL_CERT_TYPE":{"0":"身份证","1":"户口簿","2":"护照","3":"军官证","4":"士兵证","5":"港澳居民来往内地通行证","6":"台湾同胞来往内地通行证","7":"临时身份证","8":"外国人居留证","9":"警官证","X":"其他证件"},"SPOUSE_CERT_TYPE":{"0":"身份证","1":"户口簿","2":"护照","3":"军官证","4":"士兵证","5":"港澳居民来往内地通行证","6":"台湾同胞来往内地通行证","7":"临时身份证","8":"外国人居留证","9":"警官证","X":"其他证件"},"LEGAL_SEX":{"0":"未知的性别","1":"男性","2":"女性","9":"未说明性别"},"CLA_RESULT":{"10":"正常","20":"关注","30":"次级","40":"可疑","50":"损失","00":"未分类"},"COMP_INT_RATE_INC_OPT":{"1":"按浮动值计算利息","2":"按浮动比计算利息"},"COM_MRG_TYP":{"01":"法定代表人","02":"财务负责人","03":"总经理","04":"实际控制人","05":"董事长"},"INDUS_UPD_FLAG":{"1":"是","2":"否"},"LOAN_TYPE_EXT":{"10":"非关联交易","20":"一般关联交易","30":"重大关联交易"},"COM_COUNTRY":{},"COM_CL_ENTP":{"0":"否","1":"是"},"REPAYMENT_MODE":{"101":"只还利息","102":"等额本息","201":"等额本金","202":"协议还款","203":"分期归还法"},"REPAYMENT_PERIOD":{"01":"每月","02":"每年","03":"双周","04":"每半月","05":"每季度","06":"每半年","07":"到期时"},"COM_CLL_TYPE":{},"COM_CLL_NAME":{"显示格式":"门类 ->  大类 -> 中类 -> 小类"},"CONT_TYPE":{"1":"可循环","2":"不可循环"},"CONT_STATE":{"100":"未生效","200":"生效","300":"注销","800":"已出账","999":"核销"},"INDIV_MAR_ST":{"10":"未婚","20":"已婚","21":"初婚","22":"再婚","23":"复婚","30":"丧偶","40":"离婚","90":"未说明的婚姻状况"},"BAS_ACC_FLG":{"0":"否","1":"是"},"COM_GRP_MODE":{"1":"集团客户母公司","2":"集团客户子公司","3":"非集团客户"},"INTEREST_ACC_MODE":{"01":"每月","02":"每年","03":"双周","04":"每半月","05":"每季度","06":"每半年","07":"到期时"},"LOAN_USE_TYPE":{"110":"周转性流动资金周转","111":"经常性流动资金周转","120":"固定资产项目建设","121":"固定资产技术改造","130":"房地产开发","131":"科技开发","900":"其他","999":"不使用"},"COM_IMP_EXP_FLG":{"0":"否","1":"是"},"FUN_SOURCE":{"1":"财政补贴","2":"非财政补贴"},"COM_OPT_OWNER":{"1":"自有","2":"租赁","3":"其他"},"COM_OPT_ST":{"100":"正常经营","200":"经营困难","300":"关停倒闭-营业执照已吊销","400":"关停倒闭-营业执照未吊销","500":"破产-清算完毕","900":"其他"},"INDIV_RSD_ST":{"1":"自置","2":"按揭","3":"亲属楼宇","4":"集体宿舍","5":"租房","6":"共有住宅","7":"其他","9":"未知"},"CUS_TYPE":{"110":"自然人","120":"其他自然人","130":"一般农户","210":"农村经济组织","211":"企业法人","212":"企业非法人","221":"事业法人","222":"事业非法人","231":"社团法人","232":"社团非法人","241":"机关法人","242":"机关非法人","250":"民办非企业","260":"个体工商户","270":"工会法人","280":"农民专业合作社","290":"担保机构（企业类）","291":"担保机构（事业类）","299":"其他机构"},"CUST_TYPE":{"110":"自然人","120":"其他自然人","130":"一般农户","210":"农村经济组织","211":"企业法人","212":"企业非法人","221":"事业法人","222":"事业非法人","231":"社团法人","232":"社团非法人","241":"机关法人","242":"机关非法人","250":"民办非企业","260":"个体工商户","270":"工会法人","280":"农民专业合作社","290":"担保机构（企业类）","291":"担保机构（事业类）","299":"其他机构"},"CUS_BANK_REL":{"A1":"普通客户关系；","A2":"企业高管是本行管理人员亲属；","A3":"企业高管由本行管理人员兼任。","A4":"本行一般股东；","A5":"本行主要股东（持有本行5％（含）以上股份或有表决权的股东）","A6":"本行主要股东控制的企业","B1":"普通客户关系；","B2":"本行一般股东","B3":"本行主要股东(持有本行5％（含）以上股份)","B4":"本行董事、高管","B5":"本行董事、高管 ＋　股东","B6":"本行董事、高管亲属","B7":"本行普通员工","B8":"本行普通员工＋股东"},"COM_HOLD_TYPE":{"111":"国有绝对控股","112":"国有相对控股","121":"集体绝对控股","122":"集体相对控股","171":"私人绝对控股","172":"私人相对控股","201":"港澳台商绝对控股|","202":"港澳台商相对控股","301":"外商绝对控股","302":"外商相对控股","900":"其他"},"INT_RATE_TYPE":{"1":"固定利率","2":"浮动比例"},"IR_ADJUST_MODE":{"1":"固定利率","2":"立即调整","3":"按月调整","4":"按季调整","5":"按年调整","6":"月初调整","7":"季初调整","8":"年初调整"},"INT_RATE_INC_OPT":{"1":"按浮动值计算利息","2":"按浮动比计算利息"},"IR_EXE_TYPE":{"0":"立即生效","1":"下期生效","2":"对年对月对日生效","3":"次年1月1日生效","4":"次月1日生效","5":"下季1日生效"},"INT_CAL_TYPE":{"01":"按天","02":"按期"},"COM_SUB_TYP":{"1":"中央、省、市(地区)","2":"县(区、市)","3":"街道、镇、乡","4":"居民委员会","5":"村民委员和其他","9":"其他"},"GUARANTORA_GRP_IND":{"0":"否","1":"是"},"COM_HD_ENTERPRISE":{"0":"非龙头企业","1":"国家级龙头","2":"省级龙头","3":"地市级龙头","4":"县级龙头"},"INDIV_NTN":{"10":"朝鲜族","11":"满族","12":"侗族","13":"瑶族","14":"白族","15":"土家族","16":"哈尼族","17":"哈萨克族","18":"傣族","19":"黎族","20":"僳僳族","21":"佤族","22":"畲族","23":"高山族","24":"拉祜族","25":"水族","26":"东乡族","27":"纳西族","28":"景颇族","29":"柯尔克孜族","30":"土族","31":"达斡尔族","32":"仫佬族","33":"羌族","34":"布朗族","35":"撒拉族","36":"毛难族","37":"仡佬族","38":"锡伯族","01":"汉族","02":"蒙族","03":"回族","04":"藏族","05":"维吾尔族","06":"苗族","07":"彝族","08":"壮族","09":"布依族"},"INDIV_SPS_ID_TYP":{"10":"身份证","11":"户口簿","12":"护照","13":"军官证","14":"士兵证","15":"港澳居民来往内地通行证","16":"台湾同胞来往内地通行证","17":"临时身份证","18":"外国人居留证","19":"警官证","1X":"其它证件"},"INDIV_SPS_DUTY":{"1":"高级领导（行政级别局级及局级以上领导或大公司高级管理人员）","2":"中级领导（行政级别局级以下领导或大公司中级管理人员）","3":"一般员工","4":"其他","9":"未知"},"INDIV_SPS_OCC":{"0":"国家机关、党群组织、企业、事业单位负责人","1":"专业技术人员","3":"办事人员和有关人员","4":"商业、服务业人员","5":"农、林、牧、渔、水利业生产人员","6":"生产、运输设备操作人员及有关人员","X":"军人","Y":"不便分类的其他从业人员","Z":"未知"},"EVAL_TYPE":{"10001":"评估公司","10002":"自行协商估价","10003":"内部评估","10004":"其他"},"TERM_UNIT":{"000":"日","001":"月","002":"年"},"TERM_TYPE":{"0":"短期","1":"中期","2":"长期"},"TERM_TIME_TYPE":{"002":"年","001":"月","000":"日"},"COM_SCALE":{"10":"大型企业","20":"中型企业","30":"小型企业","40":"微型企业"},"AREA_CODE":{},"RIGHT_CERT_TYPE_CODE":{"20001":"存单权证","20002":"汇票权证","20003":"保单权证","20004":"债券权证","20005":"仓单/提单权证","20006":"票据权证"},"CLA_PRE":{"10":"正常","20":"关注","30":"次级","40":"可疑","50":"损失","00":"未分类"},"AGRICULTURE_TYPE":{"10":"农村企业涉农","11":"农村组织涉农","12":"城市企业涉农","13":"城市组织涉农","00":"不涉农"},"AGRICULTURE_USE":{"1000":"农林牧渔业贷款","1110":"支农贷款 - 农田基本建设","1120":"支农贷款 - 农产品加工","1130":"支农贷款 - 农业生产资料制造","1141":"支农贷款 - 农业物资和农副产品流通贷款（农产品出口）","1142":"支农贷款 - 农业物资和农副产品流通贷款（其他）","1150":"支农贷款 - 农业科技贷款","1160":"支农贷款 - 农村基础设施建设贷款","1200":"其他贷款"},"GOVERNMENT_ORG_ATTRIBUTE":{"1000":"政府所属企（事）业","2000":"政府职能部门","3000":"政府干预企业"},"PAID_CAP_CUR_TYPE":{"AUD":"澳大利亚元","CAD":"加拿大元","CNY":"人民币","EUR":"欧元","HKD":"港币","JPY":"日元","MOP":"澳门币","GBP":"英镑","CHF":"瑞士法郎","USD":"美元","KRW":"韩元","TWD":"台湾元"},"LOAN_CARD_IND":{"0":"否","1":"是"},"COPARTNER_IND":{"0":"否","1":"是"},"IF_SPECIAL_ASSET":{"0":"否","1":"是"},"COM_MRG_MEMB_FLG":{"0":"否","1":"是"},"IF_COMMON_ASSET":{"0":"否","1":"是"},"COM_DHGH_FLG":{"01":"非双高企业","02":"高污染企业","03":"高耗能企业","04":"高污染并高耗能"},"AGRI_FLG":{"0":"否","1":"是"},"IF_DISPUTED":{"0":"否","1":"是"},"GOVERNMENT_IND":{"0":"不涉政","1":"省级涉政","2":"市级涉政","3":"县级涉政","4":"区级涉政","5":"镇级涉政"},"FHF":{"1":"是","2":"否"},"DISCONT_IND":{"0":"否","1":"是"},"GOVERNMENT_PLATFORM":{"1000":"各类开发区、园区政府投融资平台","2000":"国有资产管理公司","3000":"土地储备中心类公司","4000":"城市投资建设公司","5000":"财政部门设立的税费中心","6000":"交通运输类政府投融资平台","7000":"其他类型政府投融资平台","8000":"非涉政平台"},"COM_LCL_FLG":{"0":"否","1":"是"},"PASSPORT_FLG":{"0":"否","1":"是"},"AVAILABLE_IND":{"0":"否","1":"是"},"COM_MRG_PLR_FLG":{"0":"否","1":"是"},"INDIV_DEPOSITS":{"01":"工薪收入","02":"财产性收入","03":"经营净收入","04":"转移性收"},"LIMIT_IND":{"1":"不使用借款人授信额度","2":"使用借款人循环额度"},"LOAN_FORM4":{"10":"正常","20":"逾期","30":"呆滞","40":"呆帐"},"INDIV_COM_FLD_NAME":{"10":"基本信息完整","11":"基本信息+从表信息完整","12":"基本信息+从表信息+贷款信息完整","13":"基本信息+从表信息+贷款信息+贴现信息完整","14":"基本信息+从表信息+贷款信息+贴现信息+表外业务信息完整","20":"正式客户","90":"注销","91":"合并注销","00":"草稿"},"ACCOUNT_STATUS":{"0":"出帐未确认","1":"正常","4":"结清","5":"销户","6":"垫款","7":"已扣款","8":"退回未用","9":"核销"},"COM_SP_BUSINESS":{"0":"否","1":"是"},"SUB_TYPE":{"01":"贴息利率","02":"固定金额"},"SUB_EA_TYPE":{"01":"一次性扣减","02":"减少月供"},"INVEST_TYPE":{"1":"中央政府投资","2":"省级政府投资","3":"地市级政府投资","4":"区县级政府投资","5":"法人投资","6":"自然人投资"},"PRIN_INT_RATE_INC_OPT":{"1":"按浮动值计算利息","2":"按浮动比计算利息"},"DEFAULT_FLAG":{"0":"否","1":"是"},"ENTRUST_IND":{"0":"否","1":"是"},"INDIV_EDT":{"10":"研究生","20":"大学本科（简称“大学”）","30":"大学专科和专科学校（简称“大专”）","40":"中等专业学校或中等技术学校","50":"技术学校","60":"高中","70":"初中","80":"小学","90":"文盲或半文盲","99":"未知"},"COM_CUS_RANK":{"01":"黄金客户","02":"重点客户","03":"优质客户","04":"维持类客户","05":"限额管理类客户"},"CLA":{"10":"正常","20":"关注","30":"次级","40":"可疑","50":"损失","00":"未分类"},"AUTO_CLA_RESULT":{"10":"正常","20":"关注","30":"次级","40":"可疑","50":"损失","00":"未分类"},"LAST_CLA_RESULT":{"10":"正常","20":"关注","30":"次级","40":"可疑","50":"损失","00":"未分类"},"MINOR_ENTRP_RANGE":{"0":"否","1":"是"},"COM_CRD_GRADE":{"11":"AAA","12":"AA","13":"A","14":"BBB","15":"BB","16":"B","19":"C","00":"未评级"},"COM_OUT_CRD_GRADE":{},"COM_CRD_TYP":{"11":"中介服务业","21":"公用事业","22":"交通运输业","31":"投资管理类","41":"事业单位","51":"施工业","52":"宾馆餐饮业<","61":"商业(批发)","62":"商业(零售)","71":"综合","81":"房地产业","99":"其他","01":"工业(化工)","02":"工业(电子通讯)","03":"工业(机械制造)","04":"工业(冶金)","05":"工业(纺织服装)","06":"工业(其他)","07":"工业(建材)"},"COM_MRG_SEX":{"1":"男","2":"女","3":"未知的性别","4":"未说明的性别"},"INDIV_SEX":{"0":"未知的性别","1":"男性","2":"女性","9":"未说明性别"},"ISCIRCLE":{"0":"否","1":"是"},"SYNDICATED_IND":{"1":"该笔为主办社是我行的2215/2210产品主台账","3":"该笔为主办社为他行，我行为参与的自营部分台账"},"UNPD_INT_RATE_INC_OPT":{"1":"按浮动值计算利息","2":"按浮动比计算利息"},"COM_PREP_FLG":{"0":"否","1":"是"},"LOAN_CARD_FLG":{"0":"无","1":"有"},"DELAY_DEDUCT_TYPE":{"00":"有多少扣多少","01":"不足一期不扣"},"RELATION_LENDER":{"10010":"自己提供抵（质）押品","10011":"家庭成员","10012":"亲戚","10013":"其他关系","10014":"雇佣关系","10015":"企业关键人","10016":"个人股东","10021":"合作伙伴","10022":"合作伙伴","10023":"母子公司","10024":"企业股东","10030":"担保公司担保"},"COM_REL_DGR":{"01":"非常密切","02":"密切","03":"一般"},"INDIV_HLD_ACNT":{"1":"住房贷款","2":"理财客户","3":"其他个人贷款户","4":"大额储蓄(1o万元以上)"},"BANK_DUTY":{"1":"理事/董事","2":"监事","3":"影响信贷决策的管理人员","4":"其他人员"},"EXTEND_INT_RATE_TYPE":{"1":"固定利率","2":"浮动比例"},"STRA_INDUS_TYPE":{"1":"节能环保","2":"新一代信息技术","3":"生物医药","4":"高端装备制造","5":"新能源","6":"新材料","7":"新能源汽车"},"COM_MRG_CERT_TYP":{"10":"居民身份证","11":"护照","12":"军人身份证","13":"港澳居民来往内地通行证","14":"台湾同胞来往内地通行证","20":"组织机构代码","30":"临时身份证","31":"户口簿","32":"武装警察身份证","33":"外交人员身份证","34":"外国人居留许可证","35":"边民出入境通行证","36":"个人其它证件","37":"企业法人营业执照","38":"企业营业执照","39":"政府人事部门或编制委员会的批文","40":"事业单位法人登记证书","41":"财务部门同意其开户的证明","42":"社会团体登记证书","43":"上级主管部门批文或证明","44":"工会法人资格证书","45":"民办非企业登记证书","46":"驻华机构登记证","47":"个体工商营业执照","48":"企业名称预先核准通知书","49":"国税登记证","50":"地税登记证","51":"批准证书","52":"进出口业务资格证书","53":"外贸易经营者备案登记表","54":"金融许可证","55":"外汇业务核准证","56":"外汇登记证","57":"开户许可证","58":"办学许可证","59":"单位其它证件","1X":"其它证件"},"INDIV_CRTFCTN":{"0":"无","1":"高级","2":"中级","3":"初级","9":"未知"},"COM_MRG_DUTY":{"1":"高级领导（行政级别局级及局级以上领导或大公司高级管理人员）","2":"中级领导（行政级别局级以下领导或大公司中级管理人员）","3":"一般员工","4":"其他","9":"未知"},"COM_MRG_OCC":{"0":"国家机关、党群组织、企业、事业单位负责人","1":"专业技术人员","3":"办事人员和有关人员","4":"商业、服务业人员","5":"农、林、牧、渔、水利业生产人员","6":"生产、运输设备操作人员及有关人员","X":"军人","Y":"不便分类的其他从业人员","Z":"未知"},"ASSURE_MEANS_MAIN":{"10":"抵押","20":"质押","30":"保证","00":"信用"},"REG_TYPE":{"110":"国有企业","120":"集体企业","130":"股份合作企业","141":"联营企业－国有联营企业","142":"联营企业－集体联营企业","143":"联营企业－国有与集体联营企业","144":"联营企业－国有与私人联营企业","145":"联营企业－集体与私人联营企业","149":"联营企业－国有、集体和私人联营企业","151":"有限责任公司－国有独资公司","159":"有限责任公司－其他有限责任公司","160":"股份有限公司","171":"私营企业－私营独资企业","172":"私营企业－私营合伙企业","173":"私营企业－私营有限责任公司","174":"私营企业－私营股份有限"},"REG_STATE_CODE":{},"REG_AREA_CODE2":{},"REG_AREA_CODE3":{},"REG_CUR_TYPE":{"AUD":"澳大利亚元","CAD":"加拿大元","CNY":"人民币","EUR":"欧元","HKD":"港币","JPY":"日元","MOP":"澳门币","GBP":"英镑","CHF":"瑞士法郎","USD":"美元","KRW":"韩元","TWD":"台湾元"},"SEOT":{"1":"小企业专营中心贷款","2":"三农专营中心贷款","3":"非专营中心贷款"},"BAD_LOAN_FLAG":{"0":"否","1":"是"},"REPAY_FLAG":{"10":"正常还款完成","20":"逾期未还款","21":"逾期已还款","00":"未执行"},"FUNDS_SRC":{"01":"银团资金","02":"社团资金","03":"委托资金","04":"其他资金"},"TENANCY_CIRCE":{"0":"否","1":"是"},"COM_MRG_EDT":{"10":"研究生","20":"\"大学本科（简称\"\"大学\"\"）\"","30":"\"大学专科和专科学校（简称\"\"大专\"\"）\"","40":"中等专业学校或中等技术学校","50":"技术学校","60":"高中","70":"初中","80":"小学","90":"文盲或半文盲","99":"未知"},"COM_MRG_DGR":{"0":"其他","1":"名誉博士","2":"博士","3":"硕士","4":"学士","9":"未知"},"CERT_TYPE":{"0001":"居民身份证","0002":"临时身份证","0003":"护照","0004":"户口簿","0005":"军人身份证","0006":"武装警察身份证","0007":"港澳台居民往来内地通行证","0008":"外交人员身份证","0009":"外国人居留许可证","0010":"边民出入境通行证","0011":"个人其它证件","0021":"企业法人营业执照","0022":"企业营业执照","0023":"政府人事部门或编制委员会的批文","0024":"事业单位法人登记证书","0025":"财务部门同意其开户的证明","0026":"社会团体登记证书","0027":"上级主管部门批文或证明","0028":"工会法人资格证书","0029":"民办非企业登记证书","0030":"驻华机构登记证","0031":"个体工商营业执照","0032":"企业名称预先核准通知书","0033":"组织机构代码证","0034":"国税登记证","0035":"地税登记证","0036":"批准证书","0037":"进出口业务资格证书","0038":"外贸易经营者备案登记表","0039":"金融许可证","0040":"外汇业务核准证","0041":"外汇登记证","0042":"开户许可证","0043":"办学许可证","0044":"单位其它证件","01":"居民身份证","02":"临时身份证","03":"护照","04":"户口簿","05":"军人身份证","06":"武装警察身份证","07":"港澳台居民往来内地通行证","08":"外交人员身份证","09":"外国人居留许可证","10":"边民出入境通行证","11":"个人其它证件","21":"企业法人营业执照","22":"企业营业执照","23":"政府人事部门或编制委员会的批文","24":"事业单位法人登记证书","25":"财务部门同意其开户的证明","26":"社会团体登记证书","27":"上级主管部门批文或证明","28":"工会法人资格证书","29":"民办非企业登记证书","30":"驻华机构登记证","31":"个体工商营业执照","32":"企业名称预先核准通知书","33":"组织机构代码证","34":"国税登记证","35":"地税登记证","36":"批准证书","37":"进出口业务资格证书","38":"外贸易经营者备案登记表","39":"金融许可证","40":"外汇业务核准证","41":"外汇登记证","42":"开户许可证","43":"办学许可证","44":"单位其它证件"}};



    function getFetch(url, data, callback, error, ops) {
    	if(typeof data == 'function'){
            error = callback;
    		callback = data;
    		data = null;
		}
		var idex = url.indexOf("http://")
		if(idex == -1 || (idex > -1 && idex != 0)){
			url = remoteOrigin + url;
		}
    	var headers = {}
    	var hasHeader = 0
        return $.ajax({
            url: url
            , dataType: "json"
            // , dataType: "jsonp"
            // , jsonp: "callback"
            , data: data,
			async: !ops || !ops.sync,
			headers :{
            	Token: top.store.get("token") || ""
			}
            , success: function(res){
            	if(res.success || res.Status=="200"){
					callback && callback(res.data)
				}else{
					if(res.errMessage == '请检查是否登录'){
						loginOut();
					}
					else if(res.errMessage != ""){
						layer.msg(res.errMessage, {icon: 5})
					}
					return;
				}

			}
            , error: error
        });
    }

    /**
     * only supports json params!!
     *
     * @param url
     * @param data
     * @param callback
     * @param error
     */
    var $$ifcount = 0;
    function postFetch(url, data, callback, error){
    	if(typeof data == "string"){
    		data = $.unserialize(data)
		}
		data = JSON.stringify(data);
       	return $.ajax({
			url: url
			, dataType: "json"
            , type: "post"
			, contentType: "application/json"
			, data: data,
			headers :{
				Token: top.store.get("token") || ""
			}
			, success: function (res) {
                if(!res.success){
                    if(res.errMessage == '请检查是否登录'){
                        loginOut();
                    }
                    else if(res.errMessage != ""){
                        layer.msg(res.errMessage, {icon: 5})
                    }
                    return;
                }
				callback&&callback(res.data);
            }
            , error: error
		})

        return;
        var cross = false;
        //同域的情况
        if(url.indexOf(location.origin + "/") != 0){
            cross = true;
        }
        // console.log(url,location.origin,cross)
        // var str = JSON.stringify(data);
        var ifid = "postIf_" + (top.$$ifcount++)
        var form = $("<form>").attr({
            action: url
            , method: "post"
            , target: ifid
        })
            .css({
                display: "none"
            })
            .appendTo("body");
        //add attributes
        for(var i in data){
            form.append("<input type='hidden' name='"+i+"' value='"+JSON.stringify(data[i])+"'/>");
            // .attr({
            //     type: "hidden"
            //     , name: i
            // }).val((JSON.stringify(data[i]))));
		}
        //
        // if(cross){
		form.append("<input type='hidden' name='callback' value='"+ifid+"'/>")
            // form.append($("<input>").attr({
            //     type: "hidden"
            //     , name: "callback"
            // }).val(ifid))
        // }
        var ifr = $("<iframe name='"+ifid+"' id='"+ifid+"' style='display: none'></iframe>")
            // .attr({
            //     name: ifid
            //     , id: ifid
            // })
            // .css("display","none")
            .appendTo("body");
        // console.log(ifr)
        //when this iframe loaded at first time, do not trigger the callback
        // window[ifid] = function(data){
        //     // alert(4)
        //     form.remove();
        //     ifr.remove();
        //    	window[ifid] = null;
        // 	callback && callback(data);
		// };
        // ifr[0].onload = function(){
        //     // alert("finish")
		// 	//clean
        // };
        form.submit();
    }

    function renderSearchForm(options, callback){
        layui.use(["laytpl","form"],function () {
            var done = function () {
                var xml = window.$$tmpl;
                var html = window.$$tmpl.render(options);
                callback(html);
                // layui.form.render();
            };
            window.$$tmpl ? done() : $.get("../../tmpl/tmpl.html",function (xml) {
                        var html = $(xml).filter("#search_form").html();
                    window.$$tmpl = layui.laytpl(html);
                    done();
                });
        });
    }

    var $$countDown = 0;
    function getCountDown() {
    	return $$countDown++;
    }

    function closeCurrentIframe() {
    	var id = (frameElement && frameElement.id);
    	if(id){
            top.$("a[data-iframe=" + id + "]").next("i").click()
		}
    }

    if(typeof $ !== 'undefined'){
    	$(document).on("click","[tab-href]", function () {
    		var id = $(this).attr('tab-id');
    		if(!id){
    			id = "new-tab-" + top.getCountDown()
			}
			top.addTab(
				id
				, $(this).attr("tab-title") || $(this).text()
				, $(this).attr("tab-href")
			)
        });

    	$(document).on("click","[delete-href]", function () {
    		var href=  $(this).attr("delete-href");
			actConfirm(function () {
				getFetch(href, function () {
					if(window.onInfront){
						window.onInfront();
					}
                });
            });
        });

    	$(document).on("click","[action-href]", function () {
			var t = $(this);
			var href = t.attr('action-href');
			var cb = function () {
                getFetch(href, function () {
                    window.onInfront() && window.onInfront();
                })
            };
			if(t.attr("action-confirm")){
				actConfirm(function () {
					cb();
                });
			}
			else{
				cb();
			}
        });
	}

function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
function guid() {
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}

if(window.top != window){
	$(function () {
		$("body").css("padding-bottom","100px")
		$("body").css("overflow","auto")
		$("body").css("overflow-x","hidden")
	})
}