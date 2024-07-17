package MazeSolver;

import java.awt.*;
import javax.swing.*;

import javafx.util.Pair;

import java.util.*;

public class Floodfill {

    public static void floodfill(int disttype, Cell start1, Cell goal1, boolean[][] verticalWalls,
            boolean[][] horizontalWalls, Cell[][] cells) {

        new Thread(() -> {
            int path1 = 0;
            int path2 = 0;
            int Try = 0;
            Cell start = start1;
            Cell goal = goal1;
            Pair<Integer, Integer> direction = new Pair<Integer,Integer>(1,0);
            long start_time = System.currentTimeMillis();
            Map<Cell, Cell> previous = new HashMap<>();
            Map<Cell, Cell> previous2 = new HashMap<>();
            ArrayList<Cell> stack = new ArrayList<>();

            ArrayList<Cell> visited = new ArrayList<>();

            stack.add(start);
            visited.add(start);

            while (!stack.isEmpty()) {
                Cell current = stack.get(stack.size() - 1);
                if (!(current == start) && !(current == goal)) {
                    JPanel panel = current.getPanel();
                    ColorPanel.colorpanel(panel, Color.YELLOW, 0, 500);
                }

                if (current.equals(goal)) {
                    stack.clear();
                    visited.clear();
                    Cell temp = start;
                    start = goal;
                    goal = temp;
                    stack.add(start);
                    visited.add(start);
                    if (Try == 0) {
                        Try = 1;
                        path1 = MazeGenerator.pathlength(previous, goal, current);
                        continue;
                    }
                    path2 = MazeGenerator.pathlength(previous2, current, goal);
                    JPanel panel = goal.getPanel();
                    ColorPanel.colorpanel(panel, Color.RED, 5, 0);
                    //ArrayList<Cell> splitpoints = MazeGenerator.pathsegment(previous, previous2, start);
                    if (path1 > path2){
                        MazeGenerator.path(previous2, start);
                    }
                    else{
                        MazeGenerator.path(previous, start);
                    }
                    
                    break;
                }

                double min_dist = Integer.MAX_VALUE;
                ArrayList<Cell> neighborlist = new ArrayList<>();
                ArrayList<Cell> nextlist = new ArrayList<>();
                for (Cell neighbor : current.neighbors) {

                    if (visited.contains(neighbor)) {

                        continue;
                    }
                    if (!Cell.isFree(current, neighbor, verticalWalls, horizontalWalls)){
                        continue;
                    }
                    neighborlist.add(neighbor);
                    if (Cell.distance(disttype, goal, neighbor) > min_dist) {
                        continue;
                    }

                    min_dist = Cell.distance(disttype, goal, neighbor);
                    nextlist.clear();
                    nextlist.add(neighbor);
                    
                }

                if (neighborlist.size() <= 1){
                    stack.remove(current);
                    if (neighborlist.size() == 0){
                        continue;
                    }
                }
                
                Cell closest = nextlist.get(0);
                Cell dircell = null;
                try {
                    dircell = cells[current.rowNumber + direction.getKey()][current.columnNumber + direction.getValue()];
                    if (visited.contains(dircell)){
                        dircell = null;
                    }
                }
                catch (Exception e){
                }
                
                neighborlist.remove(closest);
                neighborlist.remove(dircell);
                if (Cell.isFree(current, closest, verticalWalls, horizontalWalls) == true){
                    direction = new Pair<Integer, Integer>(closest.rowNumber - current.rowNumber,closest.columnNumber - current.columnNumber);
                    if (Try == 0){
                        previous.put(current, closest);
                    }
                    else {
                        previous2.put(closest, current);
                    }
                    stack.add(closest);
                    visited.add(closest);
                    JPanel panel = closest.getPanel();
                    ColorPanel.colorpanel(panel, Color.CYAN, 0, 500);
                }
                else if (dircell != null && Cell.isFree(current, dircell, verticalWalls, horizontalWalls) == true){
                    if (Try == 0){
                            previous.put(current, dircell);
                        }
                        else {
                            previous2.put(dircell, current);
                        }
                    stack.add(dircell);
                    visited.add(dircell);
                    JPanel panel = dircell.getPanel();
                    ColorPanel.colorpanel(panel, Color.CYAN, 0, 500);
                }
                else if (!neighborlist.isEmpty() && Cell.isFree(current, neighborlist.get(0), verticalWalls, horizontalWalls) == true){
                    Cell other = neighborlist.get(0);
                    direction = new Pair<Integer, Integer>(other.rowNumber - current.rowNumber, other.columnNumber - current.columnNumber);
                    stack.add(other);
                    visited.add(other);
                    if (Try == 0){
                            previous.put(current, other);
                        }
                        else {
                            previous2.put(other, current);
                        }
                    JPanel panel = other.getPanel();
                    ColorPanel.colorpanel(panel, Color.CYAN, 0, 500);
                }
                else if (neighborlist.size() > 1 && Cell.isFree(current, neighborlist.get(1), verticalWalls, horizontalWalls) == true){
                    Cell other = neighborlist.get(1);
                    direction = new Pair<Integer, Integer>(other.rowNumber - current.rowNumber, other.columnNumber - current.columnNumber);
                    if (Try == 0){
                            previous.put(current, other);
                        }
                        else {
                            previous2.put(other, current);
                        }
                    stack.add(other);
                    visited.add(other);
                    JPanel panel = other.getPanel();
                    ColorPanel.colorpanel(panel, Color.CYAN, 0, 500);
                }
                else {
                    
                    continue;
                }
            }
            long end_time = System.currentTimeMillis();
            MazeGenerator.textArea2.setText(Long.toString(end_time - start_time));
        }).start();
    }

}