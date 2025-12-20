#include "../include/server.h"
#include <iostream>
using namespace std;
#pragma comment(lib, "ws2_32.lib")

Server::Server() {
    int res = WSAStartup(MAKEWORD(2, 2), &wsa);
    if (res != 0) {
        cout << "WSAStartup failed: " << res << "\n";
    }
    serverSocket = INVALID_SOCKET;
    clientSocket = INVALID_SOCKET;
}

bool Server::start(int port) {
    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == INVALID_SOCKET) {
        cout << "Socket creation failed\n";
        return false;
    }

    sockaddr_in serverAddr{};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_port = htons(static_cast<u_short>(port));

    if (bind(serverSocket, (sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        cout << "Bind failed\n";
        return false;
    }

    if (listen(serverSocket, 1) == SOCKET_ERROR) {
        cout << "Listen failed\n";
        return false;
    }

    cout << "Server ready. Waiting for Java...\n";
    waitForClient();
    return true;
}

void Server::waitForClient() {
    clientSocket = accept(serverSocket, NULL, NULL);
    if (clientSocket == INVALID_SOCKET) {
        cout << "Failed to accept client\n";
        return;
    }
    cout << "Client Connected!\n";
}

string Server::recvLine() {
    char buffer[2048];
    int bytes = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);
    if (bytes <= 0) return "";
    buffer[bytes] = '\0';
    return string(buffer);
}

void Server::sendLine(const string& msg) {
    send(clientSocket, msg.c_str(), static_cast<int>(msg.size()), 0);
}

void Server::closeAll() {
    if (clientSocket != INVALID_SOCKET) {
        closesocket(clientSocket);
        clientSocket = INVALID_SOCKET;
    }
    if (serverSocket != INVALID_SOCKET) {
        closesocket(serverSocket);
        serverSocket = INVALID_SOCKET;
    }
    WSACleanup();
}
