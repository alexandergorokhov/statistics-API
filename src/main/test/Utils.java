import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static String getJsonTransaction(Instant instant) {

        DateTimeFormatter df = DateTimeFormatter.ISO_INSTANT;
        String iso = df.format(Instant.now());

        return String.format("{\"amount\": \"15.5\",\"timestamp\":  \"%s\" }", iso);
    }

}
