package xyz.gnarbot.gnar.commands.executors.mod;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import xyz.gnarbot.gnar.commands.handlers.Category;
import xyz.gnarbot.gnar.commands.handlers.Command;
import xyz.gnarbot.gnar.commands.handlers.CommandExecutor;

import java.util.List;

@Command(aliases = "kick",
        usage = "-user",
        category = Category.MODERATION,
        guildPermissions = Permission.KICK_MEMBERS)
public class KickCommand extends CommandExecutor {
    @Override
    public void execute(Message message, List<String> args) {
        Member author = getServlet().getMember(message.getAuthor());
        Member target = null;

//        if (!author.hasPermission(note.getTextChannel(), Permission.KICK_MEMBERS)) {
//            note.respond().error("You do not have permission to kick.").queue();
//            return;
//        }

        if (message.getMentionedChannels().size() >= 1) {
            target = getServlet().getMember(message.getMentionedUsers().get(0));
        } else if (args.size() >= 1) {
            target = getServlet().getMemberByName(args.get(0), false);
        }

        if (target == null) {
            message.respond().error("Could not find user.").queue();
            return;
        }

        if (!author.canInteract(target)) {
            message.respond().error("Sorry, that user has an equal or higher role.").queue();
            return;
        }

        getServlet().getController().kick(target).queue();
        message.respond().info(target.getEffectiveName() + " has been kicked.").queue();
    }
}
