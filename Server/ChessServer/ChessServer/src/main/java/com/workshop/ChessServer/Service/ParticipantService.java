package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Participant;
import com.workshop.ChessServer.Model.Winner;

import java.util.List;

public interface ParticipantService {

    Participant saveParticipant(Participant participant);

    long getParticipantWinsCount(String userName);

    long getParticipantLosesCount(String userName);

    long getParticipantDrawsCount(String userName);

    List<Winner> getWinnerParticipants();

    void deleteParticipant(long playerID);

    Participant updateParticipantScore(String userName, int score);

    Participant getParticipantWithNullGameID();

    Participant getParticipantByUserNameAndScore(String userName);

    Participant getParticipantByGameId(long gid, long pid);
}
