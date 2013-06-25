package me.G4meM0ment.Chaintrain.Bounty;

import java.util.HashMap;

import me.G4meM0ment.Chaintrain.Chaintrain;
import me.G4meM0ment.Chaintrain.Messages.Messages;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BountyListener implements Listener {

		private Bounty bounty;
		private static HashMap<Player, Player> data = new HashMap<Player, Player>();
		public BountyListener(Chaintrain plugin, Messages messages, Bounty bounty) {
			this.bounty = bounty;
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
		public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
			
			if(!(event.getEntity() instanceof Player)) return;
			
		    Entity entity = event.getDamager();
		    Player p = (Player) event.getEntity();
		    
		    if(event.getCause().toString().equals("ENTITY_ATTACK")) {
		    	if(entity instanceof Player) data.put(p, (Player) event.getDamager());
		    	if(entity instanceof Arrow) data.put(p, (Player) ((Arrow)event.getDamager()).getShooter());
		    	if(entity instanceof ThrownPotion) data.put(p, (Player) ((ThrownPotion)event.getDamager()).getShooter());
		    }
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
		public void onPlayerDeath(PlayerDeathEvent event) {
			Player p = event.getEntity();
			if(data.get((Player)event.getEntity()) == null) return;

			if(p != null && bounty.existsBounty(p) && bounty.acceptedBounty(p, data.get(p).getName()))
				bounty.finishedBounty(p, data.get(p), "kill");
		}

}
