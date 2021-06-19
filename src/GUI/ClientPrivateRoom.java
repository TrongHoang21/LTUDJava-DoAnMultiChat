package GUI;

import multiChat.Client;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientPrivateRoom extends JFrame {
    private JTextArea boxChatLog;
    private JButton btnBack;
    private JButton btnSend;
    private JTextArea boxMsg;
    private JLabel labelDestination;
    private JPanel panel1;
    private JLabel labelSource;
    private ArrayList<String> listChatLog;
    private String msgFromSocket;
    private String nameDest;

    public ClientPrivateRoom(String nameDst, String nameSrc, Client c)
    {
        this.nameDest = nameDst;
        listChatLog = new ArrayList<String>();
        labelDestination.setText("To: " + nameDest);
        labelSource.setText("From: " + nameSrc);
        boxChatLog.setText("");
        add(panel1);
        setSize(500,500);


        this.getBtnSend().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //sendMessage.start();

                String msg = getBoxMsg();
                setBoxMsg("");

                if (!msg.equals("")) {
                    String msgSent = "msg#" + nameDest + "#" + msg;
                    Client.Sender(msgSent);

                    msg = "Me: " + msg;
                    showMsgOnScreen(msg);

                }
            }
        });

        this.getBtnBack().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                c.getMainGUI().setVisible(true);
            }
        });


    }

    public void solveMessage(String received)
    {
        StringTokenizer st = new StringTokenizer(received, "#");
        String command = st.nextToken();
        String content = st.nextToken();

        switch (command){
            case "msg":
                showMsgOnScreen(content);
                break;
        }

    }

    public void setNewDestination(String nameDst)
    {
        labelDestination.setText(nameDst);
        this.nameDest = nameDst;
    }

    public String getBoxMsg() {
        return boxMsg.getText();
    }

    public void setBoxMsg(String t) {
        this.boxMsg.setText(t);
    }

    public JButton getBtnSend() {
        return btnSend;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public void showMsgOnScreen(String msg){
        listChatLog.add(msg);
        SwingUtilities.invokeLater(new Runnable() { //invokeAndWait() is meant to be called from the non-GUI thread
            @Override
            public void run() {
                boxChatLog.append("\n");
                boxChatLog.append(msg);
            }
        });

    }
}
