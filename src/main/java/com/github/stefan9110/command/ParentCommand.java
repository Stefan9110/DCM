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

package com.github.stefan9110.command;

import com.github.stefan9110.command.exceptions.CommandAlreadyExistsException;
import com.github.stefan9110.permission.CustomPermission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;


import java.util.*;

public abstract class ParentCommand implements Command {
    /**
     * HashMap used for caching the main ParentCommands found at the top of any Command -> Sub-Command hierarchy.
     */
    private static final HashMap<String, ParentCommand> parentCommandCache = new HashMap<>();

    private final String name;
    private final HashMap<String, Command> subCommands;
    private final List<CommandArgument> commandArguments;
    private boolean isSlashCommand;

    /**
     * Private constructor for the ParentCommand class.
     * ParentCommand class is a child of the Command class that contains sub-commands. Used for general
     * management of complex commands.
     *
     * @param name        The name of the ParentCommand respecting the Command interface name limitation.
     * @param subCommands HashMap of Command instances representing the sub-commands registered for the ParentCommand.
     * @param arguments   List of CommandArguments used for the JDA SlashCommand registration of the command.
     */
    protected ParentCommand(@NotNull String name, @NotNull HashMap<String, Command> subCommands, @NotNull List<CommandArgument> arguments) {
        this.name = name.toLowerCase();
        this.subCommands = subCommands;
        this.commandArguments = arguments;
    }

    /**
     * @return The Command interface name String.
     */
    @Override
    public @NotNull String getName() {
        return name;
    }

    /**
     * @return The CustomPermission required to execute the Command. If the return value is null, the ParentCommand does not require
     * any permission to be executed.
     */
    public abstract CustomPermission getRequiredPermission();

    /**
     * @return The List of CommandArgument used for the SlashCommand JDA implementation of the ParentCommand
     */
    @Override
    public List<CommandArgument> getArguments() {
        return commandArguments;
    }

    /**
     * @return Whether or not the ParentCommand should be registered through JDA as a SlashCommand or not.
     */
    public boolean isSlashCommand() {
        return isSlashCommand;
    }

    /**
     * The method returns the contents of the sub-commands of the ParentCommand as a dictionary
     * presented in a HashMap of String -> Command, where the String represents the sub-command name
     * and the Command is the sub-command registered itself.
     *
     * @return HashMap dictionary of the sub-commands registered in the ParentCommand.
     */
    public HashMap<String, Command> getSubCommands() {
        return subCommands;
    }

    /**
     * Method used to register a new sub-command in the ParentCommand instance.
     *
     * @param cmd - The sub-command registered in the ParentCommand.
     */
    public void addSubCommand(Command cmd) {
        if (subCommands.containsKey(cmd.getName().toLowerCase()))
            throw new CommandAlreadyExistsException(cmd.getName(), getName());
        subCommands.put(cmd.getName().toLowerCase(), cmd);
    }

    /**
     * Method used to obtain a specific sub-command registered in the ParentCommand instance.
     *
     * @param name The identifier name of the sub-command that shall be returned. This parameter String is case insensitive.
     * @return The Command registered through the identifier name given by the String parameter in the ParentCommand instance. If there is no
     * such command registered through the identifier given as parameter the method will return null.
     */
    public Command getSubCommand(String name) {
        return subCommands.get(name.toLowerCase());
    }

    /**
     * Method used to add a new CommandArgument to the List of arguments used for the SlashCommand implementation of the ParentCommand instance.
     *
     * @param type The CommandArgument added to the List of arguments.
     * @return The instance of the ParentCommand containing the modified data.
     */
    public ParentCommand addArgument(CommandArgument type) {
        commandArguments.add(type);
        return this;
    }

    /**
     * Method used to obtain a sub-command from the List of sub-commands registered in the ParentCommand instance.
     *
     * @param key This parameter can contain either the name of the sub-command or an alias of it. The given String is case insensitive.
     * @return The sub-command having the given key parameter as its name identifier or one of its aliases. If there is no such Command found
     * the method will return null.
     */
    public Command identifySubCommand(String key) {
        for (String it : subCommands.keySet()) {
            Command cmd = subCommands.get(it);
            if (cmd.getName().equals(key.toLowerCase()) || cmd.getAliases().contains(key.toLowerCase())) return cmd;
        }
        return null;
    }

