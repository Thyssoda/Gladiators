package com.thyssoda.gladiators.Engines;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.thyssoda.gladiators.Gladiators;
import com.thyssoda.gladiators.Game.GladiatorsState;

public class CreationEngine implements CommandExecutor, Listener {

	private Gladiators pl;
	private WaitingPlayersEngine wpe;
	private GameEngine se;
	
	public CreationEngine(Gladiators pl, WaitingPlayersEngine wpe, GameEngine se) {
		this.pl = pl;
		this.wpe = wpe;
		this.se = se;
	}
	
	private int i = 1;
	public ArrayList<UUID> playerInGame = new ArrayList<>();
	
	public Player player;
	public Scoreboard scoreboard;
	public Objective obj;
	public Objective objHealthTab;
	public Objective objHealthBN;
	public Team bleu;
	public Team rouge;
	int kills;
	int lastkills;
	
	
	private int players;

    private int timer;
    
    private int killsBleu;
    private int lastkillsBleu;
    private int killsRouge;
    private int lastkillsRouge;
   
    private int timer2;
    private int lastTimer;
    private String lastS;
    private String S;
	
	public void createScoreboard(){
		
		obj = scoreboard.registerNewObjective("gladiators", "dummy");
		obj = scoreboard.getObjective("gladiators");
		objHealthTab = scoreboard.registerNewObjective("hpTab", "health");
		objHealthTab = scoreboard.getObjective("hpTab");
		objHealthBN = scoreboard.registerNewObjective("hpBN", "health");
		objHealthBN = scoreboard.getObjective("hpBN");
		
		bleu = scoreboard.registerNewTeam("Bleu");

		bleu.setPrefix(ChatColor.BLUE + "[BLUE]");
		bleu.setAllowFriendlyFire(false);

		rouge = scoreboard.registerNewTeam("Rouge");

		rouge.setPrefix(ChatColor.RED + "[RED]");
		rouge.setAllowFriendlyFire(false);
		
	}

