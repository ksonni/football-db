package com.ksonni.footballdb.players.dto;

import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.utils.MathUtils;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
@SuperBuilder
@AllArgsConstructor
public abstract class PlayerRequest {

    /**
     * Max height of a player in cm.
     */
    public static final int MAX_HEIGHT = 500;

    /**
     * Max reputation.
     */
    public static final int MAX_REPUTATION = 10;

    /**
     * Max weight of a player in kg.
     */
    public static final int MAX_WEIGHT = 500;

    @Min(0)
    @Max(MAX_HEIGHT)
    private Integer height;

    @Min(0)
    @Max(MAX_WEIGHT)
    private Integer weight;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer overall;

    @Min(0)
    private Integer valueEuro;

    @Min(0)
    private Integer wageEuro;

    @Min(0)
    private Integer contractEndYear;

    @Min(0)
    private Integer contractStartYear;

    @Min(0)
    @Max(MAX_REPUTATION)
    private Integer reputation;

    private WorkRate attackingWorkRate;

    private WorkRate defensiveWorkRate;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer shootingTotal;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer passingTotal;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer dribblingTotal;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer defendingTotal;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer headingAccuracy;

    @Min(0)
    @Max(MathUtils.MAX_PERCENT)
    private Integer penalties;

    @Min(0)
    private Integer birthYear;

    @Length(max = StringUtils.STRING_MAX_LEN)
    private String image;

}
