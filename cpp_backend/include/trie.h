#ifndef TRIE_H
#define TRIE_H

#include <string>
#include <vector>
using namespace std;

struct TrieNode {
    char ch;
    bool isEnd;
    vector<TrieNode*> children;

    TrieNode(char c);
};

class Trie {
public:
    TrieNode* root;

    Trie();

    // Insert a word into the Trie
    void insert(const string& word);

    // Search for an exact word
    bool searchWord(const string& word);

    // Search if a prefix exists
    bool searchPrefix(const string& prefix);

    // Prediction helpers

    // Get the node where a prefix ends
    TrieNode* getNodeForPrefix(const string& prefix);

    // Collect all words under a node
    void collectWords(TrieNode* node, string prefix, vector<string>& words);

private:
    // Find a child node with given character
    TrieNode* findChild(TrieNode* node, char c);
};

#endif
