create database `jvs-team-work-ultimate` default character set utf8mb4 collate utf8mb4_general_ci;
use jvs-team-work-ultimate;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for message_notice
-- ----------------------------
DROP TABLE IF EXISTS `message_notice`;
CREATE TABLE `message_notice`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `receiver_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接收人角色(项目拥有者...)',
  `message_template_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息模板id',
  `platform` enum('EMAIL','WECHAT_WORK_AGENT','WECHAT_WORK_ROBOT','WECHAT_OFFICIAL_ACCOUNT','DING_TALK_CORP','DING_TALK_ROBOT','ALI_SMS','INSIDE_NOTIFICATION') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'INSIDE_NOTIFICATION' COMMENT '平台（站内信 邮件 短信 公众号）',
  `relevance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联id',
  `relevance_type` enum('task','project','schedule','okr_objectives','okr_key_result') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'project' COMMENT '关联类型（项目）',
  `activation_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否激活',
  `event_code` enum('project_create','project_update','project_delete','project_pigeonhole','project_move_recycle','task_create','task_update','task_delete','task_move_recycle','task_finished','okr_objective_update','okr_objective_finished','okr_objective_del','okr_key_result_update','okr_key_result_finished','okr_key_result_del','plan_probation') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '事件触发代码',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编辑人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息通知' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_notice
-- ----------------------------

-- ----------------------------
-- Table structure for message_template
-- ----------------------------
DROP TABLE IF EXISTS `message_template`;
CREATE TABLE `message_template`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板名称',
  `platform` enum('EMAIL','WECHAT_WORK_AGENT','WECHAT_WORK_ROBOT','WECHAT_OFFICIAL_ACCOUNT','DING_TALK_CORP','DING_TALK_ROBOT','ALI_SMS','INSIDE_NOTIFICATION') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'INSIDE_NOTIFICATION' COMMENT '平台（站内信 邮件 短信 公众号）',
  `template_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台申请的模板code',
  `template_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板配置（参数对应）',
  `sms_signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '短信签名',
  `template_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板内容',
  `belong_to` enum('task','project','schedule','okr_objectives','okr_key_result') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'project' COMMENT '模板归属类型',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编辑人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息模板' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_template
-- ----------------------------

-- ----------------------------
-- Table structure for message_type_variable
-- ----------------------------
DROP TABLE IF EXISTS `message_type_variable`;
CREATE TABLE `message_type_variable`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` enum('task','project','schedule','okr_objectives','okr_key_result') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'project' COMMENT '类型',
  `variable_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变量id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编辑人名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UQ_TYPE_VARIABLE_ID`(`type`, `variable_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息-类型-变量' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_type_variable
