package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Game;
import com.workshop.ChessServer.Repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService{
    @Autowired
    private GameRepository gRepository;

    @Override
    public Game saveGame(Game game) {
        return gRepository.save(game);
    }
}
