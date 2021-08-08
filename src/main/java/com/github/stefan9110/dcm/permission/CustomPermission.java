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

package com.github.stefan9110.dcm.permission;

import net.dv8tion.jda.api.entities.Member;

public interface CustomPermission {
    /**
     * Method used to check if a member has the permission found at this instance.
     *
     * @param member The member the permission is checked for
     * @return Whether the member has the permission or not.
     */
    boolean hasPermission(Member member);

    /**
     * Method used to obtain the no-permission message for the permission found at this instance.
     * This String is used in the ParentCommand and SubCommand automatic checks for permission for SlashCommands, as
     * a SlashCommand requires a response even in cases the member does not have permission
     *
     * @return The String containing the no permission message
     */
    String noPermissionMessage();
}
