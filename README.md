# Distributed Systems
## Multiplayer game

### Rules of the game
- Each Zone contols the players therein.
- In one Zone a player can only move up, down, left or right.
- Zones don't let players crash into players of other Zones.
- Players can be teleported to an unoccupied square in a neighboring Zone.

### Logic 
- Each Node desides independently how its players move.
- The Nodes `publish` the locations of their players into their own queue.
- The Nodes `subscribe` to the other Nodes' messages.
- The Nodes wait until they've heard from all the other Nodes and only then move their players.

To run the program, open a terminal in the root of the project and run:
```
docker-compose up rabbitmq -d

docker-compose up --build node-1 node-2 node-3 node-4
```
In another terminal:
```
javac -cp ".:amqp-client-5.12.0.jar:slf4j-api-1.7.32.jar:slf4j-simple-1.7.32.jar:rabbitmq-http-client-3.8.2.jar:src/main/java" -sourcepath .  src/main/java/node/Player.java src/main/java/board/ChessBoard.java

java -cp ".:amqp-client-5.12.0.jar:slf4j-api-1.7.32.jar:slf4j-simple-1.7.32.jar:rabbitmq-http-client-3.8.2.jar:src/main/java" src/main/java/board/ChessBoard.java
```
