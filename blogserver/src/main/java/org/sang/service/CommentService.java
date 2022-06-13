package org.sang.service;

import org.sang.bean.*;
import org.sang.mapper.CommentMapper;
import org.sang.utils.Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.sang.utils.Util.getCurrentUser;
import static org.sang.utils.Util.getCurrentUsername;

@Service
@Transactional
public class CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserService UserService;

    public List<CommentVo> commentsByArticleId(Long articleId) {
        List<Comments> comments = commentMapper.findCommentByArticleIdLevel(articleId,1);
        List<CommentVo> commentVoList = copyList(comments);
        return commentVoList;
    }
    public int comment(Comments comment) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        comment.setPublishDate(formatter.format(date));
        //Timestamp timestamp = new Timestamp(date.getTime());
        //comment.setPublishDate(timestamp);
        comment.setUid(Util.getCurrentUser().getId());
        //comment.setUid(7);
        //空的comment记录（Id=26）
        if (comment.getParentId()==1) {
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        return  commentMapper.addNewComment(comment);
    }


    public int changecomment(Comments comment) {
        User user = getCurrentUser();
        Comments comment1 = commentMapper.findCommentById(comment.getId());
        if(user.getId()==comment1.getUid()){
            int result = commentMapper.updateById(comment);
            return result;
        }
        else
            return 2;
    }

    public int deleteCommentByIds(long id) {
        User user = getCurrentUser();
        Comments comment = commentMapper.findCommentById(id);
        if(comment.getUid()==user.getId() || user.getAuth()!=0){
            if(comment.getLevel() == 1){
                List<Comments> comments = commentMapper.findByParentIdLevel(id,2);
                for (Comments comment1 : comments) {
                    commentMapper.deleteCommentByIds(comment1.getId());
                }
            }
            int result = commentMapper.deleteCommentByIds(id);
            return result;
        }
        else
            return 2;
    }

    private List<CommentVo> copyList(List<Comments> comments) {
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comments comment : comments) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comments comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setId(comment.getId());
        commentVo.setUid(comment.getUid());
        commentVo.setContent(comment.getContent());
        commentVo.setpublishDate(comment.getPublishDate());
        //作者信息
        User user = UserService.getUserById(comment.getUid());
        String nickname = user.getNickname();
        commentVo.setNickname(nickname);
        //子评论
        int level = comment.getLevel();
        if (1 == level){
            Long id = comment.getId();
            List<CommentVo> commentVoList = findCommentsByParentId(id);
            commentVo.setChildrens(commentVoList);
        }
        //to User 给谁评论
        if (level > 1){
            Long toUid = comment.getToUid();
            User toUser = this.UserService.getUserById(toUid);
            commentVo.setToUserNickname(toUser.getNickname());
        }
        return commentVo;
    }

    private List<CommentVo> findCommentsByParentId(Long id) {
        List<Comments> comments = commentMapper.findByParentIdLevel(id,2);
        return copyList(comments);
    }
}
