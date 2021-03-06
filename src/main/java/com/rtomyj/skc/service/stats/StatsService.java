package com.rtomyj.skc.service.stats;

import com.google.inject.internal.util.Strings;
import com.rtomyj.skc.dao.database.Dao;
import com.rtomyj.skc.model.Stats.DatabaseStats;
import com.rtomyj.skc.model.Stats.MonsterTypeStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class StatsService {

    private final Dao dao;


    @Autowired
    public StatsService(@Qualifier("jdbc") final Dao dao)
    {

        this.dao = dao;

    }


    public MonsterTypeStats getMonsterTypeStats(final String cardColor)
    {

        final MonsterTypeStats monsterTypeStats = dao.getMonsterTypeStats(Strings.capitalize(cardColor));
        monsterTypeStats.setLinks();

        return monsterTypeStats;

    }


    public DatabaseStats getDatabaseStats()
    {

        final DatabaseStats databaseStats = dao.getDatabaseStats();
        databaseStats.setLinks();

        return databaseStats;

    }

}
