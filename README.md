# TermFS - Terminal-Based File System Simulator

A Java-based in-memory file system simulator that mimics common UNIX shell commands. This project was developed for ICS-202: Data Structures and Algorithms.

## Project Structure

```
ics202-project/
├── src/
│   ├── App.java          # Main entry point with command parser
│   ├── Node.java         # Abstract base class for file system nodes
│   ├── File.java         # File class implementation
│   ├── Directory.java    # Directory class with HashMap storage
│   ├── FileSystem.java   # Core file system operations
│   └── KMP.java          # KMP string matching algorithm
├── bin/                  # Compiled class files
└── README.md
```

## How to Compile

Navigate to the project directory and compile all Java files:

```bash
cd ics202-project
javac -d bin src/*.java
```

## How to Run

After compilation, run the application:

```bash
java -cp bin App
```

## Supported Commands

| Command | Description |
|---------|-------------|
| `mkdir <dir_name>` | Creates a new directory |
| `mkdir -p <path>` | Creates directories including parent directories |
| `touch <file_name> <size>` | Creates a new file with specified size |
| `echo "<text>" > <file>` | Writes text to a file |
| `ls` | Lists contents of current directory |
| `cd <path>` | Changes current directory (supports `.`, `..`, absolute, relative) |
| `pwd` | Prints current working directory |
| `rm <name>` | Removes a file or empty directory |
| `rm -r <dir>` | Recursively removes a directory and contents |
| `tree` | Displays file system structure as a tree |
| `grep "<pattern>" <file>` | Searches for pattern using KMP algorithm |
| `du` | Displays total size of current directory |
| `exit` | Exits the simulator |

## Data Structures Used

### 1. Tree Structure
- **Where**: The entire file system hierarchy
- **Why**: Natural representation of parent-child relationships between directories and files
- **How**: `Node` abstract class with `File` and `Directory` subclasses forming a tree

### 2. Hash Table (HashMap)
- **Where**: `Directory.java` - storing children
- **Why**: O(1) average-case lookup, insertion, and deletion of files/directories
- **How**: `HashMap<String, Node>` where key is name and value is the Node object

### 3. Stack
- **Where**: Path resolution in `Node.getFullPath()` and `cd` command
- **Why**: Efficient building of path strings from current node to root
- **How**: Push path components, then pop to build string

### 4. KMP String Matching Algorithm
- **Where**: `KMP.java` - used by `grep` command
- **Why**: Efficient pattern matching with O(n+m) time complexity
- **How**: Builds failure function (LPS array) to avoid redundant comparisons

### 5. Recursion
- **Where**: `tree`, `du`, `rm -r` commands
- **Why**: Natural way to traverse hierarchical tree structures
- **How**: Recursive methods that process children before/after current node

## Sample Usage

```
/$ mkdir home
/$ cd home
/home$ mkdir user
/home$ touch profile 15
/home$ ls
profile (15B) user/
/home$ echo "Hello World" > greeting.txt
/home$ grep "World" greeting.txt
Pattern "World" found in greeting.txt.
/home$ pwd
/home
/home$ cd ..
/$ tree
.
├── home/
│   ├── greeting.txt (11B)
│   ├── profile (15B)
│   └── user/
/$ du
Total size: 26B
```

## Notes

- This is a **pure in-memory simulation** - no actual files are created on disk
- File sizes are simulated (just an integer property)
- The file system starts with only the root directory `/`
- All data is lost when the program exits

## Authors

ICS-202 Lab Project - Term 251
