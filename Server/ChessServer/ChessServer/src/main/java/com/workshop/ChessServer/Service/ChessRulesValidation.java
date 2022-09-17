package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Piece;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChessRulesValidation {

    public boolean canKnightMove(int fromCol, int fromRow, int toCol, int toRow) {
        return (Math.abs(fromCol - toCol) == 2 && Math.abs(fromRow - toRow) == 1) ||
                (Math.abs(fromCol - toCol) == 1 && Math.abs(fromRow - toRow) == 2);
    }

    public boolean canRookMove(int fromCol, int fromRow, int toCol, int toRow, List<Piece> pieceList) {
        return (fromCol == toCol && isClearVertically(fromCol, fromRow, toCol, toRow, pieceList)) || (fromRow == toRow && isClearHorizontally(fromCol, fromRow, toCol, toRow, pieceList));
    }

    public boolean isClearHorizontally(int fromCol, int fromRow, int toCol, int toRow, List<Piece> pieceList) {
        if (fromRow != toRow) return false;
        int gap = Math.abs(fromCol - toCol) - 1;
        if (gap == 0) return true;
        int nextCol;
        for (int i = 1; i <= gap; i++) {
            if (toCol > fromCol)
                nextCol = fromCol + i;
            else
                nextCol = fromCol - i;
            if (getPieceNameByPosition(Integer.toString(fromRow) + (char)('a' + nextCol - 1), pieceList) != null)   //converting the raw and col to position format. e.g. 2e instead of row = 2, col = 5
                return false;
        }
        return true;
    }

    public boolean isClearVertically(int fromCol, int fromRow, int toCol, int toRow, List<Piece> pieceList) {
        if (fromCol != toCol) return false;
        int gap = Math.abs(fromRow - toRow) - 1;
        if (gap == 0) return true;
        int nextRow;
        for (int i = 1; i <= gap; i++) {
            if (toRow > fromRow)
                nextRow = fromRow + i;
            else
                nextRow = fromRow - i;
            if (getPieceNameByPosition(Integer.toString(nextRow) + (char)('a' + fromCol - 1), pieceList) != null)
                return false;
        }
        return true;
    }

    public boolean canBishopMove(int fromCol, int fromRow, int toCol, int toRow,List<Piece> pieceList) {
        if (Math.abs(fromCol - toCol) == Math.abs(fromRow - toRow))
            return isClearDiagonally(fromCol, fromRow, toCol, toRow, pieceList);
        return false;
    }

    public boolean isClearDiagonally(int fromCol, int fromRow, int toCol, int toRow, List<Piece> pieceList) {
        int gap = Math.abs(fromCol - toCol) - 1;
        int nextCol, nextRow;
        for (int i = 1; i <= gap; i++) {
            if (toCol > fromCol)
                nextCol = fromCol + i;
            else
                nextCol = fromCol - i;
            if (toRow > fromRow)
                nextRow = fromRow + i;
            else
                nextRow = fromRow - i;
            if (getPieceNameByPosition(Integer.toString(nextRow) + (char)('a' + nextCol - 1), pieceList) != null)
                return false;
        }
        return true;
    }

    public boolean canQueenMove(int fromCol, int fromRow, int toCol, int toRow, List<Piece> pieceList) {
        return canRookMove(fromCol, fromRow, toCol, toRow, pieceList) || canBishopMove(fromCol, fromRow, toCol, toRow, pieceList);
    }

    public boolean canKingMove(int fromCol, int fromRow, int toCol, int toRow, List<Piece> pieceList) {
        if (canQueenMove(fromCol, fromRow, toCol, toRow, pieceList)) {
            int deltaCol = Math.abs(fromCol - toCol);
            int deltaRow = Math.abs(fromRow - toRow);
            return (deltaCol == 1 && deltaRow == 1) || deltaCol + deltaRow == 1;
        }
        return false;
    }

    public boolean canPawnMove(int fromCol, int fromRow, int toCol, int toRow, List<Piece> pieceList) {
        //if pawn is to move to position in the same column
        if (fromCol == toCol) {
            if (fromRow == 2)   //check if it is a starting move for the white pawn
                return (toRow == 3 || toRow == 4) && (getPieceNameByPosition(Integer.toString(3) + (char) ('a' + toCol - 1), pieceList) == null);
            else if (fromRow == 7)  //check if it is a starting move for the black pawn
                return (toRow == 6 || toRow == 5) && (getPieceNameByPosition(Integer.toString(5) + (char) ('a' + toCol - 1), pieceList) == null);
            else {  //if it is not a starting move
                String pieceName = getPieceNameByPosition(Integer.toString(fromRow) + (char) ('a' + fromCol - 1), pieceList);   //getting the piece name
                //checking the piece color to validate if the pawn is moving forward one position.
                if (pieceName.endsWith("black"))
                    return fromRow - toRow == 1;
                else if (pieceName.endsWith("white"))
                    return toRow - fromRow == 1;
                else return false;  //invalid move - can't move more than one position in the same column when it's not a starting move.
            }
        } else return Math.abs(fromCol - toCol) == 1 &&
                Math.abs(fromRow - toRow) == 1 &&
                getPieceNameByPosition(Integer.toString(toRow) + (char) ('a' + toCol - 1), pieceList) != null;    //checking if the piece is to move diagonally one position to capture.
    }

    /** Checks if its checkmate on the opponent */
    public boolean isCheckMate(String playerColor, String attackColor, List<Piece> pieceList){
        String kingPosition = getPiecePositionByName("king_" + attackColor, pieceList).get(0);
        int kingCol = kingPosition.charAt(1) - 'a' + 1;
        int kingRow = Integer.parseInt(kingPosition.substring(0, 1));
        boolean checkMate = false;

        for (int row=kingRow-1; row<=kingRow+1; row++){
            for (int col=kingCol-1; col<=kingCol+1; col++){
                if(col != kingCol && row != kingRow && row <= 8 && col <= 8 && row >= 1 && col >= 1){
                    checkMate = checkMate || checkAroundKing("rook", col, row, playerColor, 2, pieceList);
                    checkMate = checkMate || checkAroundKing("knight", col, row, playerColor, 2, pieceList);
                    checkMate = checkMate || checkAroundKing("bishop", col, row, playerColor, 2, pieceList);
                    checkMate = checkMate || checkAroundKing("queen", col, row, playerColor, 1, pieceList);
                    checkMate = checkMate || checkAroundKing("king", col, row, playerColor, 1, pieceList);
                    checkMate = checkMate || checkAroundKing("pawn", col, row, playerColor, 8, pieceList);
                    checkMate = checkMate || (getPieceNameByPosition(Integer.toString(row) + (char) ('a' + col - 1), pieceList) != null);

                    if (!checkMate)
                        return false;
                }
            }
        }
        return true;
    }

    /** Checks if any of the opponent pieces threatens any of the places the king can run to. */
    private boolean checkAroundKing(String pieceName, int toCol, int toRow, String opponentColor, int pieceCount, List<Piece> pieceList){
        List<String> positionList;
        int currCol;
        int currRow;
        boolean canMove = false;

        positionList = getPiecePositionByName(pieceName + "_" + opponentColor, pieceList);
        for (int i=0; i<pieceCount; i++){
            currCol = positionList.get(i).charAt(1) - 'a' + 1;
            currRow = Integer.parseInt(positionList.get(i).substring(0, 1));

            switch (pieceName){
                case "rook":
                    canMove = canMove || canRookMove(currCol, currRow, toCol, toRow, pieceList);
                case "knight":
                    canMove = canMove || canKnightMove(currCol, currRow, toCol, toRow);
                case "bishop":
                    canMove = canMove || canBishopMove(currCol, currRow, toCol, toRow, pieceList);
                case "queen":
                    canMove = canMove || canQueenMove(currCol, currRow, toCol, toRow, pieceList);
                case "king":
                    canMove = canMove || canKingMove(currCol, currRow, toCol, toRow, pieceList);
                case "pawn":
                    canMove = canMove || canPawnMove(currCol, currRow, toCol, toRow, pieceList);

            }
        }
        return canMove;
    }

    public String getPieceNameByPosition(String position, List<Piece> pieceList){
        for (Piece piece : pieceList){
            if(piece.getPosition().equals(position)){
                return piece.getName();
            }
        }
        return null;
    }

    public List<String> getPiecePositionByName(String name, List<Piece> pieceList){
        List<String> piecesPositions = new ArrayList<>();
        for (Piece piece : pieceList){
            if(piece.getName().equals(name))
                piecesPositions.add(piece.getPosition());
        }
        return piecesPositions;
    }
}
