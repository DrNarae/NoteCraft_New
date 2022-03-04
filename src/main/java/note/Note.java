package note;

public class Note
{
	private int sleep;
	private float pitch;
	private float volume;
	private String sound;

	public Note()
	{
		this.sleep = 1000;
		this.pitch = 1.0f;
		this.volume = 1.0f;
		this.sound = "minecraft:block.note_block.guitar";
	}
	
	public int getSleep()
	{
		return this.sleep;
	}
	
	public void setSleep(int s)
	{
		this.sleep = s;
	}
	
	public float getPitch()
	{
		return this.pitch;
	}
	
	public void setPitch(float p)
	{
		this.pitch = p;
	}
	
	public float getVolume()
	{
		return this.volume;
	}
	
	public void setVolume(float v)
	{
		this.volume = v;
	}
	
	public String getSound()
	{
		return this.sound;
	}
	
	public void setSound(String s)
	{
		this.sound = s;
	}
}
