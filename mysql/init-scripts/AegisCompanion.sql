/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80029
 Source Host           : localhost:3306
 Source Schema         : AegisCompanion

 Target Server Type    : MySQL
 Target Server Version : 80029
 File Encoding         : 65001

 Date: 21/03/2025 12:05:00
*/

CREATE DATABASE IF NOT EXISTS `AegisCompanion` 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_0900_ai_ci;

USE `AegisCompanion`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL COMMENT '居民ID',
  `doctor_id` bigint NOT NULL,
  `appoint_date` date NOT NULL COMMENT '预约日期',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '09:00-10:00',
  `status` enum('pending','confirmed','canceled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (1900352981898747905, 2004, 100, '2025-03-15', '09:00-09:30', 'canceled');
INSERT INTO `appointment` VALUES (1900353102426267649, 2004, 100, '2025-03-15', '09:00-09:30', 'canceled');
INSERT INTO `appointment` VALUES (1900363759959752705, 2004, 100, '2025-03-15', '09:00-09:30', 'canceled');

-- ----------------------------
-- Table structure for booking_blacklist
-- ----------------------------
DROP TABLE IF EXISTS `booking_blacklist`;
CREATE TABLE `booking_blacklist`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '黑名单ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `reason_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '拉黑原因类型',
  `expire_time` datetime NOT NULL COMMENT '解禁时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `booking_blacklist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '违规预约限制表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booking_blacklist
-- ----------------------------

-- ----------------------------
-- Table structure for community_room
-- ----------------------------
DROP TABLE IF EXISTS `community_room`;
CREATE TABLE `community_room`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '活动室ID',
  `room_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动室名称（如：多功能厅A）',
  `room_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型(MEETING/SPORTS/ENTERTAINMENT)',
  `max_capacity` int UNSIGNED NOT NULL COMMENT '最大容纳人数',
  `open_hour` time NOT NULL COMMENT '每日开放时间（08:00:00）',
  `close_hour` time NOT NULL COMMENT '每日关闭时间（22:00:00）',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用(1-启用 0-禁用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1894071224069627906 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区活动室基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_room
-- ----------------------------
INSERT INTO `community_room` VALUES (1, '第一会议室', 'MEETING', 30, '08:00:00', '22:00:00', 1, '2025-02-24 21:31:44');
INSERT INTO `community_room` VALUES (2, '羽毛球馆', 'SPORTS', 50, '09:00:00', '21:00:00', 1, '2025-02-24 21:31:44');
INSERT INTO `community_room` VALUES (3, '棋牌室1', 'ENTERTAINMENT', 20, '10:00:00', '20:00:00', 1, '2025-02-24 21:31:44');
INSERT INTO `community_room` VALUES (1894071224069627905, '羽毛球场', 'SPORTS', 10, '08:00:00', '22:00:00', 1, '2025-02-25 01:05:44');

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor`  (
  `id` bigint NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '医生姓名',
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '职称（全科医师/护士等）',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '简介',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES (100, '张医生', '全科医师', '擅长常见病诊疗');
INSERT INTO `doctor` VALUES (101, '李护士', '主管护师', '疫苗接种专家');

-- ----------------------------
-- Table structure for parking_log
-- ----------------------------
DROP TABLE IF EXISTS `parking_log`;
CREATE TABLE `parking_log`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `space_id` bigint UNSIGNED NOT NULL,
  `operator_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '操作人ID（NULL表示系统自动操作）',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型(RESERVE/OCCUPY/RELEASE)',
  `detail` json NOT NULL COMMENT '变更详情(JSON格式)',
  `log_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_space_log`(`space_id` ASC, `log_time` DESC) USING BTREE,
  CONSTRAINT `fk_parking_log_space` FOREIGN KEY (`space_id`) REFERENCES `parking_space` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 87 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '车位操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking_log
-- ----------------------------
INSERT INTO `parking_log` VALUES (82, 3, 2004, 'reserve', '{\"remark\": \"用户预约成功\", \"newStatus\": \"RESERVED\", \"oldStatus\": \"FREE\"}', '2025-03-17 14:38:19');
INSERT INTO `parking_log` VALUES (83, 3, 2004, 'release', '{\"remark\": \"用户取消预约\", \"newStatus\": \"FREE\", \"oldStatus\": \"RESERVED\"}', '2025-03-17 14:39:01');
INSERT INTO `parking_log` VALUES (84, 4, 2004, 'reserve', '{\"remark\": \"用户预约成功\", \"newStatus\": \"RESERVED\", \"oldStatus\": \"FREE\"}', '2025-03-18 09:36:59');
INSERT INTO `parking_log` VALUES (85, 4, 2004, 'release', '{\"remark\": \"用户取消预约\", \"newStatus\": \"FREE\", \"oldStatus\": \"RESERVED\"}', '2025-03-18 10:07:47');
INSERT INTO `parking_log` VALUES (86, 4, 1, 'admin_release', '{\"remark\": \"管理员强制释放\", \"newStatus\": \"FREE\", \"oldStatus\": \"FREE\"}', '2025-03-19 19:23:21');

-- ----------------------------
-- Table structure for parking_reservation
-- ----------------------------
DROP TABLE IF EXISTS `parking_reservation`;
CREATE TABLE `parking_reservation`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED NOT NULL COMMENT '居民ID',
  `space_id` bigint UNSIGNED NOT NULL COMMENT '车位ID',
  `reserve_time` datetime NOT NULL COMMENT '预约时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `actual_use_time` datetime NULL DEFAULT NULL COMMENT '实际使用时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/COMPLETED/EXPIRED)',
  `version` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本号(用于乐观锁)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_time`(`user_id` ASC, `reserve_time` DESC) USING BTREE,
  INDEX `fk_reservation_space`(`space_id` ASC) USING BTREE,
  CONSTRAINT `fk_reservation_space` FOREIGN KEY (`space_id`) REFERENCES `parking_space` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '车位预约记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking_reservation
-- ----------------------------
INSERT INTO `parking_reservation` VALUES (40, 2004, 3, '2025-03-17 14:38:19', '2025-03-17 15:08:19', NULL, 'canceled', 1);
INSERT INTO `parking_reservation` VALUES (41, 2004, 4, '2025-03-18 09:36:59', '2025-03-18 10:06:59', NULL, 'canceled', 1);

-- ----------------------------
-- Table structure for parking_space
-- ----------------------------
DROP TABLE IF EXISTS `parking_space`;
CREATE TABLE `parking_space`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '车位ID',
  `zone_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区域编码(A/B/C)',
  `number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '车位编号(如B2-023)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'FREE' COMMENT '状态(FREE/RESERVED/OCCUPIED)',
  `qr_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '绑定二维码URL',
  `last_status_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后状态变更时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `udx_zone_number`(`zone_code` ASC, `number` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物理车位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking_space
-- ----------------------------
INSERT INTO `parking_space` VALUES (2, 'A', 'A-002', 'FREE', NULL, '2025-03-19 18:21:20', '2025-02-23 02:55:38', 63);
INSERT INTO `parking_space` VALUES (3, 'B', 'B-001', 'FREE', NULL, '2025-03-17 14:39:01', '2025-02-23 02:55:38', 2);
INSERT INTO `parking_space` VALUES (4, 'C', 'C-001', 'FREE', NULL, '2025-03-19 19:23:21', '2025-02-23 02:55:38', 12);
INSERT INTO `parking_space` VALUES (5, 'D', 'D-001', 'FREE', NULL, '2025-03-17 14:03:17', '2025-02-23 03:24:28', 6);
INSERT INTO `parking_space` VALUES (6, 'D', 'D-002', 'FREE', NULL, '2025-03-07 01:27:53', '2025-02-24 23:01:34', 1);
INSERT INTO `parking_space` VALUES (7, 'A', 'A-004', 'FREE', '', '2025-03-19 19:25:19', '2025-03-19 16:58:18', 3);
INSERT INTO `parking_space` VALUES (9, 'C', 'C-002', 'FREE', '', '2025-03-19 19:24:05', '2025-03-19 19:24:05', 0);
INSERT INTO `parking_space` VALUES (10, 'B', 'B-002', 'FREE', '', '2025-03-19 19:24:58', '2025-03-19 19:24:58', 0);

-- ----------------------------
-- Table structure for room_booking
-- ----------------------------
DROP TABLE IF EXISTS `room_booking`;
CREATE TABLE `room_booking`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '申请人ID',
  `room_id` bigint UNSIGNED NOT NULL COMMENT '活动室ID',
  `purpose` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用途描述',
  `participant_count` int UNSIGNED NOT NULL COMMENT '参与人数',
  `start_time` datetime NOT NULL COMMENT '使用开始时间',
  `end_time` datetime NOT NULL COMMENT '使用结束时间',
  `booking_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '预约状态',
  `admin_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批意见',
  `version` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_time`(`room_id` ASC, `start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `room_booking_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `room_booking_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `community_room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1902334012960059394 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动室预约记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_booking
-- ----------------------------
INSERT INTO `room_booking` VALUES (1894074406241902594, 2004, 2, '团队会议', 15, '2025-03-20 14:00:00', '2025-03-20 16:00:00', 'CANCELED', NULL, 0, '2025-02-25 02:15:38');
INSERT INTO `room_booking` VALUES (1898170857496633345, 2004, 1, 'fd', 1, '2025-03-15 12:12:00', '2025-03-22 05:15:00', 'CANCELED', NULL, 0, '2025-03-08 08:36:13');
INSERT INTO `room_booking` VALUES (1898172843705098241, 2004, 1, 'fdf', 1, '2025-03-09 11:02:00', '2025-03-09 11:07:00', 'CANCELED', NULL, 0, '2025-03-08 08:44:07');
INSERT INTO `room_booking` VALUES (1901517649324601346, 2004, 1, '开会', 6, '2023-04-14 13:00:00', '2023-04-14 15:00:00', 'CANCELED', NULL, 0, '2025-03-17 14:15:11');
INSERT INTO `room_booking` VALUES (1902334012960059393, 2004, 1, '开会', 7, '2025-04-01 10:03:00', '2025-04-01 15:00:00', 'APPROVED', NULL, 0, '2025-03-19 20:19:07');

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule`  (
  `id` bigint NOT NULL,
  `doctor_id` bigint NOT NULL,
  `work_date` date NOT NULL COMMENT '排班日期',
  `time_slots` json NOT NULL COMMENT '{\"09:00-10:00\": \"available\", \"10:30-11:30\": \"booked\"}',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_doctor_date`(`doctor_id` ASC, `work_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedule
-- ----------------------------
INSERT INTO `schedule` VALUES (1, 100, '2026-03-15', '{\"09:00-09:30\": \"available\", \"10:00-10:30\": \"available\"}');
INSERT INTO `schedule` VALUES (2, 101, '2026-03-01', '{\"14:00-14:30\": \"available\"}');

-- ----------------------------
-- Table structure for state_machine_record
-- ----------------------------
DROP TABLE IF EXISTS `state_machine_record`;
CREATE TABLE `state_machine_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `machine_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态机ID（工单ID）',
  `state` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '当前状态',
  `context_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上下文JSON',
  `version` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `udx_machine_id`(`machine_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '状态机运行时记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of state_machine_record
-- ----------------------------
INSERT INTO `state_machine_record` VALUES (26, '29', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-04 11:15:49', '2025-03-04 11:15:49');
INSERT INTO `state_machine_record` VALUES (27, '30', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-04 22:44:17', '2025-03-04 22:44:17');
INSERT INTO `state_machine_record` VALUES (28, '31', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-04 22:52:03', '2025-03-04 22:52:03');
INSERT INTO `state_machine_record` VALUES (29, '32', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-04 22:52:34', '2025-03-04 22:52:34');
INSERT INTO `state_machine_record` VALUES (30, '33', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-04 23:55:24', '2025-03-04 23:55:24');
INSERT INTO `state_machine_record` VALUES (31, '34', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-05 00:00:37', '2025-03-05 00:00:37');
INSERT INTO `state_machine_record` VALUES (32, '35', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-05 00:03:01', '2025-03-05 00:03:01');
INSERT INTO `state_machine_record` VALUES (33, '36', 'REVIEW_PENDING', '{\"operatorId\":1002}', 3, '2025-03-05 00:56:03', '2025-03-05 01:23:21');
INSERT INTO `state_machine_record` VALUES (34, '37', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-08 09:43:46', '2025-03-08 09:43:46');
INSERT INTO `state_machine_record` VALUES (35, '38', 'REVIEW_PENDING', '{\"operatorId\":1002}', 3, '2025-03-08 10:05:31', '2025-03-09 02:43:57');
INSERT INTO `state_machine_record` VALUES (36, '39', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-09 00:52:49', '2025-03-09 00:52:49');
INSERT INTO `state_machine_record` VALUES (37, '40', 'AUTO_ASSIGNED', '{\"operatorId\":2004}', 1, '2025-03-09 02:41:18', '2025-03-09 02:41:18');
INSERT INTO `state_machine_record` VALUES (38, '41', 'PROCESSING', '{\"operatorId\":1002}', 2, '2025-03-09 02:44:38', '2025-03-09 02:45:31');

-- ----------------------------
-- Table structure for ticket
-- ----------------------------
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工单标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题描述',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工单类型（与ticket_type_skill关联）',
  `state` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '当前状态（TicketState枚举值）',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '提交用户ID',
  `assignee_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '分配处理人ID',
  `reviewer_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '审核人ID',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '用户确认时间',
  `close_time` datetime NULL DEFAULT NULL COMMENT '关闭时间',
  `version` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type_state`(`type` ASC, `state` ASC) USING BTREE,
  INDEX `fk_ticket_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_ticket_assignee`(`assignee_id` ASC) USING BTREE,
  CONSTRAINT `fk_ticket_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket
-- ----------------------------
INSERT INTO `ticket` VALUES (29, '楼道灯不亮', '路灯不亮2', '电路维修', 'AUTO_ASSIGNED', 2004, NULL, NULL, NULL, NULL, 1, '2025-03-04 11:15:49', '2025-03-04 11:15:49');
INSERT INTO `ticket` VALUES (30, '楼道灯不亮', '路灯不亮2', '电路维修', 'AUTO_ASSIGNED', 2004, NULL, NULL, NULL, NULL, 1, '2025-03-04 22:44:17', '2025-03-04 22:44:17');
INSERT INTO `ticket` VALUES (31, '楼道灯不亮', '路灯不亮2', '电路维修', 'AUTO_ASSIGNED', 2004, NULL, NULL, NULL, NULL, 1, '2025-03-04 22:52:03', '2025-03-04 22:52:03');
INSERT INTO `ticket` VALUES (32, '楼道灯不亮', '路灯不亮2', '电路维修', 'AUTO_ASSIGNED', 2004, 1001, NULL, NULL, NULL, 1, '2025-03-04 22:52:34', '2025-03-04 22:52:34');
INSERT INTO `ticket` VALUES (33, '楼道灯不亮', '路灯不亮2', '电路维修', 'AUTO_ASSIGNED', 2004, 1003, NULL, NULL, NULL, 1, '2025-03-04 23:55:24', '2025-03-04 23:55:24');
INSERT INTO `ticket` VALUES (34, '楼道灯不亮', '路灯不亮2', '电路维修', 'AUTO_ASSIGNED', 2004, 1001, NULL, NULL, NULL, 1, '2025-03-05 00:00:36', '2025-03-05 00:00:37');
INSERT INTO `ticket` VALUES (35, '楼道灯不亮', '路灯不亮2', '电路维修', 'AUTO_ASSIGNED', 2004, 1003, NULL, NULL, NULL, 1, '2025-03-05 00:03:01', '2025-03-05 00:03:01');
INSERT INTO `ticket` VALUES (36, '卫生间漏水', '马桶周边持续渗水', '水暖维修', 'REVIEW_FAILED', 2004, 1002, NULL, NULL, NULL, 4, '2025-03-05 00:56:03', '2025-03-20 12:16:36');
INSERT INTO `ticket` VALUES (37, '厨房漏电', '楼ddff', '水电维修', 'CREATED', 2004, NULL, NULL, NULL, NULL, 0, '2025-03-08 09:43:45', '2025-03-08 09:43:45');
INSERT INTO `ticket` VALUES (38, '测试', '很好', '水暖维修', 'REVIEW_FAILED', 2004, 1002, NULL, NULL, NULL, 4, '2025-03-08 10:05:31', '2025-03-20 12:24:27');
INSERT INTO `ticket` VALUES (39, 'tere', 'rererer', '电器维修', 'AUTO_ASSIGNED', 2004, 1001, NULL, NULL, NULL, 1, '2025-03-09 00:52:48', '2025-03-09 00:52:49');
INSERT INTO `ticket` VALUES (40, 'df', 'fdf', '电器维修', 'AUTO_ASSIGNED', 2004, 1001, NULL, NULL, NULL, 1, '2025-03-09 02:41:18', '2025-03-09 02:41:18');
INSERT INTO `ticket` VALUES (41, 'fdf', 'fdfd', '水暖维修', 'PROCESSING', 2004, 1002, NULL, NULL, NULL, 2, '2025-03-09 02:44:38', '2025-03-09 02:45:30');

-- ----------------------------
-- Table structure for ticket_log
-- ----------------------------
DROP TABLE IF EXISTS `ticket_log`;
CREATE TABLE `ticket_log`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `ticket_id` bigint UNSIGNED NOT NULL COMMENT '关联工单ID',
  `from_state` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原状态',
  `to_state` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '新状态',
  `operator_id` bigint UNSIGNED NOT NULL COMMENT '操作人ID',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ticket`(`ticket_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工单状态变更日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket_log
-- ----------------------------
INSERT INTO `ticket_log` VALUES (21, 29, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-04 11:15:49');
INSERT INTO `ticket_log` VALUES (22, 30, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-04 22:44:17');
INSERT INTO `ticket_log` VALUES (23, 31, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-04 22:52:03');
INSERT INTO `ticket_log` VALUES (24, 32, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-04 22:52:34');
INSERT INTO `ticket_log` VALUES (25, 33, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-04 23:55:24');
INSERT INTO `ticket_log` VALUES (26, 34, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-05 00:00:37');
INSERT INTO `ticket_log` VALUES (27, 35, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-05 00:03:01');
INSERT INTO `ticket_log` VALUES (28, 36, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-05 00:56:03');
INSERT INTO `ticket_log` VALUES (29, 36, 'AUTO_ASSIGNED', 'PROCESSING', 1002, '状态变更：已自动分配→处理中', '2025-03-05 01:17:30');
INSERT INTO `ticket_log` VALUES (30, 36, 'PROCESSING', 'REVIEW_PENDING', 1002, '状态变更：处理中→待审核', '2025-03-05 01:23:21');
INSERT INTO `ticket_log` VALUES (31, 37, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-08 09:43:46');
INSERT INTO `ticket_log` VALUES (32, 38, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-08 10:05:31');
INSERT INTO `ticket_log` VALUES (33, 39, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-09 00:52:49');
INSERT INTO `ticket_log` VALUES (34, 38, 'AUTO_ASSIGNED', 'PROCESSING', 1002, '状态变更：已自动分配→处理中', '2025-03-09 02:39:09');
INSERT INTO `ticket_log` VALUES (35, 40, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-09 02:41:18');
INSERT INTO `ticket_log` VALUES (36, 38, 'PROCESSING', 'REVIEW_PENDING', 1002, '状态变更：处理中→待审核', '2025-03-09 02:43:57');
INSERT INTO `ticket_log` VALUES (37, 41, 'CREATED', 'AUTO_ASSIGNED', 2004, '状态变更：已创建→已自动分配', '2025-03-09 02:44:38');
INSERT INTO `ticket_log` VALUES (38, 41, 'AUTO_ASSIGNED', 'PROCESSING', 1002, '状态变更：已自动分配→处理中', '2025-03-09 02:45:30');
INSERT INTO `ticket_log` VALUES (39, 38, 'REVIEW_PENDING', 'REVIEW_FAILED', 1, '不同', '2025-03-20 12:24:27');

-- ----------------------------
-- Table structure for ticket_type_skill
-- ----------------------------
DROP TABLE IF EXISTS `ticket_type_skill`;
CREATE TABLE `ticket_type_skill`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工单类型（唯一）',
  `required_skill` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所需技能标签',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `udx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工单类型与技能映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket_type_skill
-- ----------------------------
INSERT INTO `ticket_type_skill` VALUES (1, '电路维修', '电路');
INSERT INTO `ticket_type_skill` VALUES (2, '管道维修', '管道');
INSERT INTO `ticket_type_skill` VALUES (3, '门窗维修', '门窗');
INSERT INTO `ticket_type_skill` VALUES (4, '电器维修', '电器');
INSERT INTO `ticket_type_skill` VALUES (5, '水暖维修', '水暖');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `password_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'resident' COMMENT '角色(admin/maintenance/resident)',
  `skills` json NULL COMMENT '技能标签',
  `avatar_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用(1-启用 0-禁用)',
  `version` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '数据版本号',
  `current_load` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '当前待处理工单数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `udx_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `udx_email`(`email` ASC) USING BTREE,
  INDEX `idx_role_load`(`role` ASC, `current_load` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1897491967325921283 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'system_user', '13999999333', '$2a$10$jmEC0P9gVklbioBRdSilJ.1nKBNxCZQP8ZgVeayE1KS9iRt7I68p.', '', 'SUPER_ADMIN', '[]', NULL, '2025-02-20 14:44:33', '2025-03-20 12:34:09', 1, 1, 0);
INSERT INTO `user` VALUES (1001, 'electric_li', '13811112222', '$2a$10$yJj2aQlz.3V4hBq5rT7wOeXmZvA1bC2dE3fG4hI5jK6lM7nN8oP', NULL, 'MAINTENANCE', '[\"电路\", \"电器\"]', NULL, '2025-02-20 14:32:46', '2025-03-09 02:41:18', 1, 4, 4);
INSERT INTO `user` VALUES (1002, 'pipe_wang', '13822223333', '$2a$10$pSG7IzgRfxamiiWYtY0bBu/8j/5FtgXWbduCz7w/ugX3bRKDGs.Cy', NULL, 'MAINTENANCE', '[\"管道\", \"水暖\"]', NULL, '2025-02-20 14:32:46', '2025-03-09 02:44:38', 1, 4, 3);
INSERT INTO `user` VALUES (1003, 'multi_skill', '13833334444', '$2a$10$yJj2aQlz.3V4hBq5rT7wOeXmZvA1bC2dE3fG4hI5jK6lM7nN8oP', NULL, 'MAINTENANCE', '[\"电路\", \"门窗\"]', NULL, '2025-02-20 14:32:46', '2025-03-05 00:03:01', 1, 4, 2);
INSERT INTO `user` VALUES (1004, 'WorkUser', '13999999977', '$2a$10$AowXAyye16X24Zoo5dyZc.5PBGg7drdaqVt73n8OUU/y1EP7/.1Qm', NULL, 'MAINTENANCE', NULL, NULL, '2025-02-20 14:46:35', '2025-02-20 14:47:47', 1, 0, 0);
INSERT INTO `user` VALUES (2001, 'resident_zhang', '13900001111', '$2a$10$yJj2aQlz.3V4hBq5rT7wOeXmZvA1bC2dE3fG4hI5jK6lM7nN8oP', NULL, 'USER', NULL, NULL, '2025-02-20 14:32:46', '2025-02-20 14:32:46', 1, 0, 0);
INSERT INTO `user` VALUES (2002, 'resident_li', '13900002222', '$2a$10$yJj2aQlz.3V4hBq5rT7wOeXmZvA1bC2dE3fG4hI5jK6lM7nN8oP', NULL, 'USER', NULL, NULL, '2025-02-20 14:32:46', '2025-02-20 14:32:46', 1, 0, 0);
INSERT INTO `user` VALUES (2003, 'resident_wang', '13900003333', '$2a$10$KrCOXfZWXPPrcXMHdIvqs.Z8PzRhQ//5BEVLunRn3PCUVwQ0hN4gG', NULL, 'MAINTENANCE', '[]', NULL, '2025-02-20 14:32:46', '2025-03-19 14:38:50', 0, 10, 0);
INSERT INTO `user` VALUES (2004, 'TestUser', '13999999988', '$2a$10$8UBDECUWLdREyZyP3OiUTeLXosroZOpOpdKUAmGYs9kdhtYWzNiNm', NULL, 'USER', NULL, NULL, '2025-02-20 14:46:20', '2025-02-20 14:47:19', 1, 0, 0);
INSERT INTO `user` VALUES (3001, 'admin_zhao', '13600000001', '$2a$10$yJj2aQlz.3V4hBq5rT7wOeXmZvA1bC2dE3fG4hI5jK6lM7nN8oP', NULL, 'ADMIN', NULL, NULL, '2025-02-20 14:32:46', '2025-02-20 14:32:46', 1, 0, 0);
INSERT INTO `user` VALUES (1897491967325921282, 'TestUser02', '18378454878', '$2a$10$09iDsX1CGBucGsgpSxBD6ueGgrD/T7Rc2sWijqt2uc2G3e6zrYocG', NULL, 'USER', '[]', NULL, '2025-03-06 11:38:33', '2025-03-06 11:38:33', 1, 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
