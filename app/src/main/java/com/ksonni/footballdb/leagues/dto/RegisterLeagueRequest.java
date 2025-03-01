package com.ksonni.footballdb.leagues.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class RegisterLeagueRequest {

    @NotBlank
    @Length(max = StringUtils.STRING_MAX_LEN)
    private String name;

    /**
     * Creates a RegisterLeagueRequest.
     *
     * @param name name of the league
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RegisterLeagueRequest(final String name) {
        this.name = name;
    }

}
