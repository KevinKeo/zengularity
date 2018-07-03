package com.example.zengularity.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.zengularity.model.User;
import com.example.zengularity.repository.UserRepository;

@RestController
public class UserController {


    public final static String COOKIE_PROP_USER = "zengularityuser";
    public final static String COOKIE_PROP_KEY = "zengularitykey";
    private final int age = 60*60*24;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/user")
    public User createCentrale(@Valid @RequestBody User user) {
        return userRepository.create(user);
    }

    @PostMapping("/user/auth")
    public void authentification(@Valid @RequestBody User user, HttpServletResponse response){
        String privatekey = userRepository.authentification(user);
        Map<String,String> map = new HashMap<String, String>();
        map.put("user",user.getUsername());
        map.put("key", privatekey);
        Cookie cookie1 = new Cookie(COOKIE_PROP_USER,user.getUsername());
        Cookie cookie2 = new Cookie(COOKIE_PROP_KEY,privatekey);
        cookie1.setMaxAge(age);
        cookie1.setPath("/");
        cookie2.setMaxAge(age);
        cookie2.setPath("/");
        response.addCookie(cookie1);
        response.addCookie(cookie2);
    }
}
