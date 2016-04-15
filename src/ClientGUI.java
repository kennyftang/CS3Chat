import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by kenny on 4/12/16.
 */
public class ClientGUI extends JFrame{
    private JTextField chatInputField;
    private JTextField newNameField;
    private JTextArea chatBoxArea;
    private JMenuItem connectItem;
    private java.util.List<String> history;
    private int curHistory;
    private Client clientObject;
    private Thread chatThread;
    private String name;
    public static void main(String[] args){
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
            e.printStackTrace();
        }
        ClientGUI client = new ClientGUI();
        client.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        client.setSize(400, 300);
        client.setVisible(true);
    }

    public ClientGUI(){
        //Define components
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem setName = new JMenuItem("Change Name");
        JMenuItem exit = new JMenuItem("Exit");
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
        history = new LinkedList<>();
        curHistory = 0;
        history.add("");
        name = "";
        //End Definitions

        //Set properties
        nameChangeDialog.setLayout(new FlowLayout());
        connectDialog.setLayout(new BoxLayout(connectDialog.getContentPane(), BoxLayout.X_AXIS));
        //newNameField.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        newNameField.setText("");
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
                clientObject.stop();
        });
        connectButton.addActionListener((ActionEvent e) -> {
            try{
                pushLocalMessage("Trying on " + hostField.getText() + ":" + portField.getText());
                connectDialog.dispose();
                clientObject = new Client(hostField.getText(), Integer.valueOf(portField.getText()), this);
                connectItem.setText("Disconnect");
                pushLocalMessage("Connected");
                chatThread = new Thread(clientObject);
                chatThread.start();
            } catch (Exception e2){
                pushLocalMessage("Failed to connect, invalid IP or port");
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
            clientObject.send("SETHANDLE " + newNameField.getText());
            nameChangeDialog.dispose();
        });
        chatInputField.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getExtendedKeyCode() == KeyEvent.VK_UP){
//                    if(chatInputField.getText().isEmpty())
//                        curHistory++;
                    chatInputField.setText(getHistory(true));
                } else if (e.getExtendedKeyCode() == KeyEvent.VK_DOWN){
//                    if(chatInputField.getText().isEmpty())
//                        curHistory--;
                    chatInputField.setText(getHistory(false));
                }
            }
        });
        exit.addActionListener((ActionEvent e) -> System.exit(0));
        sendButton.addActionListener((ActionEvent e) -> sendInput());
        chatInputField.addActionListener((ActionEvent e) -> sendInput());
        portField.addActionListener(connectButton.getActionListeners()[0]);
        hostField.addActionListener(connectButton.getActionListeners()[0]);
        newNameField.addActionListener(changeNameButton.getActionListeners()[0]);
        //End add
        this.pack();

    }
    private void sendInput(){
        if(!chatInputField.getText().isEmpty())
            history.add(1, connectItem.getText());
        else
            return;
        clientObject.send(name.length() +" " + name + chatInputField.getText());
        chatInputField.setText("");
    }
    private String getHistory(boolean up){
        System.out.println("curHistory: " + curHistory + " history.size(): " + history.size() + " history: " + history);
        if(up) {
            if (curHistory > history.size() - 2) {
                return history.get(curHistory = history.size() - 1);
            } else {
                return history.get(++curHistory);
            }
        } else {
            if(curHistory < 1){
                return history.get(curHistory = 0);
            } else {
                return history.get(--curHistory);
            }
        }
    }
    public void pushLocalMessage(String message){
        chatBoxArea.append("LOCAL>" + message);
    }
    private void pushLiteralMessage(String message){
        chatBoxArea.append(message);
    }
    public void setName(String name){
        this.name = name;
    }
}
