package application;

import model.HumanPlayer;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}

class GameFrame extends JFrame {

    MenuPanel menuPanel;
    public GameFrame() throws HeadlessException {

        menuPanel = new MenuPanel(this);
        setSize(1250, 850);
        setTitle("Paint.io");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(menuPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class MenuPanel extends JPanel {

    GameController gameController;
    private JTextField txtUsername;
    private JButton startButton;
    private JLabel lblWarn;
    private JSpinner botLevelSpinner;
    private JSpinner botCountSpinner;
    private JSpinner gameSpeedSpinner;

    public MenuPanel(GameFrame gameFrame) {

        txtUsername = new JTextField();
        startButton = new JButton("Start");
        lblWarn = new JLabel();
        botCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        botLevelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
        gameSpeedSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        JLabel lblBotCounter = new JLabel("Bot Count:", JLabel.RIGHT);
        JLabel lblBotLevel = new JLabel("Bot Level:", JLabel.RIGHT);
        JLabel lblGameSpeed = new JLabel("Game Speed:", JLabel.RIGHT);

        txtUsername.setText("test");    //REMOVE

        // Set Layout
        this.setLayout(null);
        this.setBackground(new Color(255,250,195));
        txtUsername.setBounds(525,340,200,40);
        txtUsername.setFont(new Font(null, Font.PLAIN, 25));
        txtUsername.setHorizontalAlignment(JTextField.CENTER);
        startButton.setBounds(575, 400, 100, 40);
        startButton.setFont(new Font(null, Font.BOLD, 25));
        startButton.setFocusable(false);
        lblWarn.setBounds(0,450,1250,50);
        lblWarn.setFont(new Font(null, Font.BOLD, 30));
        lblWarn.setHorizontalAlignment(JLabel.CENTER);
        lblWarn.setForeground(Color.RED);
        botCountSpinner.setBounds(160,600,30,30);
        botLevelSpinner.setBounds(160,650,30,30);
        gameSpeedSpinner.setBounds(160,700,30,30);
        lblBotCounter.setBounds(50,600,100,20);
        lblBotCounter.setFont(new Font(null, Font.PLAIN, 15));
        lblBotLevel.setBounds(50,650,100,20);
        lblBotLevel.setFont(new Font(null, Font.PLAIN, 15));
        lblGameSpeed.setBounds(50,700,100,20);
        lblGameSpeed.setFont(new Font(null, Font.PLAIN, 15));


        // Add components to panel
        add(txtUsername);
        add(startButton);
        add(lblWarn);
        add(botCountSpinner);
        add(botLevelSpinner);
        add(gameSpeedSpinner);
        add(lblBotCounter);
        add(lblBotLevel);
        add(lblGameSpeed);

        startButton.addActionListener(e -> {

            String username = txtUsername.getText().trim();
            int botCount = (int) botCountSpinner.getValue();
            int botLevel = (int) botLevelSpinner.getValue();
            int gameSpeed = (int) gameSpeedSpinner.getValue();

            gameController = new GameController(gameSpeed);

            if (username.trim().isEmpty()) {
                lblWarn.setText("Enter username!");
            } else {
                boolean result = gameController.addPlayer(new HumanPlayer(username));

                if (!result){
                    lblWarn.setText("Username already exists!");
                } else {
                    gameFrame.remove(this);
                    gameFrame.add(gameController);
                    gameController.startGame(botCount, botLevel);
                    gameFrame.setVisible(true);
                }
            }
        });
    }
}