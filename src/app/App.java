package app;
import client.*;
import exceptions.*;
import gameplay.*;
import pokercards.*;
import server.*;

public class App {
    Game game;
    Server server;
    Client client;
    ClientHandler clientHander;

    public App(Game game) {
        this.game = game;
    }
    public static void main(String[] args) {
        return;
    }
}
