package MazeSolver;

import java.util.ArrayList;

public class graph {
    
    class vertex{
    int rowNumber;
    int columnNumber;
    
    ArrayList<edge> edges;
    }

    class edge{
        vertex[] neighbors = new vertex[2];
        ArrayList<Cell> path;
        int length;
        }
    }

