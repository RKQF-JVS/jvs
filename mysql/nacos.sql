/*
 Navicat Premium Data Transfer

 Source Server         : 160
 Source Server Type    : MySQL
 Source Server Version : 50739
 Source Host           : 10.0.0.160:3306
 Source Schema         : nacos

 Target Server Type    : MySQL
 Target Server Version : 50739
 File Encoding         : 65001

 Date: 06/09/2022 17:44:43
*/

create database `nacos` default character set utf8mb4 collate utf8mb4_general_ci;
use nacos;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `c_use` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `effect` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `c_schema` text CHARACTER SET utf8 COLLATE utf8_bin NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfo_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 844 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info
-- ----------------------------
INSERT INTO `config_info` VALUES (834, 'application.yml', 'DEFAULT_GROUP', 'spring:\n  servlet:\n    multipart:\n      enabled: true\n      max-request-size: 20MB\n      max-file-size: 20MB\n  rabbitmq:\n    host: jvs-rabbitmq\n    port: 5672\n    username: jvs\n    password: jvs\n    # 使用其他环境时需要覆盖此属性\n    virtual-host: /jvs\n    listener:\n      simple:\n        acknowledge-mode: auto\n  redis:\n    host: jvs-redis\n    port: 6379\n    timeout: 3000\n    database: 0\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://jvs-mysql:3306/jvs?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n    username: root\n    password: root\n    hikari:\n      maximum-pool-size: 100\n\n  cloud:\n  #10.0.0.126:8858\n    sentinel.transport.dashboard: sentinel:8084\n    sentinel.log.dir: log/sentinel\n  jackson:\n    default-property-inclusion: non_null\nlogging:\n  #使用logstash方式日志传递\n  config: classpath:logback-logstash.xml\n  level:\n    #屏蔽 nacos 心跳日志,因为日志打印太频繁\n    com.alibaba.nacos.client.naming: error\n    cn.bctools: trace\nmybatis-plus:\n  global-config:\n    db-config:\n      #全局逻辑删除字段值\n      logic-delete-field: delFlag\n  # 支持统配符 * 或者 ; 分割\n  typeEnumsPackage: cn.bctools.**.entity.enums\n  configuration:\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 使用枚举的name值进行映射\n    default-enum-type-handler: \'\'\n    # 使用枚举的标号进行映射\n    #default-enum-type-handler: org.apache.ibatis.type.EnumOrdinalTypeHandler\n\n\n#SQL性能分析，其它环境建议关闭。只在测试环境进行处理\nsql:\n  #SQL 执行最大时长，毫秒，如果超长会有提示\n  maxTime: 200\n  #是否开启日志记录\n  log: true\n  # SQL分析,将SQL拿来做explain解释器\n  explainIs: true\n  # 动态创建租户数据库,租户id动态路由开关\n  dynamicTenantDatabase: false\n\n\n#阿里短信 请直接在阿里应用市场申请\nsms:\n  signature: healthylife\n  accessKeyId: \n  accessKeySecret: \n  template:\n    login: SMS_187752507\n\n#文件存储内容\noss:\n  #选择阿里oss存储   name : minio   建议使用minio最新版本，oss需要一个桶绑定一个域名， 否则前端使用时会导致图片无法显示\n  name: minio\n  endpoint: http://jvs-minio:9000\n  access-key: miniominio\n  secret-key: miniominio\n  #声明公共桶，获取地址的时候直接为公有地址\n  publicBuckets:\n    - jvs-public\n    - jvs-auth\n    \n#是否做加密数据返回处理\ngateway:\n  #网关返回数据加密,返回数据为R并为成功时才加密，失败不加密,只加密mgr的数据\n  encrypt: true\n  #前端对应的加密，后端权限扫描器数据传输加密,现只加密mgr的请求，其它请求不加密\n  encryptKey: jvs\n\n\n#网关地址 授权认证\ngatewayUrl: http://localhost:10000\n#认证授权地址\noauthUrl: http://localhost:3000\nsecurity:\n  oauth2:\n    client:\n      # 默认使用项目名做为client\n      client-id: frame\n      client-secret: frame\n      access-token-uri: ${oauthUrl}/oauth/token\n      user-authorization-uri: ${oauthUrl}/oauth/authorize\n      scope: server\n    resource:\n      token-info-uri: ${oauthUrl}/oauth/check_token\n      user-info-uri: ${oauthUrl}/user\n    authorization:\n      check-token-access: ${oauthUrl}/oauth/check_token\nwx:\n  miniapp:\n    appid: xxx\n    secret: xxx\n    config-storage:\n      type: redistemplate\n  #微信公众号 登录使用\n  # mp:\n  #   appId:   \n  #   secret:  \n  #   token:  \n  #   aesKey: ', '8f1f03e56fcc82f82ebf6fd81c84c53c', '2021-12-16 00:30:41', '2022-10-14 03:42:29', NULL, '172.20.0.1', '', 'jvs', '', '', '', 'yaml', '');
INSERT INTO `config_info` VALUES (835, 'jvs-gateway', 'jvs', 'server: \n  port: 10000\n#是否做加密数据返回处理\ngateway:\n  #网关返回数据加密,返回数据为R并为成功时才加密，失败不加密,只加密mgr的数据\n  encrypt: true\n  #前端对应的加密，后端权限扫描器数据传输加密,现只加密mgr的请求，其它请求不加密\n  encryptKey: jvs\n  \nspring:\n  servlet:\n    multipart:\n      max-file-size: 20MB\n      max-request-size: 20MB\n\n', 'cbf66d588544070dd60a0a07ce33590c', '2021-12-16 00:30:41', '2022-08-24 22:28:26', NULL, '10.0.0.71', '', 'jvs', '', '', '', 'yaml', '');
INSERT INTO `config_info` VALUES (836, 'jvs-auth-parent.yml', 'DEFAULT_GROUP', '\n#扫码登录三方帐号和回调地址\njustauth:\n  enabled: true\n  cache:\n    type: redis\n    prefix: \'justauth:social:state:\'\n    timeout: 1h\n  type:\n  #开放平台微信扫码登录，需要创建应用\n    WECHAT_OPEN:\n      client-id: \n      client-secret: \n      redirect-uri: \n ', 'efdfb98cda43909269bcfc37addd033d', '2021-12-16 00:30:41', '2021-12-16 00:30:41', NULL, '172.23.0.1', '', 'jvs', NULL, NULL, NULL, 'yaml', NULL);
INSERT INTO `config_info` VALUES (837, 'jvs-apply-document.yml', 'DEFAULT_GROUP', 'spring:\n  elasticsearch:\n    rest:\n      uris: jvs-elasticsearch:9400\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://jvs-mysql:3306/jvs-document?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n    username: root\n    password: root\n    hikari:\n      maximum-pool-size: 100', 'da425ed25605d59c5ce486140def9ca6', '2022-09-02 07:38:16', '2022-09-05 06:20:07', NULL, '10.0.0.71', '', 'jvs', '', '', '', 'yaml', '');
INSERT INTO `config_info` VALUES (842, 'jvs-teamwork.yml', 'DEFAULT_GROUP', 'spring:\r\n  datasource:\r\n    driver-class-name: com.mysql.cj.jdbc.Driver\r\n    url: jdbc:mysql://jvs-mysql:3306/jvs-teamwork?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\r\n    username: root\r\n    password: root\r\n    hikari:\r\n      maximum-pool-size: 100', '80d6f8dc163652e0d1aa89e6f49b71d3', '2022-09-06 03:12:11', '2022-09-06 03:12:11', NULL, '10.0.0.71', '', 'jvs', NULL, NULL, NULL, 'yaml', NULL);

