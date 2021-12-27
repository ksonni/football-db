package com.ksonni.footballdb.players.dto;

import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.utils.MathUtils;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class RegisterPlayerRequest {

    @NotBlank
    @Length(min = 1, max = StringUtils.STRING_MAX_LEN)
    private String fullName;

    @Min(0)
    @Max(Player.MAX_HEIGHT)
    private Integer height;

    @Min(0)
    @Max(Player.MAX_WEIGHT)
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

    @NotNull
    private Side preferredFoot;

    @Min(0)
    @Max(Player.MAX_REPUTATION)
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

    @NotBlank
    private String clubId;

    @Min(0)
    @NotNull
    private Integer squadNumber;

    @NotBlank
    private String position;

    @Min(0)
    private Integer birthYear;

    @NotBlank
    @Length(min = 1, max = StringUtils.COUNTRY_CODE_MAX_LEN)
    private String countryCode;

}
