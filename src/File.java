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
     * 
     * @param name   The name of the file
     * @param parent The parent directory
     * @param size   The simulated size in bytes
     */
    public File(String name, Directory parent, long size) {
        super(name, parent);
        this.size = size;
        this.content = "";
    }

    /**
     * Constructor for creating a file with content.
     * Used by the 'echo' command.
     * 
     * @param name    The name of the file
     * @param parent  The parent directory
     * @param content The content of the file
     */
    public File(String name, Directory parent, String content) {
        super(name, parent);
        this.content = content;
        // Size is the length of the content string
        this.size = content.length();
    }

    // checks if this node is a file or a directory
    // always returns false for files so we are overriding the default behavior
    @Override
    public boolean isDirectory() {
        return false;
    }

    // gets the size of this file
    @Override
    public long getSize() {
        return size;
    }

    // sets the size of this file
    public void setSize(long size) {
        this.size = size;
    }

    // gets the content of this file
    public String getContent() {
        return content;
    }

    // sets the content of the file, and sets the new size to the length
    // of the new content
    public void setContent(String content) {
        this.content = content;
        this.size = content.length();
    }
}
