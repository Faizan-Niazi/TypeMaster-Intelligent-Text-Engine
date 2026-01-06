import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintReportPanel extends JPanel {

    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_TERTIARY = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);

    private JPanel contentPanel;

    public PrintReportPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PRIMARY);

        add(createHeader(), BorderLayout.NORTH);

        contentPanel = new JPanel();
        // Initial load
        refreshReport();

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG_PRIMARY); // Fix scroll background
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("📊 Performance Report");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24)); // Use Emoji font
        title.setForeground(TEXT_PRIMARY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = createButton("↻ Refresh", ACCENT_GREEN);
        refreshBtn.addActionListener(e -> refreshReport());

        JButton printBtn = createButton("🖨 Print Report", ACCENT_BLUE);
        printBtn.addActionListener(e -> printReport());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(printBtn);

        header.add(title, BorderLayout.WEST);
        header.add(buttonPanel, BorderLayout.EAST);

        return header;
    }

    private void refreshReport() {
        if (contentPanel == null) {
            contentPanel = new JPanel();
        }

        contentPanel.removeAll();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_PRIMARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        contentPanel.add(createReportHeader());
        contentPanel.add(Box.createVerticalStrut(30));

        // --- TYPING MASTER STATS ---
        boolean typingTested = TypingMasterPanel.hasTested;
        contentPanel.add(createSectionPanel("⌨️ Typing Master Statistics", new String[][]{
                {"Status:", typingTested ? "Tested" : "Not Tested", typingTested ? "✓" : "⚠"},
                {"Best WPM:", typingTested ? String.format("%.0f", TypingMasterPanel.globalBestWPM) : "0", "⚡"},
                {"Best Accuracy:", typingTested ? String.format("%.1f%%", TypingMasterPanel.globalBestAccuracy) : "0%", "🎯"},
                {"Total Tests:", String.valueOf(TypingMasterPanel.globalTotalTests), "🔢"}
        }));
        contentPanel.add(Box.createVerticalStrut(20));

        // --- PERFORMANCE DASHBOARD STATS ---
        boolean perfTested = PerformanceDashboardPanel.hasTested;
        contentPanel.add(createSectionPanel("📊 Performance Dashboard", new String[][]{
                {"Status:", perfTested ? "Tested" : "Not Tested", perfTested ? "✓" : "⚠"},
                {"Best WPM:", perfTested ? String.format("%.0f", PerformanceDashboardPanel.globalBestWPM) : "0", "⚡"},
                {"Total Words:", String.valueOf(PerformanceDashboardPanel.globalTotalWords), "📝"},
                {"Invalid Words:", String.valueOf(PerformanceDashboardPanel.globalInvalidWords), "✖"}
        }));
        contentPanel.add(Box.createVerticalStrut(20));

        // --- LIVE SPELL CHECKER STATS ---
        // NOTE: Ensure these static variables exist in your LiveSpellCheckerPanel class!
        boolean spellTested = LiveSpellCheckerPanel.hasTested;
        contentPanel.add(createSectionPanel("✓ Live Spell Checker", new String[][]{
                {"Status:", spellTested ? "Tested" : "Not Tested", spellTested ? "✓" : "⚠"},
                {"Total Words Checked:", String.valueOf(LiveSpellCheckerPanel.globalTotalWordsChecked), "📄"},
                {"Errors Found:", String.valueOf(LiveSpellCheckerPanel.globalErrorsFound), "✖"},
                {"Corrections Made:", String.valueOf(LiveSpellCheckerPanel.globalCorrectionsMade), "🔧"}
        }));
        contentPanel.add(Box.createVerticalStrut(20));

        // --- DICTIONARY & PREDICTOR STATS ---
        boolean dictTested = DictionaryLookupPanel.hasTested;
        boolean predTested = SmartSuggestionsPanel.hasTested;
        contentPanel.add(createSectionPanel("📚 Dictionary & AI Predictor", new String[][]{
                {"Status:", (dictTested || predTested) ? "Tested" : "Not Tested", (dictTested || predTested) ? "✓" : "⚠"},
                {"Dictionary Searches:", String.valueOf(DictionaryLookupPanel.globalTotalSearches), "🔍"},
                {"Predictions Made:", String.valueOf(SmartSuggestionsPanel.globalTotalPredictions), "🔮"},
                {"Words Found:", String.valueOf(SmartSuggestionsPanel.globalTotalWords), "💡"}
        }));

        contentPanel.add(Box.createVerticalGlue());

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createReportHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy • HH:mm");
        String dateTime = sdf.format(new Date());

        JLabel reportTitle = new JLabel("Performance Report");
        reportTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        reportTitle.setForeground(TEXT_PRIMARY);
        reportTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("Generated: " + dateTime);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(reportTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(dateLabel);

        return panel;
    }

    private JPanel createSectionPanel(String title, String[][] data) {
        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setBackground(BG_CARD);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_TERTIARY, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16)); // Emoji font for title
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel dataPanel = new JPanel(new GridLayout(data.length, 1, 0, 12));
        dataPanel.setOpaque(false);

        for (String[] row : data) {
            JPanel rowPanel = new JPanel(new BorderLayout(15, 0));
            rowPanel.setOpaque(false);

            // FIX: Use Segoe UI Emoji and set Color to WHITE
            JLabel iconLabel = new JLabel(row[2]);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            iconLabel.setForeground(Color.WHITE); // Make icons white
            iconLabel.setPreferredSize(new Dimension(25, 25)); // Fixed width for alignment

            JLabel keyLabel = new JLabel(row[0]);
            keyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            keyLabel.setForeground(TEXT_SECONDARY);

            JLabel valueLabel = new JLabel(row[1]);
            valueLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
            valueLabel.setForeground(TEXT_PRIMARY);
            valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftPanel.setOpaque(false);
            leftPanel.add(iconLabel);
            leftPanel.add(keyLabel);

            rowPanel.add(leftPanel, BorderLayout.WEST);
            rowPanel.add(valueLabel, BorderLayout.EAST);

            dataPanel.add(rowPanel);
        }

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(dataPanel, BorderLayout.CENTER);

        return section;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));

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

    private void printReport() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((g, pf, page) -> {
            if (page > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());

            // improved print font
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.setColor(Color.BLACK);

            int y = 50;
            g2d.drawString("LINGUISTIC TOOLKIT PRO - REPORT", 50, y);
            y += 25;

            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.drawString("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()), 50, y);
            y += 40;

            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            // Helper to print sections
            y = printSection(g2d, "Typing Master", y, new String[]{
                    "Best WPM: " + (TypingMasterPanel.hasTested ? TypingMasterPanel.globalBestWPM : "N/A"),
                    "Best Acc: " + (TypingMasterPanel.hasTested ? TypingMasterPanel.globalBestAccuracy + "%" : "N/A")
            });

            y = printSection(g2d, "Performance Dashboard", y, new String[]{
                    "Best WPM: " + (PerformanceDashboardPanel.hasTested ? PerformanceDashboardPanel.globalBestWPM : "N/A"),
                    "Invalid Words: " + PerformanceDashboardPanel.globalInvalidWords
            });

            y = printSection(g2d, "Spell Checker", y, new String[]{
                    "Words Checked: " + LiveSpellCheckerPanel.globalTotalWordsChecked,
                    "Errors Found: " + LiveSpellCheckerPanel.globalErrorsFound
            });

            return Printable.PAGE_EXISTS;
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Print Error: " + e.getMessage());
            }
        }
    }

    // Helper for cleaner printing code
    private int printSection(Graphics2D g2d, String title, int y, String[] lines) {
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.drawString("--- " + title + " ---", 50, y);
        y += 20;
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        for(String line : lines) {
            g2d.drawString(line, 60, y);
            y += 15;
        }
        return y + 20; // Extra spacing after section
    }
}