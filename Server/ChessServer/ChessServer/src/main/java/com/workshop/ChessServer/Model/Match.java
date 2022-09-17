package com.workshop.ChessServer.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Match {
    private String username1;
    private User user1;

    private String username2;
    private User user2;

    public Match(User user1, String username1){
        this.user1 = user1;
        this.username1 = username1;
        user2 = null;
    }
}
