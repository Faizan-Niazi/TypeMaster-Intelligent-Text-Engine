# System Architecture
## TypeMaster – Intelligent Text Engine

---

## Architectural Overview

TypeMaster is designed using a **two-tier client–server architecture** to separate user interaction from computational logic.  
This separation improves performance, maintainability, and clearly demonstrates professional software design principles.

The frontend handles all user-facing operations, while the backend is responsible for text processing and data structure management.

---

## Frontend Architecture (Client)

The frontend is implemented using **Java Swing** and provides a modern desktop user interface.

### Responsibilities

- Capture user input
- Display typing exercises and statistics
- Highlight spelling errors visually
- Show word suggestions and definitions
- Communicate with backend asynchronously

### Key Components

- `ModernMainFrame` – Main application window
- `TypingMasterPanel` – Typing practice interface
- `PerformanceDashboardPanel` – Live typing analytics
- `DictionaryLookupPanel` – Dictionary search interface
- `SmartSuggestionsPanel` – Word prediction interface
- `LiveSpellCheckerPanel` – Real-time spell checker
- `NetworkClient` – TCP communication handler

---

## Backend Architecture (Server)

The backend is implemented in **C++ (C++17)** and runs as a standalone server application.

### Responsibilities

- Load dictionary data at startup
- Perform spell checking using Trie
- Retrieve definitions using Hash Table
- Generate word predictions using Edit Distance and MaxHeap
- Handle incoming client requests via TCP sockets

### Key Components

- `main.cpp` – Program entry and request dispatcher
- `server.cpp` – TCP socket management
- `trie.cpp` – Spell checking and prefix search
- `hash.cpp` – Dictionary definition storage
- `heap.cpp` – Ranking word suggestions
- `dll.cpp` – Error logging system

---

## Communication Model

Communication between frontend and backend is handled using **TCP sockets**.

### Connection Details

- **Protocol**: TCP/IP  
- **Host**: localhost (127.0.0.1)  
- **Port**: 8080  

---

## Request–Response Protocol

### Request Format

- COMMAND:parameter


### Supported Commands

| Command | Description |
|------|------------|
| `CHECK:word` | Validate spelling |
| `DEFINE:word` | Get dictionary definition |
| `PREDICT:word` | Generate similar word suggestions |
| `LOG` | Export error log |

---

### Response Format

- RESPONSE_TYPE:data
- END

This structure ensures clear message termination and avoids partial reads.

---

## Data Flow

1. User enters input in frontend  
2. Frontend sends request to backend  
3. Backend processes request using data structures  
4. Backend sends response back to frontend  
5. Frontend updates UI accordingly  

All communication is non-blocking to maintain UI responsiveness.

---

## Dictionary Loading Strategy

- Dictionary is loaded **once at server startup**
- Stored in memory using Trie and Hash Table
- Avoids repeated disk access
- Improves runtime performance

---

## Advantages of the Architecture

- Clear separation of concerns
- High performance through C++ backend
- Responsive UI due to asynchronous calls
- Easy to extend and maintain
- Demonstrates real-world system design

---

## Limitations

- Supports only a single client
- Backend runs locally
- No encryption (safe due to localhost usage)

---

## Conclusion

The system architecture of TypeMaster ensures efficiency, clarity, and scalability.  
It effectively demonstrates how data structures and networking concepts can be combined to build a real-world desktop application.

---
