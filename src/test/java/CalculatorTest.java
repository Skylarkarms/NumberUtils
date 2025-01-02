import com.skylarkarms.numberutils.Statistics;

import java.util.Arrays;
import java.util.Random;

public class CalculatorTest {
    private static final String TAG = "CalculatorTest";

    public static void main(String[] args) {
//        int l = 10;
        int l = 1_000;
        long[] longs = new long[l];
        Random r = new Random();
        int MIN = 200, MAX = 400, DELTA = MAX - MIN;
        Arrays.setAll(
                longs,
                value -> MIN + r.nextInt(DELTA)
        );

//        System.out.println(
//                Arrays.toString(longs)
//        );
        Statistics.Snapshot snapshot = Statistics.of(TAG, longs);
        System.out.println("STATS!!!");
        System.out.println(snapshot);
        System.out.println(
                inspect(snapshot.table())
        );
    }

    public static String inspect(String[][] table) {
        int length;
        if (table == null || (length = table.length) == 0) {
            return table == null ? "Array is null" : "Array is empty";
        }

        // Calculate the maximum width of each column
        int[] columnWidths = null;
        for (String[] row : table) {
            if (columnWidths == null) {
                columnWidths = new int[row.length];
            }
            for (int col = 0; col < row.length; col++) {
                if (row[col] != null) {
                    columnWidths[col] = Math.max(columnWidths[col], row[col].length());
                }
            }
        }

        StringBuilder builder = new StringBuilder("\nTable:\n");

        for (int i = 0; i < length; i++) {
            String[] row = table[i];
            builder.append("[Row ").append(i).append("] ");
            for (int col = 0; col < row.length; col++) {
                String cell = row[col] != null ? row[col] : "null";
                builder.append(String.format("%-" + columnWidths[col] + "s ", cell));
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private static StringBuilder getBuilder(Class<?> aClass, int length) {
        return getStringBuilder(aClass.getComponentType().toString(), length);
    }

    private static StringBuilder getStringBuilder(String componentType, int length) {
        return new StringBuilder(
                "\n Reading Array..." +
                        "\n >> Type: " + componentType +
                        "\n >> Length: " + length +
                        "\n >> Contents: {"
        );
    }
}
