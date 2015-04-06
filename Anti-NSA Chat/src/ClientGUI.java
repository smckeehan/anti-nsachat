import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.UnknownHostException;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// will hold "Username:"
	private JLabel label;
	// to hold the Username
	private JTextField user;
	//to hold the recipient username
	private JTextField recipient;
	//key value fields
	private JTextField kn, ke, kd;
	//textfield for message input
	private JTextField message;
	//send button
	private JButton send;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort;
	// to Logout and get the list of the users
	private JButton login;
	//determines whether the log button is login or logout
	private boolean loggedIn;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;

	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;

		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		// the server name and the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1, 4, 1, 3));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		JPanel usernames = new JPanel (new GridLayout(1, 2, 1, 3));
		user = new AutoClearTF("Enter your username...");
		user.setBackground(Color.WHITE);
		usernames.add(user);
		recipient = new AutoClearTF("Enter recipient username...");
		recipient.setBackground(Color.WHITE);
		usernames.add(recipient);
		northPanel.add(usernames);
		add(northPanel, BorderLayout.NORTH);

		//Fields for the key, both public and private
		JPanel keys = new JPanel (new GridLayout(1, 6, 1, 3));
		keys.add(new JLabel("Public n:"));
		kn = new JTextField("");
		keys.add(kn);
		keys.add(new JLabel("Public e:"));
		ke = new JTextField("");
		keys.add(ke);
		keys.add(new JLabel("Private d:"));
		kd = new JTextField("");
		keys.add(kd);
		northPanel.add(keys);
		add(northPanel, BorderLayout.NORTH);


		// The CenterPanel which is the chat room
		ta = new JTextArea("Welcome to Anti-NSA Chat. Enter server and login details to get started!\n", 10, 10);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		//the message sending area
		message = new JTextField("", 30);
		send = new JButton("Send");
		send.addActionListener(this);
		send.setEnabled(false); //Not enabled until logged in

		// the 2 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		loggedIn = false;

		JPanel southPanel = new JPanel();
		southPanel.add(message);
		southPanel.add(send);
		southPanel.add(login);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		user.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		login.setText("Login");
		loggedIn = false;
		label.setText("Enter your username below");
		user.setText("Anonymous");
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		user.removeActionListener(this);
		connected = false;
	}

	/*
	 * Button or JTextField clicked
	 */
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		// ok it is coming from the JTextField
		if(o == message || o == send) {
			//get the message, username, and recipient name
			String text = message.getText();
			String username = user.getText().trim();
			String recName = recipient.getText().trim();

			//get key values
			EKey key = new EKey();
			key.setN(Integer.parseInt(kn.getText()));
			key.setE(Integer.parseInt(ke.getText()));
			DKey dkey = new DKey();
			dkey.setN(Integer.parseInt(kn.getText()));
			dkey.setD(Integer.parseInt(kd.getText()));

			//send message
			try {
				client.sendMessage(text, recName);
			} catch (IOException e1) {
				System.out.println("Message failed to send properly");
			}

			//put the returned message in the chat field and clear message box
			ta.append("To " + recName + "> " + text + "\n");
			message.setText("");
			return;
		}

		//if hit the login button
		if(o == login && !loggedIn) {
			// ok it is a connection request
			String username = user.getText().trim();
			// empty username ignore it
			if(username.length() == 0)
				return;
			// empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			// empty or invalid port number, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;

			//ensure the key information is filled in
			if(kn.getText().trim().length() == 0 || ke.getText().trim().length() == 0 || kd.getText().trim().length() == 0) {
				return;
			}
			//create the public portion of the key to share
			EKey ekey = new EKey();
			DKey dkey = new DKey();
			int keyN = 0;
			int keyE = 0;
			int keyD = 0;
			try {
				keyN = Integer.parseInt(kn.getText());
				keyE = Integer.parseInt(ke.getText());
				keyD = Integer.parseInt(kd.getText());
			}
			catch(Exception ex) {
				System.out.println("Key parse failure");
				return;
			}
			ekey.setN(keyN);
			ekey.setE(keyE);
			
			dkey.setN(keyN);
			dkey.setD(keyD);

			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				System.out.println("Port Number failure");
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			try {
				client = new Client(server, username, ekey, dkey, this);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				System.out.println("Interesting...");
			}

			// test if we can start the Client
			if(!client.start()) 
				return;
			connected = true;

			// change login button to logout button
			login.setText("Logout");
			loggedIn = true;


			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			// Action listener for when the user enter a message
			message.addActionListener(this);
			send.setEnabled(true);
			kn.setEditable(false);
			ke.setEditable(false);
			kd.setEditable(false);
			user.setEditable(false);
		}

		//if hit the logout button
		else if (o == login && loggedIn) {
			//close the client and remove it
			client.end();
			client = null;
			
			//change textfields to be editable and make messages not able to send
			message.removeActionListener(this);
			send.setEnabled(false);
			kn.setEditable(true);
			ke.setEditable(true);
			kd.setEditable(true);
			user.setEditable(true);
			tfServer.setEditable(true);
			tfPort.setEditable(true);
			
			//set client as logged out, and change login button to login
			login.setText("Login");
			loggedIn = false;
		}

	}
	
	/**
	 * send a typical chat message to the display
	 * @param message
	 */
	public void printMessage(ChatMessage message) {
		ta.append(message.toString() + "\n");
	}
	
	/**
	 * send a special server message to the display
	 * @param message
	 */
	public void serverMessage(String message) {
		ta.append("Server: " + message + "\n");
	}

	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 1500);
	}

	/**
	 * Textfield that clears when clicked into and reverts to initial text
	 * if it is empty when it loses focus. Functionally the same as JTextField besides
	 * auto clearing.
	 * 
	 * @see JTextField
	 * @see FocusListener
	 * 
	 **/
	class AutoClearTF extends JTextField{
		public AutoClearTF(){
			this(null,null,0);
		}
		public AutoClearTF(String text){
			this(null,text,0);
		}
		public AutoClearTF(int columns){
			this(null, null, columns);
		}
		public AutoClearTF(String text, int columns){
			this(null, text, columns);
		}
		public AutoClearTF(Document doc, String text, int columns){
			//Constructs with JTextField constructor and adds the focus listener/sets the text color
			super(doc,text,columns);
			addFocusListener(new AutoClearTextListener(this,new Color(120,120,120), new Color(0,0,0)));
			setForeground(new Color(120,120,120));
		}
	}

	/**
	 * Implementation of a focus listener that changes a JTextComponent's color
	 * and text on focus gain/lost depending on if text was entered.
	 **/
	class AutoClearTextListener implements FocusListener{
		JTextComponent tc;
		String initialText;
		Color initialColor;
		Color onFocusColor;

		public AutoClearTextListener(JTextComponent tc, Color initialColor, Color onFocusColor){
			this.tc = tc;
			initialText = tc.getText();
			this.initialColor = initialColor;
			tc.setForeground(initialColor);
			this.onFocusColor = onFocusColor;
		}

		@Override
		public void focusGained(FocusEvent e) {
			if(tc.getText().equals(initialText)){
				tc.setText("");
				tc.setForeground(onFocusColor);
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if(tc.getText().equals("")){
				tc.setText(initialText);
				tc.setForeground(initialColor);
			}
		}
	}

}