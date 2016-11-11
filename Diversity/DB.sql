-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: sentimentanalysis
-- ------------------------------------------------------
-- Server version	5.7.16-log

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
-- Current Database: `sentimentanalysis`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sentimentanalysis` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `sentimentanalysis`;

--
-- Table structure for table `acess_rights`
--

DROP TABLE IF EXISTS `acess_rights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acess_rights` (
  `role` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `view_opinion_model` bit(1) DEFAULT NULL,
  `create_edit_delete_model` bit(1) DEFAULT NULL,
  `view_opinion_results` bit(1) DEFAULT NULL,
  `save_delete_snapshots` bit(1) DEFAULT NULL,
  `use_opinion_prediction` bit(1) DEFAULT NULL,
  PRIMARY KEY (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acess_rights`
--

LOCK TABLES `acess_rights` WRITE;
/*!40000 ALTER TABLE `acess_rights` DISABLE KEYS */;
INSERT INTO `acess_rights` VALUES ('BUS_PART',NULL,'\0','\0','\0','\0','\0'),('DESIGNER',NULL,'','','','',''),('PROD_MAN',NULL,'\0','\0','','\0','\0'),('SYS_ENG',NULL,'','\0','','\0','\0');
/*!40000 ALTER TABLE `acess_rights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authors`
--

DROP TABLE IF EXISTS `authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authors` (
  `id` varchar(90) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `name` varchar(90) DEFAULT NULL,
  `gender` varchar(45) DEFAULT NULL,
  `location` varchar(45) DEFAULT NULL,
  `influence` double DEFAULT NULL,
  `comments` int(11) DEFAULT NULL,
  `likes` int(11) DEFAULT NULL,
  `views` int(11) DEFAULT NULL,
  `posts` int(11) DEFAULT NULL,
  `source` varchar(45) NOT NULL,
  PRIMARY KEY (`id`,`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authors`
--

LOCK TABLES `authors` WRITE;
/*!40000 ALTER TABLE `authors` DISABLE KEYS */;
/*!40000 ALTER TABLE `authors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `general`
--

DROP TABLE IF EXISTS `general`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `general` (
  `totalposts` int(11) DEFAULT NULL,
  `totallikes` int(11) DEFAULT NULL,
  `totalcomments` int(11) DEFAULT NULL,
  `totalviews` int(11) DEFAULT NULL,
  `lastupdated` date DEFAULT NULL,
  `id` int(11) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `general`
--

LOCK TABLES `general` WRITE;
/*!40000 ALTER TABLE `general` DISABLE KEYS */;
INSERT INTO `general` VALUES (0,0,0,0,'1970-01-01',1,8);
/*!40000 ALTER TABLE `general` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `influences`
--

DROP TABLE IF EXISTS `influences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `influences` (
  `authors_id` varchar(90) DEFAULT NULL,
  `timestamp` date DEFAULT NULL,
  `value` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `influences`
--

LOCK TABLES `influences` WRITE;
/*!40000 ALTER TABLE `influences` DISABLE KEYS */;
/*!40000 ALTER TABLE `influences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `models`
--

DROP TABLE IF EXISTS `models`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `models` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `uri` longtext NOT NULL,
  `pss` varchar(45) NOT NULL,
  `update_frequency` int(11) DEFAULT NULL,
  `archived` tinyint(4) DEFAULT NULL,
  `products` longtext,
  `created_by_user` int(11) DEFAULT NULL,
  `age_range` varchar(45) DEFAULT NULL,
  `gender` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`,`name`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=803 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `models`
--

LOCK TABLES `models` WRITE;
/*!40000 ALTER TABLE `models` DISABLE KEYS */;
/*!40000 ALTER TABLE `models` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opinions`
--

DROP TABLE IF EXISTS `opinions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opinions` (
  `id` int(11) NOT NULL,
  `reach` double DEFAULT NULL,
  `polarity` double DEFAULT NULL,
  `total_inf` double DEFAULT NULL,
  `authors_id` varchar(90) NOT NULL,
  `timestamp` date DEFAULT NULL,
  `pss` varchar(45) NOT NULL,
  `comments` int(11) DEFAULT NULL,
  `product` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`authors_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_opinions_authors_idx` (`authors_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opinions`
--

LOCK TABLES `opinions` WRITE;
/*!40000 ALTER TABLE `opinions` DISABLE KEYS */;
/*!40000 ALTER TABLE `opinions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `posts` (
  `id` int(11) NOT NULL,
  `polarity` double DEFAULT NULL,
  `message` longtext,
  `likes` int(11) DEFAULT NULL,
  `views` varchar(45) DEFAULT NULL,
  `opinions_id` int(11) NOT NULL,
  `authors_id` varchar(90) NOT NULL,
  PRIMARY KEY (`id`,`opinions_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_posts_opinions1_idx` (`opinions_id`,`authors_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reach`
--

DROP TABLE IF EXISTS `reach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reach` (
  `value` longtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reach`
--

LOCK TABLES `reach` WRITE;
/*!40000 ALTER TABLE `reach` DISABLE KEYS */;
/*!40000 ALTER TABLE `reach` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sources`
--

DROP TABLE IF EXISTS `sources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sources` (
  `source` varchar(45) NOT NULL,
  `account` varchar(45) NOT NULL,
  `last_updated` longtext,
  `next_update` longtext,
  `pss` varchar(45) NOT NULL,
  PRIMARY KEY (`source`,`account`,`pss`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sources`
--

LOCK TABLES `sources` WRITE;
/*!40000 ALTER TABLE `sources` DISABLE KEYS */;
/*!40000 ALTER TABLE `sources` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `diversity_common_repository`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `diversity_common_repository` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `diversity_common_repository`;

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
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_company_id` (`id`),
  KEY `fk_company_company_idx` (`belongs_to_company_id`),
  CONSTRAINT `fk_company_company` FOREIGN KEY (`belongs_to_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'Company 1','SUPPLIER',NULL),(2,'Company 2','CUSTOMER',1);
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
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
  `userId` tinyblob,
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
INSERT INTO `consumer` VALUES (1,'Consumer 1',1,NULL),(8,'Consumer 12',1,NULL),(9,'Consumer 343',1,NULL);
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
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_design_project_id` (`id`),
  KEY `ix_design_product_pss` (`produces_pss_id`),
  CONSTRAINT `producesPssId` FOREIGN KEY (`produces_pss_id`) REFERENCES `pss` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `design_project`
--

LOCK TABLES `design_project` WRITE;
/*!40000 ALTER TABLE `design_project` DISABLE KEYS */;
INSERT INTO `design_project` VALUES (1,'test project',NULL,1);
/*!40000 ALTER TABLE `design_project` ENABLE KEYS */;
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
-- Table structure for table `lean_rule`
--

