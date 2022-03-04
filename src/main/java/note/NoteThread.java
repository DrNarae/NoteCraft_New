package note;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class NoteThread extends Thread
{
	int id;
	Location location;
	boolean isPlayer;
	Player player;
	Sheet sheet;
	
	public NoteThread(Player p, Sheet sh, int id)
	{
		this.id = id;
		this.player = p;
		this.sheet = sh;
		this.isPlayer = true;
	}
	
	public NoteThread(double x, double y, double z, World world, Sheet sh, int id)
	{
		this.id = id;
		this.location = new Location(world, x, y, z);
		this.sheet = sh;
		this.isPlayer = false;
	}
	
	public Sheet getSheet()
	{
		return this.sheet;
	}
	
	public void run()
	{
		if (this.isPlayer)
		{
			Note n = this.sheet.next();
			while (this.player.isOnline() && n != null)
			{
				this.player.playSound(this.player.getLocation(), n.getSound(), n.getVolume(), n.getPitch());
				
//				System.out.println("---------");
//				System.out.println(n.getSleep());
//				System.out.println(n.getVolume());
//				System.out.println(n.getPitch());
//				System.out.println(n.getSound());
				
				try
				{
					Thread.sleep(n.getSleep());
				}
				catch (InterruptedException e)
				{
					this.player.sendMessage(ChatColor.RED + "노래가 강제중지 되었습니다.");
					break;
				}
				
				n = this.sheet.next();
			}
		}
		else
		{ 
			Note n = this.sheet.next();
			while (n != null)
			{
				this.location.getWorld().playSound(this.location, n.getSound(), n.getVolume(), n.getPitch());
				try
				{
					Thread.sleep(n.getSleep());
				}
				catch (InterruptedException e)
				{
					break;
				}
				
				n = this.sheet.next();
			}
		}
		
		main.Main.PLAYINGLIST.remove(this.id);
		main.Main.LOCK.remove(this.id);
	}
}
