package pro.zackpollard.dayvote;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

public class MyTask implements Runnable{
	private DayVote owner;
	
	public MyTask(DayVote owner){
		this.owner = owner;
	}
	
	public void run(){
		Server s = Bukkit.getServer();
		List<World> worlds = s.getWorlds();
		List<String> ignoredWorlds = owner.getConfig().getStringList("DayVote.IgnoredWorlds");
		for(World worldName : worlds){
			if(ignoredWorlds.contains(worldName.getName())) continue;
			if(worldName.getEnvironment() != Environment.NETHER){
				if(worldName.getEnvironment() != Environment.THE_END){
					if(worldName.getTime() >= 0 ){
						if(worldName.getTime() < owner.getConfig().getLong("DayVote.TimeCheck")){
							owner.haveAsked.remove(worldName);
						}
					}
					if(!owner.haveAsked.contains(worldName)){
						if(worldName.getTime() >= 11800){
							owner.haveAsked.add(worldName);
							for (Player p : worldName.getPlayers()){
								if(owner.getConfig().getLong("DayVote.VotingMethod") == 1){
									p.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD+ owner.getConfig().getString("DayVote.Phrases.VotingStartedMethod1"));
								}
								if(owner.getConfig().getLong("DayVote.VotingMethod") == 2){
									p.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD+ owner.getConfig().getString("DayVote.Phrases.VotingStartedMethod2"));
								}
							}
							owner.reset(worldName);
							Bukkit.getServer().getScheduler().runTaskLater(owner, new MyTask2(owner, worldName), 20 * 20L); //20 ticks in a second, multiplied by 15
						}
					}
				}
			}
		}
	}
}