package org.sang.service;

import org.sang.bean.Article;
import org.sang.bean.Comments;
import org.sang.bean.Direction;
import org.sang.bean.User;
import org.sang.mapper.ArticleMapper;
import org.sang.mapper.CommentMapper;
import org.sang.mapper.DirectionMapper;
import org.sang.mapper.TagsMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sang on 2017/12/20.
 */
@Service
@Transactional
public class ArticleService {
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    TagsMapper tagsMapper;
    @Autowired
    DirectionMapper directionMapper;

    public int addNewArticle(Article article) {
        article.setEditTime(new Timestamp(System.currentTimeMillis()));
        article.setUid(Util.getCurrentUser().getId());
        article.setCommentCounts(0);
        int i = articleMapper.addNewArticle(article);
        Long aid = article.getId();
        List<Integer> directionsId = article.getMultiDirection();
        for (Integer did : directionsId) {
            int r = setArticleDirection(aid, did);
            if (r != 1) {
                return -1;
            }
        }
        return i;
    }

    public int setArticleDirection(Long aid, Integer did) {
        return articleMapper.setArticleDirection(aid, did);
    }

    public int addReference(Long aid,List<Long> referenceList) {
        for (Long r : referenceList) {
            int i = articleMapper.addReference(aid,r);
            // UNDO the FINISHED insert
            if (i != 1) {
                return i;
            }
        }
        return 1;
    }

    public int addNote(Long aid, String note) {
        Long uid = Util.getCurrentUser().getId();
        Timestamp uploadTime = new Timestamp(System.currentTimeMillis());
        int i = articleMapper.addNote(aid,note,uid,uploadTime);
        return i;
    }
    public int updateArticle(Article article) {
        List<Integer> directionIds = article.getMultiDirection();
        Long aid = article.getId();
        articleMapper.deleteArticleDirectionByOne(aid);
        for (Integer i : directionIds) {
            articleMapper.setArticleDirection(aid,i);
        }
//        String directionName = article.getDirectionName();
//        List<Direction> allDirections = directionMapper.getAllDirection();
//        for (Direction d : allDirections) {
//            if (directionName.equals(d.getDirectionName())) {
//                article.setDirection(d.getId());
//                break;
//            }
//        }
        int i = articleMapper.updateArticle(article);
        return i;
    }

    private int addTagsToArticle(String[] dynamicTags, Long aid) {
        //1.删除该文章目前所有的标签
        tagsMapper.deleteTagsByAid(aid);
        //2.将上传上来的标签全部存入数据库
        tagsMapper.saveTags(dynamicTags);
        //3.查询这些标签的id
        List<Long> tIds = tagsMapper.getTagsIdByTagName(dynamicTags);
        //4.重新给文章设置标签
        int i = tagsMapper.saveTags2ArticleTags(tIds, aid);
        return i == dynamicTags.length ? i : -1;
    }

    public String stripHtml(String content) {
        content = content.replaceAll("<p .*?>", "");
        content = content.replaceAll("<br\\s*/?>", "");
        content = content.replaceAll("\\<.*?>", "");
        return content;
    }

    public List<Article> getArticleByState(Integer state, Integer page, Integer count,String keywords,String nickname, Integer type, String author, String conference, String direction) {
        int start = (page - 1) * count;
        Long uid = Util.getCurrentUser().getId();
        return articleMapper.getArticleByState(state, start, count, uid,keywords,nickname,type,author,conference,direction);
    }

    public List<Article> getAllArticles() {
        return articleMapper.getAllArticles();
    }
//    public List<Article> getArticleByStateByAdmin(Integer page, Integer count,String keywords) {
//        int start = (page - 1) * count;
//        return articleMapper.getArticleByStateByAdmin(start, count,keywords);
//    }

    public int getArticleCountByState(Integer state, Long uid,String keywords, String nickname, Integer type, String author, String conference, String direction) {
        return articleMapper.getArticleCountByState(state, uid,keywords,nickname,type,author,conference,direction);
    }
    public int updateArticleState(Long[] aids, Integer state) {
        if (state == 2) { //delete permanently
            User user = Util.getCurrentUser();
            if(user.getAuth()==0)
                return -4;
            //delete comments
            List<Comments> comments = commentMapper.findCommentByArticleId(aids);
            for (Comments comment1 : comments) {
                int result2 = commentService.deleteArticleCommentByIds(comment1.getId());
                if (result2==2)
                    return result2;
            }
            // delete note
            int result1 = articleMapper.deleteNoteByArticleId(aids);
            if (result1 != 1) {
                return -3;
            }
            //delete from article_direction
            articleMapper.deleteArticleDirection(aids);
            // delete from dustbin
            return articleMapper.deleteArticleById(aids);
        } else {
            // check being referenced
            int referenced = articleMapper.getReferencedNumber(aids);
            if(referenced != 0) {
                return -1;
            }
            // set state = 2
            int resullt = articleMapper.updateArticleState(aids, 2);
            // delete reference
            int referencing = articleMapper.getReferencingNumber(aids);
            int deleted = articleMapper.deleteReference(aids);
            if (referencing != deleted) {
                return -2;
            }
            return resullt;
        }
    }

    public int restoreArticle(Long articleId) {
        User user = Util.getCurrentUser();
        Article article = articleMapper.getArticleById(articleId);
        Long uid = article.getUid();
        if (user.getAuth()!=0 || user.getId()== uid) {
            return articleMapper.updateArticleStateById(articleId, 1); // 从回收站还原在原处
        }
        return 2;
    }

    public Article getArticleById(Long aid) {
        String note = articleMapper.getNoteByAid(aid);
        String referenceLinks = articleMapper.getReferenceByAid(aid);
        List<Integer> multiDirection = articleMapper.getArticleDirectionByAid(aid);
        Article article = articleMapper.getArticleById(aid);
        articleMapper.pvIncrement(aid);
        article.setNote(note);
        article.setReferenceLinks(referenceLinks);
        article.setMultiDirection(multiDirection);
        return article;
    }

    public void pvStatisticsPerDay() {
        articleMapper.pvStatisticsPerDay();
    }

    /**
     * 获取最近七天的日期
     * @return
     */
    public List<String> getCategories() {
        return articleMapper.getCategories(Util.getCurrentUser().getId());
    }

    /**
     * 获取最近七天的数据
     * @return
     */
    public List<Integer> getDataStatistics() {
        return articleMapper.getDataStatistics(Util.getCurrentUser().getId());
    }

    public List<Direction> getUserCountByDirection(Long uid) {
        Long id = uid;
        if (id==-1) {
            id = Util.getCurrentUser().getId();
        }
        return articleMapper.getUserCountByDirection(id);
    }
}