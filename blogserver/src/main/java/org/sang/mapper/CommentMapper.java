package org.sang.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Comments;

import java.util.List;
@Mapper
public interface CommentMapper {
    int addNewComment(Comments comment);
    List<Comments> findCommentByArticleIdLevel(long articleId,int level);
    List<Comments> findByParentIdLevel(long parentId,int level);
    Comments findCommentById(long id);
    int updateById (Comments comment);
    int deleteCommentByIds(@Param("id") long id);
}
