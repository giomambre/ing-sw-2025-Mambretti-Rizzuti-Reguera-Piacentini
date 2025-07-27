# Galaxy Trucker - Digital Adaptation

This project is a digital implementation of the popular board game "Galaxy Trucker," developed in Java. It supports multiplayer gameplay over the network using both Socket and RMI connections, and features both a graphical user interface (GUI) and a text-based user interface (TUI).

## Project Details

*   **Course Grade:** 30L/30
*   **Authors:**
    *   Giovanni Mambretti
    *   Raffaele Rizzuti
    *   Isabel Reguera
    *   Alice Piacentini

## Features

This implementation includes the complete rules of the base game and several technical features to ensure a robust and flexible experience.

| Feature                 | Status |
| ----------------------- | :----: |
| Complete Game Rules     |   ✅   |
| Text-based UI (TUI)     |   ✅   |
| Graphical UI (GUI)      |   ✅   |
| Socket-based Networking |   ✅   |
| RMI-based Networking    |   ✅   |
| Multiple Game Lobbies   |   ✅   |
| Client Disconnection Handling |   ✅   |

The core game logic, including the "Volo di prova" (test flight) model, is fully implemented and tested.

## Gameplay Screenshots

Here are a few moments from the game in action.

**Ship Building Phase:**
*Players select and place components to build their spaceship.*
![Ship building phase in the Galaxy Trucker game](https://github.com/user-attachments/assets/e2d4c0f5-4f33-418b-a874-9c966e099708)

**Flight Phase:**
*A ship encounters a meteor swarm during its journey.*
![A ship encountering a meteor swarm during the flight phase](https://github.com/user-attachments/assets/44ed50a8-7dd1-445c-bfcc-9a5bfc5ed7e6)

## How to Run the Game

The project is distributed as two executable JAR files: `GalaxyServer.jar` and `GalaxyClient.jar`.

### 1. Start the Server

The server application listens for incoming client connections.

*   **Socket Port:** `12345`
*   **RMI Port:** `1099`

To run the server, navigate to the directory containing the JAR file and execute the following command:

```bash
java -jar GalaxyServer.jar
```

### 2. Launch the Client

Once the server is running, clients can connect to it.

```bash
java -jar GalaxyClient.jar
```

When the client starts, you will be prompted to:
1.  **Enter the server's IP address.** (Leave blank for `localhost` if running on the same machine).
2.  **Choose the connection type** (Socket or RMI).
3.  **Select the user interface** (GUI or TUI).

## Test Coverage

The project is extensively tested to ensure reliability and correctness. Below are the code coverage reports from our test suites.

![Test coverage report summary](https://github.com/user-attachments/assets/f539d773-b9d9-43f9-975b-7d7b1d337b4b)

![Detailed test coverage report](https://github.com/user-attachments/assets/31012900-a424-41fd-90c9-5e4d725d7063)
