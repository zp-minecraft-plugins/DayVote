package pro.zackpollard.dayvote;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MyTask2 implements Runnable{
	private World world;
	private DayVote owner;
	
	public MyTask2(DayVote owner, World world){
		this.world = world;
		this.owner = owner;
	}
	
	public void run(){
		if(owner.hasNoVotes(world)){
			owner.setDay(world);
				for (Player p : world.getPlayers()){
					p.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD+ owner.getConfig().getString("DayVote.Phrases.TimeChangedToDay") + " " + world.getName());
				}
		}else{
			if(owner.getConfig().getLong("DayVote.VotingMethod") == 1){
				for (Player p : world.getPlayers()){
					p.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD+ owner.getConfig().getString("DayVote.Phrases.TimeNotAltered.NightRequest"));
				}
			}
			if(owner.getConfig().getLong("DayVote.VotingMethod") == 2){
				for (Player p : world.getPlayers()){
					if(!owner.votedNight.containsKey(world)){
						p.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD+ owner.getConfig().getString("DayVote.Phrases.TimeNotAltered.NotEnoughDayVotes"));
					}
					if(owner.votedNight.containsKey(world)){
						p.sendMessage(ChatColor.RED+ "[DayVote] " + ChatColor.GOLD+ owner.getConfig().getString("DayVote.Phrases.TimeNotAltered.NightRequest"));
					}
				}
			}
		}
		owner.worldVotes.remove(world);
	}
}