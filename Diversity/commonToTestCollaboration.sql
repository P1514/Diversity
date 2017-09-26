CREATE DATABASE  IF NOT EXISTS `diversity_common_repository` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `diversity_common_repository`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: diversity_common_repository
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `type` enum('SUPPLIER','CUSTOMER','PRODUCT VENDOR','SERVICE PROVIDER') DEFAULT NULL,
  `belongs_to_company_id` int(11) DEFAULT NULL,
  `industry` varchar(256) DEFAULT NULL,
  `business` varchar(45) DEFAULT NULL,
  `turnover` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_company_id` (`id`),
  KEY `fk_company_company_idx` (`belongs_to_company_id`),
  CONSTRAINT `fk_company_company` FOREIGN KEY (`belongs_to_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'DESMA','PRODUCT VENDOR',NULL,NULL,NULL,NULL),(2,'CAREL','CUSTOMER',NULL,NULL,NULL,NULL),(3,'BAZIGOS','SUPPLIER',NULL,NULL,NULL,NULL),(4,'BAZIGOS Leisure Team','SUPPLIER',3,NULL,NULL,NULL),(6,'Test Setup Company','SERVICE PROVIDER',NULL,NULL,NULL,NULL),(7,'Test Department of Test Company','PRODUCT VENDOR',6,NULL,NULL,NULL),(8,'Test Sub-Department of Test Depa....','CUSTOMER',7,NULL,NULL,NULL),(9,'Desma\'s ping pong team','SUPPLIER',1,NULL,NULL,NULL);
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_is_costumer_of_design_project`
--

DROP TABLE IF EXISTS `company_is_costumer_of_design_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_is_costumer_of_design_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) DEFAULT NULL,
  `design_project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_company_is_costumer_of_design_project_1_idx` (`company_id`),
  KEY `fk_company_is_costumer_of_design_project_2_idx` (`design_project_id`),
  CONSTRAINT `fk_company_is_costumer_of_design_project_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_company_is_costumer_of_design_project_2` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_is_costumer_of_design_project`
--

LOCK TABLES `company_is_costumer_of_design_project` WRITE;
/*!40000 ALTER TABLE `company_is_costumer_of_design_project` DISABLE KEYS */;
INSERT INTO `company_is_costumer_of_design_project` VALUES (1,1,52);
/*!40000 ALTER TABLE `company_is_costumer_of_design_project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_owns_pss`
--

DROP TABLE IF EXISTS `company_owns_pss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_owns_pss` (
  `company_id` int(11) NOT NULL,
  `pss_id` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`company_id`,`pss_id`),
  KEY `ix_company_owns_pss_pss` (`pss_id`),
  CONSTRAINT `fk_company_owns_pss_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_company_owns_pss_pss` FOREIGN KEY (`pss_id`) REFERENCES `pss` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_owns_pss`
--

