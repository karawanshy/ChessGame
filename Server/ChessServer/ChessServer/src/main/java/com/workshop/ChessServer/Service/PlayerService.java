package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Player;

public interface PlayerService {

    Player savePlayer(Player player);

    Player getPlayerByUserName(String userName);
}
