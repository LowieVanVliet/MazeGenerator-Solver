package MazeSolver;

import java.awt.*;
import javax.swing.*;

import java.util.*;

public class A_Star {
    
    public static void A_Star_chaos(int disttype, Integer h_type, Cell start, Cell goal, boolean[][] verticalWalls,
            boolean[][] horizontalWalls) {
        new Thread(() -> {
            long start_time = System.currentTimeMillis();
            Map<Cell, Double> fcost = new HashMap<>();
            Map<Cell, Integer> gcost = new HashMap<>();
            Map<Cell, Cell> previous = new HashMap<>();
            PriorityQueue<Cell> queue = new PriorityQueue<>(Comparator.comparingDouble(fcost::get));

            Set<Cell> closedSet = new HashSet<>();
            gcost.put(start, 0);
            fcost.put(start, Cell.distance(disttype, start, goal));
            queue.add(start);

            while (!queue.isEmpty()) {
                Cell current = queue.poll();
                JPanel panel = current.getPanel();
                if (!(current == start) && !(current == goal)) {
                    ColorPanel.colorpanel(panel, Color.YELLOW, 0,500);
                }

                if (current.equals(goal)) {
                    ColorPanel.colorpanel(panel, Color.RED, 0, 500);
                    MazeGenerator.path(previous, current);
                    break;
                }
                closedSet.add(current);

                for (Cell neighbor : current.neighbors) {
                    if (!Cell.isFree(current, neighbor, verticalWalls, horizontalWalls)){
                        continue;
                    }
                    if (closedSet.contains(neighbor)) {
                        continue;
                    }

                    int g = gcost.get(current) + 1;

                    if (!gcost.containsKey(neighbor) || g < gcost.get(neighbor)) {
                        gcost.put(neighbor, g);
                        double chaos = 1;
                        if (h_type == 1) {
                            chaos = g / Cell.distance(disttype,neighbor, start);
                        }
                        if (h_type == 2) {
                            chaos = 0;
                        }
                        fcost.put(neighbor, g + chaos * Cell.distance(disttype, neighbor, goal));
                        previous.put(neighbor, current);
                        queue.remove(neighbor);
                        queue.add(neighbor);
                    }
                    JPanel neighborpanel = neighbor.getPanel();
                    ColorPanel.colorpanel(neighborpanel, Color.CYAN, 0,500);

                }
            }
            long end_time = System.currentTimeMillis();
            MazeGenerator.textArea2.setText(Long.toString(end_time - start_time));            
        }).start();
        
    }
    
}
