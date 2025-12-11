#include <iostream>
#include "../include/trie.h"
  
using namespace std;

int main() {
    Trie trie;
    trie.insertWord("apple");
    trie.insertWord("ape");
    trie.insertWord("apt");
    trie.insertWord("application");

    string s = "application";
    if (trie.searchWord(s)) {
        cout << "found\n";
    } else {
        cout << "Searching ...not found\n";
    }

    string prefix = "a";
    if (trie.searchPrefix(prefix)) {
        cout << "word with this prefix exist..\n";
    } else {
        cout << "Searching ...Prefix not found\n";
    }

    TrieNode* prefixNode = trie.getNodeForPrefix(prefix);
    vector<string> words;
    trie.collectWords(prefixNode, prefix, words);

    for (string w : words) {
        cout << w << endl;
    }

    return 0;
}
