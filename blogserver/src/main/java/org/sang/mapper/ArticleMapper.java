package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Article;
import org.sang.bean.Direction;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sang on 2017/12/20.
 */
@Mapper
public interface ArticleMapper {
    int addNewArticle(Article article);

    int setArticleDirection(Long aid, Integer did);

    int updateArticle(Article article);

    List<Article> getArticleByState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count, @Param("uid") Long uid,@Param("keywords") String keywords, @Param("nickname") String nickname, @Param("type") Integer type, @Param("author") String author, @Param("conference") String conference, @Param("direction")String direction);

    List<Article> getAllArticles();
//    List<Article> getArticleByStateByAdmin(@Param("start") int start, @Param("count") Integer count, @Param("keywords") String keywords);

    int getArticleCountByState(@Param("state") Integer state, @Param("uid") Long uid, @Param("keywords") String keywords, @Param("nickname") String nickname, @Param("type") Integer type, @Param("author") String author, @Param("conference") String conference, @Param("direction")String direction);

    int updateArticleState(@Param("aids") Long aids[], @Param("state") Integer state);

    int updateArticleStateById(@Param("articleId") Long articleId, @Param("state") Integer state);

    int deleteArticleById(@Param("aids") Long[] aids);
    int getReferencedNumber(@Param("aids") Long[] aids);
    int getReferencingNumber(@Param("aids") Long[] aids);
    int deleteReference(@Param("aids") Long[] aids);
    int deleteNoteByArticleId(@Param("aids")Long[] aids);
    int deleteArticleDirection(@Param("aids")Long[] aids);
    int deleteArticleDirectionByOne(@Param("aid")Long aid);
    Article getArticleById(Long aid);

    String getNoteByAid(Long aid);
    String getReferenceByAid(Long aid);
    List<Integer> getArticleDirectionByAid(Long aid);
    void pvIncrement(Long aid);

    //INSERT INTO pv(countDate,pv,uid) SELECT NOW(),SUM(pageView),uid FROM article GROUP BY uid
    void pvStatisticsPerDay();

    List<String> getCategories(Long uid);

    List<Integer> getDataStatistics(Long uid);

    List<Direction> getUserCountByDirection(Long uid);

    int addReference(@Param("aid")Long aid, @Param("rid")Long reference);

    int addNote(@Param("aid") Long aid, @Param("note") String note, @Param("uid")Long uid, @Param("uploadTime")Timestamp uploadTime);

}
