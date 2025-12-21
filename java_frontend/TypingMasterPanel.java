import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;

public class TypingMasterPanel extends JPanel {

    /* ===================== GLOBAL STATS ===================== */

    public static boolean hasTested = false;
    public static double globalBestWPM = 0;
    public static double globalBestAccuracy = 100.0;
    public static int globalTotalTests = 0;

    /* ===================== COLORS ===================== */
    private static final Color BGPRIMARY = new Color(15, 23, 42);
    private static final Color BGCARD = new Color(30, 41, 59);
    private static final Color BGTERTIARY = new Color(51, 65, 85);
    private static final Color TEXTPRIMARY = new Color(241, 245, 249);
    private static final Color TEXTSECONDARY = new Color(148, 163, 184);
    private static final Color TEXTMUTED = new Color(100, 116, 139);
    private static final Color ACCENTBLUE = new Color(59, 130, 246);
    private static final Color ACCENTGREEN = new Color(34, 197, 94);
    private static final Color ERRORRED = new Color(239, 68, 68);
    private static final Color WARNINGYELLOW = new Color(251, 191, 36);

    /* ===================== UI COMPONENTS ===================== */
    private JTextPane targetPane;
    // ===== Input border flashing (NO background flashing) =====
    private Border normalInputBorder;
    private Border errorInputBorder;

    private JTextPane inputPane;
    private JComboBox<String> textSelector;

    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel errorsLabel;
    private JLabel timerLabel;
    private JLabel progressLabel;

    private JProgressBar progressBar;

    /* ===================== STATE ===================== */
    private String currentText;
    private int currentPosition = 0;
    private int correctChars = 0;
    private int totalErrors = 0;
    private long startTime = 0;
    private boolean testStarted = false;

    private Timer timer;
    private boolean highlightUpdatePending = false;

    /* ===================== TEXTS ===================== */
    private static final String[] SAMPLETEXTS = {
            "The quick brown fox jumps over the lazy dog.",
            "Programming is not just about writing code it is about solving problems efficiently.",
            "Practice makes perfect. Consistency builds mastery.",
            "Technology connects people across the globe.",
            "Success requires dedication and persistence."
    };

    /* ===================== CONSTRUCTOR ===================== */
    public TypingMasterPanel() {
        setLayout(new BorderLayout());
        setBackground(BGPRIMARY);

        add(createHeader(), BorderLayout.NORTH);
        add(createTypingArea(), BorderLayout.CENTER);
        add(createProgressPanel(), BorderLayout.SOUTH);

        timer = new Timer(100, e -> updateLiveStats());
        loadText(0);
    }

