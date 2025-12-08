#pragma once

#include <winsock2.h>
#include <ws2tcpip.h>
#include <string>
using namespace std;
#pragma comment(lib, "Ws2_32.lib")   // *** ye line sabse important ***

class Server {
private:
    WSADATA wsa;
    SOCKET serverSocket;
    SOCKET clientSocket;

public:
    Server();

    bool start(int port);
    void waitForClient();
    string recvLine();
    void sendLine(const string& msg);
    void closeAll();
};
