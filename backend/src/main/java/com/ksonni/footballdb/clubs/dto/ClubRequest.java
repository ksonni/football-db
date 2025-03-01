package com.ksonni.footballdb.clubs.dto;

import com.ksonni.footballdb.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
@SuperBuilder
@AllArgsConstructor
public abstract class ClubRequest {

    /**
     * Max prestige of a Club.
     */
    public static final int MAX_PRESTIGE = 10;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer overallRating;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer attackRating;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer midfieldRating;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer defenseRating;

    @Min(0)
    private Integer transferBudget;

    @Min(0)
    @Max(MAX_PRESTIGE)
    private Integer domesticPrestige;

    @Min(0)
    @Max(MAX_PRESTIGE)
    private Integer internationalPrestige;

}
