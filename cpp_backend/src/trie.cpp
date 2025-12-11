
#include<vector>
#include "../include/trie.h"

using namespace std;
//name defines what it does...heehe
void Trie::insertWord (const string& s){
TrieNode* current =root;

for(int i=0; i<s.size();i++){
    int idx = s[i]-'a';
    if(current->children[idx]==nullptr){
        current->children[idx] =new TrieNode;
    }
    current=current->children[idx];
}
current->isEnd=true;
}
// searching word......here
bool Trie::searchWord (const string& s)const{
TrieNode* current =root;
 
for(int i=0; i<s.size();i++){
    int idx = s[i]-'a';
    if(current->children[idx]==nullptr){
        return false;
    }
    current=current->children[idx];
}
return current->isEnd;
}
// checks prefix exists or not ,, for it doesnt matter whether it ends on eow or not
bool Trie::searchPrefix (const string& prefix)const{
TrieNode* current =root;

for(int i=0; i<prefix.size();i++){
     int idx = prefix[i]-'a';
    if(current->children[idx]==nullptr){
        return false;
    }
    current=current->children[idx];
}
return current->isEnd;
}
// returns last node of prefix...
TrieNode* Trie::getNodeForPrefix(const string & prefix)const{
TrieNode* current =root;

for(int i=0; i<prefix.size();i++){
     int idx = prefix[i]-'a';
    if(current->children[idx]==nullptr){
        return nullptr;
    }
    current=current->children[idx];
}
return current;
}
// collects all possible words(which ends on eow) can be created with given prefix...
  void Trie::collectWords(TrieNode* node, string prefix, vector<string>& words) const {
    if (node == nullptr) return;
    if (node->isEnd) {
        words.push_back(prefix);
    }
    for (size_t i = 0; i < node->children.size(); i++) {
        TrieNode* child = node->children[i];
        if (child != nullptr) {
            char nextChar = 'a' + i;
            collectWords(child, prefix + nextChar, words);
        }
    }
}
