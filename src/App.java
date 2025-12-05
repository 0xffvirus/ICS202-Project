import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main application class for TermFS - Terminal-Based File System Simulator.
 * Provides a command-line interface that mimics common UNIX shell commands.
 * 
 * This is an in-memory file system simulation - no actual files are created on
 * disk.
 * All data exists only in RAM while the program is running.
 * 
 * Supported Commands:
 * - mkdir <dir_name> : Create a new directory
 * - mkdir -p <path> : Create directories including parents
 * - touch <file_name> <size> : Create a file with specified size
 * - echo "<text>" > <file> : Write text to a file
 * - ls : List directory contents
 * - cd <path> : Change current directory
 * - pwd : Print working directory
 * - rm <name> : Remove file or empty directory
 * - rm -r
 * <dir>
 * : Recursively remove directory
 * - tree : Display file system structure
 * - grep "<pattern>" <file> : Search for pattern in file (KMP)
 * - du : Display disk usage
 * - exit : Exit the simulator
 */
public class App {

    // The file system instance
    private static FileSystem fs;

    // Scanner for reading user input
    private static Scanner scanner;

    /**
     * Main entry point for the application.
     * Initializes the file system and starts the command loop.
     */
    public static void main(String[] args) {
        // Initialize file system with root directory
        fs = new FileSystem();
        scanner = new Scanner(System.in);

        // Main command loop
        while (true) {
            // Display prompt with current path
            System.out.print(fs.getCurrentPath() + "$ ");

            // Read user input
            String input = scanner.nextLine().trim();

            // Skip empty input
            if (input.isEmpty()) {
                continue;
            }

            // Check for exit command
            if (input.equals("exit") || input.equals("quit")) {
                System.out.println("Exiting TermFS. Goodbye!");
                break;
            }

            // Parse and execute the command
            executeCommand(input);
        }

        scanner.close();
    }

    /**
     * Parses and executes a single command.
     * 
     * @param input The raw command string from user
     */
    private static void executeCommand(String input) {
        // Tokenize the input while preserving quoted strings
        String[] tokens = tokenize(input);

        if (tokens.length == 0) {
            return;
        }

        String command = tokens[0];
        String result = null;

        switch (command) {
            case "mkdir":
                result = handleMkdir(tokens);
                break;

            case "touch":
                result = handleTouch(tokens);
                break;

            case "echo":
                result = handleEcho(input);
                break;

            case "ls":
                result = fs.ls();
                if (result != null && !result.isEmpty()) {
                    System.out.println(result);
                }
                return;

            case "cd":
                result = handleCd(tokens);
                break;

            case "pwd":
                System.out.println(fs.pwd());
                return;

            case "rm":
                result = handleRm(tokens);
                break;

            case "tree":
                System.out.println(fs.tree());
                return;

            case "grep":
                result = handleGrep(input);
                if (result != null) {
                    System.out.println(result);
                }
                return;

            case "du":
                System.out.println(fs.du());
                return;

            default:
                System.out.println("Error: Unknown command '" + command + "'");
                return;
        }

        // Print error message if there was one
        if (result != null) {
            System.out.println(result);
        }
    }

    /**
     * Tokenizes input string, preserving quoted substrings.
     * 
     * @param input The input string to tokenize
     * @return Array of tokens
     */
    private static String[] tokenize(String input) {
        java.util.List<String> tokens = new java.util.ArrayList<>();

        // Pattern to match quoted strings or non-space sequences
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Double-quoted string
                tokens.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Single-quoted string
                tokens.add(matcher.group(2));
            } else {
                // Regular token
                tokens.add(matcher.group(3));
            }
        }

        return tokens.toArray(new String[0]);
    }

    /**
     * Handles the mkdir command.
     * Supports both simple mkdir and mkdir -p.
     * 
     * @param tokens Command tokens
     * @return Error message or null on success
     */
    private static String handleMkdir(String[] tokens) {
        if (tokens.length < 2) {
            return "Error: mkdir requires a directory name.";
        }

        // Check for -p flag
        if (tokens[1].equals("-p")) {
            if (tokens.length < 3) {
                return "Error: mkdir -p requires a path.";
            }
            return fs.mkdirWithParents(tokens[2]);
        }

        // Handle multiple directories (mkdir dir1 dir2 dir3...)
        for (int i = 1; i < tokens.length; i++) {
            String result = fs.mkdir(tokens[i]);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /**
     * Handles the touch command.
     * Format: touch <filename> <size>
     * 
     * @param tokens Command tokens
     * @return Error message or null on success
     */
    private static String handleTouch(String[] tokens) {
        if (tokens.length < 3) {
            return "Error: touch requires a filename and size.";
        }

        String fileName = tokens[1];
        long size;

        try {
            size = Long.parseLong(tokens[2]);
        } catch (NumberFormatException e) {
            return "Error: Invalid size '" + tokens[2] + "'.";
        }

        return fs.touch(fileName, size);
    }

    /**
     * Handles the echo command.
     * Format: echo "<text>" > <filename>
     * 
     * @param input The full input string (to preserve quotes)
     * @return Error message or null on success
     */
    private static String handleEcho(String input) {
        // Pattern to match: echo "content" > filename
        // Also handles: echo 'content' > filename
        Pattern pattern = Pattern.compile("echo\\s+[\"'](.+?)[\"']\\s*>\\s*(\\S+)");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            return "Error: Invalid echo syntax. Use: echo \"<text>\" > <filename>";
        }

        String content = matcher.group(1);
        String fileName = matcher.group(2);

        return fs.echo(content, fileName);
    }

    /**
     * Handles the cd command.
     * Supports: .., ., absolute paths, relative paths
     * 
     * @param tokens Command tokens
     * @return Error message or null on success
     */
    private static String handleCd(String[] tokens) {
        if (tokens.length < 2) {
            // cd with no args goes to root (like some shells go to home)
            return fs.cd("/");
        }

        // Handle "cd .." with space between cd and ..
        String path = tokens[1];

        // Check if the path is a file, not a directory
        Node target = fs.getCurrentDirectory().getChild(path);
        if (target != null && !target.isDirectory()) {
            return "Error: '" + path + "' is not a directory.";
        }

        return fs.cd(path);
    }

    /**
     * Handles the rm command.
     * Supports both rm and rm -r.
     * 
     * @param tokens Command tokens
     * @return Error message or null on success
     */
    private static String handleRm(String[] tokens) {
        if (tokens.length < 2) {
            return "Error: rm requires a name.";
        }

        // Check for -r flag
        if (tokens[1].equals("-r")) {
            if (tokens.length < 3) {
                return "Error: rm -r requires a name.";
            }
            return fs.rmRecursive(tokens[2]);
        }

        return fs.rm(tokens[1]);
    }

    /**
     * Handles the grep command.
     * Format: grep "<pattern>" <filename>
     * Uses KMP algorithm for pattern matching.
     * 
     * @param input The full input string (to preserve quotes)
     * @return Result message
     */
    private static String handleGrep(String input) {
        // Pattern to match: grep "pattern" filename
        // Also handles: grep 'pattern' filename
        Pattern pattern = Pattern.compile("grep\\s+[\"'](.+?)[\"']\\s+(\\S+)");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            return "Error: Invalid grep syntax. Use: grep \"<pattern>\" <filename>";
        }

        String searchPattern = matcher.group(1);
        String fileName = matcher.group(2);

        return fs.grep(searchPattern, fileName);
    }
}
