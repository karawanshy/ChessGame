package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Participant;
import com.workshop.ChessServer.Model.Winner;
import com.workshop.ChessServer.Repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantServiceImpl implements ParticipantService{
    @Autowired
    private ParticipantRepository pRepository;


    @Override
    public Participant saveParticipant(Participant participant) {
        return pRepository.save(participant);
    }

    @Override
    public Participant updateParticipantScore(String userName, int score){
        Participant participant = pRepository.findByUserNameAndScore(userName);
        participant.setScore(score);
        return pRepository.save(participant);
    }

    @Override
    public long getParticipantWinsCount(String userName) {
        return pRepository.findWinsCount(userName);
    }

    @Override
    public long getParticipantLosesCount(String userName) {
        return pRepository.findLosesCount(userName);
    }

    @Override
    public long getParticipantDrawsCount(String userName) {
        return pRepository.findDrawsCount(userName);
    }


    @Override
    public List<Winner> getWinnerParticipants(){
        return pRepository.findWinners();
    }

    @Override
    public void deleteParticipant(long playerID) {
        pRepository.deleteByUserNameAndScore(playerID);
    }

    @Override
    public Participant getParticipantWithNullGameID() {
        return pRepository.findWithNullGameID();
    }

    @Override
    public Participant getParticipantByUserNameAndScore(String userName){
        return pRepository.findByUserNameAndScore(userName);
    }

    @Override
    public Participant getParticipantByGameId(long gid, long pid){
        return pRepository.findByGameId(gid, pid);
    }
}
