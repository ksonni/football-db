package com.ksonni.footballdb.leagues;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.queryapi.Query;
import com.ksonni.footballdb.queryapi.QueryParseException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = RoutesConfig.LEAGUES_PATH)
@AllArgsConstructor
public class LeaguesController {

    private LeaguesRepository leaguesRepository;

    @GetMapping
    public Page<League> enumerateLeagues(HttpServletRequest request) throws QueryParseException {
        return leaguesRepository.findAll(new Query<>(request.getQueryString(), League.class));
    }

}
