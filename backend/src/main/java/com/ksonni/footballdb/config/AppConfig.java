package com.ksonni.footballdb.config;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.services.PlayerQueryParser;
import com.ksonni.footballdb.queryparser.DefaultQueryParser;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.services.AuthService;
import com.ksonni.footballdb.users.services.DefaultAuthService;
import com.ksonni.footballdb.users.services.UserQueryParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

@Configuration
public class AppConfig {

    @Bean
    public QueryParser<Club> clubsQueryParser() {
        return new DefaultQueryParser<>(Club.class);
    }

    @Bean
    public QueryParser<Player> playersQueryParser() {
        return new PlayerQueryParser(Player.class);
    }

    @Bean
    public QueryParser<League> leaguesQueryParser() {
        return new DefaultQueryParser<>(League.class);
    }

    @Bean
    public QueryParser<User> usersQueryParser() {
        return new UserQueryParser(User.class);
    }

    @Bean
    public AuthService authService() {
        return new DefaultAuthService();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

}
