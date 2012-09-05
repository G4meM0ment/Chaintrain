package me.G4meM0ment.Chaintrain.Messages;

import me.G4meM0ment.Chaintrain.Chaintrain;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Messenger {
	
	private Chaintrain plugin;
	
	private static String title = "Chaintrain";
	private static Material mat = Material.STRING;
	
	public Messenger(Chaintrain plugin) {
		this.plugin = plugin;
	}
	
	public void send(Player p, Messages msg) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		if(plugin.checkSpout(p)) spout(p, message);
		else					 chat(p, message);
	}
	
	public void sendVersion(Player p, Messages msg, String v) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		if(plugin.checkSpout(p)) spout(p, message.replace("%v", v));
		else					 chat(p, message.replace("%v", v));
	}
	
	public void sendMoney(Player p, Messages msg, String m) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		if(plugin.checkSpout(p)) spout(p, message.replace("%m", m));
		else					 chat(p, message.replace("%m", m));
	}
	
	public void sendMinMoney(Player p, Messages msg) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		String m = Integer.toString(plugin.getConfig().getInt("minBountyAmount"));
		if(plugin.checkSpout(p)) spout(p, message.replace("%m", m));
		else					 chat(p, message.replace("%m", m));
	}
	
	public void sendBroadcastAndPlayer(Player p, Messages msg, Messages bmsg) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		String cMessage = plugin.getConfig().getString(bmsg.toString(), bmsg.msg());
		if(plugin.checkSpout(p)) spout(p, message);
		else					 chat(p, message);
		broadcast(cMessage);
	}
	
	public void sendTimeOverIn(Player p, Messages msg) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		long oldTime = plugin.getCustomConfig().getLong("players." + p.getName() +".oldTime");
		int cooldown = plugin.getCustomConfig().getInt("players." + p.getName() +".cooldown");
		long diff = (System.currentTimeMillis() - oldTime)/60000;
		short cooldownDiff = (short) (cooldown - diff);
		String restTime = ""+cooldownDiff;
		if(cooldown <= 0 || oldTime <= 0)
		restTime = "/";
		
		if(plugin.checkSpout(p)) spout(p, message.replace("%t", restTime));
		else					 chat(p, message.replace("%t", restTime));
	}
		
	public void sendChained(Player p, Player p2, Messages msg, Messages msg2) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		String message2 = plugin.getConfig().getString(msg2.toString(), msg2.msg());
		if(plugin.checkSpout(p)){ spout(p, message.replace("%p", p2.getName()));
								  spout(p2, message2.replace("%p", p.getName())); }
		else					{ chat(p, message.replace("%p", p2.getName()));
								  chat(p2, message2.replace("%p", p.getName())); }
	}
	
	public void sendUnchained(Player p, Player p2, Messages msg, Messages msg2) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		String message2 = plugin.getConfig().getString(msg2.toString(), msg2.msg());
		if(plugin.checkSpout(p)){ spout(p, message.replace("%p", p2.getName()));
								  spout(p2, message2.replace("%p", p.getName())); }
		else					{ chat(p, message.replace("%p", p2.getName()));
								  chat(p2, message2.replace("%p", p.getName())); }
	}
	
	public void sendChaining(Player p, Player p2, Messages msg, Messages msg2, String tr) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		String message2 = plugin.getConfig().getString(msg2.toString(), msg2.msg());
		if(plugin.checkSpout(p)){ spout(p, message.replace("%p", p2.getName()).replace("%tr", tr));
								  spout(p2, message2.replace("%p", p.getName()).replace("%tr", tr)); }
		else					{ chat(p, message.replace("%p", p2.getName()).replace("%tr", tr));
								  chat(p2, message2.replace("%p", p.getName()).replace("%tr", tr)); }
	}
	
	

	public void sendTimeChained(Player p, Player p2, Messages msg, Messages msg2, String t) {
		String message = plugin.getConfig().getString(msg.toString(), msg.msg());
		String message2 = plugin.getConfig().getString(msg2.toString(), msg2.msg());
		if(plugin.checkSpout(p)){ spout(p, message.replace("%p", p2.getName()).replace("%t", t));
								  spout(p2, message2.replace("%p", p.getName()).replace("%t", t)); }
		else					{ chat(p, message.replace("%p", p2.getName()).replace("%t", t));
								  chat(p2, message2.replace("%p", p.getName()).replace("%t", t)); }
	}
	
	private void spout(Player p, String msg) {
		SpoutPlayer sp = (SpoutPlayer) p;
		if(msg.length() < 26)
		sp.sendNotification(title, parseColors(msg), mat);
		else chat(p, parseColors(msg));
	  }
	
	private void chat(Player p, String msg) {
		if(p != null)	p.sendMessage(parseColors(msg));
		else			System.out.println(parseColors(msg));
	}
	
	private void broadcast(String msg) {
		plugin.getServer().broadcastMessage(parseColors(msg));
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
