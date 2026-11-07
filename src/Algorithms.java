import java.util.ArrayList;
import java.util.List;

public class Algorithms {
    public record MatchResult(List<Integer> indices, long comparisons) {}

    public static int lcs(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1] + 1;
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[a.length()][b.length()];
    }

    public static int editDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(dp[i - 1][j] + 1,
                        Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost));
            }
        }
        return dp[a.length()][b.length()];
    }

    public static MatchResult naive(String text, String pattern) {
        List<Integer> found = new ArrayList<>();
        long comparisons = 0;
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length()) {
                comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) break;
                j++;
            }
            if (j == pattern.length()) found.add(i);
        }
        return new MatchResult(found, comparisons);
    }

    public static MatchResult kmp(String text, String pattern) {
        List<Integer> found = new ArrayList<>();
        if (pattern.isEmpty()) return emptyMatches(text.length());
        int[] lps = new int[pattern.length()];
        int len = 0;
        for (int i = 1; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(len)) lps[i++] = ++len;
            else if (len > 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        long comparisons = 0;
        for (int i = 0, j = 0; i < text.length();) {
            comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    found.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) j = lps[j - 1];
            else i++;
        }
        return new MatchResult(found, comparisons);
    }

    public static MatchResult rabinKarp(String text, String pattern) {
        List<Integer> found = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) return emptyMatches(n);
        if (m > n) return new MatchResult(found, 0);
        long base = 256, mod = 1_000_000_007L, power = 1, ph = 0, th = 0;
        for (int i = 1; i < m; i++) power = power * base % mod;
        for (int i = 0; i < m; i++) {
            ph = (ph * base + pattern.charAt(i)) % mod;
            th = (th * base + text.charAt(i)) % mod;
        }
        long comparisons = 0;
        for (int i = 0; i <= n - m; i++) {
            if (ph == th) {
                int j = 0;
                while (j < m) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) break;
                    j++;
                }
                if (j == m) found.add(i);
            }
            if (i < n - m) {
                th = (th - text.charAt(i) * power % mod + mod) % mod;
                th = (th * base + text.charAt(i + m)) % mod;
            }
        }
        return new MatchResult(found, comparisons);
    }

    private static MatchResult emptyMatches(int n) {
        List<Integer> found = new ArrayList<>();
        for (int i = 0; i <= n; i++) found.add(i);
        return new MatchResult(found, 0);
    }

    public static int[] kasai(String s, int[] suffixArray) {
        int n = s.length();
        if (suffixArray.length != n) throw new IllegalArgumentException("Invalid suffix array length");
        int[] rank = new int[n], lcp = new int[n];
        boolean[] seen = new boolean[n];
        for (int i = 0; i < n; i++) {
            int pos = suffixArray[i];
            if (pos < 0 || pos >= n || seen[pos]) throw new IllegalArgumentException("Invalid suffix array permutation");
            seen[pos] = true;
            rank[pos] = i;
        }
        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] == 0) { h = 0; continue; }
            int j = suffixArray[rank[i] - 1];
            while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) h++;
            lcp[rank[i]] = h;
            if (h > 0) h--;
        }
        return lcp;
    }
}