LOCK TABLES `company_owns_pss` WRITE;
/*!40000 ALTER TABLE `company_owns_pss` DISABLE KEYS */;
/*!40000 ALTER TABLE `company_owns_pss` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consist_of_product`
--

DROP TABLE IF EXISTS `consist_of_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consist_of_product` (
  `product_id` int(11) NOT NULL,
  `consists_of_product_id` int(11) NOT NULL,
  PRIMARY KEY (`product_id`,`consists_of_product_id`),
  KEY `ix_part_of_product_id` (`product_id`),
  KEY `fk_consists_of_product_1` (`consists_of_product_id`),
  CONSTRAINT `fk_consists_of_product_1` FOREIGN KEY (`consists_of_product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_consists_of_product_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consist_of_product`
--

LOCK TABLES `consist_of_product` WRITE;
/*!40000 ALTER TABLE `consist_of_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `consist_of_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consists_of_service`
--

DROP TABLE IF EXISTS `consists_of_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consists_of_service` (
  `service_id` int(11) NOT NULL,
  `consists_of_service_id` int(11) NOT NULL,
  PRIMARY KEY (`service_id`,`consists_of_service_id`),
  KEY `ix_consists_of_service_2` (`consists_of_service_id`),
  CONSTRAINT `fk_consists_of_service_1` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_consists_of_service_2` FOREIGN KEY (`consists_of_service_id`) REFERENCES `service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consists_of_service`
--

LOCK TABLES `consists_of_service` WRITE;
/*!40000 ALTER TABLE `consists_of_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `consists_of_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consumer`
--

DROP TABLE IF EXISTS `consumer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_consumer_id` (`id`),
  KEY `ix_consumer_user` (`user_id`),
  CONSTRAINT `fk_consumer_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumer`
--

LOCK TABLES `consumer` WRITE;
/*!40000 ALTER TABLE `consumer` DISABLE KEYS */;
INSERT INTO `consumer` VALUES (1,'Consumer 1',1),(8,'Consumer 12',1),(9,'Consumer 343',1);
/*!40000 ALTER TABLE `consumer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `content_rule`
--

DROP TABLE IF EXISTS `content_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `applied_to` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_content_rule_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_rule`
--

LOCK TABLES `content_rule` WRITE;
/*!40000 ALTER TABLE `content_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `content_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `design_project`
--

DROP TABLE IF EXISTS `design_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `produces_pss_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `time_created` varchar(250) DEFAULT NULL,
  `wiki_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `produces_pss_id` (`produces_pss_id`),
  KEY `user_id` (`user_id`),
  KEY `fk_design_project_wiki` (`wiki_id`),
  CONSTRAINT `design_project_ibfk_1` FOREIGN KEY (`produces_pss_id`) REFERENCES `pss` (`id`),
  CONSTRAINT `design_project_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_design_project_wiki` FOREIGN KEY (`wiki_id`) REFERENCES `wiki` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project`
--

LOCK TABLES `design_project` WRITE;
/*!40000 ALTER TABLE `design_project` DISABLE KEYS */;
INSERT INTO `design_project` VALUES (2,'Airflow Supersneaker Design project',31,2,NULL,5),(3,'Supersneaker Low-Tops Design project',NULL,2,NULL,NULL),(11,'Test 1 UNINOVA',NULL,1,NULL,NULL),(18,'Matteo - Carel',NULL,3,NULL,NULL),(19,'Francesca Carel',NULL,3,NULL,NULL),(20,'Airflow Supersneaker Design project 1',35,2,NULL,2),(33,'BAZIGOS test scenario',NULL,4,'1487078345352',NULL),(34,'Maintenance using history tracking',NULL,4,'1487079787946',NULL),(35,'BAZIGOS test ',NULL,4,'1487090111704',NULL),(36,'test with Carel humiFog',NULL,3,'1487154188064',NULL),(37,'test_tree_bug',NULL,1,'1487237833800',NULL),(38,'BAZIGOS PSS',27,4,'1487335116218',3),(39,'Fabiana',NULL,3,'1487344369142',NULL),(40,'CARLOBERTELE prova 20-02-2017',NULL,3,'1487527548897',NULL),(41,'CAREL_Installation_Improvement',42,3,'1487576559903',4),(42,'Test wiki project',35,1,'1487581111801',1),(43,'DESMA PSS',NULL,2,'1487685843918',NULL),(46,'test_ct_disabled',NULL,1,'1495615951660',1),(47,'test_ct_disabled_2',NULL,1,'1495616239748',2),(48,'test_ct_disabled_3',NULL,1,'1495616504253',NULL),(49,'Test_24-05_Morning',NULL,3,'1495617808458',NULL),(50,'',NULL,1,'1496235569766',2),(51,'AAA',NULL,1,'1499416566471',NULL),(52,NULL,67,1,NULL,NULL),(53,NULL,68,1,NULL,NULL),(54,NULL,69,1,NULL,NULL),(55,NULL,70,1,NULL,NULL),(56,NULL,71,1,NULL,NULL),(57,NULL,72,1,NULL,NULL),(58,NULL,73,1,NULL,NULL),(59,NULL,74,1,NULL,NULL),(60,NULL,75,1,NULL,NULL),(61,NULL,76,1,NULL,NULL),(62,NULL,77,1,NULL,NULL);
/*!40000 ALTER TABLE `design_project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `design_project_has_steps`
--

DROP TABLE IF EXISTS `design_project_has_steps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project_has_steps` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time_initiated` varchar(250) DEFAULT NULL,
  `design_project_id` int(11) DEFAULT NULL,
  `step_id` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_design_project_has_steps_1_idx` (`design_project_id`),
  KEY `fk_design_project_has_steps_2_idx` (`step_id`),
  CONSTRAINT `fk_design_project_has_steps_1` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_design_project_has_steps_2` FOREIGN KEY (`step_id`) REFERENCES `step` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_has_steps`
--

LOCK TABLES `design_project_has_steps` WRITE;
/*!40000 ALTER TABLE `design_project_has_steps` DISABLE KEYS */;
INSERT INTO `design_project_has_steps` VALUES (85,'1487003318629',2,1,2),(86,'1487003376374',3,1,0),(87,'1487006052051',2,2,2),(88,'1487006552512',2,3,2),(89,'1487006752429',2,4,0),(90,'1487006836622',2,5,2),(91,'1487009764284',19,1,2),(92,'1487009767307',19,2,2),(93,'1487009850399',3,2,2),(94,'1487009907410',3,5,2),(95,'1487016069566',11,1,0),(96,'1487030115898',11,2,2),(97,'1487030318388',19,5,2),(98,'1487030603736',19,4,0),(99,'1487064576237',19,3,2),(100,'1487067051666',11,3,0),(101,'1487067213815',11,4,0),(102,'1487067821026',11,5,0),(103,'1487072695100',18,3,0),(104,'1487082854710',34,3,0),(105,'1487090640772',35,1,1),(106,'1487090695891',35,2,2),(107,'1487090739130',35,3,0),(108,'1487090866873',35,4,0),(109,'1487090881198',35,5,2),(110,'1487094482936',18,1,2),(111,'1487154208963',36,1,2),(112,'1487160249917',36,2,2),(113,'1487170319374',36,3,2),(114,'1487172082141',36,4,0),(115,'1487174512941',20,1,2),(116,'1487230851920',18,2,2),(117,'1487231843227',18,5,1),(118,'1487234791374',20,2,2),(119,'1487234916481',20,3,2),(120,'1487235002726',20,4,2),(121,'1487235022067',20,5,2),(122,'1487237843328',37,1,2),(123,'1487241570465',37,2,2),(124,'1487252081872',36,5,0),(125,'1487254968229',18,4,0),(126,'1487335132392',38,1,0),(127,'1487344394232',39,1,2),(128,'1487344429764',39,3,0),(129,'1487344797312',39,5,2),(130,'1487344815405',39,2,2),(131,'1487345214915',39,4,2),(132,'1487375505880',37,5,2),(133,'1487376196515',37,3,2),(134,'1487527560455',40,1,2),(135,'1487528868283',40,2,2),(136,'1487529416117',40,3,2),(137,'1487529429669',40,4,0),(138,'1487529432519',40,5,2),(139,'1487576651292',41,1,2),(140,'1487580075946',41,2,2),(141,'1487580289906',41,3,0),(142,'1487581115486',42,1,2),(143,'1487581120223',42,2,0),(144,'1487581121697',42,3,0),(146,'1487581123709',42,5,0),(147,'1487608097348',37,4,1),(148,'1487609811551',41,4,0),(149,'1487609817143',41,5,2),(150,'1487614208375',3,3,0),(151,'1487618983373',38,3,0),(152,'1487684586247',33,1,1),(153,'1487686129799',43,3,2),(154,'1487686139383',43,4,0),(155,'1487686148446',43,5,0),(156,'1487748429108',43,2,0),(157,'1487748431425',43,1,2),(170,'1494257679',42,4,0),(171,'1495615958269',46,2,0),(172,'1495615965590',46,1,0),(173,'1495615973199',46,3,0),(174,'1495616052010',46,4,0),(175,'1495616244120',47,1,0),(176,'1495616247690',47,2,0),(177,'1495616269113',47,3,0),(178,'1495616274199',47,4,0),(179,'1495616275754',47,5,0),(180,'1495616536584',48,1,0),(181,'1495616540328',48,2,0),(182,'1495616541537',48,3,0),(183,'1495616542695',48,4,0),(184,'1495616543834',48,5,0),(185,'1495617815331',49,1,2),(186,'1495617858672',49,2,2),(187,'1495617960045',49,3,2),(188,'1495619269316',49,4,2),(189,'1495619335134',49,5,2),(190,'1496062983716',33,3,0),(191,'1496160256425',38,4,0),(192,'1496227741697',34,4,0),(193,'1496236947322',33,4,0),(194,'1496324429592',38,5,0),(195,'1496324485677',38,2,0),(196,'1496918216634',33,5,0),(197,'1497862429550',34,1,2),(198,'1498556565339',33,2,0),(199,'1499416600114',51,1,0);
/*!40000 ALTER TABLE `design_project_has_steps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `design_project_has_users`
--

DROP TABLE IF EXISTS `design_project_has_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project_has_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `design_project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_design_project_has_users_1_idx` (`user_id`),
  KEY `fk_design_project_has_users_2_idx` (`design_project_id`),
  CONSTRAINT `fk_design_project_has_users_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_design_project_has_users_2` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_has_users`
--

LOCK TABLES `design_project_has_users` WRITE;
/*!40000 ALTER TABLE `design_project_has_users` DISABLE KEYS */;
INSERT INTO `design_project_has_users` VALUES (1,6,52),(2,6,53),(3,4,53),(4,2,52);
/*!40000 ALTER TABLE `design_project_has_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `design_project_history`
--

DROP TABLE IF EXISTS `design_project_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `design_project_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_design_project_history_id` (`id`),
  KEY `ix_design_project_id` (`design_project_id`),
  KEY `ix_design_project_history_user` (`user_id`),
  CONSTRAINT `fk_design_project_history_design_project` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_design_project_history_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_history`
--

LOCK TABLES `design_project_history` WRITE;
/*!40000 ALTER TABLE `design_project_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `design_project_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `design_project_lean_guideline`
--

DROP TABLE IF EXISTS `design_project_lean_guideline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project_lean_guideline` (
  `id` int(11) NOT NULL,
  `design_project_id` int(11) NOT NULL,
  `lean_guideline_id` int(11) NOT NULL,
  `checked` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `design_project_id` (`design_project_id`),
  KEY `lean_guideline_id` (`lean_guideline_id`),
  CONSTRAINT `design_project_lean_guideline_ibfk_1` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`),
  CONSTRAINT `design_project_lean_guideline_ibfk_2` FOREIGN KEY (`lean_guideline_id`) REFERENCES `lean_guideline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_lean_guideline`
--

LOCK TABLES `design_project_lean_guideline` WRITE;
/*!40000 ALTER TABLE `design_project_lean_guideline` DISABLE KEYS */;
INSERT INTO `design_project_lean_guideline` VALUES (199,2,1,0),(200,2,2,1),(201,2,3,1),(205,3,25,1),(206,3,26,1),(215,35,1,1),(216,35,2,1),(226,20,34,0),(227,20,39,0),(228,20,40,0),(229,20,41,0),(232,36,2,0),(233,36,4,0),(234,36,44,0),(252,18,2,1),(253,18,4,1),(254,18,30,1),(291,40,1,1),(292,40,2,1),(293,40,3,1),(294,40,4,1),(295,40,5,0),(296,40,30,1),(297,40,46,1),(298,40,55,1),(307,19,1,1),(308,19,2,1),(309,19,3,0),(310,19,4,1),(311,19,5,0),(312,19,30,1),(313,19,31,0),(314,19,46,1),(315,37,1,1),(321,49,2,1),(322,49,1,1),(323,41,2,1),(324,41,4,1),(325,41,46,1),(326,41,55,1),(327,41,57,1),(328,11,1,0),(329,39,1,1),(330,39,2,1),(331,39,3,0);
/*!40000 ALTER TABLE `design_project_lean_guideline` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `design_project_lean_rule`
--

DROP TABLE IF EXISTS `design_project_lean_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project_lean_rule` (
  `id` int(11) NOT NULL DEFAULT '0',
  `design_project_id` int(11) NOT NULL,
  `lean_rule_id` int(11) NOT NULL,
  `checked` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `design_project_id` (`design_project_id`),
  KEY `lean_rule_id` (`lean_rule_id`),
  CONSTRAINT `design_project_lean_rule_ibfk_1` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`),
  CONSTRAINT `design_project_lean_rule_ibfk_2` FOREIGN KEY (`lean_rule_id`) REFERENCES `lean_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_lean_rule`
--

LOCK TABLES `design_project_lean_rule` WRITE;
/*!40000 ALTER TABLE `design_project_lean_rule` DISABLE KEYS */;
INSERT INTO `design_project_lean_rule` VALUES (293,2,1,1),(294,2,2,1),(295,2,3,1),(296,2,17,0),(297,2,4,0),(298,2,6,0),(299,2,5,1),(300,2,10,0),(301,2,12,1),(302,2,13,1),(303,2,11,0),(304,2,1,0),(305,2,2,0),(306,2,3,0),(307,2,17,0),(308,2,4,1),(309,2,6,1),(310,2,5,0),(311,2,10,1),(312,2,12,0),(313,2,13,0),(314,2,11,0),(329,35,2,1),(330,35,1,1),(331,35,4,1),(332,35,17,0),(333,35,5,1),(334,35,3,1),(335,35,6,1),(342,20,29,0),(344,36,6,0),(345,36,17,0),(346,36,5,0),(347,36,4,0),(348,36,3,0),(349,36,33,0),(373,18,6,1),(374,18,4,1),(375,18,3,1),(376,18,5,1),(377,18,17,1),(438,40,1,1),(439,40,2,1),(440,40,6,1),(441,40,3,1),(442,40,4,1),(443,40,5,1),(444,40,17,1),(445,40,12,1),(446,40,11,1),(447,40,13,1),(448,40,10,1),(449,40,7,1),(450,40,9,0),(451,40,8,0),(452,40,44,1),(463,19,1,1),(464,19,2,1),(465,19,3,1),(466,19,17,0),(467,19,4,1),(468,19,6,1),(469,19,5,1),(470,19,10,1),(471,19,12,1),(472,19,13,1),(473,19,11,0),(474,19,8,0),(475,19,9,0),(476,19,7,1),(477,37,1,1),(478,37,2,1),(486,49,17,0),(487,49,5,1),(488,49,3,1),(489,49,6,1),(490,49,4,1),(491,49,1,1),(492,49,2,1),(493,41,5,1),(494,41,3,1),(495,41,17,1),(496,41,6,1),(497,41,4,1),(498,41,44,1),(499,41,46,1),(500,11,1,0),(501,11,2,0),(502,39,1,1),(503,39,2,1),(504,39,6,1),(505,39,5,1),(506,39,3,1),(507,39,4,1),(508,39,17,0),(509,39,10,1),(510,39,13,1),(511,39,12,1),(512,39,11,0);
/*!40000 ALTER TABLE `design_project_lean_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `design_project_rule`
--

DROP TABLE IF EXISTS `design_project_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project_rule` (
  `design_project_id` int(11) NOT NULL,
  `lean_rule_id` int(11) NOT NULL,
  `checked` tinyint(1) DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`design_project_id`,`lean_rule_id`),
  KEY `ix_design_project_rule` (`lean_rule_id`),
  CONSTRAINT `designProjectId` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `leanRuleId` FOREIGN KEY (`lean_rule_id`) REFERENCES `lean_rule` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_rule`
--

LOCK TABLES `design_project_rule` WRITE;
/*!40000 ALTER TABLE `design_project_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `design_project_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `development_rule`
--

DROP TABLE IF EXISTS `development_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `development_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `applied_to` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_development_rule_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `development_rule`
--

LOCK TABLES `development_rule` WRITE;
/*!40000 ALTER TABLE `development_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `development_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `works_in_company_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_employee_id` (`id`),
  KEY `ix_employee_company` (`works_in_company_id`),
  KEY `ix_employee_user` (`user_id`),
  CONSTRAINT `fk_employee_company` FOREIGN KEY (`works_in_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_employee_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `indexer`
--

DROP TABLE IF EXISTS `indexer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `indexer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `local_url` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `indexer`
--

LOCK TABLES `indexer` WRITE;
/*!40000 ALTER TABLE `indexer` DISABLE KEYS */;
INSERT INTO `indexer` VALUES (1,'Index for Airflow Supersneaker X-MAS','http://div_indexer:9090','http://div_indexer:9090',2,1),(2,'Index for Airflow Supersneaker Africa','http://div_indexer:9090','http://div_indexer:9090',2,1),(3,'Bazigos indexer','http://opennebula.euprojects.net:8923','http://192.168.7.249:9000',4,3),(4,'Carel indexer','http://opennebula.euprojects.net:8941','http://192.168.7.250:9000',3,2);
/*!40000 ALTER TABLE `indexer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kpi`
--

DROP TABLE IF EXISTS `kpi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kpi` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `formula` varchar(255) DEFAULT NULL,
  `data_source` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `direction` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_kpi_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kpi`
--

LOCK TABLES `kpi` WRITE;
/*!40000 ALTER TABLE `kpi` DISABLE KEYS */;
/*!40000 ALTER TABLE `kpi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_ability`
--

DROP TABLE IF EXISTS `lean_ability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_ability` (
  `id` int(11) NOT NULL,
  `ability` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_ability`
--

LOCK TABLES `lean_ability` WRITE;
/*!40000 ALTER TABLE `lean_ability` DISABLE KEYS */;
INSERT INTO `lean_ability` VALUES (1,'Serviceability'),(2,'Manutenability'),(3,'Usability'),(4,'Testability');
/*!40000 ALTER TABLE `lean_ability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_applied_to`
--

DROP TABLE IF EXISTS `lean_applied_to`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_applied_to` (
  `id` int(11) NOT NULL,
  `applied_to` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_applied_to`
--

LOCK TABLES `lean_applied_to` WRITE;
/*!40000 ALTER TABLE `lean_applied_to` DISABLE KEYS */;
INSERT INTO `lean_applied_to` VALUES (1,'All'),(2,'All Products'),(3,'All Services'),(4,'Specific Product/s'),(5,'Specific Service/s');
/*!40000 ALTER TABLE `lean_applied_to` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_classification`
--

DROP TABLE IF EXISTS `lean_classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_classification` (
  `id` int(11) NOT NULL,
  `classification` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_classification`
--

LOCK TABLES `lean_classification` WRITE;
/*!40000 ALTER TABLE `lean_classification` DISABLE KEYS */;
INSERT INTO `lean_classification` VALUES (1,'High Importance','Classification_HighImportance'),(2,'Medium Importance','Classification_MediumImportance'),(3,'Low Importance','Classification_LowImportance'),(4,'Recommendation','Classification_Recommendation');
/*!40000 ALTER TABLE `lean_classification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_guideline`
--

DROP TABLE IF EXISTS `lean_guideline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_guideline` (
  `id` int(11) NOT NULL,
  `date` datetime DEFAULT NULL,
  `guideline` varchar(255) DEFAULT NULL,
  `lean_ability_id` int(11) DEFAULT NULL,
  `lean_applied_to_id` int(11) DEFAULT NULL,
  `lean_classification_id` int(11) NOT NULL,
  `lean_type_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `lean_ability_id` (`lean_ability_id`),
  KEY `lean_classification_id` (`lean_classification_id`),
  KEY `lean_type_id` (`lean_type_id`),
  KEY `lean_applied_to_id` (`lean_applied_to_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `lean_guideline_ibfk_1` FOREIGN KEY (`lean_ability_id`) REFERENCES `lean_ability` (`id`),
  CONSTRAINT `lean_guideline_ibfk_3` FOREIGN KEY (`lean_classification_id`) REFERENCES `lean_classification` (`id`),
  CONSTRAINT `lean_guideline_ibfk_4` FOREIGN KEY (`lean_type_id`) REFERENCES `lean_type` (`id`),
  CONSTRAINT `lean_guideline_ibfk_6` FOREIGN KEY (`lean_applied_to_id`) REFERENCES `lean_applied_to` (`id`),
  CONSTRAINT `lean_guideline_ibfk_7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_guideline`
--

LOCK TABLES `lean_guideline` WRITE;
/*!40000 ALTER TABLE `lean_guideline` DISABLE KEYS */;
INSERT INTO `lean_guideline` VALUES (1,'2017-02-07 14:30:22','Create visual cues allowing macro areas detection in order to improve installation speed',3,4,2,1,NULL),(2,'2017-02-07 14:34:54','Use standard connections in order to improve installation speed',3,4,1,1,NULL),(3,'2017-02-07 14:39:28','Evaluate positioning in installation environment to enhance installation speed',3,4,2,1,NULL),(4,'2017-02-07 14:47:15','Create visual cues allowing macro areas detection in order to improve installation simplicity',3,4,1,1,NULL),(5,'2017-02-07 14:42:38','Use standard connections in order to improve installation simplicity',3,4,2,1,NULL),(6,'2017-02-07 14:47:51','Create visual cues allowing macro areas detection in order to improve the installation process quality through Poka Yoke',3,4,2,1,NULL),(25,'2016-12-01 11:13:59','GL1 - In order to improve the operation speed you must consider the response time',3,4,2,1,2),(26,'2016-12-01 12:40:54','GL2 To improve operation speed, provide a good related topic detection',1,4,2,1,2),(28,'2016-12-13 12:32:48','Create visual cues allowing macro areas detection in order to improve installation speed.',3,4,2,1,3),(29,'2016-12-13 12:33:21','Create visual clues allowing macro areas detection in order to improve installation speed',3,4,2,1,3),(30,'2016-12-13 12:34:40','Use standard connections in order to improve installation speed.',3,4,1,1,3),(31,'2016-12-13 12:35:01','Use standard connections in order to improve installation speed',3,4,2,1,3),(34,'2017-02-14 13:41:52','In order to improve the operation speed you must consider the response time',3,5,1,1,2),(35,'2017-02-14 13:43:04','To improve operation speed, provide a good related topic detection',3,5,2,1,2),(36,'2017-02-07 16:51:56','To improve graphical user interface put all in one scree page',3,5,2,1,NULL),(37,'2017-02-07 16:52:24','To improve graphical user interface follow DESMA corporate design',3,5,2,1,NULL),(38,'2017-02-07 16:54:06','To improve user friendliness, intelligent search must provide a good result ranking',3,5,2,1,NULL),(39,'2017-02-07 16:54:50','To improve user friendliness, intelligent search must provide the right related topics',3,5,1,1,NULL),(40,'2017-02-07 16:55:34','To improve serviceability to be device independent, the solution should be browser based',1,5,1,1,NULL),(41,'2017-02-07 16:56:31','Scalability on DB size should use a virtual server',3,5,1,1,NULL),(42,'2017-02-07 16:57:00','Consider data safety when choose DB',2,5,2,1,NULL),(43,'2017-02-07 17:04:33','Use standard components to foster modularity',2,1,2,1,NULL),(44,'2017-02-07 17:08:58','Use standard components to foster maintainability',2,2,1,1,NULL),(45,'2017-02-07 17:11:03','Consider the connectivity of hydraulic and automatic connection to foster inspectability',2,2,2,1,NULL),(46,'2017-02-10 15:58:24','Provide automatic feedback fostering an ease of installation process test',4,4,1,1,NULL),(55,'2017-02-20 20:32:46','Use visual clues supporting the augmented reality to foster the installation speed',3,4,1,1,3),(57,'2017-02-23 09:38:42','Use visual clues for augmeted reality to improve ease of installation',3,4,1,1,3);
/*!40000 ALTER TABLE `lean_guideline` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_guideline_lean_tag`
--

DROP TABLE IF EXISTS `lean_guideline_lean_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_guideline_lean_tag` (
  `lean_guideline_id` int(11) NOT NULL,
  `lean_tag_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_guideline_id`,`lean_tag_id`),
  KEY `lean_tag_id` (`lean_tag_id`),
  CONSTRAINT `lean_guideline_lean_tag_ibfk_1` FOREIGN KEY (`lean_guideline_id`) REFERENCES `lean_guideline` (`id`),
  CONSTRAINT `lean_guideline_lean_tag_ibfk_2` FOREIGN KEY (`lean_tag_id`) REFERENCES `lean_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_guideline_lean_tag`
--

LOCK TABLES `lean_guideline_lean_tag` WRITE;
/*!40000 ALTER TABLE `lean_guideline_lean_tag` DISABLE KEYS */;
INSERT INTO `lean_guideline_lean_tag` VALUES (1,2),(4,2),(6,2),(28,2),(29,2),(1,4),(4,4),(28,4),(29,4),(1,5),(2,5),(3,5),(28,5),(29,5),(30,5),(31,5),(55,5),(57,5),(1,6),(4,6),(6,6),(28,6),(29,6),(55,6),(57,6),(2,7),(5,7),(30,7),(31,7),(2,8),(5,8),(30,8),(31,8),(2,9),(5,9),(30,9),(31,9),(3,10),(3,11),(6,12),(6,13),(6,14),(46,15),(25,25),(36,25),(37,25),(38,25),(39,25),(40,25),(4,55),(5,55),(25,103),(26,105),(34,105),(35,105),(38,107),(41,108),(42,108),(41,109),(43,111),(44,111),(45,112),(55,114),(57,114);
/*!40000 ALTER TABLE `lean_guideline_lean_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_guideline_product`
--

DROP TABLE IF EXISTS `lean_guideline_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_guideline_product` (
  `lean_guideline_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_guideline_id`,`product_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `lean_guideline_product_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `lean_guideline_product_ibfk_3` FOREIGN KEY (`lean_guideline_id`) REFERENCES `lean_guideline` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_guideline_product`
--

LOCK TABLES `lean_guideline_product` WRITE;
/*!40000 ALTER TABLE `lean_guideline_product` DISABLE KEYS */;
INSERT INTO `lean_guideline_product` VALUES (43,1),(44,1),(45,1),(43,2),(44,2),(45,2),(43,3),(44,3),(45,3),(43,4),(44,4),(45,4),(43,5),(44,5),(45,5),(43,6),(44,6),(45,6),(43,7),(44,7),(45,7),(43,8),(44,8),(45,8),(43,9),(44,9),(45,9),(43,10),(44,10),(45,10),(1,11),(2,11),(3,11),(4,11),(5,11),(6,11),(28,11),(29,11),(30,11),(31,11),(43,11),(44,11),(45,11),(46,11),(55,11),(57,11),(25,12),(26,12),(43,12),(44,12),(45,12),(43,13),(44,13),(45,13),(43,14),(44,14),(45,14),(43,15),(44,15),(45,15),(43,17),(44,17),(45,17),(43,18),(44,18),(45,18),(43,19),(44,19),(45,19);
/*!40000 ALTER TABLE `lean_guideline_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_guideline_service`
--

DROP TABLE IF EXISTS `lean_guideline_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_guideline_service` (
  `lean_guideline_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_guideline_id`,`service_id`),
  KEY `service_id` (`service_id`),
  CONSTRAINT `lean_guideline_service_ibfk_1` FOREIGN KEY (`lean_guideline_id`) REFERENCES `lean_guideline` (`id`),
  CONSTRAINT `lean_guideline_service_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_guideline_service`
--

LOCK TABLES `lean_guideline_service` WRITE;
/*!40000 ALTER TABLE `lean_guideline_service` DISABLE KEYS */;
INSERT INTO `lean_guideline_service` VALUES (34,6),(35,6),(36,6),(37,6),(38,6),(39,6),(40,6),(41,6),(42,6),(43,6),(43,7),(43,8),(43,9),(43,10),(43,11),(43,12),(43,13),(43,14);
/*!40000 ALTER TABLE `lean_guideline_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule`
--

DROP TABLE IF EXISTS `lean_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule` (
  `id` int(11) NOT NULL,
  `date` datetime DEFAULT NULL,
  `rule` varchar(255) DEFAULT NULL,
  `lean_ability_id` int(11) DEFAULT NULL,
  `lean_applied_to_id` int(11) DEFAULT NULL,
  `lean_classification_id` int(11) NOT NULL,
  `lean_guideline_id` int(11) DEFAULT NULL,
  `lean_type_id` int(11) NOT NULL,
  `lean_waste_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `lean_ability_id` (`lean_ability_id`),
  KEY `lean_applied_to_id` (`lean_applied_to_id`),
  KEY `lean_classification_id` (`lean_classification_id`),
  KEY `lean_type_id` (`lean_type_id`),
  KEY `lean_waste_id` (`lean_waste_id`),
  KEY `user_id` (`user_id`),
  KEY `lean_guideline_id` (`lean_guideline_id`),
  CONSTRAINT `lean_rule_ibfk_1` FOREIGN KEY (`lean_ability_id`) REFERENCES `lean_ability` (`id`),
  CONSTRAINT `lean_rule_ibfk_2` FOREIGN KEY (`lean_applied_to_id`) REFERENCES `lean_applied_to` (`id`),
  CONSTRAINT `lean_rule_ibfk_3` FOREIGN KEY (`lean_classification_id`) REFERENCES `lean_classification` (`id`),
  CONSTRAINT `lean_rule_ibfk_5` FOREIGN KEY (`lean_type_id`) REFERENCES `lean_type` (`id`),
  CONSTRAINT `lean_rule_ibfk_6` FOREIGN KEY (`lean_waste_id`) REFERENCES `lean_waste` (`id`),
  CONSTRAINT `lean_rule_ibfk_7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `lean_rule_ibfk_8` FOREIGN KEY (`lean_guideline_id`) REFERENCES `lean_guideline` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule`
--

LOCK TABLES `lean_rule` WRITE;
/*!40000 ALTER TABLE `lean_rule` DISABLE KEYS */;
INSERT INTO `lean_rule` VALUES (1,'2017-02-07 14:31:57','The sticker for water input and output must resist to condensation (100% of moisture) in the long run',3,4,1,1,1,NULL,NULL),(2,'2017-02-07 14:33:10','The sticker legibility distance indicating water input and output should be at least one meter',3,4,1,1,1,NULL,NULL),(3,'2017-02-07 14:38:25','Each terminal for wiring and electrical connection can contain at most one wire',3,4,1,2,1,NULL,NULL),(4,'2017-02-07 14:35:59','The electrical grounding wire must be yellow and green',3,4,1,2,1,NULL,NULL),(5,'2017-02-07 14:35:25','The main button for power supply must be red',3,4,1,2,1,NULL,NULL),(6,'2017-02-07 14:38:56','The fuses should always be positioned on the fuse holder',3,4,1,2,1,NULL,NULL),(7,'2017-02-07 14:46:21','The hydraulic inlet must always have ? GAS thread',3,4,1,5,1,NULL,NULL),(8,'2017-02-07 14:43:38','In order to ensure geographical customization, for the American version of the product provide a dual voltage (220 or 110 V)',3,4,2,5,1,NULL,NULL),(9,'2017-02-07 14:43:08','It is recommended to adopt electronic components that operate at either 50 or 60 Hz',3,4,2,5,1,NULL,NULL),(10,'2017-02-07 14:41:40','Always provide vibration damping feet with adjustable height',3,4,1,3,1,NULL,NULL),(11,'2017-02-07 14:40:36','Provide flexible couplings to break the vibrations transmission between the pump and the engine',3,4,2,3,1,NULL,NULL),(12,'2017-02-07 14:41:07','Place a gasket (epoxy foam layer) around the door to avoid materials (e.g. water, powder, ...) enter in the product and increase the IP rating of the product',3,4,1,3,1,NULL,NULL),(13,'2017-02-07 14:42:09','Prepare a slot on the beat of the door to engage the seal of the gasket in it',3,4,1,3,1,NULL,NULL),(17,'2017-02-14 13:38:29','VDI richtilinie',3,2,2,2,1,NULL,2),(26,'2016-12-13 12:47:23','The sticker for water input and output must resist to condensation (100% of moisture) in the long run.',3,4,1,28,1,NULL,3),(28,'2016-12-13 12:55:36','The sticker for water input and output must resist to condensation (100%) in the long run',3,4,1,29,1,NULL,3),(29,'2017-02-07 16:59:44','2 GB RAM',3,5,2,34,1,NULL,NULL),(30,'2017-02-07 17:00:37','Use state of the art algorithm',3,5,2,35,1,NULL,NULL),(31,'2017-02-07 17:01:41','Save customer buying history and his problems history',3,5,1,36,1,NULL,NULL),(32,'2017-02-07 17:14:49','To improve modularity use standard hotrunner components from validated suppliers',2,2,1,43,1,NULL,NULL),(33,'2017-02-07 17:15:28','To improve maintainability use standard hotrunner components from validated suppliers',2,2,1,44,1,NULL,NULL),(34,'2017-02-07 17:17:38','To improve inspectability use water manifold (collector of water) while designing the connectivity of hydraulic and automatic connection',2,2,2,45,1,NULL,NULL),(38,'2017-02-16 08:54:34','Before starting any PSS development, identify a technical person in charge of the overall PSS development (from concept to launch), this will be your chief engineer. ',NULL,NULL,2,NULL,2,NULL,3),(39,'2017-02-16 08:55:03','Before the PSS concept definition, the chief engineer needs to understand by visiting the customer how this will use the PSS is going to be developed. Chief engineer needs to collect such information in some formal document (e.g. the concept paper).',NULL,NULL,1,NULL,2,NULL,3),(40,'2017-02-16 08:55:26','The chief engineer or someone in his/her team will start from customer experience understanding (formalized in document such as the concept paper) and translate that into customer requirements first and customer specifications after',NULL,NULL,1,NULL,2,NULL,3),(44,'2017-02-20 20:34:02','Put QR codes on the humiFog\'s doors ',3,4,1,55,1,NULL,3),(46,'2017-02-23 09:39:51','Place a QR code on the door of the humiFog to support augmented reality',3,4,1,57,1,NULL,3);
/*!40000 ALTER TABLE `lean_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule_ability`
--

DROP TABLE IF EXISTS `lean_rule_ability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_ability` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ability` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_lean_rule_ability_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule_ability`
--

LOCK TABLES `lean_rule_ability` WRITE;
/*!40000 ALTER TABLE `lean_rule_ability` DISABLE KEYS */;
/*!40000 ALTER TABLE `lean_rule_ability` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule_classification`
--

DROP TABLE IF EXISTS `lean_rule_classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_classification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `classification` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_lean_rule_classification_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule_classification`
--

LOCK TABLES `lean_rule_classification` WRITE;
/*!40000 ALTER TABLE `lean_rule_classification` DISABLE KEYS */;
/*!40000 ALTER TABLE `lean_rule_classification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule_lean_tag`
--

DROP TABLE IF EXISTS `lean_rule_lean_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_lean_tag` (
  `lean_rule_id` int(11) NOT NULL,
  `lean_tag_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_rule_id`,`lean_tag_id`),
  KEY `lean_tag_id` (`lean_tag_id`),
  CONSTRAINT `lean_rule_lean_tag_ibfk_2` FOREIGN KEY (`lean_tag_id`) REFERENCES `lean_tag` (`id`),
  CONSTRAINT `lean_rule_lean_tag_ibfk_3` FOREIGN KEY (`lean_rule_id`) REFERENCES `lean_rule` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule_lean_tag`
--

LOCK TABLES `lean_rule_lean_tag` WRITE;
/*!40000 ALTER TABLE `lean_rule_lean_tag` DISABLE KEYS */;
INSERT INTO `lean_rule_lean_tag` VALUES (4,2),(5,2),(44,5),(46,5),(44,6),(46,6),(17,7),(1,20),(2,20),(26,20),(28,20),(1,21),(2,21),(26,21),(28,21),(1,22),(2,22),(26,22),(28,22),(1,23),(26,23),(28,23),(1,24),(26,24),(28,24),(1,25),(26,25),(28,25),(29,25),(1,26),(26,26),(28,26),(2,27),(3,28),(4,28),(3,29),(4,30),(5,31),(5,32),(6,33),(6,34),(6,35),(7,36),(7,37),(8,38),(8,39),(8,40),(9,41),(9,42),(9,43),(10,44),(10,45),(10,46),(11,47),(11,48),(11,49),(11,50),(12,51),(13,51),(12,52),(30,103),(31,108),(32,111),(33,111),(34,112),(44,114),(46,114),(38,115),(39,115),(40,115);
/*!40000 ALTER TABLE `lean_rule_lean_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule_product`
--

DROP TABLE IF EXISTS `lean_rule_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_product` (
  `lean_rule_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_rule_id`,`product_id`),
  KEY `lean_tag_id` (`product_id`),
  CONSTRAINT `lean_rule_product_ibfk_3` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `lean_rule_product_ibfk_4` FOREIGN KEY (`lean_rule_id`) REFERENCES `lean_rule` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule_product`
--

LOCK TABLES `lean_rule_product` WRITE;
/*!40000 ALTER TABLE `lean_rule_product` DISABLE KEYS */;
INSERT INTO `lean_rule_product` VALUES (17,1),(32,1),(33,1),(34,1),(17,2),(32,2),(33,2),(34,2),(17,3),(32,3),(33,3),(34,3),(17,4),(32,4),(33,4),(34,4),(17,5),(32,5),(33,5),(34,5),(17,6),(32,6),(33,6),(34,6),(17,7),(32,7),(33,7),(34,7),(17,8),(32,8),(33,8),(34,8),(17,9),(32,9),(33,9),(34,9),(17,10),(32,10),(33,10),(34,10),(1,11),(2,11),(3,11),(4,11),(5,11),(6,11),(7,11),(8,11),(9,11),(10,11),(11,11),(12,11),(13,11),(17,11),(26,11),(28,11),(32,11),(33,11),(34,11),(44,11),(46,11),(17,12),(32,12),(33,12),(34,12),(17,13),(32,13),(33,13),(34,13),(17,14),(32,14),(33,14),(34,14),(17,15),(32,15),(33,15),(34,15),(17,17),(32,17),(33,17),(34,17),(17,18),(32,18),(33,18),(34,18),(17,19),(32,19),(33,19),(34,19),(17,23),(17,24),(17,25),(17,26),(17,27),(17,28),(17,29),(17,30),(17,31),(17,32);
/*!40000 ALTER TABLE `lean_rule_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule_service`
--

DROP TABLE IF EXISTS `lean_rule_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_service` (
  `lean_rule_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_rule_id`,`service_id`),
  KEY `lean_tag_id` (`service_id`),
  CONSTRAINT `lean_rule_service_ibfk_2` FOREIGN KEY (`lean_rule_id`) REFERENCES `lean_rule` (`id`),
  CONSTRAINT `lean_rule_service_ibfk_3` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule_service`
--

LOCK TABLES `lean_rule_service` WRITE;
/*!40000 ALTER TABLE `lean_rule_service` DISABLE KEYS */;
INSERT INTO `lean_rule_service` VALUES (29,6),(30,6),(31,6);
/*!40000 ALTER TABLE `lean_rule_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule_tag`
--

DROP TABLE IF EXISTS `lean_rule_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_tag` (
  `lean_rule_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_rule_id`,`tag_id`),
  KEY `ix_lean_rule_tag_tag` (`tag_id`),
  CONSTRAINT `fk_lean_rule_tag_lean_rule` FOREIGN KEY (`lean_rule_id`) REFERENCES `lean_rule` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule_tag`
--

LOCK TABLES `lean_rule_tag` WRITE;
/*!40000 ALTER TABLE `lean_rule_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `lean_rule_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule_type`
--

DROP TABLE IF EXISTS `lean_rule_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_lean_rule_type_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule_type`
--

LOCK TABLES `lean_rule_type` WRITE;
/*!40000 ALTER TABLE `lean_rule_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `lean_rule_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_rule_waste`
--

DROP TABLE IF EXISTS `lean_rule_waste`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_waste` (
  `id` int(11) NOT NULL,
  `avoidabiity` int(11) DEFAULT NULL,
  `detectability` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pii` int(11) DEFAULT NULL,
  `probability` int(11) DEFAULT NULL,
  `severity` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_rule_waste`
--

LOCK TABLES `lean_rule_waste` WRITE;
/*!40000 ALTER TABLE `lean_rule_waste` DISABLE KEYS */;
/*!40000 ALTER TABLE `lean_rule_waste` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_tag`
--

DROP TABLE IF EXISTS `lean_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_tag` (
  `id` int(11) NOT NULL,
  `tag` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_tag`
--

LOCK TABLES `lean_tag` WRITE;
/*!40000 ALTER TABLE `lean_tag` DISABLE KEYS */;
INSERT INTO `lean_tag` VALUES (1,'Tolerance'),(2,'Colours'),(3,'Sensor'),(4,'Labels'),(5,'Installation speed'),(6,'Visual cues'),(7,'Standard connections'),(8,'Electric connection'),(9,'Hydraulic connection'),(10,'Enviroment'),(11,'Positioning'),(12,'Poka Yoke'),(13,'Process quality'),(14,'Holes'),(15,'Feedback'),(16,'Installation test'),(17,'Check button'),(18,'Diagnosis report'),(19,'Light'),(20,'Sticker'),(21,'Water input'),(22,'Water output'),(23,'Condensation'),(24,'Moisture'),(25,'Use phase'),(26,'Installation'),(27,'Legibility distance'),(28,'Wire'),(29,'Terminal for wiring and electrical connection'),(30,'Grounding system'),(31,'Power supply'),(32,'Button'),(33,'Fuse'),(34,'Fuse holder'),(35,'Position'),(36,'Hydraulic inlet'),(37,'Thread'),(38,'Geographical standards'),(39,'Voltage'),(40,'America'),(41,'Frequency'),(42,'Flexible components'),(43,'Alternating current'),(44,'Vibration damping'),(45,'Feet'),(46,'Adjustable height'),(47,'Flexible coupling'),(48,'Vibration'),(49,'Pump'),(50,'Engine'),(51,'Gasket'),(52,'IP rating'),(53,'Slot'),(54,'Door'),(55,'Installation simplicity'),(101,'Sneaker'),(102,'PRocess'),(103,'Process'),(104,'Simulation'),(105,'Operation speed'),(107,'Result ranking'),(108,'Database'),(109,'Scalability'),(110,'Modularity'),(111,'Maintenance'),(112,'Inspectability'),(113,'Traceability'),(114,'Augmented reality'),(115,'Development process improvement');
/*!40000 ALTER TABLE `lean_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_type`
--

DROP TABLE IF EXISTS `lean_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_type` (
  `id` int(11) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_type`
--

LOCK TABLES `lean_type` WRITE;
/*!40000 ALTER TABLE `lean_type` DISABLE KEYS */;
INSERT INTO `lean_type` VALUES (1,'Content Design'),(2,'Development Process');
/*!40000 ALTER TABLE `lean_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lean_waste`
--

DROP TABLE IF EXISTS `lean_waste`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_waste` (
  `id` int(11) NOT NULL,
  `avoidability` int(11) DEFAULT NULL,
  `detectability` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pii` int(11) DEFAULT NULL,
  `probability` int(11) DEFAULT NULL,
  `severity` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lean_waste`
--

LOCK TABLES `lean_waste` WRITE;
/*!40000 ALTER TABLE `lean_waste` DISABLE KEYS */;
INSERT INTO `lean_waste` VALUES (7,2,2,'Specifications not needed and/or not implemented are formulated',24,2,3,''),(8,3,2,'Specifications are formulated with too much details and/or too much earlier (for the specific development phase)',12,1,2,''),(9,4,4,'PSS functionalities not asked / needed are implemented',256,4,4,''),(10,2,3,'Projects not needed and/or not convenient are studied',18,1,3,''),(11,2,3,'Components / materials not needed are used in the product; HR, SW and HW not needed are used in the service delivery process ',24,2,2,''),(12,2,1,'Time spent (without adding value) waiting to process information',12,2,3,''),(13,4,1,'Waiting for decisions, persons, resources, data, information, documents',24,2,3,''),(14,1,2,'Information are available in different formats and ICT systems (e.g. CAD, PDM, ERP, Service scheduling) can?t interoperate ',4,1,2,''),(15,2,1,'Information might be manually retyped from one process / system to another ',4,1,2,''),(16,2,2,'Unnecessary and not needed tolerances are included',24,2,3,''),(17,2,2,'Unnecessary, not useful, not appropriate, immature, not error-free technologies are used',16,1,4,''),(18,2,3,'Development of changes not asked or not needed',24,2,2,''),(19,2,2,'Time spent for bad definition of priorities',16,2,2,''),(20,4,1,'Time is spent for reworks and revisions due to changing priorities, information, data, requirements',16,1,4,''),(21,2,2,'Time is spent working with incomplete / incorrect / inappropriate / not reliable information, data, requirements are performed',12,1,3,''),(22,2,1,'Designs wait for the next available resources',4,1,2,''),(23,2,1,'Batches of projects remains untouched',4,1,2,''),(24,4,1,'Unneeded travels might be done for visit customers',4,1,1,''),(25,2,3,'Reworks and revisions derived by poor-quality products and/ore service',18,1,3,''),(26,2,3,'Reworks and revisions due to incomplete / incorrect / inappropriate / not reliable (of suspect quality) information, data, requirements',18,1,3,''),(27,2,3,'Communications failure and non-conformance',18,1,3,''),(28,2,4,'Inability to reuse previous knowledge',48,2,3,''),(29,2,1,'New employees can&#39;t retrieve company knowledge easily',24,4,3,''),(30,NULL,NULL,'Design data and info are formulated with too much details and/or too much earlier ',0,0,NULL,''),(31,NULL,NULL,'Unneeded and not useful activities are performed along the development phase',0,0,NULL,''),(32,NULL,NULL,'Unnecessary and not useful tests are performed ',0,0,NULL,''),(33,NULL,NULL,'Development of parts / components / products already designed and existing, without re-using previous works and projects',0,0,NULL,''),(34,NULL,NULL,'Too many authorizations / controls are needed to perform an activity',0,0,NULL,''),(35,NULL,NULL,'The development process is performed in different ways, depending by customers / suppliers / others',0,0,NULL,''),(36,NULL,NULL,'The development process is performed in different ways, depending by customers / suppliers / others',0,0,NULL,''),(37,NULL,NULL,'Unneeded travels might be done for managing projects and teams',0,0,NULL,''),(38,NULL,NULL,'Unneeded and unuseful meetings are continuosly organized with customers',0,0,NULL,''),(39,NULL,NULL,'Unneeded and unuseful meetings are continuosly organized inside the company',0,0,NULL,''),(40,NULL,NULL,'Unneeded and unuseful meetings are continuosly organized with the service dleivery network',0,0,NULL,''),(41,NULL,NULL,'Reworks and revisions derived by not successful products and / or service',0,0,NULL,''),(42,NULL,NULL,'Reworks and revisions derived by not successful products and / or service',0,0,NULL,''),(43,NULL,NULL,'Too many spare parts codes asked by the customer in order to have a certain level of spare parts service, this has a cost for the company',0,0,NULL,''),(44,NULL,NULL,'New codes and new instructions are built around some part that will never be handled',0,0,NULL,''),(45,NULL,NULL,'Company generates and keeps spare parts as a safe condition to cover eventual request coming from the customer, but doing so many codes are created that will be not, or very lowly, handled',0,0,NULL,''),(46,NULL,NULL,'Specifications not needed and/or not implemented are formulated',0,NULL,NULL,'');
/*!40000 ALTER TABLE `lean_waste` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `part_of_product`
--

DROP TABLE IF EXISTS `part_of_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `part_of_product` (
  `product_id` int(11) NOT NULL,
  `part_of_product_id` int(11) NOT NULL,
  PRIMARY KEY (`product_id`,`part_of_product_id`),
  KEY `ix_part_of_product_id` (`part_of_product_id`),
  CONSTRAINT `fk_part_of_product_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_part_of_product_2` FOREIGN KEY (`part_of_product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `part_of_product`
--

LOCK TABLES `part_of_product` WRITE;
/*!40000 ALTER TABLE `part_of_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `part_of_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `part_of_service`
--

DROP TABLE IF EXISTS `part_of_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `part_of_service` (
  `service_id` int(11) NOT NULL,
  `part_of_service_id` int(11) NOT NULL,
  PRIMARY KEY (`service_id`,`part_of_service_id`),
  KEY `ix_part_of_service_2` (`part_of_service_id`),
  CONSTRAINT `fk_part_of_service_1` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_part_of_service_2` FOREIGN KEY (`part_of_service_id`) REFERENCES `service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `part_of_service`
--

LOCK TABLES `part_of_service` WRITE;
/*!40000 ALTER TABLE `part_of_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `part_of_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  `parent_product_id` int(11) DEFAULT NULL,
  `supplied_by_company_id` int(11) DEFAULT NULL,
  `is_final_product` tinyint(1) NOT NULL,
  `part_number` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_product_id` (`id`),
  KEY `ix_product_company` (`supplied_by_company_id`),
  KEY `fk_product_product_idx` (`parent_product_id`),
  CONSTRAINT `fk_product_company` FOREIGN KEY (`supplied_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_product` FOREIGN KEY (`parent_product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Morris Ground 1','',NULL,NULL,1,'humiFog_topAssy_PN',NULL),(2,'Austin Basket',NULL,NULL,NULL,1,NULL,NULL),(3,'Austin Soccer',NULL,NULL,NULL,1,NULL,NULL),(4,'Morris Sea 1000',NULL,NULL,NULL,1,NULL,NULL),(5,'Morris Sea 2099',NULL,NULL,NULL,1,NULL,NULL),(6,'Morris Wind',NULL,NULL,NULL,1,NULL,NULL),(7,'Austin Polo',NULL,NULL,NULL,1,NULL,NULL),(8,'Austin Cricket',NULL,NULL,NULL,1,NULL,NULL),(9,'Austin XC',NULL,NULL,NULL,1,NULL,NULL),(10,'Austin Base',NULL,NULL,NULL,1,NULL,NULL),(11,'humiFog',NULL,NULL,NULL,0,'humiFog_topAssy_PN',NULL),(12,'D522/30-S-LDI',NULL,NULL,NULL,0,NULL,NULL),(13,'D522/18 E-LDM',NULL,NULL,NULL,0,NULL,NULL),(14,'Airflow Supersneaker',NULL,NULL,NULL,1,NULL,NULL),(15,'Airflow 2 x-mas edition',NULL,NULL,NULL,1,NULL,NULL),(17,'Monitoring software',NULL,NULL,NULL,0,NULL,NULL),(18,'Increase product features',NULL,NULL,NULL,0,NULL,NULL),(19,'Sensors in the product',NULL,NULL,NULL,0,NULL,NULL),(23,'Mobile APP',NULL,NULL,NULL,0,NULL,NULL),(24,'Monitoring software',NULL,NULL,NULL,0,NULL,NULL),(25,'Increase product features',NULL,NULL,NULL,0,NULL,NULL),(26,'Sensors in the product',NULL,NULL,NULL,0,NULL,NULL),(27,'AAAAAAAAAAAAAAAAAAAAAAA',NULL,NULL,NULL,0,NULL,NULL),(28,'cloud',NULL,NULL,NULL,0,NULL,NULL),(29,'Mobile APP',NULL,NULL,NULL,0,NULL,NULL),(30,'Monitoring software',NULL,NULL,NULL,0,NULL,NULL),(31,'Increase product features',NULL,NULL,NULL,0,NULL,NULL),(32,'Sensors in the product',NULL,NULL,NULL,0,NULL,NULL),(33,'sensors in the product',NULL,NULL,NULL,0,NULL,NULL),(34,'Increase product features',NULL,NULL,NULL,0,NULL,NULL),(35,'Sensors in the product',NULL,NULL,NULL,0,NULL,NULL),(36,'Material & Processing data',NULL,NULL,NULL,0,NULL,NULL),(37,'y',NULL,NULL,NULL,0,NULL,NULL),(38,'Mobile APP',NULL,NULL,NULL,0,NULL,NULL),(39,'Monitoring software',NULL,NULL,NULL,0,NULL,NULL),(40,'Increase product features',NULL,NULL,NULL,0,NULL,NULL),(41,'Mobile APP',NULL,NULL,NULL,0,NULL,NULL),(42,'Monitoring software',NULL,NULL,NULL,0,NULL,NULL),(43,'Increase product features',NULL,NULL,NULL,0,NULL,NULL),(44,'Sensors in the product',NULL,NULL,NULL,0,NULL,NULL),(45,'Mobile APP',NULL,NULL,NULL,0,NULL,NULL),(46,'Monitoring software',NULL,NULL,NULL,0,NULL,NULL),(47,'Increase product features',NULL,NULL,NULL,0,NULL,NULL),(48,'Sensors in the product',NULL,NULL,NULL,0,NULL,NULL),(49,'Mobile APP',NULL,NULL,NULL,0,NULL,NULL),(50,'Monitoring software',NULL,NULL,NULL,0,NULL,NULL),(59,'Test7',NULL,23,NULL,0,NULL,NULL),(60,'Test del 24 05 di mattina',NULL,NULL,NULL,1,NULL,NULL),(63,'H Line',NULL,NULL,NULL,0,NULL,NULL),(64,'L Line',NULL,NULL,NULL,0,NULL,NULL),(65,'H5',NULL,63,NULL,0,NULL,NULL),(66,'H4',NULL,63,NULL,0,NULL,NULL),(67,'H3',NULL,63,NULL,0,NULL,NULL),(68,'L5',NULL,64,NULL,0,NULL,NULL),(69,'L4',NULL,64,NULL,0,NULL,NULL),(70,'L3',NULL,64,NULL,0,NULL,NULL),(71,'H5+',NULL,65,NULL,1,NULL,NULL),(72,'H50',NULL,65,NULL,1,NULL,NULL),(73,'H4+',NULL,66,NULL,1,NULL,NULL),(74,'H40',NULL,66,NULL,1,NULL,NULL),(75,'H30',NULL,67,NULL,1,NULL,NULL),(76,'L50',NULL,68,NULL,1,NULL,NULL),(77,'L5-',NULL,68,NULL,1,NULL,NULL),(78,'L40',NULL,69,NULL,1,NULL,NULL),(79,'L4-',NULL,69,NULL,1,NULL,NULL),(80,'L30',NULL,70,NULL,1,NULL,NULL),(81,'L3-',NULL,70,NULL,1,NULL,NULL),(83,'Test Setup Product Crowne','Demonstrator',6,2,0,NULL,NULL),(88,'New_PROD',NULL,NULL,NULL,0,NULL,'');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pss`
--

DROP TABLE IF EXISTS `pss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pss` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` enum('USE','PRODUCT','RESULT') DEFAULT NULL,
  `sold_by_company_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_pss_id` (`id`),
  KEY `ix_pss_company` (`sold_by_company_id`),
  KEY `fk_pss_user_idx` (`user_id`),
  CONSTRAINT `fk_pss_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `pssOwnedBy` FOREIGN KEY (`sold_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss`
--

LOCK TABLES `pss` WRITE;
/*!40000 ALTER TABLE `pss` DISABLE KEYS */;
INSERT INTO `pss` VALUES (1,'PRODUCT',NULL,NULL,'D522-1 PSS',NULL),(2,'PRODUCT',NULL,NULL,'D522-2 PSS',NULL),(3,'PRODUCT',NULL,NULL,'D341-1 PSS',NULL),(4,'PRODUCT',NULL,NULL,'D231-1 PSS',NULL),(5,'PRODUCT',NULL,NULL,'D231-2 PSS',NULL),(6,'PRODUCT',NULL,NULL,'D000-0 PSS',NULL),(13,'PRODUCT',NULL,3,'HumiFog Installation Service',NULL),(15,'USE',NULL,2,'Airflow PSS',NULL),(27,'RESULT',NULL,3,'test_PSS',NULL),(28,'USE',NULL,3,'Testmattina20170210',NULL),(30,'PRODUCT',NULL,3,'ProvaPomeriggio0022017',NULL),(31,'PRODUCT',NULL,2,'Airflow supersneaker x-mas edition PSS',NULL),(32,'PRODUCT',NULL,2,' Supersneaker Low-Tops PSS ',NULL),(33,'PRODUCT',NULL,3,'TEST14022017',NULL),(35,'PRODUCT',NULL,2,'Airflow supersneaker second x-mas edition PSS',NULL),(42,'PRODUCT',NULL,3,'Installation through augmented reality',NULL),(66,'PRODUCT',NULL,3,'Test del 24-05 Mattino',''),(67,'PRODUCT',NULL,NULL,'PSS-58',NULL),(68,'PRODUCT',NULL,NULL,'PSS-57',NULL),(69,'PRODUCT',NULL,NULL,'PSS-56',NULL),(70,'PRODUCT',NULL,NULL,'PSS-55',NULL),(71,'PRODUCT',NULL,NULL,'PSS-44',NULL),(72,'PRODUCT',NULL,NULL,'PSS-43',NULL),(73,'PRODUCT',NULL,NULL,'PSS-42',NULL),(74,'PRODUCT',NULL,NULL,'PSS-41',NULL),(75,'PRODUCT',NULL,NULL,'PSS-33',NULL),(76,'PRODUCT',NULL,NULL,'PSS-32',NULL),(77,'PRODUCT',NULL,NULL,'PSS-31',NULL),(78,'USE',1,NULL,'Test PSS Setup','Test PSS Setup'),(79,'RESULT',2,NULL,'Test PSS Setup 1',NULL),(80,NULL,3,NULL,'Test PSS Setup 2',NULL),(81,NULL,1,NULL,'Test PSS Setup 3',NULL),(82,'RESULT',3,NULL,'Test PSS Setup Crown',NULL);
/*!40000 ALTER TABLE `pss` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pss_has_product`
--

DROP TABLE IF EXISTS `pss_has_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pss_has_product` (
  `pss_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`pss_id`,`product_id`),
  KEY `ix_pss_product` (`product_id`),
  CONSTRAINT `pss_has_product_ibfk_1` FOREIGN KEY (`pss_id`) REFERENCES `pss` (`id`),
  CONSTRAINT `pss_has_product_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss_has_product`
--

LOCK TABLES `pss_has_product` WRITE;
/*!40000 ALTER TABLE `pss_has_product` DISABLE KEYS */;
INSERT INTO `pss_has_product` VALUES (1,1),(80,1),(82,1),(2,2),(80,2),(81,2),(2,3),(80,3),(82,3),(3,4),(81,4),(82,4),(3,5),(3,6),(4,7),(4,8),(5,9),(5,10),(27,11),(33,11),(42,11),(35,12),(31,14),(28,27),(66,59),(67,71),(68,72),(69,73),(70,74),(71,75),(72,76),(73,77),(74,78),(75,79),(76,80),(77,81);
/*!40000 ALTER TABLE `pss_has_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pss_has_service`
--

DROP TABLE IF EXISTS `pss_has_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pss_has_service` (
  `pss_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`pss_id`,`service_id`),
  KEY `ix_pss_service_service` (`service_id`),
  CONSTRAINT `pss_has_service_ibfk_1` FOREIGN KEY (`pss_id`) REFERENCES `pss` (`id`),
  CONSTRAINT `pss_has_service_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss_has_service`
--

LOCK TABLES `pss_has_service` WRITE;
/*!40000 ALTER TABLE `pss_has_service` DISABLE KEYS */;
INSERT INTO `pss_has_service` VALUES (1,6),(27,6),(35,6),(79,7),(81,7),(82,7),(78,8),(80,8),(81,8),(82,8),(42,9),(78,9),(82,9),(33,12),(28,19),(33,23),(79,23),(79,24),(79,25),(66,65),(67,66),(71,66),(67,68),(68,68),(69,68),(70,68),(71,68),(72,68),(73,68),(74,68),(75,68),(76,68),(77,68),(67,69),(68,69),(71,69),(72,69),(75,69),(69,70),(70,70),(73,70),(74,70),(76,70);
/*!40000 ALTER TABLE `pss_has_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pss_kpi`
--

DROP TABLE IF EXISTS `pss_kpi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pss_kpi` (
  `kpi_id` int(11) NOT NULL,
  `pss_id` int(11) NOT NULL,
  `design_project_id` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`kpi_id`,`pss_id`),
  KEY `fk_pss_kpi_pss_idx` (`pss_id`),
  KEY `fk_pss_kpi_design_project_idx` (`design_project_id`),
  CONSTRAINT `fk_pss_kpi_design_project` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pss_kpi_kpi` FOREIGN KEY (`kpi_id`) REFERENCES `kpi` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pss_kpi_pss` FOREIGN KEY (`pss_id`) REFERENCES `pss` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss_kpi`
--

LOCK TABLES `pss_kpi` WRITE;
/*!40000 ALTER TABLE `pss_kpi` DISABLE KEYS */;
/*!40000 ALTER TABLE `pss_kpi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pss_produces_product`
--

DROP TABLE IF EXISTS `pss_produces_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pss_produces_product` (
  `pss_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`pss_id`,`product_id`),
  KEY `ix_pss_produces_product_product` (`product_id`),
  CONSTRAINT `fk_pss_produces_product_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pss_produces_product_pss` FOREIGN KEY (`pss_id`) REFERENCES `pss` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss_produces_product`
--

LOCK TABLES `pss_produces_product` WRITE;
/*!40000 ALTER TABLE `pss_produces_product` DISABLE KEYS */;
INSERT INTO `pss_produces_product` VALUES (82,1),(81,2),(82,2),(81,3),(81,4);
/*!40000 ALTER TABLE `pss_produces_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saved_searches`
--

DROP TABLE IF EXISTS `saved_searches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saved_searches` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE latin1_german1_ci NOT NULL,
  `search` varchar(100) COLLATE latin1_german1_ci NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=32 DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_searches`
--

LOCK TABLES `saved_searches` WRITE;
/*!40000 ALTER TABLE `saved_searches` DISABLE KEYS */;
INSERT INTO `saved_searches` VALUES (17,'machine operation support','machine operation support service',2),(6,'Mein Schuh','germany',0),(7,'Dein Schuh','germany',0),(15,'diagnostics','diagnostics',2),(14,'Schuhbidu','faltus',0),(18,'machine','machine',2),(22,'airflow sneaker','airflow sneaker',2),(23,'PSS sneaker','PSS sneaker',3),(31,'formfuellung','formfuellung',2);
/*!40000 ALTER TABLE `saved_searches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `provided_by_company_id` int(11) DEFAULT NULL,
  `parent_service_id` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_service_id` (`id`),
  KEY `ix_service_company_id` (`provided_by_company_id`),
  KEY `fk_service_service` (`parent_service_id`),
  CONSTRAINT `fk_service_company` FOREIGN KEY (`provided_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_service_service` FOREIGN KEY (`parent_service_id`) REFERENCES `service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (6,'Cloud based machine operation support service',NULL,NULL,NULL),(7,'Cloud based shoe machine design and parametri',NULL,NULL,NULL),(8,'Consumer information and feedback ',NULL,NULL,NULL),(9,'Mobile APP',NULL,NULL,NULL),(10,'Customer care service',NULL,NULL,NULL),(11,'Employee in foreign countries',NULL,NULL,NULL),(12,'Standardization of service procedure',NULL,NULL,NULL),(13,'Service network',NULL,NULL,NULL),(14,'my resource 1',NULL,NULL,NULL),(16,'Standardization of service procedure',NULL,NULL,NULL),(17,'Employee in foreign countries',NULL,NULL,NULL),(18,'Service network',NULL,NULL,NULL),(19,'BBBBBBBBBBBBBBBBBBBBBB',NULL,NULL,NULL),(20,'Customer care service ',NULL,NULL,NULL),(21,'Standardization of service procedure',NULL,NULL,NULL),(22,'Employee in foreign countries',NULL,NULL,NULL),(23,'Service network',NULL,NULL,NULL),(24,'Customer care service',NULL,NULL,NULL),(25,'Customer care service',NULL,NULL,NULL),(26,'Customer care service',NULL,NULL,NULL),(27,'mobile app',NULL,NULL,NULL),(28,'customer care',NULL,NULL,NULL),(29,'mobile app',NULL,NULL,NULL),(30,'Standardization of service procedure',NULL,NULL,NULL),(31,'Mobile APP',NULL,NULL,NULL),(32,'Employee in foreign countries',NULL,NULL,NULL),(33,'Monitoring software',NULL,NULL,NULL),(34,'Service network',NULL,NULL,NULL),(35,'Customer care service',NULL,NULL,NULL),(36,'SW-Tool machine data analysis',NULL,NULL,NULL),(37,'monitoring software',NULL,NULL,NULL),(38,'mobile app',NULL,NULL,NULL),(39,'z',NULL,NULL,NULL),(40,'Inspection support using the digital tool',NULL,NULL,NULL),(41,'Delivery time estimation too',NULL,NULL,NULL),(42,'Delivery time estimation tool',NULL,NULL,NULL),(43,'Mould maintenance tool (web portal)',NULL,NULL,NULL),(44,'Maintenance project manager (engineer',NULL,NULL,NULL),(45,'Standardization of service procedure',NULL,NULL,NULL),(46,'Employee in foreign countries',NULL,NULL,NULL),(47,'Service network',NULL,NULL,NULL),(48,'Sensors in the product',NULL,NULL,NULL),(49,'Standardization of service procedure',NULL,NULL,NULL),(50,'Employee in foreing countries',NULL,NULL,NULL),(51,'Service network',NULL,NULL,NULL),(52,'Standardization of service procedure',NULL,NULL,NULL),(53,'Employee in foreign countries',NULL,NULL,NULL),(54,'Service network',NULL,NULL,NULL),(55,'customer care service',NULL,NULL,NULL),(56,'Standardization of service procedure',NULL,NULL,NULL),(57,'service network',NULL,NULL,NULL),(58,'Customer care service',NULL,NULL,NULL),(59,'Customer care service',NULL,NULL,NULL),(60,'Customer care service',NULL,NULL,NULL),(65,'Test8',NULL,10,NULL),(66,'Assistant+',NULL,NULL,NULL),(67,'Helpdesk',NULL,NULL,NULL),(68,'AppStore',NULL,NULL,NULL),(69,'HelpCall',NULL,67,NULL),(70,'HelpMsg',NULL,67,NULL),(72,'Test Service Setup',1,6,NULL),(73,'Test Service Setup Crown',6,14,NULL);
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solution`
--

DROP TABLE IF EXISTS `solution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `solution` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `design_project_id` int(11) DEFAULT NULL,
  `tree_map_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `design_project_id` (`design_project_id`),
  KEY `tree_map_id` (`tree_map_id`),
  CONSTRAINT `solution_ibfk_1` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`),
  CONSTRAINT `solution_ibfk_2` FOREIGN KEY (`tree_map_id`) REFERENCES `tree_map` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution`
--

LOCK TABLES `solution` WRITE;
/*!40000 ALTER TABLE `solution` DISABLE KEYS */;
INSERT INTO `solution` VALUES (24,'maintenance in the cloud','',2,21),(25,'Maintenance intervention traceability','',19,11),(26,'poka-yoke in product','',36,22),(27,'Easy manual','',18,11),(28,'Knowledge data base incl. intelligent search','Improved support by DESMA knowledge database enabling all technicians to fastly find an efficient solution for problems',20,22),(30,'b','',18,26),(31,'d','',39,26),(32,'easy manual','',40,30),(33,'ttt','',37,34),(34,'Augmented reality','',41,31),(35,'solution 1','',43,35),(37,'Test5','',49,36),(45,'New Solution Resources Annessi e Connessi','',34,21);
/*!40000 ALTER TABLE `solution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solution_has_product`
--

DROP TABLE IF EXISTS `solution_has_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `solution_has_product` (
  `solution_id` int(11) unsigned NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`solution_id`,`product_id`),
  KEY `service_id` (`product_id`),
  CONSTRAINT `solution_has_product_ibfk_1` FOREIGN KEY (`solution_id`) REFERENCES `solution` (`id`),
  CONSTRAINT `solution_has_product_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution_has_product`
--

LOCK TABLES `solution_has_product` WRITE;
/*!40000 ALTER TABLE `solution_has_product` DISABLE KEYS */;
INSERT INTO `solution_has_product` VALUES (45,9),(25,11),(26,11),(27,11),(31,11),(32,11),(33,11),(34,11),(24,12),(28,12),(35,12),(24,14),(33,37),(37,59);
/*!40000 ALTER TABLE `solution_has_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solution_has_service`
--

DROP TABLE IF EXISTS `solution_has_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `solution_has_service` (
  `solution_id` int(11) unsigned NOT NULL,
  `service_id` int(11) NOT NULL,
  PRIMARY KEY (`solution_id`,`service_id`),
  KEY `service_id` (`service_id`),
  CONSTRAINT `solution_has_service_ibfk_1` FOREIGN KEY (`solution_id`) REFERENCES `solution` (`id`),
  CONSTRAINT `solution_has_service_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution_has_service`
--

LOCK TABLES `solution_has_service` WRITE;
/*!40000 ALTER TABLE `solution_has_service` DISABLE KEYS */;
INSERT INTO `solution_has_service` VALUES (24,6),(28,6),(26,7),(27,9),(31,9),(32,9),(34,9),(45,10),(25,12),(33,14),(25,23),(25,27),(26,38),(37,65);
/*!40000 ALTER TABLE `solution_has_service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `step`
--

DROP TABLE IF EXISTS `step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `step` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `step`
--

LOCK TABLES `step` WRITE;
/*!40000 ALTER TABLE `step` DISABLE KEYS */;
INSERT INTO `step` VALUES (1,'Design Concept'),(2,'Associate Design Rules'),(3,'Design PSS'),(4,'Design Service'),(5,'Validate PSS Design');
/*!40000 ALTER TABLE `step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tag_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tree_map`
--

DROP TABLE IF EXISTS `tree_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tree_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `xml` text,
  `design_project_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tree_map_id` (`id`),
  KEY `design_project_id` (`design_project_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tree_map_ibfk_1` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`),
  CONSTRAINT `tree_map_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tree_map`
--

LOCK TABLES `tree_map` WRITE;
/*!40000 ALTER TABLE `tree_map` DISABLE KEYS */;
INSERT INTO `tree_map` VALUES (11,'2017-06-19 11:28:32','Albero Francesca','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>16</id>\n    <features>\n        <business>Humidification</business>\n        <industry>All</industry>\n        <name>All customers</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>---</turnover>\n    </features>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_15</id>\n        <impact>1</impact>\n        <name>asdasd</name>\n        <type>SOLUTION</type>\n    </solutions>\n</treeMapStructure>\n',41,3),(21,'2017-07-07 16:39:17','Airflow tree','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>10</id>\n    <features>\n        <business>shoes</business>\n        <industry>shoes</industry>\n        <name>Airflow Scarpe Sportivo</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>1 mio</turnover>\n    </features>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_0</id>\n        <impact>1</impact>\n        <name>New Solution Resources Annessi e Connessi</name>\n        <nodesIdTo>N_2</nodesIdTo>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesOutJSFormat>N_2;N_3;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n</treeMapStructure>\n',34,3),(22,'2017-07-06 10:47:47','Airflow supersneaker x-mas refurbished','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>17</id>\n    <features>\n        <business>Sports shoes</business>\n        <industry>Sports shoes</industry>\n        <name>Airflow Scarpe Sportivo</name>\n        <possibleCompetitors>Sporty Shoes Ltd.</possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>n.a.</turnover>\n    </features>\n    <needs>\n        <description>Monitor status &amp; performance of production chain (connected machines)</description>\n        <difficulty>1</difficulty>\n        <id>N_0</id>\n        <impact>1</impact>\n        <name>Production monitoring</name>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesOutJSFormat>N_3;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <needs>\n        <description>Best possible exploitation of production resources (machines)</description>\n        <difficulty>1</difficulty>\n        <id>N_1</id>\n        <impact>1</impact>\n        <name>Efficient production</name>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesIdTo>N_4</nodesIdTo>\n        <nodesIdTo>N_5</nodesIdTo>\n        <nodesOutJSFormat>N_3;N_4;N_5;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <needs>\n        <description>Minimum time of machines not producing shoes due to problems or otheh reasons</description>\n        <difficulty>1</difficulty>\n        <id>N_2</id>\n        <impact>1</impact>\n        <name>Minimum downtime</name>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesIdTo>N_4</nodesIdTo>\n        <nodesIdTo>N_5</nodesIdTo>\n        <nodesIdTo>N_6</nodesIdTo>\n        <nodesOutJSFormat>N_3;N_4;N_5;N_6;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <wishes>\n        <description>All project relevant data (shoe design, materials, process flow chart, machine status, ) real-time available</description>\n        <difficulty>1</difficulty>\n        <id>N_3</id>\n        <impact>1</impact>\n        <name>Instant data availability</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description>Machine and material supplier consult with operator to optimally operate machine and efficiently process materials</description>\n        <difficulty>1</difficulty>\n        <id>N_4</id>\n        <impact>1</impact>\n        <name>Support in machine operation</name>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdTo>N_7</nodesIdTo>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_9</nodesIdTo>\n        <nodesOutJSFormat>N_7;N_8;N_9;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description>Analyse and solve machine issues in normal production prior to becoming critical</description>\n        <difficulty>1</difficulty>\n        <id>N_5</id>\n        <impact>1</impact>\n        <name>Predictive Maintenance</name>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdTo>N_10</nodesIdTo>\n        <nodesOutJSFormat>N_10;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description>Fast problem solution in case of machine and/or material problem stopping production</description>\n        <difficulty>1</difficulty>\n        <id>N_6</id>\n        <impact>1</impact>\n        <name>Fast Trouble shooting</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_9</nodesIdTo>\n        <nodesIdTo>N_10</nodesIdTo>\n        <nodesOutJSFormat>N_8;N_9;N_10;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <solutions>\n        <description>DESMA experts training and supporting customer staff in machine use</description>\n        <difficulty>4</difficulty>\n        <id>N_7</id>\n        <impact>4</impact>\n        <name>Training &amp; consulting on site</name>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesIdTo>N_14</nodesIdTo>\n        <nodesOutJSFormat>N_11;N_14;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description>DESMA technician on site for trouble shooting</description>\n        <difficulty>5</difficulty>\n        <id>N_8</id>\n        <impact>5</impact>\n        <name>Support on site</name>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdFrom>N_6</nodesIdFrom>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesIdTo>N_14</nodesIdTo>\n        <nodesOutJSFormat>N_11;N_14;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description>DESMA support via telephone conversation and internet connection to customer machine control. DESMA expert in Achim viewing of status and error log.</description>\n        <difficulty>1</difficulty>\n        <id>N_9</id>\n        <impact>1</impact>\n        <name>Tele support</name>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdFrom>N_6</nodesIdFrom>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesIdTo>N_12</nodesIdTo>\n        <nodesIdTo>N_14</nodesIdTo>\n        <nodesOutJSFormat>N_11;N_12;N_14;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description>Improved support by DESMA knowledge database enabling all technicians to fastly find an efficient solution for problems</description>\n        <difficulty>2</difficulty>\n        <id>N_10</id>\n        <impact>5</impact>\n        <name>Knowledge data base incl. intelligent search</name>\n        <nodesIdFrom>N_5</nodesIdFrom>\n        <nodesIdFrom>N_6</nodesIdFrom>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesIdTo>N_12</nodesIdTo>\n        <nodesIdTo>N_13</nodesIdTo>\n        <nodesIdTo>N_14</nodesIdTo>\n        <nodesOutJSFormat>N_11;N_12;N_13;N_14;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_11</id>\n        <impact>1</impact>\n        <linkTo>Sensors in the product</linkTo>\n        <name>Shoe machine (sensors, control)</name>\n        <nodesIdFrom>N_7</nodesIdFrom>\n        <nodesIdFrom>N_8</nodesIdFrom>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesIdFrom>N_10</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>Existing Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_12</id>\n        <impact>1</impact>\n        <linkTo>Service network</linkTo>\n        <name>Internet connection</name>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesIdFrom>N_10</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>Existing Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_13</id>\n        <impact>1</impact>\n        <name>SW-Tool machine data analysis</name>\n        <nodesIdFrom>N_10</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_14</id>\n        <impact>1</impact>\n        <name>Material &amp; Processing data</name>\n        <nodesIdFrom>N_7</nodesIdFrom>\n        <nodesIdFrom>N_8</nodesIdFrom>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesIdFrom>N_10</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n</treeMapStructure>\n',20,3),(26,'2017-07-06 11:24:27','BAZIGOS PSS PSCT','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>56</id>\n    <features>\n        <business>2</business>\n        <industry>3</industry>\n        <name>1</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>4</turnover>\n    </features>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_30</id>\n        <impact>1</impact>\n        <name>Monitoring &amp; control mould lifecycle</name>\n        <nodesIdTo>N_32</nodesIdTo>\n        <nodesIdTo>N_33</nodesIdTo>\n        <nodesOutJSFormat>N_32;N_33;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_31</id>\n        <impact>1</impact>\n        <name>Shorter mould downtime</name>\n        <nodesIdTo>N_32</nodesIdTo>\n        <nodesOutJSFormat>N_32;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_32</id>\n        <impact>1</impact>\n        <name>Increased information availability</name>\n        <nodesIdFrom>N_30</nodesIdFrom>\n        <nodesIdFrom>N_31</nodesIdFrom>\n        <nodesIdTo>N_34</nodesIdTo>\n        <nodesIdTo>N_35</nodesIdTo>\n        <nodesOutJSFormat>N_34;N_35;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_33</id>\n        <impact>1</impact>\n        <name>Collaborative maintenance operations planning</name>\n        <nodesIdFrom>N_30</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_34</id>\n        <impact>1</impact>\n        <name>Analysis of each repair instance</name>\n        <nodesIdFrom>N_32</nodesIdFrom>\n        <nodesIdTo>N_39</nodesIdTo>\n        <nodesOutJSFormat>N_39;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_35</id>\n        <impact>1</impact>\n        <name>Digital History of repairs for the mould</name>\n        <nodesIdFrom>N_32</nodesIdFrom>\n        <nodesIdTo>N_40</nodesIdTo>\n        <nodesOutJSFormat>N_40;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_36</id>\n        <impact>1</impact>\n        <name>Supporting tool to handle maintenance communication/interactions</name>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_37</id>\n        <impact>1</impact>\n        <name>Accurate delivery time estimation</name>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_38</id>\n        <impact>1</impact>\n        <name>Predictive maintenance</name>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_39</id>\n        <impact>1</impact>\n        <name>Inspection support using the digital tool</name>\n        <nodesIdFrom>N_34</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_40</id>\n        <impact>1</impact>\n        <linkTo>Mobile APP</linkTo>\n        <name>Mould maintenance tool (web portal)</name>\n        <nodesIdFrom>N_35</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>Existing Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_42</id>\n        <impact>1</impact>\n        <name>Delivery time estimation tool</name>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_43</id>\n        <impact>1</impact>\n        <name>Mould maintenance tool (web portal)</name>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_44</id>\n        <impact>1</impact>\n        <name>Maintenance project manager (engineer</name>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_45</id>\n        <impact>1</impact>\n        <linkTo>Employee in foreign countries</linkTo>\n        <name>Shop floor operator;</name>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>Existing Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n</treeMapStructure>\n',34,3),(27,'2017-03-31 00:25:10','test Carlo','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>4</id>\n    <features>\n        <business>v455v4</business>\n        <industry>vc45v54</industry>\n        <name>3crc33</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>v5454v</turnover>\n    </features>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_0</id>\n        <impact>1</impact>\n        <name>t</name>\n        <type>NEED</type>\n    </needs>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_2</id>\n        <impact>1</impact>\n        <name>asdasd</name>\n        <type>NEED</type>\n    </needs>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_3</id>\n        <impact>1</impact>\n        <name>asdasdsasdasdas</name>\n        <type>NEED</type>\n    </needs>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_1</id>\n        <impact>1</impact>\n        <name>s</name>\n        <type>WISH</type>\n    </wishes>\n</treeMapStructure>\n',39,3),(28,'2017-02-17 16:13:25','BAZIGOS PSS PSCT','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>0</id>\n    <features>\n        <business>N. Bazigos SA</business>\n        <industry>Mould Maker</industry>\n        <name>Bazigos</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>3.250.000</turnover>\n    </features>\n</treeMapStructure>\n',35,4),(30,'2017-02-21 16:28:23','CARLOBERTELE Prova 20-02-2017','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>17</id>\n    <features>\n        <business>B2B</business>\n        <industry>humidifier</industry>\n        <name>humiFog</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>--</turnover>\n    </features>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_0</id>\n        <impact>1</impact>\n        <name>Long life cycle</name>\n        <nodesIdTo>N_2</nodesIdTo>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesIdTo>N_4</nodesIdTo>\n        <nodesOutJSFormat>N_2;N_3;N_4;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_1</id>\n        <impact>1</impact>\n        <name>Continuous functioning of the product</name>\n        <nodesIdTo>N_4</nodesIdTo>\n        <nodesOutJSFormat>N_4;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_2</id>\n        <impact>1</impact>\n        <name>Good installation and commisioning</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdTo>N_5</nodesIdTo>\n        <nodesIdTo>N_6</nodesIdTo>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_9</nodesIdTo>\n        <nodesOutJSFormat>N_5;N_6;N_8;N_9;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_3</id>\n        <impact>1</impact>\n        <name>good maintenance</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdTo>N_5</nodesIdTo>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_9</nodesIdTo>\n        <nodesOutJSFormat>N_5;N_8;N_9;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_4</id>\n        <impact>1</impact>\n        <name>ease of use</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdTo>N_9</nodesIdTo>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_15</nodesIdTo>\n        <nodesOutJSFormat>N_9;N_8;N_15;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <solutions>\n        <description></description>\n        <difficulty>2</difficulty>\n        <id>N_5</id>\n        <impact>3</impact>\n        <name>Augmented reality</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdTo>N_10</nodesIdTo>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesOutJSFormat>N_10;N_11;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>3</difficulty>\n        <id>N_6</id>\n        <impact>2</impact>\n        <name>increase employees in foreign countries</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdTo>N_12</nodesIdTo>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesOutJSFormat>N_12;N_11;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>2</difficulty>\n        <id>N_7</id>\n        <impact>2</impact>\n        <name>Better knowledge of the customer</name>\n        <nodesIdTo>N_12</nodesIdTo>\n        <nodesIdTo>N_14</nodesIdTo>\n        <nodesOutJSFormat>N_12;N_14;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_8</id>\n        <impact>2</impact>\n        <name>easy manual</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdTo>N_12</nodesIdTo>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesOutJSFormat>N_12;N_11;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>5</difficulty>\n        <id>N_9</id>\n        <impact>3</impact>\n        <name>poka-yoke in the product</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdTo>N_12</nodesIdTo>\n        <nodesIdTo>N_14</nodesIdTo>\n        <nodesIdTo>N_13</nodesIdTo>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesOutJSFormat>N_12;N_14;N_13;N_11;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>4</difficulty>\n        <id>N_15</id>\n        <impact>1</impact>\n        <name>Better knowledge of the final customer</name>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdTo>N_16</nodesIdTo>\n        <nodesOutJSFormat>N_16;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_10</id>\n        <impact>1</impact>\n        <name>Mobile APP</name>\n        <nodesIdFrom>N_5</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_11</id>\n        <impact>1</impact>\n        <name>Standardization of service procedure</name>\n        <nodesIdFrom>N_5</nodesIdFrom>\n        <nodesIdFrom>N_6</nodesIdFrom>\n        <nodesIdFrom>N_8</nodesIdFrom>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_12</id>\n        <impact>1</impact>\n        <name>service network</name>\n        <nodesIdFrom>N_6</nodesIdFrom>\n        <nodesIdFrom>N_7</nodesIdFrom>\n        <nodesIdFrom>N_8</nodesIdFrom>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_13</id>\n        <impact>1</impact>\n        <name>Monitoring software</name>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_14</id>\n        <impact>1</impact>\n        <name>Customer care service</name>\n        <nodesIdFrom>N_7</nodesIdFrom>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_16</id>\n        <impact>1</impact>\n        <name>Customer care service</name>\n        <nodesIdFrom>N_15</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n</treeMapStructure>\n',40,3),(31,'2017-06-16 16:28:41','CAREL_Installation_Improvement_PSCT','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>20</id>\n    <features>\n        <business>B2B</business>\n        <industry>humidifiers</industry>\n        <name>humiFog</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>--</turnover>\n    </features>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_0</id>\n        <impact>1</impact>\n        <name>Long life cycle</name>\n        <nodesIdTo>N_2</nodesIdTo>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesIdTo>N_4</nodesIdTo>\n        <nodesOutJSFormat>N_2;N_3;N_4;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_1</id>\n        <impact>1</impact>\n        <name>Continuous functioning of the product</name>\n        <nodesIdTo>N_4</nodesIdTo>\n        <nodesOutJSFormat>N_4;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_2</id>\n        <impact>1</impact>\n        <name>Good installation and commisioning</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdTo>N_5</nodesIdTo>\n        <nodesIdTo>N_7</nodesIdTo>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_9</nodesIdTo>\n        <nodesOutJSFormat>N_5;N_7;N_8;N_9;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_3</id>\n        <impact>1</impact>\n        <name>Good maintenance</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdTo>N_5</nodesIdTo>\n        <nodesIdTo>N_6</nodesIdTo>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_9</nodesIdTo>\n        <nodesIdTo>N_10</nodesIdTo>\n        <nodesOutJSFormat>N_5;N_6;N_8;N_9;N_10;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_4</id>\n        <impact>1</impact>\n        <name>Ease of use</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_9</nodesIdTo>\n        <nodesIdTo>N_18</nodesIdTo>\n        <nodesOutJSFormat>N_8;N_9;N_18;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_5</id>\n        <impact>1</impact>\n        <name>Augmented reality</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesIdTo>N_12</nodesIdTo>\n        <nodesOutJSFormat>N_11;N_12;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>5</difficulty>\n        <id>N_6</id>\n        <impact>4</impact>\n        <name>Increase service in foreign countries</name>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdTo>N_13</nodesIdTo>\n        <nodesIdTo>N_15</nodesIdTo>\n        <nodesOutJSFormat>N_13;N_15;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>4</difficulty>\n        <id>N_7</id>\n        <impact>5</impact>\n        <name>Automatic installation and commissioning</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdTo>N_14</nodesIdTo>\n        <nodesIdTo>N_17</nodesIdTo>\n        <nodesOutJSFormat>N_14;N_17;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_8</id>\n        <impact>2</impact>\n        <name>Easy manual</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdTo>N_11</nodesIdTo>\n        <nodesOutJSFormat>N_11;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>4</difficulty>\n        <id>N_9</id>\n        <impact>5</impact>\n        <name>Poka-Yoke in the product</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdTo>N_16</nodesIdTo>\n        <nodesIdTo>N_17</nodesIdTo>\n        <nodesOutJSFormat>N_16;N_17;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>2</difficulty>\n        <id>N_10</id>\n        <impact>2</impact>\n        <name>Maintenance intervention traceability</name>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdTo>N_17</nodesIdTo>\n        <nodesIdTo>N_15</nodesIdTo>\n        <nodesIdTo>N_14</nodesIdTo>\n        <nodesOutJSFormat>N_17;N_15;N_14;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>4</difficulty>\n        <id>N_18</id>\n        <impact>1</impact>\n        <name>Better knowledge of the final customer</name>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdTo>N_19</nodesIdTo>\n        <nodesIdTo>N_17</nodesIdTo>\n        <nodesOutJSFormat>N_19;N_17;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_11</id>\n        <impact>1</impact>\n        <name>Standardization of service procedure</name>\n        <nodesIdFrom>N_5</nodesIdFrom>\n        <nodesIdFrom>N_8</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_12</id>\n        <impact>1</impact>\n        <name>Mobile APP</name>\n        <nodesIdFrom>N_5</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_13</id>\n        <impact>1</impact>\n        <name>Employee in foreing countries</name>\n        <nodesIdFrom>N_6</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_14</id>\n        <impact>1</impact>\n        <name>Monitoring software</name>\n        <nodesIdFrom>N_7</nodesIdFrom>\n        <nodesIdFrom>N_10</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_15</id>\n        <impact>1</impact>\n        <name>Service network</name>\n        <nodesIdFrom>N_6</nodesIdFrom>\n        <nodesIdFrom>N_10</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_16</id>\n        <impact>1</impact>\n        <name>Increase product features</name>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_17</id>\n        <impact>1</impact>\n        <name>Sensors in the product</name>\n        <nodesIdFrom>N_7</nodesIdFrom>\n        <nodesIdFrom>N_9</nodesIdFrom>\n        <nodesIdFrom>N_10</nodesIdFrom>\n        <nodesIdFrom>N_18</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_19</id>\n        <impact>1</impact>\n        <name>Customer care service</name>\n        <nodesIdFrom>N_18</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n</treeMapStructure>\n',39,3),(34,'2017-02-21 14:56:01','testing_status','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>4</id>\n    <features>\n        <business>ww</business>\n        <industry>wwwww</industry>\n        <name>wwwww</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>wwww</turnover>\n    </features>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_0</id>\n        <impact>1</impact>\n        <name>aa</name>\n        <nodesIdTo>N_1</nodesIdTo>\n        <nodesOutJSFormat>N_1;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_1</id>\n        <impact>1</impact>\n        <name>pipo</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdTo>N_2</nodesIdTo>\n        <nodesOutJSFormat>N_2;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_2</id>\n        <impact>1</impact>\n        <name>ttt</name>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesOutJSFormat>N_3;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_3</id>\n        <impact>1</impact>\n        <linkTo>y</linkTo>\n        <name>capo</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>Existing Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n</treeMapStructure>\n',37,1),(35,'2017-04-26 11:18:42','My DESMA PSCT','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>4</id>\n    <features>\n        <business>shoes</business>\n        <industry>shoes</industry>\n        <name>Adi Adi</name>\n        <possibleCompetitors></possibleCompetitors>\n        <productSubstitutes></productSubstitutes>\n        <turnover>shoes</turnover>\n    </features>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_0</id>\n        <impact>1</impact>\n        <name>need 1</name>\n        <nodesIdTo>N_1</nodesIdTo>\n        <nodesOutJSFormat>N_1;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_1</id>\n        <impact>1</impact>\n        <name>wish 1</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdTo>N_2</nodesIdTo>\n        <nodesOutJSFormat>N_2;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <solutions>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_2</id>\n        <impact>1</impact>\n        <name>solution 1</name>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesOutJSFormat>N_3;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_3</id>\n        <impact>1</impact>\n        <linkTo>Airflow Supersneaker</linkTo>\n        <name>resource 1</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <resourceType>Existing Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n</treeMapStructure>\n',43,2),(36,'2017-05-24 10:35:21','Test_24-05_Morning','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<treeMapStructure>\n    <id>9</id>\n    <features>\n        <business>Test</business>\n        <industry>Test</industry>\n        <name>Test</name>\n        <possibleCompetitors>Test</possibleCompetitors>\n        <productSubstitutes>Test</productSubstitutes>\n        <turnover>Test</turnover>\n    </features>\n    <needs>\n        <description>Test</description>\n        <difficulty>1</difficulty>\n        <id>N_0</id>\n        <impact>1</impact>\n        <name>Test</name>\n        <nodesIdTo>N_2</nodesIdTo>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesIdTo>N_4</nodesIdTo>\n        <nodesOutJSFormat>N_2;N_3;N_4;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <needs>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_1</id>\n        <impact>1</impact>\n        <name>Test1</name>\n        <nodesIdTo>N_3</nodesIdTo>\n        <nodesIdTo>N_4</nodesIdTo>\n        <nodesOutJSFormat>N_3;N_4;</nodesOutJSFormat>\n        <type>NEED</type>\n    </needs>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_2</id>\n        <impact>1</impact>\n        <name>Test2</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdTo>N_5</nodesIdTo>\n        <nodesOutJSFormat>N_5;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_3</id>\n        <impact>1</impact>\n        <name>Test3</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdTo>N_6</nodesIdTo>\n        <nodesOutJSFormat>N_6;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <wishes>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_4</id>\n        <impact>1</impact>\n        <name>Test4</name>\n        <nodesIdFrom>N_0</nodesIdFrom>\n        <nodesIdFrom>N_1</nodesIdFrom>\n        <nodesIdTo>N_6</nodesIdTo>\n        <nodesOutJSFormat>N_6;</nodesOutJSFormat>\n        <type>WISH</type>\n    </wishes>\n    <solutions>\n        <description></description>\n        <difficulty>2</difficulty>\n        <id>N_5</id>\n        <impact>3</impact>\n        <name>Test5</name>\n        <nodesIdFrom>N_2</nodesIdFrom>\n        <nodesIdTo>N_8</nodesIdTo>\n        <nodesIdTo>N_7</nodesIdTo>\n        <nodesOutJSFormat>N_8;N_7;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <solutions>\n        <description></description>\n        <difficulty>3</difficulty>\n        <id>N_6</id>\n        <impact>3</impact>\n        <name>Test6</name>\n        <nodesIdFrom>N_3</nodesIdFrom>\n        <nodesIdFrom>N_4</nodesIdFrom>\n        <nodesIdTo>N_7</nodesIdTo>\n        <nodesOutJSFormat>N_7;</nodesOutJSFormat>\n        <type>SOLUTION</type>\n    </solutions>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_7</id>\n        <impact>1</impact>\n        <name>Test7</name>\n        <nodesIdFrom>N_5</nodesIdFrom>\n        <nodesIdFrom>N_6</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <parentResourceName>Mobile APP</parentResourceName>\n        <resourceType>New Product</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n    <resources>\n        <description></description>\n        <difficulty>1</difficulty>\n        <id>N_8</id>\n        <impact>1</impact>\n        <name>Test8</name>\n        <nodesIdFrom>N_5</nodesIdFrom>\n        <nodesOutJSFormat>none</nodesOutJSFormat>\n        <parentResourceName>Customer care service</parentResourceName>\n        <resourceType>New Service</resourceType>\n        <type>RESOURCE</type>\n    </resources>\n</treeMapStructure>\n',41,3);
/*!40000 ALTER TABLE `tree_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(250) DEFAULT NULL,
  `password` varchar(250) DEFAULT NULL,
  `email` varchar(250) DEFAULT NULL,
  `first_name` varchar(250) DEFAULT NULL,
  `last_name` varchar(250) DEFAULT NULL,
  `user_role_id` int(11) DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uq_user_username` (`username`),
  KEY `ix_user_user_role` (`user_role_id`),
  KEY `fk_user_company` (`company_id`),
  CONSTRAINT `fk_user_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
  CONSTRAINT `fk_user_user_role` FOREIGN KEY (`user_role_id`) REFERENCES `user_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'test','a94a8fe5ccb19ba61c4c0873d391e987982fbbd3','test','test','test',1,1),(2,'desma','b9eb651eba2a19575685e0e8b006cc61bdd7aded','desma','desma','desma',2,1),(3,'carel','612b43627c3800e8933106b68548c29bf0bff3c3','carel','carel','carel',3,2),(4,'bazigos','60994fc577aef03277a16f89c5852404b61fe4be','bazigos','bazigos','bazigos',4,3),(5,'external','1f7f4dbeb34e208110202e97215b8342dc090f3a',NULL,NULL,NULL,5,1),(6,'lars','94e5d4fd09b80a1b1e3cbc9a55bc8739652d0928','lars','lars','leister',6,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_role_id` (`id`),
  UNIQUE KEY `UK_apcc8lxk2xnug8377fatvbn04` (`user_id`),
  KEY `ix_user_role_user` (`user_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'DESIGNER',1),(2,'DESIGNER',2),(3,'DESIGNER',3),(4,'DESIGNER',4),(5,'DESIGNER',5),(6,'ADMIN',6);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wiki`
--

DROP TABLE IF EXISTS `wiki`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wiki` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `local_url` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m2k5nb98lcv1smuvwhvw3w967` (`user_id`),
  KEY `fk_wiki_company` (`company_id`),
  CONSTRAINT `FK_m2k5nb98lcv1smuvwhvw3w967` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_wiki_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wiki`
--

LOCK TABLES `wiki` WRITE;
/*!40000 ALTER TABLE `wiki` DISABLE KEYS */;
INSERT INTO `wiki` VALUES (1,'Wiki for Airflow Supersneaker x-mas edition PSS','http://div_wiki','http://div_wiki',2,1),(2,'Wiki for Airflow Supersneaker Africa colors PSS','http://div_wiki','http://div_wiki',2,1),(3,'Bazigos Wiki','http://opennebula.euprojects.net:8922/diversitywiki','http://192.168.7.249/diversitywiki',4,3),(4,'Carel Wiki','http://opennebula.euprojects.net:8940/diversitywiki','http://192.168.7.250/diversitywiki',3,2),(5,'Desma Wiki 1','http://opennebula.euprojects.net:8914/diversitywiki','http://192.168.7.248/diversitywiki',6,1),(6,'Desma Wiki 2','http://opennebula.euprojects.net:8937/diversitywiki','http://192.168.7.246/diversitywiki',6,1),(7,'General Semantic Wiki','localhost',NULL,6,NULL);
/*!40000 ALTER TABLE `wiki` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'diversity_common_repository'
--

--
-- Dumping routines for database 'diversity_common_repository'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-07-13 16:06:54
