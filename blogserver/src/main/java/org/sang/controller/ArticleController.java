package org.sang.controller;

import org.apache.commons.io.IOUtils;
import org.sang.bean.Article;
import org.sang.bean.Direction;
import org.sang.bean.RespBean;
import org.sang.service.ArticleService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sang on 2017/12/20.
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    ArticleService articleService;

    @RequestMapping(value = "/uploadimg", method = RequestMethod.POST)
    public RespBean uploadImg(HttpServletRequest req, MultipartFile image) {
        StringBuffer url = new StringBuffer();
        String filePath = "/blogimg/" + sdf.format(new Date());
        String imgFolderPath = req.getServletContext().getRealPath(filePath);
        File imgFolder = new File(imgFolderPath);
        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }
        url.append(req.getScheme())
                .append("://")
                .append(req.getServerName())
                .append(":")
                .append(req.getServerPort())
                .append(req.getContextPath())
                .append(filePath);
        String imgName = UUID.randomUUID() + "_" + image.getOriginalFilename().replaceAll(" ", "");
        try {
            IOUtils.write(image.getBytes(), new FileOutputStream(new File(imgFolder, imgName)));
            url.append("/").append(imgName);
            return new RespBean("success", url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RespBean("error", "上传失败!");
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Map<String, Object> getArticleByState(@RequestParam(value = "state", defaultValue = "-1") Integer state, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count, String keywords, String nickname, Integer type, String author, String conference, String direction) {
        int totalCount = articleService.getArticleCountByState(state, Util.getCurrentUser().getId(),keywords,nickname,type,author,conference,direction);
        List<Article> articles = articleService.getArticleByState(state, page, count,keywords,nickname,type,author,conference,direction);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", totalCount);
        map.put("articles", articles);
        return map;
    }

    @RequestMapping(value = "/getAllArticles", method = RequestMethod.GET)
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @RequestMapping(value = "/addNew", method = RequestMethod.POST)
    public RespBean addNewArticle(Article article) {
        int result1 = articleService.addNewArticle(article);
        if (result1 != 1) {
            return new RespBean("error", "Upload failed!");
        }
        Long aid = article.getId();
        List<Long> referenceList = article.getReferenceList();
        int result2 = articleService.addReference(aid,referenceList);
        if (result2 != 1) {
            return new RespBean("error","Set reference thesis failed!");
        }
        String note = article.getNote();
        int result3 = articleService.addNote(aid, note);
        if (result3 != 1) {
            return new RespBean("error","Upload note failed!");
        }
        return new RespBean("success","Upload succeeded!");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RespBean updateArticle(Article article) {
        int result = articleService.updateArticle(article);
        if (result == 1) {
            return new RespBean("success", "update succeeded!");
        } else {
            return new RespBean("error","update failed!");
        }
    }
    @RequestMapping(value = "/{aid}", method = RequestMethod.GET)
    public Article getArticleById(@PathVariable Long aid) {
        return articleService.getArticleById(aid);
    }

    @RequestMapping(value = "/dustbin", method = RequestMethod.PUT)
    public RespBean updateArticleState(Long[] aids, Integer state) {
        int result = articleService.updateArticleState(aids, state);
        if ( result == aids.length) {
            return new RespBean("success", "删除成功!");
        } else if (result == -1) {
            return new RespBean("error", "有选中的论文已被引用，无法删除！");
        } else if (result == -2) {
            return new RespBean("error", "有选中的论文引用删除失败，论文删除失败！请联系管理员！");
        } else if (result == -3) {
            return new RespBean("error", "有选中的论文笔记删除失败，论文永久删除失败！请联系管理员！");
        }
        return new RespBean("error", "删除失败!");
    }

    @RequestMapping(value = "/restore", method = RequestMethod.PUT)
    public RespBean restoreArticle(Long articleId) {
        int r = articleService.restoreArticle(articleId);
        if ( r == 1) {
            return new RespBean("success", "还原成功!");
        } else if ( r==0 ) {
            return new RespBean("error", "还原失败!");
        } else {  //r==2
            return new RespBean("error", "还原失败! 权限不足！");
        }
    }

    @RequestMapping(value = "/dataStatistics/{uid}", method = RequestMethod.GET)
    public Map<String, Object> dataStatistics(@PathVariable Long uid) {
        Map<String, Object> map = new HashMap<>();
        //add uid
        List<Direction> dataStatistics = articleService.getUserCountByDirection(uid);
        List<String> directionName = new ArrayList<>();
        List<Integer> directionCount = new ArrayList<>();
        int totalCount = 0;
        for (Direction d: dataStatistics) {
            totalCount ++;
            directionName.add(d.getDirectionName());
            directionCount.add(d.getDirectionCount());
        }
        map.put("directionName", directionName);
        map.put("directionCount", directionCount);
        map.put("totalCount",totalCount);
        return map;
    }
}
