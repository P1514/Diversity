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
INSERT INTO `authors` VALUES (1,25,'Ana','FEMALE','Centro',0.7879363636363635,21,10,20),(2,24,'Guilherme','MALE','Centro',0.7879363636363635,21,10,20),(3,21,'Maria','FEMALE','Norte',1.575872727272727,20,10,20),(4,24,'flavio','MALE','Sul',1.575872727272727,20,10,20),(5,20,'tttt','MALE','Centro',1.575872727272727,20,10,20),(6,20,'pedro','MALE','Sul',1.575872727272727,20,10,20);
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
INSERT INTO `opinions` VALUES (1,1.575872727272727,52.848484848484844,26.001899999999996,1,'2016-07-05',''),(7,0.7120636363636363,50,1.575872727272727,3,'2016-07-05',''),(13,0.7120636363636363,50,1.575872727272727,2,'2016-07-05','');
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
  `message` varchar(90) DEFAULT NULL,
  `opinions_id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL,
  PRIMARY KEY (`id`,`opinions_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_posts_opinions1_idx` (`opinions_id`),
  CONSTRAINT `fk_posts_opinions1` FOREIGN KEY (`opinions_id`) REFERENCES `opinions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,50,'folly',1,1),(2,12,'terrible',1,2),(3,84,'amazing',1,3),(4,50,'folly',1,4),(5,50,'folly',1,5),(6,50,'folly',1,6),(7,50,'folly',7,1),(8,50,'folly',1,2),(9,50,'folly',1,3),(10,50,'folly',1,4),(11,50,'folly',1,5),(12,50,'folly',1,6),(13,50,'folly',13,2),(14,50,'folly',1,2),(15,50,'folly',1,3),(16,50,'folly',1,4),(17,50,'folly',1,5),(18,50,'folly',1,6),(19,84,'amazing',1,1),(20,12,'terrible',1,1),(21,84,'amazing',1,1),(22,84,'amazing',1,2);
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

-- Dump completed on 2016-07-12 14:16:22
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: sentimentposts
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
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `MESSAGE` varchar(90) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `likes` int(11) DEFAULT NULL,
  `views` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `post_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (1,'folly','2016-07-05 00:00:00',10,20,1,0),(2,'terrible','2016-07-05 00:00:00',10,20,2,1),(3,'amazing','2016-07-05 00:00:00',10,20,3,1),(4,'folly','2016-07-05 00:00:00',10,20,4,1),(5,'folly','2016-07-05 00:00:00',10,20,5,1),(6,'folly','2016-07-05 00:00:00',10,20,6,1),(7,'folly','2016-07-05 00:00:00',10,20,1,0),(8,'folly','2016-07-05 00:00:00',10,20,2,1),(9,'folly','2016-07-05 00:00:00',10,20,3,1),(10,'folly','2016-07-05 00:00:00',10,20,4,1),(11,'folly','2016-07-05 00:00:00',10,20,5,1),(12,'folly','2016-07-05 00:00:00',10,20,6,1),(13,'folly','2016-07-05 00:00:00',10,20,2,0),(14,'folly','2016-07-05 00:00:00',10,20,2,1),(15,'folly','2016-07-05 00:00:00',10,20,3,1),(16,'folly','2016-07-05 00:00:00',10,20,4,1),(17,'folly','2016-07-05 00:00:00',10,20,5,1),(18,'folly','2016-07-05 00:00:00',10,20,6,1),(19,'amazing','2016-07-11 00:00:00',10,20,1,1),(20,'terrible','2016-07-11 00:00:00',10,20,1,1),(21,'amazing','2016-07-11 00:00:00',10,20,1,1),(22,'amazing','2016-07-11 00:00:00',10,20,2,1);
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` varchar(45) DEFAULT NULL,
  `location` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Ana',25,'FEMALE','Centro'),(2,'Guilherme',24,'MALE','Centro'),(3,'Maria',21,'FEMALE','Norte'),(4,'flavio',24,'MALE','Sul'),(5,'ferreira',20,'MALE','Este'),(6,'pedro',20,'MALE','Sul'),(10,'eder',21,'MALE','casa');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-07-12 14:16:22
