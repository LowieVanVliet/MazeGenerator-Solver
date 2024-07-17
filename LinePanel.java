package MazeSolver;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class LinePanel extends JPanel {
    private List<Line2D> linesToDraw;
    private double widthtodraw;

    public LinePanel() {
        this.linesToDraw = new ArrayList<>();
        setOpaque(false); // Ensure the panel is transparent
    }

    public void addLine(Line2D line, double width) {
        widthtodraw = width;
        linesToDraw.add(line);
        repaint();
    }

    public void clearLines() {
        linesToDraw.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke((float) widthtodraw/3));
        for (Line2D line : linesToDraw) {
            g2d.draw(line);
        }
    }
}
