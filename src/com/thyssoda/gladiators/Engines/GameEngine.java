package com.thyssoda.gladiators.Engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.thyssoda.gladiators.Gladiators;
import com.thyssoda.gladiators.Game.GladiatorsState;
import com.thyssoda.gladiators.Utils.ChatUtils;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_10_R1.PacketPlayInClientCommand.EnumClientCommand;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_10_R1.PlayerConnection;

@SuppressWarnings("deprecation")
public class GameEngine implements Listener {

	private Gladiators pl;
	private CreationEngine ce;
	private WaitingPlayersEngine wpe;
	private ChatUtils cu;

	public GameEngine(Gladiators pl, CreationEngine ce) {
		this.pl = pl;
		this.ce = ce;
		this.wpe = new WaitingPlayersEngine(this.pl, cu, this, this.ce);
	}

	private ArrayList<Location> locsBleu = new ArrayList<Location>();
	private ArrayList<Location> locsRouge = new ArrayList<Location>();

	public HashMap<Player, Location> joueurLoc = new HashMap<Player, Location>();

	int lb = 0;
	int lr = 0;

	public int task4;
	public int temps4 = 480;
	private Location locBleu;
	private Location locRouge;
	public int scoreBleu = 0;
	public int scoreRouge = 0;

	public void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(this, pl);
	}

	private void respawnInstant(final Player player) {
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				PacketPlayInClientCommand paquet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
				((CraftPlayer) player).getHandle().playerConnection.a(paquet);
			}
		}, 5L);
	}

	public void dropSkull(Player killed) {

		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta skullM = (SkullMeta) skull.getItemMeta();

		skullM.setOwner(killed.getName());

		skullM.setDisplayName(ChatColor.GOLD + "Ce crâne te régénère 2 coeurs !");

		skull.setItemMeta(skullM);

		killed.getWorld().dropItemNaturally(killed.getLocation(), skull);

	}

	public HashMap<Player, Integer> kills = new HashMap<>();

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {

		if (!(e.getEntity() instanceof Player)) {

			return;

		} else {

			if (!(e.getEntity().getKiller() instanceof Player)) {

				return;

			} else {

				Player killed = (Player) e.getEntity();
				Player player = (Player) e.getEntity().getKiller();

				if (!kills.containsKey(player)) {
					kills.put(player, 1);
				} else {

					kills.put(player, kills.get(player) + 1);

				}

				if (temps4 != 0) {

					player.playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 5);

					performEffectOnKill(player);

					respawnInstant(killed);

					dropSkull(killed);

					for (UUID uuid : ce.playerInGame) {

						Player p = Bukkit.getPlayer(uuid);

						ce.setScoreboard(p);

					}

					e.setDeathMessage(cu.getPrefix() + killed + " s'est fait massacrer par " + player);

				}

				if (player == wpe.joueur1 || player == wpe.joueur2 || player == wpe.joueur3) {

					if (scoreBleu == 0) {

						scoreBleu++;

					} else if (scoreBleu > 0) {

						scoreBleu++;

					}

				}

				if (player == wpe.joueur4 || player == wpe.joueur5 || player == wpe.joueur6) {

					if (scoreRouge == 0) {

						scoreRouge++;

					} else if (scoreRouge > 0) {

						scoreRouge++;

					}
				}

			}
		}
	}

	public void untilEnd(int score1, int score2) {
		task4 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {

			@Override
			public void run() {

				if (temps4 != 0) {

					if (temps4 == 240) {

						if (score1 > score2) {

							for (UUID str : ce.playerInGame) {

								Player play = Bukkit.getPlayer(str);

								play.sendMessage(ChatColor.BLUE + "A la moitié du temps, l'équipe bleue est en tête !");

							}

						} else if (score2 > score1) {

							for (UUID str : ce.playerInGame) {

								Player play = Bukkit.getPlayer(str);

								play.sendMessage(ChatColor.RED + "A la moitié du temps, l'équipe rouge est en tête !");

							}

						} else if (score1 == score2) {

							Bukkit.broadcastMessage(
									ChatColor.GREEN + "A la moitié du temps les équipes sont à égalité !");

						}

					}

					temps4--;

				} else if (temps4 == 0) {

					Iterator<UUID> itr = ce.playerInGame.iterator();
					while (itr.hasNext()) {

						Player play = (Player) Bukkit.getPlayer(itr.next());

						end(scoreBleu, scoreRouge, play);

						itr.remove();

					}

				}

			}

		}, 20, 20);

	}

	public void start() {

		GladiatorsState.setState(GladiatorsState.GAMEPVP);

		double x = pl.getConfig().getDouble("Spawns_bleu." + 1 + ".x");
		double y = pl.getConfig().getDouble("Spawns_bleu." + 1 + ".y");
		double z = pl.getConfig().getDouble("Spawns_bleu." + 1 + ".z");
		double x1 = pl.getConfig().getDouble("Spawns_rouge." + 4 + ".x");
		double y1 = pl.getConfig().getDouble("Spawns_rouge." + 4 + ".y");
		double z1 = pl.getConfig().getDouble("Spawns_rouge." + 4 + ".z");
		double x2 = pl.getConfig().getDouble("Spawns_bleu." + 2 + ".x");
		double y2 = pl.getConfig().getDouble("Spawns_bleu." + 2 + ".y");
		double z2 = pl.getConfig().getDouble("Spawns_bleu." + 2 + ".z");
		double x3 = pl.getConfig().getDouble("Spawns_rouge." + 5 + ".x");
		double y3 = pl.getConfig().getDouble("Spawns_rouge." + 5 + ".y");
		double z3 = pl.getConfig().getDouble("Spawns_rouge." + 5 + ".z");
		double x4 = pl.getConfig().getDouble("Spawns_bleu." + 3 + ".x");
		double y4 = pl.getConfig().getDouble("Spawns_bleu." + 3 + ".y");
		double z4 = pl.getConfig().getDouble("Spawns_bleu." + 3 + ".z");
		double x5 = pl.getConfig().getDouble("Spawns_rouge." + 6 + ".x");
		double y5 = pl.getConfig().getDouble("Spawns_rouge." + 6 + ".y");
		double z5 = pl.getConfig().getDouble("Spawns_rouge." + 6 + ".z");

		locsBleu.add(0, new Location(Bukkit.getWorld("world"), x, y, z, 180.0f, 1.0f));
		locsBleu.add(1, new Location(Bukkit.getWorld("world"), x2, y2, z2, 180.0f, 1.0f));
		locsBleu.add(2, new Location(Bukkit.getWorld("world"), x4, y4, z4, 180.0f, 1.0f));

		locsRouge.add(0, new Location(Bukkit.getWorld("world"), x1, y1, z1));
		locsRouge.add(1, new Location(Bukkit.getWorld("world"), x3, y3, z3));
		locsRouge.add(2, new Location(Bukkit.getWorld("world"), x5, y5, z5));

		for (UUID uuid : ce.playerInGame) {

			Player pll = Bukkit.getPlayer(uuid);

			if (!ce.playerInGame.contains((UUID) uuid)) {

				if (wpe.u == 0) {

					wpe.teamBleu.add(pll.getUniqueId());
					wpe.teamBleu2.add(pll.getUniqueId());

					wpe.joueur1 = pll;

					pll.sendMessage(ChatColor.GOLD + "Tu n'as pas d'équipe !");

					pll.sendMessage(ChatColor.BLUE + "Tu as été mis dans l'équipe bleu !");

					wpe.u++;

				} else if (wpe.u == 2) {

					wpe.teamBleu.add(pll.getUniqueId());
					wpe.teamBleu2.add(pll.getUniqueId());

					wpe.joueur2 = pll;

					pll.sendMessage(ChatColor.GOLD + "Tu n'as pas d'équipe !");

					pll.sendMessage(ChatColor.BLUE + "Tu as été mis dans l'équipe bleu !");

					wpe.u++;

				} else if (wpe.u == 4) {

					wpe.teamBleu.add(pll.getUniqueId());
					wpe.teamBleu2.add(pll.getUniqueId());

					wpe.joueur3 = pll;

					pll.sendMessage(ChatColor.GOLD + "Tu n'as pas d'équipe !");

					pll.sendMessage(ChatColor.BLUE + "Tu as été mis dans l'équipe bleu !");

					wpe.u++;

				} else {

					pll.sendMessage(ChatColor.GOLD + "Tu n'as pas d'équipe !");

					if (wpe.u == 1) {

						wpe.teamRouge.add(pll.getUniqueId());
						wpe.teamRouge2.add(pll.getUniqueId());

						wpe.joueur4 = pll;

						pll.sendMessage(ChatColor.DARK_RED + "Tu as été mis dans l'équipe rouge !");

						wpe.u++;

					} else if (wpe.u == 3) {

						wpe.teamRouge.add(pll.getUniqueId());
						wpe.teamRouge2.add(pll.getUniqueId());

						wpe.joueur5 = pll;

						pll.sendMessage(ChatColor.DARK_RED + "Tu as été mis dans l'équipe rouge !");

						wpe.u++;

					} else if (wpe.u == 5) {

						wpe.teamRouge.add(pll.getUniqueId());
						wpe.teamRouge2.add(pll.getUniqueId());

						wpe.joueur6 = pll;

						pll.sendMessage(ChatColor.DARK_RED + "Tu as été mis dans l'équipe rouge !");

					}

				}

			}

			if (wpe.teamBleu.contains(uuid)) {

				pll.teleport(locsBleu.get(lb));
				joueurLoc.put(pll, locsBleu.get(lb));
				wpe.teamBleu.remove(pll);
				lb++;

			}

			if (wpe.teamRouge.contains(uuid)) {

				pll.teleport(locsRouge.get(lr));
				joueurLoc.put(pll, locsRouge.get(lr));
				wpe.teamRouge.remove(pll);
				lr++;

			}

		}

		locBleu = new Location(Bukkit.getWorld("world"), pl.getConfig().getDouble("Block.bleu.x"),
				pl.getConfig().getDouble("Block.bleu.y"), pl.getConfig().getDouble("Block.bleu.z"));

		locRouge = new Location(Bukkit.getWorld("world"), pl.getConfig().getDouble("Block.rouge.x"),
				pl.getConfig().getDouble("Block.rouge.y"), pl.getConfig().getDouble("Block.rouge.z"));

		for (UUID uuid : ce.playerInGame) {

			Player playerss = Bukkit.getPlayer(uuid);

			if (wpe.teamBleu2.contains((UUID) uuid)) {

				ce.bleu.addPlayer(Bukkit.getPlayer(uuid));

			} else if (wpe.teamRouge2.contains((UUID) uuid)) {

				ce.rouge.addPlayer(Bukkit.getPlayer(uuid));

			}

			playerss.setGameMode(GameMode.SURVIVAL);
			playerss.setLevel(0);
			playerss.setExp(0.0f);
			playerss.getInventory().clear();
			playerss.setFoodLevel(20);
			playerss.setHealth(20.0d);
			playerss.setNoDamageTicks(200);

		}

		untilEnd(scoreBleu, scoreRouge);

		for (UUID uuid : ce.playerInGame) {

			Player p = Bukkit.getPlayer(uuid);

			ce.setScoreboard(p);

		}

	}

	public void stop() {
		Bukkit.getServer().getScheduler().cancelTask(task4);
		GladiatorsState.setState(GladiatorsState.WAIT);
	}

	private int temps3 = 4;
	private int task3;

	public void endGame() {
		task3 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {

			@Override
			public void run() {

				if (temps3 != 0) {

					for (UUID uuid : ce.playerInGame) {
						Player str = Bukkit.getPlayer(uuid);

						str.playSound(str.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);

					}

					temps3--;

				} else {

					Bukkit.getServer().getScheduler().cancelTask(task3);

				}

			}

		}, 30, 30);

	}

	public void end(int score1, int score2, Player player) {

		GladiatorsState.setState(GladiatorsState.WAIT);

		if (player == wpe.joueur1 || player == wpe.joueur2 || player == wpe.joueur3) {

			PacketPlayOutTitle winTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE,
					ChatSerializer
							.a("{\"text\":\"Fin de la partie !\",\"color\":\"gold\",\"bold\":true,\"underlined\":true}"),
					20, 40, 30);
			PacketPlayOutTitle winSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
					ChatSerializer.a("{\"text\":\"Equipe " + ChatColor.BLUE + "" + ChatColor.BOLD + "bleue gagnante "
							+ ChatColor.RESET + "!\",\"color\":\"white\",\"italic\":true}"),
					20, 40, 30);

			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

			connection.sendPacket(winTitle);
			connection.sendPacket(winSubtitle);

			endGame();

		} else if (player == wpe.joueur4 || player == wpe.joueur5 || player == wpe.joueur6) {

			PacketPlayOutTitle winTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE,
					ChatSerializer
							.a("{\"text\":\"Fin de la partie !\",\"color\":\"gold\",\"bold\":true,\"underlined\":true}"),
					20, 40, 30);
			PacketPlayOutTitle winSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE,
					ChatSerializer.a("{\"text\":\"Equipe " + ChatColor.RED + "" + ChatColor.BOLD + "rouge gagnante "
							+ ChatColor.RESET + "!\",\"color\":\"white\",\"italic\":true}"),
					20, 40, 30);

			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

			connection.sendPacket(winTitle);
			connection.sendPacket(winSubtitle);

			endGame();

		}

		scoreBleu = 0;
		scoreRouge = 0;

		wpe.u = 0;

		if (wpe.teamBleu2.contains((UUID) player.getUniqueId())) {

			lb--;
			joueurLoc.remove(player);
			wpe.teamBleu.add(player.getUniqueId());

		}
		if (wpe.teamRouge2.contains((UUID) player.getUniqueId())) {

			lr--;
			joueurLoc.remove(player);
			wpe.teamRouge.add(player.getUniqueId());

		}

	}

	public void performEffectOnKill(Player player) {

		new BukkitRunnable() {
			Location loc = player.getLocation();
			double t = 0;
			double r = 1;
			double a = 0;

			public void run() {
				t = t + Math.PI / 8;
				a = a + 0.32;
				double x = r * Math.cos(t);
				double y = r * t - a;
				double z = r * Math.sin(t);
				loc.add(x, y, z);
				player.spigot().playEffect(loc, Effect.FLAME, 26, 0, 0, 0, 0, 0, 1, 10);
				loc.subtract(x, y, z);
				if (t > Math.PI * 4) {
					this.cancel();
				}
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Gladiators"), 0, 1);
	}

	public void performEffectOnEmeraldBreak(Block b) {

		Location loc = b.getLocation();

		new BukkitRunnable() {

			double t = 0;
			double r = 1;
			double a = 0;

			public void run() {

				t = t + Math.PI / 8;

				a = a + 0.32;

				double x = r * Math.cos(t) + 0.5;
				double y = r * t - a;
				double z = r * Math.sin(t) + 0.5;

				loc.add(x, y, z);

				for (Player player : Bukkit.getOnlinePlayers()) {

					player.spigot().playEffect(loc, Effect.HAPPY_VILLAGER, 21, 0, 0, 0, 0, 0, 1, 10);

				}

				loc.subtract(x, y, z);

				if (t > Math.PI * 4) {

					this.cancel();

				}
			}

		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Gladiators"), 0, 1);

		new BukkitRunnable() {

			double t = 1;

			public void run() {

				double x = 0.5;
				double y = t;
				double z = 0.5;

				loc.add(x, y, z);

				for (Player player : Bukkit.getOnlinePlayers()) {

					player.spigot().playEffect(loc, Effect.HAPPY_VILLAGER, 21, 0, 0, 0, 0, 0, 1, 10);

				}

				loc.subtract(x, y, z);

				t = t + 0.15;

				if (t > 2.5) {

					this.cancel();

				}
			}

		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("Gladiators"), 0, 3);

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		Block block = event.getBlock();
		Material material = block.getType();

		if (material != Material.EMERALD_BLOCK) {

			if (!(block.getState() instanceof Sign)) {

				if (material == Material.ENDER_CHEST) {

					event.setCancelled(false);

				} else {

					event.setCancelled(true);

				}

			} else {

				event.setCancelled(false);

			}

		} else {

			if (player == wpe.joueur1 || player == wpe.joueur2 || player == wpe.joueur3) {

				if (block.getLocation().equals(locBleu)) {

					event.setCancelled(true);
					player.sendMessage(ChatColor.BLUE + "Tu ne peux pas détruire le bloc de ton équipe !");

				} else {

					performEffectOnEmeraldBreak(block);

					event.setCancelled(true);

					for (UUID uuid : ce.playerInGame) {
						Player p = Bukkit.getPlayer(uuid);

						end(scoreBleu, scoreRouge, p);
						this.stop();

					}

				}

			}

			if (player == wpe.joueur4 || player == wpe.joueur5 || player == wpe.joueur6) {

				if (block.getLocation().equals(locRouge)) {

					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "Tu ne peux pas détruire le bloc de ton équipe !");

				} else {

					performEffectOnEmeraldBreak(block);

					event.setCancelled(true);

					for (UUID uuid : ce.playerInGame) {
						Player p = Bukkit.getPlayer(uuid);

						end(scoreBleu, scoreRouge, p);
						this.stop();

					}
				}
			}

			event.setCancelled(true);

		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {

		Action a = e.getAction();

		if (e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			Material item = e.getMaterial();

			if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
				if (item == Material.SKULL_ITEM) {

					p.getInventory().remove(new ItemStack(item, 1));
					p.updateInventory();

					p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 0));

				}
			}
		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		if (event.getRightClicked() instanceof ItemFrame) {
			Entity e = (ItemFrame) event.getRightClicked();
			ItemFrame i = ((ItemFrame) e);

			ItemStack bow = new ItemStack(Material.BOW);
			ItemStack epDiams = new ItemStack(Material.DIAMOND_SWORD);
			ItemStack hDiams = new ItemStack(Material.DIAMOND_AXE);
			ItemStack gApple = new ItemStack(Material.GOLDEN_APPLE);
			ItemStack bRod = new ItemStack(Material.BLAZE_ROD);
			ItemStack pot = new ItemStack(Material.BREWING_STAND_ITEM);

			if (i.getRotation().equals(Rotation.NONE)) {
				event.setCancelled(true);
			}

			if (i.getItem().equals(bow)) {

				ItemStack casqueMaille = new ItemStack(Material.CHAINMAIL_HELMET);
				ItemStack plastronMaille = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
				ItemStack jambesMaille = new ItemStack(Material.CHAINMAIL_LEGGINGS);
				ItemStack bottesMaille = new ItemStack(Material.CHAINMAIL_BOOTS);

				casqueMaille.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				plastronMaille.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				jambesMaille.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				bottesMaille.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

				ItemStack epeePierre = new ItemStack(Material.STONE_SWORD);

				ItemStack arc = new ItemStack(Material.BOW);
				ItemStack fleche = new ItemStack(Material.ARROW);

				arc.addEnchantment(Enchantment.ARROW_INFINITE, 1);
				arc.addEnchantment(Enchantment.ARROW_DAMAGE, 3);

				ItemStack pioFer = new ItemStack(Material.IRON_PICKAXE);

				ItemStack os = new ItemStack(Material.BONE);

				os.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

				ItemStack steak = new ItemStack(Material.COOKED_BEEF, 32);

				PlayerInventory inv = player.getInventory();
				inv.clear();

				inv.addItem(epeePierre, arc, fleche, pioFer, os, steak);
				inv.setHelmet(casqueMaille);
				inv.setChestplate(plastronMaille);
				inv.setLeggings(jambesMaille);
				inv.setBoots(bottesMaille);
			}

			if (i.getItem().equals(epDiams)) {

				ItemStack casqueMaille = new ItemStack(Material.CHAINMAIL_HELMET);
				ItemStack plastronMaille = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
				ItemStack jambesMaille = new ItemStack(Material.CHAINMAIL_LEGGINGS);
				ItemStack bottesMaille = new ItemStack(Material.CHAINMAIL_BOOTS);

				ItemStack epeeFer = new ItemStack(Material.IRON_SWORD);

				epeeFer.addEnchantment(Enchantment.DAMAGE_ALL, 4);

				ItemStack pioPierre = new ItemStack(Material.STONE_PICKAXE);

				pioPierre.addEnchantment(Enchantment.DIG_SPEED, 2);

				ItemStack os = new ItemStack(Material.BONE);

				os.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

				ItemStack steak = new ItemStack(Material.COOKED_BEEF, 32);

				PlayerInventory inv = player.getInventory();
				inv.clear();

				inv.addItem(epeeFer, pioPierre, os, steak);
				inv.setHelmet(casqueMaille);
				inv.setChestplate(plastronMaille);
				inv.setLeggings(jambesMaille);
				inv.setBoots(bottesMaille);

			}

			if (i.getItem().equals(hDiams)) {

				ItemStack casqueCuir = new ItemStack(Material.LEATHER_HELMET);
				ItemStack plastronCuir = new ItemStack(Material.LEATHER_CHESTPLATE);
				ItemStack jambesCuir = new ItemStack(Material.LEATHER_LEGGINGS);
				ItemStack bottesCuir = new ItemStack(Material.LEATHER_BOOTS);

				ItemStack hacheDiams = new ItemStack(Material.DIAMOND_AXE);

				hacheDiams.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

				ItemStack pioBois = new ItemStack(Material.WOOD_PICKAXE);

				pioBois.addEnchantment(Enchantment.DIG_SPEED, 2);

				ItemStack os = new ItemStack(Material.BONE);

				os.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

				ItemStack steak = new ItemStack(Material.COOKED_BEEF, 32);

				PlayerInventory inv = player.getInventory();
				inv.clear();

				inv.addItem(hacheDiams, pioBois, os, steak);
				inv.setHelmet(casqueCuir);
				inv.setChestplate(plastronCuir);
				inv.setLeggings(jambesCuir);
				inv.setBoots(bottesCuir);

			}

			if (i.getItem().equals(gApple)) {

				ItemStack casqueFer = new ItemStack(Material.IRON_HELMET);
				ItemStack plastronFer = new ItemStack(Material.LEATHER_CHESTPLATE);
				ItemStack jambesFer = new ItemStack(Material.IRON_LEGGINGS);
				ItemStack bottesFer = new ItemStack(Material.LEATHER_BOOTS);

				ItemStack epeeFer = new ItemStack(Material.IRON_SWORD);

				epeeFer.addEnchantment(Enchantment.DAMAGE_ALL, 2);

				ItemStack arc = new ItemStack(Material.BOW);
				ItemStack fleche = new ItemStack(Material.ARROW, 10);

				arc.addEnchantment(Enchantment.ARROW_DAMAGE, 2);

				ItemStack pioPierre = new ItemStack(Material.STONE_PICKAXE);

				pioPierre.addEnchantment(Enchantment.DIG_SPEED, 1);

				ItemStack pommeOr = new ItemStack(Material.GOLDEN_APPLE);

				ItemStack os = new ItemStack(Material.BONE);

				os.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

				ItemStack steak = new ItemStack(Material.COOKED_BEEF, 32);

				PlayerInventory inv = player.getInventory();
				inv.clear();

				inv.addItem(epeeFer, arc, fleche, pioPierre, pommeOr, os, steak);
				inv.setHelmet(casqueFer);
				inv.setChestplate(plastronFer);
				inv.setLeggings(jambesFer);
				inv.setBoots(bottesFer);

			}

			if (i.getItem().equals(bRod)) {

				ItemStack casqueFer = new ItemStack(Material.IRON_HELMET);
				ItemStack plastronMaille = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
				ItemStack jambesFer = new ItemStack(Material.IRON_LEGGINGS);
				ItemStack bottesMaille = new ItemStack(Material.CHAINMAIL_BOOTS);

				ItemStack epeeDiams = new ItemStack(Material.DIAMOND_SWORD);

				ItemStack blazeRod = new ItemStack(Material.BLAZE_ROD);

				blazeRod.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);

				ItemStack pioPierre = new ItemStack(Material.STONE_PICKAXE);

				ItemStack os = new ItemStack(Material.BONE);

				os.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

				ItemStack steak = new ItemStack(Material.COOKED_BEEF, 32);

				PlayerInventory inv = player.getInventory();
				inv.clear();

				inv.addItem(epeeDiams, blazeRod, pioPierre, os, steak);
				inv.setHelmet(casqueFer);
				inv.setChestplate(plastronMaille);
				inv.setLeggings(jambesFer);
				inv.setBoots(bottesMaille);

			}

			if (i.getItem().equals(pot)) {

				ItemStack casqueCuir = new ItemStack(Material.LEATHER_HELMET);
				ItemStack plastronMaille = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
				ItemStack jambesCuir = new ItemStack(Material.LEATHER_LEGGINGS);
				ItemStack bottesMaille = new ItemStack(Material.CHAINMAIL_BOOTS);

				ItemStack epeeFer = new ItemStack(Material.IRON_SWORD);

				epeeFer.addEnchantment(Enchantment.DAMAGE_ALL, 2);

				ItemStack pioFer = new ItemStack(Material.IRON_PICKAXE);

				Potion p1 = new Potion(PotionType.STRENGTH, 1, true);

				ItemStack potion1 = p1.toItemStack(1);

				Potion p2 = new Potion(PotionType.SPEED, 2, true);

				ItemStack potion2 = p2.toItemStack(1);

				Potion p3 = new Potion(PotionType.POISON, 1, true);

				ItemStack potion3 = p3.toItemStack(1);

				ItemStack os = new ItemStack(Material.BONE);

				os.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

				ItemStack steak = new ItemStack(Material.COOKED_BEEF, 32);

				PlayerInventory inv = player.getInventory();
				inv.clear();

				inv.addItem(epeeFer, pioFer, potion1, potion2, potion3, os, steak);
				inv.setHelmet(casqueCuir);
				inv.setChestplate(plastronMaille);
				inv.setLeggings(jambesCuir);
				inv.setBoots(bottesMaille);

			}

		} else {

			Entity et = event.getRightClicked();
			Vector v = new Vector(player.getLocation().getDirection().getX(),
					player.getLocation().getDirection().getY() + 0.4, player.getLocation().getDirection().getZ());
			v.multiply(2.5);
			if (player.isSneaking() && player.getHealth() <= 10) {
				et.setVelocity(v);
			}

		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (GladiatorsState.isState(GladiatorsState.GAMEPVP)) {

			Player p = event.getPlayer();
			event.setRespawnLocation(joueurLoc.get(p));

		}
	}

}
