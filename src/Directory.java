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
     * 
     * @param name   The name of the directory
     * @param parent The parent directory (null for root)
     */
    public Directory(String name, Directory parent) {
        super(name, parent);
        this.children = new HashMap<>();
    }

    // checks if this node is a directory
    // always returns true for directories so we are overriding the default behavior
    @Override
    public boolean isDirectory() {
        return true;
    }

    // gets the size of the directory
    // recursively calculates the size of all children
    @Override
    public long getSize() {
        long totalSize = 0;

        // Recursively calculate size of all children
        for (Node child : children.values()) {
            totalSize += child.getSize();
        }

        return totalSize;
    }

    // gets a child by name
    // @return The child node, or null if not found
    public Node getChild(String name) {
        return children.get(name);
    }

    // adds a child to the directory
    public void addChild(Node child) {
        children.put(child.getName(), child);
        child.setParent(this);
    }

    // removes a child from the directory
    // @return The removed node, or null if not found
    public Node removeChild(String name) {
        return children.remove(name);
    }

    // checks if a child with the given name exists
    // @return true if the child exists, false otherwise
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }

    // gets all children of the directory
    public HashMap<String, Node> getChildren() {
        return children;
    }

    // gets a sorted list of child names
    // used by 'ls' and 'tree' commands for consistent output
    public List<String> getSortedChildNames() {
        List<String> names = new ArrayList<>(children.keySet());
        Collections.sort(names);
        return names;
    }

    // checks if the directory is empty
    public boolean isEmpty() {
        return children.isEmpty();
    }

    // gets the number of children in the directory
    public int getChildCount() {
        return children.size();
    }
}
