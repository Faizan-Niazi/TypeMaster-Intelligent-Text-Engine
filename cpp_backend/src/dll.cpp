#include "../include/dll.h"
#include <fstream>
#include <ctime>
using namespace std;

void ErrorLog::logError(const string& word) {
    // check if already logged
    for (int i = 0; i < (int)loggedWords.size(); i++) {
        if (loggedWords[i] == word) return; // skip duplicate
    }
    loggedWords.push_back(word);

    // append to CSV file
    ofstream file("error_log.csv", ios::app);
    file << word << " , ";
}

string ErrorLog::exportCSV() {
    if (loggedWords.empty()) return "NO_ERRORS";

    string out = "word\n";
    for (int i = 0; i < (int)loggedWords.size(); i++) {
        out += loggedWords[i] + " , ";
    }
    return out;
}
