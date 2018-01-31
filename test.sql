/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50553
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50553
File Encoding         : 65001

Date: 2018-01-31 23:28:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for photo
-- ----------------------------
DROP TABLE IF EXISTS `photo`;
CREATE TABLE `photo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(1) NOT NULL,
  `description` text NOT NULL,
  `filename` varchar(255) NOT NULL,
  `views` int(11) NOT NULL,
  `isPublished` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of photo
-- ----------------------------
INSERT INTO `photo` VALUES ('1', 'M', 'I am near polar bears', 'photo-with-bears.jpg', '1', '1');
INSERT INTO `photo` VALUES ('2', 'M', 'I am near polar bears', 'photo-with-bears.jpg', '1', '1');

-- ----------------------------
-- Table structure for t_department
-- ----------------------------
DROP TABLE IF EXISTS `t_department`;
CREATE TABLE `t_department` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjoxpd0y26uhuy0j085jvqmlo8` (`parent_id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_department
-- ----------------------------
INSERT INTO `t_department` VALUES ('1', '顶级部门，不可修改', null);
INSERT INTO `t_department` VALUES ('9', '0', '1');
INSERT INTO `t_department` VALUES ('10', '1', '1');
INSERT INTO `t_department` VALUES ('11', '23', '1');
INSERT INTO `t_department` VALUES ('12', 'sadfas', '11');

-- ----------------------------
-- Table structure for t_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permissionname` varchar(255) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1rwkq1j8auw69w9t99r1wsamf` (`role_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_permission
-- ----------------------------

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rolename` varchar(255) DEFAULT NULL,
  `max` int(11) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
  `department_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK76b2dy511g6mxmmkl8d8vryhk` (`department_id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('3', null, '1', 'ccc', '10');
INSERT INTO `t_role` VALUES ('4', null, '5', 'ff', '10');
INSERT INTO `t_role` VALUES ('5', null, '1', 'test', '10');
INSERT INTO `t_role` VALUES ('6', null, '1', 'ff', '11');
INSERT INTO `t_role` VALUES ('7', null, '1', 'cc', '11');
INSERT INTO `t_role` VALUES ('9', null, '1', 'ee', '9');
INSERT INTO `t_role` VALUES ('10', null, '1', '粗鄙', '12');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `add_time` varchar(255) DEFAULT NULL,
  `baned` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', 'c81e728d9d4c2f636f067f89cc14862c', '1', '2018-01-09 22:28:50', '\0');
INSERT INTO `t_user` VALUES ('9', 'd41d8cd98f00b204e9800998ecf8427e', '2', '2018-01-09 23:12:24', '\0');
INSERT INTO `t_user` VALUES ('10', 'd41d8cd98f00b204e9800998ecf8427e', 'test', '2018-01-11 17:18:42', '');

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `role_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `FKq5un6x7ecoef5w1n39cop66kl` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES ('6', '10');
INSERT INTO `t_user_role` VALUES ('7', '10');

-- ----------------------------
-- Table structure for t_work
-- ----------------------------
DROP TABLE IF EXISTS `t_work`;
CREATE TABLE `t_work` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `work_id` int(11) DEFAULT NULL,
  `node_list` text,
  PRIMARY KEY (`id`),
  KEY `FKgynp7sffhox5l3v4mud8x10qe` (`work_id`)
) ENGINE=MyISAM AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_work
-- ----------------------------
INSERT INTO `t_work` VALUES ('39', '2', '1', null, null);
INSERT INTO `t_work` VALUES ('40', '2', '1', null, '[{\"fields\":[{\"name\":\"\",\"value\":\"\",\"required\":false},{\"name\":\"\",\"items\":[],\"required\":false}],\"type\":\"ziliao\"},{\"num\":1,\"type\":\"shenhe\",\"passNum\":1}]');
INSERT INTO `t_work` VALUES ('41', '23', '13', null, '[{\"fields\":[{\"name\":\"4\",\"type\":\"text\",\"value\":\"\",\"required\":false},{\"name\":\"\",\"type\":\"radio\",\"items\":[\"\"],\"required\":false},{\"name\":\"\",\"type\":\"text\",\"value\":\"\",\"required\":false}],\"type\":\"ziliao\"},{\"num\":1,\"type\":\"shenhe\",\"passNum\":1}]');

-- ----------------------------
-- Table structure for t_work_node
-- ----------------------------
DROP TABLE IF EXISTS `t_work_node`;
CREATE TABLE `t_work_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `work_id` int(11) DEFAULT NULL,
  `data` text,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8o0boco6i701kgrml07cyk8h3` (`work_id`)
) ENGINE=MyISAM AUTO_INCREMENT=69 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of t_work_node
-- ----------------------------
INSERT INTO `t_work_node` VALUES ('68', '39', '{\"fields\":[{\"name\":\"123\",\"required\":false,\"type\":\"text\",\"value\":\"\"}],\"type\":\"ziliao\"}', null);
INSERT INTO `t_work_node` VALUES ('66', '38', '{\"fields\":[{\"name\":\"\",\"required\":false,\"type\":\"text\",\"value\":\"\"}],\"type\":\"ziliao\"}', null);
INSERT INTO `t_work_node` VALUES ('67', '38', '{\"num\":1,\"passNum\":1,\"type\":\"shenhe\"}', null);
