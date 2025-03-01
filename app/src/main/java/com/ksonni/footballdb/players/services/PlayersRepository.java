package com.ksonni.footballdb.players.services;

import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.queryparser.QueryableRepository;

public interface PlayersRepository extends QueryableRepository<Player, String> {
}
