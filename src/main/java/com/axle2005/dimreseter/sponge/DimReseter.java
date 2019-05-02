package com.axle2005.dimreseter.sponge;

import com.axle2005.dimreseter.common.FileUtil;
import com.axle2005.dimreseter.sponge.commands.Register;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@Plugin(id = "dimreseter", name = "DimReseter", version = "1.12.2-1.0.2")
public class DimReseter {

	@Inject
	private Logger log;

	@Inject
	private PluginContainer pluginContainer;



	@Inject
	@ConfigDir(sharedRoot = false)
	private Path defaultConfig;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> mainManager;

	File location;
	Config mainConfig;


	List<String> listRestartDims = new ArrayList<String>();
	public List<String> listMonthlyDims = new ArrayList<String>();

	Date dNow = new Date();
	SimpleDateFormat ft = new SimpleDateFormat("dd");

	private Boolean voids;
	List<String> listVoidWorlds = new ArrayList<String>();

	private static DimReseter instance;

	@Listener(order = Order.LAST)
	public void onServerStart(GameStartedServerEvent event){
		
		instance = this;
		getLogger().info(UpdateChecker.checkRecommended(pluginContainer));

	
		
		mainConfig = new Config(this, defaultConfig, mainManager, "dimreseter.conf");

		listRestartDims = mainConfig.getStringlist("EveryRestartReset");
		listMonthlyDims = mainConfig.getStringlist("EveryMonthReset");
		listVoidWorlds = mainConfig.getVoidlist();
		//voids = mainConfig.getNodeChildBoolean("CustomGenerators", "Enabled");
		new Register(this, mainConfig);
		
		
		// Can not spawn platform in this event. Worlds are not loaded.

		if ((mainConfig.getNodeInt("ResetDay") == (Integer.valueOf(ft.format(dNow)))
				&& !(mainConfig.getNodeBoolean("ResetToday")))) {
			log.info("Starting Monthly Dim Resets");
			for (String dim : listMonthlyDims) {

				if (Sponge.getServer().getWorld(dim).isPresent()) {

					FileUtil.clearAllRegions(
							Sponge.getGame().getGameDirectory() + File.separator + "world" + File.separator + "" + dim);
					FileUtil.clearData(
							Sponge.getGame().getGameDirectory() + File.separator + "world" + File.separator + "" + dim);

				}
				
				if (Util.isGPPresent()) {
					Util.clearClaims(dim);
				}

				Util.spawnPlatform(dim);
				

				log.info(dim + " has been reset");
			}
			mainConfig.setValueBoolean("ResetToday", true);
			mainConfig.saveConfig();
		} else if (mainConfig.getNodeInt("ResetDay") != Integer.valueOf(ft.format(dNow))) {
			mainConfig.setValueBoolean("ResetToday", false);
		}

		for (String dim : listRestartDims) {

			if (Sponge.getServer().getWorld(dim).isPresent()) {

				FileUtil.clearAllRegions(
						Sponge.getGame().getGameDirectory() + File.separator + "world" + File.separator + "" + dim);
				FileUtil.clearData(
						Sponge.getGame().getGameDirectory() + File.separator + "world" + File.separator + "" + dim);
				
				getLogger().info("Reset Dim: " + dim);
			}

			if (Util.isGPPresent()) {
				Util.clearClaims(dim);
			}

			Util.spawnPlatform(dim);

		}

		/*if (voids == true) {
			for (String dim : listVoidWorlds) {

				setCustomModifier(dim);
			}
		}*/
		
		
		/*try {
			Sponge.getServer().createWorldProperties("spareEnd", WorldArchetypes.THE_END).setGenerateSpawnOnLoad(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


	}

	private void setCustomModifier(String input) {

		String[] w = input.split("\\|");

		try {
			World world = Sponge.getServer().getWorld(w[0]).get();
			Sponge.getServer().unloadWorld(world);
			WorldProperties properties = Sponge.getServer().getWorldProperties(w[0]).get();
			Collection<WorldGeneratorModifier> gen = new ArrayList<WorldGeneratorModifier>();
			
			try {
				WorldGeneratorModifier e = Sponge.getRegistry().getType(WorldGeneratorModifier.class, w[1]).get();
				gen.add(e);
				properties.getGeneratorModifiers().clear();
				properties.setGeneratorModifiers(gen);
				Sponge.getServer().loadWorld(properties);
			} catch (NoSuchElementException e) {
				log.error(w[1] + " is not a valid WorldGenerator");
			}
		} catch (NoSuchElementException e) {
			log.error(w[0] + " is not a valid World");
		}

	}

	public Logger getLogger() {
		return log;
	}



	public List<String> getVoidList() {
		return listVoidWorlds;
	}

	public Boolean getVoid() {
		return voids;
	}

	public static DimReseter getInstance() {
		return instance;
	}

}