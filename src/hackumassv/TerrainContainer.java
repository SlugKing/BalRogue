package hackumassv;
import java.util.*;

import org.newdawn.slick.geom.Rectangle;

import hackumassv.TerrainTile.TileType;

public class TerrainContainer
{

	private TerrainTile[][] terrain = new TerrainTile[12][12];
	private static int STRUCT_COUNT = 7;
	
	public TerrainContainer()
	{
		for (int i = 0; i < 12; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				terrain[i][j] = new TerrainTile();
			}
		}
	}
	
	public void generateStructures(int complexity)
	{
		// initial walls
		for (int i = 0; i < 12; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				if (i == 0 || i == 11 || j == 0 || j == 11)
				{
					terrain[i][j].setType(TileType.WALL);
				}
			}
		}
		// pick a structure to generate
		// check what tiles are open for structure to fit
		// randomly pick from list of coordinate combos to put structure
		
		// structures list
		// 0: single wall tile
		// 1: 2x2 square wall tiles
		// 2: L-shape (missing top right corner)
		// 3: L-shape (missing top left corner)
		// 4: L-shape (missing bottom right corner)
		// 5: L-shape (missing bottom left corner)
		// 6: plus
		Stack<Integer> structures = new Stack<Integer>();
		for (int i = 0; i < complexity; i++)
		{
			structures.push((int)(Math.random()*STRUCT_COUNT)); // adds a random structure to be added
		}
		
		ArrayList<CoordCombo> opentiles = new ArrayList<CoordCombo>();
		while (!structures.isEmpty())
		{
			int struct = structures.pop();
			for (int i = 2; i <= 10; i++) // reduced range to keep border and rim free
			{
				for (int j = 2; j <= 10; j++)
				{
					switch (struct) // decides openness based on the structure seeking
					{
					case 0:
						if (terrain[i][j].type() == TileType.DFLT) { opentiles.add(new CoordCombo(i, j)); }
						break;
					case 1:
						if (terrain[i][j].type() == TileType.DFLT &&
							terrain[i+1][j].type() == TileType.DFLT &&
							terrain[i][j].type() == TileType.DFLT &&
							terrain[i+1][j+1].type() == TileType.DFLT) { opentiles.add(new CoordCombo(i, j)); }
						break;
					case 2:
						if (terrain[i][j].type() == TileType.DFLT &&
							terrain[i+1][j].type() == TileType.DFLT &&
							terrain[i+1][j+1].type() == TileType.DFLT) { opentiles.add(new CoordCombo(i, j)); }
						break;
					case 3:
						if (terrain[i][j].type() == TileType.DFLT &&
							terrain[i][j+1].type() == TileType.DFLT &&
							terrain[i+1][j+1].type() == TileType.DFLT) { opentiles.add(new CoordCombo(i, j)); }
						break;
					case 4:
						if (terrain[i][j].type() == TileType.DFLT &&
							terrain[i+1][j].type() == TileType.DFLT &&
							terrain[i][j+1].type() == TileType.DFLT) { opentiles.add(new CoordCombo(i, j)); }
						break;
					case 5:
						if (terrain[i][j+1].type() == TileType.DFLT &&
							terrain[i+1][j].type() == TileType.DFLT &&
							terrain[i+1][j+1].type() == TileType.DFLT) { opentiles.add(new CoordCombo(i, j)); }
						break;
					case 6:
						if (terrain[i][j].type() == TileType.DFLT &&
							terrain[i+1][j].type() == TileType.DFLT &&
							terrain[i-1][j].type() == TileType.DFLT &&
							terrain[i][j+1].type() == TileType.DFLT &&
							terrain[i][j-1].type() == TileType.DFLT) { opentiles.add(new CoordCombo(i, j)); }
						break;
					default:
						break;
					}
				}
			}
			CoordCombo selected = opentiles.get((int)(Math.random()*opentiles.size()));
			switch (struct) // generate structure based on the structure seeking
			{
			case 0:
				terrain[selected.X()][selected.Y()].setType(TileType.WALL);
				break;
			case 1:
				terrain[selected.X()][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()][selected.Y()+1].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()+1].setType(TileType.WALL);
				break;
			case 2:
				terrain[selected.X()][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()+1].setType(TileType.WALL);
				break;
			case 3:
				terrain[selected.X()][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()][selected.Y()+1].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()+1].setType(TileType.WALL);
				break;
			case 4:
				terrain[selected.X()][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()][selected.Y()+1].setType(TileType.WALL);
				break;
			case 5:
				terrain[selected.X()][selected.Y()+1].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()+1].setType(TileType.WALL);
				break;
			case 6:
				terrain[selected.X()][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()+1][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()][selected.Y()+1].setType(TileType.WALL);
				terrain[selected.X()-1][selected.Y()].setType(TileType.WALL);
				terrain[selected.X()][selected.Y()+-1].setType(TileType.WALL);
				break;
			default:
				break;
			}
		}
		//hole patching
		for (int i = 1; i < 11; i++)
		{
			for (int j = 1; j < 11; j++)
			{
				if (terrain[i+1][j].type() == TileType.WALL &&
					terrain[i-1][j].type() == TileType.WALL &&
					terrain[i][j+1].type() == TileType.WALL &&
					terrain[i][j-1].type() == TileType.WALL) { terrain[i][j].setType(TileType.WALL); }
			}
		}
		
		for (int i = 0; i < 12; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				if ((i == 1 && j >= 1 && j <= 10) ||
					(i == 10 && j >= 1 && j <= 10) ||
					(j == 1 && i >= 1 && i <= 10) ||
					(j == 10 && i >= 1 && i <= 10))
				{
					terrain[i][j].setType(TileType.DFLT);
				}
			}
		}
		
		// debug printing!
		/*
		for (int i = 0; i < 12; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				if (terrain[i][j].type() == TileType.DFLT) { System.out.print("O"); }
				if (terrain[i][j].type() == TileType.WALL) { System.out.print("X"); }
			}
			System.out.print("\n");
		}
		*/
	}
	
	public ArrayList<CoordCombo> openList ()
	{
		ArrayList<CoordCombo> openTiles = new ArrayList<CoordCombo>();
		for (int i = 1; i < 11; i++)
		{
			for (int j = 1; j < 11; j++)
			{
				if (terrain[i][j].type() == TileType.DFLT) { openTiles.add(new CoordCombo(i, j)); }
			}
		}
		return openTiles;
	}
	
	public TerrainTile[][] map()
	{
		return terrain;
	}
}
