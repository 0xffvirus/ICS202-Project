/**
 * Abstract base class representing a node in the file system.
 * Both File and Directory classes inherit from this class.
 * Uses polymorphism to allow uniform treatment of files and directories.
 */
public abstract class Node {
    
    // Name of the file or directory
    protected String name;
    
    // Reference to parent directory (null for root)
    protected Directory parent;
    
    /**
     * Constructor for creating a new node.
     * @param name The name of the node
     * @param parent The parent directory (null for root)
     */
    public Node(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }
    
    /**
     * Gets the name of this node.
     * @return The name of the node
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this node.
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the parent directory of this node.
     * @return The parent directory
     */
    public Directory getParent() {
        return parent;
    }
    
    /**
     * Sets the parent directory of this node.
     * @param parent The new parent directory
     */
    public void setParent(Directory parent) {
        this.parent = parent;
    }
    
    /**
     * Checks if this node is a directory.
     * @return true if this is a directory, false otherwise
     */
    public abstract boolean isDirectory();
    
    /**
     * Gets the size of this node.
     * For files, returns the file size.
     * For directories, returns the total size of all contents.
     * @return The size in bytes
     */
    public abstract long getSize();
    
    /**
     * Gets the full path of this node from root.
     * Uses a stack-based approach to build the path.
     * @return The full absolute path
     */
    public String getFullPath() {
        // Stack to store path components
        java.util.Stack<String> pathStack = new java.util.Stack<>();
        Node current = this;
        
        // Traverse up to root, pushing names onto stack
        while (current != null) {
            pathStack.push(current.getName());
            current = current.getParent();
        }
        
        // Build path string from stack
        StringBuilder path = new StringBuilder();
        while (!pathStack.isEmpty()) {
            String component = pathStack.pop();
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
}

