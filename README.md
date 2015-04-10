# Anti-NSA Chat README
##### Developed by Shaun McKeehan, Kyle Timmerman, Andrew Brusso, Michael Wallace for CS3141

#### Table of Contents

[About Project](#about-project)   
[Installation](#installation)    
[Server Usage](#server-usage)    
[Client Usage](#client-usage)    
[License](#license)    
[References](#references)    


#### About Project

Our project was to create a fully end-to-end encrypted, Java-based chat application. It has a Client GUI which allows you to input the server information, as well as a Public n, Public e, and Public d, which reduces the complexity of having to deal with certificates. See the wikipedia article on [RSA Encryption](http://en.wikipedia.org/wiki/RSA_%28cryptosystem%29#Key_generation) for how to choose values for the Public n, Public e, and Public d.

#### Installation

Installation requires the Java Runtime Environment (JRE) version 1.7. Compile and run the .java file (Server, or ClientGUI) to start Anti-NSA Chat. 

#### Server Usage

To start the Anti-NSA Chat server, compile and run Server.java. Keep in mind only one server instance can be running on a single port on the same machine at a time. The server automatically runs on port 9898, but you can also supply the port for it to run on as the first argument when running it.

#### Client Usage

To use the client, compile and run the ClientGui.java. Enter the server IP and port number and a display name to login, as well as the username of who you want to chat with(the recipient can be changed after you've logged in). Add your RSA key information, and start typing encrypted messages to send!

#### License

License details will be here

#### References

None yet
