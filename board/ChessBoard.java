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
// import com.rabbitmq.client.Channel;
// import com.rabbitmq.client.Connection;
// import com.rabbitmq.client.ConnectionFactory;
// import com.rabbitmq.client.DeliverCallback;

import board.PlayerIcon;

public class ChessBoard {
  private JPanel chessboardPanel;
  private JLabel[][][][] pawnLabels;
  private JLabel pawn2;
  private JLabel pawn1;
  private JLabel pawn3;
  private JLabel pawn4;
  private JLabel[][] pawns;

  public ChessBoard() {

    // createBoard();
    System.out.println("Creating board...");

    JFrame frame = new JFrame("Chess Board");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // chessboardPanel = new JPanel(new GridLayout(2, 2));
    chessboardPanel = new JPanel(new GridBagLayout());
    pawnLabels = new JLabel[2][2][5][5];

    int nRows = 5;
    int nCols = 5;

    ImageIcon icon1 = PlayerIcon.getScaledImage("img/smiley1.png", 50);
    ImageIcon icon2 = PlayerIcon.getScaledImage("img/smiley2.png", 50);
    ImageIcon icon3 = PlayerIcon.getScaledImage("img/smiley3.png", 50);
    ImageIcon icon4 = PlayerIcon.getScaledImage("img/smiley4.png", 50);

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {

        JPanel boardPanel = new JPanel(new GridLayout(nRows, nCols));
        boardPanel.setPreferredSize(new Dimension(300, 300)); // Set preferred size of chess board
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
            pawnLabelsBoard[x][y] = pawnLabel;

            // if (i == 0 && row == 3 && col == 0) { // Add pawn to top-left board
            // square.setIcon(icon1);
            // } else if (i == 1 && row == 3 && col == 4) { // Add pawn to top-right board
            // // square.setIcon(new ImageIcon("pawn.png"));
            // square.setIcon(icon2);
            // } else if (i == 2 && row == 1 && col == 0) { // Add pawn to bottom-left board
            // square.setIcon(icon3);
            // } else if (i == 3 && row == 1 && col == 4) { // Add pawn to bottom-right
            // board
            // square.setIcon(icon4);
            // }
          }
        }
        pawnLabels[i][j] = pawnLabelsBoard;
        System.out.println("Adding board to panel..." + i + " " + j);
        chessboardPanel.add(boardPanel);
        // boardPanel.add(pawn1);
        // boardPanel.add(pawn1,
        // new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
        // GridBagConstraints.NONE,
        // new Insets(0, 0, 0, 0), 0, 0));
      }
    }

    pawns = new JLabel[4][2];
    pawns[0][0] = new JLabel(icon2);
    pawns[0][1] = pawnLabels[0][0][2][2];

    // placePawn(pawns[0][0], pawns[0][1], 2, 2);

    frame.getContentPane().add(chessboardPanel);
    frame.pack();
    frame.setVisible(true);

    // ConnectionFactory factory = new ConnectionFactory();
    // factory.setHost("localhost");
    // Connection connection = factory.newConnection();
    // Channel channel = connection.createChannel();

    // channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    // System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    // DeliverCallback deliverCallback = (consumerTag, delivery) -> {
    // String message = new String(delivery.getBody(), "UTF-8");
    // System.out.println(" [x] Received '" + message + "'");
    // updateBoard();
    // };
    // channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {

    // });
  }

  public static void main(String[] args) throws Exception {
    new ChessBoard();

    while (true) {
      System.out.println("Updating board...");
      updateBoard();
      Thread.sleep(1000);
    }
  }

  public void placePawn(JLabel pawnLabel, JLabel oldSquareLabel, int newX, int newY) {
    // Get the old row and column indices of the pawn
    // GridBagConstraints gbc =
    // chessboardPanel.getLayout().getConstraints(oldSquareLabel);
    GridBagConstraints gbc = ((GridBagLayout) chessboardPanel.getLayout()).getConstraints(oldSquareLabel);

    int oldX = gbc.gridy;
    int oldY = gbc.gridx;

    // Remove the pawn label from its old position in the chessboard panel
    chessboardPanel.remove(pawnLabel);

    // Add the pawn label to its new position in the chessboard panel
    chessboardPanel.add(pawnLabel, new GridBagConstraints(newY, newX, 1, 1, 0, 0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    // Set the old square label to its new background color
    if ((oldX + oldY) % 2 == 0) {
      oldSquareLabel.setBackground(Color.WHITE);
    } else {
      oldSquareLabel.setBackground(Color.BLACK);
    }

    // Set the new square label to be empty
    JLabel newSquareLabel = pawnLabels[newX / 3][newY / 3][newX % 3][newY % 3];
    newSquareLabel.setBackground((newX + newY) % 2 == 0 ? Color.WHITE : Color.BLACK);

    // Repaint the old and new square labels to reflect the changes
    oldSquareLabel.repaint();
    newSquareLabel.repaint();

    // Repaint the chessboard panel to reflect the changes
    chessboardPanel.revalidate();
    chessboardPanel.repaint();
  }

  public static void updateBoard() {

  }

  private static void createBoard() {

  }
}
