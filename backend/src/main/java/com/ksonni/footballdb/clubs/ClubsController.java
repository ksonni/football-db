package com.ksonni.footballdb.clubs;


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
@AllArgsConstructor
@RequestMapping(value = RoutesConfig.CLUBS_PATH)
public class ClubsController {

    private final ClubsRepository clubsRepository;

    @GetMapping
    public Page<Club> enumerateClubs(HttpServletRequest request) throws QueryParseException {
        return clubsRepository.findAll(new Query<>(request.getQueryString(), Club.class));
    }

}
