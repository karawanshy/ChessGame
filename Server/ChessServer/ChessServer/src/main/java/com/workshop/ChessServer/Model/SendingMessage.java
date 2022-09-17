package com.workshop.ChessServer.Model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SendingMessage {

    private MessageType type;

    private String message;

    private String move;

    private String userName;

    private int players;
}
