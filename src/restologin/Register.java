import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Register extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField namaLengkapField;
    private JButton registerButton;
    private JButton backToLoginButton;
    private JLabel statusLabel;
    private JComboBox<String> roleComboBox;
    
    public Register() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Test database connection on startup
        testDatabaseConnection();
    }
    
    private void initializeComponents() {
        setTitle("Resto Kelabu - Registrasi User Baru");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 700);
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
        confirmPasswordField = new JPasswordField(20);
        namaLengkapField = new JTextField(20);
        registerButton = new JButton("DAFTAR");
        backToLoginButton = new JButton("Kembali ke Login");
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
        confirmPasswordField.setFont(fieldFont);
        namaLengkapField.setFont(fieldFont);
        
        // Apply consistent border styling
        Color borderColor = new Color(116, 185, 255);
        javax.swing.border.Border innerBorder = BorderFactory.createEmptyBorder(10, 15, 10, 15);
        
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2), innerBorder));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2), innerBorder));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2), innerBorder));
        namaLengkapField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2), innerBorder));
        
        // Style combo box
        roleComboBox.setFont(fieldFont);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Style register button
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setBackground(new Color(0, 184, 148));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Style back to login button
        backToLoginButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backToLoginButton.setBackground(new Color(108, 117, 125));
        backToLoginButton.setForeground(Color.WHITE);
        backToLoginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        // Logo/Title panel
        JPanel titlePanel = createTitlePanel();
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add components to main panel
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(25));
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
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("REGISTRASI USER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(116, 185, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Daftar Akun Baru Resto Kelabu");
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
        
        // Create labels
        JLabel usernameLabel = createFieldLabel("Username");
        JLabel passwordLabel = createFieldLabel("Password");
        JLabel confirmPasswordLabel = createFieldLabel("Konfirmasi Password");
        JLabel namaLengkapLabel = createFieldLabel("Nama Lengkap");
        JLabel roleLabel = createFieldLabel("Role");
        
        // Add components to form panel
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(confirmPasswordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(namaLengkapLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(namaLengkapField);
        formPanel.add(Box.createVerticalStrut(15));
        
        formPanel.add(roleLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(roleComboBox);
        
        return formPanel;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(45, 52, 54));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);
        
        return buttonPanel;
    }
    
    private void setupEventHandlers() {
        // Register button action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });
        
        // Back to login button action
        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close current window and open login window
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new login().setVisible(true);
                });
            }
        });
        
        // Enter key press on confirm password field
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });
        
        // Hover effects for register button
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(0, 206, 166));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(0, 184, 148));
            }
        });
        
        // Hover effects for back button
        backToLoginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backToLoginButton.setBackground(new Color(134, 142, 150));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backToLoginButton.setBackground(new Color(108, 117, 125));
            }
        });
    }
    
    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String namaLengkap = namaLengkapField.getText().trim();
        String selectedRole = (String) roleComboBox.getSelectedItem();
        
        // Validation
        if (username.isEmpty()) {
            showError("Username tidak boleh kosong!");
            usernameField.requestFocus();
            return;
        }
        
        if (username.length() < 3) {
            showError("Username minimal 3 karakter!");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Password tidak boleh kosong!");
            passwordField.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            showError("Password minimal 6 karakter!");
            passwordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Konfirmasi password tidak cocok!");
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (namaLengkap.isEmpty()) {
            showError("Nama lengkap tidak boleh kosong!");
            namaLengkapField.requestFocus();
            return;
        }
        
        if (selectedRole.equals("Pilih Role")) {
            showError("Silakan pilih role!");
            roleComboBox.requestFocus();
            return;
        }
        
        // Disable register button during registration
        registerButton.setEnabled(false);
        registerButton.setText("Mendaftar...");
        statusLabel.setText("Sedang memproses registrasi...");
        statusLabel.setForeground(new Color(255, 193, 7));
        
        // Perform registration in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String errorMessage = "";
            
            @Override
            protected Boolean doInBackground() throws Exception {
                return registerUser(username, password, namaLengkap, selectedRole);
            }
            
            @Override
            protected void done() {
                try {
                    boolean isRegistered = get();
                    registerButton.setEnabled(true);
                    registerButton.setText("DAFTAR");
                    
                    if (isRegistered) {
                        showSuccess("Registrasi berhasil! Akun telah dibuat.");
                        
                        // Clear form
                        clearForm();
                        
                        // Show success dialog and redirect to login
                        Timer timer = new Timer(2000, e -> {
                            JOptionPane.showMessageDialog(Register.this, 
                                "Registrasi berhasil!\nSilakan login dengan akun baru Anda.", 
                                "Sukses", JOptionPane.INFORMATION_MESSAGE);
                            
                            // Redirect to login page
                            dispose();
                            SwingUtilities.invokeLater(() -> {
                                new login().setVisible(true);
                            });
                        });
                        timer.setRepeats(false);
                        timer.start();
                        
                    } else {
                        showError(errorMessage.isEmpty() ? "Registrasi gagal! Silakan coba lagi." : errorMessage);
                    }
                } catch (Exception e) {
                    registerButton.setEnabled(true);
                    registerButton.setText("DAFTAR");
                    showError("Terjadi kesalahan: " + e.getMessage());
                }
            }
            
            private boolean registerUser(String username, String password, String namaLengkap, String role) {
                try {
                    // Use koneksi utility class for registration
                    boolean result = koneksi.registerUser(username, password, namaLengkap, role);
                    
                    if (!result) {
                        // Check if username already exists
                        if (koneksi.checkUsernameExists(username)) {
                            errorMessage = "Username sudah digunakan! Pilih username lain.";
                        } else {
                            errorMessage = "Registrasi gagal! Silakan coba lagi.";
                        }
                    }
                    
                    return result;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMessage = "Koneksi database gagal: " + e.getMessage();
                    return false;
                }
            }
        };
        
        worker.execute();
    }
    
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        namaLengkapField.setText("");
        roleComboBox.setSelectedIndex(0);
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
                        statusLabel.setText("Database terhubung - Siap untuk registrasi");
                        statusLabel.setForeground(new Color(85, 239, 196));
                    } else {
                        statusLabel.setText("Koneksi database gagal - Periksa pengaturan");
                        statusLabel.setForeground(new Color(255, 107, 107));
                        registerButton.setEnabled(false);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    statusLabel.setForeground(new Color(255, 107, 107));
                    registerButton.setEnabled(false);
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
            new Register().setVisible(true);
        });
    }
}
