package com.thyssoda.gladiators.Engines;

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

import com.thyssoda.gladiators.Gladiators;
import com.thyssoda.gladiators.Game.GladiatorsState;

public class CreationEngine implements CommandExecutor, Listener {

	private Gladiators pl;
	private WaitingPlayersEngine wpe;
	
	public CreationEngine(Gladiators pl, WaitingPlayersEngine wpe) {
		this.pl = pl;
		this.wpe = wpe;
	}
	
	private int i = 1;

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
			e.setLine(2, ChatColor.RED + String.valueOf(wpe.playerInGame.size()) + " / 6");

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
