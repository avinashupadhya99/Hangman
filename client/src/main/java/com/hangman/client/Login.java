package com.hangman.client;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login {
	
	static void startGame(JFrame login, String loginName) throws IOException {
		GameClient client = new GameClient(loginName);
		login.setVisible(false);
		login.dispose();
	}
	
	public static void main(String[] args) {
		JFrame login = new JFrame("Login");
		JPanel panel = new JPanel();
		JTextField loginName = new JTextField(20);
		JButton enterBtn = new JButton("Login");
		
		panel.add(loginName);
		panel.add(enterBtn);
		
		login.setSize(400, 150);
		login.add(panel);
		login.setVisible(true);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		enterBtn.addActionListener(event -> {
			if(loginName.getText().length()>0) {
				try {
					startGame(login,loginName.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		loginName.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					if(loginName.getText().length()>0) {
						try {
							startGame(login,loginName.getText());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
		});
		
	}

}