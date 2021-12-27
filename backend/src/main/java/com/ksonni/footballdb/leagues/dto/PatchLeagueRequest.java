package com.ksonni.footballdb.leagues.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ksonni.footballdb.utils.StringUtils;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class PatchLeagueRequest {

    @Length(max = StringUtils.STRING_MAX_LEN)
    private String name;

    /**
     * Creates a PatchLeagueRequest.
     *
     * @param name name of the league
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PatchLeagueRequest(final String name) {
        this.name = name;
    }

}
