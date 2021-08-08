[JDA]: https://github.com/DV8FromTheWorld/JDA

<img align="right" src="https://i.imgur.com/FHID1cG.png" height="230" width="230">

# Discord Command Manager

`Discord Command Manager` is a java library that helps you easily create commands for your [JDA][JDA]
Discord Bot.

## References

* [API Initialization](#api-initialization)
* [Create a command](#create-a-command)
* [Slash Commands](#slash-commands)
* [Download](#download)
* Documentation (soon)
* [License](#license)

## Basic information

This library **requires** JDA and a working JDA bot. You can read more about JDA and how to download it [here][JDA].

With `Discord Command Manager` you can create both traditional commands (normal chat commands) and slash commands.

## API Initialization

It is recommended to initialize the api when
the [ReadyEvent](https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/events/ReadyEvent.html)
is called as following:

```java
public class ExampleInitialization extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        /* Initializing CommandManagerAPI with the JDA instance from the ReadyEvent and the s! command prefix
           Note that the given command prefix will be used for all the traditional commands registered with this API. */
        CommandManagerAPI.registerAPI(event.getJDA(), "s!");
    }
}
```

After the API was initialized you can obtain it anywhere by calling:

```java
// Note: This method will return null if the API is not initialized.
CommandManagerAPI.getAPI()
```

> More stuff you might want to do in the initialization process:
> * [Register built ParentCommands](#create-a-parent-command)
> * [Update Slash Commands data](#updating-the-slash-command-data)
> * [Set no-permission message](#permissions)
> * [Set the required guild](#required-guild)

## Create a command

Now to the more interesting part: creating the actual commands!

There are two types of commands in `Discord Command Manager`:

* [ParentCommand](#create-a-parent-command)
* [SubCommand](#create-a-sub-command)

Commands in `Discord Command Manager` are built using the CommandBuilder class. This is a simple builder class that
contain methods for:

* setting command description
* setting command usage
* adding aliases
* adding pre-defined arguments (used for SlashCommands data)
* setting a command executor (method called when the command is executed by a member)
* adding sub-commands (for ParentCommands)

Example of a command being built:

```java
    public Command buildCommand() {
        return CommandBuilder
                // Initializing the CommandBuilder instance and giving the command name as the parameter
                .create("coolcommand")
                .addAliases("coolcmd", "cc")
                .setDescription("A cool command created with Discord Command Manager API!")
                .setUsage("s!coolcommand <argument>")
                // Set a permission requirement
                .setRequiredPermission(DiscordPermission.of(Permission.ADMINISTRATOR))
                .setCommandExecutor(
                        // Creating a new CommandExecutor instance
                        new CommandExecutor() {
                            @Override
                            public void execute(Member member, String[] args, GuildMessageReceivedEvent event) {
                                event.getChannel().sendMessage(member.getAsMention() + " is cool!").queue();
                            }
                        })
                // The build method returns the Command instance created
                // The parameter represents whether the Command should be registered as a ParentCommand (true) or SubCommand (false)
                .build(true);
    }
```

### Create a Parent Command

> #### Information about parent commands
> Parent commands are commands that can contain other commands in their hierarchy. These commands contained by parent commands are called **sub commands**.
>
> **TIP**: The `SubCommand` class is not the only type of command that can be considered a sub-command! A parent command can contain
> another parent command in its sub-command hierarchy. The key difference the `ParentCommand` class has is the ability
> to contain other sub commands.

**Important note**: After a top of the hierarchy (main) `ParentCommand` is built, you need to register it in order for it to be executed.
```java
    /* It is recommended to register your commands after registering the 
       CommandManagerAPI instance */
    public void register(ParentCommand command) {
        CommandManagerAPI.getAPI().registerCommand(command);
    }
```

Example of a `ParentCommand` being built

```java
    public ParentCommand buildParentCommand() {
        return (ParentCommand) CommandBuilder
                .create("say")
                .setUsage("s!greet <greetings / farewell>")
                .setDescription("Send a greeting or a farewell message to the executor!")
                .setCommandExecutor(
                        // Executor is called only if there is no sub command executed
                        new CommandExecutor() {
                            @Override
                            public void execute(Member member, String[] args, GuildMessageReceivedEvent event) {
                                event.getMessage().reply("You must specify a sub command: `greetings` or `farewell`").queue();
                            }
                        }
                )
                // Adding the greetings sub command to the ParentCommand
                .addSubCommand(CommandBuilder
                        .create("greetings")
                        .setCommandExecutor(new CommandExecutor() {
                            // Executor of the greetings sub command
                            @Override
                            public void execute(Member member, String[] args, GuildMessageReceivedEvent event) {
                                event.getMessage().reply("Greetings " + member.getAsMention()).queue();
                            }
                        })
                        // Giving the build method false as the parameter in order for the resulted command to be a SubCommand instance
                        .build(false))
                // Adding the farewell sub command to the ParentCommand
                .addSubCommand(CommandBuilder
                        .create("farewell")
                        .setCommandExecutor(new CommandExecutor() {
                            // Executor of the farewell sub command
                            @Override
                            public void execute(Member member, String[] args, GuildMessageReceivedEvent event) {
                                event.getMessage().reply("Farewell " + member.getAsMention()).queue();
                            }
                        })
                        // Giving the build method false as the parameter in order for the resulted command to be a SubCommand instance
                        .build(false))

                // Giving the build method true as the parameter in order for the resulted command to be a ParentCommand instance
                .build(true);
    }
```

**Example calls of the above command**:

`s!say greetings`,
`s!say farewell`,
`s!say`.

### Create a Sub Command

As we saw in the ParentCommand implementation, creating a sub command is just a matter of giving the `build(boolean)` method `false` as the parameter, 
in order to receive a `SubCommand` instance of the `Command` interface.

```java
    public SubCommand buildSubCommand() {
        return (SubCommand) CommandBuilder
                .create("farewell").setCommandExecutor(new CommandExecutor() {
                    @Override
                    public void execute(Member member, String[] args, GuildMessageReceivedEvent event) {
                        event.getMessage().reply("Farewell " + member.getAsMention()).queue();
                    }
                })
                // Giving the build method false as the parameter in order for the resulted command to be a SubCommand instance
                .build(false);
    }
```

## Slash Commands
You can implement slash commands similar to normal commands through the `CommandBuilder` class. 
The API makes the difference between traditional commands and slash commands from the type of `Executor` you set in the builder.

> The **CommandExecutor** type of Executor (used for traditional commands)
> 
> We have used up until this point the CommandExecutor instance to add an executor to our commands.
> This automatically marks our commands as traditional commands that use the prefix we initialized in the beginning.

> The **SlashExecutor** type of Executor (used for slash commands)
> 
> The SlashExecutor is a bit different from the CommandExecutor by having 2 methods that can be completed:
> ```java
> public @NotNull InteractionResponse reply(Member member, String[] args, SlashCommandEvent event);
> // and
> public void execute(Member member, String[] args, SlashCommandEvent event, InteractionHook hook)
> ```
> The `reply()` method is required to be completed while `execute()` is optional.

Slash commands in Discord are based off of an `InteractionHook` that needs to be answered. `Discord Command Manager` implements the 
`InteractionResponse` class used to reply to these interaction hooks. There are multiple types of interaction responses that you can choose from:
* `STRING`: Respond with a given String
* `MESSAGE`: Respond with a given JDA Message
* `EMBED`: Respond with a given MessageEmbed
* `DEFER`: Respond with a *thinking* message

You can also set this response to be *ephemeral* (only the caller of the command can see the response).

### Example SlashExecutor
```java
public class ExampleSlashExecutor extends SlashExecutor {
    @Override
    public @NotNull InteractionResponse reply(Member member, String[] args, SlashCommandEvent event) {
        // Creating a new instance of an InteractionResponse based off of a STRING
        return InteractionResponse.of("Hello")
                // Marking the response as Ephemeral
                .setEphemeral();
    }

    // The execute() method is called after the reply() method.
    @Override
    public void execute(Member member, String[] args, SlashCommandEvent event, InteractionHook hook) {
        event.getChannel().sendMessage(member.getAsMention() + " just used an ephemeral slash command.").queue();
    }
}
```

### Example SlashCommand being built

We will use the SlashExecutor created above.

```java
    public ParentCommand builtSlashCommand() {
        return (ParentCommand) CommandBuilder
                .create("hello")
                // Setting a new SlashExecutor instance of the Example above
                .setCommandExecutor(new ExampleSlashExecutor())
                .setDescription("Receive a hello message")
                .setUsage("/hello")
                .build(true);
    }
```

### Updating the slash command data

Every time you start the bot you need to send to Discord a list of data about yor registered slash commands. `Discord Command Manager` does this
automatically. All you need to do is call the `updateSlashCommands()` method in your `ReadyEvent` after initializing the API:

```java
        // Update slash commands
        CommandManagerAPI.getAPI().updateSlashCommands(event.getJDA().getGuildById("your guild id here"));
```

### Predefined command arguments

The big advantage of using slash commands is being able to set predefined arguments for your command.
You can do this by creating a `CommandArgument` instance and adding it to the command using the CommandBuilder as following:

```java
    public CommandArgument exampleCommandArgument() {
        OptionType argumentType = OptionType.MENTIONABLE;
        String argumentName = "member";
        String description = "The member you want this command to process!";
        boolean isArgumentRequired = true;

        return new CommandArgument(argumentType, argumentName, description, isArgumentRequired);
    }
```

```java
    public void addArgumentToBuilder(CommandBuilder builder, CommandArgument argument) {
        builder.addArgument(argument);
    }
```

## Other useful info

This section is dedicated to small features that make your life easier while managing commands.

### Permissions

`Discord Command Manager` has its custom permissions' system. While you can still implement Discord permission
through the `DiscordPermission` class, custom permissions can be added to commands through the `CustomPermission` 
interface.

**Example**: 
```java
public class ExamplePermission implements CustomPermission {
    // boolean value checked for a given Member instance
    @Override
    public boolean hasPermission(Member member) {
        return member.getEffectiveName().contains("cool");
    }

    // String printed for slash command implementation in case the permission requirement is not met
    @Override
    public String noPermissionMessage() {
        return "You are not cool enough to execute this command!";
    }
}
```

You can add this permission to a command using the CommandBuilder method `CommandBuilder#setRequiredPermission(CustomPermission)`

> **Note**: You can set the no-permission message for the `DiscordPermission` class using the following syntax in the `ReadyEvent`:
> ```java
> // Set the DiscordPermission no permission message.
>  CommandManagerAPI.getAPI().setNoPermissionMessage("You don't have the Discord permission $perm$ to execute this command.");
> ```

### Required Guild

You can set a Discord Guild to be the only guild commands can be executed in. The API 
automatically checks if there is a required guild set and verifies if the guild the command
was sent in is the same as the required guild.

You can set the required guild using the following syntax:
```java
// Set the required guild
// Example used in ReadyEvent (event is the ReadyEvent instance)
CommandManagerAPI.getAPI().setRequiredGuild(event.getJDA().getGuildById("your guild id here"));
```

## Download

To be added soon!

## License

This library is under the Apache License Version 2.0. See the `LICENSE` for more details.