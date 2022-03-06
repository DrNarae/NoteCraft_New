package note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Decoding
{
	int totalTime;
	File rawFile;
	String rawNote;
	CommandSender sender;
	
	final ArrayList<Integer> POW = new ArrayList<Integer>(Arrays.asList(-6, -4, -2, -1, 1, 3, 5));
	final ArrayList<String> CODE = new ArrayList<String>(Arrays.asList("c", "d", "e", "f", "g", "a", "b"));
	final ArrayList<String> SYMBOL = new ArrayList<String>(Arrays.asList("r", "v", "t", "i", "u", "#", "$", "%", "<", ">", "&", "+", ".", "!", "s", "p"));
	final ArrayList<Integer> FLAT = new ArrayList<Integer>(Arrays.asList(-11, -9, -7, -4, -2, 1, 3, 5, 8, 10));
	final ArrayList<Integer> SHARP = new ArrayList<Integer>(Arrays.asList(-11, -9, -6, -4, -1, 1, 3, 6, 8, 11));
	final ArrayList<String> NUM = new ArrayList<String>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
	
	public Decoding(String f, CommandSender p)
	{
		this.totalTime = 0;
		this.sender = p;
		this.rawNote = "";
		this.rawFile = new File("plugins/NoteCraft/Notes/" + f.replace(".txt", "") + ".txt");
		
		String errmsg = readNote();
		if (!errmsg.equals("") && main.Main.WARNINGALERT)
		{
			this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + errmsg);
		}
		
		this.rawNote = this.rawNote.replaceAll(" ", "").replaceAll("\n", "").toLowerCase();
	}
	
	public Sheet DecodingNote()
	{
		// # => Sharp
		// $ => Flat
		// % => Natural
		// c, d, e, f, g, a, b => syllable
		// 1, 2, 4, 8, 16, 32, 64, 128, 256, 512 => beat
		// r => rest
		// v => volume
		// <, > => - + octave
		// & => tie
		// t => bpm
		// + => hamorny
		// . => half-beat
		// i => init beat
		// ! => staccato
		// u{} => default sound
		// s{}
		// p{}
		
		int octave = 0;
		int beat = 4;
		int bpm = 100;
		float volume = 1.0f;
		boolean isApply = false;
		String sound = "minecraft:block.note_block.guitar";
		ArrayList<String> define_flat = new ArrayList<String>();
		ArrayList<String> define_sharp = new ArrayList<String>();
		
		ArrayList<Note> sheet = new ArrayList<Note>();
//		ArrayList<String> Errors = new ArrayList<String>();
		ArrayList<String> rawNote = splitter();
		for (String s : rawNote)
		{
			RawNote rn = new RawNote();
			rn.setBeat(beat);
			rn.setVolume(volume);
			rn.setSender(this.sender);
			
			for (int i = 0; i < s.length(); i++)
			{
				String c = s.substring(i, i+1);
				
				// skip number.
				if (NUM.indexOf(c) < 0)
				{
					int check = CODE.indexOf(c);
					if (check >= 0)
					{
						// syllable (select in number)
						int result = parseInt(s, i);
						if (result >= 0) rn.setBeat(result);
						rn.setPow(POW.get(check));
						isApply = true;
					}
					else
					{
						check = SYMBOL.indexOf(c);
						if (check == 0)
						{
							// rest (select in number)
							int result = parseInt(s, i);
							if (result >= 0) rn.setBeat(result);
							rn.setVolume(0.0f);
							isApply = true;
						}
						else if (check == 1)
						{
							// default volume (require in number)
							int result = parseInt(s, i);
							if (result >= 0)
							{
								volume = result/10.0f;
							}
							else
							{
								// no search number error
								if (main.Main.WARNINGALERT)
								{
									this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + "음량 설정에 숫자가 입력되지 않았습니다.");
								}
								if (!main.Main.IGNOREWRONGNOTE)
								{
									return null;
								}
							}
						}
						else if (check == 2)
						{
							// default bpm (require in number)
							int result = parseInt(s, i);
							if (result >= 0)
							{
								bpm = result;
							}
							else
							{
								// no search number error
								if (main.Main.WARNINGALERT)
								{
									this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + "템포 설정에 숫자가 입력되지 않았습니다.");
								}
								if (!main.Main.IGNOREWRONGNOTE)
								{
									return null;
								}
							}
						}
						else if (check == 3)
						{
							// default beat (require in number)
							int result = parseInt(s, i);
							if (result >= 0)
							{
								beat = result;
							}
							else
							{
								// no search number error
								if (main.Main.WARNINGALERT)
								{
									this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + "기본박자 설정에 숫자가 기입되지 않았습니다.");
								}
								if (!main.Main.IGNOREWRONGNOTE)
								{
									return null;
								}
							}
						}
						else if (check == 4)
						{
							// default sound {}
							String result = parseBracket(s);
							if (result.equals(""))
							{
								// wrong bracket and empty error
								if (main.Main.WARNINGALERT)
								{
									this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + "기본악기 설정에 중괄호의 내용이 비었거나, 대괄호 짝이 맞지 않습니다.");
								}
								if (!main.Main.IGNOREWRONGNOTE)
								{
									return null;
								}
							}
							else
							{
								sound = result;
							}
							break;
						}
						else if (check == 5)
						{
							// sharp
							rn.setSharp(true);
						}
						else if (check == 6)
						{
							// flat
							rn.setFlat(true);
						}
						else if (check == 7)
						{
							// natural
							rn.setNatural(true);
						}
						else if (check == 8)
						{
							// negative-octave
							octave -= 12;
						}
						else if (check == 9)
						{
							// positive-octave
							octave += 12;
						}
						else if (check == 10)
						{
							// tie
							rn.setTie(true);
						}
						else if (check == 11)
						{
							// harmony
							rn.setHarmony(true);
						}
						else if (check == 12)
						{
							// dot
							rn.setDot(true);
						}
						else if (check == 13)
						{
							// staccato
							rn.setStaccato(true);
						}
						else if (check == 14)
						{
							// default sharp {}
							String result = parseBracket(s);
							if (result.equals(""))
							{
								// sharp is reset
								define_sharp.clear();
							}
							else
							{
								for (int j = 0; j < result.length(); j++)
								{
									String isS = result.substring(j, j+1);
									if (isS.equals("c") || isS.equals("d") || isS.equals("f") || isS.equals("g") || isS.equals("a"))
									{
										define_sharp.add(isS);
									}
									else
									{
										// wrong bracket error
										if (main.Main.WARNINGALERT)
										{
											this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + "해당 음계는 샵처리 할 수 없습니다. : " + isS);
										}
										if (!main.Main.IGNOREWRONGNOTE)
										{
											return null;
										}
									}
								}
							}
							break;
						}
						else if (check == 15)
						{
							// default flat {}
							String result = parseBracket(s);
							if (result.equals(""))
							{
								// flat is reset
								define_flat.clear();
							}
							else
							{
								for (int j = 0; j < result.length(); j++)
								{
									String isP = result.substring(j, j+1);
									if (isP.equals("d") || isP.equals("e") || isP.equals("g") || isP.equals("a") || isP.equals("b"))
									{
										define_flat.add(isP);
									}
									else
									{
										// wrong bracket and empty error
										if (main.Main.WARNINGALERT)
										{
											this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + "해당 음계는 플랫처리 할 수 없습니다. : " + isP);
										}
										if (!main.Main.IGNOREWRONGNOTE)
										{
											return null;
										}
									}
								}
							}
							break;
						}
						else
						{
							// error
							if (main.Main.WARNINGALERT)
							{
								this.sender.sendMessage(main.Main.SYSTEM + ChatColor.RED + "해당 문자를 해독 할 수 없습니다. : " + c);
							}
							if (!main.Main.IGNOREWRONGNOTE)
							{
								return null;
							}
						}
					}
				}
			}
			
			rn.setBPM(bpm);
			rn.setSound(sound);
			rn.setOctave(octave);
			
			if (isApply)
			{
				if (define_sharp.size() > 0)
				{
					int sharp = define_sharp.indexOf(s.substring(0, 1));
					if (sharp >= 0)
					{
						// sharp
						rn.setSharp(true);
					}
				}
				else if (define_flat.size() > 0)
				{
					int flat = define_flat.indexOf(s.substring(0, 1));
					if (flat >= 0)
					{
						// flat
						rn.setFlat(true);
					}
				}
				
				Note oneNote = rn.getNote();
				if (oneNote != null)
				{
					this.totalTime += oneNote.getSleep();
					sheet.add(oneNote);
				}
				else
				{
					if (!main.Main.IGNOREWRONGNOTE)
					{
						return null;
					}
				}
				isApply = false;
			}
		}
		
		return (new Sheet(sheet, this.rawFile.getName(), this.totalTime));
	}
	
	private String parseBracket(String br)
	{
		int start = br.indexOf("{");
		int end = br.indexOf("}");
		if (start >= 0 && end >= 0 && start < end)
		{
			String result = br.substring(start+1, end);
			return result;
		}
		else
		{
			return "";
		}
	}
	
	private int parseInt(String s, int index)
	{
		String numRange;
		try
		{
			numRange = s.substring(index+1, s.length());
		}
		catch (IndexOutOfBoundsException e)
		{
			return -1;
		}
		
		boolean find = false;
		String strInt = "";
		
		for (int i = 0; i < numRange.length(); i++)
		{
			String parse = numRange.substring(i, i+1);
			if (NUM.indexOf(parse) >= 0)
			{
				strInt += parse;
				find = true;
			}
			else if (find)
			{
				break;
			}
		}

		return strInt.equals("") ? -1 : Integer.parseInt(strInt);
	}
	
	private ArrayList<String> splitter()
	{
		int leftBr = 0;
		int rightBr = 0;
		int start = -1;
		boolean pass = false;
		boolean find = false;
		ArrayList<String> split = new ArrayList<String>(Arrays.asList("c", "d", "e", "f", "g", "a", "b", "r", "v", "t", "i", "u", "<", ">", "s", "p"));
		ArrayList<String> node = new ArrayList<String>();
		
		for (int i = 0; i < this.rawNote.length(); i++)
		{
			String s = this.rawNote.substring(i, i+1);
			
			if (s.equals("{")) leftBr++;
			else if (s.equals("}")) rightBr++;
			
			if (pass)
			{
				if (s.equals("}")) pass = false;
			}
			else
			{
				int brI = split.indexOf(s);
				if (brI >= 0)
				{
					if (brI == 11 || brI == 14 || brI == 15) pass = true;
					
					if (find)
					{
						node.add(this.rawNote.substring(start, i));
						start = i;
					}
					else
					{
						start = i;
						find = true;
					}
				}
			}
		}
		
		node.add(this.rawNote.substring(start, this.rawNote.length()));
		
		if (leftBr == rightBr)
		{
			return node;
		}
		else
		{
			// bracket count error
			return null;
		}
		
	}
	
	private String readNote()
	{
		try
		{
			FileReader filereader = new FileReader(this.rawFile);
			BufferedReader bufReader = new BufferedReader(filereader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
			{
				this.rawNote += line;
			}
			bufReader.close();
		}
		catch (FileNotFoundException e)
		{
			return ChatColor.RED + "'" + this.rawFile.getName() + "'는 존재하지 않는 악보입니다.";
		}
		catch (SecurityException e)
		{
			return ChatColor.RED + "'" + this.rawFile.getName() + "'는 보안오류입니다. (SecurityException)";
		}
		catch (IOException e)
		{
			return ChatColor.RED + "'" + this.rawFile.getName() + "'는 파일이 닫혔습니다. (IOException)";
		}
		
		return "";
	}
}