    /**
     * Method used to execute certain instructions given by all the Commands in the ParentCommand sub-command hierarchy.
     * The method also checks if the Member executor has the permission to execute all the commands in the ParentCommand hierarchy.
     * The method will trigger the JDA SlashCommand implementation reply if the Executor is an instance of a SlashCommand.
     * Note that only one Executor will be called in the last instance, the one found at the end of the hierarchy. If the command
     * has no hierarchy then the ParentCommand#getExecutor() will be executed from the given instance.
     *
     * @param memberExecutor The JDA Member that called the command.
     * @param args           The arguments presented in the initial call.
     * @param executeEvent   The event that registered tha call through the JDA event system.
     */
    public void execute(Member memberExecutor, String[] args, Event executeEvent) {
        if (getRequiredPermission() != null && !getRequiredPermission().hasPermission(memberExecutor)) {
            if (executeEvent instanceof SlashCommandEvent)
                ((SlashCommandEvent) executeEvent).reply(getRequiredPermission().noPermissionMessage()).setEphemeral(true).queue();
            return;
        }
        if (args.length > 0 && subCommands.containsKey(args[0].toLowerCase())) {
            Command toExecute = identifySubCommand(args[0].toLowerCase());
            if (toExecute instanceof ParentCommand)
                ((ParentCommand) toExecute).execute(memberExecutor, Arrays.copyOfRange(args, 1, args.length), executeEvent);
            else if (toExecute instanceof SubCommand)
                ((SubCommand) toExecute).execute(memberExecutor, Arrays.copyOfRange(args, 1, args.length), executeEvent);
            else
                toExecute.getExecutor().onCommand(memberExecutor, Arrays.copyOfRange(args, 1, args.length), executeEvent);
            return;
        }
        getExecutor().onCommand(memberExecutor, args, executeEvent);
    }

    /**
     * Method used to register the ParentCommand as a top of the hierarchy ParentCommand.
     * This method also caches the ParentCommand in the HashMap cache of the hierarchy commands.
     *
     * @param slashCommand Whether or not the ParentCommand should be registered with a SlashCommand implementation requirement.
     * @throws CommandAlreadyExistsException if the ParentCommand instance is already registered or there already exists a Command with the
     *                                       given name identifier.
     */
    public void register(boolean slashCommand) {
        if (parentCommandCache.containsKey(name)) throw new CommandAlreadyExistsException(name);
        isSlashCommand = slashCommand;
        parentCommandCache.put(name, this);
    }

    /**
     * Method used to obtain any top of the hierarchy ParentCommand by its identifier from the HashMap cache.
     *
     * @param name The name identifier String of the ParentCommand that shall be obtained. This identifier is case insensitive.
     * @return The ParentCommand registered in the cache with the give name identifier. If there is no such command registered with this
     * identifier the method will return null.
     */
    public static ParentCommand getParentCommand(String name) {
        return parentCommandCache.get(name.toLowerCase());
    }

    /**
     * Method used to obtain any top of the hierarchy ParentCommand by its identifier or by one of its aliases from the HashMap cache.
     *
     * @param key The name identifier or one of the ParentCommand aliases that it should be identified by.
     * @return The ParentCommand registered in the cache with the given name identifier or the ParentCommand that contains in one of its
     * aliases list the given key parameter. If there is no such ParentCommand found in the cache the method will return null.
     */
    public static ParentCommand getParentIncludingAliases(String key) {
        for (ParentCommand cmd : parentCommandCache.values()) {
            if (cmd.getName().equals(key.toLowerCase()) || cmd.getAliases().contains(key.toLowerCase())) return cmd;
        }
        return null;
    }

    /**
     * Method used to obtain a Set of all the name identifiers of the ParentCommands registered as top of the hierarchy in the cache.
     *
     * @return The requested Set of Strings.
     */
    public static Set<String> getParentCommandNames() {
        return parentCommandCache.keySet();
    }

    /**
     * Method used to obtain a List of all the ParentCommands registered as top of the hierarchy in the cache.
     *
     * @return The requested List of ParentCommand.
     */
    public static List<ParentCommand> getParentCommands() {
        return new ArrayList<>(parentCommandCache.values());
    }
}
