import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager implements Serializable {
    ArrayList<Expense> expenses = new ArrayList<>();
    HashMap<String, Integer> categoryTotals = new HashMap<>();
    HashMap<String, Integer> personTotals = new HashMap<>();
    int monthlyLimit = 5000;

    public void addExpense(Expense e) {
        if (e.amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        expenses.add(e);
        categoryTotals.put(e.category, categoryTotals.getOrDefault(e.category, 0) + e.amount);
        personTotals.put(e.person, personTotals.getOrDefault(e.person, 0) + e.amount);
    }

    public int calculateMonthlyTotal(LocalDate date) {
        return expenses.stream()
                .filter(x -> x.date.getMonth() == date.getMonth() && x.date.getYear() == date.getYear())
                .mapToInt(x -> x.amount).sum();
    }

    public List<Expense> searchExpenses(String keyword) {
        return expenses.stream()
                .filter(e -> e.category.contains(keyword) || e.person.contains(keyword) || e.note.contains(keyword))
                .collect(Collectors.toList());
    }

    public void sortExpensesHighToLow() {
        expenses.sort((a, b) -> b.amount - a.amount);
    }

    public void sortExpensesLowToHigh() {
        expenses.sort(Comparator.comparingInt(a -> a.amount));
    }

    public void exportToCSV(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Amount,Category,Person,Note,Date");
            for (Expense e : expenses) {
                writer.println(e.amount + "," + e.category + "," + e.person + "," + e.note + "," + e.date);
            }
        }
    }

    public void saveData(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    public static ExpenseManager loadData(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (ExpenseManager) ois.readObject();
        }
    }
}