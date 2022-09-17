package com.workshop.ChessServer.Controller;

import com.workshop.ChessServer.Model.*;
import com.workshop.ChessServer.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    /** returns the player's number of wins, loses and draws */
    @GetMapping("/participants/{userName}")
    public ResponseEntity<PlayerProgress> getParticipantProgress(@PathVariable String userName){
        long wins = participantService.getParticipantWinsCount(userName);
        long loses = participantService.getParticipantLosesCount(userName);
        long draws = participantService.getParticipantDrawsCount(userName);

        PlayerProgress progress = new PlayerProgress(wins, loses, draws);

        return new ResponseEntity<>(progress, HttpStatus.OK);
    }

    /** returns the top 3 players with the most wins */
    @GetMapping("/participants")
    public ResponseEntity<List<Winner>> getTop3Winners(){
        // getting a list of all the players that won at least once by getting usernames and number of wins
        List<Winner> winners = participantService.getWinnerParticipants();
        List<Winner> top3 = new ArrayList<>();  //the top 3 list
        Winner first, second, third;    //top 3

        //top player
        first = winners.get(0);
        int i = 1;
        while (winners.get(i).getWins() == first.getWins()){
            i++;
        }
        //second top player
        second = winners.get(i);
        while (winners.get(i).getWins() == second.getWins()){
            i++;
        }
        //third top player
        third = winners.get(i);

        //adding the three top players
        top3.add(first);
        top3.add(second);
        top3.add(third);

        //returning a list of 3 top players
        return new ResponseEntity<List<Winner>>(top3, HttpStatus.OK);
    }

    /** Creates a participant. if there is already one participant waiting fo an opponent it creates a game
     * for them and updates the first participant whose waiting. */
    @PostMapping("/participants/{userName}")
    public ResponseEntity<Map<String, String>> createParticipant(@PathVariable String userName){
        Game game = new Game();
        Participant newParticipant = new Participant();
        Player currPlayer = playerService.getPlayerByUserName(userName);

        Map<String, String> participants = new HashMap<>();
        participants.put("first_player", "");
        participants.put("second_player", "");

        Participant firstParticipant = participantService.getParticipantWithNullGameID();

        if (firstParticipant != null){  //if there is already one participant waiting for a match
            //getting the first player
            Player firstPlayer = playerService.getPlayerByUserName(firstParticipant.getPlayerID().getUserName());

            //create a game
            game.setFirstPlayerID(firstPlayer);
            game.setSecondPlayerID(currPlayer);

            //save the game in database
            gameService.saveGame(game);

            //update the gameID for both participant
            firstParticipant.setGameID(game);
            newParticipant.setGameID(game);

            //update the first participant (after inserting the gameID)
            participantService.saveParticipant(firstParticipant);

            participants.put("first_player", firstPlayer.getUserName());
        }

        //setting the player for the participant
        newParticipant.setPlayerID(currPlayer);
        newParticipant.setScore(-2);

        //saving the participant inn the database
        participantService.saveParticipant(newParticipant);

        participants.put("second_player", userName);

        //returning the newly saved participant
        return new ResponseEntity<Map<String, String>>(participants, HttpStatus.CREATED);
    }

    /** Deletes a participant - a participant who was waiting for a match and gave up so his score is -2. */
    @DeleteMapping("/participants/{userName}")
    public ResponseEntity<HttpStatus> deleteParticipant(@PathVariable String userName){
        participantService.deleteParticipant(playerService.getPlayerByUserName(userName).getId());
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    /** Updates the participant's score. */
    @PostMapping("/participants")
    public ResponseEntity<Participant> updateParticipantScore(@RequestParam(name = "user_name") String userName, @RequestParam int score) {
        return new ResponseEntity<Participant>(participantService.updateParticipantScore(userName, score), HttpStatus.OK);
    }
}
