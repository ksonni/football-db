package com.ksonni.footballdb.players.dto;

import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class RegisterPlayerRequest {

    @NotBlank
    @Length(min = 1, max = 40)
    private String fullName;

    @Range(min = 0, max = 500)
    private Integer height;

    @Range(min = 0, max = 1000)
    private Integer weight;

    @Range(min = 0, max =  100)
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

    @Range(min = 0, max = 10)
    private Integer reputation;

    private WorkRate attackingWorkRate;

    private WorkRate defensiveWorkRate;

    @Range(min = 0, max = 100)
    private Integer shootingTotal;

    @Range(min = 0, max = 100)
    private Integer passingTotal;

    @Range(min = 0, max = 100)
    private Integer dribblingTotal;

    @Range(min = 0, max = 100)
    private Integer defendingTotal;

    @Range(min = 0, max = 100)
    private Integer headingAccuracy;

    @Range(min = 0, max = 100)
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

    @Length(min = 1, max = 4)
    @NotBlank
    private String countryCode;

}
