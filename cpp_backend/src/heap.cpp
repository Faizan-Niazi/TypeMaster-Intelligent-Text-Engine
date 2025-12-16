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