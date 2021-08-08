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

package com.github.stefan9110;


import com.github.stefan9110.command.Command;
import com.github.stefan9110.command.ParentCommand;
import com.github.stefan9110.manager.CommandManager;
import com.github.stefan9110.manager.executor.SlashExecutor;
import com.github.stefan9110.exceptions.APIAlreadyInitializedException;
import com.github.stefan9110.permission.DiscordPermission;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandManagerAPI {
    // Global instance of the API
    private static CommandManagerAPI commandManagerAPI;

    private final String commandPrefix;
    private final CommandManager commandManager;

    private CommandManagerAPI(JDA jda, String commandPrefix) {
        this.commandPrefix = commandPrefix;
        this.commandManager = new CommandManager(jda, commandPrefix);
    }

    /**
     * Method used to obtain the registered command prefix.
     *
     * @return String containing the requested prefix
     */
    public String getCommandPrefix() {
        return commandPrefix;
    }

    /**
     * Method used to register a given Command.
     * Note that the Command given as parameter must be an instance of ParentCommand.
     * Only parent commands can be found at the top of the command hierarchy.
     *
     * @param parentCommand The registered Command
     */
    public CommandManagerAPI registerCommand(@NotNull Command parentCommand) {
        if (!(parentCommand instanceof ParentCommand)) return this;
        ((ParentCommand) parentCommand).register(parentCommand.getExecutor() instanceof SlashExecutor);
        return this;
    }

    /**
     * Method used to register a ParentCommand. Create ParentCommands through the CommandBuilder class by calling
     * CommandBuilder#build(true)
     *
     * @param parentCommand The ParentCommand to register
     */
    public CommandManagerAPI registerCommand(@NotNull ParentCommand parentCommand) {
        parentCommand.register(parentCommand.getExecutor() instanceof SlashExecutor);
        return this;
    }

    /**
     * Method used to register a Discord guild as the main guild for the manager.
     * This will only allow registered commands to be used in the given guild.
     *
     * @param guild The Discord guild to be set as a main guild.
     */
    public CommandManagerAPI setRequiredGuild(@NotNull Guild guild) {
        commandManager.setRegisteredGuild(guild);
        return this;
    }

    /**
     * Method used to send the slash command data to Discord.
     * Use this method only once, when the bot is enabled.
     *
     * @param guild The guild the slash commands should be updated to.
     */
    public CommandManagerAPI updateSlashCommands(@NotNull Guild guild) {
        List<CommandData> slashCommands = commandManager.getSlashCommands();
        if (!slashCommands.isEmpty()) guild.updateCommands().addCommands(slashCommands).queue();
        return this;
    }

    /**
     * Method used to set the no-permission message for the DiscordPermission class
     * Use $perm$ in your String for it to be replaced with the name of the permission.
     *
     * @param message The String containing the no-permission message.
     */
    public CommandManagerAPI setNoPermissionMessage(String message) {
        DiscordPermission.setNoPermissionMessage(message);
        return this;
    }

    /**
     * Method used to initialize the CommandManagerAPI. Use this method only once at the bot startup.
     * Make sure that the JDA object is initialized. It is recommended to initialize the API in the onReady() event
     *
     * @param jda           The not null JDA object
     * @param commandPrefix The prefix for the non-slash commands. (example: h!)
     * @return The registered CommandManagerAPI.
     * @throws APIAlreadyInitializedException if the method is used a second time after the API was initialized.
     */
    public static CommandManagerAPI registerAPI(@NotNull JDA jda, @NotNull String commandPrefix) {
        if (commandManagerAPI != null) throw new APIAlreadyInitializedException();
        commandManagerAPI = new CommandManagerAPI(jda, commandPrefix);
        return commandManagerAPI;
    }

    public static CommandManagerAPI getAPI() {
        return commandManagerAPI;
    }
}
