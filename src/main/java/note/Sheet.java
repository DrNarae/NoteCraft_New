package note;

import java.util.ArrayList;

public class Sheet
{
	int index;
	String name;
	ArrayList<Note> context;
	
	public Sheet(ArrayList<Note> note, String name)
	{
		this.index = 0;
		this.name = name;
		this.context = note;
	}
	
	public Note next()
	{
		Note current;
		if (this.index >= this.context.size())
		{
			current = null;
		}
		else
		{
			current = this.context.get(this.index);
			this.index++;
		}
		
		return current;
	}
	
	public Note getCurrent()
	{
		if (this.index >= 0 && this.index < this.context.size())
		{
			return this.context.get(this.index);
		}
		else
		{
			return null;
		}
	}
	
	public String getName()
	{
		return this.name;
	}
}
