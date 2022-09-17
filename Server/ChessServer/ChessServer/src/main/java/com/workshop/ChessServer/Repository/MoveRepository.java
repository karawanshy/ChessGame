package com.workshop.ChessServer.Repository;

import com.workshop.ChessServer.Model.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveRepository extends JpaRepository<Move, Long> {

    // gets the count of the last move made by the given participant
    @Query("SELECT COALESCE(MAX(moveCnt), 0) FROM Move WHERE participantID.pid=:id")
    int getMaxCount(@Param("id") long participantID);
}