	public void setScoreboard(Player p){
		
		obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Gladiators");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		objHealthTab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		objHealthBN.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objHealthBN.setDisplayName("/ 20");
		
		if(GladiatorsState.isState(GladiatorsState.WAIT)){
			
			players = playerInGame.size() - 1;
           
            obj.getScoreboard().resetScores(""+players);
            obj.getScore(""+playerInGame.size()).setScore(9);
 
            obj.getScoreboard().resetScores("§7En attente de joueurs !");
            obj.getScore("§7En attente de joueurs !").setScore(7);
           
           
        }else if(GladiatorsState.isState(GladiatorsState.LOBBY)){
           
        	timer = wpe.temps - 1;
        	
        	obj.getScoreboard().resetScores("§7En attente de joueurs !");
            obj.getScore("§7Lancement de la partie dans : ").setScore(7);
            
            obj.getScoreboard().resetScores(""+wpe.temps);
            obj.getScore(""+timer).setScore(6);
       
        }else{

        	if(se.kills.containsKey(this.player)){
        		        		
            kills = se.kills.get(this.player);
            lastkills = kills - 1;
        	
        	}
        	
        	killsBleu = se.scoreBleu;
        	lastkillsBleu = se.scoreBleu - 1;
        	killsRouge = se.scoreRouge;
        	lastkillsRouge = se.scoreRouge - 1;
        	
        	timer2 = 480 - se.temps4;
        	lastTimer = timer2 + 1;
        	
        	lastS = new SimpleDateFormat("mm:ss").format(lastTimer * 1000);
        	S = new SimpleDateFormat("mm:ss").format(timer2 * 1000);
            
            obj.getScoreboard().resetScores("§7Lancement de la partie dans : ");
            obj.getScoreboard().resetScores(""+wpe.temps);
           
            obj.getScoreboard().resetScores(""+players);
            obj.getScore(""+playerInGame.size()).setScore(9);
            obj.getScoreboard().resetScores(""+lastkills);
           
            obj.getScoreboard().resetScores("§7Fin de la partie dans : ");
            obj.getScore("§7Fin de la partie dans : ").setScore(7);

            obj.getScoreboard().resetScores(lastS);
            obj.getScore(S).setScore(6);
            
            obj.getScoreboard().resetScores("§7 Kills bleus : ");
            obj.getScore("§7 Kills bleus : ").setScore(4);

            obj.getScoreboard().resetScores(lastkillsBleu + "");
            obj.getScore(killsBleu + "").setScore(3);
            
            obj.getScoreboard().resetScores("§7 Kills rouges : ");
            obj.getScore("§7 Kills rouges : ").setScore(2);
            
            obj.getScoreboard().resetScores(lastkillsRouge+ "");
            obj.getScore(killsRouge + "").setScore(1);
 
            obj.getScore("§8 ").setScore(11);
            obj.getScore("§7Joueurs: ").setScore(10);
            obj.getScore("§e ").setScore(8);
            obj.getScore("§a ").setScore(5);
           
            
        }
		
		p.setScoreboard(scoreboard);
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage("Tu dois etre un joueur afin d'executer cette commande !");
			return false;
		}else{
			
			Player player = (Player) sender;
			Inventory inv = Bukkit.createInventory(null, 9, "Gladiators");
			ItemStack lobbyItem = nameItem(Material.DIAMOND_SWORD,
					ChatColor.LIGHT_PURPLE + "Ajoute le point de spawn du lobby !");
			ItemStack spawnItem = nameItem2(Material.WOOL, (short) 11,
					ChatColor.BLUE + "Ajoute un point de spawn pour l'équipe bleue !");
			ItemStack spawnItem2 = nameItem2(Material.WOOL, (short) 14,
					ChatColor.DARK_RED + "Ajoute un point de spawn pour l'équipe rouge !");
			ItemStack blockItem1 = nameItem(Material.LAPIS_BLOCK, ChatColor.GREEN + "Bloc à détruire (équipe bleu) !");
			ItemStack blockItem2 = nameItem(Material.REDSTONE_BLOCK, ChatColor.GREEN + "Bloc à détruire (équipe rouge) !");
			ItemStack nullItem = nameItem2(Material.STAINED_GLASS_PANE, (short) 14, ChatColor.LIGHT_PURPLE + "###########");
			inv.setItem(0, spawnItem);
			inv.setItem(1, spawnItem2);
			inv.setItem(2, blockItem1);
			inv.setItem(3, blockItem2);
			inv.setItem(8, lobbyItem);
			for (int i = 4; i <= 7; i++) {
				inv.setItem(i, nullItem);
			}

			player.openInventory(inv);
		
			return true;

		}
	}
	
	public void registerEvents(){
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(this, pl);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {

		Entity p = event.getDamager();

		if (event.getEntity() instanceof ItemFrame) {

			if (p instanceof Player || p instanceof Arrow) {

				event.setCancelled(true);
				
				Arrow a = (Arrow) p;
				
				if(a.getShooter() instanceof Player){
					
					event.setCancelled(true);
					
				}

			}

		}

	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player p = event.getPlayer();

		if (GladiatorsState.isState(GladiatorsState.GAMEPVP)) {
			p.sendMessage(ChatColor.RED + "La partie a commencé !");
			p.sendMessage(ChatColor.DARK_RED + "Tu as été mis en spectateur !");
			p.setGameMode(GameMode.SPECTATOR);
		}

	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if (!(event.getBlockPlaced().getType() == Material.EMERALD_BLOCK)
				&& !(event.getBlockPlaced().getState() instanceof Sign)) {

			event.setCancelled(true);

		}

	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {

		if (e.getLine(1).equals("[setlobby]")) {
			e.setLine(1, ChatColor.AQUA + "[LOBBY]");
			e.setLine(2, ChatColor.RED + String.valueOf(playerInGame.size()) + " / 6");
			
			this.createScoreboard();

			if (pl.getConfig().getConfigurationSection("Sign") == null) {

				pl.getConfig().createSection("Sign.x");
				pl.getConfig().createSection("Sign.y");
				pl.getConfig().createSection("Sign.z");

				pl.getConfig().set("Sign.x", e.getBlock().getX());
				pl.getConfig().set("Sign.y", e.getBlock().getY());
				pl.getConfig().set("Sign.z", e.getBlock().getZ());

				pl.saveConfig();

			} else {

				pl.getConfig().set("Sign.x", e.getBlock().getX());
				pl.getConfig().set("Sign.y", e.getBlock().getY());
				pl.getConfig().set("Sign.z", e.getBlock().getZ());

				pl.saveConfig();

			}

		}

	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		
		Inventory inv = e.getInventory();
		
		if(!(e.getWhoClicked() instanceof Player)){
			return;
		}else{
			
			Player p = (Player) e.getWhoClicked();
			
			if(!inv.getTitle().equals("Gladiators")){
				return;
			}else{
				
				ItemStack item = e.getCurrentItem();
				
				if (item.getType() == Material.DIAMOND_SWORD) {

					if (pl.getConfig().getConfigurationSection("Lobby") == null) {

						pl.getConfig().createSection("Lobby.x");
						pl.getConfig().createSection("Lobby.y");
						pl.getConfig().createSection("Lobby.z");
						pl.saveConfig();

						Location loc = p.getLocation();

						double x = loc.getBlockX() + 0.5;
						double y = loc.getBlockY() + 0.5;
						double z = loc.getBlockZ() + 0.5;

						pl.getConfig().set("Lobby.x", Double.valueOf(x));
						pl.getConfig().set("Lobby.y", Double.valueOf(y));
						pl.getConfig().set("Lobby.z", Double.valueOf(z));
						pl.saveConfig();

						p.sendMessage(ChatColor.GOLD + "Spawn du lobby défini !");

						e.setCancelled(true);
						p.closeInventory();

					} else {

						Location loc = p.getLocation();

						double x = loc.getBlockX() + 0.5;
						double y = loc.getBlockY() + 0.5;
						double z = loc.getBlockZ() + 0.5;

						pl.getConfig().set("Lobby.x", Double.valueOf(x));
						pl.getConfig().set("Lobby.y", Double.valueOf(y));
						pl.getConfig().set("Lobby.z", Double.valueOf(z));
						pl.saveConfig();

						p.sendMessage(ChatColor.GOLD + "Spawn du lobby défini !");

						e.setCancelled(true);
						p.closeInventory();

					}

				}
				
				if (item.getType() == Material.STAINED_GLASS_PANE && item.getDurability() == 14) {
					e.setCancelled(true);
				}
				
				if (item.getType() == Material.LAPIS_BLOCK) {

					if (pl.getConfig().getConfigurationSection("Block") == null) {

						pl.getConfig().createSection("Block.Type");
						pl.getConfig().set("Block.Type", "EMERALD_BLOCK");
						pl.getConfig().createSection("Block.bleu.x");
						pl.getConfig().createSection("Block.bleu.y");
						pl.getConfig().createSection("Block.bleu.z");
						pl.getConfig().createSection("Block.rouge.x");
						pl.getConfig().createSection("Block.rouge.y");
						pl.getConfig().createSection("Block.rouge.z");
						pl.saveConfig();

						Location loc = p.getLocation();

						double x = loc.getBlockX();
						double y = loc.getBlockY();
						double z = loc.getBlockZ();

						pl.getConfig().set("Block.bleu.x", Double.valueOf(x));
						pl.getConfig().set("Block.bleu.y", Double.valueOf(y));
						pl.getConfig().set("Block.bleu.z", Double.valueOf(z));
						pl.saveConfig();

						p.sendMessage(ChatColor.GOLD + "Bloc à détruire (équipe bleu) !");

						e.setCancelled(true);
						p.closeInventory();

					} else {

						Location loc = p.getLocation();

						double x = loc.getBlockX();
						double y = loc.getBlockY();
						double z = loc.getBlockZ();

						pl.getConfig().set("Block.bleu.x", Double.valueOf(x));
						pl.getConfig().set("Block.bleu.y", Double.valueOf(y));
						pl.getConfig().set("Block.bleu.z", Double.valueOf(z));
						pl.saveConfig();

						p.sendMessage(ChatColor.GOLD + "Bloc à détruire (équipe bleu) !");

						e.setCancelled(true);
						p.closeInventory();

					}

				} else if (item.getType() == Material.REDSTONE_BLOCK) {

					if (pl.getConfig().getConfigurationSection("Block") == null) {

						pl.getConfig().createSection("Block.Type");
						pl.getConfig().set("Block.Type", "EMERALD_BLOCK");
						pl.getConfig().createSection("Block.bleu.x");
						pl.getConfig().createSection("Block.bleu.y");
						pl.getConfig().createSection("Block.bleu.z");
						pl.getConfig().createSection("Block.rouge.x");
						pl.getConfig().createSection("Block.rouge.y");
						pl.getConfig().createSection("Block.rouge.z");
						pl.saveConfig();

						Location loc = p.getLocation();

						double x = loc.getBlockX();
						double y = loc.getBlockY();
						double z = loc.getBlockZ();

						pl.getConfig().set("Block.rouge.x", Double.valueOf(x));
						pl.getConfig().set("Block.rouge.y", Double.valueOf(y));
						pl.getConfig().set("Block.rouge.z", Double.valueOf(z));
						pl.saveConfig();

						p.sendMessage(ChatColor.GOLD + "Bloc à détruire (équipe rouge) !");

						e.setCancelled(true);
						p.closeInventory();

					} else {

						Location loc = p.getLocation();

						double x = loc.getBlockX();
						double y = loc.getBlockY();
						double z = loc.getBlockZ();

						pl.getConfig().set("Block.rouge.x", Double.valueOf(x));
						pl.getConfig().set("Block.rouge.y", Double.valueOf(y));
						pl.getConfig().set("Block.rouge.z", Double.valueOf(z));
						pl.saveConfig();

						p.sendMessage(ChatColor.GOLD + "Bloc à détruire (équipe rouge) !");

						e.setCancelled(true);
						p.closeInventory();

					}

				}
				
				if (item.getType() == Material.WOOL && item.getDurability() == 11) {

					Location loc = p.getLocation();

					if (pl.getConfig().getConfigurationSection("Spawns_bleu") == null) {
						pl.getConfig().createSection("Spawns_bleu");
						pl.saveConfig();

						double x = loc.getBlockX() + 0.5;
						double y = loc.getBlockY();
						double z = loc.getBlockZ() + 0.5;

						if (i <= 3) {

							pl.getConfig().getConfigurationSection("Spawns_bleu")
									.createSection(String.valueOf(i));
							pl.getConfig().set("Spawns_bleu." + i + ".x", Double.valueOf(x));
							pl.getConfig().set("Spawns_bleu." + i + ".y", Double.valueOf(y));
							pl.getConfig().set("Spawns_bleu." + i + ".z", Double.valueOf(z));
							pl.saveConfig();

							p.sendMessage(ChatColor.AQUA + "Tu viens de créer un spawn pour les bleus !");

							i++;

							e.setCancelled(true);
							p.closeInventory();
						}

					} else {

						double x = loc.getBlockX() + 0.5;
						double y = loc.getBlockY();
						double z = loc.getBlockZ() + 0.5;

						if (i <= 3) {

							pl.getConfig().getConfigurationSection("Spawns_bleu")
									.createSection(String.valueOf(i));
							pl.getConfig().set("Spawns_bleu." + i + ".x", Double.valueOf(x));
							pl.getConfig().set("Spawns_bleu." + i + ".y", Double.valueOf(y));
							pl.getConfig().set("Spawns_bleu." + i + ".z", Double.valueOf(z));
							pl.saveConfig();

							p.sendMessage(ChatColor.AQUA + "Tu viens de créer un spawn pour les bleus !");

							i++;

							e.setCancelled(true);
							p.closeInventory();
						} else {

							e.setCancelled(true);
							p.closeInventory();
							p.sendMessage(ChatColor.DARK_RED
									+ "Tu ne peux pas créer plus de points de spawns pour les bleus !");
						}
					}
				}

				if (item.getType() == Material.WOOL && item.getDurability() == 14) {

					if (i < 4) {
						p.sendMessage("Tu dois enregistrer les spawns bleus en premier !");
						e.setCancelled(true);
						p.closeInventory();
					}

					Location loc = p.getLocation();

					if (pl.getConfig().getConfigurationSection("Spawns_rouge") == null) {
						pl.getConfig().createSection("Spawns_rouge");
						pl.saveConfig();

						double x = loc.getBlockX() + 0.5;
						double y = loc.getBlockY();
						double z = loc.getBlockZ() + 0.5;

						if (i <= 6 && i > 3) {

							pl.getConfig().getConfigurationSection("Spawns_rouge")
									.createSection(String.valueOf(i));
							pl.getConfig().set("Spawns_rouge." + i + ".x", Double.valueOf(x));
							pl.getConfig().set("Spawns_rouge." + i + ".y", Double.valueOf(y));
							pl.getConfig().set("Spawns_rouge." + i + ".z", Double.valueOf(z));
							pl.saveConfig();

							p.sendMessage(ChatColor.RED + "Tu viens de créer un spawn pour les rouges !");

							i++;

							e.setCancelled(true);
							p.closeInventory();
						}

					} else {

						double x = loc.getBlockX() + 0.5;
						double y = loc.getBlockY();
						double z = loc.getBlockZ() + 0.5;

						if (i <= 6 && i > 3) {

							pl.getConfig().getConfigurationSection("Spawns_rouge")
									.createSection(String.valueOf(i));
							pl.getConfig().set("Spawns_rouge." + i + ".x", Double.valueOf(x));
							pl.getConfig().set("Spawns_rouge." + i + ".y", Double.valueOf(y));
							pl.getConfig().set("Spawns_rouge." + i + ".z", Double.valueOf(z));
							pl.saveConfig();

							p.sendMessage(ChatColor.RED + "Tu viens de créer un spawn pour les rouges !");

							i++;

							e.setCancelled(true);
							p.closeInventory();
						} else if (i > 3) {

							e.setCancelled(true);
							p.closeInventory();
							p.sendMessage(ChatColor.DARK_RED
									+ "Tu ne peux pas créer plus de points de spawns pour les rouges !");
						}
					}
				}
				
			}
			
		}
		
	}
	
	private ItemStack nameItem(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);

		item.setItemMeta(meta);
		return item;
	}

	private ItemStack nameItem(Material item, String name) {
		return nameItem(new ItemStack(item), name);
	}

	private ItemStack nameItem2(ItemStack item, short id, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		item.setDurability(id);

		return item;
	}

	private ItemStack nameItem2(Material item, short id, String name) {
		return nameItem2(new ItemStack(item), id, name);
	}

}
