package com.ksonni.footballdb.players.dto;

import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

@Getter
@Setter
@SuperBuilder
public class PatchPlayerRequest extends PlayerRequest {

    @Length(min = 1, max = StringUtils.STRING_MAX_LEN)
    private String fullName;

    @Length(min = 1, max = StringUtils.COUNTRY_CODE_MAX_LEN)
    private String countryCode;

    private String position;

    private Side preferredFoot;

    private String clubId;

    @Min(0)
    private Integer squadNumber;

    // This large constructor is only used by Jackson for deserialization
    @SuppressWarnings("checkstyle:ParameterNumber")
    PatchPlayerRequest(
            final Integer height,
            final Integer weight,
            final Integer overall,
            final Integer valueEuro,
            final Integer wageEuro,
            final Integer contractEndYear,
            final Integer contractStartYear,
            final Integer reputation,
            final WorkRate attackingWorkRate,
            final WorkRate defensiveWorkRate,
            final Integer shootingTotal,
            final Integer passingTotal,
            final Integer dribblingTotal,
            final Integer defendingTotal,
            final Integer headingAccuracy,
            final Integer penalties,
            final Integer birthYear,
            final String fullName,
            final String countryCode,
            final String position,
            final Side preferredFoot,
            final String clubId,
            final Integer squadNumber) {
        super(height, weight, overall, valueEuro, wageEuro, contractEndYear, contractStartYear, reputation,
                attackingWorkRate, defensiveWorkRate, shootingTotal, passingTotal, dribblingTotal, defendingTotal,
                headingAccuracy, penalties, birthYear);
        this.fullName = fullName;
        this.countryCode = countryCode;
        this.position = position;
        this.preferredFoot = preferredFoot;
        this.clubId = clubId;
        this.squadNumber = squadNumber;
    }

}
