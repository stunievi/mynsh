package com.beeasy.hzlink.model;
import java.math.*;
import java.util.Date;
import java.sql.Timestamp;
import org.beetl.sql.core.annotatoin.Table;

import org.beetl.sql.core.annotatoin.AssignID;

/*
* 
* gen by beetlsql 2019-06-20
*/
@Table(name="T_SYSTEM_VARIABLE")
public class TSystemVariable   {

    @AssignID()
	private Long id ;
	private Integer can_delete ;
	private String var_name ;
	private String var_value ;

	public TSystemVariable() {
	}

	public Long getId(){
		return  id;
	}
	public void setId(Long id ){
		this.id = id;
	}

	public Integer getCan_delete(){
		return  can_delete;
	}
	public void setCan_delete(Integer can_delete ){
		this.can_delete = can_delete;
	}

	public String getVar_name(){
		return  var_name;
	}
	public void setVar_name(String var_name ){
		this.var_name = var_name;
	}

	public String getVar_value(){
		return  var_value;
	}
	public void setVar_value(String var_value ){
		this.var_value = var_value;
	}


}
