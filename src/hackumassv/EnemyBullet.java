package hackumassv;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class EnemyBullet extends Bullet
{
	private int lifeCounter = 0;
	public EnemyBullet(int x, int y, double v, int o, int l) throws SlickException
	{
		super(x, y, v, o, l);
		img = new Image("img/e-bullet.png");
		img.setFilter(Image.FILTER_NEAREST);
		boundingBox = new Rectangle(this.x, this.y, 20, 20);
	}
}
