package pro.zackpollard.dayvote;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class ChangeTime implements Runnable {
	
	private World world;
	private DayVote owner;
	
	public ChangeTime(DayVote owner, World world) {
		
		this.world = world;
		this.owner = owner;
	}
	
	@Override
	public void run() {
		
		if(!(world.getTime() > 0 && world.getTime() < 100)){
			
			world.setTime(world.getTime() + 30);
			
			Bukkit.getServer().getScheduler().runTaskLater(owner, new ChangeTime(owner, world), 1);
		}
	}
}