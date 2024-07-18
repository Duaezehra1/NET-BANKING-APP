import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BankAcc {
    private int balance;
    private int prevTrans;
    private String customerName;
    private String customerId;

    private List<String> transactionHistory = new ArrayList<>();

    private static final String USER_FILE_NAME = "userDetails.txt";

    BankAcc(String cname, String cid) {
        customerName = cname;
        customerId = cid;
        loadAccountDetails();
    }

    void deposit(int amount) {
        if (amount != 0) {
            balance += amount;
            prevTrans = amount;
            transactionHistory.add("Deposited: " + amount);
            saveAccountDetails();
        }
    }

    void withdraw(int amount) {
        if (amount != 0) {
            if (amount > balance) {
                JOptionPane.showMessageDialog(null, "Insufficient funds! Withdrawal amount exceeds the current balance.");
            } else {
                balance -= amount;
                prevTrans = -amount;
                transactionHistory.add("Withdrawn: " + amount);
                saveAccountDetails();
            }
        }
    }

    void getPreviousTrans() {
        if (prevTrans > 0) {
            JOptionPane.showMessageDialog(null, "Deposited: " + prevTrans);
        } else if (prevTrans < 0) {
            JOptionPane.showMessageDialog(null, "Withdrawn: " + Math.abs(prevTrans));
        } else {
            JOptionPane.showMessageDialog(null, "No transaction has occurred!");
        }
    }

    void showMenu() {
        JFrame frame = new JFrame("Bank Management System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new GridLayout(7, 1));

        JLabel welcomeLabel = new JLabel("      Welcome " + customerName);
        JLabel idLabel = new JLabel("Your ID: " + customerId);
        frame.add(welcomeLabel);
        frame.add(idLabel);

        JButton balanceButton = new JButton("Check Balance");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton previousTransButton = new JButton("Previous Transaction");
        JButton transactionHistoryButton = new JButton("Transaction History");
        JButton changePasswordButton = new JButton("Change Password");
        JButton exitButton = new JButton("Exit");

        frame.add(balanceButton);
        frame.add(depositButton);
        frame.add(withdrawButton);
        frame.add(previousTransButton);
        frame.add(transactionHistoryButton);
        frame.add(changePasswordButton);
        frame.add(exitButton);

        balanceButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Balance: " + balance));

        depositButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter amount to deposit:");
            int amount = Integer.parseInt(input);
            deposit(amount);
            JOptionPane.showMessageDialog(frame, "Amount Deposited: " + amount);
        });

        withdrawButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter amount to withdraw:");
            int amount = Integer.parseInt(input);
            withdraw(amount);
            JOptionPane.showMessageDialog(frame, "Amount Withdrawn: " + amount);
        });

        previousTransButton.addActionListener(e -> getPreviousTrans());

        transactionHistoryButton.addActionListener(e -> showTransactionHistory());

        changePasswordButton.addActionListener(e -> changePassword());

        exitButton.addActionListener(e -> {
            saveAccountDetails();
            System.exit(0);
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showTransactionHistory() {
        JFrame historyFrame = new JFrame("Transaction History");
        historyFrame.setSize(400, 300);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        historyFrame.setLayout(new BorderLayout());

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        for (String transaction : transactionHistory) {
            historyArea.append(transaction + "\n");
        }

        historyFrame.add(scrollPane, BorderLayout.CENTER);
        historyFrame.setLocationRelativeTo(null);
        historyFrame.setVisible(true);
    }

    private void changePassword() {
        String newPassword = JOptionPane.showInputDialog("Enter new password:");
        if (newPassword != null && !newPassword.isEmpty()) {
            updatePassword(customerName, newPassword);
            JOptionPane.showMessageDialog(null, "Password updated successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Password cannot be empty!");
        }
    }

    private void updatePassword(String username, String newPassword) {
        try {
            Map<String, String> users = new HashMap<>();
            File file = new File(USER_FILE_NAME);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] userDetails = line.split(",");
                        if (userDetails.length == 3) {
                            users.put(userDetails[0], userDetails[1] + "," + userDetails[2]);
                        }
                    }
                }
            }
            if (users.containsKey(username)) {
                users.put(username, newPassword + "," + customerId);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : users.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAccountDetails() {
        try (FileWriter writer = new FileWriter(getAccountFileName())) {
            writer.write(customerName + "\n");
            writer.write(customerId + "\n");
            writer.write(balance + "\n");
            writer.write(prevTrans + "\n");
            for (String transaction : transactionHistory) {
                writer.write(transaction + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAccountDetails() {
        File file = new File(getAccountFileName());
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                customerName = reader.readLine();
                customerId = reader.readLine();
                balance = Integer.parseInt(reader.readLine());
                prevTrans = Integer.parseInt(reader.readLine());
                String line;
                while ((line = reader.readLine()) != null) {
                    transactionHistory.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getAccountFileName() {
        return customerId + "_accountDetails.txt";
    }
}
