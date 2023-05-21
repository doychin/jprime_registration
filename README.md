# JPrime Registration system

This program implements the functionality required to perform the registration of all visitors on JPrime
conference during the first and second days

## Building the program

You have to clone this repo on your local machine. You will need java 11, git, and maven.

First clone this repo:

```
git clone https://github.com/bgjug/jprime_registration
```

After that, you have to build it using maven:

```
mvn package
```

## Running the program

Before you run the program, you have to configure some settings in **microprofile-config-overrides.properties**.

The default values are:

```
org.bgjug.jprime.registration.url=http://localhost:8080
org.bgjug.jprime.registration.username=admin
org.bgjug.jprime.registration.password=password
org.bgjug.jprime.registration.year=2023
org.bgjug.jprime.registration.first.day=2023-05-30
org.bgjug.jprime.registration.second.day=2023-05-31
```

If you are going to use this program against JPrime data, you have to remove the first line. There is a
default value that points to *https://jprime.io*. The lines for username and password are just for convenience,
so you don't have to type them every time you run the program.

Make sure that this file is in the current directory from where you are starting the program.

In order to start the application, you have to use:

```
java -jar target/jprime_registration-1.0.0.jar
```

Feel free to try it. If you have any suggestions or comments, please let us know or make a PR. 

Bulgarian Java Use Group