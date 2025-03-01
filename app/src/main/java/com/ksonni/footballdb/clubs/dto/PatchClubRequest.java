package com.ksonni.footballdb.clubs.dto;

import com.ksonni.footballdb.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@SuperBuilder
@Jacksonized
public class PatchClubRequest extends ClubRequest {

    @Length(max = StringUtils.STRING_MAX_LEN)
    private String name;

    private String leagueId;

    PatchClubRequest(
            final Integer overallRating,
            final Integer attackRating,
            final Integer midfieldRating,
            final Integer defenseRating,
            final Integer transferBudget,
            final Integer domesticPrestige,
            final Integer internationalPrestige,
            final String name,
            final String leagueId
    ) {
        super(overallRating, attackRating, midfieldRating, defenseRating, transferBudget,
                domesticPrestige, internationalPrestige);
        this.name = name;
        this.leagueId = leagueId;
    }

}
