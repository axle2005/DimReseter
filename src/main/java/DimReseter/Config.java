package DimReseter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class Config {

	DimReseter plugin;

	Path defaultConfig;
	File activeConfig;
	CommentedConfigurationNode rootnode;
	ConfigurationLoader<CommentedConfigurationNode> configManager;

	List<String> listRestartDims = new ArrayList<String>(Arrays.asList("DIM1"));

	public Config(DimReseter plugin, Path defaultConfig, ConfigurationLoader<CommentedConfigurationNode> configManager,
			String fileName) {
		this.plugin = plugin;
		this.defaultConfig = defaultConfig;
		this.configManager = configManager;

		activeConfig = new File(defaultConfig.toFile(), "DimReseter.conf");

		configManager = HoconConfigurationLoader.builder().setFile(activeConfig).build();

		try {

			rootnode = configManager.load();

			defaults("DimReseter.conf");

			saveConfig();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		// saveConfig(rootnode, configManager);
		save(activeConfig);
	}

	public void saveConfig() {
		try {
			configManager.save(rootnode);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void save(File input) {

		configManager = HoconConfigurationLoader.builder().setFile(input).build();

	}

	private void defaults(String filename) {

		if (rootnode.getNode("EveryMonthReset").isVirtual()) {

			rootnode.getNode("EveryMonthReset").setComment("Resets these DIM's Monthly").setValue(listRestartDims);
		}
		if (rootnode.getNode("EveryRestartReset").isVirtual()) {

			rootnode.getNode("EveryRestartReset").setComment("Resets these DIM's Every Restart")
					.setValue(listRestartDims);
		}
		if (rootnode.getNode("ResetDay").isVirtual()) {

			rootnode.getNode("ResetDay").setComment("Reset day - Day of month - 0 to disable").setValue(0);
		}
		if (rootnode.getNode("ResetToday").isVirtual()) {

			rootnode.getNode("ResetToday").setComment("Has the server reset today?").setValue(false);
		}
	}

	public List<String> getStringlist(String node) {

		List<String> listTemp = new ArrayList<String>();
		try {
			for (String list : rootnode.getNode(node).getList(TypeToken.of(String.class))) {
				listTemp.add(list);
			}
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}

		return listTemp;
	}

	public Path getConfigDir() {
		return defaultConfig;
	}

	public int getNodeInt(String node) {
		if (rootnode.getNode(node).getValue() instanceof Integer) {
			return (int) rootnode.getNode(node).getValue();
		} else {
			return 0;
		}
	}

	public int getNodeChildInt(String node, String child) {
		if (rootnode.getNode(node, child).getValue() instanceof Integer) {
			return (int) rootnode.getNode(node, child).getValue();
		} else {
			return 0;
		}
	}

	public Boolean getNodeBoolean(String node) {
		return rootnode.getNode(node).getBoolean();

	}

	public Boolean getNodeChildBoolean(String node, String child) {
		return rootnode.getNode(node, child).getBoolean();

	}

	public String getNodeString(String node) {
		return rootnode.getNode(node).getValue().toString();

	}

	public String getNodeChildString(String node, String child) {
		return rootnode.getNode(node, child).getValue().toString();

	}

	public void setValueBoolean(String node, Boolean child) {
		rootnode.getNode(node).setValue(child);
		saveConfig();

	}

	public void setValueInt(String node, Integer child) {
		rootnode.getNode(node).setValue(child);
		save(activeConfig);
	}

	public void setValueString(String node, String child) {
		rootnode.getNode(node).setValue(child);
		save(activeConfig);

	}

	public void setValueList(String node, List<String> list_entities) {
		rootnode.getNode(node).setValue(list_entities);
		save(activeConfig);

	}

}