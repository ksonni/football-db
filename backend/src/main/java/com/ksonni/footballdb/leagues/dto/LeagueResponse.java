package com.ksonni.footballdb.leagues.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeagueResponse {

    private String id;

    private String name;

}
