package com.workshop.chess;

public class ChessPiece {
    private int col;
    private int row;
    private ChessPlayer chessPlayer;
    private ChessRank rank;
    private int resID;


    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public ChessPlayer getPlayer() {
        return chessPlayer;
    }

    public void setPlayer(ChessPlayer chessPlayer) {
        this.chessPlayer = chessPlayer;
    }

    public ChessRank getRank() {
        return rank;
    }

    public void setRank(ChessRank rank) {
        this.rank = rank;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public ChessPiece(int col, int row, ChessPlayer chessPlayer, ChessRank rank, int resID) {
        this.col = col;
        this.row = row;
        this.chessPlayer = chessPlayer;
        this.rank = rank;
        this.resID = resID;
    }
}
