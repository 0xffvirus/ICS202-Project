import java.util.List;

/**
 * Main file system simulator class.
 * Implements all shell commands using appropriate data structures.
 * The file system is entirely in-memory and starts with a single root directory.
 */
public class FileSystem {
    
    // Root directory of the file system
    private Directory root;
    
    // Current working directory
    private Directory currentDirectory;
    
    /**
     * Constructor initializes the file system with a root directory.
     */
    public FileSystem() {
        // Create root directory with name "/" and no parent
        this.root = new Directory("/", null);
        this.currentDirectory = root;
    }
    
    /**
     * Gets the current working directory path for the prompt.
     * @return The path string for display
     */
    public String getCurrentPath() {
        if (currentDirectory == root) {
            return "/";
        }
        return currentDirectory.getFullPath();
    }
    
    // ================== MKDIR COMMAND ==================
    
    /**
     * Creates a new directory in the current directory.
     * Implements: mkdir <dir_name>
     * Data Structures: Tree Manipulation, Hashing
     * @param dirName The name of the directory to create
     * @return Result message
     */
    public String mkdir(String dirName) {
        // Check if name already exists
        if (currentDirectory.hasChild(dirName)) {
            return "Error: '" + dirName + "' already exists.";
        }
        
        // Create new directory and add to current directory
        Directory newDir = new Directory(dirName, currentDirectory);
        currentDirectory.addChild(newDir);
        
        return null; // Success, no output
    }
    
    /**
     * Creates directories with the -p flag (creates parent directories as needed).
     * Implements: mkdir -p <path>
     * Data Structures: Tree Manipulation, Hashing, Stack
     * @param path The path to create (e.g., "a/b/c")
     * @return Result message
     */
    public String mkdirWithParents(String path) {
        // Split path into components
        String[] parts = path.split("/");
        Directory current;
        int startIndex = 0;
        
        // Handle absolute path
        if (path.startsWith("/")) {
            current = root;
            startIndex = 1;
        } else {
            current = currentDirectory;
        }
        
        // Create each directory in the path
        for (int i = startIndex; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            
            Node child = current.getChild(part);
            if (child == null) {
                // Directory doesn't exist, create it
                Directory newDir = new Directory(part, current);
                current.addChild(newDir);
                current = newDir;
            } else if (child.isDirectory()) {
                // Directory exists, move into it
                current = (Directory) child;
            } else {
                // It's a file, error
                return "Error: '" + part + "' exists and is not a directory.";
            }
        }
        
        return null; // Success
    }
    
    // ================== TOUCH COMMAND ==================
    
    /**
     * Creates a new file with a specified size.
     * Implements: touch <file_name> <size>
     * Supports paths like "dir/filename" to create file in subdirectory.
     * Data Structures: Tree Manipulation, Hashing
     * @param fileName The name of the file (can include path)
     * @param size The simulated size in bytes
     * @return Result message
     */
    public String touch(String fileName, long size) {
        // Check if path contains directory separator
        if (fileName.contains("/")) {
            // Parse the path to find the target directory
            int lastSlash = fileName.lastIndexOf('/');
            String dirPath = fileName.substring(0, lastSlash);
            String actualFileName = fileName.substring(lastSlash + 1);
            
            // Navigate to the directory
            Directory targetDir = navigateToDirectory(dirPath);
            if (targetDir == null) {
                return "Error: Path '" + dirPath + "' not found.";
            }
            
            // Check if file already exists in target directory
            if (targetDir.hasChild(actualFileName)) {
                return "Error: '" + actualFileName + "' already exists.";
            }
            
            // Create new file in target directory
            File newFile = new File(actualFileName, targetDir, size);
            targetDir.addChild(newFile);
        } else {
            // Simple case: file in current directory
            if (currentDirectory.hasChild(fileName)) {
                return "Error: '" + fileName + "' already exists.";
            }
            
            // Create new file and add to current directory
            File newFile = new File(fileName, currentDirectory, size);
            currentDirectory.addChild(newFile);
        }
        
        return null; // Success
    }
    
    // ================== ECHO COMMAND ==================
    
