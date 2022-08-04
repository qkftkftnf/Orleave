package com.orleave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.orleave.dto.UserListDto;
import com.orleave.entity.MeetingLog;
import com.orleave.repository.MeetingLogRepository;
import com.orleave.repository.ReportRepository;
import com.orleave.repository.UserRepository;

@Service("ManagerService")
public class ManagerServiceImpl implements ManagerService{

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MeetingLogRepository meetingLogRepository;
	
	@Autowired
	ReportRepository reportRepository;
	
	@Override
	public Page<UserListDto> getUsers(Pageable pageable) {
		
		
		
		Page<UserListDto> users = userRepository.findAll(pageable).map(
				
				
				user -> UserListDto.builder()
				.no(user.getNo())
				.email(user.getEmail())
				.nickname(user.getNickname())
				.gender(user.getGender())
				.birthDay(user.getBirthDay())
				.userType(user.getUserType())
				.matchcount(meetingLogRepository.countByUser1No(user.getNo()))
				.reportcount(reportRepository.countByUserNo(user.getNo()))
				.build()
				);
				
		return users;
	}

}
