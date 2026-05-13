# LoTerm

Locally remote terminal software made easier.
Well, not made for, but yes

## How to use LoTerm

> You need to have installed a JDK or JRE ^19 in order to run LoTerm

1. Download the `.jar` file from Releases to get the latest stable or Actions to get the latest latest version.
2. Run the `.jar` file with `java -jar LoTerm.jar` or by double-clicking it in your file explorer
3. Now you're running LoTerm server!


## How to actually use LoTerm

### 1. LoTerm .jar arguments

There are some arguments for LoTerm to make its use easier.

You can change the server port like this: `java -jar LoTerm.jar 8080`. `8080` is going to be the new port, by default it's `4545`.

You can select if you want to print the commands that are being runned by doing this: `java -jar LoTerm.jar 4545 yes`. (You have to change the default port in order for it to work properly, by default, commands aren't displayed in the screen.

And you can select if you want to display error logs and messages like this: `java -jar 4545 no yes`. (The other arguments are written by its defaults arguments, I'm changing that in a near future). By default, this is set to on.


### 2. Client talking to LoTerm server

LoTerm serves it serves with Java's ServerSocket, so this examples are written in Java but you can use other languages to communicate with LoTerm.

When you run LoTerm, this is the first output:

```
Started server at port: 4545 and IPv4: 127.0.1.1
```

This means that if you send text to 127.0.1.1:4545, the LoTerm server is going to read it.

There're a few commands you can use as a client:

- `RUN`: This basically runs a command in the Server's default shell. The text that it's going to be sent should look like this: `RUNls`. Yeah, no space between `RUN` and the actual command.
- `MOVE`: This command moves the running directory into another, because `cd` doesn't work. You should send this command like this: `MOVE/home/user/someFolder/` or in Windows `MOVEC:\Users\user\someFolder\`. Again, no space between `MOVE` and the path to the folder.
- `EXIT`: Just leave the program.


Here's an example of a client-side program written in Java:
```java
package com.company.lotermtest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.1.1", 4545)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("RUNls");
            dos.flush();
        } catch (IOException e) {
            System.err.println("Failed to send command " + 0 + ": " + e.getMessage());
        }
    }
}
```

This command hipotetically works if a LoTerm server is running at `127.0.1.1:4545` and sends a `ls` to the server.

And that's all in how LoTerm can be used.


## Working in LoTerm code

If you want to modify LoTerm code, you're free for doing that.

You can compile the project using maven like this: `mvn clean compile package` which creates a .jar in target/ folder, wait, you're supposed to know how that works, right?

And obviuosly you need to have a JDK installed.


## Why I programmed LoTerm?

In the summer of 2024, I started coding AliCode, an IDE made with Java and all that. I needed a program which runs commands in a terminal remotelly and locally, so I made this.

## License

LoTerm uses the GNU GPL v3. You can read it at [LICENSE](LICENSE) file.