DROP TABLE IF EXISTS `lean_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `rule` varchar(255) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `lean_rule_ability_id` int(11) DEFAULT NULL,
  `applied_to_content_rule_id` int(11) DEFAULT NULL,
  `applied_to_development_rule_id` int(11) DEFAULT NULL,
  `lean_rule_waste_id` int(11) DEFAULT NULL,
  `lean_rule_classification_id` int(11) DEFAULT NULL,
  `lean_rule_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_lean_rule_id` (`id`),
  KEY `ix_lean_rule_user` (`user_id`),
  KEY `ix_lean_rule_ability` (`lean_rule_ability_id`),
  KEY `ix_lean_rule_content_rule` (`applied_to_content_rule_id`),
  KEY `ix_lean_rule_development_rule` (`applied_to_development_rule_id`),
  KEY `ix_lean_rule_lean_rule_waste` (`lean_rule_waste_id`),
  KEY `ix_lean_rule_lean_rule_clasification` (`lean_rule_classification_id`),
  KEY `ix_lean_rule_lean_rule_type` (`lean_rule_type_id`),
  CONSTRAINT `fk_lean_rule_ability` FOREIGN KEY (`lean_rule_ability_id`) REFERENCES `lean_rule_ability` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_content_rule` FOREIGN KEY (`applied_to_content_rule_id`) REFERENCES `content_rule` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_development_rule` FOREIGN KEY (`applied_to_development_rule_id`) REFERENCES `development_rule` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_lean_rule_clasification` FOREIGN KEY (`lean_rule_classification_id`) REFERENCES `lean_rule_classification` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_lean_rule_type` FOREIGN KEY (`lean_rule_type_id`) REFERENCES `lean_rule_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_lean_rule_waste` FOREIGN KEY (`lean_rule_waste_id`) REFERENCES `lean_rule_waste` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
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
-- Table structure for table `lean_rule_product`
--

DROP TABLE IF EXISTS `lean_rule_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lean_rule_product` (
  `lean_rule_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_rule_id`,`product_id`),
  KEY `ix_lean_rule_product_product` (`product_id`),
  CONSTRAINT `fk_lean_rule_product_lean_rule` FOREIGN KEY (`lean_rule_id`) REFERENCES `lean_rule` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_product_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
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
  `lean_rule_service_id` int(11) NOT NULL,
  PRIMARY KEY (`lean_rule_id`,`lean_rule_service_id`),
  KEY `ix_lean_rule_service_service` (`lean_rule_service_id`),
  CONSTRAINT `fk_lean_rule_service_lean_rule` FOREIGN KEY (`lean_rule_id`) REFERENCES `lean_rule` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_lean_rule_service_service` FOREIGN KEY (`lean_rule_service_id`) REFERENCES `service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_product_id` (`id`),
  KEY `ix_product_company` (`supplied_by_company_id`),
  KEY `fk_product_product_idx` (`parent_product_id`),
  CONSTRAINT `fk_product_company` FOREIGN KEY (`supplied_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_product` FOREIGN KEY (`parent_product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Morris Ground 1',NULL,NULL,NULL,1),(2,'Austin Basket',NULL,NULL,NULL,1),(3,'Austin Soccer',NULL,NULL,NULL,1),(4,'Morris Sea 1000',NULL,NULL,NULL,1),(5,'Morris Sea 2099',NULL,NULL,NULL,1),(6,'Morris Wind',NULL,NULL,NULL,1),(7,'Austin Polo',NULL,NULL,NULL,1),(8,'Austin Cricket',NULL,NULL,NULL,1),(9,'Austin XC',NULL,NULL,NULL,1),(10,'Austin Base',NULL,NULL,NULL,1);
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_pss_id` (`id`),
  KEY `ix_pss_company` (`sold_by_company_id`),
  KEY `fk_pss_user_idx` (`user_id`),
  CONSTRAINT `fk_pss_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `pssOwnedBy` FOREIGN KEY (`sold_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss`
--

LOCK TABLES `pss` WRITE;
/*!40000 ALTER TABLE `pss` DISABLE KEYS */;
INSERT INTO `pss` VALUES (1,'PRODUCT',1,NULL,'D522-1 PSS'),(2,'PRODUCT',2,NULL,'D522-2 PSS'),(3,'PRODUCT',1,NULL,'D341-1 PSS'),(4,'PRODUCT',2,NULL,'D231-1 PSS'),(5,'PRODUCT',2,NULL,'D231-2 PSS');
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
  CONSTRAINT `fk_pss_product_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pss_product_pss` FOREIGN KEY (`pss_id`) REFERENCES `pss` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss_has_product`
--

LOCK TABLES `pss_has_product` WRITE;
/*!40000 ALTER TABLE `pss_has_product` DISABLE KEYS */;
INSERT INTO `pss_has_product` VALUES (1,1),(2,2),(2,3),(3,4),(3,5),(3,6),(4,7),(4,8),(5,9),(5,10);
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
  CONSTRAINT `fk_pss_service_pss` FOREIGN KEY (`pss_id`) REFERENCES `pss` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_pss_service_service` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pss_has_service`
--

LOCK TABLES `pss_has_service` WRITE;
/*!40000 ALTER TABLE `pss_has_service` DISABLE KEYS */;
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
/*!40000 ALTER TABLE `pss_produces_product` ENABLE KEYS */;
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_service_id` (`id`),
  KEY `ix_service_company_id` (`provided_by_company_id`),
  CONSTRAINT `fk_service_company` FOREIGN KEY (`provided_by_company_id`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
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
  `user_id` int(11) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `name` varchar(250) DEFAULT NULL,
  `tree_map` blob,
  `design_project_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_tree_map_id` (`id`),
  KEY `ix_tree_map_design_project` (`design_project_id`),
  CONSTRAINT `fk_tree_map_design_project` FOREIGN KEY (`design_project_id`) REFERENCES `design_project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uq_user_username` (`username`),
  KEY `ix_user_user_role` (`user_role_id`),
  CONSTRAINT `fk_user_user_role` FOREIGN KEY (`user_role_id`) REFERENCES `user_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'test','a94a8fe5ccb19ba61c4c0873d391e987982fbbd3','test','test','test',1),(4,NULL,NULL,NULL,NULL,NULL,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'DESIGNER',1);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-11 19:05:13
