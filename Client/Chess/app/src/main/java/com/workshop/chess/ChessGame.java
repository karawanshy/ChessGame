package com.workshop.chess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** The Model */
public class ChessGame {

    private final Set<ChessPiece> piecesBox = new HashSet<>();

    {
        // resetting the board to its initial state
        reset();
    }

    /** clearing the piecesBox and restoring it to its initial state by adding the right pieces to their correct positions */
    public void reset() {
        piecesBox.clear();

        for (int i=0; i<2; i++){
            piecesBox.add(new ChessPiece(i * 7, 0, ChessPlayer.WHITE, ChessRank.ROOK, R.drawable.rook_white));
            piecesBox.add(new ChessPiece(i * 7, 7, ChessPlayer.BLACK, ChessRank.ROOK, R.drawable.rook_black));

            piecesBox.add(new ChessPiece(1 + i * 5, 0, ChessPlayer.WHITE, ChessRank.KNIGHT, R.drawable.knight_white));
            piecesBox.add(new ChessPiece(1 + i * 5, 7, ChessPlayer.BLACK, ChessRank.KNIGHT, R.drawable.knight_black));

            piecesBox.add(new ChessPiece(2 + i * 3, 0, ChessPlayer.WHITE, ChessRank.BISHOP, R.drawable.bishop_white));
            piecesBox.add(new ChessPiece(2 + i * 3, 7, ChessPlayer.BLACK, ChessRank.BISHOP, R.drawable.bishop_black));
        }

        for (int i=0; i<8; i++){
            piecesBox.add(new ChessPiece(i, 1, ChessPlayer.WHITE, ChessRank.PAWN, R.drawable.pawn_white));
            piecesBox.add(new ChessPiece(i, 6, ChessPlayer.BLACK, ChessRank.PAWN, R.drawable.pawn_black));
        }

        piecesBox.add(new ChessPiece(3, 0, ChessPlayer.WHITE, ChessRank.QUEEN, R.drawable.queen_white));
        piecesBox.add(new ChessPiece(3, 7, ChessPlayer.BLACK, ChessRank.QUEEN, R.drawable.queen_black));

        piecesBox.add(new ChessPiece(4, 0, ChessPlayer.WHITE, ChessRank.KING, R.drawable.king_white));
        piecesBox.add(new ChessPiece(4, 7, ChessPlayer.BLACK, ChessRank.KING, R.drawable.king_black));

    }

    /** returning the piece at the given location if it exists */
    public ChessPiece pieceAt(int col, int row){
        for (ChessPiece piece : piecesBox){
            if(col == piece.getCol() && row == piece.getRow()){
                return piece;
            }
        }
        return null;
    }

    /** checking if the player chose the right piece - not empty square, the destination and source squares are different, he didn't chose to attack his own pieces and he chose to move his own pieces (the right color)*/
    public boolean checkPiece(int fromCol, int fromRow, int toCol, int toRow, String playerColor){
        ChessPiece movingPiece = pieceAt(fromCol, fromRow);
        ChessPiece piece = pieceAt(toCol, toRow);
        if (piece == null)
            return (fromCol != toCol || fromRow != toRow) && movingPiece != null && movingPiece.getPlayer().toString().equals(playerColor);
        return piece.getPlayer() != movingPiece.getPlayer();
    }

    /** moving the chosen piece to the chosen location */
    public void movePiece(int fromCol, int fromRow, int toCol, int toRow){
        ChessPiece movingPiece = pieceAt(fromCol, fromRow);
        ChessPiece piece = pieceAt(toCol, toRow);

        if(movingPiece != null) {
            piecesBox.remove(piece);
            movingPiece.setCol(toCol);
            movingPiece.setRow(toRow);
        }
    }

    /** returns the current pieces on the board - their names and current position */
    public JSONObject getCurrPieces(){
        JSONArray pieceArray = new JSONArray();
        for (ChessPiece chessPiece : piecesBox){
            //getting a piece info with chessPiece info - name from rank and player, position from row and col
            String name = (chessPiece.getRank() + "_" + chessPiece.getPlayer()).toLowerCase(Locale.ROOT);
            String position = Integer.toString(chessPiece.getRow() + 1)+ (char)('a' + chessPiece.getCol());

            //creating a JsonObject with name and position
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("name", name);
                jsonBody.put("position", position);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //adding the JsonObject to the JsonArray
            pieceArray.put(jsonBody);
        }

        //putting the jsonArray inside a JsonObject to send to the server
        JSONObject params = new JSONObject();
        try {
            params.put("Data", pieceArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }
}
