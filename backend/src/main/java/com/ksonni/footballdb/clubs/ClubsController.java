package com.ksonni.footballdb.clubs;


import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = RoutesConfig.CLUBS_PATH)
public class ClubsController {

    private final ClubsRepository clubsRepository;
    private final QueryParser<Club> queryParser;

    @GetMapping
    public Page<Club> enumerateClubs(HttpServletRequest request) throws QueryParseException {
        return clubsRepository.findAll(queryParser.parse(request.getQueryString()));
    }

}
