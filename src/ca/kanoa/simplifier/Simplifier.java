package ca.kanoa.simplifier;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

public class Simplifier extends JavaPlugin {

	private static Simplifier _instance;
	
	private Set<SimpleCommand> commands;
	private CommandExecutor executor;
	
	@Override
	public void onEnable() {
		_instance = this;
		saveDefaultConfig();
		commands = new HashSet<SimpleCommand>();
		for (String str : getConfig().getConfigurationSection("commands").getValues(false).keySet()) {
			commands.add(SimpleCommand.parse(str, getConfig().getConfigurationSection("commands." + str)));
		}
		registerCommands();
	}
	
	private void registerCommands() {
		CommandMap map;
		Field commandMap;
		try {
			commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMap.setAccessible(true);
			map = (CommandMap) commandMap.get(Bukkit.getServer());
			for (SimpleCommand cmd : commands) {
				map.register(":", cmd);
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public CommandExecutor getCommandExecutor() {
		return this.executor;
	}
	
	public Set<SimpleCommand> getCommands() {
		return this.commands;
	}
	
	public static Simplifier getInstance() {
		return _instance;
	}
	
}
