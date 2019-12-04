import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class CircleLightGame {
	// Constants
	private static final int NUM_LIGHTS = 20;
	
	// Instance Variables
	private State mState;
	private CircleLightPanel mPanel;
	
	// Constructor
	public CircleLightGame() {
		// init
		JFrame frame = new JFrame("Circle Light Game");
		
		mState = State.Init;
		
		mPanel = new CircleLightPanel(NUM_LIGHTS);
		frame.add(BorderLayout.CENTER, mPanel);
		
		JButton button = new JButton("Click");
		frame.add(BorderLayout.SOUTH, button);
		
		
		frame.setSize(400, 500);
		frame.setVisible(true);
	}
	
	// Private Methods
	
	private void update() {
		
	}
}
