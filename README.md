# Network Packet Game

A Java-based network packet management game where players control packet flow through a network system.

## Installation

1. Ensure you have Java 8 or higher installed
2. Install Maven if not already installed
3. Clone this repository
4. Run `mvn clean package` to build the project
5. Run `java -jar target/network-game-1.0-SNAPSHOT.jar` to start the game

## Gameplay

### Controls
- **Left/Right Arrow Keys**: Control temporal progress
- **Mouse**: Drag between ports to create connections
- **S Key**: Open/Close shop
- **ESC**: Close shop/Return to menu

### Game Mechanics

#### Packets
- Square and Triangle packets move through the network
- Packets can be stored in systems (max 5 per system)
- Packets are affected by impact waves
- Packets must reach compatible ports

#### Network Systems
- Reference systems (blue) are required for packet routing
- Intermediate systems can store and forward packets
- Systems have input and output ports
- Ports must match packet type (Square/Triangle)

#### Shop Items
- **Atar (3 coins)**: Increases packet speed
- **Airyaman (4 coins)**: Reduces packet noise
- **Anahita (5 coins)**: Increases wire length

#### Level Progression
- Complete levels by achieving 50% success rate
- Each level has unique network layouts
- Difficulty increases with each level

### Sound Files Required
Place the following sound files in the `src/main/resources/sounds` directory:
- `background.wav`: Background music
- `atar.wav`: Atar effect sound
- `airyaman.wav`: Airyaman effect sound
- `anahita.wav`: Anahita effect sound
- `collision.wav`: Packet collision sound
- `success.wav`: Level completion sound
- `gameover.wav`: Game over sound

## Development

### Project Structure
- `src/main/java/com/networkgame/`
  - `controller/`: Game state and flow control
  - `model/`: Game objects and logic
  - `ui/`: User interface components
  - `util/`: Utility classes

### Building
```bash
mvn clean package
```

### Running Tests
```bash
mvn test
```

## License
This project is licensed under the MIT License - see the LICENSE file for details. 