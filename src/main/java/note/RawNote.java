package note;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RawNote
{
	private int pow;
	private int bpm;
	private int beat;
	private int octave;
	private float volume;
	private boolean isTie;
	private boolean isFlat;
	private boolean isSharp;
	private boolean isNatural;
	private boolean isStaccato;
	private boolean isDot;
	private boolean isHarmony;
	private String sound;
	private CommandSender sender;
	
	public RawNote()
	{
		this.pow = 0;
		this.bpm = 100;
		this.beat = 4;
		this.octave = 0;
		this.volume = 1.0f;
		this.isTie = false;
		this.isFlat = false;
		this.isSharp = false;
		this.isNatural = false;
		this.isStaccato = false;
		this.isDot = false;
		this.isHarmony = false;
		this.sound = "minecraft:block.note_block.guitar";
		this.sender = null;
	}
	
	public Note getNote()
	{
		Note note = new Note();
		String errmsg = errorCheck();
		
		if (errmsg.equals(""))
		{
			// aviliable beat
			if (this.beat <= 0) this.beat = 1;
			
			// aviliable bpm
			if (this.bpm <= 0) this.bpm = 1;
			if (this.bpm > 200) this.bpm = 200;
			
			// aviliable volume
			if (this.volume < 0.0f) this.volume = 0.0f;
			if (this.volume > 10.0f) this.volume = 10.0f;

			// errorcheck
			// order
			// dot => staccato => sharp => flat => natural => harmony => tie => . . . => bpm
			
			int sleep = (int)(200000 / (double)(bpm*this.beat));
			float pitch = this.pow;
			
			if (this.isDot)
			{
				sleep *= 1.5;
			}
			
			if (this.isStaccato)
			{
				sleep /= 2.0;
			}
			
			if (this.isSharp && !this.isNatural)
			{
				pitch++;
			}
			
			if (this.isFlat && !this.isNatural)
			{
				pitch--;
			}
			
			if (this.isHarmony)
			{
				sleep = 0;
			}
			
			if (this.isTie)
			{
				
			}
			
			// if increase bpm, sleep is short
			// if decrease bpm, sleep is long
			// if increase beat, sleep is short
			// if decrease beat, sleep is long
			
			pitch = (float)Math.pow(2, ((pitch + this.octave)/12.0));
			
			note.setSleep(sleep);
			note.setPitch(pitch);
			note.setVolume(this.volume);
			note.setSound(this.sound);
			
			return note;
		}
		else
		{
			if (main.Main.WARNINGALERT)
			{
				this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + errmsg);
			}
			if (!main.Main.IGNOREWRONGNOTE)
			{
				return null;
			}
		} 
		
		return null;
	}
	
	public int getPow()
	{
		return this.pow;
	}
	
	public void setPow(int p)
	{
		this.pow = p;
	}
	
	public int getBPM()
	{
		return this.bpm;
	}
	
	public void setBPM(int b)
	{
		this.bpm = b;
	}
	
	public int getOctave()
	{
		return this.octave;
	}
	
	public void setOctave(int o)
	{
		this.octave = o;
	}
	
	public int getBeat()
	{
		return this.beat;
	}
	
	public void setBeat(int b)
	{
		this.beat = b;
	}
	
	public float getVolume()
	{
		return this.volume;
	}
	
	public void setVolume(float v)
	{
		this.volume = v;
	}
	
	public boolean getTie()
	{
		return this.isTie;
	}
	
	public void setTie(boolean t)
	{
		this.isTie = t;
	}
	
	public boolean isFlat()
	{
		return this.isFlat;
	}
	
	public void setFlat(boolean f)
	{
		this.isFlat = f;
	}
	
	public boolean isSharp()
	{
		return this.isSharp;
	}
	
	public void setSharp(boolean s)
	{
		this.isSharp = s;
	}
	
	public boolean isNatural()
	{
		return this.isNatural;
	}
	
	public void setNatural(boolean n)
	{
		this.isNatural = n;
	}
	
	public boolean isStaccato()
	{
		return this.isStaccato;
	}
	
	public void setStaccato(boolean s)
	{
		this.isStaccato = s;
	}
	
	public boolean isDot()
	{
		return this.isDot;
	}
	
	public void setDot(boolean d)
	{
		this.isDot = d;
	}
	
	public boolean isHarmony()
	{
		return this.isHarmony;
	}
	
	public void setHarmony(boolean h)
	{
		this.isHarmony = h;
	}
	
	public String getSound()
	{
		return this.sound;
	}
	
	public void setSound(String s)
	{
		this.sound = s;
	}
	
	public CommandSender getSender()
	{
		return this.sender;
	}
	
	public void setSender(CommandSender cs)
	{
		this.sender = cs;
	}
	
	private String errorCheck()
	{
		// sharp and flat
		if (this.isSharp && this.isFlat)
		{
			return "샵과 플랫을 동시에 사용 할 수 없습니다.";
		}

		// aviliable octave
		if (this.octave < -12 || this.octave > 12)
		{
			return "옥타브의 구간은 0~2를 벗어날 수 없습니다.";
		}
		
		if (this.pow + this.octave > 12 || this.pow + this.octave < -12)
		{
			return "현재 옥타브에서의 이 음계는 존재하지 않습니다.";
		}
		
		return "";
	}
}