    /* ===================== HEADER ===================== */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(BGCARD);
        header.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("⌨ Typing Master");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        title.setForeground(TEXTPRIMARY);

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        timerLabel.setForeground(ACCENTBLUE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        left.setOpaque(false);
        left.add(title);
        left.add(timerLabel);

        textSelector = new JComboBox<>(new String[]{
                "Quick Fox Pangram",
                "Programming Wisdom",
                "Practice Mastery",
                "Technology Communication",
                "Success Dedication"
        });
        styleComboBox(textSelector);
        textSelector.addActionListener(e -> loadText(textSelector.getSelectedIndex()));

        // --- REDESIGNED RESET BUTTON ---
        JButton resetBtn = new JButton("↻  Reset");
        resetBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16)); // Changed font for emoji support
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.setFocusPainted(false);
        resetBtn.setBorderPainted(false);
        resetBtn.setContentAreaFilled(false); // Important for custom shape
        resetBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Bigger internal padding

        // Add the action
        resetBtn.addActionListener(e -> resetTest());

        // Custom Painting for Rounded Corners & Hover Effect
        resetBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Change color on hover
                AbstractButton b = (AbstractButton) c;
                if (b.getModel().isPressed()) {
                    g2.setColor(ACCENTBLUE.darker());
                } else if (b.getModel().isRollover()) {
                    g2.setColor(ACCENTBLUE.brighter());
                } else {
                    g2.setColor(ACCENTBLUE);
                }

                // Draw Rounded Rectangle (Radius 20)
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 5, 5);

                // Let Swing draw the text centered over our shape
                super.paint(g, c);
                g2.dispose();
            }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);
        right.add(textSelector);
        right.add(resetBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    /* ===================== TYPING AREA ===================== */
    private JPanel createTypingArea() {
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 25));
        container.setBackground(BGPRIMARY);
        container.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        /* -------- Reference Text -------- */
        targetPane = new JTextPane();
        targetPane.setEditable(false);
        targetPane.setFont(new Font("JetBrains Mono", Font.PLAIN, 17));
        targetPane.setBackground(BGCARD);
        targetPane.setForeground(TEXTPRIMARY);
        targetPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        targetPane.setFocusable(false);
        targetPane.setCaret(null);
        targetPane.setCursor(Cursor.getDefaultCursor());

        // FULLY NON-SELECTABLE
        targetPane.setHighlighter(null);
        targetPane.setSelectionColor(new Color(0, 0, 0, 0));
        targetPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { e.consume(); }
            public void mouseReleased(MouseEvent e) { e.consume(); }
        });

        JScrollPane targetScroll = new JScrollPane(targetPane);
        targetScroll.setBorder(BorderFactory.createLineBorder(BGTERTIARY, 2));

        /* -------- Input Text -------- */
        inputPane = new JTextPane();
        inputPane.setFont(new Font("JetBrains Mono", Font.PLAIN, 17));
        inputPane.setBackground(BGCARD);
        inputPane.setForeground(TEXTPRIMARY);
        inputPane.setCaretColor(ACCENTGREEN);
        inputPane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        inputPane.setSelectionColor(ACCENTBLUE);
        inputPane.setSelectedTextColor(Color.WHITE);

        inputPane.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { handleKeyTyped(e); }
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    e.consume();
                    flashError();
                }
            }
        });

        JScrollPane inputScroll = new JScrollPane(inputPane);
        normalInputBorder = BorderFactory.createLineBorder(ACCENTBLUE, 2);
        errorInputBorder  = BorderFactory.createLineBorder(ERRORRED, 2);

        inputScroll.setBorder(normalInputBorder);

     //   inputScroll.setBorder(BorderFactory.createLineBorder(ACCENTBLUE, 2));

        container.add(targetScroll);
        container.add(inputScroll);
        return container;
    }

    /* ===================== PROGRESS ===================== */
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BGCARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(ACCENTGREEN);
        progressBar.setBackground(BGTERTIARY);

        progressLabel = new JLabel("", SwingConstants.CENTER);
        progressLabel.setForeground(TEXTSECONDARY);

        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(progressLabel, BorderLayout.CENTER);
        return panel;
    }

    /* ===================== LOGIC ===================== */
    private void handleKeyTyped(KeyEvent e) {
        if (!testStarted) startTest();

        // Prevent typing past the end
        if (currentPosition >= currentText.length()) {
            e.consume();
            return;
        }

        char typed = e.getKeyChar();
        char expected = currentText.charAt(currentPosition);

        if (typed == expected) {
            correctChars++;
            currentPosition++;
            updateHighlighting();
            updateProgress();

            // *** NEW CHECK: IF TEXT IS COMPLETED ***
            if (currentPosition == currentText.length()) {
                finishTest();
            }

        } else {
            e.consume();
            totalErrors++;
            Toolkit.getDefaultToolkit().beep();
            flashError();
        }
    }
    private void finishTest() {
        // 1. Stop the timer
        timer.stop();

        // 2. Calculate Stats
        long elapsedTime = System.currentTimeMillis() - startTime;
        double minutes = elapsedTime / 60000.0;

        // Standard WPM formula: (Characters / 5) / Minutes
        int wpm = (int) ((correctChars / 5.0) / minutes);

        // Accuracy formula: (Correct / (Correct + Errors)) * 100
        int totalInputs = correctChars + totalErrors;
        double accuracy = totalInputs == 0 ? 0 : ((double) correctChars / totalInputs) * 100;

        // 3. Update Global Variables
        globalTotalTests++;
        hasTested = true;

        // Update best WPM if current is higher
        if (wpm > globalBestWPM) {
            globalBestWPM = wpm;
        }

        // Update best Accuracy (usually we track accuracy of the best run,
        // or you might want to track 'average'. Here we store best accuracy of any run)
        // Note: You might want to update this logic depending on if you want
        // "Best Accuracy ever" or "Accuracy of the Best WPM run".
        // For now, let's track the highest accuracy achieved.
        if (accuracy > globalBestAccuracy && accuracy != 100.0) {
            // Logic can be tricky: usually 100% is default best.
            // If globalBest is 100 but user hasn't tested, we might overwrite.
            // Let's assume high score logic:
            globalBestAccuracy = Math.max(globalBestAccuracy, accuracy);
        }
        // Simple overwrite for now based on your initial variable state:
        // If it's the first test or better accuracy:
        if (globalTotalTests == 1 || accuracy > globalBestAccuracy) {
            globalBestAccuracy = accuracy;
        }

        // 4. Show Success Message
        String message = String.format(
                "<html><body style='width: 200px; color: #0f172a; font-family: Segoe UI;'>" +
                        "<h2 style='color: #22c55e;'>Exercise Completed!</h2>" +
                        "<p><b>WPM:</b> %d<br>" +
                        "<b>Accuracy:</b> %.1f%%<br>" +
                        "<b>Errors:</b> %d</p>" +
                        "</body></html>",
                wpm, accuracy, totalErrors
        );

        JOptionPane.showMessageDialog(this, message, "Result", JOptionPane.INFORMATION_MESSAGE);

        // 5. Auto-reset for the next round
        resetTest();
    }
    private void startTest() {
        testStarted = true;
        startTime = System.currentTimeMillis();
        timer.start();
    }

    private void updateLiveStats() {
        long elapsed = System.currentTimeMillis() - startTime;
        int secs = (int) (elapsed / 1000);
        timerLabel.setText(String.format("%02d:%02d", secs / 60, secs % 60));
    }

    private void updateHighlighting() {
        if (highlightUpdatePending) return;
        highlightUpdatePending = true;

        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = targetPane.getStyledDocument();

                // Completed text (GREEN)
                if (currentPosition > 0) {
                    Style done = targetPane.addStyle("done", null);
                    StyleConstants.setForeground(done, ACCENTGREEN);
                    StyleConstants.setBold(done, true);
                    doc.setCharacterAttributes(0, currentPosition, done, true);
                }

                // Current character (YELLOW)
                if (currentPosition < currentText.length()) {
                    Style current = targetPane.addStyle("current", null);
                    StyleConstants.setForeground(current, WARNINGYELLOW);
                    StyleConstants.setBold(current, true);
                    StyleConstants.setUnderline(current, true);
                    doc.setCharacterAttributes(currentPosition, 1, current, true);
                }

            } finally {
                highlightUpdatePending = false;
            }
        });
    }

    private void updateProgress() {
        int percent = (int) (currentPosition * 100.0 / currentText.length());
        progressBar.setValue(percent);
        progressBar.setString(percent + "%");
        progressLabel.setText(currentPosition + " / " + currentText.length() + " characters");
    }

    private void loadText(int index) {
        currentText = SAMPLETEXTS[index];
        targetPane.setText(currentText);
        applyBaseStyleOnce();

        resetTest();
    }
    private void applyBaseStyleOnce() {
        StyledDocument doc = targetPane.getStyledDocument();

        Style base = targetPane.addStyle("base", null);
        StyleConstants.setForeground(base, TEXTMUTED);
        StyleConstants.setBold(base, false);
        StyleConstants.setUnderline(base, false);
        StyleConstants.setFontSize(base, 17);

        doc.setCharacterAttributes(0, doc.getLength(), base, true);
    }

    private void resetTest() {
        inputPane.setText("");
        currentPosition = 0;
        correctChars = 0;
        totalErrors = 0;
        testStarted = false;
        timer.stop();
        timerLabel.setText("00:00");

        updateProgress();
        inputPane.requestFocus();
    }

    /* ===================== UI HELPERS ===================== */
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleComboBox(JComboBox<String> combo) {
        // 1. Basic properties
        combo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        combo.setForeground(TEXTPRIMARY);
        combo.setBackground(BGTERTIARY); // Matches the arrow button background
        combo.setFocusable(false);

        // 2. Remove the ugly line border, just add padding
        combo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        // 3. Apply Custom UI
        combo.setUI(new BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {
                // Create a completely custom button for the arrow
                JButton arrow = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Background
                        g2.setColor(BGTERTIARY);
                        g2.fillRect(0, 0, getWidth(), getHeight());

                        // Arrow Icon
                        int size = 8;
                        int x = (getWidth() - size) / 2;
                        int y = (getHeight() - size) / 2 + 1;

                        // Draw a downward triangle
                        Path2D path = new Path2D.Double();
                        path.moveTo(x, y);
                        path.lineTo(x + size, y);
                        path.lineTo(x + size / 2.0, y + size / 1.5);
                        path.closePath();

                        g2.setColor(ACCENTBLUE); // Arrow color
                        g2.fill(path);
                        g2.dispose();
                    }
                };

                // Make the arrow button borderless and match size
                arrow.setBorder(BorderFactory.createEmptyBorder());
                arrow.setContentAreaFilled(false);
                arrow.setFocusable(false);
                return arrow;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Force the background color so it doesn't turn white on click
                g.setColor(BGTERTIARY);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            // Keep the dropdown list renderer (User liked this part)
            @Override
            protected ListCellRenderer<Object> createRenderer() {
                return new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index,
                            boolean isSelected, boolean cellHasFocus) {

                        JLabel label = (JLabel) super.getListCellRendererComponent(
                                list, value, index, isSelected, cellHasFocus);

                        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

                        if (isSelected) {
                            label.setBackground(ACCENTBLUE);
                            label.setForeground(TEXTPRIMARY);
                        } else {
                            label.setBackground(BGCARD); // Dropdown background
                            label.setForeground(TEXTSECONDARY);
                        }
                        return label;
                    }
                };
            }
        });
    }

    private void flashError() {
        JScrollPane scroll = (JScrollPane) inputPane.getParent().getParent();
        scroll.setBorder(errorInputBorder);

        Timer t = new Timer(120, e -> scroll.setBorder(normalInputBorder));
        t.setRepeats(false);
        t.start();
    }

}
