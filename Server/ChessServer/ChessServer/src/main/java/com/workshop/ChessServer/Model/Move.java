package com.workshop.ChessServer.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "move")
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "move_cnt")
    private int moveCnt;

    @Column(name = "from_position")
    private String from;

    @Column(name = "to_position")
    private String to;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participantID;

    public Move(int moveCnt, String from, String to, Participant participantID) {
        this.moveCnt = moveCnt;
        this.from = from;
        this.to = to;
        this.participantID = participantID;
    }
}
