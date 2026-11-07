import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class Benchmark {
    private static final Random RANDOM = new Random(42);
    private static volatile long sink;
    private static final List<String> rows = new ArrayList<>();

    private static String randomString(int n) {
        StringBuilder s = new StringBuilder(n);
        for (int i = 0; i < n; i++) s.append("ABCD".charAt(RANDOM.nextInt(4)));
        return s.toString();
    }

    private static double measure(Supplier<Long> task) {
        for (int i = 0; i < 3; i++) sink = task.get();
        long total = 0;
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            sink = task.get();
            total += System.nanoTime() - start;
        }
        return total / 5_000_000.0;
    }

    private static void record(String algorithm, int size, String caseName, double ms, long comparisons) {
        rows.add("%s,%d,%s,%.6f,%d".formatted(algorithm, size, caseName, ms, comparisons));
        System.out.printf("%s %d %s %.4f ms%n", algorithm, size, caseName, ms);
    }

    private static int[] suffixArray(String s) {
        Integer[] indices = new Integer[s.length()];
        for (int i = 0; i < indices.length; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> {
            int i = a, j = b;
            while (i < s.length() && j < s.length()) {
                int diff = Character.compare(s.charAt(i++), s.charAt(j++));
                if (diff != 0) return diff;
            }
            return Integer.compare(s.length() - a, s.length() - b);
        });
        int[] result = new int[indices.length];
        for (int i = 0; i < result.length; i++) result[i] = indices[i];
        return result;
    }

    private static void matching(String text, String pattern, int n, String caseName) {
        Algorithms.MatchResult expected = Algorithms.naive(text, pattern);
        Algorithms.MatchResult k = Algorithms.kmp(text, pattern);
        Algorithms.MatchResult r = Algorithms.rabinKarp(text, pattern);
        if (!expected.indices().equals(k.indices()) || !expected.indices().equals(r.indices()))
            throw new AssertionError("Search disagreement");
        record("Naive", n, caseName, measure(() -> {
            var result = Algorithms.naive(text, pattern);
            return (long) result.indices().size();
        }), expected.comparisons());
        record("KMP", n, caseName, measure(() -> {
            var result = Algorithms.kmp(text, pattern);
            return (long) result.indices().size();
        }), k.comparisons());
        record("RabinKarp", n, caseName, measure(() -> {
            var result = Algorithms.rabinKarp(text, pattern);
            return (long) result.indices().size();
        }), r.comparisons());
    }

    public static void main(String[] args) throws IOException {
        rows.add("algorithm,size,case,average_ms,search_char_comparisons");
        for (int n : new int[]{100, 500, 1000}) {
            String a = randomString(n), b = randomString(n);
            record("LCS", n, "random", measure(() -> (long) Algorithms.lcs(a, b)), -1);
            record("EditDistance", n, "random", measure(() -> (long) Algorithms.editDistance(a, b)), -1);
        }
        for (int n : new int[]{10000, 50000, 100000}) {
            String text = randomString(n);
            String present = text.substring(n / 2, n / 2 + 12);
            matching(text, present, n, "present");
            matching(text, "ZZZZZZZZZZZZ", n, "absent");
        }
        for (int n : new int[]{1000, 5000, 10000}) {
            String s = randomString(n);
            int[] sa = suffixArray(s);
            record("Kasai", n, "random", measure(() -> {
                int[] lcp = Algorithms.kasai(s, sa);
                return (long) lcp.length;
            }), -1);
        }
        Files.createDirectories(Path.of("results"));
        Files.write(Path.of("results", "timings.csv"), rows);
    }
}
