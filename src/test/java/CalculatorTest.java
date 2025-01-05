import com.skylarkarms.numberutils.Statistics;

import java.util.Arrays;
import java.util.Random;

public class CalculatorTest {
    private static final String TAG = "CalculatorTest";

    public static void main(String[] args) {
        int l = 1_000;
        long[] longs = new long[l];
        Random r = new Random();
        int MIN = 200, MAX = 400, DELTA = MAX - MIN;
        Arrays.setAll(
                longs,
                value -> MIN + r.nextInt(DELTA)
        );

        Statistics.Snapshot snapshot = Statistics.of(TAG, longs);
        Statistics.Snapshot snapshot_2 = Statistics.of(TAG.concat(" 2"), longs);
        Statistics.Snapshot snapshot_3 = Statistics.of(longs);
        System.out.println("STATS!!!");
        System.out.println(
                inspect(Statistics.Snapshot.tableOf(snapshot, snapshot_2, snapshot_3))
        );
        System.out.println("STATS 2!!!");
        System.out.println(
                inspect(snapshot.table(
                        Statistics.Tabs.hide,
                        Statistics.Tabs.hide)
                )
        );
    }

    public static String inspect(String[][] table) {
        int length;
        if (table == null || (length = table.length) == 0) {
            return table == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = new StringBuilder("\nTable:\n");

        if (table.length == 1) {
                String[] row = table[0];
                builder.append("[Row ").append("0").append("] ");
                for (String s : row) {
                    String cell = s != null ? s : "null";
                    builder.append(cell).append(" ");
                }
            return builder.toString();
        }

        // Calculate the maximum width of each column
        int[] columnWidths = null;
        try {
            for (String[] row : table) {
                if (columnWidths == null) {
                    columnWidths = new int[row.length];
                }
                for (int col = 0; col < row.length; col++) {
                    if (row[col] != null) {
                        columnWidths[col] =
                                Math.max(
                                columnWidths[col],
                                row[col].length()
                        );
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    "Jagged Arrays not supported... or maybe... if the 1st row is the lengthiest..."
            );
        }

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
