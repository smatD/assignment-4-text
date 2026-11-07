import java.util.Arrays;
import java.util.List;

public class Tests {
    private static int passed = 0;

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
        passed++;
    }

    private static void match(String text, String pattern, List<Integer> expected) {
        List<Integer> a = Algorithms.naive(text, pattern).indices();
        List<Integer> b = Algorithms.kmp(text, pattern).indices();
        List<Integer> c = Algorithms.rabinKarp(text, pattern).indices();
        check(a.equals(expected) && a.equals(b) && a.equals(c), "Matching: " + text + " / " + pattern);
    }

    public static void main(String[] args) {
        check(Algorithms.lcs("abc", "abc") == 3, "LCS identical");
        check(Algorithms.lcs("abc", "xyz") == 0, "LCS different");
        check(Algorithms.lcs("", "abc") == 0, "LCS empty");
        check(Algorithms.lcs("ABCBDAB", "BDCABA") == 4, "LCS example");
        check(Algorithms.editDistance("hello", "hello") == 0, "Edit identical");
        check(Algorithms.editDistance("", "abc") == 3, "Edit empty");
        check(Algorithms.editDistance("cat", "cut") == 1, "Substitution");
        check(Algorithms.editDistance("cat", "cart") == 1, "Insertion");
        check(Algorithms.editDistance("cart", "cat") == 1, "Deletion");
        check(Algorithms.editDistance("kitten", "sitting") == 3, "Edit example");
        match("abcxyz", "abc", List.of(0));
        match("abcxyz", "cx", List.of(2));
        match("abcxyz", "xyz", List.of(3));
        match("ababa", "aba", List.of(0, 2));
        match("abcxyz", "qq", List.of());
        match("abc", "abcd", List.of());
        match("abc", "", List.of(0, 1, 2, 3));
        match("", "", List.of(0));
        match("aaaaa", "aa", List.of(0, 1, 2, 3));
        check(Arrays.equals(Algorithms.kasai("banana", new int[]{5, 3, 1, 0, 4, 2}),
                new int[]{0, 1, 3, 0, 0, 2}), "Kasai banana");
        check(Arrays.equals(Algorithms.kasai("", new int[]{}), new int[]{}), "Kasai empty");
        System.out.println("Passed " + passed + " tests");
    }
}
