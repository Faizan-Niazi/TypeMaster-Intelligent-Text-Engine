#include "../include/heap.h"

// Empty constructor
MaxHeap::MaxHeap() {
}

// Check if heap has no elements
bool MaxHeap::isEmpty() {
    return arr.empty();
}

// Insert a new item and restore heap property
void MaxHeap::insert(const string& word, int score) {
    HeapItem item{word, score};
    arr.push_back(item);
    heapifyUp((int)arr.size() - 1);
}

// Remove and return the highest-scoring word
string MaxHeap::extractMax() {
    if (arr.empty()) return "NO_SUGGESTION";

    string best = arr[0].word;
    arr[0] = arr.back();
    arr.pop_back();

    if (!arr.empty())
        heapifyDown(0);

    return best;
}

// Move element up until parent has higher score
void MaxHeap::heapifyUp(int index) {
    while (index > 0) {
        int parent = (index - 1) / 2;
        if (arr[parent].score >= arr[index].score) break;

        swap(arr[parent], arr[index]);
        index = parent;
    }
}

// Move element down until children have lower scores
void MaxHeap::heapifyDown(int index) {
    int n = (int)arr.size();
    while (true) {
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        int largest = index;
        
        if (left < n && arr[left].score > arr[largest].score)
            largest = left;
        if (right < n && arr[right].score > arr[largest].score)
            largest = right;
        if (largest == index) break;

        swap(arr[index], arr[largest]);
        index = largest;
    }
}
