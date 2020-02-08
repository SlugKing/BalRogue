package hackumassv;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.font.effects.ColorEffect;

import hackumassv.Enemy.MonsterType;
import hackumassv.TerrainTile.TileType;

import java.util.*;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

public class MainGame extends BasicGame
{

	private TerrainContainer terrain;
	private ArrayList<Entity> life = new ArrayList<Entity>();
	private ArrayList<Entity> bullethell = new ArrayList<Entity>();
	private boolean goalVisible = false;
	private boolean splash = true;
	private boolean gameplay = false;
	private boolean gameover = false;
	private boolean leaderboards = false;
	private boolean top5 = false;
	private ArrayList<String> scores;
	private ArrayList<String> names;
	private TextField nameEntry;
	private int delay = 200;
	private int levelsPassed = 0;
	private int GAMEWORLD_OFFSET_X = 0;
	private int GAMEWORLD_OFFSET_Y = 0;
	private Rectangle goalBox;
	private UnicodeFont ttf2, ttf2h;
	private GameContainer gc;
	
	public MainGame(String name)
	{
		super(name);
	}	

	public void init(GameContainer gc) throws SlickException
	{
		this.gc = gc;
		splash = true;
		Image splash = new Image("img/splash.png");
		splash.draw(GAMEWORLD_OFFSET_X, GAMEWORLD_OFFSET_Y);
		gameplay = false;
		gameover = false;
		leaderboards = false;
		top5 = false;
		scores = new ArrayList<String>();
		names = new ArrayList<String>();
		levelsPassed = 0;
		delay = 200;
		InputStream inputStream	= ResourceLoader.getResourceAsStream("img/FTB.ttf");
//		try {
			Font ttf = new Font("Frutiger Bold", Font.BOLD, 24);
			Font ttfh = new Font("Frutiger Bold", Font.BOLD, 48);
			ttf2 = new UnicodeFont(ttf);
			ttf2h = new UnicodeFont(ttfh);
//		} catch (FontFormatException e) {} catch (IOException e) { }
		ttf2.addAsciiGlyphs();
		ttf2.getEffects().add(new ColorEffect());
		ttf2.addAsciiGlyphs();
		ttf2.loadGlyphs();
		ttf2h.addAsciiGlyphs();
		ttf2h.getEffects().add(new ColorEffect());
		ttf2h.addAsciiGlyphs();
		ttf2h.loadGlyphs();
		terrain = new TerrainContainer();
	    terrain.generateStructures(1);
	    goalBox = new Rectangle(GAMEWORLD_OFFSET_X, GAMEWORLD_OFFSET_Y, 0, 0); // placeholder box
		life.add(new Player(GAMEWORLD_OFFSET_X+65, GAMEWORLD_OFFSET_Y+65));
		for (int i = 0; i < 1; i++) // generate enemies
		{
			ArrayList<CoordCombo> openTiles = new ArrayList<CoordCombo>();
			for (int j = 1; j < 11; j++)
			{
				for (int k = 1; k < 11; k++)
				{
					if (terrain.map()[j][k].type() == TileType.DFLT) { openTiles.add(new CoordCombo(j, k)); }
				}
			}
			CoordCombo picked = null;
			while (picked == null || (picked.X() <= 2 && picked.Y() <= 2))
			{
				picked = openTiles.get((int)(Math.random()*openTiles.size()));
			}
			MonsterType mtypeRoll;
			switch((int)(Math.random()*2))
			{
			case 0:
				mtypeRoll = MonsterType.DFLT;
				break;
			case 1:
				mtypeRoll = MonsterType.WLKR;
				break;
			default:
				mtypeRoll = MonsterType.DFLT;
				break;
			}
			life.add(new Enemy(GAMEWORLD_OFFSET_X+10+(picked.X()*60), GAMEWORLD_OFFSET_Y+10+(picked.Y()*60), mtypeRoll));
			// System.out.println("created enemy at "+picked.X()+", "+picked.Y()+" on tile type "+terrain.map()[picked.X()][picked.Y()].type());
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		if (gameplay) {
		// terrain rendering
		TerrainTile[][] tileset = terrain.map();
		for (int i = 0; i < 12; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				TerrainTile tile = tileset[i][j];
				switch(tile.type()) // drawing the tile
				{
				case DFLT:
					Image tileimg = new Image("img/dflt.png");
					tileimg.draw(GAMEWORLD_OFFSET_X+(j*60), GAMEWORLD_OFFSET_Y+(i*60));
					break;
				case WALL:
					tileimg = new Image("img/wall.png");
					tileimg.draw(GAMEWORLD_OFFSET_X+(j*60), GAMEWORLD_OFFSET_Y+(i*60));
					break;
				case GOAL:
					tileimg = new Image("img/goal.png");
					tileimg.draw(GAMEWORLD_OFFSET_X+(j*60), GAMEWORLD_OFFSET_Y+(i*60));
					break;
				default:
					break;
				}
			}
		}
		
		// entity/bullet rendering
		for (Entity e : life)
		{
			e.render();
		}
		for (Entity b : bullethell)
		{
			b.render();
		}
		
		ttf2.drawString(GAMEWORLD_OFFSET_X+10, GAMEWORLD_OFFSET_Y+10, ("Level "+(levelsPassed+1)+""), Color.white);
		for (int i = 0; i < ((Player)life.get(0)).getHP(); i++) // hp bars
		{
			Image hp = new Image("img/hpbar.png");
			hp.draw(GAMEWORLD_OFFSET_X+700-(5*(i-1)+(10*i)), GAMEWORLD_OFFSET_Y+10);
		}
		
		}
		else if (!gameplay && !splash && !leaderboards && gameover) // gameover and name entry
		{
			Image dscreen = new Image("img/dscreen.png");
			dscreen.draw(GAMEWORLD_OFFSET_X, GAMEWORLD_OFFSET_Y);
			ttf2h.drawString(GAMEWORLD_OFFSET_X+220, GAMEWORLD_OFFSET_Y+150, "GAME OVER", Color.white);
			if (levelsPassed == 0)
			{
				ttf2.drawString(GAMEWORLD_OFFSET_X+215, GAMEWORLD_OFFSET_Y+250, "You didn't clear any levels", Color.white);
			}
			else if (levelsPassed == 1)
			{
				ttf2.drawString(GAMEWORLD_OFFSET_X+252, GAMEWORLD_OFFSET_Y+250, "You cleared "+levelsPassed+" level", Color.white);
			}
			else
			{
				ttf2.drawString(GAMEWORLD_OFFSET_X+245, GAMEWORLD_OFFSET_Y+250, "You cleared "+levelsPassed+" levels", Color.white);
			}
			if (top5)
			{
				if (nameEntry.getText().length() > 0)
				{
					ttf2.drawString(GAMEWORLD_OFFSET_X+275, GAMEWORLD_OFFSET_Y+600, "Click to continue");
				}
				ttf2.drawString(GAMEWORLD_OFFSET_X+200, GAMEWORLD_OFFSET_Y+400, "Top 5 score! Enter your name:", Color.white);
				nameEntry.render(gc, g);
			}
			else
			{
				if (delay <= 0)
				{
					ttf2.drawString(GAMEWORLD_OFFSET_X+275, GAMEWORLD_OFFSET_Y+600, "Click to continue");
				}
			}
		}
		else if (!gameplay && !splash && leaderboards && !gameover)
		{
			Image dscreen = new Image("img/dscreen.png");
			dscreen.draw(GAMEWORLD_OFFSET_X, GAMEWORLD_OFFSET_Y);
			ttf2h.drawString(GAMEWORLD_OFFSET_X+200, GAMEWORLD_OFFSET_Y+100, "LEADERBOARDS", Color.white);
			for (int i = 0; i < scores.size(); i++)
			{
				ttf2h.drawString(GAMEWORLD_OFFSET_X+100, GAMEWORLD_OFFSET_Y+200+(50*i), (names.get(i)), Color.white);
				ttf2h.drawString(GAMEWORLD_OFFSET_X+275, GAMEWORLD_OFFSET_Y+200+(50*i), ("......................."), Color.white);
				ttf2h.drawString(GAMEWORLD_OFFSET_X+600, GAMEWORLD_OFFSET_Y+200+(50*i), (scores.get(i)), Color.white);
			}
			ttf2.drawString(GAMEWORLD_OFFSET_X+275, GAMEWORLD_OFFSET_Y+600, "Click to continue");
		}
		else // splash
		{
			Image splash = new Image("img/splash.png");
			splash.draw(GAMEWORLD_OFFSET_X, GAMEWORLD_OFFSET_Y);
			ttf2.drawString(GAMEWORLD_OFFSET_X+285, GAMEWORLD_OFFSET_Y+400, "Click to start");
		}
	}
	
