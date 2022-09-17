package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Player;
import com.workshop.ChessServer.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository pRepository;

    @Override
    public Player savePlayer(Player player) {
        return pRepository.save(player);
    }

    @Override
    public Player getPlayerByUserName(String userName) {
        return pRepository.findByUserName(userName);
    }
}
