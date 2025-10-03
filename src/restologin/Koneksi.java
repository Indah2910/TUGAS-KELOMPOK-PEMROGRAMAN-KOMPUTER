import java.sql.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Database Connection Utility Class for Resto Kelabu
 * Handles MySQL database connections with proper error handling
 * Author Indah Fitriyani
 */
public class koneksi {
    
    // Database configuration
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "resto_kelabu";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Connection instance
    private static Connection connection = null;
    
    /**
     * Get database connection
     * @return Connection object or null if failed
     */
    public static Connection getConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection with additional parameters for better compatibility
            String fullUrl = DB_URL + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            try {
                connection = DriverManager.getConnection(fullUrl, DB_USER, DB_PASSWORD);
            } catch (SQLException ex) {
                // Handle unknown database error (error code 1049)
                if (isUnknownDatabaseError(ex)) {
                    System.err.println("Database '" + DB_NAME + "' not found. Attempting to create and import schema...");
                    if (createDatabaseAndImportSchema()) {
                        // Retry connection after creating DB and importing schema
                        connection = DriverManager.getConnection(fullUrl, DB_USER, DB_PASSWORD);
                    } else {
                        throw ex;
                    }
                } else {
                    throw ex;
                }
            }
            
            System.out.println("Database connection successful!");
            return connection;
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            System.err.println("Make sure mysql-connector-java.jar is in your classpath");
            e.printStackTrace();
            return null;
            
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isUnknownDatabaseError(SQLException e) {
        // MySQL/MariaDB: SQLState 42000 with vendor code 1049 for unknown database
        return ("42000".equals(e.getSQLState()) && e.getErrorCode() == 1049)
            || (e.getMessage() != null && e.getMessage().toLowerCase().contains("unknown database"));
    }

    private static Connection getServerConnection() throws SQLException {
        String serverUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(serverUrl, DB_USER, DB_PASSWORD);
    }

    private static boolean createDatabaseAndImportSchema() {
        Connection serverConn = null;
        Statement stmt = null;
        try {
            serverConn = getServerConnection();
            serverConn.setAutoCommit(true);
            stmt = serverConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            System.out.println("Database created or already exists: " + DB_NAME);

            // Now import schema into the specific database
            try (Connection dbConn = DriverManager.getConnection(DB_URL + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", DB_USER, DB_PASSWORD)) {
                Path sqlPath = resolveSqlPath("resto_kelabu.sql");
                if (sqlPath == null) {
                    System.err.println("SQL file 'resto_kelabu.sql' not found. Skipping import.");
                    return true; // DB exists; allow app to proceed
                }
                System.out.println("Importing schema from: " + sqlPath.toAbsolutePath());
                runSqlScript(dbConn, sqlPath);
            }
            return true;
        } catch (SQLException ex) {
            System.err.println("Failed to create database or import schema: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            closeAll(null, stmt, serverConn);
        }
    }

    private static Path resolveSqlPath(String filename) {
        // Try current working directory
        Path p1 = Paths.get(filename);
        if (Files.exists(p1)) return p1;
        // Try common workspace-relative locations
        Path p2 = Paths.get(System.getProperty("user.dir"), filename);
        if (Files.exists(p2)) return p2;
        // Try project root known structure (same folder as this file likely resides when running)
        Path p3 = Paths.get("d:\\Pertemuan Algo\\" + filename);
        if (Files.exists(p3)) return p3;
        return null;
    }

    private static void runSqlScript(Connection conn, Path sqlFile) {
        try (BufferedReader reader = Files.newBufferedReader(sqlFile, StandardCharsets.UTF_8)) {
            conn.setAutoCommit(false);
            StringBuilder statementBuilder = new StringBuilder();
            String line;
            try (Statement stmt = conn.createStatement()) {
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    // Skip comments and empty lines
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    // Remove MySQL conditional comments like /*!40101 SET ... */
                    if (trimmed.startsWith("/*!")) {
                        int end = trimmed.indexOf("*/");
                        if (end >= 0) {
                            trimmed = trimmed.substring(trimmed.indexOf(' '), end).trim();
                        } else {
                            continue;
                        }
                    }

                    statementBuilder.append(trimmed).append(' ');
                    // Execute when encountering semicolon end
                    if (trimmed.endsWith(";")) {
                        String sql = statementBuilder.toString();
                        sql = sql.substring(0, sql.lastIndexOf(';')).trim();
                        if (!sql.isEmpty()) {
                            stmt.execute(sql);
                        }
                        statementBuilder.setLength(0);
                    }
                }
                // Execute any remaining statement without trailing semicolon
                String remaining = statementBuilder.toString().trim();
                if (!remaining.isEmpty()) {
                    stmt.execute(remaining);
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            System.out.println("Schema import completed successfully.");
        } catch (IOException | SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {}
            System.err.println("Error importing SQL script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                // Test with a simple query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                
                if (rs.next()) {
                    System.out.println("Database connection test: SUCCESS");
                    closeConnection(conn);
                    return true;
                }
            } catch (SQLException e) {
                System.err.println("Database connection test failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * Close database connection
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Close ResultSet, Statement, and Connection
     * @param rs ResultSet to close
     * @param stmt Statement to close
     * @param conn Connection to close
     */
    public static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing database resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Execute a SELECT query and return ResultSet
     * @param query SQL SELECT query
     * @return ResultSet or null if failed
     */
    public static ResultSet executeQuery(String query) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                return stmt.executeQuery(query);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Execute INSERT, UPDATE, DELETE queries
     * @param query SQL query to execute
     * @return number of affected rows, -1 if failed
     */
    public static int executeUpdate(String query) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            if (conn != null) {
                stmt = conn.createStatement();
                return stmt.executeUpdate(query);
            }
        } catch (SQLException e) {
            System.err.println("Error executing update: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeAll(null, stmt, conn);
        }
        return -1;
    }
    
    /**
     * Authenticate user against database
     * @param username User's username
     * @param password User's password
     * @param role User's role
     * @return User data array [nama_lengkap, role] or null if authentication failed
     */
    public static String[] authenticateUser(String username, String password, String role) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            if (conn == null) {
                return null;
            }
            
            String query = "SELECT nama_lengkap, role FROM users WHERE username = ? AND password = ? AND role = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String[] userData = new String[2];
                userData[0] = rs.getString("nama_lengkap");
                userData[1] = rs.getString("role");
                return userData;
            }
            
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeAll(rs, pstmt, conn);
        }
        
        return null;
    }
    
    /**
     * Check if database and required tables exist
     * @return true if database is properly set up
     */
    public static boolean checkDatabaseSetup() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            if (conn == null) {
                return false;
            }
            
            // Check if users table exists
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(null, null, "users", new String[]{"TABLE"});
            
            if (rs.next()) {
                System.out.println("Database setup verified: users table exists");
                return true;
            } else {
                System.err.println("Database setup error: users table not found");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking database setup: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeAll(rs, stmt, conn);
        }
    }
    
    /**
     * Register a new user in the database
     * @param username User's username
     * @param password User's password
     * @param namaLengkap User's full name
     * @param role User's role
     * @return true if registration successful, false otherwise
     */
    public static boolean registerUser(String username, String password, String namaLengkap, String role) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            if (conn == null) {
                return false;
            }
            
            // Check if username already exists
            if (checkUsernameExists(username)) {
                System.err.println("Registration failed: Username already exists");
                return false;
            }
            
            String query = "INSERT INTO users (username, password, role, nama_lengkap) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.setString(4, namaLengkap);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                System.out.println("User registered successfully: " + username);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeAll(null, pstmt, conn);
        }
        
        return false;
    }
    
