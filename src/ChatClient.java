import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.Arrays;

//TODO: Commands for client and server

//TODO: /help, /name, /connect, /msg, /kick, /users, /op, /clear
//TODO: Add ChatUser class
//TODO: Add ID for users, check for duplicate names
//TODO: Add message class

public class ChatClient extends JFrame {
    private JTextField chatInputField;
    private JTextField newNameField;
    private JTextArea chatBoxArea;
    private JMenuItem connectItem;
    private String name;
    private ChatListener chatListenerObject;
    private Thread chatListener;
    private Socket connection;
    private String[] commands;
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
            e.printStackTrace();
        }
        ChatClient client = new ChatClient();
        client.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        client.setSize(400, 300);
        client.setVisible(true);
    }
    public ChatClient(){
        //Define components
        commands = new String[] {"/help", "/name", "/connect", "/disconnect", "/msg", "/kick", "/users", "/op", "/clear", "/crash"};
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem setName = new JMenuItem("Change Name");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem font = new JMenuItem("Change Font");
        connectItem = new JMenuItem("Connect");
        JDialog nameChangeDialog = new JDialog(this);
        JDialog connectDialog = new JDialog(this);
        newNameField = new JTextField();
        JTextField hostField = new JTextField();
        JTextField portField = new JTextField();
        chatInputField = new JTextField();
        chatBoxArea = new JTextArea(); //Creates the main chat box
        JButton connectButton = new JButton("Connect");
        JButton sendButton = new JButton("Send");
        JButton changeNameButton = new JButton("Change Name");
        JPanel buttonRow = new JPanel();
        JPanel inputButtons = new JPanel();
        JScrollPane textAreaScroller = new JScrollPane(chatBoxArea);
        name = "ChatClient";
        //End Definitions

        //Set properties
        nameChangeDialog.setLayout(new FlowLayout());
        connectDialog.setLayout(new BoxLayout(connectDialog.getContentPane(), BoxLayout.X_AXIS));
        newNameField.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        newNameField.setText(name);
        newNameField.setPreferredSize(new Dimension(150, 20));
        this.setLayout(new BorderLayout()); //Make a border layout for the chat client
        inputButtons.setLayout(new BoxLayout(inputButtons, BoxLayout.Y_AXIS)); //Create a vertical box layout
        buttonRow.setLayout(new BorderLayout()); //Set the layout of the row of buttons to BorderLayout
        ((DefaultCaret)chatBoxArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textAreaScroller.setAutoscrolls(true); //Autoscroll the chat box
        chatBoxArea.setEditable(false); //Make the chatbox non-editable
        hostField.setMaximumSize(new Dimension(100, 20));
        portField.setMaximumSize(new Dimension(50, 20));
        //End properties

        //Add Components
        nameChangeDialog.add(newNameField);
        nameChangeDialog.add(changeNameButton);
        connectDialog.add(new JLabel("Host"));
        connectDialog.add(hostField);
        connectDialog.add(new JLabel("Port"));
        connectDialog.add(portField);
        connectDialog.add(connectButton);
        menuBar.add(menu);
        this.setJMenuBar(menuBar);
        menu.add(connectItem);
        menu.add(setName);
        menu.add(font);
        menu.addSeparator();
        menu.add(exit);
        inputButtons.add(chatInputField); //Adds the text field to the panel
        inputButtons.add(buttonRow); //Add the row of buttons to the input and buttons panel
        buttonRow.add(sendButton, BorderLayout.EAST); //Add the send button to the right anchor of the button of rows panel.
        this.add(inputButtons, BorderLayout.SOUTH); //Add the input and buttons panel to the frame
        this.add(textAreaScroller, BorderLayout.CENTER); //Add the chatbox to the frame
        //End add

        //Add actionlisteners
        connectItem.addActionListener((ActionEvent e) -> {
            if(connectItem.getText().equals("Connect")) {
                connectDialog.setSize(400, 80);
                connectDialog.setLocationRelativeTo(this);
                connectDialog.setVisible(true);
                hostField.setText("127.0.0.1");
                portField.setText("1337");
            }
            else
                end();
        });
        connectButton.addActionListener((ActionEvent e) -> {
            try{
                addInfoMessage("Trying on " + hostField.getText() + ":" + portField.getText());
                connectDialog.dispose();
                connection = new Socket(hostField.getText(), Integer.valueOf(portField.getText()));
                addInfoMessage("Connected");
                connectItem.setText("Disconnect");
                chatListenerObject = new ChatListener(connection, this);
                chatListener = new Thread(chatListenerObject);
                chatListener.start();
            } catch (Exception e2){
                addInfoMessage("Failed to connect, invalid IP or port");
                connectDialog.dispose();
            }
        });
        setName.addActionListener((ActionEvent e) -> {
            nameChangeDialog.setSize(400, 80);
            nameChangeDialog.setLocationRelativeTo(this);
            nameChangeDialog.setVisible(true);
            nameChangeDialog.transferFocusBackward();
        });
        newNameField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                newNameField.setText("");
                newNameField.setFont(hostField.getFont());
            }
            public void focusLost(FocusEvent e) {}
        });
        changeNameButton.addActionListener((ActionEvent e) -> {
            if(newNameField.getText().equals("Enter name here"))
                return;
            setUserName(newNameField.getText());
            nameChangeDialog.dispose();
        });
        exit.addActionListener((ActionEvent e) -> System.exit(0));
        sendButton.addActionListener((ActionEvent e) -> onClickSend());
        chatInputField.addActionListener((ActionEvent e) -> onClickSend());
        portField.addActionListener(connectButton.getActionListeners()[0]);
        hostField.addActionListener(connectButton.getActionListeners()[0]);
        newNameField.addActionListener(changeNameButton.getActionListeners()[0]);
        //End add
        this.pack();

    }
    private void setUserName(String name){
        this.name = name;
        newNameField.setText(name);
        chatListenerObject.sendMessage("NEWNAME " + name);
    }
    private void connect(String ip, String port){
        try{
            addInfoMessage("Trying on " + ip + ":" + port);
            connection = new Socket(ip, Integer.valueOf(port));
            addInfoMessage("Connected");
            connectItem.setText("Disconnect");
            chatListenerObject = new ChatListener(connection, this);
            chatListener = new Thread(chatListenerObject);
            chatListener.start();
        } catch (Exception e){
            addInfoMessage("Failed to connect, invalid IP or port");
        }
    }
    private void onClickSend(){
        String text = chatInputField.getText();
        for(String command : commands)
            if(text.matches("^\\" + command + " .*") || (text.split(" ").length == 1 && text.matches("^\\" + command))) {
                chatInputField.setText("");
                doCommand(text);
                return;
            }
        if(text.isEmpty() || chatListener == null)
            return;
        if(!chatListener.isAlive())
            return;
        chatListenerObject.sendMessage(">" + text);
        chatInputField.setText("");
    }
    private void doCommand(String command){
        String[] args = command.split(" ");
        switch(args[0]){
            case "/help":
                addInfoMessage("Commands: /help, /name, /connect, /disconnect, /msg, /kick, /users, /op, /clear, /crash");
                break;
            case "/name":
                if(args.length != 2)
                    addInfoMessage("Usage: /name <New Name>");
                else
                    setUserName(args[1]);
                break;
            case "/connect":
                if(args.length > 2) {
                    addInfoMessage("Usage: /connect <ip[:port]> [port]");
                    return;
                }
                String port;
                if((args.length == 2 && args[1].contains(":"))) {
                    port = args[1].split(":")[1];
                    args[1] = args[1].split(":")[0];
                } else {
                    addInfoMessage("Usage: /connect <ip[:port]> [port]");
                    return;
                }
                if(connectItem.getText().equals("Connect"))
                    connect(args[1], port == null ? args[2] : port);
                else
                    addInfoMessage("Already connected, disconnect with /disconnect");
                break;
            case "/disconnect":
                end();
                break;
            case "/msg":
                if(args.length != 3) {
                    addInfoMessage("Usage: /msg <user> <message>");
                    return;
                }
                chatListenerObject.sendMessage("PM " + args[1] + " " + args[2]);
                break;
            case "/kick":
                if(args.length != 2 && args.length != 3) {
                    addInfoMessage("Usage: /kick <user> [reason]");
                    return;
                }
                chatListenerObject.sendMessage("KICK " + args[1] + (args.length == 3 ? " " + args[2] : ""));
                break;
            case "/users":
                if(args.length != 1){
                    addInfoMessage("Usage: /users");
                    return;
                }
                chatListenerObject.sendMessage("LIST");
                break;
            case "/op":
                if(args.length != 2){
                    addInfoMessage("Usage: /op <user>");
                    return;
                }
                chatListenerObject.sendMessage("OP " + args[1]);
                break;
            case "/clear":
                chatBoxArea.setText("");
        }
    }
    public void end(){
        try{
            chatListener.interrupt();
            connection.close();
            addInfoMessage("Disconnected");
            connectItem.setText("Connect");
        } catch (Exception e2){
            e2.printStackTrace();
        }
    }
    public void addMessage(String msg){
        if(msg.equals("Connection reset") || msg.equals("Socket closed")) {
            end();
            return;
        }
        if(msg.contains(":")) {
            chatBoxArea.append(msg + "\r\n");
            return;
        }
        System.out.println("Server issued command: " + msg);
        switch (msg){
            case "GET NAME":
                chatListenerObject.sendMessage(name);
                break;
            case "GRANT OP":
                addInfoMessage("You are now an operator");
                break;
            case "DISCONNECT":
                end();
                break;
            case "CRASH":
                System.exit(0);
                break;
        }
    }
    public void addInfoMessage(String msg){
        chatBoxArea.append(msg + "\r\n");
    }
}
