package com.lec.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.standard.StandardDialect;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
			.csrf().disable()  // csrf 방어 비활성화
        	.cors().and()
            .authorizeRequests()
            .antMatchers("/", "/login", "/insertMember", "/getBoardList", "/index","/getInformlist","/getInform","/shopBoardView","/shopBoardList","/getNewsList","/getNews","/updateMember","/selectMember","/getCommunityList","/getCommunity","/getLikedCommunities").permitAll()  // 이 URL에 대해 인증 없이 접근 허용
            .antMatchers("/checkId","/checkNickname").permitAll() // 로그인하지 않은 상태 중복확인 접근 가능
                .antMatchers("/images/**").permitAll()  // 이미지 접근 허용 별걸 다 하네 이미지 아무나 접근 허용 (갑질)
                .antMatchers("/image/**").permitAll()  // 이미지 접근 허용 별걸 다 하네 이미지 아무나 접근 허용 (갑질)
                .antMatchers("/css/**","/logo/**","/informfile/**","/kimupload/**","/kimdownload/**","/review/**","/upload/**","/carousel/**","/news/**","/community/**").permitAll()  // 접근 허용 별걸 다 하네 이미지 아무나 접근 허용 (갑질)
                .antMatchers("/insertBoard","/getCommunity","/getCommunityList","/getLikedCommunities").authenticated() // "/insertBoard" URL에 대해서는 인증된 사용자만 접근 허용
                .antMatchers("/adminpage","/adminReply","/updateInform","/deleteInform","/shopBoardRegister","/memberOrderList","/insertNews","/updateNews","/deleteNews","/getMemberList").hasAuthority("ROLE_ADMIN") //ROLE_ADMIN 권한을 가진 사용자만 접근 허용
                .antMatchers("/adminPage","/adminReply").hasAuthority("ADMIN") // 임시방편 
                .anyRequest().authenticated()  // 나머지 모든 URL에 대해서는 인증을 요구
                .and()
            .formLogin() // 전통적인(templates에 들어가는 html파일) 서버사이드 렌더링
                .loginPage("/login")  // 로그인 페이지의 URL 매핑
                .defaultSuccessUrl("/index", true)  // 로그인 성공 시 "/getBoardList"로 리다이렉트
                .successHandler(new CustomAuthenticationSuccessHandler())  // 커스텀 인증 성공 핸들러
                .permitAll()  // 로그인 페이지에 대해 인증 없이 접근 허용
                .usernameParameter("id")  // 로그인 폼에서 사용되는 사용자명 파라미터 이름
                .passwordParameter("password")  // 로그인 폼에서 사용되는 비밀번호 파라미터 이름
                .failureUrl("/login?error")  // 로그인 실패 시 URL 매핑	
            .and()
            .logout()
                .logoutUrl("/**/logout")  // 로그아웃 동작의 URL 매핑
                .logoutSuccessHandler(customLogoutSuccessHandler())
                .logoutSuccessUrl("/login?logout")  // 로그아웃 성공 시 "/login?logout"로 리다이렉트
                .invalidateHttpSession(true)  // 로그아웃 시 HttpSession 무효화
                .deleteCookies("JSESSIONID")  // 로그아웃 시 "JSESSIONID" 쿠키 삭제
            .permitAll();  // 로그아웃 동작에 대해 인증 없이 접근 허용
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)  // 인증을 위한 UserDetailsService 설정
            .passwordEncoder(passwordEncoder());  // 비밀번호 인코더 설정
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 인코딩을 위한 빈 객체
    }
    
    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();  // 커스텀 인증 성공 핸들러를 위한 빈 객체
    }
    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }


}
