package MazeSolver;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class Cell {
    int rowNumber;
    int columnNumber;
    ArrayList<Cell> neighbors;
    JPanel panel;
    //Boolean isVertex;
    //ArrayList<edge> edges;

    Cell(int row, int column) {
        this.rowNumber = row;
        this.columnNumber = column;
        this.neighbors = new ArrayList<>();
        this.panel = new JPanel();
        //this.isVertex = false;
        //this.edges = new ArrayList<>();
    }

    // Add a neighbor to this cell
    public void addNeighbor(Cell neighbor) {
        neighbors.add(neighbor);
    }

    // Get the panel of this cell
    public JPanel getPanel() {
        return panel;
    }

    public Cell getRandomNeighbor(ArrayList<Cell> visited) {
        ArrayList<Cell> check = new ArrayList<>(neighbors); // Copy the neighbors list
        check.removeAll(visited);
        if (check.isEmpty()) {
            return null; // No neighbors available
        }
        Random random = new Random();
        int randomIndex = random.nextInt(check.size());
        return check.get(randomIndex);
    }

    public static double distance(int type, Cell a, Cell b) {
        switch (type) {
            case 0:
                return Math.abs(a.rowNumber - b.rowNumber) + Math.abs(a.columnNumber - b.columnNumber);

            case 1:
                return Math.sqrt((a.rowNumber - b.rowNumber) * (a.rowNumber - b.rowNumber)
                        + (a.columnNumber - b.columnNumber) * (a.columnNumber - b.columnNumber));

            case 2:
                return Math.max(Math.abs(a.rowNumber - b.rowNumber), Math.abs(a.columnNumber - b.columnNumber));

            default:
                return 0.0;
        }

    }

    public static Boolean isFree(Cell current, Cell neighbor, boolean[][] verticalWalls,
            boolean[][] horizontalWalls) {
        Boolean result = false;
        if (neighbor.rowNumber == current.rowNumber) {
            if (neighbor.columnNumber > current.columnNumber) {
                result = !verticalWalls[current.rowNumber][neighbor.columnNumber];
            }

            else if (neighbor.columnNumber < current.columnNumber) {
                result = !verticalWalls[current.rowNumber][current.columnNumber];
            }
        } else if (neighbor.rowNumber > current.rowNumber) {
            result = !horizontalWalls[neighbor.rowNumber][current.columnNumber];
        } else
            result = !horizontalWalls[current.rowNumber][current.columnNumber];
        return result;
    }

    public Boolean hasUpWall(){
        return MazeGenerator.horizontalWalls[this.rowNumber][this.columnNumber];
    }

    public Boolean hasDownWall(){
        return MazeGenerator.horizontalWalls[this.rowNumber + 1][this.columnNumber];
    }

    public Boolean hasLeftWall(){
        return MazeGenerator.verticalWalls[this.rowNumber][this.columnNumber];
    }

    public Boolean hasRightWall(){
        return MazeGenerator.verticalWalls[this.rowNumber][this.columnNumber + 1];
    }

    public Boolean isVertex(){
        int hcount = 0;
        int vcount = 0;
        int totalcount = 0;
        if (hasUpWall()){
            hcount = hcount + 1;
            totalcount = totalcount + 1;
        }

        if (hasDownWall()){
            hcount = hcount + 1;
            totalcount = totalcount + 1;
        }

        if (hasLeftWall()){
            vcount = vcount + 1;
            totalcount = totalcount + 1;
        }

        if (hasRightWall()){
            vcount = vcount + 1;
            totalcount = totalcount + 1;
        }

        if (totalcount == 2){
            return (vcount == 1);
        }
        else{
            return true;
        }        
    }

   /*  public static Cell getOtherCell(Cell cell, edge edge){
        if (cell == edge.neighbors[0]){
            return edge.neighbors[1];
        }
        else{
            return edge.neighbors[0];
        }
    }*/
}
