package com.rtomyj.yugiohAPI.service;

import java.util.List;

import com.rtomyj.yugiohAPI.dao.Dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BanListDiffService
{
	@Autowired
	@Qualifier("jdbc")
	private Dao dao;


	public List<String> getNewContentFromBanList(String banListDate, String string)	{ return dao.getNewContentFromBanList(banListDate, string); }
	public List<String> getRemovedContentOfBanList(String banListDate)	{ return dao.getRemovedContentOfBanList(banListDate); }
	public String getPreviousBanListDate(String banList)	{ return dao.getPreviousBanListDate(banList); }
}