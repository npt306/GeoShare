package com.example.geoshare;

import java.util.ArrayList;
import java.util.Arrays;

public class CommunityGroup {
    private String groupID;
    private String groupName;
    private String groupDescription;
    private String groupImageURL;
    private ArrayList<String> membersList;
    public CommunityGroup() {

    }
    public CommunityGroup(String groupID, String groupName, String groupDescription, String groupImageURL) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupImageURL = groupImageURL;
        this.membersList = new ArrayList<>(Arrays.asList("empty"));
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public String getGroupImageURL() {
        return groupImageURL;
    }

    public ArrayList<String> getMembersList() {
        return membersList;
    }
    public boolean groupIsEmpty() {
        return membersList.get(0) == "empty";
    }
}
