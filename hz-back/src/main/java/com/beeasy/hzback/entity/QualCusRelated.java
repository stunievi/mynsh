package com.beeasy.hzback.entity;

import java.math.*;
import java.util.Date;
import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.annotatoin.Table;

import org.beetl.sql.core.annotatoin.AssignID;

@Table(name="T_QUAL_CUS_RELATED")
@Data
public class QualCusRelated extends ValidGroup {

	/*
	流水号
	*/
    @AssignID()
	private Long id ;
	/*
	地址信息
	*/
	private String addrInfo ;
	/*
	证件号码
	*/
	private String certCode ;
	/*
	证件类型
	*/
	private String certType ;
	/*
	客户名称
	*/
	private String cusName ;
	/*
	取数规则说明
	*/
	private String getRuleInfo ;
	/*
	操作者
	*/
	private long operator ;
	/*
	创建时间
	*/
	private Date addTime ;

	@Override
	public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
		Date nowDate = new Date();
		long uid = AuthFilter.getUid();
		object.put("uid", uid);
		switch (action) {
			case "getDList":
				return sqlManager.select("link.查询资质客户关联方",JSONObject.class, object);
		}
		return null;
	}
}
