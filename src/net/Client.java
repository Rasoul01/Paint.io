package net;

import application.gui.ClientPanel;
import model.Board;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client extends Thread {

    private Socket socket;
    private ClientPanel clientPanel;
    private ObjectOutputStream outputStream;

    public int createClient (String ip, int port) {

        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(5000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            return 1;
        } catch (UnknownHostException eu) {
            return 0;
        } catch (IllegalArgumentException ei) {
            return 2;
        } catch(SocketTimeoutException e) {
            System.out.println("The socket timed out");
            e.printStackTrace();
            return 3;
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
            return 4;
        }
    }

    @Override
    public void run() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            while (!socket.isClosed()) {
                processPacket(inputStream.readObject());
//                inputStream.reset();
            }
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPacket(Object packet) {
        try {
            outputStream.writeObject(packet);
            outputStream.flush();
            outputStream.reset();
            System.out.println("client sending: " + packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processPacket(Object packet) {
//        System.out.println("Client received: " + packet); //REMOVE

        if (packet instanceof Board board) {
            clientPanel.setBoard(board);
        } else if (packet instanceof ArrayList playersList) {
            clientPanel.setPlayersList(playersList);
        }
    }

    public void setClientPanel(ClientPanel clientPanel) {
        this.clientPanel = clientPanel;
    }
}
