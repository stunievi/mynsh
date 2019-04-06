define({ "api": [
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
          "content": "{\n    \"Status\": \"200\",\n    \"Message\": \"查询成功\",\n    \"Paging\": {\n        \"PageSize\": 10,\n        \"TotalRecords\": 1,\n        \"PageIndex\": 1\n    },\n    \"Result\": [\n        {\n            \"CaseNo\": \"资环罚[2017]48号\",\n            \"PunishGov\": \"资阳市环境保护局\",\n            \"PunishDate\": \"2017-09-30 12:00:00\",\n            \"IllegalType\": \"\",\n            \"Id\": \"17e3156d060094b649c7f8be77d15fa2\",\n            \"BeetlRn\": 1\n        }\n    ]\n}",
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
  }
] });
