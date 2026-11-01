import java.util.ArrayList;
import java.util.Scanner;

public class SES {
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<User> users = new ArrayList<>();
    private static final ArrayList<Expense> expenses = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== SMART EXPENSE SPLITTER ===");
        int choice;
        do {
            System.out.println("\n1. Add member");
            System.out.println("2. Show members");
            System.out.println("3. Add expense (equal split)");
            System.out.println("4. Show expenses");
            System.out.println("5. Show balances");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> addMember();
                case 2 -> showMembers();
                case 3 -> addExpense();
                case 4 -> showExpenses();
                case 5 -> showBalances();
                case 0 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private static void addMember() {
        System.out.print("Enter member name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        users.add(new User(users.size() + 1, name));
        System.out.println("Member added with ID " + users.size() + ".");
    }

    private static void showMembers() {
        if (users.isEmpty()) {
            System.out.println("No members added yet.");
            return;
        }
        System.out.println("\nMembers:");
        for (User user : users)
            System.out.println(user.getId() + ". " + user.getName());
    }

    private static void addExpense() {
        if (users.isEmpty()) {
            System.out.println("Add members first.");
            return;
        }

        System.out.print("Expense description: ");
        String description = sc.nextLine().trim();
        if (description.isEmpty()) {
            System.out.println("Description cannot be empty.");
            return;
        }

        System.out.print("Total amount: ");
        double amount = readDouble();
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        showMembers();
        System.out.print("Enter ID of person who paid: ");
        User payer = findUser(readInt());
        if (payer == null) {
            System.out.println("Invalid member ID.");
            return;
        }

        System.out.print("Enter number of participants: ");
        int count = readInt();
        if (count < 1 || count > users.size()) {
            System.out.println("Invalid number of participants.");
            return;
        }

        ArrayList<User> participants = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.print("Enter participant ID " + (i + 1) + ": ");
            User user = findUser(readInt());
            if (user == null || participants.contains(user)) {
                System.out.println("Invalid or duplicate member ID. Expense cancelled.");
                return;
            }
            participants.add(user);
        }

        expenses.add(new Expense(description, amount, payer, participants));
        System.out.printf("Expense added. Share per participant: %.2f%n", amount / count);
    }

    private static void showExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }
        System.out.println("\nExpense history:");
        for (Expense expense : expenses) {
            System.out.printf("%s | Total: %.2f | Paid by: %s | Share each: %.2f%n",
                    expense.getDescription(), expense.getAmount(),
                    expense.getPayer().getName(),
                    expense.getAmount() / expense.getParticipants().size());
        }
    }

    private static void showBalances() {
        if (users.isEmpty()) {
            System.out.println("No members added yet.");
            return;
        }

        
        ArrayList<Double> balances = new ArrayList<>();
        for (int i = 0; i < users.size(); i++)
            balances.add(0.0);

        for (Expense expense : expenses) {
            int payerIndex = users.indexOf(expense.getPayer());
            balances.set(payerIndex, balances.get(payerIndex) + expense.getAmount());

            double share = expense.getAmount() / expense.getParticipants().size();
            for (User participant : expense.getParticipants()) {
                int participantIndex = users.indexOf(participant);
                balances.set(participantIndex, balances.get(participantIndex) - share);
            }
        }

        System.out.println("\nBalances (positive = receives, negative = owes):");
        for (int i = 0; i < users.size(); i++) {
            double balance = balances.get(i);
            if (Math.abs(balance) < 0.005) balance = 0.0;
            if (balance > 0)
                System.out.printf("%s should receive %.2f%n", users.get(i).getName(), balance);
            else if (balance < 0)
                System.out.printf("%s owes %.2f%n", users.get(i).getName(), -balance);
            else
                System.out.println(users.get(i).getName() + " is settled.");
        }
    }

    private static User findUser(int id) {
        for (User user : users)
            if (user.getId() == id) return user;
        return null;
    }

    private static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double readDouble() {
        try {
            return Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static class User {
        private final int id;
        private final String name;

        User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        int getId() { return id; }
        String getName() { return name; }
    }

    static class Expense {
        private final String description;
        private final double amount;
        private final User payer;
        private final ArrayList<User> participants;

        Expense(String description, double amount, User payer, ArrayList<User> participants) {
            this.description = description;
            this.amount = amount;
            this.payer = payer;
            this.participants = new ArrayList<>(participants);
        }

        String getDescription() { return description; }
        double getAmount() { return amount; }
        User getPayer() { return payer; }
        ArrayList<User> getParticipants() { return new ArrayList<>(participants); }
    }
}
