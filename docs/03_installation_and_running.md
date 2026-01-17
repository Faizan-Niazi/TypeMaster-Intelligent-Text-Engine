# Installation and Running Guide  
## TypeMaster – Intelligent Text Engine

---

## System Requirements

Before running TypeMaster, ensure the following requirements are met.

### Operating System
- Windows 10 / 11  
- macOS 10.14 or later  
- Linux (Ubuntu / Debian / Fedora)

### Software Requirements
- **Java JDK 17 or higher**
- **C++17 compatible compiler**
  - Windows: Visual Studio / MinGW
  - macOS: Clang (Xcode Command Line Tools)
  - Linux: g++

### Hardware Requirements
- Minimum 4 GB RAM (8 GB recommended)
- At least 500 MB free disk space

---

## Repository Setup

Clone the project repository using Git:

```bash
git clone https://github.com/TypeMaster – Intelligent Text Engine.git
cd DSA_Project_TypeMaster_FaizanKhan_AbdulRafay
```

Ensure that `dictionary.csv` exists in the project root directory.

---

## Backend Compilation (C++)

Navigate to the backend source directory:

```bash
cd cpp_backend/src
```

### Compile Backend

#### Windows (MinGW)
```bash
g++ -std=c++17 *.cpp -o TypeMaster_Backend.exe -lws2_32
```

#### macOS
```bash
clang++ -std=c++17 *.cpp -o TypeMaster_Backend
```

#### Linux
```bash
g++ -std=c++17 *.cpp -o TypeMaster_Backend
```

After compilation, an executable file will be generated in the same directory.

---

## Frontend Compilation (Java)

Navigate to the frontend directory:

```bash
cd java_frontend
```

Compile all Java source files:

```bash
javac *.java
```

Ensure that `.class` files are generated successfully.

---

## Running the Application

⚠️ **Important:** The backend must be started **before** the frontend.

---

### Step 1: Start Backend Server

Open a terminal and run:

```bash
cd cpp_backend/src
./TypeMaster_Backend
```

Expected output:

```
Dictionary fully loaded
Server ready. Waiting for Java...
```

Leave this terminal **open**.

---

### Step 2: Start Frontend Application

Open a **new terminal window** and run:

```bash
cd java_frontend
java ModernMainFrame
```

If the connection is successful, the graphical user interface will launch.

---

## Verifying Successful Execution

- Backend terminal displays: `Client Connected!`
- Frontend shows a connected status indicator
- All application panels are accessible

---

## Stopping the Application

1. Close the Java GUI window  
2. Press `Ctrl + C` in t