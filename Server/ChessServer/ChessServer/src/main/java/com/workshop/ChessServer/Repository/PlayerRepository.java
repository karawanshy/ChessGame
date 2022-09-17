package com.workshop.ChessServer.Repository;

import com.workshop.ChessServer.Model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByUserName(String userName);
}
