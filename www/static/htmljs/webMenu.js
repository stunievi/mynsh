
var _menu = [
    {
        id: "menu-mybench",
        name:'工作台',
        icon: '<span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>&nbsp',
        children:[
            {
                name: "还款余额不足",
                href: "/htmlsrc/postLoanManage/insufficientBalance.html"
            },
            {
                id: "menu-mybench-bench",
                name: '我的工作台',
                href: '/htmlsrc/bench/myBench.html',
                api: [ ],
                childs: [
                    { name: "待处理任务", api: [remoteApi.apiMyNeedingWorks], className:["#myTabs a[href='#taskType3']","#taskType3"] },
                    { name: "已处理任务", api: [remoteApi.apiMyDealedWorks] },
                    { name: "我观察的任务", api: [remoteApi.apiMyObserveredWorks] },
                    { name: "部门待处理任务", api: [remoteApi.apiDeptUndealedWorks] },
                    { name: "公共任务", api: [remoteApi.apiCanAcceptCommonWorks, remoteApi.apiAcceptCommonWork]},
                    { name: "个人任务统计图表" }
                ]
            },
            {
                id: "menu-mybench-notice2",
                name: "收件箱",
                href:'/htmlsrc/bench/notice/notice.list.html',
                api: [remoteApi.apiReadMessages, remoteApi.apiUserRecentMsg, remoteApi.apiOpenDownload, remoteApi.apiMsgSendString, remoteApi.apiGetUnreadUserList, remoteApi.apiGetNoticeList,remoteApi.apiMsgSendFile]
            },
            {
                id: "menu-mybench-notice",
                name: "发件箱",
                href:'/htmlsrc/bench/notice/notice.sent.html',
                api: [remoteApi.apiReadMessages, remoteApi.apiUserRecentMsg, remoteApi.apiOpenDownload, remoteApi.apiMsgSendString, remoteApi.apiGetUnreadUserList, remoteApi.apiGetNoticeList,remoteApi.apiMsgSendFile]
            },
            {
                id: "menu-put-user-info",
                name: "个人设置",
                href: "/htmlsrc/bench/personalSetting.html",
                api:[remoteApi.apiUserFace, remoteApi.apiUserFaceEdit, remoteApi.apiModifyPassword, remoteApi.apiModifyProfile]
            },
            {
                id: "menu-change-password",
                name: "修改密码",
                href: "/htmlsrc/bench/setPassword.html"
            },
            {
                name: "个人信息",
                href: "/htmlsrc/bench/personalInfo.html"
            }
        ]
    },
    {
        name: '信贷数据管理',
        icon: '<span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>&nbsp;',
        children:[
            // {
            //     name: "数据维护",
            //     children: [
            //         // {
            //         //     name: "数据导入管理",
            //         //     href: "/htmlsrc/creditDataManage/dataImportManage/dataImport.list.html"
            //         // },
            //         {
            //             name: "ODS数据同步",
            //             href: "javascript:;"
            //         },
            //     ]
            // },
            {
                name: "关联方",
                children: [
                    {
                        name: "集团客户贷款",
                        href: "/htmlsrc/creditDataManage/linkLoan/groupCusListLoan.html"
                    },
                    {
                        name: "关联方贷款",
                        href: "/htmlsrc/creditDataManage/linkLoan/linkListLoan.html"
                    },
                    {
                       name: "股东及股东关联贷款",
                        href: "/htmlsrc/creditDataManage/linkLoan/stockholderLoan.html"
                    }
                ]
            },
            {
                name: "客户资料",
                children:[
                    {
                        name: "所有客户",
                        href: "/htmlsrc/dataQuery/clientList/clientList.html"
                    },
                    {
                        name: "对公客户",
                        href: '/htmlsrc/dataQuery/company/company.list.html',
                        api: [remoteApi.apiOdsSearchPublicClient, remoteApi.apiOdsSearchCUS_COM, remoteApi.apiOdsSearchCusComManager, remoteApi.apiOdsSearchComAddr]
                    },
                    {
                        name: "对私客户",
                        href: '/htmlsrc/dataQuery/private/private.list.html',
                        api: [ remoteApi.apiOdsSearchPrivateClient, remoteApi.apiOdsSearchCUS_INDIV, remoteApi.apiOdsSearchCusInDiv ]
                    }
                ]
            },
            {

                name: "贷款资料",
                children: [
                    {
                        name: "贷款台账",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?su=1&linkPerson=1",
                        api: [remoteApi.apiOdsSearchAccloan,remoteApi.apiOdsSearchACC_LOAN, remoteApi.apiOdsSearchGrtGuar, remoteApi.apiOdsSearchCrtLoan, remoteApi.apiOdsSearchGRTGBasicInfo]
                    },
                    {
                        name: "贷款资料",
                        href: "/htmlsrc/creditDataManage/ledger/loanData.html",
                        api: [remoteApi.apiOdsSearchAccloanData,remoteApi.apiOdsSearchACC_LOAN,remoteApi.apiWorkflowBinds]
                    }
                    ,{
                        name: "抵押物明细"
                        , href: "/htmlsrc/creditDataManage/ledger/dywmx.html"
                    }
                ]
            },
            {
                name: "其他信息",
                children: [
                    {
                        name: "拒贷记录",
                        href: "/htmlsrc/creditDataManage/reject/rejectCollect.html",
                        api: [remoteApi.apiGetRejectCollectList]
                    },
                    {
                        name: "实际控制人查询"
                        , href: "/htmlsrc/creditDataManage/ledger/loanData.rm.list.html"
                    }
                    /*,
                    {
                        name: "全行共享查询",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?su=1"
                    }*/
                ]
            }
        ]
    },
    {
        name: "对公客户贷后信息",
        children: [
            {
                name: "客户列表",
                href: "/htmlsrc/comInfoQuery/company/company.list.html"
            }
            ,{
                name: "风险信息",
                href: "/htmlsrc/comInfoQuery/company/qccRiskInfo.html"
            }
            // {
            //     name: "动产抵押",
            //     href: "/htmlsrc/qichacha/ChattelMortgage/ChattelMortgageDetail.html"
            // }
        ]
    },
    {
        name: '资料收集',
        icon: '<span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>&nbsp;',
        children: [
            {
                name: '资料收集',
                href: '/htmlsrc/preLendingCollect/collect.html',
                api: [remoteApi.apigetMyInfoCollect]
            },
            {
                name: "台账绑定",
                href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.preLendingCollect),
                api: [remoteApi.apiOdsSearchAccloan]
            }
        ]
    },


    {
        name: '贷后管理',
        icon: '<span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>&nbsp;',
        children: [
            {
                name: "指派/移交",
                href: "/htmlsrc/taskManage/taskTransfer.html?workFlow="+encodeURI(enumWorkFlowModel.postLoanTask),
                api: [remoteApi.apiGetUnreceivedWorks, remoteApi.apiPointTask]
            },
            // {
            //     name: "任务接受",
            //     href: "/htmlsrc/taskManage/taskAccept.html?workFlow="+encodeURI(enumWorkFlowModel.postLoanTask),
            //     api: [remoteApi.apicanAccpetWorks, remoteApi.apiWorkflowRejectWorks,remoteApi.apiWorkflowAcceptWorks ]
            // },
            {
                name: "任务发起",
                href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.postLoanTask),
                api: [remoteApi.apiOdsSearchAccloan]
            },
            {
                name: "预任务列表",
                href: "/htmlsrc/taskManage/preTask.html",
                api: [remoteApi.apiGetPreWorks, remoteApi.apiPointTask]
            },
            {
                name: "任务列表",
                href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.postLoanTask),
                api: [remoteApi.apiWorkflowIinstances, remoteApi.apiWorkflowGoNext]
            }
        ]
    },
    {
        name: "开放权限申请",
        icon: '<span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>&nbsp;',
        children: [
            {
                name: "任务发起",
                href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.menuApply),
                api: [remoteApi.apiOdsSearchAccloan]
            },
            {
                name: "任务列表",
                href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.menuApply),
                api: [remoteApi.apiWorkflowIinstances, remoteApi.apiWorkflowGoNext]
            }
        ]
    },
    {
        name: '不良资产管理',
        icon: '<span class="glyphicon glyphicon-align-left" aria-hidden="true"></span>&nbsp;',
        children: [
            {
                name: "不良资产登记",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+enumWorkFlowModel.npaRegister,
                        api: [
                            remoteApi.apiOdsSearchAccloan+"?register=false&modelName="+encodeURI(enumWorkFlowModel.npaRegister)
                        ]
                    },
                    {
                        name: "登记记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+enumWorkFlowModel.npaRegister,
                        api: [
                            remoteApi.apiWorkflowIinstances+"?modelName="+encodeURI(enumWorkFlowModel.npaRegister),
                            remoteApi.apiWorkflowGoNext
                        ]
                    }
                ]
            },
            {
                name: "不良资产主流程",
                children: [
                    {
                        name: "指派移交",
                        href: "/htmlsrc/taskManage/taskTransfer.html?workFlow="+encodeURI(enumWorkFlowModel.npaManage),
                        api: [
                            remoteApi.apiGetUnreceivedWorks+"?modelName="+encodeURI(enumWorkFlowModel.npaManage),
                            remoteApi.apiPointTask
                        ]
                    },
                    // {
                    //     name: "任务接受",
                    //     href: "/htmlsrc/taskManage/taskAccept.html?workFlow="+encodeURI(enumWorkFlowModel.npaManage),
                    //     api: [
                    //         remoteApi.apicanAccpetWorks,
                    //         remoteApi.apiWorkflowRejectWorks,
                    //         remoteApi.apiWorkflowAcceptWorks
                    //     ]
                    // },
                    {
                        name: "任务列表",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.npaManage),
                        api: [
                            remoteApi.apiWorkflowIinstances+"?modelName="+encodeURI(enumWorkFlowModel.npaManage),
                            remoteApi.apiWorkflowGoNext
                        ]
                    }
                ]
            },
            {
                name: "催收管理",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.urge),
                        api: [
                            remoteApi.apiOdsSearchAccloan+"?register=true&modelName="+encodeURI(enumWorkFlowModel.urge)
                        ]
                    },
                    {
                        name: "催收记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.urge),
                        api: [
                            remoteApi.apiWorkflowIinstances+"?modelName="+encodeURI(enumWorkFlowModel.urge),
                            remoteApi.apiWorkflowGoNext
                        ]

                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.urge)
                    }
                ]
            },
            {
                name: "利息减免管理",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.interestRelief),
                        api: [
                            remoteApi.apiOdsSearchAccloan+"?register=true&modelName="+encodeURI(enumWorkFlowModel.interestRelief)
                        ]
                    },
                    {
                        name: "利息减免记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.interestRelief),
                        api: [
                            remoteApi.apiWorkflowIinstances+"?modelName="+encodeURI(enumWorkFlowModel.interestRelief),
                            remoteApi.apiWorkflowGoNext
                        ]
                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.interestRelief)
                    }
                ]
            },
            {
                name: "诉讼管理",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.litigation),
                        api: [
                            remoteApi.apiOdsSearchAccloan+"?register=true&modelName="+encodeURI(enumWorkFlowModel.litigation)
                        ]
                    },
                    {
                        name: "诉讼记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.litigation),
                        api: [
                            remoteApi.apiWorkflowIinstances+"?modelName="+encodeURI(enumWorkFlowModel.litigation),
                            remoteApi.apiWorkflowGoNext
                        ]
                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.litigation)
                    }
                ]
            },
            {
                name: "抵债资产接收管理",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.debtAssets),
                        api: [
                            remoteApi.apiOdsSearchAccloan+"?register=true&modelName="+encodeURI(enumWorkFlowModel.debtAssets)
                        ]
                    },
                    {
                        name: "抵债资产接收记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.debtAssets),
                        api: [
                            remoteApi.apiWorkflowIinstances+"?modelName="+encodeURI(enumWorkFlowModel.debtAssets),
                            remoteApi.apiWorkflowGoNext
                        ]
                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.debtAssets)
                    }
                ]
            },
            {
                name: "资产处置管理",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.debtAssetsDeal),
                        api: [
                            remoteApi.apiOdsSearchAccloan+"?register=true&modelName="+encodeURI(enumWorkFlowModel.debtAssetsDeal)
                        ]
                    },
                    {
                        name: "资产处置记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.debtAssetsDeal),
                        api: [
                            remoteApi.apiWorkflowIinstances+"?modelName="+encodeURI(enumWorkFlowModel.debtAssetsDeal),
                            remoteApi.apiWorkflowGoNext
                        ]
                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.debtAssetsDeal)
                    }
                ]
            }
        ]
    },

    {
        name: '文件管理',
        icon: '<span class="glyphicon glyphicon-paperclip" aria-hidden="true"></span>&nbsp;',
        children:[
            {
                name: '个人文件柜',
                href: '/htmlsrc/document/private/cloud.fileList.html'
            },
            // {
            //     name: "个人文件柜回收站",
            //     href: "/htmlsrc/document/myFileTrashListSync.html"
            // },
            {
                name: '资料库',
                href: '/htmlsrc/document/public/commCloud.fileList.html'
            },
            // {
            //     name: "资料库回收站",
            //     href: "/htmlsrc/document/archiveFileRecycle.html"
            // },
            {
                name: "文件检索",
                href: "/htmlsrc/document/cloudSearch.html"
            },
            {
                name: '文件分享',
                children: [
                    {
                        name: '我的分享',
                        href: '/htmlsrc/document/share/myShare.html'
                    },
                    {
                        name: '共享给我的',
                        href: '/htmlsrc/document/share/receiveShare.html'
                    }
                ]
            }
            // {
            //     name: '个人文件柜',
            //     href: '/htmlsrc/document/private/private.fileList.html'
            // },
            // {
            //     name: '公共文件柜',
            //     href: '/htmlsrc/document/public/public.fileList.html'
            // }
        ]
    },
    {
        name: '数据报表',
        icon: '<span class="glyphicon glyphicon-equalizer"></span>&nbsp;',
        children: [
            //{
            //    name: "报表列表",
            //    href:'/htmlsrc/creditDataManage/reports/reports.list.html'
            //},
            {
                name: "预期/逾期",
                children: [
                    {
                        name: "预期应收本金（到期）",
                        href: "/htmlsrc/creditDataManage/reports/reports_cap_expect.html"
                    },
                    {
                        name: "预期应收利息",
                        href: "/htmlsrc/creditDataManage/reports/reports_int_expect.html"
                    },
                    {
                        name: "逾期应收本金",
                        href: "/htmlsrc/creditDataManage/reports/reports_cap_overdue.html"
                    },
                    {
                        name: "逾期应收利息",
                        href: "/htmlsrc/creditDataManage/reports/reports_int_overdue.html"
                    }
                ]
            },
            {
                name: "授信管理部",
                //href: '/htmlsrc/creditDataManage/reports/reports.list.html?dept=credit',
                children: [
                    // {
                    //     name: "表内正常贷款欠息情况统计报表",
                    //     href: "/htmlsrc/creditDataManage/reports/reports_r22.html"
                    // },
                    // {
                    //     name: "隐性不良贷款明细表",
                    //     href: "/htmlsrc/creditDataManage/reports/reports_r23.html"
                    // },
                    // {
                    //     name: "五级分类不良贷款明细表",
                    //     href: "/htmlsrc/creditDataManage/reports/reports_r24.html"
                    // },
                    // {
                    //     name: "五级分类不良贷款现金收回明细表",
                    //     href: "/htmlsrc/creditDataManage/reports/reports_r25.html"
                    // },

                    //{
                    //    name: "广东省农村合作金融机构不良贷款压降情况季报表",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r21.html"
                    //},
                    {
                        name: "信贷资产质量情况统计表（月报表）",
                        href: "/htmlsrc/creditDataManage/reports/reports_r1.html"
                    },
                    {
                        name: "五级分类不良贷款现金收回明细表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r9.html"
                    },
                    {
                        name: "五级分类不良贷款上调明细表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r10.html"
                    },
                    {
                        name: "一般贷款上一年年度统计表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r11.html"
                    },
                    {
                        name: "信贷营销情况（一般贷款）",
                        href: "/htmlsrc/creditDataManage/reports/reports_r12.html"
                    },
                    {
                        name: "信贷营销情况（按揭贷款）",
                        href: "/htmlsrc/creditDataManage/reports/reports_r13.html"
                    },
                    {
                        name: "信贷资产质量情况",
                        href: "/htmlsrc/creditDataManage/reports/reports_r17.html"
                    },
                    {
                        name: "按担保类型划分",
                        href: "/htmlsrc/creditDataManage/reports/reports_r19.html"
                    },
                    {
                        name: "前十大户和最大单户情况",
                        href: "/htmlsrc/creditDataManage/reports/reports_r20.html"
                    },
                    {
                        name: "房地产开发贷款情况",
                        href: "/htmlsrc/creditDataManage/reports/reports_r21.html"
                    },
                    {
                        name: "正常贷款的五大欠息户",
                        href: "/htmlsrc/creditDataManage/reports/reports_r25.html"
                    },
                    {
                        name: "新增贷款利率结构表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r26.html"
                    },
                    {
                        name: "正常类贷款逾期统计表"
                        , href: "/htmlsrc/creditDataManage/reports/reports_r30.html"
                    },
                    //{
                    //    name: "广东省农村合作金融机构贷款五级分类迁徙表",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r20.html"
                    //},
                    // {
                    //     name: "信贷营销情况（一般贷款）",
                    //     href: "/htmlsrc/creditDataManage/reports/reports_r3.html"
                    // },
                    // {
                    //     name: "信贷营销情况（按揭贷款）",
                    //     href: "/htmlsrc/creditDataManage/reports/reports_r28.html"
                    // }
                    //{
                    //    name: "发放及到期收回情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r12.html"
                    //},
                    //{
                    //    name: "利息收回情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r29.html"
                    //},
                    //{
                    //    name: "正常类贷款欠息情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r30.html"
                    //},
                    //{
                    //    name: "信贷资产质量情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r13.html"
                    //},
                    //{
                    //    name: "隐性不良贷款情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r14.html"
                    //},
                    //{
                    //    name: "前十大户和最大单户情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r16.html"
                    //},
                    //{
                    //    name: "房地产贷款情况 - 房地产开发贷款情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r17.html"
                    //},
                    //{
                    //    name: "个人按揭业务情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r18.html"
                    //},
                    //{
                    //    name: "卡贷宝贷款业务情况",
                    //    href: "/htmlsrc/creditDataManage/reports/reports_r19.html"
                    //},
                    {
                        name: "新增贷款统计表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r31.html"
                    }
                    ,{
                        name: "新增不良贷款统计表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r32.html"
                    }

                ]
            }
            ,{
                name: "风险资产管理部",
                href:'/htmlsrc/creditDataManage/reports/reports.list.html?dept=risk',
                children: [
                    //        {
                    //            name: "表外不良贷款清收旬报表",
                    //            href: "/htmlsrc/creditDataManage/reports/reports_r9.html"
                    //        },
                    //        {
                    //            name: "广东省农村合作金融机构以物抵债情况季报表",
                    //            href: "/htmlsrc/creditDataManage/reports/reports_r10.html"
                    //        },
                    {
                        name: "不良贷款重点系列（大户）情况表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r11.html"
                    }
                    //        {
                    //            name: "不良贷款减免情况明细表(季报)",
                    //            href: "/htmlsrc/creditDataManage/reports/reports_r32.html"
                    //        }
                    //
                ]
            }
            ,{
                name:"公共报表"
                ,children:[
                    {
                        name: "分期贷款单户明细表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r33.html"
                    },
                    {
                        name: "表内正常贷款欠息情况统计报表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r6.html"
                    },
                    {
                        name: "隐性不良贷款明细表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r7.html"
                    },
                    {
                        name: "五级分类不良贷款明细表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r8.html"
                    },
                    {
                        name: "新增贷款明细表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r27.html"
                    },
                    {
                        name: "新增不良贷款明细表",
                        href: "/htmlsrc/creditDataManage/reports/reports_r28.html"
                    },
                    {
                        name: "逾期台帐明细表"
                        , href: "/htmlsrc/creditDataManage/reports/reports_r29.html"
                    }
                ]
            }
        ]

    },
    {
        name: '系统管理',
        icon: '<span class="glyphicon glyphicon-wrench" aria-hidden="true"></span>&nbsp;',
        children:[
            {
                name:'系统设置',
                children:[
                    {
                        name: '系统设置',
                        href: '/htmlsrc/sysManage/baseSet/base.html',
                        api: [
                            remoteApi.apiSetSysVar
                        ]
                    },
                    {
                        name: '系统信息',
                        href: '/htmlsrc/sysManage/baseSet/sysInfo.html',
                        api: [ ]
                    }
                ]
            },
            {
                name:'数据维护',
                children:[
                    {
                        name: '信贷中间表',
                        href: '/htmlsrc/sysManage/dataManage/dataMaintain.html',
                        api: []
                    },
                    {
                        name: '资本净额维护',
                        href: '/htmlsrc/sysManage/dataManage/netCapital.html',
                        api: []
                    }
                    ,{
                        name: '关联方清单维护',
                        href: '/htmlsrc/sysManage/dataManage/linkList.html',
                        api: []
                    },
                    {
                        name: '股东清单维护',
                        href: '/htmlsrc/sysManage/dataManage/shareholderList.html',
                        api: []
                    },
                    {
                        name: '集团客户维护',
                        href: '/htmlsrc/sysManage/dataManage/enterpriseCustomer.html',
                        api: []
                    },
                    {
                        name: "股东关联维护",
                        href: "/htmlsrc/sysManage/dataManage/holderLink.html"
                    }
                    // {
                    //     name: "股东关联维护",
                    //     href:""
                    // },
                    // {
                    //     name: "关联方日志查询",
                    //     href:""
                    // }

                    /*,{
                        name: '触发',
                        href: '/htmlsrc/sysManage/dataManage/touchRule.html',
                        api: []
                    }*/

                ]
            },
            // {
            //     name: '安全设置',
            //     children: [
            //         {
            //             name: '用户在线管理',
            //             href: '/htmlsrc/sysManage/safe/onlineUser.html'
            //         }
            //     ]  
            // },
            // {
            //     name: '消息设置',
            //     children: [
            //         {
            //             name: '消息模板管理',
            //             href: "/htmlsrc/sysManage/noticeManage/templet/templet.list.html"
            //         },
            //         {
            //             name: '消息规则管理',
            //             href: "/htmlsrc/sysManage/noticeManage/rules/rules.list.html"
            //         },
            //         {
            //             name: '消息应用管理',
            //             href: "/htmlsrc/sysManage/noticeManage/apply/apply.list.html"
            //         },
            //     ]
            // },
            {
                name: '组织架构',
                children: [
                    {
                        name: "用户列表",
                        href: '/htmlsrc/sysManage/userManage/user.list.html',
                        api: [
                            remoteApi.apiUser,
                            remoteApi.apiUpdateBaned
                        ]
                    },
                    // {
                    //     name:"部门列表"   ,
                    //     href: "/htmlsrc/sysManage/deptManage/dep.list.html"
                    // },
                    {
                        name: '部门管理',
                        href: '/htmlsrc/sysManage/deptManage/department.html',
                        api: [
                            remoteApi.apiAlldepartment,
                            remoteApi.apiDepartment
                        ]
                    },
                    {
                        name: '岗位管理',
                        href: '/htmlsrc/sysManage/quartersManage/quarters.html',
                        api: [
                            remoteApi.apiAlldepartment,
                            remoteApi.apiDeptDel
                        ]
                    },
                    // {
                    //     name: "部门人员",
                    //     href: '/htmlsrc/sysManage/userManage/user.dept.html'
                    // },
                    {
                        name: '角色管理',
                        href: '/htmlsrc/sysManage/role/role.list.html',
                        api: [
                            remoteApi.apiGetRoleList,
                            remoteApi.apiRoleDel,
                            remoteApi.apiGetUserListByRoleId,
                            remoteApi.apiRoleDelUsers,
                            remoteApi.apiRoleAddUsers
                        ]
                    }
                ]
            },
            {
                name: '消息模板管理',
                href: "/htmlsrc/sysManage/noticeManage/templet/templet.list.html",
                api: [ remoteApi.apiSysMsgTmplList ],
                childs: [
                    { name: "新增", api: [ remoteApi.apiSysMsgTmplAdd ] },
                    { name: "删除", api: [ remoteApi.apiSysMsgTmplDel ] },
                    { name: "编辑", api: [ remoteApi.apiSysMsgTmplEdit ] }
                ]
            },
            {
                name: '工作流管理',
                href: '/htmlsrc/sysManage/flowRelated/workFlow.list.html',
                api: [
                    remoteApi.apiWorkFlowModels,
                    remoteApi.apiWorkFlowModelDel
                ]
                //children:[
                //    {
                //        name: '工作流程列表'
                //    }
                //]
            },
            {
                name: "任务管理",
                href: '/htmlsrc/taskManage/taskManage.html',
            },
            {
                name: "字典管理"
                ,href: "/htmlsrc/dict_manage/dict.list.html"
            },
            // {
            //     name: '数据备份',
            //     children: [
            //         {
            //             name: "数据备份",
            //             href: ""
            //         },
            //         {
            //             name: "数据还原",
            //             href: ""
            //         },
            //         {
            //             name: "系统初始化",
            //             href: ""
            //         }
            //     ]
            // },
            // {
            //     name: '占位符管理',
            //     href: "/htmlsrc/sysManage/placeholder/placeholder.list.html"
            // },
            {
                name: '日志管理',
                children: [
                    {
                        name: "系统日志",
                        href: '/htmlsrc/sysManage/logManage/log.list.html',
                        api: [
                            remoteApi.apiSystemLog
                        ]
                    },
                    {
                        name: "企查查日志",
                        href: '/htmlsrc/sysManage/logManage/qccLog.html',
                        api: [ ]
                    },
                    {
                        name: "关联方查询日志",
                        href: "/htmlsrc/sysManage/logManage/relationSearchLog.html"
                    },
                    {
                        name: "企查查接口调用次数统计",
                        href: "/htmlsrc/sysManage/logManage/qccCount.html"
                    },
                    {
                        name: "短信发送历史",
                        href: '/htmlsrc/sysManage/logManage/shortMsg.html',
                        api: [
                            remoteApi.apiSysShortMessageList
                        ]
                    }
                ]
            }
        ]
    }
];

