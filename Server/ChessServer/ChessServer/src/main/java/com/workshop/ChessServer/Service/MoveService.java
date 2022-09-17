package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Move;

public interface MoveService {

    void saveMove(Move move);

    int getMaxCountByParticipant(long participantID);
}
