import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ModernMainFrame extends JFrame {
//sd
    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_SECONDARY = new Color(30, 41, 59);
    private static final Color BG_TERTIARY = new Color(51, 65, 85);
    private static final Color SIDEBAR_BG = new Color(20, 27, 45);
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ERROR_RED = new Color(239, 68, 68);

    public static NetworkClient CLIENT;
    private JPanel contentPanel;
    private java.util.List<NavButton> navButtons = new ArrayList<>();
    private JLabel statusDot;
    private JLabel statusText;
    private javax.swing.Timer connectionCheckTimer;

    public ModernMainFrame() {
        setTitle("TypeMaster Intelligent Text Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   //     setSize(1500, 950);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        CLIENT = new NetworkClient("127.0.0.1", 8080);

        JPanel mainContainer = new JPanel(new BorderLayout(0, 0));
        mainContainer.setBackground(BG_PRIMARY);

        mainContainer.add(createPremiumSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_PRIMARY);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);

        showPanel(new TypingMasterPanel());

        // Start connection monitoring
        startConnectionMonitoring();

        // Initial connection check
        checkConnection();
    }

    private void startConnectionMonitoring() {
        connectionCheckTimer = new javax.swing.Timer(2000, e -> checkConnection());
        connectionCheckTimer.start();
    }

    private void checkConnection() {
        new Thread(() -> {
            boolean connected = CLIENT.testConnection();
            SwingUtilities.invokeLater(() -> updateConnectionStatus(connected));
        }).start();
    }

    private void updateConnectionStatus(boolean connected) {
        if (connected) {
            statusDot.setForeground(ACCENT_GREEN);
            statusText.setText("Connected");
            statusText.setForeground(TEXT_SECONDARY);
        } else {
            statusDot.setForeground(ERROR_RED);
            statusText.setText("Disconnected");
            statusText.setForeground(ERROR_RED);
        }
    }

    private JPanel createPremiumSidebar() {
        // 1. Create the container panel that holds the buttons
        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBackground(SIDEBAR_BG);

        // 2. Add Content with reduced spacing (Changed Struts from 30/20 to 15/10)
        sidebarContent.add(createSidebarHeader());
        sidebarContent.add(Box.createVerticalStrut(10)); // Reduced from 30

        sidebarContent.add(createSectionLabel("TYPING TOOLS"));
        sidebarContent.add(createNavButton("⌨", "Typing Master", "Practice with sample texts",
                e -> showPanel(new TypingMasterPanel())));
        sidebarContent.add(createNavButton("📊", "Performance", "Real-time typing analytics",
                e -> showPanel(new PerformanceDashboardPanel())));

        sidebarContent.add(Box.createVerticalStrut(10)); // Reduced from 20
        sidebarContent.add(createSectionLabel("LANGUAGE TOOLS"));
        sidebarContent.add(createNavButton("📚", "Dictionary", "Word definitions & meanings",
                e -> showPanel(new DictionaryLookupPanel())));
        sidebarContent.add(createNavButton("🤖", "AI Predictor", "Smart word suggestions",
                e -> showPanel(new SmartSuggestionsPanel())));

        sidebarContent.add(Box.createVerticalStrut(10)); // Reduced from 20
        sidebarContent.add(createSectionLabel("SPELL CHECKER"));
        sidebarContent.add(createNavButton("✓", "Live Checker", "Check as you type",
                e -> showPanel(new LiveSpellCheckerPanel())));

        sidebarContent.add(Box.createVerticalStrut(10)); // Reduced from 20
        sidebarContent.add(createSectionLabel("REPORTS"));
        sidebarContent.add(createNavButton("📋", "Print Report", "View performance statistics",
                e -> showPanel(new PrintReportPanel())));

        sidebarContent.add(Box.createVerticalGlue());
        sidebarContent.add(createSidebarFooter());

        // 3. Wrap everything in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(sidebarContent);
        scrollPane.setBorder(null); // Remove standard border
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling

        // 4. Create a wrapper panel to hold the ScrollPane and apply the border
        JPanel sidebarWrapper = new JPanel(new BorderLayout());
        sidebarWrapper.setBackground(SIDEBAR_BG);
        sidebarWrapper.setPreferredSize(new Dimension(280, 0));
        sidebarWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BG_TERTIARY));
        sidebarWrapper.add(scrollPane, BorderLayout.CENTER);

        return sidebarWrapper;
    }
    private JPanel createSidebarHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(SIDEBAR_BG);
        header.setBorder(BorderFactory.createEmptyBorder(30, 25, 20, 25));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- CHANGE STARTED HERE ---
        // 1. Changed text to "⌨️" (Keyboard) or "⚡" (Bolt) - put whatever you want inside the quotes
        JLabel logo = new JLabel("⌨️");

        // 2. Set the color to WHITE
        logo.setForeground(Color.WHITE);

        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        // --- CHANGE ENDED HERE ---

        JLabel title = new JLabel("TypeMaster Engine");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Professional Edition");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(ACCENT_BLUE);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(10));
        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);

        return header;
    }
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(TEXT_SECONDARY);
        label.setBorder(BorderFactory.createEmptyBorder(10, 25, 8, 25));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(280, 30));
        return label;
    }

    private NavButton createNavButton(String icon, String title, String desc, java.awt.event.ActionListener action) {
        NavButton btn = new NavButton(icon, title, desc);
        btn.addActionListener(e -> {
            for (NavButton nb : navButtons) {
                nb.setSelected(false);
            }
            btn.setSelected(true);
            action.actionPerformed(e);
        });

        navButtons.add(btn);
        return btn;
    }

    private JPanel createSidebarFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(SIDEBAR_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusPanel.setOpaque(false);
        statusPanel.setMaximumSize(new Dimension(280, 30));

        statusDot = new JLabel("●");
        statusDot.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusDot.setForeground(ACCENT_GREEN);

        statusText = new JLabel("Connected");
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusText.setForeground(TEXT_SECONDARY);

        statusPanel.add(statusDot);
        statusPanel.add(statusText);

        JLabel version = new JLabel("v2.0.0 • Premium");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(new Color(100, 116, 139));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);

        footer.add(statusPanel);
        footer.add(Box.createVerticalStrut(8));
        footer.add(version);

        return footer;
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private class NavButton extends JPanel {
        private boolean selected = false;
        private String icon, title, description;
        private java.util.List<java.awt.event.ActionListener> listeners = new ArrayList<>();

        public NavButton(String icon, String title, String description) {
            this.icon = icon;
            this.title = title;
            this.description = description;

            setLayout(new BorderLayout(0, 0));
            setBackground(SIDEBAR_BG);
            setMaximumSize(new Dimension(280, 70));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 25));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // FIX: Changed 50 to 35. This reduces the gap between icon and text.
            JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            iconLabel.setForeground(TEXT_PRIMARY);
            iconLabel.setPreferredSize(new Dimension(35, 0));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
            titleLabel.setForeground(TEXT_PRIMARY);

            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            descLabel.setForeground(TEXT_SECONDARY);

            textPanel.add(titleLabel);
            textPanel.add(Box.createVerticalStrut(2));
            textPanel.add(descLabel);

            add(iconLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!selected) {
                        setBackground(BG_SECONDARY);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!selected) {
                        setBackground(SIDEBAR_BG);
                        repaint();
                    }
                }

                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    for (java.awt.event.ActionListener listener : listeners) {
                        listener.actionPerformed(new java.awt.event.ActionEvent(this, 0, ""));
                    }
                }
            });
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            if (selected) {
                setBackground(BG_TERTIARY);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, ACCENT_BLUE),
                        BorderFactory.createEmptyBorder(12, 12, 12, 25)
                ));
            } else {
                setBackground(SIDEBAR_BG);
                setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 25));
            }
        }

        public void addActionListener(java.awt.event.ActionListener listener) {
            listeners.add(listener);
        }
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ModernMainFrame frame = new ModernMainFrame();
            frame.setVisible(true);
        });
    }
}