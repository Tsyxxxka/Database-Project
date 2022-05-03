package org.sang.bean;

import java.sql.Timestamp;

public class Direction {
    private Long direction_id;
    private String direction_name;
    private Long parent_id;
/*
    public Direction() {
    }*/
    /*
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }*/

    public Long getId() {
        return direction_id;
    }

/*    public void setId(Long id) {
        this.direction_id = direction_id;
    }*/

    public String getDirectionName() {
        return direction_name;
    }

    public Long getParentId() {
        return parent_id;
    }
/*
    public void setCateName(String cateName) {
        this.cateName = cateName;
    }*/
}
