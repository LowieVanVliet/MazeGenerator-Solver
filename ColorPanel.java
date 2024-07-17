package MazeSolver;

import java.awt.*;
import javax.swing.*;


public class ColorPanel {
    public static void colorpanel(JPanel panel, Color color, int sleepmilli, int sleepnano) {
        SwingUtilities.invokeLater(() -> {
            panel.setBackground(color);
            panel.revalidate();
            panel.repaint();
        });
        try {
            Thread.sleep(sleepmilli, sleepnano); // Small delay for visualization
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
