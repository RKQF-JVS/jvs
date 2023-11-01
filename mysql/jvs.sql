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

 Date: 06/09/2022 17:44:31
*/
create database `jvs` default character set utf8mb4 collate utf8mb4_general_ci;
use jvs;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for jvs_chart_page
-- ----------------------------
DROP TABLE IF EXISTS `jvs_chart_page`;
CREATE TABLE `jvs_chart_page`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `is_deploy` tinyint(1) NULL DEFAULT 0 COMMENT '发布状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '所属租户',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除状态',
  `dept_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门ID',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `job_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位ID',
  `filter_json` json NULL COMMENT '筛选条件',
  `data_json` json NULL COMMENT '数据json',
  `supported_client_type` enum('all','pc','mobile') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多端支持',
  `check_login` tinyint(1) NULL DEFAULT 1 COMMENT '是否校验登录',
  `icon` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标',
  `jvs_app_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用',
  `type` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类',
  `role` json NULL COMMENT '权限集',
  `role_type` tinyint(1) NULL DEFAULT 1 COMMENT '权限类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '页面配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of jvs_chart_page
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '保存执行计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sql_performance
-- ----------------------------

-- ----------------------------
-- Table structure for sys_apply
-- ----------------------------
DROP TABLE IF EXISTS `sys_apply`;
CREATE TABLE `sys_apply`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `app_key` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'APPID',
  `app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'app_secret',
  `describes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `scope` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'server,all' COMMENT '指定client的权限范围，比如读写权限，比如移动端还是web端权限',
  `authorized_grant_types` json NULL COMMENT '可选值 授权码模式:authorization_code,密码模式:password,刷新token: refresh_token, 隐式模式: implicit: 客户端模式: client_credentials。支持多个用逗号分隔',
  `authorities` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '指定用户的权限范围，如果授权的过程需要用户登陆，该字段不生效，implicit和client_credentials需要',
  `access_token_validity_seconds` int(11) NULL DEFAULT 3000 COMMENT '设置access_token的有效时间(秒),默认(60*60*12,12小时)',
  `refresh_token_validity_seconds` int(11) NULL DEFAULT 30000 COMMENT '设置refresh_token有效期(秒)，默认(60*60*24*30, 30填)',
  `additional_information` json NULL COMMENT '值必须是json格式',
  `archived` tinyint(1) NULL DEFAULT 0,
  `trusted` tinyint(1) NULL DEFAULT 0,
  `auto_approve_scopes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '默认false,适用于authorization_code模式,设置用户是否自动approval操作,设置true跳过用户确认授权操作页面，直接跳到redirect_uri',
  `registered_redirect_uris` json NULL,
  `icon` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'icon',
  `logo` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'logo',
  `bg_img` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '背景图',
  `login_domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用的域名',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统应用' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_apply
-- ----------------------------
INSERT INTO `sys_apply` VALUES ('0b3096f9e82e8a79c3cbf2d955d370d8', '任务管理', 'teamwork', '$2a$10$/xDYsV9/gD4/RpjYyBDw0ugg9tW1xXkYt16VepcePcLR/QK0iVJZS', '任务管理', 0, 'server,all', '[\"password\", \"refresh_token\", \"authorization_code\", \"client_credentials\"]', NULL, 3000, 30000, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '1', '2022-09-06 16:29:59', 'admin', '2022-09-06 16:29:59');
INSERT INTO `sys_apply` VALUES ('1ff53290763d889bb85813d1845b3bc8c', 'JVS服务平台', 'frame', '$2a$10$RtdtlcyALRa4BhsYWRS3CORtcvzR2sGwrdMcDdy9K7IQYN/vGre3.', 'PC端', 0, 'server,all', '[\"password\", \"refresh_token\", \"authorization_code\", \"client_credentials\"]', NULL, 30000, 300000, NULL, 0, 0, 'false', NULL, 'http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico', 'http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png', 'http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png', 'localhost', NULL, NULL, '2021-11-15 17:31:43', NULL, '2021-11-15 17:31:43');
INSERT INTO `sys_apply` VALUES ('bbb46b6e0f779fdb156371548b000ba6', '文档库', 'knowledge', '$2a$10$t5Qu8j8XHdGEIpK0RuRS1OMNh8iM1362PDR9qDk1vCPDYTv8.lrem', 'knowledge', 0, 'server,all', '[\"password\", \"refresh_token\", \"authorization_code\", \"client_credentials\"]', NULL, 30000, 300000, NULL, 0, 0, NULL, NULL, 'http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico', 'http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png', 'http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png', 'knowledge.bctools.cn', NULL, NULL, '2021-11-15 17:31:43', NULL, '2021-11-15 17:31:43');

-- ----------------------------
-- Table structure for sys_bulletin
-- ----------------------------
DROP TABLE IF EXISTS `sys_bulletin`;
CREATE TABLE `sys_bulletin`  (
  `id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ID',
  `title` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `content` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  `app_keys` json NULL COMMENT '系统应用APPID数组',
  `start_time` datetime NOT NULL COMMENT '公告生效时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '公告结束时间',
  `publish` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否发布 0-待发布, 1-发布',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除  -1：已删除  0：正常',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `create_by_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统应用公告' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_bulletin
-- ----------------------------

-- ----------------------------
-- Table structure for sys_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_data`;
CREATE TABLE `sys_data`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `type` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `function_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '功能名',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
  `api` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求地址',
  `describes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '租户ID默认1',
  `remark` json NULL COMMENT '扩展',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据权限资源标识表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_data
-- ----------------------------
INSERT INTO `sys_data` VALUES ('07bdcf158b2a84453944b5844efe5e8e', 'POST', '9', '9', '9', '描述9', '9f8765b3faae36d71728369665cc7d33', NULL);
INSERT INTO `sys_data` VALUES ('251ee7e61ed03dedbbc80b7375910145', 'POST', '数据源-SQL执行权', '数据源-SQL执行权限', '/mgr/datasource/api/crud/**', '数据源-SQL执行权限', '1', NULL);
INSERT INTO `sys_data` VALUES ('5d4885afe1677449c1ecbd30a3a28677', 'DELETE', '3', '3', '3', '描述3', '9f8765b3faae36d71728369665cc7d33', NULL);
INSERT INTO `sys_data` VALUES ('8811349793295e11d1388192098082b6', 'POST', '客户统计', '客户管理', '/mgr/jvs-auth/role/save', '用于查看客户管理中客户的基本信息查看', 'b7566ac352652eacca9b383120e7d566', NULL);
INSERT INTO `sys_data` VALUES ('8f611afe0e477da4c8a9de1db15c33ea', 'GET', '功能', '数据权限名称', '网络接口', '描述', '9f8765b3faae36d71728369665cc7d33', NULL);
INSERT INTO `sys_data` VALUES ('98a87765c6f226a2866bc2c405e0fcd5', 'DELETE', '8', '8', '8', '8描述', '9f8765b3faae36d71728369665cc7d33', NULL);
INSERT INTO `sys_data` VALUES ('9d24d5dcb3508f119683028bffe5fc58', 'POST', '客户统计', '客户管理', '/mgr/jvs-auth/role/save', '主要用于客户管理中的客户基本信息等xxxxxx', '1', NULL);
INSERT INTO `sys_data` VALUES ('c28025959fde342dbbd62693fc9b6742', 'GET', 'dfsa', '发票管理', '/mgr/jvs-auth/data/page', '通常情况下，发票，只看自己上报的发票，其它人不能看。如果是财务小姐姐，可以看所有的发票', '1', NULL);
INSERT INTO `sys_data` VALUES ('e88317af1610c5e2bb70ba4e0cdfd35f', 'POST', '用于限制可查询的数据', '某业务查询权限', '/mgr/datasource/**', '这里是描述', '1', NULL);
INSERT INTO `sys_data` VALUES ('f06e5316b4cb310ce32e734663f73203', 'GET', 'adfsdfsadf', '测试客流统计数据', '/get', '客户统计数据中的，园区统计数据部分，根据各个分区进行统计，用户属于哪个分区，查看哪个分区 。', '1', NULL);

