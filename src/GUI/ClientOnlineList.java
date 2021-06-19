package GUI;

import multiChat.Client;

import javax.swing.*;

import java.util.ArrayList;



public class ClientOnlineList extends JFrame {
    private JList listOnlineList;
    private JPanel panel1;
    private JButton btnUpdateList;
    private JLabel labelList;
    private JLabel labelThisClient;
    private JButton btnCreatePrivateRoom;


    public ClientOnlineList(Client c)
    {
        Client.sendOnlineListRequest();
        c.sendInfoRequest(c.getUserName());
        add(panel1);
        setSize(500,500);
        setVisible(true);
        updateOnlineListOnScreen(c.getCurrentOnlineList());
    }

    public void updateOnlineListOnScreen(ArrayList<String> list) {
        if (!list.isEmpty() && list != null) {
            DefaultListModel<String> l1 = new DefaultListModel<>();

            for (int i = 0; i < list.size(); i++)
                l1.addElement(list.get(i));

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listOnlineList.setModel(l1);
                }
            });
        }
    }

    public void updateInfoOnScreen(String nameSrc, String userName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                labelThisClient.setText("You are " + userName + " ("+ nameSrc + ")");
            }
        });

    }

    public JButton getBtnUpdateList() {
        return btnUpdateList;
    }

    public JButton getBtnCreatePrivateRoom() {
        return btnCreatePrivateRoom;
    }

    public JList getListOnlineList() {
        return listOnlineList;
    }
}
