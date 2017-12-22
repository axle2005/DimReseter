package DimReseter;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.google.inject.Inject;

import DimReseter.Commands.Register;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "dimreseter", name = "DimReseter", version = "1.0.2", dependencies = @Dependency(id = "griefprevention", optional = true))
public class DimReseter {

	@Inject
	private Logger log;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path defaultConfig;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> mainManager;

	File location;
	Config mainConfig;

	// Used to clear claims from reset dimensions
	private GriefPreventionApi gpApi;
	private Boolean gpPresent = false;

	List<String> listRestartDims = new ArrayList<String>();
	public List<String> listMonthlyDims = new ArrayList<String>();

	Date dNow = new Date();
	SimpleDateFormat ft = new SimpleDateFormat("dd");

	private Boolean voids;
	List<String> listVoidWorlds = new ArrayList<String>();

	Reset r = new Reset(this);

	@Listener
	public void preInitialization(GamePreInitializationEvent event) {

		mainConfig = new Config(this, defaultConfig, mainManager, "DimReseter.conf");

		listRestartDims = mainConfig.getStringlist("EveryRestartReset");
		listMonthlyDims = mainConfig.getStringlist("EveryMonthReset");
		listVoidWorlds = mainConfig.getVoidlist();
		voids = mainConfig.getNodeChildBoolean("CustomGenerators", "Enabled");

	}

	@Listener
	public void initialization(GameInitializationEvent event) {
		new Register(this, mainConfig);
	}

	@Listener(order = Order.LATE)
	public void onServerStart(GameStartedServerEvent event) throws Exception {
		Optional<PluginContainer> optGpContainer = Sponge.getPluginManager().getPlugin("griefprevention");
		if (optGpContainer.isPresent()) {
			this.gpApi = GriefPrevention.getApi();
			log.info("Loaded GriefPrevention API");
			gpPresent = true;

		}
		// Can not spawn platform in this event. Worlds are not loaded.

		if ((mainConfig.getNodeInt("ResetDay") == (Integer.valueOf(ft.format(dNow)))
				&& !(mainConfig.getNodeBoolean("ResetToday")))) {
			log.info("Starting Monthly Dim Resets");
			for (String dim : listMonthlyDims) {
				r.deleteRegions(dim);
				r.clearClaims(dim, gpPresent);
				log.info(dim + " has been reset");
			}
			mainConfig.setValueBoolean("ResetToday", true);
			mainConfig.saveConfig();
		} else if (mainConfig.getNodeInt("ResetDay") != Integer.valueOf(ft.format(dNow))) {
			mainConfig.setValueBoolean("ResetToday", false);
		}

		for (String dim : listRestartDims) {

			r.deleteRegions(dim);
			r.clearClaims(dim, gpPresent);

		}

		if (voids == true) {
			for (String dim : listVoidWorlds) {

				setCustomModifier(dim);
			}
		}

		for (String dim : listRestartDims) {

			r.spawnPlatform(dim);

		}
		for (String dim : listMonthlyDims) {
			r.spawnPlatform(dim);
		}

	}

	@Listener
	public void gameStarted(GameStartedServerEvent event) {

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

	public GriefPreventionApi getGPApi() {
		return gpApi;
	}

	public List<String> getVoidList() {
		return listVoidWorlds;
	}

	public Boolean getVoid() {
		return voids;
	}

}