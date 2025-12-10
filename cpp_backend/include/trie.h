#ifndef TRIE_H
#define TRIE_H

#include <string>
#include <vector>
using namespace std;

struct TrieNode {
    vector<TrieNode*> children;
    bool isEnd;
};

class Trie{
    private:
        TrieNode root;

    public:

    void insert(const string & s);
};
#endif