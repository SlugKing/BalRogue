package hackumassv;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

public class Entity
{
	protected Image img;
	protected int x, y, o;
	protected double v;
	protected Shape boundingBox;
	protected boolean alive = true;
	protected boolean moveFlagX = true;
	protected boolean moveFlagY = true;
	
	public int X() { return x; }
	public int Y() { return y; }
	public int O() { return o; }
	public double VX() { return v*Math.cos(Math.toRadians(o)); }
	public double VY() { return v*-Math.sin(Math.toRadians(o)); }
	public void render() { img.draw(x, y); }
	public void setX(int x)
	{
		this.x = x;
		boundingBox.setX(x);
	}
	public void setY(int y)
	{
		this.y = y;
		boundingBox.setY(y);
	}
	public void dynamicUpdates(int delta) {} // blank for general entities, can be overridden
	public Shape bb() { return boundingBox; }
	public void kill()
	{
		alive = false;
	}
	public boolean alive()
	{
		return alive;
	}
	public void setMoveFlagX(boolean f)
	{
		moveFlagX = f;
	}
	public boolean getMoveFlagX() { return moveFlagX; }
	public void setMoveFlagY(boolean f)
	{
		moveFlagY = f;
	}
	public boolean getMoveFlagY() { return moveFlagY; }
	
}
