package com.kob.backend.consumer.util;

import com.kob.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class Jwt {
    public static Integer getUserId(String token){
        int userId = -1;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId =Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userId;
    }
}
