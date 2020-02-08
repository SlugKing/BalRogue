package hackumassv;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

public class TerrainTile
{

	public enum TileType { DFLT, WALL, GOAL };
	private TileType type;
	public TerrainTile()
	{
		type = TileType.DFLT;
	}
	
	public TileType type()
	{
		return type;
	}
	
	public void setType(TileType t)
	{
		type = t;
	}
}
