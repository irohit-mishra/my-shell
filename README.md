# 🐚 Java Shell Emulator
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/irohit-mishra/my-shell/blob/master/LICENSE)

A custom shell emulator written in Java that mimics Unix-like terminal behavior. It supports built-in commands, external process execution, redirection, aliasing, and more all within a Java console.


## 🚀 Features

* `cd` – Change directories
* `pwd` – Print current working directory
* `ls` / `ls -l` – List files with optional long-format info
* `echo` – Print to console
* `alias` – Define and use shell aliases
* `clear` – Clears the terminal screen
* `type` – Check if a command is built-in or from the system
* Command chaining (`;`, `&&`, `||`)
* I/O redirection (`>`, `>>`, `<`)
* External command execution (e.g., `ping`, `java`, `gcc`)
* Exit using `exit 0`


## 📦 How to Run

### Prerequisites

* Java 17 or later

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/your-repo.git
   cd your-repo
   ```

2. Compile the code:

   ```bash
   javac Main.java
   ```

3. Run the shell:

   ```bash
   java Main
   ```


## 🧪 Example Commands

```shell
$ alias ll='ls -l'
$ ll
$ cd Documents
$ pwd
$ echo Hello World > file.txt
$ cat < file.txt
$ clear
$ java -version
$ type echo
$ exit 0
```


## 📁 Project Structure

```
Main.java       # Entry point for the shell
README.md       # You are reading this
```


## 🙌 Contributing

PRs are welcome. Fork the repo, make your changes, and open a pull request.


## 📄 License

This project is open-source and licensed under the MIT License.


## 💡 Inspiration

Built to learn about:

* Java’s Process API
* Terminal emulation
* Filesystem interaction
* Shell command parsing

> *“The shell is not just a program. It’s your window to the system.”*
