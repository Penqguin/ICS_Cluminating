# Running the Code

### For macOS/Linux (Bash/Zsh):
```bash
# Compile the code
javac -cp ".:lib/sqlite-jdbc-3.53.2.0.jar" Main.java src/*.java src/ui/*.java

# Run the code
java -cp ".:lib/sqlite-jdbc-3.53.2.0.jar" Main
```

### For Windows:
```cmd
# Compile the code
javac -cp ".;lib/sqlite-jdbc-3.53.2.0.jar" Main.java src/*.java src/ui/*.java src/util/*.java src/model/*.java

# Run the code
java -cp ".;lib/sqlite-jdbc-3.53.2.0.jar" Main
```