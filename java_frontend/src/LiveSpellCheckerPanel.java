import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Future;

public class LiveSpellCheckerPanel extends JPanel {

    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color BG_TERTIARY = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);

    private JTextPane editor;
    private JLabel statusLabel;
    private JLabel wordCountLabel;
    private JLabel errorCountLabel;
    private JWindow popupWindow;
    private JPanel suggestionPopup;
    private UnderlineHighlighterPainter errorUnderline;
    private Map<Object, HighlightInfo> highlights = new HashMap<>();
    private javax.swing.Timer debounceTimer;
    private int errorCount = 0;

    // Global metrics for reporting
    public static int globalTotalWordsChecked = 0;
    public static int globalErrorsFound = 0;
    public static int globalCorrectionsMade = 0;
    public static boolean hasTested = false;

    private static class HighlightInfo {
        final int start, end;
        final Object tag;
        HighlightInfo(int s, int e, Object t) { start = s; end = e; tag = t; }
    }

    private static class WordSpan {
        final String word;
        final int start, end, length;
        WordSpan(String w, int s, int e) {
            word = w; start = s; end = e; length = e - s;
        }
    }

    public LiveSpellCheckerPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PRIMARY);

        errorUnderline = new UnderlineHighlighterPainter(ERROR_COLOR);

        add(createHeader(), BorderLayout.NORTH);
        add(createEditorPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        setupSuggestionPopup();

        debounceTimer = new javax.swing.Timer(300, e -> checkAndMark());
        debounceTimer.setRepeats(false);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("✓ Live Spell Checker");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlPanel.setOpaque(false);

        JButton clearBtn = createButton("🗑 Clear", new Color(71, 85, 105));
        clearBtn.addActionListener(e -> {
            editor.setText("");
            clearHighlights();
        });

        JButton checkBtn = createButton("✓ Check Now", ACCENT_BLUE);
        checkBtn.addActionListener(e -> checkAndMark());

        controlPanel.add(checkBtn);
        controlPanel.add(clearBtn);

        header.add(title, BorderLayout.WEST);
        header.add(controlPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel instruction = new JLabel("📝 Start typing - errors will be underlined in red. Click them for suggestions.");
        instruction.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        instruction.setForeground(TEXT_SECONDARY);

        editor = new JTextPane();
        editor.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        editor.setBackground(BG_CARD);
        editor.setForeground(TEXT_PRIMARY);
        editor.setCaretColor(ACCENT_BLUE);
        editor.setMargin(new Insets(25, 25, 25, 25));
        editor.setSelectionColor(new Color(59, 130, 246, 50));
        editor.setSelectedTextColor(TEXT_PRIMARY);

        JScrollPane scroll = new JScrollPane(editor);
        scroll.setBorder(BorderFactory.createLineBorder(BG_TERTIARY, 2));
        scroll.setPreferredSize(new Dimension(0, 400));

        editor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                debounceTimer.restart();
                updateWordCount();
                hasTested = true;
            }
            public void removeUpdate(DocumentEvent e) {
                debounceTimer.restart();
                updateWordCount();
            }
            public void changedUpdate(DocumentEvent e) {}
        });

        editor.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int pos = editor.viewToModel2D(e.getPoint());
                for (HighlightInfo info : highlights.values()) {
                    if (pos >= info.start && pos <= info.end) {
                        String wrongWord = getWordAt(info.start, info.end);
                        showSuggestions(info.start, info.end, wrongWord, e);
                        return;
                    }
                }
                if (popupWindow != null) {
                    popupWindow.setVisible(false);
                }
            }
        });

        panel.add(instruction, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(20, 0));
        bar.setBackground(BG_CARD);
        bar.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);

        statusLabel = new JLabel("Ready to check");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(TEXT_SECONDARY);

        errorCountLabel = new JLabel("No errors");
        errorCountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        errorCountLabel.setForeground(ACCENT_GREEN);

        leftPanel.add(statusLabel);
        leftPanel.add(createDot());
        leftPanel.add(errorCountLabel);

        wordCountLabel = new JLabel("0 words • 0 characters");
        wordCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        wordCountLabel.setForeground(TEXT_SECONDARY);

        bar.add(leftPanel, BorderLayout.WEST);
        bar.add(wordCountLabel, BorderLayout.EAST);

        return bar;
    }

    private JLabel createDot() {
        JLabel dot = new JLabel("•");
        dot.setForeground(new Color(71, 85, 105));
        return dot;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 40));

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

    private void setupSuggestionPopup() {
        popupWindow = new JWindow();
        popupWindow.setAlwaysOnTop(true);
        popupWindow.setFocusable(false);

        suggestionPopup = new JPanel();
        suggestionPopup.setLayout(new BoxLayout(suggestionPopup, BoxLayout.Y_AXIS));
        suggestionPopup.setBackground(BG_TERTIARY);
        suggestionPopup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));

        popupWindow.add(suggestionPopup);
    }

    private void updateWordCount() {
        String text = editor.getText();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int chars = text.length();
        wordCountLabel.setText(String.format("%d words • %d characters", words, chars));

        globalTotalWordsChecked = Math.max(globalTotalWordsChecked, words);
    }

    private void checkAndMark() {
        String text = editor.getText();
        if (text.isEmpty()) return;

        List<WordSpan> words = tokenize(text);
        clearHighlights();

        statusLabel.setText("Checking...");
        statusLabel.setForeground(ACCENT_BLUE);
        errorCount = 0;

        List<Future<String>> futures = new ArrayList<>();
        for (WordSpan w : words) {
            futures.add(ModernMainFrame.CLIENT.sendAsync("CHECK:" + w.word));
        }

        new Thread(() -> {
            for (int i = 0; i < words.size(); i++) {
                WordSpan w = words.get(i);
                try {
                    String resp = futures.get(i).get();
                    boolean invalid = !(resp.contains("SPELL:VALID") || resp.contains("SPELL:PREFIX"));
                    if (invalid && w.length > 0) {
                        errorCount++;
                        SwingUtilities.invokeLater(() -> underline(w.start, w.end));
                    }
                } catch (Exception ignored) {}
            }

            SwingUtilities.invokeLater(() -> {
                if (errorCount == 0) {
                    statusLabel.setText("All good!");
                    statusLabel.setForeground(ACCENT_GREEN);
                    errorCountLabel.setText("No errors found");
                    errorCountLabel.setForeground(ACCENT_GREEN);
                } else {
                    statusLabel.setText("Check complete");
                    statusLabel.setForeground(TEXT_SECONDARY);
                    errorCountLabel.setText(errorCount + " error" + (errorCount > 1 ? "s" : ""));
                    errorCountLabel.setForeground(ERROR_COLOR);
                }

                globalErrorsFound = Math.max(globalErrorsFound, errorCount);
            });
        }).start();
    }

    private void underline(int start, int end) {
        try {
            Highlighter hl = editor.getHighlighter();
            Object tag = hl.addHighlight(start, end, errorUnderline);
            highlights.put(tag, new HighlightInfo(start, end, tag));
        } catch (BadLocationException ignored) {}
    }

    private void clearHighlights() {
        Highlighter hl = editor.getHighlighter();
        for (HighlightInfo info : highlights.values()) {
            hl.removeHighlight(info.tag);
        }
        highlights.clear();
        errorCount = 0;
        errorCountLabel.setText("No errors");
        errorCountLabel.setForeground(ACCENT_GREEN);
    }

    private List<WordSpan> tokenize(String text) {
        List<WordSpan> list = new ArrayList<>();
        int i = 0, n = text.length();
        while (i < n) {
            while (i < n && !Character.isLetter(text.charAt(i))) i++;
            int start = i;
            while (i < n && Character.isLetter(text.charAt(i))) i++;
            int end = i;
            if (end > start) {
                list.add(new WordSpan(text.substring(start, end), start, end));
            }
        }
        return list;
    }

    private String getWordAt(int start, int end) {
        try {
            return editor.getText(start, end - start);
        } catch (BadLocationException e) {
            return "";
        }
    }

    private void showSuggestions(int start, int end, String wrongWord, MouseEvent clickEvent) {
        Future<String> pred = ModernMainFrame.CLIENT.sendAsync("PREDICT:" + wrongWord);

        new Thread(() -> {
            try {
                String resp = pred.get();
                String csv = resp.replace("PRED:", "").trim();
                if (!csv.isEmpty()) {
                    SwingUtilities.invokeLater(() -> displayPopup(start, end, csv.split(","), clickEvent));
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private void displayPopup(int start, int end, String[] suggestions, MouseEvent clickEvent) {
        suggestionPopup.removeAll();

        JPanel header = new JPanel();
        header.setBackground(BG_TERTIARY);
        header.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        header.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel headerLabel = new JLabel("Suggestions");
        headerLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        headerLabel.setForeground(TEXT_SECONDARY);
        header.add(headerLabel);

        suggestionPopup.add(header);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(300, 1));
        suggestionPopup.add(sep);

        for (int i = 0; i < Math.min(suggestions.length, 5); i++) {
            String suggestion = suggestions[i].trim();
            JPanel item = createSuggestionItem(suggestion, start, end);
            suggestionPopup.add(item);
        }

        try {
            Rectangle rect = editor.modelToView(start);
            Point editorLocation = editor.getLocationOnScreen();

            popupWindow.pack();
            popupWindow.setLocation(
                    editorLocation.x + rect.x,
                    editorLocation.y + rect.y + rect.height + 5
            );
            popupWindow.setVisible(true);
        } catch (Exception ex) {}
    }

    private JPanel createSuggestionItem(String suggestion, int start, int end) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(BG_TERTIARY);
        item.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        item.setMaximumSize(new Dimension(300, 40));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(suggestion);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
        item.add(label, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(51, 65, 85, 180));
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(BG_TERTIARY);
            }
            public void mouseClicked(MouseEvent e) {
                try {
                    editor.getDocument().remove(start, end - start);
                    editor.getDocument().insertString(start, suggestion, null);
                    popupWindow.setVisible(false);
                    globalCorrectionsMade++;
                    checkAndMark();
                } catch (BadLocationException ex) {}
            }
        });

        return item;
    }
}