package org.sang.bean;

import java.sql.Timestamp;

public class Direction {
    private Integer ID;
    private String directionName;
    private Integer parentID;
/*
    public Direction() {
    }*/
    /*
    public Timestamp getDate() {
        return date;
    }
*/
    public void setId(Integer id) {
        this.ID = id;
    }

    public Integer getId() {
        return ID;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public String getDirectionName() {
        return directionName;
    }

    public Integer getParentId() {
        return parentID;
    }

    public void setParentId(Integer parentID) {
        this.parentID = parentID;
    }
}
