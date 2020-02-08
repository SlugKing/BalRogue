package hackumassv;
import java.util.ArrayList;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

public class Enemy extends Entity
{

	private int lifeCounter = 0; // unlike bullets, this is hp
	private int shotCounter = 0;
	public enum MonsterType { DFLT, WLKR };
	private MonsterType mtype;
	private double vx, vy;
	
	public Enemy (int x, int y, MonsterType m) throws SlickException
	{
		this.x = x;
		this.y = y;
		this.mtype = m;
		switch(this.mtype)
		{
		case DFLT:
			v = 0;
			lifeCounter = 5;
			shotCounter = 33;
			img = new Image("img/e-dflt.png");
			boundingBox = new Rectangle(this.x, this.y, 40, 40);
			break;
		case WLKR:
			v = 0.25;
			lifeCounter = 5;
			shotCounter = 40;
			img = new Image("img/e-wlkr.png");
			boundingBox = new Rectangle(this.x, this.y, 40, 40);
			break;
		default:
			lifeCounter = 0;
			img = null;
			boundingBox = null;
			break;
		}
	}
	
	public void dynamicUpdates(int delta, ArrayList<Entity> life, ArrayList<Entity> bullets) throws SlickException
	{
		switch (mtype)
		{
		case DFLT:
			if (shotCounter <= 0)
			{
				bullets.add(new EnemyBullet(x+10, y+10, 0.1+(.02*(5-lifeCounter)), 0+(2*(5-lifeCounter)), 200));
				bullets.add(new EnemyBullet(x+10, y+10, 0.1+(.02*(5-lifeCounter)), 45+(2*(5-lifeCounter)), 200));
				bullets.add(new EnemyBullet(x+10, y+10, 0.1+(.02*(5-lifeCounter)), 90+(2*(5-lifeCounter)), 200));
				bullets.add(new EnemyBullet(x+10, y+10, 0.1+(.02*(5-lifeCounter)), 135+(2*(5-lifeCounter)), 200));
				bullets.add(new EnemyBullet(x+10, y+10, 0.1+(.02*(5-lifeCounter)), 180+(2*(5-lifeCounter)), 200));
				bullets.add(new EnemyBullet(x+10, y+10, 0.1+(.02*(5-lifeCounter)), 225+(2*(5-lifeCounter)), 200));
				bullets.add(new EnemyBullet(x+10, y+10, 0.1+(.02*(5-lifeCounter)), 270+(2*(5-lifeCounter)), 200));
				bullets.add(new EnemyBullet(x+10, y+10, 0.1+(.02*(5-lifeCounter)), 315+(2*(5-lifeCounter)), 200));
				shotCounter = 33;
			}
			else { shotCounter--; }
			break;
		case WLKR:
			o = (int)(Math.toDegrees(Math.atan2(-(y-life.get(0).Y()), x-life.get(0).X())));
			if (shotCounter <= 0)
			{
				bullets.add(new EnemyBullet(x+10, y+10, 0.60, 180+o, 30));
				shotCounter = 40;
			}
			else { shotCounter--; }
			break;
		default:
			break;
		}
	}
	
	public void decHP() { lifeCounter--; }
	public void decHP(int amt) { lifeCounter -= amt; }
	public int getHP() { return lifeCounter; }
	
	public double VX()
	{
		return (v*Math.cos(o));
	}
	public double VY()
	{
		return (v*-Math.sin(o));
	}
	public void setVX(double vx) { this.vx = vx; }
	public void setVY(double vy) { this.vy = vy; }
}
