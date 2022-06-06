package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Direction;

import java.util.List;

@Mapper
public interface DirectionMapper {
    List<Direction> getAllDirection();

    int addReference(List<Integer> referenceList);
    int deleteDirectionByIds(@Param("ids") String[] ids);

    int updateDirectionById(Direction direction);

    int addDirection(Direction direction);

    Direction findDirectionNameById(Long id);

}
