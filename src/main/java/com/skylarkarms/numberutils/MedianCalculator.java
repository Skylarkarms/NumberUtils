package com.skylarkarms.numberutils;

import java.util.Collections;
import java.util.PriorityQueue;

public class MedianCalculator {
    private long min;
    private long max;
    private int count;
    // Store two heaps: maxHeap for lower half, minHeap for upper half
    private final PriorityQueue<Long> maxHeap; // Lower half
    private final PriorityQueue<Long> minHeap; // Upper half
    //max/min-Heap refears to the way in which the inner trees are structured.
    // For the "max" heap, the ROOT of the tree is always the HIGHEST in value.
    // the "min", the ROOT is always the LOWEST.

    public MedianCalculator() {
        this.min = Long.MAX_VALUE;
        this.max = Long.MIN_VALUE;
        this.count = 0;
        // Max heap for the lower half
        this.maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        // Min heap for the upper half
        this.minHeap = new PriorityQueue<>();
    }

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
    }

    public double getMedian() {
        if (count == 0) { throw new IllegalStateException("No values added yet"); }
        assert !maxHeap.isEmpty();
        // If odd number of elements, return middle element
        if (count % 2 != 0) { return maxHeap.peek(); }
        assert !minHeap.isEmpty();
        // If even number of elements, return average of middle two elements
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }

    public long[] getStatistics() { return new long[]{min, (long)getMedian(), max}; }

    public int getCount() { return count; }

    @Override
    public String toString() {
        return "MedianCalculator{" +
                "\n  >> min=" + min +
                "\n  >> max=" + max +
                "\n  >> count=" + count +
                "\n  >> maxHeap=\n" + maxHeap.toString().indent(3) +
                "  >> minHeap=\n" + minHeap.toString().indent(3) +
                "}@".concat(Integer.toString(hashCode()));
    }
}
