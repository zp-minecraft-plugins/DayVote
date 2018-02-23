package pro.zackpollard.dayvote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DayVote extends JavaPlugin{
	private static final Logger log = Logger.getLogger("Minecraft");
	public HashMap<World, Integer> worldVotes;
	public HashMap<World, ArrayList<Player>> hasVoted;
	public ArrayList<World> haveAsked;
	public HashMap<World, Integer> votedNight;
	
	public void onEnable(){
		
		final FileConfiguration config = this.getConfig();
        config.options().header("Voting Methods");
        config.addDefault("DayVote.VotingMethod", Integer.valueOf(1));
        config.addDefault("DayVote.VotingMethodsSettings.Method2.DayVotesNeeded", Integer.valueOf(3));
        config.addDefault("DayVote.TimeCheck", Integer.valueOf(11800));

		config.addDefault("DayVote.IgnoredWorlds", Arrays.asList("ignoredWorld1", "ignoredWorld2"));

        config.addDefault("DayVote.Phrases.VotingNotStartedYet", "Voting hasn't started for your world yet.");
        config.addDefault("DayVote.Phrases.AlreadyVoted", "You've already voted for this world!");
        config.addDefault("DayVote.Phrases.VotedNight", "Voted for night in");
        config.addDefault("DayVote.Phrases.VotedDay", "Voted for day in");
        config.addDefault("DayVote.Phrases.VotingStartedMethod1", "Type /dv night' to keep the night");
        config.addDefault("DayVote.Phrases.VotingStartedMethod2", "Type /dv day to vote for day and /dv night to vote for night");
        config.addDefault("DayVote.Phrases.TimeChangedToDay", "Time was changed to day in");
        config.addDefault("DayVote.Phrases.TimeNotAltered.NightRequest", "The time was not altered because a player requested night");
        config.addDefault("DayVote.Phrases.TimeNotAltered.NotEnoughDayVotes", "The time was not altered because there were not enough votes for day");
        
		worldVotes = new HashMap<World, Integer>();
		hasVoted = new HashMap<World, ArrayList<Player>>();
		haveAsked = new ArrayList<World>();
		votedNight = new HashMap<World, Integer>();
		Bukkit.getServer().getScheduler().runTaskTimer(this, new MyTask(this), 0, 600L);
		log.info("DayVote Version 0.4 Enabled");
        config.options().copyDefaults(true);
        saveConfig();
	}	
	
	public void onDisable(){
		log.info("DayVote Version 0.4 Disabled");
	}
	
	public void reset(World w){
		worldVotes.put(w, 0);
		hasVoted.put(w, new ArrayList<Player>());
		votedNight.remove(w);
	}
	
	public boolean hasNoVotes(World world){
		if(this.getConfig().getLong("DayVote.VotingMethod") == 1){
			return (worldVotes.containsKey(world) && worldVotes.get(world) == 0);
		}
		if(this.getConfig().getLong("DayVote.VotingMethod") == 2){
			return (worldVotes.containsKey(world) && worldVotes.get(world) >= this.getConfig().getLong("DayVote.VotingMethodsSettings.Method2.DayVotesNeeded"));
		}
		return false;
	}
	
	public void setDay(World world) {
		
		Bukkit.getServer().getScheduler().runTask(this, new ChangeTime(this, world));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(label.equalsIgnoreCase("dv")){
				World w = player.getLocation().getWorld();
				if(args.length == 1){
					if(this.getConfig().getLong("DayVote.VotingMethod") == 2){
						if(args[0].equalsIgnoreCase("day")){ // command was /dv day
							if(!worldVotes.containsKey(w)){
								player.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD + this.getConfig().getString("DayVote.Phrases.VotingNotStartedYet"));
								return true;
							}
							if(hasVoted.get(w).contains(player)){
								player.sendMessage(this.getConfig().getString("DayVote.Phrases.AlreadyVoted"));
							} else {
								worldVotes.put(w, worldVotes.get(w)+1);
								hasVoted.get(w).add(player);
								player.sendMessage(this.getConfig().getString("DayVote.Phrases.VotedDay") + " " + w.getName());
							}
							return true;
						}
					}
					if(this.getConfig().getLong("DayVote.VotingMethod") == 1){
						if(args[0].equalsIgnoreCase("night")){ //command was /dv night
							if(!worldVotes.containsKey(w)){
								player.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD+ this.getConfig().getString("DayVote.Phrases.VotingNotStartedYet"));
								return true;
							}
							if(hasVoted.get(w).contains(player)){
								player.sendMessage(this.getConfig().getString("DayVote.Phrases.AlreadyVoted"));
							} else {
								worldVotes.put(w, worldVotes.get(w)-1);
								votedNight.put(w, 1);
								hasVoted.get(w).add(player);
								player.sendMessage(this.getConfig().getString("DayVote.Phrases.VotedNight") + " " + w.getName());
							}
							return true;
						}
					}
					if(this.getConfig().getLong("DayVote.VotingMethod") == 2){
						if(args[0].equalsIgnoreCase("night")){ //command was /dv night
							if(!worldVotes.containsKey(w)){
								player.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD+ this.getConfig().getString("DayVote.Phrases.VotingNotStartedYet"));
								return true;
							}
							if(hasVoted.get(w).contains(player)){
								player.sendMessage(this.getConfig().getString("DayVote.Phrases.AlreadyVoted"));
							} else {
								worldVotes.put(w, worldVotes.get(w)-10000);
								votedNight.put(w, 1);
								hasVoted.get(w).add(player);
								player.sendMessage(this.getConfig().getString("DayVote.Phrases.VotedNight") + " " + w.getName());
							}
						return true;
						}
					}
					if(args[0].equalsIgnoreCase("reload")){
						if(player.hasPermission("dayvote.reload")){
							this.reloadConfig();
							player.sendMessage("The DayVote config has been reloaded");
						} else {
							player.sendMessage("You don't have permission to reload the DayVote config, sorry");
						}
					return true;
					}
				}
			}
		}
		return false;
	}
}