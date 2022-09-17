package com.workshop.ChessServer.Repository;

import com.workshop.ChessServer.Model.Participant;
import com.workshop.ChessServer.Model.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    /** Counts the wins made by the player with the given username */
    @Query("SELECT COUNT(score) FROM Participant WHERE playerID.userName=:userName AND score=1")
    long findWinsCount(String userName);

    /** Counts the loses made by the player with the given username */
    @Query("SELECT COUNT(score) FROM Participant WHERE playerID.userName=:userName AND score=0")
    long findLosesCount(String userName);

    /** Counts the draws made by the player with the given username */
    @Query("SELECT COUNT(score) FROM Participant WHERE playerID.userName=:userName AND score=2")
    long findDrawsCount(String userName);

    /** Finds a participant with a null game id - a participant that yet to start a match and is waiting for an opponent */
    @Query(value = "select * from participant where game_id is null", nativeQuery = true) //there can't be more than one cause the client always makes a request to delete participant if a match was cancelled before the game started.
    Participant findWithNullGameID();

    /** Finds the participant with the given username with score=-2 - a participant that is waiting for an opponent */
    @Query(value = "select * from participant p1 inner join player p2 on p1.player_id = p2.id where user_name=:userName and score=-2", nativeQuery = true)
    Participant findByUserNameAndScore(String userName);

    /** Finds participant by game id */
    @Query(value = "select * from participant p inner join game g on p.game_id = g.gid where game_id=:gameID and p.pid<>:participantID", nativeQuery = true)
    Participant findByGameId(long gameID, long participantID);

    /** Deletes a participant with score=-2 - a participant who didn't start a game - quit when waiting foe a match */
    @Transactional
    @Modifying
    @Query(value = "delete from participant p where p.player_id=:playerID and score=-2", nativeQuery = true)
    void deleteByUserNameAndScore(long playerID);

    /** Stores in a table all the players with at least one win and their number of wins*/
    @Query(value = "select p2.user_name as userName, count(p1.pid) as wins from participant p1 inner join player p2 on p1.player_id=p2.id where score=1 group by p2.user_name order by wins desc", nativeQuery = true)
    List<Winner> findWinners();
}
