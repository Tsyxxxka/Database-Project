package org.sang.service;

import org.sang.bean.Direction;
import org.sang.mapper.DirectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class DirectionService {
    @Autowired
    DirectionMapper directionMapper;

    public List<Direction> getAllDirection() {
        return directionMapper.getAllDirection();
    }

    public boolean deleteDirectionByIds(String ids) {
        String[] split = ids.split(",");
        int result = directionMapper.deleteDirectionByIds(split);
        return result == split.length;
    }

    public int updateDirectionById(Direction direction) {
        System.out.println(22222);
        System.out.println(direction);
        System.out.println(22222);
        return directionMapper.updateDirectionById(direction);
    }

    public int addDirection(Direction direction) {
        // direction.setDate(new Timestamp(System.currentTimeMillis()));
        return directionMapper.addDirection(direction);
    }
}
