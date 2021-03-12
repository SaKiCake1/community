package com.xky.community.controller;

import com.xky.community.dto.AccessTokenDto;
import com.xky.community.dto.GithubUser;
import com.xky.community.mapper.UserMapper;
import com.xky.community.model.User;
import com.xky.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private UserMapper userMapper;

    /*@Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;*/

    @GetMapping("/callback")
    public String AuthorizeController(@RequestParam("code") String code,
                                      @RequestParam("state") String state,
                                      HttpServletRequest request) {
        AccessTokenDto accessTokenDto = new AccessTokenDto();
        accessTokenDto.setClient_id("Iv1.fde4a2e0572f3a08");
        accessTokenDto.setClient_secret("046ae3b8068b78764ca41d74093dd6debcfc3cd7");
        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri("http://localhost:8877/callback");
        accessTokenDto.setState(state);
        githubProvider.getAccessToken(accessTokenDto);
        String accessToken = githubProvider.getAccessToken(accessTokenDto);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if (githubUser != null) {
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtModified());
            userMapper.insert(user);
            //登录成功，写cookie和session
            //再session中放入user对象
            request.getSession().setAttribute("user", githubUser);
            return "redirect:/";
        } else {
            //登录失败，重新登录
            return "redirect:/";
        }
    }
}
