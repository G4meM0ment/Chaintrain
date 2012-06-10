package me.G4meM0ment.Chaintrain;


import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemStack;

import me.G4meM0ment.Chaintrain.Chaintrain;

public class EventListener implements Listener {

	Chaintrain chaintrain;
	Messages messages;
	public EventListener(Chaintrain Chaintrain)
	{
    Bukkit.getServer().getPluginManager().registerEvents(this, Chaintrain);
    chaintrain = Chaintrain;
    messages = new Messages(this.chaintrain);
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
				Messages.moveWhileChained(mover);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event)
	{
		if(event.getRightClicked() instanceof Player)
		{
			//checks if player holds needed item in hand
			if(event.getPlayer().getItemInHand().getType() != getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase()))
			{
				return;
		    }
		    
			Player clicked = (Player) event.getRightClicked();
		    Player clicker = (Player) event.getPlayer();
		    boolean chainCostsItem = chaintrain.getConfig().getBoolean("chainCostsItem");
		  
		   //checks for permissions and if the player who wants to chain/unchain is already chained
		  if(!(chaintrain.isChained(clicker)) && ((clicker.hasPermission("chaintrain.chain") || event.getPlayer().isOp() || event.getPlayer().hasPermission("chaintrain.admin"))));
		   {
			   //if the clicked player is already chaine dhe gets unchained
		    if(chaintrain.isChained(clicked))
		    {
		    	chaintrain.unchain(clicked.getName());
		    	//if chain costs an item it will be added
		    	if(chainCostsItem)
		    	{
		    		clicker.getInventory().addItem(new ItemStack(getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase()), chaintrain.getConfig().getInt("chainCostsItemAmount")));
		    	}
                Messages.unchained(clicked, clicker);
		    }
		    //else he gets chained
		    else if(!(chaintrain.isChained(clicker)))
		    {
		    	chaintrain.chain(clicked, event.getPlayer());
		    	//item removed
		    	if(chainCostsItem)
		    	{
		    	   clicker.getInventory().removeItem(new ItemStack(getMaterial(chaintrain.getConfig().getString("chainItem").toUpperCase()), chaintrain.getConfig().getInt("chainCostsItemAmount")));
		    	}
		    	Messages.chained(clicked, clicker);
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
			Messages.dropWhileChained(dropper);
		}
	}	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		Player interacter = (Player) event.getPlayer();
		if (chaintrain.isChained(interacter))
		{
			event.setCancelled(true);
            Messages.interactWhileChained(interacter);
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
                Messages.attackWhileChained(damager);
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
				Messages.shootWhileChained(shooter);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		//checks if chainall is active and if it is the player will be chained
		boolean allBoolean = Boolean.parseBoolean(chaintrain.allBoolean.get("chainall"));
		Player joiner = event.getPlayer();
		if(allBoolean)
		{
			if(!(chaintrain.isChained(joiner)) && !(joiner.hasPermission("chaintrain.ignore") || joiner.hasPermission("chaintrain.admin") || joiner.isOp()))
			{
				chaintrain.chain(joiner, null);
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
			if(event.getMessage().startsWith(command))
			{
				String commandName = event.getMessage();
				event.setCancelled(true);
				Messages.disabledCommand(chained, commandName);
			}
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
   
}
