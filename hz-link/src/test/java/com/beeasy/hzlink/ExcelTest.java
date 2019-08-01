package com.beeasy.hzlink;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.llyb120.nami.core.Json.a;
import static com.github.llyb120.nami.core.Json.o;

public class ExcelTest {

    @Test
    public void test() throws IOException {
        Context context = new Context();
        context.putVar("values", a(
                o("name", "fuck")
        ));
        var bs = IoUtil.read(new FileInputStream("d:/template.xls"));
        var sw = new StringWriter();
        var baos = new ByteArrayOutputStream();
        JxlsHelper.getInstance().processTemplate(new FileInputStream("d:/template.xls"), new FileOutputStream("d:/export.xls"), context);
//        XLSTransformer transformer = new XLSTransformer();
//        var bean = o();
//        bean.put("values",
//        transformer.transformXLS("C:\\Users\\bin\\Documents\\WeChat Files\\llyb120\\FileStorage\\File\\2019-06\\软需附件(1)\\软需附件\\附件7-股东关联明细导出模板.xls", bean, ("d:/export.xls"));
    }

    String s = "{\n" +
            "\"Status\": \"200\",\n" +
            "\"Message\": \"查询成功\",\n" +
            "\"Result\": {\n" +
            "\"eachData\": [{\n" +
            "\"sheetEngName\": \"jingyingyichang\",\n" +
            "\"sheetName\": \"经营异常1\",\n" +
            "\"dataList\": [{\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"InnerCompanyName\": \"xxxxxxxxxxxxxxxxxxx\",\n" +
            "\"InnerCompanyNo\": \"f8802efbc17dc731ee049bf62ca7d8b3\",\n" +
            "\"AddDate\": \"2016-07-12 12:00:00\",\n" +
            "\"RemoveDate\": \"2016-11-25 12:00:00\",\n" +
            "\"InnerId\": \"5d1dbbce40da1216e351673d\",\n" +
            "\"InnerCompanyId\": null,\n" +
            "\"AddReason\": \"未依照《企业信息公示暂行条例》第八条规定的期限公示年度报告的\",\n" +
            "\"InnerCompanyCode\": null,\n" +
            "\"RemoveDecisionOffice\": \"惠阳区工商行政管理局\",\n" +
            "\"RomoveReason\": \"列入经营异常名录3年内且依照《经营异常名录管理办法》第六条规定被列入经营异常名录的企业，可以在补报未？\",\n" +
            "\"DecisionOffice\": \"惠阳区工商行政管理局\"\n" +
            "}]\n" +
            "},\n" +
            "{\n" +
            "\"sheetEngName\": \"tudidiya\",\n" +
            "\"sheetName\": \"土地抵押6\",\n" +
            "\"dataList\": [{\n" +
            "\"OnBoardStartTime\": \"2010-01-11 12:00:00\",\n" +
            "\"InnerCompanyName\": \"凸凸凸凸凸凸\",\n" +
            "\"Re1KeyNo\": \"1bb39c30c1a3c4f984273eb7f20895ff\",\n" +
            "\"MortgagorNature\": \"私营\",\n" +
            "\"ObligeeNo\": \"惠阳他项（2010）第024D号\",\n" +
            "\"OnBoardEndTime\": \"2013-01-10 12:00:00\",\n" +
            "\"Address\": \"淡水南门大街\",\n" +
            "\"Re1Name\": \"中国建设银行股份有限公司惠州惠阳支行\",\n" +
            "\"UsufructNo\": \"惠阳国用（2005）第0100148号\",\n" +
            "\"MortgagePurpose\": \"商服用地\",\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"StartDate\": \"2010-01-11 12:00:00\",\n" +
            "\"LandNo\": \"0309246\",\n" +
            "\"Re2Name\": \"惠州市宏耀实业有限公司\",\n" +
            "\"Re2KeyNo\": \"f8802efbc17dc731ee049bf62ca7d8b3\",\n" +
            "\"LandSign\": null,\n" +
            "\"AdministrativeArea\": \"441303000\",\n" +
            "\"InnerId\": \"5d1dbbce40da1216e3516659\",\n" +
            "\"MortgagePrice\": \"52.1900\",\n" +
            "\"EndDate\": \"2013-01-10 12:00:00\",\n" +
            "\"Acreage\": \"0.0636\",\n" +
            "\"NatureAndType\": \"国有、出让\",\n" +
            "\"Id\": \"55548c47ef404dda15450d5bb1e7c094\",\n" +
            "\"AssessmentPrice\": \"104.3800\",\n" +
            "\"MortgageAcreage\": \"0.0636\"\n" +
            "},\n" +
            "{\n" +
            "\"OnBoardStartTime\": \"2010-01-11 12:00:00\",\n" +
            "\"InnerCompanyName\": \"惠州市宏耀实业有限公司\",\n" +
            "\"Re1KeyNo\": \"1bb39c30c1a3c4f984273eb7f20895ff\",\n" +
            "\"MortgagorNature\": \"私营\",\n" +
            "\"ObligeeNo\": \"惠阳他项（2010）第024A号\",\n" +
            "\"OnBoardEndTime\": \"2013-01-10 12:00:00\",\n" +
            "\"Address\": \"淡水南门大街\",\n" +
            "\"Re1Name\": \"中国建设银行股份有限公司惠州惠阳支行\",\n" +
            "\"UsufructNo\": \"惠阳国用（2005）第0100150号\",\n" +
            "\"MortgagePurpose\": \"住宅用地\",\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"StartDate\": \"2010-01-11 12:00:00\",\n" +
            "\"LandNo\": \"0309243\",\n" +
            "\"Re2Name\": \"惠州市宏耀实业有限公司\",\n" +
            "\"Re2KeyNo\": \"f8802efbc17dc731ee049bf62ca7d8b3\",\n" +
            "\"LandSign\": null,\n" +
            "\"AdministrativeArea\": \"441303000\",\n" +
            "\"InnerId\": \"5d1dbbce40da1216e35166a2\",\n" +
            "\"MortgagePrice\": \"19.1700\",\n" +
            "\"EndDate\": \"2013-01-10 12:00:00\",\n" +
            "\"Acreage\": \"0.0235\",\n" +
            "\"NatureAndType\": \"国有、出让\",\n" +
            "\"Id\": \"b1ad5c39367ff79c20d20105db917eb8\",\n" +
            "\"AssessmentPrice\": \"38.3400\",\n" +
            "\"MortgageAcreage\": \"0.0235\"\n" +
            "}\n" +
            "]\n" +
            "},\n" +
            "{\n" +
            "\"sheetEngName\": \"huanbaochufa\",\n" +
            "\"sheetName\": \"环保处罚3\",\n" +
            "\"dataList\": [{\n" +
            "\"CaseNo\": \"惠市环(惠城)罚[2017]102号\",\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"InnerCompanyName\": \"惠州市家兴畜牧有限公司\",\n" +
            "\"PunishGov\": \"广东省惠州市惠城区环保局\",\n" +
            "\"PunishDate\": \"2017-05-11 12:00:00\",\n" +
            "\"InnerId\": \"5d1dbbce40da1216e351685f\",\n" +
            "\"PunishmentResult\": \"超标或超总量排污、违反限期治理制度；《中华人民共和国水污染防治法》第七十四条和《惠州市环境保护局环境行政处罚自由裁量权裁量标准（2016年版）》；罚款；罚款：1.063万元；其他：责令立即改正违法行为\",\n" +
            "\"PunishBasis\": null,\n" +
            "\"IllegalType\": null,\n" +
            "\"PunishReason\": null,\n" +
            "\"Id\": \"32d1e5916a987a5beb428cb3e1ced13a\",\n" +
            "\"Implementation\": null\n" +
            "},\n" +
            "{\n" +
            "\"CaseNo\": \"惠市环(惠城)罚[2016]96号\",\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"InnerCompanyName\": \"惠州市家兴畜牧有限公司\",\n" +
            "\"PunishGov\": \"广东省惠州市惠城区环保局\",\n" +
            "\"PunishDate\": \"2016-03-29 12:00:00\",\n" +
            "\"InnerId\": \"5d1dbbce40da1216e3516882\",\n" +
            "\"PunishmentResult\": \"超标或超总量排污、违反限期治理制度；?《中华人民共和国水污染防治法》第七十四条和《惠州市环境保护局环境行政处罚自由裁量权裁量标准（2015年版）》；罚款；?罚款：0.464万元；其他：责令立即改正?\",\n" +
            "\"PunishBasis\": null,\n" +
            "\"IllegalType\": null,\n" +
            "\"PunishReason\": null,\n" +
            "\"Id\": \"3a612313d78bfb0784bc9f53a1c90c52\",\n" +
            "\"Implementation\": null\n" +
            "}\n" +
            "]\n" +
            "}, {\n" +
            "\"sheetEngName\": \"dongchandiya\",\n" +
            "\"sheetName\": \"动产抵押2\",\n" +
            "\"dataList\": [{\n" +
            "\"Ex2Amount\": 2000000.00,\n" +
            "\"RegisterOffice\": \"广东省惠州市惠城区工商行政管理局\",\n" +
            "\"Ex2Kind\": \"人民币\",\n" +
            "\"Ex1RegistDate\": null,\n" +
            "\"InnerId\": \"5d1dbbce40da1216e35168d1\",\n" +
            "\"RegisterDate\": \"2015-11-12 12:00:00\",\n" +
            "\"DebtSecuredAmount\": \"200万\",\n" +
            "\"Ex3CancelDate\": \"2015-11-12 12:00:00\",\n" +
            "\"Ex2AssuranceScope\": \"主债权及其利息、违约金、损害赔偿金、保管担保财产和实现担保物权的费用\",\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"Ex1RegistOffice\": \"广东省惠州市惠城区工商行政管理局\",\n" +
            "\"Ex1RegistNo\": \"0752惠城20141222030\",\n" +
            "\"Ex2FulfillObligation\": \"至2015-12-15\",\n" +
            "\"RegisterNo\": \"0752惠城20141222030\",\n" +
            "\"PublicDate\": null,\n" +
            "\"Ex2Remark\": \"\",\n" +
            "\"Ex3CancelReason\": \"\"\n" +
            "},\n" +
            "{\n" +
            "\"Ex2Amount\": 2000000.00,\n" +
            "\"RegisterOffice\": \"广东省惠州市惠城区工商行政管理局\",\n" +
            "\"Ex2Kind\": \"人民币\",\n" +
            "\"Ex1RegistDate\": null,\n" +
            "\"InnerId\": \"5d1dbbce40da1216e3516932\",\n" +
            "\"RegisterDate\": \"2015-07-28 12:00:00\",\n" +
            "\"DebtSecuredAmount\": \"200万\",\n" +
            "\"Ex3CancelDate\": null,\n" +
            "\"Ex2AssuranceScope\": \"主债权及其利息、违约金、损害赔偿金、保管担保财产和实现担保物权的费用\",\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"Ex1RegistOffice\": \"广东省惠州市惠城区工商行政管理局\",\n" +
            "\"Ex1RegistNo\": \"0752惠城20150728019\",\n" +
            "\"Ex2FulfillObligation\": \"至2016-07-26\",\n" +
            "\"RegisterNo\": \"0752惠城20150728019\",\n" +
            "\"PublicDate\": null,\n" +
            "\"Ex2Remark\": \"\",\n" +
            "\"Ex3CancelReason\": \"\"\n" +
            "}\n" +
            "]\n" +
            "}, {\n" +
            "\"sheetEngName\": \"sifapaimai\",\n" +
            "\"sheetName\": \"司法拍卖2\",\n" +
            "\"dataList\": [{\n" +
            "\"Context\": \"<p style=\\\"background: white;text-align: center;line-height: 22.0px;text-indent: 37.0px;\\\"><strong><span style=\\\"color: black;font-size: 21.0px;\\\">惠阳区人民法院关于拍卖</span></strong><strong><span style=\\\"color: red;font-size: 21.0px;\\\">被执行人惠州市龙海广发食品有限公司名下位于惠州市惠城区汝湖仍图仍中村新二小组岭下的地上厂房、宿舍房地产及机器设备</span></strong><strong><span style=\\\"color: black;font-size: 21.0px;\\\">的公告（一拍）</span></strong><strong></strong></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">惠州市惠阳区人民法院将于</span><span style=\\\"color: red;font-size: 19.0px;\\\">2018</span><span style=\\\"color: red;font-size: 19.0px;\\\">年<span>4</span>月<span>21</span>日<span>10</span>时至<span>2018</span>年<span>4</span>月<span>22</span>日<span>10</span></span><span style=\\\"color: black;font-size: 19.0px;\\\">时止<strong><span>（延时除外）</span></strong>在惠阳区人民法院淘宝网司法拍卖网络平台上进行公开拍卖活动（账户名：惠州市惠阳区人民法院，法院主页网址：<span><a target=\\\"_blank\\\"><span style=\\\"color: #0000ff;\\\">https://sf.taobao.com/0752/05</span></a>?</span>），现公告如下：</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>一、</span></strong><span>拍卖标的：</span></span><strong><span style=\\\"color: red;font-size: 21.0px;\\\">被执行人惠州市龙海广发食品有限公司名下位于惠州市惠城区汝湖仍图仍中村新二小组岭下的地上厂房、宿舍房地产及机器设备。</span></strong></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: red;font-size: 19.0px;\\\">起拍价：<span>11787511</span>元</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: red;font-size: 19.0px;\\\">保证金：<span>2357502</span>元</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: red;font-size: 19.0px;\\\">增价幅度：<span>58000</span>元。</span></p><p style=\\\"line-height: 27.0px;text-indent: 37.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>二、</span></strong><span>竞买人条件：凡具备完全民事行为能力的公民、法人和其他组织均可参加竞买。如参与竞买人未开设淘宝账户，可委托代理人（具备完全民事行为能力的自然人）进行，竞买人（法定代表人、其他组织的负责人）须与委托代理人一同于拍卖开始前<span>5</span>个工作日内携带委托书、双方身份证到我院办理委托备案手续，资格经法院确认后方能进行委托。拍卖当天不接收委托。如委托手续不全，竞买活动认定为委托代理人的个人行为。</span></span></p><p style=\\\"line-height: 24.0px;text-indent: 19.0px;\\\"><span style=\\\"color: #666666;font-size: 19.0px;\\\">因不符合条件参加竞买的，由竞买人自行承担相应的法律责任。买受人应自行了解当地限购政策，如因限购原因造成无法办理产权过户手续的由买受人自行承担不利后果，港澳台及外籍人士须符合相关房地产政策规定。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 32.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>三、</span></strong><span>咨询的时间：自</span></span><span style=\\\"color: red;font-size: 19.0px;\\\">2018</span><span style=\\\"color: red;font-size: 19.0px;\\\">年<span>3</span>月<span>21</span>日起至<span>2018</span>年<span>4</span>月<span>16</span>日止</span><span style=\\\"color: #666666;font-size: 19.0px;\\\">（节假日休息）接受咨询。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 32.0px;\\\"><span style=\\\"color: #666666;font-size: 19.0px;\\\">不动产按现状拍卖，有意者请亲自实地看样，未看样的竞买人视为对本标的实物现状的确认，责任自负，法院不负责清场交付。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 32.0px;\\\"><span style=\\\"color: #666666;font-size: 19.0px;\\\">拍卖动产，标的物在法院的，可在咨询时间范围内电话联系，届时统一时间查看。标的物在其他的地方的，自行前往查看。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 32.0px;\\\"><span style=\\\"color: #666666;font-size: 19.0px;\\\">标的物是否存在租赁关系，租赁关系是否到期、有效等情况，由竞买人自行核实。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>四、</span></strong><span>本次拍卖活动设置延时出价功能，在拍卖活动结束前，每最后<span>5</span>分钟如果有竞买人出价，将自动延迟<span>5</span>分钟。</span></span></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>五、拍卖方式：</span></strong><span>设有保留价的增价拍卖方式，保留价等于起拍价，至少一人报名且出价不低于起拍价，方可成交。</span></span></p><p style=\\\"line-height: 28.0px;text-indent: 43.0px;\\\"><strong><span style=\\\"color: black;font-size: 19.0px;\\\">六、特别提醒</span></strong></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">（一）竞买人一旦参加竞买，无论是否实地看样，都视为对本拍卖财产实物现状的确认，即视为对拍卖财产完全了解，并接受拍卖财产一切已知和未知的瑕疵，责任自负。标的物转让登记手续由买受人自行办理，对能否办理过户情况，请竞买人自行到当地规划局、国土局等咨询确认，拍卖人不作过户的任何承诺，不承担过户涉及的一切费用。</span></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">（二）公告所标示不动产面积均为参考面积，一切以房产管理部门宗地丈量为准。动产的状况以实物为准，有产权登记的特殊动产或其他财产权的状况以实物及登记机关的登记资料为准。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>七、</span></strong><span>交易过程中产生税费及有可能存在的水、电等欠费依照税法等相关法律法规和政策的规定均由买受人自行承担，买受人自行办理水、电、煤等户名变更手续。拍卖人不作可出证的任何承诺。</span></span></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">标的物可能涉及的违法、违章部分，由买受人自行接受行政主管部门依照有关行政法规的处理。</span></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">本院不保证拍卖财产真伪或者品质，不承担瑕疵担保责任。拍卖财产按交付时现状交付，交付时实物与评估报告不一致的风险及评估基准日至交付之时所产生的风险如隐藏瑕疵、缺陷、损毁等均由买受人承担，本院概不作多退少补。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>八</span></strong><span>、对上述标的权属有异议者，请于</span></span><span style=\\\"color: red;font-size: 19.0px;\\\">2018</span><span style=\\\"color: red;font-size: 19.0px;\\\">年<span>4</span>月<span>16</span>日</span><span style=\\\"color: black;font-size: 19.0px;\\\">前与本院联系。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>九、</span></strong><span>与本标的物有利害关系的当事人可参加竞拍，不参加竞拍的请关注本次拍卖活动的整个过程。</span></span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十、</span></strong><span>本标的物优先购买权人相关说明：</span></span><span style=\\\"color: red;font-size: 19.0px;\\\">有</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十一、</span></strong><span>拍卖竞价前将通过网拍系统将在竞买人支付宝账户内冻结相应资金作为应缴的保证金，拍卖结束后未能竞得者冻结的保证金自动解冻，冻结期间不计利息。本标的物竞得者原冻结的保证金自动转入法院指定账户。</span></span></p><p style=\\\"line-height: 28.0px;text-indent: 38.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十二、</span></strong><span>拍卖竞价前淘宝系统将锁定竞买人支付宝账户内的资金作为应缴的保证金，拍卖结束后未能竞得者锁定的保证金自动释放，锁定期间不计利息。本标的物竞得者原锁定的保证金自动转入法院指定账户，拍卖余款在成交后</span></span><span style=\\\"color: red;font-size: 19.0px;\\\">十日内</span><span style=\\\"color: black;font-size: 19.0px;\\\">（遇节假日顺延）缴入法院指定账户（户名：惠州市惠阳区人民法院，开户银行：中国工商银行惠州市惠阳支行，账号：<span>2008023329200475617</span>），拍卖未成交的，竞买人锁定的保证金自动释放，锁定期间不计利息。</span></p><p style=\\\"line-height: 28.0px;text-indent: 43.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">拍卖成交后，买受人在尾款缴纳后</span><span style=\\\"color: red;font-size: 19.0px;\\\">三日内</span><span style=\\\"color: black;font-size: 19.0px;\\\">（遇节假日顺延）（凭付款凭证及相关身份材料）到法院执行局签署相关法律文书，收到裁定书后应及时自行办理过户或交接手续，逾期不办理的，买受人应支付由此产生的逾期滞纳金等费用，并承担本拍卖财产可能发生的损毁、灭失等后果。</span></p><p style=\\\"background: white;line-height: 24.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十三、</span></strong><span>参加竞买的人应当遵守竞买须知的规定，不得阻挠其他竞买人竞拍，不得操纵、垄断竞拍价格，严禁竞买人恶意串标，上述行为一经发现，将取消其竞买资格，并追究相关的法律责任。</span></span></p><p style=\\\"line-height: 22.0px;text-indent: 38.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十四、</span></strong><span>司法拍卖因标的物本身价值，其起拍价、保证金、竞拍成交价格相对较高的。竞买人参与竞拍，支付保证金及余款可能会碰到当天限额无法支付的情况，请竞买人根据自身情况选择网上充值银行。各大银行充值和支付的限额情况可上网查询，网址</span><strong><span>：</span></strong></span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\"><a href=\\\"https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q1\\\" target=\\\"_blank\\\"><span style=\\\"color: #0000ff;\\\">https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q1</span></a></span></p><p style=\\\"line-height: 22.0px;text-indent: 38.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十五、</span></strong><span>依照法释〔<span>2016</span>〕<span>18</span>号《最高人民法院关于人民法院网络司法拍卖若干问题的规定》，竞买人成功竞得网拍标的物后，淘宝网拍平台将生成相应《司法拍卖网络竞价成功确认书》，确认书中载明实际买受人姓名、网拍竞买号信息。竞买人决定参与竞买的，视为同意载明买受人真实身份的拍卖成交确认书在网络司法拍卖平台上公示。</span></span></p><p style=\\\"background: white;line-height: 22.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">竞买人在拍卖竞价前务必再仔细阅读本院发布的拍卖须知。</span></p><p style=\\\"line-height: 24.0px;\\\"><span style=\\\"color: #444444;font-size: 19.0px;\\\">标的物详情咨询电话：</span><span style=\\\"color: red;font-size: 19.0px;\\\">0752-3915366(</span><span style=\\\"color: red;font-size: 19.0px;\\\">黄法官<span>)</span></span></p><p style=\\\"line-height: 24.0px;\\\"><span style=\\\"color: #444444;font-size: 19.0px;\\\">法院监督电话：<span>0752-3375252</span></span></p><p style=\\\"line-height: 24.0px;\\\"><span style=\\\"color: #444444;font-size: 19.0px;\\\">联系地址：广东省惠州市惠阳区淡水开城大道中<span>113</span>号</span></p><p style=\\\"line-height: 24.0px;\\\"><span style=\\\"color: #444444;font-size: 19.0px;\\\">网址：<span>http://www.hycourt.gov.cn</span></span></p><p style=\\\"background: white;text-align: right;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">惠州市惠阳区人民法院</span></p><p style=\\\"background: white;text-align: right;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">二〇一八年三月二十一日</span></p><p><span style=\\\"undefinedcolor: #000000;\\\">&nbsp;</span></p><p></p>\",\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"InnerCompanyName\": \"惠州市龙海广发食品有限公司\",\n" +
            "\"ActionRemark\": \"2018年4月21日10时至2018年4月22日10时止\",\n" +
            "\"YiWu\": \"11787511\",\n" +
            "\"InnerId\": \"5d1dbbce40da1216e3516a4f\",\n" +
            "\"Title\": \"惠州市惠阳区人民法院关于被执行人惠州市龙海广发食品有限公司名下位于惠州市惠城区汝湖仍图仍中村新二小组岭下的地上厂房、宿舍房地产及机器设备（第一次拍卖）的公告\",\n" +
            "\"Id\": \"37bc7dbbeb26e60122c5b9d168fab073\",\n" +
            "\"ExecuteGov\": \"惠州市惠阳区人民法院\"\n" +
            "},\n" +
            "{\n" +
            "\"Context\": \"<p style=\\\"background: white;text-align: center;line-height: 22.0px;text-indent: 37.0px;\\\"><strong><span style=\\\"color: black;font-size: 21.0px;\\\">惠阳区人民法院关于拍卖</span></strong><strong><span style=\\\"color: red;font-size: 21.0px;\\\">被执行人惠州市龙海广发食品有限公司名下位于惠州市惠城区汝湖仍图仍中村新二小组岭下的地上厂房、宿舍房地产及机器设备</span></strong><strong><span style=\\\"color: black;font-size: 21.0px;\\\">的公告（一拍）</span></strong><strong></strong></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">惠州市惠阳区人民法院将于</span><span style=\\\"color: red;font-size: 19.0px;\\\">2018</span><span style=\\\"color: red;font-size: 19.0px;\\\">年<span>4</span>月<span>21</span>日<span>10</span>时至<span>2018</span>年<span>4</span>月<span>22</span>日<span>10</span></span><span style=\\\"color: black;font-size: 19.0px;\\\">时止<strong><span>（延时除外）</span></strong>在惠阳区人民法院淘宝网司法拍卖网络平台上进行公开拍卖活动（账户名：惠州市惠阳区人民法院，法院主页网址：<span><a target=\\\"_blank\\\"><span style=\\\"color: #0000ff;\\\">https://sf.taobao.com/0752/05</span></a>?</span>），现公告如下：</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>一、</span></strong><span>拍卖标的：</span></span><strong><span style=\\\"color: red;font-size: 21.0px;\\\">被执行人惠州市龙海广发食品有限公司名下位于惠州市惠城区汝湖仍图仍中村新二小组岭下的地上厂房、宿舍房地产及机器设备。</span></strong></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: red;font-size: 19.0px;\\\">起拍价：<span>11787511</span>元</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: red;font-size: 19.0px;\\\">保证金：<span>2357502</span>元</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: red;font-size: 19.0px;\\\">增价幅度：<span>58000</span>元。</span></p><p style=\\\"line-height: 27.0px;text-indent: 37.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>二、</span></strong><span>竞买人条件：凡具备完全民事行为能力的公民、法人和其他组织均可参加竞买。如参与竞买人未开设淘宝账户，可委托代理人（具备完全民事行为能力的自然人）进行，竞买人（法定代表人、其他组织的负责人）须与委托代理人一同于拍卖开始前<span>5</span>个工作日内携带委托书、双方身份证到我院办理委托备案手续，资格经法院确认后方能进行委托。拍卖当天不接收委托。如委托手续不全，竞买活动认定为委托代理人的个人行为。</span></span></p><p style=\\\"line-height: 24.0px;text-indent: 19.0px;\\\"><span style=\\\"color: #666666;font-size: 19.0px;\\\">因不符合条件参加竞买的，由竞买人自行承担相应的法律责任。买受人应自行了解当地限购政策，如因限购原因造成无法办理产权过户手续的由买受人自行承担不利后果，港澳台及外籍人士须符合相关房地产政策规定。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 32.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>三、</span></strong><span>咨询的时间：自</span></span><span style=\\\"color: red;font-size: 19.0px;\\\">2018</span><span style=\\\"color: red;font-size: 19.0px;\\\">年<span>3</span>月<span>21</span>日起至<span>2018</span>年<span>4</span>月<span>16</span>日止</span><span style=\\\"color: #666666;font-size: 19.0px;\\\">（节假日休息）接受咨询。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 32.0px;\\\"><span style=\\\"color: #666666;font-size: 19.0px;\\\">不动产按现状拍卖，有意者请亲自实地看样，未看样的竞买人视为对本标的实物现状的确认，责任自负，法院不负责清场交付。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 32.0px;\\\"><span style=\\\"color: #666666;font-size: 19.0px;\\\">拍卖动产，标的物在法院的，可在咨询时间范围内电话联系，届时统一时间查看。标的物在其他的地方的，自行前往查看。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 32.0px;\\\"><span style=\\\"color: #666666;font-size: 19.0px;\\\">标的物是否存在租赁关系，租赁关系是否到期、有效等情况，由竞买人自行核实。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>四、</span></strong><span>本次拍卖活动设置延时出价功能，在拍卖活动结束前，每最后<span>5</span>分钟如果有竞买人出价，将自动延迟<span>5</span>分钟。</span></span></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>五、拍卖方式：</span></strong><span>设有保留价的增价拍卖方式，保留价等于起拍价，至少一人报名且出价不低于起拍价，方可成交。</span></span></p><p style=\\\"line-height: 28.0px;text-indent: 43.0px;\\\"><strong><span style=\\\"color: black;font-size: 19.0px;\\\">六、特别提醒</span></strong></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">（一）竞买人一旦参加竞买，无论是否实地看样，都视为对本拍卖财产实物现状的确认，即视为对拍卖财产完全了解，并接受拍卖财产一切已知和未知的瑕疵，责任自负。标的物转让登记手续由买受人自行办理，对能否办理过户情况，请竞买人自行到当地规划局、国土局等咨询确认，拍卖人不作过户的任何承诺，不承担过户涉及的一切费用。</span></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">（二）公告所标示不动产面积均为参考面积，一切以房产管理部门宗地丈量为准。动产的状况以实物为准，有产权登记的特殊动产或其他财产权的状况以实物及登记机关的登记资料为准。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>七、</span></strong><span>交易过程中产生税费及有可能存在的水、电等欠费依照税法等相关法律法规和政策的规定均由买受人自行承担，买受人自行办理水、电、煤等户名变更手续。拍卖人不作可出证的任何承诺。</span></span></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">标的物可能涉及的违法、违章部分，由买受人自行接受行政主管部门依照有关行政法规的处理。</span></p><p style=\\\"line-height: 28.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">本院不保证拍卖财产真伪或者品质，不承担瑕疵担保责任。拍卖财产按交付时现状交付，交付时实物与评估报告不一致的风险及评估基准日至交付之时所产生的风险如隐藏瑕疵、缺陷、损毁等均由买受人承担，本院概不作多退少补。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>八</span></strong><span>、对上述标的权属有异议者，请于</span></span><span style=\\\"color: red;font-size: 19.0px;\\\">2018</span><span style=\\\"color: red;font-size: 19.0px;\\\">年<span>4</span>月<span>16</span>日</span><span style=\\\"color: black;font-size: 19.0px;\\\">前与本院联系。</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>九、</span></strong><span>与本标的物有利害关系的当事人可参加竞拍，不参加竞拍的请关注本次拍卖活动的整个过程。</span></span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十、</span></strong><span>本标的物优先购买权人相关说明：</span></span><span style=\\\"color: red;font-size: 19.0px;\\\">有</span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十一、</span></strong><span>拍卖竞价前将通过网拍系统将在竞买人支付宝账户内冻结相应资金作为应缴的保证金，拍卖结束后未能竞得者冻结的保证金自动解冻，冻结期间不计利息。本标的物竞得者原冻结的保证金自动转入法院指定账户。</span></span></p><p style=\\\"line-height: 28.0px;text-indent: 38.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十二、</span></strong><span>拍卖竞价前淘宝系统将锁定竞买人支付宝账户内的资金作为应缴的保证金，拍卖结束后未能竞得者锁定的保证金自动释放，锁定期间不计利息。本标的物竞得者原锁定的保证金自动转入法院指定账户，拍卖余款在成交后</span></span><span style=\\\"color: red;font-size: 19.0px;\\\">十日内</span><span style=\\\"color: black;font-size: 19.0px;\\\">（遇节假日顺延）缴入法院指定账户（户名：惠州市惠阳区人民法院，开户银行：中国工商银行惠州市惠阳支行，账号：<span>2008023329200475617</span>），拍卖未成交的，竞买人锁定的保证金自动释放，锁定期间不计利息。</span></p><p style=\\\"line-height: 28.0px;text-indent: 43.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">拍卖成交后，买受人在尾款缴纳后</span><span style=\\\"color: red;font-size: 19.0px;\\\">三日内</span><span style=\\\"color: black;font-size: 19.0px;\\\">（遇节假日顺延）（凭付款凭证及相关身份材料）到法院执行局签署相关法律文书，收到裁定书后应及时自行办理过户或交接手续，逾期不办理的，买受人应支付由此产生的逾期滞纳金等费用，并承担本拍卖财产可能发生的损毁、灭失等后果。</span></p><p style=\\\"background: white;line-height: 24.0px;text-indent: 35.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十三、</span></strong><span>参加竞买的人应当遵守竞买须知的规定，不得阻挠其他竞买人竞拍，不得操纵、垄断竞拍价格，严禁竞买人恶意串标，上述行为一经发现，将取消其竞买资格，并追究相关的法律责任。</span></span></p><p style=\\\"line-height: 22.0px;text-indent: 38.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十四、</span></strong><span>司法拍卖因标的物本身价值，其起拍价、保证金、竞拍成交价格相对较高的。竞买人参与竞拍，支付保证金及余款可能会碰到当天限额无法支付的情况，请竞买人根据自身情况选择网上充值银行。各大银行充值和支付的限额情况可上网查询，网址</span><strong><span>：</span></strong></span></p><p style=\\\"background: white;line-height: 22.0px;text-indent: 35.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\"><a href=\\\"https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q1\\\" target=\\\"_blank\\\"><span style=\\\"color: #0000ff;\\\">https://www.taobao.com/market/paimai/sf-helpcenter.php?path=sf-hc-right-content5#q1</span></a></span></p><p style=\\\"line-height: 22.0px;text-indent: 38.0px;\\\"><span style=\\\"color: #000000;\\\"><strong><span>十五、</span></strong><span>依照法释〔<span>2016</span>〕<span>18</span>号《最高人民法院关于人民法院网络司法拍卖若干问题的规定》，竞买人成功竞得网拍标的物后，淘宝网拍平台将生成相应《司法拍卖网络竞价成功确认书》，确认书中载明实际买受人姓名、网拍竞买号信息。竞买人决定参与竞买的，视为同意载明买受人真实身份的拍卖成交确认书在网络司法拍卖平台上公示。</span></span></p><p style=\\\"background: white;line-height: 22.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">竞买人在拍卖竞价前务必再仔细阅读本院发布的拍卖须知。</span></p><p style=\\\"line-height: 24.0px;\\\"><span style=\\\"color: #444444;font-size: 19.0px;\\\">标的物详情咨询电话：</span><span style=\\\"color: red;font-size: 19.0px;\\\">0752-3915366(</span><span style=\\\"color: red;font-size: 19.0px;\\\">黄法官<span>)</span></span></p><p style=\\\"line-height: 24.0px;\\\"><span style=\\\"color: #444444;font-size: 19.0px;\\\">法院监督电话：<span>0752-3375252</span></span></p><p style=\\\"line-height: 24.0px;\\\"><span style=\\\"color: #444444;font-size: 19.0px;\\\">联系地址：广东省惠州市惠阳区淡水开城大道中<span>113</span>号</span></p><p style=\\\"line-height: 24.0px;\\\"><span style=\\\"color: #444444;font-size: 19.0px;\\\">网址：<span>http://www.hycourt.gov.cn</span></span></p><p style=\\\"background: white;text-align: right;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">惠州市惠阳区人民法院</span></p><p style=\\\"background: white;text-align: right;line-height: 22.0px;text-indent: 37.0px;\\\"><span style=\\\"color: black;font-size: 19.0px;\\\">二〇一八年三月二十一日</span></p><p><span style=\\\"undefinedcolor: #000000;\\\">&nbsp;</span></p><p></p><br/>财产交接\",\n" +
            "\"InputDate\": \"2019-07-04 04:41:50\",\n" +
            "\"InnerCompanyName\": \"惠州市龙海广发食品有限公司\",\n" +
            "\"ActionRemark\": \"2018年4月21日10时至2018年4月22日10时止\",\n" +
            "\"YiWu\": \"11787511\",\n" +
            "\"InnerId\": \"5d1dbbce40da1216e3516a50\",\n" +
            "\"Title\": \"惠州市惠阳区人民法院关于被执行人惠州市龙海广发食品有限公司名下位于惠州市惠城区汝湖仍图仍中村新二小组岭下的地上厂房、宿舍房地产及机器设备（第一次拍卖）的公告\",\n" +
            "\"Id\": \"a8d26dfd492246b75702da4463e0fcce\",\n" +
            "\"ExecuteGov\": \"惠州市惠阳区人民法院\"\n" +
            "}\n" +
            "]\n" +
            "}\n" +
            "],\n" +
            "\"sheetNames\": [\n" +
            "\"经营异常1\",\n" +
            "\"土地抵押6\",\n" +
            "\"环保处罚3\",\n" +
            "\"动产抵押2\",\n" +
            "\"司法拍卖2\"\n" +
            "]\n" +
            "}\n" +
            "}";
    @Test
    public void test2() throws FileNotFoundException {
        JSONObject jsonObject = JSONObject.parseObject(s.toString());
//        JSONArray departments = JSONObject.parseArray("[{\"name\":\"d\",\"chief\":{\"name\":\"a\"},\"headcount\":\"s\",\"link\":\"www\",\"staff\":[{\"name\":\"a\"}]},{\"name\":\"dd\",\"chief\":{\"name\":\"a\"},\"headcount\":\"s\",\"link\":\"www\",\"staff\":[{\"name\":\"a\"}]}]");
        JSONArray eachData = jsonObject.getJSONObject("Result").getJSONArray("eachData");

        try (FileInputStream is = new FileInputStream("D:/mynsh/mynsh/hz-link/src/main/resources/excel/t.xlsx")) {
                OutputStream os = new FileOutputStream(new File("D:/mynsh/mynsh/hz-link/src/main/resources/excel/test.xlsx"));
                Context context = PoiTransformer.createInitialContext();
                context.putVar("eachData", eachData);
                context.putVar("sheetNames", jsonObject.getJSONObject("Result").getJSONArray("sheetNames")
                );
                JxlsHelper.getInstance().setUseFastFormulaProcessor(false).setDeleteTemplateSheet(true).processTemplate(is, os, context);

            FileInputStream fis = new FileInputStream("D:/mynsh/mynsh/hz-link/src/main/resources/excel/test.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(fis);

            //删除Sheet
            wb.removeSheetAt(wb.getSheetIndex("template"));

            FileOutputStream fileOut = new FileOutputStream("D:/mynsh/mynsh/hz-link/src/main/resources/excel/test.xlsx");
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();

            fis.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
