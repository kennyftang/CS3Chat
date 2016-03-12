import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;

//TODO: Add check for valid IP and Port
//TODO: Create server class and create the client listener thread
public class ChatClient extends JFrame {
    private JTextField chatInputField;
    private JTextArea chatBoxArea;
    private JButton sendButton;
    private String name;
    private ChatListener chatListenerObject;
    private Thread chatListener;
    private Boolean connected;

    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){

        }
        ChatClient client = new ChatClient();
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.setSize(400, 300);
        client.setVisible(true);
    }
    public ChatClient(){
        //Define components
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem setName;
        JMenuItem exit;
        JMenuItem font;
        JMenuItem connectItem;
        JDialog nameChangeDialog;
        JDialog connectDialog;
        JTextField newNameField;
        JTextField hostField;
        JTextField portField;
        JButton connectButton;
        JButton changeNameButton;
        JPanel buttonRow;
        JPanel inputButtons;
        JScrollPane textAreaScroller;
        //End define
        //Create components
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        setName = new JMenuItem("Change Name");
        exit = new JMenuItem("Exit");
        font = new JMenuItem("Change Font");
        connectItem = new JMenuItem("Connect");
        nameChangeDialog = new JDialog(this);
        connectDialog = new JDialog(this);
        newNameField = new JTextField();
        hostField = new JTextField();
        portField = new JTextField();
        connectButton = new JButton("Connect");
        changeNameButton = new JButton("Change Name");
        inputButtons = new JPanel(); //Panel that includes input text field and buttons
        buttonRow = new JPanel(); //Adds the button panel to the panel containing input and buttons
        chatInputField = new JTextField(); //Creates the input text field
        sendButton = new JButton("Send"); //Create the send button in the row of buttons
        chatBoxArea = new JTextArea(); //Creates the main chat box
        textAreaScroller = new JScrollPane(chatBoxArea); //Wraps the text area in a scroll pane
        name = "ChatClient";
        //End create
        //Set properties
//        connectItem.setMaximumSize(new Dimension(connectItem.getPreferredSize().width, connectItem.getMaximumSize().height));
        nameChangeDialog.setLayout(new FlowLayout());
        connectDialog.setLayout(new BoxLayout(connectDialog.getContentPane(), BoxLayout.X_AXIS));
        newNameField.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        newNameField.setText("Enter name here");
        newNameField.setPreferredSize(new Dimension(150, 20));
        this.setLayout(new BorderLayout()); //Make a border layout for the chat client
        inputButtons.setLayout(new BoxLayout(inputButtons, BoxLayout.Y_AXIS)); //Create a vertical box layout
        buttonRow.setLayout(new BorderLayout()); //Set the layout of the row of butotns to BorderLayout
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
        exit.addActionListener((ActionEvent e) -> {
            this.dispose();
        });
        setName.addActionListener((ActionEvent e) -> {
            nameChangeDialog.setSize(400, 80);
            nameChangeDialog.setVisible(true);
            nameChangeDialog.transferFocusBackward();
        });
        connectItem.addActionListener((ActionEvent e) -> {
            connectDialog.setSize(400, 80);
            connectDialog.setVisible(true);
        });
        newNameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                newNameField.setText("");
                newNameField.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 10));
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        changeNameButton.addActionListener((ActionEvent e) -> {
            if(newNameField.getText().equals("Enter name here"))
                return;
            name = newNameField.getText();
            nameChangeDialog.dispose();
        });
        sendButton.addActionListener((ActionEvent e) -> {
            onClickSend();
        });
        chatInputField.addActionListener((ActionEvent e) -> {
            onClickSend();
        });
        connectButton.addActionListener((ActionEvent e) -> {
            if(connected == null){
                connected = false;
            }
            if(connected){
                try{
                    chatListener.join();
                    chatBoxArea.append("Disconnected\r\n");
                } catch (Exception e2){
                    e2.printStackTrace();
                }
            } else {
                Socket connection;
                try{
                    chatBoxArea.append("Trying on " + hostField.getText() + ":" + portField.getText() + "\r\n");;
                    connection = new Socket(hostField.getText(), Integer.valueOf(portField.getText()));
                    connectDialog.dispose();
                    chatBoxArea.append("Connected\r\n");
                    connected = true;
                } catch (Exception e2){
                    chatBoxArea.append("Failed to connect, invalid IP or port\r\n");
                    connectDialog.dispose();
                    connected = false;
                    return;
                }
                chatListenerObject = new ChatListener(connection, chatBoxArea, name);
                chatListener = new Thread(chatListenerObject);
                chatListener.start();
                connectItem.setText("Disconnect");
            }
        });
        //End add
        this.pack();
    }
    private void onClickSend(){
        if(chatInputField.getText().isEmpty() || chatListener == null)
            return;
        chatBoxArea.append(name + ": " + chatInputField.getText() + "\r\n");
        chatListenerObject.sendMessage(chatInputField.getText());
        chatInputField.setText("");
    }
}
