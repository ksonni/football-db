package com.ksonni.footballdb.controllers;


import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.domain.Club;
import com.ksonni.footballdb.repositories.ClubsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.ksonni.footballdb.lib.HttpUtils.QueryParseException;
import static com.ksonni.footballdb.lib.HttpUtils.parseRequestQuery;

@RestController
@AllArgsConstructor
@RequestMapping(value = RoutesConfig.CLUBS_PATH)
public class ClubsController {

    private final ClubsRepository clubsRepository;

    @GetMapping
    public Page<Club> enumerateClubs(HttpServletRequest request) throws QueryParseException {
        return clubsRepository.findAll(parseRequestQuery(request, Club.class));
    }

}
