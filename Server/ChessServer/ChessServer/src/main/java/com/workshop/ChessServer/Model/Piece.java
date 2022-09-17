package com.workshop.ChessServer.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Piece {
    @JsonProperty("name")
    private String name;

    @JsonProperty("position")
    private String position;
}
