import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
//import org.math.plot.Plot2DPanel;

public class EllipticCurvePoints {

    public static void main(String[] args) {
        int a = 0;
        int b = 7;
        int p = 17;

        //Генерируем точки
        ArrayList<Point> points = generatePoints(a, b, p);
        System.out.println("Найдено точек на кривой: " + points.size());
        System.out.println("Точки: " + points);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите операцию: ");
        System.out.println("1 - Сложение двух точек кривой");
        System.out.println("2 - Удвоение точки кривой");

        if(scanner.nextInt() == 1){
            //сложение точек
            Point p1 = new Point(1, 12, a, b, p); // 1 12
            Point p2 = new Point(5, 8, a, b, p); //5 8
            Point p3 = p1.summ(p2);
            System.out.println("P(Синяя) = " + p1);
            System.out.println("Q(Салатовая) = " + p2);
            System.out.println("P + Q = " + p3 + " (желтая)");

            // Отрисовка сложения точек
            Diagram diagram = new Diagram(points, p1, p2, p3);
            Frame frame = new Frame("Сложение точек");
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent event) {
                    System.exit(0);
                }
            });
            frame.add(diagram);
            frame.setSize(600, 600);
            frame.setVisible(true);
        }
        else {
            Point g = new Point(15, 13, a, b, p);
            Point g2 = g.doublePoint();
            Point g4 = g2.doublePoint();

            System.out.println("G(Синяя) = " + g);
            System.out.println("G2(Салатовая) = " + g2);
            System.out.println("G4(Желтая) = " + g4);

            //отрисовка удвоения точки
            Diagram diagram = new Diagram(points, g, g2, g4);
            Frame frame = new Frame("Удвоение точки");
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent event) {
                    System.exit(0);
                }
            });
            frame.add(diagram);
            frame.setSize(600, 600);
            frame.setVisible(true);
        }
    }

    //метод для генерации точек
    public static ArrayList<Point> generatePoints(int a, int b, int p) {
        ArrayList<Point> points = new ArrayList<Point>();

        for (int x = 0; x < p; x++) {
            for (int y = 0; y < p; y++) {
                Point point = new Point(x, y, a, b, p);
                if (point.isOnCurve()) {
                    points.add(point);
                }
            }
        }

        return points;
    }
}