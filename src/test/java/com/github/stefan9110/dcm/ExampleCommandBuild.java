/*
 * Copyright 2021 Stefan9110
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.stefan9110.dcm;

import com.github.stefan9110.dcm.builder.CommandBuilder;
import com.github.stefan9110.dcm.command.Command;
import com.github.stefan9110.dcm.command.CommandArgument;
import com.github.stefan9110.dcm.command.ParentCommand;
import com.github.stefan9110.dcm.command.SubCommand;
import com.github.stefan9110.dcm.manager.executor.CommandExecutor;
import com.github.stefan9110.dcm.permission.DiscordPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class ExampleCommandBuild {
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

    public ParentCommand builtSlashCommand() {
        return (ParentCommand) CommandBuilder
                .create("hello")
                // Setting a new SlashExecutor instance of the Example above
                .setCommandExecutor(new ExampleSlashExecutor())
                .setDescription("Receive a hello message")
                .setUsage("/hello")
                .build(true);
    }

    public CommandArgument exampleCommandArgument() {
        OptionType argumentType = OptionType.MENTIONABLE;
        String argumentName = "member";
        String description = "The member you want this command to process!";
        boolean isArgumentRequired = true;

        return new CommandArgument(argumentType, argumentName, description, isArgumentRequired);
    }

    public void addArgumentToBuilder(CommandBuilder builder, CommandArgument argument) {
        builder.addArgument(argument);
    }
}
