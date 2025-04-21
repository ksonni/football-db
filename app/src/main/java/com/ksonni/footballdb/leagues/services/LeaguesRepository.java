package com.ksonni.footballdb.leagues.services;

import com.ksonni.footballdb.leagues.domain.League;
import com.ksonni.footballdb.query.QueryableRepository;

public interface LeaguesRepository extends QueryableRepository<League, String> {
}
