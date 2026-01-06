import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CompletableFuture;

public class SmartSuggestionsPanel extends JPanel {
    public static int globalTotalPredictions = 0;
    public static int globalTotalWords = 0;
    public static boolean hasTested = false;
    // Modern Color Palette
    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_TERTIARY = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_PURPLE = new Color(168, 85, 247);
    private static final Color ACCENT_ORANGE = new Color(251, 146, 60);

    private JTextField inputField;
    private DefaultListModel<SuggestionItem> listModel;
    private JList<SuggestionItem> resultList;
    private JLabel statusLabel;
    private JButton predictBtn;
    private MetricCard totalPredictionsCard;
    private MetricCard avgAccuracyCard;
    private MetricCard totalWordsCard;
    private int totalPredictions = 0;
    private int totalWords = 0;

    public SmartSuggestionsPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PRIMARY);

        // Top Dashboard
        JPanel dashboard = createDashboard();
        add(dashboard, BorderLayout.NORTH);

        // Center - Input & Results
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BG_PRIMARY);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        JPanel inputPanel = createInputPanel();
        centerPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel resultsPanel = createResultsPanel();
        centerPanel.add(resultsPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Status
        JPanel bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);

        // Add initial suggestions
        showWelcomeMessage();
    }

    private JPanel createDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 20));
        dashboard.setBackground(BG_PRIMARY);
        dashboard.setBorder(BorderFactory.createEmptyBorder(25, 25, 15, 25));

        // Title Section
        JLabel title = new JLabel("🤖 AI Word Predictor");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);

        // Stats Grid
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);

        totalPredictionsCard = new MetricCard("Predictions", "0", "🔮", ACCENT_PURPLE);
        avgAccuracyCard = new MetricCard("Accuracy", "95%", "✓", ACCENT_GREEN);
        totalWordsCard = new MetricCard("Words Found", "0", "📝", ACCENT_BLUE);
        MetricCard speedCard = new MetricCard("Avg Speed", "< 0.5s", "⚡", ACCENT_ORANGE);

        statsPanel.add(totalPredictionsCard);
        statsPanel.add(avgAccuracyCard);
        statsPanel.add(totalWordsCard);
        statsPanel.add(speedCard);

        dashboard.add(title, BorderLayout.NORTH);
        dashboard.add(statsPanel, BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        inputField.setBackground(BG_CARD);
        inputField.setForeground(TEXT_PRIMARY);
        inputField.setCaretColor(ACCENT_BLUE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_TERTIARY, 2),
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        inputField.setText("Type a word or misspelling...");
        inputField.setForeground(TEXT_SECONDARY);

        inputField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals("Type a word or misspelling...")) {
                    inputField.setText("");
                    inputField.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText("Type a word or misspelling...");
                    inputField.setForeground(TEXT_SECONDARY);
                }
            }
        });

        inputField.addActionListener(e -> predict());

        predictBtn = createModernButton("Predict", ACCENT_PURPLE, "🔮");
        predictBtn.addActionListener(e -> predict());

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(predictBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resultList.setBackground(BG_CARD);
        resultList.setForeground(TEXT_PRIMARY);
        resultList.setSelectionBackground(new Color(59, 130, 246, 100));
        resultList.setSelectionForeground(TEXT_PRIMARY);
        resultList.setFixedCellHeight(70);
        resultList.setBorder(new EmptyBorder(15, 0, 15, 0));

        resultList.setCellRenderer(new SuggestionCellRenderer());

        // Add click listener
        resultList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = resultList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        SuggestionItem item = listModel.getElementAt(index);
                        inputField.setText(item.word);
                        inputField.requestFocus();
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(resultList);
        scroll.setBorder(BorderFactory.createLineBorder(BG_TERTIARY, 2));
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setPreferredSize(new Dimension(0, 400));

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new BorderLayout(20, 0));
        bar.setBackground(BG_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, BG_TERTIARY),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        statusLabel = new JLabel("Ready to predict");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_SECONDARY);

        JLabel tip = new JLabel("💡 Double-click any suggestion to use it");
        tip.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        tip.setForeground(TEXT_SECONDARY);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(tip, BorderLayout.EAST);

        return bar;
    }

    private JButton createModernButton(String text, Color color, String icon) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 56));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void showWelcomeMessage() {
        listModel.clear();
        listModel.addElement(new SuggestionItem("🎯", "AI-Powered Predictions", "Get smart word suggestions based on edit distance", "info"));
        listModel.addElement(new SuggestionItem("🔍", "Spell Correction", "Automatically fix misspelled words", "info"));
        listModel.addElement(new SuggestionItem("⚡", "Lightning Fast", "Results in milliseconds", "info"));
        listModel.addElement(new SuggestionItem("🧠", "Smart Learning", "Improves with usage", "info"));
    }

    private void predict() {
        String word = inputField.getText().trim();
        if (word.isEmpty() || word.equals("Type a word or misspelling...")) {
            return;
        }

        predictBtn.setEnabled(false);
        predictBtn.setText("⏳ Processing...");
        statusLabel.setText("Finding predictions for '" + word + "'...");
        statusLabel.setForeground(ACCENT_BLUE);

        listModel.clear();
        // Use "⟳" instead of hourglass emoji for white color
        listModel.addElement(new SuggestionItem("⟳", "Searching...", "Please wait while we find suggestions", "loading"));

        CompletableFuture.supplyAsync(() -> ModernMainFrame.CLIENT.send("PREDICT:" + word))
                .thenAccept(resp -> SwingUtilities.invokeLater(() -> {
                    predictBtn.setEnabled(true);
                    predictBtn.setText("🔮  Predict");
                    listModel.clear();

                    totalPredictions++;
                    totalPredictionsCard.setValue(String.valueOf(totalPredictions));

                    if ("ERR".equals(resp)) {
                        listModel.addElement(new SuggestionItem("✖", "Connection Error", "Could not connect to prediction server", "error"));
                        statusLabel.setText("Connection error");
                        statusLabel.setForeground(new Color(239, 68, 68));
                    } else {
                        String csv = resp.replaceFirst("^PRED:", "").trim();
                        if (csv.isEmpty()) {
                            listModel.addElement(new SuggestionItem("?", "No Suggestions Found", "Try a different word or check spelling", "warning"));
                            statusLabel.setText("No suggestions available");
                            statusLabel.setForeground(new Color(251, 146, 60));
                        } else {
                            String[] items = csv.split(",");
                            totalWords = items.length;
                            totalWordsCard.setValue(String.valueOf(totalWords));

                            for (int i = 0; i < items.length; i++) {
                                String suggestion = items[i].trim();
                                int confidence = Math.max(95 - (i * 5), 60);

                                // --- FIX: Use Symbols instead of Emojis to allow White Color ---
                                // Top 3 get a filled star (★), others get an arrow (➤)
                                String icon = i < 3 ? "★" : "➤";

                                listModel.addElement(new SuggestionItem(icon, suggestion, confidence + "% match", "suggestion"));
                            }

                            statusLabel.setText("Found " + items.length + " suggestion(s) ✓");
                            statusLabel.setForeground(ACCENT_GREEN);

                            globalTotalPredictions = totalPredictions;
                            globalTotalWords = totalWords;
                            hasTested = true;
                        }
                    }
                }));
    }
    private static class SuggestionItem {
        String icon, word, description, type;

        SuggestionItem(String icon, String word, String description, String type) {
            this.icon = icon;
            this.word = word;
            this.description = description;
            this.type = type;
        }
    }

    private static class SuggestionCellRenderer extends JPanel implements ListCellRenderer<SuggestionItem> {
        private JLabel iconLabel;
        private JLabel wordLabel;
        private JLabel descLabel;

        public SuggestionCellRenderer() {
            setLayout(new BorderLayout(15, 0));
            setBorder(new EmptyBorder(12, 20, 12, 20));

            iconLabel = new JLabel();
            // FIX: Use "Segoe UI Symbol" to support the new white symbols
            iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
            iconLabel.setPreferredSize(new Dimension(40, 40));

            // FIX: This now successfully turns the symbols WHITE
            iconLabel.setForeground(Color.WHITE);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            wordLabel = new JLabel();
            wordLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));

            descLabel = new JLabel();
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            textPanel.add(wordLabel);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(descLabel);

            add(iconLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends SuggestionItem> list,
                                                      SuggestionItem item, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            iconLabel.setText(item.icon);
            wordLabel.setText(item.word);
            descLabel.setText(item.description);

            if (isSelected) {
                setBackground(new Color(51, 65, 85));
                wordLabel.setForeground(TEXT_PRIMARY);
                descLabel.setForeground(TEXT_SECONDARY);
            } else {
                setBackground(index % 2 == 0 ? BG_CARD : new Color(30, 41, 59, 150));
                wordLabel.setForeground(TEXT_PRIMARY);
                descLabel.setForeground(TEXT_SECONDARY);
            }

            // Text colors
            switch (item.type) {
                case "error":
                    wordLabel.setForeground(new Color(239, 68, 68));
                    break;
                case "warning":
                    wordLabel.setForeground(new Color(251, 146, 60));
                    break;
                case "suggestion":
                    wordLabel.setForeground(ACCENT_GREEN);
                    break;
                case "info":
                    wordLabel.setForeground(ACCENT_BLUE);
                    break;
            }

            return this;
        }
    }
    private static class MetricCard extends JPanel {
        private JLabel valueLabel;

        public MetricCard(String title, String value, String icon, Color accentColor) {
            setLayout(new BorderLayout(10, 8));
            setBackground(BG_CARD);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BG_TERTIARY, 2),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)
            ));

            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            iconLabel.setForeground(accentColor);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel titleLabel = new JLabel(title.toUpperCase());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            titleLabel.setForeground(TEXT_SECONDARY);

            valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            valueLabel.setForeground(TEXT_PRIMARY);

            textPanel.add(titleLabel);
            textPanel.add(Box.createVerticalStrut(4));
            textPanel.add(valueLabel);

            add(iconLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        public void setValue(String value) {
            valueLabel.setText(value);
        }
    }
}