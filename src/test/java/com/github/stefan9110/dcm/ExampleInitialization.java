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

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class ExampleInitialization extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        /* Initializing CommandManagerAPI with the JDA instance from the ReadyEvent and the s! command prefix
           Note that the given command prefix will be used for all the traditional commands registered with this API. */
        CommandManagerAPI.registerAPI(event.getJDA(), "s!");

        // NOTE: In actual implementation you can use the CommandManagerAPI instance created above as a builder for any of the following methods

        // Update slash commands
        CommandManagerAPI.getAPI().updateSlashCommands(event.getJDA().getGuildById("your guild id here"));

        // Set the DiscordPermission no permission message.
        CommandManagerAPI.getAPI().setNoPermissionMessage("You don't have the Discord permission %perm% to execute this command.");

        // Set the required guild
        CommandManagerAPI.getAPI().setRequiredGuild(event.getJDA().getGuildById("your guild id here"));
    }

    // Obtain API after initialization
    public CommandManagerAPI obtainAPI() {
        // Note: This method will return null if the API is not initialized.
        return CommandManagerAPI.getAPI();
    }
}
