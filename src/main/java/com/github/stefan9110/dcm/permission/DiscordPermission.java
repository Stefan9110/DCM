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

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;

public class DiscordPermission implements CustomPermission {
    private static final HashMap<Permission, DiscordPermission> permissionCache = new HashMap<>();
    private static String noPermissionMessage = "";
    private final Permission discordPerm;

    private DiscordPermission(Permission perm) {
        this.discordPerm = perm;
    }

    /**
     * Method used to obtain a DiscordPermission instance of a given JDA Permission
     * Permissions are cached after the first request
     *
     * @param permission The requested JDA Permission
     * @return DiscordPermission instance of the requested Permission
     * @see CustomPermission
     */
    public static DiscordPermission of(Permission permission) {
        if (!permissionCache.containsKey(permission)) {
            DiscordPermission result = new DiscordPermission(permission);
            permissionCache.put(permission, result);
            return result;
        }
        return permissionCache.get(permission);
    }

    @Override
    public boolean hasPermission(Member m) {
        return m.hasPermission(discordPerm);
    }

    @Override
    public String noPermissionMessage() {
        return noPermissionMessage.replace("%perm%", discordPerm.toString());
    }

    /**
     * Method used to set the no permission message for all the DiscordPermission instances
     *
     * @param noPermMessage The no permission message (may contain %perm% placeholder that will be replaced with the JDA Permission name on call)
     * @see CustomPermission#noPermissionMessage()
     */
    public static void setNoPermissionMessage(String noPermMessage) {
        noPermissionMessage = noPermMessage;
    }
}