-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'datum_id',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfoaggr_datagrouptenantdatum`(`data_id`, `group_id`, `tenant_id`, `datum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '增加租户字段' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_aggr
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfobeta_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info_beta' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_beta
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfotag_datagrouptenanttag`(`data_id`, `group_id`, `tenant_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info_tag' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_tag
-- ----------------------------

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`) USING BTREE,
  UNIQUE INDEX `uk_configtagrelation_configidtag`(`id`, `tag_name`, `tag_type`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_tag_relation' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '集群、各Group容量信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info`  (
  `id` bigint(64) UNSIGNED NOT NULL,
  `nid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `src_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `op_type` char(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`nid`) USING BTREE,
  INDEX `idx_gmt_create`(`gmt_create`) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified`) USING BTREE,
  INDEX `idx_did`(`data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1187 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '多租户改造' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of his_config_info
-- ----------------------------
INSERT INTO `his_config_info` VALUES (834, 1186, 'application.yml', 'DEFAULT_GROUP', '', 'spring:\n  servlet:\n    multipart:\n      enabled: true\n      max-request-size: 20MB\n      max-file-size: 20MB\n  rabbitmq:\n    host: 10.0.0.160\n    port: 5672\n    username: jvs\n    password: jvs\n    # 使用其他环境时需要覆盖此属性\n    virtual-host: /jvs\n    listener:\n      simple:\n        acknowledge-mode: auto\n  redis:\n    host: 10.0.0.160\n    port: 6379\n    timeout: 3000\n    database: 0\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://10.0.0.160:3306/jvs?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true\n    username: root\n    password: root\n    hikari:\n      maximum-pool-size: 100\n\n  cloud:\n  #10.0.0.126:8858\n    sentinel.transport.dashboard: sentinel:8084\n    sentinel.log.dir: log/sentinel\n  jackson:\n    default-property-inclusion: non_null\nlogging:\n  #使用logstash方式日志传递\n  config: classpath:logback-logstash.xml\n  level:\n    #屏蔽 nacos 心跳日志,因为日志打印太频繁\n    com.alibaba.nacos.client.naming: error\n    cn.bctools: trace\nmybatis-plus:\n  global-config:\n    db-config:\n      #全局逻辑删除字段值\n      logic-delete-field: delFlag\n  # 支持统配符 * 或者 ; 分割\n  typeEnumsPackage: cn.bctools.**.entity.enums\n  configuration:\n    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl\n    # 使用枚举的name值进行映射\n    default-enum-type-handler: \'\'\n    # 使用枚举的标号进行映射\n    #default-enum-type-handler: org.apache.ibatis.type.EnumOrdinalTypeHandler\n\n\n#SQL性能分析，其它环境建议关闭。只在测试环境进行处理\nsql:\n  #SQL 执行最大时长，毫秒，如果超长会有提示\n  maxTime: 200\n  #是否开启日志记录\n  log: true\n  # SQL分析,将SQL拿来做explain解释器\n  explainIs: true\n  # 动态创建租户数据库,租户id动态路由开关\n  dynamicTenantDatabase: false\n\n\n#阿里短信 请直接在阿里应用市场申请\nsms:\n  signature: healthylife\n  accessKeyId: \n  accessKeySecret: \n  template:\n    login: SMS_187752507\n\n#文件存储内容\noss:\n  #选择阿里oss存储   name : minio   建议使用minio最新版本，oss需要一个桶绑定一个域名， 否则前端使用时会导致图片无法显示\n  name: minio\n  endpoint: http://10.0.0.160:9000\n  access-key: miniominio\n  secret-key: miniominio\n  #声明公共桶，获取地址的时候直接为公有地址\n  publicBuckets:\n    - jvs-public\n    - jvs-auth\n    \n#是否做加密数据返回处理\ngateway:\n  #网关返回数据加密,返回数据为R并为成功时才加密，失败不加密,只加密mgr的数据\n  encrypt: true\n  #前端对应的加密，后端权限扫描器数据传输加密,现只加密mgr的请求，其它请求不加密\n  encryptKey: jvs\n\n\n#网关地址 授权认证\ngatewayUrl: http://localhost:10000\n#认证授权地址\noauthUrl: http://localhost:3000\nsecurity:\n  oauth2:\n    client:\n      # 默认使用项目名做为client\n      client-id: frame\n      client-secret: frame\n      access-token-uri: ${oauthUrl}/oauth/token\n      user-authorization-uri: ${oauthUrl}/oauth/authorize\n      scope: server\n    resource:\n      token-info-uri: ${oauthUrl}/oauth/check_token\n      user-info-uri: ${oauthUrl}/user\n    authorization:\n      check-token-access: ${oauthUrl}/oauth/check_token\nwx:\n  miniapp:\n    appid: xxx\n    secret: xxx\n    config-storage:\n      type: redistemplate\n  #微信公众号 登录使用\n  # mp:\n  #   appId:   \n  #   secret:  \n  #   token:  \n  #   aesKey: ', 'ca3d6bd2a264d0b4ecbbedf1bcd39609', '2022-10-14 16:42:29', '2022-10-14 03:42:29', NULL, '172.20.0.1', 'U', 'jvs');

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `role` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `resource` varchar(512) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `action` varchar(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  UNIQUE INDEX `uk_role_permission`(`role`, `resource`, `action`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES ('nacosjvs', 'jvs:*:*', 'r');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `role` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  UNIQUE INDEX `idx_user_role`(`username`, `role`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('ZXyjXjrCJZpQmczM', 'ROLE_ADMIN');

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '租户容量信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_info_kptenantid`(`kp`, `tenant_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'tenant_info' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tenant_info
-- ----------------------------
INSERT INTO `tenant_info` VALUES (6, '1', 'jvs', 'jvs', 'jvs', 'nacos', 1629336775823, 1629336775823);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('nacos', '$2a$10$wXCNtzCsAV0NtkY6w274IuDIkcSTK3EFVocAsxSDTlHSNFI/svjCS', 1);

SET FOREIGN_KEY_CHECKS = 1;
