package com.thyssoda.gladiators;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.thyssoda.gladiators.Engines.CreationEngine;
import com.thyssoda.gladiators.Engines.GameEngine;
import com.thyssoda.gladiators.Engines.WaitingPlayersEngine;
import com.thyssoda.gladiators.Game.GladiatorsState;
import com.thyssoda.gladiators.Scoreboards.CustomScoreboardManager;
import com.thyssoda.gladiators.Scoreboards.ScoreboardRunnable;
import com.thyssoda.gladiators.Utils.ChatUtils;

public class Gladiators extends JavaPlugin {
	
	private CreationEngine ce;
	private WaitingPlayersEngine wpe;
	private ChatUtils cu;
	private GameEngine se;
	private ScoreboardRunnable sr;
	private CustomScoreboardManager csm;

	public void onEnable(){
		PluginDescriptionFile pdfFile = getDescription();
		Logger logger = getLogger();
		
		this.ce = new CreationEngine(this, wpe);
		ce.registerEvents();
		this.wpe = new WaitingPlayersEngine(this, cu, se, sr);
		wpe.registerEvents();
		wpe.signUpdate();
		this.se = new GameEngine(this, csm);
		se.registerEvents();
				
		new ScoreboardRunnable().runTaskTimer(this, 20L, 20L);
		
		GladiatorsState.setState(GladiatorsState.WAIT);
		
		registerCommands();
		registerConfig();
		
		
		logger.info(pdfFile.getName()+" has been enabled (v"+pdfFile.getVersion()+")");
	}
	
	public void onDisable(){
		PluginDescriptionFile pdfFile = getDescription();
		Logger logger = getLogger();
		
		se.end(se.scoreBleu, se.scoreRouge, wpe.joueurNone);
		
		logger.info(pdfFile.getName() + " has been disabled (v" + pdfFile.getVersion() + ")");
	}
	
	public void registerCommands(){
		getCommand("gladiators").setExecutor(new CreationEngine(this, wpe));
		getCommand("leave").setExecutor(new WaitingPlayersEngine(this, cu, se, sr));
	}
	
	public void registerConfig(){
		getConfig().options().copyDefaults(true);
		saveConfig();
		saveDefaultConfig();
		reloadConfig();
	}
	
}
