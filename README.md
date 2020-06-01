# Hangman
A GUI game of the classic pen and paper Hangman which can be played single player or multiplayer
This project was implemented in Java. Java swing and socket programming was used for the development of the project.

## Run using maven

- Java 8 or above is required
- Maven 3.3.9 or above is recommended

Clone the repo and inside the Hangman directory
```
mvn -f server/pom.xml clean package
java -jar server/target/HangmanServer-0.0.1-SNAPSHOT.jar
```
This will start the server on port 5215 of the localhost

In a new terminal, inside the Hangman directory
```
mvn -f client/pom.xml clean package
java -jar server/target/HangmanClient-0.0.1-SNAPSHOT.jar
```
This will start a Login window(client).

To start another client, for multiplayer -
In a new terminal, inside the Hangman directory
```
java -jar server/target/HangmanServer-0.0.1-SNAPSHOT.jar
```
