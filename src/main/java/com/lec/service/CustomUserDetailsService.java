package com.lec.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lec.domain.Member;
import com.lec.persistence.MemberRepository;
import com.lec.domain.UserPrincipal;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    
    @Autowired
    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("찾을 수 없음: " + username));
        
        String profile = member.getProfile();
        String nickname = member.getNickname(); // 닉네임 가져오기
       
        
        UserPrincipal userPrincipal = UserPrincipal.create(member);
        userPrincipal.setProfile(profile);
        userPrincipal.setNickname(nickname); // 닉네임 설정
        
        
        
        return userPrincipal;
    }



}
