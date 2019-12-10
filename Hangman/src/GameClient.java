import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GameClient extends JFrame implements Runnable {
	
	ArrayList<String> loginNames;
	String loginName;
	Random rand = new Random();
	
	static final int maxChances = 5;
	
	private boolean exit;//Used to kill a thread
	
	String words[] = {"AMAZON","GOOGLE","ORACLE","ADOBE","FACEBOOK","MICROSOFT","APPLE","CISCO","INTEL","SAMSUNG","RAKUTEN","NVIDIA","DELL","ASUS","ACER","ACCENTURE","INFOSYS","WIPRO","PHILIPS"};
	String word;
	
	DataInputStream in;
	DataOutputStream out;
	
	ArrayList<Character> entr;
	
	private int length, count, chances, ind, aFlg;
	
	JPanel panel = new JPanel();

	
	ImageIcon imageIcon;
	JLabel pict;
	
	JCheckBox cBox;
	JComboBox<String> userList;
	
	JTextField mWord;
	JTextField input;
	JTextField[] fields;
	JTextField result;
	JTextField tries;
	JTextField info;
	JTextField cInfo;
	
	JButton reset;
	JButton logout;
	JButton computer;
	JButton friend;
	JButton rMainMenu;
	
	
	public GameClient(String loginName) throws IOException {
		super(loginName);
		this.loginName = loginName;
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				logout();
			}
		});
		
		Socket socket = new Socket("127.0.0.1",5215);
		
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		
		out.writeUTF(loginName);
		out.writeUTF("LOGIN "+loginName);
		
		this.aFlg = 0;
		
		info = new JTextField(15);
		info.setBorder(null);
		info.setEditable(false);
		info.setText("Welcome to Hangman");
		
		cInfo = new JTextField(50);
		cInfo.setBorder(null);
		cInfo.setEditable(false);
		cInfo.setText("Clue: Tech Companies");
		
		logout = new JButton("Logout");
		computer= new JButton("Against Computer");
		friend = new JButton("With Friends");
		
		entr = new ArrayList<Character>();
		
		computer.addActionListener(event ->{
			playBot();
		});
		
		friend.addActionListener(event ->{
			multiPlayer();
		});
		
		logout.addActionListener(event -> {
			logout();
		});
		
		setSize(650,550);
		setResizable(false);
		
		userList = new JComboBox<String>();
		cBox = new JCheckBox("Accept word");
		pict = new JLabel();
		reset = new JButton("Reset");
		logout = new JButton("Logout");
		rMainMenu = new JButton("Return to Main Menu");
		input = new JTextField(1);
		mWord = new JTextField(10);
		tries =  new JTextField();
		result = new JTextField(9);
		result.setBorder(null);
		result.setEditable(false);
		tries.setEditable(false);
		input.setDocument(new JTextFieldLimit(1));
		
		panel.add(info);
		panel.add(computer);
		panel.add(friend);
		panel.add(logout);
		
		add(panel);
		setVisible(true);
		
	}


	private void logout() {
		try {
			out.writeUTF("LOGOUT "+loginName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(1);		
	}


	private void multiPlayer() {
		panel.removeAll();
		panel.add(rMainMenu);
		panel.add(mWord);
		panel.add(cBox);
		panel.add(userList);
		
		this.exit = false;
		this.word="";
		System.out.println("CheckPoint");
		new Thread(this).start();
		
		rMainMenu.addActionListener(event ->{
			if(this.word.length()>0) {
				for(int i=0;i<this.word.length();i++) {
					//deletes all the fields JTextFields 
					this.fields[i] = null;
				}
			}
			//Kill the thread
			System.out.println(this.loginName+" thread stopped");
			this.stop();
			mainMenu();
		});
		
		mWord.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(!mWord.getText().isEmpty()) {
						try {
							String sndName = (String) userList.getSelectedItem();
							out.writeUTF("WORD "+sndName+" "+mWord.getText());
							mWord.setText("");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			
		});
		
		cBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		           chFlg(1);
		        } else {//checkbox has been deselected
		           chFlg(0);
		        };				
			}
			
		});		
		
		panel.revalidate();
		panel.repaint();
	}


	protected void chFlg(int i) {
		this.aFlg = i;
	}


	private void playBot() {
		panel.removeAll();
		ind = rand.nextInt(words.length);
		this.word = words[ind];
		this.length = word.length();
		this.count = 0;
		this.chances=0;
		this.pict.setIcon(new ImageIcon("src/Images/zeroth.png"));
		this.result.setText("");
		this.tries.setText(""+chances);
		
		rMainMenu.addActionListener(event ->{
			if(this.word.length()>0) {
				for(int i=0;i<this.word.length();i++) {
					//deletes all the fields JTextFields 
					this.fields[i] = null;
				}
			}
			mainMenu();
		});
		
		reset.addActionListener(event -> {
			reset();
		});
		
		panel.add(cInfo);
		panel.add(rMainMenu);
		panel.add(reset);
		panel.add(tries);
		panel.add(input);
		
		
		fields = new JTextField[length];
		
		int i;

		
		for(i=0; i<fields.length; i++) {
			fields[i] = new JTextField(1);
			fields[i].setEditable(false);
			panel.add(fields[i]);
		}
		
		panel.add(result);
		panel.add(pict);
		panel.revalidate();
		panel.repaint();
		funct();
	}


	private void mainMenu() {
		
		panel.removeAll();
		
		entr.clear();
		
		panel.add(info);
		panel.add(computer);
		panel.add(friend);
		panel.add(logout);
		logout.addActionListener(event -> {
			logout();
		});
		
		panel.revalidate();
		panel.repaint();
		
	}


	private void funct() {
		
		input.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(!input.getText().isEmpty()) {
						checkB();
					}		
				}	
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				
			}
			private void checkB() { //Checks of the character entered is present in the string word
				int flg = 0; //Used to check if a wrong character was entered by the user
				char ch[] = input.getText().toCharArray();
				input.setText("");
				if(checkC(ch[0])) {
					for(int i=0;i<word.length();i++) {
						if(word.charAt(i) == ch[0] ) {
							count++;
							entr.add(ch[0]);
							fields[i].setText(""+ch[0]);
							flg = 1;
						}
						if(!checkC(ch[0])) {
							flg = 1;
						}
					}
					if(flg == 0) {
						chances++;
						switch(chances) {
						case 1: pict.setIcon(new ImageIcon("src/Images/first.png"));
						break;
						case 2: pict.setIcon(new ImageIcon("src/Images/second.png"));
						break;
						case 3: pict.setIcon(new ImageIcon("src/Images/third.png"));
						break;
						case 4: pict.setIcon(new ImageIcon("src/Images/fourth.png"));
						break;
						case 5: pict.setIcon(new ImageIcon("src/Images/fifth.png"));
						break;
						default: pict.setIcon(new ImageIcon("src/Images/zeroth.png"));
						break;
						}
						tries.setText(""+chances);
						if(chances == maxChances) {
							System.out.println(word);
							result.setText("Game Over");
							input.setEditable(false);
						}
					}
					if(count == word.length()) {
						
						result.setText("Congrats");
						input.setEditable(false);
					}
				}
			}

			private boolean checkC(char c) { //Checks if the correct character was already entered  
				for(int i=0;i<entr.size();i++) {
					if(entr.get(i) == c)
						return false;
				}
				return true;
			}
			
		});
		
	}


	private void reset() {

		for(int i=0;i<word.length();i++) {
			//deletes all the fields JTextFields 
			panel.remove(fields[i]);
			fields[i] = null;
		}
		
		panel.remove(pict);
		panel.remove(result);
		
		entr.clear();
		
		int nInd;
		while(true) { //To generate unique words after each reset
			nInd = rand.nextInt(words.length);
			if(nInd == ind)
				continue;
			else {
				ind = nInd;
				break;
			}
		}
		
		this.count = 0;
		this.chances=0;
		input.setEditable(true);
		tries.setText(""+chances);
		result.setText("");
		this.word = words[ind];
		this.length = word.length();
		
		fields = new JTextField[length];
		
		for(int i=0;i<fields.length;i++) {
			//Creates the new fields JTextFields and adds them to the panel
			fields[i] = new JTextField(1);
			fields[i].setEditable(false);
			panel.add(fields[i]);
		}
		
		pict.setIcon(new ImageIcon("src/Images/zeroth.png"));

		panel.add(result);
		panel.add(pict);
		panel.revalidate();
		panel.repaint();
		
		funct();
		
	}


	@Override
	public void run() {
		while(true) {
			try {

				String msgFrmServer = in.readUTF();
				System.out.println(msgFrmServer);
				StringTokenizer msgParts = new StringTokenizer(msgFrmServer);
				String msgType = msgParts.nextToken();
				
				switch(msgType) {
				case "WORD":
					if(this.aFlg==1) {
						if(this.word.length()>0) {
							for(int i=0;i<word.length();i++) {
								//deletes all the fields JTextFields 
								panel.remove(fields[i]);
								fields[i] = null;
							}
							panel.remove(tries);
							panel.remove(input);
							panel.remove(result);
							panel.remove(pict);
						}
							this.word = msgParts.nextToken();
							System.out.println(this.loginName+" received "+this.word);
							this.length = this.word.length();
							this.count = 0;
							this.chances=0;
							entr.clear();
							pict.setIcon(new ImageIcon("src/Images/zeroth.png"));
							fields = new JTextField[length];
							panel.add(tries);
							panel.add(input);
							int i;
							input.setEditable(true);
							tries.setText(""+chances);
							result.setText("");
							
							for(i=0; i<fields.length; i++) {
								fields[i] = new JTextField(1);
								fields[i].setEditable(false);
								panel.add(fields[i]);
							}
							
							panel.add(result);
							panel.add(pict);
							
							panel.revalidate();
							panel.repaint();
							
							funct();
						}
					break;
				case "LOGIN":
					String n = msgParts.nextToken();
					System.out.println("Added "+n+" to "+this.loginName);
						if(n.compareTo(this.loginName)!=0) {
							//Add user to combo box
							userList.addItem(n);
						}
						break;
				case "LOGOUT":
					String nm = msgParts.nextToken();
					if(nm.compareTo(this.loginName)!=0) {
						//Remove user from combo box
						userList.removeItem(nm);
					}
					break;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
		}
	}
	
	public void stop() 
    { 
        this.exit = true; 
    }
}
