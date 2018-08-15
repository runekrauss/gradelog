# Gradelog by RainersRaiders

## Description

Gradelog is a JSF based web application which provides all features you need to
organise and plan your school business.

## Features

Following features are included:

* Calendar with a reminder function
* Messaging-System
* Overview about your newest feed on the dashboard
* Upload the representation plan through a CSV import
* Create your own groups
* And much more...

## Requirements

* Operating-System: Windows / Linux / Mac OS X
* Java: Java JDK 8
* Glassfish Application Server 4.1.1

## Package Maven Project

Open your terminal and navigate to the source folder. Afterwards, write the command ```mvn package```. Finally, the **WAR-File** is located in the created folder **target**. 

## Installation Glassfish

Please make sure you already installed [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

### Windows

1. Download [Glassfish 4.1.1(Java EE 7 Full Platform)](https://glassfish.java.net/download.html).
2. Unzip the file.
3. Go into the directory /glassfish4/bin/.
4. Execute asadmin.bat.
5. Enter following commands:
    ```
    start-domain
    start-database
    ```
    
The server is online and can be reached at http://localhost:4848. 

### Linux

1. Open the terminal via Strg+Alt+T.
2. Enter the following commands:
 ```
 cd /tmp
 wget http://download.oracle.com/glassfish/4.1.1/release/glassfish-4.1.1.zip
 unzip glassfish-4.1.1.zip -d ~/glassfish
 ~/glassfish/glassfish4/bin/asadmin start-domain
 ~/glassfish/glassfish4/bin/asadmin start-database
 ```
3. If you want to stop the server, open the terminal:
 ```
 ~/glassfish/glassfish4/bin/asadmin stop-domain
 ~/glassfish/glassfish4/bin/asadmin stop-database
 ```
 
The server is available at http://localhost:4848.
 
### Mac 
1. Download [Glassfish 4.1.1(Java EE 7 Full Platform)](https://glassfish.java.net/download.html).
2. Unzip the file.
3. Open the terminal and enter following commands:
```
sudo ~/glassfish4/bin/asadmin start-domain
sudo ~/glassfish4/bin/asadmin start-database
```
4. To stop the server, run these commands:
```
sudo ~/glassfish4/bin/asadmin stop-domain
sudo ~/glassfish4/bin/asadmin stop-database
```


The server is available at http://localhost:4848.

## Installation Gradelog
Make sure the server is running.

1. Open http://localhost:4848 in your browser.
2. Click **Applications** at the left sidebar.
3. Click **Deploy**.
4. Click **Choose File** at **Packaged File to Be Uploaded to the server**.
5. Select the "WAR"-File.
6. Look for **Context Root** where you can edit the root, i.e. if you change it to **gradelog**, the page will be available at http://localhost:8080/gradelog.
7. Click **Ok**.
8. The application has been installed successfully. 

