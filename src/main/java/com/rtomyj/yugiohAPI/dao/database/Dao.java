package com.rtomyj.yugiohAPI.dao.database;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.rtomyj.yugiohAPI.helper.exceptions.YgoException;
import com.rtomyj.yugiohAPI.helper.enumeration.products.ProductType;
import com.rtomyj.yugiohAPI.model.BrowseResults;
import com.rtomyj.yugiohAPI.model.banlist.BanList;
import com.rtomyj.yugiohAPI.model.banlist.BanListComparisonResults;
import com.rtomyj.yugiohAPI.model.banlist.BanListStartDates;
import com.rtomyj.yugiohAPI.model.Card;
import com.rtomyj.yugiohAPI.model.Stats.DatabaseStats;
import com.rtomyj.yugiohAPI.model.Stats.MonsterType;
import com.rtomyj.yugiohAPI.model.product.Product;
import com.rtomyj.yugiohAPI.model.product.ProductContent;
import com.rtomyj.yugiohAPI.model.product.Products;

/**
 * Contract for database operations.
 */
public interface Dao
{
	/**
	 * Defines statuses of a card a ban list that is used by the database.
	 * In other words, these strings are used in the database to differentiate between different statuses.
	 */
	public enum Status
	{
		/**
		 * Card cannot be used in advanced format
		 */
		FORBIDDEN("Forbidden"),
		/**
		 * Only one instance of the card can be used.
		 */
		LIMITED("Limited"),
		/**
		 * Only two instance of the card can be used.
		 */
		SEMI_LIMITED("Semi-Limited");

		private final String status;

		Status(final String status)
		{
			this.status = status;
		}

		/**
		 * String representation of enum.
		 */
		@Override
		public String toString()
		{
			return status;
		}
	}

	/**
	 * Get the list of dates of all the ban lists stored in the database.
	 * @return A list of BanList
	 */
	public BanListStartDates getBanListStartDates();

	/**
	 * Retrieve the information about a Card given the ID.
	 * @param cardID The ID of a Yugioh card.
	 * @return The Card requested.
	 */
	public Card getCardInfo(String cardID) throws YgoException;

	/**
	 * Checks the databases and returns a list of cards in a specified ban list (date) that has the specified status (forbidden, limited, semi-limited)
	 * @param date Valid start date of the ban list desired.
	 * @param status The status
	 * @return List of Cards that have the status wanted for the desired date.
	 */
	public List<Card> getBanListByBanStatus(String date, Status status);

	/**
	 * Checks the database for the number of ban lists stored.
	 * @return the number of ban lists in database.
	 */
	public int getNumberOfBanLists();

	/**
	 * Returns an integer ([1, n]) that corresponds to the position of the banListDate when the database is sorted by banListDate ASC.
	 * @param banListDate the starting date for a ban list to query the database with.
	 * @return The position of the ban list queried in the database: -1 if not in database, 1 if it is the first ban list while database is sorted ASC.
	 */
	public int getBanListPosition(String banListDate);

	/**
	 * Checks the database and returns the date of the previous ban list as the one passed into the method.
	 *
	 * Returns an empty string if there are no previous ban lists or if the ban list date requested doesn't exist in database,
	 * ie: its the oldest ban list or the date isn't a date where a ban list started.
	 * @param currentBanList the date of a ban list to use to find the previous sequential ban list relative to it.
	 * @return String of the date of the previous ban list.
	 */
	public String getPreviousBanListDate(String currentBanList);

	/**
	 * Checks the database to find card(s) that where added to the ban list, ie: the card(s) was not in the previous ban list.
	 * Checks the database to find card(s) that have switched status compared to the previous ban list, ie: a card went from forbidden to limited.
	 * @param banListDate the date of the ban list to get the newly added cards for.
	 * @param status (forbidden, limited, semi-limited) used to get only new cards for that status.
	 * @return A list of maps that contains the following:
	 * 		id: Identifier of newly added card
	 * 		previousStatus: status the card had on the previous ban list, empty string if card wasn't in previous ban list.
	 */
	public List<BanListComparisonResults> getNewContentOfBanList(String banListDate, Status status);

	/**
	 *
	 * @param newBanList
	 * @return
	 */
	public List<BanListComparisonResults> getRemovedContentOfBanList(String newBanList);

	/**
	 *
	 * @param cardId
	 * @param banListDate
	 * @return
	 */
	public String getCardBanListStatusByDate(String cardId, String banListDate);

	public boolean isValidBanList(final String banListDate);

	public List<Card> getCardNameByCriteria(final String cardId, final String cardName, final String cardAttribute, final String cardColor, final String monsterType, final int limit);

	public Products getAllProductsByType(final ProductType productType, final String locale);

	public Map<String, Integer> getProductRarityCount(final String packId);

	public Set<ProductContent> getProductContents(final String productId, final String locale);

	public MonsterType getMonsterTypeStats(final String cardColor);

	public DatabaseStats getDatabaseStats();

	public Set<Product> getProductDetailsForCard(final String cardId);

	public List<BanList> getBanListDetailsForCard(final String cardId);

	public BrowseResults getBrowseResults(final Set<String> cardColors, final Set<String> attributeSet, final Set<String> monsterLevels);

	public CompletableFuture<Set<String>> getCardColors();

	public CompletableFuture<Set<String>> getMonsterAttributes();

	public CompletableFuture<Set<Integer>> getLevels();

	public CompletableFuture<Set<Integer>> getRanks();

	public CompletableFuture<Set<Integer>> getLinkRatings();

	public Product getProductInfo(final String productId, final String locale);

	public List<Product> getProductsByLocale(final String locale);

}