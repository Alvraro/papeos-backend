/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;

CREATE DATABASE IF NOT EXISTS `papeos` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `papeos`;

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
	`id` INT(11) NOT NULL,
	`nombre` VARCHAR(50) NOT NULL,
	`password` VARCHAR(50) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `nombre` (`nombre`) USING BTREE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `comidas`;
CREATE TABLE IF NOT EXISTS `comidas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `tipo_comida` enum('comida','cena') NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `fecha_tipo_comida` (`fecha`,`tipo_comida`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `platos`;
CREATE TABLE IF NOT EXISTS `platos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(50) DEFAULT '',
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_plato` (`nombre`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `comidas_platos`;
CREATE TABLE IF NOT EXISTS `comidas_platos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comida_id` int(11) NOT NULL,
  `plato_id` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `comida_plato` (`comida_id`,`plato_id`),
  KEY `FK_comidas_platos_platos` (`plato_id`),
  CONSTRAINT `FK_comidas_platos_comidas` FOREIGN KEY (`comida_id`) REFERENCES `comidas` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `FK_comidas_platos_platos` FOREIGN KEY (`plato_id`) REFERENCES `platos` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `alimentos`;
CREATE TABLE IF NOT EXISTS `alimentos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `categoria_id` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_alimento` (`nombre`),
  KEY `FK_alimentos_categorias_alimentos` (`categoria_id`),
  CONSTRAINT `FK_alimentos_categorias_alimentos` FOREIGN KEY (`categoria_id`) REFERENCES `categorias_alimentos` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `categorias_alimentos`;
CREATE TABLE IF NOT EXISTS `categorias_alimentos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre_categoria` (`nombre`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `alimentos_platos`;
CREATE TABLE IF NOT EXISTS `alimentos_platos` (
  `alimento_id` int(11) NOT NULL,
  `plato_id` int(11) NOT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`alimento_id`,`plato_id`),
  KEY `FK_alimentos_platos_alimentos` (`alimento_id`),
  KEY `FK_alimentos_platos_platos` (`plato_id`),
  CONSTRAINT `FK_alimentos_platos_alimentos` FOREIGN KEY (`alimento_id`) REFERENCES `alimentos` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `FK_alimentos_platos_platos` FOREIGN KEY (`plato_id`) REFERENCES `platos` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
