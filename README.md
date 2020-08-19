# NetworkHelper
## What is it?
A tool to make network administrating easier <br />
But actually I'm just learning Java by myself, so this thing will be kinda useless for you
## What does it do?
- Tells you about new connections to your network
- Tells you about disconnections from your network
- ...
## What is it built with?
Java
## How to use?
**Make sure you are executing these commands from project root**
- Server side
 1. Compile server classes
 ```
 javac -d ./bin ./src/server/*.java
 ```
 2. cd to a folder with compiled classses
 ```
 cd bin
 ```
 3. Make a jar file
 ```
 jar -cfe NetworkHelperServer.jar server.Main server/*.class
 ```
 4. Open NetworkHelperServer.jar using your terminal
 ```
 java -jar NetworkHelperServer.jar
 ```
- Client side
 1. Compile client classes
 ```
 javac -d ./bin ./src/client/*.java
 ```
 2. cd to a folder with compiled classses
 ```
 cd bin
 ```
 3. Make a jar file
 ```
 jar -cfe NetworkHelperClient.jar client.Main client/*.class
 ```
 4. Open NetworkHelperClient.jar using your terminal
 ```
  java -jar NetworkHelperClient.jar
 ```