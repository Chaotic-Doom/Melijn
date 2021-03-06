package com.pixelatedsource.jda.commands.util;

import com.pixelatedsource.jda.Helpers;
import com.pixelatedsource.jda.blub.Category;
import com.pixelatedsource.jda.blub.Command;
import com.pixelatedsource.jda.blub.CommandEvent;
import com.pixelatedsource.jda.utils.MessageHelper;

import static com.pixelatedsource.jda.PixelSniper.PREFIX;

public class TexttoemojiCommand extends Command {

    public TexttoemojiCommand() {
        this.commandName = "t2e";
        this.description = "Converts input text and numbers to emotes";
        this.usage = PREFIX + this.commandName + " <text>";
        this.aliases = new String[]{"TextToEmojis", "Text2Emojis"};
        this.category = Category.UTILS;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() == null || Helpers.hasPerm(event.getMember(), this.commandName, 0)) {
            if (event.getArgs().length() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : event.getArgs().split("")) {
                    if (Character.isLetter(s.toLowerCase().charAt(0))) {
                        sb.append(":regional_indicator_").append(s.toLowerCase()).append(":");
                    } else if (Character.isDigit(s.charAt(0))) {
                        sb.append(":").append(Helpers.numberToString(Integer.valueOf(s))).append(":");
                    } else {
                        if (" ".equals(s)) sb.append(" ");
                        sb.append(s);
                    }
                }
                event.reply(sb.toString());
            } else {
                MessageHelper.sendUsage(this, event);
            }
        } else {
            event.reply("You need the permission `" + commandName + "` to execute this command.");
        }
    }
}
