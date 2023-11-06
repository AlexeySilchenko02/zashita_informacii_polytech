import java.awt.*;
import java.util.ArrayList;

public class Diagram extends Panel {
    ArrayList<Point> points;
    Point summ;
    Point dubl;
    Point therd;
    public Diagram(ArrayList<Point> points, Point summ, Point dubl, Point therd) {
        this.points = points;
        this.summ = summ;
        this.dubl = dubl;
        this.therd = therd;
    }

    public void paint(Graphics g) {
        int width = getSize().width;
        int height = getSize().height;
        int barWidth = Math.max(1, width);
        int[] xArray = new int[points.size()];
        int[] yArray = new int[points.size()];
        int x;
        int y;

        // Отрисовка оси координат
        g.setColor(Color.GRAY);
        for (int i = 0; i < width; i++) {
            g.drawLine(0 + 30 * i, 0, 0 + 30 * i, height);
        }
        for (int i = 0; i < height; i++) {
            g.drawLine(0, 0 + 30 * i, width, 0 + 30 * i);
        }

        // Отрисовка точек
        g.setColor(Color.RED);
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            xArray[i] = point.getX();
            x = xArray[i];
            yArray[i] = point.getY();
            y = height / 30 - yArray[i] - 1;
            g.fillOval(x * 30, y * 30,10, 10);
        }

        // Дополнительная точка 1
        g.setColor(Color.blue);
        x = summ.getX();
        y = height / 30 - summ.getY() - 1;
        g.fillOval(x * 30, y * 30,10, 10);

        // Дополнительная точка 2
        g.setColor(Color.green);
        x = dubl.getX();
        y = height / 30 - dubl.getY() - 1;
        g.fillOval(x * 30, y * 30,10, 10);

        //Дополнительная точка 3
        g.setColor(Color.orange);
        x = therd.getX();
        y = height / 30 - therd.getY() - 1;
        g.fillOval(x * 30, y * 30,10, 10);
    }
}
