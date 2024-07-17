package MazeSolver;

import java.awt.*;
import javax.swing.*;
import java.util.*;

public class DFS_alg {
    public static void DFS(Cell start, Cell goal, boolean[][] verticalWalls, boolean[][] horizontalWalls) {
        new Thread(() -> {
            long start_time = System.currentTimeMillis();
            Map<Cell, Cell> previous = new HashMap<>();
            ArrayList<Cell> stack = new ArrayList<>();

            ArrayList<Cell> visited = new ArrayList<>();

            stack.add(start);
            visited.add(start);

            while (!stack.isEmpty()) {
                Cell current = stack.get(stack.size() - 1);
                JPanel currentpanel = current.getPanel();
                if (!(current == start) && !(current == goal)) {
                    ColorPanel.colorpanel(currentpanel, Color.YELLOW, 0, 500);
                }

                if (current.equals(goal)) {

                    ColorPanel.colorpanel(currentpanel, Color.RED, 5, 0);
                    MazeGenerator.path(previous, current);
                    break;
                }

                ArrayList<Cell> nextlist = new ArrayList<>();
                for (Cell neighbor : current.neighbors) {
                    if (visited.contains(neighbor)) {
                        continue;
                    }

                    if (neighbor.rowNumber == current.rowNumber) {
                        if (neighbor.columnNumber > current.columnNumber
                                && verticalWalls[current.rowNumber][neighbor.columnNumber]) {
                            continue;
                        } else if (neighbor.columnNumber < current.columnNumber
                                && verticalWalls[current.rowNumber][current.columnNumber]) {
                            continue;
                        }
                    } else if (neighbor.rowNumber > current.rowNumber
                            && horizontalWalls[neighbor.rowNumber][current.columnNumber]) {
                        continue;
                    } else if (neighbor.rowNumber < current.rowNumber
                            && horizontalWalls[current.rowNumber][current.columnNumber]) {
                        continue;
                    }
                    nextlist.add(neighbor);
                }

                if (nextlist.isEmpty()) {
                    stack.remove(current);
                } else {
                    Random random = new Random();
                    int randomIndex = random.nextInt(nextlist.size());
                    Cell next = nextlist.get(randomIndex);
                    previous.put(next, current);
                    stack.add(next);
                    visited.add(next);
                    JPanel panel = next.getPanel();
                    ColorPanel.colorpanel(panel, Color.CYAN, 0, 500);
                }
            }
            long end_time = System.currentTimeMillis();
            MazeGenerator.textArea2.setText(Long.toString(end_time - start_time));
        }).start();       
    }
}
