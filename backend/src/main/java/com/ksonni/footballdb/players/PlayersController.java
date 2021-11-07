package com.ksonni.footballdb.players;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@RequestMapping(value = RoutesConfig.PLAYERS_PATH)
public class PlayersController {

    private final PlayersRepository playersRepository;
    private final QueryParser<Player> queryParser;

    @GetMapping
    public Page<Player> enumeratePlayers(HttpServletRequest request) throws QueryParseException {
        return playersRepository.findAll(queryParser.parse(request.getQueryString()));
    }

}
