# ChessGame
A multiplayer online chess application that allows pairs of players to play simultaneously in real time.

### Features
* Play live chess games against other online players
* View official chess rules
* Track player stats: wins, losses, and ties
* View a leaderboard of top players

### Technologies Used
* Java – Core language for backend logic
* Spring MVC – Structured server-side architecture and RESTful APIs
* WebSocket – Enabled real-time gameplay and communication between players
* JPA – Simplified database interaction and entity management
* MySQL – Stored user data, game results, and leaderboards

### Why These Technologies
* Java + Spring MVC offer a robust and scalable backend environment
* WebSocket was ideal for real-time interaction compared to HTTP polling
* JPA made it easier to map relational data to Java objects cleanly
* MySQL provided a reliable and familiar relational database for managing structured data

### Notes
* Players must be logged in to access any features
* A game starts automatically when a second player joins the queue
* The player who joins first will play as white
