package com.pixelatedsource.jda.commands.management;

import com.pixelatedsource.jda.Helpers;
import com.pixelatedsource.jda.blub.Category;
import com.pixelatedsource.jda.blub.Command;
import com.pixelatedsource.jda.blub.CommandEvent;
import com.pixelatedsource.jda.utils.MessageHelper;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.pixelatedsource.jda.PixelSniper.PREFIX;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        this.commandName = "purge";
        this.description = "Deletes messages messages";
        this.usage = PREFIX + commandName + " [1 - 500]";
        this.category = Category.MANAGEMENT;
        this.permissions = new Permission[] {
                Permission.MESSAGE_MANAGE
        };
    }

    private ExecutorService service = new ScheduledThreadPoolExecutor(5);

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() != null) {
            if (Helpers.hasPerm(event.getMember(), commandName, 1)) {
                if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    String[] args = event.getArgs().split("\\s+");
                    if (args.length == 1 && args[0].matches("\\d+")) {
                        try {
                            Runnable run = () -> {
                                int amount = Integer.parseInt(args[0]);
                                if (amount < 1) {
                                    MessageHelper.sendUsage(this, event);
                                    return;
                                }

                                List<Message> toPurge = event.getTextChannel().getHistory().retrievePast(Integer.parseInt(args[0]) + 1).complete();
                                toPurge.forEach(blub -> MessageHelper.purgedMessages.put(blub.getId(), event.getAuthor()));
                                while (toPurge.size() > 100) {
                                    List<Message> deleteablePurgeList = new ArrayList<>();
                                    while (deleteablePurgeList.size() != 100) {
                                        deleteablePurgeList.add(toPurge.get(deleteablePurgeList.size()));
                                    }
                                    toPurge.removeAll(deleteablePurgeList);
                                    event.getTextChannel().deleteMessages(deleteablePurgeList).queue();
                                }
                                event.getTextChannel().deleteMessages(toPurge).queue();
                            };
                            service.execute(run);
                        } catch (NumberFormatException e) {
                            MessageHelper.sendUsage(this, event);
                        }
                    } else {
                        MessageHelper.sendUsage(this, event);
                    }
                } else {
                    event.reply("I have no permission to manage messages.");
                }
            } else {
                event.reply("You need the permission `" + commandName + "` to execute this command.");
            }
        } else {
            event.reply(Helpers.guildOnly);
        }
    }
}
