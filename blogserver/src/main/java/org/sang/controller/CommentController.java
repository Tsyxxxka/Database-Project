package org.sang.controller;

import org.sang.bean.CommentVo;
import org.sang.bean.Comments;
import org.sang.bean.Direction;
import org.sang.bean.RespBean;
import org.sang.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//数据库还未添加articleId的外键
//前端最好将评论和评论的评论分开来
import java.util.List;
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentsService;


    @RequestMapping(value = "/{articleId}", method = RequestMethod.GET)
    public List<CommentVo> getAllComment(@PathVariable long articleId) {
        return commentsService.commentsByArticleId(articleId);
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RespBean addComment(Comments comment){
        if ("".equals(comment.getContent())) {
            return new RespBean("error", "请输入评论内容!");
        }
        int result = commentsService.comment(comment);
        if(result == 1)
            return new RespBean("success", "添加成功!");
        else
            return new RespBean("error", "添加失败!");
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public RespBean updateComment(Comments comment){
        int result = commentsService.changecomment(comment);
        if(result == 1) {
            return new RespBean("success", "修改成功!");
        }
        else if(result == 2) {
            return new RespBean("error", "修改失败，无权限!");
        }
        else {
            return new RespBean("error", "修改失败!");
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public RespBean deleteById(@PathVariable long id) {
        int result = commentsService.deleteCommentByIds(id);
        if (result==1) {
            return new RespBean("success", "删除成功!");
        }
        if (result==2) {
            return new RespBean("error", "删除失败,权限不足!");
        }
        return new RespBean("error", "删除失败!");
    }


}
