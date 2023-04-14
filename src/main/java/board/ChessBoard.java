package board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import board.PlayerIcon;
import node.Player;

public class ChessBoard {
  // private JPanel chessboardPanel;
  // private JLabel[][][][] pawnLabels;
  private JLabel pawn2;
  private JLabel pawn1;
  private JLabel pawn3;
  private JLabel pawn4;
  private JLabel[][] pawns;
  private static final String EXCHANGE_NAME = "direct-msg";

  private static List<Player> players = new ArrayList<>();
  private static int msgCounter = 0;

  static JFrame frame = new JFrame("Chess Board");

  static JPanel panel = new JPanel(new GridLayout(2, 2));

  public static void main(String[] args) throws Exception {
    System.out.println("Creating board...");

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    panel = createBoard();
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);

    String containerName = "listener";
    List<String> allNodes = Arrays.asList("NODE-1", "NODE-2", "NODE-3", "NODE-4");

    // Create a new connection to RabbitMQ
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    // Declare the exchange with the container name
    channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

    // Bind to all exchanges with the pattern "*_exchange.#"
    channel.queueDeclare(containerName, false, false, false, null);

    for (String node : allNodes) {
      channel.queueBind(containerName, EXCHANGE_NAME, node);
    }

    // declare a mutable variable to keep track of the number of messages
    // Start consuming messages from the queue
    String consumerTag = channel.basicConsume(containerName, true, (tag, delivery) -> {
      String msg = new String(delivery.getBody(), "UTF-8");
      String routingKey = delivery.getEnvelope().getRoutingKey();

      System.out.println("Received message '" + msg + "' from exchange " +
          EXCHANGE_NAME + " with routing key "
          + routingKey);
      players.add(Player.fromString(msg));
      if (players.size() == 4) {
        System.out.println("All players have joined the game!");
        System.out.println("Starting game...");
        frame.getContentPane().remove(panel);
        panel = createBoard();
        System.out.println("Players: " + players.size());
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
        // Start game
        players.clear();
      }
    }, tag -> {
    });
    System.out.println("Subscribed to all messages on all topics with consumer tag " + consumerTag);

  }

  private static JPanel createBoard() {
    // createBoard();

    JPanel chessboardPanel = new JPanel(new GridLayout(2, 2));
    // chessboardPanel = new JPanel(new GridBagLayout());
    // pawnLabels = new JLabel[2][2][5][5];

    int nRows = 5;
    int nCols = 5;

    ImageIcon icon1 = PlayerIcon.getScaledImage("img/smiley1.png", 50);
    ImageIcon icon2 = PlayerIcon.getScaledImage("img/smiley2.png", 50);
    ImageIcon icon3 = PlayerIcon.getScaledImage("img/smiley3.png", 50);
    ImageIcon icon4 = PlayerIcon.getScaledImage("img/smiley4.png", 50);

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {

        JPanel boardPanel = new JPanel(new GridLayout(nRows, nCols));
        boardPanel.setPreferredSize(new Dimension(400, 400)); // Set preferred size of chess board
        JLabel[][] pawnLabelsBoard = new JLabel[5][5];

        for (int x = 0; x < nRows; x++) {
          for (int y = 0; y < nCols; y++) {
            JLabel squareLabel = new JLabel();
            if ((x + y) % 2 == 0) {
              squareLabel.setBackground(Color.WHITE);
            } else {
              squareLabel.setBackground(Color.BLACK);
            }
            squareLabel.setOpaque(true);
            squareLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            // boardPanel.add(squareLabel);
            boardPanel.add(squareLabel, new GridBagConstraints(y, x, 1, 1, 0, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            JLabel pawnLabel = new JLabel(icon1);
            // boardPanel.add(pawnLabel);
            // boardPanel.add(pawnLabel, new GridBagConstraints(y, x, 1, 1, 0, 0,
            // GridBagConstraints.CENTER,
            // GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            // pawnLabelsBoard[x][y] = pawnLabel;
            // if (x == 0 && y == 0) {
            // squareLabel.setIcon(icon1);
            // }

            int idx = 2 * i + j;
            for (Player player : players) {
              if (player.getBoard() == idx && player.getRow() == x && player.getCol() == y) {
                System.out.println("Adding pawn to board " + idx + " at " + x + " " + y + " " + player.getName());
                if (player.getName().equals("player.1")) {
                  System.out.println("Adding pawn to board " + idx + " at " + x + "--------------- " + y);
                  squareLabel.setIcon(icon1);
                } else if (player.getName().equals("player.2")) {
                  squareLabel.setIcon(icon2);
                } else if (player.getName().equals("player.3")) {
                  squareLabel.setIcon(icon3);
                } else if (player.getName().equals("player.4")) {
                  squareLabel.setIcon(icon4);
                }
              }
            }
          }
        }
        // System.out.println("Adding board to panel..." + i + " " + j);
        chessboardPanel.add(boardPanel);
      }
    }
    System.out.println("Returning panel...!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    return chessboardPanel;

    // frame.getContentPane().add(chessboardPanel);
    // frame.pack();
    // frame.setVisible(true);

  }
}
