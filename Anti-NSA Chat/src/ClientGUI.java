import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// will hold "Username:"
	private JLabel label;
	// to hold the Username
	private JTextField user;
	//to hold the recipient username
	private JTextField recipient;
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
		user = new JTextField("Enter your username...");
		user.setBackground(Color.WHITE);
		usernames.add(user);
		recipient = new JTextField("Enter recipient username...");
		recipient.setBackground(Color.WHITE);
		usernames.add(recipient);
		northPanel.add(usernames);
		add(northPanel, BorderLayout.NORTH);
		
		//Fields for the key, both public and private
		JPanel keys = new JPanel (new GridLayout(1, 6, 1, 3));
		keys.add(new JLabel("Public n:"));
		keys.add(new JTextField(""));
		keys.add(new JLabel("Public e:"));
		keys.add(new JTextField(""));
		keys.add(new JLabel("Private d:"));
		keys.add(new JTextField(""));
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
		message.addActionListener(this);
		send = new JButton("Send");
		send.addActionListener(this);
		send.setEnabled(true); //Not enabled until logged in
		
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
		if(o == message) {
			// just have to send the message
			//client.sendMessage("");
			String text = message.getText();
			String username = user.getText().trim();
			ta.append(username + ": " + text + "\n");
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
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			//client = new Client(server, port, username, this);
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
		}
		
		//if hit the logout button
		if (o == login && loggedIn) {
			client.sendMessage("");
			return;
		}
		
		if (o == send) {
			String text = message.getText();
			String username = user.getText().trim();
			ta.append(username + ": " + text + "\n");
			message.setText("");
		}

	}

	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 1500);
	}

}

