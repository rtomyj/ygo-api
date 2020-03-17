package com.rtomyj.yugiohAPI.service.banlist;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtomyj.yugiohAPI.configuration.exception.YgoException;
import com.rtomyj.yugiohAPI.dao.database.Dao;
import com.rtomyj.yugiohAPI.dao.database.Dao.Status;
import com.rtomyj.yugiohAPI.helper.constants.TestConstants;
import com.rtomyj.yugiohAPI.model.BanListComparisonResults;
import com.rtomyj.yugiohAPI.model.BanListNewContent;
import com.rtomyj.yugiohAPI.model.BanListRemovedContent;
import com.rtomyj.yugiohAPI.model.NewCards;

import org.cache2k.integration.CacheLoaderException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DiffServiceTest {
	@InjectMocks
	private DiffService diffService;

	@Mock
	private Dao dao;

	private final static String BAN_LIST_START_DATE = "2018-12-03";
	private final static String PREVIOUS_BAN_LIST_START_DATE = "2018-09-17";

	private BanListNewContent banListNewContent;
	private BanListRemovedContent banListRemovedContent;



	@Before
	public void before() throws JsonParseException, JsonMappingException, IOException
	{
		final ObjectMapper mapper = new ObjectMapper();

		this.banListNewContent = mapper.readValue(new File(TestConstants.BAN_LIST_NEW_CONTENT), BanListNewContent.class);
		this.banListRemovedContent = mapper .readValue(new File(TestConstants.BAN_LIST_REMOVED_CONTENT), BanListRemovedContent.class);
	}



	@Test
	public void testFetchingBanListNewContent_FromDB_Success()
		throws YgoException
	{
		when(this.dao.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.FORBIDDEN)))
			.thenReturn(this.banListNewContent.getNewCards().getForbidden());
		when(this.dao.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.LIMITED)))
			.thenReturn(this.banListNewContent.getNewCards().getLimited());
		when(this.dao.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.SEMI_LIMITED)))
			.thenReturn(this.banListNewContent.getNewCards().getSemiLimited());
		when(this.dao.getPreviousBanListDate(eq(BAN_LIST_START_DATE)))
			.thenReturn(PREVIOUS_BAN_LIST_START_DATE);


		final BanListNewContent banListNewContentInstance = this.diffService.getNewContentOfBanList(BAN_LIST_START_DATE);
		final NewCards newCards = banListNewContentInstance.getNewCards();
		final List<BanListComparisonResults> newForbiddenCards = newCards.getForbidden();
		final List<BanListComparisonResults> newLimitedCards = newCards.getLimited();
		final List<BanListComparisonResults> newSemiLimitedCards = newCards.getSemiLimited();

		assertEquals(BAN_LIST_START_DATE, banListNewContentInstance.getListRequested());
		assertEquals(PREVIOUS_BAN_LIST_START_DATE, banListNewContentInstance.getComparedTo());

		assertNotNull(newForbiddenCards);
		assertNotNull(newLimitedCards);
		assertNotNull(newSemiLimitedCards);
		assertEquals(1, newForbiddenCards.size());
		assertEquals(1, newLimitedCards.size());
		assertEquals(1, newSemiLimitedCards.size());

		assertEquals(TestConstants.STRATOS_ID, newForbiddenCards.get(0).getId());
		assertEquals("Limited", newForbiddenCards.get(0).getPreviousState());

		assertEquals(TestConstants.A_HERO_LIVES_ID, newLimitedCards.get(0).getId());
		assertEquals("Unlimited", newLimitedCards.get(0).getPreviousState());

		assertEquals(TestConstants.D_MALICIOUS_ID, newSemiLimitedCards.get(0).getId());
		assertEquals("Forbidden", newSemiLimitedCards.get(0).getPreviousState());


		verify(this.dao, times(1))
			.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.FORBIDDEN));
		verify(this.dao, times(1))
			.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.LIMITED));
		verify(this.dao, times(1))
			.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.SEMI_LIMITED));
		verify(this.dao, times(1))
			.getPreviousBanListDate(eq(BAN_LIST_START_DATE));
	}



	@Test(expected = CacheLoaderException.class)
	public void testFetchingBanListNewContent_FromDB_Failure()
		throws YgoException
	{
		when(this.dao.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.FORBIDDEN)))
			.thenReturn(new ArrayList<>());
		when(this.dao.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.LIMITED)))
			.thenReturn(new ArrayList<>());
		when(this.dao.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.SEMI_LIMITED)))
			.thenReturn(new ArrayList<>());


		this.diffService.getNewContentOfBanList(BAN_LIST_START_DATE);


		verify(this.dao, times(1))
			.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.FORBIDDEN));
		verify(this.dao, times(1))
			.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.LIMITED));
		verify(this.dao, times(1))
			.getNewContentOfBanList(eq(BAN_LIST_START_DATE), eq(Status.SEMI_LIMITED));
		verify(this.dao, times(0))
			.getPreviousBanListDate(any(String.class));
	}



	@Test
	public void testFetchingBanListRemovedContent_FromDB_Success()
		throws YgoException
	{
		when(this.dao.getRemovedContentOfBanList(eq(BAN_LIST_START_DATE)))
			.thenReturn(this.banListRemovedContent.getRemovedCards());
		when(this.dao.getPreviousBanListDate(eq(BAN_LIST_START_DATE)))
			.thenReturn(PREVIOUS_BAN_LIST_START_DATE);


		final BanListRemovedContent banListRemovedContentInstance = this.diffService.getRemovedContentOfBanList(BAN_LIST_START_DATE);

		final List<BanListComparisonResults> removedCards = banListRemovedContentInstance.getRemovedCards();

		assertNotNull(removedCards);

		assertEquals(BAN_LIST_START_DATE, banListRemovedContentInstance.getListRequested());
		assertEquals(PREVIOUS_BAN_LIST_START_DATE, banListRemovedContentInstance.getComparedTo());

		assertEquals(3, removedCards.size());

		assertEquals(TestConstants.STRATOS_ID, removedCards.get(0).getId());
		assertEquals("Forbidden", removedCards.get(0).getPreviousState());

		assertEquals(TestConstants.A_HERO_LIVES_ID, removedCards.get(1).getId());
		assertEquals("Limited", removedCards.get(1).getPreviousState());

		assertEquals(TestConstants.D_MALICIOUS_ID, removedCards.get(2).getId());
		assertEquals("Semi-Limited", removedCards.get(2).getPreviousState());


		verify(this.dao, times(1))
			.getRemovedContentOfBanList(eq(BAN_LIST_START_DATE));
		verify(this.dao, times(1))
			.getPreviousBanListDate(eq(BAN_LIST_START_DATE));
	}



	@Test(expected = CacheLoaderException.class)
	public void testFetchingBanListRemovedContent_FromDB_Failure()
		throws YgoException
	{
		when(this.dao.getRemovedContentOfBanList(eq(BAN_LIST_START_DATE)))
			.thenReturn(new ArrayList<>());


		final BanListRemovedContent banListRemovedContentInstance = this.diffService.getRemovedContentOfBanList(BAN_LIST_START_DATE);

		banListRemovedContentInstance.getRemovedCards();


		verify(this.dao, times(1))
			.getRemovedContentOfBanList(eq(BAN_LIST_START_DATE));
		verify(this.dao, times(0))
			.getPreviousBanListDate(eq(BAN_LIST_START_DATE));
	}
}