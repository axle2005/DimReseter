package com.axle2005.dimreseter.sponge.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import com.axle2005.dimreseter.sponge.Config;
import com.axle2005.dimreseter.sponge.DimReseter;





public class CommandDimAdd implements CommandExecutor {
	DimReseter plugin;
	Config config;


	Collection<World> worlds;
	List<String> worldnames = new ArrayList<String>();

	public CommandDimAdd(DimReseter plugin, Config config) {
		this.plugin = plugin;
		this.config = config;
		
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext arguments) throws CommandException {
		String args = arguments.getOne("Dim|Here").toString();

		if (args.equalsIgnoreCase("Optional[here]")) {
			args = "here";
		} else {
			args = args.substring(9, args.length() - 1);
		}
		worlds = Sponge.getServer().getWorlds();
		for (World w : worlds) {
			worldnames.add(w.getName());
		
		}
		

		if (src instanceof Player && !src.hasPermission("dimreset.manage.add")) {
			Player player = (Player) src;
			player.sendMessage(Text.of("You do not have permission to use this command!"));
			return CommandResult.empty();

		} else {

			if (args.equals("here")) {
				Player player = (Player) src;
				plugin.listMonthlyDims.add(getWorldInfo(player.getWorld().getName()));
				config.setValueList("EveryMonthReset", plugin.listMonthlyDims);
				config.saveConfig();
				src.sendMessage(Text.of("Added "+args+" to config"));
				return CommandResult.success();

			} else if (worldnames.contains(args)) {
				if(!plugin.listMonthlyDims.contains(getWorldInfo(args)))
				{
					plugin.listMonthlyDims.add(getWorldInfo(args));
					config.setValueList("EveryMonthReset", plugin.listMonthlyDims);
					config.saveConfig();
				}
				src.sendMessage(Text.of("Added "+args+" to config"));
				return CommandResult.success();
			}
			
			else {
				src.sendMessage(Text.of("/dimreset <add><Dim|Here>"));
				return CommandResult.empty();

			}

		}
	}

	public String getWorldInfo(String dimName) {
		World world = Sponge.getServer().getWorld(dimName).get();
		String info = "";

		info = world.getName();
		return info;
	}
}
