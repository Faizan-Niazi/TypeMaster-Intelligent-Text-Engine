#pragma once
#include <string>
#include <vector>

using namespace std;

// Represents an item stored in the heap
// Each item has a word and a score (higher = better)
struct HeapItem {
    string word;
    int score;      // priority valueI(min changes required to create a valid word)
};

// MaxHeap class: maintains a collection of HeapItems
// Provides insert, extractMax, and utility methods
class MaxHeap {
private:
    vector<HeapItem> arr;   // underlying storage for heap

    // Restore heap property upwards from a given index
    void heapifyUp(int index);

    // Restore heap property downwards from a given index
    void heapifyDown(int index);

public:
    // Constructor: initializes an empty heap
    MaxHeap();

    // Insert a new word with its score
    void insert(const string& word, int score);

    // Check if heap is empty
    bool isEmpty();

    // Remove and return the word with the highest score
    string extractMax();
};
