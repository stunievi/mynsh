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
            "field": "fullName",
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
    "url": "/CIAEmployeeV4/GetStockRelationInfo",
    "title": "企业人员董监高信息",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "type": "object[]",
            "optional": false,
            "field": "CIACompanyLegals",
            "description": "<p>担任法人公司信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIACompanyLegals.Name",
            "description": "<p>企业名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIACompanyLegals.RegNo",
            "description": "<p>注册号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIACompanyLegals.RegCap",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIACompanyLegals.RegCapCur",
            "description": "<p>注册资本币种</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIACompanyLegals.Status",
            "description": "<p>企业状态</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIACompanyLegals.EcoKind",
            "description": "<p>企业类型</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "CIAForeignInvestments",
            "description": "<p>对外投资信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignInvestments.SubConAmt",
            "description": "<p>认缴出资额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignInvestments.SubCurrency",
            "description": "<p>认缴出资币种</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignInvestments.EcoKind",
            "description": "<p>企业类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignInvestments.Name",
            "description": "<p>企业名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignInvestments.RegNo",
            "description": "<p>注册号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignInvestments.RegCap",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignInvestments.RegCapCur",
            "description": "<p>注册资本币种</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignInvestments.Status",
            "description": "<p>企业状态</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "CIAForeignOffices",
            "description": "<p>在外任职信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignOffices.Position",
            "description": "<p>职位</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignOffices.EcoKind",
            "description": "<p>企业类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignOffices.Name",
            "description": "<p>企业名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignOffices.RegNo",
            "description": "<p>注册号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignOffices.RegCap",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignOffices.RegCapCur",
            "description": "<p>注册资本币种</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CIAForeignOffices.Status",
            "description": "<p>企业状态</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"CIAForeignInvestments\": [\n            {\n                \"InputDate\": \"2019-04-14 01:12:43\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"SubConAmt\": \"1\",\n                \"RegNo\": \"110105020574345\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京普达鑫投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:43\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"SubConAmt\": \"800\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海纵庭酒业有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:43\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"SubConAmt\": \"1\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京普惠思投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:43\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"SubConAmt\": \"800\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海水晶荔枝娱乐文化有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:43\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"SubConAmt\": \"1\",\n                \"RegNo\": \"110105020574466\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京昌盛四海投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:43\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"SubConAmt\": \"200\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划体育文化有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"SubConAmt\": \"200\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划演出经纪有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"SubConAmt\": \"11.880700\",\n                \"RegNo\": \"310116003315491\",\n                \"EcoKind\": \"有限合伙企业\",\n                \"Name\": \"上海牛铺信息科技合伙企业（有限合伙）\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"SubConAmt\": \"10.000000\",\n                \"RegNo\": \"440003000139391\",\n                \"EcoKind\": \"有限合伙企业\",\n                \"Name\": \"珠海横琴普斯股权投资企业（有限合伙）\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"注销\",\n                \"RegCap\": \"30万人民币\",\n                \"SubConAmt\": \"3\",\n                \"RegNo\": \"5101042007639\",\n                \"EcoKind\": \"有限责任公司(自然人投资或控股)\",\n                \"Name\": \"成都市锦江区大歌星餐饮娱乐有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"200.000000万人民币\",\n                \"SubConAmt\": \"18\",\n                \"RegNo\": \"330211000110854\",\n                \"EcoKind\": \"有限责任公司(自然人投资或控股)\",\n                \"Name\": \"宁波朗盛投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"SubConAmt\": \"10\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海普思投资有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"注销\",\n                \"RegCap\": \"4000万人民币\",\n                \"SubConAmt\": \"80\",\n                \"RegNo\": \"310115000814817\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海万尚置业有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万元人民币\",\n                \"SubConAmt\": \"10\",\n                \"RegNo\": \"110105016521299\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京达德厚鑫投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"7860万人民币\",\n                \"SubConAmt\": \"157.2\",\n                \"EcoKind\": \"有限责任公司(自然人投资或控股)\",\n                \"Name\": \"大连合兴投资有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"SubConAmt\": \"200\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划影视文化有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万元人民币\",\n                \"SubConAmt\": \"10万元\",\n                \"RegNo\": \"440003000137855\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"珠海横琴普斯投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"100万元人民币\",\n                \"SubConAmt\": \"1\",\n                \"RegNo\": \"110105016521346\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京汇德信投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10000万人民币\",\n                \"SubConAmt\": \"6850\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划文化发展有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegNo\": \"330522000184262\",\n                \"EcoKind\": \"个人独资企业\",\n                \"Name\": \"珺娱（湖州）文化发展中心\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"526.3158万元人民币\",\n                \"SubConAmt\": \"98\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司(自然人投资或控股)\",\n                \"Name\": \"北京叮咚柠檬科技有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"SubConAmt\": \"1.000000\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限合伙企业\",\n                \"Name\": \"上海沓厚投资合伙企业（有限合伙）\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"SubConAmt\": \"200\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划音乐有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1111.11万人民币\",\n                \"SubConAmt\": \"\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海爱洛星食品有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"2000万元人民币\",\n                \"SubConAmt\": \"2000\",\n                \"RegNo\": \"110105012460853\",\n                \"EcoKind\": \"有限责任公司(自然人独资)\",\n                \"Name\": \"北京普思投资有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:44\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000.000000万人民币\",\n                \"SubConAmt\": \"10\",\n                \"RegNo\": \"120116000437468\",\n                \"EcoKind\": \"有限责任公司\",\n                \"Name\": \"天津普思资产管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1333.3325万人民币\",\n                \"SubConAmt\": \"200.000000\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划电子游戏有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"注销\",\n                \"RegCap\": \"50.000000万元人民币\",\n                \"SubConAmt\": \"5 万元\",\n                \"RegNo\": \"330212000097826\",\n                \"EcoKind\": \"私营有限责任公司(自然人控股或私营性质企业控股)\",\n                \"Name\": \"宁波市鄞州大歌星餐饮娱乐有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1111.1111万元人民币\",\n                \"SubConAmt\": \"200\",\n                \"RegNo\": \"110105020849335\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京香蕉计划体育文化有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000.000000万人民币\",\n                \"SubConAmt\": \"10.000000\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司\",\n                \"Name\": \"平潭普思资产管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万\",\n                \"SubConAmt\": \"300\",\n                \"RegNo\": \"210200000248224\",\n                \"EcoKind\": \"有限责任公司(自然人投资或控股)\",\n                \"Name\": \"蓝泰科技（大连）有限公司\"\n            }\n        ],\n        \"CIACompanyLegals\": [\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"注销\",\n                \"RegNo\": \"320503600090522\",\n                \"EcoKind\": \"个体工商户\",\n                \"Name\": \"苏州市平江区大歌星超市\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"RegNo\": \"110105020574345\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京普达鑫投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京普惠思投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"RegNo\": \"110105020574466\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京昌盛四海投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"在业\",\n                \"RegNo\": \"330212600096923\",\n                \"EcoKind\": \"个体工商户\",\n                \"Name\": \"宁波市鄞州钟公庙大歌星自选超市\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"注销\",\n                \"RegNo\": \"320105600200053\",\n                \"EcoKind\": \"个体工商户\",\n                \"Name\": \"南京市建邺区大歌星食品店\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海普思投资有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"注销\",\n                \"RegNo\": \"110107600527132\",\n                \"EcoKind\": \"个体（内地）\",\n                \"Name\": \"北京万达星歌烟酒商店\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万元人民币\",\n                \"RegNo\": \"110105016521299\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京达德厚鑫投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"注销\",\n                \"RegNo\": \"310110600351288\",\n                \"EcoKind\": \"个体\",\n                \"Name\": \"上海市杨浦区大歌星食品综合商店\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegNo\": \"310101000680259\",\n                \"EcoKind\": \"有限责任公司分公司（自然人独资）\",\n                \"Name\": \"北京普思投资有限公司上海分公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"注销\",\n                \"RegNo\": \"310225600370456\",\n                \"EcoKind\": \"个体\",\n                \"Name\": \"上海市浦东新区周浦镇新歌食品商店\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"100万元人民币\",\n                \"RegNo\": \"110105016521346\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京汇德信投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:45\",\n                \"Status\": \"存续\",\n                \"RegNo\": \"330522000184262\",\n                \"EcoKind\": \"个人独资企业\",\n                \"Name\": \"珺娱（湖州）文化发展中心\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"2000万元人民币\",\n                \"RegNo\": \"110105012460853\",\n                \"EcoKind\": \"有限责任公司(自然人独资)\",\n                \"Name\": \"北京普思投资有限公司\"\n            }\n        ],\n        \"CIAForeignOffices\": [\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"Position\": \"执行董事,经理\",\n                \"RegNo\": \"110105020574345\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京普达鑫投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"Position\": \"执行董事,经理\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京普惠思投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10万元人民币\",\n                \"Position\": \"经理,执行董事\",\n                \"RegNo\": \"110105020574466\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京昌盛四海投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划体育文化有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划演出经纪有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"4179.7304万人民币\",\n                \"Position\": \"董事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海网鱼信息科技有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"12884.6436万人民币\",\n                \"Position\": \"董事长\",\n                \"RegNo\": \"310113001373438\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海熊猫互娱文化有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"注销\",\n                \"RegCap\": \"30万\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"610103100001977\",\n                \"EcoKind\": \"有限责任公司(自然人独资)\",\n                \"Name\": \"西安市碑林区大歌星餐饮娱乐有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"注销\",\n                \"RegCap\": \"30万人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"5101042007639\",\n                \"EcoKind\": \"有限责任公司(自然人投资或控股)\",\n                \"Name\": \"成都市锦江区大歌星餐饮娱乐有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"100000万人民币\",\n                \"Position\": \"董事\",\n                \"RegNo\": \"310000400782886\",\n                \"EcoKind\": \"有限责任公司（台港澳法人独资）\",\n                \"Name\": \"飞凡电子商务有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"Position\": \"执行董事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海普思投资有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"注销\",\n                \"Position\": \"\",\n                \"RegNo\": \"110107600527132\",\n                \"EcoKind\": \"个体（内地）\",\n                \"Name\": \"北京万达星歌烟酒商店\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万元人民币\",\n                \"Position\": \"执行董事,经理\",\n                \"RegNo\": \"110105016521299\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京达德厚鑫投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"Position\": \"董事长\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉云集新媒体有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划影视文化有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万元人民币\",\n                \"Position\": \"执行董事\",\n                \"RegNo\": \"440003000137855\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"珠海横琴普斯投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"44000万人民币\",\n                \"Position\": \"董事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股的法人独资）\",\n                \"Name\": \"上海新飞凡电子商务有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:46\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"13860.9431万元人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"110108003312259\",\n                \"EcoKind\": \"其他股份有限公司(非上市)\",\n                \"Name\": \"北京英雄互娱科技股份有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"100000万人民币\",\n                \"Position\": \"董事\",\n                \"EcoKind\": \"其他股份有限公司(非上市)\",\n                \"Name\": \"大连万达集团股份有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"100万元人民币\",\n                \"Position\": \"经理,执行董事\",\n                \"RegNo\": \"110105016521346\",\n                \"EcoKind\": \"其他有限责任公司\",\n                \"Name\": \"北京汇德信投资管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"10000万人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划文化发展有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划音乐有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1111.11万人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海爱洛星食品有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"2000万元人民币\",\n                \"Position\": \"执行董事,经理\",\n                \"RegNo\": \"110105012460853\",\n                \"EcoKind\": \"有限责任公司(自然人独资)\",\n                \"Name\": \"北京普思投资有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1333.3325万人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司（自然人投资或控股）\",\n                \"Name\": \"上海香蕉计划电子游戏有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"注销\",\n                \"RegCap\": \"50.000000万元人民币\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"330212000097826\",\n                \"EcoKind\": \"私营有限责任公司(自然人控股或私营性质企业控股)\",\n                \"Name\": \"宁波市鄞州大歌星餐饮娱乐有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000.000000万人民币\",\n                \"Position\": \"执行董事\",\n                \"RegNo\": \"\",\n                \"EcoKind\": \"有限责任公司\",\n                \"Name\": \"平潭普思资产管理有限公司\"\n            },\n            {\n                \"InputDate\": \"2019-04-14 01:12:47\",\n                \"Status\": \"存续\",\n                \"RegCap\": \"1000万\",\n                \"Position\": \"监事\",\n                \"RegNo\": \"210200000248224\",\n                \"EcoKind\": \"有限责任公司(自然人投资或控股)\",\n                \"Name\": \"蓝泰科技（大连）有限公司\"\n            }\n        ]\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetCiaemployeev4Getstockrelationinfo",
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
            "description": "<p>上诉人信息</p>"
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
            "description": "<p>被上诉人信息</p>"
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
            "field": "fullName",
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
            "field": "fullName",
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
            "field": "fullName",
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
            "field": "fullName",
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
    "url": "/ECICompanyMap/GetStockAnalysisData",
    "title": "企业股权穿透十层接口查询",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "type": "object",
            "optional": false,
            "field": "CompanyData",
            "description": "<p>公司资料</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.TermStart",
            "description": "<p>营业期限自</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.TeamEnd",
            "description": "<p>营业期限至</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.CheckDate",
            "description": "<p>发照日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Name",
            "description": "<p>企业名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.No",
            "description": "<p>注册号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.BelongOrg",
            "description": "<p>所属机构</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.OperName",
            "description": "<p>法人名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.StartDate",
            "description": "<p>成立日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.EndDate",
            "description": "<p>吊销日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Status",
            "description": "<p>状态</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Province",
            "description": "<p>省份代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.UpdatedDate",
            "description": "<p>更新日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.ShortStatus",
            "description": "<p>状态简称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.RegistCapi",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.EconKind",
            "description": "<p>类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Address",
            "description": "<p>地址</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Scope",
            "description": "<p>营业范围</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.OrgNo",
            "description": "<p>组织机构代码</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "CompanyData.Partners",
            "description": "<p>股东信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Partners.CompanyId",
            "description": "<p>公司ID</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Partners.StockName",
            "description": "<p>股东名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Partners.StockType",
            "description": "<p>股东类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Partners.StockPercent",
            "description": "<p>股东持股百分比</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Partners.IdentifyType",
            "description": "<p>证件类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Partners.IdentifyNo",
            "description": "<p>证件号码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Partners.ShouldCapi",
            "description": "<p>出资额（万元）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyData.Partners.ShoudDate",
            "description": "<p>出资日期</p>"
          },
          {
            "group": "Success 200",
            "type": "tree",
            "optional": false,
            "field": "StockList",
            "description": "<p>股东列表</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.KeyNo",
            "description": "<p>KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.Name",
            "description": "<p>企业名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.PathName",
            "description": "<p>投资路径</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.RegistCapi",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.EconKind",
            "description": "<p>企业类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.StockType",
            "description": "<p>股东类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.FundedAmount",
            "description": "<p>出资额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.FundedRate",
            "description": "<p>出资比列</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.InvestType",
            "description": "<p>投资类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockList.Level",
            "description": "<p>层级</p>"
          },
          {
            "group": "Success 200",
            "type": "tree[]",
            "optional": false,
            "field": "StockList.Children",
            "description": "<p>以上字段的子树</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"CompanyData\": {\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"RegistCapi\": \"100000万人民币\",\n            \"No\": null,\n            \"BelongOrg\": \"大连市工商行政管理局\",\n            \"CreditCode\": \"91210200241281392F\",\n            \"OperName\": \"王健林\",\n            \"EconKind\": \"其他股份有限公司(非上市)\",\n            \"Address\": \"辽宁省大连市西岗区长江路539号\",\n            \"UpdatedDate\": \"2018-01-30 06:16:02\",\n            \"OrgNo\": \"24128139-2\",\n            \"EndDate\": null,\n            \"Province\": \"LN\",\n            \"TermStart\": \"1992-09-28 12:00:00\",\n            \"Name\": \"大连万达集团股份有限公司\",\n            \"TeamEnd\": \"2037-09-28 12:00:00\",\n            \"KeyNo\": \"befe52d9753b511b6aef5e33fe00f97d\",\n            \"StartDate\": \"1992-09-28 12:00:00\",\n            \"Scope\": \"商业地产投资及经营、酒店建设投资及经营、连锁百货投资及经营、电影院线等文化产业投资及经营；投资与资产管理、项目管理（以上均不含专项审批）；货物进出口、技术进出口，国内一般贸易；代理记账、财务咨询、企业管理咨询、经济信息咨询、计算机信息技术服务与技术咨询、计算机系统集成、网络设备安装与维护。（依法须经批准的项目，经相关部门批准后，方可开展经营活动）***\",\n            \"CheckDate\": \"2016-05-23 12:00:00\",\n            \"ShortStatus\": \"存续\",\n            \"Partners\": [\n                {\n                    \"ShoudDate\": \"2013-04-03,2013-04-03,2013-04-03\",\n                    \"IdentifyType\": \"企业法人营业执照(公司)\",\n                    \"CompanyId\": \"971e2cafbecd8c978e959d69fc305f42\",\n                    \"StockName\": \"大连合兴投资有限公司\",\n                    \"StockType\": \"企业法人\",\n                    \"IdentifyNo\": \"2102001108389\",\n                    \"StockPercent\": \"99.7600%\",\n                    \"ShouldCapi\": \"88000,3760,8000\"\n                },\n                {\n                    \"ShoudDate\": \"1993-03-15\",\n                    \"IdentifyType\": \"非公示项\",\n                    \"CompanyId\": \"\",\n                    \"StockName\": \"王健林\",\n                    \"StockType\": \"自然人股东\",\n                    \"StockPercent\": \"0.2400%\",\n                    \"ShouldCapi\": \"240\"\n                }\n            ]\n        },\n        \"StockStatistics\": {\n            \"TotalCount\": 5,\n            \"LevelDataList\": [\n                {\n                    \"TotalCount\": 2,\n                    \"Level\": 1\n                },\n                {\n                    \"TotalCount\": 2,\n                    \"Level\": 2\n                }\n            ],\n            \"EconKindDataList\": [\n                {\n                    \"TotalCount\": 2,\n                    \"EconKind\": \"其他股份有限公司(非上市)\"\n                }\n            ],\n            \"StockTypeDataList\": [\n                {\n                    \"TotalCount\": 1,\n                    \"StockType\": \"企业法人\"\n                },\n                {\n                    \"TotalCount\": 3,\n                    \"StockType\": \"自然人股东\"\n                }\n            ]\n        },\n        \"StockList\": {\n            \"KeyNo\": \"befe52d9753b511b6aef5e33fe00f97d\",\n            \"RegistCapi\": \"100000万人民币\",\n            \"EconKind\": \"其他股份有限公司(非上市)\",\n            \"Level\": \"0\",\n            \"PathName\": \"\",\n            \"Children\": [\n                {\n                    \"RegistCapi\": \"7860万人民币\",\n                    \"EconKind\": \"有限责任公司(自然人投资或控股)\",\n                    \"FundedRate\": \"99.7600%\",\n                    \"InvestType\": \"货币,货币,货币\",\n                    \"Name\": \"大连合兴投资有限公司\",\n                    \"KeyNo\": \"971e2cafbecd8c978e959d69fc305f42\",\n                    \"StockType\": \"企业法人\",\n                    \"Level\": \"1\",\n                    \"PathName\": \"大连万达集团股份有限公司\",\n                    \"FundedAmount\": \"88000,3760,8000万元\",\n                    \"Children\": [\n                        {\n                            \"KeyNo\": \"\",\n                            \"StockType\": \"自然人股东\",\n                            \"FundedRate\": \"98.00%\",\n                            \"Level\": \"2\",\n                            \"PathName\": \"大连万达集团股份有限公司/大连合兴投资有限公司\",\n                            \"FundedAmount\": \"7702.8万元\",\n                            \"Children\": [\n                            ],\n                            \"InvestType\": \"货币,货币,货币\",\n                            \"Name\": \"王健林\"\n                        },\n                        {\n                            \"KeyNo\": \"\",\n                            \"StockType\": \"自然人股东\",\n                            \"FundedRate\": \"2.00%\",\n                            \"Level\": \"2\",\n                            \"PathName\": \"大连万达集团股份有限公司/大连合兴投资有限公司\",\n                            \"FundedAmount\": \"157.2万元\",\n                            \"Children\": [\n                            ],\n                            \"InvestType\": \"货币\",\n                            \"Name\": \"王思聪\"\n                        }\n                    ]\n                },\n                {\n                    \"KeyNo\": \"\",\n                    \"StockType\": \"自然人股东\",\n                    \"FundedRate\": \"0.2400%\",\n                    \"Level\": \"1\",\n                    \"PathName\": \"大连万达集团股份有限公司\",\n                    \"FundedAmount\": \"240万元\",\n                    \"Children\": [\n                    ],\n                    \"InvestType\": \"货币\",\n                    \"Name\": \"王健林\"\n                }\n            ],\n            \"Name\": \"大连万达集团股份有限公司\"\n        }\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEcicompanymapGetstockanalysisdata",
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
            "field": "fullName",
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
    "url": "/ECIRelationV4/GenerateMultiDimensionalTreeCompanyMap",
    "title": "企业图谱",
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
            "description": "<p>公司keyNo</p>"
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
            "field": "Name",
            "description": "<p>名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "KeyNo",
            "description": "<p>内部KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Category",
            "description": "<p>1:当前公司2：对外投资3：股东4：高管5：法院公告6：裁判文书8：历史股东9：历史法人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ShortName",
            "description": "<p>简称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Count",
            "description": "<p>数量</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Level",
            "description": "<p>层级</p>"
          },
          {
            "group": "Success 200",
            "type": "node[]",
            "optional": false,
            "field": "Children",
            "description": "<p>以上字段的树结构</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e\",\n        \"Category\": \"1\",\n        \"Level\": \"0\",\n        \"ShortName\": \"北京小桔\",\n        \"Count\": \"79\",\n        \"Children\": [\n            {\n                \"Category\": \"2\",\n                \"Level\": \"0\",\n                \"ShortName\": \"对外投资\",\n                \"Count\": \"23\",\n                \"Children\": [\n                    {\n                        \"KeyNo\": \"afb3daf1797df272997f22b143c964f6\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"北京车胜\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京车胜科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"274d9311979595d54821e1d9e8d73e36\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"北京再造\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京再造科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"7bb231394fe204e1a3c3e6cfdb24ec21\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴滴旅行\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"苏州滴滴旅行科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"a1b4b72b29c862926c7e715c365640c8\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"杭州青奇\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州青奇科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"6de14b4eeddfad4c9d320e7635c664e5\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"小木吉软件\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州小木吉软件科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"aa0eb37b66413114095520caa0c15961\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"运达无限\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京运达无限科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"3fe7fa121a61e0d869a52b4752b9e272\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴滴汽车服务\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州滴滴汽车服务有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"eb8957f85861e53f023ac63a419a2ce4\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"天津舒行\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"天津舒行科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"39b198dc9b68b3958e21594e4071cdf5\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"橙资互联网\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"橙资（上海）互联网科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"05c090155e36541c83e9ab59ab3f402d\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"橙子投资\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"嘉兴橙子投资管理有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"66658d9633f7002768bbacbd02efb226\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"桔子共享投资\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"嘉兴桔子共享投资合伙企业（有限合伙）\"\n                    },\n                    {\n                        \"KeyNo\": \"3049b862cd42fe8cb946497bd075ad20\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"小桔子投资合\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"嘉兴小桔子投资合伙企业（有限合伙）\"\n                    },\n                    {\n                        \"KeyNo\": \"94c0f5d6508919e29aff5a66f86ebca1\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴图\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"滴图（北京）科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"9376917a29748c8a7590d16fa01d3e7c\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"上海桔道\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"上海桔道网络科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"d1c68c75aedf702f2422bebf07b1bd2b\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"北岸商业保理\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"深圳北岸商业保理有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"44d5992e16ff513c91f86c5b0fdf2227\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴滴出行\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"滴滴出行科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"e108693960d310ee9e5d663c0f3227ca\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴滴商业服务\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"滴滴商业服务有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"d92b9c60a5e4b3456812eb0a3e4bba63\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"博通畅达\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京博通畅达科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"8bd250d6875caa56dc9a6747b49689c0\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"吾步信息技术\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"上海吾步信息技术有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"307efc57070d09ba32b8f01ee1322647\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"北京长亭\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京长亭科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"3a079aa5beaf85378a2dba72ec6d563a\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"通达无限\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京通达无限科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"2bbaaaf09d9877b8dd851a02ad9600a2\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"奇漾信息技术\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"上海奇漾信息技术有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"c02337970cc15c084571cf1c982f8e22\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"杭州快智\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州快智科技有限公司\"\n                    }\n                ],\n                \"Name\": \"对外投资\"\n            },\n            {\n                \"Category\": \"3\",\n                \"Level\": \"0\",\n                \"ShortName\": \"股东\",\n                \"Count\": \"5\",\n                \"Children\": [\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_王刚\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"王刚\"\n                    },\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_张博\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"张博\"\n                    },\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_程维\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"程维\"\n                    },\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_陈汀\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"陈汀\"\n                    },\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_吴睿\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"吴睿\"\n                    }\n                ],\n                \"Name\": \"股东\"\n            },\n            {\n                \"Category\": \"4\",\n                \"Level\": \"0\",\n                \"ShortName\": \"高管\",\n                \"Count\": \"3\",\n                \"Children\": [\n                    {\n                        \"Category\": \"4\",\n                        \"Level\": \"0\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"程维\"\n                    },\n                    {\n                        \"Category\": \"4\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"经理\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"程维\"\n                    },\n                    {\n                        \"Category\": \"4\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"监事\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"吴睿\"\n                    }\n                ],\n                \"Name\": \"高管\"\n            },\n            {\n                \"Category\": \"8\",\n                \"Level\": \"0\",\n                \"ShortName\": \"历史股东\",\n                \"Count\": \"1\",\n                \"Children\": [\n                    {\n                        \"Category\": \"8\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"徐涛\"\n                    }\n                ],\n                \"Name\": \"历史股东\"\n            },\n            {\n                \"Category\": \"9\",\n                \"Level\": \"0\",\n                \"ShortName\": \"历史法人\",\n                \"Count\": \"0\",\n                \"Children\": [\n                ],\n                \"Name\": \"历史法人\"\n            },\n            {\n                \"Category\": \"6\",\n                \"Level\": \"0\",\n                \"ShortName\": \"裁判文书\",\n                \"Count\": \"47\",\n                \"Children\": [\n                    {\n                        \"KeyNo\": \"7c38ac16b20be711a8f8825a11323db6\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京畅行信息技术有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"0d780bcdc7ad871d824c7dce51973af0\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"武汉兴广亚汽车租赁有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"44d5992e16ff513c91f86c5b0fdf2227\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"滴滴出行科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"ccb6c71c2be0ad917d194e34b1b8292f\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京嘀嘀无限科技发展有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"5a8ee7ebaa5c091e328f0693e204beb9\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"高德信息技术有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"b38d90ee05f8c4f6a2a800d44ef12355\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"高德软件有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"1a8ec2bd97cbe5940f2c234976f4f42c\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中智项目外包服务有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"48d4019cbee2e41ba614205894848661\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京东方车云信息技术有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"f289e491413cfc3846b16f5eca46634b\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"宁波市科技园区妙影电子有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"149e40dd5827381f410076824625f872\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州妙影微电子有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"cbdf2d8f1404d5c43bdf67a932460098\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"深圳市唐氏龙行汽车租赁有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"acea4bb4a5bd3698caa3bd5d1aea6cdd\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人民财产保险股份有限公司深圳市分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"73dc248532ff345f11d5dd8ac2fd8278\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京万古恒信科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"dfb69ffd668429fa619d1fbee9ac4965\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"比亚迪汽车工业有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"bbccdb715f39775aa18016d6d3e8bbd1\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"深圳市迪滴新能源汽车租赁有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"bedd0b1c57c8ea2e4e2f1c57a5683165\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国平安财产保险股份有限公司深圳市龙岗支公司\"\n                    },\n                    {\n                        \"KeyNo\": \"4d109516d62f4dd04eb8942460c330d6\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人民财产保险股份有限公司泉州市分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"dcad4bc1a5a4de2c7be5b6be79e80c0e\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国平安财产保险股份有限公司陕西分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"3f1930ed2fe91302978aee2b273231f9\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中华联合财产保险股份有限公司西安中心支公司\"\n                    },\n                    {\n                        \"KeyNo\": \"1d780a0fcddf561272c35dffbe213319\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"西安志华土方工程有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"aca913da8a64ac18db164eef3543528f\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"长春金城汽车贸易有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"69b0b3cba566d2be56b45934c39c65f0\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国平安财产保险股份有限公司四川分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"2a835b4eb8a584ae34f878b33460fead\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人民财产保险股份有限公司金华市分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"86d75562aa40a2b085089fc77ccbba61\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"华泰财产保险有限公司深圳分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"44ee722fd23e701019c60d1adb314315\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国平安财产保险股份有限公司深圳分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"d0328234128aa5cdea42395c34bd8c5c\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"大连百名汽车租赁有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"0c0d20305779153c5c20002c1e806770\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"浙江外企德科人力资源服务有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"c02337970cc15c084571cf1c982f8e22\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州快智科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"6d82400bcb5d4944e53080a521100913\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国平安财产保险股份有限公司浙江分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"c290851899ee5306fda9706fed8ffa61\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国太平洋财产保险股份有限公司西安中心支公司\"\n                    },\n                    {\n                        \"KeyNo\": \"e7da6d63073b887c3885ada53fa22e7d\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人民财产保险股份有限公司成都市分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"3239389ea6405e28bbf466ea0631e4df\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人民财产保险股份有限公司郑州市分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"f59629208c9f6088b0eef880267ead76\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"太平财产保险有限公司郑州分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"8dd7a2fe01fe2b5e6e8143d68440353d\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"华安财产保险股份有限公司郑州中心支公司\"\n                    },\n                    {\n                        \"KeyNo\": \"6ddf5e6cdcf645f670c7c0d8a8de0433\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"华泰财产保险有限公司北京分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"59def76b4adae88ebd768b46272f8241\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"首汽租赁有限责任公司\"\n                    },\n                    {\n                        \"KeyNo\": \"6b0f0f3f784d8cc1bf339cfc55f4dc96\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人民财产保险股份有限公司苏州市太湖国家旅游度假区支公司\"\n                    },\n                    {\n                        \"KeyNo\": \"gfffa08d683fd30a69de11f216de1776\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州整形医院\"\n                    },\n                    {\n                        \"KeyNo\": \"1e3c58e9518324d55bca783423aafa94\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"永诚财产保险股份有限公司双流支公司\"\n                    },\n                    {\n                        \"KeyNo\": \"e78d6de329b6dc6a34be158f968a639b\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人寿财产保险股份有限公司深圳市分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"3a079aa5beaf85378a2dba72ec6d563a\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京通达无限科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"1f12f7d5879a7249181eab15abbb4969\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"熹锦实业(上海)有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"1f12f7d5879a7249181eab15abbb4969\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"熹锦实业（上海）有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"a2c4b70a848e7cf3beaf1406839c0d4d\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人民财产保险股份有限公司扬州市分公司\"\n                    },\n                    {\n                        \"KeyNo\": \"d53d1d2759b4903253c0e9910e890eac\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国石化集团江苏石油勘探局\"\n                    },\n                    {\n                        \"KeyNo\": \"6ebe7ad82a3a3d2eb886b429edafa6b4\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国太平洋财产保险股份有限公司扬州中心支公司\"\n                    },\n                    {\n                        \"KeyNo\": \"80cd056a31c603ca594f27a05a8fa616\",\n                        \"Category\": \"6\",\n                        \"Level\": \"0\",\n                        \"ShortName\": \"北京小桔\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"中国人寿财产保险股份有限公司阜阳市颍州区支公司\"\n                    }\n                ],\n                \"Name\": \"裁判文书\"\n            },\n            {\n                \"Category\": \"5\",\n                \"Level\": \"0\",\n                \"ShortName\": \"法院公告\",\n                \"Count\": \"0\",\n                \"Children\": [\n                ],\n                \"Name\": \"法院公告\"\n            }\n        ],\n        \"Name\": \"北京小桔科技有限公司\"\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEcirelationv4Generatemultidimensionaltreecompanymap",
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
    "url": "/ECIRelationV4/GetCompanyEquityShareMap",
    "title": "股权结构图",
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
            "description": "<p>公司keyNo</p>"
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
            "field": "Name",
            "description": "<p>公司名称或者人名</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "KeyNo",
            "description": "<p>当前股东的公司keyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Category",
            "description": "<p>1是公司，2是个人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockType",
            "description": "<p>股东类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Count",
            "description": "<p>对应的childrencount</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "FundedRatio",
            "description": "<p>出资比例</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "SubConAmt",
            "description": "<p>出资金额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "IsAbsoluteController",
            "description": "<p>是否绝对控股</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Grade",
            "description": "<p>对应的层级</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OperName",
            "description": "<p>法人代表</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "InParentActualRadio",
            "description": "<p>当前股东所在公司在该公司父级中所占实际比例</p>"
          },
          {
            "group": "Success 200",
            "type": "node[]",
            "optional": false,
            "field": "Children",
            "description": "<p>以上所有字段的树结构</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "ActualControllerLoopPath",
            "description": "<p>实际控股信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ActualControllerLoopPath.Name",
            "description": "<p>实际控股名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ActualControllerLoopPath.StockType",
            "description": "<p>实际控股类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ActualControllerLoopPath.KeyNo",
            "description": "<p>公司keyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ActualControllerLoopPath.SubConAmt",
            "description": "<p>出资额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ActualControllerLoopPath.FundedRatio",
            "description": "<p>出资比例</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e\",\n        \"OperName\": \"程维\",\n        \"ActualControllerLoopPath\": [\n            {\n                \"KeyNo\": \"\",\n                \"FundedRatio\": \"49.1900%\",\n                \"SubConAmt\": \"491.9万元\",\n                \"StockType\": \"自然人股东\",\n                \"Name\": \"程维\"\n            }\n        ],\n        \"InParentActualRadio\": \"0\",\n        \"Category\": \"1\",\n        \"FundedRatio\": \"100%\",\n        \"Grade\": \"1\",\n        \"IsAbsoluteController\": \"True\",\n        \"Count\": \"5\",\n        \"Children\": [\n            {\n                \"KeyNo\": \"7B0CFF16CA8BA1EC_\",\n                \"InParentActualRadio\": \"0.4919\",\n                \"Category\": \"2\",\n                \"FundedRatio\": \"49.1900%\",\n                \"Grade\": \"2\",\n                \"IsAbsoluteController\": \"True\",\n                \"Count\": \"0\",\n                \"Children\": [\n                ],\n                \"Name\": \"程维\"\n            },\n            {\n                \"InParentActualRadio\": \"0.48225\",\n                \"Category\": \"2\",\n                \"FundedRatio\": \"48.2250%\",\n                \"Grade\": \"2\",\n                \"IsAbsoluteController\": \"False\",\n                \"Count\": \"0\",\n                \"Children\": [\n                ],\n                \"Name\": \"王刚\"\n            },\n            {\n                \"InParentActualRadio\": \"0.01553\",\n                \"Category\": \"2\",\n                \"FundedRatio\": \"1.5530%\",\n                \"Grade\": \"2\",\n                \"IsAbsoluteController\": \"False\",\n                \"Count\": \"0\",\n                \"Children\": [\n                ],\n                \"Name\": \"张博\"\n            },\n            {\n                \"InParentActualRadio\": \"0.00723\",\n                \"Category\": \"2\",\n                \"FundedRatio\": \"0.7230%\",\n                \"Grade\": \"2\",\n                \"IsAbsoluteController\": \"False\",\n                \"Count\": \"0\",\n                \"Children\": [\n                ],\n                \"Name\": \"吴睿\"\n            },\n            {\n                \"InParentActualRadio\": \"0.00309\",\n                \"Category\": \"2\",\n                \"FundedRatio\": \"0.3090%\",\n                \"Grade\": \"2\",\n                \"IsAbsoluteController\": \"False\",\n                \"Count\": \"0\",\n                \"Children\": [\n                ],\n                \"Name\": \"陈汀\"\n            }\n        ],\n        \"Name\": \"北京小桔科技有限公司\"\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEcirelationv4Getcompanyequitysharemap",
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
    "url": "/ECIRelationV4/SearchTreeRelationMap",
    "title": "投资图谱",
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
            "description": "<p>公司keyNo</p>"
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
            "field": "Name",
            "description": "<p>名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "KeyNo",
            "description": "<p>内部KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Category",
            "description": "<p>数据类别，1(当前公司)，2(对外投资)，3(股东)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ShortName",
            "description": "<p>简称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Count",
            "description": "<p>数量</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Level",
            "description": "<p>层级</p>"
          },
          {
            "group": "Success 200",
            "type": "node[]",
            "optional": false,
            "field": "Children",
            "description": "<p>树结构，包含以上字段</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e\",\n        \"Category\": \"1\",\n        \"Level\": \"0\",\n        \"ShortName\": \"北京小桔\",\n        \"Count\": \"28\",\n        \"Children\": [\n            {\n                \"Category\": \"3\",\n                \"Level\": \"0\",\n                \"ShortName\": \"股东\",\n                \"Count\": \"5\",\n                \"Children\": [\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_王刚\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"王刚\"\n                    },\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_张博\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"张博\"\n                    },\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_程维\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"程维\"\n                    },\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_陈汀\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"陈汀\"\n                    },\n                    {\n                        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e_吴睿\",\n                        \"Category\": \"3\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"自然人股东\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"吴睿\"\n                    }\n                ],\n                \"Name\": \"股东\"\n            },\n            {\n                \"Category\": \"2\",\n                \"Level\": \"0\",\n                \"ShortName\": \"对外投资\",\n                \"Count\": \"23\",\n                \"Children\": [\n                    {\n                        \"KeyNo\": \"afb3daf1797df272997f22b143c964f6\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"北京车胜\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京车胜科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"274d9311979595d54821e1d9e8d73e36\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"北京再造\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京再造科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"7bb231394fe204e1a3c3e6cfdb24ec21\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴滴旅行\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"苏州滴滴旅行科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"a1b4b72b29c862926c7e715c365640c8\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"杭州青奇\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州青奇科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"6de14b4eeddfad4c9d320e7635c664e5\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"小木吉软件\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州小木吉软件科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"aa0eb37b66413114095520caa0c15961\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"运达无限\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京运达无限科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"3fe7fa121a61e0d869a52b4752b9e272\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴滴汽车服务\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州滴滴汽车服务有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"eb8957f85861e53f023ac63a419a2ce4\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"天津舒行\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"天津舒行科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"39b198dc9b68b3958e21594e4071cdf5\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"橙资互联网\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"橙资（上海）互联网科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"05c090155e36541c83e9ab59ab3f402d\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"橙子投资\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"嘉兴橙子投资管理有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"66658d9633f7002768bbacbd02efb226\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"桔子共享投资\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"嘉兴桔子共享投资合伙企业（有限合伙）\"\n                    },\n                    {\n                        \"KeyNo\": \"3049b862cd42fe8cb946497bd075ad20\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"小桔子投资合\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"嘉兴小桔子投资合伙企业（有限合伙）\"\n                    },\n                    {\n                        \"KeyNo\": \"94c0f5d6508919e29aff5a66f86ebca1\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴图\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"滴图（北京）科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"9376917a29748c8a7590d16fa01d3e7c\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"上海桔道\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"上海桔道网络科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"d1c68c75aedf702f2422bebf07b1bd2b\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"北岸商业保理\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"深圳北岸商业保理有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"44d5992e16ff513c91f86c5b0fdf2227\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴滴出行\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"滴滴出行科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"e108693960d310ee9e5d663c0f3227ca\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"滴滴商业服务\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"滴滴商业服务有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"d92b9c60a5e4b3456812eb0a3e4bba63\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"博通畅达\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京博通畅达科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"8bd250d6875caa56dc9a6747b49689c0\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"吾步信息技术\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"上海吾步信息技术有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"307efc57070d09ba32b8f01ee1322647\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"北京长亭\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京长亭科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"3a079aa5beaf85378a2dba72ec6d563a\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"通达无限\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"北京通达无限科技有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"2bbaaaf09d9877b8dd851a02ad9600a2\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"奇漾信息技术\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"上海奇漾信息技术有限公司\"\n                    },\n                    {\n                        \"KeyNo\": \"c02337970cc15c084571cf1c982f8e22\",\n                        \"Category\": \"2\",\n                        \"Level\": \"1\",\n                        \"ShortName\": \"杭州快智\",\n                        \"Count\": \"0\",\n                        \"Children\": [\n                        ],\n                        \"Name\": \"杭州快智科技有限公司\"\n                    }\n                ],\n                \"Name\": \"对外投资\"\n            }\n        ],\n        \"Name\": \"北京小桔科技有限公司\"\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEcirelationv4Searchtreerelationmap",
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
    "url": "/ECIV4/GetDetailsByName",
    "title": "企业关键字精确获取详细信息(Master)",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "field": "KeyNo",
            "description": "<p>内部KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Name",
            "description": "<p>公司名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "No",
            "description": "<p>注册号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "BelongOrg",
            "description": "<p>登记机关</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OperName",
            "description": "<p>法人名</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StartDate",
            "description": "<p>成立日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EndDate",
            "description": "<p>吊销日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Status",
            "description": "<p>企业状态</p>"
          },
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
            "field": "UpdatedDate",
            "description": "<p>更新日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditCode",
            "description": "<p>社会统一信用代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegistCapi",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EconKind",
            "description": "<p>企业类型</p>"
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
            "field": "Scope",
            "description": "<p>经营范围</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "TermStart",
            "description": "<p>营业开始日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "TeamEnd",
            "description": "<p>营业结束日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CheckDate",
            "description": "<p>发照日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgNo",
            "description": "<p>组织机构代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "IsOnStock",
            "description": "<p>是否上市(0为未上市，1为上市)</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockNumber",
            "description": "<p>上市公司代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockType",
            "description": "<p>上市类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ImageUrl",
            "description": "<p>企业Logo</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "OriginalName",
            "description": "<p>曾用名</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OriginalName.Name",
            "description": "<p>曾用名</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OriginalName.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Partners",
            "description": "<p>股东信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.StockName",
            "description": "<p>股东</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.StockType",
            "description": "<p>股东类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.StockPercent",
            "description": "<p>出资比例</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.ShouldCapi",
            "description": "<p>认缴出资额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.ShoudDate",
            "description": "<p>认缴出资时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.InvestType",
            "description": "<p>认缴出资方式</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.InvestName",
            "description": "<p>实际出资方式</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.RealCapi",
            "description": "<p>实缴出资额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Partners.CapiDate",
            "description": "<p>实缴时间</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Employees",
            "description": "<p>主要人员</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Employees.Name",
            "description": "<p>姓名</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Employees.Job",
            "description": "<p>职位</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Branches",
            "description": "<p>分支机构</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Branches.CompanyId",
            "description": "<p>CompanyId</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Branches.RegNo",
            "description": "<p>注册号或社会统一信用代码（存在社会统一信用代码显示社会统一信用代码，否则显示注册号）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Branches.Name",
            "description": "<p>名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Branches.BelongOrg",
            "description": "<p>登记机关</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Branches.CreditCode",
            "description": "<p>社会统一信用代码（保留字段，目前为空）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Branches.OperName",
            "description": "<p>法人姓名或负责人姓名（保留字段，目前为空）</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "ChangeRecords",
            "description": "<p>变更信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ChangeRecords.ProjectName",
            "description": "<p>变更事项</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ChangeRecords.BeforeContent",
            "description": "<p>变更前内容</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ChangeRecords.AfterContent",
            "description": "<p>变更后内容</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ChangeRecords.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "ContactInfo",
            "description": "<p>联系信息</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "ContactInfo.WebSite",
            "description": "<p>网址信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ContactInfo.WebSite.Name",
            "description": "<p>网站名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ContactInfo.WebSite.Url",
            "description": "<p>网站地址</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ContactInfo.PhoneNumber",
            "description": "<p>联系电话</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ContactInfo.Email",
            "description": "<p>联系邮箱</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "Industry",
            "description": "<p>行业信息</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Industry.IndustryCode",
            "description": "<p>行业门类code</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Industry.Industry",
            "description": "<p>行业门类描述</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Industry.SubIndustryCode",
            "description": "<p>行业大类code</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Industry.SubIndustry",
            "description": "<p>行业大类描述</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Industry.MiddleCategoryCode",
            "description": "<p>行业中类code</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Industry.MiddleCategory",
            "description": "<p>行业中类描述</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Industry.SmallCategoryCode",
            "description": "<p>行业小类code</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Industry.SmallCategory",
            "description": "<p>行业小类描述</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"RegistCapi\": \"100万元人民币\",\n        \"BelongOrg\": \"深圳市市场监督管理局\",\n        \"CreditCode\": \"91440300786561802R\",\n        \"EconKind\": \"有限责任公司\",\n        \"Address\": \"深圳市福田区梅林街道梅丰社区梅华路105号多丽工业区3栋4层402E房\",\n        \"UpdatedDate\": null,\n        \"Employees\": [\n            {\n                \"Job\": \"执行董事\",\n                \"Name\": \"陈海文\"\n            },\n            {\n                \"Job\": \"监事\",\n                \"Name\": \"黄坚\"\n            },\n            {\n                \"Job\": \"总经理\",\n                \"Name\": \"陈海文\"\n            }\n        ],\n        \"Name\": \"深圳市桑协世纪科技有限公司\",\n        \"StartDate\": \"2006-03-17 12:00:00\",\n        \"Industry\": {\n            \"Industry\": \"科学研究和技术服务业\",\n            \"SubIndustryCode\": \"75\",\n            \"IndustryCode\": \"M\",\n            \"MiddleCategory\": \"其他科技推广服务业\",\n            \"SmallCategoryCode\": \"7590\",\n            \"SmallCategory\": \"其他科技推广服务业\",\n            \"SubIndustry\": \"科技推广和应用服务业\",\n            \"MiddleCategoryCode\": \"759\"\n        },\n        \"StockType\": null,\n        \"ChangeRecords\": [\n            {\n                \"ProjectName\": \"章程备案\",\n                \"ChangeDate\": \"2019-03-11 12:00:00\",\n                \"AfterContent\": \"2019-03-07\",\n                \"BeforeContent\": \"2018-12-19\"\n            },\n            {\n                \"ProjectName\": \"名称变更（字号名称、集团名称等）\",\n                \"ChangeDate\": \"2019-03-11 12:00:00\",\n                \"AfterContent\": \"深圳市桑协世纪科技有限公司\",\n                \"BeforeContent\": \"深圳市康银信息技术有限公司\"\n            },\n            {\n                \"ProjectName\": \"名称变更（字号名称、集团名称等）\",\n                \"ChangeDate\": \"2018-12-21 12:00:00\",\n                \"AfterContent\": \"深圳市康银信息技术有限公司\",\n                \"BeforeContent\": \"深圳市桑协世纪科技有限公司\"\n            },\n            {\n                \"ProjectName\": \"章程备案\",\n                \"ChangeDate\": \"2018-12-21 12:00:00\",\n                \"AfterContent\": \"2018-12-19\",\n                \"BeforeContent\": \"2018-10-31\"\n            },\n            {\n                \"ProjectName\": \"地址变更（住所地址、经营场所、驻在地址等变更）\",\n                \"ChangeDate\": \"2018-11-02 12:00:00\",\n                \"AfterContent\": \"深圳市福田区梅林街道梅丰社区梅华路105号多丽工业区3栋4层402E房\",\n                \"BeforeContent\": \"深圳市福田区华强北街道鹏基上步工业区101栋第五层516室(入驻深圳市网协商务秘书有限公司)\"\n            },\n            {\n                \"ProjectName\": \"\",\n                \"ChangeDate\": \"2018-11-02 12:00:00\",\n                \"AfterContent\": \"计算机软件、信息系统软件的开发、销售；信息系统设计、集成、运行维护；信息技术咨询；集成电路设计、研发；通信线路和设备安装；电子设备工程安装；电子自动化工程安装；监控系统安装；保安监控及防盗报警系统安装；智能卡系统安装；电子工程安装；智能化系统安装；建筑物空调设备、采暖系统、通风设备系统安装；机电设备安装、维修；门窗安装；电工维修；木工维修；管道工维修；计算机、软件及辅助设备的销售；通讯设备的销售；j计算机系统集成；无线数据产品(不含限制项目)的销售。(法律、行政法规、国务院决定禁止的项目除外,限制的项目须取得许可后方可经营)\",\n                \"BeforeContent\": \"通信线路和设备安装；电子设备工程安装；电子自动化工程安装；监控系统安装；保安监控及防盗报警系统安装；智能卡系统安装；电子工程安装；智能化系统安装；建筑物空调设备、采暖系统、通风设备系统安装；机电设备安装、维修；门窗安装；电工维修；木工维修；管道工维修。计算机、软件及辅助设备的销售。通讯设备的销售；系统集成及无线数据产品(不含限制项目)的销售。\"\n            },\n            {\n                \"ProjectName\": \"章程备案\",\n                \"ChangeDate\": \"2018-11-02 12:00:00\",\n                \"AfterContent\": \"2018-10-31\",\n                \"BeforeContent\": \"2017-03-06\"\n            },\n            {\n                \"ProjectName\": \"章程备案\",\n                \"ChangeDate\": \"2017-03-08 12:00:00\",\n                \"AfterContent\": \"2017-03-06\",\n                \"BeforeContent\": \"2016-05-06\"\n            },\n            {\n                \"ProjectName\": \"\",\n                \"ChangeDate\": \"2017-03-08 12:00:00\",\n                \"AfterContent\": \"通信线路和设备安装；电子设备工程安装；电子自动化工程安装；监控系统安装；保安监控及防盗报警系统安装；智能卡系统安装；电子工程安装；智能化系统安装；建筑物空调设备、采暖系统、通风设备系统安装；机电设备安装、维修；门窗安装；电工维修；木工维修；管道工维修。计算机、软件及辅助设备的销售。通讯设备的销售；系统集成及无线数据产品(不含限制项目)的销售。\",\n                \"BeforeContent\": \"电子产品的技术开发、上门维修,信息咨询(以上不含人才中介服务及其它限制项目)。\"\n            },\n            {\n                \"ProjectName\": \"其他事项备案\",\n                \"ChangeDate\": \"2016-05-10 12:00:00\",\n                \"AfterContent\": \"91440300786561802R\",\n                \"BeforeContent\": \"\"\n            },\n            {\n                \"ProjectName\": \"期限变更（经营期限、营业期限、驻在期限等变更）\",\n                \"ChangeDate\": \"2016-05-10 12:00:00\",\n                \"AfterContent\": \"2006-03-17,5000-01-01\",\n                \"BeforeContent\": \"2006-03-17,2016-03-17\"\n            },\n            {\n                \"ProjectName\": \"地址变更（住所地址、经营场所、驻在地址等变更）\",\n                \"ChangeDate\": \"2016-05-10 12:00:00\",\n                \"AfterContent\": \"深圳市福田区华强北街道鹏基上步工业区101栋第五层516室(入驻深圳市网协商务秘书有限公司)\",\n                \"BeforeContent\": \"深圳市福田区梅华路105号福田国际电子商务产业园1栋1422室\"\n            },\n            {\n                \"ProjectName\": \"地址变更（住所地址、经营场所、驻在地址等变更）\",\n                \"ChangeDate\": \"2015-06-11 12:00:00\",\n                \"AfterContent\": \"深圳市福田区梅华路105号福田国际电子商务产业园1栋1422室\",\n                \"BeforeContent\": \"深圳市福田区振华路(东)兰光大厦C座312房\"\n            },\n            {\n                \"ProjectName\": \"期限变更（经营期限、营业期限、驻在期限等变更）\",\n                \"ChangeDate\": \"2016-05-10 12:00:00\",\n                \"AfterContent\": \"永续经营\",\n                \"BeforeContent\": \"从2006-03-17至2016-03-17\"\n            },\n            {\n                \"ProjectName\": \"指定联系人\",\n                \"ChangeDate\": \"2015-06-11 12:00:00\",\n                \"AfterContent\": \"陈海文*\",\n                \"BeforeContent\": \"\"\n            },\n            {\n                \"ProjectName\": \"经营范围变更（含业务范围变更）\",\n                \"ChangeDate\": \"2010-05-10 12:00:00\",\n                \"AfterContent\": \"电子产品的技术开发、上门维修,信息咨询(以上不含人才中介服务及其它限制项目)。\",\n                \"BeforeContent\": \"电子及信息产品的技术开发及咨询(不含限制项目)；国内商业、物资供销业(不含专营、专控、专卖商品)；兴办实业(具体项目另行申报)。\"\n            },\n            {\n                \"ProjectName\": \"注册号/注册号升级\",\n                \"ChangeDate\": \"2008-12-11 12:00:00\",\n                \"AfterContent\": \"440301103762313\",\n                \"BeforeContent\": \"4403011216989\"\n            },\n            {\n                \"ProjectName\": \"地址变更（住所地址、经营场所、驻在地址等变更）\",\n                \"ChangeDate\": \"2008-12-11 12:00:00\",\n                \"AfterContent\": \"深圳市福田区振华路(东)兰光大厦C座312房\",\n                \"BeforeContent\": \"深圳市福田区燕南路403栋399A\"\n            },\n            {\n                \"ProjectName\": \"地址变更（住所地址、经营场所、驻在地址等变更）\",\n                \"ChangeDate\": \"2007-06-19 12:00:00\",\n                \"AfterContent\": \"深圳市福田区燕南路403栋399A\",\n                \"BeforeContent\": \"深圳市福田区振华路(东)兰光大厦C座307房\"\n            },\n            {\n                \"ProjectName\": \"名称变更（字号名称、集团名称等）\",\n                \"ChangeDate\": \"2007-03-19 12:00:00\",\n                \"AfterContent\": \"深圳市桑协世纪科技有限公司\",\n                \"BeforeContent\": \"深圳市辰光伟业科技有限公司\"\n            },\n            {\n                \"ProjectName\": \"地址变更（住所地址、经营场所、驻在地址等变更）\",\n                \"ChangeDate\": \"2006-08-18 12:00:00\",\n                \"AfterContent\": \"深圳市福田区振华路(东)兰光大厦C座307房\",\n                \"BeforeContent\": \"深圳市福田区华强北路2006号华联发大厦1023号\"\n            },\n            {\n                \"ProjectName\": \"经营范围\",\n                \"ChangeDate\": \"2017-03-08 12:00:00\",\n                \"AfterContent\": \"通信线路和设备安装；电子设备工程安装；电子自动化工程安装；监控系统安装；保安监控及防盗报警系统安装；智能卡系统安装；电子工程安装；智能化系统安装；建筑物空调设备、采暖系统、通风设备系统安装；机电设备安装、维修；门窗安装；电工维修；木工维修；管道工维修。计算机、软件及辅助设备的销售。通讯设备的销售；系统集成及无线数据产品(不含限制项目)的销售。^\",\n                \"BeforeContent\": \"电子产品的技术开发、上门维修,信息咨询(以上不含人才中介服务及其它限制项目)。^\"\n            },\n            {\n                \"ProjectName\": \"审批项目\",\n                \"ChangeDate\": \"2017-03-08 12:00:00\",\n                \"AfterContent\": \"验资报告深中法验字[2006]第B036号\",\n                \"BeforeContent\": \"验资报告深中法验字[2006]第B036号\"\n            },\n            {\n                \"ProjectName\": \"经营期限\",\n                \"ChangeDate\": \"2016-05-10 12:00:00\",\n                \"AfterContent\": \"永续经营\",\n                \"BeforeContent\": \"自2006年3月17日起至2016年3月17日止\"\n            },\n            {\n                \"ProjectName\": \"指定联系人\",\n                \"ChangeDate\": \"2015-06-11 12:00:00\",\n                \"AfterContent\": \"姓名:电话:邮箱:\",\n                \"BeforeContent\": \"姓名:电话:邮箱:\"\n            }\n        ],\n        \"CheckDate\": \"2019-03-11 12:00:00\",\n        \"ContactInfo\": {\n            \"Email\": \"840019811@qq.com\",\n            \"WebSite\": {\n            },\n            \"PhoneNumber\": \"0755-83314237\"\n        },\n        \"Status\": \"存续（在营、开业、在册）\",\n        \"No\": \"440301103762313\",\n        \"OperName\": \"陈海文\",\n        \"Branches\": [\n        ],\n        \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n        \"OrgNo\": \"78656180-2\",\n        \"OriginalName\": [\n            {\n                \"ChangeDate\": \"2019-03-11 12:00:00\",\n                \"Name\": \"深圳市康银信息技术有限公司\"\n            },\n            {\n                \"ChangeDate\": \"2007-03-19 12:00:00\",\n                \"Name\": \"深圳市辰光伟业科技有限公司\"\n            }\n        ],\n        \"EndDate\": null,\n        \"Province\": \"GD\",\n        \"TermStart\": \"2006-03-17 12:00:00\",\n        \"KeyNo\": \"692a8d87536443b042bccb655398e3a0\",\n        \"TeamEnd\": null,\n        \"Partners\": [\n            {\n                \"StockName\": \"陈海文\",\n                \"StockType\": \"自然人股东\",\n                \"StockPercent\": \"55.00%\",\n                \"ShouldCapi\": \"55\",\n                \"InvestType\": \"\"\n            },\n            {\n                \"StockName\": \"黄坚\",\n                \"StockType\": \"自然人股东\",\n                \"StockPercent\": \"45.00%\",\n                \"ShouldCapi\": \"45\",\n                \"InvestType\": \"\"\n            }\n        ],\n        \"Scope\": \"计算机软件、信息系统软件的开发、销售;信息系统设计、集成、运行维护;信息技术咨询;集成电路设计、研发;通信线路和设备安装;电子设备工程安装;电子自动化工程安装;监控系统安装;保安监控及防盗报警系统安装;智能卡系统安装;电子工程安装;智能化系统安装;建筑物空调设备、采暖系统、通风设备系统安装;机电设备安装、维修;门窗安装;电工维修;木工维修;管道工维修;计算机、软件及辅助设备的销售;通讯设备的销售;j计算机系统集成;无线数据产品(不含限制项目)的销售。(法律、行政法规、国务院决定禁止的项目除外,限制的项目须取得许可后方可经营)\",\n        \"IsOnStock\": \"0\",\n        \"StockNumber\": null\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEciv4Getdetailsbyname",
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
    "url": "/ECIV4/SearchFresh",
    "title": "新增公司列表",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "keyword",
            "description": "<p>关键字</p>"
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
            "field": "KeyNo",
            "description": "<p>内部KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Name",
            "description": "<p>公司名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OperName",
            "description": "<p>法人名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StartDate",
            "description": "<p>成立日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Status",
            "description": "<p>企业状态</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "No",
            "description": "<p>注册号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditCode",
            "description": "<p>社会统一信用代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegistCapi",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Address",
            "description": "<p>地址</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 10,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"KeyNo\": \"c1ad948f28014ad8cd412feae7ad7324\",\n            \"RegistCapi\": \"\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"110108604483716\",\n            \"CreditCode\": \"92110108MA00GTG727\",\n            \"OperName\": \"陈立国\",\n            \"Address\": \"北京市海淀区圆明园西路2号院11号112室\",\n            \"Name\": \"北京食香优源餐饮管理中心\"\n        },\n        {\n            \"KeyNo\": \"8160fbebc5ebd81ff9e46aaab2d65da4\",\n            \"RegistCapi\": \"5000万元人民币\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"\",\n            \"CreditCode\": \"91110109MA00GT10X0\",\n            \"OperName\": \"马红利\",\n            \"Address\": \"北京市门头沟区石龙开发区平安路7号LQ0022\",\n            \"Name\": \"北京弘利宜居房地产开发有限公司\"\n        },\n        {\n            \"KeyNo\": \"7919266d1488404f9e6a85d76b136887\",\n            \"RegistCapi\": \"\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"110116604199841\",\n            \"CreditCode\": \"92110116MA00GT310K\",\n            \"OperName\": \"邵仕龙\",\n            \"Address\": \"北京市怀柔区北房镇宰相庄村111号\",\n            \"Name\": \"北京国旭龙商店\"\n        },\n        {\n            \"KeyNo\": \"39bfe9a434e1cc812c78f3df27c1f602\",\n            \"RegistCapi\": \"\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"110108604483708\",\n            \"CreditCode\": \"92110108MA00GTFT6H\",\n            \"OperName\": \"原五根\",\n            \"Address\": \"北京市海淀区圆明园西路2号院8号102室\",\n            \"Name\": \"北京鑫食健源餐饮管理中心\"\n        },\n        {\n            \"KeyNo\": \"65fbd3478a120e20f76d6923aa1f5c8f\",\n            \"RegistCapi\": \"10万元人民币\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"\",\n            \"CreditCode\": \"91110101MA00GRWH06\",\n            \"OperName\": \"颜廷坝\",\n            \"Address\": \"北京市东城区东花市南里东区3号楼1层B06\",\n            \"Name\": \"北京虎视健康咨询有限公司\"\n        },\n        {\n            \"KeyNo\": \"8f8bd462c8330f60220ddbc9f7e85eaf\",\n            \"RegistCapi\": \"\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"110116604199905\",\n            \"CreditCode\": \"92110116MA00GTJA6L\",\n            \"OperName\": \"张国\",\n            \"Address\": \"北京市怀柔区雁栖湖南岸(北京市律师培训中心5幢1层)\",\n            \"Name\": \"北京悦文军商店\"\n        },\n        {\n            \"KeyNo\": \"d2fb554448bd83656c62003f32fa2ad2\",\n            \"RegistCapi\": \"1000万元人民币\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"\",\n            \"CreditCode\": \"91110109MA00GTBF61\",\n            \"OperName\": \"杨慧如\",\n            \"Address\": \"北京市门头沟区石龙经济开发区永安路20号1号楼14层2单元1401室-DXF061\",\n            \"Name\": \"北京大地纯风电子商务有限公司\"\n        },\n        {\n            \"KeyNo\": \"5d8da44e84978183e1d43bc934a5f9e6\",\n            \"RegistCapi\": \"200万元人民币\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"\",\n            \"CreditCode\": \"91110109MA00GT4686\",\n            \"OperName\": \"肖海洋\",\n            \"Address\": \"北京市门头沟区雁翅镇高芹路1号院YC-0095\",\n            \"Name\": \"北京元析科技有限公司\"\n        },\n        {\n            \"KeyNo\": \"365a227a6dc3613cc369f5cdcb19cdd8\",\n            \"RegistCapi\": \"500万元人民币\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"\",\n            \"CreditCode\": \"91110105MA00GTB54N\",\n            \"OperName\": \"赵鹏\",\n            \"Address\": \"北京市朝阳区广渠东路唐家村23幢18-A\",\n            \"Name\": \"北京黑马先生服装有限公司\"\n        },\n        {\n            \"KeyNo\": \"2c120f9f2fae38cb2a2210bf2624b5f8\",\n            \"RegistCapi\": \"100万元人民币\",\n            \"StartDate\": \"2017-08-04 12:00:00\",\n            \"Status\": \"存续（在营、开业、在册）\",\n            \"No\": \"\",\n            \"CreditCode\": \"91110109MA00GRQH29\",\n            \"OperName\": \"宋凯\",\n            \"Address\": \"北京市门头沟区清水镇洪水口村8号\",\n            \"Name\": \"北京豫峰园农业科技有限公司\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetEciv4Searchfresh",
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
            "field": "fullName",
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
    "url": "/History/GetHistoryShiXin",
    "title": "历史失信查询",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "description": "<p>Id值</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ActionRemark",
            "description": "<p>其他有履行能力而拒不履行生效法律文书确定义务</p>"
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
            "field": "ExecuteStatus",
            "description": "<p>被执行的履行情况</p>"
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
            "field": "PublicDate",
            "description": "<p>发布时间</p>"
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
            "type": "string",
            "optional": false,
            "field": "ExecuteGov",
            "description": "<p>执行法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AnNo",
            "description": "<p>执行依据文号</p>"
          },
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
            "field": "LiAnDate",
            "description": "<p>立案时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgNo",
            "description": "<p>组织机构代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgType",
            "description": "<p>组织类型，1：自然人，2：企业，3：社会组织，空白：无法判定）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgTypeName",
            "description": "<p>组织类型名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Name",
            "description": "<p>名称</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 2\n    },\n    \"Result\": [\n        {\n            \"YiWu\": \"向申请人中国化学工程第四建设有限公司支付11961141.75元，执行费79361.14元。\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"OrgNo\": \"68648046-6\",\n            \"Province\": \"新疆\",\n            \"ExecuteGov\": \"乌鲁木齐市中级人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",v\n            \"CaseNo\": \"（2017）新01执514号\",\n            \"AnNo\": \"（2017）新01执514号\",\n            \"ExecuteUnite\": \"乌鲁木齐仲裁委员会\",\n            \"ActionRemark\": \"有履行能力而拒不履行生效法律文书确定义务,违反财产报告制度\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2018-01-10 12:00:00\",\n            \"LiAnDate\": \"2017-08-03 12:00:00\",\n            \"Id\": \"fd9285c28fffc4caa262eedf064ff74f2\",\n            \"ExecuteNo\": \"（2016）乌仲裁字第0348号\",\n            \"OrgTypeName\": \"失信企业\"\n        },\n        {\n            \"YiWu\": \"支付6163938元\",\n            \"ExecuteStatus\": \"全部未履行\",\n            \"OrgNo\": \"68648046-6\",\n            \"Province\": \"浙江\",\n            \"ExecuteGov\": \"长兴县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"CaseNo\": \"(2016)浙0522执3375号\",\n            \"AnNo\": \"(2016)浙0522执3375号\",\n            \"ExecuteUnite\": \"湖州长兴法院\",\n            \"ActionRemark\": \"其他有履行能力而拒不履行生效法律文书确定义务\",\n            \"OrgType\": \"2\",\n            \"PublicDate\": \"2016-11-11 12:00:00\",\n            \"LiAnDate\": \"2016-10-26 12:00:00\",\n            \"Id\": \"c8073c2b875733fbc031199970ccc1e82\",\n            \"ExecuteNo\": \"(2015)湖长泗商初字第00549号\",\n            \"OrgTypeName\": \"失信企业\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistoryshixin",
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
    "url": "/History/GetHistorytAdminLicens",
    "title": "历史行政许可",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "type": "object[]",
            "optional": false,
            "field": "EciList",
            "description": "<p>历史工商行政许可</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.LicensDocNo",
            "description": "<p>许可文件编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.LicensDocName",
            "description": "<p>许可文件名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.LicensOffice",
            "description": "<p>许可机关</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.LicensContent",
            "description": "<p>许可内容</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.ValidityFrom",
            "description": "<p>有效期自</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.ValidityTo",
            "description": "<p>有效期至</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "CreditChinaList",
            "description": "<p>历史信用中国行政许可</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.CaseNo",
            "description": "<p>编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.Name",
            "description": "<p>项目名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.LiAnDate",
            "description": "<p>决定日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.Province",
            "description": "<p>地域</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.OwnerName",
            "description": "<p>公司</p>"
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
    "name": "GetHistoryGethistorytadminlicens",
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
    "url": "/History/GetHistorytAdminPenalty",
    "title": "历史行政处罚",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "type": "object[]",
            "optional": false,
            "field": "EciList",
            "description": "<p>工商行政处罚</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.DocNo",
            "description": "<p>文号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.PenaltyType",
            "description": "<p>违法行为类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.Content",
            "description": "<p>处罚内容</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.PenaltyDate",
            "description": "<p>决定日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.PublicDate",
            "description": "<p>作出行政公示日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EciList.OfficeName",
            "description": "<p>决定机关</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "CreditChinaList",
            "description": "<p>信用中国行政处罚</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.CaseNo",
            "description": "<p>决定文书号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.Name",
            "description": "<p>处罚名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.LiAnDate",
            "description": "<p>决定时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.Province",
            "description": "<p>省份</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.OwnerName",
            "description": "<p>公司</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CreditChinaList.CaseReason",
            "description": "<p>案由</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"EciList\": [\n            {\n                \"PenaltyDate\": \"2016-11-25 12:00:00\",\n                \"Content\": \"罚款金额0.2万元;没收金额0.0万元\",\n                \"DocNo\": \"津红国税罚〔2016〕20021\",\n                \"PenaltyType\": \"违反税收管理\",\n                \"OfficeName\": \"天津市红桥区国家税务局\"\n            },\n            {\n                \"Content\": \"罚款金额0.2万元;没收金额0.0万元\",\n                \"DocNo\": \"津红国税罚〔2016〕20021\",\n                \"PenaltyType\": \"违反税收管理\",\n                \"OfficeName\": \"天津市红桥区国家税务局\"\n            }\n        ],\n        \"CreditChinaList\": [\n        ]\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistorytadminpenalty",
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
    "url": "/History/GetHistorytCourtNotice",
    "title": "历史法院公告",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "description": "<p>Id值</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Category",
            "description": "<p>公告类型</p>"
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
            "field": "Court",
            "description": "<p>公告人</p>"
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
            "type": "string",
            "optional": false,
            "field": "Province",
            "description": "<p>省份</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PublishPage",
            "description": "<p>刊登版面</p>"
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
            "field": "PublishDate",
            "description": "<p>公示日期</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 8\n    },\n    \"Result\": [\n        {\n            \"PublishDate\": \"2016-01-09 12:00:00\",\n            \"Category\": \"裁判文书\",\n            \"Party\": \"恒大地产集团有限公司、严东\",\n            \"SubmitDate\": \"2016-01-09 12:00:00\",\n            \"Content\": \"严东：本院受理原告恒大地产集团有限公司诉被告严东房屋买卖合同纠纷一案已审理终结。现依法向你公告送达（2015）穗云法民四初字451号民事判决书一份。自公告之日起60日内来本院领取民事判决书，逾期则视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于广东省广州市中级人民法院。...\",\n            \"PublishPage\": \"\",\n            \"Id\": \"289B595F89FE87DE\",\n            \"Province\": \"GD\",\n            \"Court\": \"广州市白云区人民法院\"\n        },\n        {\n            \"PublishDate\": \"2015-09-26 12:00:00\",\n            \"Category\": \"裁判文书\",\n            \"Party\": \"周细清\",\n            \"SubmitDate\": \"2015-09-26 12:00:00\",\n            \"Content\": \"周细清：本院受理原告恒大地产集团有限公司诉被告周细清房屋买卖合同纠纷一案已审理终结。现依法向你公告送达（2015）穗云法民四初字第320号民事判决书一份。自公告之日起60日内来本院领取民事判决书，逾期则视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于广东省广州市中级人民法院。...\",\n            \"PublishPage\": \"\",\n            \"Id\": \"3490C356FC007113\",\n            \"Province\": \"GD\",\n            \"Court\": \"[广东]广州市白云区人民法院\"\n        },\n        {\n            \"PublishDate\": \"2015-04-01 12:00:00\",\n            \"Category\": \"诉状副本及开庭传票\",\n            \"Party\": \"谢雨波\",\n            \"SubmitDate\": \"2015-04-01 12:00:00\",\n            \"Content\": \"谢雨波：本院受理原告恒大地产集团有限公司诉你商品房销售合同纠纷二案，因你下落不明，现依法向你公告送达起诉状及证据副本、应诉通知书、举证通知书、民事裁定书、告知合议庭组成人员通知书和开庭传票等法律文书。自本公告发出之日起经过60日即视为送达。提出答辩状和举证期限分别为公告期满后的15日和30日内。...\",\n            \"PublishPage\": \"\",\n            \"Id\": \"46671945D858DC70\",\n            \"Province\": \"GD\",\n            \"Court\": \"[广东]恩平市人民法院\"\n        },\n        {\n            \"PublishDate\": \"2015-09-12 12:00:00\",\n            \"Category\": \"起诉状副本及开庭传票\",\n            \"Party\": \"恒大地产集团有限公司、严东\",\n            \"SubmitDate\": \"2015-09-12 12:00:00\",\n            \"Content\": \"严东：本院受理原告恒大地产集团有限公司诉被告严东房屋买卖合同纠纷一案【案号：（2015）穗云法民四初字第451号】，现依法向你公告送达起诉状副本、开庭传票。自公告之日起经过六十天，即视为送达。提出答辩状和举证的期限均为公告期满后的30日内。并定于举证期满后的2015年12月16日9时整（遇法定节假日顺延）在本院第十七...\",\n            \"PublishPage\": \"\",\n            \"Id\": \"4AC228FDB9432223\",\n            \"Province\": \"GD\",\n            \"Court\": \"广州市白云区人民法院\"\n        },\n        {\n            \"PublishDate\": \"2015-09-26 12:00:00\",\n            \"Category\": \"裁判文书\",\n            \"Party\": \"恒大地产集团有限公司、周细清\",\n            \"SubmitDate\": \"2015-09-26 12:00:00\",\n            \"Content\": \"周细清：本院受理原告恒大地产集团有限公司诉被告周细清房屋买卖合同纠纷一案已审理终结。现依法向你公告送达（2015）穗云法民四初字第320号民事判决书一份。自公告之日起60日内来本院领取民事判决书，逾期则视为送达。如不服本判决，可在公告期满后15日内，向本院递交上诉状及副本，上诉于广东省广州市中级人民法院。...\",\n            \"PublishPage\": \"\",\n            \"Id\": \"50AEFB2D3AF33A72\",\n            \"Province\": \"GD\",\n            \"Court\": \"广州市白云区人民法院\"\n        },\n        {\n            \"PublishDate\": \"2015-11-04 12:00:00\",\n            \"Category\": \"诉状副本及开庭传票\",\n            \"Party\": \"朱震宇\",\n            \"SubmitDate\": \"2015-11-04 12:00:00\",\n            \"Content\": \"朱震宇：本院受理原告恒大地产集团有限公司诉朱震宇商品房销售合同纠纷一案，现依法向你公告送达起诉状副本、应诉通知书、举证通知书及开庭传票。自公告之日起，经过60日即视为送达。提出答辩状的期限和举证期限分别为公告期满后15日和30日内。并定于举证期满后第3日上午9时（遇法定假日顺延）在本院东三楼第二审判法庭开庭...\",\n            \"PublishPage\": \"\",\n            \"Id\": \"8B1F91E4AE28C0EF\",\n            \"Province\": \"NMG\",\n            \"Court\": \"[内蒙古]包头市九原区人民法院\"\n        },\n        {\n            \"PublishDate\": \"2015-09-12 12:00:00\",\n            \"Category\": \"诉状副本及开庭传票\",\n            \"Party\": \"严东\",\n            \"SubmitDate\": \"2015-09-12 12:00:00\",\n            \"Content\": \"严东：本院受理原告恒大地产集团有限公司诉被告严东房屋买卖合同纠纷一案【案号：（2015）穗云法民四初字第451号】，现依法向你公告送达起诉状副本、开庭传票。自公告之日起经过六十天，即视为送达。提出答辩状和举证的期限均为公告期满后的30日内。并定于举证期满后的2015年12月16日9时整（遇法定节假日顺延）在本院第十七...\",\n            \"PublishPage\": \"\",\n            \"Id\": \"99EBD52A387F988E\",\n            \"Province\": \"GD\",\n            \"Court\": \"[广东]广州市白云区人民法院\"\n        },\n        {\n            \"PublishDate\": \"2015-06-06 12:00:00\",\n            \"Category\": \"诉状副本及开庭传票\",\n            \"Party\": \"周细清\",\n            \"SubmitDate\": \"2015-06-06 12:00:00\",\n            \"Content\": \"周细清：本院受理原告恒大地产集团有限公司诉被告周细清房屋买卖合同纠纷一案，现依法向你公告送达起诉状副本、开庭传票。自公告之日起经过六十天，即视为送达。提出答辩状和举证的期限分别为公告期满后的30日内。并定于举证期满后的2015年9月2日10时30分（遇法定节假日顺延）在本院第十七法庭开庭审理，逾期将依法缺席判决...\",\n            \"PublishPage\": \"\",\n            \"Id\": \"A78778C01BA06C5C\",\n            \"Province\": \"GD\",\n            \"Court\": \"[广东]广州市白云区人民法院\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistorytcourtnotice",
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
    "url": "/History/GetHistorytEci",
    "title": "历史工商信息",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "field": "KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "CompanyNameList",
            "description": "<p>历史名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyNameList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyNameList.CompanyName",
            "description": "<p>公司名称</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "OperList",
            "description": "<p>历史名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OperList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OperList.OperName",
            "description": "<p>公司名称</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "RegistCapiList",
            "description": "<p>历史注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegistCapiList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegistCapiList.RegistCapi",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegistCapiList.Amount",
            "description": "<p>金额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegistCapiList.Unit",
            "description": "<p>单位</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "AddressList",
            "description": "<p>历史地址</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AddressList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AddressList.Address",
            "description": "<p>地址</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "ScopeList",
            "description": "<p>历史经营范围</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ScopeList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ScopeList.Scope",
            "description": "<p>经营范围</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "EmployeeList",
            "description": "<p>历史主要人员</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EmployeeList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EmployeeList.Employees.KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EmployeeList.Employees.EmployeeName",
            "description": "<p>名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EmployeeList.Employees.Job",
            "description": "<p>职位</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "BranchList",
            "description": "<p>历史分支机构</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "BranchList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "BranchList.KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "BranchList.BranchName",
            "description": "<p>机构名称</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "TelList",
            "description": "<p>历史电话</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "TelList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "TelList.Tel",
            "description": "<p>电话</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "EmailList",
            "description": "<p>历史邮箱</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EmailList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EmailList.Email",
            "description": "<p>邮箱</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "WebsiteList",
            "description": "<p>历史网站</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "WebsiteList.ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "WebsiteList.Email",
            "description": "<p>邮箱</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "\n{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Result\": {\n        \"KeyNo\": \"befe52d9753b511b6aef5e33fe00f97d\",\n        \"RegistCapiList\": [\n            {\n                \"RegistCapi\": \"1.2亿人民币元\",\n                \"Amount\": \"120000000\",\n                \"ChangeDate\": \"2013-04-08\",\n                \"Unit\": \"人民币元\"\n            }\n        ],\n        \"TelList\": [\n        ],\n        \"CompanyNameList\": [\n        ],\n        \"BranchList\": [\n        ],\n        \"OperList\": [\n        ],\n        \"WebsiteList\": [\n        ],\n        \"ScopeList\": [\n            {\n                \"Scope\": \"商业地产投资及经营、酒店建设投资及经营、连锁百货投资及经营、电影院线等文化产业投资及经营;投资与资产管理、项目管理(以上均不含专项审批);货物进出口、技术进出口,国内一般贸易;代理记账、财务咨询、企业管理咨询、经济信息咨询、计算机信息技术服务与技术咨询、计算机系统集成、网络设备安装与维护(依法须经批准的项目,经相关部门批准后,方可开展经营活动)***\",\n                \"ChangeDate\": \"2018-04-19\"\n            }\n        ],\n        \"EmailList\": [\n        ],\n        \"AddressList\": [\n            {\n                \"Address\": \"大连中山区解放街9号\",\n                \"ChangeDate\": \"1999-03-12\"\n            },\n            {\n                \"Address\": \"大连市中山区解放街９号\",\n                \"ChangeDate\": \"1997-11-13\"\n            }\n        ],\n        \"EmployeeList\": [\n            {\n                \"ChangeDate\": \"2018-08-13\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"p70fb7e3420d037533540165fe84b545\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"林宁\"\n                    },\n                    {\n                        \"KeyNo\": \"pd54d0f650573ecbc2036f333fb0cfe0\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"尹海\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"董事长\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"p2d91474fa9ffa548de9464ad3d0a1f5\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"侯鸿军\"\n                    },\n                    {\n                        \"KeyNo\": \"pe31835041554c724fde88ec748aa6f4\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"韩旭\"\n                    },\n                    {\n                        \"KeyNo\": \"p3ef4e77096b20e7aef87d487aff8a0a\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"张谌\"\n                    },\n                    {\n                        \"KeyNo\": \"p165991ca474f1da37007ab96536b1a5\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"张霖\"\n                    },\n                    {\n                        \"KeyNo\": \"p532a92afcfac2fe4ba8a22ba866dc2f\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"齐界\"\n                    },\n                    {\n                        \"KeyNo\": \"pdd3325c48473fcd64180921d82ead80\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"王思聪\"\n                    },\n                    {\n                        \"KeyNo\": \"pf902f9eaae047fe1173120b7509a21d\",\n                        \"Job\": \"董事兼总经理\",\n                        \"EmployeeName\": \"丁本锡\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2016-01-26\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"p2d91474fa9ffa548de9464ad3d0a1f5\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"侯鸿军\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"总经理\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"p90e173852d40037a1bec4ea12ec426e\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"王贵亚\"\n                    },\n                    {\n                        \"KeyNo\": \"p532a92afcfac2fe4ba8a22ba866dc2f\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"齐界\"\n                    },\n                    {\n                        \"KeyNo\": \"pf902f9eaae047fe1173120b7509a21d\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"丁本锡\"\n                    },\n                    {\n                        \"KeyNo\": \"p165991ca474f1da37007ab96536b1a5\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"张霖\"\n                    },\n                    {\n                        \"KeyNo\": \"p70fb7e3420d037533540165fe84b545\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"林宁\"\n                    },\n                    {\n                        \"KeyNo\": \"p3ef4e77096b20e7aef87d487aff8a0a\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"张谌\"\n                    },\n                    {\n                        \"KeyNo\": \"pe31835041554c724fde88ec748aa6f4\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"韩旭\"\n                    },\n                    {\n                        \"KeyNo\": \"pd54d0f650573ecbc2036f333fb0cfe0\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"尹海\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2014-03-14\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"pf902f9eaae047fe1173120b7509a21d\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"丁本锡\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"总经理\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"p165991ca474f1da37007ab96536b1a5\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"张霖\"\n                    },\n                    {\n                        \"KeyNo\": \"p3ef4e77096b20e7aef87d487aff8a0a\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"张谌\"\n                    },\n                    {\n                        \"KeyNo\": \"pe31835041554c724fde88ec748aa6f4\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"韩旭\"\n                    },\n                    {\n                        \"KeyNo\": \"pd54d0f650573ecbc2036f333fb0cfe0\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"尹海\"\n                    },\n                    {\n                        \"KeyNo\": \"pf8025469fdbad176f535893d11c7e49\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"陈平\"\n                    },\n                    {\n                        \"KeyNo\": \"p2d91474fa9ffa548de9464ad3d0a1f5\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"侯鸿军\"\n                    },\n                    {\n                        \"KeyNo\": \"p923fc15cca706138ccabf568bf4a3a3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"李耀汉\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2011-03-08\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"总经理\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"pdd3325c48473fcd64180921d82ead80\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"王思聪\"\n                    },\n                    {\n                        \"KeyNo\": \"p2d91474fa9ffa548de9464ad3d0a1f5\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"侯鸿军\"\n                    },\n                    {\n                        \"KeyNo\": \"p5a2d2e51101af0b5d7a56cb8fcc9908\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"张诚\"\n                    },\n                    {\n                        \"KeyNo\": \"p532a92afcfac2fe4ba8a22ba866dc2f\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"齐界\"\n                    },\n                    {\n                        \"KeyNo\": \"p3ef4e77096b20e7aef87d487aff8a0a\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"张谌\"\n                    },\n                    {\n                        \"KeyNo\": \"pf8025469fdbad176f535893d11c7e49\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"陈平\"\n                    },\n                    {\n                        \"KeyNo\": \"p923fc15cca706138ccabf568bf4a3a3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"李耀汉\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2010-02-10\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"prdd0277127508b36a18d7a264b31467\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"王健\"\n                    },\n                    {\n                        \"KeyNo\": \"p3ef4e77096b20e7aef87d487aff8a0a\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"张谌\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"董事长\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"pdd3325c48473fcd64180921d82ead80\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"王思聪\"\n                    },\n                    {\n                        \"KeyNo\": \"p8d85ac0618f79ea3da1c2f4ec478857\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"崔宗明\"\n                    },\n                    {\n                        \"KeyNo\": \"p923fc15cca706138ccabf568bf4a3a3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"李耀汉\"\n                    },\n                    {\n                        \"KeyNo\": \"p83317b99e4b5cb7a5cc9e7be55841c4\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"黄平\"\n                    },\n                    {\n                        \"KeyNo\": \"p532a92afcfac2fe4ba8a22ba866dc2f\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"齐界\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2009-08-07\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"p532a92afcfac2fe4ba8a22ba866dc2f\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"齐界\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"总经理\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"pf902f9eaae047fe1173120b7509a21d\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"丁本锡\"\n                    },\n                    {\n                        \"KeyNo\": \"p3ef4e77096b20e7aef87d487aff8a0a\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"张谌\"\n                    },\n                    {\n                        \"KeyNo\": \"prdd0277127508b36a18d7a264b31467\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"王健\"\n                    },\n                    {\n                        \"KeyNo\": \"pd54d0f650573ecbc2036f333fb0cfe0\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"尹海\"\n                    },\n                    {\n                        \"KeyNo\": \"pf8025469fdbad176f535893d11c7e49\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"陈平\"\n                    },\n                    {\n                        \"KeyNo\": \"p923fc15cca706138ccabf568bf4a3a3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"李耀汉\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2009-01-06\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"p3ef4e77096b20e7aef87d487aff8a0a\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"张谌\"\n                    },\n                    {\n                        \"KeyNo\": \"p414902f5dbbd64f9d73d668c390ecdd\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"聂茁\"\n                    },\n                    {\n                        \"KeyNo\": \"pf8025469fdbad176f535893d11c7e49\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"陈平\"\n                    },\n                    {\n                        \"KeyNo\": \"p532a92afcfac2fe4ba8a22ba866dc2f\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"齐界\"\n                    },\n                    {\n                        \"KeyNo\": \"pr7beece1b71e105b84851644efc54b4\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"罗昕\"\n                    },\n                    {\n                        \"KeyNo\": \"pf479e05f9306979f83a621cdd451dbf\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"冷传金\"\n                    },\n                    {\n                        \"KeyNo\": \"prdd0277127508b36a18d7a264b31467\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"王健\"\n                    },\n                    {\n                        \"KeyNo\": \"p923fc15cca706138ccabf568bf4a3a3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"李耀汉\"\n                    },\n                    {\n                        \"KeyNo\": \"pf902f9eaae047fe1173120b7509a21d\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"丁本锡\"\n                    },\n                    {\n                        \"KeyNo\": \"pd54d0f650573ecbc2036f333fb0cfe0\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"尹海\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"董事长\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"pa21c9d253848f65999adb9011ed7d11\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"孙喜双\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2005-12-29\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"pf902f9eaae047fe1173120b7509a21d\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"丁本锡\"\n                    },\n                    {\n                        \"KeyNo\": \"p83317b99e4b5cb7a5cc9e7be55841c4\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"黄平\"\n                    },\n                    {\n                        \"KeyNo\": \"pf479e05f9306979f83a621cdd451dbf\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"冷传金\"\n                    },\n                    {\n                        \"KeyNo\": \"p923fc15cca706138ccabf568bf4a3a3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"李耀汉\"\n                    },\n                    {\n                        \"KeyNo\": \"p414902f5dbbd64f9d73d668c390ecdd\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"聂茁\"\n                    },\n                    {\n                        \"KeyNo\": \"pa21c9d253848f65999adb9011ed7d11\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"孙喜双\"\n                    },\n                    {\n                        \"KeyNo\": \"pr0eb93d36e0a295df4f6c9effa1d66d\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"孙湛\"\n                    },\n                    {\n                        \"KeyNo\": \"p0910f694bab2853557fd86156fefba6\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"汤天伟\"\n                    },\n                    {\n                        \"KeyNo\": \"prdd0277127508b36a18d7a264b31467\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"王健\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"董事长\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"pd54d0f650573ecbc2036f333fb0cfe0\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"尹海\"\n                    },\n                    {\n                        \"KeyNo\": \"p876e7ef826ecd738b2f3dfc5c5bd926\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"周良君\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2004-02-09\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"pr87ad6e0eb962a5adf29c5df27c65e3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"董永成\"\n                    },\n                    {\n                        \"KeyNo\": \"prc8109677552074586879e0137796a4\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"郭岩\"\n                    },\n                    {\n                        \"KeyNo\": \"prfbf7c12ad06f5fed2ba6988cddb3ad\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"姜雄城\"\n                    },\n                    {\n                        \"KeyNo\": \"pf479e05f9306979f83a621cdd451dbf\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"冷传金\"\n                    },\n                    {\n                        \"KeyNo\": \"pr943a6cdf9e73966e9d9ff9c46c1b44\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"李学峰\"\n                    },\n                    {\n                        \"KeyNo\": \"p923fc15cca706138ccabf568bf4a3a3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"李耀汉\"\n                    },\n                    {\n                        \"KeyNo\": \"p414902f5dbbd64f9d73d668c390ecdd\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"聂茁\"\n                    },\n                    {\n                        \"KeyNo\": \"p532a92afcfac2fe4ba8a22ba866dc2f\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"齐界\"\n                    },\n                    {\n                        \"KeyNo\": \"pa21c9d253848f65999adb9011ed7d11\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"孙喜双\"\n                    },\n                    {\n                        \"KeyNo\": \"prf35887e34d98ced1031ba18dcae409\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"谭业军\"\n                    },\n                    {\n                        \"KeyNo\": \"p0910f694bab2853557fd86156fefba6\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"汤天伟\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"董事长\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"pr1963fa9b51a96fd9ef5c7f69dfbbaa\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"谢里修\"\n                    },\n                    {\n                        \"KeyNo\": \"p876e7ef826ecd738b2f3dfc5c5bd926\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"周良君\"\n                    }\n                ]\n            },\n            {\n                \"ChangeDate\": \"2002-12-23\",\n                \"Employees\": [\n                    {\n                        \"KeyNo\": \"prdf7c0c6fa5d2ed5bd1a740f7d9ddce\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"程绍运\"\n                    },\n                    {\n                        \"KeyNo\": \"pr87ad6e0eb962a5adf29c5df27c65e3\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"董永成\"\n                    },\n                    {\n                        \"KeyNo\": \"p99bbfce6356861ccd4a78a32adbac90\",\n                        \"Job\": \"副总经理\",\n                        \"EmployeeName\": \"高茜\"\n                    },\n                    {\n                        \"KeyNo\": \"p83317b99e4b5cb7a5cc9e7be55841c4\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"黄平\"\n                    },\n                    {\n                        \"KeyNo\": \"pr0d86904673eae79a7475b8ec782bc5\",\n                        \"Job\": \"总经理\",\n                        \"EmployeeName\": \"姜积成\"\n                    },\n                    {\n                        \"KeyNo\": \"pf479e05f9306979f83a621cdd451dbf\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"冷传金\"\n                    },\n                    {\n                        \"KeyNo\": \"pr75fc6a0715bec3b280c6e79ee52190\",\n                        \"Job\": \"监事\",\n                        \"EmployeeName\": \"苏仲义\"\n                    },\n                    {\n                        \"KeyNo\": \"pr8a1582fb0f163f476231f990275fbb\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"孙昆双\"\n                    },\n                    {\n                        \"KeyNo\": \"prf35887e34d98ced1031ba18dcae409\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"谭业军\"\n                    },\n                    {\n                        \"KeyNo\": \"pr59f365564c03fb79c2399a26adedf0\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"汤闯\"\n                    },\n                    {\n                        \"KeyNo\": \"pea5ac417585edc0effd7d23406510da\",\n                        \"Job\": \"董事长\",\n                        \"EmployeeName\": \"王健林\"\n                    },\n                    {\n                        \"KeyNo\": \"pr1963fa9b51a96fd9ef5c7f69dfbbaa\",\n                        \"Job\": \"董事\",\n                        \"EmployeeName\": \"谢里修\"\n                    },\n                    {\n                        \"KeyNo\": \"p55a2871e4369c3b03d16f93a4b7c27d\",\n                        \"Job\": \"副总经理\",\n                        \"EmployeeName\": \"佘世耀\"\n                    }\n                ]\n            }\n        ]\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistoryteci",
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
    "url": "/History/GetHistorytInvestment",
    "title": "历史对外投资",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "field": "ChangeDate",
            "description": "<p>变更日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyName",
            "description": "<p>公司名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OperName",
            "description": "<p>法人名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegistCapi",
            "description": "<p>注册资本</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "EconKind",
            "description": "<p>公司类型</p>"
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
            "type": "string",
            "optional": false,
            "field": "FundedRatio",
            "description": "<p>出资比例</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StartDate",
            "description": "<p>投资日期</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "\n{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 10\n    },\n    \"Result\": [\n        {\n            \"KeyNo\": \"0076d172a84d94e537eafa7d8aa97509\",\n            \"RegistCapi\": \"3030万人民币元\",\n            \"StartDate\": \"2017-01-09 12:00:00\",\n            \"Status\": \"存续\",\n            \"CompanyName\": \"平顶山长久置业有限公司\",\n            \"OperName\": \"贾飞\",\n            \"EconKind\": \"其他有限责任公司\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2018-04-13 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"4886a625749ad0c33a4fe4615882c35d\",\n            \"RegistCapi\": \"10000万人民币元\",\n            \"StartDate\": \"2007-04-30 12:00:00\",\n            \"Status\": \"\",\n            \"CompanyName\": \"广州恒大材料设备有限公司\",\n            \"OperName\": \"苏鑫\",\n            \"EconKind\": \"有限责任公司(法人独资)\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2018-01-09 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"3b87edcc0b73147d0d220e27985a4a64\",\n            \"RegistCapi\": \"2000万人民币元\",\n            \"StartDate\": \"2009-04-24 12:00:00\",\n            \"Status\": \"\",\n            \"CompanyName\": \"广东恒大排球俱乐部有限公司\",\n            \"OperName\": \"李一萌\",\n            \"EconKind\": \"有限责任公司(法人独资)\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2017-12-12 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"97eeeffd2a1d8ccbfb8d8472f5aa5584\",\n            \"RegistCapi\": \"10000万人民币元\",\n            \"StartDate\": \"2015-09-30 12:00:00\",\n            \"Status\": \"存续\",\n            \"CompanyName\": \"深圳市小牛消费服务有限公司\",\n            \"OperName\": \"彭最鸿\",\n            \"EconKind\": \"有限责任公司（法人独资）\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2017-11-20 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"24850a5c259e5ab475affeb0c1ae8e6b\",\n            \"RegistCapi\": \"36255万人民币元\",\n            \"Status\": \"\",\n            \"CompanyName\": \"广州市俊鸿房地产开发有限公司\",\n            \"OperName\": \"吉兴顺\",\n            \"EconKind\": \"有限责任公司(法人独资)\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2017-06-21 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"b0bad4b66053fa8cf409d186bfcb3a4d\",\n            \"RegistCapi\": \"2000000万人民币元\",\n            \"StartDate\": \"2015-05-19 12:00:00\",\n            \"Status\": \"\",\n            \"CompanyName\": \"恒大旅游集团有限公司\",\n            \"OperName\": \"汤济泽\",\n            \"EconKind\": \"有限责任公司(法人独资)\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2016-11-28 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"112df39675782fa6688179ea2795e1b6\",\n            \"RegistCapi\": \"5398498.000000万人民币元\",\n            \"StartDate\": \"2009-08-26 12:00:00\",\n            \"Status\": \"\",\n            \"CompanyName\": \"恒大集团(南昌)有限公司\",\n            \"OperName\": \"鞠志明\",\n            \"EconKind\": \"其他有限责任公司\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2016-11-28 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"e72fd45d73e23667c84e8f9bb6b4dc98\",\n            \"RegistCapi\": \"100万人民币元\",\n            \"Status\": \"\",\n            \"CompanyName\": \"深圳市铭之瑞科技有限公司\",\n            \"OperName\": \"张波\",\n            \"EconKind\": \"有限责任公司（自然人独资）\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2016-09-27 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"27508fccfd3110a8056327e45e703ac2\",\n            \"RegistCapi\": \"500万人民币元\",\n            \"Status\": \"\",\n            \"CompanyName\": \"启东市欣晴娱乐有限公司\",\n            \"OperName\": \"艾冬\",\n            \"EconKind\": \"有限责任公司（法人独资）\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2016-05-30 12:00:00\"\n        },\n        {\n            \"KeyNo\": \"517ae662bfd202a8eb567224c0637d4b\",\n            \"RegistCapi\": \"600万人民币元\",\n            \"Status\": \"\",\n            \"CompanyName\": \"启东市金色海岸大酒店有限公司\",\n            \"OperName\": \"艾冬\",\n            \"EconKind\": \"有限责任公司（法人独资）\",\n            \"FundedRatio\": \"\",\n            \"ChangeDate\": \"2016-05-30 12:00:00\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistorytinvestment",
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
    "url": "/History/GetHistorytJudgement",
    "title": "历史裁判文书",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "description": "<p>Id值</p>"
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
            "description": "<p>案件名称</p>"
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
            "field": "CaseNo",
            "description": "<p>案件编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CaseType",
            "description": "<p>案件类型</p>"
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
            "field": "CourtYear",
            "description": "<p>年份</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 3\n    },\n    \"Result\": [\n        {\n            \"CaseNo\": \"（2016）京0108民初33393号\",\n            \"CourtYear\": \"2016\",\n            \"CaseType\": \"ms\",\n            \"SubmitDate\": \"2016-11-17 12:00:00\",\n            \"CaseName\": \"张海合与北京小桔科技有限公司网络服务合同纠纷一审民事判决书\",\n            \"CaseRole\": \"[{\\\"P\\\":\\\"张海合\\\",\\\"R\\\":\\\"原告\\\"},{\\\"P\\\":\\\"北京小桔科技有限公司\\\",\\\"R\\\":\\\"被告\\\"}]\",\n            \"Id\": \"6f6b6dd992c6527acb9bdaacae29fffd\",\n            \"Court\": \"北京市海淀区人民法院\"\n        },\n        {\n            \"CaseNo\": \"（2016）京0108民初33183号\",\n            \"CourtYear\": \"2016\",\n            \"CaseType\": \"ms\",\n            \"SubmitDate\": \"2016-11-17 12:00:00\",\n            \"CaseName\": \"庞晶磊与北京小桔科技有限公司合同纠纷一审民事判决书\",\n            \"CaseRole\": \"[{\\\"P\\\":\\\"庞晶磊\\\",\\\"R\\\":\\\"原告\\\"},{\\\"P\\\":\\\"北京小桔科技有限公司\\\",\\\"R\\\":\\\"被告\\\"}]\",\n            \"Id\": \"7956b180216019230566c4a6e7a06a94\",\n            \"Court\": \"北京市海淀区人民法院\"\n        },\n        {\n            \"CaseNo\": \"（2016）浙0602民初9693号\",\n            \"CourtYear\": \"2016\",\n            \"CaseType\": \"ms\",\n            \"SubmitDate\": \"2016-11-16 12:00:00\",\n            \"CaseName\": \"\",\n            \"CaseRole\": \"[{\\\"P\\\":\\\"翁坚超\\\",\\\"R\\\":\\\"原告\\\"},{\\\"P\\\":\\\"北京小桔科技有限公司\\\",\\\"R\\\":\\\"被告\\\"}]\",\n            \"Id\": \"786440d8293e3a2f81acb63b759c20f8\",\n            \"Court\": \"绍兴市越城区人民法院\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistorytjudgement",
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
    "url": "/History/GetHistorytMPledge",
    "title": "历史动产抵押",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "field": "RegisterNo",
            "description": "<p>登记编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegisterDate",
            "description": "<p>登记日期</p>"
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
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 1,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"Status\": \"有效\",\n            \"RegisterOffice\": \"资阳市工商行政管理局\",\n            \"RegisterNo\": \"\",\n            \"RegisterDate\": \"2015-09-16 12:00:00\",\n            \"DebtSecuredAmount\": \"1321.8258万元\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistorytmpledge",
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
    "url": "/History/GetHistorytPledge",
    "title": "历史股权出质",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "field": "RegistNo",
            "description": "<p>登记编号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Pledgor",
            "description": "<p>出质人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Pledgee",
            "description": "<p>质权人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PledgedAmount",
            "description": "<p>出质股权数额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "RegDate",
            "description": "<p>股权出质设立登记日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "PublicDate",
            "description": "<p>公布日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Status",
            "description": "<p>状态</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 1,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"Pledgee\": \"鑫融基投资担保有限公司\",\n            \"Status\": \"无效\",\n            \"RegistNo\": \"410700201400000043\",\n            \"Pledgor\": \"堵召辉\",\n            \"RegDate\": \"2014-09-10 12:00:00\",\n            \"PublicDate\": \"2015-07-01 12:00:00\",\n            \"PledgedAmount\": \"330万人民币元\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistorytpledge",
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
    "url": "/History/GetHistorytSessionNotice",
    "title": "历史开庭公告",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "description": "<p>Id值</p>"
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
            "field": "ProsecutorList",
            "description": "<p>公诉人/原告/上诉人/申请人</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "DefendantList",
            "description": "<p>被告人/被告/被上诉人/被申请人</p>"
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
            "field": "CaseNo",
            "description": "<p>案号</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "LiAnDate",
            "description": "<p>开庭日期</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 4\n    },\n    \"Result\": [\n        {\n            \"CaseNo\": \"(2017)川01民终4786号\",\n            \"ProsecutorList\": \"中国平安财产保险股份有限公司四川分公司\",\n            \"LiAnDate\": \"2017-04-12 12:00:00\",\n            \"CaseReason\": \"机动车交通事故责任纠纷\",\n            \"Id\": \"8226945fd8445ebfc0df482dd5f3b82f5\",\n            \"DefendantList\": \"王国强\\t北京小桔科技有限公司\\t何立新\\t蒋海涛\\t陈玉刚\\t曾翠平\\t蒋习武\\t李美琪\",\n            \"ExecuteGov\": \"四川省成都市中级人民法院\"\n        },\n        {\n            \"CaseNo\": \"(2017)沪0115民初15113号\",\n            \"ProsecutorList\": \"柳正浩\",\n            \"LiAnDate\": \"2017-03-15 12:00:00\",\n            \"CaseReason\": \"生命权、健康权、身体权纠纷\",\n            \"Id\": \"53243f38c04c0cd710dcf941ee3d5cc05\",\n            \"DefendantList\": \"杜超杰\\t北京小桔科技有限公司\",\n            \"ExecuteGov\": \"上海市浦东新区人民法院\"\n        },\n        {\n            \"CaseNo\": \"(2017)沪0112民初4106号\",\n            \"ProsecutorList\": \"左桂军\",\n            \"LiAnDate\": \"2017-03-14 12:00:00\",\n            \"CaseReason\": \"机动车交通事故责任纠纷\",\n            \"Id\": \"487d209e24933dfa94d3aea165b773085\",\n            \"DefendantList\": \"邹建荣\\t北京小桔科技有限公司\\t沙磊\\t上海市闵行区医疗急救中心\\t中国人民财产保险股份有限公司上海市分公司\",\n            \"ExecuteGov\": \"上海市闵行区人民法院\"\n        },\n        {\n            \"CaseNo\": \"(2017)沪0112民初4103号\",\n            \"ProsecutorList\": \"王庆乐\",\n            \"LiAnDate\": \"2017-03-14 12:00:00\",\n            \"CaseReason\": \"机动车交通事故责任纠纷\",\n            \"Id\": \"7d1da8d2b4b5f8d05eb9b079ecc11d9a5\",\n            \"DefendantList\": \"邹建荣\\t北京小桔科技有限公司\\t沙磊\\t上海市闵行区医疗急救中心\\t中国人民财产保险股份有限公司上海市分公司\",\n            \"ExecuteGov\": \"上海市闵行区人民法院\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistorytsessionnotice",
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
    "url": "/History/GetHistorytShareHolder",
    "title": "历史股东",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "field": "PartnerName",
            "description": "<p>股东名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "StockPercent",
            "description": "<p>持股比例</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ShouldCapi",
            "description": "<p>认缴出资额</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ShouldDate",
            "description": "<p>认缴出资日期</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ShouldType",
            "description": "<p>出资类型</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "ChangeDateList",
            "description": "<p>变更日期</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 10\n    },\n    \"Result\": [\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"73.8806%\",\n            \"PartnerName\": \"广州市凯隆置业有限公司\",\n            \"ShouldCapi\": \"250000万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"2.4254%\",\n            \"PartnerName\": \"苏州工业园区睿灿投资企业（有限合伙）\",\n            \"ShouldCapi\": \"8207.0707万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"2.0522%\",\n            \"PartnerName\": \"马鞍山市茂文科技工业园有限公司\",\n            \"ShouldCapi\": \"6944.4444万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"1.8657%\",\n            \"PartnerName\": \"中信聚恒（深圳）投资控股中心（有限合伙）\",\n            \"ShouldCapi\": \"6313.1313万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"1.8657%\",\n            \"PartnerName\": \"深圳市麒翔投资有限公司\",\n            \"ShouldCapi\": \"6313.1313万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"1.8657%\",\n            \"PartnerName\": \"深圳市宝信投资控股有限公司\",\n            \"ShouldCapi\": \"6313.1313万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"1.8657%\",\n            \"PartnerName\": \"深圳市华建控股有限公司\",\n            \"ShouldCapi\": \"6313.1313万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"1.8657%\",\n            \"PartnerName\": \"江西省华达置业集团有限公司\",\n            \"ShouldCapi\": \"6313.1313万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"1.8657%\",\n            \"PartnerName\": \"广田投资有限公司\",\n            \"ShouldCapi\": \"6313.1313万元\"\n        },\n        {\n            \"ShouldType\": \"\",\n            \"ShouldDate\": \"\",\n            \"ChangeDateList\": \"[\\\"2017-11-23\\\",\\\"2017-06-01\\\",\\\"2017-04-01\\\"]\",\n            \"StockPercent\": \"1.3060%\",\n            \"PartnerName\": \"深圳市键诚投资有限公司\",\n            \"ShouldCapi\": \"4419.1919万元\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistorytshareholder",
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
    "url": "/History/GetHistoryZhiXing",
    "title": "历史被执行",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "field": "BiaoDi",
            "description": "<p>标地</p>"
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
            "type": "string",
            "optional": false,
            "field": "ExecuteGov",
            "description": "<p>执行法院</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "AnNo",
            "description": "<p>执行依据文号</p>"
          },
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
            "field": "LiAnDate",
            "description": "<p>立案时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgNo",
            "description": "<p>组织机构代码</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgType",
            "description": "<p>组织类型，1：自然人，2：企业，3：社会组织，空白：无法判定）</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "OrgTypeName",
            "description": "<p>组织类型名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Name",
            "description": "<p>名称</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 10\n    },\n    \"Result\": [\n        {\n            \"CaseNo\": \"(2016)新4021执732号\",\n            \"AnNo\": \"(2016)新4021执732号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2016-07-13 12:00:00\",\n            \"OrgNo\": \"00\",\n            \"Province\": \"新疆\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"伊宁县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"450000\"\n        },\n        {\n            \"CaseNo\": \"(2016)粤01执3073号\",\n            \"AnNo\": \"(2016)粤01执3073号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2016-08-18 12:00:00\",\n            \"OrgNo\": \"91654021686****4661\",\n            \"Province\": \"广东\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"广州市中级人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"114040352\"\n        },\n        {\n            \"CaseNo\": \"(2016)新40执49号\",\n            \"AnNo\": \"(2016)新40执49号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2016-05-05 12:00:00\",\n            \"OrgNo\": \"686480466\",\n            \"Province\": \"新疆\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"新疆维吾尔自治区高级人民法院伊犁哈萨克自治州分院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"1040000\"\n        },\n        {\n            \"CaseNo\": \"(2015)伊县法执字第01010号\",\n            \"AnNo\": \"(2015)伊县法执字第01010号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2015-08-04 12:00:00\",\n            \"OrgNo\": \"00\",\n            \"Province\": \"新疆\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"伊宁县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"72157\"\n        },\n        {\n            \"CaseNo\": \"(2016)新4021执622号\",\n            \"AnNo\": \"(2016)新4021执622号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2016-05-25 12:00:00\",\n            \"OrgNo\": \"00\",\n            \"Province\": \"新疆\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"伊宁县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"251198.34\"\n        },\n        {\n            \"CaseNo\": \"(2016)新4021执345号\",\n            \"AnNo\": \"(2016)新4021执345号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2016-03-11 12:00:00\",\n            \"OrgNo\": \"00\",\n            \"Province\": \"新疆\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"伊宁县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"193984\"\n        },\n        {\n            \"CaseNo\": \"(2016)新4021执1113号\",\n            \"AnNo\": \"(2016)新4021执1113号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2016-09-18 12:00:00\",\n            \"OrgNo\": \"00\",\n            \"Province\": \"新疆\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"伊宁县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"6460\"\n        },\n        {\n            \"CaseNo\": \"（2017）新4021执1081号\",\n            \"AnNo\": \"（2017）新4021执1081号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2017-08-03 12:00:00\",\n            \"OrgNo\": \"91654021686****4661\",\n            \"Province\": \"新疆\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"伊宁县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"954000\"\n        },\n        {\n            \"CaseNo\": \"(2016)浙0522执3375号\",\n            \"AnNo\": \"(2016)浙0522执3375号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2016-10-26 12:00:00\",\n            \"OrgNo\": \"68648046-6\",\n            \"Province\": \"浙江\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"长兴县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"6163938\"\n        },\n        {\n            \"CaseNo\": \"(2016)新4021执1005号\",\n            \"AnNo\": \"(2016)新4021执1005号\",\n            \"OrgType\": \"2\",\n            \"LiAnDate\": \"2016-08-22 12:00:00\",\n            \"OrgNo\": \"00\",\n            \"Province\": \"新疆\",\n            \"OrgTypeName\": \"失信企业\",\n            \"ExecuteGov\": \"伊宁县人民法院\",\n            \"Name\": \"新疆庆华能源集团有限公司\",\n            \"BiaoDi\": \"450000\"\n        }\n    ]\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHistoryGethistoryzhixing",
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
    "url": "/HoldingCompany/GetHoldingCompany",
    "title": "控股公司信息",
    "group": "QCC",
    "version": "0.0.1",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "optional": false,
            "field": "fullName",
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
            "field": "KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "CompanyName",
            "description": "<p>公司名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "NameCount",
            "description": "<p>控股公司个数</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Names",
            "description": "<p>控股公司列表</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.Name",
            "description": "<p>公司名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.PercentTotal",
            "description": "<p>投资比例</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.Level",
            "description": "<p>层级数</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.ShortStatus",
            "description": "<p>状态</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.StartDate",
            "description": "<p>成立时间</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.RegistCapi",
            "description": "<p>注册资金</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.ImageUrl",
            "description": "<p>Logo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.EconKind",
            "description": "<p>企业类型</p>"
          },
          {
            "group": "Success 200",
            "type": "object[]",
            "optional": false,
            "field": "Names.Paths",
            "description": "<p>Paths</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.Paths.KeyNo",
            "description": "<p>公司KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.Paths.Name",
            "description": "<p>公司名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.Paths.PercentTotal",
            "description": "<p>投资比例</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.Paths.Level",
            "description": "<p>层级</p>"
          },
          {
            "group": "Success 200",
            "type": "object",
            "optional": false,
            "field": "Names.Oper",
            "description": "<p>Oper</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.Oper.Name",
            "description": "<p>法人名称</p>"
          },
          {
            "group": "Success 200",
            "type": "string",
            "optional": false,
            "field": "Names.Oper.KeyNo",
            "description": "<p>法人对应KeyNo</p>"
          },
          {
            "group": "Success 200",
            "type": "int",
            "optional": false,
            "field": "Names.Oper.CompanyCount",
            "description": "<p>关联公司个数</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "请求成功:",
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 10,\n        \"PageIndex\": 1\n    },\n    \"Result\": {\n        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e\",\n        \"CompanyName\": \"北京小桔科技有限公司\",\n        \"NameCount\": \"47\",\n        \"Names\": [\n            {\n                \"KeyNo\": \"05c090155e36541c83e9ab59ab3f402d\",\n                \"RegistCapi\": \"1000万人民币元\",\n                \"StartDate\": \"2016-03-24 12:00:00\",\n                \"EconKind\": \"有限责任公司（自然人投资或控股的法人独资）\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n                \"Level\": \"1\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"05c090155e36541c83e9ab59ab3f402d\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"嘉兴橙子投资管理有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"p3f038f0b735b9a50fd66c193435f9b0\",\n                    \"CompanyCount\": 7,\n                    \"Name\": \"求非曲\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"存续\",\n                \"Name\": \"嘉兴橙子投资管理有限公司\"\n            },\n            {\n                \"KeyNo\": \"1047b1886e63c9475e10163343a09b76\",\n                \"RegistCapi\": \"200万人民币元\",\n                \"StartDate\": \"2018-03-12 12:00:00\",\n                \"EconKind\": \"有限责任公司(法人独资)\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n                \"Level\": \"1\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"1047b1886e63c9475e10163343a09b76\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"北京滴滴承信科技咨询服务有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"pre3325af8698e0188fa65a334bdd134\",\n                    \"CompanyCount\": 1,\n                    \"Name\": \"张露文\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"在业\",\n                \"Name\": \"北京滴滴承信科技咨询服务有限公司\"\n            },\n            {\n                \"KeyNo\": \"2018201d8e12e8769946032dbf5e7ac1\",\n                \"RegistCapi\": \"100万人民币元\",\n                \"StartDate\": \"2004-04-27 12:00:00\",\n                \"EconKind\": \"有限责任公司（法人独资）\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n                \"Level\": \"3\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"3fe7fa121a61e0d869a52b4752b9e272\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"杭州滴滴汽车服务有限公司\"\n                        },\n                        {\n                            \"KeyNo\": \"a1b0f97ad43b0e721246790556890b99\",\n                            \"Level\": \"2\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"杭州小木吉汽车服务有限公司\"\n                        },\n                        {\n                            \"KeyNo\": \"2018201d8e12e8769946032dbf5e7ac1\",\n                            \"Level\": \"3\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"深圳市伟恒汽车有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"pr98c18a754ba7493fd8a4ddb18953c6\",\n                    \"CompanyCount\": 1,\n                    \"Name\": \"杨志新\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"存续\",\n                \"Name\": \"深圳市伟恒汽车有限公司\"\n            },\n            {\n                \"KeyNo\": \"205a8c9f2bd6b437ce8b3d0bdd3ae62a\",\n                \"RegistCapi\": \"100万人民币元\",\n                \"StartDate\": \"2018-05-31 12:00:00\",\n                \"EconKind\": \"有限责任公司(法人独资)\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n                \"Level\": \"2\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"afb3daf1797df272997f22b143c964f6\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"北京车胜科技有限公司\"\n                        },\n                        {\n                            \"KeyNo\": \"205a8c9f2bd6b437ce8b3d0bdd3ae62a\",\n                            \"Level\": \"2\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"小桔(北京)汽车服务有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"pba983a8d51f856d6977e4d6e1c2e49b\",\n                    \"CompanyCount\": 3,\n                    \"Name\": \"邵韬\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"在业\",\n                \"Name\": \"小桔(北京)汽车服务有限公司\"\n            },\n            {\n                \"KeyNo\": \"25658b066d565464839d7c0b214fd42b\",\n                \"RegistCapi\": \"1000万人民币元\",\n                \"StartDate\": \"2015-07-15 12:00:00\",\n                \"EconKind\": \"有限责任公司（自然人投资或控股的法人独资）\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n                \"Level\": \"2\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"8bd250d6875caa56dc9a6747b49689c0\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"上海吾步信息技术有限公司\"\n                        },\n                        {\n                            \"KeyNo\": \"25658b066d565464839d7c0b214fd42b\",\n                            \"Level\": \"2\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"贵阳吾步数据服务有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"pf9428a8a2afb5057a10f3e4802b9eee\",\n                    \"CompanyCount\": 46,\n                    \"Name\": \"陈汀\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"存续\",\n                \"Name\": \"贵阳吾步数据服务有限公司\"\n            },\n            {\n                \"KeyNo\": \"263ed5f366aa352491f2a9112db02cdd\",\n                \"RegistCapi\": \"40000万人民币元\",\n                \"StartDate\": \"2005-05-19 12:00:00\",\n                \"EconKind\": \"有限责任公司（自然人投资或控股的法人独资）\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n                \"Level\": \"2\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"44d5992e16ff513c91f86c5b0fdf2227\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"滴滴出行科技有限公司\"\n                        },\n                        {\n                            \"KeyNo\": \"263ed5f366aa352491f2a9112db02cdd\",\n                            \"Level\": \"2\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"上海时园科技有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"p30478fe73bc161a5988c4bb77d43f56\",\n                    \"CompanyCount\": 3,\n                    \"Name\": \"刘少荣\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"存续\",\n                \"Name\": \"上海时园科技有限公司\"\n            },\n            {\n                \"KeyNo\": \"274d9311979595d54821e1d9e8d73e36\",\n                \"RegistCapi\": \"500万人民币元\",\n                \"StartDate\": \"2017-11-22 12:00:00\",\n                \"EconKind\": \"有限责任公司(法人独资)\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/274d9311979595d54821e1d9e8d73e36.jpg\",\n                \"Level\": \"1\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"274d9311979595d54821e1d9e8d73e36\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"北京再造科技有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"p661ca55f69289b4e72edac3164e99ff\",\n                    \"CompanyCount\": 2,\n                    \"Name\": \"罗文\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"在业\",\n                \"Name\": \"北京再造科技有限公司\"\n            },\n            {\n                \"KeyNo\": \"2bbaaaf09d9877b8dd851a02ad9600a2\",\n                \"RegistCapi\": \"2000万人民币元\",\n                \"StartDate\": \"2013-10-21 12:00:00\",\n                \"EconKind\": \"有限责任公司（自然人投资或控股的法人独资）\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/2bbaaaf09d9877b8dd851a02ad9600a2.jpg\",\n                \"Level\": \"1\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"2bbaaaf09d9877b8dd851a02ad9600a2\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"上海奇漾信息技术有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"pf9428a8a2afb5057a10f3e4802b9eee\",\n                    \"CompanyCount\": 46,\n                    \"Name\": \"陈汀\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"存续\",\n                \"Name\": \"上海奇漾信息技术有限公司\"\n            },\n            {\n                \"KeyNo\": \"3a079aa5beaf85378a2dba72ec6d563a\",\n                \"RegistCapi\": \"100万人民币元\",\n                \"StartDate\": \"2014-06-12 12:00:00\",\n                \"EconKind\": \"有限责任公司(法人独资)\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n                \"Level\": \"1\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"3a079aa5beaf85378a2dba72ec6d563a\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"北京通达无限科技有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"pf243c8bcd428b850f367092d7f9b34c\",\n                    \"CompanyCount\": 2,\n                    \"Name\": \"李锦飞\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"在业\",\n                \"Name\": \"北京通达无限科技有限公司\"\n            },\n            {\n                \"KeyNo\": \"3d1a9683a46bf88fdb82ba7c88720406\",\n                \"RegistCapi\": \"2000万人民币元\",\n                \"StartDate\": \"2018-04-16 12:00:00\",\n                \"EconKind\": \"有限责任公司(法人独资)\",\n                \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/default.jpg\",\n                \"Level\": \"3\",\n                \"Paths\": [\n                    [\n                        {\n                            \"KeyNo\": \"44d5992e16ff513c91f86c5b0fdf2227\",\n                            \"Level\": \"1\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"滴滴出行科技有限公司\"\n                        },\n                        {\n                            \"KeyNo\": \"483a812cab4c1ab3b9c63acbf0d1e357\",\n                            \"Level\": \"2\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"迪润（天津）科技有限公司\"\n                        },\n                        {\n                            \"KeyNo\": \"3d1a9683a46bf88fdb82ba7c88720406\",\n                            \"Level\": \"3\",\n                            \"PercentTotal\": \"100%\",\n                            \"Name\": \"西安小木吉网络科技有限公司\"\n                        }\n                    ]\n                ],\n                \"Oper\": {\n                    \"KeyNo\": \"pr4193775c4f2e113f7537bc00b81aa8\",\n                    \"CompanyCount\": 1,\n                    \"Name\": \"高翔\"\n                },\n                \"PercentTotal\": \"100%\",\n                \"ShortStatus\": \"在业\",\n                \"Name\": \"西安小木吉网络科技有限公司\"\n            }\n        ]\n    }\n}",
          "type": "json"
        }
      ]
    },
    "filename": "src/main/java/com/beeasy/zed/QccService.java",
    "groupTitle": "QCC",
    "name": "GetHoldingcompanyGetholdingcompany",
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
            "field": "fullName",
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
            "field": "fullName",
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
            "field": "EEquityFreezeDetail.ExecutionMatters",
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
            "field": "fullName",
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
            "field": "fullName",
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
