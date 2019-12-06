import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class CircleLightPanel extends JPanel {
	// Constants
	private static final int NUM_LIGHTS = 48;
	private static final int MARGIN = 50; // pixels
	private static final long UPDATE_PERIOD_INIT = 100; // millis
	private static final long UPDATE_PERIOD_PLAYING = 5; // millis
	private static final long UPDATE_PERIOD_STOPPED = 600; // millis
	private static final long UPDATE_PERIOD_LOSE= 300; // millis
	private static final long UPDATE_PERIOD_WIN = 10; // millis
	private static final int NUM_LOSE_UPDATES = 5; // number of calls to update() executed during Lose State
	private static final float HUE_INCREMENT = 6f/360; // hue is logically degrees from 0 to 360 expressed as a floating point from 0.0 to 1.0
	
	private class UpdateTask extends TimerTask {
		@Override
		public void run() {
			CircleLightPanel.this.update();
		}
	}
	
	// Instance Variables
	private int mCurrentLightIndex;
	private ArrayList<Light> mLights;
	private State mState;
	private Timer mTimer;
	private int mLoseUpdateCount;
	
	// Constructor
	public CircleLightPanel() {
		// init
		mLights = new ArrayList<Light>();
		this.setBackground(Color.DARK_GRAY);
		
		// add Lights
		for (int i = 0; i < NUM_LIGHTS; i++) {
			mLights.add(new Light());
		}
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// update each Light's position based on the size of the Panel
				int a = e.getComponent().getWidth() / 2;
				int b = e.getComponent().getHeight() / 2;
				int diameter = Math.min(e.getComponent().getWidth() - 2*MARGIN,
						e.getComponent().getHeight() - 2*MARGIN);
				int radius = diameter / 2;
				
				double deltaTheta = 2*Math.PI / NUM_LIGHTS;
				double theta = Math.PI / 2;
				for (Light light : mLights) {
					double x = a + radius * Math.cos(theta);
					double y = b + radius * Math.sin(theta);
					light.setX((int)x);
					light.setY((int)y);
					theta += deltaTheta;
				}
			}
		});
		
		mTimer = new Timer();
		this.setState(State.Init);
	}
	
	// Private Methods
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// determine Light's radius
		// Light's radius will be half the distance between two
		// adjacent Light's centers
		Light light0 = mLights.get(0);
		Light light1 = mLights.get(1);
		// Pythagorean stuff
		int a = light0.getX() - light1.getX();
		int b = light0.getY() - light1.getY();
		double c = Math.sqrt((a*a) + (b*b));
		int r = (int) (c / 2);
		
		for (Light light : mLights) {
			g.setColor(Color.LIGHT_GRAY);
			drawCircle(g, light.getX(), light.getY(), r);
			if (light.getIsOn()) {
				g.setColor(light.getColor());
				fillCircle(g, light.getX(), light.getY(), r);
			}
		}
	}
	
	/**
	 * Draws a circle with center point at (x, y) and radius r
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param r
	 */
	private void drawCircle(Graphics g, int x, int y, int r) {
		g.drawOval(x-r, y-r, r*2, r*2);
	}
	
	/**
	 * Fills a circle with center point at (x, y) and radius r
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param r
	 */
	private void fillCircle(Graphics g, int x, int y, int r) {
		g.fillOval(x-r, y-r, r*2, r*2);
	}
	
	private void setState(State newState) {
		mTimer.cancel();
		mState = newState;
		// TODO perform one-time actions on state change
		long updateDelay = 0;
		long updatePeriod = 0;
		switch (newState) {
		case Init:
			updateDelay = UPDATE_PERIOD_INIT;
			updatePeriod = UPDATE_PERIOD_INIT;
			for (int i = 0; i < mLights.size(); i++) {
				Light light = mLights.get(i);
				// set initial pattern for Lights
				switch (i % 6) {
				case 0:
				case 1:
				case 2:
					light.setIsOn(false);
					break;
				case 3:
				case 4:
				case 5:
					light.setColor(Color.WHITE);
					light.setIsOn(true);
					break;
				}
			}
			break;
		case Playing:
			updateDelay = UPDATE_PERIOD_PLAYING;
			updatePeriod = UPDATE_PERIOD_PLAYING;
			mCurrentLightIndex = new Random().nextInt(mLights.size());
			for (int i = 0; i < mLights.size(); i++) {
				if (i == mCurrentLightIndex) {
					// set the current Light to green
					mLights.get(i).setColor(Color.GREEN);
					mLights.get(i).setIsOn(true);
				} else if (i == 0) {
					// set the bottom Light to green
					mLights.get(i).setColor(Color.RED);
					mLights.get(i).setIsOn(true);
				} else {
					// turn off all other Lights
					mLights.get(i).setIsOn(false);
				}
			}
			break;
		case Stopped:
			updateDelay = UPDATE_PERIOD_STOPPED;
			updatePeriod = UPDATE_PERIOD_STOPPED;
			break;
		case Lose:
			updateDelay = UPDATE_PERIOD_LOSE;
			updatePeriod = UPDATE_PERIOD_LOSE;
			mLoseUpdateCount = 0;
			// set each Light red and turn on
			for (Light light : mLights) {
				light.setColor(Color.RED);
				light.setIsOn(true);
			}
			break;
		case Win:
			updateDelay = 0;
			updatePeriod = UPDATE_PERIOD_WIN;
			
			float hue = 0f;
			float hueIncrement = 1f / mLights.size();
			for (Light light : mLights) {
//				light.setColor(Color.getHSBColor(120f / 360, 1, 1));
				light.setColor(Color.getHSBColor(hue, 1, 1));
				light.setIsOn(true);
				hue += hueIncrement;
			}
			break;
		}
		this.repaint();
		
		// start update timer
		mTimer = new Timer();
		mTimer.schedule(new UpdateTask(), updateDelay, updatePeriod);
	}
	
	private void startNextState() {
		State newState = mState;
		switch (mState) {
		case Init:
			newState = State.Playing;
			break;
		case Playing:
			newState = (mCurrentLightIndex == 0) ? State.Win : State.Stopped;
			break;
		case Stopped:
			newState = State.Lose;
			break;
		case Lose:
			newState = State.Playing;
			break;
		case Win:
			newState = State.Playing;
			break;
		}
		setState(newState);
	}
	
	/**
	 * Update UI for 1 game tick based on given State
	 * @param state
	 */
	private void update() {
		ArrayList<Light> oldState = new ArrayList<Light>();
		
		switch (mState) {
		case Init:
			for (int i = 0; i < mLights.size(); i++) {
				Light currentLight = mLights.get(i);
				Light previousLight = (i == 0) ? mLights.get(mLights.size() - 1) : oldState.get(i - 1);
				oldState.add(new Light(currentLight));
				
				if (!currentLight.getIsOn() && previousLight.getIsOn()) {
					// this Light is off, previous Light was on
					currentLight.setColor(Color.WHITE);
					currentLight.setIsOn(true);
				} else if (currentLight.getIsOn() && !previousLight.getIsOn()) {
					// this Light is on, previous Light was off
					currentLight.setIsOn(false);
				}
			}
			break;
		case Playing:
			for (int i = 0; i < mLights.size(); i++) {
				Light currentLight = mLights.get(i);
				Light previousLight = (i == 0) ? mLights.get(mLights.size() - 1) : oldState.get(i - 1);
				oldState.add(new Light(currentLight));
				
				if (i == 0) {
					// this Light is the bottom Light
					if (currentLight.getColor().equals(Color.GREEN)) {
						// this Light is currently green, set it to red
						currentLight.setColor(Color.RED);
						currentLight.setIsOn(true);
					} else if (previousLight.getIsOn()) {
						// Light before this one was on, set this one to green
						currentLight.setColor(Color.GREEN);
						currentLight.setIsOn(true);
					}
					continue;
				}
				// current Light isn't the bottom Light
				if (currentLight.getIsOn()) {
					// current Light is on, turn it off
					currentLight.setIsOn(false);
				} else if (previousLight.getIsOn()
						&& previousLight.getColor().equals(Color.GREEN)) {
					// Light before this one was on and green, turn this one on and green
					currentLight.setColor(Color.GREEN);
					currentLight.setIsOn(true);
				}
			}
			// increment mCurrentLightIndex
			mCurrentLightIndex = (mCurrentLightIndex == mLights.size() - 1) ?
					0 : mCurrentLightIndex + 1;
			break;
		case Stopped:
			startNextState();
			break;
		case Lose:
			// flash Lights
			boolean isOn = mLights.get(1).getIsOn();
			for (Light light : mLights) {
				light.setIsOn(!isOn);
			}
			
			if (++mLoseUpdateCount > NUM_LOSE_UPDATES) {
				startNextState();
			}
			break;
		case Win:
			// TODO increment Light's hue
			for (Light light : mLights) {
				Color col = light.getColor();
				float[] hsbVals = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null /* create new array to return */);
				
				light.setColor(Color.getHSBColor(hsbVals[0] - HUE_INCREMENT /* minus for clockwise effect */, hsbVals[1], hsbVals[2]));
			}
			break;
		}
		
		this.repaint();
	}//end update()
	
	
	// Public Methods
	
	public void onButtonClicked() {
		switch (mState) {
		// button clicks only have an effect in the following States
		case Init:
		case Playing:
		case Win:
			startNextState();
			break;
		}
	}
	
}
