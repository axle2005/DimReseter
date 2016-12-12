package DimReseter;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import com.google.inject.Inject;

import DimReseter.Commands.Register;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "dimreseter", name = "DimReseter", version = "1.0.0")
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

	List<String> listRestartDims = new ArrayList<String>();
	public List<String> listMonthlyDims = new ArrayList<String>();

	Date dNow = new Date();
	SimpleDateFormat ft = new SimpleDateFormat("dd");

	Reset r = new Reset(this);

	@Listener
	public void preInitialization(GamePreInitializationEvent event) {

		mainConfig = new Config(this, defaultConfig, mainManager, "DimReseter.conf");

		listRestartDims = mainConfig.getStringlist("EveryRestartReset");
		listMonthlyDims = mainConfig.getStringlist("EveryMonthReset");

	}

	@Listener
	public void initialization(GameInitializationEvent event) {
		new Register(this, mainConfig);
	}

	@Listener
	public void gameStart(GameAboutToStartServerEvent event) {
		// Can not spawn platform in this event. Worlds are not loaded.
	
		if ((mainConfig.getNodeInt("ResetDay") == (Integer.valueOf(ft.format(dNow)))
				&& !(mainConfig.getNodeBoolean("ResetToday")))) {
			log.info("Starting Monthly Dim Resets");
			for (String dim : listMonthlyDims) {
				r.deleteRegions(dim);
				log.info(dim + " has been reset");
			}
			mainConfig.setValueBoolean("ResetToday", true);
			mainConfig.saveConfig();
		} else if(mainConfig.getNodeInt("ResetDay") != Integer.valueOf(ft.format(dNow)))
		{
			mainConfig.setValueBoolean("ResetToday", false);
		}

		for (String dim : listRestartDims) {

			r.deleteRegions(dim);

		}

	}

	@Listener
	public void gameStarting(GameStartingServerEvent event) {
		for (String dim : listRestartDims) {
			r.spawnPlatform(dim);
		}
		for (String dim : listMonthlyDims) {
			r.spawnPlatform(dim);
		}

	}

	public Logger getLogger() {
		return log;
	}

}
