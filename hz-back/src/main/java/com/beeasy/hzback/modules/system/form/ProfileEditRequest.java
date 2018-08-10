//package com.beeasy.hzback.modules.system.form;
//
//import lombok.Data;
//import org.hibernate.validator.constraints.Email;
//
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Pattern;
//
//@Data
//public class ProfileEditRequest {
////    @NotNull(message = "用户ID不能为空")
////    Long id;
//
//    @Pattern(regexp = "^[\\u4E00-\\u9FA5\\uf900-\\ufa2d·s]{2,20}$", message = "请输入正确的姓名")
//    String trueName;
//
//    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$", message = "请输入正确的手机号码")
//    String phone;
//
//    @Email(message = "请输入正确的邮箱")
//    String email;
//}
