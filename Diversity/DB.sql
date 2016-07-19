CREATE DATABASE  IF NOT EXISTS `diversitydb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `diversitydb`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: diversitydb
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
-- Table structure for table `authors`
--

DROP TABLE IF EXISTS `authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authors` (
  `id` int(11) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `name` varchar(90) DEFAULT NULL,
  `gender` varchar(45) DEFAULT NULL,
  `location` varchar(45) DEFAULT NULL,
  `influence` double DEFAULT NULL,
  `comments` int(11) DEFAULT NULL,
  `likes` int(11) DEFAULT NULL,
  `views` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
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
  `lastupdated` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `general`
--

LOCK TABLES `general` WRITE;
/*!40000 ALTER TABLE `general` DISABLE KEYS */;
/*!40000 ALTER TABLE `general` ENABLE KEYS */;
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
  `authors_id` int(11) NOT NULL,
  `timestamp` date DEFAULT NULL,
  `tag_id` varchar(45) NOT NULL,
  `comments` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`authors_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_opinions_authors_idx` (`authors_id`),
  CONSTRAINT `fk_opinions_authors` FOREIGN KEY (`authors_id`) REFERENCES `authors` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
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
  `opinions_id` int(11) NOT NULL,
  `authors_id` int(11) NOT NULL,
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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-07-19 18:13:59
