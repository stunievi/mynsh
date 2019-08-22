package com.beeasy.hzlink.model;

import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;
import java.util.Date;

/**
 * 关联方清单表（包含股东关联）
 */
@Table(name="T_RELATED_PARTY_LIST")
public class RelatedPartyList {

    @AssignID("simple")
    Long id;

    Date add_time;
    String related_name; //关联人名称
    String link_rule;    // 关联类型
    String link_info;   // 关联信息（要素）
    String cert_code;    // 关联人证件号码
    String remark_1;      //备注信息1（关系说明）
    String remark_2;      //备注信息2（相关信息）
    String remark_3;      //备注信息3（控制程度）
    String data_flag;      //数据标志

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    public String getRelated_name() {
        return related_name;
    }

    public void setRelated_name(String related_name) {
        this.related_name = related_name;
    }

    public String getLink_rule() {
        return link_rule;
    }

    public void setLink_rule(String link_rule) {
        this.link_rule = link_rule;
    }

    public String getLink_info() {
        return link_info;
    }

    public void setLink_info(String link_info) {
        this.link_info = link_info;
    }

    public String getCert_code() {
        return cert_code;
    }

    public void setCert_code(String cert_code) {
        this.cert_code = cert_code;
    }

    public String getRemark_1() {
        return remark_1;
    }

    public void setRemark_1(String remark_1) {
        this.remark_1 = remark_1;
    }

    public String getRemark_2() {
        return remark_2;
    }

    public void setRemark_2(String remark_2) {
        this.remark_2 = remark_2;
    }

    public String getRemark_3() {
        return remark_3;
    }

    public void setRemark_3(String remark_3) {
        this.remark_3 = remark_3;
    }

    public String getData_flag() {
        return data_flag;
    }

    public void setData_flag(String data_flag) {
        this.data_flag = data_flag;
    }
}
