package org.sang.bean;

import java.sql.Timestamp;
import java.util.List;

public class CommentVo {
    private long id;

    private String nickname;

    private long uid;

    private String content;

    private List<CommentVo> childrens;

    private String publishDate;

    private Integer level;

    private String toUserNickname;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<CommentVo> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<CommentVo> childrens) {
        this.childrens = childrens;
    }

    public String getpublishDate() {
        return publishDate;
    }

    public void setpublishDate(String createDate) {
        this.publishDate = createDate;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getToUserNickname() {
        return toUserNickname;
    }

    public void setToUserNickname(String toUserNickname) {
        this.toUserNickname = toUserNickname;
    }
}
