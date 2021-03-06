package com.pixelatedsource.jda.commands.music;

import com.pixelatedsource.jda.Helpers;
import com.pixelatedsource.jda.blub.Category;
import com.pixelatedsource.jda.blub.Command;
import com.pixelatedsource.jda.blub.CommandEvent;
import com.pixelatedsource.jda.music.MusicManager;
import com.pixelatedsource.jda.music.MusicPlayer;
import net.dv8tion.jda.core.Permission;

import static com.pixelatedsource.jda.PixelSniper.PREFIX;

public class ClearCommand extends Command {

    public ClearCommand() {
        this.commandName = "clear";
        this.description = "Clears the queue";
        this.usage = PREFIX + this.commandName;
        this.aliases = new String[]{"cls"};
        this.category = Category.MUSIC;
        this.permissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild() != null) {
            if (Helpers.hasPerm(event.getGuild().getMember(event.getAuthor()), this.commandName, 0)) {
                MusicPlayer player = MusicManager.getManagerinstance().getPlayer(event.getGuild());
                if (player.getAudioPlayer().getPlayingTrack() != null)
                    player.getAudioPlayer().getPlayingTrack().stop();
                if (!player.getListener().getTracks().isEmpty()) player.getListener().getTracks().clear();
                event.reply("**The queue has been cleared**");
            } else {
                event.reply("You need the permission `" + commandName + "` to execute this command.");
            }
        } else {
            event.reply(Helpers.guildOnly);
        }
    }
}
