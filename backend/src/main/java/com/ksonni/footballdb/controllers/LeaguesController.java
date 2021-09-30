package com.ksonni.footballdb.controllers;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.domain.League;
import com.ksonni.footballdb.repositories.LeaguesRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.ksonni.footballdb.lib.HttpUtils.QueryParseException;
import static com.ksonni.footballdb.lib.HttpUtils.parseRequestQuery;

@RestController
@RequestMapping(value = RoutesConfig.LEAGUES_PATH)
@AllArgsConstructor
public class LeaguesController {

    private LeaguesRepository leaguesRepository;

    @GetMapping
    public Page<League> enumerateLeagues(HttpServletRequest request) throws QueryParseException {
        return leaguesRepository.findAll(parseRequestQuery(request, League.class));
    }

}
