package com.rtomyj.yugiohAPI.service;

import com.rtomyj.yugiohAPI.dao.Dao;
import com.rtomyj.yugiohAPI.dao.Dao.Status;
import com.rtomyj.yugiohAPI.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class that allows interfacing with the contents of a ban list.
 */
@Service
public class BannedCardsService
{
	/**
	 * Dao for DB;
	 */
	@Autowired
	@Qualifier("jdbc")
	private Dao dao;



	/**
	 * Uses the desired date of the ban list and retrieves the contents of the ban list but with the cards that have the desired status.
	 * @param date The date of the ban list to retrieve from DB. Must follow format: YYYY-DD-MM.
	 * @param status Restriction on what kind of ban list cards to retrieve from DB (forbidden, limited, semi-limited)
	 * @return List of Cards that satisfy the wanted criteria.
	 */
	public List<Card> getBanListByBanStatus(String date, Status status)
	{
		return dao.getBanListByBanStatus(date, status);
	}
}