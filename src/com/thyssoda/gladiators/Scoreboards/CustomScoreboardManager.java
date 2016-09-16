package com.thyssoda.gladiators.Scoreboards;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.thyssoda.gladiators.Engines.GameEngine;
import com.thyssoda.gladiators.Engines.WaitingPlayersEngine;
import com.thyssoda.gladiators.Game.GladiatorsState;

import net.md_5.bungee.api.ChatColor;

public class CustomScoreboardManager implements ScoreboardManager{

	private GladiatorsState gs;
	private WaitingPlayersEngine wpe;
	private GameEngine se;
	
	public CustomScoreboardManager(GladiatorsState gs, GameEngine se, WaitingPlayersEngine wpe){
		this.gs = gs;
		this.se = se;
		this.wpe = wpe;
	}
	
	public Player player;
	public Scoreboard scoreboard;
	public Objective obj;
	public Objective objHealthTab;
	public Objective objHealthBN;
	public Team bleu;
	public Team rouge;
	int kills;
	int lastkills;

	public static HashMap<Player, CustomScoreboardManager> sb = new HashMap<>();
	
	private String name = "gladiators";
	private String name2 = "hpTab";
	private String name3 = "hpBN";
	
	public CustomScoreboardManager(Player p){
		
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.player = p;
		
		if(sb.containsKey(p)) return;
		
		sb.put(p, this);
		
		Random r = new Random();
		
		this.name = "sb."+r.nextInt(1000000);
		this.name2 = "sb."+r.nextInt(1000000);
		this.name3 = "sb."+r.nextInt(1000000);

		obj = scoreboard.registerNewObjective(name, "dummy");
		obj = scoreboard.getObjective(name);
		objHealthTab = scoreboard.registerNewObjective(name2, "health");
		objHealthTab = scoreboard.getObjective(name2);
		objHealthBN = scoreboard.registerNewObjective(name3, "health");
		objHealthBN = scoreboard.getObjective(name3);
		
		obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Gladiators");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		objHealthTab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		objHealthBN.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objHealthBN.setDisplayName("/ 20");
		
		bleu = scoreboard.registerNewTeam("Bleu");

		bleu.setPrefix(ChatColor.BLUE + "[BLUE]");
		bleu.setAllowFriendlyFire(false);

		rouge = scoreboard.registerNewTeam("Rouge");

		rouge.setPrefix(ChatColor.RED + "[RED]");
		rouge.setAllowFriendlyFire(false);

		
		
	}
	
	public Team teamBlue() {
		return bleu;
	}
	public Team teamRed() {
		return rouge;
	}

	@SuppressWarnings("static-access")
	public void refresh(){
		
		if(gs.isState(gs.WAIT)){
	           
            int players = wpe.playerInGame.size() - 1;
           
            obj.getScoreboard().resetScores(""+players);
            obj.getScore(""+wpe.playerInGame.size()).setScore(9);
 
            obj.getScoreboard().resetScores("§7En attente de joueurs !");
            obj.getScore("§7En attente de joueurs !").setScore(7);
           
           
        }else if(gs.isState(gs.LOBBY)){
           
            int timer = wpe.temps - 1;
        	
        	obj.getScoreboard().resetScores("§7En attente de joueurs !");
            obj.getScore("§7Lancement de la partie dans : ").setScore(7);
            
            obj.getScoreboard().resetScores(""+wpe.temps);
            obj.getScore(""+timer).setScore(6);
       
        }else{

        	if(se.kills.containsKey(this.player)){
        		        		
            kills = se.kills.get(this.player);
            lastkills = kills - 1;
        	
        	}
            
            int players = wpe.playerInGame.size() - 1;
            int killsBleu = se.scoreBleu;
            int lastkillsBleu = se.scoreBleu - 1;
            int killsRouge = se.scoreRouge;
            int lastkillsRouge = se.scoreRouge - 1;
           
            int timer = 480 - se.temps4;
            int lastTimer = timer + 1;
            String lastS = new SimpleDateFormat("mm:ss").format(lastTimer * 1000);
            String S = new SimpleDateFormat("mm:ss").format(timer * 1000);
           
            obj.getScoreboard().resetScores("§7Lancement de la partie dans : ");
            obj.getScoreboard().resetScores(""+wpe.temps);
           
            obj.getScoreboard().resetScores(""+players);
            obj.getScore(""+wpe.playerInGame.size()).setScore(9);
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
 
        	
        }
		
	}
	
	public void getLine(){
		

        obj.getScore("§8 ").setScore(11);
        obj.getScore("§7Joueurs: ").setScore(10);
        obj.getScore("§e ").setScore(8);
        obj.getScore("§a ").setScore(5);
       
		
	}
	
	public static CustomScoreboardManager getScoreboard(Player p){
		return sb.get(p);
	}
	
	@Override
	public Scoreboard getMainScoreboard() {
		return scoreboard;
	}

	@Override
	public Scoreboard getNewScoreboard() {
		return null;
	}
	
}
