package note;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoteThread extends Thread
{
	int id;
	int timer;
	Location location;
	boolean isPlayer;
	CommandSender sender;
	Sheet sheet;

	public NoteThread(CommandSender cs, int timer, int id)
	{
		this.id = id;
		this.sheet = null;
		this.timer = timer;
		this.sender = cs;
		this.isPlayer = true;
	}
	
	public NoteThread(CommandSender cs, Sheet sh, int id)
	{
		this.id = id;
		this.sender = cs;
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
		if (this.sheet == null)
		{
			try
			{
				Thread.sleep(this.timer);
			}
			catch (InterruptedException e)
			{
				return;
			}

			main.Main.PLAYINGLIST.remove(this.id);
			main.Main.LOCK.remove(this.id);
			
			return;
		}
		
		if (this.isPlayer)
		{
			Player p = ((Player)(this.sender));
			Note n = this.sheet.next();
			while (p.isOnline() && n != null)
			{
				p.playSound(p.getLocation(), n.getSound(), n.getVolume(), n.getPitch());
				
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
					p.sendMessage(main.Main.SYSTEM + ChatColor.RED + "노래가 강제중지 되었습니다.");
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
		
//		for (NoteThread nt : main.Main.PLAYINGLIST.get(this.id))
//		{
//			if (!this.equals(nt) && nt.isAlive())
//			{
//				System.out.println("건너뜀");
//				return;
//			}
//		}
//
//		System.out.println("스레드 삭제됨");
//		main.Main.PLAYINGLIST.remove(this.id);
//		main.Main.LOCK.remove(this.id);
	}
}
