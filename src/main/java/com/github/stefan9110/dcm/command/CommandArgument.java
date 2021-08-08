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

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class CommandArgument {
    private final OptionType type;
    private final String name, description;
    private final boolean required;

    /**
     * Constructor used to initialize an argument of a command given to a Command interface instance.
     *
     * @param type The JDA enum for the type of data given to the argument.
     * @param name The name of the argument. The String shall be lowercase and unique due to SlashCommand implementations.
     * @param description The description of the argument. SlashCommand implementation limitations required.
     * @param required Whether or not the argument is required in the SlashCommand usage of the argument.
     */
    public CommandArgument(OptionType type, String name, String description, boolean required) {
        this.type = type;
        this.name = name.toLowerCase();
        this.description = description;
        this.required = required;
    }

    /**
     * @return The description of the argument.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The name of the argument. The String returned should be lowercase and unique to the list of arguments for the Command this
     * instance is used in.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Whether or not this argument is required to be filled in the command it's used in.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @return The JDA implementation of the type of argument the instance shall provide to the SlashCommand instance from JDA.
     */
    public OptionType getType() {
        return type;
    }
}
