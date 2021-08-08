package com.github.stefan9110;

import com.github.stefan9110.permission.CustomPermission;
import net.dv8tion.jda.api.entities.Member;

public class ExamplePermission implements CustomPermission {
    // boolean value checked for a given Member instance
    @Override
    public boolean hasPermission(Member member) {
        return member.getEffectiveName().contains("cool");
    }

    // String printed for slash command implementation in case the permission requirement is not met
    @Override
    public String noPermissionMessage() {
        return "You are not cool enough to execute this command!";
    }
}
