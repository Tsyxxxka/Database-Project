/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : localhost:3306
 Source Schema         : vueblog2

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 19/05/2022 23:42:34
*/


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for article
-- ----------------------------

 DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
 `id` int NOT NULL AUTO_INCREMENT,
 `title` varchar(255) DEFAULT NULL,
 `summary` text,
 `uid` int DEFAULT NULL COMMENT '发布人',
 `publishDate` datetime DEFAULT NULL COMMENT '发表时间',
 `editTime` datetime DEFAULT NULL COMMENT '上传日期或编辑日期',
 `state` int DEFAULT NULL COMMENT '1表示已发表，2表示已删除',
 `pageView` int DEFAULT '0' COMMENT '浏览次数',
 `author` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '论文作者',
 `conference` varchar(255) DEFAULT NULL,
 `link` varchar(255) DEFAULT NULL,
 `type` int DEFAULT NULL,
 `direction` int DEFAULT NULL,
 PRIMARY KEY (`id`),
 KEY `uid` (`uid`),
 KEY `direction` (`direction`),
 CONSTRAINT `article_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`id`),
 CONSTRAINT `article_ibfk_3` FOREIGN KEY (`direction`) REFERENCES `search_direction` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=159 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of article
-- ----------------------------

-- ----------------------------
-- Table structure for note
-- ----------------------------

CREATE TABLE `note` (
 `id` int NOT NULL AUTO_INCREMENT,
 `uid` int DEFAULT NULL,
 `content` longtext,
 `uploadTime` datetime DEFAULT NULL,
 `aid` int DEFAULT NULL,
 PRIMARY KEY (`id`),
 KEY `uid` (`uid`),
 KEY `aid` (`aid`),
 CONSTRAINT `note_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`id`),
 CONSTRAINT `note_ibfk_2` FOREIGN KEY (`aid`) REFERENCES `article` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of note
-- ----------------------------
-- ----------------------------
-- Table structure for reference
-- ----------------------------

