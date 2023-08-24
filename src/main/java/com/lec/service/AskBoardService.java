package com.lec.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.domain.AskBoard;

public interface AskBoardService {

	long getTotalRowCount(AskBoard board);
	AskBoard getBoard(AskBoard board);
	Page<AskBoard> getBoardList(Pageable pageable, String searchType, String searchWord);
	void insertBoard(AskBoard board);
	void updateBoard(AskBoard board);
	void deleteBoard(AskBoard board);
	int updateReadCount(AskBoard board);
	void adminReply(AskBoard board, String reply);
}	