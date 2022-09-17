package com.workshop.ChessServer.Controller;

import com.workshop.ChessServer.Model.SendingMessage;
import com.workshop.ChessServer.Model.MessageType;
import com.workshop.ChessServer.Model.User;
import com.workshop.ChessServer.Service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
public class WebSocketEventListener {
    private int numOfConnections = 0;

    @Autowired
    private SimpMessageSendingOperations sendingOperation;

    @Autowired
    private WebSocketService wsService;


    @EventListener
    public void handleWebSocketConnectListener(final SessionConnectedEvent event){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        numOfConnections++;

        final String userID = headerAccessor.getUser().getName();
        final String username = wsService.getUsername(event);

        final SendingMessage sendingMessage = SendingMessage.builder().type(MessageType.CONNECT).message(userID).userName(username).players(numOfConnections).build();

        //add player to a match
        wsService.addPlayer(userID, username);

        Runnable task = () -> sendingOperation.convertAndSend("/players/topic/chess", sendingMessage);
        executorService.schedule(task, 100, TimeUnit.MILLISECONDS);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event){
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        numOfConnections--;

        final String userID =  headerAccessor.getUser().getName();

        User opponent = wsService.getOpponent(userID);
        if (opponent != null) { //there is already two players in this match (it returns null if the first player doesn't want to wait for another player anymore) - which means there was never second player in the match.
            sendingOperation.convertAndSendToUser(opponent.getName(), "/topic/chess", SendingMessage.builder().type(MessageType.DISCONNECT).build());
        }

        wsService.deleteMatch(userID);
    }
}
