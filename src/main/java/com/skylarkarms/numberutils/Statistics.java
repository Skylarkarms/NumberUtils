package com.skylarkarms.numberutils;

import java.util.Collections;
import java.util.PriorityQueue;

public class Statistics {
    private final String tag;
    private long min;
    private long max;
    private int count;
    private long sum;
    // Store two heaps: maxHeap for lower half, minHeap for upper half
    private final PriorityQueue<Long> maxHeap; // Lower half
    private final PriorityQueue<Long> minHeap; // Upper half
    //max/min-Heap refears to the way in which the inner trees are structured.
    // For the "max" heap, the ROOT of the tree is always the HIGHEST in value.
    // the "min", the ROOT is always the LOWEST.

    public Statistics(String tag) {
        this.tag = tag;
        this.min = Long.MAX_VALUE;
        this.max = Long.MIN_VALUE;
        this.count = 0;
        // Max heap for the lower half
        this.maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        // Min heap for the upper half
        this.minHeap = new PriorityQueue<>();
    }

    public Statistics() { this(null); }

    public void addValue(long value) {
        if (count++ == 0) {
            min = value;
            max = value;
            maxHeap.offer(value);
        } else {
            if (value <= min) {
                min = value;
                maxHeap.offer(value);
                if (maxHeap.size() > minHeap.size()) {
                    minHeap.offer(maxHeap.poll());
                }
            } else if (value >= max) {
                max = value;
                minHeap.offer(value);
                if (minHeap.size() > maxHeap.size()) {
                    maxHeap.offer(minHeap.poll());
                }
            } else {
                // Only need to compare with maxHeap.peek() for values between min and max
                assert !maxHeap.isEmpty();
                if (value < maxHeap.peek()) {
                    maxHeap.offer(value);
                    if (maxHeap.size() > minHeap.size()) {
                        minHeap.offer(maxHeap.poll());
                    }
                } else {
                    minHeap.offer(value);
                    if (minHeap.size() > maxHeap.size()) {
                        maxHeap.offer(minHeap.poll());
                    }
                }
            }
        }
        sum += value;
    }

    public static Snapshot of(String tag, long[] values) {
        int l = values.length;
        Statistics calc = new Statistics(tag);
        for (int i = 0; i < l; i++) {
            calc.addValue(values[i]);
        }
        return calc.getSnapshot();
    }

    /**
     * Returns a {@link Snapshot} object containing the statistics of the values provided.
     * @return {@link Snapshot}
     * */
    public static Snapshot of(long[] values) { return of(null, values); }

