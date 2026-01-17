TypeMaster – Intelligent Text Engine

Offline intelligent text processing and typing analysis system built using C++ and Java Swing

📌 Project Overview

TypeMaster – Intelligent Text Engine is a desktop-based application that provides real-time spell checking, intelligent word prediction, dictionary lookup, and typing performance analytics. The system works 100% offline, ensuring privacy, low latency, and reliability.

The project demonstrates practical implementation of Data Structures & Algorithms using a real-world client–server architecture.

🎯 Key Features

Real-time spell checking using Trie

Intelligent word prediction using Edit Distance + MaxHeap

Fast dictionary lookup using Hash Table

Typing speed and accuracy analysis (WPM, CPM, Accuracy)

Fully offline execution

Modern Java Swing graphical interface

TCP socket-based client–server communication

🧠 Educational Focus

This project is designed to:

Apply theoretical DSA concepts in a practical system

Demonstrate time and space complexity benefits

Showcase client–server communication using sockets

Build a complete, professional academic project

🛠 Technology Stack

Backend

Language: C++ (C++17)

Concepts: Trie, Hash Table, Heap, Socket Programming

Frontend

Language: Java (JDK 17+)

Framework: Java Swing

Architecture

Two-tier Client–Server Architecture

Communication via TCP sockets (localhost:8080)

📂 Project Structure

DSA_Project_TypeMaster_FaizanKhan_AbdulRafay/
│
├── README.md
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
│   ├── include/
│   └── src/
│
└── java_frontend/

⚙️ Quick Start

1️⃣ Clone Repository

git clone https://github.com/TypeMaster – Intelligent Text Engine.git
cd DSA_Project_TypeMaster_FaizanKhan_AbdulRafay

2️⃣ Compile Backend (C++)

cd cpp_backend/src
g++ -std=c++17 *.cpp -o TypeMaster_Backend

3️⃣ Compile Frontend (Java)

cd java_frontend
javac *.java

4️⃣ Run Application

# Terminal 1 – Start Backend
cd cpp_backend/src
./TypeMaster_Backend

# Terminal 2 – Start Frontend
cd java_frontend
java ModernMainFrame

📄 Documentation

Complete project documentation is available in the docs/ folder, including:

Project Overview

Installation & Running Guide

User Manual

Data Structures Implementation

System Architecture

UML Diagrams

Performance Analysis



📜 License

This project is released under the MIT License.

You are free to:

Use

Modify

Distribute

Share

for personal, academic, or commercial purposes, provided that the original authors are credited.

See the LICENSE file for full license text.
