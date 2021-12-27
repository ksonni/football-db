package com.ksonni.footballdb.clubs.services;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.queryparser.QueryableRepository;

public interface ClubsRepository extends QueryableRepository<Club, String> {
}