    /**
     * Type of tab configuration for the {@link Snapshot#table(Tabs, Tabs)} method.
     * */
    public enum Tabs {
        show, show_tagged, hide
    }
    /**
     * Class containing precomputed results of the {@link Statistics} that created it via {@link #getSnapshot()}
     * */
    public record Snapshot(String tag, double median, double average, long min, long max, long sum, int count){
        record SAllocs() {
            static final String STATS = "STATISTICS", TAGS = "TAGS";
            static final String[]
                    tabs =
                    new String[]{
                            "Average", "Median", "Min", "Max", "Sum", "Count"
                    },
                    header =
                            new String[]{
                                    "|---", "----", "----", "----", "----", "---|"
                            };
        }
        /**
         * @return a table with all precomputed results at the moment of creation.
         * This table is composed of:
         * <lu>
         *     <li>{@link SAllocs#header} When the {@link String} {@code `tag`} is non-null in {@link Statistics#Statistics(String)}</li>
         *     <li>{@link SAllocs#tabs} defining the multiple results computed by {@link Statistics}</li>
         *     <li> A 3rd row containing all precomputed results at the moment of {@link Snapshot} creation</li>
         * </lu>
         *
         * */
        public String[][] table() { return table(Tabs.show_tagged, Tabs.show); }
        public static String[][] tableOf(Snapshot... statistics) {
            int sl = statistics.length;
            final String[][] res = new String[sl + 2][7];
            String[] header = SAllocs.header;
            System.arraycopy(
                    header, 0, res[0], 1, header.length
            );
            res[0][0] = SAllocs.STATS;
            String[] tabs = SAllocs.tabs;
            System.arraycopy(
                    tabs, 0, res[1], 1, tabs.length
            );
            res[1][0] = SAllocs.TAGS;
            for (int i = 2, s_i = 0; s_i < sl; i++, s_i++) {
                Snapshot s = statistics[s_i];
                String[] row = res[i];
                if (s.tag != null) {
                    row[0] = s.tag;
                } else {
                    row[0] = "No.".concat(Integer.toString(s_i + 1));
                }
                row[1] = Double.toString(s.average);
                row[2] = Double.toString(s.median);
                row[3] = Long.toString(s.min);
                row[4] = Long.toString(s.max);
                row[5] = Long.toString(s.sum);
                row[6] = Integer.toString(s.count);
            }
            return res;
        }
        public String[][] table(
                Tabs header, Tabs info
        ) {
            String[][] res;
            boolean showHead;
            if ((showHead = header != Tabs.hide) && info != Tabs.hide) {
                res = new String[3][6];
            } else {
                if (!showHead && info == Tabs.hide) {
                    res = new String[1][6];
                } else {
                    res = new String[2][6];
                }
            }
            if (showHead) {
                String[] head = SAllocs.header;
                System.arraycopy(
                        head, 0, res[0], 0, head.length
                );
                if (tag != null && header == Tabs.show_tagged) {
                    res[0][0] = tag;
                }
            }
            boolean showInfoTag;
            if ((showInfoTag = info == Tabs.show_tagged) || info == Tabs.show) {
                String[] tabs = SAllocs.tabs;
                System.arraycopy(
                        tabs, 0, res[res.length - 2], 0, tabs.length
                );
                String[] resTab = res[res.length - 2];
                if (showInfoTag && tag != null) {
                    for (int i = 0; i < tabs.length; i++) {
                        resTab[i] = tabs[i].concat(" (" + tag + ")");
                    }
                }
            }
            int lastI = res.length - 1;
            res[lastI][0] = Double.toString(average);
            res[lastI][1] = Double.toString(median);
            res[lastI][2] = Long.toString(min);
            res[lastI][3] = Long.toString(max);
            res[lastI][4] = Long.toString(sum);
            res[lastI][5] = Integer.toString(count);
            return res;
        }
    }

    private Snapshot snapshot;

    /**
     * Applies all lazy computations into a class containing a snapshot of results at the time of creation.
     * @return {@link Snapshot}
     * */
    public Snapshot getSnapshot() {
        if (snapshot == null || snapshot.count < count) {
            snapshot = new Snapshot(
                    tag,
                    getMedian(), getAverage(), min, max, sum, count
            );
        }
        return snapshot;
    }

    IntDouble median = new IntDouble(0, 0.0d);

    public double getMedian() {
        if (median.i != count) {
            Long min = maxHeap.peek();
            assert min != null;
            if (count % 2 != 0) {
                median = new IntDouble(count, min);
                return min;
            }
            else {
                Long max = minHeap.peek();
                assert max != null;
                double res = (min + max) / 2.0;
                median = new IntDouble(count, res);
                return res;
            }
        }
        else return median.d;
    }

    IntDouble average = new IntDouble(0, 0.0d);
    record IntDouble(int i, double d) {}

    public double getAverage() {
        if (average.i != count) {
            average = new IntDouble(count, (double) sum / count);
        }
        return average.d;
    }

    public String[][] statisticsTable() {
        return getSnapshot().table();

    }

    public long getMin() { return min; }

    public long getMax() { return max; }

    public int getCount() { return count; }

    @Override
    public String toString() {
        return "MedianCalculator{" +
                "\n  >> average=" + getAverage() +
                "\n  >> median=" + getMedian() +
                "\n  >> min=" + min +
                "\n  >> max=" + max +
                "\n  >> count=" + count +
                "\n  >> maxHeap=\n" + maxHeap.toString().indent(3) +
                "  >> minHeap=\n" + minHeap.toString().indent(3) +
                "}@".concat(Integer.toString(hashCode()));
    }
}
