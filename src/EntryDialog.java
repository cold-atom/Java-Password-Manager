import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EntryDialog extends JDialog {
    
    private final Color BG_MAIN = Color.decode("#121212");
    private final Color ACCENT = Color.decode("#00e676");
    private final Color TEXT_WHITE = Color.decode("#ffffff");
    private final Color TEXT_GRAY = Color.decode("#b0b0b0");
    private final Color FIELD_BG = Color.decode("#333333");
    
    private JTextField titleField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField urlField;
    
    private PasswordEntry result;
    private boolean isEditMode;
    
    public EntryDialog(JFrame parent, PasswordEntry existingEntry) {
        super(parent, existingEntry == null ? "Add New Entry" : "Edit Entry", true);
        this.isEditMode = existingEntry != null;
        
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel(isEditMode ? "Edit Entry" : "Add New Entry");
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        mainPanel.add(createDialogLabel("Title"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titleField = new JTextField();
        mainPanel.add(createDialogRoundedField(titleField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        mainPanel.add(createDialogLabel("Username / Email"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        usernameField = new JTextField();
        mainPanel.add(createDialogRoundedField(usernameField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        mainPanel.add(createDialogLabel("Password"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        passwordField = new JPasswordField();
        passwordField.setEchoChar('\u2022');
        mainPanel.add(createDialogRoundedField(passwordField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        mainPanel.add(createDialogLabel("Website URL"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        urlField = new JTextField();
        mainPanel.add(createDialogRoundedField(urlField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        if (existingEntry != null) {
            titleField.setText(existingEntry.getTitle());
            usernameField.setText(existingEntry.getUsername());
            passwordField.setText(existingEntry.getPassword());
            urlField.setText(existingEntry.getUrl());
        }
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(400, 50));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(0, -5, 0, 0));
        
        JButton saveButton = new JButton("Save Entry");
        styleDialogButton(saveButton, ACCENT, Color.BLACK);
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String url = urlField.getText().trim();
            
            if (title.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Title, Username, and Password are required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            result = new PasswordEntry(title, username, password, url);
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        styleDialogButton(cancelButton, FIELD_BG, TEXT_WHITE);
        cancelButton.addActionListener(e -> {
            result = null;
            dispose();
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    public PasswordEntry getResult() {
        return result;
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
    
    private void styleDialogButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
}
