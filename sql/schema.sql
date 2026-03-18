/*
 Navicat Premium Dump SQL

 Source Server         : con
 Source Server Type    : MySQL
 Source Server Version : 80025 (8.0.25)
 Source Host           : localhost:3306
 Source Schema         : contract_cms

 Target Server Type    : MySQL
 Target Server Version : 80025 (8.0.25)
 File Encoding         : 65001

 Date: 18/03/2026 21:14:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for contract
-- ----------------------------
DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `contract_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '合同编号',
                             `contract_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '合同名称',
                             `contract_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '其他合同',
                             `counterparty` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '未填写相对方',
                             `amount` decimal(15, 2) NOT NULL COMMENT '合同金额',
                             `sign_date` date NOT NULL COMMENT '签署日期',
                             `expire_date` date NULL DEFAULT NULL,
                             `status` enum('待签署','待审批','履行中','已归档','已完成','将过期') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待签署',
                             `attachment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `user_id` bigint NULL DEFAULT NULL COMMENT '创建人(关联sys_user)',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_contract_user_no`(`user_id` ASC, `contract_no` ASC) USING BTREE,
                             CONSTRAINT `fk_contract_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '合同业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract
-- ----------------------------
INSERT INTO `contract` VALUES (1, '234234355', '11111', '其他合同', '未填写相对方', 32788.00, '2026-03-17', NULL, '履行中', '34534536366', 1, '2026-03-17 21:11:08', '2026-03-18 21:07:54');
INSERT INTO `contract` VALUES (3, '001', '合同1', '其他合同', '未填写相对方', 100.00, '2026-03-18', '2026-03-20', '已完成', '这是一份合同', 1, '2026-03-18 19:43:24', '2026-03-18 21:11:20');
INSERT INTO `contract` VALUES (6, '001', '合同1', '其他合同', '未填写相对方', 2100.00, '2026-03-18', NULL, '履行中', '这是一份合同', 2, '2026-03-18 20:26:13', '2026-03-18 21:07:54');
INSERT INTO `contract` VALUES (7, '001', '合同1', '运维合同', '阿里云', 2000.00, '2026-03-18', '2026-03-20', '待签署', '阿里云合同', 3, '2026-03-18 21:13:25', '2026-03-18 21:13:25');

-- ----------------------------
-- Table structure for merchant
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `merchant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客商名称',
                             `merchant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客商编号',
                             `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
                             `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
                             `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
                             `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '启用' COMMENT '状态',
                             `user_id` bigint NULL DEFAULT NULL COMMENT '创建者用户ID',
                             `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_merchant_code`(`merchant_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客商信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of merchant
-- ----------------------------
INSERT INTO `merchant` VALUES (3, '客商1', 'MS-091730', '张三', '110', '河南', '禁用', 1, '2026-03-18 19:58:29', '2026-03-18 19:58:29');
INSERT INTO `merchant` VALUES (4, '客商2', 'MS-423596', '李四', '120', '北京', '启用', 1, '2026-03-18 20:03:57', '2026-03-18 20:03:57');
INSERT INTO `merchant` VALUES (5, '客商1', 'MS-876991', '单工', '111', '河南', '禁用', 2, '2026-03-18 20:28:13', '2026-03-18 20:28:13');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键名',
                               `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置键值',
                               `is_system` tinyint NULL DEFAULT 0 COMMENT '是否系统内置(1是 0否)',
                               `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                               `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'sys.name', 'CMS - Pro', 1, '系统运行名称', '2026-03-17 21:17:43', '2026-03-17 21:17:43');
INSERT INTO `sys_config` VALUES (2, 'sys.theme', '#ec4899', 1, '系统主色调', '2026-03-17 21:17:43', '2026-03-17 21:17:43');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
                             `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                             `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
                             `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
                             `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1有效 0禁用',
                             `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '管理员', NULL, 1, '2026-03-17 21:10:25', '2026-03-17 21:10:25');
INSERT INTO `sys_user` VALUES (2, 'user01', '123456', '用户1', NULL, 1, '2026-03-17 21:27:53', '2026-03-17 21:27:53');
INSERT INTO `sys_user` VALUES (3, 'user02', '123456', '用户2', NULL, 1, '2026-03-18 20:29:29', '2026-03-18 20:29:29');

SET FOREIGN_KEY_CHECKS = 1;
