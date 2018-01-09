package com.beeasy.hzback.core.shiro;

import org.apache.commons.lang.StringUtils;

public class TokenManager {
//    protected UserTokenOperHelper userTokenOperHelper;

//    public StatelessToken createToken(String userCode) {
//        StatelessToken tokenModel = null;
//        String token = userTokenOperHelper.getUserToken(userTokenPrefix+userCode);
//        if(StringUtils.isEmpty(token)){
//            token = createStringToken(userCode);
//        }
//        userTokenOperHelper.updateUserToken(userTokenPrefix+userCode, token, expirateTime);
//        tokenModel = new StatelessToken(userCode, token);
//        return tokenModel;
//    }

    public StatelessToken getToken(String authentication){
        if(StringUtils.isEmpty(authentication)){
            return null;
        }
        String[] au = authentication.split("_");
        if (au.length <=1) {
            return null;
        }
        String userCode = au[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < au.length; i++) {
            sb.append(au[i]);
            if(i<au.length-1){
                sb.append("_");
            }
        }
        return new StatelessToken(userCode, sb.toString());
    }

}
