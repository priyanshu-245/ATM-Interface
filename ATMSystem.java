// Main class to run the ATM system
class ATMSystem {
    public static void main(String[] args) {
        // Create a bank with some accounts for demo
        Bank bank = new Bank();
        
        // Add some demo accounts
        Account account1 = new Account("123456", "1234", "John Doe", 5000.0);
        Account account2 = new Account("654321", "4321", "Jane Smith", 3000.0);
        
        bank.addAccount(account1);
        bank.addAccount(account2);
        
        // Create and start the ATM
        ATM atm = new ATM(bank);
        atm.start();
    }
}

// Bank class to manage accounts
class Bank {
    private java.util.Map<String, Account> accounts;
    
    public Bank() {
        accounts = new java.util.HashMap<>();
    }
    
    public void addAccount(Account account) {
        accounts.put(account.getUserId(), account);
    }
    
    public Account getAccount(String userId) {
        return accounts.get(userId);
    }
    
    public boolean authenticateUser(String userId, String pin) {
        Account account = accounts.get(userId);
        if (account != null) {
            return account.validatePin(pin);
        }
        return false;
    }
}

// Account class to store user account information
class Account {
    private String userId;
    private String pin;
    private String name;
    private double balance;
    private java.util.List<Transaction> transactionHistory;
    
    public Account(String userId, String pin, String name, double initialBalance) {
        this.userId = userId;
        this.pin = pin;
        this.name = name;
        this.balance = initialBalance;
        this.transactionHistory = new java.util.ArrayList<>();
        
        // Add initial deposit transaction
        addTransaction("Initial Deposit", initialBalance);
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getName() {
        return name;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public boolean validatePin(String enteredPin) {
        return pin.equals(enteredPin);
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addTransaction("Deposit", amount);
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            addTransaction("Withdrawal", -amount);
            return true;
        }
        return false;
    }
    
    public boolean transfer(Account recipient, double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            recipient.deposit(amount);
            addTransaction("Transfer to " + recipient.getUserId(), -amount);
            return true;
        }
        return false;
    }
    
    public void addTransaction(String type, double amount) {
        Transaction transaction = new Transaction(type, amount);
        transactionHistory.add(transaction);
    }
    
    public java.util.List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
}

// Transaction class to store transaction details
class Transaction {
    private String type;
    private double amount;
    private java.util.Date timestamp;
    
    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.timestamp = new java.util.Date();
    }
    
    @Override
    public String toString() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sign = amount < 0 ? "-" : "+";
        return String.format("%s | %s | %s$%.2f", 
                dateFormat.format(timestamp), 
                type, 
                sign, 
                Math.abs(amount));
    }
}

// ATM class to handle user interaction
class ATM {
    private Bank bank;
    private Account currentAccount;
    private java.util.Scanner scanner;
    
    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new java.util.Scanner(System.in);
    }
    
    public void start() {
        System.out.println("\n===== Welcome to the ATM System =====\n");
        
        while (true) {
            if (currentAccount == null) {
                if (!login()) {
                    continue;
                }
            }
            
            displayMenu();
            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    showTransactionHistory();
                    break;
                case 2:
                    withdrawMoney();
                    break;
                case 3:
                    depositMoney();
                    break;
                case 4:
                    transferMoney();
                    break;
                case 5:
                    logout();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private boolean login() {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();
        
        if (bank.authenticateUser(userId, pin)) {
            currentAccount = bank.getAccount(userId);
            System.out.println("\nLogin successful! Welcome, " + currentAccount.getName() + "!");
            return true;
        } else {
            System.out.println("\nInvalid User ID or PIN. Please try again.");
            return false;
        }
    }
    
    private void displayMenu() {
        System.out.println("\n===== ATM Menu =====");
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
    }
    
    private int getUserChoice() {
        System.out.print("\nEnter your choice (1-5): ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void showTransactionHistory() {
        System.out.println("\n===== Transaction History =====");
        java.util.List<Transaction> transactions = currentAccount.getTransactionHistory();
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("Date & Time | Type | Amount");
            System.out.println("--------------------------------");
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
        
        System.out.println("\nCurrent Balance: $" + String.format("%.2f", currentAccount.getBalance()));
    }
    
    private void withdrawMoney() {
        System.out.println("\n===== Withdraw Money =====");
        System.out.println("Current Balance: $" + String.format("%.2f", currentAccount.getBalance()));
        
        System.out.print("Enter amount to withdraw: $");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
            
            if (currentAccount.withdraw(amount)) {
                System.out.println("Withdrawal successful!");
                System.out.println("New Balance: $" + String.format("%.2f", currentAccount.getBalance()));
            } else {
                System.out.println("Insufficient funds or invalid amount.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        }
    }
    
    private void depositMoney() {
        System.out.println("\n===== Deposit Money =====");
        System.out.println("Current Balance: $" + String.format("%.2f", currentAccount.getBalance()));
        
        System.out.print("Enter amount to deposit: $");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
            
            currentAccount.deposit(amount);
            System.out.println("Deposit successful!");
            System.out.println("New Balance: $" + String.format("%.2f", currentAccount.getBalance()));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        }
    }
    
    private void transferMoney() {
        System.out.println("\n===== Transfer Money =====");
        System.out.println("Current Balance: $" + String.format("%.2f", currentAccount.getBalance()));
        
        System.out.print("Enter recipient's User ID: ");
        String recipientId = scanner.nextLine();
        
        Account recipient = bank.getAccount(recipientId);
        if (recipient == null) {
            System.out.println("Recipient not found.");
            return;
        }
        
        if (recipient.getUserId().equals(currentAccount.getUserId())) {
            System.out.println("Cannot transfer to your own account.");
            return;
        }
        
        System.out.print("Enter amount to transfer: $");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
            
            if (currentAccount.transfer(recipient, amount)) {
                System.out.println("Transfer successful!");
                System.out.println("New Balance: $" + String.format("%.2f", currentAccount.getBalance()));
            } else {
                System.out.println("Insufficient funds or invalid amount.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
        }
    }
    
    private void logout() {
        System.out.println("\nThank you for using the ATM, " + currentAccount.getName() + "!");
        currentAccount = null;
        System.out.println("You have been logged out.");
    }
}
