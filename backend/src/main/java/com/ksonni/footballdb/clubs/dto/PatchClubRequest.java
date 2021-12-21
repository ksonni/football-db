package com.ksonni.footballdb.clubs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

@Data
@SuperBuilder
@AllArgsConstructor
public class PatchClubRequest {

    @Length(max = 40)
    private String name;

    private String leagueId;

    @Range(min = 0, max = 100)
    private Integer overallRating;

    @Range(min = 0, max = 100)
    private Integer attackRating;

    @Range(min = 0, max = 100)
    private Integer midfieldRating;

    @Range(min = 0, max = 100)
    private Integer defenseRating;

    @Min(0)
    private Integer transferBudget;

    @Range(min = 0, max = 10)
    private Integer domesticPrestige;

    @Range(min = 0, max = 10)
    private Integer internationalPrestige;

}
