package org.sang.bean;

import java.sql.Timestamp;

public class Direction {
    private Long ID;
    private String directionName;
    private Long parentID;
/*
    public Direction() {
    }*/
    /*
    public Timestamp getDate() {
        return date;
    }
*/
    public void setId(Long id) {
        this.ID = id;
    }

    public Long getId() {
        return ID;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public String getDirectionName() {
        return directionName;
    }

    public Long getParentId() {
        return parentID;
    }

    public void setParentId(Long parentID) {
        this.parentID = parentID;
    }
}
