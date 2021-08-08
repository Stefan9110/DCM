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

import com.github.stefan9110.manager.executor.Executor;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Later in this block we will refer to the instances of the interface as `command registered through the interface`
 * representing any descendant class / variable instance that uses the class to store data.
 */
public interface Command {

    /**
     * Method used to obtain the name of the command registered through the interface.
     * Commands shall have a String unique identifier, stored lowercase and not null.
     *
     * @return The name of the command registered through the interface
     */
    @NotNull
    String getName();

    /**
     * Method used to obtain the aliases of a command registered through the interface.
     * Aliases should respect the same rules Command#getName() String presents.
     * Aliases shall be stored in lowercase form and be unique to the given Command as well as other commands.
     * In case there are no aliases this method shall return an empty List.
     *
     * @return List of Strings representing the aliases the command registered through the interface presents.
     */
    List<String> getAliases();

    /**
     * Method used to obtain the description of the command registered through the interface.
     * The description String has no restrictions in formation, but shall respect the SlashCommand description registration rules.
     *
     * @return The description of the command registered through the interface.
     */
    String getDescription();

    /**
     * Method used to obtain the usage of the command registered through the interface.
     * No limitations sets to the formation / length of the String.
     * To be noted: the String may be used in embed forms so proper indentation for embeds is required.
     * Example of command usage: "at!test [requiredArgument] <optionalArgument>.
     *
     * @return Useful information about the usage of the command registered through the interface.
     */
    String getUsage();

    /**
     * The Executor interface is the key feature of the command registered through the interface.
     * The interface value returned by the method contains the executor command which is used to execute the command when called.
     * See Executor documentation for further information.
     *
     * @return Executor interface, including methods used for the proper execution of the command registered through the interface when called.
     */
    @NotNull
    Executor getExecutor();

    /**
     * The method is used to obtain the arguments of the command registered through the interface.
     * The value returned is used in the ShlashCommand registration of the command.
     * The values stored in the List shall respect the JDA implementations of the CommandArgument class.
     * In the case of a command not containing any predefined arguments the method shall return an empty List.
     *
     * @return List of CommandArgument used for the SlashCommand identification of the command registered through the interface.
     */
    List<CommandArgument> getArguments();
}
