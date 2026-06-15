package src.util;

import src.model.Transaction;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for manual algorithm implementations.
 * Demonstrates proficiency in sorting and searching.
 */
public class Algorithms {

    /**
     * Performs a Merge Sort on a list of Transactions based on amount.
     * 
     * @param transactions The list to sort.
     * @return A sorted list of Transactions.
     */
    public static List<Transaction> mergeSort(List<Transaction> transactions) {
        if (transactions.size() <= 1) {
            return transactions;
        }

        int mid = transactions.size() / 2;
        List<Transaction> left = mergeSort(new ArrayList<>(transactions.subList(0, mid)));
        List<Transaction> right = mergeSort(new ArrayList<>(transactions.subList(mid, transactions.size())));

        return merge(left, right);
    }

    /**
     * Merges two sorted lists of Transactions into one sorted list.
     * @param left  The left sorted sublist
     * @param right The right sorted sublist
     * @return The merged sorted list
     */
    private static List<Transaction> merge(List<Transaction> left, List<Transaction> right) {
        List<Transaction> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getAmount() <= right.get(j).getAmount()) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        result.addAll(left.subList(i, left.size()));
        result.addAll(right.subList(j, right.size()));
        return result;
    }

    /**
     * Performs a Binary Search on a sorted list of Transactions.
     * Note: List must be sorted by amount for this to function correctly.
     * 
     * @param transactions Sorted list of transactions.
     * @param amount The target amount to search for.
     * @return Index of the transaction, or -1 if not found.
     */
    public static int binarySearch(List<Transaction> transactions, double amount) {
        int left = 0;
        int right = transactions.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            double midAmount = transactions.get(mid).getAmount();

            if (midAmount == amount) {
                return mid;
            } else if (midAmount < amount) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }
}
