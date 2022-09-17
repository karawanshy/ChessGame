package com.workshop.ChessServer.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.ChessServer.Service.ChessRulesValidation;
import com.workshop.ChessServer.Model.*;
import com.workshop.ChessServer.Service.MoveService;
import com.workshop.ChessServer.Service.ParticipantService;
import com.workshop.ChessServer.Service.WebSocketService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class MoveController {
    @Autowired
    private MoveService moveService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ChessRulesValidation validate;

    @Autowired
    private WebSocketService wsService;

    /** The move is given as a concatenation of the strings: the source position and the destination position. e.g. 1b3c - move the piece in 1b to 3c
     * The data is a jsonObject were the value is a jsonArray holding a list of the current pieces in the board in the client */
    @PostMapping("/moves/{move}")
    public Map<String, String> checkAndSaveMove(@PathVariable String move, @RequestParam("user_name") String userName, @RequestBody String data) {

        JSONObject json = new JSONObject(data);
        final ObjectMapper objectMapper = new ObjectMapper();
        List<Piece> pieceList = null;

        try {
            pieceList = objectMapper.readValue(json.getString("Data"), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // get the source and destination of the move
        String from = move.substring(0, 2);
        String to = move.substring(2, 4);

        //getting the moving piece name
        assert pieceList != null;
        String pieceName = getPieceNameByPosition(from, pieceList);

        if (canMove(pieceName, from, to, pieceList)) {
            Participant participant = participantService.getParticipantByUserNameAndScore(userName); //score = -2 means the game is still going

            //saving the move in database
            moveService.saveMove(new Move(  moveService.getMaxCountByParticipant(participant.getPid()) + 1, from , to, participant));

            // updating the moving piece position in the pieces list
            updatePiece(pieceList, from, to);

            boolean checkMate = false;
            String pieceColor = pieceName.substring(pieceName.length() - 5);    //getting the piece color

            // check if after making the move any of the opponent pieces are threatening the player's king
            if (check(pieceColor, pieceList)) {
                // the move is invalid
                return Collections.singletonMap("response", "INVALID");
            }

            if (pieceColor.equals("white")) {
                // check if after making the move any of the pieces is threatening the opponent's king
                if (check("black", pieceList)) {
                    //check if it's checkmate
                    checkMate = validate.isCheckMate("white", "black", pieceList);
                }
            } else {
                // check if after making the move any of the pieces is threatening the opponent's king
                if (check("white", pieceList))
                    //check if it's checkmate
                    checkMate = validate.isCheckMate("black", "white", pieceList);
            }

            if(checkMate) {
                wsService.sendMove(MessageType.MOVE, "CHECKMATE", move, userName);
                return Collections.singletonMap("response", "CHECKMATE");
            }

            if (check50Moves(participant, pieceList)) {
                wsService.sendMove(MessageType.MOVE, "50MOVES", move, userName);
                return Collections.singletonMap("response", "50MOVES");
            }

            wsService.sendMove(MessageType.MOVE, "VALID", move, userName);
            return Collections.singletonMap("response", "VALID");
        }

        return Collections.singletonMap("response", "INVALID");
    }

    /** checks if the given move is valid */
    private boolean canMove(String pieceName, String from, String to, List<Piece> pieceList){

        int fromCol = from.charAt(1) - 'a' + 1; //getting the number of the column to move from
        int fromRow = Integer.parseInt(from.substring(0, 1));   //getting the number of the row to move from
        int toCol = to.charAt(1) - 'a' + 1; //getting the number of the column to move to
        int toRow = Integer.parseInt(to.substring(0, 1)); //getting the number of the column to move to
        pieceName = pieceName.substring(0, pieceName.length() - 6); //getting the name of the chess piece to move

        //checking if it's a valid move
        return switch (pieceName){
            case "knight" -> validate.canKnightMove(fromCol, fromRow, toCol, toRow);
            case "rook" -> validate.canRookMove(fromCol, fromRow, toCol, toRow, pieceList);
            case "bishop" -> validate.canBishopMove(fromCol, fromRow, toCol, toRow, pieceList);
            case "queen" -> validate.canQueenMove(fromCol, fromRow, toCol, toRow, pieceList);
            case "king" -> validate.canKingMove(fromCol, fromRow, toCol, toRow, pieceList);
            case "pawn" -> validate.canPawnMove(fromCol, fromRow, toCol, toRow, pieceList);
            default -> false;
        };
    }

    /** gets the moving piece name from the pieces list by the given position */
    public String getPieceNameByPosition(String position, List<Piece> pieceList){
        for (Piece piece : pieceList){
            if(piece.getPosition().equals(position))
                return piece.getName();
        }
        return null;
    }

    /** updates the piece position in the pieces list - needed to check checkmate after*/
    public void updatePiece(List<Piece> pieceList, String from, String to){
        //if there is a piece in "to"' it means we need to capture it, so we remove it from the list
        pieceList.removeIf(piece -> piece.getPosition().equals(to));

        // updating the moving piece position
        for (Piece piece : pieceList){
            if (piece.getPosition().equals(from))
                piece.setPosition(to);
        }
    }

    /** checks if any of the pieces threatening the king with the given color */
    public boolean check(String pieceColor, List<Piece> pieceList){
        boolean check = false;
        for (Piece piece : pieceList){
            // checks if the piece is the opposite color of the given color and can move to the king's position of the given color - threatening the king
            if(!piece.getName().endsWith(pieceColor) && canMove(piece.getName(), piece.getPosition(), validate.getPiecePositionByName("king_" + pieceColor, pieceList).get(0), pieceList)) {
                check = true;
            }
        }
        return check;
    }

    /** checks if the players made already 50 moves and no piece has been captured yet */
    public boolean check50Moves(Participant participant, List<Piece> pieceList){
        Participant opponent = participantService.getParticipantByGameId(participant.getGameID().getGid(), participant.getPid());
        return  moveService.getMaxCountByParticipant(opponent.getPid()) == 25 && moveService.getMaxCountByParticipant(participant.getPid()) == 25 && pieceList.size() == 32;
    }
}
