package com.Mixiao.bilibili.service.util;


import com.Mixiao.bilibili.domain.exception.ConditionException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {

    private static final String ISSUER = "签发者";
    //创建token
    public static String generateToken(Long userId) throws Exception{
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());//算法 RSA加密
        Calendar calendar =Calendar.getInstance();//帮忙后期生成过期时间
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND,30);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())//这个是JWT过期时间
                .sign(algorithm);  //生成签名
    }
    public static String generateRefreshToken(Long userId) throws Exception{
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
    //校验 Long -> userid
    public static Long verifyToken(String token){
        try{
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
            JWTVerifier verifier =JWT.require(algorithm).build();
            DecodedJWT jwt =  verifier.verify(token);
            String userid = jwt.getKeyId();
            return Long.valueOf(userid);
        }catch (TokenExpiredException e){
            throw new ConditionException("555","token过期!");
        }catch (Exception e){
            throw new ConditionException("非法用户token!");
        }
    }

}
