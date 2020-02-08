package hackumassv;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

public class Player extends Entity
{
	private int lifeCounter = 10;
	private double vx, vy;
	public Player(int x, int y) throws SlickException
	{
		this.x = x;
		this.y = y;
		img = new Image("img/player.png");
		boundingBox = new Rectangle(this.x, this.y, 40, 40);
	}
	
	public void decHP() { lifeCounter--; }
	public void decHP(int amt) { lifeCounter -= amt; }
	public int getHP() { return lifeCounter; }
	
	public double VX() { return vx; }
	public double VY() { return vy; }
	public void setVX(double vx) { this.vx = vx; }
	public void setVY(double vy) { this.vy = vy; }
}
