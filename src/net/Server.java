package net;

import application.Game;
import application.SharedSources;
import model.Board;
import model.HumanPlayer;
import model.Player;
import net.packet.InputPacket;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{
    private final Socket socket;
    public Server(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        receiveLoop();
        sendLoop();
    }

    public void receiveLoop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    Object receivedPacket;
                    Game game;

                    while (true) {
                        game = SharedSources.game;
                        receivedPacket = inputStream.readObject();
//                        inputStream.reset();

                        if (receivedPacket instanceof InputPacket inputPacket) {
                            processKeyCode(inputPacket, game);
                            System.out.println("Input: " + inputPacket);
                        } else if (receivedPacket instanceof HumanPlayer HP) {
                            System.out.println("HP: " + HP);
                            game.addPlayer(HP);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Server exception: " + e.getMessage());
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void sendLoop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    Game game;
                    Board board;
                    ArrayList<Player> playersList;

                    while (true) {
                        game = SharedSources.game;
                        board = game.getBoard();
                        playersList = new ArrayList<>(game.getPlayersList())  ;

//                        synchronized (board = game.getBoard()) {
                            sendPacket(outputStream, board);
//                        }
//                        synchronized (playersList = new ArrayList<>(game.getPlayersList())){
                            sendPacket(outputStream, playersList);
//                        }
                    }
                } catch (IOException e) {
                    System.out.println("Server exception: " + e.getMessage());
                }
            }
        }).start();
    }

    private void processKeyCode(InputPacket inputPacket, Game game) {
        HumanPlayer playerToProcess = inputPacket.player();
        for (Player player : game.getPlayersList()) {
            if (player.getUsername().equals(playerToProcess.getUsername())) {
                ((HumanPlayer) player).processInput(inputPacket.keycode());
            }
        }
    }

    public void sendPacket(ObjectOutputStream outputStream, Object packet) {
        try {
            outputStream.writeObject(packet);
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
