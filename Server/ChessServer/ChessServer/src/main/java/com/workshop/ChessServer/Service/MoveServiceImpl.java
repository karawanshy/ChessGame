package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Move;
import com.workshop.ChessServer.Repository.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MoveServiceImpl implements MoveService{

    @Autowired
    MoveRepository mRepository;

    @Override
    public void saveMove(Move move) {
        mRepository.save(move);
    }

    @Override
    public int getMaxCountByParticipant(long participantID) {
        return mRepository.getMaxCount(participantID);
    }
}
