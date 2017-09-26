CREATE DATABASE  IF NOT EXISTS `sentimentanalysis` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `sentimentanalysis`;
-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: 127.0.0.1    Database: sentimentanalysis
-- ------------------------------------------------------
-- Server version	5.7.17

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
  `admin` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_rights`
--

LOCK TABLES `access_rights` WRITE;
/*!40000 ALTER TABLE `access_rights` DISABLE KEYS */;
INSERT INTO `access_rights` VALUES ('BUS_PART',NULL,0,0,0,0,0,0),('DESIGNER',NULL,1,1,1,1,1,0),('DEVELOPER',NULL,1,1,1,1,1,1),('PLM%20Configuration%20Manager',NULL,1,1,1,1,1,NULL),('PROD_MAN',NULL,0,0,1,0,0,0),('SYS_ENG',NULL,1,0,1,0,0,0);
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
INSERT INTO `general` VALUES (0,0,0,0,'2017-09-25',1,11);
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
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `log` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_wiki`
--

DROP TABLE IF EXISTS `media_wiki`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `media_wiki` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `pss` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media_wiki`
--

LOCK TABLES `media_wiki` WRITE;
/*!40000 ALTER TABLE `media_wiki` DISABLE KEYS */;
/*!40000 ALTER TABLE `media_wiki` ENABLE KEYS */;
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
  `media_wiki` tinyint(4) DEFAULT NULL,
  `design_project` int(11) NOT NULL,
  PRIMARY KEY (`id`,`name`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=861 DEFAULT CHARSET=utf8;
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
-- Table structure for table `post_source`
--

DROP TABLE IF EXISTS `post_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post_source` (
  `post_id` int(11) NOT NULL,
  `post_source` varchar(20) NOT NULL,
  PRIMARY KEY (`post_id`,`post_source`),
  UNIQUE KEY `post_id_UNIQUE` (`post_id`),
  CONSTRAINT `post_source_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_source`
--

LOCK TABLES `post_source` WRITE;
/*!40000 ALTER TABLE `post_source` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_source` ENABLE KEYS */;
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
-- Table structure for table `snapshots`
--

DROP TABLE IF EXISTS `snapshots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `snapshots` (
  `id` bigint(45) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `creation_date` bigint(45) DEFAULT NULL,
  `creation_user` varchar(45) DEFAULT NULL,
  `result` longtext,
  `type` varchar(45) DEFAULT NULL,
  `timespan` int(11) DEFAULT NULL,
  `model_id` bigint(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `snapshots`
--

LOCK TABLES `snapshots` WRITE;
/*!40000 ALTER TABLE `snapshots` DISABLE KEYS */;
/*!40000 ALTER TABLE `snapshots` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `sources` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tagcloud`
--

DROP TABLE IF EXISTS `tagcloud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tagcloud` (
  `userid` int(11) NOT NULL,
  `modelid` int(11) NOT NULL,
  `ignoredwords` text,
  PRIMARY KEY (`userid`,`modelid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tagcloud`
--

LOCK TABLES `tagcloud` WRITE;
/*!40000 ALTER TABLE `tagcloud` DISABLE KEYS */;
INSERT INTO `tagcloud` VALUES (0,858,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(0,859,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(0,860,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,inspired,'),(1,1,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,2,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,3,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,4,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,5,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,6,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,7,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,8,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,9,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,10,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,11,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,831,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(1,841,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(10,858,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(10,859,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,'),(10,860,'and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,');
/*!40000 ALTER TABLE `tagcloud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sentimentanalysis'
--

--
-- Dumping routines for database 'sentimentanalysis'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-09-26 14:57:34
