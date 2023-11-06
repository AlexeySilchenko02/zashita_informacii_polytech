import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LFSRDiagram extends Panel {
    private int[] sequence;

    public LFSRDiagram(int[] sequence) {
        this.sequence = sequence;
    }

    public void paint(Graphics g) {
        int width = getSize().width;
        int height = getSize().height;
        int barWidth = Math.max(1, width / sequence.length);

        // Рисуем оси координат
        g.setColor(Color.BLACK);
        g.drawLine(0, height / 2, width, height / 2);
        g.drawLine(width / 2, 0, width / 2, height);

        // Рисуем точки
        g.setColor(Color.RED);
        for (int i = 0; i < sequence.length; i++) {
            int x = i * barWidth;
            int y = height / 2 - 10 * sequence[i];
            g.fillOval(x, y, barWidth / 2, barWidth / 2);
        }
    }
}
