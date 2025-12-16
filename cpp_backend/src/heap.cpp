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
