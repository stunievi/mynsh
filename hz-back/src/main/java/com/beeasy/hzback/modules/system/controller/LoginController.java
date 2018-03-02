package com.beeasy.hzback.modules.system.controller;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.core.security.CustomUserService;
import com.beeasy.hzback.core.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    CustomUserService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @RequestMapping("/login")
    public Result login(String username,String password){
        //UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        //Authentication authentication = authenticationManager.authenticate(upToken);
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!userDetails.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
            return Result.error();
        }
        return Result.ok(jwtTokenUtil.generateToken(userDetails));
    }
}