    /**
     * Check if username already exists in database
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public static boolean checkUsernameExists(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            if (conn == null) {
                return false;
            }
            
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeAll(rs, pstmt, conn);
        }
        
        return false;
    }
    
    /**
     * Create sample users for testing (run this once to populate database)
     */
    public static void createSampleUsers() {
        String[] insertQueries = {
            "INSERT IGNORE INTO users (username, password, role, nama_lengkap) VALUES ('admin', 'admin123', 'admin', 'Administrator Sistem')",
            "INSERT IGNORE INTO users (username, password, role, nama_lengkap) VALUES ('kasir1', 'kasir123', 'kasir', 'Kasir Utama')",
            "INSERT IGNORE INTO users (username, password, role, nama_lengkap) VALUES ('kasir2', 'kasir123', 'kasir', 'Kasir Kedua')",
            "INSERT IGNORE INTO users (username, password, role, nama_lengkap) VALUES ('pelayan1', 'pelayan123', 'pelayan', 'Pelayan Restoran')",
            "INSERT IGNORE INTO users (username, password, role, nama_lengkap) VALUES ('pelayan2', 'pelayan123', 'pelayan', 'Pelayan Senior')"
        };
        
        for (String query : insertQueries) {
            int result = executeUpdate(query);
            if (result > 0) {
                System.out.println("Sample user created successfully");
            }
        }
    }
    
    /**
     * Main method for testing database connection
     */
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===");
        
        // Test basic connection
        if (testConnection()) {
            System.out.println("✓ Database connection working");
            
            // Check database setup
            if (checkDatabaseSetup()) {
                System.out.println("✓ Database tables verified");
                
                // Create sample users
                System.out.println("Creating sample users...");
                createSampleUsers();
                
                // Test authentication
                System.out.println("Testing authentication...");
                String[] userData = authenticateUser("admin", "admin123", "admin");
                if (userData != null) {
                    System.out.println("✓ Authentication test successful");
                    System.out.println("  User: " + userData[0] + " (" + userData[1] + ")");
                } else {
                    System.out.println("✗ Authentication test failed");
                }
                
            } else {
                System.out.println("✗ Database setup incomplete");
            }
        } else {
            System.out.println("✗ Database connection failed");
            System.out.println("Please check:");
            System.out.println("1. MySQL server is running");
            System.out.println("2. Database 'resto_kelabu' exists");
            System.out.println("3. MySQL JDBC driver is in classpath");
            System.out.println("4. Database credentials are correct");
        }
    }
}
