package com.pixelatedsource.jda.commands.management;

import com.pixelatedsource.jda.Helpers;
import com.pixelatedsource.jda.PixelSniper;
import com.pixelatedsource.jda.blub.Category;
import com.pixelatedsource.jda.blub.Command;
import com.pixelatedsource.jda.blub.CommandEvent;
import com.pixelatedsource.jda.blub.RoleType;
import com.pixelatedsource.jda.utils.MessageHelper;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

import static com.pixelatedsource.jda.PixelSniper.PREFIX;

public class MuteCommand extends Command {

    public MuteCommand() {
        this.commandName = "mute";
        this.description = "Mute user on your server and give them a nice message in pm.";
        this.usage = PREFIX + commandName + " <member> <reason>";
        this.category = Category.MANAGEMENT;
        this.aliases = new String[]{"permmute"};
        this.permissions = new Permission[] {
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MANAGE_ROLES
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() != null) {
            if (Helpers.hasPerm(event.getMember(), commandName, 1)) {
                String[] args = event.getArgs().split("\\s+");
                if (args.length >= 2) {
                    User target = Helpers.getUserByArgsN(event, args[0]);
                    String reason = event.getArgs().replaceFirst(args[0] + "\\s+", "");
                    if (target != null) {
                        if (!SetMuteRoleCommand.muteRoles.containsKey(event.getGuild().getIdLong())) {
                            event.reply("**No mute role set!**\nCreating Role..");
                            if (event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                                long roleId = event.getGuild().getController().createRole().setColor(Color.gray).setMentionable(false).setName("muted").setPermissions(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.VOICE_CONNECT).complete().getIdLong();
                                new Thread(() -> PixelSniper.mySQL.setRole(event.getGuild().getIdLong(), roleId, RoleType.MUTE)).start();
                                SetMuteRoleCommand.muteRoles.put(event.getGuild().getIdLong(), roleId);
                                event.reply("Role created. You can change the settings of the role to your desires in the role managment tab.\nThis role wil be added to the muted users so it should have no talk permissions!");
                            } else {
                                event.reply("No permission to create roles.\n" + "You can create a role yourself with the permissions you desire and set it with " + SetPrefixCommand.prefixes.getOrDefault(event.getGuild().getIdLong(), ">") + "setmuterole <@role | roleId>\nOr give the bot role managment permissions.");
                                return;
                            }
                        }
                        new Thread(() -> {
                            if (PixelSniper.mySQL.setPermMute(event.getAuthor(), target, event.getGuild(), reason)) {
                                event.getGuild().getController().addSingleRoleToMember(event.getGuild().getMember(target), event.getGuild().getRoleById(SetMuteRoleCommand.muteRoles.get(event.getGuild().getIdLong()))).queue();
                                event.getMessage().addReaction("\u2705").queue();
                            } else {
                                event.getMessage().addReaction("\u274C").queue();
                            }
                        }).start();
                    } else {
                        event.reply("Unknown user");
                    }
                } else {
                    MessageHelper.sendUsage(this, event);
                }
            } else {
                event.reply("You need the permission `" + commandName + "` to execute this command.");
            }
        } else {
            event.reply(Helpers.guildOnly);
        }
    }
}
