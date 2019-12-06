import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;

public class CircleLightGame {
	// Instance Variables
	private CircleLightPanel mPanel;
	
	// Constructor
	public CircleLightGame() {
		// init
		JFrame frame = new JFrame("Circle Light Game");
		
		mPanel = new CircleLightPanel();
		frame.add(BorderLayout.CENTER, mPanel);
		
		JButton button = new JButton("Click");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mPanel.onButtonClicked();
			}
		});
		frame.add(BorderLayout.SOUTH, button);
		
		frame.setSize(900, 800);
		frame.setVisible(true);
	}
}
