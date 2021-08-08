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

package com.github.stefan9110.dcm.command;

import com.github.stefan9110.dcm.permission.CustomPermission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SubCommand implements Command {
    private final String name;
    private final List<CommandArgument> arguments;

    /**
     * Creating a SubCommand instance is not allowed through the general constructor. It should be created through the CommandBuilder instead.
     * This constructor is used in the CommandBuilder class to generate a new instance of a SubCommand.
     *
     * @param name The name identifier of the SubCommand.
     * @param arguments The List of CommandArguments for the SubCommand instance used in the SlashCommand JDA implementation.
     */
    protected SubCommand(@NotNull String name, @NotNull List<CommandArgument> arguments) {
        this.name = name.toLowerCase();
        this.arguments = arguments;
    }

    /**
     * @return The name identifier of the SubCommand instance.
     */
    @NotNull
    @Override
    public String getName() {
        return name;
    }

    /**
     * Method used to obtain the list of CommandArgument used for the SlashCommand JDA implementation of the SubCommand instance.
     * @return The requested List of CommandArgument.
     */
    @Override
    public List<CommandArgument> getArguments() {
        return arguments;
    }

    /**
     * Method used to obtain the required permission to execute the SubCommand.
     * @return The requested permission. If the SubCommand does not required a permission to be executed the method shall return null.
     */
    public abstract CustomPermission getRequiredPermission();

    /**
     * Method used to execute code when the SubCommand is called.
     * This method is usually called through the ParentCommand#execute() method through the Command hierarchy.
     * The method checks if the JDA Member has the required permission to execute the Command, if there is a permission requirement.
     * If the JDA Member does not have permission to execute the Command and the SubCommand is found in a SlashCommand hierarchy,
     * the SlashCommand interaction is completed automatically with a no-permission message.
     *
     * @param memberExecutor The JDA Member that called the command.
     * @param args The argument array used in the execution of the command.
     * @param executeEvent The event that triggered the command.
     */
    public void execute(Member memberExecutor, String[] args, Event executeEvent) {
        if (getRequiredPermission() != null && !getRequiredPermission().hasPermission(memberExecutor)) {
            if (executeEvent instanceof SlashCommandEvent)
                ((SlashCommandEvent) executeEvent).reply(getRequiredPermission().noPermissionMessage()).setEphemeral(true).queue();
            return;
        }
        getExecutor().onCommand(memberExecutor, args, executeEvent);
    }
}
