import java.awt.Color;

public class Light {
	// Instance Variables
	private int mX;
	private int mY;
	private boolean mIsOn;
	private Color mColor;
	
	// Constructor
	public Light() {
		mX = 0;
		mY = 0;
		mIsOn = false;
		mColor = Color.RED;
	}
	
	public Light(Light toCopy) {
		mX = toCopy.getX();
		mY = toCopy.getY();
		mIsOn = toCopy.getIsOn();
		mColor = toCopy.getColor();
	}
	
	// Getters
	public int getX() { return mX; }
	public int getY() { return mY; }
	public boolean getIsOn() { return mIsOn; }
	public Color getColor() { return mColor; }
	
	// Setters
	public void setX(int x) { mX = x; }
	public void setY(int y) { mY = y; }
	public void setIsOn(boolean value) { mIsOn = value; }
	public void setColor(Color color) { mColor = color; }
}
