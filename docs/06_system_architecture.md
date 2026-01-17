# System Architecture
## TypeMaster – Intelligent Text Engine

---

## Architectural Overview

TypeMaster follows a **two-tier client–server architecture**:

- **Frontend (Client):** Java Swing application
- **Backend (Server):** C++ processing engine

The two components communicate using TCP sockets over localhost.

---

## Frontend Architecture (Java)

### Responsibilities
- User interaction
- Displaying results
- Collecting input
- Sending requests to backend

### Key Components
- ModernMainFrame
- NetworkClient
- TypingMasterPanel
- DictionaryLookupPanel
- SmartSuggestionsPanel
- LiveSpellCheckerPanel

---

## Backend Architecture (C++)

### Responsibilities
- Dictionary loading
- Spell validation
- Word prediction
- Definition lookup

### Core Components
- Server (Socket handling)
- Trie (Spell checking)
- Hash Table (Dictionary)
- Max Heap (Prediction ranking)

---

## Communication Model

- Protocol: TCP
- Port: 8080
- Message Format: COMMAND:DATA

### Supported Commands
- CHECK
- DEFINE
- PREDICT

---

## Advantages

- Clear separation of concerns
- High performance backend
- Responsive user interface
- Scalable and modular design

---

## Conclusion

The architecture ensures efficient processing, maintainability, and clear data flow between system components.

---

