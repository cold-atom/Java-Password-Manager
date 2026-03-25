import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class PasswordVault extends JFrame {

    private final Color BG_MAIN     = Color.decode("#121212");
    private final Color BG_SIDEBAR  = Color.decode("#1e1e1e");
    private final Color BG_TOOLBAR  = Color.decode("#2d2d30");
    private final Color ACCENT      = Color.decode("#00e676");
    private final Color TEXT_WHITE  = Color.decode("#ffffff");
    private final Color TEXT_GRAY   = Color.decode("#b0b0b0");
    private final Color FIELD_BG    = Color.decode("#333333");
    private final Color HOVER_BG    = Color.decode("#3e3e42");
    
    private List<PasswordEntry> entries;
    private JPanel listPanel;
    private JPanel detailPanel;
    private PasswordEntry selectedEntry;
    private int selectedIndex = -1;
    private String searchQuery = "";

    public PasswordVault() {
        setTitle("Password Manager");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        entries = new ArrayList<>();
        loadEntriesFromCsv();
        
        if (!entries.isEmpty()) {
            selectedEntry = entries.get(0);
            selectedIndex = 0;
        }

        add(createTopToolbar(), BorderLayout.NORTH);
        add(createLeftSidebar(), BorderLayout.WEST);
        add(createDetailPanel(), BorderLayout.CENTER);
    }

    // top toolbar
    private JPanel createTopToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(BG_TOOLBAR);
        toolbar.setPreferredSize(new Dimension(0, 60));
        toolbar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        actions.add(createToolButton("＋ Add", ACCENT, Color.BLACK, e -> showAddEntryDialog()));
        actions.add(createToolButton("✎ Edit", BG_MAIN, TEXT_WHITE, e -> showEditEntryDialog()));
        actions.add(createToolButton("✖ Delete", BG_MAIN, Color.decode("#ff5252"), e -> showDeleteConfirmation())); // Red text for delete

        // search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(" Search entries...");
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBackground(FIELD_BG);
        searchField.setForeground(TEXT_WHITE);
        searchField.setCaretColor(ACCENT);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals(" Search entries...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(" Search entries...");
                    searchField.setForeground(TEXT_GRAY);
                }
            }
        });
        
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }
            private void updateSearch() {
                String text = searchField.getText();
                if (!text.equals(" Search entries...")) {
                    searchQuery = text.toLowerCase();
                    refreshEntryList();
                }
            }
        });

        searchPanel.add(searchField);

        toolbar.add(actions, BorderLayout.WEST);
        toolbar.add(searchPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JButton createToolButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createToolButton(String text, Color bg, Color fg, ActionListener listener) {
        JButton btn = createToolButton(text, bg, fg);
        btn.addActionListener(listener);
        return btn;
    }

    private JPanel createLeftSidebar() {
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_SIDEBAR);
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        refreshEntryList();

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(300, 0));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(80, 80, 80);
                this.trackColor = BG_SIDEBAR;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, java.awt.Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 10, 10);
                g2.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, java.awt.Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });
        
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BG_SIDEBAR);
        containerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(60, 60, 60)));
        
        containerPanel.add(scroll, BorderLayout.CENTER);
        
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(BG_SIDEBAR);
        footerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JPanel dividerContainer = new JPanel();
        dividerContainer.setLayout(new BoxLayout(dividerContainer, BoxLayout.X_AXIS));
        dividerContainer.setOpaque(false);
        dividerContainer.add(Box.createHorizontalStrut(40));
        
        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(60, 60, 60));
        divider.setBackground(new Color(60, 60, 60));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        
        dividerContainer.add(divider);
        dividerContainer.add(Box.createHorizontalStrut(40));
        
        JLabel creditLabel = new JLabel("Original Author: cold-atom");
        creditLabel.setForeground(TEXT_GRAY);
        creditLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        creditLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        creditLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
        
        footerPanel.add(dividerContainer);
        footerPanel.add(creditLabel);
        
        containerPanel.add(footerPanel, BorderLayout.SOUTH);
        
        return containerPanel;
    }

    private JPanel createListItem(String title, String subtitle, boolean isActive) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(isActive ? new Color(45, 45, 48) : BG_SIDEBAR);
        item.setMaximumSize(new Dimension(280, 60));
        item.setPreferredSize(new Dimension(280, 60));
        item.setBorder(new EmptyBorder(10, 15, 10, 10));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if(isActive) {
            JPanel strip = new JPanel();
            strip.setBackground(ACCENT);
            strip.setPreferredSize(new Dimension(4, 0));
            item.add(strip, BorderLayout.WEST);
        }

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(TEXT_WHITE);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setForeground(TEXT_GRAY);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        textPanel.add(titleLbl);
        textPanel.add(subLbl);
        item.add(textPanel, BorderLayout.CENTER);

        return item;
    }

    private JPanel createDetailPanel() {
        detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(BG_MAIN);
        detailPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        
        updateDetailPanel();
        
        return detailPanel;
    }
    
    private void updateDetailPanel() {
        detailPanel.removeAll();
        
        if (selectedEntry == null) {
            JLabel noSelectionLabel = new JLabel("Select an entry to view details");
            noSelectionLabel.setForeground(TEXT_GRAY);
            noSelectionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            detailPanel.add(noSelectionLabel);
            detailPanel.revalidate();
            detailPanel.repaint();
            return;
        }

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(1000, 80));

        RoundedPanel iconBox = new RoundedPanel(64, 20);
        iconBox.setBackground(getColorForEntry(selectedEntry.getTitle()));
        iconBox.setPreferredSize(new Dimension(64, 64));
        iconBox.setLayout(new GridBagLayout());

        String firstLetter = selectedEntry.getTitle().isEmpty() ? "?" : 
                            String.valueOf(selectedEntry.getTitle().charAt(0)).toUpperCase();
        JLabel iconLetter = new JLabel(firstLetter);
        iconLetter.setForeground(Color.WHITE);
        iconLetter.setFont(new Font("Segoe UI", Font.BOLD, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        iconBox.add(iconLetter, gbc);

        JLabel title = new JLabel(selectedEntry.getTitle());
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));

        header.add(iconBox);
        header.add(title);

        detailPanel.add(header);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        detailPanel.add(createLabel("USERNAME / EMAIL"));
        detailPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailPanel.add(createDarkField(selectedEntry.getUsername(), false));

        detailPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        detailPanel.add(createLabel("PASSWORD"));
        detailPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailPanel.add(createDarkField(selectedEntry.getPassword(), true));

        detailPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel strengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        strengthPanel.setOpaque(false);
        strengthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        PasswordStrength strength = calculatePasswordStrength(selectedEntry.getPassword());
        JLabel strengthLbl = new JLabel("Strength: " + strength.label + " ");
        strengthLbl.setForeground(strength.color);

        JPanel strengthBar = new JPanel();
        strengthBar.setPreferredSize(new Dimension(150, 4));
        strengthBar.setBackground(strength.color);

        strengthPanel.add(strengthLbl);
        strengthPanel.add(strengthBar);
        detailPanel.add(strengthPanel);

        detailPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        detailPanel.add(createLabel("WEBSITE URL"));
        detailPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailPanel.add(createDarkField(selectedEntry.getUrl().isEmpty() ? "Not specified" : selectedEntry.getUrl(), false));

        detailPanel.add(Box.createVerticalGlue());
        
        detailPanel.revalidate();
        detailPanel.repaint();
    }
    
    private Color getColorForEntry(String title) {
        Color[] colors = {
            Color.decode("#4285F4"),
            Color.decode("#EA4335"),
            Color.decode("#FBBC05"),
            Color.decode("#34A853"),
            Color.decode("#FF6D00"),
            Color.decode("#9C27B0"),
            Color.decode("#00BCD4"),
            Color.decode("#E91E63"),
            Color.decode("#009688"),
            Color.decode("#FF5722")
        };
        
        int hash = Math.abs(title.hashCode());
        return colors[hash % colors.length];
    }
    
    private PasswordStrength calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return new PasswordStrength("Very Weak", Color.decode("#d32f2f"));
        }
        
        int score = 0;
        int length = password.length();
        
        if (length >= 8) score++;
        if (length >= 12) score++;
        if (length >= 16) score++;
        
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;
        
        boolean hasConsecutive = false;
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i) == password.charAt(i + 2)) {
                hasConsecutive = true;
                break;
            }
        }
        if (hasConsecutive) score--;
        
        if (score <= 2) {
            return new PasswordStrength("Weak", Color.decode("#ff5252"));
        } else if (score <= 4) {
            return new PasswordStrength("Fair", Color.decode("#ff9800"));
        } else if (score <= 6) {
            return new PasswordStrength("Good", Color.decode("#FBBC05"));
        } else {
            return new PasswordStrength("Excellent", Color.decode("#00e676"));
        }
    }
    
    private static class PasswordStrength {
        String label;
        Color color;
        
        PasswordStrength(String label, Color color) {
            this.label = label;
            this.color = color;
        }
    }

    private void showAddEntryDialog() {
        EntryDialog dialog = new EntryDialog(this, null);
        dialog.setVisible(true);
        
        PasswordEntry newEntry = dialog.getResult();
        if (newEntry != null) {
            saveEntryToCsv(newEntry);
        }
    }
    
    private void showEditEntryDialog() {
        if (selectedEntry == null || selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an entry to edit.",
                "No Entry Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        EntryDialog dialog = new EntryDialog(this, selectedEntry);
        dialog.setVisible(true);
        
        PasswordEntry updatedEntry = dialog.getResult();
        if (updatedEntry != null) {
            updateEntry(selectedIndex, updatedEntry);
        }
    }
    
    private void showDeleteConfirmation() {
        if (selectedEntry == null || selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an entry to delete.",
                "No Entry Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog confirmDialog = new JDialog(this, "Delete Entry", true);
        confirmDialog.setSize(450, 280);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        
        JLabel warningIcon = new JLabel("⚠️");
        warningIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        warningIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel messageLabel = new JLabel("Are you sure you want to delete this entry?");
        messageLabel.setForeground(TEXT_WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel entryLabel = new JLabel(selectedEntry.getTitle());
        entryLabel.setForeground(TEXT_GRAY);
        entryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        entryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel warningText = new JLabel("This action cannot be undone.");
        warningText.setForeground(Color.decode("#ff5252"));
        warningText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        warningText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(warningIcon);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(entryLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(warningText);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton deleteButton = new JButton("Yes, Delete");
        deleteButton.setBackground(Color.decode("#ff5252"));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteButton.setPreferredSize(new Dimension(130, 40));
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> {
            deleteEntry(selectedIndex);
            confirmDialog.dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(FIELD_BG);
        cancelButton.setForeground(TEXT_WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(130, 40));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel);
        
        confirmDialog.add(mainPanel, BorderLayout.CENTER);
        confirmDialog.setVisible(true);
    }
    
    private void saveEntryToCsv(PasswordEntry entry) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("vault.csv");
            boolean fileExists = java.nio.file.Files.exists(path);
            
            try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(
                    path, 
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND)) {
                
                if (!fileExists) {
                    writer.write("Title,Username,Password,URL");
                    writer.newLine();
                }
                
                writer.write(entry.toCsvRow());
                writer.newLine();
            }
            
            entries.add(entry);
            selectedIndex = entries.size() - 1;
            selectedEntry = entry;
            refreshEntryList();
            updateDetailPanel();
            
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error saving entry: " + ex.getMessage(),
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateEntry(int index, PasswordEntry updatedEntry) {
        try {
            entries.set(index, updatedEntry);
            
            java.nio.file.Path path = java.nio.file.Paths.get("vault.csv");
            try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(
                    path,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {
                
                writer.write("Title,Username,Password,URL");
                writer.newLine();
                
                for (PasswordEntry entry : entries) {
                    writer.write(entry.toCsvRow());
                    writer.newLine();
                }
            }
            
            selectedEntry = updatedEntry;
            refreshEntryList();
            updateDetailPanel();
            
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error updating entry: " + ex.getMessage(),
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEntry(int index) {
        try {
            entries.remove(index);
            
            java.nio.file.Path path = java.nio.file.Paths.get("vault.csv");
            try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(
                    path,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {
                
                writer.write("Title,Username,Password,URL");
                writer.newLine();
                
                for (PasswordEntry entry : entries) {
                    writer.write(entry.toCsvRow());
                    writer.newLine();
                }
            }
            
            if (!entries.isEmpty()) {
                selectedIndex = Math.min(index, entries.size() - 1);
                selectedEntry = entries.get(selectedIndex);
            } else {
                selectedIndex = -1;
                selectedEntry = null;
            }
            
            refreshEntryList();
            updateDetailPanel();
            
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error deleting entry: " + ex.getMessage(),
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadEntriesFromCsv() {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("vault.csv");
            
            if (!java.nio.file.Files.exists(path)) {
                return;
            }
            
            List<String> lines = java.nio.file.Files.readAllLines(path);
            
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    PasswordEntry entry = PasswordEntry.fromCsvRow(line);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
            }
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading entries: " + ex.getMessage(),
                "File Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshEntryList() {
        listPanel.removeAll();
        
        List<PasswordEntry> filteredEntries = getFilteredEntries();
        
        if (filteredEntries.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(50, 20, 20, 20));
            
            JLabel emptyIcon = new JLabel(searchQuery.isEmpty() ? "🔒" : "🔍");
            emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptyLabel = new JLabel(searchQuery.isEmpty() ? "Vault is Empty" : "No Results Found");
            emptyLabel.setForeground(TEXT_GRAY);
            emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptyHint = new JLabel(searchQuery.isEmpty() ? "Click + Add to create your first entry" : "Try a different search term");
            emptyHint.setForeground(TEXT_GRAY);
            emptyHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emptyHint.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            emptyPanel.add(emptyLabel);
            emptyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            emptyPanel.add(emptyHint);
            
            listPanel.add(emptyPanel);
        } else {
            for (int i = 0; i < filteredEntries.size(); i++) {
                PasswordEntry entry = filteredEntries.get(i);
                final int originalIndex = entries.indexOf(entry);
                boolean isSelected = (selectedEntry != null && entry == selectedEntry);
                
                JPanel itemPanel = createListItem(entry.getTitle(), entry.getUsername(), isSelected);
                itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        selectEntry(originalIndex);
                    }
                });
                listPanel.add(itemPanel);
                if (i < filteredEntries.size() - 1) {
                    listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        }
        
        listPanel.add(Box.createVerticalGlue());
        listPanel.revalidate();
        listPanel.repaint();
    }
    
    private List<PasswordEntry> getFilteredEntries() {
        if (searchQuery.isEmpty()) {
            return entries;
        }
        
        List<PasswordEntry> filtered = new ArrayList<>();
        for (PasswordEntry entry : entries) {
            String title = entry.getTitle().toLowerCase();
            String username = entry.getUsername().toLowerCase();
            
            if (title.contains(searchQuery) || username.contains(searchQuery)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }
    
    private void selectEntry(int index) {
        selectedIndex = index;
        selectedEntry = entries.get(index);
        refreshEntryList();
        updateDetailPanel();
    }
    
    private JLabel createDialogLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_GRAY);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createDialogRoundedField(JTextField field) {
        RoundedPanel roundedPanel = new RoundedPanel(400, 15);
        roundedPanel.setLayout(new BorderLayout());
        roundedPanel.setBackground(FIELD_BG);
        roundedPanel.setMaximumSize(new Dimension(400, 40));
        roundedPanel.setPreferredSize(new Dimension(400, 40));
        roundedPanel.setBorder(new EmptyBorder(0, 15, 0, 15));
        roundedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(ACCENT);
        field.setBorder(null);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        if (field instanceof JPasswordField) {
            JPanel fieldPanel = new JPanel(new BorderLayout());
            fieldPanel.setOpaque(false);
            fieldPanel.add(field, BorderLayout.CENTER);
            
            // Create toggle button
            JButton toggleButton = new JButton("👁");
            toggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            toggleButton.setBackground(FIELD_BG);
            toggleButton.setForeground(TEXT_GRAY);
            toggleButton.setFocusPainted(false);
            toggleButton.setBorderPainted(false);
            toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            toggleButton.setPreferredSize(new Dimension(30, 30));
            
            toggleButton.addActionListener(e -> {
                JPasswordField passwordField = (JPasswordField) field;
                if (passwordField.getEchoChar() == '\u2022') {
                    passwordField.setEchoChar((char) 0);
                    toggleButton.setText("👁");
                } else {
                    passwordField.setEchoChar('\u2022');
                    toggleButton.setText("👁");
                }
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            buttonPanel.setOpaque(false);
            buttonPanel.add(toggleButton);
            fieldPanel.add(buttonPanel, BorderLayout.EAST);
            
            roundedPanel.add(fieldPanel, BorderLayout.CENTER);
        } else {
            roundedPanel.add(field, BorderLayout.CENTER);
        }
        
        return roundedPanel;
    }
    
    private JTextField createDialogTextField() {
        JTextField field = new JTextField();
        return field;
    }
    
    private void styleDialogField(JTextField field) {
        field.setMaximumSize(new Dimension(400, 40));
        field.setPreferredSize(new Dimension(400, 40));
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(ACCENT);
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
    
    private void styleDialogButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- HELPER METHODS ---

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_GRAY);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel createDarkField(String text, boolean isPassword) {
        RoundedPanel panel = new RoundedPanel(100, 15);
        panel.setLayout(new BorderLayout());
        panel.setBackground(FIELD_BG);
        panel.setPreferredSize(new Dimension(400, 45));
        panel.setBorder(new EmptyBorder(0, 15, 0, 15));

        JTextField field;
        if (isPassword) {
            field = new JPasswordField(text);
            ((JPasswordField) field).setEchoChar('\u2022');
        } else {
            field = new JTextField(text);
        }

        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_WHITE);
        field.setBorder(null);
        field.setFont(new Font("Monospaced", Font.PLAIN, 15));
        field.setCaretColor(ACCENT);
        field.setEditable(false);

        if (isPassword) {
            JPanel fieldPanel = new JPanel(new BorderLayout());
            fieldPanel.setOpaque(false);
            fieldPanel.add(field, BorderLayout.CENTER);
            
            JButton toggleButton = new JButton("👁");
            toggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            toggleButton.setBackground(FIELD_BG);
            toggleButton.setForeground(TEXT_GRAY);
            toggleButton.setFocusPainted(false);
            toggleButton.setBorderPainted(false);
            toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            toggleButton.setPreferredSize(new Dimension(30, 30));
            
            toggleButton.addActionListener(e -> {
                JPasswordField passwordField = (JPasswordField) field;
                if (passwordField.getEchoChar() == '\u2022') {
                    passwordField.setEchoChar((char) 0);
                    toggleButton.setText("👁");
                } else {
                    passwordField.setEchoChar('\u2022');
                    toggleButton.setText("👁");
                }
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            buttonPanel.setOpaque(false);
            buttonPanel.add(toggleButton);
            fieldPanel.add(buttonPanel, BorderLayout.EAST);
            
            panel.add(fieldPanel, BorderLayout.CENTER);
        } else {
            panel.add(field, BorderLayout.CENTER);
        }

        JPanel outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.setOpaque(false);
        outerWrapper.add(panel, BorderLayout.CENTER);
        
        JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        copyPanel.setOpaque(false);
        
        JLabel copyIcon = new JLabel("[Copy]");
        copyIcon.setForeground(TEXT_GRAY);
        copyIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        copyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyIcon.setToolTipText("Copy to clipboard");
        
        copyIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                String textToCopy = isPassword ? new String(((JPasswordField)field).getPassword()) : field.getText();
                java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(textToCopy);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                
                copyIcon.setForeground(ACCENT);
                Timer timer = new Timer(500, e -> copyIcon.setForeground(TEXT_GRAY));
                timer.setRepeats(false);
                timer.start();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                copyIcon.setForeground(TEXT_WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                copyIcon.setForeground(TEXT_GRAY);
            }
        });
        
        copyPanel.add(copyIcon);
        outerWrapper.add(copyPanel, BorderLayout.EAST);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(outerWrapper, BorderLayout.CENTER);
        wrapper.setMaximumSize(new Dimension(600, 45));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        return wrapper;
    }


    static class RoundedPanel extends JPanel {
        private int radius;
        public RoundedPanel(int width, int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordVault().setVisible(true));
    }
}
