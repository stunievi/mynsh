import COM.ibm.db2.app.UDF;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.regex.Pattern;

public class Json extends UDF {

//    public static Pattern =

    public void toJsonString(String str, String out) throws Exception {
        JSONObject object = new JSONObject();
        do{
            if(S.empty(str)){
                break;
            }
            str = str.replaceAll("<A>|<\\/A>|<A\\/>","");
            String[] arr = str.split("\\$\\|\\$");
            for (String s : arr) {
                int idex = s.indexOf("$:$");
                if(idex == -1){
                    continue;
                }
                object.put(
                    s.substring(0,idex)
                    , s.substring(idex + 3)
                );
            }

        }while(false);

        set(2, JSON.toJSONString(object));

    }

    public static void main(String[] args) throws Exception {
        new Json().toJsonString("</A><A>USE_DEC$:$住宅按揭$|$</A><A>PHONE$:$11111111111$|$</A><A>INDIV_RSD_ADDR$:$湖南省溆浦县葛竹坪镇政府机关宿舍                            $|$</A><A>SEVEN_RESULT$:$正常一$|$</A><A>DBRDQK_1$:$配偶$|$</A><A>DBRDQK_2$:$是$|$</A><A>DBRDQK_3$:$是$|$</A><A>REPAYMENT_ABILITY$:$诚信记录优良，在金融机构没有拖欠贷款本息的记录$|$</A><A>IS_BASIC_CHANGE_P$:$是$|$</A><A>SFMYQNZSHBFX$:$是$|$</A><A>YEAR_INCOME$:$10000$|$</A><A>PLEDGE_VALUE$:$预计变现价值（或当前市场价值）不低于评估价值$|$</A><A>IS_INCOME_CHANGE_P$:$是$|$</A><A>YWTQBJLX_AMT$:$13434$|$</A><A>NO_SEND_CAUSE$:$$|$</A><A>YWTQBJLX$:$是$|$</A><A>YXDKCHDBLYS$:$没有$|$</A><A>REPAYMENT_WILL$:$良好$|$</A><A>PLEDGE_STATUS$:$保持完整$|$</A><A>FCZ_DATE$:$213243$|$</A><A>FXFF$:$$|$</A><A>THDKQK$:$在他行无贷款记录$|$</A><A>KFSBZNL$:$良好$|$</A><A>KFSQK_1$:$是$|$</A><A>KFSQK_2$:$是$|$</A><A>FCZ$:$已出证$|$</A>","");
    }
}
