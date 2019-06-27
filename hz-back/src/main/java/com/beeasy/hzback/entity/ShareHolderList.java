package com.beeasy.hzback.entity;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import java.util.Date;

/**
 * 股东明细表
 */
@Table(name="T_SHARE_HOLDER_LIST")
@Data
public class ShareHolderList {
    @AssignID("simple")
    Long id;

    Date addTime;
    String openBrId; //开户机构
    String openBrName;    // 机构名称
    String openDate;   // 开户日期
    String gdType;    // 股东类型
    String cusName;      //户名
    String gjpzhm;      //股金凭证号码
    String gpzh;      //股金/股票账号
    String zgg;      //资格股
    String tzg;      //投资股
    String gp;      //股票
    String gfhj;      //股份合计
    String jszh;      //结算帐号（分红关联帐号）
    String fhzhkhjg;      //分红账号开户机构
    String fhzhzt;      //分红账号状态
    String certType;      //证件类型
    String certCode;      //证件号码
    String indivHouhRegAdd;      //户口地址
    String postAddr;      //联系地址
    String phone;      //股东联系电话
    String mobile;      //移动电话
    String indivComPhn;      //单位电话
    String fphone;      //家庭电话
    String faxCode;      //传真
    String isTurn;      //是否转股票
    String gqdjtgqqFlg;      //股权登记托管确权标志
}
