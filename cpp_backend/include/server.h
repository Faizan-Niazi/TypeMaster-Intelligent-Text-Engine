#pragma once

#include <winsock2.h>
#include <ws2tcpip.h>
#include <string>
#pragma comment(lib, "Ws2_32.lib")

class Server {
private:
    WSADATA wsa;
    SOCKET serverSocket;
    SOCKET clientSocket;

public:
    Server();

    bool start(int port);
    void waitForClient();
    std::string recvLine();
    void sendLine(const std::string& msg);
    void closeAll();
};
