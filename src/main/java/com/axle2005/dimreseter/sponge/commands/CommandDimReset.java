package com.axle2005.dimreseter.sponge.commands;

import java.io.File;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.axle2005.dimreseter.common.FileUtil;
import com.axle2005.dimreseter.sponge.DimReseter;
import com.axle2005.dimreseter.sponge.Util;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimManager;

public class CommandDimReset implements CommandExecutor {

	DimReseter plugin;
	public CommandDimReset(DimReseter plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext arguments) throws CommandException {
		if(!src.hasPermission("dimreseter.admin")){
			return CommandResult.empty();
		} else {
			//String argOption = arguments.<String>getOne("run|schedule|abort").get();
			String argDim = arguments.<String>getOne("dimname").get();
			
			Optional<World> world = Sponge.getServer().getWorld(argDim);
			 
			if(world.isPresent()) {
				String path = Sponge.getGame().getGameDirectory() + File.separator + "world" + File.separator + world.get().getName();
				if(world.get().getName().equals("world"))
				{
					path = Sponge.getGame().getGameDirectory() + File.separator + "world";
				}
				Sponge.getServer().unloadWorld(world.get());
				
				if (DimReseter.getInstance().isGPPresent()) {
					ClaimManager cm = DimReseter.getGPApi().getClaimManager(world.get());
					for (Claim c : cm.getWorldClaims()) {
						if(c.isAdminClaim()) {
							src.sendMessage(Text.of(TextColors.AQUA,"Claim: "+c.getUniqueId()+" has been deleted"));
							cm.deleteClaim(c);
						}
					}
				}
				
				
				FileUtil.clearAllRegions(path);
				FileUtil.clearData(path);
				Sponge.getServer().loadWorld(world.get().getName());
				Util.spawnPlatform(world.get().getName());
			}
			
			
			src.sendMessage(Text.of(TextColors.AQUA,"Dimension: "+argDim+" has been reset"));
			return CommandResult.empty();
		}

	}
}
