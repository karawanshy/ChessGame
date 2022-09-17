package com.workshop.ChessServer.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Represents the player's progress to be sent back to the client */
@AllArgsConstructor
@Getter
public class PlayerProgress {

    private long wins;

    private long loses;

    private long draws;
}
