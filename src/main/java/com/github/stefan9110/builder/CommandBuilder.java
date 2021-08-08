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

package com.github.stefan9110.builder;

import com.github.stefan9110.command.Command;
import com.github.stefan9110.command.CommandArgument;
import com.github.stefan9110.command.ParentCommand;
import com.github.stefan9110.command.SubCommand;
import com.github.stefan9110.command.exceptions.CommandAlreadyExistsException;
import com.github.stefan9110.manager.executor.Executor;
import com.github.stefan9110.permission.CustomPermission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The CommandBuilder class is used for easier initialization of the two different subtypes of the Command interface.
 * After proceeding with the initialization steps the class will return a SubCommand, ParentCommand or Command value of the Command interface.
 */
public class CommandBuilder {
    private final String name;
    private String description, usage;
    private Executor executor;
    private CustomPermission requiredPermission;
    private final HashMap<String, Command> subCommands;
    private final List<CommandArgument> arguments;
    private final List<String> aliases;

    /**
     * Includes initializations of all the Lists required to create a Command.
     *
     * @param commandName The name of the command respecting the Command#getName() rules
     */
    private CommandBuilder(@NotNull String commandName) {
        this.name = commandName.toLowerCase();
        subCommands = new HashMap<>();
        arguments = new ArrayList<>();
        aliases = new ArrayList<>();
    }

    /**
     * Adds the description of the command in the builder.
     *
     * @param description The String that defines the command description
     * @return The same CommandBuilder instance containing the modified data
     */
    public CommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Adds one command alias to the list of aliases of the command being built.
     * Aliases must respect the rules bound by the Command interface.
     *
     * @param alias The lowercase unique String determining the added alias.
     * @return The same CommandBuilder instance containing the modified data.
     */
    public CommandBuilder addAlias(String alias) {
        this.aliases.add(alias.toLowerCase());
        return this;
    }

    /**
     * Adds multiple command aliases to the list of aliases of the command being built.
     * Aliases must respect the rules bound by the Command interface.
     *
     * @param aliases The lowercase unique Strings determining the added aliases.
     * @return The same CommandBuilder instance containing the modified data.
     */
    public CommandBuilder addAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
        return this;
    }

    /**
     * Adds a CommandArgument to the list of arguments for the command being built.
     * The CommandArgument given must respect the rules set by the JDA SlashCommand implementation of arguments.
     *
     * @param arg The argument added respecting the SlashCommand argument rules.
     * @return The same CommandBuilder instance containing the modified data.
     */
    public CommandBuilder addArgument(CommandArgument arg) {
        arguments.add(arg);
        return this;
    }

    /**
     * Adds an array of arguments to the list of arguments for the command being built.
     * Uses the implementation of the CommandBuilder#addArgument(CommandArgument#)
     *
     * @param args The array of arguments added to the list of CommandArgument of the built command
     * @return The same CommandBuilder instance containing the modified data.
     */
    public CommandBuilder addArguments(CommandArgument... args) {
        arguments.addAll(Arrays.asList(args));
        return this;
    }

    /**
     * Sets the usage of the Command being built.
     * Example of usage: at!mute [user] <duration>
     *
     * @param usage The string representing thr usage of the command. No limitations set. Note that the String will be used in embed
     *              implementations so proper format is required.
     * @return The same CommandBuilder instance containing the modified data.
     */
    public CommandBuilder setUsage(String usage) {
        this.usage = usage;
        return this;
    }


    /**
     * Sets the Executor of the command being built, used for the response of the Command interface call.
     * Example of Executor instance can be the CommandExecutor class used for simple call management.
     *
     * @param executor Executor instance used for the built Command.
     * @return The same CommandBuilder instance containing the modified data.
     */
    public CommandBuilder setCommandExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Adds a Command to the sub-command hierarchy of the built Command.
     * Note: a Command can contain in its sub-command hierarchy both ParentCommand and SubCommands children of the Command interface.
     *
     * @param cmd The Command used as a sub-command in the hierarchy of the
     * @return The same CommandBuilder instance containing the modified data.
     */
    public CommandBuilder addSubCommand(Command cmd) {
        if (subCommands.containsKey(cmd.getName().toLowerCase()))
            throw new CommandAlreadyExistsException(cmd.getName(), name);
        subCommands.put(cmd.getName().toLowerCase(), cmd);
        return this;
    }

    /**
     * Sets the required permission to use the built Command.
     * The CustomPermission interface contains a method to check if a JDA Member instance has the permission to
     * execute the Command in the given permission.
     * Example of custom permission: CustomPermission enum
     *
     * @param perm The CustomPermission required to execute the Command.
     * @return The same CommandBuilder instance containing the modified data.
     */
    public CommandBuilder setRequiredPermission(CustomPermission perm) {
        this.requiredPermission = perm;
        return this;
    }

    /**
     * Builds the Command given the data through the builder.
     *
     * @param parentCommand Whether or not the Command created should be considered a ParentCommand or not
     * @return ParentCommand instance of Command if the parentCommand boolean is true or SubCommand instance of
     */
    public Command build(boolean parentCommand) {
        return parentCommand ?
                new ParentCommand(name, subCommands, arguments) {
                    @Override
                    public CustomPermission getRequiredPermission() {
                        return requiredPermission;
                    }

                    @Override
                    public List<String> getAliases() {
                        return aliases;
                    }

                    @Override
                    public String getDescription() {
                        return description;
                    }

                    @Override
                    public String getUsage() {
                        return usage;
                    }

                    @Override
                    public @NotNull Executor getExecutor() {
                        return executor;
                    }
                } :
                new SubCommand(name, arguments) {
                    @Override
                    public CustomPermission getRequiredPermission() {
                        return requiredPermission;
                    }

                    @Override
                    public List<String> getAliases() {
                        return aliases;
                    }

                    @Override
                    public String getDescription() {
                        return description;
                    }

                    @Override
                    public String getUsage() {
                        return usage;
                    }

                    @Override
                    public @NotNull Executor getExecutor() {
                        return executor;
                    }
                };
    }

    /**
     * Method used to create a new CommandBuilder. The instance shall be used to collect the data
     * necessary to create a Command instance.
     *
     * @param name The name identifier of the Command that should be created.
     * @return The CommandBuilder instance requested
     */
    public static CommandBuilder create(@NotNull String name) {
        return new CommandBuilder(name);
    }
}
