package me.G4meM0ment.Chaintrain;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Messages {

	Chaintrain chaintrain;
	private static String title = "Chaintrain";
	private static Material mat = Material.STRING;
	
	public Messages(Chaintrain chaintrain)
	{
		this.chaintrain = chaintrain;
	}
	
	/*
	 * gets the message from the config, replaces the %chained/%chainer with the name of the player and sends the message
	 */
	public void newVersion(Player player, String line)
	{
		PluginDescriptionFile pdf = chaintrain.getDescription();
		String message = chaintrain.getConfig().getString("newVersionMessage");
		message = message.replace("%oldV", pdf.getVersion()).replace("%newV", line);
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void playerNotChained(Player player)
	{
		String message = chaintrain.getConfig().getString("playerNotChainedMessage");
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void playerAlreadyChained(Player player)
	{
		String message = chaintrain.getConfig().getString("playerAlreadyChainedMessage");
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void timeOverIn(Player player)
	{
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		long oldTime = chaintrain.getCustomConfig().getLong("players." + player.getName() +".oldTime");
		int cooldown = chaintrain.getCustomConfig().getInt("players." + player.getName() +".cooldown");
		long diff = (System.currentTimeMillis() - oldTime)/60000;
		short cooldownDiff = (short) (cooldown - diff);
		String restTime = ""+cooldownDiff;
		
		if(cooldown <= 0 || oldTime <= 0)
			restTime = "/";
		
		String message = chaintrain.getConfig().getString("timeOverInMessage");
		message = message.replace("%time", restTime);
		
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void cantFindPlayer(Player player)
	{
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		String message = chaintrain.getConfig().getString("cantFindPlayerMessage");
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void chaining(Player player, int timer)
	{
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		String message = chaintrain.getConfig().getString("chainingMessage");
		String time = String.valueOf(timer);
		message = message.replace("%timer", time);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	public void getChained(Player player, int timer)
	{
		String message = chaintrain.getConfig().getString("getChainedMessage");
		String time = String.valueOf(timer);
		message = message.replace("%timer", time);
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void moveWhileChained(Player player)
	{		
    	String message = chaintrain.getConfig().getString("moveWhileChainedMessage");
    	//replaces the %chained in the message with the name of the player
    	message = message.replace("%chained", player.getName());    	
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void dropWhileChained(Player player)
	{
    	String message = chaintrain.getConfig().getString("dropWhileChainedMessage");
        message = message.replace("%chained", player.getName());
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void attackWhileChained(Player player)
	{
    	String message = chaintrain.getConfig().getString("attackWhileChainedMessage");
    	message = message.replace("%chained", player.getName());
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void interactWhileChained(Player player)
	{
    	String message = chaintrain.getConfig().getString("interactWhileChainedMessage");
    	message = message.replace("%chained", player.getName());
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void shootWhileChained(Player player)
	{
    	String message = chaintrain.getConfig().getString("shootWhileChainedMessage");
    	message = message.replace("%chained", player.getName());
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(chaintrain.getConfig().getBoolean("useSpout") && sp.isSpoutCraftEnabled() && message.length() < 26)
		{
			sp.sendNotification(title, parseColors(message), mat);
		}
		else
		{
			player.sendMessage(parseColors(message));
		}
	}
	
	public void disabledCommand(Player player, String command)
	{
		String message = chaintrain.getConfig().getString("disabledCommand");
		message = message.replace("%chained", player.getName());
		message = message.replace("%command", command);
		player.sendMessage(parseColors(message));
	}
	
	public void chained(Player clicked, Player clicker)
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
	
	public void timeChained(Player clicked, Player clicker, String stringTime)
	{
    	String chainedOverTime = chaintrain.getConfig().getString("chainedOverTimeMessage");
    	chainedOverTime = chainedOverTime.replace("%chained", clicked.getName());
    	chainedOverTime = chainedOverTime.replace("%chainer", clicker.getName());
    	chainedOverTime = chainedOverTime.replace("%time", stringTime);
    	
    	String chainedVictimOverTime = chaintrain.getConfig().getString("chainedVictimOverTimeMessage");
    	chainedVictimOverTime = chainedVictimOverTime.replace("%chained", clicked.getName());
    	chainedVictimOverTime = chainedVictimOverTime.replace("%chainer", clicker.getName());
    	chainedVictimOverTime = chainedVictimOverTime.replace("%time", stringTime);
    	
    	clicked.sendMessage(parseColors(chainedOverTime));
    	clicker.sendMessage(parseColors(chainedVictimOverTime));
	}
	
	public void timeOver(Player player)
	{
		String timeOver = chaintrain.getConfig().getString("timeOverMessage");
		player.sendMessage(parseColors(timeOver));
	}
	
	public void unchained(Player clicked, Player clicker)
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
