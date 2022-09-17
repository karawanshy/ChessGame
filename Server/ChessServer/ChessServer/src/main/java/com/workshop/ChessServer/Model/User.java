package com.workshop.ChessServer.Model;

import lombok.Getter;

import java.security.Principal;

@Getter
public class User implements Principal {

    private String name;

    public User(String name) {
        this.name = name;
    }
}