package application.gui;

import model.Board;
import model.HumanPlayer;
import model.Painter;
import model.Player;
import net.Client;
import net.packet.InputPacket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ClientPanel extends JPanel {

    private Board board;
    private HumanPlayer mainPlayer;
    private ArrayList<Player> playersList;

    // make sure colsCount & rowsCount match each other
    // (colsCount = screenWidth / desired unit size & rowsCount = screenHeight / desired unit size)
    // so unitSize = screenHeight / rowsCount = screenWidth / colsCount which will make everything in game square
    private final int screenHeight = 850;
    private final int screenWidth = 1250;
    final int colsCount = 25;
    final int rowsCount = 17;
    private final int unitSize = screenHeight / rowsCount;

    private final Painter painter;

    public ClientPanel(Client client, HumanPlayer mainPlayer) {

        setBounds(0, 0, 1250, 850);
        this.mainPlayer = mainPlayer;

        painter = new Painter();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                client.sendPacket(new InputPacket(mainPlayer, e.getKeyCode()));
            }
        });

        final int INITIAL_DELAY = 0;
        final int PERIOD_INTERVAL = 1000/60;
        java.util.Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, INITIAL_DELAY, PERIOD_INTERVAL);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        painter.drawAll(g, board, playersList, mainPlayer, colsCount, rowsCount, unitSize);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayersList(ArrayList<Player> playersList) {
        this.playersList = playersList;
        updateMainPlayer();
    }

    private void updateMainPlayer() {
        for (Player player : playersList) {
            if (mainPlayer.getUsername().equals(player.getUsername())) {
                mainPlayer = (HumanPlayer) player;
            }
        }
    }
}
