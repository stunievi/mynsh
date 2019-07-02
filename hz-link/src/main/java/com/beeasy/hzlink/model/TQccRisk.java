package com.beeasy.hzlink.model;
import java.math.*;
import java.util.Date;
import java.sql.Timestamp;
import org.beetl.sql.core.annotatoin.Table;

import org.beetl.sql.core.annotatoin.AssignID;

/*
* 
* gen by beetlsql 2019-07-01
*/
@Table(name="T_QCC_RISK")
public class TQccRisk   {

	/*
	流水号
	*/
    @AssignID()
	private Long id ;
	/*
	证件号码
	*/
	private String cert_code ;
	/*
	客户经理
	*/
	private String cust_mgr ;
	/*
	客户号
	*/
	private String cus_id ;
	/*
	客户名称
	*/
	private String cus_name ;
	/*
	主管机构
	*/
	private String main_br_id ;
	/*
	风险信息
	*/
	private String risk_info ;
	/*
	创建时间
	*/
	private Date add_time ;

	public TQccRisk() {
	}

	/**
	* 流水号
	*@return
	*/
	public Long getId(){
		return  id;
	}
	/**
	* 流水号
	*@param  id
	*/
	public void setId(Long id ){
		this.id = id;
	}

	/**
	* 证件号码
	*@return
	*/
	public String getCert_code(){
		return  cert_code;
	}
	/**
	* 证件号码
	*@param  cert_code
	*/
	public void setCert_code(String cert_code ){
		this.cert_code = cert_code;
	}

	/**
	* 客户经理
	*@return
	*/
	public String getCust_mgr(){
		return  cust_mgr;
	}
	/**
	* 客户经理
	*@param  cust_mgr
	*/
	public void setCust_mgr(String cust_mgr ){
		this.cust_mgr = cust_mgr;
	}

	/**
	* 客户号
	*@return
	*/
	public String getCus_id(){
		return  cus_id;
	}
	/**
	* 客户号
	*@param  cus_id
	*/
	public void setCus_id(String cus_id ){
		this.cus_id = cus_id;
	}

	/**
	* 客户名称
	*@return
	*/
	public String getCus_name(){
		return  cus_name;
	}
	/**
	* 客户名称
	*@param  cus_name
	*/
	public void setCus_name(String cus_name ){
		this.cus_name = cus_name;
	}

	/**
	* 主管机构
	*@return
	*/
	public String getMain_br_id(){
		return  main_br_id;
	}
	/**
	* 主管机构
	*@param  main_br_id
	*/
	public void setMain_br_id(String main_br_id ){
		this.main_br_id = main_br_id;
	}

	/**
	* 风险信息
	*@return
	*/
	public String getRisk_info(){
		return  risk_info;
	}
	/**
	* 风险信息
	*@param  risk_info
	*/
	public void setRisk_info(String risk_info ){
		this.risk_info = risk_info;
	}

	/**
	* 创建时间
	*@return
	*/
	public Date getAdd_time(){
		return  add_time;
	}
	/**
	* 创建时间
	*@param  add_time
	*/
	public void setAdd_time(Date add_time ){
		this.add_time = add_time;
	}


}