if(location.href.indexOf("localhost") > -1 || location.href.indexOf("47.94.97.138") > -1){
_menu.push(
    {
        name: "资产台账管理",
        children: [
            {
                name: "资产台账",
                href: "/htmlsrc/creditDataManage/ledger/grt.list.html"
            },
            {
                name: "强制执行",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.qiangzhizhixing),
                    },
                    {
                        name: "强制执行记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.qiangzhizhixing),
                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.qiangzhizhixing)
                    }
                ]
            },
            {
                name: "房屋出租",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.fangwuchuzu),
                    },
                    {
                        name: "房屋出租记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.fangwuchuzu),
                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.fangwuchuzu)
                    }
                ]
            },
            {
                name: "资产拍卖",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.zichanpaimai),
                    },
                    {
                        name: "资产拍卖记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.zichanpaimai),
                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.zichanpaimai)
                    }
                ]
            },
            {
                name: "资产协议出售",
                children: [
                    {
                        name: "任务发起",
                        href: "/htmlsrc/creditDataManage/ledger/loan.html?workFlow="+encodeURI(enumWorkFlowModel.zichanxieyichushou),
                    },
                    {
                        name: "资产协议出售记录",
                        href: "/htmlsrc/taskManage/taskList.html?workFlow="+encodeURI(enumWorkFlowModel.zichanxieyichushou),
                    },
                    {
                        name: "历史记录",
                        href:"/htmlsrc/creditDataManage/ledger/loan.html?history=1&workFlow="+encodeURI(enumWorkFlowModel.zichanxieyichushou)
                    }
                ]
            }

        ]
    }
);
    _menu.push(
        {
            name: '贷前任务',
            children: [
                {
                    name: '贷前任务',
                    href: '/htmlsrc/preLendingCollect/collect2.html',
                    api: [remoteApi.apigetMyInfoCollect]
                },
                {
                    name: '关联方查询',
                    href: '/htmlsrc/preLendingCollect/relationSearch.html',
                    api: []
                },
                {
                    name: '客户资质查询',
                    href: '/htmlsrc/preLendingCollect/qualCusList.html',
                    api: []
                }
            ]
        }
    );
    _menu.push({
        "name":"实验室功能"
        , "children":[
            {
                name:"系统更新"
                , href: "/htmlsrc/lab/update.html"
            },
            {
                name:"APP配置",
                href: "/htmlsrc/lab/appui.html"
            },
            {
                name:"网关服务",
                href: "/htmlsrc/lab/gate/gate.list.html"
            }

        ]
    });
    _menu.push({
        name: '文件管理alpha',
        icon: '<span class="glyphicon glyphicon-paperclip" aria-hidden="true"></span>&nbsp;',
        children:[
            {
                name: '个人文件柜',
                href: '/htmlsrc/documentex/private/cloud.fileList.html'
            },
            // {
            //     name: "个人文件柜回收站",
            //     href: "/htmlsrc/document/myFileTrashListSync.html"
            // },
            // {
            //     name: '资料库',
            //     href: '/htmlsrc/documentex/public/commCloud.fileList.html'
            // },
            // {
            //     name: "资料库回收站",
            //     href: "/htmlsrc/document/archiveFileRecycle.html"
            // },
            {
                name: "文件检索",
                href: "/htmlsrc/documentex/cloudSearch.html"
            },
            {
                name: '文件分享',
                children: [
                    {
                        name: '我的分享',
                        href: '/htmlsrc/documentex/share/myShare.html'
                    },
                    {
                        name: '共享给我的',
                        href: '/htmlsrc/documentex/share/receiveShare.html'
                    }
                ]
            }
            // {
            //     name: '个人文件柜',
            //     href: '/htmlsrc/document/private/private.fileList.html'
            // },
            // {
            //     name: '公共文件柜',
            //     href: '/htmlsrc/document/public/public.fileList.html'
            // }
        ]
    })
    // _menu.push({
    //     id: "qichacha",
    //     name: "企查查",
    //     children: [
    //         {
    //             name: "工商信息",
    //             children:[
    //                 {
    //                     name: "企业关键字精模糊查询",
    //                     href: "/htmlsrc/qichacha/ECI/search.html"
    //                 },
    //                 {
    //                     name: "企业关键字精确获取详细信息",
    //                     href: "/htmlsrc/qichacha/ECI/detailsByName.html"
    //                 },
    //                 {
    //                     name: "获取新增的公司信息",
    //                     href: "/htmlsrc/qichacha/ECI/SearchFresh.html"
    //                 },
    //                 {
    //                     name: "企业经营异常信息",
    //                     href: "/htmlsrc/qichacha/ECIException/GetOpException.html"
    //                 },
    //                 {
    //                     name: "企业人员董监高信息",
    //                     href: "/htmlsrc/qichacha/CIAEmployee/GetStockRelationInfo.html"
    //                 }
    //             ]
    //         },
    //         {
    //             name: "法律诉讼",
    //             children: [
    //                 {
    //                     name: "查询开庭公告",
    //                     href: "/htmlsrc/qichacha/CourtAnno/SearchCourtNotice.html"
    //                 },
    //                 {
    //                     name: "查询裁判文书",
    //                     href: "/htmlsrc/qichacha/JudgeDoc/SearchJudgmentDoc.html"
    //                 },
    //                 {
    //                     name:  "查询法院公告",
    //                     href: "/htmlsrc/qichacha/CourtNotice/SearchCourtAnnouncement.html"
    //                 },
    //                 {
    //                     name: "失信信息",
    //                     href: "/htmlsrc/qichacha/Court/SearchShiXin.html"
    //                 },
    //                 {
    //                     name: "被执行人信息",
    //                     href: "/htmlsrc/qichacha/Court/SearchZhiXing.html"
    //                 },
    //                 {
    //                     name: "司法协助查询",
    //                     href: "/htmlsrc/qichacha/JudicialAssistance/GetJudicialAssistance.html"
    //                 }
    //             ]
    //         },
    //         {
    //             name: "关联族谱",
    //             children: [
    //                 {
    //                     name: "企业族谱查询",
    //                     href: "/htmlsrc/qichacha/ECIRelation/ECIRelationIndex.html"
    //                 }
    //             ]
    //         },
    //         {
    //             name: "经营风险",
    //             children: [
    //                 {
    //                     name: "司法拍卖",
    //                     href: "/htmlsrc/qichacha/JudicialSale/GetJudicialSaleList.html"
    //                 },
    //                 {
    //                     name: "土地抵押",
    //                     href: "/htmlsrc/qichacha/LandMortgage/GetLandMortgageList.html"
    //                 },
    //                 {
    //                     name: "环保处罚",
    //                     href: "/htmlsrc/qichacha/EnvPunishment/GetEnvPunishmentList.html"
    //                 },
    //                 {
    //                     name: "动产抵押",
    //                     href: "/htmlsrc/qichacha/ChattelMortgage/GetChattelMortgage.html"
    //                 }
    //             ]
    //         },
    //         {
    //             name: "增值服务",
    //             children: [
    //                 {
    //                     name: "控股公司",
    //                     href: "/htmlsrc/qichacha/HoldingCompany/GetHoldingCompany.html"
    //                 }
    //             ]
    //         },
    //         {
    //             name: "历史信息",
    //             children: [
    //                 {
    //                     name: "历史工商信息",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytEci.html"
    //                 },
    //                 {
    //                     name: "历史对外投资",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytInvestment.html"
    //                 },
    //                 {
    //                     name: "历史失信信息",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistoryShiXin.html"
    //                 },
    //                 {
    //                     name: "历史被执行信息",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistoryZhiXing.html"
    //                 },
    //                 {
    //                     name: "历史法院公告",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytCourtNotice.html"
    //                 },
    //                 {
    //                     name: "历史裁判文书",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytJudgement.html"
    //                 },
    //                 {
    //                     name: "历史开庭公告",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytSessionNotice.html"
    //                 },
    //                 {
    //                     name: "历史动产抵押",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytMPledge.html"
    //                 },
    //                 {
    //                     name: "历史股权出质",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytPledge.html"
    //                 },
    //                 {
    //                     name: "历史行政处罚",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytAdminPenalty.html"
    //                 },
    //                 {
    //                     name: "历史行政许可",
    //                     href: "/htmlsrc/qichacha/History/HistoryGetHistorytAdminLicens.html"
    //                 }
    //
    //             ]
    //         },
    //         {
    //             name: "监控报告",
    //             children: [
    //                 {
    //                     name: "雷达监控",
    //                     href: "javascript:alert('未开通');"
    //                 }
    //             ]
    //         }
    //     ]
    // })
}


