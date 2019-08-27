package com.beeasy.hzlink.model;
import java.math.*;
import java.util.Date;
import java.sql.Timestamp;
import org.beetl.sql.core.annotatoin.Table;

import org.beetl.sql.core.annotatoin.AssignID;

/*
* 
* gen by beetlsql 2019-06-27
*/
@Table(name="T_GROUP_CUS_LIST")
public class TGroupCusList   {

	/*
	流水号
	*/
    @AssignID(value = "simple")
	private Long id ;
	/*
	证件号码
	*/
	private String cert_code ;
	/*
	客户名称
	*/
	private String cus_name ;
	/*
	数据标志
	*/
	private String data_flag ;
	/*
	关联人证件号码
	*/
	private String link_cert_code ;
	/*
	关联人名称
	*/
	private String link_name ;
	/*
	关联类型
	*/
	private String link_rule ;
	/*
	备注信息1（关系说明）
	*/
	private String remark_1 ;
	/*
	备注信息2（相关信息）
	*/
	private String remark_2 ;
	/*
	备注信息3（控制程度）
	*/
	private String remark_3 ;
	/*
	创建时间
	*/
	private Date add_time ;

	public TGroupCusList() {
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
	* 数据标志
	*@return
	*/
	public String getData_flag(){
		return  data_flag;
	}
	/**
	* 数据标志
	*@param  data_flag
	*/
	public void setData_flag(String data_flag ){
		this.data_flag = data_flag;
	}

	/**
	* 关联人证件号码
	*@return
	*/
	public String getLink_cert_code(){
		return  link_cert_code;
	}
	/**
	* 关联人证件号码
	*@param  link_cert_code
	*/
	public void setLink_cert_code(String link_cert_code ){
		this.link_cert_code = link_cert_code;
	}

	/**
	* 关联人名称
	*@return
	*/
	public String getLink_name(){
		return  link_name;
	}
	/**
	* 关联人名称
	*@param  link_name
	*/
	public void setLink_name(String link_name ){
		this.link_name = link_name;
	}

	/**
	* 关联类型
	*@return
	*/
	public String getLink_rule(){
		return  link_rule;
	}
	/**
	* 关联类型
	*@param  link_rule
	*/
	public void setLink_rule(String link_rule ){
		this.link_rule = link_rule;
	}

	/**
	* 备注信息1（关系说明）
	*@return
	*/
	public String getRemark_1(){
		return  remark_1;
	}
	/**
	* 备注信息1（关系说明）
	*@param  remark_1
	*/
	public void setRemark_1(String remark_1 ){
		this.remark_1 = remark_1;
	}

	/**
	* 备注信息2（相关信息）
	*@return
	*/
	public String getRemark_2(){
		return  remark_2;
	}
	/**
	* 备注信息2（相关信息）
	*@param  remark_2
	*/
	public void setRemark_2(String remark_2 ){
		this.remark_2 = remark_2;
	}

	/**
	* 备注信息3（控制程度）
	*@return
	*/
	public String getRemark_3(){
		return  remark_3;
	}
	/**
	* 备注信息3（控制程度）
	*@param  remark_3
	*/
	public void setRemark_3(String remark_3 ){
		this.remark_3 = remark_3;
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
