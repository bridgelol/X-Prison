package dev.drawethree.xprison.gangs.commands.impl;

import dev.drawethree.xprison.gangs.commands.GangCommand;
import dev.drawethree.xprison.gangs.commands.GangSubCommand;
import me.lucko.helper.Schedulers;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class GangTopSubCommand extends GangSubCommand {

	public GangTopSubCommand(GangCommand command) {
		super(command, "top", "leaderboard");
	}

	@Override
	public String getUsage() {
		return ChatColor.RED + "/gang top";
	}

	@Override
	public boolean execute(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			Schedulers.async().run(() -> {
				this.command.getPlugin().getGangsManager().sendGangTop(sender);
			});
			return true;
		}
		return false;
	}


	@Override
	public boolean canExecute(CommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabComplete() {
		return new ArrayList<>();
	}
}
