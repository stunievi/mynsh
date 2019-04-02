//package com.beeasy.hzqcc.controller;
//
//import com.beeasy.hzqcc.service.QccService;
//import com.beeasy.mscommon.Result;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/qcc")
//public class QccDataController {
//
//    @Autowired
//    QccService qccService;
//
//    // 企业关键字精确获取详细信息(Master)
//    @GetMapping(value = "/ECI/GetDetailsByName")
//    Result ECI_GetDetailsByName(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.ECI_GetDetailsByName(param)
//        );
//    }
//    // 企业关键字精确获取详细信息(basic)
//    @GetMapping(value = "/ECI/GetBasicDetailsByName")
//    Result ECI_GetBasicDetailsByName(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.ECI_GetBasicDetailsByName(param)
//        );
//    }
//
//    // 裁判文书列表
//    @GetMapping(value = "/JudgeDoc/SearchJudgmentDoc")
//    Result JudgeDoc_SearchJudgmentDoc(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.JudgeDoc_SearchJudgmentDoc(param)
//        );
//    }
//    // 裁判文书详情
//    @GetMapping(value = "/JudgeDoc/GetJudgementDetail")
//    Result JudgeDoc_GetJudgementDetail(
//            @RequestParam() Map param
//    ){
//
//        return Result.ok(
//                qccService.JudgeDoc_GetJudgementDetail(param)
//        );
//    }
//    // 开庭公告查询
//    @GetMapping(value = "/CourtAnno/SearchCourtNotice")
//    Result CourtAnno_SearchCourtNotice(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.CourtAnno_SearchCourtNotice(param)
//        );
//    }
//    // 开庭公告详情查询
//    @GetMapping(value = "/CourtAnno/GetCourtNoticeInfo")
//    Result CourtAnno_GetCourtNoticeInfo(
//            @RequestParam() Map param
//    ){
//
//        return Result.ok(
//                qccService.CourtAnno_GetCourtNoticeInfo(param)
//        );
//    }
//    // 失信列表
//    @GetMapping(value = "/Court/SearchShiXin")
//    Result Court_SearchShiXin(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.Court_SearchShiXin(param)
//        );
//    }
//    // 执行列表
//    @GetMapping(value = "/Court/SearchZhiXing")
//    Result Court_SearchZhiXing(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.Court_SearchZhiXing(param)
//        );
//    }
//    // 法院公告列表信息
//    @GetMapping(value = "/CourtNotice/SearchCourtAnnouncement")
//    Result CourtNotice_SearchCourtAnnouncement(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.CourtNotice_SearchCourtAnnouncement(param)
//        );
//    }
//    // 法院详情
//    @GetMapping(value = "/CourtNotice/SearchCourtAnnouncementDetail")
//    Result CourtNotice_SearchCourtAnnouncementDetail(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.CourtNotice_SearchCourtAnnouncementDetail(param)
//        );
//    }
//    @GetMapping(value = "/JudicialAssistance/GetJudicialAssistance")
//    Result JudicialAssistance_GetJudicialAssistance(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.JudicialAssistance_GetJudicialAssistance(param)
//        );
//    }
//
//    // 历史工商信息
//    @GetMapping(value = "/History/GetHistorytEci")
//    Result History_GetHistorytEci(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytEci(param)
//        );
//    }
//    // 历史对外投资
//    @GetMapping(value = "/History/GetHistorytInvestment")
//    Result History_GetHistorytInvestment(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytInvestment(param)
//        );
//    }
//    // 历史股东
//    @GetMapping(value = "/History/GetHistorytShareHolder")
//    Result History_GetHistorytShareHolder(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytShareHolder(param)
//        );
//    }
//    // 历史失信查询
//    @GetMapping(value = "/History/GetHistoryShiXin")
//    Result History_GetHistoryShiXin(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistoryShiXin(param)
//        );
//    }
//    // 历史被执行
//    @GetMapping(value = "/History/GetHistoryZhiXing")
//    Result History_GetHistoryZhiXing(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistoryZhiXing(param)
//        );
//    }
//    // 历史法院公告
//    @GetMapping(value = "/History/GetHistorytCourtNotice")
//    Result History_GetHistorytCourtNotice(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytCourtNotice(param)
//        );
//    }
//    // 历史裁判文书
//    @GetMapping(value = "/History/GetHistorytJudgement")
//    Result History_GetHistorytJudgement(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytJudgement(param)
//        );
//    }
//    // 历史开庭公告
//    @GetMapping(value = "/History/GetHistorytSessionNotice")
//    Result History_GetHistorytSessionNotice(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytSessionNotice(param)
//        );
//    }
//    // 历史动产抵押
//    @GetMapping(value = "/History/GetHistorytMPledge")
//    Result History_GetHistorytMPledge(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytMPledge(param)
//        );
//    }
//    // 历史股权出质
//    @GetMapping(value = "/History/GetHistorytPledge")
//    Result History_GetHistorytPledge(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytPledge(param)
//        );
//    }
//    // 历史行政处罚
//    @GetMapping(value = "/History/GetHistorytAdminPenalty")
//    Result History_GetHistorytAdminPenalty(
//            @RequestParam() Map param
//
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytAdminPenalty(param)
//        );
//    }
//    // 历史行政许可
//    @GetMapping(value = "/History/GetHistorytAdminLicens")
//    Result History_GetHistorytAdminLicens(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.History_GetHistorytAdminLicens(param)
//        );
//    }
//    // 获取动产抵押信息
//    @GetMapping(value = "/ChattelMortgage/GetChattelMortgage")
//    Result ChattelMortgage_GetChattelMortgage(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.ChattelMortgage_GetChattelMortgage(param)
//        );
//    }
//    // 司法拍卖列表
//    @GetMapping(value = "/JudicialSale/GetJudicialSaleList")
//    Result JudicialSale_GetJudicialSaleList(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.JudicialSale_GetJudicialSaleList(param)
//        );
//    }
//    // 司法拍卖详情
//    @GetMapping(value = "/JudicialSale/GetJudicialSaleDetail")
//    Result JudicialSale_GetJudicialSaleDetail(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.JudicialSale_GetJudicialSaleDetail(param)
//        );
//    }
//    // 获取土地抵押列表
//    @GetMapping(value = "/LandMortgage/GetLandMortgageList")
//    Result LandMortgage_GetLandMortgageList(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.LandMortgage_GetLandMortgageList(param)
//        );
//    }
//    // 获取土地抵押详情
//    @GetMapping(value = "/LandMortgage/GetLandMortgageDetails")
//    Result LandMortgage_GetLandMortgageDetails(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.LandMortgage_GetLandMortgageDetails(param)
//        );
//    }
//    // 获取环保处罚列表
//    @GetMapping(value = "/EnvPunishment/GetEnvPunishmentList")
//    Result EnvPunishment_GetEnvPunishmentList(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.EnvPunishment_GetEnvPunishmentList(param)
//        );
//    }
//    // 获取环保处罚详情
//    @GetMapping(value = "/EnvPunishment/GetEnvPunishmentDetails")
//    Result EnvPunishment_GetEnvPunishmentDetails(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.EnvPunishment_GetEnvPunishmentDetails(param)
//        );
//    }
//    // 企业图谱
//    @GetMapping(value = "/ECIRelation/GenerateMultiDimensionalTreeCompanyMap")
//    Result ECIRelationV4_GenerateMultiDimensionalTreeCompanyMap(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.ECIRelation_GenerateMultiDimensionalTreeCompanyMap(param)
//        );
//    }
//
//
//    // 控股公司
//    @GetMapping(value = "/HoldingCompany/GetHoldingCompany")
//    Result HoldingCompany_GetHoldingCompany(
//            @RequestParam() Map param
//    ){
//        return Result.ok(
//                qccService.HoldingCompany_GetHoldingCompany(param)
//        );
//    }
//}
