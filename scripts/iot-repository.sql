SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_sensor
-- ----------------------------
DROP TABLE IF EXISTS `tb_sensor`;
CREATE TABLE `tb_sensor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sensor_source_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8iacr1nconflem1qa5fy75dlu` (`sensor_source_id`),
  CONSTRAINT `FK8iacr1nconflem1qa5fy75dlu` FOREIGN KEY (`sensor_source_id`) REFERENCES `tb_sensor_source` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_sensor_has_sensor_measure_type
-- ----------------------------
DROP TABLE IF EXISTS `tb_sensor_has_sensor_measure_type`;
CREATE TABLE `tb_sensor_has_sensor_measure_type` (
  `sensor_id` bigint(20) NOT NULL,
  `sensor_measure_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`sensor_id`,`sensor_measure_type_id`),
  KEY `FKo4b836kfoir93b40t56rs62q6` (`sensor_measure_type_id`),
  CONSTRAINT `FK7kh7vycrh6wo1xopweg4tbu0f` FOREIGN KEY (`sensor_id`) REFERENCES `tb_sensor` (`id`),
  CONSTRAINT `FKo4b836kfoir93b40t56rs62q6` FOREIGN KEY (`sensor_measure_type_id`) REFERENCES `tb_sensor_measure_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_sensor_measure
-- ----------------------------
DROP TABLE IF EXISTS `tb_sensor_measure`;
CREATE TABLE `tb_sensor_measure` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `value` varchar(255) DEFAULT NULL,
  `sensor_id` bigint(20) NOT NULL,
  `sensor_measure_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtbhtqf9ftmiueppjy6evacfm2` (`sensor_id`),
  KEY `FKtabdn3sejqud2dm2oym8olqiu` (`sensor_measure_type_id`),
  CONSTRAINT `FKtabdn3sejqud2dm2oym8olqiu` FOREIGN KEY (`sensor_measure_type_id`) REFERENCES `tb_sensor_measure_type` (`id`),
  CONSTRAINT `FKtbhtqf9ftmiueppjy6evacfm2` FOREIGN KEY (`sensor_id`) REFERENCES `tb_sensor` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_sensor_measure_type
-- ----------------------------
DROP TABLE IF EXISTS `tb_sensor_measure_type`;
CREATE TABLE `tb_sensor_measure_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_sensor_source
-- ----------------------------
DROP TABLE IF EXISTS `tb_sensor_source`;
CREATE TABLE `tb_sensor_source` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
