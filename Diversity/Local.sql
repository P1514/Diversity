CREATE DATABASE  IF NOT EXISTS `sentimentanalysis` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `sentimentanalysis`;
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
-- Table structure for table `access_rights`
--

DROP TABLE IF EXISTS `access_rights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `access_rights` (
  `role` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `view_opinion_model` tinyint(1) DEFAULT NULL,
  `create_edit_delete_model` tinyint(1) DEFAULT NULL,
  `view_opinion_results` tinyint(1) DEFAULT NULL,
  `save_delete_snapshots` tinyint(1) DEFAULT NULL,
  `use_opinion_prediction` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_rights`
--

LOCK TABLES `access_rights` WRITE;
/*!40000 ALTER TABLE `access_rights` DISABLE KEYS */;
INSERT INTO `access_rights` VALUES ('BUS_PART',NULL,0,0,0,0,0),('DESIGNER',NULL,1,1,1,1,1),('PROD_MAN',NULL,0,0,1,0,0),('SYS_ENG',NULL,1,0,1,0,0);
/*!40000 ALTER TABLE `access_rights` ENABLE KEYS */;
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
INSERT INTO `general` VALUES (0,0,0,0,'1970-01-02',1,10);
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
  `created_date` bigint(45) DEFAULT NULL,
  `next_update` bigint(45) DEFAULT NULL,
  PRIMARY KEY (`id`,`name`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=857 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `models`
--

LOCK TABLES `models` WRITE;
/*!40000 ALTER TABLE `models` DISABLE KEYS */;
INSERT INTO `models` VALUES (838,'D522-1 PSS','Facebook,123;','1',7,0,'1,',1,NULL,NULL,0,1484301475293),(839,'Morris Ground 1','Facebook,123;','1',8,0,'1,',1,NULL,NULL,0,1484301489363),(841,'D522-2 PSS','Facebook,123;','2',14,0,'2,3,',1,NULL,NULL,0,1484301522995),(842,'Austin Basket','Facebook,12345;','2',15,0,'2,',1,NULL,NULL,0,1484301569427),(843,'Austin Soccer','Facebook,123;','2',16,0,'3,',1,NULL,NULL,0,1484301583115),(845,'D341-1 PSS','Facebook,123;','3',14,0,'4,5,6,',1,NULL,NULL,0,1484301611723),(846,'Morris Sea 1000','Facebook,123;','3',16,0,'4,',1,NULL,NULL,0,1484301624795),(847,'Morris Sea 2099','Facebook,123;','3',15,0,'5,',1,NULL,NULL,0,1484301640811),(848,'Morris Wind','Facebook,123;','3',27,0,'6,',1,NULL,NULL,0,1484301686803),(849,'D231-1 PSS','Facebook,123;','4',13,0,'7,8,',1,NULL,NULL,0,1484301700603),(852,'Austin Polo','Facebook,123;','4',17,0,'7,',1,NULL,NULL,0,1484301825434),(853,'Austin Cricket','Facebook,123;','4',23,0,'8,',1,NULL,NULL,0,1484301837315),(854,'D231-2 PSS','Facebook,123;','5',23,0,'9,10,',1,NULL,NULL,0,1484301854939),(855,'Austin XC','Facebook,123;','5',23,0,'9,',1,NULL,NULL,0,1484301867787),(856,'Austin Base','Facebook,123;','5',24,0,'10,',1,NULL,NULL,0,1484301878570);
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
  `timestamp` bigint(45) DEFAULT NULL,
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
  `last_updated` bigint(45) DEFAULT '0',
  `pss` varchar(45) NOT NULL,
  PRIMARY KEY (`source`,`account`,`pss`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sources`
--

LOCK TABLES `sources` WRITE;
/*!40000 ALTER TABLE `sources` DISABLE KEYS */;
INSERT INTO `sources` VALUES ('Facebook','123',0,'1'),('Facebook','123',0,'2'),('Facebook','123',0,'3'),('Facebook','123',0,'4'),('Facebook','123',0,'5'),('Facebook','12345',0,'2'),('Facebook','abcd',0,'1'),('Facebook','teste',0,'1'),('Facebook','teste2',123123,'1'),('Facebook','teste2',0,'3');
/*!40000 ALTER TABLE `sources` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-01-13 10:06:56
