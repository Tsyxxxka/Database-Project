package org.sang.controller;

        import org.sang.bean.Direction;
        import org.sang.bean.RespBean;
        import org.sang.service.DirectionService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.PathVariable;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.RestController;

        import javax.swing.plaf.synth.SynthEditorPaneUI;
        import java.util.List;

@RestController
@RequestMapping("/direction")
public class DirectionController {
    @Autowired
    DirectionService directionService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Direction> getAllDirection() {
        return directionService.getAllDirection();
    }

    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    public RespBean deleteById(@PathVariable String ids) {
        boolean result = directionService.deleteDirectionByIds(ids);
        if (result) {
            return new RespBean("success", "删除成功!");
        }
        return new RespBean("error", "删除失败!");
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public RespBean addNewCate(Direction direction) {

        if ("".equals(direction.getDirectionName()) || direction.getDirectionName() == null) {
            return new RespBean("error", "请输入栏目名称!");
        }
        int result = directionService.addDirection(direction);

        if (result == 1) {
            return new RespBean("success", "添加成功!");
        }
        return new RespBean("error", "添加失败!");
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public RespBean updateDirection(Direction direction) {
        int i = directionService.updateDirectionById(direction);
        if (i == 1) {
            return new RespBean("success", "修改成功!");
        }
        return new RespBean("error", "修改失败!");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Direction findDirectionNameById(@PathVariable Long id) {
        return directionService.findDirectionNameById(id);
    }
}
