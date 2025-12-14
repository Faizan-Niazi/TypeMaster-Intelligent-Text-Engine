#include <iostream>
#include <fstream>
#include "../include/trie.h"
  #include "../include/hash.h"
using namespace std;
void loadDictionary(string &path){

    ifstream file(path);
    if(!file.is_open()) {
        cout<<"Failed to load Dictionary!......Exiting....";
        return;
    }
    string line; int count=0;

    while (getline(file,line))
    {
        if(line.empty()) continue;
        
        int pos1= line.find(',');
        int pos2= line.find(',',pos1+1);

        string word { line.substr(0,pos1)};
        string type { line.substr(pos1+1,pos2-pos1-1)};
        string definition {line.substr(pos2+1)};

        Entry e{word,type,definition};

        HashTable dictTable;
        dictTable.insert(e); // for word type and definition lookup

        Trie trie;
        trie.insertWord(word);// for word lookpu

        count++;
        if(count%5000==0) cout<<count<<" words loaded";
    }
    
}
int main() {
    string path="../dictionary.csv";
    loadDictionary(path);
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
