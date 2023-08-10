package application.gui;

import net.ServerHandler;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CreateMenu extends JPanel {

    private JTextField txtPort;
    private JButton createButton;
    private JLabel lblPort, lblGameSpeed, lblBotCounter, lblBotLevel, lblWarn, lblTeam, lblIPs;
    private JSpinner gameSpeedSpinner, botLevelSpinner, botCountSpinner;
    private JCheckBox teamCheckBox;

    public CreateMenu() {

        setBounds(625, 0, 625, 850);
        // Default Port set
        txtPort = new JTextField("3462");
        createButton = new JButton("Create Server");
        lblWarn = new JLabel();
        lblPort = new JLabel("Port:", JLabel.LEFT);
        lblTeam = new JLabel("Team Game", JLabel.LEFT);
        lblIPs = new JLabel("<html>Your IPs:<br>", JLabel.LEFT);
        teamCheckBox = new JCheckBox();
        botCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        botLevelSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 3, 1));
        gameSpeedSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        lblBotCounter = new JLabel("Bot Count:", JLabel.RIGHT);
        lblBotLevel = new JLabel("Bot Level:", JLabel.RIGHT);
        lblGameSpeed = new JLabel("Game Speed:", JLabel.RIGHT);

        // Set Layout
        Font font = new Font(null, Font.PLAIN, 25);
        this.setLayout(null);
        this.setBackground(new Color(255, 250, 195));

        txtPort.setBounds(125, 245, 100, 40);
        txtPort.setFont(font);
        txtPort.setHorizontalAlignment(JTextField.CENTER);
        lblPort.setBounds(50, 245, 75, 40);
        lblPort.setFont(font);

        gameSpeedSpinner.setBounds(535, 245, 40, 40);
        gameSpeedSpinner.setFont(font);
        lblGameSpeed.setBounds(375, 245, 150, 40);
        lblGameSpeed.setFont(font);

        botCountSpinner.setBounds(535,600,40,40);
        botCountSpinner.setFont(font);
        lblBotCounter.setBounds(375,600,150,40);
        lblBotCounter.setFont(font);

        botLevelSpinner.setBounds(535,650,40,40);
        botLevelSpinner.setFont(font);
        lblBotLevel.setBounds(375,650,150,40);
        lblBotLevel.setFont(font);

        createButton.setBounds(200, 405, 225, 40);
        createButton.setFont(new Font(null, Font.BOLD, 25));
        createButton.setFocusable(false);

        teamCheckBox.setBounds(215, 325, 40, 40);
        lblTeam.setBounds(260, 325, 150, 40);
        lblTeam.setFont(font);

        lblWarn.setBounds(0, 475, 625, 50);
        lblWarn.setFont(new Font(null, Font.BOLD, 30));
        lblWarn.setHorizontalAlignment(JLabel.CENTER);
        lblWarn.setForeground(Color.RED);

        lblIPs.setBounds(50, 600, 150, 200);
        lblIPs.setFont(font);
        lblIPs.setVerticalAlignment(JLabel.TOP);

        // Add components to panel
        add(txtPort);
        add(lblPort);
        add(gameSpeedSpinner);
        add(lblGameSpeed);
        add(botCountSpinner);
        add(lblBotCounter);
        add(botLevelSpinner);
        add(lblBotLevel);
        add(teamCheckBox);
        add(lblTeam);
        add(lblWarn);
        add(createButton);

        new Thread(new Runnable() {
            @Override
            public void run() {
                java.util.List<String> IPs = getIPs();

                for (String i : IPs) {
                    lblIPs.setText(lblIPs.getText() + i + "<br>");
                }
                lblIPs.setText(lblIPs.getText() + "</html>");
                add(lblIPs);
                repaint();
            }
        }).start();

        createButton.addActionListener(e -> {
            int gameSpeed = (int) gameSpeedSpinner.getValue();
            int botCount = (int) botCountSpinner.getValue();
            int botLevel = (int) botLevelSpinner.getValue();

            try {
                int port = Integer.parseInt(txtPort.getText().trim());
                ServerHandler serverHandler = new ServerHandler();
                int result = serverHandler.createServer(port, gameSpeed, botCount, botLevel);

                switch (result) {
                    case 0 -> lblWarn.setText("starting server failed!");
                    case 1 -> {
                        serverHandler.start();
                        lblWarn.setText("Server started on port " + port);
                    }
                    case 2 -> lblWarn.setText("Enter proper port!");
                }
            } catch (NumberFormatException ex) {
                lblWarn.setText("Enter proper port!");
            }
        });
    }

    private java.util.List<String> getIPs() {
        List<String> IPs = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out inactive interfaces
                if (!iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String ip = addr.getHostAddress();
                    if (ip.contains(":")) {
                        // skip IPv6 addresses
                        continue;
                    }
                    IPs.add(ip);
                }
            }

            URL url = new URL("https://api.ipify.org");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String ip = in.readLine();
            IPs.add(ip);
        } catch (Exception ignored) {
        }

        return IPs;
    }
}
