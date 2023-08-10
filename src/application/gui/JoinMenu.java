package application.gui;

import application.AppFrame;
import model.HumanPlayer;
import net.Client;

import javax.swing.*;
import java.awt.*;

public class JoinMenu extends JPanel {
    private JTextField txtUsername, txtIP, txtPort;
    private JButton joinButton;
    private JLabel lblUsername, lblPort, lblWarn, lblIP;

    public JoinMenu(AppFrame appFrame) {

        setBounds(0, 0, 625, 850);
        txtUsername = new JTextField("test");   // REMOVE
        txtIP = new JTextField("127.0.0.1");   // REMOVE
        // Default Port set
        txtPort = new JTextField("3462");
        joinButton = new JButton("Join Server");
        lblUsername = new JLabel("Username:");
        lblWarn = new JLabel();
        lblPort = new JLabel("Port:");
        lblIP = new JLabel("IP:");

        // Set Layout
        Font font = new Font(null, Font.PLAIN, 25);
        this.setLayout(null);

        txtUsername.setBounds(275, 245, 200, 40);
        txtUsername.setFont(font);
        txtUsername.setHorizontalAlignment(JTextField.CENTER);
        lblUsername.setBounds(150, 245, 125, 40);
        lblUsername.setHorizontalAlignment(JLabel.LEFT);
        lblUsername.setFont(font);

        txtIP.setBounds(100, 325, 200, 40);
        txtIP.setFont(font);
        txtIP.setHorizontalAlignment(JTextField.CENTER);
        lblIP.setBounds(50, 325, 75, 40);
        lblIP.setHorizontalAlignment(JLabel.LEFT);
        lblIP.setFont(font);

        txtPort.setBounds(475, 325, 100, 40);
        txtPort.setFont(font);
        txtPort.setHorizontalAlignment(JTextField.CENTER);
        lblPort.setBounds(415, 325, 75, 40);
        lblPort.setHorizontalAlignment(JLabel.LEFT);
        lblPort.setFont(font);

        joinButton.setBounds(200, 405, 225, 40);
        joinButton.setFont(new Font(null, Font.BOLD, 25));
        joinButton.setFocusable(false);

        lblWarn.setBounds(0, 475, 625, 50);
        lblWarn.setFont(new Font(null, Font.BOLD, 30));
        lblWarn.setHorizontalAlignment(JLabel.CENTER);
        lblWarn.setForeground(Color.RED);

        // Add components to panel
        add(txtUsername);
        add(lblUsername);
        add(txtIP);
        add(lblIP);
        add(txtPort);
        add(lblPort);
        add(lblWarn);
        add(joinButton);

        joinButton.addActionListener(e -> {
            try {
                String ip = txtIP.getText().trim();
                int port = Integer.parseInt(txtPort.getText().trim());
                Client client = new Client();
                int result = client.createClient(ip, port);

                switch (result) {
                    case 0 -> lblWarn.setText("Unknown Host!");
                    case 1 -> {
                        HumanPlayer mainPlayer = new HumanPlayer(txtUsername.getText().trim());
                        client.sendPacket(mainPlayer);
                        ClientPanel clientPanel = new ClientPanel(client, mainPlayer);
                        client.setClientPanel(clientPanel);
                        client.start();

                        appFrame.getContentPane().removeAll();
//                        appFrame.revalidate();
                        appFrame.add(clientPanel);
                        clientPanel.requestFocus();
                        appFrame.repaint();
                    }
                    case 2 -> lblWarn.setText("Enter proper port!");
                    case 4 -> lblWarn.setText("Connection refused!");
                    // add connection timeout //REMOVE
                }
            } catch (NumberFormatException ex) {
                lblWarn.setText("Enter proper port!");
            }
        });
    }
}
