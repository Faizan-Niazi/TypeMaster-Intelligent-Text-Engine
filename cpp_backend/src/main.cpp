#include <iostream>
#include <fstream>
#include "../include/hash.h"
#include "../include/trie.h"
 
using namespace std;

string toLowerStr(string s) {
    for (int i = 0; i < (int)s.size(); i++) {
        char& c = s[i];
        // if (c >= 'A' && c <= 'Z')
        //    c = c - 'A' + 'a';
        if (c >= 'A' && c <= 'Z') c = c + 32;
    }
    return s;
}
Trie trie;
// ----------------- LOAD DICTIONARY -----------------
void loadDictionaryCSV(const string& filename, HashTable& dict, Trie& trie) {
    ifstream file(filename);
    if (!file.is_open()) {
        cout << "Failed to open dictionary file: " << filename << "\n";
        return;
    }
    string line; int count = 0;
    while (getline(file, line)) {
        if (line.empty()) continue;
        size_t pos1 = line.find(','), pos2 = line.find(',', pos1 + 1);
        if (pos1 == string::npos || pos2 == string::npos) continue;

        string word = toLowerStr(line.substr(0, pos1));
        string wordtype = line.substr(pos1 + 1, pos2 - pos1 - 1);
        string def = line.substr(pos2 + 1);

        Entry e{ word, wordtype, def, 1 };
        dict.insert(e);
        trie.insertWord(word);

        if (++count % 100 == 0) cout << "Loaded " << count << " words...\n";
    }
    cout << "Dictionary fully loaded: " << count << "\n";
}

int main() {
    HashTable dict;
    Trie trie;
    loadDictionaryCSV("../dictionary.csv", dict, trie);

    trie.insertWord("apple");
    trie.insertWord("ape");
    trie.insertWord("apt");
    trie.insertWord("application");

    string s = "application";
    cout << (trie.searchWord(s) ? "found\n" : "Searching ...not found\n");

    string prefix = "a";
    cout << (trie.searchPrefix(prefix) ? "word with this prefix exist..\n"
        : "Searching ...Prefix not found\n");

    TrieNode* prefixNode = trie.getNodeForPrefix(prefix);
    vector<string> words;
    trie.collectWords(prefixNode, prefix, words);

    for (string w : words) cout << w << endl;
}
