package com.thyssoda.gladiators.Engines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.thyssoda.gladiators.Gladiators;
import com.thyssoda.gladiators.Game.GladiatorsState;
import com.thyssoda.gladiators.Scoreboards.CustomScoreboardManager;
import com.thyssoda.gladiators.Scoreboards.ScoreboardRunnable;
import com.thyssoda.gladiators.Utils.ChatUtils;

import net.md_5.bungee.api.ChatColor;

public class WaitingPlayersEngine implements CommandExecutor, Listener {

	private Gladiators pl;
	private ChatUtils cu;
	private CustomScoreboardManager csm;
	private ScoreboardRunnable sr;
	private GladiatorsState gs;
	private GameEngine se;
	
	public WaitingPlayersEngine(Gladiators pl, ChatUtils cu, GameEngine se, ScoreboardRunnable sr) {
		this.pl = pl;
		this.cu = cu;
		this.se = se;
		this.sr = sr;
		this.csm = new CustomScoreboardManager(gs, se, this);
	}
	
	private Location locAtSign = null;
	public ArrayList<UUID> playerInGame = new ArrayList<>();
	public ArrayList<UUID> teamBleu = new ArrayList<>();
	public ArrayList<UUID> teamBleu2 = new ArrayList<>();
	public ArrayList<UUID> teamRouge = new ArrayList<>();
	public ArrayList<UUID> teamRouge2 = new ArrayList<>();
	public Player joueur1, joueur2, joueur3, joueur4, joueur5, joueur6, joueurNone;
	private Location locSign;
	public int u = 0;
	
	public void signUpdate() {

		locSign = new Location(Bukkit.getWorld("world"), pl.getConfig().getDouble("Sign.x"),
				pl.getConfig().getDouble("Sign.y"),
				pl.getConfig().getDouble("Sign.z"));

		World w = locSign.getWorld();

		Block b = w.getBlockAt(locSign);

		if (b.getState() instanceof Sign) {

			Sign sign = (Sign) b.getState();

			sign.setLine(2, ChatColor.RED + String.valueOf(playerInGame.size()) + " / 6");

			sign.update();

		}
		
		
	}
	
	public void setLevel(int timer) {

		for (UUID uuid : playerInGame) {

			Player player = Bukkit.getPlayer(uuid);

			player.setLevel(timer);
			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

		}

	}
	
	public int task;
	public int temps = 10;
	
