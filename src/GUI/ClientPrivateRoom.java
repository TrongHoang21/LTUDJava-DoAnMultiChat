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
    private String nameSource;

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

                    //Save data
                    listChatLog.add(msg);
                }
            }
        });

        this.getBtnBack().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                c.getMainGUI().setVisible(true);
                boxMsg.setText("");
                boxChatLog.setText("");

                //save data
                //c.saveChatLog(nameDest, listChatLog);
            }
        });
    }

    public void preloadChatLog(Client c){
        //Preload chat log
        for(int i = 0; i< c.getListPartner().size(); i++) {
            System.out.println(c.getListPartner().get(i).getPartnerName());
            if(nameDest.equals(c.getListPartner().get(i).getPartnerName())) {
                listChatLog = c.getListPartner().get(i).getListChatLog();
            }
        }

        String data = "";
        for(String msg : listChatLog){
            data += msg;
            data += "\n";
        }

        String finalData = data;
        System.out.println(data);

        boxChatLog.setText("");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showMsgOnScreen(finalData);
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

                //Save data
                listChatLog.add(content);
                break;
        }

    }

    public void setNewNameDest(String nameDst)
    {
        labelDestination.setText(nameDst);
        this.nameDest = nameDst;
    }
    public void setNewNameSrc(String nameSrc)
    {
        labelSource.setText(nameSrc);
        this.nameSource = nameSrc;
    }

    public String getBoxMsg() {
        return boxMsg.getText();
    }

    public void setBoxMsg(String t) {
        this.boxMsg.setText(t);
    }

    public void setBoxChatLog(String t) {
        this.boxChatLog.setText(t);
    }

    public JButton getBtnSend() {
        return btnSend;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public ArrayList<String> getListChatLog() {
        return listChatLog;
    }

    public void setListChatLog(ArrayList<String> listChatLog) {
        this.listChatLog = listChatLog;
    }

    public void showMsgOnScreen(String msg){
        SwingUtilities.invokeLater(new Runnable() { //invokeAndWait() is meant to be called from the non-GUI thread
            @Override
            public void run() {
                boxChatLog.append("\n");
                boxChatLog.append(msg);
            }
        });
    }
}
