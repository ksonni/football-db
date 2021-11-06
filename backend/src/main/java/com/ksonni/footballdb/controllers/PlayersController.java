package com.ksonni.footballdb.controllers;

import com.ksonni.footballdb.config.RoutesConfig;
import com.ksonni.footballdb.domain.Player;
import com.ksonni.footballdb.lib.HttpUtils;
import com.ksonni.footballdb.repositories.PlayersRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.ksonni.footballdb.lib.HttpUtils.parseRequestQuery;

@RestController
@AllArgsConstructor
@RequestMapping(value = RoutesConfig.PLAYERS_PATH)
public class PlayersController {

    private final PlayersRepository playersRepository;

    @GetMapping
    public Page<Player> enumeratePlayers(HttpServletRequest request) throws HttpUtils.QueryParseException {
        return playersRepository.findAll(parseRequestQuery(request, Player.class));
    }

}
