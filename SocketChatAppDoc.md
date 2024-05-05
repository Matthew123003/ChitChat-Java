

---

# Socket Chat Application Documentation

## Overview

The Socket Chat Application is a simple client-server chat application that allows users to connect to a server and exchange messages with other connected clients. It consists of two main components: the server and the client.

## Running the Application

### Server

To run the server:

1. Compile the `SocketServer.java` file.
    ```bash
    javac SocketServer.java
    ```

2. Run the compiled `SocketServer.class` file.
    ```bash
    java SocketServer
    ```

3. The server will start running and wait for client connections.

### Client

To run the client:

1. Compile the `SocketClient.java` file.
    ```bash
    javac SocketClient.java
    ```

2. Run the compiled `SocketClient.class` file.
    ```bash
    java SocketClient
    ```

3. Enter the server IP address and your nickname when prompted.

4. You can now start sending and receiving messages in the chat window.

## Features

- **Real-time Chat**: Exchange messages with other connected clients in real-time.
- **User-friendly Interface**: The client application provides an intuitive interface for ease of use.
- **Dynamic IP Configuration**: The server dynamically handles IP configurations to accept incoming client connections.

## Usage

1. Launch the server by executing the `SocketServer` class.
2. Launch one or more client instances by executing the `SocketClient` class.
3. Enter the server IP address and a unique nickname for each client.
4. Start chatting with other connected clients by typing messages in the input box and pressing Enter.

## Commands

- `/list`: Display the list of connected users.

## Troubleshooting

- If you encounter any issues during the execution of the application, ensure that the server is running and that the client has entered the correct server IP address.

## Logging

- The application logs server and client activities to log files (`server.log` and `client.log` respectively) for troubleshooting purposes. Check these log files for detailed information about the application's operation.

---

