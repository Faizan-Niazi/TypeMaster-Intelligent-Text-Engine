# TypeMaster – Intelligent Text Engine

> **Offline intelligent text processing and typing analysis system** built using **C++ (C++17)** and **Java Swing (JDK 17+)**

---

## 📌 Project Overview

**TypeMaster – Intelligent Text Engine** is a desktop-based application that provides:
- Real-time spell checking
- Intelligent word prediction
- Dictionary lookup
- Typing performance analytics (WPM, CPM, Accuracy)

The system works **100% offline**, ensuring **privacy**, **low latency**, and **reliability**.  
It demonstrates the **practical implementation of Data Structures & Algorithms** using a real-world **client–server architecture**.

---

## 🎯 Key Features

- Real-time spell checking using **Trie**
- Intelligent word prediction using **Edit Distance + MaxHeap**
- Fast dictionary lookup using **Hash Table**
- Typing speed and accuracy analysis (WPM, CPM, Accuracy)
- Fully offline execution
- Modern Java Swing graphical interface
- TCP socket-based client–server communication

---

## 🧠 Educational Focus

This project is designed to:
- Apply theoretical **DSA concepts** in a practical system
- Demonstrate **time and space complexity** benefits
- Showcase **client–server communication** using sockets
- Serve as a **complete academic project** for Data Structures courses

---

## 🛠 Technology Stack

### Backend
- **Language:** C++ (C++17)
- **Concepts:** Trie, Hash Table, Heap, Socket Programming

### Frontend
- **Language:** Java (JDK 17 or higher)
- **Framework:** Java Swing

### Architecture
- Two-tier **Client–Server Architecture**
- Communication via **TCP sockets (localhost:8080)**

---

## 📂 Project Structure

```text
DSA_Project_TypeMaster_FaizanKhan_AbdulRafay/
│
├── README.md
├── LICENSE
├── dictionary.csv
├── docs/
│   ├── 02_Project_Overview.md
│   ├── 03_Installation_and_Running.md
│   ├── 04_User_Guide.md
│   ├── 05_Data_Structures.md
│   ├── 06_System_Architecture.md
│   ├── 07_UML_Diagrams.md
│   └── 08_Performance_Analysis.md
│
├── cpp_backend/
│   ├── include/   # Header files
│   └── src/       # C++ source files
│
└── java_frontend/ # Java Swing frontend
```

---

## ⚙️ Setup & Installation

### Prerequisites

- **Java JDK 17 or higher**
- **C++ compiler (C++17 compatible)**
  - Windows: Visual Studio / MinGW
  - macOS: Clang (Xcode Command Line Tools)
  - Linux: g++

Verify installations:

```bash
java -version
javac -version
g++ --version
```

---

## 🚀 Build & Run Guide

⚠️ **Important:** Always start the **backend first**, then the **frontend**.

### 1️⃣ Clone the Repository

```bash
[git clone https://github.com/.git](https://github.com/Faizan-Niazi/TypeMaster-Intelligent-Text-Engine.git)
cd TypeMaster-Intelligent-Text-Engine
```

---

### 2️⃣ Compile Backend (C++)

```bash
cd cpp_backend/src
g++ -std=c++17 *.cpp -o TypeMaster_Backend
```

On Windows (MinGW):

```bash
g++ -std=c++17 *.cpp -o TypeMaster_Backend.exe -lws2_32
```

---

### 3️⃣ Compile Frontend (Java)

```bash
cd java_frontend
javac *.java
```

---

### 4️⃣ Run the Application

#### Terminal 1 – Start Backend

```bash
cd cpp_backend/src
./TypeMaster_Backend   # Linux / macOS
# OR
TypeMaster_Backend.exe # Windows
```

Expected output:

```text
Dictionary fully loaded
Server ready. Waiting for Java...
```

#### Terminal 2 – Start Frontend

```bash
cd java_frontend
java ModernMainFrame
```

If successful:
- Backend shows: `Client Connected!`
- Java GUI opens

---

## 📄 Documentation

Detailed documentation is available in the **docs/** folder:

- Project Overview
- Installation & Running Guide
- User Guide
- Data Structures Implementation
- System Architecture
- UML Diagrams
- Performance Analysis

---

## 📜 License

This project is released under the **MIT License**.

You are free to:
- Use
- Modify
- Distribute
- Share

for **personal, academic, or commercial purposes**, provided that the original authors are credited.

See the `LICENSE` file for full license text.

---

✅ *This README is properly formatted for GitHub and includes complete setup, build, and run instructions.*