-- ----------------------------
-- Table structure for sys_data_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_role`;
CREATE TABLE `sys_data_role`  (
  `role_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色ID',
  `data_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
  `tenant_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `remark` json NULL COMMENT '扩展数据权限扩展字段',
  PRIMARY KEY (`role_id`, `data_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色与数据权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_data_role
-- ----------------------------
INSERT INTO `sys_data_role` VALUES ('1', '9d24d5dcb3508f119683028bffe5fc58', '1', '{\"jobIds\": [\"a39a93122fe800574ca4d4ef502602a0\"], \"userId\": \"\", \"deptIds\": [\"7166a02d6cd05db7bd6a2fce24b95da0\", \"3efe4da73247c4fb47cf65ba4d5493ca\"], \"createByIds\": [], \"dataScopeType\": \"customize\"}');
INSERT INTO `sys_data_role` VALUES ('20b63dcefa237453decd99166edd28aa', '9d24d5dcb3508f119683028bffe5fc58', '1', '{\"jobIds\": [], \"userId\": \"\", \"deptIds\": [\"7166a02d6cd05db7bd6a2fce24b95da0\", \"3efe4da73247c4fb47cf65ba4d5493ca\"], \"createByIds\": [], \"dataScopeType\": \"customize\"}');
INSERT INTO `sys_data_role` VALUES ('223e3e95681063adec5c6220fbe99faf', '8f611afe0e477da4c8a9de1db15c33ea', '9f8765b3faae36d71728369665cc7d33', '{\"jobIds\": [\"84437a52181da635935116f851452755\", \"9fb43cc4f3503ce99bd7e51b9b1093d0\"], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [\"272a3ae3fecaffd841665ed2b9f47b60\", \"661f46776fee5ee10a57de39ee284c32\"], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"self\"}');
INSERT INTO `sys_data_role` VALUES ('b16a6b13e98b6eba6770e7ca0ccd4364', '251ee7e61ed03dedbbc80b7375910145', '1', '{\"jobIds\": [], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"curr_dept\"}');
INSERT INTO `sys_data_role` VALUES ('b16a6b13e98b6eba6770e7ca0ccd4364', '9d24d5dcb3508f119683028bffe5fc58', '1', '{\"jobIds\": [\"a39a93122fe800574ca4d4ef502602a0\"], \"userId\": \"\", \"deptIds\": [\"7166a02d6cd05db7bd6a2fce24b95da0\"], \"createByIds\": [], \"dataScopeType\": \"customize\"}');
INSERT INTO `sys_data_role` VALUES ('b16a6b13e98b6eba6770e7ca0ccd4364', 'a83f9ef7ead18205c8999d2348e1877a', '1', '{\"jobIds\": [], \"userId\": \"\", \"deptIds\": [], \"createByIds\": [], \"dataScopeType\": \"all_dept\"}');
INSERT INTO `sys_data_role` VALUES ('b16a6b13e98b6eba6770e7ca0ccd4364', 'c28025959fde342dbbd62693fc9b6742', '1', '{\"jobIds\": [], \"userId\": \"\", \"deptIds\": [], \"createByIds\": [], \"dataScopeType\": \"curr_dept_tree\"}');
INSERT INTO `sys_data_role` VALUES ('b16a6b13e98b6eba6770e7ca0ccd4364', 'e88317af1610c5e2bb70ba4e0cdfd35f', '1', '{\"jobIds\": [], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"curr_dept_tree\"}');
INSERT INTO `sys_data_role` VALUES ('e7ad379f55685524b304c717ee181344', '8f611afe0e477da4c8a9de1db15c33ea', '9f8765b3faae36d71728369665cc7d33', '{\"jobIds\": [\"9fb43cc4f3503ce99bd7e51b9b1093d0\", \"84437a52181da635935116f851452755\"], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [\"66ff4b4473f008636422d29ca7005dbf\", \"bbdd1b65a0424598648790aa3f7c0712\"], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"self\"}');
INSERT INTO `sys_data_role` VALUES ('e84aefeed0936072f2bd7dae59d33b08', '251ee7e61ed03dedbbc80b7375910145', '1', '{\"jobIds\": [], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"all_dept\"}');
INSERT INTO `sys_data_role` VALUES ('e84aefeed0936072f2bd7dae59d33b08', '9d24d5dcb3508f119683028bffe5fc58', '1', '{\"jobIds\": [], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"all_dept\"}');
INSERT INTO `sys_data_role` VALUES ('e84aefeed0936072f2bd7dae59d33b08', 'c28025959fde342dbbd62693fc9b6742', '1', '{\"jobIds\": [], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"all_dept\"}');
INSERT INTO `sys_data_role` VALUES ('e84aefeed0936072f2bd7dae59d33b08', 'e88317af1610c5e2bb70ba4e0cdfd35f', '1', '{\"jobIds\": [], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"all_dept\"}');
INSERT INTO `sys_data_role` VALUES ('e84aefeed0936072f2bd7dae59d33b08', 'f06e5316b4cb310ce32e734663f73203', '1', '{\"jobIds\": [], \"dataApi\": {\"api\": \"\", \"type\": \"\"}, \"deptIds\": [], \"dataDicts\": [], \"createByIds\": [], \"dataScopeType\": \"all_dept\"}');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除  -1：已删除  0：正常',
  `parent_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '-1' COMMENT '上级部门为  可能为租户ID，先根据租户进行查询',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '租户ID默认1',
  `leader_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门负责人ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('029e4553d7c73be504e533cf8748802d', '测试部', '2021-11-01 23:20:05', '2021-11-02 01:23:04', 1, 'b7566ac352652eacca9b383120e7d566', 'b7566ac352652eacca9b383120e7d566', NULL);
INSERT INTO `sys_dept` VALUES ('0a962a32272dd51fdfa17f26f2a96136', '3333', '2021-10-01 04:28:55', NULL, 0, '3efe4da73247c4fb47cf65ba4d5493ca', '1', 'd0541b16e72ef98a08a8463bf796f4be');
INSERT INTO `sys_dept` VALUES ('2178a380578b987961fe78e3ac929b02', 'fdsfdfd', '2021-09-23 02:01:17', '2021-09-23 02:01:27', 1, '610f8d4ef4f116d8a16b17e37d5f10a9', '1', '');
INSERT INTO `sys_dept` VALUES ('272a3ae3fecaffd841665ed2b9f47b60', '部门006', '2021-10-14 22:29:55', '2021-10-21 18:38:27', 0, '9f8765b3faae36d71728369665cc7d33', '9f8765b3faae36d71728369665cc7d33', '45595b4eafb4027ee61dbdcedbde7282');
INSERT INTO `sys_dept` VALUES ('299d32163016d261d9b84ef1e8de67a6', 'sasdsdasds', '2021-10-15 00:38:58', '2021-10-15 02:49:43', 1, '1', '1', '');
INSERT INTO `sys_dept` VALUES ('36a72e3eede80677e90679cd1f12caf3', '部门名称', '2021-09-17 00:53:07', NULL, 0, '3efe4da73247c4fb47cf65ba4d5493ca', 'default', 'f9f5f28328c93efad32837a298f3f10f');
INSERT INTO `sys_dept` VALUES ('3970e18f522eafb41c93990a6b940211', '测试部门2', '2021-09-17 17:17:55', NULL, 0, '5a453c864d3b0a0ac8f3188363f4dc47', 'default', '');
INSERT INTO `sys_dept` VALUES ('3efe4da73247c4fb47cf65ba4d5493ca', '下级部门', '2021-09-01 02:25:55', '2021-09-01 02:31:57', 0, '7166a02d6cd05db7bd6a2fce24b95da0', '1', '1');
INSERT INTO `sys_dept` VALUES ('50e07e2f8698e265311841a295def989', '市场部', '2021-11-01 23:20:40', NULL, 0, 'b7566ac352652eacca9b383120e7d566', 'b7566ac352652eacca9b383120e7d566', '');
INSERT INTO `sys_dept` VALUES ('5a453c864d3b0a0ac8f3188363f4dc47', '测试部门', '2021-09-17 17:17:47', NULL, 0, '3efe4da73247c4fb47cf65ba4d5493ca', 'default', '');
INSERT INTO `sys_dept` VALUES ('5f72bde66c4984456207cd5492cac2ff', '运营部的', '2021-11-15 18:10:05', '2021-11-25 18:27:43', 0, 'b7566ac352652eacca9b383120e7d566', 'b7566ac352652eacca9b383120e7d566', NULL);
INSERT INTO `sys_dept` VALUES ('610f8d4ef4f116d8a16b17e37d5f10a9', '测试部门5', '2021-09-17 17:41:03', NULL, 0, '9f02195c986567266db13530b68b69a5', 'default', '');
INSERT INTO `sys_dept` VALUES ('661f46776fee5ee10a57de39ee284c32', '部门02', '2021-10-14 19:04:15', '2021-10-21 18:42:05', 1, '9f8765b3faae36d71728369665cc7d33', '9f8765b3faae36d71728369665cc7d33', '');
INSERT INTO `sys_dept` VALUES ('66ff4b4473f008636422d29ca7005dbf', '测试部门01', '2021-10-08 23:17:24', '2021-10-15 18:37:35', 1, '9f8765b3faae36d71728369665cc7d33', '9f8765b3faae36d71728369665cc7d33', '');
INSERT INTO `sys_dept` VALUES ('72d5883f6a5d9b567207fec9c9bda6a2', '后勤综合部', '2021-10-30 18:40:59', NULL, 0, '1', '1', '');
INSERT INTO `sys_dept` VALUES ('77ea6e4d0fd60c65b35000ebda45269d', '市场部', '2021-10-14 17:36:52', '2021-10-30 18:40:50', 0, '1', '1', NULL);
INSERT INTO `sys_dept` VALUES ('7d27b74659361d39e5ad50115887e9af', '研发部', '2021-10-20 17:50:26', '2021-10-30 18:40:42', 0, '1', '1', NULL);
INSERT INTO `sys_dept` VALUES ('7da89be0a1905f89819c664ca0a61cc7', '下下级部门', '2021-09-30 17:22:08', NULL, 0, '3efe4da73247c4fb47cf65ba4d5493ca', '1', '');
INSERT INTO `sys_dept` VALUES ('847381a77ce8e318da7ea0f607d586ab', '11', '2021-10-18 19:48:33', '2021-10-18 19:48:47', 1, '986e4829bf65e2bf571fcca9e532bcf0', '1', '');
INSERT INTO `sys_dept` VALUES ('8686286b189daf1f4d285e2006eaf6bd', '总经办', '2021-10-13 02:03:47', '2021-11-11 19:10:36', 0, '1', '1', '1');
INSERT INTO `sys_dept` VALUES ('8c70975c28a5a38c265541633af1810f', 'asddsadsadsad', '2021-10-20 19:24:56', '2021-10-20 19:25:18', 1, 'e06c954a419e2d5d47e10cd4090cb376', '1', NULL);
INSERT INTO `sys_dept` VALUES ('8dc95d108ce1edb8df2ca5e9e67f3e0f', '应用研发中心', '2021-10-30 18:42:16', NULL, 0, '7d27b74659361d39e5ad50115887e9af', '1', '');
INSERT INTO `sys_dept` VALUES ('9457ff86bdcffd66e91b4e0ae6c27806', '333444', '2021-10-01 04:29:07', NULL, 0, '0a962a32272dd51fdfa17f26f2a96136', '1', '');
INSERT INTO `sys_dept` VALUES ('946efdcd2f62d41696f439e0dfc10420', '劳而无功', '2021-09-03 17:21:01', NULL, 0, '7166a02d6cd05db7bd6a2fce24b95da0', '1', '');
INSERT INTO `sys_dept` VALUES ('986e4829bf65e2bf571fcca9e532bcf0', '1', '2021-10-18 19:48:21', '2021-10-18 19:48:51', 1, '1', '1', '');
INSERT INTO `sys_dept` VALUES ('9ad546d574fa9658896b99dd3cdd6e2a', 'aaaa', '2021-10-15 00:36:10', '2021-10-15 00:38:47', 1, '1', '1', '');
INSERT INTO `sys_dept` VALUES ('9f02195c986567266db13530b68b69a5', '测试部门3', '2021-09-17 17:40:53', NULL, 0, '3970e18f522eafb41c93990a6b940211', 'default', '');
INSERT INTO `sys_dept` VALUES ('a109f4f58d9562d4a884d9a57f13df3b', '发现大苏打', '2021-10-28 20:07:54', '2021-10-28 20:08:09', 1, '1', '1', '');
INSERT INTO `sys_dept` VALUES ('b587753a0dd8d6f1c117cd996a1e2463', '测试部门', '2021-11-04 22:38:26', '2021-11-04 22:39:28', 1, 'b7566ac352652eacca9b383120e7d566', 'b7566ac352652eacca9b383120e7d566', '');
INSERT INTO `sys_dept` VALUES ('b7d7ff843d8786453bea704e0bf586c0', '1', '2021-10-11 22:36:38', NULL, 0, '946efdcd2f62d41696f439e0dfc10420', '1', '');
INSERT INTO `sys_dept` VALUES ('bace5af300440be0472105fcef2e3cf3', '规则委员会', '2021-10-30 18:41:21', NULL, 0, '8686286b189daf1f4d285e2006eaf6bd', '1', '');
INSERT INTO `sys_dept` VALUES ('bbdd1b65a0424598648790aa3f7c0712', '部门03', '2021-10-14 19:05:41', '2021-10-21 23:46:17', 0, '9f8765b3faae36d71728369665cc7d33', '9f8765b3faae36d71728369665cc7d33', '57b6eb97675cbe5872ed21a6d9b43ca4');
INSERT INTO `sys_dept` VALUES ('c33e96c993b22bcc9085a8416fb36a69', '123', '2021-10-01 01:52:10', NULL, 0, '7166a02d6cd05db7bd6a2fce24b95da0', '1', '');
INSERT INTO `sys_dept` VALUES ('ce145147575a97782308cc612b9eaf4a', '部门01', '2021-10-15 19:53:29', NULL, 0, '9f8765b3faae36d71728369665cc7d33', '9f8765b3faae36d71728369665cc7d33', '57b6eb97675cbe5872ed21a6d9b43ca4');
INSERT INTO `sys_dept` VALUES ('de03fa1854712aeb2b858899d3d62384', '战略规划中心', '2021-10-30 18:41:51', NULL, 0, '8686286b189daf1f4d285e2006eaf6bd', '1', '');
INSERT INTO `sys_dept` VALUES ('e06c954a419e2d5d47e10cd4090cb376', 'fsda11', '2021-10-13 02:04:19', '2021-10-28 20:05:22', 1, '1', '1', NULL);
INSERT INTO `sys_dept` VALUES ('f0dc042f945b9ba171bf71b485082b5a', '发到饭否', '2021-09-16 01:31:49', NULL, 0, '7166a02d6cd05db7bd6a2fce24b95da0', 'default', '');
INSERT INTO `sys_dept` VALUES ('f7e117b6ae4c7dadaebf847cc5e72fc5', '决策委员会', '2021-10-30 18:41:32', NULL, 0, '8686286b189daf1f4d285e2006eaf6bd', '1', '');
INSERT INTO `sys_dept` VALUES ('fcca27a3cc688ed30d217878873b7202', '基础框架中心', '2021-10-30 18:42:04', NULL, 0, '7d27b74659361d39e5ad50115887e9af', '1', '');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `system` enum('SYSTEM','BIZ') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除状态',
  `create_by` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `parent_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '-1' COMMENT '上级id，顶级-1',
  `uniq_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一id，数据迁移用到',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sys_dict_del_flag`(`del_flag`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('0f2920b2ec620be14598ff21dc92b238', 'jvsapp', '微应用分类处理', '2021-11-23 18:36:14', '2021-11-23 18:36:14', NULL, 'SYSTEM', '0', 'admin', '-1', '3860e1ef0fe04ea09ba892fcc3b3e152');
INSERT INTO `sys_dict` VALUES ('101', '客户类型', '市场营销客户管理', '2021-05-09 09:48:48', '2021-10-26 14:54:42', '内部客户管理分类', 'SYSTEM', '0', 'admin', '-1', '6592c944e8814df9a52e2a1f89e8c3b8');
INSERT INTO `sys_dict` VALUES ('102', '客户地域', '客户地域', '2021-05-09 09:51:35', '2021-10-26 14:54:42', '客户地域', 'SYSTEM', '0', 'admin', '-1', '428a4eda6b2c4c0eabb6355a34ecff27');
INSERT INTO `sys_dict` VALUES ('103', 'T/F', '是/否', '2021-05-09 09:56:28', '2021-10-26 14:54:42', '是/否', 'SYSTEM', '0', 'admin', '-1', '98f10083a69f4f6db5bd7a56aa15d0f6');
INSERT INTO `sys_dict` VALUES ('104', '有效状态', '有效状态', '2021-05-09 11:15:07', '2021-10-26 14:54:42', '有效状态', 'SYSTEM', '0', 'admin', '-1', 'bb87dc2d30fd46efa8f73aa8c29fd304');
INSERT INTO `sys_dict` VALUES ('105', '市场动作', '市场动作', '2021-05-09 11:26:23', '2021-10-26 14:54:42', '市场动作', 'SYSTEM', '0', 'admin', '-1', '93dd9d1a4eaf42618b130f60645562b5');
INSERT INTO `sys_dict` VALUES ('107', '在职状态', '在职状态', '2021-05-11 19:50:00', '2021-10-26 14:54:42', '在职状态', 'SYSTEM', '0', 'admin', '-1', '7cc5b51b6e8942b68d16cc6720624f66');
INSERT INTO `sys_dict` VALUES ('109', '数据权限', '描述', '2021-05-17 14:55:11', '2021-11-23 19:06:40', '备注', 'SYSTEM', '1', 'admin', '-1', '5cc2492922464e858bfcda19f151114e');
INSERT INTO `sys_dict` VALUES ('125', 'fenbao', '企业性质', '2021-05-25 10:29:43', '2021-10-26 14:54:42', '企业性质', 'SYSTEM', '0', 'admin', '-1', 'a81da949070d41b5b7337b94e2629d3a');
INSERT INTO `sys_dict` VALUES ('126', 'guarantee:letter_status', '保函状态', '2021-05-27 15:02:21', '2021-10-26 14:54:42', NULL, 'SYSTEM', '0', 'admin', '-1', 'fd002e8d6d5641a7a80403bc8f7c4dca');
INSERT INTO `sys_dict` VALUES ('130', '账款专家', '合同执行状态', '2021-05-29 19:33:43', '2021-11-23 19:06:40', '合同执行状态，未签约、已签约', 'SYSTEM', '0', 'admin', '-1', 'a0042888bc6c485f8249998d095eee83');
INSERT INTO `sys_dict` VALUES ('131', '合同类型', '合同类型', '2021-05-30 09:16:44', '2021-11-23 19:06:40', '人事合同、服务合同、商贸合同、采购合同', 'SYSTEM', '0', 'admin', '-1', '83e3b5389a2c46f6a6569e9bbd0cf8b9');
INSERT INTO `sys_dict` VALUES ('132', '核销状态', '核销状态', '2021-05-30 10:00:03', '2021-11-23 19:06:40', '核销状态：未核销、已核销、部分核销', 'SYSTEM', '0', 'admin', '-1', '01ca1b9df8ca4067b6f5e02310448072');
INSERT INTO `sys_dict` VALUES ('133', '签约模式', '合同签约模式', '2021-05-30 10:23:10', '2021-11-23 19:06:40', '在线、离线', 'SYSTEM', '0', 'admin', '-1', 'a99b8f150b3c4ffa9775ed6856751b4f');
INSERT INTO `sys_dict` VALUES ('135', '发票类型', '发票类型', '2021-05-31 21:38:37', '2021-11-23 19:06:40', '增值业务专用发票6%、增值业务专用发票13%、增值业务普通发票3%', 'SYSTEM', '0', 'admin', '-1', 'c02d83cdce3a49ed96f78098319d27e6');
INSERT INTO `sys_dict` VALUES ('139', 'pay', '业务系统支付状态', '2021-06-02 16:15:54', '2021-11-23 19:06:40', NULL, 'SYSTEM', '0', 'admin', '-1', '5da3561583f540d9b1a16858f02658ad');
INSERT INTO `sys_dict` VALUES ('141', '需求分类', '发布不同类别的需求', '2021-06-07 14:17:15', '2021-11-23 19:06:40', NULL, 'SYSTEM', '0', 'admin', '-1', '35274afc11634d8daf92604a2ba97432');
INSERT INTO `sys_dict` VALUES ('142', 'jobstatus', '员工在职状态', '2021-06-12 10:23:54', '2021-11-23 19:06:40', '试用、在职、离职', 'SYSTEM', '0', 'admin', '-1', '0d217b44d20942ffa2ddcb4d9d5d3d17');
INSERT INTO `sys_dict` VALUES ('144', 'probationperiod', '试用周期', '2021-06-12 12:17:47', '2021-11-23 19:06:40', '0、1、2、3、6', 'SYSTEM', '0', 'admin', '-1', '989c32fd13e049d19a66d00d6aa25cdb');
INSERT INTO `sys_dict` VALUES ('168', '性别', '性别', '2021-06-25 14:45:09', '2021-11-23 19:06:40', NULL, 'SYSTEM', '0', 'admin', '-1', '58fd9222129a4aeda25e6f2db46f9fc0');
INSERT INTO `sys_dict` VALUES ('176', '数据权限', '描述', '2021-07-06 14:12:34', '2021-11-23 19:06:40', '备注', 'SYSTEM', '0', 'admin', '-1', 'c9838805264d4a7a9b938247b11c83f0');
INSERT INTO `sys_dict` VALUES ('186', 'SEO', 'SEO关键词', '2021-07-07 17:04:38', '2021-11-23 19:06:40', NULL, 'SYSTEM', '0', 'admin', '-1', 'e3828d744cc74611b4ece712088cd043');
INSERT INTO `sys_dict` VALUES ('187', '测试业务', '测试业务字典', '2021-07-09 16:45:04', '2021-11-23 19:06:40', NULL, 'SYSTEM', '0', 'admin', '-1', '19a99d0f62d3406c933c4bdb128692bb');
INSERT INTO `sys_dict` VALUES ('1dc044815217ef71d38868af260569fd', '业务类型', '描述', '2021-07-16 15:08:56', '2021-11-23 19:06:40', '备注', 'SYSTEM', '1', 'admin', '-1', '4f31a4aa02254ab88e3d65952ae3ff13');
INSERT INTO `sys_dict` VALUES ('2b29f7d99083602aef6dce5f9c807fd6', 'icon', '所有图标库', '2021-11-16 11:06:28', '2021-11-16 11:06:28', NULL, 'SYSTEM', '0', 'admin', '-1', 'acaf821e73c240be9a2e071e7141dc46');
INSERT INTO `sys_dict` VALUES ('34b0822233113729862a929529c5d3da', 'SEO关键词', 'SEO关键词', '2021-11-02 09:29:40', '2021-11-23 18:39:07', '备注信息', 'BIZ', '1', 'admin', '-1', '4185cf03f62243b2a68674b41a3a3d53');
INSERT INTO `sys_dict` VALUES ('52118494eca4204bee9ef42673cdaefa', '测试', '业务系统测试字典用', '2021-07-16 15:58:51', '2021-11-23 19:06:40', '那些', 'SYSTEM', '0', 'admin', '-1', 'aa841b3b77454396a771a860a0d091f6');
INSERT INTO `sys_dict` VALUES ('614bb08c1dfcab7366a47e1aafca8374', 'dsvfdvcsxz', '系统', '2021-10-11 11:04:03', '2021-11-23 19:06:39', NULL, 'SYSTEM', '0', 'admin', '-1', '2351f100f0414a04880f1a609eace77d');
INSERT INTO `sys_dict` VALUES ('7e47aff5bb3f2a75d9d772c6a9f1fa3d', 'meetingStatus', '会议状态', '2021-08-28 18:17:06', '2021-11-23 19:06:40', '会议状态', 'SYSTEM', '0', 'admin', '-1', '8512a1b4b2f44999a54e09ce687b70f7');
INSERT INTO `sys_dict` VALUES ('82', '模板类别', '方便管理不同类别的模板', '2021-03-30 14:10:27', '2021-10-26 14:54:46', NULL, 'SYSTEM', '0', 'admin', '-1', 'a832f40f-fee3-46ae-a38c-84c62e54061a');
INSERT INTO `sys_dict` VALUES ('8dd3ab5dcb3edf54640e6b2f7c0d9148', '会议类型', '会议类型', '2021-08-28 18:17:51', '2021-11-23 19:06:40', '会议类型', 'SYSTEM', '0', 'admin', '-1', '9c1d942280bf4c4aa738aff56e01b079');
INSERT INTO `sys_dict` VALUES ('ab83069fc2b945f9ed7ce5529b1a7b79', 'contractStatus', '合同状态', '2021-08-28 18:09:27', '2021-11-23 19:06:39', '合同状态', 'SYSTEM', '0', 'admin', '-1', '25a2d16777b64ec393954d9bd0065105');
INSERT INTO `sys_dict` VALUES ('b5c521d6c646850d6715c46d8b67f683', 'meetingPlace', '会议地点', '2021-08-28 18:16:13', '2021-11-23 19:06:40', '会议地点', 'SYSTEM', '0', 'admin', '-1', '7915b25a32aa4b09be7190cd2ea85948');
INSERT INTO `sys_dict` VALUES ('dd5aa4d7c06b919a437e934e5af07032', 'certificate_type', '证件类型', '2021-12-15 16:16:15', '2021-12-15 16:16:15', NULL, 'BIZ', '0', 'admin', '-1', '8d60e3180d5d49b482661da584e78f50');
INSERT INTO `sys_dict` VALUES ('df45ee3c48af5af53cf136be38d9a713', '授信状态', '授信状态', '2021-11-13 22:38:04', '2021-11-23 18:38:59', '授信状态', 'BIZ', '1', 'admin', '-1', 'ea31cf3256614654b35446dbd147f107');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
  `dict_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sort` int(10) NOT NULL DEFAULT 0 COMMENT '排序（升序）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典项' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES ('006a6cb93ce5893749afb00b7081ec11', '8fe320325009e65644c36d1b16574531', '出纳放款确认', '出纳放款确认', 'processTagType', '出纳放款确认', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:48', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('0240fbce7233ec943c922211e73ec205', 'bdb79cde74266f58bae93ca65f862efa', '7', '九龙坡区', 'regionType', '九龙坡区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:08', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('038329ba9c5b8a858948a30221ca0270', '8fe320325009e65644c36d1b16574531', '审贷中心放款审批', '审贷中心放款审批', 'processTagType', '审贷中心放款审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:48', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('0478285344fdc3901f1e58edf0bf5066', '8fe320325009e65644c36d1b16574531', '财务负责人审批', '财务负责人审批', 'processTagType', '财务负责人审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:48', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('07aab34a7f5bc46512eaea56021cb3c4', 'ddf4becc7bdbb0c482bff051166cb54c', 'SH', '上会', '321', '上会', 0, '2021-08-28 18:59:52', '2021-08-28 18:59:52', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('0c6cd764ce423dbe162a6e76721d6d83', '126ceca0dc43d3f84df03221a09b3c75', '4', '云南代表处', 'parent_company', '云南代表处', 0, '2021-08-12 11:41:53', '2021-08-12 11:41:53', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('0d60d36c5e91bc1a62703fb89d331400', '7ab996b18d4fa75e8837369f4c071b4a', '2', '小额贷款', 'productType', '小额贷款', 0, '2021-08-11 10:14:39', '2021-08-11 10:14:39', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('10f59b2707fef0359e24150756f8d5d6', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'INVALID', '已作废', 'contractStatus', '已作废', 0, '2021-08-28 18:09:45', '2021-08-28 18:09:45', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('12a49e9a3b8b2e57f90e8351cfad3d96', 'fec53be21d29d0cbf2c8683724dc9e80', '2', '0~100', 'companyScale', '0~100', 0, '2021-08-11 10:09:33', '2021-08-11 10:09:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('146a1309a6cff167a8a9fc7fe40a8378', '0f2920b2ec620be14598ff21dc92b238', '生产管理', '生产管理', 'jvsapp', NULL, 0, '2021-12-14 17:45:30', '2021-12-14 17:45:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('189b10242f9dd9ed113371eebd7fa778', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_2940969_4fwoh698c66.css', '金融服务', 'icon', 'http://at.alicdn.com/t/font_2940969_4fwoh698c66.js', 0, '2021-11-16 11:08:14', '2021-11-16 11:08:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('1a6cd15ede26c7941bace5766a65d75a', '8fe320325009e65644c36d1b16574531', '副总经理审批', '副总经理审批', 'processTagType', '副总经理审批', 0, '2021-08-02 19:37:09', '2021-08-11 09:47:48', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('1a9ef5b74ada804086a4abe6849e5d5b', 'bdb79cde74266f58bae93ca65f862efa', '26', '武隆区', 'regionType', '武隆区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:08', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('1b1ad1d1d7b39cb5fa8e1df2e3ae134b', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'SEAL', '已用印未回传', 'contractStatus', '已用印未回传', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('1dd4ad8940dea000811717544309a252', '126ceca0dc43d3f84df03221a09b3c75', '1', '公司重庆总部', 'parent_company', '公司重庆总部', 0, '2021-08-12 11:41:53', '2021-08-12 11:41:53', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('1e1c1da96a7cdaf4d5db340cbfa6415c', 'dd5aa4d7c06b919a437e934e5af07032', 'sfz', '身份证', 'certificate_type', NULL, 0, '2021-12-15 16:16:46', '2021-12-15 16:16:46', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('1f8a2afa8a689240d0bfc1268b288d80', 'e113a39dd3d9b0f785b5b54a25b03e96', 'AA', 'AA', 'customerCreditLevel', 'AA', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('22e0952a855acb32fe64a73ae5e1ea81', '1948170e01b107463b41895ff2a78a44', 'AUDIT_INFO_1', '无法表示意见的审计报告', 'auditOpinionTypes', '无法表示意见的审计报告', 0, '2021-08-28 18:15:56', '2021-08-28 18:15:56', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('237', '75', '1', '1', '测试', '1', 0, '2021-03-10 14:56:14', '2021-04-26 14:34:20', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('238', '75', '1', '1', '测试', '1', 0, '2021-03-10 14:56:14', '2021-04-26 14:34:20', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('24553ef4efc13a67b51b237f3f56d623', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_2940994_0e9q5r86o6e4.css', 'OA', 'icon', 'http://at.alicdn.com/t/font_2940994_0e9q5r86o6e4.js', 0, '2021-11-16 11:08:14', '2021-11-16 11:08:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('247', '82', '合同模板', NULL, '模板类别', NULL, 0, '2021-03-30 14:20:30', '2021-03-30 14:20:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('252', '80', '2', '已支付', '数据', '订单金额买家成功支付', 0, '2021-04-01 09:29:47', '2021-04-01 09:29:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('253', '80', '1', '未支付', '数据', '买家下单未付款', 0, '2021-04-01 09:29:47', '2021-04-01 09:29:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('259', '87', '1', '1', 'test-cc-dd', '1', 0, '2021-04-13 10:10:41', '2021-04-13 10:10:47', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('260', '87', '1', '1', 'test-cc-dd', '1', 0, '2021-04-13 10:10:41', '2021-04-13 10:10:47', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('263', '88', '1', '1', '1', '1', 0, '2021-04-16 16:01:35', '2021-04-16 16:01:35', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('264', '88', '1', '1', '1', '1', 0, '2021-04-16 16:01:35', '2021-04-16 16:01:41', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('290', '93', '1', 'paying', 'pay', '支付中', 0, '2021-04-26 15:24:47', '2021-07-16 15:10:23', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('291', '93', NULL, NULL, '1wdewdwedwed', NULL, 0, '2021-04-26 15:24:47', '2021-04-27 17:23:28', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('292', '93', NULL, NULL, '1wdewdwedwed', NULL, 0, '2021-04-26 15:24:47', '2021-04-27 17:23:28', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('293', '93', NULL, NULL, '1wdewdwedwed', NULL, 0, '2021-04-26 15:24:47', '2021-04-27 17:23:28', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('294', '93', NULL, NULL, '1wdewdwedwed', NULL, 0, '2021-04-26 15:24:47', '2021-04-27 17:23:28', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('295', '93', NULL, NULL, '1wdewdwedwed', NULL, 0, '2021-04-26 15:24:47', '2021-04-27 17:23:28', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('296', '93', NULL, NULL, '1wdewdwedwed', NULL, 0, '2021-04-26 15:24:47', '2021-04-27 17:23:28', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('297', '93', '0', 'payed', 'pay', '已支付', 0, '2021-04-27 17:25:09', '2021-07-16 15:10:23', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('298', '94', '0', '首页', 'qifu_imgtype', '首页banner', 0, '2021-04-27 18:18:31', '2021-04-27 18:18:31', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('299', '94', '1', '登录', 'qifu_imgtype', '登录的banner', 0, '2021-04-27 18:19:02', '2021-04-27 18:19:02', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('2b6bb88f22f084d344099baebf5377d1', 'bdb79cde74266f58bae93ca65f862efa', '25', '梁平区', 'regionType', '梁平区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:09', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('2b91b311831810416ef1491dbacf8ae8', '0f2920b2ec620be14598ff21dc92b238', 'IT服务', 'IT服务', 'jvsapp', NULL, 0, '2021-11-23 18:37:00', '2021-11-23 18:37:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('2c6b129d1bbf0da7ff17ea7636efc3aa', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_3014699_lzve1k0llfe.css', '表单组件', 'icon', 'http://at.alicdn.com/t/font_3014699_lzve1k0llfe.js', 0, '2021-12-14 11:10:52', '2021-12-14 11:10:52', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('2c771547ad0a69be77b97b9c0c0c792a', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_2940910_4dkiosscqim.css', '工作社交', 'icon', 'http://at.alicdn.com/t/font_2940910_4dkiosscqim.js', 0, '2021-11-16 11:08:14', '2021-11-16 11:08:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('2df560fcaeea55132915b0e1164e5405', '8fe320325009e65644c36d1b16574531', '总经理审批', '总经理审批', 'processTagType', '总经理审批', 0, '2021-08-02 19:37:09', '2021-08-11 09:47:48', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('2effc2ed59d4c5e750e45272b9db1ca4', '78a9b6c9cc81d2348ee2034fdf69d34d', '3', '提前结清', 'RepayStatus', '提前结清', 3, '2021-07-21 11:20:04', '2021-07-21 11:22:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('310', '95', 'IDENTITY_CARD', '身份证', 'jingkong', NULL, 0, '2021-04-30 12:03:10', '2021-04-30 12:03:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('311', '95', 'ARMY_IDENTITY_CARD', '军官证', 'jingkong', NULL, 0, '2021-04-30 12:03:10', '2021-04-30 12:03:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('312', '95', 'STUDENT_CARD', '学生证', 'jingkong', NULL, 0, '2021-04-30 12:03:10', '2021-04-30 12:03:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('313', '95', 'PASSPORT', '护照', 'jingkong', NULL, 0, '2021-04-30 12:03:10', '2021-04-30 12:03:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('314', '95', 'OTHER', '其他证件', 'jingkong', NULL, 0, '2021-04-30 12:03:10', '2021-04-30 12:03:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('3145a17807ba9ff981aa9c542f787435', 'fec53be21d29d0cbf2c8683724dc9e80', '4', '501~1000', 'companyScale', '501~1000', 0, '2021-08-11 10:09:33', '2021-08-11 10:09:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('31992236e20c646c6f49e53f8663883c', '0f2920b2ec620be14598ff21dc92b238', 'IT服务', 'IT服务', 'jvsapp', NULL, 0, '2021-12-14 17:45:30', '2021-12-14 17:46:46', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('32d730bc5fdad5e7ee5f58e1fd29a4cb', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'CANCEL', '已撤销', 'contractStatus', '已撤销', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('3306780a2af5b190e4bc45d3598e511d', '8fe320325009e65644c36d1b16574531', '发起放款审批', '发起放款审批', 'processTagType', '发起放款审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:48', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('340', '101', '1', '个人客户', '客户类型', '个人客户', 0, '2021-05-09 09:49:14', '2021-05-09 09:49:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('341', '101', '2', '企业客户', '客户类型', '企业客户', 0, '2021-05-09 09:49:14', '2021-05-09 09:49:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('342', '102', '1', '北京', '客户地域', '北京市', 0, '2021-05-09 09:53:00', '2021-05-09 09:53:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('343', '102', '2', '上海', '客户地域', '上海市', 0, '2021-05-09 09:53:00', '2021-05-09 09:53:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('344', '102', '3', '广州', '客户地域', '广州市', 0, '2021-05-09 09:53:00', '2021-05-09 09:53:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('345', '102', '4', '深圳', '客户地域', '深圳市', 0, '2021-05-09 09:53:00', '2021-05-09 09:53:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('346', '102', '5', '重庆', '客户地域', '重庆市', 0, '2021-05-09 09:53:00', '2021-05-09 09:53:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('347', '103', '0', 'true', 'T/F', '是', 0, '2021-05-09 09:57:10', '2021-05-09 09:57:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('348', '103', '1', 'false', 'T/F', '否', 0, '2021-05-09 09:57:10', '2021-05-09 09:57:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('349', '104', '0', '有效', '线索状态', '有效', 0, '2021-05-09 11:15:59', '2021-05-09 11:15:59', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('350', '104', '1', '无效', '线索状态', '无效', 0, '2021-05-09 11:15:59', '2021-05-09 11:15:59', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('351', '105', '1', '电话回访', '市场动作', '电话回访', 0, '2021-05-09 11:27:46', '2021-05-09 11:27:46', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('352', '105', '2', '技术交流', '市场动作', '技术交流', 0, '2021-05-09 11:27:46', '2021-05-09 11:27:46', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('353', '105', '3', '线下拜访', '市场动作', '线下拜访', 0, '2021-05-09 11:27:46', '2021-05-09 11:27:46', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('354', '105', '4', '礼品赠送', '市场动作', '礼品赠送', 0, '2021-05-09 11:27:46', '2021-05-09 11:27:46', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('355', '105', '5', '邀约参观', '市场动作', '邀约参观', 0, '2021-05-09 11:27:46', '2021-05-09 11:27:46', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('356', '107', '1', '在职', '在职状态', '在职', 0, '2021-05-11 19:50:24', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('357', '107', '1', '在职', '在职状态', '在职', 0, '2021-05-11 19:50:26', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('358', '107', '2', '离职', '在职状态', '离职', 0, '2021-05-11 19:50:27', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('359', '107', '2', '离职', '在职状态', '离职', 0, '2021-05-11 19:50:27', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('360', '107', '1', '在职', '在职状态', '在职', 0, '2021-05-11 19:50:29', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('361', '107', '2', '离职', '在职状态', '离职', 0, '2021-05-11 19:50:29', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('362', '107', '1', '在职', '在职状态', '在职', 0, '2021-05-11 19:50:29', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('363', '107', '2', '离职', '在职状态', '离职', 0, '2021-05-11 19:50:29', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('364', '107', '1', '在职', '在职状态', '在职', 0, '2021-05-11 19:50:29', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('365', '107', '2', '离职', '在职状态', '离职', 0, '2021-05-11 19:50:29', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('366', '107', '1', '在职', '在职状态', '在职', 0, '2021-05-11 19:50:29', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('367', '107', '2', '离职', '在职状态', '离职', 0, '2021-05-11 19:50:29', '2021-05-11 19:50:36', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('368', '107', '1', '在职', '在职状态', '在职', 0, '2021-05-11 19:50:36', '2021-05-11 19:50:36', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('369', '107', '2', '离职', '在职状态', '离职', 0, '2021-05-11 19:50:36', '2021-05-11 19:50:36', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('370', '109', '1', '1', '数据权限', '1', 0, '2021-05-17 14:55:38', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('371', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('372', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('373', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('374', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('375', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('37535399e972253b8a2a5960abc3e213', '1059438e8c74d226182966a2a0460597', '1', '等额本息', 'backType', '等额本息', 0, '2021-08-11 10:03:06', '2021-08-11 10:03:06', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('376', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('377', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('378', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('379', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('380', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('381', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('382', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('383', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('384', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('385', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('386', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('387', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('388', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('389', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('390', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('391', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:02', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('392', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:51', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('393', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:51', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('394', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:51', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('395', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:51', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('396', '109', NULL, NULL, '数据权限', NULL, 0, '2021-05-17 14:56:51', '2021-05-17 14:57:17', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('3b18948bcf36dc802f1be318de4f9f73', '91d9e2833ee1be20f98f09477766a7d4', '4', '其他', 'channelType', '其他', 0, '2021-08-11 10:25:37', '2021-08-11 10:25:37', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('3b37bbcf293f0d14c2ae957b3a3c32b0', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'SUBMIT', '待审核/已提交', 'contractStatus', '待审核/已提交', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('3b800d6e3e05f27a60756cba88e601e8', '78a9b6c9cc81d2348ee2034fdf69d34d', '6', '待还利息', 'RepayStatus', '待还利息', 6, '2021-07-21 11:20:04', '2021-07-21 11:22:22', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('3b8a2a6fa2e4e5d582bb29bea1a11ed6', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'DELETED', '已经删除/到作废列表去的依据', 'contractStatus', '已经删除/到作废列表去的依据', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('3ca268ec2de085ad2f713c03930f93e8', '8fe320325009e65644c36d1b16574531', '合同审核', '合同审核', 'processTagType', '合同审核', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:48', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('3db8b50fd197142d4bb5fdb533418a6f', '2a13720000aa29c7cc890ca05849fb3b', '2', '城镇户口', 'residenceType', '城镇户口', 0, '2021-07-19 11:26:01', '2021-07-19 11:26:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('423095bb0ad6a5eaa374340f5c69d87d', '52118494eca4204bee9ef42673cdaefa', '6', '6', '测试', '6', 0, '2021-09-14 17:04:02', '2021-09-14 17:04:02', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('43f4f8426cd028f71d90b7a59ca46c33', 'a9d16a3c562968e982fd64cdb6af1abc', '2', '二楼小会议室', 'roomType', '二楼小会议室', 0, '2021-08-09 17:46:07', '2021-08-09 17:46:07', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('441', '125', '1', '国有参股', 'fenbao', '国有参股', 0, '2021-05-25 10:31:04', '2021-05-25 10:31:04', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('442', '125', '2', '民营企业', 'fenbao', '民营企业', 0, '2021-05-25 10:31:05', '2021-05-25 10:31:05', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('443', '125', '3', '国有控股', 'fenbao', '国有控股', 0, '2021-05-25 10:31:05', '2021-05-25 10:31:05', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('444', '125', '4', '其他', 'fenbao', '其他', 0, '2021-05-25 10:31:05', '2021-05-25 10:31:05', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('44428d5fc4521132505d763a642952b4', 'df45ee3c48af5af53cf136be38d9a713', '0', '授信正常', '授信状态', '授信正常', 0, '2021-11-13 22:38:47', '2021-11-23 18:38:59', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('448cd20cd4a15ac862c4e8f6c8ef641b', '8fe320325009e65644c36d1b16574531', '总经理助理', '总经理助理', 'processTagType', '总经理助理', 0, '2021-08-02 19:37:09', '2021-08-11 09:47:48', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('449', '126', '1', '待生成', 'guarantee:letter_status', '待生成', 0, '2021-05-27 15:03:06', '2021-05-27 15:03:06', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('450', '126', '2', '待签署', 'guarantee:letter_status', '待签署', 0, '2021-05-27 15:03:06', '2021-05-27 15:03:06', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('451', '126', '3', '签署成功', 'guarantee:letter_status', '签署成功', 0, '2021-05-27 15:03:06', '2021-05-27 15:03:06', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('462', '130', '0', '待审批', '账款专家', '生成合同草稿未完成审批', 0, '2021-05-29 19:35:58', '2021-05-29 19:35:58', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('463', '130', '1', '待签约', '账款专家', '为完成签约或未对合同进行回传', 0, '2021-05-29 19:35:58', '2021-05-29 19:35:58', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('464', '130', '2', '已签收', '账款专家', '完成电子签章或者人工确认签署完成', 0, '2021-05-29 19:35:58', '2021-05-29 19:35:58', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('465', '131', '1', '服务合同', '合同类型', '服务合同', 0, '2021-05-30 09:17:45', '2021-05-30 09:17:45', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('466', '131', '2', '商贸合同', '合同类型', '产品销售', 0, '2021-05-30 09:17:45', '2021-05-30 09:17:45', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('467', '131', '3', '人事合同', '合同类型', '员工人事', 0, '2021-05-30 09:17:45', '2021-05-30 09:17:45', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('468', '131', '4', '战略协议', '合同类型', '战略合作', 0, '2021-05-30 09:17:45', '2021-05-30 09:17:45', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('469', '132', '1', '未核销', '核销状态', '未核销', 0, '2021-05-30 10:04:01', '2021-05-30 10:04:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('46b5665d89cef08aabb9b8b970337d69', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'CHECKED', '已审核未确认', 'contractStatus', '已审核未确认', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('46e99dc7bf6b75da7a2686f2ccecd850', '7e47aff5bb3f2a75d9d772c6a9f1fa3d', 'IN_PROGRESS', '会议中', 'meetingStatus', '会议中', 0, '2021-08-28 18:17:30', '2021-08-28 18:17:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('470', '132', '2', '已核销', '核销状态', '已核销', 0, '2021-05-30 10:04:01', '2021-05-30 10:04:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('471', '132', '3', '部分核销', '核销状态', '部分核销', 0, '2021-05-30 10:04:01', '2021-05-30 10:04:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('472', '133', '1', '在线签约', '签约模式', '在线签约', 0, '2021-05-30 10:26:24', '2021-05-30 10:26:24', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('473', '133', '2', '离线签约', '签约模式', '离线签约', 0, '2021-05-30 10:26:24', '2021-05-30 10:26:24', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('475', '108', 'test01', '测试01', 'df ', '描述01', 0, '2021-05-31 17:30:40', '2021-05-31 17:30:40', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('475bfcc8f3f6b9cb34c80fef0133b1fa', '78a9b6c9cc81d2348ee2034fdf69d34d', '1', '待还款', 'RepayStatus', '待还款', 1, '2021-07-21 11:20:04', '2021-07-21 11:22:06', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('476', '108', 'test02', '测试02', 'df ', '面搜02', 0, '2021-05-31 17:30:40', '2021-05-31 17:30:40', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('477', '135', '1', '增值业务专用发票6%', '发票类型', '6%专票', 0, '2021-05-31 21:40:25', '2021-05-31 21:40:25', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('47748732562cbe9d1a5857c76cc5860e', '36f93f7c4afff1d0071864e7020fbfe5', '5', '撤销', 'meetStatus', '撤销', 0, '2021-08-09 17:37:29', '2021-08-11 09:54:35', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('478', '135', '2', '增值业务专用发票13%', '发票类型', '3%专票', 0, '2021-05-31 21:40:25', '2021-05-31 21:40:25', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('479', '135', '3', '增值业务普通发票3%', '发票类型', '3%普票', 0, '2021-05-31 21:40:25', '2021-05-31 21:40:25', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('480', '136', '1', '已支付', 'pay', '已支付', 0, '2021-06-02 10:14:13', '2021-06-02 10:14:13', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('481', '136', '2', '支付完成', 'pay', '支付完成', 0, '2021-06-02 10:14:13', '2021-06-02 10:14:13', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('482', '136', '0', '未支付', 'pay', '未支付', 0, '2021-06-02 10:14:13', '2021-06-02 10:14:13', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('483', '136', '1', '已支付', 'pay', '已支付', 0, '2021-06-02 10:14:15', '2021-06-02 10:14:57', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('484', '136', '2', '支付完成', 'pay', '支付完成', 0, '2021-06-02 10:14:15', '2021-06-02 10:14:57', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('485', '136', '0', '未支付', 'pay', '未支付', 0, '2021-06-02 10:14:15', '2021-06-02 10:14:57', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('486', '139', '0', '待支付', 'pay', '订单状态待支付中', 0, '2021-06-02 16:17:01', '2021-06-02 16:17:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('487', '139', '1', '支付中 ', 'pay', '订单支付中状态', 0, '2021-06-02 16:17:01', '2021-06-02 16:17:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('488', '139', '2', '已支付', 'pay', '订单已经完成支付', 0, '2021-06-02 16:17:01', '2021-06-02 16:17:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('489', '139', NULL, NULL, 'pay', NULL, 0, '2021-06-02 16:17:01', '2021-11-23 19:06:59', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('490', '141', NULL, '服务', '需求分类', NULL, 0, '2021-06-07 14:17:45', '2021-06-07 14:17:45', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('491', '142', '1', '试用', 'jobstatus', '试用期员工', 0, '2021-06-12 10:31:27', '2021-06-12 10:31:27', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('492', '142', '2', '在职', 'jobstatus', NULL, 0, '2021-06-12 10:31:27', '2021-06-12 10:31:27', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('493', '142', '3', '离职', 'jobstatus', NULL, 0, '2021-06-12 10:31:27', '2021-06-12 10:31:27', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('494', '143', '1', '居民身份证', 'identitytype', '居民身份证', 0, '2021-06-12 11:35:33', '2021-06-12 11:35:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('495', '143', '2', '士官证', 'identitytype', '士官证', 0, '2021-06-12 11:35:33', '2021-06-12 11:35:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('496', '143', '3', '学生证', 'identitytype', '学生证', 0, '2021-06-12 11:35:33', '2021-06-12 11:35:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('497', '143', '4', '驾驶证', 'identitytype', '驾驶证', 0, '2021-06-12 11:35:33', '2021-06-12 11:35:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('498', '143', '5', '护照', 'identitytype', '护照', 0, '2021-06-12 11:35:33', '2021-06-12 11:35:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('499', '143', '6', '港澳通行证', 'identitytype', '港澳通行证', 0, '2021-06-12 11:35:33', '2021-06-12 11:35:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('4a08adaad561f69661314c3bbf9cffc5', '52118494eca4204bee9ef42673cdaefa', '数据值6', '标签名6', '测试', '描述', 0, '2021-08-04 15:25:25', '2021-09-14 17:04:10', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('4b2f5123799208ba1164320ff0a1e143', '0c293703b963c510f1245e1948ba3a85', '现金', '现金', 'moneyType', '现金', 0, '2021-07-23 10:47:01', '2021-07-23 10:47:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('4c375ebd7a74509a3c2ee1bc9bd64691', 'b5c521d6c646850d6715c46d8b67f683', '五会议室(负一楼)', '五会议室(负一楼)', 'meetingPlace', '五会议室(负一楼)', 0, '2021-08-28 18:16:47', '2021-08-28 18:16:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('4d76cf1ecd41c3416949cd12147204c5', '0f2920b2ec620be14598ff21dc92b238', '项目管理', '项目管理', 'jvsapp', NULL, 0, '2021-12-14 17:45:30', '2021-12-14 17:45:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('4ed9bf23b383a368d000bd0f47c0dd20', 'bdb79cde74266f58bae93ca65f862efa', '1', '万州区', 'regionType', '万州区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:09', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('4fd1c73200287e4b1f34fbe5d8ec3bec', 'b5c521d6c646850d6715c46d8b67f683', '一会议室(五楼)', '一会议室(五楼)', 'meetingPlace', '一会议室(五楼)', 0, '2021-08-28 18:16:47', '2021-08-28 18:16:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('500', '144', '0', '无试用期', 'probationperiod', '无试用期', 0, '2021-06-12 12:19:00', '2021-06-12 12:19:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('501', '144', '1', '试用期一个月', 'probationperiod', '试用期一个月', 0, '2021-06-12 12:19:00', '2021-06-12 12:19:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('502', '144', '2', '试用期两个月', 'probationperiod', '试用期两个月', 0, '2021-06-12 12:19:00', '2021-06-12 12:19:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('502f62cdde60771d02399b8cd345c084', 'bdb79cde74266f58bae93ca65f862efa', '18', '永川区', 'regionType', '永川区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:09', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('503', '144', '3', '试用期三个月', 'probationperiod', '试用期三个月', 0, '2021-06-12 12:19:00', '2021-06-12 12:19:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('504', '144', '6', '试用期六个月', 'probationperiod', '试用期六个月', 0, '2021-06-12 12:19:00', '2021-06-12 12:19:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('505', '145', '1', '前端', 'workposition', '前端', 0, '2021-06-12 12:22:28', '2021-06-12 12:22:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('506', '145', '2', '开发', 'workposition', '开发', 0, '2021-06-12 12:22:28', '2021-06-12 12:22:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('507', '145', '3', '测试', 'workposition', '测试', 0, '2021-06-12 12:22:28', '2021-06-12 12:22:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('508', '145', '4', '运维', 'workposition', '运维', 0, '2021-06-12 12:22:28', '2021-06-12 12:22:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('509', '145', '5', '产品', 'workposition', '产品', 0, '2021-06-12 12:22:28', '2021-06-12 12:22:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('50abcb970c05426fa5d32b92e30369e8', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'BACK', '驳回', 'contractStatus', '驳回', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('510', '145', '6', '人事', 'workposition', '人事', 0, '2021-06-12 12:22:28', '2021-06-12 12:22:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('511', '145', '7', '财务', 'workposition', '财务', 0, '2021-06-12 12:22:28', '2021-06-12 12:22:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('5118829ca0a2bef6d080c234973dbb68', 'df9cfe7d46016f258f7be32dfc84873d', '3', '个人独资企业营业执照', 'licenseType', '个人独资企业营业执照', 0, '2021-08-11 10:12:12', '2021-08-11 10:12:12', '', '0');
INSERT INTO `sys_dict_item` VALUES ('513', '138', '1', '支付状态', '实习生', '描述', 0, '2021-06-23 09:42:20', '2021-06-23 09:42:20', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('514', '138', '2', '支付过程', '实习生', '描述', 0, '2021-06-23 09:42:35', '2021-06-23 09:42:35', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('5335d331aa44da3ec99605eabe4ca503', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'END', '单据已作废', 'contractStatus', '单据已作废', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('53b84a438124c89f80d3574a06521e98', 'df9cfe7d46016f258f7be32dfc84873d', '4', '个人独资企业分支机构营业执照', 'licenseType', '个人独资企业分支机构营业执照', 0, '2021-08-11 10:12:12', '2021-08-11 10:12:12', '', '0');
INSERT INTO `sys_dict_item` VALUES ('54b0e6de8d33b41a859e8df4d6a47ef5', 'bdb79cde74266f58bae93ca65f862efa', '21', '铜梁区', 'regionType', '铜梁区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('56325080ee5d43a4f3300bc6f8c48a0a', 'a9d16a3c562968e982fd64cdb6af1abc', '1', '大厅会议室', 'roomType', '大厅会议室', 0, '2021-08-09 17:45:47', '2021-08-09 17:45:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('57300e3cf90b5bfb543398d2c3b18bcc', '0f2920b2ec620be14598ff21dc92b238', '办公协作', '办公协作', 'jvsapp', NULL, 0, '2021-12-14 17:45:30', '2021-12-14 17:45:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('574fd46ebafb7e357d2b834931f85896', 'df45ee3c48af5af53cf136be38d9a713', '1', '授信冻结', '授信状态', '授信冻结', 0, '2021-11-13 22:38:47', '2021-11-23 18:38:59', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('591', '168', '男', '男', '性别', NULL, 0, '2021-06-25 14:45:24', '2021-06-25 14:45:24', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('592', '168', '女', '女', '性别', NULL, 0, '2021-06-25 14:45:24', '2021-06-25 14:45:24', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('599', '93', '2', '2', 'pay', 'dvd 333', 0, '2021-07-06 10:17:28', '2021-07-16 15:10:23', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('5c8577f3b805d063a35cb972fc433d6c', '8fe320325009e65644c36d1b16574531', '录入尽职调查信息', '录入尽职调查信息', 'processTagType', '录入尽职调查信息', 0, '2021-07-23 10:17:22', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('5c9dba1ca29680766ef9e0f2a9a5ac27', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_2940989_87pnl15d274.css', '货品', 'icon', 'http://at.alicdn.com/t/font_2940989_87pnl15d274.js', 0, '2021-11-16 11:08:14', '2021-11-16 11:08:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('5cceee6be2bb0ae79e547996ba4e8333', '52118494eca4204bee9ef42673cdaefa', '8sfs', '8sdcsv', '测试', '8qwefgbvcxz', 0, '2021-09-29 10:34:25', '2021-09-29 10:34:25', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('5d1d6616b88961cedb73a42f6be6cebc', '614bb08c1dfcab7366a47e1aafca8374', 'dsc', 'scd', 'dsvfdvcsxz', 'sd', 0, '2021-11-01 15:33:22', '2021-11-01 15:33:22', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('5e42a1cf879d6f2e0c66b90e0669ead0', '78a9b6c9cc81d2348ee2034fdf69d34d', '5', '逾期(已确认)', 'RepayStatus', '逾期(已确认)', 5, '2021-07-21 11:20:04', '2021-07-21 11:22:18', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('5ea035b2ba090c5eef8d3426398a1910', '7e47aff5bb3f2a75d9d772c6a9f1fa3d', 'BREAK_UP', '已结束', 'meetingStatus', '已结束', 0, '2021-08-28 18:17:30', '2021-08-28 18:17:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('600', '138', '3', '3', '实习生', '3', 0, '2021-07-06 14:03:54', '2021-07-06 14:03:54', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('601', '137', NULL, NULL, '都是', NULL, 0, '2021-07-06 14:04:15', '2021-07-06 14:04:15', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('602', '174', NULL, NULL, '11', NULL, 0, '2021-07-06 14:11:57', '2021-07-06 14:11:57', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('603', '175', NULL, NULL, '22', NULL, 0, '2021-07-06 14:12:17', '2021-07-06 18:14:04', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('604', '176', '33', '33', '数据权限', '33', 0, '2021-07-06 14:12:51', '2021-07-06 14:12:51', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('605', '177', '1233', '1233', '123', '1233', 0, '2021-07-06 15:35:08', '2021-07-06 15:35:08', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('606', '177', '3211', '3211', '123', '3211', 0, '2021-07-06 15:35:20', '2021-07-06 15:35:20', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('609b32d3135fa6f7fdc190e8cd36f7d1', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'SEALING', '用印中', 'contractStatus', '用印中', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('617d071f126850b2d36c7eb25f4a825c', '78a9b6c9cc81d2348ee2034fdf69d34d', '8', '部分还款', 'RepayStatus', '部分还款', 8, '2021-07-21 11:20:04', '2021-07-21 11:22:27', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('61df564307d13a9f9111e7de72fce52d', 'bdb79cde74266f58bae93ca65f862efa', '15', '长寿区', 'regionType', '长寿区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('625', '185', '重庆进出口融资担保有限公司', 'website_title', 'website_title', '网站标题', 0, '2021-07-07 17:04:03', '2021-07-07 17:04:03', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('626', '186', '进出口担保,担保', 'SEO', 'SEO', 'SEO关键词', 0, '2021-07-07 17:04:56', '2021-07-07 17:04:56', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('627', '93', '3', '33', 'pay', '333', 0, '2021-07-12 09:30:43', '2021-07-12 16:09:10', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('628', '106', '123', '123', 'aa', '123', 0, '2021-07-12 09:30:59', '2021-07-12 09:30:59', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('629', '108', 'test03', 'test03', 'df ', 'test03', 0, '2021-07-12 09:31:22', '2021-07-12 09:31:22', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('6384a8370cca58bfb4299c377e926f24', '78a9b6c9cc81d2348ee2034fdf69d34d', '9', '部分还款', 'RepayStatus', '部分还款', 9, '2021-07-21 11:20:04', '2021-07-21 11:22:32', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('6460cfadba72b47c839066b316478717', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'AUDITING', '审核中', 'contractStatus', '审核中', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('6754c1ba7ab247dcc3190b4c40ea36a5', '52118494eca4204bee9ef42673cdaefa', '2', '2', '测试', '2', 0, '2021-07-16 15:59:04', '2021-07-16 15:59:04', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('698d13accc055dd8b83c74e5747188da', 'b5c521d6c646850d6715c46d8b67f683', '二会议室(四楼)', '二会议室(四楼)', 'meetingPlace', '二会议室(四楼)', 0, '2021-08-28 18:16:47', '2021-08-28 18:16:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('69c6153022acd50b38cefd293e9d8029', 'df9cfe7d46016f258f7be32dfc84873d', '1', '企业法人营业执照', 'licenseType', '企业法人营业执照', 0, '2021-08-11 10:12:12', '2021-08-11 10:12:12', '', '0');
INSERT INTO `sys_dict_item` VALUES ('6c0e897b11e3163416b1513c25eb055e', '8fe320325009e65644c36d1b16574531', '风控审查审批', '风控审查审批', 'processTagType', '风控审查审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('6ec0e94c4df6128cd8e732b04602677c', '78a9b6c9cc81d2348ee2034fdf69d34d', '7', '部分还款', 'RepayStatus', '部分还款', 7, '2021-07-21 11:20:04', '2021-07-21 11:22:25', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('75a06fdde04fa2eee1674affc981fbbb', '8fe320325009e65644c36d1b16574531', '执行监事审批', '执行监事审批', 'processTagType', '执行监事审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('77', '29', '1', '父子', NULL, NULL, 0, '2021-01-22 15:00:45', '2021-03-26 16:15:31', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('78', '29', '2', '兄弟', NULL, NULL, 0, '2021-01-22 15:00:45', '2021-03-26 16:15:31', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('7ac9a513093dfb5c4d979fe67cade856', 'bdb79cde74266f58bae93ca65f862efa', '14', '黔江区', 'regionType', '黔江区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('7ad136e048c6dbb82f2d4c828328d786', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_3014573_lb0nbbj3ag.css', '官方图标库', 'icon', 'http://at.alicdn.com/t/font_3014573_lb0nbbj3ag.js', 0, '2021-12-14 11:01:12', '2021-12-14 11:01:12', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('7b8fd841c4d571adc4e2845d6c8f39b3', '1948170e01b107463b41895ff2a78a44', 'AUDIT_INFO_3', '应出具保留意见的审核报告', 'auditOpinionTypes', '应出具保留意见的审核报告', 0, '2021-08-28 18:15:56', '2021-08-28 18:15:56', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('7d03e62afcef75c479af01e15da29d43', '1059438e8c74d226182966a2a0460597', '2', '利随本清', 'backType', '利随本清', 0, '2021-08-11 10:03:06', '2021-08-11 10:03:06', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('7d09ffd2df019d287573c216d90acdd8', 'bdb79cde74266f58bae93ca65f862efa', '5', '江北区', 'regionType', '江北区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('7d6d7bc4f4b985f4bcf7fa48a6698988', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'CONFIRMED', '已确认未用印', 'contractStatus', '已确认未用印', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('7f98e545517c6681bde831c7624f4ee5', '8dd3ab5dcb3edf54640e6b2f7c0d9148', 'RISK_HANDLE', '风险处置会', '会议类型', '风险处置会', 0, '2021-08-28 18:18:10', '2021-08-28 18:18:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('812a51f2bae8765f17020b01bc85d754', 'e113a39dd3d9b0f785b5b54a25b03e96', 'AA-', 'AA-', 'customerCreditLevel', 'AA-', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('823d89425535cacf184e4617f53da00d', 'e113a39dd3d9b0f785b5b54a25b03e96', 'BBB', 'BBB', 'customerCreditLevel', 'BBB', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('827c923452096c30dd0ef61949e783a9', '1dc044815217ef71d38868af260569fd', '3', '3', '业务类型', '3', 0, '2021-07-16 15:09:18', '2021-07-16 15:10:18', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('835a6730b10e6266f4efee0c25c34def', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_2940996_vb02yuy990j.css', '协同管理', 'icon', 'http://at.alicdn.com/t/font_2940996_vb02yuy990j.js', 0, '2021-11-16 11:08:14', '2021-11-16 11:08:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('847be73efce145d5760c21aa6a324890', '126ceca0dc43d3f84df03221a09b3c75', '3', '湖南分公司', 'parent_company', '湖南分公司', 0, '2021-08-12 11:41:53', '2021-08-12 11:41:53', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('8639e1cae14dcb14890d8da5ac2d9aa4', 'bdb79cde74266f58bae93ca65f862efa', '19', '南川区', 'regionType', '南川区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:11', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('884d95a240622145423b3e12337d9b0e', '126ceca0dc43d3f84df03221a09b3c75', '2', '四川分公司', 'parent_company', '四川分公司', 0, '2021-08-12 11:41:53', '2021-08-12 11:41:53', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('8a18bee6f6b249ad9892da7185651659', '614bb08c1dfcab7366a47e1aafca8374', 'sdc', 'd', '系统', 'd', 0, '2021-10-21 10:59:23', '2021-10-21 10:59:34', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('8bad4f3ddb790a4b2023593ab8cd992f', 'e113a39dd3d9b0f785b5b54a25b03e96', 'F', 'F', 'customerCreditLevel', 'F', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('90b4c081c3b07e178b7aa49492c07062', 'bdb79cde74266f58bae93ca65f862efa', '16', '江津区', 'regionType', '江津区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:11', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('92dded75dfe1ab019ab015de72d2b65a', 'ddf4becc7bdbb0c482bff051166cb54c', 'ZC', '草稿', '321', '草稿', 0, '2021-08-28 18:59:52', '2021-08-28 18:59:52', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('93de132971ad9e16be34634f1a8c7e58', '78a9b6c9cc81d2348ee2034fdf69d34d', '2', '提前部分还款', 'RepayStatus', '提前部分还款', 2, '2021-07-21 11:20:04', '2021-07-21 11:22:11', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('96fac833c5c7d8c085db5ff9cd631ab5', '52118494eca4204bee9ef42673cdaefa', '4', 'ctcyjhcvuykjh', '测试', 'tretarstdyfhjkb,', 0, '2021-09-28 17:37:49', '2021-09-28 17:38:05', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('97afe9d53cda98cf3cc6060f6e2cec6b', '8fe320325009e65644c36d1b16574531', '执行董事审批', '执行董事审批', 'processTagType', '执行董事审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('97bc19d5b7354483645e02a7060f724f', '36f93f7c4afff1d0071864e7020fbfe5', '3', '进行中', 'meetStatus', '进行中', 0, '2021-08-09 17:37:29', '2021-08-11 09:54:35', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('98f70239324d8a369603cab1301267e3', 'fec53be21d29d0cbf2c8683724dc9e80', '1', '全部', 'companyScale', '全部', 0, '2021-08-11 10:09:33', '2021-08-11 10:09:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('99c1fd009acecdb873ed74747c504b6b', '34b0822233113729862a929529c5d3da', 'gsd', 'sdfd', 'SEO关键词', 'sds', 0, '2021-11-04 11:04:10', '2021-11-23 18:39:07', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('9af38aa68c5f5fb716fcee7de6bf9eef', '52118494eca4204bee9ef42673cdaefa', '5', '5', '测试', '5', 0, '2021-08-04 15:25:05', '2021-08-04 15:25:05', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('9c9b200e02b2523cf1663f797e8db094', 'bdb79cde74266f58bae93ca65f862efa', '17', '合川区', 'regionType', '合川区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:11', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('9cb5d4112578e01af9a8bb52efb30187', '8fe320325009e65644c36d1b16574531', '打印申请单', '打印申请单', 'processTagType', '打印申请单', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('9cbffe0ed24b4ab57807f3d73aabdbb5', 'bdb79cde74266f58bae93ca65f862efa', '22', '潼南区', 'regionType', '潼南区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:11', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('9d34bb22cb7fcb7f1c1bff497bdd02a1', '7e47aff5bb3f2a75d9d772c6a9f1fa3d', 'NOT_BEGIN', '未开始', 'meetingStatus', '未开始', 0, '2021-08-28 18:17:30', '2021-08-28 18:17:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('9d8409c8d15604d5dcad8e87afbc763f', '0f2920b2ec620be14598ff21dc92b238', '项目任务', '项目任务', 'jvsapp', NULL, 0, '2021-11-23 18:37:00', '2021-11-23 18:37:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('9e293a702be8388c82205899a5bece10', '1948170e01b107463b41895ff2a78a44', 'AUDIT_INFO_5', '应出具带强调查事项无保留意见的审计报告', 'auditOpinionTypes', '应出具带强调查事项无保留意见的审计报告', 0, '2021-08-28 18:15:56', '2021-08-28 18:15:56', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('9e85d2bb7360e9ea41cfa59a466ee576', '8fe320325009e65644c36d1b16574531', '风控初审', '风控初审', 'processTagType', '风控初审', 0, '2021-07-23 10:17:47', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('9fda2ffb907890450a7fd5fc61c71d02', '8fe320325009e65644c36d1b16574531', '部门放款审批', '部门放款审批', 'processTagType', '部门放款审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('a18df47d5396242e29d9dbe255a2e978', '91d9e2833ee1be20f98f09477766a7d4', '1', '银行', 'channelType', '银行', 0, '2021-08-11 10:25:37', '2021-08-11 10:25:37', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('a4c636cda323f9e3b3171e0ca9ffa53f', '8dd3ab5dcb3edf54640e6b2f7c0d9148', 'PROJECT_REVIEW', '项目评审会', '会议类型', '项目评审会', 0, '2021-08-28 18:18:10', '2021-08-28 18:18:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('a5c09f93f726db4d44b12a9c54842679', '8fe320325009e65644c36d1b16574531', '风控部副经理审批', '风控部副经理审批', 'processTagType', '风控部副经理审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('a5d86c7704301a95fdea0d0cd77a66e0', 'dc06f6585bb28fc6fbee85a6335d83ad', '2', '民营', 'companyType', '民营', 0, '2021-08-11 10:07:37', '2021-08-11 10:07:37', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('a6b693e621c4cd16c7e219db327b525f', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'DRAFT', '草稿/暂存', 'contractStatus', '草稿/暂存', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('a766b943a3d143f126756d300448a8dd', '91d9e2833ee1be20f98f09477766a7d4', '3', '信托公司', 'channelType', '信托公司', 0, '2021-08-11 10:25:37', '2021-08-11 10:25:37', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('a818ac1b25fc98f47c1a98b28b84bb5e', 'fec53be21d29d0cbf2c8683724dc9e80', '3', '101~500', 'companyScale', '101~500', 0, '2021-08-11 10:09:33', '2021-08-11 10:09:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('a8975e8d64e25e302d3fa0d62a9336a6', '1dc044815217ef71d38868af260569fd', '2', '2', '业务类型', '2', 0, '2021-07-16 15:09:18', '2021-07-16 15:10:18', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('a945b6ec5c369cd58d240d61d87f8551', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_3014750_klpn8hmm37.css', '阿里云官方图标库', 'icon', 'http://at.alicdn.com/t/font_3014750_klpn8hmm37.js', 0, '2021-12-14 11:20:16', '2021-12-14 11:20:16', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('aaa2ca0e79c4629c89d8d7a59c8317a5', 'bdb79cde74266f58bae93ca65f862efa', '2', '涪陵区', 'regionType', '涪陵区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:12', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('aecfaf5e10304d2b3fe2ba211184690e', 'fec53be21d29d0cbf2c8683724dc9e80', '5', '1000+', 'companyScale', '1000+', 0, '2021-08-11 10:09:33', '2021-08-11 10:09:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('aeec81ffd3dfc7fb813cc4532b4976ee', 'e113a39dd3d9b0f785b5b54a25b03e96', 'BB', 'BB', 'customerCreditLevel', 'BB', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('af4fe0c13ec979426f15f6f8d4aedeb6', 'bdb79cde74266f58bae93ca65f862efa', '4', '大渡口区', 'regionType', '大渡口区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:12', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('af9578c6508c06b89d94ea18e0725e3d', '0c293703b963c510f1245e1948ba3a85', '其他', '其他', 'moneyType', '其他', 0, '2021-07-23 10:47:01', '2021-07-23 10:47:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('afa9b05239c9a3b81483722592ff9aa2', 'b5c521d6c646850d6715c46d8b67f683', '保理公司会议室', '保理公司会议室', 'meetingPlace', '保理公司会议室', 0, '2021-08-28 18:16:47', '2021-08-28 18:16:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('b138666fecc64c1d1a6e5b6be6c3a31f', '8fe320325009e65644c36d1b16574531', '关闭订单', '关闭订单', 'processTagType', '关闭订单', 0, '2021-08-02 19:30:24', '2021-08-11 09:47:49', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('b1f5753468264d53fc1013c157dedade', '8fe320325009e65644c36d1b16574531', '会计审批', '会计审批', 'processTagType', '会计审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:50', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('b322217867157b2a636a07f31fab2bff', '36f93f7c4afff1d0071864e7020fbfe5', '4', '已结束', 'meetStatus', '已结束', 0, '2021-08-09 17:37:29', '2021-08-11 09:54:35', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('b39c41152250b4beefc96f49164e7c0d', '0f2920b2ec620be14598ff21dc92b238', '人事', '人事', 'jvsapp', NULL, 0, '2021-11-23 18:37:00', '2021-11-23 18:37:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('b6a50f01dd0cd13f8daaf20b3412ecb9', 'b5c521d6c646850d6715c46d8b67f683', '四会议室(二楼)', '四会议室(二楼)', 'meetingPlace', '四会议室(二楼)', 0, '2021-08-28 18:16:47', '2021-08-28 18:16:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('b79e2db95748fe214245b79dc5e60116', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_3014678_sekvij9njwq.css', '字母', 'icon', 'http://at.alicdn.com/t/font_3014678_sekvij9njwq.js', 0, '2021-12-14 11:07:52', '2021-12-14 11:07:52', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('b7fb91083ef434f3da27f34554cb8aeb', 'bdb79cde74266f58bae93ca65f862efa', '12', '渝北区', 'regionType', '渝北区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:12', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('b9c0f182c4e508ff3d69727efc52c2e6', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_2941921_jlxpeybabmk.css', '工具', 'icon', 'http://at.alicdn.com/t/font_2941921_jlxpeybabmk.js', 0, '2021-11-16 15:38:18', '2021-11-16 15:38:18', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('bde8d1f136b7dcd7e082046894682e6d', 'bdb79cde74266f58bae93ca65f862efa', '20', '璧山区', 'regionType', '璧山区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:13', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('bf420d1856473409419806af9d6ddfae', '0f2920b2ec620be14598ff21dc92b238', '教育', '教育', 'jvsapp', NULL, 0, '2021-12-14 17:45:30', '2021-12-14 17:45:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('bf601879d1eb2ab9722de3d0b4c36e49', 'bdb79cde74266f58bae93ca65f862efa', '24', '开州区', 'regionType', '开州区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:13', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('c31ff31ca6a95ce691a335edc76dc7c5', 'df9cfe7d46016f258f7be32dfc84873d', '2', '营业执照', 'licenseType', '营业执照', 0, '2021-08-11 10:12:12', '2021-08-11 10:12:12', '', '0');
INSERT INTO `sys_dict_item` VALUES ('c699533396ed895f620d96d9aa5847c4', 'bdb79cde74266f58bae93ca65f862efa', '9', '北碚区', 'regionType', '北碚区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:13', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('c882775c73dfe394d559533981a119d5', '0f2920b2ec620be14598ff21dc92b238', '财务报销', '财务报销', 'jvsapp', NULL, 0, '2021-11-23 18:37:00', '2021-11-23 18:37:00', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('c99bae1554262fefb800b0e4e242c383', 'ab83069fc2b945f9ed7ce5529b1a7b79', 'RETURN', '已回传', 'contractStatus', '已回传', 0, '2021-08-28 18:12:10', '2021-08-28 18:12:10', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('c9ba013f3aad2f7351358cbc7bd68159', '8fe320325009e65644c36d1b16574531', '部门审核', '部门审核', 'processTagType', '部门审核', 0, '2021-07-23 10:17:47', '2021-08-11 09:47:50', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('cd47f3596e006b2b0adbb432ba026061', 'bdb79cde74266f58bae93ca65f862efa', '10', '綦江区', 'regionType', '綦江区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('cdc8be709706771f83be11580f2841b8', 'dc06f6585bb28fc6fbee85a6335d83ad', '1', '国企', 'companyType', '国企', 0, '2021-08-11 10:07:37', '2021-08-11 10:07:37', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('cff67407552b1fcbe1965a9a32278912', 'ddf4becc7bdbb0c482bff051166cb54c', 'ON', '已提交', '321', '已提交', 0, '2021-08-28 18:59:52', '2021-08-28 18:59:52', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('d28c963b6a79a2da5dcb4f84f21cde85', '1dc044815217ef71d38868af260569fd', '1', '1', '业务类型', '1', 0, '2021-07-16 15:09:17', '2021-07-16 15:10:18', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('d434341ed6150704ce3c516ea2a370ef', '7ab996b18d4fa75e8837369f4c071b4a', '1', '农信小贷', 'productType', '农信小贷', 0, '2021-08-11 10:14:39', '2021-08-11 10:14:39', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('d527ec617e71a89077857536cb7bf184', '91d9e2833ee1be20f98f09477766a7d4', '2', '证券公司', 'channelType', '证券公司', 0, '2021-08-11 10:25:37', '2021-08-11 10:25:37', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('d5c7a3bb3b4627779c4edba43a6a28d9', '2d1d0952c277f276b282d8105cf7caf4', '2', '3', '呃', '3', 0, '2021-09-03 17:36:45', '2021-09-03 17:36:48', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('d67d19f940d54640fa29af908abd67f1', '2b29f7d99083602aef6dce5f9c807fd6', 'http://at.alicdn.com/t/font_2940978_zj7nn5755ze.css', '简约', 'icon', 'http://at.alicdn.com/t/font_2940978_zj7nn5755ze.js', 0, '2021-11-16 11:08:14', '2021-11-16 11:08:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('d6d74535daf2229ed0f338d85011e572', 'bdb79cde74266f58bae93ca65f862efa', '11', '大足区', 'regionType', '大足区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('d73403c96afcfda97ef4a05b99ad747f', '78a9b6c9cc81d2348ee2034fdf69d34d', '4', '逾期', 'RepayStatus', '逾期', 4, '2021-07-21 11:20:04', '2021-07-21 11:22:16', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('d9eaf6c705ee71f27a252c99618e9e15', '2a13720000aa29c7cc890ca05849fb3b', '1', '农村户口', 'residenceType', '农村户口', 0, '2021-07-19 11:26:01', '2021-07-19 11:26:01', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('da362df6261a7333beded0c240437db1', 'df9cfe7d46016f258f7be32dfc84873d', '5', '合伙企业营业执照', 'licenseType', '合伙企业营业执照', 0, '2021-08-11 10:12:12', '2021-08-11 10:12:12', '', '0');
INSERT INTO `sys_dict_item` VALUES ('dc20be837e246563c8d685fa9de43d3f', '1948170e01b107463b41895ff2a78a44', 'AUDIT_INFO_2', '无保留意见的审核报告', 'auditOpinionTypes', '无保留意见的审核报告', 0, '2021-08-28 18:15:56', '2021-08-28 18:15:56', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('dc8f2d5d769b6a04d23843659f9c151e', '36f93f7c4afff1d0071864e7020fbfe5', '2', '待开启', 'meetStatus', '待开启', 0, '2021-08-09 17:37:29', '2021-08-11 09:54:35', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('dde30be59d5d2b6c617cac148603a22c', '1948170e01b107463b41895ff2a78a44', 'AUDIT_INFO_4', '否定意见的审核报告', 'auditOpinionTypes', '否定意见的审核报告', 0, '2021-08-28 18:15:56', '2021-08-28 18:15:56', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('e0021a4926623f625523446c062d6dd1', 'bdb79cde74266f58bae93ca65f862efa', '6', '沙坪坝区', 'regionType', '沙坪坝区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('e21ced886e03545eefcd024086639e8d', 'bdb79cde74266f58bae93ca65f862efa', '13', '巴南区', 'regionType', '巴南区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:14', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('e4f743720aded623959a803c54b69053', '52118494eca4204bee9ef42673cdaefa', '1', '1', '测试', '1', 0, '2021-07-16 15:59:04', '2021-07-16 15:59:04', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('e7c18e2a46b7d82598052176b73f6b3a', 'e113a39dd3d9b0f785b5b54a25b03e96', 'B', 'B', 'customerCreditLevel', 'B', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('e7e9bfd6b9a975672aec68ce1acd32ec', 'b5c521d6c646850d6715c46d8b67f683', '三会议室(三楼)', '三会议室(三楼)', 'meetingPlace', '三会议室(三楼)', 0, '2021-08-28 18:16:47', '2021-08-28 18:16:47', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('e8c95f73055415e95420024d785c5c43', 'bdb79cde74266f58bae93ca65f862efa', '23', '荣昌区', 'regionType', '荣昌区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:15', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('eb074a42fd9bf2a11d0cf9f35c17220b', '2d1d0952c277f276b282d8105cf7caf4', '为', '未测', '呃', NULL, 0, '2021-09-03 17:36:38', '2021-09-03 17:36:48', NULL, '1');
INSERT INTO `sys_dict_item` VALUES ('eb0ff37d2b5fffb4c7e8ba4afc26f715', '8fe320325009e65644c36d1b16574531', '风控复审', '风控复审', 'processTagType', '风控复审', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:50', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('eb70d09865c974618103830c9dfa76d6', 'e113a39dd3d9b0f785b5b54a25b03e96', 'A+', 'A+', 'customerCreditLevel', 'A+', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('ede016827329cab9fd3af7519adc94c2', 'e113a39dd3d9b0f785b5b54a25b03e96', 'AAA', 'AAA', 'customerCreditLevel', 'AAA', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('ef0ba42b446f86a4e58b0e31e78fc4ec', '614bb08c1dfcab7366a47e1aafca8374', '5ds', '3ws', 'dsvfdvcsxz', '3wd', 0, '2021-11-01 15:33:36', '2021-11-01 15:33:36', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('f5114297aa352d5bccfa3dcc3c501af5', 'bdb79cde74266f58bae93ca65f862efa', '8', '南岸区', 'regionType', '南岸区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:15', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('f57f331df2da9f09afc95d99b67c0383', 'bdb79cde74266f58bae93ca65f862efa', '3', '渝中区', 'regionType', '渝中区', 0, '2021-07-26 14:21:22', '2021-08-11 09:58:15', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('f7d9526875dc0dd22e7aed3418ce6b4f', '8fe320325009e65644c36d1b16574531', '风控部负责人审批', '风控部负责人审批', 'processTagType', '风控部负责人审批', 0, '2021-07-23 10:20:51', '2021-08-11 09:47:50', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('f82803033990d46e26c5a27f0e98d44c', '36f93f7c4afff1d0071864e7020fbfe5', '1', '草稿箱', 'meetStatus', '草稿箱', 0, '2021-08-09 17:37:29', '2021-08-11 09:54:35', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('f8b81f6b03a65ffba3bee443abbc1941', '52118494eca4204bee9ef42673cdaefa', '3', '3', '测试', '3', 0, '2021-07-16 15:59:04', '2021-07-16 15:59:04', NULL, '0');
INSERT INTO `sys_dict_item` VALUES ('fa1083f6ec608ef72921833fe49bd5fe', 'e113a39dd3d9b0f785b5b54a25b03e96', 'A', 'A', 'customerCreditLevel', 'A', 0, '2021-08-28 18:14:33', '2021-08-28 18:14:33', NULL, '0');

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
  `create_time` timestamp NULL DEFAULT NULL COMMENT '上传时间',
  `file_path` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件地址',
  `size` bigint(11) NULL DEFAULT NULL COMMENT '文件大小 单位:字节',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务备注',
  `label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `file_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '占位字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件统一管理，不包含租户信息，只做文件转发和统一保存管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES ('3231c2c13441332cd9084d17ef6fe8cf', 'fdas/未命名文件/2022-09-05-751506660198879232-69bd08631fdd4f47af4110a04e46e4c6.document_html', 'document_html', 'document-mgr', 'fdas/未命名文件', '2022-09-05 18:19:10', 'fdas/未命名文件/2022-09-05-751506660198879232-69bd08631fdd4f47af4110a04e46e4c6.document_html', 36, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_gateway_code
-- ----------------------------
DROP TABLE IF EXISTS `sys_gateway_code`;
CREATE TABLE `sys_gateway_code`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键ID',
  `code` int(4) NOT NULL COMMENT '具体错误码确定',
  `msg` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '错误信息确定，与代码保持一致',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '其它信息备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `msg`(`msg`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'code异常码转换关系和业务类型区分表-主要用于网关返回给前端 业务返回，默认在feign标准返回给上层,代码示例为new BusinessException(\"这是一个测试异常\")' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_gateway_code
-- ----------------------------
INSERT INTO `sys_gateway_code` VALUES ('1', -2, '登录已失效', '退出的异常code码');
INSERT INTO `sys_gateway_code` VALUES ('2', -2, 'access token has expired,please reacquire token', '退出的异常code码');

-- ----------------------------
-- Table structure for sys_gateway_ignore_path
-- ----------------------------
DROP TABLE IF EXISTS `sys_gateway_ignore_path`;
CREATE TABLE `sys_gateway_ignore_path`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键ID',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_gateway_ignore_path
-- ----------------------------
INSERT INTO `sys_gateway_ignore_path` VALUES ('11', '/auth/**', NULL);
INSERT INTO `sys_gateway_ignore_path` VALUES ('112', '/gateway/**', '多关消息注册方法');
INSERT INTO `sys_gateway_ignore_path` VALUES ('12', '/mgr/**', '开发环境默认配置所有,因为现在超级管理员所有权限都没有所以需要放开所有的');
INSERT INTO `sys_gateway_ignore_path` VALUES ('145', '/api/**', NULL);
INSERT INTO `sys_gateway_ignore_path` VALUES ('146', '/oauth/api/dict/list/*', '系统字典查询接口');
INSERT INTO `sys_gateway_ignore_path` VALUES ('148', '/mgr/mail/websocket', '邮件Websocket放开');

-- ----------------------------
-- Table structure for sys_gateway_route
-- ----------------------------
DROP TABLE IF EXISTS `sys_gateway_route`;
CREATE TABLE `sys_gateway_route`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键ID',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '备注说明',
  `uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '网关路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_gateway_route
-- ----------------------------
INSERT INTO `sys_gateway_route` VALUES ('auth', '/auth/**', '权限', 'lb://jvs-auth');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '岗位名称',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除  -1：已删除  0：正常',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '租户ID默认1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES ('0f115f4a3c90e156cab0f7eeaf5d0545', '删除岗位', '2021-08-31 23:06:17', '2021-08-31 23:19:49', 1, '1');
INSERT INTO `sys_job` VALUES ('1820ef870f0d7e50f9222fca935ceed3', 'sdds', '2021-10-19 18:50:06', NULL, 0, '9f8765b3faae36d71728369665cc7d33');
INSERT INTO `sys_job` VALUES ('23b62ab4763bc1b8b525f33808978b91', 'afsd', '2021-08-31 22:47:23', NULL, 0, '1');
INSERT INTO `sys_job` VALUES ('3d36eb72fdf455bb2cf8b81efc765230', 'test002', '2021-11-03 01:59:24', NULL, 0, 'b7566ac352652eacca9b383120e7d566');
INSERT INTO `sys_job` VALUES ('56fd3223254de465640180496f05f182', 'qwcs', '2021-10-28 19:46:58', NULL, 0, '1');
INSERT INTO `sys_job` VALUES ('7141fd3ab3ec51e889a4447bc624cc18', 'dAW15@!#', '2021-09-17 00:07:37', '2021-09-17 00:07:57', 0, 'default');
INSERT INTO `sys_job` VALUES ('813f3bc45d534ac6268f842b28a20fb9', '测试岗位', '2021-11-02 00:19:55', '2021-11-02 00:20:55', 0, 'b7566ac352652eacca9b383120e7d566');
INSERT INTO `sys_job` VALUES ('84437a52181da635935116f851452755', 'zaidgangwie', '2021-10-14 19:03:41', '2021-10-15 18:38:08', 1, '9f8765b3faae36d71728369665cc7d33');
INSERT INTO `sys_job` VALUES ('93ad479cae30062c78372ed465651084', '的SVv', '2021-09-16 22:40:46', NULL, 0, 'default');
INSERT INTO `sys_job` VALUES ('9fb43cc4f3503ce99bd7e51b9b1093d0', '后期岗位', '2021-10-08 22:17:35', '2021-10-11 18:52:31', 0, '9f8765b3faae36d71728369665cc7d33');
INSERT INTO `sys_job` VALUES ('a39a93122fe800574ca4d4ef502602a0', '测试岗位专用12', '2021-08-31 23:01:14', '2021-08-31 23:04:07', 0, '1');
INSERT INTO `sys_job` VALUES ('c6a1f3f505f3fe2071c9c0fbbdfefcba', 'asf', '2021-09-01 03:30:14', '2021-09-17 00:14:34', 1, '1');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `business_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名',
  `function_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '功能名',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `method_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名',
  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类名',
  `thread_user` json NULL COMMENT '当前用户对象JSON',
  `parameters` json NULL COMMENT '请求参数对象JSON',
  `tid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT 'tid',
  `version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本号',
  `consuming_time` int(11) NULL DEFAULT NULL COMMENT '耗时',
  `return_obj` json NULL COMMENT '返回对象JSON',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '返回状态是否是成功',
  `ip` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求IP地址',
  `elements` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常数据JSON',
  `exception_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常消息数据',
  `env` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '环境参数',
  `api` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'api地址',
  `type` tinyint(1) NULL DEFAULT 1 COMMENT '日志类型:1-AOP拦截,2-过程中的日志,3-自定义日志',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT '用户名称',
  `create_date` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT '租户id',
  `operation_type` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT '操作类型',
  `client_id` char(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '--' COMMENT '终端',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `start_time`(`start_time`) USING BTREE,
  INDEX `client_id`(`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '请求日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES ('1567066019332694018', 'jvs-auth-mgr', '用户登录信息-获取当前登录用户所在租户信息', '2022-09-06 16:23:48', '2022-09-06 16:23:48', 'thisTenant', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 43, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/this/tenant', 1, 'admin', '2022-09-06 16:23:48', '1', '获取当前登录用户所在租户信息', 'frame');
INSERT INTO `sys_log` VALUES ('1567066019332694019', 'jvs-auth-mgr', '用户消息-未读总数', '2022-09-06 16:23:48', '2022-09-06 16:23:48', 'unread', 'cn.bctools.auth.controller.UserMessageController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 43, '\"return:{\\\"code\\\":0,\\\"data\\\":0,\\\"msg\\\":\\\"success\\\",\\\"timestamp\\\":\\\"2022-09-06 16:23:48 337\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/userlog/unread', 1, 'admin', '2022-09-06 16:23:48', '1', '未读总数', 'frame');
INSERT INTO `sys_log` VALUES ('1567066019936673793', 'jvs-auth-mgr', '用户登录信息-用户登录后获取当前菜单', '2022-09-06 16:23:48', '2022-09-06 16:23:48', 'menu', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[\"frame\"]', '--', '黎铃果', 193, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/menu/frame', 1, 'admin', '2022-09-06 16:23:48', '1', '用户登录后获取当前菜单', 'frame');
INSERT INTO `sys_log` VALUES ('1567066033421361154', 'jvs-auth-mgr', '用户登录信息-获取公告', '2022-09-06 16:23:52', '2022-09-06 16:23:52', 'bulletins', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[\"frame\"]', '--', '黎铃果', 41, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/bulletin/frame', 1, 'admin', '2022-09-06 16:23:52', '1', '获取公告', 'frame');
INSERT INTO `sys_log` VALUES ('1567066035484958721', 'jvs-auth-mgr', '用户登录信息-获取当前登录用户所在租户信息', '2022-09-06 16:23:52', '2022-09-06 16:23:52', 'thisTenant', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 29, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/this/tenant', 1, 'admin', '2022-09-06 16:23:52', '1', '获取当前登录用户所在租户信息', 'frame');
INSERT INTO `sys_log` VALUES ('1567066035610787841', 'jvs-auth-mgr', '图标-所有图标', '2022-09-06 16:23:52', '2022-09-06 16:23:52', 'all', 'cn.bctools.auth.controller.IconController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 68, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/icon/all', 1, 'admin', '2022-09-06 16:23:52', '1', '所有图标', 'frame');
INSERT INTO `sys_log` VALUES ('1567066039100448769', 'jvs-auth-mgr', '终端管理-分页查询', '2022-09-06 16:23:53', '2022-09-06 16:23:53', 'page', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[null, {\"id\": \"\", \"icon\": \"\", \"logo\": \"\", \"name\": \"\", \"bgImg\": \"\", \"appKey\": \"\", \"delFlag\": null, \"createBy\": \"\", \"updateBy\": \"\", \"appSecret\": \"\", \"describes\": \"\", \"createById\": \"\", \"createTime\": null, \"updateTime\": null, \"loginDomain\": \"\", \"autoApproveScopes\": \"\", \"authorizedGrantTypes\": [], \"additionalInformation\": null, \"registeredRedirectUris\": [], \"accessTokenValiditySeconds\": null, \"refreshTokenValiditySeconds\": null}]', '--', '黎铃果', 67, '\"return:{\\\"code\\\":0,\\\"data\\\":{\\\"current\\\":1,\\\"hitCount\\\":false,\\\"optimizeCountSql\\\":true,\\\"orders\\\":[],\\\"pages\\\":1,\\\"records\\\":[{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"frame\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************re3.\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"autoApproveScopes\\\":\\\"false\\\",\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"PC端\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"1ff53290763d889bb85813d1845b3bc8c\\\",\\\"loginDomain\\\":\\\"localhost\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"JVS服务平台\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"},{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"knowledge\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************lrem\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"knowledge\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"bbb46b6e0f779fdb156371548b000ba6\\\",\\\"loginDomain\\\":\\\"knowledge.bctools.cn\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"文档库\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"}],\\\"searchCount\\\":true,\\\"size\\\":20,\\\"total\\\":2},\\\"msg\\\":\\\"success\\\",\\\"timestamp\\\":\\\"2022-09-06 16:23:53 057\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/page', 1, 'admin', '2022-09-06 16:23:53', '1', '分页查询', 'frame');
INSERT INTO `sys_log` VALUES ('1567066127466045442', 'jvs-auth-mgr', '终端管理-添加应用', '2022-09-06 16:24:14', '2022-09-06 16:24:14', 'save', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[{\"id\": \"7291d4dc628bb50d4cc20bdb5c42873e\", \"icon\": \"\", \"logo\": \"\", \"name\": \"任务管理\", \"bgImg\": \"\", \"appKey\": \"client_id\", \"delFlag\": null, \"createBy\": \"admin\", \"updateBy\": \"admin\", \"appSecret\": \"$2a$10$5jlP0bJhnUa/tgKyOiNQEu3izgEG/XZb49T0Lp13sX.fBn3TL4jqW\", \"describes\": \"任务管理\", \"createById\": \"1\", \"createTime\": \"2022-09-06T16:24:14.110\", \"updateTime\": \"2022-09-06T16:24:14.110\", \"loginDomain\": \"\", \"autoApproveScopes\": \"\", \"authorizedGrantTypes\": [\"password\", \"refresh_token\", \"authorization_code\", \"client_credentials\"], \"additionalInformation\": null, \"registeredRedirectUris\": [], \"accessTokenValiditySeconds\": null, \"refreshTokenValiditySeconds\": null}]', '--', '黎铃果', 122, '\"return:{\\\"code\\\":0,\\\"data\\\":true,\\\"msg\\\":\\\"添加成功\\\",\\\"timestamp\\\":\\\"2022-09-06 16:24:14 124\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/save', 1, 'admin', '2022-09-06 16:24:14', '1', '添加应用', 'frame');
INSERT INTO `sys_log` VALUES ('1567066128342654977', 'jvs-auth-mgr', '终端管理-分页查询', '2022-09-06 16:24:14', '2022-09-06 16:24:14', 'page', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[null, {\"id\": \"\", \"icon\": \"\", \"logo\": \"\", \"name\": \"\", \"bgImg\": \"\", \"appKey\": \"\", \"delFlag\": null, \"createBy\": \"\", \"updateBy\": \"\", \"appSecret\": \"\", \"describes\": \"\", \"createById\": \"\", \"createTime\": null, \"updateTime\": null, \"loginDomain\": \"\", \"autoApproveScopes\": \"\", \"authorizedGrantTypes\": [], \"additionalInformation\": null, \"registeredRedirectUris\": [], \"accessTokenValiditySeconds\": null, \"refreshTokenValiditySeconds\": null}]', '--', '黎铃果', 27, '\"return:{\\\"code\\\":0,\\\"data\\\":{\\\"current\\\":1,\\\"hitCount\\\":false,\\\"optimizeCountSql\\\":true,\\\"orders\\\":[],\\\"pages\\\":1,\\\"records\\\":[{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"frame\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************re3.\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"autoApproveScopes\\\":\\\"false\\\",\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"PC端\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"1ff53290763d889bb85813d1845b3bc8c\\\",\\\"loginDomain\\\":\\\"localhost\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"JVS服务平台\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"},{\\\"accessTokenValiditySeconds\\\":3000,\\\"appKey\\\":\\\"client_id\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************4jqW\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"createBy\\\":\\\"admin\\\",\\\"createById\\\":\\\"1\\\",\\\"createTime\\\":\\\"2022-09-06T16:24:14\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"任务管理\\\",\\\"id\\\":\\\"7291d4dc628bb50d4cc20bdb5c42873e\\\",\\\"name\\\":\\\"任务管理\\\",\\\"refreshTokenValiditySeconds\\\":30000,\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2022-09-06T16:24:14\\\"},{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"knowledge\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************lrem\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"knowledge\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"bbb46b6e0f779fdb156371548b000ba6\\\",\\\"loginDomain\\\":\\\"knowledge.bctools.cn\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"文档库\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"}],\\\"searchCount\\\":true,\\\"size\\\":20,\\\"total\\\":3},\\\"msg\\\":\\\"success\\\",\\\"timestamp\\\":\\\"2022-09-06 16:24:14 343\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/page', 1, 'admin', '2022-09-06 16:24:14', '1', '分页查询', 'frame');
INSERT INTO `sys_log` VALUES ('1567066436858880001', 'jvs-auth-mgr', '终端管理-分页查询', '2022-09-06 16:25:28', '2022-09-06 16:25:28', 'page', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[null, {\"id\": \"\", \"icon\": \"\", \"logo\": \"\", \"name\": \"\", \"bgImg\": \"\", \"appKey\": \"\", \"delFlag\": null, \"createBy\": \"\", \"updateBy\": \"\", \"appSecret\": \"\", \"describes\": \"\", \"createById\": \"\", \"createTime\": null, \"updateTime\": null, \"loginDomain\": \"\", \"autoApproveScopes\": \"\", \"authorizedGrantTypes\": [], \"additionalInformation\": null, \"registeredRedirectUris\": [], \"accessTokenValiditySeconds\": null, \"refreshTokenValiditySeconds\": null}]', '--', '黎铃果', 36, '\"return:{\\\"code\\\":0,\\\"data\\\":{\\\"current\\\":1,\\\"hitCount\\\":false,\\\"optimizeCountSql\\\":true,\\\"orders\\\":[],\\\"pages\\\":1,\\\"records\\\":[{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"frame\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************re3.\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"autoApproveScopes\\\":\\\"false\\\",\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"PC端\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"1ff53290763d889bb85813d1845b3bc8c\\\",\\\"loginDomain\\\":\\\"localhost\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"JVS服务平台\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"},{\\\"accessTokenValiditySeconds\\\":3000,\\\"appKey\\\":\\\"client_id\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************4jqW\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"createBy\\\":\\\"admin\\\",\\\"createById\\\":\\\"1\\\",\\\"createTime\\\":\\\"2022-09-06T16:24:14\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"任务管理\\\",\\\"id\\\":\\\"7291d4dc628bb50d4cc20bdb5c42873e\\\",\\\"name\\\":\\\"任务管理\\\",\\\"refreshTokenValiditySeconds\\\":30000,\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2022-09-06T16:24:14\\\"},{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"knowledge\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************lrem\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"knowledge\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"bbb46b6e0f779fdb156371548b000ba6\\\",\\\"loginDomain\\\":\\\"knowledge.bctools.cn\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"文档库\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"}],\\\"searchCount\\\":true,\\\"size\\\":20,\\\"total\\\":3},\\\"msg\\\":\\\"success\\\",\\\"timestamp\\\":\\\"2022-09-06 16:25:27 891\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/page', 1, 'admin', '2022-09-06 16:25:28', '1', '分页查询', 'frame');
INSERT INTO `sys_log` VALUES ('1567067393382490113', 'jvs-auth-mgr', '用户登录信息-获取当前登录用户所在租户信息', '2022-09-06 16:29:16', '2022-09-06 16:29:16', 'thisTenant', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 18, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/this/tenant', 1, 'admin', '2022-09-06 16:29:16', '1', '获取当前登录用户所在租户信息', 'frame');
INSERT INTO `sys_log` VALUES ('1567067393382490114', 'jvs-auth-mgr', '图标-所有图标', '2022-09-06 16:29:16', '2022-09-06 16:29:16', 'all', 'cn.bctools.auth.controller.IconController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 10, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/icon/all', 1, 'admin', '2022-09-06 16:29:16', '1', '所有图标', 'frame');
INSERT INTO `sys_log` VALUES ('1567067396591132674', 'jvs-auth-mgr', '用户登录信息-获取当前登录用户所在租户信息', '2022-09-06 16:29:17', '2022-09-06 16:29:17', 'thisTenant', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 17, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/this/tenant', 1, 'admin', '2022-09-06 16:29:17', '1', '获取当前登录用户所在租户信息', 'frame');
INSERT INTO `sys_log` VALUES ('1567067396649852929', 'jvs-auth-mgr', '用户消息-未读总数', '2022-09-06 16:29:17', '2022-09-06 16:29:17', 'unread', 'cn.bctools.auth.controller.UserMessageController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 19, '\"return:{\\\"code\\\":0,\\\"data\\\":0,\\\"msg\\\":\\\"success\\\",\\\"timestamp\\\":\\\"2022-09-06 16:29:16 720\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/userlog/unread', 1, 'admin', '2022-09-06 16:29:17', '1', '未读总数', 'frame');
INSERT INTO `sys_log` VALUES ('1567067397153169410', 'jvs-auth-mgr', '用户登录信息-用户登录后获取当前菜单', '2022-09-06 16:29:17', '2022-09-06 16:29:17', 'menu', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[\"frame\"]', '--', '黎铃果', 124, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/menu/frame', 1, 'admin', '2022-09-06 16:29:17', '1', '用户登录后获取当前菜单', 'frame');
INSERT INTO `sys_log` VALUES ('1567067397836840961', 'jvs-auth-mgr', '用户登录信息-获取公告', '2022-09-06 16:29:17', '2022-09-06 16:29:17', 'bulletins', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[\"frame\"]', '--', '黎铃果', 12, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/bulletin/frame', 1, 'admin', '2022-09-06 16:29:17', '1', '获取公告', 'frame');
INSERT INTO `sys_log` VALUES ('1567067398889611266', 'jvs-auth-mgr', '图标-所有图标', '2022-09-06 16:29:17', '2022-09-06 16:29:17', 'all', 'cn.bctools.auth.controller.IconController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 12, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/icon/all', 1, 'admin', '2022-09-06 16:29:17', '1', '所有图标', 'frame');
INSERT INTO `sys_log` VALUES ('1567067398948331521', 'jvs-auth-mgr', '用户登录信息-获取当前登录用户所在租户信息', '2022-09-06 16:29:17', '2022-09-06 16:29:17', 'thisTenant', 'cn.bctools.auth.controller.UserIndexController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', NULL, '--', '黎铃果', 18, NULL, 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/index/this/tenant', 1, 'admin', '2022-09-06 16:29:17', '1', '获取当前登录用户所在租户信息', 'frame');
INSERT INTO `sys_log` VALUES ('1567067402295386113', 'jvs-auth-mgr', '终端管理-分页查询', '2022-09-06 16:29:18', '2022-09-06 16:29:18', 'page', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[null, {\"id\": \"\", \"icon\": \"\", \"logo\": \"\", \"name\": \"\", \"bgImg\": \"\", \"appKey\": \"\", \"delFlag\": null, \"createBy\": \"\", \"updateBy\": \"\", \"appSecret\": \"\", \"describes\": \"\", \"createById\": \"\", \"createTime\": null, \"updateTime\": null, \"loginDomain\": \"\", \"autoApproveScopes\": \"\", \"authorizedGrantTypes\": [], \"additionalInformation\": null, \"registeredRedirectUris\": [], \"accessTokenValiditySeconds\": null, \"refreshTokenValiditySeconds\": null}]', '--', '黎铃果', 27, '\"return:{\\\"code\\\":0,\\\"data\\\":{\\\"current\\\":1,\\\"hitCount\\\":false,\\\"optimizeCountSql\\\":true,\\\"orders\\\":[],\\\"pages\\\":1,\\\"records\\\":[{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"frame\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************re3.\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"autoApproveScopes\\\":\\\"false\\\",\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"PC端\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"1ff53290763d889bb85813d1845b3bc8c\\\",\\\"loginDomain\\\":\\\"localhost\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"JVS服务平台\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"},{\\\"accessTokenValiditySeconds\\\":3000,\\\"appKey\\\":\\\"client_id\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************4jqW\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"createBy\\\":\\\"admin\\\",\\\"createById\\\":\\\"1\\\",\\\"createTime\\\":\\\"2022-09-06T16:24:14\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"任务管理\\\",\\\"id\\\":\\\"7291d4dc628bb50d4cc20bdb5c42873e\\\",\\\"name\\\":\\\"任务管理\\\",\\\"refreshTokenValiditySeconds\\\":30000,\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2022-09-06T16:24:14\\\"},{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"knowledge\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************lrem\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"knowledge\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"bbb46b6e0f779fdb156371548b000ba6\\\",\\\"loginDomain\\\":\\\"knowledge.bctools.cn\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"文档库\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"}],\\\"searchCount\\\":true,\\\"size\\\":20,\\\"total\\\":3},\\\"msg\\\":\\\"success\\\",\\\"timestamp\\\":\\\"2022-09-06 16:29:18 076\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/page', 1, 'admin', '2022-09-06 16:29:18', '1', '分页查询', 'frame');
INSERT INTO `sys_log` VALUES ('1567067524513210369', 'jvs-auth-mgr', '终端管理-删除应用', '2022-09-06 16:29:47', '2022-09-06 16:29:47', 'delete', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[\"7291d4dc628bb50d4cc20bdb5c42873e\"]', '--', '黎铃果', 15, '\"return:{\\\"code\\\":0,\\\"data\\\":true,\\\"msg\\\":\\\"删除成功\\\",\\\"timestamp\\\":\\\"2022-09-06 16:29:47 212\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/7291d4dc628bb50d4cc20bdb5c42873e', 1, 'admin', '2022-09-06 16:29:47', '1', '删除应用', 'frame');
INSERT INTO `sys_log` VALUES ('1567067525490483201', 'jvs-auth-mgr', '终端管理-分页查询', '2022-09-06 16:29:47', '2022-09-06 16:29:47', 'page', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[null, {\"id\": \"\", \"icon\": \"\", \"logo\": \"\", \"name\": \"\", \"bgImg\": \"\", \"appKey\": \"\", \"delFlag\": null, \"createBy\": \"\", \"updateBy\": \"\", \"appSecret\": \"\", \"describes\": \"\", \"createById\": \"\", \"createTime\": null, \"updateTime\": null, \"loginDomain\": \"\", \"autoApproveScopes\": \"\", \"authorizedGrantTypes\": [], \"additionalInformation\": null, \"registeredRedirectUris\": [], \"accessTokenValiditySeconds\": null, \"refreshTokenValiditySeconds\": null}]', '--', '黎铃果', 34, '\"return:{\\\"code\\\":0,\\\"data\\\":{\\\"current\\\":1,\\\"hitCount\\\":false,\\\"optimizeCountSql\\\":true,\\\"orders\\\":[],\\\"pages\\\":1,\\\"records\\\":[{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"frame\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************re3.\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"autoApproveScopes\\\":\\\"false\\\",\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"PC端\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"1ff53290763d889bb85813d1845b3bc8c\\\",\\\"loginDomain\\\":\\\"localhost\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"JVS服务平台\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"},{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"knowledge\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************lrem\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"knowledge\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"bbb46b6e0f779fdb156371548b000ba6\\\",\\\"loginDomain\\\":\\\"knowledge.bctools.cn\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"文档库\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"}],\\\"searchCount\\\":true,\\\"size\\\":20,\\\"total\\\":2},\\\"msg\\\":\\\"success\\\",\\\"timestamp\\\":\\\"2022-09-06 16:29:47 440\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/page', 1, 'admin', '2022-09-06 16:29:47', '1', '分页查询', 'frame');
INSERT INTO `sys_log` VALUES ('1567067572269555713', 'jvs-auth-mgr', '终端管理-添加应用', '2022-09-06 16:29:58', '2022-09-06 16:29:59', 'save', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[{\"id\": \"0b3096f9e82e8a79c3cbf2d955d370d8\", \"icon\": \"\", \"logo\": \"\", \"name\": \"任务管理\", \"bgImg\": \"\", \"appKey\": \"teamwork\", \"delFlag\": null, \"createBy\": \"admin\", \"updateBy\": \"admin\", \"appSecret\": \"$2a$10$/xDYsV9/gD4/RpjYyBDw0ugg9tW1xXkYt16VepcePcLR/QK0iVJZS\", \"describes\": \"任务管理\", \"createById\": \"1\", \"createTime\": \"2022-09-06T16:29:58.593\", \"updateTime\": \"2022-09-06T16:29:58.593\", \"loginDomain\": \"\", \"autoApproveScopes\": \"\", \"authorizedGrantTypes\": [\"password\", \"refresh_token\", \"authorization_code\", \"client_credentials\"], \"additionalInformation\": null, \"registeredRedirectUris\": [], \"accessTokenValiditySeconds\": null, \"refreshTokenValiditySeconds\": null}]', '--', '黎铃果', 104, '\"return:{\\\"code\\\":0,\\\"data\\\":true,\\\"msg\\\":\\\"添加成功\\\",\\\"timestamp\\\":\\\"2022-09-06 16:29:58 601\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/save', 1, 'admin', '2022-09-06 16:29:58', '1', '添加应用', 'frame');
INSERT INTO `sys_log` VALUES ('1567067573192302593', 'jvs-auth-mgr', '终端管理-分页查询', '2022-09-06 16:29:59', '2022-09-06 16:29:59', 'page', 'cn.bctools.auth.controller.ApplyController', '{\"id\": \"1\", \"ip\": \" 局域网IP(10.0.0.71)\", \"sex\": \"男\", \"email\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": 3, \"phone\": \"13555555555\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"tenant\": {\"id\": \"1\", \"icon\": \"\", \"jobId\": \"23b62ab4763bc1b8b525f33808978b91\", \"level\": \"3\", \"deptId\": \"8686286b189daf1f4d285e2006eaf6bd\", \"jobName\": \"afsd\", \"deptName\": \"总经办\", \"shortName\": \"\", \"employeeNo\": \"0001\"}, \"headImg\": \"https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico\", \"jobName\": \"afsd\", \"tenants\": [{\"id\": \"1\", \"icon\": \"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\", \"jobId\": \"\", \"level\": \"\", \"deptId\": \"\", \"jobName\": \"\", \"deptName\": \"\", \"shortName\": \"JVS快速开发服务平台\", \"employeeNo\": \"\"}], \"clientId\": \"frame\", \"deptName\": \"总经办\", \"password\": \"\", \"realName\": \"admin\", \"tenantId\": \"1\", \"adminFlag\": true, \"loginType\": \"帐号密码\", \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36\", \"cancelFlag\": false, \"clientName\": \"JVS服务平台\", \"employeeNo\": \"0001\", \"exceptions\": {}, \"accountName\": \"admin\", \"callBackUrl\": \"\"}', '[null, {\"id\": \"\", \"icon\": \"\", \"logo\": \"\", \"name\": \"\", \"bgImg\": \"\", \"appKey\": \"\", \"delFlag\": null, \"createBy\": \"\", \"updateBy\": \"\", \"appSecret\": \"\", \"describes\": \"\", \"createById\": \"\", \"createTime\": null, \"updateTime\": null, \"loginDomain\": \"\", \"autoApproveScopes\": \"\", \"authorizedGrantTypes\": [], \"additionalInformation\": null, \"registeredRedirectUris\": [], \"accessTokenValiditySeconds\": null, \"refreshTokenValiditySeconds\": null}]', '--', '黎铃果', 21, '\"return:{\\\"code\\\":0,\\\"data\\\":{\\\"current\\\":1,\\\"hitCount\\\":false,\\\"optimizeCountSql\\\":true,\\\"orders\\\":[],\\\"pages\\\":1,\\\"records\\\":[{\\\"accessTokenValiditySeconds\\\":3000,\\\"appKey\\\":\\\"teamwork\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************VJZS\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"createBy\\\":\\\"admin\\\",\\\"createById\\\":\\\"1\\\",\\\"createTime\\\":\\\"2022-09-06T16:29:59\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"任务管理\\\",\\\"id\\\":\\\"0b3096f9e82e8a79c3cbf2d955d370d8\\\",\\\"name\\\":\\\"任务管理\\\",\\\"refreshTokenValiditySeconds\\\":30000,\\\"updateBy\\\":\\\"admin\\\",\\\"updateTime\\\":\\\"2022-09-06T16:29:59\\\"},{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"frame\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************re3.\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"autoApproveScopes\\\":\\\"false\\\",\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"PC端\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"1ff53290763d889bb85813d1845b3bc8c\\\",\\\"loginDomain\\\":\\\"localhost\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"JVS服务平台\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"},{\\\"accessTokenValiditySeconds\\\":30000,\\\"appKey\\\":\\\"knowledge\\\",\\\"appSecret\\\":\\\"$2a$10**************************************************lrem\\\",\\\"authorizedGrantTypes\\\":[\\\"password\\\",\\\"refresh_token\\\",\\\"authorization_code\\\",\\\"client_credentials\\\"],\\\"bgImg\\\":\\\"http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png\\\",\\\"createTime\\\":\\\"2021-11-15T17:31:43\\\",\\\"delFlag\\\":false,\\\"describes\\\":\\\"knowledge\\\",\\\"icon\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico\\\",\\\"id\\\":\\\"bbb46b6e0f779fdb156371548b000ba6\\\",\\\"loginDomain\\\":\\\"knowledge.bctools.cn\\\",\\\"logo\\\":\\\"http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png\\\",\\\"name\\\":\\\"文档库\\\",\\\"refreshTokenValiditySeconds\\\":300000,\\\"updateTime\\\":\\\"2021-11-15T17:31:43\\\"}],\\\"searchCount\\\":true,\\\"size\\\":20,\\\"total\\\":3},\\\"msg\\\":\\\"success\\\",\\\"timestamp\\\":\\\"2022-09-06 16:29:58 814\\\"}\"', 1, ' 局域网IP(10.0.0.71)', NULL, NULL, '[jvs-auth-mgr]-[10011]-[jvs]', '/apply/page', 1, 'admin', '2022-09-06 16:29:59', '1', '分页查询', 'frame');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单ID',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'url地址（如果为HTTP开头的，为外部地址，如果为/开头的地址，会自动拼接环境配置地址）',
  `parent_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '-1' COMMENT '父菜单ID(只支持一层父级),为-1时为最上级',
  `apply_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用ID',
  `layer` int(255) NULL DEFAULT NULL COMMENT '层关系',
  `icon` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int(11) NULL DEFAULT 1 COMMENT '默认排序值(用户可搜藏后自定义重新排序)',
  `new_window` tinyint(1) NULL DEFAULT 0 COMMENT '是否新窗口打开',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('00f9335c9741fa5133084aa4824dc06b', '测试菜单001', NULL, '9616c7059ef5b7ff0f888cffda34488a', '9616c7059ef5b7ff0f888cffda34488a', 1, 'icon-jiaose', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('059defc44dec1bfaf157070fa437dce9', '风控快速查询', NULL, '4674f33582607d990e0627fd1ee7e49c', '4674f33582607d990e0627fd1ee7e49c', 1, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('0609bfea28961dfa1df86939a9567fc2', '资源管理', '/zebra-upms-ui/resource', '531674c7f1fa28e49fe3cd535552948d', '1ff53290763d889bb85813d1845b3bc8c', NULL, 'icon-xitongshezhi', 4, 0, 0);
INSERT INTO `sys_menu` VALUES ('0b7d7c1fc0412ecbb0344b1ed46f5a4f', '系统管理', NULL, '61cfa63bb3d4ad4d29fb5b8913efab93', '1ff53290763d889bb85813d1845b3bc8c', 2, 'iconxitongshezhi', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('16cd9e27febd5c3e9d2e969f7e5a33f2', '菜单管理', '/jvs-upms-ui/resource', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconxinjianwangluoIPwangluoshenqingbiao', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('17face133bbf175239cf50dafad9eef3', '都是传v是大V是', NULL, 'cf0e8634b74d2753cee46892d6b65bc6', 'cf0e8634b74d2753cee46892d6b65bc6', 1, 'icon-caidanguanli4', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('19dda06784f10b1b13518c5ed62fc0e9', '组织结构', '/zebra-upms-ui/resource', '531674c7f1fa28e49fe3cd535552948d', '1ff53290763d889bb85813d1845b3bc8c', NULL, 'icon-xitongshezhi', 3, 0, 0);
INSERT INTO `sys_menu` VALUES ('1d1219dd156b6f00cef7afcf0376f3c5', '角色管理', '/jvs-upms-ui/role', '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', 3, 'icondengdaichukusvg', 4, 0, 0);
INSERT INTO `sys_menu` VALUES ('1e0626f0216df743573cf532bc590dd8', '系统设置', NULL, 'a654eb5a01a1d733136c0892c2e5406e', 'cf0e8634b74d2753cee46892d6b65bc6', 2, 'icon-caidanguanli3', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('2433db2a9e5475436e75215086f78822', '分类字典', '/jvs-upms-ui/treeDictionaries', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconxiaoshouguanlisvg', 2, 0, 0);
INSERT INTO `sys_menu` VALUES ('271c776718ae2ebcb32e03150b88c192', '系统管理', '/zebra-upms-ui/resource', '-1', '1ff53290763d889bb85813d1845b3bc8c', NULL, 'icon-danwei2', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('3e2698798f5b4ee436bb4ecfdd0597dc', 'fdsa', 'fds', '5a099a530c64baaf97ec84a5802d7af2', '4674f33582607d990e0627fd1ee7e49c', 3, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('41efeb1a3de895bd91a988fc674df08a', '登录日志', '/jvs-upms-ui/loginlogs', '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconpandian', 9, 0, 0);
INSERT INTO `sys_menu` VALUES ('45bfabe6359245f1af4cb6fce5b34327', '图标管理', '/jvs-iconmanage-ui/iconList', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconhoucangguanlisvg1', 101, 1, 0);
INSERT INTO `sys_menu` VALUES ('531674c7f1fa28e49fe3cd535552948d', '系统管理', '/zebra-upms-ui/resource', '271c776718ae2ebcb32e03150b88c192', '1ff53290763d889bb85813d1845b3bc8c', NULL, 'icon-xitongshezhi', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('5626986adcd3b25907ebcbcfc5e15967', '系统设置', '/jvs-upms-ui/basicsetting', '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', 3, 'icondanjuluzhi_1svg', 7, 0, 0);
INSERT INTO `sys_menu` VALUES ('5a099a530c64baaf97ec84a5802d7af2', '风控查询', NULL, '059defc44dec1bfaf157070fa437dce9', '4674f33582607d990e0627fd1ee7e49c', 2, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('6156e6f01d4e867ee70ebb67d41cc621', '终端管理', '/jvs-upms-ui/application', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconsaomiaoguanli', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('61cfa63bb3d4ad4d29fb5b8913efab93', '系统管理', '1', '1ff53290763d889bb85813d1845b3bc8c', '1ff53290763d889bb85813d1845b3bc8c', 1, 'iconxitongshezhi', 2, 0, 0);
INSERT INTO `sys_menu` VALUES ('7235548d96578a6857d8487d21d019fd', '租户组织', '/jvs-upms-ui/tenant', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconxiaoneiduanqishangwangzhanghaoshenqingbiao', 3, 0, 0);
INSERT INTO `sys_menu` VALUES ('758a6dacc22ad6fcac35f4dd47ed1fd9', '企业广告', NULL, '4674f33582607d990e0627fd1ee7e49c', '4674f33582607d990e0627fd1ee7e49c', 1, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('793d127b70985f8795e4975a10503cb5', '都是', NULL, 'cf0e8634b74d2753cee46892d6b65bc6', 'cf0e8634b74d2753cee46892d6b65bc6', 1, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('7be8c08582dd3a1b80787b80e650ab85', '字典管理', '/jvs-upms-ui/dictionaries', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconkucunchaxun', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('7c233a179e4050b2b11d4e710383202e', '群组管理', '/jvs-upms-ui/group', '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconfenjianhuoguanli', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('8d81e8e3bd779f21e49122397170da99', '运维管理', NULL, '1ff53290763d889bb85813d1845b3bc8c', '1ff53290763d889bb85813d1845b3bc8c', 1, 'iconcaiwuyusuantiaozhengshenqingbiao', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('94c370101f09f399da14ff6ce2e4e4fe', '请求日志', '/jvs-upms-ui/errlogs', '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconpandian', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('99fb245c3ab519acf609014c2552a4a2', '风控系统', '', '-1', '1ff53290763d889bb85813d1845b3bc8c', NULL, 'icon-caidanguanli4', 0, 0, 0);
INSERT INTO `sys_menu` VALUES ('9cd36aca7e401eaa5874f2555c5b1970', '功能分配(租户级)', '/jvs-upms-ui/tenantRole', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconfenjianhuoguanli', 4, 0, 0);
INSERT INTO `sys_menu` VALUES ('a654eb5a01a1d733136c0892c2e5406e', '系统管理', NULL, 'cf0e8634b74d2753cee46892d6b65bc6', 'cf0e8634b74d2753cee46892d6b65bc6', 1, 'icon-caidanguanli', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('b9c9f46baea6bc135919b81dde5249ea', '轻邹桌面端aaa', NULL, '53b994fa3b6cb8b15a8a196cc8b17add', '53b994fa3b6cb8b15a8a196cc8b17add', 1, 'icon-caidanguanli', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('bad14adbde51aa4e6b2d31140e1699ba', 'sdfbfdsa', NULL, 'cc7b7a56bdc86580a73929c1d164f9ac', 'cc7b7a56bdc86580a73929c1d164f9ac', 1, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('beb0019b2cc6b0c3deeeb7a698d02568', '用户签退', '/jvs-upms-ui/onlineUser', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconrukuguanli', 8, 0, 0);
INSERT INTO `sys_menu` VALUES ('cc7deca63abe05541ec3e2fa970b4119', '风控系统', NULL, '-1', '1ff53290763d889bb85813d1845b3bc8c', NULL, 'icon-caidanguanli4', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('d049c566ff33f513a8a0e159d59e9575', '正则字典', '/jvs-upms-ui/regExpList', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconquanqudaosvg', 2, 0, 0);
INSERT INTO `sys_menu` VALUES ('d3c886b70744b51364224b4aa7838e64', '统计图', NULL, '4674f33582607d990e0627fd1ee7e49c', '4674f33582607d990e0627fd1ee7e49c', 1, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('d68d57e618aae912474c092779f38202', '数据权限', '/jvs-upms-ui/dataAuthority', 'fa1c2aae3b9cb8eee3e941f6ceb34acb', '1ff53290763d889bb85813d1845b3bc8c', 3, 'icontiaobotongyongsvg', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('db88509cb47267804a6cfd717a864441', '8ujhnbvc', NULL, 'cc7b7a56bdc86580a73929c1d164f9ac', 'cc7b7a56bdc86580a73929c1d164f9ac', 1, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('db9c67b2294cd0b9d8a557b410120932', '风控系统', NULL, '-1', '1ff53290763d889bb85813d1845b3bc8c', NULL, 'icon-caidanguanli4', 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('dc635856ebc3018b5cda782121eb124c', '文件管理', '/jvs-upms-ui/file', '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconshujutongbu', 10, 0, 0);
INSERT INTO `sys_menu` VALUES ('ddf4ab133c0bac34aa9a6833f47e7562', '岗位管理', '/jvs-upms-ui/postList', '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', 3, 'iconxitongshezhisvg', 6, 0, 0);
INSERT INTO `sys_menu` VALUES ('e5b6336c3f881007816a226a60311449', '用户设置', 'https://www.teambition.com/project/600437ab14c36bb368434265/story/section/all', '1e0626f0216df743573cf532bc590dd8', 'cf0e8634b74d2753cee46892d6b65bc6', 3, NULL, 1, 0, 0);
INSERT INTO `sys_menu` VALUES ('e999dd44952be57f1b2de6981088fe5a', '用户组织', '/jvs-upms-ui/department', '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', 3, 'icondanjuguanlisvg1', 5, 0, 0);
INSERT INTO `sys_menu` VALUES ('f496d63b2455de8765e3ccb0496a9c63', 'vvvvvvvv', '', '-1', '1ff53290763d889bb85813d1845b3bc8c', NULL, 'icon-danwei4', 0, 0, 0);
INSERT INTO `sys_menu` VALUES ('fa1c2aae3b9cb8eee3e941f6ceb34acb', '运营管理', NULL, '8d81e8e3bd779f21e49122397170da99', '1ff53290763d889bb85813d1845b3bc8c', 2, 'iconxitongshezhi', 1, 0, 0);

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `send_type` enum('sms','email','interior') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送类型\\短信\\邮件\\站内信',
  `send_message_type` enum('broadcast','warning','notification','system','business') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息类型： 广播消息\\警告消息\\通知消息\\系统消息\\业务消息',
  `status` tinyint(5) UNSIGNED NOT NULL DEFAULT 2 COMMENT '发送状态(0失败;1成功;2未发送;3发送中)',
  `send_count` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '消息成功发送次数',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息来源',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '发送内容,如果是邮件和站内信，支持html标签功能',
  `recipients` json NULL COMMENT '批量收件人,最多500个，多余500会切分，可能是用户id，可能是用户手机号',
  `other` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '发送返回信息,邮件和短信发送的消息',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `extension` json NULL COMMENT '用于业务扩展字段方便排查问题',
  `business_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务id',
  `business_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务系统,如果业务系统需要发送站内信，直接使用mq-core进行发送即可查收',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息发送日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_message
-- ----------------------------

-- ----------------------------
-- Table structure for sys_message_user_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_message_user_log`;
CREATE TABLE `sys_message_user_log`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主建',
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `read_status` tinyint(1) NULL DEFAULT 0 COMMENT '读取状态,默认未读',
  `read_time` timestamp NULL DEFAULT NULL COMMENT '读取时间',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  `hide` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否隐藏,默认显示',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息来源,默认系统发送为系统,人工发送为某个人',
  `send_message_type` enum('broadcast','warning','notification','system','business') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息类型： 广播消息\\警告消息\\通知消息\\系统消息\\业务消息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`, `read_status`, `hide`, `send_message_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '站内信用户消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_message_user_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源标识(随机生成-建议使用中文名称进行拼音生成以_分割) 如果添加了忽略字段集，则直接放开即可,如果删除了，会生成uuid进行占位',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源名称(为Swagger的注解名，或前端的按钮|资源名称)',
  `api` json NULL COMMENT '请求地址',
  `type` enum('button','remark') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '按钮、说明',
  `client_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端或服务端名称',
  `menu_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '将资源挂在菜单上（不能挂一级菜单）',
  `apply_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用ID',
  `remark` json NULL COMMENT '扩展',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `permission`(`permission`) USING BTREE COMMENT '同一客户端，唯一标识，不能重复'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资源数据表，用于统计所有项目的请求地址' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('008a96ccd080df9825a854a80a4385a8', 'upms_systemSetting_page', '查询', '[\"get /api/jvs-auth/index/this/tenant\"]', 'button', NULL, '5626986adcd3b25907ebcbcfc5e15967', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('1e19071f08c6a6e455b8092c0893e835', 'upms_dataAuth_page', '查询', '[\"get /api/jvs-auth/data/page\"]', 'button', NULL, 'd68d57e618aae912474c092779f38202', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('1f93ef281db660a44ff67c03ac38d414', 'upms_group_delete', '删除群组', '[\"delete /api/jvs-auth/user/group/{id}\"]', 'button', NULL, '7c233a179e4050b2b11d4e710383202e', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('20bc106cb5e88fe680b12c97e3a68d80', 'upms_group_remove', '移出成员', '[\"delete /api/jvs-auth/user/group/user/{userId}/{groupId}\"]', 'button', NULL, '7c233a179e4050b2b11d4e710383202e', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('24ef17953dc14ac7f408b138c4df71dd', 'page_application_deploy_template', '发布到模板', '[\"post /mgr/jvs-design/JvsApp/deployTemplate\"]', 'button', NULL, 'cb106a550ca0241e1ebfa068311b1d9d', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('3057ab15926ae67eda48ad4de05fa184', 'scs ', 'afcs', '[\"delete /api/chart-design/page/{id}\"]', 'button', NULL, '6caef0a9f1d65dd27c185d78aa462639', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('30fa28475dc14d28e9d2092a0b2a917e', 'upms_role_permision_menu', '菜单权限', '[\"get /api/jvs-auth/role/role/menus/{roleId}\"]', 'button', NULL, '1d1219dd156b6f00cef7afcf0376f3c5', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('34247c7c395e66155924da21f05c3fcb', 'upms_dict_items_page', '字典项-查询', '[\"get /api/jvs-auth/dict/item/{id}\"]', 'button', NULL, '7be8c08582dd3a1b80787b80e650ab85', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('3491741e5ac1a1fa45197ce15127d292', 'upms_dept_delete', '删除部门', '[\"delete /mgr/jvs-auth/dept/{id}\"]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('3929e30e912d7937b3390bc81e7fb48f', 'upms_dict_items_save', '字典项-保存', '[\"post /api/jvs-auth/dict/item/{id}\"]', 'button', NULL, '7be8c08582dd3a1b80787b80e650ab85', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('3a4c08ab2a8d2742995433efa0a32e48', 'upms_role_add', '新增角色', '[\"post /api/jvs-auth/role/save\"]', 'button', NULL, '1d1219dd156b6f00cef7afcf0376f3c5', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('3b303d7344c685e184990958b73cf480', 'upms_dict_page', '查询', '[\"get /api/jvs-auth/dict/page\"]', 'button', NULL, '7be8c08582dd3a1b80787b80e650ab85', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('42f550be8c76d32a44dffad5ffbbef50', 'upms_dept_editUser', '编辑成员', '[\"put /api/jvs-auth/user/update\"]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('43982e2bc69e9e331e545da678a65a44', 'upms_post_add', '添加岗位', '[\"post /api/jvs-auth/job\"]', 'button', NULL, 'ddf4ab133c0bac34aa9a6833f47e7562', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('484521eff1e0f2a4df33f1378426bdb7', 'upms_dict_add', '新增', '[\"post /api/jvs-auth/dict\"]', 'button', NULL, '7be8c08582dd3a1b80787b80e650ab85', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('4ccf3944529f3524d21e1108458e5d4a', 'upms_post_remove', '移除成员', '[\"delete /api/jvs-auth/job/user/{userId}\"]', 'button', NULL, 'ddf4ab133c0bac34aa9a6833f47e7562', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('511e5bd5abcdd7b46acd5e87c423e705', 'upms_menu_add', '新增菜单', '[\"post /api/jvs-auth/menu/menu\"]', 'button', NULL, '16cd9e27febd5c3e9d2e969f7e5a33f2', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('52d8da9546717bf571545728a90edda0', 'upms_dept_page', '查询', '[\"get /api/jvs-auth/dept/all\"]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('570558234b4351275ac989f9f3f8607d', 'fsad', 'fdsa', '[]', 'button', NULL, '0b7d7c1fc0412ecbb0344b1ed46f5a4f', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('65093b3bfeefb020813dbd830b980e5a', 'upms_role_delete', '删除角色', '[\"delete /api/jvs-auth/role/{id}\"]', 'button', NULL, '1d1219dd156b6f00cef7afcf0376f3c5', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('6c88ae4ad447f1601ab7b9102ae1c135', 'upms_dict_delete', '删除', '[\"delete /api/jvs-auth/dict/{id}\"]', 'button', NULL, '7be8c08582dd3a1b80787b80e650ab85', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('72fc5295066c011668061450520ef30e', 's的测试大Vs', '是多少', NULL, 'remark', NULL, 'b636fd44fb12dcf1b2fb526d9c941e4f', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('763e209c020ada1065d4af9c93ae9b1d', 'upms_systemSetting_save', '保存', '[\"put /api/jvs-auth/tenant/info\"]', 'button', NULL, '5626986adcd3b25907ebcbcfc5e15967', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('7ef6e15541a3dac8ce0ad9d827e6da55', 'upms_menu_delete', '删除菜单', '[\"delete /api/jvs-auth/menu/menu/{id}\"]', 'button', NULL, '16cd9e27febd5c3e9d2e969f7e5a33f2', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('8c81e07aee0ca5b50dc8f79af7f1fe09', 'upms_group_addUser', '添加成员', '[\"put /api/jvs-auth/user/group/user/{groupId}\"]', 'button', NULL, '7c233a179e4050b2b11d4e710383202e', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('908f9f71ae9d2af72dae3b250d8b59da', 'sd', 'sc', NULL, 'remark', NULL, '15ddbaed384e4242998ec7eafdd9b29c', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('941dff68413878c3daca8fedcb91f8ee', 'upms_dept_viewUser', '成员详情', '[]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('94b20e4287f4ed903ed9e39bdadf7bd4', 'rvdv', 'dvdv', NULL, 'remark', NULL, 'aca6ad374c3eeebe14c891319d2776e3', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('9954e6a123d984f17173c44f673410f8', 'upms_role_edit', '修改角色', '[\"put /api/jvs-auth/role/update\"]', 'button', NULL, '1d1219dd156b6f00cef7afcf0376f3c5', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('9ba9749d07788ef7f9d1951eda1636d3', 'upms_role_addUser', '添加人员', '[\"put /api/jvs-auth/role/user/{roleId}\"]', 'button', NULL, '1d1219dd156b6f00cef7afcf0376f3c5', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('a1f6ef3988b2bbb3695381b07dd8a0ce', 'upms_role_permision_data', '数据权限', '[\"get /api/jvs-auth/role/role/data/{roleId}\"]', 'button', NULL, '1d1219dd156b6f00cef7afcf0376f3c5', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('a30398dd2cdc768f41b8827a32c1f4f9', 'upms_post_page', '查询', '[\"get /api/jvs-auth/job/list\"]', 'button', NULL, 'ddf4ab133c0bac34aa9a6833f47e7562', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ac650daceca120b40769ed2c0818d42b', 'upms_dept_addUser', '添加成员', '[\"post /api/jvs-auth/user/save\"]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ad33654fc3d108bb489ded4493bd473d', 'upms_dept_invite', '邀请成员', '[\"get /api/jvs-auth/user/get/invite\"]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('af27ad4b5e8109c32528257a5e281e53', 'fdsafds', 'dsfafds', '[]', 'button', NULL, '3e2698798f5b4ee436bb4ecfdd0597dc', '4674f33582607d990e0627fd1ee7e49c', NULL);
INSERT INTO `sys_permission` VALUES ('b451106effa87eed57529dccd819d74d', 'upms_explain_edit', '编辑解释', '[]', 'button', NULL, '16cd9e27febd5c3e9d2e969f7e5a33f2', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('b49f1efc537c080eadf341b61014a23d', 'drsd', 'sr', NULL, 'remark', NULL, '15ddbaed384e4242998ec7eafdd9b29c', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ba0b8994a586b819411b65c105b37554', 'upms_role_remove', '移出人员', '[\"delete /api/jvs-auth/role/user/{roleId}/{userId}\"]', 'button', NULL, '1d1219dd156b6f00cef7afcf0376f3c5', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ba5611cb234f85e8831fa994f921a89e', '5', '1', NULL, 'remark', NULL, '15ddbaed384e4242998ec7eafdd9b29c', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('c154ac1535e15a56b51de01ff66096ec', 'sdsdfg', '2ws', '[\"delete /api/jvs-auth/dept/{id}\"]', 'button', NULL, '6caef0a9f1d65dd27c185d78aa462639', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('c65726c072112f7f219f18fcdf408a18', 'fbf', 'dffb', NULL, 'remark', NULL, 'aca6ad374c3eeebe14c891319d2776e3', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('caac8515772f6f2cd06d9b7cae3cac3e', 'upms_dept_disableUser', '禁用成员', '[\"delete /api/jvs-auth/user/users/disabled/{userId}\"]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('cb4e88e00345f10d443bf2816d67ed63', 'upms_dept_enableUser', '启用成员', '[\"delete /api/jvs-auth/user/users/enable/{userId}\"]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('cddfb6cd123e7e435608406751ec160b', 'upms_dept_add', '添加部门', '[\"post /api/jvs-auth/dept/save\"]', 'button', NULL, 'e999dd44952be57f1b2de6981088fe5a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('d203a0eb2866fca9943af21d56ffa2c6', 'upms_dataAuth_edit', '编辑', '[\"put /api/jvs-auth/data\"]', 'button', NULL, 'd68d57e618aae912474c092779f38202', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('d2efe80967ebaa3839e49442057207b2', 'upms_group_edit', '编辑群组', '[\"put /api/jvs-auth/user/group\"]', 'button', NULL, '7c233a179e4050b2b11d4e710383202e', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('d5123f9547156fbff96fce86e5338706', 'upms_dataAuth_add', '新增', '[\"get /api/jvs-auth/data/page\"]', 'button', NULL, 'd68d57e618aae912474c092779f38202', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('d57cc91deb8968125b8a58a71d8766df', '7', '7', NULL, 'remark', NULL, '15ddbaed384e4242998ec7eafdd9b29c', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('d61aed962684fe3ab901d3e1bbb65ebe', 'upms_post_addUser', '添加成员', '[\"put /api/jvs-auth/job/user/{jobId}\"]', 'button', NULL, 'ddf4ab133c0bac34aa9a6833f47e7562', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('dcbe95135136006d44e9186804c457bd', 'upms_post_delete', '删除岗位', '[\"delete /api/jvs-auth/job/{jobId}\"]', 'button', NULL, 'ddf4ab133c0bac34aa9a6833f47e7562', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('e0c48892edafbb5430daa831d58552e9', 'upms_login_log', '查询', '[\"get /api/jvs-auth/login/log/page\"]', 'button', NULL, '41efeb1a3de895bd91a988fc674df08a', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('e41f33d13d5d21cdd46c7433f29bf5d8', 'evc', 'wefw', NULL, 'remark', NULL, 'b636fd44fb12dcf1b2fb526d9c941e4f', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('e72a6e1d549a9384ff7521a9682d9ae1', 'upms_resource_edit', '编辑资源', '[\"post /api/jvs-auth/permission/permission/{menuId}\"]', 'button', NULL, '16cd9e27febd5c3e9d2e969f7e5a33f2', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('e86c863ff5ceca09706840e5a2b8d553', 'upms_dataAuth_delete', '删除', '[\"delete /api/jvs-auth/data/{id}\"]', 'button', NULL, 'd68d57e618aae912474c092779f38202', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('e8c3269e3e1a8e69bb98d0d1485bc5d6', 'upms_group_add', '添加群组', '[\"post /api/jvs-auth/user/group\"]', 'button', NULL, '7c233a179e4050b2b11d4e710383202e', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('e9dfa603521d01a5576c12043e60e1ed', 'upms_menu_edit', '编辑菜单', '[\"put /api/jvs-auth/menu/menu\"]', 'button', NULL, '16cd9e27febd5c3e9d2e969f7e5a33f2', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ea5a2acc070fa3df9a00c969c13a8d25', '6', '1', NULL, 'remark', NULL, '15ddbaed384e4242998ec7eafdd9b29c', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ea7d0b0ffe99db502355d09fcc2a623e', 'upms_role_page', '查询', '[\"get /api/jvs-auth/role/all/{type}\"]', 'button', NULL, '1d1219dd156b6f00cef7afcf0376f3c5', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ed957b733876b4b89beefa938ff5654a', 'upms_post_edit', '编辑岗位', '[\"put /api/jvs-auth/job\"]', 'button', NULL, 'ddf4ab133c0bac34aa9a6833f47e7562', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ee6d7cfd82d01272b148c037365d3246', 'upms_dict_view', '查看', '[]', 'button', NULL, '7be8c08582dd3a1b80787b80e650ab85', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('ef61cddc42ffc10215d376e0f9b9b0c1', 'upms_dict_edit', '编辑', '[\"put /api/jvs-auth/dict\"]', 'button', NULL, '7be8c08582dd3a1b80787b80e650ab85', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('f71bac75977ab12be303c742d919efbe', '3', '1', NULL, 'remark', NULL, '15ddbaed384e4242998ec7eafdd9b29c', '1ff53290763d889bb85813d1845b3bc8c', NULL);
INSERT INTO `sys_permission` VALUES ('fd2a13109dcdac5d2d2c4248e580851f', '', '查询', '[\"get /api/jvs-auth/user/group/list\"]', 'button', NULL, '7c233a179e4050b2b11d4e710383202e', '1ff53290763d889bb85813d1845b3bc8c', NULL);

-- ----------------------------
-- Table structure for sys_regexp
-- ----------------------------
DROP TABLE IF EXISTS `sys_regexp`;
CREATE TABLE `sys_regexp`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '正则名称',
  `expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '正则表达式',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `unique_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'UUID,该值唯一且永不改变,用作被引用时的key',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '正则表达式,用于在数据表设计时，可以动态修改的模块' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_regexp
-- ----------------------------
INSERT INTO `sys_regexp` VALUES ('1', '数字校验', '^[0-9]*$', '客户端', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('16', '测试校验', 'sdvcsv@#￥%……&   《》》', '客户端', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('18', '数字校验', '^[0-9]*$', '客户端', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('19', '0-100位数字校验', '^\\d{0,100}$', '数字位数校验', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('2', '0-100位数字校验', '^\\d{0,100}$', '数字位数校验', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('21', '全汉字', '^[\\u4e00-\\u9fa5]{0,}$', '字符', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('22', '测试校验', '正则1111111111是山地车文', '客户端', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('26', '测试正则', '^\\-[1-9][]0-9\"*$ 或 ^-[1-9]\\d*$', '测试类型', NULL, '1c75bbd7-6cc9-495e-bc90-d8646889a001');
INSERT INTO `sys_regexp` VALUES ('28', '手机号校验', '^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$', '服务器校准', NULL, 'c372a134-ffbb-4ca3-8704-cf0bcdacc0bc');
INSERT INTO `sys_regexp` VALUES ('29', '测试正则2', '$^', '测试类型', NULL, '16abf70f-7b0c-4f0c-b4cc-b135f1840b5c');
INSERT INTO `sys_regexp` VALUES ('29b909b5e6cac794127e68c948b39aaa', '小贷-身份证号验证', '^([1-6][1-9]|50)\\d{4}(18|19|20)\\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$', '小贷', NULL, '866765c3-0d5c-4319-9043-0829ca00cf3c');
INSERT INTO `sys_regexp` VALUES ('3', '11位数字校验', '^\\d{11}$', '数字位数校验', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('31', '正整数(不包括0)', '^[1-9]\\d*$', '客户端', NULL, '2f5c57dc-9af8-451a-ab9d-f3eb49443809');
INSERT INTO `sys_regexp` VALUES ('32', '信用统一代码(15,18)', '^([0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}|[1-9]\\d{14})$', '数字位数校验', NULL, 'c82f810d-a92a-4d7b-94df-f6e9df544d12');
INSERT INTO `sys_regexp` VALUES ('33', '座机', '0\\d{2,3}-\\d{7,8}', '数字位数校验', NULL, '7e2d7fa4-6268-43bd-9399-b15756d5b992');
INSERT INTO `sys_regexp` VALUES ('34', '邮政编码', '^[0-8][0-7]\\d{4}$', '数字位数校验', NULL, '208665e4-0f0f-4423-8f73-f48b805f4606');
INSERT INTO `sys_regexp` VALUES ('35', '测试正则表达式1', 'afds', '测试类型', NULL, 'de8bd004-c061-4235-83ba-e2748089176b');
INSERT INTO `sys_regexp` VALUES ('37', '组织1', '正常', '测试', NULL, 'dceef2f3-9374-40fc-9825-58c72b55639b');
INSERT INTO `sys_regexp` VALUES ('38', '123', '123', '测试类型', NULL, '0ba1bcc2-f865-4f3b-a740-a46112c3fa00');
INSERT INTO `sys_regexp` VALUES ('4', '全汉字', '^[\\u4e00-\\u9fa5]{0,}$', '字符', NULL, NULL);
INSERT INTO `sys_regexp` VALUES ('48', '测试文字校验', '^[0-9]*$', '客户端', NULL, 'e53de7eb-3ecf-445d-b9a8-72d58ad06e6a');
INSERT INTO `sys_regexp` VALUES ('49', '测试', '111111111', '测试1', NULL, 'fb0e7113-e8a0-40e8-bf52-f4897a3472b0');
INSERT INTO `sys_regexp` VALUES ('54c7f489c74ac6ec6d73fd02941ebb22', '数字类型', 'd', '小贷', NULL, 'b67d172f-cbfa-441f-84c6-ddf71d557ef2');
INSERT INTO `sys_regexp` VALUES ('9f375a0192371f4fbd184fa9faaa20c3', '小贷-0~10000000.00正则', '^[1-9][0-9]{0,7}(\\.[0-9]{0,2})?$', '小贷', NULL, '6574268f-06b7-4d07-9f5d-b7d99dcdbd8a');
INSERT INTO `sys_regexp` VALUES ('a1f97a6121339a885c2b643281f2908f', '小贷-邮箱验证', '^((\\+?86)|(\\(\\+86\\)))?(13[0123456789][0-9]{8}|15[0123456789][0-9]{8}|17[0123456789][0-9]{8}|18[0123456789][0-9]{8}|147[0-9]{8}|1349[0-9]{7})$', '小贷', NULL, '1a53415d-0834-438f-8803-4e1ee933ba29');
INSERT INTO `sys_regexp` VALUES ('b28faec96cf4d40c5034b318a9707a11', '小贷-邮编验证', '^[0-9]{6}$', '小贷', NULL, 'a8f6bbc9-16c0-493c-ba82-97e1131a51f4');
INSERT INTO `sys_regexp` VALUES ('b9c3de477a219b70d4bbf445493b7e91', '小贷-出资比例正则', '([1-9]?\\d|100)$', '小贷', NULL, '52bcc49a-522b-4856-a0f7-9a7f4fe79cd0');
INSERT INTO `sys_regexp` VALUES ('c9539019ba1d2a13aa7cda8775dfd106', '小贷-联系方式正则', '^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$', '小贷', NULL, 'fa391609-9b22-42e9-8514-618e926f88de');
INSERT INTO `sys_regexp` VALUES ('d52d014cb27bae98512c9a8c09519e87', '小贷-正整数验证', '^[1-9]\\d*$', '小贷', NULL, 'a5cc1d0b-9d40-4847-8e9e-2f191d49ed09');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
  `role_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色解释',
  `type` enum('tenantRole','userRole') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'userRole' COMMENT '系统角色、租户角色',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标识（0-正常,1-删除）',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '1' COMMENT '租户ID默认1',
  `ds_type` enum('all','oneself','subordinate','custom') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'all' COMMENT 'all:所有部门\r\noneself: 当前部门\r\nsubordinate:当前部门及以下部门\r\ncustom:自定义部门集使用,分割',
  `data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '所有的部门数据集IDS,当ds_type为custom,时，此字段才会生效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('00e3efd1f9438e1dab67e0aa9d853d5b', '测试专用公司', '测试专用公司租户角色', 'tenantRole', '2021-09-16 01:09:52', '2021-09-30 00:01:49', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('01e53522914b2c8009b9016ff3916b63', '系统管理员', '系统管理员角色, 此角色信息不能删除', 'userRole', '2021-11-24 02:22:11', '2021-11-24 02:22:11', 0, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('02a497041a514df5b769850ed9dba251', '14', '14', 'userRole', '2021-10-01 23:49:30', '2021-10-15 02:48:28', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('0f21534cdc2ae567d1b6d32b5b7b9dce', '系统管理员', '系统管理员角色, 此角色信息不能删除', 'userRole', '2021-11-24 02:22:11', '2021-11-24 02:22:11', 0, '7a2922d5030d036455956722632b2d0c', 'all', NULL);
INSERT INTO `sys_role` VALUES ('0fcdd4b818d0e69cc1553acd8666df4e', '10', '10', 'userRole', '2021-10-01 23:49:12', '2021-10-15 02:49:24', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('1', '系统管理员', '默认系统管理员1', 'tenantRole', '2021-08-31 00:02:17', '2021-09-08 02:08:19', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('18a7c6c4953327be8bb90d8a571d2c50', 'ces1', 'ces', 'tenantRole', '2021-09-08 01:33:07', '2021-09-08 01:33:21', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('1d5c95172fa2e459e5ec4b7f83c1084a', '5', '5', 'userRole', '2021-10-01 23:48:32', '2021-10-21 01:49:38', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('1eaacf0ee2ab00e97f018000d288852e', '游客', '游客角色, 此角色信息不能删除, 当用户通过注册方式进入组织, 将自动赋予此权限', 'userRole', '2021-11-18 22:35:12', '2021-11-18 22:35:12', 0, '30b19cb44b6eeafcb73337e58d715d60', 'all', NULL);
INSERT INTO `sys_role` VALUES ('20b63dcefa237453decd99166edd28aa', '财务数据角色', '测试专用哈哈', 'userRole', '2021-08-31 19:16:14', '2021-08-31 19:16:14', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('20d7531fa4ef71a981470610754763d2', '服务租户角色', 'rdtyfugihlk,n ', 'tenantRole', '2021-09-17 17:50:33', '2021-10-30 18:32:53', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('214551c87e0ea28ae8ce2736dfa28866', '主租户信息', '主租户信息', 'tenantRole', '2021-10-18 18:26:14', '2021-10-21 01:15:42', 0, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('223e3e95681063adec5c6220fbe99faf', 'sd', 'sd', 'userRole', '2021-10-12 19:29:29', '2021-10-21 18:31:05', 0, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('2313a6e2b0b48f981bb6f50810188afc', '1', '111122223', 'userRole', '2021-10-01 23:48:15', '2021-10-21 01:49:53', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('231b7e84cc2e296378c4d038ef74c45c', 'VIP级租户角色', '赋权所有应用', 'tenantRole', '2021-09-30 18:04:41', '2021-10-30 18:30:25', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('2635db8649c90be9614b8da8983f04d6', ' 游客', '游客信息', 'userRole', '2021-10-21 18:35:55', '2021-10-21 19:33:22', 1, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('28bb829769e4b91192ecd3000c7586eb', '12', '12', 'userRole', '2021-10-01 23:49:19', '2021-10-15 02:48:40', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('2f1a67b6c29378f0752f0714dc0205cc', '应用管理员', '应用管理员角色, 此角色信息不能删除', 'userRole', '2021-11-18 22:35:12', '2021-11-18 22:35:12', 0, '30b19cb44b6eeafcb73337e58d715d60', 'all', NULL);
INSERT INTO `sys_role` VALUES ('3030b4f7f1c3c9d9f4234f46e5252e36', '应用管理员', '应用管理员角色, 此角色信息不能删除', 'userRole', '2021-11-18 22:35:12', '2021-11-18 22:35:12', 0, 'b7566ac352652eacca9b383120e7d566', 'all', NULL);
INSERT INTO `sys_role` VALUES ('371d1316f07e4c42793afd4da1ea5b73', '游客', '游客角色, 此角色信息不能删除, 当用户通过注册方式进入组织, 将自动赋予此权限', 'userRole', '2021-10-30 18:35:53', '2021-10-30 18:35:53', 0, '7a2922d5030d036455956722632b2d0c', 'all', NULL);
INSERT INTO `sys_role` VALUES ('3be350275c96b8c89fb73e07bb06840b', '测试角色', 'aa', 'userRole', '2021-12-11 02:30:33', '2021-12-11 02:30:33', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('3f4f6dcca86aff199fad3a0a4e6e2438', '测试角色1', '8', 'userRole', '2021-10-01 23:48:46', '2021-12-01 01:00:45', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('40abe99e776a893f853e3ade76dd58c8', '游客', '游客信息', 'userRole', '2021-10-21 00:54:20', '2021-10-21 00:54:20', 0, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('42cb8bae7f39b47019b96a83086ea711', 'fasd', 'dfsa', 'userRole', '2021-09-03 02:26:38', '2021-09-03 02:26:41', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('4726811bb73b4c02d9ed6d28a2f30232', 'ces', 'ces', 'tenantRole', '2021-09-08 02:23:29', '2021-09-08 02:23:36', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('47446a787236919e255f5b12d06af12b', '游客', '游客角色, 此角色信息不能删除, 当用户通过注册方式进入组织, 将自动赋予此权限', 'userRole', '2021-11-18 18:53:44', '2021-11-18 18:53:44', 0, '0fa705bf0824fba0e38ce86fc25326cd', 'all', NULL);
INSERT INTO `sys_role` VALUES ('4d30aad3225071a9f001e44e4b05f8a1', '大租户', '大租户角色', 'tenantRole', '2021-10-08 18:18:17', '2021-10-21 23:40:12', 0, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('4e7f4bca76ac4b0280393a0c697b5995', '系统管理员', '系统管理员角色, 此角色信息不能删除', 'userRole', '2021-11-24 02:22:11', '2021-11-24 02:22:11', 0, '30b19cb44b6eeafcb73337e58d715d60', 'all', NULL);
INSERT INTO `sys_role` VALUES ('5b41137255bf881b74c645cb8862464c', '应用管理员', '应用管理员角色, 此角色信息不能删除', 'userRole', '2021-11-18 22:35:12', '2021-11-18 22:35:12', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('5bf66788c0ce34d277e13354a6cffa3d', '2', '2', 'userRole', '2021-10-01 23:48:20', '2021-10-21 01:49:48', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('5d6bb64b861f661ff24bcc66142e60f0', '13', '13', 'userRole', '2021-10-01 23:49:26', '2021-10-15 02:48:33', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('5f1076cb849b44714011fca4e6bc719c', '4', '4', 'userRole', '2021-10-01 23:48:29', '2021-10-21 01:49:34', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('5f3ad01bd3f4f4738970adc268768054', '管理角色', '管理角色', 'userRole', '2021-09-16 19:49:05', '2021-09-30 00:01:49', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('72a7b1d0bb978a448b2ba34744a78519', '测试删除角色', '测试删除角色', 'userRole', '2021-08-31 19:20:06', '2021-08-31 19:27:46', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('73df08bc39d1dc90014937735e2281af', '1', '1', 'tenantRole', '2021-10-13 01:23:54', '2021-10-13 01:30:36', 1, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('76088569dcd30770400a5f365e26c886', '系统管理员', '系统管理员角色, 此角色信息不能删除', 'userRole', '2021-11-24 02:22:11', '2021-11-24 02:22:11', 0, 'b7566ac352652eacca9b383120e7d566', 'all', NULL);
INSERT INTO `sys_role` VALUES ('8b54ac5c69d0ce2568be138fe907550a', '应用管理员', '应用管理员角色, 此角色信息不能删除', 'userRole', '2021-11-18 22:35:12', '2021-11-18 22:35:12', 0, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('911d9385c6b6fb7534f70b8a6c4b0c78', '测试-租户角色', '测试-租户角色', 'tenantRole', '2021-10-14 22:32:15', '2021-10-14 22:32:15', 0, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('93a8b75b702100e69643f51459443373', '新增', '新增', 'tenantRole', '2021-10-18 18:28:45', '2021-10-18 18:38:57', 1, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('988b95b48cf59d803a2c19e3732e3c96', '系统角色', '角色描述', 'userRole', '2021-11-01 23:45:33', '2021-11-02 01:25:32', 0, 'b7566ac352652eacca9b383120e7d566', 'all', NULL);
INSERT INTO `sys_role` VALUES ('98d2fd6f2d9463ea82667f8bc029d7ea', '大租户', '大租户描述', 'tenantRole', '2021-11-01 23:41:15', '2021-11-01 23:41:15', 0, 'b7566ac352652eacca9b383120e7d566', 'all', NULL);
INSERT INTO `sys_role` VALUES ('a580cb47b852daab4905775e65cceeac', '应用管理员', '应用管理员角色, 此角色信息不能删除', 'userRole', '2021-11-18 22:35:12', '2021-11-18 22:35:12', 0, '7a2922d5030d036455956722632b2d0c', 'all', NULL);
INSERT INTO `sys_role` VALUES ('af72ef671ad61826e66cf45cd4e50869', '应用管理员', '应用管理员角色, 此角色信息不能删除', 'userRole', '2021-11-18 22:35:12', '2021-11-18 22:35:12', 0, '0fa705bf0824fba0e38ce86fc25326cd', 'all', NULL);
INSERT INTO `sys_role` VALUES ('b16a6b13e98b6eba6770e7ca0ccd4364', '系统管理员', '系统管理员', 'tenantRole', '2021-09-08 02:08:39', '2021-09-08 02:08:39', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('be162921c31d119c8e4d73bd6bdc0ead', '系统管理员', '系统管理员角色, 此角色信息不能删除', 'userRole', '2021-11-24 02:22:11', '2021-11-24 02:22:11', 0, '0fa705bf0824fba0e38ce86fc25326cd', 'all', NULL);
INSERT INTO `sys_role` VALUES ('c366440615bbad4246de81522037a419', '7', '7', 'userRole', '2021-10-01 23:48:41', '2021-10-21 01:49:43', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('c7d1bcdc2f61f21ebe1e872325b57ac1', '6', '6', 'userRole', '2021-10-01 23:48:36', '2021-10-22 00:00:48', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('ccb09663b4e6cbf4cc8f4b7bf2a5ebf5', 'younglinuxer', 'younglinuxer', 'userRole', '2021-11-12 23:26:52', '2021-11-12 23:26:59', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('ce905d9a6c73707ca35fb2d9993812d1', '游客', '开发人员', 'userRole', '2021-09-29 23:53:09', '2021-10-20 01:58:18', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('d50e83aa81534aee0197cadb2e1d9cf5', '11', '11', 'userRole', '2021-10-01 23:49:15', '2021-10-15 02:48:45', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('d6afa2885e0bdbc85c4f8b232235465f', '小程序运营客服', '小程序运营客服', 'userRole', '2021-09-16 17:54:34', '2021-09-30 00:01:49', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('e51aab8bf81aac8f173621f37bf3f5a0', '测试角色2', '3', 'userRole', '2021-10-01 23:48:25', '2021-12-01 01:00:48', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('e7ad379f55685524b304c717ee181344', '测试角色', '角色面搜', 'userRole', '2021-10-08 18:16:49', '2021-10-21 18:31:01', 0, '9f8765b3faae36d71728369665cc7d33', 'all', NULL);
INSERT INTO `sys_role` VALUES ('e84aefeed0936072f2bd7dae59d33b08', '功能体验租户角色', '角色用于平台功能试用场景，提供基础系统能力，系统给管理，开发套件，演示的场景等。', 'tenantRole', '2021-12-08 18:20:34', '2021-12-08 18:20:34', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('ea146a29a58050c15800989f71937c29', '荆棘鸟特殊角色', '荆棘鸟特殊角色', 'tenantRole', '2021-11-18 17:24:28', '2021-11-18 17:24:28', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('ebc6cc0defa8538155c29a20bc94847b', '9', '9', 'userRole', '2021-10-01 23:49:06', '2021-10-15 02:49:28', 1, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('eecfc7d3f86060224dd19c4db7579956', '游客', '游客角色, 此角色信息不能删除, 当用户通过注册方式进入组织, 将自动赋予此权限', 'userRole', '2021-11-18 22:35:12', '2021-11-18 22:35:12', 0, 'b7566ac352652eacca9b383120e7d566', 'all', NULL);
INSERT INTO `sys_role` VALUES ('f7898b140f4f84a9285f732603ebb810', '普通级租户角色', '具备基础功能权限', 'tenantRole', '2021-09-30 18:27:06', '2021-10-30 18:30:02', 0, '1', 'all', NULL);
INSERT INTO `sys_role` VALUES ('f89cf27ea3c4027f36ae3094f4c53877', '运维管理', '是大V不带', 'userRole', '2021-09-17 18:00:13', '2021-09-30 00:01:49', 0, '1', 'all', NULL);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `role_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID',
  `type` enum('menu','button') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资源类型(数据权限，和网络权限，菜单,、租户，分别对应的为sys_permission\\sys_menu\\sys_tenant',
  `permission_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资源ID',
  `apply_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用ID方便查询'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for sys_sql
-- ----------------------------
DROP TABLE IF EXISTS `sys_sql`;
CREATE TABLE `sys_sql`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `application_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务名',
  `executed_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '执行SQL',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '执行时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `consuming_time` int(2) NULL DEFAULT NULL COMMENT '消耗时间',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '租户ID默认1',
  `slow_sql` tinyint(1) NULL DEFAULT NULL COMMENT '是否是慢sql',
  `access_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问类型',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `application_name`(`application_name`, `tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SQL执行记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_sql
-- ----------------------------

-- ----------------------------
-- Table structure for sys_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant`  (
  `id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公司ID',
  `name` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公司全称',
  `short_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '系统名称',
  `addr` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司地址',
  `desc_msg` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司简介',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '是否删除  -1：已删除  0：正常',
  `admin_user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '管理员Id',
  `login_domain` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录域名',
  `default_index_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认首页',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用 1启用 0禁用',
  `icon` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'icon',
  `logo` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'logo',
  `bg_img` varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '背景图',
  `parent_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级租户id,如果是顶级租户，该值和顶级租户的id一样',
  `remark` json NULL COMMENT '备注其它信息\r\n',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '公司租户管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_tenant
-- ----------------------------
INSERT INTO `sys_tenant` VALUES ('1', '\r\n重庆软开企业服务有限公司', 'JVS快速开发服务平台', '测试地址', '此为测试企业信息，如果需要私有化部署请联系我们。', '2021-11-15 09:15:15', '2021-08-27 17:41:28', '0', '1', 'demo', '', 1, 'http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643778850076921856-bitbug_favicon.ico', 'http://jvsoss.bctools.cn/jvs-public/tenantPicture/2021-11-12-643809521591816192-bb.png', 'http://jvs-public.oss-cn-beijing.aliyuncs.com/tenantPicture2021-09-09-620688438873460736%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20210909183336.png', '0', '{}');

-- ----------------------------
-- Table structure for sys_tree
-- ----------------------------
DROP TABLE IF EXISTS `sys_tree`;
CREATE TABLE `sys_tree`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称, 同一层级不能重复',
  `value` varchar(2550) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示值',
  `unique_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '唯一标识, 作为该字典被引用的key',
  `group_id` bigint(20) NULL DEFAULT NULL COMMENT '分组id, 区分不同的字典树',
  `sort` int(10) NULL DEFAULT NULL COMMENT '排序',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `parent_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级ID, 根节点为-1',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '租户ID',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义树,可以针对所有的树型结构进行处理,使用方式与字典类似' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_tree
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户',
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '真名',
  `user_type` enum('BACKEND_USER','FRONT_USER','OTHER_USER') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户类型。\r\n1、后端用户\r\n2、前端用户\r\n3、其它业务系统用户',
  `account_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '帐号名',
  `email` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮件',
  `head_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico' COMMENT '头像',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '$2a$10$rV8aIFfDTg6G2SsQdkE9C.Be1kFb0kery5akAh8pdjc3C9c.6lLiu' COMMENT '密码(默认123456)',
  `phone` char(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `cancel_flag` tinyint(1) NULL DEFAULT 0 COMMENT '0-正常，1- 注销  不要逻辑删除，删除后，业务找不到数据',
  `sex` enum('male','female','unknown') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'unknown' COMMENT '性别 ',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `invite` char(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邀请用户的ID做上下级关系',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_name`(`account_name`) USING BTREE,
  UNIQUE INDEX `phone`(`phone`) USING BTREE COMMENT '手机号索引全局唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', 'BACKEND_USER', 'admin', NULL, 'https://jvs-public.oss-cn-beijing.aliyuncs.com/favicon%20%284%29.ico', '$2a$10$xzWjUsBePiTeq.g/iskz2OqseEE6c.7Vtb86ctdLAh8wcWafkVwTO', '13555555555', '2021-03-30 01:59:56', '2022-08-25 17:13:12', 0, 'male', '2021-08-27', NULL);

-- ----------------------------
-- Table structure for sys_user_extension
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_extension`;
CREATE TABLE `sys_user_extension`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主鍵',
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '三方类型',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称显示',
  `open_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '三方唯一键',
  `extension` json NULL COMMENT '三方信息扩展字段',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` json NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户扩展信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_extension
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_external_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_external_group`;
CREATE TABLE `sys_user_external_group`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分组名称',
  `del_flag` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标记',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `config_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱配置id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `create_by_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dept_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `job_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '外部用户分组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_external_group
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_external_mail
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_external_mail`;
CREATE TABLE `sys_user_external_mail`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id（发送人）',
  `group_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '分组id',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `del_flag` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标记',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `create_by_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '外部用户邮箱' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_external_mail
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_group`;
CREATE TABLE `sys_user_group`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户组名称',
  `users` json NULL COMMENT '用户数组对象',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建人',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '租户ID',
  `principal_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '负责人姓名',
  `principal_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '负责人id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_group
-- ----------------------------
INSERT INTO `sys_user_group` VALUES ('064a2c4b1643edc61889667a1b310f36', 'test02', '[\"1\"]', '2021-10-01 04:25:39', 'admin', '1', NULL, NULL);
INSERT INTO `sys_user_group` VALUES ('341b943e514a0cb76a738dc11140cc52', 'test003', '[\"57b6eb97675cbe5872ed21a6d9b43ca4\", \"07496f9194bad2b81f6bd9f8014019f8\", \"1\", \"7ac5c45bc1fda8c74ebc023d9341f723\"]', '2021-11-23 17:45:39', 'admin', '1', NULL, NULL);
INSERT INTO `sys_user_group` VALUES ('4bb14248f92a1bce42b9c86e4a071ab9', '测试组01', '[\"07496f9194bad2b81f6bd9f8014019f8\", \"0fa38a30ea09f27114122bfdc87de669\", \"28dea7db9fdc53cb15327c2b094733fa\", \"d0541b16e72ef98a08a8463bf796f4be\", \"ccef2a9fcaf998fbd1e32cb0d648c5ee\", \"ccac6f6cdaaee9a0abef7f11cb66c0a8\", \"c3d1a7415a38efaa5e2a706dc479b2b7\", \"b7cbed75aee4ee20ec9a5c9dd633ca82\", \"45595b4eafb4027ee61dbdcedbde7282\", \"57b6eb97675cbe5872ed21a6d9b43ca4\", \"57b6eb97675cbe5872ed21a6d9b43ca4\", \"57b6eb97675cbe5872ed21a6d9b43ca4\"]', '2021-10-11 18:53:44', 'admin', '9f8765b3faae36d71728369665cc7d33', NULL, NULL);
INSERT INTO `sys_user_group` VALUES ('5841382365864434dbb297f2f7d0adba', '测试-群组', '[]', '2021-11-19 19:01:14', 'admin', '1', NULL, NULL);
INSERT INTO `sys_user_group` VALUES ('b064b219f44030ee3c42600badfb660c', '群组2', '[\"0dfc8a1ad48ebf25b738b5736440013e\", \"16b20eb2ff605b2a9da928cd9251b5e4\", \"21cff184e6d201e4ad529226b5864ae4\", \"28dea7db9fdc53cb15327c2b094733fa\", \"3271494df094d1b239f97c0c6d85c67d\", \"8222cb139f62d3d207a04a63564c17a1\", \"7adf626f04647d303ae224e41d37c990\", \"75f651ece997de7dab4eaef3d22a4dd4\", \"b0391462a8c5771a3e842d75a8634c6c\", \"b7cbed75aee4ee20ec9a5c9dd633ca82\", \"c3d1a7415a38efaa5e2a706dc479b2b7\"]', '2021-10-14 18:52:37', 'admin', '9f8765b3faae36d71728369665cc7d33', NULL, NULL);
INSERT INTO `sys_user_group` VALUES ('ef2b0adef74afd04d694a8b4d473acfc', 'test01', '[\"c3d1a7415a38efaa5e2a706dc479b2b7\", \"c3d1a7415a38efaa5e2a706dc479b2b7\", \"1\", \"1\", \"1\", \"1\", \"2\", \"ccac6f6cdaaee9a0abef7f11cb66c0a8\"]', '2021-09-02 01:33:34', 'admin', '1', NULL, NULL);
INSERT INTO `sys_user_group` VALUES ('f418f4b0454c5992d4e38dd765f92583', '协同组', '[\"72ebf41a1c3147420dec28cb68f8d331\", \"65aec37f0dbe55c7046ceffa72c3481d\", \"72ebf41a1c3147420dec28cb68f8d331\", \"57b6eb97675cbe5872ed21a6d9b43ca4\", \"d0541b16e72ef98a08a8463bf796f4be\", \"57b6eb97675cbe5872ed21a6d9b43ca4\"]', '2021-11-02 00:30:39', 'momo', 'b7566ac352652eacca9b383120e7d566', NULL, NULL);
INSERT INTO `sys_user_group` VALUES ('f6581205bde40ffd339c8b86bf3781c3', '搬码组321', '[\"f9f5f28328c93efad32837a298f3f10f\"]', '2021-09-16 19:48:22', 'admin', 'default', NULL, NULL);
INSERT INTO `sys_user_group` VALUES ('f96639d82fe196196a3bdc1304769d38', 'sdfasfgb', '[]', '2021-09-06 17:29:22', 'admin', '1', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_login_log`;
CREATE TABLE `sys_user_login_log`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帐号',
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `operate_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间',
  `device` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备',
  `user_agent` varchar(999) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求标识头',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '登录成功状态',
  `login_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录类型',
  `client_id` char(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '逻辑定义数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_login_log
-- ----------------------------
INSERT INTO `sys_user_login_log` VALUES ('02d539301f8447c203eb84afb8654bf7', '1', 'admin', 'admin', ' 本机地址(127.0.0.1)', '2022-08-25 17:08:24', NULL, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36', NULL, 0, 1, '帐号密码', 'frame');
INSERT INTO `sys_user_login_log` VALUES ('1363e9e4a5aa62a8682f247482d448ad', '1', 'admin', 'admin', ' 局域网IP(10.0.0.71)', '2022-09-06 16:23:47', NULL, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36', NULL, 0, 1, '帐号密码', 'frame');
INSERT INTO `sys_user_login_log` VALUES ('147fc6105c5d2120ddca5ddac39f6315', '1', 'admin', 'admin', ' 局域网IP(10.0.0.71)', '2022-09-05 17:56:44', NULL, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36', NULL, 0, 1, '帐号密码', 'knowledge');
INSERT INTO `sys_user_login_log` VALUES ('522dd51cc598adc5857d521112b018ff', '1', 'admin', 'admin', ' 局域网IP(10.0.0.71)', '2022-09-05 16:51:04', NULL, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36', NULL, 0, 1, '帐号密码', 'knowledge');
INSERT INTO `sys_user_login_log` VALUES ('88d41ef0607a08fe377a82904e6ec06b', '1', 'admin', 'admin', ' 局域网IP(10.0.0.71)', '2022-09-06 16:30:26', NULL, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36', NULL, 0, 1, '帐号密码', 'teamwork');
INSERT INTO `sys_user_login_log` VALUES ('9c82a050d2ae4c0f29a53e6e8ff1cc27', '1', 'admin', 'admin', ' 局域网IP(10.0.0.71)', '2022-09-02 19:56:37', NULL, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36', NULL, 0, 1, '帐号密码', 'frame');
INSERT INTO `sys_user_login_log` VALUES ('b55e93af6f2265c22c7ac74eb14b5458', '1', 'admin', 'admin', ' 局域网IP(10.0.0.71)', '2022-09-02 20:07:40', NULL, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36', NULL, 0, 1, '帐号密码', 'knowledge');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID',
  `role_id` char(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '20b63dcefa237453decd99166edd28aa');
INSERT INTO `sys_user_role` VALUES ('1', '20d7531fa4ef71a981470610754763d2');
INSERT INTO `sys_user_role` VALUES ('1', 'b16a6b13e98b6eba6770e7ca0ccd4364');
INSERT INTO `sys_user_role` VALUES ('1', 'd6afa2885e0bdbc85c4f8b232235465f');
INSERT INTO `sys_user_role` VALUES ('1', '214551c87e0ea28ae8ce2736dfa28866');
INSERT INTO `sys_user_role` VALUES ('1', 'ce905d9a6c73707ca35fb2d9993812d1');
INSERT INTO `sys_user_role` VALUES ('1', '5b41137255bf881b74c645cb8862464c');
INSERT INTO `sys_user_role` VALUES ('1', '5f3ad01bd3f4f4738970adc268768054');

-- ----------------------------
-- Table structure for sys_user_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_tenant`;
CREATE TABLE `sys_user_tenant`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `real_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称,对应用户真名',
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `employee_no` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职工编号',
  `level` int(11) NULL DEFAULT NULL COMMENT '帐号等级',
  `hire_date` date NULL DEFAULT NULL COMMENT '入职时间',
  `job_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位ID',
  `job_name` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位名称',
  `dept_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门ID',
  `dept_name` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `tenant_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '租户ID',
  `cancel_flag` tinyint(1) NULL DEFAULT 0 COMMENT '0-正常，1- 注销  不要逻辑删除，删除后，业务找不到数据',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户和用户关联表（用于确定某个租户下的所有用户）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_tenant
-- ----------------------------
INSERT INTO `sys_user_tenant` VALUES ('1', 'admin', '1', '0001', 3, '2021-08-31', '23b62ab4763bc1b8b525f33808978b91', 'afsd', '8686286b189daf1f4d285e2006eaf6bd', '总经办', '1', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('261ebef165f08d15fdae8b737288734d', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '71bd503f1082cc75797454f1fe7efe76', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('34ff92e47f11e045ba62ff8bd757868d', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '201b40aade955ad9901a908c6f2d2d2d', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('4702eb045c130b97e7a03eb52dc1324c', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '30b19cb44b6eeafcb73337e58d715d60', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('610c24cd5eab3f36f0c3bfcb520ff3a7', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'aeebee9af752dac9400183e955aaa7a4', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('79c3deacaf2cf4092f86ec510cff193b', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '9f8765b3faae36d71728369665cc7d33', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('a8b254e4a091c310c44cde01cea5fbc8', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '1e3e7891aaa9a1a19572b4343e6b4c4a', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('bd982a7eab6fe38ac95fc4dfa851c844', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'f66374133e6c52326dc543762964c39b', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('be487d9300927223ecc474b0ff58f544', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3f8786d5f5a7310faf7980d0d126388e', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('e28fad15a3f3e679880ebdcf19166ee1', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '7a2922d5030d036455956722632b2d0c', 0, '2021-03-29 17:59:56');
INSERT INTO `sys_user_tenant` VALUES ('f618f9a3509b8569821a4b12e44fa4dc', 'admin', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0fa705bf0824fba0e38ce86fc25326cd', 0, '2021-03-29 17:59:56');

SET FOREIGN_KEY_CHECKS = 1;
