#include <iostream>
#include <fstream>
#include "../include/hash.h"
#include "../include/trie.h"
#include "../include/heap.h"
#include "../include/dll.h"
#include "../include/server.h"

using namespace std;

// ----------------- HELPER FUNCTIONS -----------------

string toLowerStr(string s)
{
    for (int i = 0; i < (int)s.size(); i++)
    {
        char &c = s[i];
        if (c >= 'A' && c <= 'Z')
            c = c + 32;
    }
    return s;
}

// ----------------- EDIT DISTANCE -----------------
int editDistance(const string &a, const string &b)
{
    int n = (int)a.size(), m = (int)b.size();
    vector<vector<int>> dp(n + 1, vector<int>(m + 1));

    for (int i = 0; i <= n; i++)
        dp[i][0] = i;
    for (int j = 0; j <= m; j++)
        dp[0][j] = j;

    for (int i = 1; i <= n; i++)
    {
        for (int j = 1; j <= m; j++)
        {
            if (a[i - 1] == b[j - 1])
                dp[i][j] = dp[i - 1][j - 1];
            else
            {
                int del = dp[i - 1][j];
                int ins = dp[i][j - 1];
                int rep = dp[i - 1][j - 1];
                int smallest = del;
                if (ins < smallest)
                    smallest = ins;
                if (rep < smallest)
                    smallest = rep;
                dp[i][j] = 1 + smallest;
            }
        }
    }
    return dp[n][m];
}

// ----------------- LOAD DICTIONARY -----------------
void loadDictionaryCSV(const string &filename, HashTable &dict, Trie &trie)
{
    ifstream file(filename);
    if (!file.is_open())
    {
        cout << "Failed to open dictionary file: " << filename << "\n";
        return;
    }
    string line;
    int count = 0;
    while (getline(file, line))
    {
        if (line.empty())
            continue;
        size_t pos1 = line.find(','), pos2 = line.find(',', pos1 + 1);
        if (pos1 == string::npos || pos2 == string::npos)
            continue;

        string word = toLowerStr(line.substr(0, pos1));
        string wordtype = line.substr(pos1 + 1, pos2 - pos1 - 1);
        string def = line.substr(pos2 + 1);

        Entry e{word, wordtype, def, 1};
        dict.insert(e);
        trie.insertWord(word);

        if (++count % 5000 == 0)
            cout << "Loaded " << count << " words...\n";
    }
    cout << "Dictionary fully loaded: " << count << "\n";
}

// ----------------- GET NEAREST WORDS TRIE -----------------
vector<string> getNearestWordsTrie(const string &query, Trie &trie, int maxSuggestions = 10, int maxDistance = 3)
{
    vector<string> result;
    if (query.empty())
        return result;

    // Start from the prefix node
    string prefix = query.substr(0, 1);
    TrieNode *node = trie.getNodeForPrefix(prefix);
    if (!node)
        return result;

    // Collect all candidate words under this prefix
    vector<string> candidates;
    trie.collectWords(node, prefix, candidates);

    // Use your MaxHeap to rank suggestions
    MaxHeap heap;

    for (const string &cand : candidates)
    {
        int d = editDistance(query, cand);
        if (d <= maxDistance)
        {
            // Smaller edit distance = better match
            // Heap is max-based, so invert the score
            int score = -d;
            heap.insert(cand, score);
        }
    }

    // Extract top N suggestions
    for (int i = 0; i < maxSuggestions && !heap.isEmpty(); i++)
    {
        result.push_back(heap.extractMax());
    }

    return result;
}

// ----------------- PROCESS CLIENT REQUEST -----------------
void processRequest(const string &req, HashTable &dict, Trie &trie, ErrorLog &log, Server &server)
{
    if (req.rfind("CHECK:", 0) == 0)
    {
        string w = toLowerStr(req.substr(6));
        if (trie.searchWord(w))
            server.sendLine("SPELL:VALID\nEND\n");
        else if (trie.searchPrefix(w))
            server.sendLine("SPELL:PREFIX\nEND\n");
        else
        {
            log.logError(w);
            server.sendLine("SPELL:INVALID\nEND\n");
        }
    }
    else if (req.rfind("DEFINE:", 0) == 0)
    {
        string w = toLowerStr(req.substr(7));
        WordGroup *wg = dict.search(w);

        if (wg && !wg->entries.empty())
        {
            string out = "DEF:";
            for (int i = 0; i < (int)wg->entries.size(); i++)
            {
                out += "(" + wg->entries[i].wordtype + ") " + wg->entries[i].definition;
                if (i < (int)wg->entries.size() - 1)
                    out += "\n";
            }
            server.sendLine(out + "\nEND\n");
        }
        else
        {
            server.sendLine("DEF:NOTFOUND\nEND\n");
        }
    }
    else if (req.rfind("PREDICT:", 0) == 0)
    {
        string w = toLowerStr(req.substr(8));
        vector<string> sug = getNearestWordsTrie(w, trie, 10, 3);
        string out;
        for (int i = 0; i < (int)sug.size(); i++)
        {
            out += sug[i];
            if (i < (int)sug.size() - 1)
                out += ",";
        }
        server.sendLine("PRED:" + out + "\nEND\n");
    }
    else if (req == "LOG")
    {
        server.sendLine(log.exportCSV() + "\nEND\n");
    }
    else
    {
        server.sendLine("UNKNOWN\nEND\n");
    }
}

// ----------------- MAIN -----------------
int main()
{
    // Local variables (not global)
    HashTable dict;
    Trie trie;
    ErrorLog log;
    Server server;

    // Load dictionary
    loadDictionaryCSV("../dictionary.csv", dict, trie);

    // Start server
    if (!server.start(8080))
    {
        cerr << "Server failed\n";
        return 1;
    }
    cout << "Backend ready...\n";

    // Main server loop
    while (true)
    {
        string req = server.recvLine();
        if (req.empty())
            break;

        // Clean up request string
        while (!req.empty() && (req.back() == '\r' || req.back() == '\n'))
            req.pop_back();

        cout << "JAVA -> [" << req << "]\n";

        // Process request with local variables
        processRequest(req, dict, trie, log, server);
    }

    // Cleanup
    server.closeAll();
    return 0;
}
// sd