-- ----------------------------
INSERT INTO `message_type_variable` VALUES ('08f28b5c3faf43810d6d1785ee065d94', 'okr_objectives', '3ab8cd06b5fa4cd35fedcbf23cab11a9', '2022-06-29 17:10:37', '2022-06-29 17:10:37', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('12ed28884820eb54701e811397831d72', 'project', '6bf73acff9f3206fa6f33d16a532834b', '2022-06-29 17:04:58', '2022-06-29 17:04:58', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('173001da2ea9083b65f905020aea3c3d', 'okr_objectives', '1f3395a6b4ae9846b1d900242c9f84f6', '2022-06-29 17:10:42', '2022-06-29 17:10:42', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('2cf6f7d04cae2c8ca91c8b71df7ca1d8', 'task', '1f3395a6b4ae9846b1d900242c9f84f6', '2022-06-29 17:12:41', '2022-06-29 17:12:41', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('3292e2b4dca2d2eb8b9ca1b0199afe77', 'project', '768e01159480742a621f1c6dccda4ec8', '2022-06-29 17:04:52', '2022-06-29 17:04:52', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('32a3ed5a2552f3f37147e9f244f89110', 'project', 'ea5c4ba0628369a45e0667124868b30c', '2022-07-06 11:05:28', '2022-07-06 11:05:28', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('3fb6f120c6b92a76971779670612c287', 'okr_key_result', 'cdc634301f4956d70ab2e1bc89b52a08', '2022-06-29 17:11:54', '2022-06-29 17:11:54', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('4474b65a8d18c1257fd0cde01e59af10', 'project', '1f3395a6b4ae9846b1d900242c9f84f6', '2022-06-29 17:05:11', '2022-06-29 17:05:11', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('5b27989634396b1de847f76756a2eb66', 'okr_key_result', 'c5647999d0bd9570aa6cbe46541b249f', '2022-06-29 17:11:50', '2022-06-29 17:11:50', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('5da48547f3dcb2c6bd66afcd1c4a2bed', 'okr_key_result', '797d8ce330f1180fb6737e6b1c6e84be', '2022-06-29 17:12:04', '2022-06-29 17:12:04', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('62b65b8bd2296f406fe3d4dccb841908', 'task', '0c86f854c8446c3f88cf0cb3e9263815', '2022-06-29 17:04:26', '2022-06-29 17:04:26', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('6feb0435c11c627088904980762eab16', 'task', '160db8d20e4bfe45e1fdd13b22b7faad', '2022-06-29 16:39:05', '2022-06-29 16:39:05', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('713bbcc27f839d473d8c948a0e6248c0', 'task', '5af6dccd51da280c53602bfa2da2c2df', '2022-06-29 17:04:18', '2022-06-29 17:04:18', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('76ac07a26741ad268b5e37e8b0d0523b', 'project', '12dd9c6e31f9c97eacdd1415025b9814', '2022-06-29 17:13:50', '2022-06-29 17:13:50', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('82a705932339788b8b377587e9d5c492', 'project', 'c18aac4fc97ce12afd98c957575b4996', '2022-06-29 17:04:37', '2022-06-29 17:04:37', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('82bebce28edb2681043c0a0e96393611', 'task', 'ea5c4ba0628369a45e0667124868b30c', '2022-07-06 11:05:37', '2022-07-06 11:05:37', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('82d20c80ecc325af059bd338e39c94af', 'okr_objectives', 'c5647999d0bd9570aa6cbe46541b249f', '2022-06-29 17:10:46', '2022-06-29 17:10:46', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('871fc444520dc589d3a33c43f8db2c9c', 'task', '0c16577010910cab69bf302b00508ed7', '2022-06-29 17:04:10', '2022-06-29 17:04:10', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('9268a6527c6d09cdd1563a5a41c48559', 'project', '4e1c0256eda5967e562d96772e0f6c9a', '2022-06-29 17:13:45', '2022-06-29 17:13:45', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('b72e354a197deefa41d5a5cc23d10a7f', 'okr_key_result', '86395121a610a538936f361f6c5651d3', '2022-06-29 17:12:00', '2022-06-29 17:12:00', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('d2d269d9b398e06cc481a65f2da36550', 'task', 'c5647999d0bd9570aa6cbe46541b249f', '2022-06-29 17:12:46', '2022-06-29 17:12:46', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('db759382530b915b4ab9dfe3b56f33f0', 'okr_objectives', 'ea5c4ba0628369a45e0667124868b30c', '2022-07-06 11:05:51', '2022-07-06 11:05:51', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('dc5ab5f7e520c5423d93d9bf48f19665', 'okr_objectives', '287076dda40d3a9df514818e2a0f844a', '2022-06-29 17:07:20', '2022-06-29 17:07:20', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('de7bb938c2447ea213c44d9e42c14204', 'okr_objectives', '73bda205188feb87b23270f65d8dacc6', '2022-06-29 17:06:37', '2022-06-29 17:05:34', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('e2a6e525b54aec4e1f8dfcc6fd3b36cd', 'okr_key_result', '1f3395a6b4ae9846b1d900242c9f84f6', '2022-06-29 17:11:45', '2022-06-29 17:11:45', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('e6211ca1b36a51084a4401de7e2e1a34', 'okr_key_result', 'af8ab336204fbcc12c4a3a7049f9f08c', '2022-06-29 17:12:11', '2022-06-29 17:12:11', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('ebdf7415a9244bcf95e3255054143f60', 'okr_key_result', 'ea5c4ba0628369a45e0667124868b30c', '2022-07-06 11:05:45', '2022-07-06 11:05:45', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_type_variable` VALUES ('f4c25b75a727990419e366b71cdf46d1', 'project', 'c5647999d0bd9570aa6cbe46541b249f', '2022-06-29 17:05:06', '2022-06-29 17:05:06', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');

-- ----------------------------
-- Table structure for message_variable
-- ----------------------------
DROP TABLE IF EXISTS `message_variable`;
CREATE TABLE `message_variable`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `variable_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `variable_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变量code',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编辑人名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UQ_VARIABLE_CODE`(`variable_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息 -事件-变量' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_variable
-- ----------------------------
INSERT INTO `message_variable` VALUES ('0c16577010910cab69bf302b00508ed7', '任务开始时间', 'taskStartTime', '2022-06-06 09:53:55', '2022-06-06 09:53:55', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('0c86f854c8446c3f88cf0cb3e9263815', '任务结束时间', 'taskEndTime', '2022-06-06 09:54:20', '2022-06-06 09:54:20', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('12dd9c6e31f9c97eacdd1415025b9814', '计划结束时间', 'endTime', '2022-06-06 10:18:49', '2022-07-06 10:13:56', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('160db8d20e4bfe45e1fdd13b22b7faad', '任务名称', 'taskName', '2022-06-06 09:53:26', '2022-06-06 09:53:26', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('1f3395a6b4ae9846b1d900242c9f84f6', '操作时间', 'operateTime', '2022-06-29 09:04:54', '2022-06-29 09:04:54', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('287076dda40d3a9df514818e2a0f844a', '目标分数', 'fraction', '2022-06-29 09:05:52', '2022-06-29 09:05:52', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('3ab8cd06b5fa4cd35fedcbf23cab11a9', '目标进度', 'progress', '2022-06-29 09:05:43', '2022-06-29 09:05:43', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('4e1c0256eda5967e562d96772e0f6c9a', '计划归档时间', 'accomplishTime', '2022-06-06 10:19:03', '2022-07-06 10:14:00', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('5af6dccd51da280c53602bfa2da2c2df', '任务备注', 'taskRemark', '2022-06-06 09:54:35', '2022-06-06 09:54:35', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('6bf73acff9f3206fa6f33d16a532834b', '计划介绍', 'projectIntroduce', '2022-06-02 10:36:40', '2022-07-06 10:13:45', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('73bda205188feb87b23270f65d8dacc6', '目标名称', 'name', '2022-06-29 09:03:37', '2022-06-29 09:03:37', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('768e01159480742a621f1c6dccda4ec8', '计划名称', 'projectName', '2022-06-02 10:36:26', '2022-07-06 10:13:42', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('797d8ce330f1180fb6737e6b1c6e84be', '关键结果名称', 'key_result_name', '2022-06-29 09:07:19', '2022-06-29 09:07:19', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('86395121a610a538936f361f6c5651d3', '关键结果权重', 'key_result_weight', '2022-06-29 09:07:52', '2022-06-29 09:07:52', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('af8ab336204fbcc12c4a3a7049f9f08c', '关键结果分数', 'key_result_fraction', '2022-06-29 09:08:03', '2022-06-29 09:08:03', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('c18aac4fc97ce12afd98c957575b4996', '计划开始时间', 'startTime', '2022-06-06 10:18:35', '2022-07-06 10:13:38', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('c5647999d0bd9570aa6cbe46541b249f', '操作人', 'operator', '2022-06-06 09:57:28', '2022-06-06 09:57:22', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('cdc634301f4956d70ab2e1bc89b52a08', '关键结果进度', 'key_result_progress', '2022-06-29 09:07:41', '2022-06-29 09:07:41', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');
INSERT INTO `message_variable` VALUES ('ea5c4ba0628369a45e0667124868b30c', '系统名称', 'systemName', '2022-07-06 11:04:09', '2022-07-06 11:04:09', '484929d49e10f203a7fc536e9582f629', 'wl', 'wl');

-- ----------------------------
-- Table structure for okr_cycle
-- ----------------------------
DROP TABLE IF EXISTS `okr_cycle`;
CREATE TABLE `okr_cycle`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'okr 周期id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '周期名称',
  `start_at` date NULL DEFAULT NULL COMMENT '周期开始时间 yyyy-MM',
  `end_at` date NULL DEFAULT NULL COMMENT '周期结束时间 yyyy-MM',
  `param_year` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数1 year 年份',
  `param_defined` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数2 自定义数值',
  `param_unit` enum('WEEK','MONTH','QUARTER','ALL') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'WEEK' COMMENT '参数3 自定义单位',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '编辑时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'okr 周期表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of okr_cycle
-- ----------------------------

-- ----------------------------
-- Table structure for okr_key_result
-- ----------------------------
DROP TABLE IF EXISTS `okr_key_result`;
CREATE TABLE `okr_key_result`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'okr 关键结果id',
  `objectives_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'okr 所属目标id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'okr 关键结果名称',
  `progress` double(4, 1) NULL DEFAULT NULL COMMENT 'okr 结果进度',
  `weight` int(3) NULL DEFAULT 1 COMMENT 'okr 关键结果权重',
  `relevancy_is` tinyint(1) NULL DEFAULT 0 COMMENT 'okr 关键结果是否关联项目',
  `relevancy_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'okr 关键结果关联id(项目)',
  `fraction` double(4, 1) NULL DEFAULT 0.0 COMMENT '分数',
  `sort` int(3) NULL DEFAULT NULL COMMENT '排序序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'okr 关键结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of okr_key_result
-- ----------------------------

-- ----------------------------
-- Table structure for okr_objectives
-- ----------------------------
DROP TABLE IF EXISTS `okr_objectives`;
CREATE TABLE `okr_objectives`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'okr 目标id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'okr 目标名称',
  `type` enum('company','dept','team','person') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'person' COMMENT 'okr 目标类型',
  `visible_range` enum('all','specify_scope') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'all' COMMENT 'okr 目标可见性',
  `parent_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'okr 父级目标id',
  `status` enum('normal','risk','extended') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'normal' COMMENT 'okr 目标状态',
  `cycle_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '周期id',
  `fraction` double(4, 1) NULL DEFAULT 0.0 COMMENT '分数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
  `auto_progress` tinyint(1) NULL DEFAULT 0 COMMENT '是否自动更新进度',
  `progress` double(4, 1) NULL DEFAULT NULL COMMENT 'okr 目标进度',
  `push_message_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否推送消息',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编辑人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'okr_目标表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of okr_objectives
-- ----------------------------

-- ----------------------------
-- Table structure for okr_objectives_his
-- ----------------------------
DROP TABLE IF EXISTS `okr_objectives_his`;
CREATE TABLE `okr_objectives_his`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'okr 目标id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'okr 目标名称',
  `type` enum('company','dept','team','person') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'person' COMMENT 'okr 目标类型',
  `visible_range` enum('all','specify_scope') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'all' COMMENT 'okr 目标可见性',
  `parent_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'okr 父级目标id',
  `status` enum('normal','risk','extended') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'normal' COMMENT 'okr 目标状态',
  `cycle_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '周期id',
  `fraction` double(4, 1) NULL DEFAULT 0.0 COMMENT '分数',
  `progress` double(4, 1) NULL DEFAULT NULL COMMENT 'okr 目标进度',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑时间',
  `auto_progress` tinyint(1) NULL DEFAULT 0 COMMENT '是否自动更新进度',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编辑人名称',
  `record_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '历史记录生成时间',
  `push_message_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否推送消息',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'okr_目标表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of okr_objectives_his
-- ----------------------------

-- ----------------------------
-- Table structure for plugins
-- ----------------------------
DROP TABLE IF EXISTS `plugins`;
CREATE TABLE `plugins`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `plugin_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件名称',
  `plugin_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件描述',
  `plugin_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件图片',
  `plugin_type` enum('base') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'base' COMMENT '插件类型',
  `sort_num` int(11) NULL DEFAULT 0 COMMENT '插件优先级（排序）',
  `plugin_router` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件路由',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '插件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of plugins
-- ----------------------------
INSERT INTO `plugins` VALUES ('c98b5fe09c39abe76bd3d758c4d60816', '任务', '任务看板', 'iconfont icon-renwu', 'base', 1, '/project/space/task');
INSERT INTO `plugins` VALUES ('50e525fc8387a1c0ef1907a30072c1e4', '概览', '任务概览', 'iconfont icon-lvzhou_gailan', 'base', 2, '/project/space/overview');
INSERT INTO `plugins` VALUES ('ed7ee8e9a0fe457d9ee9b876ed089548', '甘特图', '甘特图', 'iconfont icon-gantetu', 'base', 3, '/project/space/gantt');

-- ----------------------------
-- Table structure for project_announcement
-- ----------------------------
DROP TABLE IF EXISTS `project_announcement`;
CREATE TABLE `project_announcement`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '公告内容',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目公告' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_announcement
-- ----------------------------

-- ----------------------------
-- Table structure for project_attention
-- ----------------------------
DROP TABLE IF EXISTS `project_attention`;
CREATE TABLE `project_attention`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `relevance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目关注表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_attention
-- ----------------------------

-- ----------------------------
-- Table structure for project_log
-- ----------------------------
DROP TABLE IF EXISTS `project_log`;
CREATE TABLE `project_log`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `log_type` enum('task_comment','project_create','task_create','task_update_status','task_update_name','task_update_user_claim','task_update_start_time','task_update_remark','task_update_user_assign','task_create_children','task_renewal_children','task_accomplish_children','task_update_priority','task_update_claimant','task_execute_status','task_delete_user_assign','task_add_tag','task_delete_tag','task_clear_start_time','task_update_end_time','task_clear_end_time','task_delete_other_field_value','task_update_other_field_value','project_template_create','project_del','project_template_del','project_move_to_recycle_bin','work_time_save','work_time_update','work_time_del','task_move','okr_objectives_create','okr_objectives_update','okr_objectives_align','okr_key_result_create','okr_key_result_update','okr_key_result_update_name','okr_key_result_update_progress','okr_key_result_update_weight','okr_key_result_update_fraction','okr_key_result_update_head_person','okr_key_result_update_relevancy_id','okr_key_result_del','schedule_create','schedule_update','schedule_remark_create','schedule_remark_update','okr_objectives_update_name','okr_objectives_update_type','okr_objectives_update_status','okr_objectives_update_head_person','okr_objectives_update_progress','okr_objectives_update_cycle','okr_objectives_update_visible_scope','annex_rename') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志类型',
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `relevance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务id或日程id',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `inform_user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '@的人或者被操作的人 指派给 被认领给',
  `comment_value` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '评论 的内容或日志内容',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `file_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文件存储的路径',
  `bucket_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '桶名称',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '源文件名称',
  `type` enum('task','project','schedule','okr_objectives','okr_key_result') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '大的分类',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `second_level_type` enum('update_field_value','comment','save_task','upload_file','work_time_operate','move_task','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '二级分类 方便在不同的模块展示 评论 修改字段',
  `log_action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志动作',
  `file_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件类型',
  `file_name_customization` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_log
-- ----------------------------

-- ----------------------------
-- Table structure for project_master
-- ----------------------------
DROP TABLE IF EXISTS `project_master`;
CREATE TABLE `project_master`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名称',
  `project_introduce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目介绍',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件存储的路径',
  `bucket_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '桶名称',
  `open_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开',
  `push_message_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否推送消息',
  `project_status` enum('underway','pigeonhole') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'underway' COMMENT '项目状态  underway进行中 pigeonhole 归档',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0未删除  1已删除',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `recycle_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否在回收站',
  `show_start_time` tinyint(1) NULL DEFAULT 1 COMMENT '是否需要开始时间任务',
  `prefix_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前缀id',
  `accomplish_time` datetime NULL DEFAULT NULL COMMENT '归档时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `project_type` enum('project','project_template') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目类型',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '其他' COMMENT '分类',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '系统默认模板',
  `whether_defined_prefix` tinyint(1) NULL DEFAULT 0 COMMENT '是否自定义前缀',
  `defined_prefix_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义前缀名称',
  `template_type_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板分类id',
  `date_type` enum('week_day','nature_day') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'week_day' COMMENT '资源投入日期类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_master
-- ----------------------------

-- ----------------------------
-- Table structure for project_member
-- ----------------------------
DROP TABLE IF EXISTS `project_member`;
CREATE TABLE `project_member`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id、目标id',
  `relevance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务、日程、关键结果id',
  `create_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否为创建者 拥有者',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `executor_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否为执行者',
  `member_type` enum('task','project','schedule','okr_objectives','okr_key_result') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型 task任务 project 项目 schedule 日程 okr_objectives目标 okr_key_result 关键结果',
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `data_auth_type` enum('person','dept','job','role','user_group') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'person' COMMENT '数据权限类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目成员和任务参与者' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_member
-- ----------------------------

-- ----------------------------
-- Table structure for project_plugins
-- ----------------------------
DROP TABLE IF EXISTS `project_plugins`;
CREATE TABLE `project_plugins`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `project_master_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目计划id',
  `plugin_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '插件id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UQ_PLUGIN_PROJECT`(`project_master_id`, `plugin_id`) USING BTREE COMMENT '一个项目只能安装一个相同插件'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '计划插件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_plugins
-- ----------------------------

-- ----------------------------
-- Table structure for project_resource_input
-- ----------------------------
DROP TABLE IF EXISTS `project_resource_input`;
CREATE TABLE `project_resource_input`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `role_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色id',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `member_num` int(11) NULL DEFAULT NULL COMMENT '参与人数',
  `job_percentage` double(3, 1) NULL DEFAULT NULL COMMENT '工作占比',
  `start_time` datetime NULL DEFAULT NULL COMMENT '周期开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '周期结束时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目资源投入表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_resource_input
-- ----------------------------

-- ----------------------------
-- Table structure for project_role
-- ----------------------------
DROP TABLE IF EXISTS `project_role`;
CREATE TABLE `project_role`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目id',
  `role_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色id',
  `role_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色类型system 系统 / project 项目',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为默认角色0否1是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_role
-- ----------------------------

-- ----------------------------
-- Table structure for project_schedule
-- ----------------------------
DROP TABLE IF EXISTS `project_schedule`;
CREATE TABLE `project_schedule`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `schedule_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日程/代办名称',
  `schedule_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `remind_type` enum('day','minute') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提醒类型 day 天 minute 分钟',
  `remind_value` int(10) NULL DEFAULT NULL COMMENT '提醒的时间值',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `accomplish_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否完成',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
  `style` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '显示样式',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_schedule
-- ----------------------------

-- ----------------------------
-- Table structure for project_schedule_remark
-- ----------------------------
DROP TABLE IF EXISTS `project_schedule_remark`;
CREATE TABLE `project_schedule_remark`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `schedule_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日程、代办id',
  `schedule_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `remark_sort` int(20) NULL DEFAULT NULL COMMENT '顺序',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日程 代办备注' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_schedule_remark
-- ----------------------------

-- ----------------------------
-- Table structure for project_tag
-- ----------------------------
DROP TABLE IF EXISTS `project_tag`;
CREATE TABLE `project_tag`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名称',
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `colour` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目标签' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_tag
-- ----------------------------

-- ----------------------------
-- Table structure for project_task
-- ----------------------------
DROP TABLE IF EXISTS `project_task`;
CREATE TABLE `project_task`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `task_status` tinyint(1) NULL DEFAULT 0 COMMENT '是否完成',
  `task_start_time` datetime NULL DEFAULT NULL COMMENT '任务开始时间',
  `task_end_time` datetime NULL DEFAULT NULL COMMENT '任务结束时间',
  `task_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `parent_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '上级id',
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `task_priority_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统配置表 id 优先级',
  `working_hours` int(11) NULL DEFAULT NULL COMMENT '总工时',
  `task_list_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否为任务列表',
  `open_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否为公开',
  `sort` int(11) NULL DEFAULT 0 COMMENT '模板任务排序',
  `recycle_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否在回收站',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0未删除  1已删除',
  `task_execute_status_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '-1' COMMENT '系统配置表 id 执行状态',
  `accomplish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `task_list_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '-1' COMMENT '任务列表id',
  `defined_task_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义任务id',
  `defined_suffix_value` int(20) NULL DEFAULT NULL COMMENT '自定义任务id 后缀（第几个创建的任务）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_task
-- ----------------------------

-- ----------------------------
-- Table structure for project_task_tag
-- ----------------------------
DROP TABLE IF EXISTS `project_task_tag`;
CREATE TABLE `project_task_tag`  (
  `task_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务id',
  `tag_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签id',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签与任务的关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_task_tag
-- ----------------------------

-- ----------------------------
-- Table structure for project_task_update_prompts
-- ----------------------------
DROP TABLE IF EXISTS `project_task_update_prompts`;
CREATE TABLE `project_task_update_prompts`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `relevance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务id',
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `attribution_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属id （部门 角色 岗位 群组）',
  `attribution_type` enum('dept','role','job','user_group','person') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'person' COMMENT '归属类型（部门 角色 岗位 群组）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目任务更新提示' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_task_update_prompts
-- ----------------------------

-- ----------------------------
-- Table structure for project_template_type
-- ----------------------------
DROP TABLE IF EXISTS `project_template_type`;
CREATE TABLE `project_template_type`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板分类id',
  `template_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板分类名称',
  `template_sort` int(20) NULL DEFAULT 0 COMMENT '模板分类排序',
  `template_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板分类图标',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模板分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_template_type
-- ----------------------------
INSERT INTO `project_template_type` VALUES ('da3fd8c72cba359ceea74218be8705a1', '模板执行', 0, '#icon-kaoqindaka', '2022-07-08 14:46:21', '2022-07-08 14:46:21', 'f97f7323faf112201eeb1cab9290e949', '谢舒欣', '谢舒欣', '1');

-- ----------------------------
-- Table structure for project_user_top
-- ----------------------------
DROP TABLE IF EXISTS `project_user_top`;
CREATE TABLE `project_user_top`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '计划id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人名称',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目置顶标记' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project_user_top
-- ----------------------------

-- ----------------------------
-- Table structure for project_work_time
-- ----------------------------
DROP TABLE IF EXISTS `project_work_time`;
CREATE TABLE `project_work_time`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `task_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务id',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `consume_time` int(11) NULL DEFAULT NULL COMMENT '消耗时间',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '工作内容',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务工时表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of project_work_time
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
-- Table structure for sys_field_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_field_setting`;
CREATE TABLE `sys_field_setting`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `field_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段名称',
  `field_property` enum('teamwork_text','rich_text','radio','check_box','teamwork_number','teamwork_time') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'teamwork_text' COMMENT '字段属性 teamwork_text 文本输入框 teamwork_number 数字输入框\r\nteamwork_time 日期输入框',
  `field_interior_value` json NULL COMMENT '字段值 例如下拉框值',
  `system_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否为系统默认',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '所属租户id',
  `field_type` enum('system','project_template','interior','project','task','task_template') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字段所属类型 sysstem 系统 project 项目  interior 内置字段',
  `required_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否为必填项',
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目模板id或项目id',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除 0未删除  1已删除',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `field_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户输入的值',
  `relevance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务id或日程id',
  `show_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否在项目的任务列表中展示',
  `source_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '源id 就是从什么地方映射出来的数据 用于更新时的同步更新',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义字段' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_field_setting
-- ----------------------------

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
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件统一管理，不包含租户信息，只做文件转发和统一保存管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_identifying
-- ----------------------------
DROP TABLE IF EXISTS `sys_identifying`;
CREATE TABLE `sys_identifying`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `identifying_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识名称',
  `identifying_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识key',
  `identifying_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标识类型',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_identifying
-- ----------------------------
INSERT INTO `sys_identifying` VALUES ('0142cdfc40750187006240f16255a735', '文件移动', 'library.file.move', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:02:45', '2022-11-25 17:02:45', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('10d5fb64c0a3b40d39446c644d59e1c0', '附件编辑', 'library.annex.edit', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 18:05:29', '2022-11-25 18:05:29', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('16e7e100438beb54abe4f4c5893ce7bd', '移交计划', 'project.handover', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:49:50', '2022-07-01 17:55:00', 'wl');
INSERT INTO `sys_identifying` VALUES ('17af15873268d85a8d696e42316515aa', '文件更名', 'library.file.rename', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:02:33', '2022-11-25 17:02:33', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('258829400c35cab3a700a5c6b458093d', '文件夹移动', 'library.folder.move', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:00:56', '2022-11-25 17:00:56', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('2610bbacc86d894628d8f8dd5db4916d', '移动任务', 'task.move', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:40:00', '2022-03-24 16:40:00', 'wl');
INSERT INTO `sys_identifying` VALUES ('2a4ae5b1a5dc096cdc76eba5ec0085cd', '文件上传', 'library.file.upload', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:02:06', '2022-11-25 17:02:06', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('2c5760754a6bcfb9c9bdd5fdaa875c7d', '删除任务', 'task.del', 'task', '484929d49e10f203a7fc536e9582f629', '微信用户', '2022-03-24 11:36:20', '2022-03-24 16:39:33', '微信用户');
INSERT INTO `sys_identifying` VALUES ('3225425a7a3b57284e2d381ca76456f4', '文件删除', 'library.file.del', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:03:18', '2022-11-25 17:03:18', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('3574a8cb917bec4f9ca11e7af590fd3f', '设置可见性', 'project.set.visibility', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:51:26', '2022-03-24 16:51:26', 'wl');
INSERT INTO `sys_identifying` VALUES ('39c412cd4fd48d4cf765ce9fb078e319', '批量下载', 'library.batch.download', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:03:39', '2022-11-25 17:03:39', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('4a9b126d2d70edeb3b4a9dee2222f7c6', '移动回收站/恢复任务', 'task.recycle.move.restore', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:45:52', '2022-03-24 16:48:26', 'wl');
INSERT INTO `sys_identifying` VALUES ('4c2a8f8e75b447ef730c27cc73c8c27e', '添加/移除参与者', 'task.add.remove.participants', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-30 15:48:33', '2022-03-30 15:49:07', 'wl');
INSERT INTO `sys_identifying` VALUES ('508ae7ad78d2e168ffe27ada8433bbea', '任务设置', 'project.task.settings', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-28 18:01:33', '2022-03-28 18:01:33', 'wl');
INSERT INTO `sys_identifying` VALUES ('53802617aba988c739a894d6357e6a94', '附件更名', 'library.annex.rename', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 18:05:42', '2022-11-25 18:05:42', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('573f93424f3f71995209b1d3c3b7d603', '设置任务优先级', 'set.task.priorities', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:44:10', '2022-03-24 16:44:10', 'wl');
INSERT INTO `sys_identifying` VALUES ('5d7cef49f01ba175d22461c195a1cac3', '设置任务执行人员', 'set.task.performers', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:43:09', '2022-03-24 16:43:09', 'wl');
INSERT INTO `sys_identifying` VALUES ('672beb05833569f2bb3a9fa0b9705c6d', '消息通知', 'project.message.notification', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-28 18:00:55', '2022-03-28 18:00:55', 'wl');
INSERT INTO `sys_identifying` VALUES ('68be1274bd14e528c5fb4add360700e5', '附件删除', 'library.annex.del', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 18:09:20', '2022-11-25 18:09:20', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('6c5f15c0ec6d5ebb33fcfc5710070c43', '文件编辑', 'library.file.edit', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:02:18', '2022-11-25 17:02:18', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('72da67b8708033b7b4c26bda94462540', '文件夹下载', 'library.folder.download', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:01:34', '2022-11-25 17:01:34', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('7780eea2c6cb8fbfd0b5311b32ae6d46', '文件下载', 'library.file.download', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:03:05', '2022-11-25 17:03:05', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('7daf3257a9b4d43aa2d4006d04aaee4c', '编辑任务列表', 'tasklist.edit', 'task_list', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:46:50', '2022-03-24 16:46:50', 'wl');
INSERT INTO `sys_identifying` VALUES ('84a7d8a701c82671c7866a5e6a9dbf07', '修改执行状态', 'modify.task.execution.status', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:42:28', '2022-03-24 16:42:28', 'wl');
INSERT INTO `sys_identifying` VALUES ('983202c309247acb813f1a72255ccce2', '批量删除', 'library.batch.del', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:08:23', '2022-11-25 17:08:23', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('9ac72349ddcea7608727f74d72bafdf0', '切换成员权限', 'project.toggle.permissions', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-06-07 11:03:36', '2022-06-07 11:03:36', 'wl');
INSERT INTO `sys_identifying` VALUES ('9b0c6ce7f11ead3aec988aad1850c0c5', '新增/管理自定义字段', 'project.add.manage.custom.fields', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:50:26', '2022-03-24 16:50:56', 'wl');
INSERT INTO `sys_identifying` VALUES ('9b665960e7cd82f01e3dcda1f3754ef7', '修改计划基础信息', 'project.base.edit', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:47:26', '2022-07-01 17:55:04', 'wl');
INSERT INTO `sys_identifying` VALUES ('9df4385a0f82b6dd228a0f25afddf745', '添加/移除参与者', 'project.add.remove.participants', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:52:06', '2022-03-28 17:56:15', 'wl');
INSERT INTO `sys_identifying` VALUES ('a0e888603206c217226e98b189919e5f', '复制计划', 'project.copy', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:49:25', '2022-07-01 17:55:11', 'wl');
INSERT INTO `sys_identifying` VALUES ('a343530f6db293e1667eba2b59679126', '设置任务时间', 'set.task.time', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:43:33', '2022-03-24 16:43:33', 'wl');
INSERT INTO `sys_identifying` VALUES ('a685e0cc87dcebf0f1abce640f41e196', '创建子任务', 'create.subtasks', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:45:00', '2022-03-24 16:45:00', 'wl');
INSERT INTO `sys_identifying` VALUES ('ab1b608ee3f3c8e3bf96c51344bb42b9', '设置任务标签', 'set.task.tag', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:44:22', '2022-03-24 16:44:22', 'wl');
INSERT INTO `sys_identifying` VALUES ('b8a17277321f255bff71890f19f4a643', '计划角色与权限', 'project.roles.permissions', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-28 17:55:36', '2022-07-01 17:55:18', 'wl');
INSERT INTO `sys_identifying` VALUES ('be9fffd7f3197d63d57fc134e409be2a', '创建文件夹', 'library.folder.create', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:00:23', '2022-11-25 17:00:23', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('d6da10db62549e2b687277bff20bc1a8', '删除任务列表', 'tasklist.del', 'task_list', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:47:00', '2022-03-24 16:47:00', 'wl');
INSERT INTO `sys_identifying` VALUES ('d8f1bc7502b9f717dbba3eb959c3ca2b', '附件下载', 'library.annex.download', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 18:09:04', '2022-11-25 18:09:04', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('dd3800e99f70eb72204b56de26095543', '设置任务备注', 'set.task.remark', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:43:48', '2022-03-24 16:43:48', 'wl');
INSERT INTO `sys_identifying` VALUES ('e1129cfd5864756d7b515edc4c820003', '移至回收站/恢复计划', 'project.recycle.move.restore', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:49:06', '2022-07-01 17:55:21', 'wl');
INSERT INTO `sys_identifying` VALUES ('ed183f250991aae2e7eee6f719ef23f2', '修改完成状态', 'modify.task.completion.status', 'task', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:41:50', '2022-03-24 16:41:50', 'wl');
INSERT INTO `sys_identifying` VALUES ('ed8f9e1f9e937ae337af8efb7b4bbe2a', '创建任务列表', 'tasklist.create', 'task_list', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:46:39', '2022-03-24 16:46:39', 'wl');
INSERT INTO `sys_identifying` VALUES ('f1e2d8718e6d7cccb8fa1585396ade76', '归档计划/取消归档计划', 'project.archive.Unarchive', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:48:08', '2022-07-01 17:55:27', 'wl');
INSERT INTO `sys_identifying` VALUES ('f2k2d3714e6d7abca2fe1585306age71', '资源投入', 'project.resource.input.settings', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:49:50', '2022-07-01 17:55:00', 'wl');
INSERT INTO `sys_identifying` VALUES ('f2k2d8714e6d7cccb8fe1585306age71', '同步参与人', 'project.synchronize.participants', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:49:50', '2022-07-01 17:55:00', 'wl');
INSERT INTO `sys_identifying` VALUES ('f30698f628b88b8a4cd77314baca705a', '文件夹更名', 'library.folder.rename', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:00:43', '2022-11-25 17:00:43', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('f36727b84a5e79cb1a81eb9f8b563e58', '任务创建', 'task.create', 'task', '484929d49e10f203a7fc536e9582f629', '微信用户', '2022-03-24 11:35:51', '2022-03-24 11:35:51', '微信用户');
INSERT INTO `sys_identifying` VALUES ('f73824b83c15bf300c5647688f0768af', '删除文件夹', 'library.folder.del', 'library', 'fa0c7f28b86a09f5ee4ad1e27ceb890e', 'wanglin', '2022-11-25 17:01:10', '2022-11-25 17:01:10', 'wanglin');
INSERT INTO `sys_identifying` VALUES ('j1f1d6724e4d7abca3fe1585306age72', '消息通知', 'project.message.notifications', 'project', '484929d49e10f203a7fc536e9582f629', 'wl', '2022-03-24 16:49:50', '2022-07-01 17:55:00', 'wl');

-- ----------------------------
-- Table structure for sys_identifying_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_identifying_role`;
CREATE TABLE `sys_identifying_role`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色id',
  `identifying` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源id',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资源与角色的关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_identifying_role
-- ----------------------------
INSERT INTO `sys_identifying_role` VALUES ('0849c8f6f6f9e0e3a3fb647cfab94f3b', 'cca554ac4e694ec5674c269ee84729e2', '573f93424f3f71995209b1d3c3b7d603', '1');
INSERT INTO `sys_identifying_role` VALUES ('092dd8caf271d2756dc5e84c90497b3d', 'cca554ac4e694ec5674c269ee84729e2', '9b665960e7cd82f01e3dcda1f3754ef7', '1');
INSERT INTO `sys_identifying_role` VALUES ('20a57eab978acb6cfa2bbffb9c861e79', 'be09a0a432caa9721a82596fb972ef37', '2c5760754a6bcfb9c9bdd5fdaa875c7d', '1');
INSERT INTO `sys_identifying_role` VALUES ('2b4bb3835b04e0ff44747b1d30595bed', '801340a3a2ce8ed7f73edd1f82e30f38', '508ae7ad78d2e168ffe27ada8433bbea', '1');
INSERT INTO `sys_identifying_role` VALUES ('2da23ded05dc3c2c0c3a272bce7e9f6e', 'cca554ac4e694ec5674c269ee84729e2', 'd6da10db62549e2b687277bff20bc1a8', '1');
INSERT INTO `sys_identifying_role` VALUES ('2f17b380e460060c269f5a4195f284a7', 'cca554ac4e694ec5674c269ee84729e2', 'ed8f9e1f9e937ae337af8efb7b4bbe2a', '1');
INSERT INTO `sys_identifying_role` VALUES ('3152f2d691aa413e72b7bd9496c951f4', '801340a3a2ce8ed7f73edd1f82e30f38', '9b665960e7cd82f01e3dcda1f3754ef7', '1');
INSERT INTO `sys_identifying_role` VALUES ('360e26445c6cf86a6e8b16dd61da35b2', 'cca554ac4e694ec5674c269ee84729e2', '4a9b126d2d70edeb3b4a9dee2222f7c6', '1');
INSERT INTO `sys_identifying_role` VALUES ('3b645396688b61b31875dd1f40b78aee', '801340a3a2ce8ed7f73edd1f82e30f38', '9df4385a0f82b6dd228a0f25afddf745', '1');
INSERT INTO `sys_identifying_role` VALUES ('464f8970b7c6f44a20d4fcaf460ae7bb', 'cca554ac4e694ec5674c269ee84729e2', '5d7cef49f01ba175d22461c195a1cac3', '1');
INSERT INTO `sys_identifying_role` VALUES ('48617e8455c578d8ff24b2cf4cdda213', '801340a3a2ce8ed7f73edd1f82e30f38', 'e1129cfd5864756d7b515edc4c820003', '1');
INSERT INTO `sys_identifying_role` VALUES ('4b1068d55332d93bb370e131e499a5a9', 'cca554ac4e694ec5674c269ee84729e2', '9b0c6ce7f11ead3aec988aad1850c0c5', '1');
INSERT INTO `sys_identifying_role` VALUES ('4c17cbb272081c9ef31deeea4f5bdab6', 'cca554ac4e694ec5674c269ee84729e2', 'j1f1d6724e4d7abca3fe1585306age72', '1');
INSERT INTO `sys_identifying_role` VALUES ('4d77c0b0eff4e55e583e5f87bda09bb8', 'cca554ac4e694ec5674c269ee84729e2', '2c5760754a6bcfb9c9bdd5fdaa875c7d', '1');
INSERT INTO `sys_identifying_role` VALUES ('4de1aaef1f56981e5ea33b15746ae629', 'cca554ac4e694ec5674c269ee84729e2', 'b8a17277321f255bff71890f19f4a643', '1');
INSERT INTO `sys_identifying_role` VALUES ('4e9096ca3386aeedd3fd136b6dc21009', 'cca554ac4e694ec5674c269ee84729e2', '508ae7ad78d2e168ffe27ada8433bbea', '1');
INSERT INTO `sys_identifying_role` VALUES ('513ff99320a5caa09ef1604005ece328', 'cca554ac4e694ec5674c269ee84729e2', 'f36727b84a5e79cb1a81eb9f8b563e58', '1');
INSERT INTO `sys_identifying_role` VALUES ('53e3098a111b60d4f5ac32b751caae12', 'cca554ac4e694ec5674c269ee84729e2', 'ed183f250991aae2e7eee6f719ef23f2', '1');
INSERT INTO `sys_identifying_role` VALUES ('563984e7439d9d4cd066e9a08e843ab5', 'cca554ac4e694ec5674c269ee84729e2', 'f1e2d8718e6d7cccb8fa1585396ade76', '1');
INSERT INTO `sys_identifying_role` VALUES ('5a98b7770eccdbe59ff321f5791ace2c', 'cca554ac4e694ec5674c269ee84729e2', '9ac72349ddcea7608727f74d72bafdf0', '1');
INSERT INTO `sys_identifying_role` VALUES ('5b8be61e569116f0da65dff24568d299', 'cca554ac4e694ec5674c269ee84729e2', '3574a8cb917bec4f9ca11e7af590fd3f', '1');
INSERT INTO `sys_identifying_role` VALUES ('6981623809aac9061221a57c7738028b', 'cca554ac4e694ec5674c269ee84729e2', '84a7d8a701c82671c7866a5e6a9dbf07', '1');
INSERT INTO `sys_identifying_role` VALUES ('6fbebac205623ab6a4d59161c09bb0e2', '801340a3a2ce8ed7f73edd1f82e30f38', '16e7e100438beb54abe4f4c5893ce7bd', '1');
INSERT INTO `sys_identifying_role` VALUES ('717a4b410445432e6cbf71b95d60c996', 'cca554ac4e694ec5674c269ee84729e2', '9df4385a0f82b6dd228a0f25afddf745', '1');
INSERT INTO `sys_identifying_role` VALUES ('75c08c74c45c4b833264014575f4c8dc', 'cca554ac4e694ec5674c269ee84729e2', 'a685e0cc87dcebf0f1abce640f41e196', '1');
INSERT INTO `sys_identifying_role` VALUES ('7648619a6485f7d1630ae3ffa6f30db6', 'cca554ac4e694ec5674c269ee84729e2', '7daf3257a9b4d43aa2d4006d04aaee4c', '1');
INSERT INTO `sys_identifying_role` VALUES ('7a8f2c6b4bed086c14739f7f3cc9ef63', '801340a3a2ce8ed7f73edd1f82e30f38', 'a0e888603206c217226e98b189919e5f', '1');
INSERT INTO `sys_identifying_role` VALUES ('7cda81552aad988465e16d3d0c889136', '801340a3a2ce8ed7f73edd1f82e30f38', '672beb05833569f2bb3a9fa0b9705c6d', '1');
INSERT INTO `sys_identifying_role` VALUES ('8b7c538d4badd148fd449b92f9854184', 'cca554ac4e694ec5674c269ee84729e2', '16e7e100438beb54abe4f4c5893ce7bd', '1');
INSERT INTO `sys_identifying_role` VALUES ('937c2852376b07a35d6e4f146a5ae5b4', 'cca554ac4e694ec5674c269ee84729e2', 'a343530f6db293e1667eba2b59679126', '1');
INSERT INTO `sys_identifying_role` VALUES ('98707363878cf6ecfb2d0e0a304036d4', '801340a3a2ce8ed7f73edd1f82e30f38', '9b0c6ce7f11ead3aec988aad1850c0c5', '1');
INSERT INTO `sys_identifying_role` VALUES ('9f921a0344768804033129bf9fd2679d', '801340a3a2ce8ed7f73edd1f82e30f38', '3574a8cb917bec4f9ca11e7af590fd3f', '1');
INSERT INTO `sys_identifying_role` VALUES ('ac4aa39d92651582e3248a56509fa5ba', 'cca554ac4e694ec5674c269ee84729e2', 'e1129cfd5864756d7b515edc4c820003', '1');
INSERT INTO `sys_identifying_role` VALUES ('b0cfccbd14f484c0b102a22e781508d1', 'cca554ac4e694ec5674c269ee84729e2', 'f2k2d3714e6d7abca2fe1585306age71', '1');
INSERT INTO `sys_identifying_role` VALUES ('b60a17a7d5e3e4d278cb89aaafbf892e', 'cca554ac4e694ec5674c269ee84729e2', 'ab1b608ee3f3c8e3bf96c51344bb42b9', '1');
INSERT INTO `sys_identifying_role` VALUES ('b660e6a5b151bb6c8a76fadb475c07b1', 'cca554ac4e694ec5674c269ee84729e2', 'dd3800e99f70eb72204b56de26095543', '1');
INSERT INTO `sys_identifying_role` VALUES ('cc55b774e12df95383b5825ab5ec0587', 'cca554ac4e694ec5674c269ee84729e2', 'f2k2d8714e6d7cccb8fe1585306age71', '1');
INSERT INTO `sys_identifying_role` VALUES ('d05f3bab88ee673bc3682784d24066dd', 'cca554ac4e694ec5674c269ee84729e2', '672beb05833569f2bb3a9fa0b9705c6d', '1');
INSERT INTO `sys_identifying_role` VALUES ('e068b4acf0e0ec43948b4163c4c88736', 'cca554ac4e694ec5674c269ee84729e2', '4c2a8f8e75b447ef730c27cc73c8c27e', '1');
INSERT INTO `sys_identifying_role` VALUES ('e1497596829c82a41a437661baca8352', '801340a3a2ce8ed7f73edd1f82e30f38', 'b8a17277321f255bff71890f19f4a643', '1');
INSERT INTO `sys_identifying_role` VALUES ('e94f251b3f0cb46db55d76fdfc15104b', 'cca554ac4e694ec5674c269ee84729e2', 'a0e888603206c217226e98b189919e5f', '1');
INSERT INTO `sys_identifying_role` VALUES ('ed8df3f2c595c8cf477d8ef72c16d2ca', 'cca554ac4e694ec5674c269ee84729e2', '2610bbacc86d894628d8f8dd5db4916d', '1');
INSERT INTO `sys_identifying_role` VALUES ('edabf990fd980c9e1f36975575fbb7c2', 'be09a0a432caa9721a82596fb972ef37', 'f36727b84a5e79cb1a81eb9f8b563e58', '1');
INSERT INTO `sys_identifying_role` VALUES ('f59507c3ce9576a7bd8629a61c82b4de', '801340a3a2ce8ed7f73edd1f82e30f38', 'f1e2d8718e6d7cccb8fa1585396ade76', '1');

-- ----------------------------
-- Table structure for sys_like
-- ----------------------------
DROP TABLE IF EXISTS `sys_like`;
CREATE TABLE `sys_like`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `relevance_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务或日程id',
  `type` enum('task','schedule','project') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_like
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `business_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名',
  `function_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '功能名',
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
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求IP地址',
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
  `client_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端名称',
  `user_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `start_time`(`start_time`) USING BTREE,
  INDEX `client_id`(`client_id`(191)) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '请求日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '角色描述',
  `role_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色类型system 系统 / project 项目',
  `can_deleted` tinyint(1) NULL DEFAULT 1 COMMENT '是否可删除',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by_id` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者id',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('801340a3a2ce8ed7f73edd1f82e30f38', '计划管理员', '管理计划', 'system', 0, '1', '484929d49e10f203a7fc536e9582f629', '微信用户', '2022-03-23 09:37:31', '2022-11-09 11:21:33', '微信用户');
INSERT INTO `sys_role` VALUES ('be09a0a432caa9721a82596fb972ef37', '计划成员', '参与计划', 'system', 0, '1', '484929d49e10f203a7fc536e9582f629', '微信用户', '2022-03-24 09:51:02', '2022-07-01 17:58:20', '微信用户');
INSERT INTO `sys_role` VALUES ('cca554ac4e694ec5674c269ee84729e2', '计划拥有者', '计划所属人', 'system', 0, '1', '484929d49e10f203a7fc536e9582f629', '微信用户', '2022-03-22 09:57:36', '2022-10-31 17:26:38', '微信用户');

-- ----------------------------
-- Table structure for sys_system_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_system_setting`;
CREATE TABLE `sys_system_setting`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `field_type` enum('priority_task','project_execute_task_status') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段类型priority 任务优先级 project_execute_task_status 任务执行状态',
  `field_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段值',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `default_is` tinyint(1) NULL DEFAULT 0 COMMENT '是否为默认值',
  `colour` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统字段值配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_system_setting
-- ----------------------------
INSERT INTO `sys_system_setting` VALUES ('0a55b6e0f87644464c804133f6d23d3e', 'priority_task', '重要紧急', '1', 5, '2022-03-24 14:11:16', '2022-06-07 18:49:31', 0, '#FB7894');
INSERT INTO `sys_system_setting` VALUES ('0ac9ae8cb5d0082f43f4b320fdae28e9', 'project_execute_task_status', '等待', '1', 7, '2022-03-25 19:42:23', '2022-06-15 16:37:46', 0, '#D287F8');
INSERT INTO `sys_system_setting` VALUES ('2511e05f358f034255a45ab0d7f0fe94', 'project_execute_task_status', '暂停', '1', 5, '2022-03-24 11:06:38', '2022-11-07 18:59:54', 0, '#FF9F73');
INSERT INTO `sys_system_setting` VALUES ('4369def6eeab1595dfb267e27797e93e', 'priority_task', '重要不紧急', '1', 5, '2022-03-24 14:11:23', '2022-06-15 16:39:17', 0, '#F6C659');
INSERT INTO `sys_system_setting` VALUES ('71c2ad888f8fdc33ebeb0ead29f56208', 'project_execute_task_status', '挂起', '1', 9, '2022-03-27 16:19:42', '2022-07-07 17:56:34', 0, '#F6C659');
INSERT INTO `sys_system_setting` VALUES ('c9054a1230f4f91603943cce1035924d', 'priority_task', '普通任务', '1', 6, '2022-03-25 18:49:09', '2022-06-15 16:39:29', 1, '#5DCFFF');
INSERT INTO `sys_system_setting` VALUES ('feca07c4a449bb70c4c90bc2ce5ee8ba', 'project_execute_task_status', '正常', '1', 6, '2022-03-25 19:42:23', '2022-11-07 18:59:46', 1, '#40E0C3');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id 部门id 岗位id 群组id',
  `role_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色id',
  `project_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目id',
  `data_auth_type` enum('person','dept','job','role','user_group') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'person' COMMENT '数据权限类型',
  `tenant_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
