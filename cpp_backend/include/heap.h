#pragma once
#include <string>
#include <vector>

using namespace std;

struct HeapItem {
    string word;
    int score;      // higher = better
};

class MaxHeap {
private:
    vector<HeapItem> arr;

    void heapifyUp(int index);
    void heapifyDown(int index);

public:
    MaxHeap();

    void insert(const string& word, int score);
    bool isEmpty();
    string extractMax();    // returns and removes best word
};
