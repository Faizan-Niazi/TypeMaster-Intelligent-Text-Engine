import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Future;

public class PerformanceDashboardPanel extends JPanel {

    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_TERTIARY = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_PURPLE = new Color(168, 85, 247);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);

    private JTextArea textArea;
    private CircularMeter wpmMeter;
    private CircularMeter accuracyMeter;
    private MetricCard totalCharsCard;
    private MetricCard errorsCard;
    private MetricCard cpmCard;
    private MetricCard wordsCard;
    private JLabel timeLabel;

    private long startTime = 0;
    private int totalCharactersTyped = 0;
    private int totalWordsChecked = 0;
    private int validWords = 0;
    private int invalidWords = 0;
    private boolean isTyping = false;
    private String lastCheckedText = "";

    // Metrics for reporting
    public static int globalInvalidWords = 0;
    public static int globalTotalWords = 0;
    public static double globalBestWPM = 0;
    public static double globalBestAccuracy = 100.0;
    public static boolean hasTested = false;

    private javax.swing.Timer updateTimer;
    private javax.swing.Timer checkTimer;

    public PerformanceDashboardPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PRIMARY);

        add(createDashboard(), BorderLayout.NORTH);
        add(createTypingArea(), BorderLayout.CENTER);

        updateTimer = new javax.swing.Timer(100, e -> updateMetrics());
        updateTimer.start();
    }

    private JPanel createDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 20));
        dashboard.setBackground(BG_PRIMARY);
        dashboard.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("📊 Performance Dashboard");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);

        timeLabel = new JLabel("0:00");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        timeLabel.setForeground(ACCENT_BLUE);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(title, BorderLayout.WEST);
        header.add(timeLabel, BorderLayout.EAST);

        JPanel metricsGrid = new JPanel(new GridLayout(2, 3, 20, 20));
        metricsGrid.setOpaque(false);

        wpmMeter = new CircularMeter("WPM", 0, 150, ACCENT_BLUE);
        accuracyMeter = new CircularMeter("ACCURACY", 100, 100, ACCENT_GREEN);

        totalCharsCard = new MetricCard("Characters", "0", "📝", ACCENT_PURPLE);
        errorsCard = new MetricCard("Invalid Words", "0", "❌", ERROR_COLOR);
        cpmCard = new MetricCard("CPM", "0", "⚡", ACCENT_BLUE);
        wordsCard = new MetricCard("Total Words", "0", "📄", ACCENT_GREEN);

        metricsGrid.add(wpmMeter);
        metricsGrid.add(accuracyMeter);
        metricsGrid.add(cpmCard);
        metricsGrid.add(totalCharsCard);
        metricsGrid.add(errorsCard);
        metricsGrid.add(wordsCard);

        dashboard.add(header, BorderLayout.NORTH);
        dashboard.add(metricsGrid, BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel createTypingArea() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel instruction = new JLabel("📝 Start typing to see real-time performance metrics");
        instruction.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        instruction.setForeground(TEXT_SECONDARY);

        JButton resetBtn = createButton("↻ Reset", ACCENT_BLUE);
        resetBtn.addActionListener(e -> reset());

        headerPanel.add(instruction, BorderLayout.WEST);
        headerPanel.add(resetBtn, BorderLayout.EAST);

        textArea = new JTextArea();
        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 16));
        textArea.setBackground(BG_CARD);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setCaretColor(ACCENT_BLUE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (!isTyping) {
                    startTyping();
                }
                scheduleCheck();
            }
            public void removeUpdate(DocumentEvent e) {
                scheduleCheck();
            }
            public void changedUpdate(DocumentEvent e) {}
        });

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createLineBorder(BG_TERTIARY, 2));
        scroll.setPreferredSize(new Dimension(0, 300));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void startTyping() {
        isTyping = true;
        startTime = System.currentTimeMillis();
        hasTested = true;
    }

    private void scheduleCheck() {
        if (checkTimer != null && checkTimer.isRunning()) {
            checkTimer.stop();
        }
        checkTimer = new javax.swing.Timer(500, e -> checkSpelling());
        checkTimer.setRepeats(false);
        checkTimer.start();
    }

    private void checkSpelling() {
        String text = textArea.getText().trim();

        if (text.equals(lastCheckedText)) {
            return;
        }

        lastCheckedText = text;
        totalCharactersTyped = text.length();
        totalCharsCard.setValue(String.valueOf(totalCharactersTyped));

        if (text.isEmpty()) {
            totalWordsChecked = 0;
            validWords = 0;
            invalidWords = 0;
            errorsCard.setValue("0");
            wordsCard.setValue("0");
            return;
        }

        // Split into words
        String[] words = text.split("\\s+");
        totalWordsChecked = 0;
        validWords = 0;
        invalidWords = 0;

        List<Future<String>> futures = new ArrayList<>();
        List<String> cleanWords = new ArrayList<>();

        for (String word : words) {
            if (word.length() < 1) continue;

            String cleaned = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (cleaned.isEmpty()) continue;

            cleanWords.add(cleaned);
            futures.add(ModernMainFrame.CLIENT.sendAsync("CHECK:" + cleaned));
        }

        // Process results
        new Thread(() -> {
            int valid = 0;
            int invalid = 0;

            for (int i = 0; i < futures.size(); i++) {
                try {
                    String resp = futures.get(i).get();

                    if (resp != null && !resp.equals("ERR")) {
                        if (resp.contains("SPELL:VALID") || resp.contains("SPELL:PREFIX")) {
                            valid++;
                        } else {
                            invalid++;
                        }
                    }
                } catch (Exception ex) {
                    // Skip on error
                }
            }

            final int finalValid = valid;
            final int finalInvalid = invalid;
            final int finalTotal = valid + invalid;

            SwingUtilities.invokeLater(() -> {
                validWords = finalValid;
                invalidWords = finalInvalid;
                totalWordsChecked = finalTotal;

                errorsCard.setValue(String.valueOf(invalidWords));
                wordsCard.setValue(String.valueOf(totalWordsChecked));

                // Update global stats
                globalInvalidWords = Math.max(globalInvalidWords, invalidWords);
                globalTotalWords = Math.max(globalTotalWords, totalWordsChecked);
            });
        }).start();
    }

    private void updateMetrics() {
        if (!isTyping || totalCharactersTyped == 0) {
            return;
        }

        long elapsedMs = System.currentTimeMillis() - startTime;
        double minutes = elapsedMs / 60000.0;

        // Update time display
        int seconds = (int) (elapsedMs / 1000);
        int mins = seconds / 60;
        int secs = seconds % 60;
        timeLabel.setText(String.format("%d:%02d", mins, secs));

        if (minutes > 0.016) { // At least 1 second
            // Calculate WPM (standard: 5 chars = 1 word)
            double words = totalCharactersTyped / 5.0;
            int wpm = (int) (words / minutes);
            wpmMeter.setValue(Math.min(wpm, 150));

            // Update global best WPM
            if (wpm > globalBestWPM) {
                globalBestWPM = wpm;
            }

            // Calculate CPM
            int cpm = (int) (totalCharactersTyped / minutes);
            cpmCard.setValue(String.valueOf(cpm));
        }

        // Calculate accuracy
        if (totalWordsChecked > 0) {
            int accuracy = (int) ((validWords * 100.0) / totalWordsChecked);
            accuracyMeter.setValue(Math.max(0, Math.min(100, accuracy)));

            // Update global best accuracy
            if (accuracy > globalBestAccuracy && totalWordsChecked >= 5) {
                globalBestAccuracy = accuracy;
            }
        } else {
            accuracyMeter.setValue(100);
        }
    }

    private void reset() {
        textArea.setText("");
        isTyping = false;
        startTime = 0;
        totalCharactersTyped = 0;
        totalWordsChecked = 0;
        validWords = 0;
        invalidWords = 0;
        lastCheckedText = "";

        wpmMeter.setValue(0);
        accuracyMeter.setValue(100);
        totalCharsCard.setValue("0");
        errorsCard.setValue("0");
        cpmCard.setValue("0");
        wordsCard.setValue("0");
        timeLabel.setText("0:00");
    }

    private static class CircularMeter extends JPanel {
        private String label;
        private int value;
        private int maxValue;
        private Color color;

        public CircularMeter(String label, int value, int maxValue, Color color) {
            this.label = label;
            this.value = value;
            this.maxValue = maxValue;
            this.color = color;

            setBackground(BG_CARD);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BG_TERTIARY, 2),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            setPreferredSize(new Dimension(200, 200));
        }

        public void setValue(int value) {
            this.value = Math.min(value, maxValue);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 80;
            int x = (getWidth() - size) / 2;
            int y = 40;
            int strokeWidth = 12;

            g2.setColor(BG_TERTIARY);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.drawArc(x, y, size, size, 90, -360);

            double percentage = (double) value / maxValue;
            int angle = (int) (360 * percentage);

            g2.setColor(color);
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(x, y, size, size, 90, -angle);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
            String valueText = String.valueOf(value);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(valueText)) / 2;
            g2.setColor(TEXT_PRIMARY);
            g2.drawString(valueText, textX, y + size / 2 + 15);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(TEXT_SECONDARY);
            String labelText = label.toUpperCase();
            int labelX = (getWidth() - g2.getFontMetrics().stringWidth(labelText)) / 2;
            g2.drawString(labelText, labelX, 25);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String subtitle = "/ " + maxValue;
            int subX = (getWidth() - g2.getFontMetrics().stringWidth(subtitle)) / 2;
            g2.drawString(subtitle, subX, y + size + 25);
        }
    }

    private static class MetricCard extends JPanel {
        private JLabel valueLabel;

        public MetricCard(String title, String value, String icon, Color accentColor) {
            // Use BorderLayout to pin title to top and number to center
            setLayout(new BorderLayout(0, 0));
            setBackground(BG_CARD);

            // Same border style as CircularMeter
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BG_TERTIARY, 2),
                    BorderFactory.createEmptyBorder(15, 10, 15, 10)
            ));

            // Top Panel for Title and Icon
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            topPanel.setOpaque(false);

            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Smaller icon
            iconLabel.setForeground(accentColor);

            JLabel titleLabel = new JLabel(title.toUpperCase());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(TEXT_SECONDARY);

            topPanel.add(iconLabel);
            topPanel.add(titleLabel);

            // Center Label for the big number
            valueLabel = new JLabel(value);
            // Font size 42 matches the CircularMeter font size
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
            valueLabel.setForeground(TEXT_PRIMARY);
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            valueLabel.setVerticalAlignment(SwingConstants.CENTER);

            add(topPanel, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
        }

        public void setValue(String value) {
            valueLabel.setText(value);
        }
    }

}