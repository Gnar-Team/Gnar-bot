package xyz.gnarbot.gnar.commands.executors.mod;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import xyz.gnarbot.gnar.commands.handlers.Category;
import xyz.gnarbot.gnar.commands.handlers.Command;
import xyz.gnarbot.gnar.commands.handlers.CommandExecutor;

import java.util.List;

@Command(aliases = "ban",
        category = Category.MODERATION,
        guildPermissions = Permission.BAN_MEMBERS)
public class BanCommand extends CommandExecutor {
    @Override
    public void execute(Message message, List<String> args) {
        Member author = message.getMember();
        Member target = null;

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

        getServlet().getController().ban(target, 2).queue();
        message.respond().info(target.getEffectiveName() + " has been banned.").queue();
    }
}