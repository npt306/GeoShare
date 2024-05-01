package com.example.geoshare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CommunityGroup {
    private String groupID;
    private String groupName;
    private String groupDescription;
    private String groupImageURL;
    private ArrayList<String> membersList;
    public CommunityGroup() {

    }
    public CommunityGroup(String groupName, String groupDescription) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupImageURL = "default";
        this.membersList = new ArrayList<>(Arrays.asList("empty"));
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setGroupImageURL(String groupImageURL) {
        this.groupImageURL = groupImageURL;
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
        return Objects.equals(membersList.get(0), "empty");
    }
    public void addNewMember(String newMemberID) {
        if(groupIsEmpty()) {
            this.membersList.set(0, newMemberID);
        }else {
            this.membersList.add(newMemberID);
        }
    }
    public void removeMember(String memberID) {
        this.membersList.remove(memberID);
        if(this.membersList.isEmpty()) {
            this.membersList.add("empty");
        }
    }
    public String getMembersListCount() {
        if(groupIsEmpty()) {
            return "0";
        }else {
            return String.valueOf(this.membersList.size());
        }
    }
    public boolean isInCommunity(String memberID) {
        return this.membersList.contains(memberID);
    }
}
