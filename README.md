Tank Battle Game (Java Swing)
A 2D top-down tank combat game developed in Java, focusing on object-oriented design and game state management

Features:
Core Mechanics: Smooth player movement and shooting system controlled via keyboard ("X" to fire).
Randomized Enemies: Enemy tanks that move and fire at semi-random intervals, featuring a spawning logic.
Combat System: Bullet physics, including tank destruction and explosion animations using Piazza-provided assets.
Environment: Static walls that surround the map and block movement/bullets.

UI & Menus: 
* Pause Menu: Ability to resume, restart, or exit the game.
* Game Over: Triggers upon losing all lives, with options to restart or quit.
* Scoring: Real-time score tracking and life management

Technical Requirements:
Java Version: 1.8.0_xxx (Required for compilation and execution).
Assets: All textures and sprites are consistent with the provided Piazza folder.

How to Run:
Open your terminal in the project's main folder.

Compile the code:
javac *.java

Run the application:
java Main

Controls:
Arrows / WASD: Move Tank 
X: Fire Bullet 
P / ESC: Pause Game

Known Issues (Incomplete Parts):
Wall Collision: A bug exists where pressing two movement keys simultaneously (e.g., Up + Right) may allow the tank to bypass wall collisions.
Enemy Spawning: Occasionally, enemy tanks may spawn directly on top of wall assets.

Have Fun!
