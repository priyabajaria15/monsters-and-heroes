import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class GameMap {
    private final Tile[][] tiles;
    private final int rows;
    private final int cols;
    private final Random random = new Random();

    // ANSI colors
    private static final String RESET = "\u001B[0m";
    private static final String RED   = "\u001B[31m";  // X
    private static final String GREEN = "\u001B[32m";  // M
    private static final String CYAN  = "\u001B[96m";  // H

    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        tiles = new Tile[rows][cols];
        generateMap();
    }

    private void generateMap() {
        // Keep generating until all accessible tiles form one connected component
        boolean ok = false;
        while (!ok) {

            // 1) Randomly assign tile types
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    double roll = random.nextDouble();
                    TileType type;

                    // 15% INACCESSIBLE, 10% MARKET, 75% COMMON
                    if (roll < 0.15) {
                        type = TileType.INACCESSIBLE;
                    } else if (roll < 0.25) {
                        type = TileType.MARKET;
                    } else {
                        type = TileType.COMMON;
                    }

                    tiles[r][c] = new Tile(type);
                }
            }

            // 2) Make sure starting position is accessible
            tiles[0][0] = new Tile(TileType.COMMON);

            // 3) Check if all accessible tiles are connected
            ok = accessibleConnected();
        }
    }

    public boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < rows && c < cols;
    }

    public Tile getTile(int r, int c) {
        return tiles[r][c];
    }

    /**
     * BFS over accessible tiles (COMMON or MARKET).
     * Returns true if all such tiles belong to one connected component.
     */
    private boolean accessibleConnected() {
        boolean[][] visited = new boolean[rows][cols];

        // 1. Find a starting accessible tile
        int startR = -1, startC = -1;
        for (int r = 0; r < rows && startR == -1; r++) {
            for (int c = 0; c < cols && startC == -1; c++) {
                if (tiles[r][c].getType() != TileType.INACCESSIBLE) {
                    startR = r;
                    startC = c;
                }
            }
        }

        // If somehow everything is inaccessible, reject this map
        if (startR == -1) {
            return false;
        }

        // 2. BFS queue setup
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{startR, startC});
        visited[startR][startC] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        // 3. BFS: visit all reachable accessible tiles
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0];
            int c = cur[1];

            for (int k = 0; k < 4; k++) {
                int nr = r + dr[k];
                int nc = c + dc[k];
                if (!inBounds(nr, nc)) continue;
                if (visited[nr][nc]) continue;
                if (tiles[nr][nc].getType() == TileType.INACCESSIBLE) continue;

                visited[nr][nc] = true;
                q.add(new int[]{nr, nc});
            }
        }

        // 4. Check that every accessible tile was visited
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tiles[r][c].getType() != TileType.INACCESSIBLE && !visited[r][c]) {
                    // Found an accessible tile that we could NOT reach by BFS
                    return false;
                }
            }
        }
        return true;
    }

    // ========= PRETTY BOX-STYLE PRINT =========

    public void print(int heroRow, int heroCol) {
        System.out.println("\n=== Map ===");

        // Build a border line like: +---+---+---+
        StringBuilder borderBuilder = new StringBuilder();
        for (int c = 0; c < cols; c++) {
            borderBuilder.append("+---");
        }
        borderBuilder.append("+");
        String border = borderBuilder.toString();

        // Top border
        System.out.println(border);

        for (int r = 0; r < rows; r++) {
            StringBuilder row = new StringBuilder();

            for (int c = 0; c < cols; c++) {
                row.append("|");

                String symbol; // use String so we can include color codes

                if (r == heroRow && c == heroCol) {
                    // Hero tile
                    symbol = CYAN + "H" + RESET;
                } else {
                    TileType type = tiles[r][c].getType();
                    switch (type) {
                        case INACCESSIBLE:
                            symbol = RED + "X" + RESET;
                            break;
                        case MARKET:
                            symbol = GREEN + "M" + RESET;
                            break;
                        case COMMON:
                        default:
                            symbol = " ";
                            break;
                    }
                }

                row.append(" ").append(symbol).append(" ");
            }

            row.append("|"); // right border
            System.out.println(row);
            System.out.println(border); // row separator
        }

        System.out.println("\nLegend:");
        System.out.println(
                CYAN + "H" + RESET + " = Hero, " +
                        GREEN + "M" + RESET + " = Market, " +
                        RED + "X" + RESET + " = Inaccessible, blank = Common tile"
        );

        System.out.println("\nControls:");
        System.out.println("W = Move Up, A = Move Left, S = Move Down, D = Move Right");
        System.out.println("I = Show Hero Info, M = Enter Market (when on a Market tile), Q = Quit Game");
    }
}
