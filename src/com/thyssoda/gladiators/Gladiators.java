package com.thyssoda.gladiators;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import com.thyssoda.gladiators.Engines.CreationEngine;
import com.thyssoda.gladiators.Engines.GameEngine;
import com.thyssoda.gladiators.Engines.WaitingPlayersEngine;
import com.thyssoda.gladiators.Game.GladiatorsState;
import com.thyssoda.gladiators.Utils.ChatUtils;

public class Gladiators extends JavaPlugin {

	private CreationEngine ce;
	private WaitingPlayersEngine wpe;
	private ChatUtils cu;
	private GameEngine se;

	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		Logger logger = getLogger();

		this.ce = new CreationEngine(this, wpe, se);
		ce.registerEvents();
		ce.createScoreboard();
		this.wpe = new WaitingPlayersEngine(this, cu, se, ce);
		wpe.registerEvents();
		wpe.signUpdate();
		this.se = new GameEngine(this, ce);
		se.registerEvents();

		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(sb);
		}

		GladiatorsState.setState(GladiatorsState.WAIT);

		registerCommands();
		registerConfig();

		logger.info(pdfFile.getName() + " has been enabled (v" + pdfFile.getVersion() + ")");
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		Logger logger = getLogger();

		logger.info(pdfFile.getName() + " has been disabled (v" + pdfFile.getVersion() + ")");
	}

	public void registerCommands() {
		getCommand("gladiators").setExecutor(new CreationEngine(this, wpe, se));
		getCommand("leave").setExecutor(new WaitingPlayersEngine(this, cu, se, ce));
	}

	public void registerConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		saveDefaultConfig();
		reloadConfig();
	}

}
