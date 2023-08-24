package com.lec.domain;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private Member member;
    private List<GrantedAuthority> authorities;
    private String profile;
    private String nickname; // 닉네임 추가
    private Integer weight; // 몸무게 추가
    private Integer height; // 키 추가
    private String role; // 권한
    private String name;
    
    public UserPrincipal(Member member, String profile, String nickname, String name, Integer weight, Integer height, List<GrantedAuthority> authorities) {
        this.member = member;
        this.profile = profile;
        this.nickname = nickname;
        this.name = name;
        this.authorities = authorities;
        
    }

    public static UserPrincipal create(Member member) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getRole()));
        String profile = member.getProfile();  // profile 가져오기
        String nickname = member.getNickname(); // 닉네임 가져오기
        String name = member.getName(); // 이름 가져오기
        Integer weight = member.getWeight(); // 이름 가져오기
        Integer height = member.getHeight(); // 이름 가져오기
        return new UserPrincipal(member, profile, nickname, name, height, weight, authorities);  // profile 및 nickname 설정
    }
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + member.getRole();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        return Collections.singletonList(authority);
    }


    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public String getProfile() {
        return profile;
    }
    
    public void setProfile(String profile) {
        this.profile = profile;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public Integer getWeight() {
       return weight;
    }
    
    public void setWeight(Integer weight) {
       this.weight = weight;
    }
    
    public Integer getHeight() {
       return weight;
    }
    
    public void setHeight(Integer height) {
       this.height = height;
    }

    // member 필드에 접근할 수 있는 getter 메서드 추가
    public Member getMember() {
        return this.member;
    }


}