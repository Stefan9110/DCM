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

import com.github.stefan9110.manager.executor.SlashExecutor;
import com.github.stefan9110.manager.executor.reply.InteractionResponse;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

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
