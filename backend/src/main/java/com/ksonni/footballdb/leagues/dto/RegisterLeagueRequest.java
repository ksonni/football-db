package com.ksonni.footballdb.leagues.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class RegisterLeagueRequest {

    @NotBlank
    @Length(max = 40)
    private String name;

    // Jackson fails to deserialize single property objects without this
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RegisterLeagueRequest(String name) {
        this.name = name;
    }
}
