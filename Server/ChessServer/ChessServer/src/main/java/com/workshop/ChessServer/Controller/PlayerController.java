package com.workshop.ChessServer.Controller;

import com.workshop.ChessServer.Model.Player;
import com.workshop.ChessServer.Service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class PlayerController {

    @Autowired
    private PlayerService pService;

    /** adds the given player to the database if values of the attributes are legal*/
    @PostMapping("/players")
    public ResponseEntity<Player> savePlayer(@Valid @RequestBody Player player){
        return new ResponseEntity<Player>(pService.savePlayer(player), HttpStatus.CREATED);
    }

    /** gets the player with given username from the database and returns it to the client*/
    @GetMapping("/players/{userName}")
    public ResponseEntity<Player> getPlayerByUserName(@PathVariable String userName){
        return new ResponseEntity<Player>(pService.getPlayerByUserName(userName), HttpStatus.OK);
    }
}