    /**
     * Writes content to a file (creates or overwrites).
     * Implements: echo "<text>" > <file_name>
     * Data Structures: Tree Manipulation, Hashing
     * @param content The content to write
     * @param fileName The name of the file
     * @return Result message
     */
    public String echo(String content, String fileName) {
        // Check if path contains directory separator
        if (fileName.contains("/")) {
            // Parse the path to find the target directory
            int lastSlash = fileName.lastIndexOf('/');
            String dirPath = fileName.substring(0, lastSlash);
            String actualFileName = fileName.substring(lastSlash + 1);
            
            // Navigate to the directory
            Directory targetDir = navigateToDirectory(dirPath);
            if (targetDir == null) {
                return "Error: Path '" + dirPath + "' not found.";
            }
            
            // Create or update file in target directory
            Node existing = targetDir.getChild(actualFileName);
            if (existing != null) {
                if (existing.isDirectory()) {
                    return "Error: '" + actualFileName + "' is a directory.";
                }
                // Overwrite existing file content
                ((File) existing).setContent(content);
            } else {
                // Create new file with content
                File newFile = new File(actualFileName, targetDir, content);
                targetDir.addChild(newFile);
            }
        } else {
            // Simple case: file in current directory
            Node existing = currentDirectory.getChild(fileName);
            if (existing != null) {
                if (existing.isDirectory()) {
                    return "Error: '" + fileName + "' is a directory.";
                }
                // Overwrite existing file content
                ((File) existing).setContent(content);
            } else {
                // Create new file with content
                File newFile = new File(fileName, currentDirectory, content);
                currentDirectory.addChild(newFile);
            }
        }
        
        return null; // Success
    }
    
    // ================== LS COMMAND ==================
    
    /**
     * Lists contents of the current directory.
     * Implements: ls
     * Data Structures: Hash Table Iterator
     * @return Formatted listing of directory contents
     */
    public String ls() {
        StringBuilder result = new StringBuilder();
        
        // Get sorted list of children for consistent output
        List<String> childNames = currentDirectory.getSortedChildNames();
        
        for (String name : childNames) {
            Node child = currentDirectory.getChild(name);
            if (child.isDirectory()) {
                result.append(name).append("/");
            } else {
                result.append(name).append(" (").append(child.getSize()).append("B)");
            }
            result.append(" ");
        }
        
        return result.toString().trim();
    }
    
    // ================== CD COMMAND ==================
    
    /**
     * Changes the current working directory.
     * Implements: cd <path>
     * Supports: .. (parent), absolute paths, relative paths
     * Data Structures: Stack, Tree Traversal
     * @param path The path to navigate to
     * @return Result message (null on success)
     */
    public String cd(String path) {
        // Handle special case: cd to root
        if (path.equals("/")) {
            currentDirectory = root;
            return null;
        }
        
        // Handle parent directory
        if (path.equals("..")) {
            if (currentDirectory.getParent() != null) {
                currentDirectory = currentDirectory.getParent();
            }
            return null;
        }
        
        // Handle current directory
        if (path.equals(".")) {
            return null;
        }
        
        // Parse and navigate path
        Directory target = navigateToDirectory(path);
        if (target == null) {
            return "Error: Path '" + path + "' not found.";
        }
        
        currentDirectory = target;
        return null;
    }
    
    /**
     * Helper method to navigate to a directory by path.
     * Uses stack-based path resolution.
     * @param path The path to navigate
     * @return The target directory, or null if not found
     */
    private Directory navigateToDirectory(String path) {
        String[] parts = path.split("/");
        Directory current;
        int startIndex = 0;
        
        // Handle absolute vs relative path
        if (path.startsWith("/")) {
            current = root;
            startIndex = 1;
        } else {
            current = currentDirectory;
        }
        
        // Navigate through each path component
        for (int i = startIndex; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }
            
            if (part.equals("..")) {
                // Go to parent
                if (current.getParent() != null) {
                    current = current.getParent();
                }
            } else {
                // Find child
                Node child = current.getChild(part);
                if (child == null) {
                    return null; // Not found
                }
                if (!child.isDirectory()) {
                    // Check if this is the last part or not
                    if (i == parts.length - 1) {
                        // Trying to cd into a file
                        System.out.println("Error: '" + part + "' is not a directory.");
                        return null;
                    }
                    return null; // Can't traverse through a file
                }
                current = (Directory) child;
            }
        }
        
