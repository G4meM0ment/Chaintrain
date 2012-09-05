package me.G4meM0ment.Chaintrain;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.G4meM0ment.Chaintrain.Chaintrain;
import me.G4meM0ment.Chaintrain.Messages.Messages;
import me.G4meM0ment.Chaintrain.Messages.Messenger;

public class EventListener implements Listener {

	private Chaintrain chaintrain;
	private Messenger messenger;
	public EventListener(Chaintrain chaintrain, Messenger messenger)
	{
		this.chaintrain = chaintrain;
		this.messenger = messenger;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void blockChainedMove(PlayerMoveEvent event)
	{
		if(chaintrain.isChained(event.getPlayer()))
		{
			//Teleports the player to the location he came from, so he can't move (but he can jump, so he wont stuck in the air)
			if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())
			{
				Player mover = (Player) event.getPlayer();
				mover.teleport(event.getFrom());
				messenger.send(mover, Messages.moveWhileChained);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event)
	{
		if(event.getRightClicked() instanceof Player)
		{		    
			Player clicked = (Player) event.getRightClicked();
		    Player clicker = (Player) event.getPlayer();
		    boolean chainCostsItem = chaintrain.getConfig().getBoolean("chainCostsItem");
		    
		   //checks for permissions and if the player who wants to chain/unchain is already chained
		  if(!(chaintrain.isChained(clicker)) && ((clicker.hasPermission("chaintrain.chain") || event.getPlayer().isOp() || event.getPlayer().hasPermission("chaintrain.admin"))));
		  {
			   //if the clicked player is already chained he gets unchained
		    if(chaintrain.isChained(clicked) && isChaining.get(clicker.getName()) == null && (event.getPlayer().getItemInHand().getTypeId() == 0 || event.getPlayer().getItemInHand().getType() == getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase())))
		    {
		    	if(chaintrain.getConfig().getBoolean("chainTimer"))
		    	{
		    		chainTimer(clicker, clicked);
		    	}
		    	else
		    	{
		    		chaintrain.unchain(clicked.getName());
		    		if(chaintrain.getConfig().getBoolean("useSpout"))
		    		{
		    		chaintrain.playSpoutSound(clicked);
		    		chaintrain.playSpoutSound(clicker);
		    		}
		    		//if chain costs an item it will be added
		    		if(chainCostsItem)
		    		{
		    			clicker.getInventory().addItem(new ItemStack(getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase()), chaintrain.getConfig().getInt("chainCostsItemAmount")));
		    		}
					messenger.sendUnchained(clicked, clicker, Messages.unchainedChained, Messages.unchainedChainer);
		    	}
		    }
		    //else he gets chained
		    
		    else if(!chaintrain.isChained(clicker) && isChaining.get(clicker.getName()) == null && event.getPlayer().getItemInHand().getType() == getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase()))
		    {
		    	if(chaintrain.getConfig().getBoolean("chainTimer"))
		    	{
		    		chainTimer(clicker, clicked);
		    	}
		    	else
		    	{
		    		chaintrain.chain(clicked, event.getPlayer());
		    		if(chaintrain.getConfig().getBoolean("useSpout"))
		    		{
		    		chaintrain.playSpoutSound(clicked);
		    		chaintrain.playSpoutSound(clicker);
		    		}
		    		//item removed
		    		if(chainCostsItem)
		    		{
		    			clicker.getInventory().removeItem(new ItemStack(getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase()), chaintrain.getConfig().getInt("chainCostsItemAmount")));
		    		}
					messenger.sendChained(clicked, clicker, Messages.chained, Messages.chainedBy);
		    	}
		    }
		   }
		}
	}
	
	/*
	 * Few events which gets cancelled if the player is chained
	 */
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerDropEvent(PlayerDropItemEvent event)
	{
		Player dropper = (Player) event.getPlayer();
		if (chaintrain.isChained(dropper))
		{
			event.setCancelled(true);
			messenger.send(dropper, Messages.dropWhileChained);
		}
	}	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		Player interacter = (Player) event.getPlayer();
		if (chaintrain.isChained(interacter))
		{
			event.setCancelled(true);
			messenger.send(interacter, Messages.interactWhileChained);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	{
		if(event.getDamager() instanceof Player)
		{
			Player damager = (Player) event.getDamager();
			if(chaintrain.isChained(damager))
			{
				event.setCancelled(true);
                messenger.send(damager, Messages.attackWhileChained);
			}
		}
		
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onBowShot(EntityShootBowEvent event)
	{
		
		if(event.getEntity() instanceof Player)
		{
			Player shooter = (Player) event.getEntity();
			if(chaintrain.isChained(shooter))
			{
				event.setCancelled(true);
				messenger.send(shooter, Messages.shootWhileChained);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		//checks if chainall is active and if it is the player will be chained
		boolean allBoolean = Boolean.parseBoolean(chaintrain.allBoolean.get("chainall"));
		final Player player = event.getPlayer();
		chaintrain.newPlayer(player);
		if(allBoolean)
		{
			if(!(chaintrain.isChained(player)) && !(player.hasPermission("chaintrain.ignore") || player.hasPermission("chaintrain.admin") || player.isOp()))
			{
				chaintrain.chain(player, null);
			}
		}
		if(chaintrain.getCustomConfig().getBoolean("players." + player.getName() +".chained"))
		{
			chaintrain.chain(player.getName(), null);
		}
		
		// version checking
		// in it's own thread because it takes some time and would stop the rest of the world to load
			if(chaintrain.getConfig().getBoolean("checkVersion") && (player.isOp() || player.hasPermission("chaintrain.admin"))) {
				new Thread() {
					public void run() {
						try {
							URL versionURL = new URL("http://dl.dropbox.com/u/96045686/Chaintrain/Version.txt");
							BufferedReader reader = new BufferedReader(new InputStreamReader(versionURL.openStream()));
							
							String line = reader.readLine();
							if (!chaintrain.getDescription().getVersion().equalsIgnoreCase(line)) {
								messenger.sendVersion(player, Messages.newVersion, line);
							}
							reader.close();
						} catch (MalformedURLException e) {
							// versionURL
							e.printStackTrace();
						} catch (IOException e) {
							// versionURL.openstream()
							e.printStackTrace();
						}
					}
				}.start();
			}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPluginEnable(PluginEnableEvent event) {
		if(!chaintrain.getConfig().getBoolean("autoDetectSpout")) return;
		
		String pluginName = event.getPlugin().getDescription().getName();
		PluginManager pm = chaintrain.getServer().getPluginManager();		
		// Spout
		if(pluginName.equalsIgnoreCase("spout")) {
			Plugin spoutPlugin = pm.getPlugin("Spout");
			if (spoutPlugin != null) {
				chaintrain.getConfig().set("useSpout", true);
			}
			else {
				chaintrain.getConfig().set("useSpout", false);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		Player chained = event.getPlayer();
		//for every command which is in the disabledCommands list it creates an string command and if the used command starts with that string it will be cancelled
		for(String command : chaintrain.getConfig().getStringList("disabledCommandsWhileChained"))
		{
			if(event.getMessage().startsWith(command) && chaintrain.isChained(chained))
			{
				event.setCancelled(true);
				messenger.send(chained, Messages.disabledCommand);
			}
		}		
	}
	
	public void checkForPlugins() {
		if(!chaintrain.getConfig().getBoolean("autoDetectSpout")) return;
		
		PluginManager plugman = chaintrain.getServer().getPluginManager();
		// spout
		Plugin spoutPlugin = plugman.getPlugin("Spout");
		if (spoutPlugin != null) {
			chaintrain.getConfig().set("useSpout", true);
		}
		else {
			chaintrain.getConfig().set("useSpout", false);
		}
	}
	
	//gets the id or the name of the item from config and returns it as Material
	public Material getMaterial(String id)
	{
		Material get = Material.getMaterial(id);
		if(get != null) return get;
	try
	{
		get = Material.getMaterial(Integer.valueOf(id));
	}
	catch(NumberFormatException e)
	{
	}
	   return get;
	}
	
	public static HashMap<String, Boolean> isChaining = new HashMap<String, Boolean>();
	public void chainTimer(final Player player, final Player target)
	{
		final String name = player.getName();
		new Thread() {
			public void run() {
				int counter = 0;
				int time = chaintrain.getConfig().getInt("timer");
					
				isChaining.put(name, true);
				while (counter < time && isChaining.get(name)) {
					if (player.getLocation().distance(target.getLocation()) > 3) {
						isChaining.put(name, false);
						continue;
					}
					messenger.sendChaining(player, target, Messages.chaining, Messages.getChained, String.valueOf(counter+1));
		    		if(chaintrain.getConfig().getBoolean("useSpout")) {
		    			chaintrain.playSpoutSound(target);
		    			chaintrain.playSpoutSound(player);
		    		}
					try {
						sleep(1000);
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
					counter++;
				}
				isChaining.remove(name);
				if(counter >= time) {					
					if(chaintrain.isChained(target)) {
						chaintrain.unchain(target.getName());
			    		if(chaintrain.getConfig().getBoolean("chainCostsItem")) {
			    			player.getInventory().addItem(new ItemStack(getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase()), chaintrain.getConfig().getInt("chainCostsItemAmount")));
			    		}
						messenger.sendUnchained(target, player, Messages.unchainedChained, Messages.unchainedChainer);
					}
					else if(!chaintrain.isChained(player)) {
			    		chaintrain.chain(target, player);
			    		if(chaintrain.getConfig().getBoolean("chainCostsItem")) {
			    			player.getInventory().removeItem(new ItemStack(getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase()), chaintrain.getConfig().getInt("chainCostsItemAmount")));
			    		}
						messenger.sendChained(target, player, Messages.chained, Messages.chainedBy);
					}
				}
			}
		}.start();
	}   
}
