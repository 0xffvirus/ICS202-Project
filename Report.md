# TermFS Project Report
## ICS-202: Data Structures and Algorithms

---

## Table of Contents
1. [Data Structures Used](#data-structures-used)
2. [Algorithms Used](#algorithms-used)
3. [Performance Analysis](#performance-analysis)
4. [Conclusion](#conclusion)

---

## Data Structures Used

### 1. Tree Structure (N-ary Tree)

#### Where Used
The entire file system hierarchy is modeled as a tree structure, with `Node` as the abstract base class and `File` and `Directory` as concrete implementations.

#### Why Used
- **Natural representation**: File systems inherently have a hierarchical parent-child structure
- **Intuitive traversal**: Tree structure allows easy navigation through directories
- **Polymorphism**: Using an abstract `Node` class enables uniform treatment of files and directories

#### How Used
The tree is implemented through parent-child references:

```java
public abstract class Node {
    protected String name;
    protected Directory parent;  // Reference to parent (tree structure)
    
    public Node(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }
}
```

**File Implementation:**

```java
public class File extends Node {
    private long size;
    private String content;
    
    public File(String name, Directory parent, long size) {
        super(name, parent);  // Links to parent directory
        this.size = size;
        this.content = "";
    }
}
```

**Directory Implementation:**

```java
public class Directory extends Node {
    private HashMap<String, Node> children;  // Children nodes
    
    public Directory(String name, Directory parent) {
        super(name, parent);  // Links to parent directory
        this.children = new HashMap<>();
    }
}
```

**Example Usage in FileSystem:**

```java
public FileSystem() {
    this.root = new Directory("/", null);  // Root has no parent
    this.currentDirectory = root;
}
```

---

### 2. Hash Table (HashMap)

#### Where Used
`Directory.java` - Each directory uses a `HashMap<String, Node>` to store its children.

#### Why Used
- **Fast lookups**: O(1) average-case time complexity for finding files/directories by name
- **Efficient insertion/deletion**: O(1) average-case for adding or removing children
- **No duplicate names**: HashMap ensures unique keys (file/directory names)
- **Better than arrays**: No fixed size limit, dynamic resizing

#### How Used

**Declaration in Directory class:**

```java
public class Directory extends Node {
    // Hash Table to store children (key: name, value: Node)
    // Provides O(1) average-case lookup, insertion, and deletion
    private HashMap<String, Node> children;
    
    public Directory(String name, Directory parent) {
        super(name, parent);
        this.children = new HashMap<>();
    }
}
```

**Adding a child (O(1) operation):**

```java
public void addChild(Node child) {
    children.put(child.getName(), child);  // HashMap insertion
    child.setParent(this);
}
```

**Looking up a child (O(1) operation):**

```java
public Node getChild(String name) {
    return children.get(name);  // HashMap lookup
}
```

**Removing a child (O(1) operation):**

```java
public Node removeChild(String name) {
    return children.remove(name);  // HashMap deletion
}
```

**Checking existence (O(1) operation):**

```java
public boolean hasChild(String name) {
    return children.containsKey(name);  // HashMap containsKey
}
```

**Usage in mkdir command:**

```java
public String mkdir(String dirName) {
    // O(1) check if name already exists
    if (currentDirectory.hasChild(dirName)) {
        return "Error: '" + dirName + "' already exists.";
    }
    
    Directory newDir = new Directory(dirName, currentDirectory);
    currentDirectory.addChild(newDir);  // O(1) insertion
    
    return null;
}
```

---

### 3. Stack

#### Where Used
- `Node.getFullPath()` - Building the full path from current node to root
- Path resolution in navigation (`cd` command)

#### Why Used
- **LIFO property**: When traversing from child to root, we need to reverse the order for the path
- **Efficient path building**: Push names while going up, pop while building string
- **Natural fit**: Stack reverses the traversal order automatically

#### How Used

**Path Building in Node.java:**

```java
public String getFullPath() {
    // Stack to store path components
    java.util.Stack<String> pathStack = new java.util.Stack<>();
    Node current = this;
    
    // Traverse up to root, pushing names onto stack
    while (current != null) {
        pathStack.push(current.getName());  // Push each component
        current = current.getParent();
    }
    
    // Build path string from stack (pops in reverse order)
    StringBuilder path = new StringBuilder();
    while (!pathStack.isEmpty()) {
        String component = pathStack.pop();  // Pop to reverse order
        if (component.equals("/")) {
            path.append("/");
        } else {
            if (path.length() > 1) {
                path.append("/");
            }
            path.append(component);
        }
    }
    
    return path.toString();
}
```

**Example:**
- If we're in `/home/user/documents`
- Stack operations: push "documents" → push "user" → push "home" → push "/"
- Pop to build: "/" + "home" + "/" + "user" + "/" + "documents"

---

### 4. ArrayList

#### Where Used
`Directory.java` - Sorting and listing directory contents for `ls` and `tree` commands.

#### Why Used
- **Dynamic array**: Flexible size for variable number of children
- **Sorting support**: Easy to sort child names alphabetically
- **Iteration**: Simple iteration for display purposes

#### How Used

**Getting Sorted Child Names:**

```java
public List<String> getSortedChildNames() {
    List<String> names = new ArrayList<>(children.keySet());  // ArrayList from HashMap keys
    Collections.sort(names);  // Sort alphabetically
    return names;
}
```

**Usage in ls command:**

```java
public String ls() {
    StringBuilder result = new StringBuilder();
    
    // Get sorted list of children for consistent output
    List<String> childNames = currentDirectory.getSortedChildNames();
    
    for (String name : childNames) {  // Iterate through ArrayList
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
```

---

### 5. Regular Expression Pattern Matching

#### Where Used
`App.java` - Parsing commands like `echo` and `grep` that contain quoted strings.

#### Why Used
- **Complex parsing**: Need to extract quoted strings and handle special characters
- **Flexibility**: Regex handles various formats and edge cases
- **Built-in support**: Java's Pattern and Matcher classes provide robust functionality

#### How Used

**Tokenizing Input:**

```java
private static String[] tokenize(String input) {
    java.util.List<String> tokens = new java.util.ArrayList<>();
    
    // Pattern to match quoted strings or non-space sequences
    Pattern pattern = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");
    Matcher matcher = pattern.matcher(input);
    
    while (matcher.find()) {
        if (matcher.group(1) != null) {
            tokens.add(matcher.group(1));  // Double-quoted string
        } else if (matcher.group(2) != null) {
            tokens.add(matcher.group(2));  // Single-quoted string
        } else {
            tokens.add(matcher.group(3));  // Regular token
        }
    }
    
    return tokens.toArray(new String[0]);
}
```

**Parsing Echo Command:**

```java
private static String handleEcho(String input) {
    // Pattern to match: echo "content" > filename
    Pattern pattern = Pattern.compile("echo\\s+[\"'](.+?)[\"']\\s*>\\s*(\\S+)");
    Matcher matcher = pattern.matcher(input);
    
    if (!matcher.find()) {
        return "Error: Invalid echo syntax. Use: echo \"<text>\" > <filename>";
    }
    
    String content = matcher.group(1);
    String fileName = matcher.group(2);
    
    return fs.echo(content, fileName);
}
```

---

## Algorithms Used

### 1. Tree Traversal (Depth-First Search)

#### Where Used
- `tree` command - Display entire directory structure
- `du` command - Calculate total directory size
- `rm -r` command - Recursively delete directories

#### Why Used
- **Complete exploration**: Need to visit every node in a subtree
- **Natural recursion**: Tree structure maps perfectly to recursive algorithms
- **Hierarchical processing**: Process parent after/before children as needed

#### How Used

**Recursive Tree Display (Pre-order Traversal):**

```java
public String tree() {
    StringBuilder result = new StringBuilder();
    result.append(".\n");
    buildTree(currentDirectory, result, "", true);
    return result.toString().trim();
}

private void buildTree(Directory dir, StringBuilder result, String prefix, boolean isRoot) {
    List<String> childNames = dir.getSortedChildNames();
    int count = 0;
    int total = childNames.size();
    
    for (String name : childNames) {
        count++;
        Node child = dir.getChild(name);
        boolean isLast = (count == total);
        
        // Choose connector based on position
        String connector = isLast ? "└── " : "├── ";
        
        if (child.isDirectory()) {
            result.append(prefix).append(connector).append(name).append("/\n");
            // Recursively process subdirectory (DFS)
            String newPrefix = prefix + (isLast ? "    " : "│   ");
            buildTree((Directory) child, result, newPrefix, false);
        } else {
            result.append(prefix).append(connector).append(name)
                  .append(" (").append(child.getSize()).append("B)\n");
        }
    }
}
```

**Recursive Size Calculation (Post-order Traversal):**

```java
@Override
public long getSize() {
    long totalSize = 0;
    
    // Recursively calculate size of all children (DFS)
    for (Node child : children.values()) {
        totalSize += child.getSize();  // Recursive call
    }
    
    return totalSize;
}
```

**Usage in du command:**

```java
public String du() {
    long totalSize = currentDirectory.getSize();  // Triggers recursive DFS
    return "Total size: " + totalSize + "B";
}
```

---

### 2. KMP String Matching Algorithm

#### Where Used
`KMP.java` - Used by the `grep` command to search for patterns in file content.

#### Why Used
- **Efficiency**: O(n + m) time complexity vs O(n×m) for naive approach
- **No backtracking**: Avoids re-checking already matched characters
- **Optimal for text search**: Industry-standard pattern matching algorithm

#### How Used

**Main Search Algorithm:**

```java
public static boolean search(String text, String pattern) {
    // Handle edge cases
    if (pattern == null || pattern.isEmpty()) return true;
    if (text == null || text.isEmpty()) return false;
    if (pattern.length() > text.length()) return false;
    
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
                // No prefix to fall back to
                i++;
            }
        }
    }
    
    return false;
}
```

**LPS (Longest Proper Prefix which is also Suffix) Array Computation:**

```java
private static int[] computeLPSArray(String pattern) {
    int m = pattern.length();
    int[] lps = new int[m];
    
    // Length of previous longest prefix suffix
    int length = 0;
    lps[0] = 0;  // lps[0] is always 0
    
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
                length = lps[length - 1];
            } else {
                // No proper prefix which is also suffix
                lps[i] = 0;
                i++;
            }
        }
    }
    
    return lps;
}
```

**Usage in grep command:**

```java
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
```

**Example:**
- Pattern: "ABABC"
- LPS Array: [0, 0, 1, 2, 0]
- When mismatch occurs after "ABAB", instead of starting over, KMP uses LPS to skip to position 2

---

### 3. Path Resolution Algorithm

#### Where Used
`FileSystem.java` - `cd` command and file/directory navigation.

#### Why Used
- **Complex navigation**: Support for `.`, `..`, absolute paths (`/home/user`), and relative paths (`dir1/dir2`)
- **Path normalization**: Resolve components step by step
- **Error handling**: Detect non-existent paths or invalid operations

#### How Used

**Navigation Algorithm:**

```java
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
            continue;  // Skip empty and current directory references
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
                return null;  // Not found
            }
            if (!child.isDirectory()) {
                return null;  // Can't traverse through a file
            }
            current = (Directory) child;
        }
    }
    
    return current;
}
```

**Usage in cd command:**

```java
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
```

**Usage in mkdir -p command:**

```java
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
    
    return null;
}
```

---

### 4. String Building Algorithm

#### Where Used
Throughout the project for building output strings (ls, tree, pwd, etc.)

#### Why Used
- **Efficiency**: StringBuilder is more efficient than string concatenation for multiple operations
- **Mutable**: Avoids creating new string objects for each append
- **Performance**: O(n) vs O(n²) for repeated string concatenation

#### How Used

**In ls command:**

```java
public String ls() {
    StringBuilder result = new StringBuilder();  // Efficient string building
    
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
```

**In tree command:**

```java
private void buildTree(Directory dir, StringBuilder result, String prefix, boolean isRoot) {
    List<String> childNames = dir.getSortedChildNames();
    int count = 0;
    int total = childNames.size();
    
    for (String name : childNames) {
        count++;
        Node child = dir.getChild(name);
        boolean isLast = (count == total);
        
        String connector = isLast ? "└── " : "├── ";
        
        if (child.isDirectory()) {
            result.append(prefix).append(connector).append(name).append("/\n");
            String newPrefix = prefix + (isLast ? "    " : "│   ");
            buildTree((Directory) child, result, newPrefix, false);
        } else {
            result.append(prefix).append(connector).append(name)
                  .append(" (").append(child.getSize()).append("B)\n");
        }
    }
}
```

---

## Performance Analysis

### Time Complexity Analysis

| Operation | Data Structure | Time Complexity | Explanation |
|-----------|---------------|-----------------|-------------|
| `mkdir <dir>` | HashMap | O(1) | Hash table insertion |
| `mkdir -p <path>` | HashMap + Tree | O(d) | d = depth of path |
| `touch <file>` | HashMap | O(1) | Hash table insertion |
| `echo "text" > file` | HashMap | O(1) | Hash table lookup/insertion |
| `ls` | HashMap + ArrayList | O(n log n) | n = number of children (sorting) |
| `cd <path>` | HashMap + Tree | O(d) | d = depth of path |
| `pwd` | Stack + Tree | O(d) | d = depth from root |
| `rm <name>` | HashMap | O(1) | Hash table deletion |
| `rm -r <dir>` | Tree | O(n) | n = total nodes in subtree |
| `tree` | Tree (DFS) | O(n) | n = total nodes |
| `grep "pattern" file` | KMP | O(n + m) | n = text length, m = pattern length |
| `du` | Tree (DFS) | O(n) | n = total nodes in directory |

### Space Complexity Analysis

| Component | Space Complexity | Explanation |
|-----------|------------------|-------------|
| File System | O(n) | n = total files + directories |
| HashMap per Directory | O(k) | k = number of children |
| Stack in getFullPath() | O(d) | d = depth from root |
| KMP LPS Array | O(m) | m = pattern length |
| Tree Recursion Stack | O(h) | h = height of tree |

### Advantages of Our Design

1. **Fast Lookups**: HashMap provides O(1) average-case file/directory access
2. **Efficient Pattern Matching**: KMP avoids redundant character comparisons
3. **Scalable Tree Structure**: Handles arbitrary directory depths
4. **Memory Efficient**: Only stores what's needed, no wasted space
5. **Clear Separation of Concerns**: Each class has a well-defined responsibility

### Potential Optimizations

1. **Caching**: Cache frequently accessed paths
2. **Lazy Evaluation**: Compute directory sizes only when needed
3. **Indexing**: Add a global path index for faster absolute path resolution
4. **Compression**: For large file contents, use compression algorithms

---

## Conclusion

This file system simulator effectively demonstrates the practical application of fundamental data structures and algorithms:

### Data Structures Implemented
1. **N-ary Tree** - Hierarchical file system representation
2. **Hash Table (HashMap)** - Fast O(1) file/directory lookups
3. **Stack** - Path building and resolution
4. **ArrayList** - Sorted directory listings
5. **Regular Expressions** - Command parsing

### Algorithms Implemented
1. **Depth-First Search (DFS)** - Tree traversal for `tree`, `du`, and `rm -r`
2. **KMP String Matching** - Efficient O(n+m) pattern search for `grep`
3. **Path Resolution** - Navigate complex file system paths
4. **String Building** - Efficient output generation

### Key Achievements
- ✅ Modular, object-oriented design with proper encapsulation
- ✅ Efficient algorithms with optimal time complexity
- ✅ Support for all required UNIX-like commands
- ✅ Well-documented code with clear explanations
- ✅ Robust error handling for edge cases

### Learning Outcomes
This project reinforced the importance of:
- Choosing appropriate data structures for specific problems
- Understanding time/space complexity trade-offs
- Implementing industry-standard algorithms (KMP)
- Writing clean, maintainable, and well-documented code
- Designing systems that are both efficient and extensible

---

**Project Team**: ICS-202 Lab Project - Term 251

