package com.workshop.ChessServer.Service;

import com.workshop.ChessServer.Model.Match;
import com.workshop.ChessServer.Model.SendingMessage;
import com.workshop.ChessServer.Model.MessageType;
import com.workshop.ChessServer.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpTemplate;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    private static final List<Match> matches = new ArrayList<>();

    /** Sends the move made to the other participant */
    public void sendMove(MessageType messageType, String message, String move, String userName){
        User opponent = getOpponentByUsername(userName);
        simpTemplate.convertAndSendToUser(opponent.getName(), "/topic/chess", SendingMessage.builder().type(messageType).userName(userName).message(message).move(move).build());
    }

    public boolean isFirst(){
        if (matches.isEmpty())
            return  true;
        Match match = matches.get(matches.size() - 1);
        return match.getUser2() != null;  //return if it's the first player or the second player
    }

    /** returns the opponent of the given user by username */
    public User getOpponentByUsername(String username){
        for (Match match : matches){
            if (match.getUsername1().equals(username))
                return match.getUser2();
            if (match.getUsername2().equals(username))
                return match.getUser1();
        }
        return null;
    }

    /** returns the opponent of the given user */
    public User getOpponent(String userID){
        for (Match match : matches){
            if (match.getUser1().getName().equals(userID))
                return match.getUser2();
            if (match.getUser2().getName().equals(userID))
                return match.getUser1();
        }
        return null;
    }

    public synchronized void addPlayer(String userID, String username){
        if (isFirst()){ //if it's the first player create a new match - there is no player waiting yet
            matches.add(new Match(new User(userID), username));
        } else {    //if it's the second player add him to the match of last connected player
            matches.get(matches.size() - 1).setUser2(new User(userID));
            matches.get(matches.size() - 1).setUsername2(username);
        }
    }

    public synchronized void deleteMatch(String username) {
        matches.removeIf(match -> match.getUser1().getName().equals(username) || (match.getUser2() != null && match.getUser2().getName().equals(username)));
    }

    /** gets the username of the newly connected user */
    public String getUsername(final SessionConnectedEvent event){
        Message<?> message = (Message<?>) event.getMessage().getHeaders().get("simpConnectMessage");

        Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
        String username = "";

        if (raw instanceof Map) {
            Object name = ((Map) raw).get("username");

            if (name instanceof ArrayList) {
                username =  ((ArrayList<String>) name).get(0);
            }
        }
        return username;
    }
}
