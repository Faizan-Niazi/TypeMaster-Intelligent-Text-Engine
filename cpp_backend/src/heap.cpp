#include "../include/heap.h"

MaxHeap::MaxHeap() {
}

bool MaxHeap::isEmpty() {
    return arr.empty();
}

void MaxHeap::insert(const string& word, int score) {
    HeapItem item;
    item.word = word;
    item.score = score;
    arr.push_back(item);

    heapifyUp((int)arr.size() - 1);
}

void MaxHeap::heapifyUp(int index) {
    while (index > 0) {
        int parent = (index - 1) / 2;
        if (arr[parent].score >= arr[index].score) break;

        HeapItem temp = arr[parent];
        arr[parent] = arr[index];
        arr[index] = temp;

        index = parent;
    }
}

// Initial structure
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

        HeapItem temp = arr[index];
        arr[index] = arr[largest];
        arr[largest] = temp;

        index = largest;
    }
}
