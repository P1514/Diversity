-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: diversity_common_repository
-- ------------------------------------------------------
-- Server version	5.7.16

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
-- Table structure for table `collaboration_ranking`
--

DROP TABLE IF EXISTS `collaboration_ranking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collaboration_ranking` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `suggestion_id` int(11) DEFAULT NULL,
  `role` varchar(45) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `company` varchar(45) DEFAULT NULL,
  `ranking` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collaboration_ranking`
--

LOCK TABLES `collaboration_ranking` WRITE;
/*!40000 ALTER TABLE `collaboration_ranking` DISABLE KEYS */;
INSERT INTO `collaboration_ranking` VALUES (1,133243243,'DESIGNER','lala','lala','desma',343.22),(2,133243243,'DESIGNER','lolo','lala','desma',343.22),(3,1231233131,'DESIGNER','desma','desma','DESMA',89.77),(4,1231233131,'ADMIN','lars','leister','DESMA',84.51138888888889),(5,1231233131,'DESIGNER','desma','desma','DESMA',89.77);
/*!40000 ALTER TABLE `collaboration_ranking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `type` enum('SUPPLIER','CUSTOMER','PRODUCT VENDOR','SERVICE PROVIDER') DEFAULT NULL,
  `belongs_to_company_id` int(11) DEFAULT NULL,
  `industry` varchar(256) DEFAULT NULL,
  `business` varchar(45) DEFAULT NULL,
  `turnover` double DEFAULT NULL,
  `street` varchar(256) DEFAULT NULL,
  `number` varchar(24) DEFAULT NULL,
  `postal_code` varchar(24) DEFAULT NULL,
  `city` varchar(256) DEFAULT NULL,
  `country` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_company_id` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `fk_company_company_idx` (`belongs_to_company_id`),
  CONSTRAINT `fk_company_company` FOREIGN KEY (`belongs_to_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'sandbox-company','',NULL,NULL,'B2B',12,NULL,NULL,NULL,NULL,NULL),(8,'Desma','PRODUCT VENDOR',NULL,'Shoe production engineering','B2B',12,'Desmastraße','3-5','28832','Achim','Germany'),(9,'Adike','CUSTOMER',NULL,NULL,'B2B',12,NULL,NULL,NULL,NULL,NULL),(10,'Test1','CUSTOMER',1,'test','B2B',12,NULL,NULL,NULL,NULL,NULL),(11,'Test2','CUSTOMER',NULL,'test','B2B',12,NULL,NULL,NULL,NULL,NULL),(12,'Test3','CUSTOMER',1,'test','B2B',2,NULL,NULL,NULL,NULL,NULL),(13,'Test4','CUSTOMER',1,'test','B2B',3,NULL,NULL,NULL,NULL,NULL),(14,'Test5','CUSTOMER',1,'test','B2B',4,NULL,NULL,NULL,NULL,NULL),(15,'Test6','CUSTOMER',10,'test','B2C',3,NULL,NULL,NULL,NULL,NULL),(16,'Test7','CUSTOMER',11,'test','B2C',2,NULL,NULL,NULL,NULL,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_is_costumer_of_design_project`
--

LOCK TABLES `company_is_costumer_of_design_project` WRITE;
/*!40000 ALTER TABLE `company_is_costumer_of_design_project` DISABLE KEYS */;
INSERT INTO `company_is_costumer_of_design_project` VALUES (1,9,2),(2,9,3),(3,10,3),(4,9,4),(5,11,4),(6,9,5),(7,9,6),(8,9,7),(9,12,8),(10,13,9),(11,9,10),(12,14,10),(13,10,11),(14,10,12),(15,15,12),(16,14,13),(17,16,13),(18,16,14),(19,13,15),(20,10,16),(21,11,17),(22,10,18),(23,10,19),(24,11,20),(25,10,21),(26,11,22),(27,11,23),(28,12,24),(29,10,25),(30,11,26),(31,10,27),(32,11,28),(33,10,30),(34,11,33),(35,10,35),(36,12,36),(37,10,37),(38,11,38),(39,12,39),(40,11,40),(41,9,43),(50,1,NULL);
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumer`
--

LOCK TABLES `consumer` WRITE;
/*!40000 ALTER TABLE `consumer` DISABLE KEYS */;
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
  `name` varchar(250) NOT NULL,
  `produces_pss_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `time_created` varchar(250) DEFAULT NULL,
  `wiki_id` int(11) DEFAULT NULL,
  `closed` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `produces_pss_id` (`produces_pss_id`),
  KEY `user_id` (`user_id`),
  KEY `fk_design_project_wiki` (`wiki_id`),
  CONSTRAINT `design_project_ibfk_1` FOREIGN KEY (`produces_pss_id`) REFERENCES `pss` (`id`),
  CONSTRAINT `design_project_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_design_project_wiki` FOREIGN KEY (`wiki_id`) REFERENCES `wiki` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project`
--

LOCK TABLES `design_project` WRITE;
/*!40000 ALTER TABLE `design_project` DISABLE KEYS */;
INSERT INTO `design_project` VALUES (1,'Default',NULL,12,NULL,1,'\0'),(2,'Test Project new',NULL,12,'1503488938923',1,'\0'),(3,'Test2',NULL,12,'1503666395891',1,'\0'),(4,'Test3',NULL,12,'1503667341284',1,'\0'),(5,'Test4',NULL,12,'1503668277226',1,'\0'),(6,'Test5',NULL,12,'1503668428564',1,'\0'),(7,'Test6',NULL,12,'1503669695353',1,'\0'),(8,'Test7',NULL,12,'1503670787422',1,'\0'),(9,'Test8',NULL,12,'1503671939378',1,'\0'),(10,'Test9',NULL,12,'1503671998635',1,'\0'),(11,'Test10',78,10,'1503672054562',1,'\0'),(12,'Testproject',NULL,10,'1503913561398',1,''),(13,'testproject7',NULL,10,'1503913684124',1,'\0'),(14,'New Project',NULL,10,'1504009740498',1,'\0'),(15,'new test project',NULL,10,'1504010114123',2,'\0'),(16,'New Project test2',NULL,10,'1504019830460',1,'\0'),(17,'Design Project',NULL,10,'1504085155570',1,'\0'),(18,'New Design Project',NULL,10,'1504085582956',1,'\0'),(19,'Project10',NULL,10,'1505395646789',2,'\0'),(20,'Project11',NULL,10,'1505396062088',1,'\0'),(21,'Project12',NULL,10,'1505396181924',2,'\0'),(22,'Project13',NULL,10,'1505396297477',2,'\0'),(23,'Project14',NULL,10,'1505396364487',2,'\0'),(24,'Project15',NULL,10,'1505396505131',1,'\0'),(25,'Project16',NULL,10,'1505396624603',1,'\0'),(26,'Project18',NULL,10,'1505396950542',2,'\0'),(27,'HumiFog Project',78,10,'1505399343190',3,''),(28,'Project20',NULL,10,'1505472206620',2,'\0'),(29,'test no company',NULL,10,'1505472331098',1,'\0'),(30,'Project21',NULL,12,'1505472517628',2,'\0'),(31,'Project22',NULL,12,'1505472600889',1,'\0'),(32,'Project24',NULL,12,'1505472773366',NULL,'\0'),(33,'Project25',NULL,12,'1505485001125',2,'\0'),(34,'Project28',NULL,12,'1505485899412',NULL,'\0'),(35,'Project30',NULL,12,'1505486709503',2,'\0'),(36,'Project31',NULL,10,'1505737289259',2,'\0'),(37,'Project32',NULL,12,'1505737503496',2,'\0'),(38,'Project33',NULL,10,'1505737647188',2,'\0'),(39,'Project34',NULL,10,'1505737747727',2,'\0'),(40,'Project35',NULL,10,'1505738032041',1,'\0'),(41,'project_rita',NULL,10,'1506077960306',NULL,'\0'),(42,'a_project',NULL,10,'1506077995276',NULL,'\0'),(43,'project z',NULL,10,'1506417284698',NULL,'\0');
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
  `user_id` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `lat` double DEFAULT NULL,
  `lng` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_design_project_has_steps_1_idx` (`design_project_id`),
  KEY `fk_design_project_has_steps_2_idx` (`step_id`),
  KEY `fk_design_project_has_steps_3_idx` (`user_id`),
  CONSTRAINT `fk_design_project_has_steps_1` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_design_project_has_steps_2` FOREIGN KEY (`step_id`) REFERENCES `step` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_design_project_has_steps_3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_has_steps`
--

LOCK TABLES `design_project_has_steps` WRITE;
/*!40000 ALTER TABLE `design_project_has_steps` DISABLE KEYS */;
INSERT INTO `design_project_has_steps` VALUES (1,'1503658462450',2,3,NULL,0,NULL,NULL),(2,'1503658464190',2,1,NULL,1,NULL,NULL),(3,'1503658466121',2,2,NULL,0,NULL,NULL),(4,'1503658467225',2,4,NULL,0,NULL,NULL),(5,'1503909797116',11,1,NULL,0,NULL,NULL),(6,'1503910080328',11,2,NULL,0,NULL,NULL),(7,'1503910237459',11,5,NULL,0,NULL,NULL),(12,'1504175897350',17,1,NULL,1,40.4276,18.2437),(13,'1504533406',1,4,NULL,0,NULL,NULL),(15,'1504549186248',12,1,NULL,0,NULL,NULL),(16,'1504549332642',12,3,NULL,0,NULL,NULL),(17,'1504549728077',13,1,NULL,1,NULL,NULL),(18,'1504553376056',15,1,NULL,0,NULL,NULL),(19,'1504553381541',15,2,NULL,0,NULL,NULL),(20,'1504553384623',15,3,NULL,0,NULL,NULL),(21,'1504553386872',15,4,NULL,0,NULL,NULL),(22,'1504553390330',15,5,NULL,0,NULL,NULL),(25,'1504686996427',13,5,NULL,0,NULL,NULL),(37,'1504713728242',18,2,NULL,0,NULL,NULL),(38,'1504713730434',18,1,NULL,0,NULL,NULL),(39,'1504795800867',12,5,NULL,0,NULL,NULL),(43,'1505119339278',14,3,NULL,0,NULL,NULL),(44,'1505123560693',16,1,NULL,0,NULL,NULL),(45,'1505123576785',18,3,NULL,0,NULL,NULL),(46,'1505124270997',13,4,NULL,0,NULL,NULL),(47,'1505124648429',4,1,NULL,0,NULL,NULL),(48,'1505124746253',3,1,NULL,0,NULL,NULL),(49,'1505381530368',12,2,NULL,0,NULL,NULL),(50,'1505399953437',17,4,NULL,0,NULL,NULL),(51,'1505805400256',19,1,NULL,1,NULL,NULL),(52,'1505895948136',24,1,NULL,0,NULL,NULL),(53,'1506072542099',17,3,NULL,0,NULL,NULL),(54,'1506076075310',27,1,NULL,0,NULL,NULL),(55,'1506097963780',12,4,NULL,0,NULL,NULL);
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
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_design_project_has_users_1_idx` (`user_id`),
  KEY `fk_design_project_has_users_2_idx` (`design_project_id`),
  KEY `fk_design_project_has_users_3_idx` (`role_id`),
  CONSTRAINT `fk_design_project_has_users_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_design_project_has_users_2` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_design_project_has_users_3` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_has_users`
--

LOCK TABLES `design_project_has_users` WRITE;
/*!40000 ALTER TABLE `design_project_has_users` DISABLE KEYS */;
INSERT INTO `design_project_has_users` VALUES (1,12,2,2),(3,12,3,2),(4,12,4,2),(5,12,6,2),(6,12,7,2),(8,12,9,2),(9,12,10,2),(10,12,11,2),(12,10,13,2),(13,10,14,2),(14,10,15,2),(16,10,16,2),(19,10,17,2),(43,10,18,2),(44,10,19,2),(45,12,19,3),(46,10,20,2),(47,12,20,7),(49,10,21,2),(50,12,22,4),(51,10,22,2),(52,10,23,2),(53,12,23,5),(54,10,24,2),(55,12,24,7),(56,10,25,2),(57,12,25,5),(58,10,26,2),(59,10,27,2),(60,10,28,2),(63,12,35,2),(64,13,21,6),(67,13,36,3),(68,12,37,2),(69,12,37,2),(70,12,38,2),(71,10,39,2),(72,13,39,7),(73,10,40,2),(74,13,40,6),(88,10,41,2),(89,10,42,2),(90,10,43,2);
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
/*!40000 ALTER TABLE `design_project_lean_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `design_project_product`
--

DROP TABLE IF EXISTS `design_project_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `foreign_design_idx` (`project_id`),
  KEY `foreign_product_idx` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_product`
--

LOCK TABLES `design_project_product` WRITE;
/*!40000 ALTER TABLE `design_project_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `design_project_product` ENABLE KEYS */;
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
-- Table structure for table `design_project_service`
--

DROP TABLE IF EXISTS `design_project_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `design_project_service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL,
  `service_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `foreign_project_idx` (`project_id`),
  KEY `foreign_service_idx` (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project_service`
--

LOCK TABLES `design_project_service` WRITE;
/*!40000 ALTER TABLE `design_project_service` DISABLE KEYS */;
/*!40000 ALTER TABLE `design_project_service` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,8,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `indexer`
--

LOCK TABLES `indexer` WRITE;
/*!40000 ALTER TABLE `indexer` DISABLE KEYS */;
INSERT INTO `indexer` VALUES (1,'Default Indexer','indexer','indexer',2,1);
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
-- Table structure for table `kpi_snapshot`
--

DROP TABLE IF EXISTS `kpi_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kpi_snapshot` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `url` varchar(45) DEFAULT NULL,
  `design_project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `foreign_design_project_idx` (`design_project_id`),
  CONSTRAINT `foreign_design_project` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kpi_snapshot`
--

LOCK TABLES `kpi_snapshot` WRITE;
/*!40000 ALTER TABLE `kpi_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `kpi_snapshot` ENABLE KEYS */;
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
-- Table structure for table `plm`
--

DROP TABLE IF EXISTS `plm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `plm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `system` varchar(45) DEFAULT NULL,
  `url` varchar(45) DEFAULT NULL,
  `db` varchar(45) DEFAULT NULL,
  `user` varchar(250) DEFAULT NULL,
  `password` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plm`
--

LOCK TABLES `plm` WRITE;
/*!40000 ALTER TABLE `plm` DISABLE KEYS */;
INSERT INTO `plm` VALUES (8,'Aras','http://10.80.0.241/InnovatorServer','CARELSolutions','root','innovator');
/*!40000 ALTER TABLE `plm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `type` varchar(45) DEFAULT NULL,
  `parent_product_id` int(11) DEFAULT NULL,
  `supplied_by_company_id` int(11) DEFAULT NULL,
  `is_final_product` tinyint(1) NOT NULL,
  `part_number` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_product_id` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `ix_product_company` (`supplied_by_company_id`),
  KEY `fk_product_product_idx` (`parent_product_id`),
  CONSTRAINT `fk_product_company` FOREIGN KEY (`supplied_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_product` FOREIGN KEY (`parent_product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'test','test',NULL,10,0,NULL,NULL),(2,'test2','new',1,13,0,NULL,NULL),(10,'test3','new',2,12,0,NULL,NULL),(63,'H Line',NULL,NULL,NULL,0,NULL,NULL),(64,'L Line',NULL,NULL,NULL,0,NULL,NULL),(65,'H5',NULL,63,NULL,0,NULL,NULL),(66,'H4',NULL,63,NULL,0,NULL,NULL),(67,'H3',NULL,63,NULL,0,NULL,NULL),(68,'L5',NULL,64,NULL,0,NULL,NULL),(69,'L4',NULL,64,NULL,0,NULL,NULL),(70,'L3',NULL,64,NULL,0,NULL,NULL),(71,'H5+',NULL,65,NULL,1,NULL,NULL),(72,'H50',NULL,65,NULL,1,NULL,NULL),(73,'H4+',NULL,66,NULL,1,NULL,NULL),(74,'H40',NULL,66,NULL,1,NULL,NULL),(75,'H30',NULL,67,NULL,1,NULL,NULL),(76,'L50',NULL,68,NULL,1,NULL,NULL),(77,'L5-',NULL,68,NULL,1,NULL,NULL),(78,'L40',NULL,69,NULL,1,NULL,NULL),(79,'L4-',NULL,69,NULL,1,NULL,NULL),(80,'L30',NULL,70,NULL,1,NULL,NULL),(81,'L3-',NULL,70,NULL,1,NULL,NULL),(97,'Product1','',NULL,1,1,NULL,NULL),(98,'Nike AirForce',NULL,NULL,NULL,1,NULL,NULL),(99,'R1',NULL,NULL,NULL,0,NULL,'');
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
  `name` varchar(45) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_pss_id` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `ix_pss_company` (`sold_by_company_id`),
  KEY `fk_pss_user_idx` (`user_id`),
  CONSTRAINT `fk_pss_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `pssOwnedBy` FOREIGN KEY (`sold_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss`
--

LOCK TABLES `pss` WRITE;
/*!40000 ALTER TABLE `pss` DISABLE KEYS */;
INSERT INTO `pss` VALUES (1,'PRODUCT',NULL,10,'default',NULL),(67,'PRODUCT',NULL,10,'PSS-58',NULL),(68,'PRODUCT',NULL,10,'PSS-57',NULL),(69,'PRODUCT',NULL,10,'PSS-56',NULL),(70,'PRODUCT',NULL,10,'PSS-55',NULL),(71,'PRODUCT',NULL,10,'PSS-44',NULL),(72,'PRODUCT',NULL,10,'PSS-43',NULL),(73,'PRODUCT',NULL,10,'PSS-42',NULL),(74,'PRODUCT',NULL,10,'PSS-41',NULL),(75,'PRODUCT',NULL,10,'PSS-33',NULL),(76,'PRODUCT',NULL,10,'PSS-32',NULL),(77,'PRODUCT',NULL,10,'PSS-31',NULL),(78,'PRODUCT',NULL,10,'HumiFog',NULL),(79,'PRODUCT',NULL,10,'PSS NAF',NULL),(85,'PRODUCT',12,2,'Test pss2',NULL),(90,'PRODUCT',9,2,'Test PSS Corel',NULL),(91,'PRODUCT',8,2,'pss test',NULL);
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
INSERT INTO `pss_has_product` VALUES (90,1),(91,1),(90,2),(85,10),(67,71),(68,72),(69,73),(70,74),(71,75),(72,76),(73,77),(74,78),(75,79),(76,80),(77,81),(78,97),(79,98);
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
INSERT INTO `pss_has_service` VALUES (67,66),(71,66),(91,66),(85,67),(90,67),(67,68),(68,68),(69,68),(70,68),(71,68),(72,68),(73,68),(74,68),(75,68),(76,68),(77,68),(90,68),(67,69),(68,69),(71,69),(72,69),(75,69),(69,70),(70,70),(73,70),(74,70),(76,70);
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
INSERT INTO `pss_produces_product` VALUES (90,1),(90,2),(91,2),(85,10),(90,10);
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
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saved_searches`
--

LOCK TABLES `saved_searches` WRITE;
/*!40000 ALTER TABLE `saved_searches` DISABLE KEYS */;
INSERT INTO `saved_searches` VALUES (1,'Shoe','shoe',0),(2,'Shoebidoo','shoebidoo',0),(6,'hgfhfghfghgfg','shoe',0),(5,'fdfdfd','shoe',0);
/*!40000 ALTER TABLE `saved_searches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `search_snapshot`
--

DROP TABLE IF EXISTS `search_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_snapshot` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `search_string` varchar(45) DEFAULT NULL,
  `design_project_id` int(11) DEFAULT NULL,
  `activity` varchar(45) DEFAULT NULL,
  `role` varchar(45) DEFAULT NULL,
  `location` varchar(45) DEFAULT NULL,
  `pss` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `foreign_design_project_idx` (`design_project_id`),
  CONSTRAINT `design_project` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_snapshot`
--

LOCK TABLES `search_snapshot` WRITE;
/*!40000 ALTER TABLE `search_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `search_snapshot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sentiment_snapshot`
--

DROP TABLE IF EXISTS `sentiment_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sentiment_snapshot` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `url` varchar(45) DEFAULT NULL,
  `design_project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `foreign_design_project_idx` (`design_project_id`),
  CONSTRAINT `designproject` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sentiment_snapshot`
--

LOCK TABLES `sentiment_snapshot` WRITE;
/*!40000 ALTER TABLE `sentiment_snapshot` DISABLE KEYS */;
INSERT INTO `sentiment_snapshot` VALUES (1,'sentiment 1','http://wiki.',12);
/*!40000 ALTER TABLE `sentiment_snapshot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `provided_by_company_id` int(11) DEFAULT NULL,
  `parent_service_id` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_service_id` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `ix_service_company_id` (`provided_by_company_id`),
  KEY `fk_service_service` (`parent_service_id`),
  CONSTRAINT `fk_service_company` FOREIGN KEY (`provided_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_service_service` FOREIGN KEY (`parent_service_id`) REFERENCES `service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (66,'Assistant+',NULL,NULL,NULL),(67,'Helpdesk',NULL,NULL,NULL),(68,'AppStore',NULL,NULL,NULL),(69,'HelpCall',NULL,67,NULL),(70,'HelpMsg',NULL,67,NULL);
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution`
--

LOCK TABLES `solution` WRITE;
/*!40000 ALTER TABLE `solution` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `step`
--

LOCK TABLES `step` WRITE;
/*!40000 ALTER TABLE `step` DISABLE KEYS */;
INSERT INTO `step` VALUES (1,'Design Concept'),(2,'Associate Design Rules'),(3,'Design PSS'),(4,'Design Service'),(5,'Validate PSS Design'),(6,'Model KPI'),(7,'Model Sentiment');
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
  `possible_competitors` varchar(255) DEFAULT NULL,
  `product_substitutes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tree_map_id` (`id`),
  KEY `design_project_id` (`design_project_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `tree_map_ibfk_1` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`),
  CONSTRAINT `tree_map_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tree_map`
--

LOCK TABLES `tree_map` WRITE;
/*!40000 ALTER TABLE `tree_map` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2,'admin','d033e22ae348aeb5660fc2140aec35850c4da997','admin','admin','admin',8,1),(10,'test','92bf6a0f30f75a98d786a12ad789b1e4c07bc792','test','test','test',2,1),(12,'test2','a94a8fe5ccb19ba61c4c0873d391e987982fbbd3','','Test2','',2,14),(13,'test3','a94a8fe5ccb19ba61c4c0873d391e987982fbbd3','','test3','',1,12);
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'DESIGNER',NULL),(2,'PLM System Engineer',NULL),(3,'PLM Configuration Manager',NULL),(4,'Product Manufacturer',NULL),(5,'Business Costumer',NULL),(6,'Business Partner',NULL),(7,'Knowledge Engineer',NULL),(8,'ADMIN',NULL);
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
  `name` varchar(255) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `local_url` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m2k5nb98lcv1smuvwhvw3w967` (`user_id`),
  KEY `fk_wiki_company` (`company_id`),
  CONSTRAINT `FK_m2k5nb98lcv1smuvwhvw3w967` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_wiki_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wiki`
--

LOCK TABLES `wiki` WRITE;
/*!40000 ALTER TABLE `wiki` DISABLE KEYS */;
INSERT INTO `wiki` VALUES (1,'Default Wiki','http://wiki','http://wiki',2,1),(2,'Carel Wiki','http://opennebula.euprojects.net:8940/diversitywiki/index.php/Main_Page','http://opennebula.euprojects.net:8940/diversitywiki/index.php/Main_Page',10,1),(3,'Carel Wiki 2','http://opennebula.euprojects.net/intelligent-search/pages/search.php','http://opennebula.euprojects.net/intelligent-search/pages/search.php',10,1);
/*!40000 ALTER TABLE `wiki` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-09-27 14:44:25
