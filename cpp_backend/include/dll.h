#pragma once
#include <string>
#include <vector>

using namespace std;
class ErrorLog {
private:
    vector<string> loggedWords;
public:
    void logError(const string& word);
    string exportCSV();
};
