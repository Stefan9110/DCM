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

package com.github.stefan9110.dcm.manager.executor;

import com.github.stefan9110.dcm.manager.executor.reply.InteractionResponse;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

public abstract class SlashExecutor implements Executor {
    public void execute(Member member, String[] args, SlashCommandEvent event, InteractionHook hook) {

    }

    /**
     * @return InteractionResponse instance containing the data the SlashCommand interaction should be replied with
     */
    public abstract @NotNull InteractionResponse reply(Member member, String[] args, SlashCommandEvent event);

    /**
     * The SlashExecutor onCommand() instance first calls the SlashExecutor#reply() method and then calls the
     * SlashExecutor#execute() method with the InteractionHook parameter obtained from the reply() method.
     *
     * @param member The Member that called the command
     * @param args   List of arguments provided by the member
     * @param event  The event where the command was triggered (GuildMessageReceivedEvent or SlashCommandEvent)
     */
    @Override
    public final void onCommand(Member member, String[] args, Event event) {
        SlashCommandEvent slashEvent = (SlashCommandEvent) event;

        InteractionResponse response = reply(member, args, slashEvent);
        InteractionHook responseHook = null;
        switch (response.getResponseType()) {
            case STRING: {
                responseHook = slashEvent.reply(response.getStringResponse()).setEphemeral(response.isEphemeral()).complete();
                break;
            }
            case EMBED: {
                responseHook = slashEvent.replyEmbeds(response.getEmbedResponse()).setEphemeral(response.isEphemeral()).complete();
                break;
            }
            case MESSAGE: {
                responseHook = slashEvent.reply(response.getMessageResponse()).setEphemeral(response.isEphemeral()).complete();
                break;
            }
            case DEFFER: {
                responseHook = slashEvent.deferReply().setEphemeral(response.isEphemeral()).complete();
                break;
            }
        }

        execute(member, args, slashEvent, responseHook);
    }
}
