package com.ksonni.footballdb.config;

import com.ksonni.footballdb.clubs.Club;
import com.ksonni.footballdb.leagues.League;
import com.ksonni.footballdb.players.Player;
import com.ksonni.footballdb.queryparser.DefaultQueryParser;
import com.ksonni.footballdb.queryparser.QueryParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public QueryParser<Club> clubsQueryParser() {
        return new DefaultQueryParser<>(Club.class);
    }

    @Bean
    public QueryParser<Player> playersQueryParser() {
        return new DefaultQueryParser<>(Player.class);
    }

    @Bean
    public QueryParser<League> leaguesQueryParser() {
        return new DefaultQueryParser<>(League.class);
    }

}
