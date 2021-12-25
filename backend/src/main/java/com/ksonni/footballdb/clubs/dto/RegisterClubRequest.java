package com.ksonni.footballdb.clubs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@SuperBuilder
@AllArgsConstructor
public class RegisterClubRequest {

    @NotBlank
    @Length(max = 40)
    private String name;

    @NotBlank
    private String leagueId;

    @Min(0)
    @Max(100)
    private Integer overallRating;

    @Min(0)
    @Max(100)
    private Integer attackRating;

    @Min(0)
    @Max(100)
    private Integer midfieldRating;

    @Min(0)
    @Max(100)
    private Integer defenseRating;

    @Min(0)
    private Integer transferBudget;

    @Min(0)
    @Max(10)
    private Integer domesticPrestige;

    @Min(0)
    @Max(10)
    private Integer internationalPrestige;

}