	public void timer() {
		task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {

			@Override
			public void run() {

				setLevel(temps);

				if (temps != 0) {

					Bukkit.broadcastMessage(ChatColor.GREEN + "La partie commence dans : " + ChatColor.GOLD + ""
							+ ChatColor.BOLD + temps);

				} else if (temps == 0) {

					Bukkit.broadcastMessage(ChatColor.GREEN + "La partie commence !");

					Bukkit.getServer().getScheduler().cancelTask(task);

					se.start();

				} else if (playerInGame.size() < 2) {

					Bukkit.getServer().getScheduler().cancelTask(task);

				}

				temps--;

			}

		}, 20, 20);

	}
	
	public void performClickSignEffect() {

		Location loc = locSign;

		new BukkitRunnable() {
			double t = 0;

			public void run() {

				double x = 0;
				double y = t;
				double z = 0;

				loc.add(x, y, z);
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.spigot().playEffect(loc, Effect.FIREWORKS_SPARK, 3, 0, 0, 0, 0, 0, 1, 10);
				}
				loc.subtract(x, y, z);
				t = t + 0.25;
				if (t > 2.5) {
					this.cancel();
				}
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Gladiators"), 0, 1);

		new BukkitRunnable() {
			double t = 0;

			public void run() {

				double x = 0;
				double y = t;
				double z = 0;

				loc.add(x, y, z);
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.spigot().playEffect(loc, Effect.FIREWORKS_SPARK, 3, 0, 0, 0, 0, 0, 1, 10);
				}
				loc.subtract(x, y, z);
				t = t - 0.25;
				if (t > 2.5) {
					this.cancel();
				}
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Gladiators"), 0, 1);

		new BukkitRunnable() {
			double t = 0;

			public void run() {

				double x = 0;
				double y = t;
				double z = 1;

				loc.add(x, y, z);
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.spigot().playEffect(loc, Effect.FIREWORKS_SPARK, 3, 0, 0, 0, 0, 0, 1, 10);
				}
				loc.subtract(x, y, z);
				t = t + 0.25;
				if (t > 2.5) {
					this.cancel();
				}
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Gladiators"), 0, 1);

		new BukkitRunnable() {
			double t = 0;

			public void run() {

				double x = 0;
				double y = t;
				double z = 1;

				loc.add(x, y, z);
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.spigot().playEffect(loc, Effect.FIREWORKS_SPARK, 3, 0, 0, 0, 0, 0, 1, 10);
				}
				loc.subtract(x, y, z);
				t = t - 0.25;
				if (t > 2.5) {
					this.cancel();
				}
			}

		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Gladiators"), 0, 1);

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();

		for (UUID uuid : playerInGame) {
			Player pl = Bukkit.getPlayer(uuid);
			if (pl == p) {

				p.setDisplayName(cu.getPrefix() + p.getName());

			} else {
				return;
			}
		}
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
	
	ItemStack equipe = new ItemStack(Material.ENDER_CHEST);
	ItemMeta im = equipe.getItemMeta();

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		
		Block b = e.getClickedBlock();
		Action a = e.getAction();
		
		if(!(e.getPlayer() instanceof Player)){
			return;
		}else{
			
			Player p = (Player) e.getPlayer();
			Material item = e.getMaterial();
			
			if(!(a == Action.RIGHT_CLICK_BLOCK)){
				
				if(a == Action.RIGHT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK){
					
				
				if (item == Material.ENDER_CHEST) {

					Inventory newInv = Bukkit.createInventory(null, 9, "Equipe");
					ItemStack equipeBleu = nameItem2(Material.WOOL, (short) 11,
							ChatColor.DARK_AQUA + "Rejoins l'équipe bleue !");
					ItemStack equipeRouge = nameItem2(Material.WOOL, (short) 14,
							ChatColor.DARK_RED + "Rejoins l'équipe rouge !");

					newInv.setItem(2, equipeBleu);
					newInv.setItem(6, equipeRouge);

					p.openInventory(newInv);

				}
				
				}

				return;
			}else{
				
				if(b.getState() instanceof Sign) {
					
					Sign s = (Sign) b.getState();
					
					if (s.getLine(1).equals(ChatColor.AQUA + "[LOBBY]")) {
						
						if (!playerInGame.contains(p.getUniqueId())) {

							Location loc = new Location(Bukkit.getWorld("world"),
									pl.getConfig().getDouble("Lobby.x"),
									pl.getConfig().getDouble("Lobby.y"),
									pl.getConfig().getDouble("Lobby.z"));

							playerInGame.add(p.getUniqueId());
							
							new CustomScoreboardManager(p);
							sr.sendLine();
							p.setScoreboard(CustomScoreboardManager.getScoreboard(p).getMainScoreboard());

							if (pl.getConfig().getConfigurationSection("Players") == null) {

								pl.getConfig().createSection("Players." + p.getName());

								pl.getConfig().createSection("Players." + p.getName() + ".x");

								pl.getConfig().createSection("Players." + p.getName() + ".y");

								pl.getConfig().createSection("Players." + p.getName() + ".z");

								pl.getConfig().createSection("Players." + p.getName() + ".yaw");

								pl.getConfig().createSection("Players." + p.getName() + ".pitch");

								pl.getConfig().createSection("Players.Name." + p.getName());

								pl.getConfig().set("Players." + p.getName() + ".x",
										p.getLocation().getBlockX() + 0.5);

								pl.getConfig().set("Players." + p.getName() + ".y",
										p.getLocation().getBlockY() + 0.5);

								pl.getConfig().set("Players." + p.getName() + ".z",
										p.getLocation().getBlockZ() + 0.5);

								pl.getConfig().set("Players." + p.getName() + ".yaw",
										p.getLocation().getYaw());

								pl.getConfig().set("Players." + p.getName() + ".pitch",
										p.getLocation().getPitch());

								pl.getConfig().set("Players.Name." + p.getName(), p.getName());

								locAtSign = new Location(Bukkit.getWorld("world"),
										pl.getConfig().getDouble("Players." + p.getName() + ".x"),
										pl.getConfig().getDouble("Players." + p.getName() + ".y"),
										pl.getConfig().getDouble("Players." + p.getName() + ".z"),
										(float) pl.getConfig().get("Players." + p.getName() + ".yaw"),
										(float) pl.getConfig()
												.get("Players." + p.getName() + ".pitch"));

								pl.saveConfig();

							} else {

								pl.getConfig().createSection("Players.Name." + p.getName());

								pl.getConfig().createSection("Players." + p.getName() + ".x");

								pl.getConfig().createSection("Players." + p.getName() + ".y");

								pl.getConfig().createSection("Players." + p.getName() + ".z");

								pl.getConfig().createSection("Players." + p.getName() + ".yaw");

								pl.getConfig().createSection("Players." + p.getName() + ".pitch");

								pl.getConfig().set("Players.Name." + p.getName(), p.getName());

								pl.getConfig().set("Players." + p.getName() + ".x",
										p.getLocation().getBlockX() + 0.5);

								pl.getConfig().set("Players." + p.getName() + ".y",
										p.getLocation().getBlockY() + 0.5);

								pl.getConfig().set("Players." + p.getName() + ".z",
										p.getLocation().getBlockZ() + 0.5);

								pl.getConfig().set("Players." + p.getName() + ".yaw",
										p.getLocation().getYaw());

								pl.getConfig().set("Players." + p.getName() + ".pitch",
										p.getLocation().getPitch());

								locAtSign = new Location(Bukkit.getWorld("world"),
										pl.getConfig().getDouble("Players." + p.getName() + ".x"),
										pl.getConfig().getDouble("Players." + p.getName() + ".y"),
										pl.getConfig().getDouble("Players." + p.getName() + ".z"),
										(float) pl.getConfig().get("Players." + p.getName() + ".yaw"),
										(float) pl.getConfig()
												.get("Players." + p.getName() + ".pitch"));

								pl.saveConfig();

							}

							p.teleport(loc);

							signUpdate();

							performClickSignEffect();

							p.getInventory().clear();
							p.getInventory().setItem(4, equipe);
							im.setDisplayName(ChatColor.DARK_PURPLE + "Choisis ton équipe !");
							equipe.setItemMeta(im);

							if (playerInGame.size() == 2) {

								GladiatorsState.setState(GladiatorsState.LOBBY);
								timer();

							}

						} else if (playerInGame.size() == 6) {

							p.sendMessage(ChatColor.RED + "La partie est pleine !");
							e.setCancelled(true);

						}
						
					}
					
				}
				
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
			
			if(!inv.getTitle().equals("Equipe")){
				return;
			}else{
				
				ItemStack item = e.getCurrentItem();

				Player plaayerrs = p;

				if (!teamBleu2.contains(plaayerrs.getUniqueId())
						&& !teamRouge2.contains(plaayerrs.getUniqueId())) {

					if (item.getType() == Material.WOOL && item.getDurability() == 11) {

						if (u == 0) {

							teamBleu.add(plaayerrs.getUniqueId());
							teamBleu2.add(plaayerrs.getUniqueId());

							joueur1 = plaayerrs;

							plaayerrs.sendMessage(ChatColor.GOLD + "Tu as rejoins l'équipe bleu !");

							u++;

							e.setCancelled(true);
							plaayerrs.closeInventory();

						} else if (u == 2) {

							teamBleu.add(plaayerrs.getUniqueId());
							teamBleu2.add(plaayerrs.getUniqueId());

							joueur2 = plaayerrs;

							plaayerrs.sendMessage(ChatColor.GOLD + "Tu as rejoins l'équipe bleu !");

							u++;

							e.setCancelled(true);
							plaayerrs.closeInventory();

						} else if (u == 4) {

							teamBleu.add(plaayerrs.getUniqueId());
							teamBleu2.add(plaayerrs.getUniqueId());

							joueur3 = plaayerrs;

							plaayerrs.sendMessage(ChatColor.GOLD + "Tu as rejoins l'équipe bleu !");

							u++;

							e.setCancelled(true);
							plaayerrs.closeInventory();

						} else {

							plaayerrs.sendMessage(
									ChatColor.RED + "Afin d'équilibrer les équipes tu as rejoins l'équipe rouge !");

							if (u == 1) {

								teamRouge.add(plaayerrs.getUniqueId());
								teamRouge2.add(plaayerrs.getUniqueId());

								joueur4 = plaayerrs;

								u++;

								e.setCancelled(true);
								plaayerrs.closeInventory();

							} else if (u == 3) {

								teamRouge.add(plaayerrs.getUniqueId());
								teamRouge2.add(plaayerrs.getUniqueId());

								joueur5 = plaayerrs;

								u++;

								e.setCancelled(true);
								plaayerrs.closeInventory();

							} else if (u == 5) {

								teamRouge.add(plaayerrs.getUniqueId());
								teamRouge2.add(plaayerrs.getUniqueId());

								joueur6 = plaayerrs;

								e.setCancelled(true);
								plaayerrs.closeInventory();

							}

						}

					}

					if (item.getType() == Material.WOOL && item.getDurability() == 14) {

						if (u == 1) {

							teamRouge.add(plaayerrs.getUniqueId());
							teamRouge2.add(plaayerrs.getUniqueId());

							joueur4 = plaayerrs;

							u++;

							plaayerrs.sendMessage(ChatColor.GOLD + "Tu as rejoins l'équipe rouge !");

							e.setCancelled(true);
							plaayerrs.closeInventory();

						} else if (u == 3) {

							teamRouge.add(plaayerrs.getUniqueId());
							teamRouge2.add(plaayerrs.getUniqueId());

							joueur5 = plaayerrs;

							u++;

							plaayerrs.sendMessage(ChatColor.GOLD + "Tu as rejoins l'équipe rouge !");

							e.setCancelled(true);
							plaayerrs.closeInventory();

						} else if (u == 5) {

							teamRouge.add(plaayerrs.getUniqueId());
							teamRouge2.add(plaayerrs.getUniqueId());

							joueur6 = plaayerrs;

							plaayerrs.sendMessage(ChatColor.GOLD + "Tu as rejoins l'équipe rouge !");

							e.setCancelled(true);
							plaayerrs.closeInventory();

						} else {

							plaayerrs.sendMessage(
									ChatColor.AQUA + "Afin d'équilibrer les équipes tu as rejoins l'équipe bleue !");

							if (u == 0) {

								teamBleu.add(plaayerrs.getUniqueId());
								teamBleu2.add(plaayerrs.getUniqueId());

								joueur1 = plaayerrs;

								joueur1 = plaayerrs;

								u++;

								e.setCancelled(true);
								plaayerrs.closeInventory();

							} else if (u == 2) {

								teamBleu.add(plaayerrs.getUniqueId());
								teamBleu2.add(plaayerrs.getUniqueId());

								joueur2 = plaayerrs;

								u++;

								e.setCancelled(true);
								plaayerrs.closeInventory();

							} else if (u == 4) {

								teamBleu.add(plaayerrs.getUniqueId());
								teamBleu2.add(plaayerrs.getUniqueId());

								joueur3 = plaayerrs;

								u++;

								e.setCancelled(true);
								plaayerrs.closeInventory();

							}

						}

					}

				} else {

					plaayerrs.sendMessage(ChatColor.YELLOW + "Tu as déjà rejoins ta team !");
					e.setCancelled(true);
					plaayerrs.closeInventory();

				}
				
			}
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		
		if(playerInGame.size() > 0){
			
			
			Iterator<UUID> itr = playerInGame.iterator();
			while (itr.hasNext()) {

				Player pl = (Player) Bukkit.getPlayer(itr.next());
				UUID uuid = itr.next();

				if (p == pl) {
					
					u--;

					if (pl == joueur1) {

						teamBleu.remove(uuid);
						teamBleu2.remove(uuid);

						csm.bleu.removePlayer(p);
						joueur1 = null;

						itr.remove();

					}
					if (pl == joueur2) {

						teamBleu.remove(uuid);
						teamBleu2.remove(uuid);

						csm.bleu.removePlayer(p);
						joueur2 = null;

						itr.remove();

					}
					if (pl == joueur3) {

						teamBleu.remove(uuid);
						teamBleu2.remove(uuid);

						csm.bleu.removePlayer(p);
						joueur3 = null;

						itr.remove();

					} else {

						if (pl == joueur4) {

							teamRouge.remove(uuid);
							teamRouge2.remove(uuid);

							csm.rouge.removePlayer(p);
							joueur4 = null;

							itr.remove();

						}

						if (pl == joueur5) {

							teamRouge.remove(uuid);
							teamRouge2.remove(uuid);

							csm.rouge.removePlayer(p);
							joueur5 = null;

							itr.remove();

						}

						if (pl == joueur6) {

							teamRouge.remove(uuid);
							teamRouge2.remove(uuid);

							csm.rouge.removePlayer(p);
							joueur6 = null;

							itr.remove();

						}

					}

					e.setQuitMessage(
							cu.getPrefix() + p.getName() + " s'est déconnecté et a été retiré de la partie");

				}

			}

			playerInGame.remove(p.getUniqueId());
			
			if(playerInGame.size() < 2){
				
				Bukkit.getServer().getScheduler().cancelTask(task);
				GladiatorsState.setState(GladiatorsState.WAIT);
				
			}
			
		}
		
	}
	
	public void registerEvents(){
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(this, pl);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage("Tu dois etre un joueur afin d'executer cette commande !");
			return false;
		}else {
			
			Player p = (Player) sender;
			
			if(locAtSign == null){
				return false;
			}else{
				
				p.teleport(locAtSign);

				p.sendMessage(ChatColor.GOLD + "Tu as été téléporté au lobby !");

				Iterator<UUID> itr = playerInGame.iterator();
				while (itr.hasNext()) {

					Player pl = (Player) Bukkit.getPlayer(itr.next());
					UUID uuid = itr.next();

					if (p == pl) {

						u--;

						if (teamBleu.contains(uuid)) {

							teamBleu.remove(uuid);
							teamBleu2.remove(uuid);

							csm.bleu.removePlayer(p);

							if (joueur1 == p) {

								joueur1 = null;

							} else if (joueur2 == p) {

								joueur2 = null;

							} else if (joueur3 == p) {

								joueur3 = null;

							}

						} else if (teamRouge.contains(uuid)) {

							teamRouge.remove(uuid);
							teamRouge2.remove(uuid);

							csm.rouge.removePlayer(p);

							if (joueur4 == p) {

								joueur4 = null;

							} else if (joueur5 == p) {

								joueur5 = null;

							} else if (joueur6 == p) {

								joueur6 = null;

							}

						}

						itr.remove();
						
						if(CustomScoreboardManager.sb.containsKey(p)){
							
							CustomScoreboardManager.sb.remove(p);

							}
					}

				}

				if (playerInGame.size() < 2) {

					GladiatorsState.setState(GladiatorsState.WAIT);
					Bukkit.getServer().getScheduler().cancelTask(task);

				}

				signUpdate();
				p.getInventory().clear();
				
				return true;

			}
		}
	}

}
