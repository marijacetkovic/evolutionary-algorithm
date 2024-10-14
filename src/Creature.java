import java.util.Random;

public class Creature {
    private int id;
    //position within the world
    private int i;
    private int j;
    private Random r;
    public Creature(int id, int i, int j){
        this.id = id;
        this.i = i;
        this.j = j;
        this.r = new Random(4);
        System.out.println("Spawned a creature at positions " +i+","+j);
    }

    public void takeAction(EventManager eventManager, int[][] world) {
        int[][] directions = {
                {1, 0},
                {0, 1},
                {0, -1},
                {-1, 0}
        };

        for (int[] dir : directions) {
            int newRow = i + dir[0];
            int newCol = j + dir[1];

            if (isValidMove(world, newRow, newCol)) {
                world[newRow][newCol] = id;
                world[i][j] = -1;
                System.out.println("Creature "+id+" moved to position" + newRow + ", " + newCol);
                i = newRow;
                j = newCol;
                return;
            }
        }
    }

    private boolean isValidMove(int[][] world, int row, int col) {
        return row >= 0 && row < world.length && col >= 0 && col < world[0].length && world[row][col] == -1;
    }
}
