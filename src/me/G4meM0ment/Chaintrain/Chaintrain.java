package me.G4meM0ment.Chaintrain;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.G4meM0ment.Chaintrain.Bounty.Bounty;
import me.G4meM0ment.Chaintrain.Bounty.BountyListener;
import me.G4meM0ment.Chaintrain.Messages.Messages;
import me.G4meM0ment.Chaintrain.Messages.Messenger;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

public class Chaintrain extends JavaPlugin {
			
	Chaintrain chaintrain;
	private EventListener listener;
	private Messages messages;
	private Messenger messenger;
	private Bounty bounty;
	private BountyListener blistener;
	private FileConfiguration customConfig = null;
	private File dataFile;
	public static Economy econ= null;
	public SpoutManager sm;
	public PluginManager pm;
		
	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		saveConfig();
		saveCustomConfig();
	}
	
	@Override
	public void onEnable() {
		
		pm = getServer().getPluginManager();
		
		setupEconomy();
		chaintrain = this;
		bounty = new Bounty(this, messenger);
		messenger = new Messenger(this);
		listener = new EventListener(this, messenger);
		pm.registerEvents(listener, this);
		blistener = new BountyListener(this, messages, bounty);
		pm.registerEvents(blistener, this);
		
		//creates the config
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		bounty.reloadCustomConfig();
		bounty.saveCustomConfig();
		reloadCustomConfig();
		saveCustomConfig();
		
		listener.checkForPlugins();
		
		if(this.getConfig().getBoolean("useSpout")) {
			this.getLogger().info("Spout found, features enabled.");		
		}
		this.getLogger().info("Successfully enabled!");
		this.getLogger().info("By G4meM0ment (Originaly by T4sk)");
	}
	
	private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }
        return (econ != null);
    }
	
	public void reloadCustomConfig() {
	    if (dataFile == null) {
	    	dataFile = new File(this.getDataFolder(), "data");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(dataFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource("data");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (customConfig == null) {
	        reloadCustomConfig();
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig() {
	    if (customConfig == null || dataFile == null) {
	    	return;
	    }
	    try {
	        customConfig.save(dataFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + dataFile, ex);
	    }
	}
	
	public void newPlayer(Player player) {
		if(existsPlayer(player)) {
			return;
		}
		String pname = player.getName();
				
		getCustomConfig().set("players." +pname +".chained", false);
		getCustomConfig().set("players." +pname +".oldTime", 0);
		getCustomConfig().set("players." +pname +".cooldown", 0);		
		saveCustomConfig();
	}
	
	/**
	 * Checks if the player is already listed	
	 * @param player to be checked
	 * @return boolean
	 */
	public boolean existsPlayer(Player player) {
		ConfigurationSection cfgsel = getCustomConfig().getConfigurationSection("players");
		if (cfgsel == null) return false;
		Set<String> names = cfgsel.getKeys(false);
		String pname = player.getName();
		
		try {
			for (String name : names) {
				if(name.equalsIgnoreCase(pname)) {
					return true;
				}
			}
		}catch (NullPointerException e) {
			return false;
		}
		return false;
	}
	
	public final HashMap<String,String> data = new HashMap<String,String>();
	
	public boolean isChained(String chained) {
		if(chained == null) return false;
	    return data.containsKey(chained);
	}
	public boolean isChained(Player chained) {
		if(chained == null) return false;
		
		if(this.getCustomConfig().getLong("players." + chained.getName() +".oldTime") > 0 && this.getCustomConfig().getLong("players." + chained.getName() +".cooldown") > 0) {
			long oldTime = this.getCustomConfig().getLong("players." + chained.getName() +".oldTime");
			int cooldown = this.getCustomConfig().getInt("players." + chained.getName() +".cooldown")*60;
			long diff = (System.currentTimeMillis() - oldTime)/1000;
			if (diff > cooldown) {
	    	unchain(chained.getName());
	    	messenger.send(chained, Messages.timeOver);
	    	return false;
			}
		}
	    else{
	    	return isChained(chained.getName());
	    }
		return isChained(chained.getName());
	}
	
	public void chainAll(Player[] all, Player chainer) {
		//for every player online which isn't filtered by permissions, chained will be set
		for(Player chained:all) {
			if(!(chained.hasPermission("chaintrain.ignore") || chained.hasPermission("chaintrain.admin") || chained.isOp()))
			chain(chained, chainer);
		}
	}
	public void unchainAll(Player[] all) {		
		for(Player chained:all) {
		   String player = chained.getName();
		   unchain(player);
	    }
	}
	
	public void chain(Player chained,Player chainer) {
		chain(chained.getName(),chainer.getName());
		if(bounty.existsBounty(chained) && bounty.acceptedBounty(chainer, chained.getName())) {
			bounty.finishedBounty(chainer, chained, "chain");
		}
	}
	public void chain(String player, String chainer) {
		data.put(player, chainer);
		this.getCustomConfig().set("players." + player +".chained", true);
		this.saveCustomConfig();
	}
	
	public void unchain(Player player) {
		unchain(player.getName());
	}
	public void unchain(String player)
	{
		if(!isChained(player))
			return;
		data.remove(player);
		this.getCustomConfig().set("players." + player +".chained", false);
		this.getCustomConfig().set("players." + player +".oldTime", 0);
		this.getCustomConfig().set("players." + player +".cooldown", 0);
		this.saveCustomConfig();
	}
	
	public void playSpoutSound(Player player)
	{
		SpoutPlayer sp = SpoutManager.getPlayer(player);
		if(!sp.isSpoutCraftEnabled()) return;
		Plugin spoutPlugin = player.getServer().getPluginManager().getPlugin("Chaintrain");
	
		// *** Sound effect ***
		SoundManager soundM = SpoutManager.getSoundManager();
		soundM.playCustomSoundEffect(spoutPlugin, sp, chaintrain.getConfig().getString("chainSound"), false);
	}

//The command block
    HashMap<String,String> allBoolean = new HashMap<String,String>();
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		String p = sender.getName();
		Player player = (Player) sender;
		
		if(command.getName().equalsIgnoreCase("chaintrain")) {
			
			if(args.length > 0 && args[0].equals("reload") && (player.hasPermission("chaintrain.command.reload") || player.hasPermission("chaintrain.admin") || player.isOp())) {
				reloadConfig();
				saveConfig();
				reloadCustomConfig();
				saveCustomConfig();
				bounty.getCustomConfig();
				bounty.saveCustomConfig();
				player.sendMessage("Chaintrain reloaded!");
				getLogger().info("Reloaded config!");
				return true;
			}
		
			if(args.length > 0 && args[0].equals("help") && (player.hasPermission("chaintrain.admin") || player.isOp())) {
				PluginDescriptionFile pdf = getDescription();
				player.sendMessage(ChatColor.DARK_RED + "<<<--------->>> *** HELP MENU *** <<<--------->>>");
				player.sendMessage(ChatColor.DARK_RED + "<<<--------->>> ** CHAIN HELP MENU ** <<<--------->>>");
				player.sendMessage(ChatColor.GREEN + "/chaintrain reload" + ChatColor.GOLD + " Reloads the Configs.");
				player.sendMessage(ChatColor.GREEN + "/chaintrain help" + ChatColor.GOLD + " Displays the help menu.");
				player.sendMessage(ChatColor.GREEN + "/chain <player> (<time>)" + ChatColor.GOLD + " Chain a player, arg time is to chain the player over time (minutes).");
				player.sendMessage(ChatColor.GREEN + "/unchain <player>" + ChatColor.GOLD + " Unchain the player.");
				player.sendMessage(ChatColor.GREEN + "/chainall" + ChatColor.GOLD + " Chain all players online and joining.");
		 		player.sendMessage(ChatColor.GREEN + "/unchainall" + ChatColor.GOLD + " Removes the effect of '/chainall'.");
		 		player.sendMessage(ChatColor.GREEN + "/chaintrain time" + ChatColor.GOLD + " To check how many minutes left, you're chained.");
		 		player.sendMessage(ChatColor.GREEN + "/chain " + ChatColor.GOLD + "AND " + ChatColor.GREEN + "/unchain" + ChatColor.GOLD + " alone will chain/unchain you.");
				player.sendMessage(ChatColor.DARK_RED + "<<<--------->>> ** BOUNTY HELP MENU ** <<<--------->>>");
		 		player.sendMessage(ChatColor.GREEN + "/bounty add <player> <amount> <type>" + ChatColor.GOLD + "Sets a player as wanted, the amount of money, type can be chain or kill, info are information for the killer.");
		 		player.sendMessage(ChatColor.GREEN + "/bounty accept <player>" + ChatColor.GOLD + "To accept a bounty, which is set in the players name.");
		 		player.sendMessage(ChatColor.GREEN + "/bounty cancel <player>" + ChatColor.GOLD + "Cancel a bounty you have accepted.");
		 		player.sendMessage(ChatColor.GREEN + "/bounty mylist" + ChatColor.GOLD + "List all bountys you have accepted and you can do.");
		 		player.sendMessage(ChatColor.GREEN + "/bounty list" + ChatColor.GOLD + "Lists all bountys you can do.");
		 		player.sendMessage(ChatColor.GOLD + "Information: Devs: G4meM0ment Version: " + pdf.getVersion());
		 		player.sendMessage(ChatColor.GOLD + "Thanks to ferrybig for helping me! Plugin originally by T4sk!");
		 		return true;
			}
			
			if(args.length > 0 && args[0].equalsIgnoreCase("time") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.chaintime") || player.isOp())) {
				messenger.sendTimeOverIn(player, Messages.timeOverIn);
				return true;
			}
		}
			
		if(command.getName().equalsIgnoreCase("chain") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.chain") || player.isOp())) {
			
			if(args.length > 1) {
				Player chained = getPlayer(args[0]);
				if(chained != null) {
					if(!chaintrain.isChained(chained)) {
						int time = Integer.parseInt(args[1]);
						long currentTime = System.currentTimeMillis();
						getCustomConfig().set("players." + chained.getName() +".oldTime", currentTime);
						getCustomConfig().set("players." + chained.getName() +".cooldown", time);
						saveCustomConfig();
						chain(chained.getName(), p);
						messenger.sendTimeChained(chained, player, Messages.chainedOverTime, Messages.chainedVictimOverTime, args[1]);
						return true;
					}
					else {
						messenger.send(player, Messages.playerAlreadyChained);
						return true;
					}
				}
				else {
					messenger.send(player, Messages.cantFindPlayer);
					return true;
				}
			}
		
			if(args.length > 0)
			{			
				Player chained = getPlayer(args[0]);
				if(chained != null)
				{
					if(!chaintrain.isChained(chained))
					{
						chaintrain.chain(chained.getName(), p);
						messenger.sendChained(chained, player, Messages.chained, Messages.chainedBy);
						return true;
					}
					else {
						messenger.send(player, Messages.playerAlreadyChained);
						return true;
					}
				}
				else {
					messenger.send(player, Messages.cantFindPlayer);
					return true;
				}
			}
			if(args.length == 0)
			{
				chaintrain.chain(p, p);
				messenger.sendChained(player, player, Messages.chained, Messages.chainedBy);
				return true;
			}
		}
		
		if(command.getName().equalsIgnoreCase("unchain") && args.length > 0 && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.chain") || player.isOp())) {
			Player chained = getPlayer(args[0]);
			if(chained != null) {
				if(chaintrain.isChained(chained)) {
					chaintrain.unchain(chained.getName());
					messenger.sendUnchained(chained, player, Messages.unchainedChained, Messages.unchainedChainer);
					return true;
				}
				else {
					messenger.send(player, Messages.playerNotChained);
					return true;
				}
			}
			else {
				messenger.send(player, Messages.cantFindPlayer);
				return true;
			}
		}
		
		if(command.getName().equalsIgnoreCase("unchain") && args.length == 0 && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.chain") || player.isOp())) {
				chaintrain.unchain(p);
				messenger.sendUnchained(player, player, Messages.unchainedChained, Messages.unchainedChainer);
				return true;
		}
		
		if(command.getName().equalsIgnoreCase("chainall") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.chainall") || player.isOp())) {
			//sets chainall to active so if a player joins he will be chained too
			allBoolean.put("chainAll", "true");
			Player[] all = Bukkit.getServer().getOnlinePlayers();
			chaintrain.chainAll(all, player);
			messenger.sendBroadcastAndPlayer(player, Messages.allChained, Messages.allChained);
			return true;
		}
		
		if(command.getName().equalsIgnoreCase("unchainall") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.chainall") || player.isOp())) {
			allBoolean.put("chainAll", "false");
			Player[] all = Bukkit.getServer().getOnlinePlayers();
			chaintrain.unchainAll(all);
			messenger.sendBroadcastAndPlayer(player, Messages.allUnchained, Messages.allUnchained);
			return true;
		}
		
		if(command.getName().equalsIgnoreCase("bounty")) {
			
			if(args.length > 3 && args[0].equalsIgnoreCase("add") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.bounty.add") || player.hasPermission("chaintrain.command.bounty") || player.isOp())) {
				Player wanted = getPlayer(args[1]);
				Double amount = Double.parseDouble(args[2]);
				
				if(!(args[3].equals("kill") || args[3].equals("chain"))) return false;
				
				if(!bounty.checkAmount(player, amount)) {
					messenger.sendMoney(player, Messages.notEnoughMoney, String.valueOf(amount));
					return true;
				}
				
				if(!bounty.checkMinAmount(player, amount)) {
					messenger.sendMinMoney(player, Messages.minMoney);
					return true;
				}
					
				if(wanted != null) {
					bounty.newBounty(wanted, amount, args[3]);
					if(getConfig().getBoolean("broadcastNewBounty")) messenger.sendBroadcastAndPlayer(player, Messages.bountyExposed, Messages.newBounty);
					else 											 messenger.send(player, Messages.newBounty);
					return true;
				}
			}
			
			if(args.length > 1 && args[0].equalsIgnoreCase("accept") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.bounty.accept") || player.hasPermission("chaintrain.command.bounty") || player.isOp())) {
				Player bp = getPlayer(args[1]);
				String bpname = bp.getName();
				for(int i=1; i < bounty.getAllBountys().size(); i++) {
					if(bounty.getAllBountys().get(i).split(" ")[0].equalsIgnoreCase(bpname)) {
						if(bounty.acceptedBounty(player, bpname)) {
							messenger.send(player, Messages.bountyAlreadyAccepted);
							break;
						}
						bounty.getCustomConfig().set("hunters." + player.getName() + "." + bpname, true);
						bounty.saveCustomConfig();
						messenger.send(player, Messages.bountyAccepted);
						break;
					}
					else
						messenger.send(player, Messages.cantFindPlayer);
				}
				return true;
			}
			
			if(args.length > 1 && args[0].equalsIgnoreCase("cancel") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.bounty.cancel") || player.hasPermission("chaintrain.command.bounty") || player.isOp())) {
				Player bp = getPlayer(args[1]);
				if(bounty.acceptedBounty(player, bp.getName())) {
					if(!bounty.acceptedBounty(player, bp.getName())) {
						messenger.send(player, Messages.bountyNotAccepted);
						return true;
					}
					bounty.getCustomConfig().set("hunters." + player.getName() + "." + bp.getName(), false);
					bounty.saveCustomConfig();
					messenger.send(player, Messages.bountyCanceled);
					return true;
				}
				else {
					messenger.send(player, Messages.cantFindPlayer);	
					return true;
				}

			}
		
			if(args.length > 0 && args[0].equalsIgnoreCase("list") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.bounty.list") || player.hasPermission("chaintrain.command.bounty") || player.isOp())) {
				List<String> bountys = bounty.getAllBountys();
				int size = bountys.size();
				int page = 0;
				if(args.length > 1 && args[1] == String.valueOf(1)) page = 0;
				player.sendMessage(ChatColor.DARK_RED + "<<<--------->>> *** BOUNTYS *** <<<--------->>>");
				player.sendMessage(ChatColor.GREEN + "To accept a bounty: /bounty accept <player>");	
					for(int i=page*10;i<size;i++){
						if(i <= page*10)
							player.sendMessage(ChatColor.GOLD + String.valueOf((i+1))+". "+bountys.get(i));
					}
				return true;
			}
			
			if(args.length > 0 && args[0].equalsIgnoreCase("mylist") && (player.hasPermission("chaintrain.admin") || player.hasPermission("chaintrain.command.bounty.mylist") || player.hasPermission("chaintrain.command.bounty") || player.isOp())) {
				List<String> bountys = bounty.getHuntersBountys(player);
				int size = bountys.size();
				int page = 0;
				if(args.length > 1 && args[1] == String.valueOf(1)) page = 0;
				player.sendMessage(ChatColor.DARK_RED + "<<<--------->>> *** MY BOUNTYS *** <<<--------->>>");
				player.sendMessage(ChatColor.GREEN + "To cancel a bounty: /bounty cancel <player>");	
					for(int i=page*10;i<size;i++){
						if(i <= page*10)
							player.sendMessage(ChatColor.GOLD + String.valueOf((i+1))+". "+bountys.get(i));
					}
				return true;
			}
		}
		return false;
	}
	
    public Player getPlayer(final String name) {
        Player[] players = Bukkit.getOnlinePlayers();
 
        Player found = null;
        String lowerName = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : players) {
            if (player.getName().toLowerCase().startsWith(lowerName)) {
                int curDelta = player.getName().length() - lowerName.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                    break;
                }
                if (curDelta == 0) break;
            }

        }
        return found;
    }
    
    public boolean checkSpout(Player p) {
		if(SpoutManager.getPlayer(p).isSpoutCraftEnabled())
			return true;
		else
			return false;
    }
}
