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

public:
    MaxHeap();
}