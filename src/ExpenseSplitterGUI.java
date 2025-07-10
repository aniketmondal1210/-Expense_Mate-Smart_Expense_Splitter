import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.*;
import java.util.Date;
import java.util.List;

public class ExpenseSplitterGUI {
    ExpenseManager manager = new ExpenseManager();
    JFrame frame;
    JTable table;
    DefaultTableModel model;

    public ExpenseSplitterGUI() {
        frame = new JFrame("Smart Expense Splitter");
        frame.setSize(850, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 5, 5));
        JTextField amountField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField personField = new JTextField();
        JTextField noteField = new JTextField();
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        JButton addButton = new JButton("Add");
        JButton searchButton = new JButton("Search");
        JButton sortHighButton = new JButton("Sort High-Low");
        JButton sortLowButton = new JButton("Sort Low-High");
        JButton exportButton = new JButton("Export CSV");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        JButton budgetButton = new JButton("Set Budget");

        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Person:"));
        inputPanel.add(personField);
        inputPanel.add(new JLabel("Note:"));
        inputPanel.add(noteField);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateSpinner);
        inputPanel.add(budgetButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Amount", "Category", "Person", "Note", "Date"}, 0);
        table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(sortHighButton);
        buttonPanel.add(sortLowButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText().trim());
                String category = categoryField.getText().trim();
                String person = personField.getText().trim();
                String note = noteField.getText().trim();
                Date selectedDate = (Date) dateSpinner.getValue();
                Instant instant = selectedDate.toInstant();
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDate date = instant.atZone(zoneId).toLocalDate();

                Expense exp = new Expense(amount, category, person, note, date);
                manager.addExpense(exp);
                model.addRow(exp.toRow());

                int monthlyTotal = manager.calculateMonthlyTotal(LocalDate.now());
                if (monthlyTotal > manager.monthlyLimit * 0.8 && monthlyTotal <= manager.monthlyLimit) {
                    JOptionPane.showMessageDialog(frame, " You have crossed 80% of your monthly limit.");
                } else if (monthlyTotal > manager.monthlyLimit) {
                    JOptionPane.showMessageDialog(frame, " You have exceeded your monthly limit!");
                }

                amountField.setText("");
                categoryField.setText("");
                personField.setText("");
                noteField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid data.");
            }
        });

        searchButton.addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog(frame, "Enter keyword to search:");
            List<Expense> results = manager.searchExpenses(keyword);
            model.setRowCount(0);
            for (Expense exp : results) {
                model.addRow(exp.toRow());
            }
        });

        sortHighButton.addActionListener(e -> {
            manager.sortExpensesHighToLow();
            refreshTable();
        });

        sortLowButton.addActionListener(e -> {
            manager.sortExpensesLowToHigh();
            refreshTable();
        });

        exportButton.addActionListener(e -> {
            try {
                manager.exportToCSV("expenses.csv");
                JOptionPane.showMessageDialog(frame, "Data exported to expenses.csv");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Export failed.");
            }
        });

        saveButton.addActionListener(e -> {
            try {
                manager.saveData("expenses.dat");
                JOptionPane.showMessageDialog(frame, "Data saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Save failed.");
            }
        });

        loadButton.addActionListener(e -> {
            try {
                manager = ExpenseManager.loadData("expenses.dat");
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Data loaded successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Load failed.");
            }
        });

        budgetButton.addActionListener(e -> {
            String newLimit = JOptionPane.showInputDialog(frame, "Enter new monthly limit (current: " + manager.monthlyLimit + "):");
            try {
                manager.monthlyLimit = Integer.parseInt(newLimit);
                JOptionPane.showMessageDialog(frame, "Monthly limit updated to " + manager.monthlyLimit);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        });

        frame.setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Expense exp : manager.expenses) {
            model.addRow(exp.toRow());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseSplitterGUI::new);
    }
}
