package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import note.Decoding;
import note.NoteThread;
import note.Sheet;

public class Main extends JavaPlugin
{
	public static int MAX_SYNC = 7; // 1 ~ N
	public static int MAXIMUM = 10000; // 1 ~ N
	public static boolean OPONLYCMD = true;
	public static boolean WARNINGALERT = true;
	public static boolean IGNOREWRONGNOTE = false;
	public static HashMap<Integer, Boolean> LOCK = new HashMap<Integer, Boolean>();
	public static HashMap<String, Boolean> WHITELIST = new HashMap<String, Boolean>();
	public static HashMap<Integer, ArrayList<NoteThread>> PLAYINGLIST = new HashMap<Integer, ArrayList<NoteThread>>();
	public static String SYSTEM = ChatColor.YELLOW + "[" + ChatColor.DARK_GREEN + "NoteCraft" + ChatColor.YELLOW + "]" + ChatColor.WHITE + " : ";
	
	@Override
	public void onEnable()
	{
		configFileCreate();
		getSetting();
		getLogger().info("노트크래프트 로딩 완료.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		// /notecraft <name> [cmd]
		/*
		 * (player) play <fileName1:String> [fileName2] . . . => return thread id
		 * (common) playLocation <x:double> <y:double> <z:double> <world:World> <fileName1:String> [fileName2] . . . => return (thread id:int)
		 * (common) playPlayer <player:Player> <fileName1:String> [fileName2] . . . => return (thread id:int)
		 * (common) stop <thread id:int>
		 * (common) clear => all stop playing music
		 * (common) list => show playing music list (thread id)
		*/
		
		if (cmd.getName().equalsIgnoreCase("notecraft") && args.length > 0)
		{
			if (OPONLYCMD)
			{
				if (!sender.isOp() && !WHITELIST.containsKey(sender.getName()))
				{
					sender.sendMessage(SYSTEM + ChatColor.RED + "서버관리자 또는 인가된 유저만 사용 할 수 있습니다.");
					return false;
				}
			}
			
			if (args[0].equalsIgnoreCase("play"))
			{
				if (sender instanceof Player)
				{
					if (args.length >= 2 && args.length <= (MAX_SYNC-1)+2)
					{
						String[] filenames = Arrays.copyOfRange(args, 1, args.length);
						String error = existFile(filenames);
						if (error.equals(""))
						{
							// decoding note
							ArrayList<Sheet> sheets = new ArrayList<Sheet>();
							ArrayList<NoteThread> sheetlist = new ArrayList<NoteThread>();
							
							for (String fn : filenames)
							{
								Decoding rawNote = new Decoding(fn, sender);
								Sheet sheet = rawNote.DecodingNote();
								if (sheet != null)
								{
									sheets.add(sheet);
								}
								else
								{
									// error
									if (!main.Main.IGNOREWRONGNOTE)
									{
										return false;
									}
								}
							}

							// generate thread id
							int id = findID();
							if (id >= 0)
							{
								PLAYINGLIST.put(id, sheetlist);
								LOCK.put(id, true);
								
								for (Sheet s : sheets)
								{
									NoteThread nt = new NoteThread((Player)sender, s, id);
									sheetlist.add(nt);
									nt.start();
								}

								sender.sendMessage(SYSTEM + ChatColor.GREEN + "노래가 재생됩니다. ID : [" + ChatColor.LIGHT_PURPLE + id + ChatColor.GREEN + "]");
								LOCK.put(id, false);
							}
							else
							{
								sender.sendMessage(SYSTEM + ChatColor.RED + "현재 재생 가능한 채널이 없습니다. 잠시후에 다시 시도해주세요.");
							}
						}
						else
						{
							sender.sendMessage(SYSTEM + ChatColor.RED + "'" + error + "'는 존재하지 않는 악보입니다.");
						}
					}
					else
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "악보는 최소 1개 부터 최대 " + MAX_SYNC + "개 까지 입력 할 수 있습니다.");
					}
				}
				else
				{
					sender.sendMessage(SYSTEM + ChatColor.RED + "이 명령어는 플레이어만 사용할 수 있습니다.");
				}
			}
			else if (args[0].equalsIgnoreCase("playLocation"))
			{
				if (args.length >= 6)
				{
					double x;
					double y;
					double z;
					World world = null;
					String[] filenames = Arrays.copyOfRange(args, 5, args.length);
					String error = existFile(filenames);
					
					try
					{
						x = Double.parseDouble(args[1]);
						y = Double.parseDouble(args[2]);
						z = Double.parseDouble(args[3]);
						world = Bukkit.getWorld(args[4]);
						if (world == null) throw new Exception();
					}
					catch (NullPointerException e)
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "잘못된 좌표입니다.");
						return false;
					}
					catch (NumberFormatException e)
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "잘못된 좌표입니다.");
						return false;
					}
					catch (Exception e)
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "존재하지 않는 월드입니다.");
						return false;
					}
					
					if (error.equals(""))
					{
						// decoding note
						ArrayList<Sheet> sheets = new ArrayList<Sheet>();
						ArrayList<NoteThread> sheetlist = new ArrayList<NoteThread>();
						
						for (String fn : filenames)
						{
							Decoding rawNote = new Decoding(fn, sender);
							Sheet sheet = rawNote.DecodingNote();
							if (sheet != null)
							{
								sheets.add(sheet);
							}
							else
							{
								// error
								if (!main.Main.IGNOREWRONGNOTE)
								{
									return false;
								}
							}
						}

						// generate thread id
						int id = findID();
						if (id >= 0)
						{
							PLAYINGLIST.put(id, sheetlist);
							LOCK.put(id, true);
							
							for (Sheet s : sheets)
							{
								NoteThread nt = new NoteThread(x, y, z, world, s, id);
								sheetlist.add(nt);
								nt.start();
							}

							sender.sendMessage(SYSTEM + ChatColor.GREEN + "해당 위치에 노래가 재생됩니다. ID : [" + ChatColor.LIGHT_PURPLE + id + ChatColor.GREEN + "]");
							LOCK.put(id, false);
						}
						else
						{
							sender.sendMessage(SYSTEM + ChatColor.RED + "현재 재생 가능한 채널이 없습니다. 잠시후에 다시 시도해주세요.");
						}
					}
					else
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "'" + error + "'는 존재하지 않는 악보입니다.");
					}
				}
				else
				{
					sender.sendMessage(SYSTEM + ChatColor.RED + "/notecraft playLocation <x> <y> <z> <world> <filename1> <filename2> . . .");
				}

			}
			else if (args[0].equalsIgnoreCase("playPlayer"))
			{
				if (args.length >= 3)
				{
					Player p = Bukkit.getPlayer(args[1]);
					String[] filenames = Arrays.copyOfRange(args, 2, args.length);
					String error = existFile(filenames);
					
					if (error.equals(""))
					{
						// decoding note
						ArrayList<Sheet> sheets = new ArrayList<Sheet>();
						ArrayList<NoteThread> sheetlist = new ArrayList<NoteThread>();
						
						for (String fn : filenames)
						{
							Decoding rawNote = new Decoding(fn, sender);
							Sheet sheet = rawNote.DecodingNote();
							if (sheet != null)
							{
								sheets.add(sheet);
							}
							else
							{
								// error
								if (!main.Main.IGNOREWRONGNOTE)
								{
									return false;
								}
							}
						}

						// generate thread id
						int id = findID();
						if (id >= 0)
						{
							PLAYINGLIST.put(id, sheetlist);
							LOCK.put(id, true);
							
							for (Sheet s : sheets)
							{
								NoteThread nt = new NoteThread(p, s, id);
								sheetlist.add(nt);
								nt.start();
							}

							sender.sendMessage(SYSTEM + ChatColor.GREEN + p.getName() + "에게 노래가 재생됩니다. ID : [" + ChatColor.LIGHT_PURPLE + id + ChatColor.GREEN + "]");
							LOCK.put(id, false);
						}
						else
						{
							sender.sendMessage(SYSTEM + ChatColor.RED + "현재 재생 가능한 채널이 없습니다. 잠시후에 다시 시도해주세요.");
						}
					}
					else
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "'" + error + "'는 존재하지 않는 악보입니다.");
					}
				}
				else
				{
					sender.sendMessage(SYSTEM + ChatColor.RED + "/notecraft playPlayer <PlayerName> <filename1> <filename2> . . .");
				}
			}
			else if (args[0].equalsIgnoreCase("stop"))
			{
				if (args.length == 2)
				{
					try
					{
						int id = Integer.parseInt(args[1]);
						if (LOCK.get(id))
						{
							sender.sendMessage(SYSTEM + ChatColor.RED + "지금은 할 수 없습니다. 잠시후에 다시 시도해주세요.");
						}
						else
						{
							if (stopMusic(id))
							{
								sender.sendMessage(SYSTEM + ChatColor.GREEN + "해당 노래를 강제중지 했습니다.");
								return true;
							}
							else
							{
								sender.sendMessage(SYSTEM + ChatColor.RED + "노래를 찾을 수 없습니다.");
							}
						}
					}
					catch (NumberFormatException e)
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "ID는 숫자입니다.");
					}
					catch (NullPointerException e)
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "노래를 찾을 수 없습니다.");
					}
					catch (Exception e)
					{
						sender.sendMessage(SYSTEM + ChatColor.RED + "지금은 할 수 없습니다. 잠시후에 다시 시도해주세요.");
					}
				}
				else
				{
					sender.sendMessage(SYSTEM + ChatColor.RED + "/notecraft stop <ID>");
				}
			}
			else if (args[0].equalsIgnoreCase("clear"))
			{
				clearList();
				if (PLAYINGLIST.size() > 0)
				{
					sender.sendMessage(SYSTEM + ChatColor.DARK_AQUA + "음악을 모두 종료시키려 했지만, 여전히 " + PLAYINGLIST.size() + "개의 음악이 재생되고 있습니다.");
					return true;
				}
				else
				{
					sender.sendMessage(SYSTEM + ChatColor.AQUA + "음악이 모두 종료되었습니다.");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("list"))
			{
				sender.sendMessage(SYSTEM + ChatColor.AQUA + "재생 가능한 채널 수 : " + (MAXIMUM - PLAYINGLIST.size()) + "개");
				
				
				// NO DELETE, below code add paging feature
				
//				Set<Integer> set = playingList.keySet();
//				Iterator<Integer> iterator = set.iterator();
//				sender.sendMessage(ChatColor.LIGHT_PURPLE + "[ ID ] | 노래목록");
//				while (iterator.hasNext())
//				{
//					int id = (int) iterator.next();
//					sender.sendMessage(ChatColor.AQUA + "[ " + id + " ] : " + playingList.get(id).get(0).getSheet().getName().replace(".txt", "") + "외 " + (playingList.get(id).size() - 1) + "개 재생중...");
//				}
				
				return true;
			}
			else
			{
				alertCommand(sender);
			}
		}
		else
		{
			alertCommand(sender);
		}
		return false;
	}
	
	public void alertCommand(CommandSender cs)
	{
		// /notecraft <name> [cmd]
		/*
		 * (player) play <fileName1:String> [fileName2] . . . => return thread id
		 * (common) playLocation <x:double> <y:double> <z:double> <world:World> <fileName1:String> [fileName2] . . . => return (thread id:int)
		 * (common) playPlayer <player:Player> <fileName1:String> [fileName2] . . . => return (thread id:int)
		 * (common) stop <thread id:int>
		 * (common) clear => all stop playing music
		 * (common) list => show playing music list (thread id)
		*/
		
		String alert = SYSTEM + ChatColor.DARK_PURPLE + "! - 명령어 도움말 - !\n" + ChatColor.LIGHT_PURPLE + "   | " + ChatColor.DARK_RED + "<> = 필수 " + ChatColor.LIGHT_PURPLE + "| " + ChatColor.DARK_AQUA + " [] = 선택 " + ChatColor.LIGHT_PURPLE + "|\n";
		alert += ChatColor.GOLD + " /notecraft play " + ChatColor.DARK_RED + "<fileName1> " + ChatColor.DARK_AQUA + "[fileName2] " + ChatColor.GOLD + ". . .\n";
		alert += ChatColor.GOLD + " /notecraft playLocation " + ChatColor.DARK_RED + "<x> " + ChatColor.DARK_RED + "<y> " + ChatColor.DARK_RED + "<z> " + ChatColor.DARK_RED + "<world> " + ChatColor.DARK_RED + "<fileName1> " + ChatColor.DARK_AQUA + "[fileName2]" + ChatColor.GOLD + ". . .\n";
		alert += ChatColor.GOLD + " /notecraft playPlayer " + ChatColor.DARK_RED + "<playerName> " + ChatColor.DARK_RED + "<fileName1> " + ChatColor.DARK_AQUA + "[fileName2] " + ChatColor.GOLD + ". . .\n";
		alert += ChatColor.GOLD + " /notecraft stop " + ChatColor.DARK_RED + "<ID>\n";
		alert += ChatColor.GOLD + " /notecraft clear\n";
		alert += ChatColor.GOLD + " /notecraft list";
		
		cs.sendMessage(alert);
	}
	
	public void clearList()
	{
		Set<Integer> set = PLAYINGLIST.keySet();
		Iterator<Integer> iterator = set.iterator();
		
		while (iterator.hasNext())
		{
			int id = (int) iterator.next();
			try
			{
				if (!LOCK.get(id))
				{
					stopMusic(id);
				}
			}
			catch (Exception e) {}
		}
	}
	
	public boolean stopMusic(int id)
	{
		try
		{
			if (PLAYINGLIST.containsKey(id))
			{
				ArrayList<NoteThread> nts = PLAYINGLIST.get(id);
				for (NoteThread nt : nts)
				{
					nt.interrupt();
				}
				
				PLAYINGLIST.remove(id);
				LOCK.remove(id);
				
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public int findID()
	{
		for (int i = 0; i < MAXIMUM; i++)
		{
			if (!PLAYINGLIST.containsKey(i))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public String existFile(String[] fs)
	{
		for (String f : fs)
		{
			File file = new File("plugins/NoteCraft/Notes/" + f.replace(".txt", "") + ".txt");
			if (!file.exists())
			{
				return f;
			}
		}
		return "";
	}
	
	public void configFileCreate()
	{
		getLogger().info("노트크래프트 파일 검사중...");
		
		FileConfiguration config = this.getConfig();
		
		config.addDefault("WhiteList", "{}");
		config.addDefault("Limit Simultaneous Play", 7);
		config.addDefault("Limit Total Playing", 10000);
		config.addDefault("Warning Alert", true);
		config.addDefault("OP Only Command", true);
		config.addDefault("Ignore Wrong Note", false);
		config.options().copyDefaults(true);
		saveConfig();
		
		File file = new File("plugins/NoteCraft/Notes/");
		if (!file.exists()) file.mkdirs();
	}
	
	public void getSetting()
	{
		getLogger().info("노트크래프트 설정 읽는중...");
		
		String white = "";
		FileConfiguration config = this.getConfig();
		
		white = config.getString("WhiteList");
		MAXIMUM = config.getInt("Limit Total Playing");
		MAXIMUM = config.getInt("Limit Total Playing");
		MAX_SYNC = config.getInt("Limit Simultaneous Play");
		OPONLYCMD = config.getBoolean("OP Only Command");
		WARNINGALERT = config.getBoolean("Warning Alert");
		IGNOREWRONGNOTE = config.getBoolean("Ignore Wrong Note");

		if (MAXIMUM <= 0) MAXIMUM = 1;
		if (MAX_SYNC <= 0) MAX_SYNC = 1;
		if (!white.equals(""))
		{
			white = white.replaceAll(" ", "").replace("{", "").replace("}", "");
			String[] users = white.split(",");
			
			for (String user : users)
			{
				if (!user.equals(""))
				{
					WHITELIST.put(user, true);
				}
			}
		}
	}
}
