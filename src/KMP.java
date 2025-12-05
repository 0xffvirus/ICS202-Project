/**
 * Implementation of the Knuth-Morris-Pratt (KMP) string matching algorithm.
 * Used by the 'grep' command to search for patterns in file content.
 * Time Complexity: O(n + m) where n is text length and m is pattern length.
 */
public class KMP {
    
    /**
     * Searches for a pattern in the given text using KMP algorithm.
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return true if pattern is found, false otherwise
     */
    public static boolean search(String text, String pattern) {
        // Handle edge cases
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (text == null || text.isEmpty()) {
            return false;
        }
        if (pattern.length() > text.length()) {
            return false;
        }
        
        int n = text.length();
        int m = pattern.length();
        
        // Build the failure function (prefix table)
        int[] lps = computeLPSArray(pattern);
        
        int i = 0; // Index for text
        int j = 0; // Index for pattern
        
        while (i < n) {
            // Characters match, move both pointers forward
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
            
            // Pattern found (all characters matched)
            if (j == m) {
                return true;
            }
            // Mismatch after some matches
            else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    // Use the failure function to skip characters
                    // This is the key optimization of KMP
                    j = lps[j - 1];
                } else {
                    // No prefix to fall back to, move to next character in text
                    i++;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Computes the Longest Proper Prefix which is also Suffix (LPS) array.
     * This is the failure function used by KMP algorithm.
     * @param pattern The pattern to compute LPS for
     * @return The LPS array
     */
    private static int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        
        // Length of previous longest prefix suffix
        int length = 0;
        
        // lps[0] is always 0
        lps[0] = 0;
        
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                // Found matching prefix and suffix
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    // Fall back using previously computed LPS
                    // This is similar to the main KMP search
                    length = lps[length - 1];
                    // Note: we don't increment i here
                } else {
                    // No proper prefix which is also suffix
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
    
    /**
     * Finds all occurrences of pattern in text.
     * Returns list of starting indices where pattern is found.
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return List of starting indices
     */
    public static java.util.List<Integer> findAllOccurrences(String text, String pattern) {
        java.util.List<Integer> occurrences = new java.util.ArrayList<>();
        
        if (pattern == null || pattern.isEmpty() || text == null || text.isEmpty()) {
            return occurrences;
        }
        
        int n = text.length();
        int m = pattern.length();
        
        int[] lps = computeLPSArray(pattern);
        
        int i = 0;
        int j = 0;
        
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
            
            if (j == m) {
                // Pattern found at index (i - j)
                occurrences.add(i - j);
                // Continue searching for more occurrences
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        
        return occurrences;
    }
}

