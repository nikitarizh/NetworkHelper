# NetworkHelper
## What is it?
A tool to make network administrating easier
## What does it do?
- Tells you about new connections to your network
- Tells you about disconnections from your network
## What is it built with?
Java
## How to use?
1. Compile NetworkHelper.java to a class
```
javac -d ./build NetworkHelper.java
```
2. Make a jar file out of the NetworkHelper.class
```
jar -cfe ./build/NetworkHelper.jar NetworkHelper ./build/NetworkHelper.class
```
3. Open NetworkHelper.jar using your terminal
