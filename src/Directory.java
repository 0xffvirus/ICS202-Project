import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Represents a directory in the simulated file system.
 * Uses a HashMap (Hash Table) to store children for O(1) lookup.
 * Inherits from Node abstract class.
 */
public class Directory extends Node {
    
    // Hash Table to store children (key: name, value: Node)
    // Provides O(1) average-case lookup, insertion, and deletion
    private HashMap<String, Node> children;
    
    /**
     * Constructor for creating a new directory.
     * @param name The name of the directory
     * @param parent The parent directory (null for root)
     */
    public Directory(String name, Directory parent) {
        super(name, parent);
        this.children = new HashMap<>();
    }
    
    /**
     * Checks if this node is a directory.
     * @return true since this is a directory
     */
    @Override
    public boolean isDirectory() {
        return true;
    }
    
    /**
     * Calculates the total size of this directory recursively.
     * Uses tree traversal to sum up all file sizes.
     * @return The total size of all contents in bytes
     */
    @Override
    public long getSize() {
        long totalSize = 0;
        
        // Recursively calculate size of all children
        for (Node child : children.values()) {
            totalSize += child.getSize();
        }
        
        return totalSize;
    }
    
    /**
     * Gets a child node by name using hash table lookup.
     * Time Complexity: O(1) average case
     * @param name The name of the child to find
     * @return The child node, or null if not found
     */
    public Node getChild(String name) {
        return children.get(name);
    }
    
    /**
     * Adds a child node to this directory.
     * Uses hash table insertion.
     * Time Complexity: O(1) average case
     * @param child The child node to add
     */
    public void addChild(Node child) {
        children.put(child.getName(), child);
        child.setParent(this);
    }
    
    /**
     * Removes a child node from this directory.
     * Uses hash table deletion.
     * Time Complexity: O(1) average case
     * @param name The name of the child to remove
     * @return The removed node, or null if not found
     */
    public Node removeChild(String name) {
        return children.remove(name);
    }
    
    /**
     * Checks if a child with the given name exists.
     * Uses hash table containsKey operation.
     * Time Complexity: O(1) average case
     * @param name The name to check
     * @return true if child exists, false otherwise
     */
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }
    
    /**
     * Gets all children of this directory.
     * @return HashMap of all children
     */
    public HashMap<String, Node> getChildren() {
        return children;
    }
    
    /**
     * Gets a sorted list of child names.
     * Used by 'ls' and 'tree' commands for consistent output.
     * @return Sorted list of child names
     */
    public List<String> getSortedChildNames() {
        List<String> names = new ArrayList<>(children.keySet());
        Collections.sort(names);
        return names;
    }
    
    /**
     * Checks if this directory is empty.
     * @return true if directory has no children
     */
    public boolean isEmpty() {
        return children.isEmpty();
    }
    
    /**
     * Gets the number of children in this directory.
     * @return The number of children
     */
    public int getChildCount() {
        return children.size();
    }
}

