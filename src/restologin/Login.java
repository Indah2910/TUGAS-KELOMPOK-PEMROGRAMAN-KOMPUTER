import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    private JComboBox<String> roleComboBox;
    
    // Using koneksi utility class for database operations
    
    public login() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Test database connection on startup
        testDatabaseConnection();
    }
    
    private void initializeComponents() {
        setTitle("Resto Kelabu - Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If setting look and feel fails, continue with default
        }
        // Create components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("LOGIN");
        registerButton = new JButton("Daftar Akun Baru");
        statusLabel = new JLabel(" ");
        roleComboBox = new JComboBox<>(new String[]{"Pilih Role", "admin", "kasir", "pelayan"});
        
        // Style components
        styleComponents();
    }
    
    private void styleComponents() {
        // Set background color
        getContentPane().setBackground(new Color(45, 52, 54));
        
        // Style text fields
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(116, 185, 255), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(116, 185, 255), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Style combo box
        roleComboBox.setFont(fieldFont);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(116, 185, 255), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Style login button
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 184, 148));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Style register button
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerButton.setBackground(new Color(108, 117, 125));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Style status label
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(255, 107, 107));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(45, 52, 54));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        // Logo/Title panel
        JPanel titlePanel = createTitlePanel();
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add components to main panel
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusLabel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(45, 52, 54));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        // Restaurant icon (using Unicode)
        JLabel iconLabel = new JLabel("🍽️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("RESTO KELABU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(116, 185, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Sistem Kasir Restoran");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(178, 190, 195));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(45, 52, 54));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Role field
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(roleLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(roleComboBox);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(45, 52, 54));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Register button action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close current window and open register window
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new register().setVisible(true);
                });
            }
        });
        
        // Enter key press on password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Hover effects for login button
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(0, 206, 166));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(0, 184, 148));
            }
        });
        
        // Hover effects for register button
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(134, 142, 150));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(108, 117, 125));
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();
        
        // Validation
        if (username.isEmpty()) {
            showError("Username tidak boleh kosong!");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Password tidak boleh kosong!");
            passwordField.requestFocus();
            return;
        }
        
        if (selectedRole.equals("Pilih Role")) {
            showError("Silakan pilih role!");
            roleComboBox.requestFocus();
            return;
        }
        
        // Disable login button during authentication
        loginButton.setEnabled(false);
        loginButton.setText("Memverifikasi...");
        statusLabel.setText("Sedang memverifikasi login...");
        statusLabel.setForeground(new Color(255, 193, 7));
        
        // Perform authentication in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String userFullName = "";
            private String userRole = "";
            
            @Override
            protected Boolean doInBackground() throws Exception {
                return authenticateUser(username, password, selectedRole);
            }
            
            @Override
            protected void done() {
                try {
                    boolean isAuthenticated = get();
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                    
                    if (isAuthenticated) {
                        showSuccess("Login berhasil! Selamat datang, " + userFullName);
                        // Here you can open the main application window
                        // For now, we'll just show a success message
                        Timer timer = new Timer(2000, e -> {
                            // Open main application window here
                            JOptionPane.showMessageDialog(login.this, 
                                "Membuka aplikasi kasir...\nRole: " + userRole + 
                                "\nNama: " + userFullName, 
                                "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showError("Username, password, atau role tidak valid!");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                    showError("Terjadi kesalahan: " + e.getMessage());
                }
            }
            
            private boolean authenticateUser(String username, String password, String role) {
                try {
                    // Use koneksi utility class for authentication
                    String[] userData = koneksi.authenticateUser(username, password, role);
                    
                    if (userData != null) {
                        userFullName = userData[0];
                        userRole = userData[1];
                        return true;
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> 
                        showError("Koneksi database gagal: " + e.getMessage()));
                }
                return false;
            }
        };
        
        worker.execute();
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(255, 107, 107));
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(85, 239, 196));
    }
    
    /**
     * Test database connection on application startup
     */
    private void testDatabaseConnection() {
        SwingWorker<Boolean, Void> connectionTester = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return koneksi.testConnection();
            }
            
            @Override
            protected void done() {
                try {
                    boolean isConnected = get();
                    if (isConnected) {
                        statusLabel.setText("Database terhubung - Siap untuk login");
                        statusLabel.setForeground(new Color(85, 239, 196));
                        
                        // Check if database has sample users, if not create them
                        SwingWorker<Void, Void> userCreator = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                if (koneksi.checkDatabaseSetup()) {
                                    koneksi.createSampleUsers();
                                }
                                return null;
                            }
                        };
                        userCreator.execute();
                        
                    } else {
                        statusLabel.setText("Koneksi database gagal - Periksa pengaturan");
                        statusLabel.setForeground(new Color(255, 107, 107));
                        loginButton.setEnabled(false);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    statusLabel.setForeground(new Color(255, 107, 107));
                    loginButton.setEnabled(false);
                }
            }
        };
        
        connectionTester.execute();
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}