CREATE TABLE `reference` (
 `id` int NOT NULL,
 `rid` int NOT NULL,
 PRIMARY KEY (`id`,`rid`),
 KEY `rid` (`rid`),
 CONSTRAINT `reference_ibfk_1` FOREIGN KEY (`id`) REFERENCES `article` (`id`),
 CONSTRAINT `reference_ibfk_2` FOREIGN KEY (`rid`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


-- ----------------------------
-- Records of reference
-- ----------------------------


-- ----------------------------
-- Table structure for article_tags
-- ----------------------------
DROP TABLE IF EXISTS `article_tags`;
CREATE TABLE `article_tags`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `aid` int(0) NULL DEFAULT NULL,
  `tid` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `tid`(`tid`) USING BTREE,
  INDEX `article_tags_ibfk_1`(`aid`) USING BTREE,
  CONSTRAINT `article_tags_ibfk_1` FOREIGN KEY (`aid`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `article_tags_ibfk_2` FOREIGN KEY (`tid`) REFERENCES `tags` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_tags
-- ----------------------------
INSERT INTO `article_tags` VALUES (26, 116, 42);
INSERT INTO `article_tags` VALUES (27, 116, 44);
INSERT INTO `article_tags` VALUES (28, 116, 35);
INSERT INTO `article_tags` VALUES (29, 118, 45);
INSERT INTO `article_tags` VALUES (32, 119, 40);
INSERT INTO `article_tags` VALUES (33, 119, 41);
INSERT INTO `article_tags` VALUES (36, 109, 35);
INSERT INTO `article_tags` VALUES (37, 109, 50);
INSERT INTO `article_tags` VALUES (38, 109, 51);
INSERT INTO `article_tags` VALUES (39, 110, 36);
INSERT INTO `article_tags` VALUES (48, 108, 33);
INSERT INTO `article_tags` VALUES (49, 108, 34);
INSERT INTO `article_tags` VALUES (50, 120, 66);
INSERT INTO `article_tags` VALUES (51, 120, 65);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `cateName` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `date` date NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (56, 'Vue22', '2017-12-21');
INSERT INTO `category` VALUES (58, '人生感悟', '2017-12-21');
INSERT INTO `category` VALUES (60, 'JavaEE', '2017-12-21');
INSERT INTO `category` VALUES (61, 'Git', '2017-12-21');
INSERT INTO `category` VALUES (62, 'Linux', '2017-12-21');
INSERT INTO `category` VALUES (64, 'MongoDB', '2017-12-23');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `noteid` int(0) NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `publishDate` datetime(0) NULL DEFAULT NULL,
  `parentId` int(0) NULL DEFAULT NULL,
  `uid` int(0) NULL DEFAULT NULL,
  `toUid` int(0) NULL DEFAULT NULL,
  `level` tinyint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `aid`(`noteid`) USING BTREE,
  INDEX `uid`(`uid`) USING BTREE,
  INDEX `parentId`(`parentId`) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`noteid`) REFERENCES `article` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_3` FOREIGN KEY (`parentId`) REFERENCES `comments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for pv
-- ----------------------------
DROP TABLE IF EXISTS `pv`;
CREATE TABLE `pv`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `countDate` date NULL DEFAULT NULL,
  `pv` int(0) NULL DEFAULT NULL,
  `uid` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pv_ibfk_1`(`uid`) USING BTREE,
  CONSTRAINT `pv_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pv
-- ----------------------------
INSERT INTO `pv` VALUES (1, '2017-12-24', 20, 6);
INSERT INTO `pv` VALUES (2, '2017-12-24', 14, 7);
INSERT INTO `pv` VALUES (4, '2017-12-25', 40, 6);
INSERT INTO `pv` VALUES (5, '2017-12-25', 23, 7);
INSERT INTO `pv` VALUES (6, '2017-12-26', 11, 6);
INSERT INTO `pv` VALUES (7, '2017-12-26', 32, 7);
INSERT INTO `pv` VALUES (26, '2017-12-23', 2, 6);
INSERT INTO `pv` VALUES (27, '2017-12-23', 77, 7);

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, '超级管理员');
INSERT INTO `roles` VALUES (2, '普通用户');
INSERT INTO `roles` VALUES (3, '测试角色1');
INSERT INTO `roles` VALUES (4, '测试角色2');
INSERT INTO `roles` VALUES (5, '测试角色3');

-- ----------------------------
-- Table structure for roles_user
-- ----------------------------
DROP TABLE IF EXISTS `roles_user`;
CREATE TABLE `roles_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `rid` int(0) NULL DEFAULT 2,
  `uid` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `rid`(`rid`) USING BTREE,
  INDEX `roles_user_ibfk_2`(`uid`) USING BTREE,
  CONSTRAINT `roles_user_ibfk_1` FOREIGN KEY (`rid`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `roles_user_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 131 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles_user
-- ----------------------------
INSERT INTO `roles_user` VALUES (8, 2, 7);
INSERT INTO `roles_user` VALUES (9, 1, 7);
INSERT INTO `roles_user` VALUES (17, 5, 7);
INSERT INTO `roles_user` VALUES (106, 2, 14);
INSERT INTO `roles_user` VALUES (108, 2, 16);
INSERT INTO `roles_user` VALUES (109, 2, 17);
INSERT INTO `roles_user` VALUES (110, 2, 18);
INSERT INTO `roles_user` VALUES (111, 2, 19);
INSERT INTO `roles_user` VALUES (112, 2, 20);
INSERT INTO `roles_user` VALUES (119, 2, 15);
INSERT INTO `roles_user` VALUES (120, 5, 15);
INSERT INTO `roles_user` VALUES (121, 2, 6);
INSERT INTO `roles_user` VALUES (123, 2, 13);
INSERT INTO `roles_user` VALUES (124, 3, 13);
INSERT INTO `roles_user` VALUES (128, 2, 10);
INSERT INTO `roles_user` VALUES (129, 5, 10);
INSERT INTO `roles_user` VALUES (130, 1, 6);

-- ----------------------------
-- Table structure for search_direction
-- ----------------------------
DROP TABLE IF EXISTS `search_direction`;

CREATE TABLE `search_direction` (
 `ID` int NOT NULL AUTO_INCREMENT,
 `directionName` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL,
 `parentID` int DEFAULT NULL,
 PRIMARY KEY (`ID`) USING BTREE,
 KEY `parentID` (`parentID`),
 CONSTRAINT `search_direction_ibfk_1` FOREIGN KEY (`parentID`) REFERENCES `search_direction` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of search_direction
-- ----------------------------

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `tagName` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tagName`(`tagName`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tags
-- ----------------------------
INSERT INTO `tags` VALUES (66, '666');
INSERT INTO `tags` VALUES (35, 'Ajax');
INSERT INTO `tags` VALUES (36, 'Dubbo');
INSERT INTO `tags` VALUES (40, 'git');
INSERT INTO `tags` VALUES (33, 'Linux');
INSERT INTO `tags` VALUES (45, 'mongodb');
INSERT INTO `tags` VALUES (42, 'spring');
INSERT INTO `tags` VALUES (44, 'SpringSecurity');
INSERT INTO `tags` VALUES (37, 'websocket');
INSERT INTO `tags` VALUES (34, 'Zookeeper');
INSERT INTO `tags` VALUES (50, '图片上传');
INSERT INTO `tags` VALUES (51, '图片预览');
INSERT INTO `tags` VALUES (41, '学习资料');
INSERT INTO `tags` VALUES (65, '杂谈');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `nickname` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `enabled` tinyint(1) NULL DEFAULT 1,
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `userface` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `regTime` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (6, 'linghu', '令狐葱', '202cb962ac59075b964b07152d234b70', 1, 'linghu@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920326&di=44a6fa6b597d86f475c2b15fa93008dd&imgtype=0&src=http%3A%2F%2Fwww.qqzhi.com%2Fuploadpic%2F2015-01-12%2F023019564.jpg', '2017-12-08 09:30:22', NULL);
INSERT INTO `user` VALUES (7, 'sang', '江南一点雨', '202cb962ac59075b964b07152d234b70', 1, 'sang123@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-21 13:30:29', NULL);
INSERT INTO `user` VALUES (10, 'qiaofeng', '乔峰', '202cb962ac59075b964b07152d234b70', 1, 'qiaofeng@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);
INSERT INTO `user` VALUES (13, 'duanzhengchun', '段正淳', '202cb962ac59075b964b07152d234b70', 0, 'duanzhengchun@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);
INSERT INTO `user` VALUES (14, 'chenjialuo', '陈家洛', '202cb962ac59075b964b07152d234b70', 0, 'chenjialuo@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);
INSERT INTO `user` VALUES (15, 'yuanchengzhi', '袁承志', '202cb962ac59075b964b07152d234b70', 1, 'yuanchengzhi@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);
INSERT INTO `user` VALUES (16, 'chuliuxiang', '楚留香', '202cb962ac59075b964b07152d234b70', 1, 'chuliuxiang@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);
INSERT INTO `user` VALUES (17, 'baizhantang', '白展堂', '202cb962ac59075b964b07152d234b70', 0, 'baizhantang@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);
INSERT INTO `user` VALUES (18, 'renwoxing', '任我行', '202cb962ac59075b964b07152d234b70', 1, 'renwoxing@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);
INSERT INTO `user` VALUES (19, 'zuolengchan', '左冷禅', '202cb962ac59075b964b07152d234b70', 1, 'zuolengchan@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);
INSERT INTO `user` VALUES (20, 'fengqingyang', '风清扬', '202cb962ac59075b964b07152d234b70', 1, 'fengqingyang@qq.com', 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514093920321&di=913e88c23f382933ef430024afd9128a&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F9771429316733.jpg', '2017-12-24 06:30:46', NULL);

-- ----------------------------
-- Table structure for user_code
-- ----------------------------
DROP TABLE IF EXISTS `user_code`;
CREATE TABLE `user_code`  (
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `trueCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- View structure for pvview
-- ----------------------------
DROP VIEW IF EXISTS `pvview`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `pvview` AS select sum(`pv`.`pv`) AS `pv`,`pv`.`uid` AS `uid` from `pv` group by `pv`.`uid`;

-- ----------------------------
-- View structure for totalpvview
-- ----------------------------
DROP VIEW IF EXISTS `totalpvview`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `totalpvview` AS select sum(`a`.`pageView`) AS `totalPv`,`a`.`uid` AS `uid` from `article` `a` group by `a`.`uid`;

SET FOREIGN_KEY_CHECKS = 1;
