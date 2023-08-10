package net;

import application.SharedSources;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerHandler extends Thread {
    private ServerSocket serverSocket;

    public int createServer(int port, int gameSpeed, int botCount, int botLevel) {
        try {
            SharedSources.createGame(gameSpeed, botCount, botLevel);
            serverSocket = new ServerSocket(port);
            return 1;
        } catch(IOException e) {
            System.out.println("Server accept exception " + e.getMessage());
            return 0;
        } catch (IllegalArgumentException ei) {
            return 2;
        }
    }

    @Override
    public void run() {
        while(!serverSocket.isClosed()) {
            try {
                new Thread(new Server(serverSocket.accept())).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
