package com.ksonni.footballdb.clubs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubResponse {

    private String id;

    private String name;

    private String leagueId;

    private Integer overallRating;

    private Integer attackRating;

    private Integer midfieldRating;

    private Integer defenseRating;

    private Integer transferBudget;

    private Integer domesticPrestige;

    private Integer internationalPrestige;

}
