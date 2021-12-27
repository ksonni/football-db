package com.ksonni.footballdb.clubs.dto;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.utils.MathUtils;
import com.ksonni.footballdb.utils.StringUtils;
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
    @Length(max = StringUtils.STRING_MAX_LEN)
    private String name;

    @NotBlank
    private String leagueId;

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
    @Max(Club.MAX_PRESTIGE)
    private Integer domesticPrestige;

    @Min(0)
    @Max(Club.MAX_PRESTIGE)
    private Integer internationalPrestige;

}
