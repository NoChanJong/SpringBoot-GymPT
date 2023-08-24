package com.lec.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lec.domain.AskBoard;
import com.lec.domain.Member;
import com.lec.persistence.AskBoardRepository;
import com.lec.service.AskBoardService;

@Service
public class AskBoardServiceImpl implements AskBoardService {

	@Autowired
	private AskBoardRepository askboardRepo;

	public AskBoard getBoard(AskBoard board) {
	    Optional<AskBoard> findBoard = askboardRepo.findBySeq(board.getSeq());
	    if (findBoard.isPresent()) {
	        return findBoard.get();
	    } else {
	        return null;
	    }
	}


	public Page<AskBoard> getBoardList(Pageable pageable, String searchType, String searchWord) {		
		if(searchType.equalsIgnoreCase("title")) {
			return askboardRepo.findByTitleContaining(searchWord, pageable);
		} else if(searchType.equalsIgnoreCase("writer")) {
			return askboardRepo.findByWriterContaining(searchWord, pageable);
		} else {
			return askboardRepo.findByContentContaining(searchWord, pageable);
		}
	}
    

	public void insertBoard(AskBoard board) {
	    // 현재 인증된 사용자를 가져옵니다.
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserName = authentication.getName();

	    // 게시글에 작성자 정보를 추가합니다.
	    board.setWriter(currentUserName);

	    askboardRepo.save(board);
	    askboardRepo.updateLastSeq(0L, 0L, board.getSeq());
	}

	public void updateBoard(AskBoard board) {
		AskBoard findBoard = askboardRepo.findById(board.getSeq()).get();

		findBoard.setTitle(board.getTitle());
		findBoard.setContent(board.getContent());
		findBoard.setAdminReply(board.getAdminReply());
		askboardRepo.save(findBoard);
	}

	public void deleteBoard(AskBoard board) {
		askboardRepo.deleteById(board.getSeq());
	}

	@Override
	public long getTotalRowCount(AskBoard board) {
		return askboardRepo.count();
	}

	@Override
	public int updateReadCount(AskBoard board) {
		return askboardRepo.updateReadCount(board.getSeq());
	}

	@Override
	public void adminReply(AskBoard board, String reply) {
		board.setAdminReply(reply);
		askboardRepo.updateReplyStatus(board.getSeq(), board.getReplyStatus());
		updateBoard(board);
	}





	
}
