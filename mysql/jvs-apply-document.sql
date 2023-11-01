/*
 Navicat Premium Data Transfer

 Source Server         : 160
 Source Server Type    : MySQL
 Source Server Version : 50739
 Source Host           : 10.0.0.160:3306
 Source Schema         : jvs

 Target Server Type    : MySQL
 Target Server Version : 50739
 File Encoding         : 65001

 Date: 05/09/2022 19:00:55
*/
create database `jvs-apply-document` default character set utf8mb4 collate utf8mb4_general_ci;
use jvs-apply-document;



SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dc_button_authority
-- ----------------------------
DROP TABLE IF EXISTS `dc_button_authority`;
CREATE TABLE `dc_button_authority`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `button_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮名称',
  `button_tag` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮标签或标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文档库功能按钮表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dc_button_role
-- ----------------------------
DROP TABLE IF EXISTS `dc_button_role`;
CREATE TABLE `dc_button_role`  (
  `dc_role_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文档库角色id',
  `button_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文档库按钮id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色与 按钮中间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dc_label
-- ----------------------------
DROP TABLE IF EXISTS `dc_label`;
CREATE TABLE `dc_label`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT ' 标签',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签图片地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dc_label
-- ----------------------------
INSERT INTO `dc_label` VALUES ('1', '部署问题', 'https://p1.kdvas.wpscdn.cn/kdvcover/file/b/2/c/7/8/eef9b77b2fde0953c98d205e72bd4a8b.svg');
INSERT INTO `dc_label` VALUES ('2', '项目管理问题1', 'https://p1.kdvas.wpscdn.cn/kdvcover/file/2/6/c/1/1/a67520006f71e5a7c07026db1a478d1d.svg');
INSERT INTO `dc_label` VALUES ('3', '项目管理问题2', 'https://p1.kdvas.wpscdn.cn/kdvcover/file/2/6/c/1/1/a67520006f71e5a7c07026db1a478d1d.svg');
INSERT INTO `dc_label` VALUES ('4', '项目管理问题3', 'https://p1.kdvas.wpscdn.cn/kdvcover/file/2/6/c/1/1/a67520006f71e5a7c07026db1a478d1d.svg');
INSERT INTO `dc_label` VALUES ('5', '项目管理问题4', 'https://p1.kdvas.wpscdn.cn/kdvcover/file/2/6/c/1/1/a67520006f71e5a7c07026db1a478d1d.svg');

-- ----------------------------
-- Table structure for dc_library
-- ----------------------------
DROP TABLE IF EXISTS `dc_library`;
CREATE TABLE `dc_library`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `share_role` enum('user','register','all') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'user' COMMENT '查看权限/成员开放、注册用户、完全开放',
  `share_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分享密码',
  `share` tinyint(1) NULL DEFAULT 0 COMMENT '分享/不分享，分享',
  `share_validity_type` enum('PERPETUAL','ONE_DAY','SEVEN_DAY','THIRTY_DAY') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分享有效期',
  `share_end_time` datetime(0) NULL DEFAULT NULL COMMENT '分享结束时间',
  `share_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分享的文档链接,二维码可通过链接生成即可',
  `share_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分享key',
  `type` enum('knowledge','directory','document_html','document_xlsx','document_map','document_flow','document_unrecognized','document_upload') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'knowledge' COMMENT '类型\\知识库、目录、文本文档、表格文档、脑图文档、流程文档。',
  `parent_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '上级ID',
  `size` double(11, 4) NULL DEFAULT 0.0000 COMMENT '文档大小',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '桐名称',
  `file_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文件存储路径',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0未删除  1已删除',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `sub_json` json NULL COMMENT '子集目录的json',
  `order_id` int(11) NULL DEFAULT 0 COMMENT '排序字段',
  `editing_by` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '该文档此时正在被哪个用户编辑,没有则为null',
  `knowledge_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '知识库id',
  `color` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '知识库颜色',
  `read_notify` tinyint(1) NULL DEFAULT 1 COMMENT '自动发送查看提醒开关',
  `label` json NULL COMMENT '标签',
  `help` tinyint(255) NULL DEFAULT NULL COMMENT '是否是帮助',
  `searchable` tinyint(1) NULL DEFAULT 1 COMMENT '是否允许搜索 0-否，1-是',
  `commentable` tinyint(1) NULL DEFAULT 1 COMMENT '是否可评论 0-否，1-是',
  `other_json` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端用于存储其他字段(例如:脑图需要一个type类型存储）',
  `name_suffix` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_knowledge_id`(`knowledge_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dc_library
-- ----------------------------
INSERT INTO `dc_library` VALUES ('0020d9ecbf402d348f758c43fbaa6960', '未命名文件', 'user', NULL, 0, NULL, NULL, NULL, NULL, 'document_html', 'aeb7fd6bf122b71935e41472a59a18d3', 0.0000, 'document-mgr', 'cscs/未命名文件/2022-04-12-698487577669177344-d9e3824c17cd4fa08e889ba959f6b956.document_html', '超级管理员', '1', '2022-04-12 10:59:36', '超级管理员', '2022-04-12 11:00:16', 0, NULL, NULL, '1', NULL, 0, NULL, 'aeb7fd6bf122b71935e41472a59a18d3', NULL, 1, NULL, NULL, 1, 1, NULL, NULL);
INSERT INTO `dc_library` VALUES ('aeb7fd6bf122b71935e41472a59a18d3', 'cscs', 'user', NULL, 0, NULL, NULL, NULL, NULL, 'knowledge', '0', 0.0000, NULL, NULL, '超级管理员', '1', '2022-04-12 10:59:28', '超级管理员', '2022-04-12 10:59:28', 0, 'csscs', NULL, '1', NULL, 0, NULL, NULL, NULL, 1, NULL, NULL, 1, 1, NULL, NULL);
INSERT INTO `dc_library` VALUES ('da31a138acce7bd835859dce96071c7f', '微信图片_20220221155258.png', 'user', NULL, 0, NULL, NULL, NULL, NULL, 'document_upload', 'aeb7fd6bf122b71935e41472a59a18d3', 0.0000, 'jvs-apply-document', 'document-mgr/2022-04-12-698564836253929472-微信图片_20220221155258.png', '超级管理员', '1', '2022-04-12 16:07:16', '超级管理员', '2022-04-12 16:07:16', 0, NULL, NULL, '1', NULL, 2, NULL, 'aeb7fd6bf122b71935e41472a59a18d3', NULL, 1, NULL, NULL, 1, 1, NULL, 'png');
INSERT INTO `dc_library` VALUES ('f8ee537da95f1f6cd1e2355e4c653886', '未命名文件', 'user', NULL, 0, NULL, NULL, NULL, NULL, 'document_xlsx', 'aeb7fd6bf122b71935e41472a59a18d3', 0.0000, 'document-mgr', 'cscs/未命名文件/2022-04-12-698561036705239040-70f09b7639d14d9092765a56050f77b0.document_xlsx', '超级管理员', '1', '2022-04-12 11:00:35', '超级管理员', '2022-04-12 15:52:10', 0, NULL, NULL, '1', NULL, 1, NULL, 'aeb7fd6bf122b71935e41472a59a18d3', NULL, 1, NULL, NULL, 1, 1, NULL, NULL);

-- ----------------------------
-- Table structure for dc_library_authority
-- ----------------------------
DROP TABLE IF EXISTS `dc_library_authority`;
CREATE TABLE `dc_library_authority`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id（不是具体用户时为空）',
  `member_scope` enum('registerAll','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户范围：所有企业成员、用户',
  `authority` enum('read','write') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限：查看、编辑,',
  `dc_library_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '知识库的ID值',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0未删除  1已删除',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dc_library_id`(`dc_library_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识库-权限' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dc_library_collect
-- ----------------------------
DROP TABLE IF EXISTS `dc_library_collect`;
CREATE TABLE `dc_library_collect`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `doc_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文档id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dc_library_comment
-- ----------------------------
DROP TABLE IF EXISTS `dc_library_comment`;
CREATE TABLE `dc_library_comment`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `message` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '留言',
  `knowledge_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '知识库id',
  `parent_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级评论',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识库-用户留言' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dc_library_like
-- ----------------------------
DROP TABLE IF EXISTS `dc_library_like`;
CREATE TABLE `dc_library_like`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键ID',
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户ID',
  `real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `biz_type` enum('LIBRARY','COMMENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '业务类型。LIBRARY-知识库，COMMENT-评论',
  `biz_resource_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '点赞资源id',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_resource`(`biz_resource_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '知识库-点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dc_library_log
-- ----------------------------
DROP TABLE IF EXISTS `dc_library_log`;
CREATE TABLE `dc_library_log`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `dc_library_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文档的ID值',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0未删除  1已删除',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `dept_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文档浏览记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dc_library_read
-- ----------------------------
DROP TABLE IF EXISTS `dc_library_read`;
CREATE TABLE `dc_library_read`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名-标记为已读时，必须要登录',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `knowledge_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '知识库id',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识库-用户已读记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dc_library_user
-- ----------------------------
DROP TABLE IF EXISTS `dc_library_user`;
CREATE TABLE `dc_library_user`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `role` enum('owner','admin','member') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '成员角色：所有者、管理员、成员',
  `dc_library_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '知识库的ID值',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0未删除  1已删除',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识库-文档对应的用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dc_library_user
-- ----------------------------
INSERT INTO `dc_library_user` VALUES ('0c80134962ff84116f682df9ea3991a1', '1', '超级管理员', 'owner', 'aeb7fd6bf122b71935e41472a59a18d3', '2022-04-12 10:59:28', '2022-04-12 10:59:28', 0, NULL, '1', '超级管理员', '超级管理员', '1');

-- ----------------------------
-- Table structure for dc_role
-- ----------------------------
DROP TABLE IF EXISTS `dc_role`;
CREATE TABLE `dc_role`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0未删除  1已删除',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文档角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dc_role
-- ----------------------------
INSERT INTO `dc_role` VALUES ('1', '所有者', NULL, NULL, '2022-02-16 15:37:13', NULL, '2022-02-16 15:37:38', 0, NULL, '1');
INSERT INTO `dc_role` VALUES ('2', '管理者', NULL, NULL, '2022-02-16 15:37:35', NULL, '2022-02-16 15:37:41', 0, NULL, '1');
INSERT INTO `dc_role` VALUES ('3', '分享者', NULL, NULL, '2022-02-16 15:38:10', NULL, '2022-02-16 15:38:13', 0, NULL, '1');
INSERT INTO `dc_role` VALUES ('4', '阅读者', NULL, NULL, '2022-02-16 15:38:34', NULL, '2022-02-16 15:38:37', 0, NULL, '1');

-- ----------------------------
-- Table structure for dc_template
-- ----------------------------
DROP TABLE IF EXISTS `dc_template`;
CREATE TABLE `dc_template`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件存储路径',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '桶名称',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文档名称',
  `size` double(11, 4) NULL DEFAULT 0.0000 COMMENT '文档大小(MB)',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文档类型',
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板分类',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板封面图片',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sql_performance
-- ----------------------------
DROP TABLE IF EXISTS `sql_performance`;
CREATE TABLE `sql_performance`  (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sql` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `start_time` int(11) NULL DEFAULT NULL,
  `end_time` int(11) NULL DEFAULT NULL,
  `consuming_time` int(11) NULL DEFAULT NULL,
  `access_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `slow_sql` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `app_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '保存执行计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名 ',
  `file_type` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '桶名',
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模块名',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '上传时间',
  `file_path` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件地址',
  `size` bigint(11) NULL DEFAULT NULL COMMENT '文件大小 单位:字节',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务备注',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `file_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '占位字段',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件统一管理，不包含租户信息，只做文件转发和统一保存管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES ('05d7a8b22c5fd30a4f827682447bcc3a', 'document-mgr/2022-04-12-698564836253929472-微信图片_20220221155258.png', 'image/png', 'jvs-apply-document', 'document-mgr', '2022-04-12 16:07:16', 'document-mgr/2022-04-12-698564836253929472-微信图片_20220221155258.png', 277066, NULL, NULL, NULL);
INSERT INTO `sys_file` VALUES ('5c542c33fd427bc4ef2dff6081215106', 'cscs/未命名文件/2022-04-12-698487577669177344-d9e3824c17cd4fa08e889ba959f6b956.document_html', 'document_html', 'document-mgr', 'cscs/未命名文件', '2022-04-12 11:00:16', 'cscs/未命名文件/2022-04-12-698487577669177344-d9e3824c17cd4fa08e889ba959f6b956.document_html', 81, NULL, NULL, NULL);
INSERT INTO `sys_file` VALUES ('f352a97c51c823b96b1772fdaeaf34e0', 'cscs/未命名文件/2022-04-12-698561036705239040-70f09b7639d14d9092765a56050f77b0.document_xlsx', 'document_xlsx', 'document-mgr', 'cscs/未命名文件', '2022-04-12 15:52:10', 'cscs/未命名文件/2022-04-12-698561036705239040-70f09b7639d14d9092765a56050f77b0.document_xlsx', 26529, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `business_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名',
  `function_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '功能名',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `method_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名',
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类名',
  `thread_user` json NULL COMMENT '当前用户对象JSON',
  `parameters` json NULL COMMENT '请求参数对象JSON',
  `tid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT 'tid',
  `version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号',
  `consuming_time` int(11) NULL DEFAULT NULL COMMENT '耗时',
  `return_obj` json NULL COMMENT '返回对象JSON',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '返回状态是否是成功',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求IP地址',
  `elements` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常数据JSON',
  `exception_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常消息数据',
  `env` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '环境参数',
  `api` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'api地址',
  `type` tinyint(1) NULL DEFAULT 1 COMMENT '日志类型:1-AOP拦截,2-过程中的日志,3-自定义日志',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT '用户名称',
  `create_date` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT '租户id',
  `operation_type` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT '操作类型',
  `client_id` char(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT '终端',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `start_time`(`start_time`) USING BTREE,
  INDEX `client_id`(`client_id`(191)) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '请求日志' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

