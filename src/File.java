/**
 * Represents a file in the simulated file system.
 * Stores file name, size, and content in memory.
 * Inherits from Node abstract class.
 */
public class File extends Node {
    
    // Simulated size of the file in bytes
    private long size;
    
    // Content of the file (for echo command)
    private String content;
    
    /**
     * Constructor for creating a file with a specified size.
     * Used by the 'touch' command.
     * @param name The name of the file
     * @param parent The parent directory
     * @param size The simulated size in bytes
     */
    public File(String name, Directory parent, long size) {
        super(name, parent);
        this.size = size;
        this.content = "";
    }
    
    /**
     * Constructor for creating a file with content.
     * Used by the 'echo' command.
     * @param name The name of the file
     * @param parent The parent directory
     * @param content The content of the file
     */
    public File(String name, Directory parent, String content) {
        super(name, parent);
        this.content = content;
        // Size is the length of the content string
        this.size = content.length();
    }
    
    /**
     * Checks if this node is a directory.
     * @return false since this is a file
     */
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    /**
     * Gets the size of this file.
     * @return The file size in bytes
     */
    @Override
    public long getSize() {
        return size;
    }
    
    /**
     * Sets the size of this file.
     * @param size The new size in bytes
     */
    public void setSize(long size) {
        this.size = size;
    }
    
    /**
     * Gets the content of this file.
     * @return The file content
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Sets the content of this file.
     * Also updates the size to match content length.
     * @param content The new content
     */
    public void setContent(String content) {
        this.content = content;
        this.size = content.length();
    }
}

