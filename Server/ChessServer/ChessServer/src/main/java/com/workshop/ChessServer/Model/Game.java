package com.workshop.ChessServer.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gid;

    @CreationTimestamp
    @Column(name = "start_time", nullable = false, updatable = false)
    private LocalDate startTime;

    @ManyToOne
    @JoinColumn(name = "first_player_id")
    private Player firstPlayerID;

    @ManyToOne
    @JoinColumn(name = "second_player_id")
    private Player secondPlayerID;
}
