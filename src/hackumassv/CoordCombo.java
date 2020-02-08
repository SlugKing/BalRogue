package hackumassv;

public class CoordCombo
{
	// this is how i'm storing coordinate combos lol why doesnt java have tuples
	private int x, y;
	
	public CoordCombo(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int X() { return x; }
	public int Y() { return y; }
}
