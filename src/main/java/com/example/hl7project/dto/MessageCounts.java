package com.example.hl7project.dto;

public class MessageCounts {
    private int creationCount;
    private int modifyCount;
    private int noShowCount;

    public MessageCounts(int creationCount, int modifyCount, int noShowCount) {
        this.creationCount = creationCount;
        this.modifyCount = modifyCount;
        this.noShowCount = noShowCount;
    }

    // Getters and setters
    public int getCreationCount() {
        return creationCount;
    }

    public void setCreationCount(int creationCount) {
        this.creationCount = creationCount;
    }

    public int getModifyCount() {
        return modifyCount;
    }

    public void setModifyCount(int modifyCount) {
        this.modifyCount = modifyCount;
    }

    public int getNoShowCount() {
        return noShowCount;
    }

    public void setNoShowCount(int noShowCount) {
        this.noShowCount = noShowCount;
    }
}
