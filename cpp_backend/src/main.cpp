#include<iostream>
#include "trie.cpp"
using namespace std;
// testing Trie class
int main() {
    Trie trie;
    trie.insertWord("apple");
    trie.insertWord("ape");
    trie.insertWord("apt");
    trie.insertWord("application");  //5x words inserted
string s="application";
    if (trie.searchWord(s)) {
        cout << "found";
        return 1;
    }
    cout << "not found";
    return 0;
}