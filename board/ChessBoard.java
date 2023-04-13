package board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
// import com.rabbitmq.client.Channel;
// import com.rabbitmq.client.Connection;
// import com.rabbitmq.client.ConnectionFactory;
// import com.rabbitmq.client.DeliverCallback;

import board.PlayerIcon;

public class ChessBoard {
  public static void main(String[] args) throws Exception {

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
    while (true) {
      System.out.println("Updating board...");
      updateBoard();
      Thread.sleep(1000);
    }
  }

  public static void updateBoard() {
    JFrame frame = new JFrame("Chess Board");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel panel = new JPanel(new GridLayout(2, 2));

    int nRows = 5;
    int nCols = 5;

    ImageIcon icon1 = PlayerIcon.getScaledImage("img/smiley1.png", 50);
    ImageIcon icon2 = PlayerIcon.getScaledImage("img/smiley2.png", 50);
    ImageIcon icon3 = PlayerIcon.getScaledImage("img/smiley3.png", 50);
    ImageIcon icon4 = PlayerIcon.getScaledImage("img/smiley4.png", 50);
    for (int i = 0; i < 4; i++) {
      JPanel chessBoard = new JPanel(new GridLayout(nRows, nCols));
      chessBoard.setPreferredSize(new Dimension(400, 400)); // Set preferred size of chess board
      for (int row = 0; row < nRows; row++) {
        for (int col = 0; col < nCols; col++) {
          JLabel square = new JLabel();
          if ((row + col) % 2 == 0) {
            square.setBackground(Color.WHITE);
          } else {
            square.setBackground(Color.BLACK);
          }
          square.setOpaque(true);
          square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
          chessBoard.add(square);

          if (i == 0 && row == 3 && col == 0) { // Add pawn to top-left board
            square.setIcon(icon1);
          } else if (i == 1 && row == 3 && col == 4) { // Add pawn to top-right board
            // square.setIcon(new ImageIcon("pawn.png"));
            square.setIcon(icon2);
          } else if (i == 2 && row == 1 && col == 0) { // Add pawn to bottom-left board
            square.setIcon(icon3);
          } else if (i == 3 && row == 1 && col == 4) { // Add pawn to bottom-right board
            square.setIcon(icon4);
          }
        }
      }
      panel.add(chessBoard);
    }

    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
  }
}