doIfDev(function () {
    _menu.push(
        {
            name:"流程管理",
            children: [
                {
                    name: "表单设计",
                    href: "/htmlsrc/bpm/form/cat.html"
                },
                // {
                //     name:"表单配置",
                //     href: "/htmlsrc/bpm/form/form.html"
                // },
                {
                    name: "流程设计",
                    href: "/htmlsrc/bpm/workFlow/workFlowManage.html"
                },
                {
                    name: "任务管理",
                    href: "/htmlsrc/bpm/ins/ins.list.html"
                }
                // {
                //     name: "发起流程",
                //     href: "/htmlsrc/bpm/workFlow/startWorkflow.html"
                // }
            ]
        }
    )
    // _menu.push({
    //     name:"任务管理",
    //     children: [
    //     ]
    // })
});

Array.prototype.notempty = function(){
    for(var i=0; i<this.length; i++){
        if(this[i] == "" || typeof(this[i]) == "undefined"){
            this.splice(i,1);
            i--;
        }
    }
    return this;
};


Object.defineProperty(window, "menu", {
    get: function () {
        return _menu;
    }
});

window.registerMethod = function (methods) {
    $.each(methods, function (i,v) {
        _menu.push(v)
    })
}

// getRemoteData({
//     url: remoteApi.apiUserMyMethods,
//     callback: function(origin){
//         top.topCacheMenuAuthority = origin;
//         // 设置显示功能菜单 -begin
//         if(!isSuperAdmin){
//             function showMenu(mmm, pname){
//                 pname = pname || '';
//                 mmm.forEach(function(element, index) {
//                     if(origin.indexOf(pname+element.name) < 0){
//                         delete mmm[index];
//                     }else{
//                         if(element.children && element.children.length>0){
//                             showMenu(element.children, pname+element.name+".");
//                         }
//                     }
//                 });
//             }
//             showMenu(menu);
//         }
//
//         var randomId = 0;
//         function clearEmptyData(data){
//             data.notempty();
//             if(data.length>0){
//                 data.forEach(function(item){
//                     if(item.children && item.children.length>0){
//                         clearEmptyData(item.children);
//                     }
//                     if(!item.id){ randomId++; item.id = "menu"+randomId; }
//                 })
//             }
//         }
//         clearEmptyData(menu);
//
//         // 设置显示功能菜单 - end
//         if(menu.length == 0){
//             return;
//         }
//         // 更新iframe链接
//         function changeIframe(event, treeId, treeNode, clickFlag) {
//            if(treeNode.href){
//             addTab(treeNode.id, treeNode.name, treeNode.href);
//            }
//
//             // $('#workspace').attr('src', treeNode.href);
//         }
//         // ztree侧边栏配置
//         var sidebarZtreeSetting = {
//             callback: {
//                 onClick: changeIframe
//             },
//             view: {
//                 showIcon: false,
//                 showLine: false
//             }
//         };
//
//         // 侧边栏缺省选择导航第一项渲染
//         //$.fn.zTree.init($("#x-wrap-menu"), sidebarZtreeSetting, menu[0].children);
//
//         //var zTree = $.fn.zTree.getZTreeObj("x-wrap-menu");
//         //zTree.expandAll(true);
//         // 顶部导航html
//         var wrapNavHtml = '';
//         for(var i=0; i<menu.length; i++){
//             var menuid = menu[i].id ? menu[i].id : '';
//             wrapNavHtml += '<li id="'+menuid+'"><a href="javascript:">'+menu[i].icon+menu[i].name+'</a><span class="tonav-choose"></span></li>';
//         }
//
//         // 插入顶部导航栏
//         $(".x-wrap-nav ul").html(wrapNavHtml);
//         setTimeout(function(){
//             $("#menu-mybench").click()
//         })
//         $(".x-wrap-nav li").click(function(){
//             var index = $(this).index();
//             // 更新active
//             $(this).siblings().removeClass('active');
//             $(this).addClass('active');
//             // 重新渲染侧边栏
//             $.fn.zTree.init($("#x-wrap-menu"), sidebarZtreeSetting, menu[index].children);
//             var zTree = $.fn.zTree.getZTreeObj("x-wrap-menu");
//             zTree.expandAll(true);
//         });
//     }
// })
