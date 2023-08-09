package application;

import application.gui.CreateMenu;
import application.gui.JoinMenu;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppFrame::new);
    }

    public AppFrame() throws HeadlessException {

        super("Paint.io");
        setSize(1250, 850);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setLayout(null);

        setMenuPanel();
        setVisible(true);
    }

    private void setMenuPanel() {
        add(new JoinMenu(this));
        add(new CreateMenu());
    }
}