        return current;
    }
    
    // ================== PWD COMMAND ==================
    
    /**
     * Prints the current working directory path.
     * Implements: pwd
     * Data Structures: Stack (used in getFullPath)
     * @return The full path of current directory
     */
    public String pwd() {
        return getCurrentPath();
    }
    
    // ================== RM COMMAND ==================
    
    /**
     * Removes a file or empty directory.
     * Implements: rm <name>
     * Data Structures: Hashing, Tree Manipulation
     * @param name The name of the file/directory to remove
     * @return Result message
     */
    public String rm(String name) {
        Node target = currentDirectory.getChild(name);
        
        if (target == null) {
            return "Error: '" + name + "' not found.";
        }
        
        if (target.isDirectory()) {
            Directory dir = (Directory) target;
            if (!dir.isEmpty()) {
                return "Error: Cannot remove directory '" + name + "'. It is not empty.";
            }
        }
        
        currentDirectory.removeChild(name);
        return null; // Success
    }
    
    /**
     * Recursively removes a directory and all its contents.
     * Implements: rm -r <dir_name>
     * Data Structures: Recursion, Tree Traversal
     * @param name The name of the directory to remove
     * @return Result message
     */
    public String rmRecursive(String name) {
        Node target = currentDirectory.getChild(name);
        
        if (target == null) {
            return "Error: '" + name + "' not found.";
        }
        
        // Remove the node (recursive deletion happens automatically 
        // since we're just removing the reference)
        currentDirectory.removeChild(name);
        return null; // Success
    }
    
    // ================== TREE COMMAND ==================
    
    /**
     * Displays the file system structure as a tree.
     * Implements: tree
     * Data Structures: Recursion / Queue
     * @return The formatted tree structure
     */
    public String tree() {
        StringBuilder result = new StringBuilder();
        result.append(".\n");
        buildTree(currentDirectory, result, "", true);
        return result.toString().trim();
    }
    
    /**
     * Recursive helper to build tree structure.
     * Uses special Unicode characters for tree drawing.
     * @param dir The current directory
     * @param result StringBuilder for accumulating output
     * @param prefix Current line prefix for indentation
     * @param isRoot Whether this is the root of tree display
     */
    private void buildTree(Directory dir, StringBuilder result, String prefix, boolean isRoot) {
        List<String> childNames = dir.getSortedChildNames();
        int count = 0;
        int total = childNames.size();
        
        for (String name : childNames) {
            count++;
            Node child = dir.getChild(name);
            boolean isLast = (count == total);
            
            // Choose connector based on position
            // ├── for non-last items
            // └── for last item
            String connector = isLast ? "└── " : "├── ";
            
            if (child.isDirectory()) {
                result.append(prefix).append(connector).append(name).append("/\n");
                // Recursively process subdirectory
                // │ for continuation, spaces for last item
                String newPrefix = prefix + (isLast ? "    " : "│   ");
                buildTree((Directory) child, result, newPrefix, false);
            } else {
                result.append(prefix).append(connector).append(name)
                      .append(" (").append(child.getSize()).append("B)\n");
            }
        }
    }
    
    // ================== GREP COMMAND ==================
    
    /**
     * Searches for a pattern in a file using KMP algorithm.
     * Implements: grep "<pattern>" <file_name>
     * Data Structures: KMP String Matching Algorithm
     * @param pattern The pattern to search for
     * @param fileName The file to search in
     * @return Result message indicating if pattern was found
     */
    public String grep(String pattern, String fileName) {
        Node target = currentDirectory.getChild(fileName);
        
        if (target == null) {
            return "Error: '" + fileName + "' not found.";
        }
        
        if (target.isDirectory()) {
            return "Error: '" + fileName + "' is a directory.";
        }
        
        File file = (File) target;
        String content = file.getContent();
        
        // Use KMP algorithm to search for pattern
        if (KMP.search(content, pattern)) {
            return "Pattern \"" + pattern + "\" found in " + fileName + ".";
        } else {
            return "Pattern \"" + pattern + "\" not found in " + fileName + ".";
        }
    }
    
    // ================== DU COMMAND ==================
    
    /**
     * Calculates and displays total size of current directory.
     * Implements: du
     * Data Structures: Recursion, Tree Traversal
     * @return The total size string
     */
    public String du() {
        long totalSize = currentDirectory.getSize();
        return "Total size: " + totalSize + "B";
    }
    
    // ================== HELPER METHODS ==================
    
    /**
     * Gets the root directory.
     * @return The root directory
     */
    public Directory getRoot() {
        return root;
    }
    
    /**
     * Gets the current directory.
     * @return The current working directory
     */
    public Directory getCurrentDirectory() {
        return currentDirectory;
    }
}

