package com.workshop.ChessServer.Model;

/** A wrapper for the top 3 winners to be sent back to the client */
public interface Winner {

    String getUserName();

    long getWins();
}
