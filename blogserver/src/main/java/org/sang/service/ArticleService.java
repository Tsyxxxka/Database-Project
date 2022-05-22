package org.sang.service;

import org.sang.bean.Article;
import org.sang.bean.Direction;
import org.sang.mapper.ArticleMapper;
import org.sang.mapper.DirectionMapper;
import org.sang.mapper.TagsMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
    TagsMapper tagsMapper;
    @Autowired
    DirectionMapper directionMapper;

    public int addNewArticle(Article article) {
        String directionName = article.getDirectionName();
        List<Direction> allDirections = directionMapper.getAllDirection();
        for (Direction d : allDirections) {
            if (directionName.equals(d.getDirectionName())) {
                article.setDirection(d.getId());
                break;
            }
        }
        article.setEditTime(new Timestamp(System.currentTimeMillis()));
        article.setUid(Util.getCurrentUser().getId());
        article.setCommentCounts(0);
        int i = articleMapper.addNewArticle(article);
        return i;
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
        String directionName = article.getDirectionName();
        List<Direction> allDirections = directionMapper.getAllDirection();
        for (Direction d : allDirections) {
            if (directionName.equals(d.getDirectionName())) {
                article.setDirection(d.getId());
                break;
            }
        }
        int i = articleMapper.updateArticle(article);
        return i;
    }
    /*public int addNewArticle(Article article) {
        //处理文章摘要
        if (article.getSummary() == null || "".equals(article.getSummary())) {
            //直接截取
            String stripHtml = stripHtml(article.getHtmlContent());
            article.setSummary(stripHtml.substring(0, stripHtml.length() > 50 ? 50 : stripHtml.length()));
        }
        if (article.getId() == -1) {
            //添加操作
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (article.getState() == 1) {
                //设置发表日期
                article.setPublishDate(timestamp);
            }
            article.setEditTime(timestamp);
            //设置当前用户
            article.setUid(Util.getCurrentUser().getId());
            int i = articleMapper.addNewArticle(article);
            //打标签
            String[] dynamicTags = article.getDynamicTags();
            if (dynamicTags != null && dynamicTags.length > 0) {
                int tags = addTagsToArticle(dynamicTags, article.getId());
                if (tags == -1) {
                    return tags;
                }
            }
            return i;
        } else {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (article.getState() == 1) {
                //设置发表日期
                article.setPublishDate(timestamp);
            }
            //更新
            article.setEditTime(new Timestamp(System.currentTimeMillis()));
            int i = articleMapper.updateArticle(article);
            //修改标签
            String[] dynamicTags = article.getDynamicTags();
            if (dynamicTags != null && dynamicTags.length > 0) {
                int tags = addTagsToArticle(dynamicTags, article.getId());
                if (tags == -1) {
                    return tags;
                }
            }
            return i;
        }
    }*/

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
        if (state == 2) {
            // delete note
            int result1 = articleMapper.deleteNoteByArticleId(aids);
            if (result1 != 1) {
                return -3;
            }
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

    public int restoreArticle(Integer articleId) {
        return articleMapper.updateArticleStateById(articleId, 1); // 从回收站还原在原处
    }

    public Article getArticleById(Long aid) {
        String note = articleMapper.getNoteByAid(aid);
        Article article = articleMapper.getArticleById(aid);
        articleMapper.pvIncrement(aid);
        article.setNote(note);
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
}
