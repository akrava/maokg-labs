package ua.kpi.fpm.pzks.maokg;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends JPanel implements ActionListener {
    Timer timer;
    private float alpha = 1;
    private float delta = 0.01f;

    private double dx = 1;
    private double tx = 1;
    private double ty = 0;
    private static int maxWidth;
    private static int maxHeight;

    private static final int imageHeight = 170;
    private static final int imageWidth = 200;
    private static final double[][] tvPoints = {
            { 0, 30 }, { 200, 30 }, { 200, 170 }, { 0, 170 }, { 0, 30 }
    };

    public Main() {
        timer = new Timer(10, this);
        timer.start();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setBackground(new Color(127, 255, 0));
        g2d.clearRect(0, 0, maxWidth, maxHeight);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.translate(maxWidth / 4, maxHeight / 2);
        paintImage(g2d);
        g2d.translate(-maxWidth / 4, -maxHeight / 2);

        BasicStroke bs1 = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(bs1);
        g2d.drawRect(maxWidth/2, 0,maxWidth / 2 - 1, maxHeight - 1);
        g2d.setStroke(new BasicStroke());
        g2d.translate(maxWidth * 3/4, maxHeight/2);

        g2d.translate(tx, ty);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        paintImage(g2d);
    }

    private void paintImage(Graphics2D g2d) {
        int startCanvasX = -imageWidth / 2;
        int startCanvasY = -imageHeight / 2;

        GeneralPath tv = new GeneralPath();
        var gradient = new GradientPaint(0, 0, new Color (255,165,0),
                0, 140, new Color(160, 80, 21));
        g2d.setPaint(gradient);
        tv.moveTo(startCanvasX + tvPoints[0][0], startCanvasY + tvPoints[0][1]);
        for (int k = 1; k < tvPoints.length; k++) {
            tv.lineTo(startCanvasX + tvPoints[k][0], startCanvasY + tvPoints[k][1]);
        }
        tv.closePath();
        g2d.fill(tv);

        g2d.setColor(new Color(128, 128, 128));
        g2d.fillRoundRect(startCanvasX + 10, startCanvasY + 40,140,120, 20, 20);

        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 3; i++) {
            g2d.fillOval(startCanvasX + 175, startCanvasY + 90 + (25 * i), 10,10);
        }

        g2d.drawLine(-30, startCanvasY,0,startCanvasY + 30);
        g2d.drawLine(0, startCanvasY + 30,30,startCanvasY);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lab #2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(new Main());
        frame.setVisible(true);
        var size = frame.getSize();
        var insets = frame.getInsets();
        maxWidth = size.width - insets.left - insets.right - 1;
        maxHeight = size.height - insets.top - insets.bottom - 1;
    }

    @SuppressWarnings("all")
    public void actionPerformed(ActionEvent e) {
        if (alpha < 0.01f || alpha > 0.99f) {
            delta = -delta;
        }

        double radiusCanvas = Math.pow(maxHeight / 2 - imageHeight / 2, 2);
        if (tx <= 0 && ty < 0) {
            tx -= dx;
            ty = -Math.sqrt(radiusCanvas - Math.pow(tx, 2));
        } else if (tx > 0 && ty <= 0) {
            tx -= dx;
            ty = -Math.sqrt(radiusCanvas - Math.pow(tx, 2));
        } else if (tx >= 0 && ty > 0) {
            tx += dx;
            ty = Math.sqrt(radiusCanvas - Math.pow(tx, 2));
        } else if (tx < 0 && ty >= 0) {
            tx += dx;
            ty = Math.sqrt(radiusCanvas - Math.pow(tx, 2));
        }

        alpha += delta;
        repaint();
    }
}