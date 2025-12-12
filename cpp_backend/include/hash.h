#pragma once
#include <string>
#include <vector>

using namespace std;

// =======================
// Dictionary Structures
// =======================
struct Entry {
    string word;
    string wordtype;
    string definition;
    int frequency;      // for s ranking 
};

struct WordGroup {
    string word;            // the key word
    vector<Entry> entries;  // all entries (multiple meanings)
};

// =======================
// Chained Hash Table
// =======================
class HashTable {
private:
    static const int SIZE = 200003;     // large prime for dictionary
    vector<WordGroup>* table;           // array of buckets (vectors)

    unsigned int hash(const string& s);

public:
    HashTable();
    ~HashTable();

    void insert(const Entry& e);
    WordGroup* search(const string& word);
};
