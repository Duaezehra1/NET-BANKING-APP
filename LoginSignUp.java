import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LoginSignUp extends JFrame {
    private JTextField loginUserField;
    private JPasswordField loginPassField;
    private JTextField signUpUserField;
    private JPasswordField signUpPassField;
    private JPasswordField confirmPassField;

    private static final String USER_FILE_NAME = "userDetails.txt";
    private static final int ID_LENGTH = 4;
    private static final int ID_BOUND = 10000; // 4-digit numbers range from 0000 to 9999

    public LoginSignUp() {
        setTitle("Bank Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel loginPanel = createLoginPanel();
        JPanel signupPanel = createSignUpPanel();

        tabbedPane.addTab("Login", loginPanel);
        tabbedPane.addTab("Sign Up", signupPanel);

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        loginUserField = new JTextField();
        loginPassField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginPanel.add(userLabel);
        loginPanel.add(loginUserField);
        loginPanel.add(passLabel);
        loginPanel.add(loginPassField);
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = loginUserField.getText();
            String password = new String(loginPassField.getPassword());

            if (authenticate(username, password)) {
                String userId = getUserId(username);
                BankAcc bankAcc = new BankAcc(username, userId);
                bankAcc.showMenu();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return loginPanel;
    }

    private JPanel createSignUpPanel() {
        JPanel signupPanel = new JPanel();
        signupPanel.setLayout(new GridLayout(4, 2, 10, 10));
        signupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        signUpUserField = new JTextField();
        signUpPassField = new JPasswordField();
        confirmPassField = new JPasswordField();
        JButton signupButton = new JButton("Sign Up");

        signupPanel.add(userLabel);
        signupPanel.add(signUpUserField);
        signupPanel.add(passLabel);
        signupPanel.add(signUpPassField);
        signupPanel.add(confirmPassLabel);
        signupPanel.add(confirmPassField);
        signupPanel.add(new JLabel(""));
        signupPanel.add(signupButton);

        signupButton.addActionListener(e -> {
            String username = signUpUserField.getText();
            String password = new String(signUpPassField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (username.contains(" ") || username.contains("-")) {
                JOptionPane.showMessageDialog(this, "Username must not contain spaces or hyphens", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username already taken", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String userId = generateUniqueId();
                saveUserDetails(username, password, userId);
                JOptionPane.showMessageDialog(this, "Signup successful! Your User ID is " + userId + ". Please log in.");
                dispose();
                new LoginSignUp();
            }
        });

        return signupPanel;
    }

    private boolean authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length == 3 && userDetails[0].trim().equals(username) && userDetails[1].trim().equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length == 3 && userDetails[0].trim().equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getUserId(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length == 3 && userDetails[0].trim().equals(username)) {
                    return userDetails[2].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveUserDetails(String username, String password, String userId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_NAME, true))) {
            writer.write(username + "," + password + "," + userId);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateUniqueId() {
        Set<String> existingIds = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length == 3) {
                    existingIds.add(userDetails[2].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        String uniqueId;
        do {
            uniqueId = String.format("%04d", random.nextInt(ID_BOUND));
        } while (existingIds.contains(uniqueId));

        return uniqueId;
    }

    public static void main(String[] args) {
        new LoginSignUp();
    }
}
