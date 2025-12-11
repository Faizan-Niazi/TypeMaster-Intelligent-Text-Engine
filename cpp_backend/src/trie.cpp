#include<iostream>
#include<vector>
#include<./include/trie.h>
using namespace std;

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

bool Trie::searchPrefix (const string& s)const{
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