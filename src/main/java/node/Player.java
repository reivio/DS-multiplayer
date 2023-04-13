package node;

class Player {
    String name;
    int board, row, col;

    public Player() {
    }

    public Player(String name, int board, int row, int col) {
        this.name = name;
        this.board = board;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return String.format("%s,%d,%d,%d", name, board, row, col);
    }

    public static Player fromString(String str) {
        String[] parts = str.split(",");
        String name = parts[0];
        int board = Integer.parseInt(parts[1]);
        int row = Integer.parseInt(parts[2]);
        int col = Integer.parseInt(parts[3]);
        return new Player(name, board, row, col);
    }
}
