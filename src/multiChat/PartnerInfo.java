package multiChat;

import java.util.ArrayList;

public class PartnerInfo
{
    private ArrayList<String> listChatLog;
    private String thisClientName;
    private String partnerName;
    private String partnerUserName;

    public PartnerInfo(String partnerName, String partnerUserName){
        this.partnerName = partnerName;
        this.partnerUserName = partnerUserName;
        listChatLog = new ArrayList<>();
    }

    public ArrayList<String> getListChatLog() {
        return listChatLog;
    }

    public void setListChatLog(ArrayList<String> listChatLog) {
        this.listChatLog = listChatLog;
    }

    public String getPartnerName() {
        return partnerName;
    }
}