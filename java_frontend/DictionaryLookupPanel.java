`import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CompletableFuture;

public class DictionaryLookupPanel extends JPanel {
    public static int globalTotalSearches = 0;
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

    private JTextField searchField;
    private JTextPane resultPane;
    private JLabel statusLabel;
    private JButton searchBtn;
    private JPanel statsPanel;
    private MetricCard totalSearchesCard;
    private MetricCard cachedWordsCard;
    private int totalSearches = 0;
    private int cachedWords = 0;

    public DictionaryLookupPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PRIMARY);

        // Top Header
        JPanel header = createDashboard();
        add(header, BorderLayout.NORTH);

        // Center - Search & Results
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BG_PRIMARY);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        JPanel searchPanel = createSearchPanel();
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel resultsPanel = createResultsPanel();
        centerPanel.add(resultsPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);
    }


    private JPanel createDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 20));
        dashboard.setBackground(BG_PRIMARY);
        dashboard.setBorder(BorderFactory.createEmptyBorder(25, 25, 15, 25));

        // Title Section
        JLabel title = new JLabel("📚 Dictionary Explorer");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);

        // Stats Grid
        statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);

        totalSearchesCard = new MetricCard("Searches", "0", "🔍", ACCENT_BLUE);
        cachedWordsCard = new MetricCard("Cached", "0", "💾", ACCENT_GREEN);
        MetricCard avgTimeCard = new MetricCard("Avg Time", "< 1s", "⚡", ACCENT_PURPLE);
        MetricCard successCard = new MetricCard("Success", "100%", "✓", ACCENT_GREEN);

        statsPanel.add(totalSearchesCard);
        statsPanel.add(cachedWordsCard);
        statsPanel.add(avgTimeCard);
        statsPanel.add(successCard);

        dashboard.add(title, BorderLayout.NORTH);
        dashboard.add(statsPanel, BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchField.setBackground(BG_CARD);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(ACCENT_BLUE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_TERTIARY, 2),
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        // Placeholder effect
        searchField.setText("Type a word to explore...");
        searchField.setForeground(TEXT_SECONDARY);

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Type a word to explore...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Type a word to explore...");
                    searchField.setForeground(TEXT_SECONDARY);
                }
            }
        });

        searchField.addActionListener(e -> searchWord());

        searchBtn = createModernButton("Search", ACCENT_BLUE, "🔍");
        searchBtn.addActionListener(e -> searchWord());

        panel.add(searchField, BorderLayout.CENTER);
        panel.add(searchBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        resultPane = new JTextPane();
        resultPane.setEditable(false);
        resultPane.setContentType("text/html");
        resultPane.setBackground(BG_CARD);
        resultPane.setForeground(TEXT_PRIMARY);
        resultPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resultPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Initial message
        setStyledText("<div style='font-family: Segoe UI; color: #94a3b8; text-align: center; padding: 60px 40px;'>" +
                "<div style='font-size: 64px; margin-bottom: 20px;'>📖</div>" +
                "<h2 style='color: #f1f5f9; margin: 0 0 10px 0;'>Welcome to Dictionary Explorer</h2>" +
                "<p style='font-size: 15px; line-height: 1.6;'>Enter any word above to discover its meaning, usage, and more.<br>" +
                "Get instant definitions powered by our comprehensive dictionary.</p>" +
                "</div>");

        JScrollPane scroll = new JScrollPane(resultPane);
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

        statusLabel = new JLabel("Ready to search");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_SECONDARY);

        JLabel tip = new JLabel("💡 Tip: Press Enter to search quickly");
        tip.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
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

    private void searchWord() {
        String word = searchField.getText().trim();
        if (word.isEmpty() || word.equals("Type a word to explore...")) {
            return;
        }

        searchBtn.setEnabled(false);
        searchBtn.setText("⏳ Searching...");
        statusLabel.setText("Searching for '" + word + "'...");
        statusLabel.setForeground(ACCENT_BLUE);

        setStyledText("<div style='font-family: Segoe UI; color: #94a3b8; text-align: center; padding: 60px;'>" +
                "<div style='font-size: 48px;'>⏳</div>" +
                "<h3 style='color: #3b82f6;'>Searching...</h3>" +
                "</div>");

        CompletableFuture.supplyAsync(() -> ModernMainFrame.CLIENT.send("DEFINE:" + word))
                .thenAccept(resp -> SwingUtilities.invokeLater(() -> {
                    searchBtn.setEnabled(true);
                    searchBtn.setText("🔍  Search");

                    totalSearches++;
                    totalSearchesCard.setValue(String.valueOf(totalSearches));

                    if ("ERR".equals(resp) || resp.isEmpty()) {
                        displayError("Connection Error", "Could not connect to the dictionary server.");
                        statusLabel.setText("Connection error");
                        statusLabel.setForeground(new Color(239, 68, 68));
                    } else if (resp.startsWith("DEF:NOTFOUND")) {
                        displayNotFound(word);
                        statusLabel.setText("Word not found");
                        statusLabel.setForeground(new Color(251, 146, 60));
                    } else {
                        String def = resp.replaceFirst("^DEF:", "");
                        displayDefinition(word, def);
                        cachedWords++;
                        cachedWordsCard.setValue(String.valueOf(cachedWords));
                        statusLabel.setText("Definition loaded successfully");
                        statusLabel.setForeground(ACCENT_GREEN);
                        globalTotalSearches = totalSearches;
                        hasTested = true;
                    }

                }));
    }

    private void displayDefinition(String word, String definition) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Segoe UI; background: #1e293b; color: #f1f5f9; padding: 20px;'>");

        // Word Header
        html.append("<div style='border-bottom: 2px solid #334155; padding-bottom: 20px; margin-bottom: 25px;'>");
        html.append("<h1 style='color: #3b82f6; font-size: 36px; margin: 0 0 10px 0;'>").append(word).append("</h1>");
        html.append("<p style='color: #94a3b8; font-size: 14px; margin: 0;'>📚 Dictionary Definition</p>");
        html.append("</div>");

        // Parse and format definitions
        String formattedDef = formatDefinition(definition);
        html.append(formattedDef);

        html.append("</body></html>");
        setStyledText(html.toString());
    }

    private String formatDefinition(String def) {
        return def.replace("(n.)", "<div style='margin: 20px 0;'><span style='background: #334155; color: #3b82f6; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold;'>NOUN</span><p style='margin: 12px 0 0 0; line-height: 1.8; color: #e2e8f0;'>")
                .replace("(v.)", "</p></div><div style='margin: 20px 0;'><span style='background: #334155; color: #22c55e; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold;'>VERB</span><p style='margin: 12px 0 0 0; line-height: 1.8; color: #e2e8f0;'>")
                .replace("(adj.)", "</p></div><div style='margin: 20px 0;'><span style='background: #334155; color: #a855f7; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold;'>ADJECTIVE</span><p style='margin: 12px 0 0 0; line-height: 1.8; color: #e2e8f0;'>")
                .replace("(adv.)", "</p></div><div style='margin: 20px 0;'><span style='background: #334155; color: #fb923c; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold;'>ADVERB</span><p style='margin: 12px 0 0 0; line-height: 1.8; color: #e2e8f0;'>")
                .replace("(v. t.)", "</p></div><div style='margin: 20px 0;'><span style='background: #334155; color: #22c55e; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold;'>TRANSITIVE VERB</span><p style='margin: 12px 0 0 0; line-height: 1.8; color: #e2e8f0;'>")
                .replace("(v. i.)", "</p></div><div style='margin: 20px 0;'><span style='background: #334155; color: #22c55e; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold;'>INTRANSITIVE VERB</span><p style='margin: 12px 0 0 0; line-height: 1.8; color: #e2e8f0;'>")
                + "</p></div>";
    }

    private void displayNotFound(String word) {
        String html = "<html><body style='font-family: Segoe UI; background: #1e293b; color: #f1f5f9; padding: 40px; text-align: center;'>" +
                "<div style='font-size: 64px; margin-bottom: 20px;'>❓</div>" +
                "<h2 style='color: #fb923c; margin: 0 0 15px 0;'>Word Not Found</h2>" +
                "<p style='color: #94a3b8; font-size: 15px; line-height: 1.6;'>" +
                "The word <strong style='color: #f1f5f9;'>\"" + word + "\"</strong> was not found in our dictionary.<br>" +
                "Please check the spelling or try a different word.</p>" +
                "</body></html>";
        setStyledText(html);
    }

    private void displayError(String title, String message) {
        String html = "<html><body style='font-family: Segoe UI; background: #1e293b; color: #f1f5f9; padding: 40px; text-align: center;'>" +
                "<div style='font-size: 64px; margin-bottom: 20px;'>⚠️</div>" +
                "<h2 style='color: #ef4444; margin: 0 0 15px 0;'>" + title + "</h2>" +
                "<p style='color: #94a3b8; font-size: 15px; line-height: 1.6;'>" + message + "</p>" +
                "</body></html>";
        setStyledText(html);
    }

    private void setStyledText(String html) {
        resultPane.setText(html);
        resultPane.setCaretPosition(0);
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