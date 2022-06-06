package org.sang.bean;

/**
 * Created by sang on 2017/12/17.
 */
public class Role { //id= 1:chaoji 2:guanli 3:putong
    private Long id;
    private String name;

    public Role() {
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role(Long id, String name) {

        this.id = id;
        this.name = name;
    }
}
