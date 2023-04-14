package node;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import node.Player;

public class Node {
    private static final int BOARD_SIZE = 5;
    private static final int NUM_BOARDS = 4;
    private static final String EXCHANGE_NAME = "direct-msg";
    private static final String STATUS_ROUTING_KEY = ".status";
    // Add a list to keep track of neighbors
    private static List<Player> ownPlayers = new ArrayList<>();
    private static List<Player> otherPlayers = new ArrayList<>();
    private static int msgIteration = 0;
    private static int nodeNumber = -1;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        String containerName = System.getenv("CONTAINER_NAME");
        System.out.println("Container name: " + containerName);

        nodeNumber = Integer.parseInt(containerName.substring(containerName.length() - 1));
        System.out.println("Node number: " + nodeNumber);

        List<String> allNodes = Arrays.asList("NODE-1", "NODE-2", "NODE-3", "NODE-4");
        List<String> neighborNodes = new ArrayList<>();
        for (String node : allNodes) {
            if (node.equals(containerName)) {
                continue;
            }
            neighborNodes.add(node);
        }

        // Create a new connection to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
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
            if (!routingKey.equals(containerName)) {
                addOtherPlayer(msg);
            }
            // System.out.println("Received message '" + msg + "' from exchange " +
            // EXCHANGE_NAME + " with routing key "
            // + routingKey);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Publish a message to the instance's own queue
            // String ownQueue = QUEUE_NAME_PREFIX + System.getenv("HOSTNAME");
            String ownMessage = "Hello from " + containerName; // System.getenv("HOSTNAME");
            msgIteration++;
            if (msgIteration == 3) {
                updatePlayerLists(ownPlayers, otherPlayers);
                ownPlayers = getRandomLegalMoves(ownPlayers, otherPlayers);
                for (Player player : ownPlayers) {
                    System.out.println("Publishing player: " + player.toString());
                    channel.basicPublish(EXCHANGE_NAME, containerName, null, player.toString().getBytes("UTF-8"));
                }

                // !!!!!!!!!!!!!!!!!!!!!!!!Moved update!!!!!!!!!!!!!!!!!

                // channel.basicPublish(EXCHANGE_NAME, containerName, null,
                // ownMessage.getBytes("UTF-8"));
                msgIteration = 0;
                otherPlayers.clear();
                // players.remove("123");
            }
        }, tag -> {
        });
        System.out.println("Subscribed to all messages on all topics with consumer tag " + consumerTag);

        // initial player position
        Player player = new Player("player." + Integer.toString(nodeNumber), nodeNumber - 1, 2, 2);
        System.out.println("Initial player position: " + player.toString());
        ownPlayers.add(player);
        channel.basicPublish(EXCHANGE_NAME, containerName, null, player.toString().getBytes("UTF-8"));
    }

    // private static boolean isValidMove(Player pos, List<Player> positions) {
    // for (Player otherPos : positions) {
    // if (otherPos.board == pos.board && otherPos.row == pos.row && otherPos.col ==
    // pos.col) {
    // return false;
    // }
    // }
    // return pos.row >= 0 && pos.row < BOARD_SIZE && pos.col >= 0 && pos.col <
    // BOARD_SIZE;
    // }
    private static boolean isValidMove(Player pos, List<Player> positions, List<Player> badPieces) {
        for (Player otherPos : positions) {
            if (otherPos.board == pos.board && otherPos.row == pos.row && otherPos.col == pos.col) {
                return false;
            }
        }

        int colPos = (pos.board % 2) * BOARD_SIZE + pos.col;
        int rowPos = (pos.board / 2) * BOARD_SIZE + pos.row;
        for (Player badPos : badPieces) {
            int colBad = (badPos.board % 2) * BOARD_SIZE + badPos.col;
            int rowBad = (badPos.board / 2) * BOARD_SIZE + badPos.row;
            if ((Math.abs(rowBad - rowPos) <= 1) && (Math.abs(colBad - colPos) <= 1)) {
                return false;
            }
        }

        return pos.row >= 0 && pos.row < BOARD_SIZE && pos.col >= 0 && pos.col < BOARD_SIZE;
    }

    public static List<Player> getRandomLegalMoves(List<Player> positions, List<Player> badPieces) {
        List<Player> legalMoves = new ArrayList<>();
        Random random = new Random();

        for (Player pos : positions) {
            List<Player> candidates = new ArrayList<>();

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr * dc != 0 || (dr == 0 && dc == 0))
                        continue;

                    Player newPos = new Player(pos.name, pos.board, pos.row + dr, pos.col + dc);

                    // Crossing over to a neighboring board
                    if (newPos.row < 0) {
                        if (newPos.board == 0 || newPos.board == 1) {
                            continue;
                        }
                        newPos.row = BOARD_SIZE - 1;
                        newPos.board = (newPos.board + 2) % NUM_BOARDS;
                    } else if (newPos.row >= BOARD_SIZE) {
                        if (newPos.board == 2 || newPos.board == 3) {
                            continue;
                        }
                        newPos.row = 0;
                        newPos.board = (newPos.board + 2) % NUM_BOARDS;
                    } else if (newPos.col < 0) {
                        if (newPos.board == 0 || newPos.board == 2) {
                            continue;
                        }
                        newPos.col = BOARD_SIZE - 1;
                        newPos.board = (newPos.board + 1) % NUM_BOARDS;
                    } else if (newPos.col >= BOARD_SIZE) {
                        if (newPos.board == 1 || newPos.board == 3) {
                            continue;
                        }
                        newPos.col = 0;
                        newPos.board = (newPos.board + 1) % NUM_BOARDS;
                    }

                    if (isValidMove(newPos, positions, badPieces)) {
                        // TODO : check availability against newly found legal moves
                        Boolean spaceIsFree = true;
                        for (Player legal : legalMoves) {
                            if (legal.board == pos.board && legal.row == pos.row && legal.col == pos.col) {
                                spaceIsFree = false;
                                break;
                            }
                        }
                        if (spaceIsFree) {
                            candidates.add(newPos);
                        }
                    }
                }
            }

            if (!candidates.isEmpty()) {
                legalMoves.add(candidates.get(random.nextInt(candidates.size())));
            } else {
                legalMoves.add(pos); // If there are no legal moves, stay in the same position
            }
        }
        // add assert that legalMoves.size() == positions.size()
        assert legalMoves.size() == positions.size();
        return legalMoves;
    }

    // public static List<Player> getNextMoves(List<Player> positions, List<Player>
    // badPieces) {

    // List<Player> nextlMoves = new ArrayList<>();
    // int limit = 2 * BOARD_SIZE;

    // }

    public static void updatePlayerLists(List<Player> own, List<Player> other) {
        List<Player> newOwnPlayers = new ArrayList<>();
        for (Player pos : own) {
            if (pos.board != nodeNumber - 1) {
                otherPlayers.add(pos);
                // ownPlayers.remove(pos);
                System.out.println(pos.name + " moved away to board " + pos.board);
            } else {
                newOwnPlayers.add(pos);
            }
        }
        ownPlayers = newOwnPlayers;
        List<Player> newOtherPlayers = new ArrayList<>();
        for (Player pos : other) {
            if (pos.board == nodeNumber - 1) {
                ownPlayers.add(pos);
                // otherPlayers.remove(pos);
                System.out.println(pos.name + " moved to into this board  (" + pos.board + ")");
            } else {
                newOtherPlayers.add(pos);
            }
        }
        otherPlayers = newOtherPlayers;
    }

    public static void addOtherPlayer(String msg) {
        Player player = Player.fromString(msg);
        otherPlayers.add(player);
    }
}
// while (true) {

// Thread.sleep(10000);
// channel.basicPublish(EXCHANGE_NAME, containerName, null,
// "Hello".getBytes("UTF-8"));
// }

// while (true) {
// for (String routingKey : routingKeys) {
// publishStatus(containerName, EXCHANGE_NAME, routingKey, channel);
// }
// Thread.sleep(5000);
// }

// // Start consuming messages from the queue
// String consumerTag = channel.basicConsume(queueName, true, (tag, delivery) ->
// {
// String msg = new String(delivery.getBody(), "UTF-8");
// System.out.println("Received message '" + msg + "' from exchange " +
// EXCHANGE_NAME + " with routing key "
// + delivery.getEnvelope().getRoutingKey());
// }, tag -> {
// });
// System.out.println("Subscribed to all messages on all topics with consumer
// tag " + consumerTag);
// }

// private static void publishStatus(String containerName, String exchangeName,
// String routingKey, Channel channel)
// throws IOException {
// String message = "I'm " + containerName;
// channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
// System.out.println("Sent message: " + message + " to routing key: " +
// routingKey);
// }
