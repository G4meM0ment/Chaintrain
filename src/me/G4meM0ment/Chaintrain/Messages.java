package me.G4meM0ment.Chaintrain;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messages {

	static Chaintrain chaintrain;
	
	public Messages(Chaintrain plugin)
	{
		chaintrain = plugin;
	}
	
	/*
	 * gets the message from the config, replaces the %chained/%chainer with the name of the player and sends the message
	 */
	public static void moveWhileChained(Player mover)
	{		
    	String Move = chaintrain.getConfig().getString("moveWhileChainedMessage");
    	//replaces the %chained in the message with the name of the player
    	Move = Move.replace("%chained", mover.getName());    	
		mover.sendMessage(parseColors(Move));
	}
	
	public static void dropWhileChained(Player dropper)
	{
    	String Drop = chaintrain.getConfig().getString("dropWhileChainedMessage");
    	Drop = Drop.replace("%chained", dropper.getName());
		dropper.sendMessage(parseColors(Drop));
	}
	
	public static void attackWhileChained(Player damager)
	{
    	String Damage = chaintrain.getConfig().getString("attackWhileChainedMessage");
    	Damage = Damage.replace("%chained", damager.getName());
		damager.sendMessage(parseColors(Damage));
	}
	
	public static void interactWhileChained(Player interacter)
	{
    	String Interact = chaintrain.getConfig().getString("interactWhileChainedMessage");
    	Interact = Interact.replace("%chained", interacter.getName());
		interacter.sendMessage(parseColors(Interact));
	}
	
	public static void shootWhileChained(Player shooter)
	{
    	String Shoot = chaintrain.getConfig().getString("shootWhileChainedMessage");
    	Shoot = Shoot.replace("%chained", shooter.getName());
		shooter.sendMessage(parseColors(Shoot));
	}
	
	public static void disabledCommand(Player chained, String command)
	{
		String useCommand = chaintrain.getConfig().getString("disabledCommand");
		useCommand = useCommand.replace("%chained", chained.getName());
		useCommand = useCommand.replace("%command", command);
		chained.sendMessage(parseColors(useCommand));
	}
	
	public static void chained(Player clicked, Player clicker)
	{		
    	String Chained = chaintrain.getConfig().getString("chainedByMessage");
    	Chained = Chained.replace("%chained", clicked.getName());
    	Chained = Chained.replace("%chainer", clicker.getName());
    	  	
    	String Chainer = chaintrain.getConfig().getString("chainedMessage");
    	Chainer = Chainer.replace("%chained", clicked.getName());
    	Chainer = Chainer.replace("%chainer", clicker.getName());
    	
    	clicked.sendMessage(parseColors(Chained));
    	clicker.sendMessage(parseColors(Chainer));
	}
	
	public static void unchained(Player clicked, Player clicker)
	{
    	
    	String UnchainedChainer = chaintrain.getConfig().getString("unchainedChainedMessage");
    	UnchainedChainer = UnchainedChainer.replace("%chained", clicked.getName());
    	UnchainedChainer = UnchainedChainer.replace("%chainer", clicker.getName());
    	
    	String UnchainedChained = chaintrain.getConfig().getString("unchainedChainerMessage");
    	UnchainedChained = UnchainedChained.replace("%chained", clicked.getName());
    	UnchainedChained = UnchainedChained.replace("%chainer", clicker.getName());
    	
    	clicked.sendMessage(parseColors(UnchainedChained));
    	clicker.sendMessage(parseColors(UnchainedChainer));
	}
	
	//This parses the colors to useful numbers in the config
	public static String parseColors(String message){
		message = message.replace("&4", ChatColor.DARK_RED.toString());
		message = message.replace("&c", ChatColor.RED.toString());
		message = message.replace("&e", ChatColor.YELLOW.toString());
		message = message.replace("&6", ChatColor.GOLD.toString());
		message = message.replace("&2", ChatColor.DARK_GREEN.toString());
		message = message.replace("&a", ChatColor.GREEN.toString());
		message = message.replace("&b", ChatColor.AQUA.toString());
		message = message.replace("&3", ChatColor.DARK_AQUA.toString());
		message = message.replace("&1", ChatColor.DARK_BLUE.toString());
		message = message.replace("&9", ChatColor.BLUE.toString());
		message = message.replace("&d", ChatColor.LIGHT_PURPLE.toString());
		message = message.replace("&5", ChatColor.DARK_PURPLE.toString());
		message = message.replace("&f", ChatColor.WHITE.toString());
		message = message.replace("&7", ChatColor.GRAY.toString());
		message = message.replace("&8", ChatColor.DARK_GRAY.toString());
		message = message.replace("&0", ChatColor.BLACK.toString());
		return message; 
	}
	
}
