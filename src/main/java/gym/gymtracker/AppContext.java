package gym.gymtracker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AppContext {
    private static int currentUserId;

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static String getFormatedDate() {
        LocalDate actualDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return actualDate.format(formatter);
    }
}
