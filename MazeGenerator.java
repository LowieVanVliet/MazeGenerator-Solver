package MazeSolver;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.*;

public class MazeGenerator extends JFrame {
    private int rows;
    private int columns;
    private double wallpercent;
    private Cell[][] cells;
    /*private graph graph;*/
    public static boolean[][] horizontalWalls;
    public static boolean[][] verticalWalls;
    private JPanel mazePanel;
    private static LinePanel linePanel;
    private JButton updateButton;
    private JComboBox<String> algorithmSelector;
    private JComboBox<String> distanceSelector;
    private JButton startButton;

    private Cell start;
    private static Cell goal;

    private static JTextArea textArea1;
    public static JTextArea textArea2;

    public MazeGenerator(int rows, int columns, double wallpercent) {
        this.rows = rows;
        this.columns = columns;
        this.wallpercent = wallpercent;
        this.cells = new Cell[rows][columns];
        horizontalWalls = new boolean[rows + 1][columns];
        verticalWalls = new boolean[rows][columns + 1];
        initializeCells();
        generateMaze();
        initializeGUI();
    }

    private void initializeCells() {
        // Create cells and set their neighbors
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        // Set neighbors for each cell
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = cells[i][j];
                if (i > 0)
                    cell.addNeighbor(cells[i - 1][j]); // Upper neighbor
                if (i < rows - 1)
                    cell.addNeighbor(cells[i + 1][j]); // Lower neighbor
                if (j > 0)
                    cell.addNeighbor(cells[i][j - 1]); // Left neighbor
                if (j < columns - 1)
                    cell.addNeighbor(cells[i][j + 1]); // Right neighbor
            }
        }
    }

    public void generateMaze() {
        start = cells[0][0]; // Start from the top-left cell
        Random random = new Random();

        int goalrow = random.nextInt(0, rows - 1);
        int goalcol = random.nextInt(0, columns - 1);
        goal = cells[goalrow][goalcol];

        ArrayList<Cell> stack = new ArrayList<>();
        ArrayList<Cell> visited = new ArrayList<>();

        stack.add(start);
        visited.add(start);

        // Initialize all walls
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < columns; j++) {
                horizontalWalls[i][j] = true;
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= columns; j++) {
                verticalWalls[i][j] = true;
            }
        }

        while (!stack.isEmpty()) {
            Cell cell = stack.remove(stack.size() - 1); // Remove from the end to mimic stack behavior
            Cell neighbor = cell.getRandomNeighbor(visited);
            if (neighbor != null && neighbor != goal) {
                stack.add(cell);
                stack.add(neighbor);
                visited.add(neighbor);

                // Remove the wall between cell and neighbor
                if (neighbor.rowNumber == cell.rowNumber) {
                    // Horizontal neighbors
                    if (neighbor.columnNumber > cell.columnNumber) {
                        verticalWalls[cell.rowNumber][cell.columnNumber + 1] = false;
                    } else {
                        verticalWalls[cell.rowNumber][cell.columnNumber] = false;
                    }
                } else {
                    // Vertical neighbors
                    if (neighbor.rowNumber > cell.rowNumber) {
                        horizontalWalls[cell.rowNumber + 1][cell.columnNumber] = false;
                    } else {
                        horizontalWalls[cell.rowNumber][cell.columnNumber] = false;
                    }
                }
            }
        }
        // Calculate total number of walls
        int totalHorizontalWalls = rows * (columns - 1);
        int totalVerticalWalls = (rows - 1) * columns;
        int totalWalls = totalHorizontalWalls + totalVerticalWalls;

        // Calculate number of walls to remove (1% of total walls)
        int wallsToRemove = (int) Math.floor(totalWalls * wallpercent);

        // Randomly remove walls until we've removed the required amount

        while (wallsToRemove > 0) {
            // Determine whether to remove a horizontal or vertical wall
            boolean removeHorizontal = random.nextBoolean();

            if (removeHorizontal) {
                // Remove a random horizontal wall
                int row = random.nextInt(1, rows);
                int column = random.nextInt(columns - 1);
                if (!horizontalWalls[row][column]
                        || ((row == goal.rowNumber) || (row == goal.rowNumber + 1)) && (column == goal.columnNumber)) {
                    continue; // Skip if already removed
                }
                horizontalWalls[row][column] = false;
            } else {
                // Remove a random vertical wall
                int row = random.nextInt(rows - 1);
                int column = random.nextInt(1, columns);
                if (!verticalWalls[row][column] || ((column == goal.columnNumber) || (column == goal.columnNumber + 1))
                        && (column == goal.columnNumber)) {
                    continue; // Skip if already removed
                }
                verticalWalls[row][column] = false;
            }

            wallsToRemove--;
        }
        boolean removeHorizontalgoal = random.nextBoolean();
        boolean removeLR = random.nextBoolean();
        if (horizontalWalls[goal.rowNumber + 1][goal.columnNumber] == true
                && horizontalWalls[goal.rowNumber][goal.columnNumber] == true
                && verticalWalls[goal.rowNumber][goal.columnNumber + 1] == true
                && verticalWalls[goal.rowNumber][goal.columnNumber] == true) {
            if (removeHorizontalgoal) {
                if (removeLR || goal.rowNumber == 0) {
                    horizontalWalls[goal.rowNumber + 1][goal.columnNumber] = false;
                } else {
                    horizontalWalls[goal.rowNumber][goal.columnNumber] = false;
                }
            } else {
                if (removeLR || goal.columnNumber == 0) {
                    verticalWalls[goal.rowNumber][goal.columnNumber + 1] = false;
                } else {
                    verticalWalls[goal.rowNumber][goal.columnNumber] = false;
                }
            }
        }
        
    }

    private void initializeGUI() {
        /*graph = new graph();
        graph.vertexcells.clear();
        graph.creategraph();*/
        mazePanel = new JPanel();
        mazePanel.setLayout(new GridLayout(rows, columns));

        linePanel = new LinePanel();
        linePanel.setLayout(new GridLayout(rows, columns));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new OverlayLayout(mainPanel));
        mainPanel.add(linePanel);
        mainPanel.add(mazePanel);

        displayMaze();

        updateButton = new JButton("Update Maze");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePanels();
            }
        });

        String[] algorithms = { "A*", "A* chaos", "Dijkstra", "Floodfill", "DFS" };
        algorithmSelector = new JComboBox<>(algorithms);

        String[] distances = { "Manhattan", "Euclidean", "Chebyshev" };
        distanceSelector = new JComboBox<>(distances);

        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the lines
                linePanel.clearLines();

                // Reset the background color of each panel to white
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (cells[i][j] == start || cells[i][j] == goal) {
                            continue;
                        }
                        cells[i][j].getPanel().setBackground(Color.WHITE);
                    }
                }

                int disttype = 0;
                String selectedDistance = (String) distanceSelector.getSelectedItem();
                switch (selectedDistance) {
                    case "Manhattan":
                        disttype = 0;
                        break;

                    case "Euclidean":
                        disttype = 1;
                        break;

                    case "Chebyshev":
                        disttype = 2;
                        break;

                    default:
                        disttype = 0;
                }

                String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
                switch (selectedAlgorithm) {
                    case "A*":
                        A_Star.A_Star_chaos(disttype, 0, start, goal, verticalWalls, horizontalWalls);
                        break;
                    case "Floodfill":
                        Floodfill.floodfill(disttype, start, goal, verticalWalls, horizontalWalls, cells);
                        break;
                    case "A* chaos":
                        A_Star.A_Star_chaos(disttype, 1, start, goal, verticalWalls, horizontalWalls);
                        break;
                    case "DFS":
                        DFS_alg.DFS(start, goal, verticalWalls, horizontalWalls);
                        break;
                    case "Dijkstra":
                        A_Star.A_Star_chaos(disttype, 2, start, goal, verticalWalls, horizontalWalls);
                        break;
                }
            }
        });

        // Initialize JTextArea components for displaying text
        textArea1 = new JTextArea(1, 10); // 3 rows, 20 columns
        textArea2 = new JTextArea(1, 10);

        // Set initial text and disable editing
        textArea1.setText("");
        textArea1.setEditable(false);
        textArea2.setText("");
        textArea2.setEditable(false);

        // Create JLabels for captions
        JLabel label1 = new JLabel("Path Length");
        JLabel label2 = new JLabel("Search time in ms");

        // Optionally, add scroll panes if you want scrollable text areas
        JScrollPane scrollPane1 = new JScrollPane(textArea1);
        JScrollPane scrollPane2 = new JScrollPane(textArea2);

        JPanel textAreaPanel1 = new JPanel();
        textAreaPanel1.setLayout(new BorderLayout());
        textAreaPanel1.add(label1, BorderLayout.NORTH);
        textAreaPanel1.add(scrollPane1, BorderLayout.CENTER);

        JPanel textAreaPanel2 = new JPanel();
        textAreaPanel2.setLayout(new BorderLayout());
        textAreaPanel2.add(label2, BorderLayout.NORTH);
        textAreaPanel2.add(scrollPane2, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout()); // Arrange components horizontally
        controlPanel.add(updateButton);
        controlPanel.add(algorithmSelector);
        controlPanel.add(distanceSelector);
        controlPanel.add(startButton);
        controlPanel.add(textAreaPanel1); // Add the panels to the control panel
        controlPanel.add(textAreaPanel2);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void displayMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = cells[i][j];
                JPanel panel = cell.getPanel();
                panel.setBackground(Color.WHITE);
                int top = horizontalWalls[i][j] ? 1 : 0;
                int left = verticalWalls[i][j] ? 1 : 0;
                int bottom = horizontalWalls[i + 1][j] ? 1 : 0;
                int right = verticalWalls[i][j + 1] ? 1 : 0;
                panel.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                mazePanel.add(panel);
            }
        }
        start.getPanel().setBackground(Color.GREEN);
        goal.getPanel().setBackground(Color.RED);
        mazePanel.revalidate();
        mazePanel.repaint();
    }

    private void updatePanels() {
        // Clear and regenerate the maze
        generateMaze();

        // Clear the lines
        linePanel.clearLines();
       /* graph = new graph();
        graph.vertexcells.clear();
        graph.creategraph();*/
        // Redisplay the maze
        displayMaze();
    }

    public static void path(Map<Cell, Cell> previous, Cell current) {
        Cell nextStep = current;
        int pathlen = 0;
        while (previous.containsKey(nextStep)) {
            Cell prevStep = previous.get(nextStep);
            pathlen = pathlen + (int) Cell.distance(0, nextStep, prevStep);
            drawLine(prevStep, nextStep);
            nextStep = prevStep;
        }
        textArea1.setText(Integer.toString(pathlen));
    }

    public static ArrayList<Cell> pathsegment(Map<Cell, Cell> prev1, Map<Cell, Cell> prev2, Cell goal) {
        ArrayList<Cell> splitpoints = new ArrayList<>();
        Cell next = goal;
        while (prev1.containsKey(next)) {
            Cell prevStep = prev1.get(next);
            next = prevStep;
            if (prev2.containsKey(prevStep) && prev1.get(prevStep) != prev2.get(prevStep)) {
                splitpoints.add(prevStep);
            }
        }
        splitpoints.add(goal);
        Map<Cell, Integer> switchList = new HashMap<>();
        for (int i = 0; i < splitpoints.size() - 1; i++) {
            int len1 = pathlength(prev1, splitpoints.get(i), splitpoints.get(i + 1));
            int len2 = pathlength(prev2, splitpoints.get(i + 1), splitpoints.get(i));
            if (len1 > len2) {
                switchList.put(splitpoints.get(i), 1);
            } else {
                switchList.put(splitpoints.get(i), 0);
            }
        }
        next = goal;
        Map<Cell, Cell> previous = prev1;
        int path_len = 1;

        while (previous.containsKey(next)) {
            if (switchList.containsKey(next)) {
                if (switchList.get(next) == 0) {
                    previous = prev1;
                } else {
                    previous = prev2;
                }
            }
            Cell prevStep = previous.get(next);
            drawLine(prevStep, next);
            next = prevStep;
            path_len++;
        }
        textArea1.setText(Integer.toString(path_len));
        return splitpoints;
    }

    public static int pathlength(Map<Cell, Cell> previous, Cell current, Cell goal) {
        Cell nextStep = current;
        int path_len = 1;

        while (previous.containsKey(nextStep)) {
            Cell prevStep = previous.get(nextStep);
            nextStep = prevStep;
            path_len++;
        }
        return path_len;
    }

    private static void drawLine(Cell start, Cell end) {
        // Calculate the center of the start cell
        Point startCenter = start.getPanel().getLocation();
        int startX = startCenter.x + start.getPanel().getWidth() / 2;
        int startY = startCenter.y + start.getPanel().getHeight() / 2;

        // Calculate the center of the end cell
        Point endCenter = end.getPanel().getLocation();
        int endX = endCenter.x + end.getPanel().getWidth() / 2;
        int endY = endCenter.y + end.getPanel().getHeight() / 2;
        // Add the line to the list of lines to draw
        linePanel.addLine(new Line2D.Float(startX, startY, endX, endY), start.getPanel().getWidth());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            double rows = 75;
            double columns = 75;
            MazeGenerator maze = new MazeGenerator((int) rows, (int) columns, 0.01); // 50 by 50 cells
            maze.setTitle("Maze Generator");
            maze.setSize(Math.min((int) Math.floor((columns/rows)*1000),2000), 1000);
            maze.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            maze.setVisible(true);
        });
    }

  /*   public class graph {
        ArrayList<edge> edges;
        ArrayList<Cell> vertexcells;

        graph() {
            this.edges = new ArrayList<>();
            this.vertexcells = new ArrayList<>();
        }

        class edge {
            Cell[] neighbors = new Cell[2];
            ArrayList<Cell> path;
            int length;

            edge(Cell cell, boolean isH) {
                this.path = new ArrayList<>();
                this.neighbors[0] = cell;
                cell.edges.add(this);
                path.add(cell);
                this.length = 1;
                if (isH) {
                    for (int i = cell.columnNumber - 1; i >= 0; i--) {
                        Cell neighbor = cells[cell.rowNumber][i];
                        path.add(neighbor);
                        if (vertexcells.contains(neighbor)) {
                            this.neighbors[1] = neighbor;
                            neighbor.edges.add(this);
                            break;
                        }
                        this.length++;
                    }
                } else {
                    for (int i = cell.rowNumber - 1; i >= 0; i--) {
                        Cell neighbor = cells[i][cell.columnNumber];
                        path.add(neighbor);
                        if (vertexcells.contains(neighbor)) {
                            this.neighbors[1] = neighbor;
                            neighbor.edges.add(this);
                            break;
                        }
                    }
                }
            }
        }

        public void creategraph() {
            vertexcells.clear();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    Cell current = cells[i][j];
                    if (current.isVertex()) {
                        
                        vertexcells.add(current);
                        if (!current.hasLeftWall()) {
                            new edge(current, true);
                        }
                        if (!current.hasUpWall()) {
                            new edge(current, false);
                        }
                    }
                }
            }
              ArrayList<Cell> toRemove = new ArrayList<>();
            for (Cell vertex : vertexcells) {
                
                int edgecount = vertex.edges.size();
                if (edgecount == 1 && vertex != start && vertex != goal) {
                    Cell other = Cell.getOtherCell(vertex, vertex.edges.get(0));
                    other.edges.remove(vertex.edges.get(0));
                    vertex.edges.clear();
                    vertex.isVertex = false;
                    toRemove.add(vertex);
                }
                if (edgecount == 2) {
                    vertex.isVertex = false;
                    edge edge1 = vertex.edges.get(0);
                    edge edge2 = vertex.edges.get(1);

                    edge1.path.addAll(edge2.path);
                    edge1.neighbors[0] = Cell.getOtherCell(vertex, edge1);
                    edge1.neighbors[1] = Cell.getOtherCell(vertex, edge2);
                    Cell.getOtherCell(vertex, edge2).edges.remove(edge2);
                    Cell.getOtherCell(vertex, edge2).edges.add(edge1);
                    toRemove.add(vertex);
                }
                else{
                    vertex.isVertex = true;
                }
            }
            vertexcells.removeAll(toRemove); 
        }

    }*/
}
