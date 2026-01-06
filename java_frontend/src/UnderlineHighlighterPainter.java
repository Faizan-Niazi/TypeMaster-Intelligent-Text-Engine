import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class UnderlineHighlighterPainter extends DefaultHighlighter.DefaultHighlightPainter {
    private Color color;

    public UnderlineHighlighterPainter(Color color) {
        super(color);
        this.color = color;
    }

    @Override
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            // Get the rectangle for the text range
            Rectangle r1 = c.modelToView(offs0).getBounds();
            Rectangle r2 = c.modelToView(offs1).getBounds();

            // Draw wavy underline
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(2.0f));

            int y = r1.y + r1.height - 2;
            int x1 = r1.x;
            int x2 = r2.x;

            // Create wavy line path
            Path2D.Float path = new Path2D.Float();
            path.moveTo(x1, y);

            int waveWidth = 4;
            int waveHeight = 2;

            for (int x = x1; x < x2; x += waveWidth) {
                int nextX = Math.min(x + waveWidth / 2, x2);
                path.quadTo(x + waveWidth / 4, y - waveHeight, nextX, y);

                x += waveWidth / 2;
                nextX = Math.min(x + waveWidth / 2, x2);
                if (x < x2) {
                    path.quadTo(x + waveWidth / 4, y + waveHeight, nextX, y);
                }
            }

            g2d.draw(path);

        } catch (BadLocationException e) {
            // Ignore
        }

        return null;
    }
}