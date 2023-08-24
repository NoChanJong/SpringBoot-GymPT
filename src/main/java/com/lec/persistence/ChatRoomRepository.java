package com.lec.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lec.domain.ChatRoom;
import com.lec.domain.Member;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr, cm.createDate_c " +
            "FROM ChatRoom cr " +
            "LEFT JOIN cr.messages cm ON cm.messageId = (SELECT MAX(m.messageId) FROM cr.messages m)")
     List<Object[]> findRoomsAndRecentMessageCreateDates();

	List<ChatRoom> findByMember1OrMember2(Member member, Member member2);

}
