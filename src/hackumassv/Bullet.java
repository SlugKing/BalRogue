package hackumassv;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;
public class Bullet extends Entity
{
	// bullets will rotate according to changes in their orientation.
	
	private int lifeCounter = 0;
	public Bullet(int x, int y, double v, int o, int l) throws SlickException // x-coord, y-coord, orientation in deg, velocity, lifetime
	{
		this.x = x;
		this.y = y;
		this.v = v;
		this.o = o;
		this.lifeCounter = l;
		img = new Image("img/bullet.png");
		img.setFilter(Image.FILTER_NEAREST);
		boundingBox = new Rectangle(this.x, this.y, 20, 20);
	}
	
	public void dynamicUpdates(int delta)
	{
		lifeCounter--;
		if (lifeCounter <= 0) { alive = false; }
		img.setRotation(-o);
		//v = (0.05*v)+0.01*o;
		//o += 1;
	}
	
}