	public void update(GameContainer gc, int delta) throws SlickException
	{
		if (gameplay)
		{
		if (gc.getInput().isKeyDown(Input.KEY_A)) { ((Player)life.get(0)).setVX(-0.25); }
		else if (gc.getInput().isKeyDown(Input.KEY_D)) { ((Player)life.get(0)).setVX(0.25); }
		else { ((Player)life.get(0)).setVX(0); }
		if (gc.getInput().isKeyDown(Input.KEY_W)) { ((Player)life.get(0)).setVY(-0.25); }
		else if (gc.getInput().isKeyDown(Input.KEY_S)) { ((Player)life.get(0)).setVY(0.25); }
		else { ((Player)life.get(0)).setVY(0); }


		// create master collision box for walls
		ArrayList<Shape> walls = new ArrayList<Shape>();
		for (int i = 0; i < 12; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				if (terrain.map()[i][j].type() == TileType.WALL)
				{
					walls.add(new Rectangle(GAMEWORLD_OFFSET_X+(j*60), GAMEWORLD_OFFSET_Y+(i*60), 60, 60));
				}
			}
		}
		// collision detection for bullets
		for (int i = 0; i < bullethell.size(); i++)
		{
			for (Shape s : walls)
			{
				if (bullethell.get(i).bb().intersects(s))
				{
					bullethell.get(i).kill();
				}
			}
			
			if (bullethell.get(i) instanceof EnemyBullet && bullethell.get(i).bb().intersects(life.get(0).bb()))
			{
				((Player)life.get(0)).decHP();
				bullethell.get(i).kill();
			}
			for (int j = 1; j < life.size(); j++)
			{
				if (!(bullethell.get(i) instanceof EnemyBullet) && bullethell.get(i).bb().intersects(life.get(j).bb()))
				{
					((Enemy)life.get(j)).decHP();
					bullethell.get(i).kill();
				}
			}
			if (!bullethell.get(i).alive()) { bullethell.remove(i); }
		}
		// collision / barring movement from player
		for (int i = 0; i < life.size(); i++)
		{
			Rectangle predictiveBox = new Rectangle((int)(life.get(i).bb().getX()+(life.get(i).VX()*delta)),
					(int)(life.get(i).bb().getY()+(life.get(i).VY()*delta)), life.get(i).bb().getWidth(), life.get(i).bb().getHeight());
			for (Shape s : walls)
			{
				if (predictiveBox.intersects(s))
				{
					Rectangle fixedBoxNoX = new Rectangle(life.get(i).X(), predictiveBox.getY(), predictiveBox.getWidth(), predictiveBox.getHeight());
					if (!fixedBoxNoX.intersects(s)) { life.get(i).setMoveFlagX(false); }
					else
					{
						Rectangle fixedBoxNoY = new Rectangle(predictiveBox.getX(), life.get(i).Y(), predictiveBox.getWidth(), predictiveBox.getHeight());
						if (!fixedBoxNoY.intersects(s)) { life.get(i).setMoveFlagY(false); }
						else
						{
							life.get(i).setMoveFlagX(false);
							life.get(i).setMoveFlagY(false);
						}
					}
					Rectangle fixedBoxNoY = new Rectangle(predictiveBox.getX(), life.get(i).Y(), predictiveBox.getWidth(), predictiveBox.getHeight());
					if (!fixedBoxNoY.intersects(s)) { life.get(i).setMoveFlagY(false); }
					else
					{
						if (!fixedBoxNoX.intersects(s)) { life.get(i).setMoveFlagX(false); }
						else
						{
							life.get(i).setMoveFlagX(false);
							life.get(i).setMoveFlagY(false);
						}
					}
				}
			}
		}
		for (Entity b : bullethell)
		{
			b.setX(b.X()+(int)(b.VX()*delta));
			b.setY(b.Y()+(int)(b.VY()*delta));
			b.dynamicUpdates(delta);
		}
		for (int i = 0; i < life.size(); i++)
		{
			if (life.get(i) instanceof Enemy)
			{
				((Enemy)life.get(i)).dynamicUpdates(delta, life, bullethell);
				if (((Enemy)life.get(i)).getHP() <= 0)
				{
					life.get(i).kill();
				}
			}
			if (life.get(i).getMoveFlagX()) { life.get(i).setX(life.get(i).X()+(int)(life.get(i).VX()*delta)); }
			if (life.get(i).getMoveFlagY()) { life.get(i).setY(life.get(i).Y()+(int)(life.get(i).VY()*delta)); }
			life.get(i).dynamicUpdates(delta);
			life.get(i).setMoveFlagX(true);
			life.get(i).setMoveFlagY(true);
			if (!life.get(i).alive()) { life.remove(i); }
		}

		if (life.size() <= 1 && !goalVisible)
		{
			// choose random coordinate in bottom right section, generate open tiles+goalspot
			int goalX = (int)(Math.random()*4)+7;
			int goalY = (int)(Math.random()*4)+7;
			terrain.map()[goalY][goalX].setType(TileType.GOAL);
			if (goalX < 10) { terrain.map()[goalY][goalX+1].setType(TileType.DFLT); }
			if (goalY < 10) { terrain.map()[goalY+1][goalX].setType(TileType.DFLT); }
			terrain.map()[goalY-1][goalX].setType(TileType.DFLT);
			terrain.map()[goalY][goalX-1].setType(TileType.DFLT);
			goalVisible = true;
			goalBox = new Rectangle((goalX*60)+20, (goalY*60)+20, 20, 20);
		}
		
		// GOAL ATTAINED, RESET LEVEL
		if (life.get(0).bb().intersects(goalBox))
		{
			levelsPassed++;
			terrain = new TerrainContainer();
			for (int i = life.size()-1; i > 0; i--) { life.remove(i); }
			bullethell.clear();
			goalVisible = false;
			goalBox = new Rectangle(GAMEWORLD_OFFSET_X, GAMEWORLD_OFFSET_Y, 0, 0);
		    terrain.generateStructures(1+(levelsPassed/3));
			life.get(0).setX(GAMEWORLD_OFFSET_X+65);
			life.get(0).setY(GAMEWORLD_OFFSET_Y+65);
			for (int i = 0; i < 1+(levelsPassed/3); i++) // generate enemies
			{
				ArrayList<CoordCombo> openTiles = new ArrayList<CoordCombo>();
				for (int j = 1; j < 11; j++)
				{
					for (int k = 1; k < 11; k++)
					{
						if (terrain.map()[j][k].type() == TileType.DFLT) { openTiles.add(new CoordCombo(j, k)); }
					}
				}
				CoordCombo picked = null;
				while (picked == null || (picked.X() <= 2 && picked.Y() <= 2))
				{
					picked = openTiles.get((int)(Math.random()*openTiles.size()));
				};
				MonsterType mtypeRoll;
				switch((int)(Math.random()*2))
				{
				case 0:
					mtypeRoll = MonsterType.DFLT;
					break;
				case 1:
					mtypeRoll = MonsterType.WLKR;
					break;
				default:
					mtypeRoll = MonsterType.DFLT;
					break;
				}
				life.add(new Enemy(GAMEWORLD_OFFSET_X+10+(picked.X()*60), GAMEWORLD_OFFSET_Y+10+(picked.Y()*60), mtypeRoll));
				// System.out.println("created enemy at "+picked.X()+", "+picked.Y()+" on tile type "+terrain.map()[picked.X()][picked.Y()].type());
			}
		}
		if (((Player)life.get(0)).getHP() <= 0) { endgame(); }
		}
		if (gameover)
		{
			if (!top5 && delay > 0) { delay--; }
		}
		
	}
	
	public void endgame() throws SlickException
	{
		gameplay = false;
		gameover = true;
		try (FileInputStream inputStream = new FileInputStream("leaderboards.txt"))
		{
			String raw = IOUtils.toString(inputStream, "UTF-8");
			if (!(raw.split(" ")[0]).equals("No"))
			{
				String[] leaders = raw.split(" ");
				for (String l : leaders)
				{
					names.add(l.split(":")[0]);
					scores.add(l.split(":")[1]);
				}
				for (int i = 0; i < scores.size(); i++)
				{
					if (levelsPassed > Integer.parseInt(scores.get(i)))
					{
						top5 = true;
					}
				}
				if (scores.size() < 5)
				{
					top5 = true;
				}
			}
			else { top5 = true; }
		} catch(IOException e) {}
		if (top5)
		{
			nameEntry = new TextField(gc, ttf2h, GAMEWORLD_OFFSET_X+250, GAMEWORLD_OFFSET_Y+450, 200, 75);
			nameEntry.setMaxLength(5);
			nameEntry.setFocus(true);
		}
		life.clear();
		bullethell.clear();
		terrain = null;
	}
	
	public void mousePressed(int button, int x, int y)
	{
		if (splash)
		{
			splash = false;
			gameplay = true;
			gameover = false;
			leaderboards = false;
		}
		else if (gameplay) {
			try
			{
				// single shot
				bullethell.add(new Bullet(life.get(0).X()+10, life.get(0).Y()+10, 0.5, (int)(Math.toDegrees(Math.atan2(-(y-life.get(0).Y()), x-life.get(0).X()))), 60));
				
				// EP
				/*
				  bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.75, 0, 10));
				  bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.75, 45, 10));
				  bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.75, 90, 10));
				  bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.75, 135, 10));
				  bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.75, 180, 10));
				  bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.75, 225, 10));
				  bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.75, 270, 10));
				  bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.75, 315, 10));
				  */
				 	
				// fork shot (dblade)
				//bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.5, (int)(Math.toDegrees(Math.atan2(-(y-life.get(0).Y()), x-life.get(0).X()))+15), 20));
				//bullethell.add(new Bullet(life.get(0).X(), life.get(0).Y(), 0.5, (int)(Math.toDegrees(Math.atan2(-(y-life.get(0).Y()), x-life.get(0).X()))-15), 20));
	
			}
			catch (SlickException se) {}
		}
		else if (!gameplay && !splash && !leaderboards && gameover) // death screen
		{
			
			if (delay <= 0)
			{
				if (!top5)
				{
					gameover = false;
					leaderboards = true;
				}
			}
			if (top5 && nameEntry.getText().length() > 0)
			{
				int gIndex = 6;
				for (int i = 0; i < scores.size(); i++)
				{
					if (gIndex > i && Integer.parseInt(scores.get(i)) < levelsPassed) { gIndex = i; }
				}
				if (scores.isEmpty()) { gIndex = 0; }
				if (gIndex != 6)
				{
					names.add(gIndex, nameEntry.getText());
					scores.add(gIndex, ""+levelsPassed);
				}
				else
				{
					names.add(nameEntry.getText());
					scores.add(""+levelsPassed);
				}
				if (scores.size() > 5)
				{
					names.remove(names.size()-1);
					scores.remove(names.size()-1);
				}
				String ldbstring = "";
				for (int i = 0; i < names.size(); i++)
				{
					ldbstring += (names.get(i)+":"+scores.get(i)+" ");
				}
				try
				{
					FileUtils.writeStringToFile(new File("leaderboards.txt"), ldbstring, "UTF-8");
				} catch(IOException e) {}
				gameover = false;
				leaderboards = true;
			}
		}
		else if (leaderboards)
		{
			try
			{
				init(gc);
			} catch (SlickException se) {}
		}
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc = new AppGameContainer(new MainGame("BalRogue"));
			appgc.setShowFPS(false);
			appgc.setVSync(true);
			appgc.setDisplayMode(720, 720, false);
			appgc.start();
		}
		catch (SlickException se) {}
		
	}
}
