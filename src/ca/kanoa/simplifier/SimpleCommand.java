package ca.kanoa.simplifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import ca.kanoa.simplifier.util.Range;

public class SimpleCommand extends Command {

	private final String name;
	private final String description;
	private final String help;
	private final String perm;
	private final Range arguments;
	private final String[] commands;
	
	//To prevent code injection
	private int key;
	private Random generator;

	public SimpleCommand(String name, String description, String help, String perm, String arguments, String[] commands, String[] aliases) {
		super(name, description, help, Arrays.asList(aliases != null ? aliases : new String[]{}));
		this.name = name;
		this.description = create(description);
		this.help = create(help);
		this.perm = create(perm);
		this.arguments = new Range(create(arguments));
		this.commands = commands;
		
		generator = new Random();
	}
	
	private String create(String str) {
		return str == null ? "" : str;
	}

	public void execute(CommandSender player, String[] args) {
		for (String str : commands) {
			String command = format(str, player, args);
			if (player instanceof Player) {
				((Player) player).chat(command);
			} else {
				Bukkit.getServer().dispatchCommand(player, command.startsWith("/") ? command.substring(1) : command);
			}
		}
	}

	private String format(String command, CommandSender player, String[] args) {
		command = command.replace("{player}", player.getName());
		command = command.replace("{sender}", player.getName());
		cleanArguments(args);
		int argPos;
		String str;
		while ((argPos = command.indexOf("{args:")) != -1) {
			str = command.substring(argPos);
			str = str.substring(str.indexOf(':') + 1, str.indexOf('}'));
			Range argRange = new Range(str);
			str = command.substring(argPos, command.indexOf('}') + 1);
			str = str.replace("{", "\\{").replace("}", "\\}");
			command = command.replaceFirst(str, getArguments(argRange, args));
		}
		command = fixArguments(command);
		return command;
	}
	
	/**
	 * Removes all instances of the characters "{" and "}" from everything string so the formatter can work without error.
	 * <br>
	 * Don't forgot to run fixArguements afterwards
	 * @param args
	 */
	private void cleanArguments(String[] args) {
		key = generator.nextInt();
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replace("{", key + "O");
			args[i] = args[i].replace("}", key + "C");
		}
	}
	
	private String fixArguments(String formattedArgs) {
		return formattedArgs.replace(key + "O", "{").replace(key + "C", "}");
	}

	private String getArguments(Range range, String[] args) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			if (range.contains(i+1)) {
				builder.append(args[i]).append(' ');
			}
		}
		return builder.toString().trim();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getPerm() {
		return perm;
	}

	public Range getArguments() {
		return arguments;
	}

	public String[] getCommands() {
		return commands;
	}

	public String getHelp() {
		return help;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (sender.hasPermission(getPerm()) || sender.isOp()) {
			if (getArguments().contains(args.length)) {
				execute(sender, args);
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid arguments!");
				sender.sendMessage(ChatColor.RED + getHelp());
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
		}
		return true;
	}
	
	public static SimpleCommand parse(String name, ConfigurationSection config) {
		return new SimpleCommand(name, 
				config.getString("description"), 
				config.getString("help"), 
				config.getString("permission"), 
				config.getString("arguments"), 
				config.getList("commands").toArray(new String[0]), 
				config.getList("aliases", new ArrayList<String>()).toArray(new String[0]));
	}

}
