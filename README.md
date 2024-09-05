# CommandLibrary

### Overview of CommandLibrary

CommandLibrary is a lightweight yet powerful command processing framework designed specifically for the Minecraft Bukkit platform, tailored to work with versions 1.8.x up to the latest available. It simplifies the development process by providing a convenient and efficient way to manage commands in your server plugins.

---

### Getting Started

#### Installation

To use CommandLibrary, you need to follow these steps:

1. **Download**: Obtain the latest version of CommandLibrary from its official repository or plugin website.
2. **Add to Your Project**: Place the JAR file into the `lib` folder of your Minecraft Bukkit project.
3. **Include Dependency**: Ensure that your project is configured to include the dependency in your build path.

#### Usage

CommandLibrary can be integrated into your Bukkit plugins with ease. Here's a brief guide on how to get started:

1. **Import CommandLibrary**: Include necessary imports for command handling within your plugin files.
2. **Create Commands**: Define your commands using annotations.
3. **Register Commands**: Use the library's methods to register your commands with the server's command map.
4. **Handle Permissions**: Assign permissions to commands if needed.

#### Example Code

```java
import cn.monshine.commandlibrary.CommandTopic;
import cn.monshine.commandlibrary.annotation.Command;
import cn.monshine.commandlibrary.annotation.CommandTopicGetter;
import cn.monshine.commandlibrary.annotation.Param;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyCommandClass {
    @Command(names = {"mycommand"}, description = "This is a simple command")
    public void myCommand(Player player) {
        player.sendMessage("Hello, World!");
    }

    @Command(names = {"mycommand greet greetTo"}, description = "Greet to someone", playerOnly = false)
    public void greeting(CommandSender sender, @Param(name = "target") Player target, @Param(name = "message", wildcard = true) String message) {
        // we provide a transformer that automatically solves argument to player
        // wildcard accepts all arguments in the tail of the command.
        target.sendMessage("Greeting from " + sender.getName() + ": " + message);
    }

    @Command(names = {"mycommand greet greetAll"}, description = "Greet to everyone", playerOnly = false)
    public void greeting(CommandSender sender, @Param(name = "message", wildcard = true) String message) {
        // wildcard accepts all arguments in the tail of the command.
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.sendMessage("Greeting from " + sender.getName() + ": " + message);
        }
    }

    @CommandTopicGetter(label = "mycommand greet")
    public static CommandTopic greetTopicGetter() {
        return new CommandTopic("Greeting Commands",
                ImmutableList.of("Say greeting messages",
                        "to someone or everyone!"
                )
        );
    }
}
```

To register this command, you would call `CommandLibrary.registerCommands(<your command class>)` in your plugin's main class.

---

### Features

- **Flexible Command Registration**: Register commands using annotations or the traditional Java method.
- **Permission Management**: Assign permissions to commands and check them before execution.
- **Parameter Handling**: Define parameters for your commands with ease, including flags and variable arguments.