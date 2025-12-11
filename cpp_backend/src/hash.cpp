#include "../include/hash.h"

HashTable::HashTable() {
    table = new vector<WordGroup>[SIZE];
}

HashTable::~HashTable() {
    delete[] table;
}

unsigned int HashTable::hash(const string& s) { //polynomial rolling hash
    unsigned int h = 0;
    for (int i = 0; i < (int)s.size(); i++) {
        h = (h * 31u + (unsigned char)s[i]) % SIZE;
    }
    return h;
}

void HashTable::insert(const Entry& e) {
    unsigned int idx = hash(e.word);

    // Check if word group already exists
    for (int i = 0; i < (int)table[idx].size(); i++) {
        if (table[idx][i].word == e.word) {
            table[idx][i].entries.push_back(e);
            return;
        }
    }

    // New word group
    WordGroup wg;
    wg.word = e.word;
    wg.entries.push_back(e);

    table[idx].push_back(wg);
}

WordGroup* HashTable::search(const string& word) {
    unsigned int idx = hash(word);

    for (int i = 0; i < (int)table[idx].size(); i++) {
        if (table[idx][i].word == word) {
            return &table[idx][i];
        }
    }
    return nullptr;
}
