#ifndef TRIE_H
#define TRIE_H

#include <string>
#include <vector>
using namespace std;

class TrieNode {
public:
    vector<TrieNode*> children;
    bool isEnd;

    TrieNode() : children(26, nullptr), isEnd(false) {}
    ~TrieNode() {
        for (TrieNode* child : children) {
            delete child; // recursively free children
        }
    }
};

class Trie {
private:
    TrieNode* root;

public:
    Trie() { root = new TrieNode(); }
    ~Trie() { delete root; }

    void insertWord(const string& s);
    bool searchWord(const string& s) const;
    bool searchPrefix(const string& prefix) const;
    TrieNode* getNodeForPrefix(const string& prefix) const;
    void collectWords(TrieNode* node, string prefix, vector<string>& words) const;
};

#endif
