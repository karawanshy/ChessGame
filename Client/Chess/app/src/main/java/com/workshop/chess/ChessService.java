package com.workshop.chess;

/** The Service Interface - connects between The model and The View */
public interface ChessService {
    ChessPiece pieceAt(int col, int row);
    void movePiece(int fromCol, int fromRow, int toCol, int toRow);
    void moveOpponentPiece(int fromCol, int fromRow, int toCol, int toRow);
}
