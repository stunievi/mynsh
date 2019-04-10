define({ "api": [
  {
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "optional": false,
            "field": "varname1",
            "description": "<p>No type.</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "varname2",
            "description": "<p>With type.</p>"
          }
        ]
      }
    },
    "type": "",
    "url": "",
    "version": "0.0.0",
    "filename": "src/main/resources/doc/main.js",
    "group": "D__work_hznsh_hz_ms_zed_src_main_resources_doc_main_js",
    "groupTitle": "D__work_hznsh_hz_ms_zed_src_main_resources_doc_main_js",
    "name": ""
  },
  {
    "type": "get",
    "url": "/ChattelMortgage/GetChattelMortgage",
    "title": "动产抵押信息",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "keyWord",
            "description": "<p>公司全名</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegisterNo",
            "description": "<p>登记编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegisterDate",
            "description": "<p>登记时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PublicDate",
            "description": "<p>公示时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegisterOffice",
            "description": "<p>登记机关</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "DebtSecuredAmount",
            "description": "<p>被担保债权数额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Status",
            "description": "<p>状态</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "Detail",
            "description": "<p>动产抵押详细信息</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "Detail.Pledge",
            "description": "<p>动产抵押Pledge</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.Pledge.RegistNo",
            "description": "<p>登记编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.Pledge.RegistDate",
            "description": "<p>注册时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.Pledge.RegistOffice",
            "description": "<p>注册单位</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Detail.PledgeeList",
            "description": "<p>动产抵押PledgeeList</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.PledgeeList.Name",
            "description": "<p>名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.PledgeeList.IdentityType",
            "description": "<p>抵押权人证照/证件类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.PledgeeList.IdentityNo",
            "description": "<p>证照/证件号码</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "Detail.SecuredClaim",
            "description": "<p>动产抵押SecuredClaim</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.SecuredClaim.Kind",
            "description": "<p>种类</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.SecuredClaim.Amount",
            "description": "<p>数额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.SecuredClaim.AssuranceScope",
            "description": "<p>担保的范围</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.SecuredClaim.FulfillObligation",
            "description": "<p>债务人履行债务的期限</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.SecuredClaim.Remark",
            "description": "<p>备注</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Detail.GuaranteeList",
            "description": "<p>动产抵押GuaranteeList</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.GuaranteeList.Name",
            "description": "<p>名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.GuaranteeList.Ownership",
            "description": "<p>所有权归属</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.GuaranteeList.Other",
            "description": "<p>数量、质量、状况、所在地等情况</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.GuaranteeList.Remark",
            "description": "<p>备注</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "Detail.CancelInfo",
            "description": "<p>动产抵押CancelInfo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.CancelInfo.CancelDate",
            "description": "<p>动产抵押登记注销时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.CancelInfo.CancelReason",
            "description": "<p>动产抵押登记注销原因</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Detail.ChangeList",
            "description": "<p>动产抵押ChangeList</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.ChangeList.ChangeDate",
            "description": "<p>动产抵押登记变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Detail.ChangeList.ChangeContent",
            "description": "<p>动产抵押登记变更内容</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": [\n        {\n            \"RegisterOffice\": \"伊宁县市场监督管理局\",\n            \"RegisterDate\": \"2014-12-17 12:00:00\",\n            \"DebtSecuredAmount\": \"36236\",\n            \"RegisterNo\": \"新抵F212014030\",\n            \"Detail\": {\n                \"GuaranteeList\": [\n                    {\n                        \"Ownership\": \"\",\n                        \"Other\": \"抵押数量为：5抵押品牌为：抵押单价为：\",\n                        \"Name\": \"设备\",\n                        \"Remark\": \"\"\n                    }\n                ],\n                \"ChangeList\": [\n                ],\n                \"CancelInfo\": {\n                },\n                \"Pledge\": {\n                    \"RegistNo\": \"新抵F212014030\",\n                    \"RegistOffice\": \"伊宁县市场监督管理局\",\n                    \"RegistDate\": \"2014-12-17 12:00:00\"\n                },\n                \"SecuredClaim\": {\n                    \"AssuranceScope\": \"1,2,3,4,\",\n                    \"Amount\": 36236,\n                    \"Kind\": \"买卖合同\",\n                    \"FulfillObligation\": \"2015-06-15至2027-06-14\",\n                    \"Remark\": \"\"\n                },\n                \"PledgeeList\": [\n                    {\n                        \"IdentityType\": \"\",\n                        \"IdentityNo\": \"\",\n                        \"Name\": \"国家开发银行股份有限公司伊犁哈萨克自治州分行\"\n                    }\n                ]\n            }\n        },\n        {\n            \"RegisterOffice\": \"伊宁县市场监督管理局\",\n            \"RegisterDate\": \"2018-05-02 12:00:00\",\n            \"DebtSecuredAmount\": \"5000\",\n            \"RegisterNo\": \"新抵F212018258\",\n            \"Detail\": {\n                \"GuaranteeList\": [\n                    {\n                        \"Ownership\": \"\",\n                        \"Other\": \"抵押数量为：1抵押品牌为：抵押单价为：\",\n                        \"Name\": \"设备\",\n                        \"Remark\": \"\"\n                    }\n                ],\n                \"ChangeList\": [\n                ],\n                \"CancelInfo\": {\n                },\n                \"Pledge\": {\n                    \"RegistNo\": \"新抵F212018258\",\n                    \"RegistOffice\": \"伊宁县市场监督管理局\",\n                    \"RegistDate\": \"2018-05-02 12:00:00\"\n                },\n                \"SecuredClaim\": {\n                    \"AssuranceScope\": \"1,2,3,4,\",\n                    \"Amount\": 5000,\n                    \"Kind\": \"买卖合同\",\n                    \"FulfillObligation\": \"2015-12-17至2018-06-17\",\n                    \"Remark\": \"\"\n                },\n                \"PledgeeList\": [\n                    {\n                        \"IdentityType\": \"企业法人营业执照(公司)\",\n                        \"IdentityNo\": \"\",\n                        \"Name\": \"伊犁哈萨克自治州财通国有资产经营有限责任公司\"\n                    }\n                ]\n            }\n        },\n        {\n            \"RegisterOffice\": \"伊宁县市场监督管理局\",\n            \"RegisterDate\": \"2018-05-02 12:00:00\",\n            \"DebtSecuredAmount\": \"18500\",\n            \"RegisterNo\": \"新抵F212018258\",\n            \"Detail\": {\n                \"GuaranteeList\": [\n                    {\n                        \"Ownership\": \"\",\n                        \"Other\": \"抵押数量为：1抵押品牌为：抵押单价为：\",\n                        \"Name\": \"设备\",\n                        \"Remark\": \"\"\n                    }\n                ],\n                \"ChangeList\": [\n                ],\n                \"CancelInfo\": {\n                },\n                \"Pledge\": {\n                    \"RegistNo\": \"新抵F212018258\",\n                    \"RegistOffice\": \"伊宁县市场监督管理局\",\n                    \"RegistDate\": \"2018-05-02 12:00:00\"\n                },\n                \"SecuredClaim\": {\n                    \"AssuranceScope\": \"1,2,3,4,\",\n                    \"Amount\": 18500,\n                    \"Kind\": \"买卖合同\",\n                    \"FulfillObligation\": \"2016-06-17至2018-06-15\",\n                    \"Remark\": \"\"\n                },\n                \"PledgeeList\": [\n                    {\n                        \"IdentityType\": \"企业法人营业执照(公司)\",\n                        \"IdentityNo\": \"\",\n                        \"Name\": \"伊犁哈萨克自治州财通国有资产经营有限责任公司\"\n                    }\n                ]\n            }\n        },\n        {\n            \"RegisterOffice\": \"伊宁县市场监督管理局\",\n            \"RegisterDate\": \"2016-08-05 12:00:00\",\n            \"DebtSecuredAmount\": \"8000\",\n            \"RegisterNo\": \"新抵F2120160021\",\n            \"Detail\": {\n                \"GuaranteeList\": [\n                    {\n                        \"Ownership\": \"\",\n                        \"Other\": \"抵押数量为：265抵押品牌为：抵押单价为：\",\n                        \"Name\": \"设备\",\n                        \"Remark\": \"\"\n                    }\n                ],\n                \"ChangeList\": [\n                ],\n                \"CancelInfo\": {\n                },\n                \"Pledge\": {\n                    \"RegistNo\": \"新抵F2120160021\",\n                    \"RegistOffice\": \"伊宁县市场监督管理局\",\n                    \"RegistDate\": \"2016-08-05 12:00:00\"\n                },\n                \"SecuredClaim\": {\n                    \"AssuranceScope\": \"1,2,3,4,\",\n                    \"Amount\": 8000,\n                    \"Kind\": \"买卖合同\",\n                    \"FulfillObligation\": \"2016-03-01至2020-03-01\",\n                    \"Remark\": \"\"\n                },\n                \"PledgeeList\": [\n                    {\n                        \"IdentityType\": \"企业法人营业执照(非公司)\",\n                        \"IdentityNo\": \"\",\n                        \"Name\": \"广发银行股份有限公司乌鲁木齐分行\"\n                    }\n                ]\n            }\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetChattelmortgageGetchattelmortgage",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/CourtAnnoV4/GetCourtNoticeInfo",
    "title": "开庭公告详情",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "id",
            "description": "<p>id</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Province",
            "description": "<p>省份</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseReason",
            "description": "<p>案由</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ScheduleTime",
            "description": "<p>排期日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteGov",
            "description": "<p>法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UndertakeDepartment",
            "description": "<p>承办部门</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteUnite",
            "description": "<p>法庭</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ChiefJudge",
            "description": "<p>审判长/主审人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OpenTime",
            "description": "<p>开庭日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseNo",
            "description": "<p>案号</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Prosecutor",
            "description": "<p>案号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Prosecutor.Name",
            "description": "<p>上诉人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Prosecutor.KeyNo",
            "description": "<p>KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Defendant",
            "description": "<p>案号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Defendant.Name",
            "description": "<p>被上诉人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Defendant.KeyNo",
            "description": "<p>KeyNo</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"CaseNo\": \"（2017）沪0112民初23287号\",\n        \"ExecuteUnite\": \"新虹桥第六法庭\",\n        \"OpenTime\": \"2017-10-09 01:45:00\",\n        \"ScheduleTime\": \"2017-10-09 12:00:00\",\n        \"Defendant\": [\n            {\n                \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e\",\n                \"Name\": \"北京小桔科技有限公司\"\n            }\n        ],\n        \"CaseReason\": \"运输合同纠纷\",\n        \"Prosecutor\": [\n            {\n                \"KeyNo\": \"\",\n                \"Name\": \"林允昌\"\n            }\n        ],\n        \"UndertakeDepartment\": \"新虹桥法庭\",\n        \"ChiefJudge\": \"陈雪琼\",\n        \"Province\": \"上海\",\n        \"ExecuteGov\": \"闵行\"\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetCourtannov4Getcourtnoticeinfo",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/CourtAnnoV4/SearchCourtNotice",
    "title": "开庭公告列表",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "searchKey",
            "description": "<p>公司全名</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageIndex",
            "description": "<p>页码，默认1</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageSize",
            "description": "<p>每页数量，默认10</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "DefendantList",
            "description": "<p>被告/被上诉人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteGov",
            "description": "<p>法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ProsecutorList",
            "description": "<p>原告/上诉人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "LiAnDate",
            "description": "<p>开庭日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseReason",
            "description": "<p>案由</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>内部ID</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseNo",
            "description": "<p>案号</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 10,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"CaseNo\": \"（2017）沪0112民初23287号\",\n            \"ProsecutorList\": \"林允昌\",\n            \"LiAnDate\": \"2017-10-09 01:45:00\",\n            \"CaseReason\": \"运输合同纠纷\",\n            \"Id\": \"88f9247d61008eb59cc932358554e78f5\",\n            \"DefendantList\": \"北京小桔科技有限公司\",\n            \"ExecuteGov\": \"闵行\"\n        },\n        {\n            \"CaseNo\": \"（2017）沪0115民初15113号\",\n            \"LiAnDate\": \"2017-07-25 01:40:00\",\n            \"CaseReason\": \"生命权、健康权、身体权纠纷\",\n            \"Id\": \"b54ec153ee9dd8a073eda1b0b42c044b5\",\n            \"ExecuteGov\": \"浦东\"\n        },\n        {\n            \"CaseNo\": \"（2017）沪0112民初18563号\",\n            \"LiAnDate\": \"2017-07-24 08:45:00\",\n            \"CaseReason\": \"机动车交通事故责任纠纷\",\n            \"Id\": \"86f1f6b1e100c82775d7188739c37d865\",\n            \"ExecuteGov\": \"闵行\"\n        },\n        {\n            \"CaseNo\": \"（2016）浙0702民初14535号\",\n            \"LiAnDate\": \"2017-06-22 09:30:00\",\n            \"CaseReason\": \"生命权、健康权、身体权纠纷\",\n            \"Id\": \"6cb34523c61a2085eb3cfa5c830164c15\",\n            \"ExecuteGov\": \"金华市婺城区人民法院\"\n        },\n        {\n            \"CaseNo\": \"（2017）粤03民终7473号\",\n            \"LiAnDate\": \"2017-05-26 04:10:00\",\n            \"CaseReason\": \"机动车交通事故责任纠纷\",\n            \"Id\": \"a00a158c02b62a4c134302821b8984575\",\n            \"ExecuteGov\": \"深圳市中级人民法院\"\n        },\n        {\n            \"CaseNo\": \"（2017）粤03民终7468号\",\n            \"LiAnDate\": \"2017-05-24 11:00:00\",\n            \"CaseReason\": \"机动车交通事故责任纠纷\",\n            \"Id\": \"753546f91fcefdf7bdb0bc504bff03fd5\",\n            \"ExecuteGov\": \"深圳市中级人民法院\"\n        },\n        {\n            \"CaseNo\": \"（2017）沪0104民初5567号\",\n            \"LiAnDate\": \"2017-05-18 02:00:00\",\n            \"CaseReason\": \"财产损害赔偿纠纷\",\n            \"Id\": \"f82d642cc3e9d08000b9d46801ccb0295\",\n            \"ExecuteGov\": \"徐汇\"\n        },\n        {\n            \"CaseNo\": \"(2017)渝0101民初4156号\",\n            \"LiAnDate\": \"2017-05-11 02:30:00\",\n            \"CaseReason\": \"运输合同纠纷\",\n            \"Id\": \"92203bb36fede57755d60f0db01a61125\",\n            \"ExecuteGov\": \"重庆市万州区人民法院\"\n        },\n        {\n            \"CaseNo\": \"（2017）浙07民终1315号\",\n            \"LiAnDate\": \"2017-04-26 08:40:00\",\n            \"CaseReason\": \"机动车交通事故责任纠纷\",\n            \"Id\": \"4c283bea02021eb9d43e3d7e01e011a75\",\n            \"ExecuteGov\": \"金华市中级人民法院\"\n        },\n        {\n            \"CaseNo\": \"（2017）沪0109民初1841号\",\n            \"LiAnDate\": \"2017-04-21 09:00:00\",\n            \"CaseReason\": \"运输合同纠纷\",\n            \"Id\": \"b0da79d5c7182f98e8462a78a017552f5\",\n            \"ExecuteGov\": \"虹口 \"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetCourtannov4Searchcourtnotice",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/CourtNoticeV4/SearchCourtAnnouncement",
    "title": "法院公告列表",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "companyName",
            "description": "<p>公司全名</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageIndex",
            "description": "<p>页码，默认1</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageSize",
            "description": "<p>每页数量，默认10</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UploadDate",
            "description": "<p>下载时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Court",
            "description": "<p>执行法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Content",
            "description": "<p>内容</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Category",
            "description": "<p>种类</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PublishedDate",
            "description": "<p>公布日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PublishedPage",
            "description": "<p>公布、页</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Party",
            "description": "<p>公司名、当事人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>主键</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 20,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"PublishedPage\": \"G41\",\n            \"UploadDate\": \"2019-03-29 12:00:00\",\n            \"Category\": \"裁判文书\",\n            \"Party\": \"吉林省安华通讯科技发展有限公司\",\n            \"Content\": \"吉林省安华通讯科技发展有限公司：本院受理小米科技有限责任公司诉你侵害商标权纠纷一案，已审理终结，现依法向你公告送达（2017）吉01民初770号民事判决书。自公告之日起60日内来本院领取民事判决书，逾期即视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于吉林省高级人民法院。逾期本判决即发生法律效力。\",\n            \"Id\": \"52C02354F9069DD6CF28F1EB3C398EFE\",\n            \"Court\": \"吉林省长春市中级人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G47\",\n            \"UploadDate\": \"2019-03-28 12:00:00\",\n            \"Category\": \"起诉状副本及开庭传票\",\n            \"Party\": \"彭育洲\",\n            \"Content\": \"彭育洲:本院受理原告小米科技有限责任公司与被告彭育洲、浙江淘宝网络有限公司侵害商标权纠纷一案，现依法向你送达起诉状副本、应诉通知书、举证通知书、合议庭组成人员通知书及开庭传票等。自本公告发出之日起，经过60日视为送达，提出答辩状和举证期限均为公告送达期满后的15日内。并定于2019年6月24日上午9时于本院二十...\",\n            \"Id\": \"B3401AFD0F71D64E8589B4039EF8F9EB\",\n            \"Court\": \"杭州市余杭区人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G15\",\n            \"UploadDate\": \"2019-03-22 12:00:00\",\n            \"Category\": \"裁判文书\",\n            \"Party\": \"陈銮卿\",\n            \"Content\": \"陈銮卿（公民身份号码440524196608184264）：本院受理原告小米科技有限责任公司诉你侵害商标权纠纷一案，现依法向你公告送达(2018)粤05民初20号民事判决。本院判决你应立即停止销售侵犯8911270号商标的商品，销毁库存侵权商品，在判决生效之日起10日内支付赔偿金2.5万元。自公告发出之日起经过60日即视为送达。...\",\n            \"Id\": \"058D2A58AC141970DFD05935F43DD6E9\",\n            \"Court\": \"广东省汕头市中级人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G91\",\n            \"UploadDate\": \"2019-03-21 12:00:00\",\n            \"Category\": \"起诉状副本及开庭传票\",\n            \"Party\": \"赖木辉\",\n            \"Content\": \"赖木辉：本院受理的原告小米科技有限责任公司与被告赖木辉、浙江淘宝网络有限公司侵害商标权纠纷一案，现依法向你公告送达起诉状副本、应诉通知书、举证通知书、开庭传票等。自本公告发出之日起，经过60日即视为送达，提出答辩状和举证期限均为公告送达期满后的15日内。并定于2019年6月26日下午14时30分在本院第二十三审判...\",\n            \"Id\": \"008F9CFA6E0C42DDE50D759B6275F77C\",\n            \"Court\": \"杭州市余杭区人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G68\",\n            \"UploadDate\": \"2019-03-20 12:00:00\",\n            \"Category\": \"裁判文书\",\n            \"Party\": \"牟平区栋财手机店\",\n            \"Content\": \"牟平区栋财手机店：本院受理原告小米科技有限责任公司诉被告牟平区栋财手机店侵害商标权纠纷一案，现依法向你方公告送达（2017）鲁06民初134号民事判决书，自公告之日起满60日，即视为送达。如不服本判决，可自送达之日起15日内向本院递交上诉状，上诉于山东省高级人民法院。逾期本判决即发生法律效力。...\",\n            \"Id\": \"AACB2C6C9ADBE303D98BA65AFB0081BF\",\n            \"Court\": \"山东省烟台市中级人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G14G15中缝\",\n            \"UploadDate\": \"2019-03-19 12:00:00\",\n            \"Category\": \"起诉状副本及开庭传票\",\n            \"Party\": \"北京华美通联通讯器材销售中心\",\n            \"Content\": \"北京市东城区人民法院公告北京华美通联通讯器材销售中心：本院受理原告小米科技有限责任公司诉北京华美通联通讯器材销售中心侵害商标权纠纷一案，现依法向你公司公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告送达期满后次日起15日和30日内。...\",\n            \"Id\": \"E690D42D47091DADDB6E4BD541513BB1\",\n            \"Court\": \"北京市东城区人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G26\",\n            \"UploadDate\": \"2019-03-14 12:00:00\",\n            \"Category\": \"起诉状副本及开庭传票\",\n            \"Party\": \"龚坤宏\",\n            \"Content\": \"龚坤宏：本院受理小米科技有限责任公司诉龚坤宏侵害商标权纠纷一案，现依法向你龚坤宏公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。龚坤宏自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午16时00(遇法定假日顺延)在本院上海市徐汇区龙漕路...\",\n            \"Id\": \"3667F54FDF5BB15AA8192C3FC6DDD0CE\",\n            \"Court\": \"上海市徐汇区人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G26\",\n            \"UploadDate\": \"2019-03-14 12:00:00\",\n            \"Category\": \"起诉状副本及开庭传票\",\n            \"Party\": \"项春燕\",\n            \"Content\": \"项春燕：本院受理小米科技有限责任公司诉项春燕侵害商标权纠纷一案，现依法向你项春燕公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。项春燕自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午14时40分(遇法定假日顺延)在本院上海市徐汇区龙漕路...\",\n            \"Id\": \"3667F54FDF5BB15A26D2ADF663C0C649\",\n            \"Court\": \"上海市徐汇区人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G18G19中缝\",\n            \"UploadDate\": \"2019-03-14 12:00:00\",\n            \"Category\": \"起诉状副本及开庭传票\",\n            \"Party\": \"张少鸿\",\n            \"Content\": \"张少鸿:本院受理原告小米科技有限责任公司诉你方侵害商标权纠纷一案，原告诉请本院判令:1.被告立即停止(2016)冀石国证字第9081号公证书公证的侵犯原告第8911270号商标专用权的行为，即停止销售和许诺销售侵权产品;2.被告赔偿原告经济损失及制止侵权所支付的费用合计五万元;3.被告承担本案诉讼费、公告费、保全费等全部费用...\",\n            \"Id\": \"6620FD7E3529C46E51DFABE3544CD9B9\",\n            \"Court\": \"深圳市龙岗区人民法院\"\n        },\n        {\n            \"PublishedPage\": \"G26\",\n            \"UploadDate\": \"2019-03-14 12:00:00\",\n            \"Category\": \"起诉状副本及开庭传票\",\n            \"Party\": \"周远沐\",\n            \"Content\": \"周远沐：本院受理小米科技有限责任公司诉周远沐侵害商标权纠纷一案，现依法向你周远沐公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。周远沐自公告之日起，经过60日即视为送达。提出答辩状和举证的期限分别为公告期满后15日和30日内。并定于举证期满后7月1日下午15时00分(遇法定假日顺延)在本院上海市徐汇区龙漕路...\",\n            \"Id\": \"CF993D8969C131444897BA2A16FB3A24\",\n            \"Court\": \"上海市徐汇区人民法院\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetCourtnoticev4Searchcourtannouncement",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/CourtNoticeV4/SearchCourtAnnouncementDetail",
    "title": "法院公告详情",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "id",
            "description": "<p>id</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Court",
            "description": "<p>公告法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Content",
            "description": "<p>内容</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "SubmitDate",
            "description": "<p>上传日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Province",
            "description": "<p>所在省份代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Category",
            "description": "<p>类别</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PublishedDate",
            "description": "<p>刊登日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Party",
            "description": "<p>当事人</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "NameKeyNoCollection",
            "description": "<p>当事人信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "NameKeyNoCollection.KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "NameKeyNoCollection.Name",
            "description": "<p>名称</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"Category\": \"裁判文书\",\n        \"Party\": \"吉林省安华通讯科技发展有限公司\",\n        \"Content\": \"吉林省安华通讯科技发展有限公司：本院受理小米科技有限责任公司诉你侵害商标权纠纷一案，已审理终结，现依法向你公告送达（2017）吉01民初770号民事判决书。自公告之日起60日内来本院领取民事判决书，逾期即视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于吉林省高级人民法院。逾期本判决即发生法律效力。\",\n        \"SubmitDate\": \"2019-03-29 12:00:00\",\n        \"PublishedDate\": \"2019-04-04 12:00:00\",\n        \"NameKeyNoCollection\": [\n            {\n                \"KeyNo\": \"\",\n                \"Name\": \"吉林省安华通讯科技发展有限公司\"\n            }\n        ],\n        \"Province\": \"JL\",\n        \"Court\": \"吉林省长春市中级人民法院\"\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetCourtnoticev4Searchcourtannouncementdetail",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/CourtV4/SearchShiXin",
    "title": "失信信息",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "searchKey",
            "description": "<p>公司全名</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageIndex",
            "description": "<p>页码，默认1</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageSize",
            "description": "<p>每页数量，默认10</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>主键</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "SourceId",
            "description": "<p>官网主键（内部保留字段，一般为空）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UniqueNo",
            "description": "<p>唯一编号（内部保留字段，一般为空）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Name",
            "description": "<p>被执行人姓名/名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "LiAnDate",
            "description": "<p>立案时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AnNo",
            "description": "<p>案号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgNo",
            "description": "<p>身份证号码/组织机构代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OwnerName",
            "description": "<p>法定代表人或者负责人姓名</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteGov",
            "description": "<p>执行法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Province",
            "description": "<p>所在省份缩写</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteUnite",
            "description": "<p>做出执行依据单位</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "YiWu",
            "description": "<p>生效法律文书确定的义务</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteStatus",
            "description": "<p>被执行人的履行情况</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ActionRemark",
            "description": "<p>失信被执行人行为具体情形</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PublicDate",
            "description": "<p>发布时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Age",
            "description": "<p>年龄</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Sexy",
            "description": "<p>性别</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UpdateDate",
            "description": "<p>数据更新时间（内部保留字段，现已不再更新时间）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteNo",
            "description": "<p>执行依据文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PerformedPart",
            "description": "<p>已履行</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UnperformPart",
            "description": "<p>未履行</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgType",
            "description": "<p>组织类型（1：自然人，2：企业，3：社会组织，空白：无法判定）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgTypeName",
            "description": "<p>组织类型名称</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 10,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"OwnerName\": \"秦亚\",\n            \"YiWu\": \"236000\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854-1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执798号\",\n            \"ExecuteUnite\": \"鹿邑县人民法院\",\n            \"ActionRemark\": \"被执行人无正当理由拒不履行执行和解协议\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2017-07-02 12:00:00\",\n            \"LiAnDate\": \"2017-06-16 12:00:00\",\n            \"Id\": \"478230a106a17e95c7c0785314802c372\",\n            \"ExecuteNo\": \"（2016）豫1628民初1162号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"秦亚\",\n            \"YiWu\": \"11034234.66\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854-1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执700号\",\n            \"ExecuteUnite\": \"鹿邑县人民法院\",\n            \"ActionRemark\": \"有履行能力而拒不履行生效法律文书确定义务\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2017-07-02 12:00:00\",\n            \"LiAnDate\": \"2017-06-08 12:00:00\",\n            \"Id\": \"e23d3af6bb54cad3a63392f19d953be12\",\n            \"ExecuteNo\": \"（2017）豫1628民初1125号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"秦亚\",\n            \"YiWu\": \"详见判决书\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"796788541\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"周口市川汇区人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1602执1496号\",\n            \"ExecuteUnite\": \"河南省周口市川汇区人民法院\",\n            \"ActionRemark\": \"有履行能力而拒不履行生效法律文书确定义务的\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2017-05-22 12:00:00\",\n            \"LiAnDate\": \"2017-05-03 12:00:00\",\n            \"Id\": \"0f171a88f75ad73aada5542e8400c5e22\",\n            \"ExecuteNo\": \"（2017）豫1602民初70号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"秦亚\",\n            \"YiWu\": \"1000000\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854-1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执517号\",\n            \"ExecuteUnite\": \"鹿邑县人民法院\",\n            \"ActionRemark\": \"有履行能力而拒不履行生效法律文书确定义务\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2017-07-02 12:00:00\",\n            \"LiAnDate\": \"2017-04-17 12:00:00\",\n            \"Id\": \"b317ac796a0999dddf0930c1f9114fb62\",\n            \"ExecuteNo\": \"（2016）豫1628民初2572号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"秦亚\",\n            \"YiWu\": \"详见判决书\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854-1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"周口市川汇区人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1602执802号\",\n            \"ExecuteUnite\": \"周口市中级人民法院\",\n            \"ActionRemark\": \"其他有履行能力而拒不履行生效法律文书确定义务的\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2017-03-17 12:00:00\",\n            \"LiAnDate\": \"2017-03-03 12:00:00\",\n            \"Id\": \"277d2b15e90f798970e60603dd903ce12\",\n            \"ExecuteNo\": \"（2015）周民初字第82号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"秦亚\",\n            \"YiWu\": \"详见判决书\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854-1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"周口市川汇区人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1602执803号\",\n            \"ExecuteUnite\": \"周口市中级人民法院\",\n            \"ActionRemark\": \"其他有履行能力而拒不履行生效法律文书确定义务的\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2017-04-18 12:00:00\",\n            \"LiAnDate\": \"2017-03-03 12:00:00\",\n            \"Id\": \"dce46e0648b52fbbc5ad9f62eba84ff62\",\n            \"ExecuteNo\": \"（2015）周民初字第82号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"秦亚\",\n            \"YiWu\": \"详见民事判决书\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854-1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执恢33号\",\n            \"ExecuteUnite\": \"鹿邑县人民法院\",\n            \"ActionRemark\": \"其他有履行能力而拒不履行生效法律文书确定义务的\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2017-02-22 12:00:00\",\n            \"LiAnDate\": \"2017-02-22 12:00:00\",\n            \"Id\": \"8e8ff1fc6966bde81d4745e7be83957a2\",\n            \"ExecuteNo\": \"（2016）豫1628民初1161号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"无\",\n            \"YiWu\": \"\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854－1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2016)豫1628执682号\",\n            \"ExecuteUnite\": \"鹿邑县人民法院\",\n            \"ActionRemark\": \"其它规避执行\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2016-09-29 12:00:00\",\n            \"LiAnDate\": \"2016-08-16 12:00:00\",\n            \"Id\": \"b1b8effa78373af62ce348193f2dede22\",\n            \"ExecuteNo\": \"（2016）豫1628民初1386号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"无\",\n            \"YiWu\": \"\",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854-1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2016)豫1628执640号\",\n            \"ExecuteUnite\": \"鹿邑县人民法院\",\n            \"ActionRemark\": \"其他有履行能力而拒不履行生效法律文书确定义务\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2016-08-09 12:00:00\",\n            \"LiAnDate\": \"2016-08-01 12:00:00\",\n            \"Id\": \"31a81bb2643f7f4e65a9ea41f2a60bf42\",\n            \"ExecuteNo\": \"（2016）豫1628民初1161号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        },\n        {\n            \"OwnerName\": \"无\",\n            \"YiWu\": \"23.6万 \",\n            \"SourceId\": \"0\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"UnperformPart\": \"\",\n            \"OrgNo\": \"79678854-1\",\n            \"Province\": \"河南\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"PerformedPart\": \"\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2016)豫1628执624号\",\n            \"ExecuteUnite\": \"鹿邑县人民法院\",\n            \"ActionRemark\": \"其他有履行能力而拒不履行生效法律文书确定义务\",\n            \"Sexy\": \"\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2016-07-14 12:00:00\",\n            \"LiAnDate\": \"2016-07-12 12:00:00\",\n            \"Id\": \"3c95eccd2708d62ec56174d3e2f4040d2\",\n            \"ExecuteNo\": \"（2016）豫1628民初1162号\",\n            \"OrgTypeName\": \"失信企业\",\n            \"Age\": \"0\",\n            \"UniqueNo\": \"\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetCourtv4Searchshixin",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/CourtV4/SearchZhiXing",
    "title": "被执行信息",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "searchKey",
            "description": "<p>公司全名</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageIndex",
            "description": "<p>页码，默认1</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageSize",
            "description": "<p>每页数量，默认10</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>Id</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "SourceId",
            "description": "<p>官网系统ID（内部保留字段，一般为空）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Name",
            "description": "<p>名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "LiAnDate",
            "description": "<p>立案时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AnNo",
            "description": "<p>立案号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteGov",
            "description": "<p>执行法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "BiaoDi",
            "description": "<p>标地</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Status",
            "description": "<p>状态（内部保留字段，现已不使用）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PartyCardNum",
            "description": "<p>身份证号码/组织机构代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UpdateDate",
            "description": "<p>数据更新时间（内部保留字段，现已不再更新时间）</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 10,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫0105执8021号\",\n            \"LiAnDate\": \"2017-07-05 12:00:00\",\n            \"Id\": \"35792ff96142cda1b079ae9f7f7b61fa1\",\n            \"PartyCardNum\": \"796788541\",\n            \"ExecuteGov\": \"郑州市金水区人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"2794488\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执798号\",\n            \"LiAnDate\": \"2017-06-16 12:00:00\",\n            \"Id\": \"478230a106a17e95c7c0785314802c371\",\n            \"PartyCardNum\": \"79678854-1\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"236000\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执700号\",\n            \"LiAnDate\": \"2017-06-08 12:00:00\",\n            \"Id\": \"e23d3af6bb54cad3a63392f19d953be11\",\n            \"PartyCardNum\": \"79678854-1\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"11034235\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1627执812号\",\n            \"LiAnDate\": \"2017-05-16 12:00:00\",\n            \"Id\": \"2d97ded6b7cccc4f0c2c5b03ee46bc381\",\n            \"PartyCardNum\": \"79678854-1\",\n            \"ExecuteGov\": \"太康县人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"1022300\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1602执1496号\",\n            \"LiAnDate\": \"2017-05-03 12:00:00\",\n            \"Id\": \"0f171a88f75ad73aada5542e8400c5e21\",\n            \"PartyCardNum\": \"796788541\",\n            \"ExecuteGov\": \"周口市川汇区人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"1.799E+7\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执517号\",\n            \"LiAnDate\": \"2017-04-17 12:00:00\",\n            \"Id\": \"b317ac796a0999dddf0930c1f9114fb61\",\n            \"PartyCardNum\": \"79678854-1\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"1000000\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执502号\",\n            \"LiAnDate\": \"2017-04-13 12:00:00\",\n            \"Id\": \"ee8179791e9d6d271da732cff759e1df1\",\n            \"PartyCardNum\": \"41160100006184\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"4286040\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1602执802号\",\n            \"LiAnDate\": \"2017-03-03 12:00:00\",\n            \"Id\": \"277d2b15e90f798970e60603dd903ce11\",\n            \"PartyCardNum\": \"79678854-1\",\n            \"ExecuteGov\": \"周口市川汇区人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"5000000\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1602执803号\",\n            \"LiAnDate\": \"2017-03-03 12:00:00\",\n            \"Id\": \"dce46e0648b52fbbc5ad9f62eba84ff61\",\n            \"PartyCardNum\": \"79678854-1\",\n            \"ExecuteGov\": \"周口市川汇区人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"6000000\"\n        },\n        {\n            \"Status\": \"\",\n            \"UpdateDate\": \"0001-01-01T00:00:00\",\n            \"AnNo\": \"(2017)豫1628执恢33号\",\n            \"LiAnDate\": \"2017-02-22 12:00:00\",\n            \"Id\": \"8e8ff1fc6966bde81d4745e7be83957a1\",\n            \"PartyCardNum\": \"79678854-1\",\n            \"ExecuteGov\": \"鹿邑县人民法院\",\n            \"Name\": \"河南亚华安全玻璃有限公司\",\n            \"BiaoDi\": \"0\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetCourtv4Searchzhixing",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/ECIException/GetOpException",
    "title": "企业经营异常",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "keyNo",
            "description": "<p>公司全名</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AddReason",
            "description": "<p>列入经营异常名录原因</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AddDate",
            "description": "<p>列入日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RomoveReason",
            "description": "<p>移出经营异常名录原因</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RemoveDate",
            "description": "<p>移出日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "DecisionOffice",
            "description": "<p>作出决定机关</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RemoveDecisionOffice",
            "description": "<p>移除决定机关</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": [\n        {\n            \"AddDate\": \"2015-07-08 12:00:00\",\n            \"RemoveDate\": \"2016-01-29 12:00:00\",\n            \"AddReason\": \"未依照《企业信息公示暂行条例》第八条规定的期限公示年度报告的\",\n            \"RomoveReason\": \"列入经营异常名录3年内且依照《经营异常名录管理办法》第六条规定被列入经营异常名录的企业，可以在补报未报年份的年度报告并公示后，申请移出\",\n            \"DecisionOffice\": \"北京市工商行政管理局海淀分局\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEciexceptionGetopexception",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/EnvPunishment/GetEnvPunishmentDetails",
    "title": "环保处罚详情",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "id",
            "description": "<p>id</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseNo",
            "description": "<p>决定书文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "IllegalType",
            "description": "<p>违法类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PunishReason",
            "description": "<p>处罚事由</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PunishBasis",
            "description": "<p>处罚依据</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PunishmentResult",
            "description": "<p>处罚结果</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PunishDate",
            "description": "<p>处罚日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PunishGov",
            "description": "<p>处罚单位</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Implementation",
            "description": "<p>执行情况</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"CaseNo\": \"资环罚[2017]48号\",\n        \"PunishGov\": \"资阳市环境保护局\",\n        \"PunishDate\": \"2017-09-30 12:00:00\",\n        \"PunishmentResult\": \"我局决定对你单位处以3万元（大写：叁万元整）的罚款。\",\n        \"PunishBasis\": \"依据《建设项目环保保护管理条例》第二十八条之规定。\",\n        \"IllegalType\": \"\",\n        \"PunishReason\": \"发现你单位实施了以下环境违法行为：你单位新建汽车零部件项目未经环保竣工验收即投产使用。\",\n        \"Implementation\": \"\"\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEnvpunishmentGetenvpunishmentdetails",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/EnvPunishment/GetEnvPunishmentList",
    "title": "环保处罚列表",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "keyWord",
            "description": "<p>公司全名</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageIndex",
            "description": "<p>页码，默认1</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageSize",
            "description": "<p>每页数量，默认10</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>Id</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseNo",
            "description": "<p>决定书文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PunishDate",
            "description": "<p>处罚日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "IllegalType",
            "description": "<p>违法类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PunishGov",
            "description": "<p>处罚单位</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 1,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"CaseNo\": \"资环罚[2017]48号\",\n            \"PunishGov\": \"资阳市环境保护局\",\n            \"PunishDate\": \"2017-09-30 12:00:00\",\n            \"IllegalType\": \"\",\n            \"Id\": \"17e3156d060094b649c7f8be77d15fa2\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEnvpunishmentGetenvpunishmentlist",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/JudgeDocV4/GetJudgementDetail",
    "title": "裁判文书详情",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "id",
            "description": "<p>id</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>Id</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseName",
            "description": "<p>裁判文书名字</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseNo",
            "description": "<p>裁判文书编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseType",
            "description": "<p>裁判文书类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Content",
            "description": "<p>裁判文书内容</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Court",
            "description": "<p>执行法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreateDate",
            "description": "<p>创建时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "SubmitDate",
            "description": "<p>提交时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UpdateDate",
            "description": "<p>修改时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Appellor",
            "description": "<p>当事人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudgeDate",
            "description": "<p>裁判时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseReason",
            "description": "<p>案由</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "TrialRound",
            "description": "<p>审理程序</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Defendantlist",
            "description": "<p>被告</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Prosecutorlist",
            "description": "<p>原告</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "IsValid",
            "description": "<p>是否有效，True或false</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ContentClear",
            "description": "<p>裁判文书内容（QCC加工）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudgeResult",
            "description": "<p>判决结果（文书内容）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PartyInfo",
            "description": "<p>当事人(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "TrialProcedure",
            "description": "<p>审理经过(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtConsider",
            "description": "<p>本院认为(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PlaintiffRequest",
            "description": "<p>原告诉求(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "DefendantReply",
            "description": "<p>被告答辩(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtInspect",
            "description": "<p>本院查明(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PlaintiffRequestOfFirst",
            "description": "<p>一审原告诉求(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "DefendantReplyOfFirst",
            "description": "<p>一审被告答辩(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtInspectOfFirst",
            "description": "<p>一审法院查明(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtConsiderOfFirst",
            "description": "<p>一审法院认为(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AppellantRequest",
            "description": "<p>上诉人诉求(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AppelleeArguing",
            "description": "<p>被上诉人答辩(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteProcess",
            "description": "<p>执行经过(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CollegiateBench",
            "description": "<p>合议庭(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudegeDate",
            "description": "<p>裁判日期(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Recorder",
            "description": "<p>记录员(文书内容)</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "CourtNoticeList",
            "description": "<p>关联开庭公告</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtNoticeList.TotalNum",
            "description": "<p>关联开庭公告总条目数</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "CourtNoticeList.CourtNoticeInfo",
            "description": "<p>关联开庭公告详情</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtNoticeList.CourtNoticeInfo.CaseNo",
            "description": "<p>案号，CourtNoticeInfo列表字段</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtNoticeList.CourtNoticeInfo.OpenDate",
            "description": "<p>开庭日期，CourtNoticeInfo列表字段</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtNoticeList.CourtNoticeInfo.Defendant",
            "description": "<p>被告，CourtNoticeInfo列表字段</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtNoticeList.CourtNoticeInfo.CaseReason",
            "description": "<p>案由，CourtNoticeInfo列表字段</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtNoticeList.CourtNoticeInfo.Prosecutor",
            "description": "<p>原告，CourtNoticeInfo列表字段</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtNoticeList.CourtNoticeInfo.Id",
            "description": "<p>内部ID，CourtNoticeInfo列表字段</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "RelatedCompanies",
            "description": "<p>关联公司列表</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RelatedCompanies.KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RelatedCompanies.Name",
            "description": "<p>公司名称</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"CollegiateBench\": \"\\n审判员何绍辉\\n\",\n        \"SubmitDate\": \"2018-12-27 12:00:00\",\n        \"ProsecutorList\": [\n            \"瑞幸咖啡（北京）有限公司\"\n        ],\n        \"AppelleeArguing\": null,\n        \"CreateDate\": \"2019-01-06 07:50:13\",\n        \"Court\": \"上海市浦东新区人民法院\",\n        \"CourtConsider\": \"\\n本院认为，原、被告就系争房屋签订的房屋租赁合同系双方当事人真实意思表示，内容不违反法律规定，合法有效。合同签订后，原告依约向被告指定账户支付了租金、押金、进场费计47,500元，但被告未按约定向原告交付租赁物，违反了合同约定，原告行使合同解除权，符合合同约定，本院予以准许。合同解除后，尚未履行的，终止履行；已经履行的，根据履行情况和合同性质，当事人可以要求恢复原状、采取其他补救措施，并有权要求赔偿损失。原告未实际使用系争房屋，合同解除后，被告应将其收取原告的47,500元返还原告。原告举证的付款金额为8,400元的收条，该收条系案外人签某，对于收款人的身份，原告未进一步举证证实收款人与被告之间的关系或者该收款人有权代被告收款的相应证据。且原告向案外其他人的付款行为，亦与被告出具的转账委托书内容不符。故仅凭案外人出具的收条，不足以证明原告向被告付款8,400元的事实。原告主张被告返还该笔款项，本院难以支持。由于被告违约致使涉案租赁合同解除，原告要求被告按照合同约定承担解除合同的违约金5万元，于法有据，本院予以支持。被告经本院合法传唤，无正当理由未到庭，放弃了对原告提交证据的质证权利，由此造成的不利后果由其自行承担。\\n综上，依照《中华人民共和国合同法》第六十条、第九十三条第二款、第九十七条，《中华人民共和国民事诉讼法》第一百四十四条之规定，判决如下：\\n\",\n        \"CaseNo\": \"（2018）沪0115民初60627号\",\n        \"PlaintiffRequestOfFirst\": null,\n        \"UpdateDate\": \"2019-01-06 07:50:13\",\n        \"CourtInspectOfFirst\": null,\n        \"TrialRound\": \"一审\",\n        \"DefendantReply\": \"\\n上海梓赫置业有限公司未作答辩。\\n\",\n        \"JudgeDate\": \"2018-10-24 12:00:00\",\n        \"AppellantRequest\": null,\n        \"DefendantList\": [\n            \"上海梓赫置业有限公司\"\n        ],\n        \"IsValid\": \"true\",\n        \"Appellor\": [\n            \"上海梓赫置业有限公司\",\n            \"瑞幸咖啡（北京）有限公司\",\n            \"瑞幸咖啡(北京)有限公司\"\n        ],\n        \"ContentClear\": \"XXXXXXXXXX\",\n        \"CaseName\": \"瑞幸咖啡(北京)有限公司与上海梓赫置业有限公司房屋租赁合同纠纷一审民事判决书\",\n        \"JudegeDate\": \"\\n二零一八年十月二十四日\\n\",\n        \"DefendantReplyOfFirst\": null,\n        \"Recorder\": \"\\n书记员陈韫鏐\\n\",\n        \"PartyInfo\": \"\\n原告：瑞幸咖啡(北京)有限公司，住所地北京市。\\n法定代表人：钱治亚，执行董事。\\n委托诉讼代理人：刘超。\\n被告：上海梓赫置业有限公司，住所地上海市浦东新区。\\n法定代表人：陈鸣，执行董事。\\n\",\n        \"ExecuteProcess\": null,\n        \"TrialProcedure\": \"\\n原告瑞幸咖啡(北京)有限公司与被告上海梓赫置业有限公司房屋租赁合同纠纷一案，本院于2018年8月8日立案受理后，依法适用简易程序，公开开庭进行了审理。原告瑞幸咖啡(北京)有限公司的委托诉讼代理人刘超到庭参加诉讼，被告上海梓赫置业有限公司经本院传票传唤，无正当理由拒不到庭参加诉讼。本院依法缺席审理。本案现已审理终结。\\n\",\n        \"CaseType\": \"ms\",\n        \"CourtConsiderOfFirst\": null,\n        \"Content\": \"XXXXXXXXX\",\n        \"PlaintiffRequest\": \"\\n瑞幸咖啡(北京)有限公司向本院提出诉讼请求1、判令解除双方的《房屋租赁合同》；2、判令被告向原告返还55,900元(包括房租25,500元、押金17,000元、进场费5,000元、装修押金7,000元、装修管理费1,400元)；3、判令被告向原告支付违约金50,000元。事实和理由：2017年11月30日，原、被告签订《房屋租赁合同》，约定被告将上海市浦东新区东明路2600、2608、2612号晶华公馆地下二层A21编号B2-A21的商铺(以下简称系争房屋)租赁给被告经营咖啡及轻餐饮使用。租赁合同签订后，原告按约向被告支付了租金、押金等各款项，但被告迟迟未将系争房屋交付给原告。直至原告发现经营场所贴出告知书，原告才得知由于被告一直拖欠其上家的租金导致被案外人解除与被告的租赁合同。被告无法向原告交付系争房屋的违约行为，导致双方合同目的无法实现。综上，为了维护原告的合法权益，诉至法院。\\n\",\n        \"CourtInspect\": \"\\n本院经审理认定事实如下：2017年11月30日，被告(出租代理方、甲方)与原告(承租方、乙方)签订《房屋租赁合同》一份，合同约定，甲方代理出租给乙方的商户位于上海市浦东新区东明路2600、2608、2612号晶华公馆地下二层A21，商铺编号B2-A21,乙方承租的商铺使用面积为14平方米。本房屋的实际租赁区域平面图见本合同附件一。乙方承租租赁房屋主营产品为现场制售咖啡及轻餐饮，并遵守国家和本市有关房屋使用和物业管理的规定。甲方同意于2017年12月1日前将本房屋交付乙方。乙方租赁本房屋的租赁期限为2年，自2017年12月15日至2019年12月14日止。租赁房屋的起租日为2017年12月15日。甲、乙双方约定，本房屋每合同年的租金为102,000元。本房屋先付租金后使用。乙方同意在本合同签订之日起3天内向甲方支付第一季度租金25,500元，以后乙方最晚应于下季度开始前的15日向甲方支付下一季度的租金。甲、乙双方约定，甲方交付租赁房屋前，乙方应向甲方支付押金，押金金额相当于2个月的租金，即17,000元。甲、乙双方同意，有下列情形之一的，一方可书面通知另一方解除本合同。违反合同的一方，应向另一方支付50,000元的违约金；给对方造成损失的，若支付的违约金不足抵付一方损失，还应赔偿造成损失与违约金的差额部分：(一)甲方延期交付租赁房屋超过30日，经宽限期后仍未交付的；……。合同另对其他事项作了约定。合同附件中还附标注商铺的室号、面积的平面分割图。同日双方又签订了《晶华公馆B2-A21商户进场协议》，该协议约定，乙方(即原告)须支付甲方(即被告)进场费10,000元整，5,000元于合同生效三个工作日内支付给甲方。协议另对其他事项对了约定。\\n2017年12月2日，被告向原告出具转账委托授权书，同意原告将业务往来款打入被告指定的受托人陈超的账号(并附账号)。\\n2017年12月5日，原告向陈超账户转账付款47,500元。原告称前述款项中包括房租25,500元、押金17,000元、进场费5,000元。\\n审理中，原告另提交日期为2017年12月22日的收条一份，内容为“今收到瑞幸咖啡(北京)有限公司装修押金7,000元，装修管理费1,400元，共计8,400元”。末尾署名：(上海梓赫置业)陈佳；2018年6月，原告至租赁地点拍摄的照片及视频，证明截止今年6月，系争房屋处大门紧闭，门上张贴了上海辕盛实业发展有限公司的告商户函，该函件载明由于上海梓赫置业有限公司欠付其租金，现通知解除其与上海梓赫置业有限公司的租赁合同。因被告未能向原告交付租赁物，原告提起诉讼。\\n\",\n        \"RelatedCompanies\": [\n            {\n                \"KeyNo\": \"8fab5089a695e3ef695c8af434345e43\",\n                \"Name\": \"上海梓赫置业有限公司\"\n            },\n            {\n                \"KeyNo\": \"7035da3364f34e4f291ab35ca6489285\",\n                \"Name\": \"上海辕盛实业发展有限公司\"\n            },\n            {\n                \"KeyNo\": \"ac3c8ac00cba0a53e918435104ae21e7\",\n                \"Name\": \"瑞幸咖啡(北京)有限公司\"\n            }\n        ],\n        \"CaseReason\": \"房屋租赁合同纠纷\",\n        \"Id\": \"73cb2065b6b986d442056ece96efafa20\",\n        \"JudgeResult\": \"一、解除原告瑞幸咖啡(北京)有限公司与被告上海梓赫置业有限公司于2017年11月30日签订的《房屋租赁合同》；二、被告上海梓赫置业有限公司于本判决生效之日起十日内返还原告瑞幸咖啡(北京)有限公司47,500元；三、被告上海梓赫置业有限公司于本判决生效之日起十日内支付原告瑞幸咖啡(北京)有限公司违约金50,000元。如果未按本判决指定的期间履行给付金钱义务，应当依照《中华人民共和国民事诉讼法》第二百五十三条规定，加倍支付迟延履行期间的债务利息。案件受理费2,418元，减半收取计1,209元，由原告瑞幸咖啡(北京)有限公司负担25元，被告上海梓赫置业有限公司负担1,184元。如不服本判决，可以在判决书送达之日起十五日内，向本院递交上诉状，并按对方当事人的人数提出副本，上诉于上海市第一中级人民法院。\",\n        \"CourtNoticeList\": {\n            \"TotalNum\": 2,\n            \"CourtNoticeInfo\": [\n                {\n                    \"InputDate\": \"2019-04-04 03:35:54\",\n                    \"CnId\": \"73cb2065b6b986d442056ece96efafa20\",\n                    \"InnerId\": \"5ca5b3da2970c436733f86b6\",\n                    \"QccId\": \"6cadf82f0d19d2c9ccf7acd2bba329c45\"\n                },\n                {\n                    \"InputDate\": \"2019-04-04 03:35:54\",\n                    \"CnId\": \"73cb2065b6b986d442056ece96efafa20\",\n                    \"InnerId\": \"5ca5b3da2970c436733f86b7\",\n                    \"QccId\": \"40f42e340ef30b945f6804ab1acccd295\"\n                }\n            ]\n        }\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetJudgedocv4Getjudgementdetail",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/JudgeDocV4/SearchJudgmentDoc",
    "title": "裁判文书列表",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "searchKey",
            "description": "<p>公司全名</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageIndex",
            "description": "<p>页码，默认1</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageSize",
            "description": "<p>每页数量，默认10</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>Id</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Court",
            "description": "<p>执行法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseName",
            "description": "<p>裁判文书名字</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseNo",
            "description": "<p>裁判文书编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseType",
            "description": "<p>裁判文书类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "SubmitDate",
            "description": "<p>发布时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UpdateDate",
            "description": "<p>审判时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "IsProsecutor",
            "description": "<p>是否原告（供参考）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "IsDefendant",
            "description": "<p>是否被告（供参考）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtYear",
            "description": "<p>开庭时间年份</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseRole",
            "description": "<p>涉案人员角色</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtLevel",
            "description": "<p>法院级别，最高法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseReason",
            "description": "<p>案由</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseReasonType",
            "description": "<p>案由类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CourtMonth",
            "description": "<p>开庭时间月份</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 1,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"CourtMonth\": \"201812\",\n            \"SubmitDate\": \"2018-12-27 12:00:00\",\n            \"CaseName\": \"瑞幸咖啡(北京)有限公司与上海梓赫置业有限公司房屋租赁合同纠纷一审民事判决书\",\n            \"IsDefendant\": \"false\",\n            \"Court\": \"上海市浦东新区人民法院\",\n            \"CaseNo\": \"（2018）沪0115民初60627号\",\n            \"UpdateDate\": \"2019-01-06 07:50:13\",\n            \"CourtYear\": \"2018\",\n            \"IsProsecutor\": \"false\",\n            \"CaseReasonType\": \"0019\",\n            \"CaseType\": \"ms\",\n            \"CaseRole\": \"[{\\\"P\\\":\\\"瑞幸咖啡(北京)有限公司\\\",\\\"R\\\":\\\"原告\\\"},{\\\"P\\\":\\\"上海梓赫置业有限公司\\\",\\\"R\\\":\\\"被告\\\"}]\",\n            \"CaseReason\": \"房屋租赁合同纠纷\",\n            \"Id\": \"73cb2065b6b986d442056ece96efafa20\",\n            \"CourtLevel\": \"2\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetJudgedocv4Searchjudgmentdoc",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/JudicialAssistance/GetJudicialAssistance",
    "title": "司法协助信息",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "keyWord",
            "description": "<p>公司全名</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecutedBy",
            "description": "<p>被执行人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityAmount",
            "description": "<p>股权数额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EnforcementCourt",
            "description": "<p>执行法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecutionNoticeNum",
            "description": "<p>执行通知书文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Status",
            "description": "<p>类型</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "EquityFreezeDetail",
            "description": "<p>股权冻结情况</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.CompanyName",
            "description": "<p>相关企业名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EEquityFreezeDetail.xecutionMatters",
            "description": "<p>执行事项</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.ExecutionDocNum",
            "description": "<p>执行文书文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.ExecutionVerdictNum",
            "description": "<p>执行裁定书文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.ExecutedPersonDocType",
            "description": "<p>被执行人证件种类</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.ExecutedPersonDocNum",
            "description": "<p>被执行人证件号码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.FreezeStartDate",
            "description": "<p>冻结开始日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.FreezeEndDate",
            "description": "<p>冻结结束日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.FreezeTerm",
            "description": "<p>冻结期限</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityFreezeDetail.PublicDate",
            "description": "<p>公示日期</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "EquityUnFreezeDetail",
            "description": "<p>解除冻结详情</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.ExecutionMatters",
            "description": "<p>执行事项</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.ExecutionVerdictNum",
            "description": "<p>执行裁定书文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.ExecutionDocNum",
            "description": "<p>执行文书文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.ExecutedPersonDocType",
            "description": "<p>被执行人证件种类</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.ExecutedPersonDocNum",
            "description": "<p>被执行人证件号码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.UnFreezeDate",
            "description": "<p>解除冻结日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.PublicDate",
            "description": "<p>公示日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.ThawOrgan",
            "description": "<p>解冻机关</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EquityUnFreezeDetail.ThawDocNo",
            "description": "<p>解冻文书号</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "JudicialPartnersChangeDetail",
            "description": "<p>股东变更信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.ExecutionMatters",
            "description": "<p>执行事项</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.ExecutionVerdictNum",
            "description": "<p>执行裁定书文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.ExecutedPersonDocType",
            "description": "<p>被执行人证件种类</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.ExecutedPersonDocNum",
            "description": "<p>被执行人证件号码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.Assignee",
            "description": "<p>受让人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.AssistExecDate",
            "description": "<p>协助执行日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.AssigneeDocKind",
            "description": "<p>受让人证件种类</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.AssigneeRegNo",
            "description": "<p>受让人证件号码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "JudicialPartnersChangeDetail.StockCompanyName",
            "description": "<p>股权所在公司名称</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": [\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"EnforcementCourt\": \"北京市第二中级人民法院\",\n            \"EquityUnFreezeDetail\": {\n                \"CompanyName\": \"小米\",\n                \"FreezeTerm\": \"1095\",\n                \"ExecutionVerdictNum\": \"（2017）京02民初58号\",\n                \"ExecutedPersonDocNum\": \"\",\n                \"FreezeStartDate\": \"2017-09-13 12:00:00\",\n                \"ExecutionMatters\": \"轮候冻结股权、其他投资权益\",\n                \"FreezeEndDate\": \"2020-09-12 12:00:00\",\n                \"ExecutedPersonDocType\": \"居民身份证\"\n            },\n            \"EquityAmount\": \"15000万人民币元\",\n            \"ExecutionNoticeNum\": \"（2017）京02民初58号\",\n            \"ExecutedBy\": \"霍庆华\"\n        },\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"EnforcementCourt\": \"新疆维吾尔自治区高级人民法院\",\n            \"EquityUnFreezeDetail\": {\n                \"CompanyName\": \"小米\",\n                \"FreezeTerm\": \"1095\",\n                \"ExecutionVerdictNum\": \"(2017)新执47号\",\n                \"ExecutedPersonDocNum\": \"110105011796483\",\n                \"FreezeStartDate\": \"2017-08-16 12:00:00\",\n                \"ExecutionMatters\": \"轮候冻结股权、其他投资权益\",\n                \"FreezeEndDate\": \"2020-08-16 12:00:00\",\n                \"ExecutedPersonDocType\": \"\"\n            },\n            \"EquityAmount\": \"254984.5万人民币元\",\n            \"ExecutionNoticeNum\": \"(2017)新执47号\",\n            \"ExecutedBy\": \"中国庆华能源集团有限公司\"\n        },\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"EnforcementCourt\": \"杭州市西湖区人民法院\",\n            \"EquityUnFreezeDetail\": {\n                \"CompanyName\": \"小米\",\n                \"FreezeTerm\": \"1095\",\n                \"ExecutionVerdictNum\": \"(2017)浙0106民初6913号\",\n                \"ExecutedPersonDocNum\": \"\",\n                \"FreezeStartDate\": \"2017-08-29 12:00:00\",\n                \"ExecutionMatters\": \"公示冻结股权、其他投资权益\",\n                \"FreezeEndDate\": \"2020-08-28 12:00:00\",\n                \"ExecutedPersonDocType\": \"居民身份证\"\n            },\n            \"EquityAmount\": \"15000万人民币元\",\n            \"ExecutionNoticeNum\": \"(2017)浙0106民初6913号\",\n            \"ExecutedBy\": \"霍庆华\"\n        },\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"EnforcementCourt\": \"北京市第二中级人民法院\",\n            \"EquityUnFreezeDetail\": {\n                \"CompanyName\": \"小米\",\n                \"FreezeTerm\": \"1095\",\n                \"ExecutionVerdictNum\": \"(2017)京02民初58号\",\n                \"ExecutedPersonDocNum\": \"110105011796483\",\n                \"FreezeStartDate\": \"2017-09-13 12:00:00\",\n                \"ExecutionMatters\": \"轮候冻结股权、其他投资权益\",\n                \"FreezeEndDate\": \"2020-09-12 12:00:00\",\n                \"ExecutedPersonDocType\": \"\"\n            },\n            \"EquityAmount\": \"254984.5万人民币元\",\n            \"ExecutionNoticeNum\": \"(2017)京02民初58号\",\n            \"ExecutedBy\": \"中国庆华能源集团有限公司\"\n        },\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"ExecutionNoticeNum\": \"（2016）粤01执3073号\",\n            \"EnforcementCourt\": \"广东省广州市中级人民法院\",\n            \"JudicialPartnersChangeDetail\": {\n                \"ExecutionVerdictNum\": \"（2016）粤01执3073号\",\n                \"ExecutedPersonDocNum\": \"110105011796483\",\n                \"ExecutionMatters\": \"续行冻结股权、其他投资权益\",\n                \"ExecutedPersonDocType\": \"居民身份证\"\n            },\n            \"EquityAmount\": \"15000万人民币元\",\n            \"ExecutedBy\": \"霍庆华\"\n        },\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"ExecutionNoticeNum\": \"(2017)浙0106民初6913号\",\n            \"EnforcementCourt\": \"杭州市西湖区人民法院\",\n            \"JudicialPartnersChangeDetail\": {\n                \"ExecutionVerdictNum\": \"(2017)浙0106民初6913号\",\n                \"ExecutedPersonDocNum\": \"110105011796483\",\n                \"ExecutionMatters\": \"轮候冻结股权、其他投资权益\",\n                \"ExecutedPersonDocType\": \"\"\n            },\n            \"EquityAmount\": \"254984.5万人民币元\",\n            \"ExecutedBy\": \"中国庆华能源集团有限公司\"\n        },\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"ExecutionNoticeNum\": \"(2016)粤01执3073号\",\n            \"EnforcementCourt\": \"广东省广州市中级人民法院\",\n            \"JudicialPartnersChangeDetail\": {\n                \"ExecutionVerdictNum\": \"(2016)粤01执3073号\",\n                \"ExecutedPersonDocNum\": \"110105011796483\",\n                \"ExecutionMatters\": \"公示冻结股权、其他投资权益\",\n                \"ExecutedPersonDocType\": \"\"\n            },\n            \"EquityAmount\": \"254984.5万人民币元\",\n            \"ExecutedBy\": \"中国庆华能源集团有限公司\"\n        },\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"ExecutionNoticeNum\": \"（2017）浙0106民初702、703号\",\n            \"EnforcementCourt\": \"杭州市西湖区人民法院\",\n            \"JudicialPartnersChangeDetail\": {\n                \"ExecutionVerdictNum\": \"（2017）浙0106民初702、703号\",\n                \"ExecutedPersonDocNum\": \"110105011796483\",\n                \"ExecutionMatters\": \"轮候冻结股权、其他投资权益\",\n                \"ExecutedPersonDocType\": \"\"\n            },\n            \"EquityAmount\": \"254984.5万人民币元\",\n            \"ExecutedBy\": \"中国庆华能源集团有限公司\"\n        },\n        {\n            \"Status\": \"股权冻结|冻结\",\n            \"ExecutionNoticeNum\": \"（2016）粤01执3073号\",\n            \"EnforcementCourt\": \"广东省广州市中级人民法院\",\n            \"JudicialPartnersChangeDetail\": {\n                \"ExecutionVerdictNum\": \"（2016）粤01执3073号\",\n                \"ExecutedPersonDocNum\": \"110105011796483\",\n                \"ExecutionMatters\": \"轮候冻结股权、其他投资权益\",\n                \"ExecutedPersonDocType\": \"\"\n            },\n            \"EquityAmount\": \"254984.5万人民币元\",\n            \"ExecutedBy\": \"中国庆华能源集团有限公司\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetJudicialassistanceGetjudicialassistance",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/JudicialSale/GetJudicialSaleDetail",
    "title": "司法拍卖详情",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "id",
            "description": "<p>id</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Title",
            "description": "<p>标题</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Context",
            "description": "<p>详情内容</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "\n{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"Context\": \"<p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 25.15pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">江苏省无锡市新吴区人民法院将于<\\/span><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\">2018<\\/span><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\">年<span>5<\\/span>月<span>4<\\/span>日<span>10<\\/span>时<\\/span><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\">至<span>2018<\\/span>年<span>5<\\/span>月<span>5<\\/span>日<span>10<\\/span>时止<\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">（延时除外）在阿里巴巴司法拍卖网络平台（本院账户名：江苏省无锡市新吴区人民法院，网址<span><a target=\\\"_blank\\\">https://sf.taobao.com<\\/a><\\/span>）进行公开拍卖活动，现公告如下：<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 29.9pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">一、拍卖标的：<\\/span><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\">无锡尚德太阳能电力有限公司所有的一批太阳能组件。<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 29.9pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\">起拍价：<span>2737600<\\/span>元；保证金：<span>400000<\\/span>元；加价幅度：<span>13000<\\/span>元；<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\">【特别提醒】按现状拍卖，法院不保证资产质量，以交付当天为现状为准。无锡尚德太阳能电力有限公司所有的一批太阳能组件<span>6678<\\/span>片，合计<span>1760445<\\/span>瓦，评估值（含税）为<span>4887675<\\/span>元，该批资产现场勘察主要采用目测观察手段，未使用仪器对委估资产进行测试和查验，不可能确定其有无内部缺损。拍卖成交后，相关手续由买受人自行办理，并承担费用。买受人应当自收到本院成交确认书之日起十五日内自行前往尚德公司提取上述资产，由此产生的整理、装载、运输等相关费用和安全责任均由买受人承担。付款期限为竞拍成功后<span>10<\\/span>日内。<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 27.85pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">二、咨询、展示看样的时间与方式：自公告之日起至开拍当日止（双休、节假日除外）接受咨询<\\/span><strong><span style=\\\"color: red;\\\">（咨询时间为正常工作日，上午<span>9<\\/span>点至<span>11<\\/span>点，下午<span>1<\\/span>点至<span>4<\\/span>点），咨询人：无锡嘉元拍卖行有限公司 张伟<span>13771020898 ,本次拍卖不安排看样。<\\/span><\\/span><\\/strong><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 25.15pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">三、拍卖裁定与拍卖通知邮寄送达到被执行人住所地。<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">四、优先购买权人参与竞拍情况说明：<\\/span><strong><span style=\\\"color: red;\\\">本标的如有优先购买权人，请最迟于开始拍卖前五日到本院执行局<\\/span><\\/strong><strong><span style=\\\"color: red;\\\">1703<\\/span><\\/strong><strong><span style=\\\"color: red;\\\">室提交书面申请。<\\/span><\\/strong><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">.<\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">五、竞买人应通过支付宝账户缴纳足额的拍卖保证金，<span>(<\\/span>因本院目前条件无法满足接受竞买人线下缴纳保证金报名<span>)<\\/span>。（保证金支付帮助：<span><a target=\\\"_blank\\\"><span style=\\\"color: black;\\\">https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q1<\\/span><\\/a><\\/span>）<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">六、拍卖成交余款请在成交当日起<span>10<\\/span>日内支付：<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">1<\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">、银行付款：通过银行汇款到法院指定帐户（户名：<\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">无锡市新吴区人民法院，开户银行：中国建设银行股份有限公司无锡高新技术产业开发区支行，账号：<span>32050161543600000413<\\/span><\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">）<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">2<\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">、支付宝网付款：登录我的淘宝<span>-<\\/span>我的拍卖支付（付款教程：<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"><a target=\\\"_blank\\\"><span style=\\\"color: black;\\\">https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q8<\\/span><\\/a><\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">）；<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 25.8pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">七、司法拍卖因标的物本身价值，其起拍价、保证金、竞拍成交价格相对较高的。竞买人参与竞拍，支付保证金及余款可能当天限额而无法支付，请根据自身情况选择网上充值银行。各大银行充值和支付限额的查询网址：<span><a target=\\\"_blank\\\"><span style=\\\"color: black;\\\">https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q1<\\/span><\\/a><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 25.8pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">八、委托竞买的，委托人与受托人须提前将委托手续将法院确认，竞买成功后，及时与承办法官至法院办理交接手续。如委托手续不全，竞买活动认定为受托人的个人行为。<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">九、特别提醒：标的物以实物现状为准，本院不承担本标的瑕疵保证。有意者请亲自实地看样，未看样的竞买人视为对本标的实物现状的确认，责任自负。<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">十、与本标的物有利害关系的当事人可参加竞拍，不参加竞拍的请关注本次拍卖活动的整个过程。<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">十一、对本次拍卖标的物权属有异议者，请于开拍当日前与本院联系。<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">十二、本拍卖标的物，可（或者不可）<span style=\\\"background: white;\\\">办理银行贷款的。如可以，请竞买人关注“拍卖贷款”栏目。并要求注明银行贷款最迟发放到法院的时间及操作程序及相应法律后果<span>(<\\/span>可在须知中明确<span>)<\\/span>。<\\/span><span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">   <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">竞买人在拍卖竞价前请务必再仔细阅读本院发布的拍卖须知，遵守拍卖须知规定。<span><\\/span><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;line-height: 150.0%;text-indent: 24.45pt;\\\" class=\\\"MsoNormal\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">本规则其他未尽事宜请向本院咨询，系统平台管理：<span>0510-82211199<\\/span>；淘宝技术咨询：<span>400-822-2870<\\/span>；法院举报监督：<\\/span><span style=\\\"color: #333333;line-height: 150.0%;font-size: 14.0pt;\\\">0510-81190520<\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\">。<\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;text-align: right;line-height: 150.0%;text-indent: 25.15pt;\\\" class=\\\"MsoNormal\\\" align=\\\"right\\\"><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <\\/span><span style=\\\"color: black;line-height: 150.0%;font-size: 14.0pt;\\\"> <a target=\\\"_blank\\\"><span style=\\\"color: black;\\\"><span>江苏省无锡市<\\/span><\\/span><span style=\\\"color: black;\\\"><span>新吴区<\\/span><\\/span><span style=\\\"color: black;\\\"><span>人民法院<\\/span><\\/span><\\/a><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p><p style=\\\"background: white;margin: 0.0cm 0.0cm 0.0pt;text-align: right;line-height: 150.0%;text-indent: 23.75pt;\\\" class=\\\"MsoNormal\\\" align=\\\"right\\\"><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\">2018<\\/span><span style=\\\"color: red;line-height: 150.0%;font-size: 14.0pt;\\\">年<span>4<\\/span>月<span>18<\\/span>日<\\/span><span style=\\\"line-height: 150.0%;font-size: 14.0pt;\\\"><\\/span><\\/p><p><span style=\\\"color: #000000;\\\"><\\/span><\\/p>\",\n        \"Title\": \"无锡市新吴区人民法院关于无锡尚德太阳能电力有限公司所有的一批太阳能组件。（第二次拍卖）的公告\"\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetJudicialsaleGetjudicialsaledetail",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/JudicialSale/GetJudicialSaleList",
    "title": "司法拍卖列表",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "keyWord",
            "description": "<p>公司全名</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageIndex",
            "description": "<p>页码，默认1</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageSize",
            "description": "<p>每页数量，默认10</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>主键</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Name",
            "description": "<p>标题</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ExecuteGov",
            "description": "<p>委托法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ActionRemark",
            "description": "<p>拍卖时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "YiWu",
            "description": "<p>起拍价</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 2,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"ActionRemark\": \"2018年5月4日10时至2018年5月5日10时止\",\n            \"YiWu\": \"2,737,600\",\n            \"Id\": \"ba518106e41c0966120d69236f3e0eb1\",\n            \"ExecuteGov\": \"无锡市新吴区人民法院\",\n            \"Name\": \"无锡市新吴区人民法院关于无锡尚德太阳能电力有限公司所有的一批太阳能组件。（第二次拍卖）的公告\"\n        },\n        {\n            \"ActionRemark\": \"2018年4月16日10时至2018年4月17日10时止\",\n            \"YiWu\": \"3,422,000\",\n            \"Id\": \"76180fda649d71a71642f5e3ef703b9e\",\n            \"ExecuteGov\": \"无锡市新吴区人民法院\",\n            \"Name\": \"无锡市新吴区人民法院关于无锡尚德太阳能电力有限公司所有的一批太阳能组件。（第一次拍卖）的公告\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetJudicialsaleGetjudicialsalelist",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/LandMortgage/GetLandMortgageDetails",
    "title": "土地抵押详情",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "id",
            "description": "<p>id</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "LandSign",
            "description": "<p>宗地标识</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "LandNo",
            "description": "<p>宗地编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AdministrativeArea",
            "description": "<p>行政区</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Acreage",
            "description": "<p>土地面积（公顷）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Address",
            "description": "<p>宗地地址</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ObligeeNo",
            "description": "<p>土地他项权利人证号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "UsufructNo",
            "description": "<p>土地使用权证号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "MortgagorNature",
            "description": "<p>土地抵押人性质</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "MortgagePurpose",
            "description": "<p>抵押土地用途</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "NatureAndType",
            "description": "<p>抵押土地权属性质与使用权类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "MortgageAcreage",
            "description": "<p>抵押面积（公顷）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AssessmentPrice",
            "description": "<p>评估金额（万元）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "MortgagePrice",
            "description": "<p>抵押金额（万元）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OnBoardStartTime",
            "description": "<p>土地抵押登记起始时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OnBoardEndTime",
            "description": "<p>土地抵押结束时间</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "MortgagorName",
            "description": "<p>土地抵押人名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "MortgagorName.KeyNo",
            "description": "<p>KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "stirng",
            "optional": false,
            "field": "MortgagorName.Name",
            "description": "<p>名称</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "MortgagePeople",
            "description": "<p>土地抵押人名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "MortgagePeople.KeyNo",
            "description": "<p>KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "stirng",
            "optional": false,
            "field": "MortgagePeople.Name",
            "description": "<p>名称</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"AdministrativeArea\": \"510113000\",\n        \"OnBoardStartTime\": \"2014-09-04 12:00:00\",\n        \"ObligeeNo\": \"青他项（2014）第88号\",\n        \"MortgagorNature\": \"有限责任公司\",\n        \"OnBoardEndTime\": \"2017-09-02 12:00:00\",\n        \"Address\": \"青白江区九峰路以南、青白江大道以西\",\n        \"UsufructNo\": \"青国用（2011）第5536号\",\n        \"MortgagePrice\": \"550.0000\",\n        \"MortgagePurpose\": \"工业用地\",\n        \"Acreage\": \"1.1357\",\n        \"LandNo\": \"QBJ1-22-80\",\n        \"MortgagorName\": {\n            \"KeyNo\": \"4d3b51b032f55c2c8e196615bf4ba628\",\n            \"Name\": \"成都农村商业银行股份有限公司青白江弥牟支行\"\n        },\n        \"MortgagePeople\": {\n            \"KeyNo\": \"005ab78bc0c2e3535a40050f855fa844\",\n            \"Name\": \"成都威格佳门窗有限公司\"\n        },\n        \"NatureAndType\": \"国有土地、出让\",\n        \"LandSign\": \"\",\n        \"MortgageAcreage\": \"1.1357\",\n        \"AssessmentPrice\": \"554.2100\"\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetLandmortgageGetlandmortgagedetails",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  },
  {
    "type": "get",
    "url": "/LandMortgage/GetLandMortgageList",
    "title": "土地抵押列表",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "keyWord",
            "description": "<p>公司全名</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageIndex",
            "description": "<p>页码，默认1</p>"
          },
          {
            "group": "Parameter",
            "type": "int",
            "optional": false,
            "field": "pageSize",
            "description": "<p>每页数量，默认10</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Id",
            "description": "<p>Id</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Address",
            "description": "<p>地址</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AdministrativeArea",
            "description": "<p>行政区</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "MortgageAcreage",
            "description": "<p>抵押面积（公顷）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "MortgagePurpose",
            "description": "<p>抵押土地用途</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StartDate",
            "description": "<p>开始日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EndDate",
            "description": "<p>结束日期</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 2,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"AdministrativeArea\": \"510113000\",\n            \"StartDate\": \"2014-09-04 12:00:00\",\n            \"Address\": \"青白江区九峰路以南、青白江大道以西\",\n            \"Id\": \"a05a38627a629a6f1a49fef7fff4d648\",\n            \"EndDate\": \"2017-09-02 12:00:00\",\n            \"MortgageAcreage\": \"1.1357\",\n            \"MortgagePurpose\": \"工业用地\"\n        },\n        {\n            \"AdministrativeArea\": \"510113000\",\n            \"StartDate\": \"2012-06-28 12:00:00\",\n            \"Address\": \"青白江区九峰路以南、青白江大道以西\",\n            \"Id\": \"71f3f48cc1cf485f0b4516879c39f311\",\n            \"MortgageAcreage\": \"1.1357\",\n            \"MortgagePurpose\": \"工业用地\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetLandmortgageGetlandmortgagelist",
    "error": {
      "examples": [
        {
          "title": "请求异常:",
          "content": "{\n    \"Status\": \"500\",\n    \"Message\": \"错误请求\"\n}",
          "type": "json"
        }
      ]
    }
  }
] });
