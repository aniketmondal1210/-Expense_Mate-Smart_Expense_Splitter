import java.io.Serializable;
import java.time.LocalDate;

public class Expense implements Serializable {
    int amount;
    String category, person, note;
    LocalDate date;

    public Expense(int amount, String category, String person, String note, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.person = person;
        this.note = note;
        this.date = date;
    }

    public Object[] toRow() {
        return new Object[]{amount, category, person, note, date.toString()};
    }
}