package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.leagues.services.LeaguesRepository;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = RoutesConfig.LEAGUES_PATH)
@RequiredArgsConstructor
public class LeaguesController {

    private final LeaguesRepository leaguesRepository;
    private final QueryParser<League> queryParser;

    @GetMapping
    public Page<League> enumerateLeagues(HttpServletRequest request) throws QueryParseException {
        return leaguesRepository.findAll(queryParser.parse(request.getQueryString()));
    }

}
