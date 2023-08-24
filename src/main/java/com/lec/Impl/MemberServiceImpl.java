package com.lec.Impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lec.domain.ChatRoom;
import com.lec.domain.Community;
import com.lec.domain.CommunityReply;
import com.lec.domain.Likes;
import com.lec.domain.Member;
import com.lec.domain.PointList;
import com.lec.persistence.ChatRoomRepository;
import com.lec.persistence.CommunityReplyRepository;
import com.lec.persistence.CommunityRepository;
import com.lec.persistence.LikesRepository;
import com.lec.persistence.MemberRepository;
import com.lec.persistence.PointListRepository;
import com.lec.service.ChatRoomService;
import com.lec.service.CommunityService;
import com.lec.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService {
   
   @Autowired
    private final MemberRepository memberRepo;
   
   @Autowired
   private final ChatRoomRepository chatRoomRepository;

   @Autowired
   private final CommunityRepository communityRepository;
   
   @Autowired
   private final LikesRepository likesRepository;
   
   @Autowired
   private final CommunityReplyRepository communityReplyRepository;

   @Autowired
   private final ChatRoomService chatRoomService;
   
   @Autowired
   private final CommunityService communityService;
    
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepo, 
                       PasswordEncoder passwordEncoder, 
                       ChatRoomRepository chatRoomRepository, 
                       CommunityRepository communityRepository, 
                       ChatRoomService chatRoomService, 
                       CommunityService communityService, 
                       LikesRepository likesRepository, 
                       CommunityReplyRepository communityReplyRepository) {
        this.memberRepo = memberRepo;
      this.chatRoomRepository = chatRoomRepository;
      this.communityRepository = communityRepository;
      this.likesRepository = likesRepository;
      this.communityReplyRepository = communityReplyRepository;
      this.chatRoomService = chatRoomService;
      this.communityService = communityService;
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
   private PointListRepository pointListRepo;

    @Override
    public Member getMember(Member member) {
        Optional<Member> findMember = memberRepo.findById(member.getId());
        return findMember.orElse(null);
    }

    @Override
    public long getTotalRowCount(Member member) {
        return memberRepo.count();
    }

    @Override
    public Page<Member> getMemberList(Pageable pageable, String searchType, String searchWord) {
        if (searchType.equalsIgnoreCase("id")) {
            return memberRepo.findByIdContaining(searchWord, pageable);
        } else {
            return memberRepo.findByNameContaining(searchWord, pageable);
        }
    }

    @Override
    public void insertMember(Member member) {
        String encodedPassword = passwordEncoder.encode(member.getPassword()); // 비밀번호 암호화
        member.setPassword(encodedPassword); // 암호화된 비밀번호로 설정
        memberRepo.save(member);
    }

    @Override
    public void deleteMember(Member member) {
        memberRepo.deleteById(member.getId());
    }

    @Override
    @Transactional
    public void deleteMemberAndChatRooms(Member member) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMember1OrMember2(member, member);
        List<Community> communities = communityRepository.findAllByMemberId(member.getId());
        for (ChatRoom chatRoom : chatRooms) {
            chatRoomService.deleteRoom(chatRoom.getRoom_id());
        }
        List<Likes> likesByMember = likesRepository.findByMember(member);
        for(Likes like : likesByMember) {
        	likesRepository.delete(like);
        }
        
        List<CommunityReply> repliesByMember = communityReplyRepository.findByMember(member);
        for(CommunityReply reply : repliesByMember) {
        	communityReplyRepository.delete(reply);
        }

        for (Community community : communities) {
            // 커뮤니티 게시물 삭제
            communityRepository.delete(community);
        }
        memberRepo.delete(member);
    }

    @Override
    public void updateMember(Member member) {
        Optional<Member> findMember = memberRepo.findById(member.getId());
        if (findMember.isPresent()) {
            Member existingMember = findMember.get();
            existingMember.setNickname(member.getNickname());
            existingMember.setProfile(member.getProfile());
            existingMember.setWeight(member.getWeight());
            existingMember.setHeight(member.getHeight());
            memberRepo.save(existingMember);
        } else {
        }
    }

    @Override
    public Member getMemberById(String memberId) {
        Optional<Member> findMember = memberRepo.findById(memberId);
        return findMember.orElse(null);
    }

    @Override
    public boolean existsById(String id) {
        return memberRepo.existsById(id);
    }

   @Override
   public void insertPoint(PointList pointList) {
      pointListRepo.save(pointList);
      
   }

   @Override
   public void updatePoint(Member member) {
       Member findMember = memberRepo.findById(member.getId()).orElse(null);
       if (findMember != null) {
           findMember.setPoint(member.getPoint());
           memberRepo.save(findMember);

           // Add logging to check if the method is called and the update is successful
           System.out.println("Member point updated: " + findMember.getId() + ", New point: " + findMember.getPoint());
       } else {
           // Add logging for the case when the member is not found in the database
           System.out.println("Member not found with id: " + member.getId());
       }
   }

   @Override
   public Member findByName(String name) {
      return memberRepo.findByName(name);
   }

   @Override
   public Page<Member> selectMember(Pageable pageable, String searchType, String searchWord) {
       if (searchType.equalsIgnoreCase("id")) {
           return memberRepo.findByIdContainingOrNicknameIgnoreCaseContainingOrHeightOrWeight(searchWord, searchWord, parseInteger(searchWord), parseInteger(searchWord), pageable);
       } else if (searchType.equalsIgnoreCase("nickname")) {
           return memberRepo.findByIdContainingOrNicknameIgnoreCaseContainingOrHeightOrWeight(searchWord, searchWord, parseInteger(searchWord), parseInteger(searchWord), pageable);
       } else {
           return memberRepo.findByIdContainingOrNicknameIgnoreCaseContainingOrNameContainingOrHeightOrWeight(searchWord, searchWord, searchWord, parseInteger(searchWord), parseInteger(searchWord), pageable);
       }
   }

   private Integer parseInteger(String value) {
       try {
           return Integer.parseInt(value);
       } catch (NumberFormatException e) {
           return null;
       }
   }
   @Override
   public Member findMemberById(String memberId) {
      return memberRepo.findById(memberId).orElse(null);
   }
}