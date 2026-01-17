# Data Structures Implementation
## TypeMaster – Intelligent Text Engine

---

## Overview

TypeMaster heavily relies on efficient data structures to achieve real-time performance. All major data structures are implemented manually in C++ to demonstrate strong understanding of Data Structures & Algorithms.

---

## Trie (Prefix Tree)

### Purpose
- Spell checking
- Prefix-based word collection for predictions

### Description
A Trie stores characters in a tree structure where each node represents a character. Words are formed by traversing from the root to terminal nodes.

### Operations
- Insert word: O(L)
- Search word: O(L)
- Prefix search: O(L)

(L = length of word)

### Usage in Project
- Validates spelling in real time
- Collects candidate words for prediction

---

## Hash Table

### Purpose
- Fast dictionary lookup

### Description
A hash table maps words to their definitions using a hash function. Collisions are handled using separate chaining.

### Operations
- Insert: O(1) average
- Search: O(1) average

### Usage in Project
- Retrieves word definitions instantly
- Stores multiple meanings per word

---

## Max Heap

### Purpose
- Ranking word suggestions

### Description
A Max Heap prioritizes words based on similarity score. Higher score means better suggestion.

### Operations
- Insert: O(log n)
- Extract max: O(log n)

### Usage in Project
- Ranks words returned by edit distance
- Displays top suggestions to user

---

## Edit Distance Algorithm

### Purpose
- Measures similarity between words

### Description
Levenshtein Edit Distance calculates the minimum number of operations required to convert one word into another.

### Complexity
- Time: O(n × m)
- Space: O(n × m)

### Usage in Project
- Filters candidate words
- Generates confidence-based predictions

---

## Conclusion

The combination of Trie, Hash Table, Heap, and Edit Distance ensures fast, accurate, and scalable text processing throughout the system.

---

