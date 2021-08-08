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

package com.github.stefan9110.manager;

import com.github.stefan9110.command.Command;
import com.github.stefan9110.command.ParentCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.stefan9110.command.ParentCommand.*;

public class CommandManager extends ListenerAdapter {
    private Guild registeredGuild;
    private final String commandPrefix;

    public CommandManager(JDA jda, String commandPrefix) {
        jda.addEventListener(this);
        this.commandPrefix = commandPrefix;
    }

    /* Message method of calling a command through the commandPrefix String */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent e) {
        // If the command is not called in the registered guild we don't want to run the command.
        if (registeredGuild != null && !registeredGuild.getId().equals(e.getGuild().getId())) return;
        // If the member is null (mostly WebHook cases) or the member is a bot we don't want to run the command.
        if (e.getMember() == null || e.getMember().getUser().isBot()) return;

        // Checking if the call message starts with the command prefix in order to differentiate between normal message and command calls.
        if (!e.getMessage().getContentDisplay().toLowerCase().startsWith(commandPrefix)) return;

        // Building the command hierarchy from the initial message
        String message = e.getMessage().getContentDisplay().substring(commandPrefix.length());
        String[] messageFormatted = message.split(" ");

        // Special case: if the call message only contained the command prefix we do not validate the call.
        if (message.equals("")) return;

        // Calling the Executor of the command
        ParentCommand cmd = getParentIncludingAliases(messageFormatted[0]);
        /*
            Make sure that the command with the name identifier given exists and checking if the command is not a SlashCommand type.
            We are treating message-called commands and slash-commands separately for the time being, it is possible that in the future
            we will make all slash-commands accessible through legacy message calls.
         */
        if (cmd != null && !cmd.isSlashCommand())
            cmd.execute(e.getMember(), (messageFormatted.length == 1 ? new String[0] : Arrays.copyOfRange(messageFormatted, 1, messageFormatted.length)), e);

        super.onGuildMessageReceived(e);
    }

    /* SlashCommand implementation method of calling a command */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent e) {
        // If the command is not called in the registered guild we don't want to run the command.
        if (e.getGuild() == null || (registeredGuild != null && !registeredGuild.getId().equals(e.getGuild().getId())))
            return;

        // Registering all the arguments from the SlashCommand implementation
        List<String> args = new ArrayList<>();
        if (e.getSubcommandName() != null) args.add(e.getSubcommandName());
        e.getOptions().forEach(option -> args.add(option.getAsString()));

        // Calling the top of the hierarchy ParentCommand found at the SlashCommand name with the build arguments.
        getParentCommand(e.getName().toLowerCase()).execute(e.getMember(),
                !args.isEmpty() ? args.toArray(new String[0]) : new String[0], e);
        super.onSlashCommand(e);
    }

    // Method used to obtain the SlashCommand implementation data from a given ParentCommand
    private static CommandData getCommandData(ParentCommand parent) {
        CommandData cmdData = new CommandData(parent.getName(), (parent.getDescription() == null ? parent.getName() : parent.getDescription()));

        // If the command doesn't have any sub-commands we will add its own CommandArgument data in the SlashCommand implementation
        if (parent.getSubCommands().isEmpty()) {
            parent.getArguments().forEach(arg -> cmdData.addOption(arg.getType(), arg.getName(), arg.getDescription(), arg.isRequired()));
            return cmdData;
        }

        // Obtaining the SlashCommand implementation data from the sub-commands of the ParentCommand given.
        List<SubcommandData> subCommandData = new ArrayList<>();
        for (Command sb : parent.getSubCommands().values()) {
            SubcommandData sbData = new SubcommandData(sb.getName(), sb.getDescription());
            sb.getArguments().forEach(arg -> sbData.addOption(arg.getType(), arg.getName(), arg.getDescription(), arg.isRequired()));
            subCommandData.add(sbData);
        }

        // Adding all the sub-command data to the main CommandData block.
        cmdData.addSubcommands(subCommandData);
        return cmdData;
    }

    public void setRegisteredGuild(Guild guild) {
        registeredGuild = guild;
    }

    /**
     * Method used to obtain all the SlashCommand implementation CommandData of the
     * top of the hierarchy ParentCommands registered in the cache.
     *
     * @return List of CommandData to be sent to the Discord API through JDA.
     */
    public List<CommandData> getSlashCommands() {
        List<CommandData> slashCommandsList = new ArrayList<>();
        getParentCommands().forEach(cmd -> {
            if (cmd.isSlashCommand()) slashCommandsList.add(getCommandData(cmd));
        });
        return slashCommandsList;
    }
}
