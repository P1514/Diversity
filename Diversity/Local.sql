CREATE DATABASE  IF NOT EXISTS `sentimentanalysis` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `sentimentanalysis`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: sentimentanalysis
-- ------------------------------------------------------
-- Server version	5.7.17-log

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
INSERT INTO `access_rights` VALUES ('BUS_PART',NULL,0,0,0,0,0,0),('DESIGNER',NULL,1,1,1,1,1,0),('DEVELOPER',NULL,1,1,1,1,1,1),('PROD_MAN',NULL,0,0,1,0,0,0),('SYS_ENG',NULL,1,0,1,0,0,0);
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
INSERT INTO `general` VALUES (0,0,0,0,'1970-01-01',1,10);
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
  `timestamp` date NOT NULL,
  `log` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
INSERT INTO `logs` VALUES (1,1,'2017-03-29','Mar 29, 2017 2:28:55 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(2,1,'2017-03-29','Mar 29, 2017 2:28:55 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(3,1,'2017-03-29','Mar 29, 2017 2:28:55 PM general.Startup contextInitialized\r\nINFO: Starting up!\r\n'),(4,1,'2017-03-29','Mar 29, 2017 2:28:55 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(5,1,'2017-03-29','Mar 29, 2017 2:28:55 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(6,1,'2017-03-29','Mar 29, 2017 2:28:55 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(7,1,'2017-03-29','Mar 29, 2017 2:28:55 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(8,1,'2017-03-29','Mar 29, 2017 2:28:55 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(9,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader loadroles\r\nSEVERE: ERROR LOADING ROLES\r\njava.sql.SQLException: Column \'admin\' not found.\r\n	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:963)\r\n	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:896)\r\n	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:885)\r\n	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:860)\r\n	at com.mysql.jdbc.ResultSetImpl.findColumn(ResultSetImpl.java:1077)\r\n	at com.mysql.jdbc.ResultSetImpl.getBoolean(ResultSetImpl.java:1631)\r\n	at general.Loader.loadroles(Loader.java:582)\r\n	at general.Loader.loadp1(Loader.java:79)\r\n	at general.Loader.load(Loader.java:50)\r\n	at monitoring.Oversight.run(Oversight.java:199)\r\n	at general.Startup.contextInitialized(Startup.java:46)\r\n	at org.apache.catalina.core.StandardContext.listenerStart(StandardContext.java:4727)\r\n	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5189)\r\n	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:150)\r\n	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1419)\r\n	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1409)\r\n	at java.util.concurrent.FutureTask.run(Unknown Source)\r\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)\r\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)\r\n	at java.lang.Thread.run(Unknown Source)\r\n\r\n'),(10,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader loadGeneral\r\nINFO:  Variable Init 191118183115812\r\n'),(11,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(12,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader loadmodels\r\nSEVERE: Error on SQL Input\r\njava.sql.SQLException: Column \'design_project\' not found.\r\n	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:963)\r\n	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:896)\r\n	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:885)\r\n	at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:860)\r\n	at com.mysql.jdbc.ResultSetImpl.findColumn(ResultSetImpl.java:1077)\r\n	at com.mysql.jdbc.ResultSetImpl.getLong(ResultSetImpl.java:2759)\r\n	at general.Loader.loadmodels(Loader.java:466)\r\n	at general.Loader.loadp1(Loader.java:85)\r\n	at general.Loader.load(Loader.java:50)\r\n	at monitoring.Oversight.run(Oversight.java:199)\r\n	at general.Startup.contextInitialized(Startup.java:46)\r\n	at org.apache.catalina.core.StandardContext.listenerStart(StandardContext.java:4727)\r\n	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5189)\r\n	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:150)\r\n	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1419)\r\n	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1409)\r\n	at java.util.concurrent.FutureTask.run(Unknown Source)\r\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)\r\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)\r\n	at java.lang.Thread.run(Unknown Source)\r\n\r\n'),(13,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(14,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader updatelocal\r\nINFO:  update general 31534147\r\n'),(15,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader loadp2\r\nINFO: Started Waiting to Check NULLS\r\n'),(16,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader evaluatedata\r\nINFO:  calc eval and reach 10221013\r\n'),(17,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader insertauthors\r\nINFO:  insert authors 308543\r\n'),(18,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader insertposts\r\nINFO:  insert opinions and posts 50568\r\n'),(19,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Loader loadtimescalc\r\nSEVERE: Took:148052982 ns to finish processing 0 posts which averages Infinity ns/post\r\n'),(20,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(21,1,'2017-03-29','Mar 29, 2017 2:28:56 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(22,1,'2017-03-29','Mar 29, 2017 2:28:59 PM general.Server runn\r\nINFO: \r\nIN:{\"Role\":\"DESIGNER\",\"Op\":\"getrestrictions\",\"Key\":\"3D43211234DDDFFGGT542\"}\r\n'),(23,1,'2017-03-29','Mar 29, 2017 2:28:59 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(24,1,'2017-03-29','Mar 29, 2017 2:28:59 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(25,1,'2017-03-29','Mar 29, 2017 2:28:59 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(26,1,'2017-03-29','Mar 29, 2017 2:28:59 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n'),(27,1,'2017-03-29','Mar 29, 2017 2:28:59 PM general.Logging create\r\nINFO: Logger initialized\n\n\n\r\n');
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
INSERT INTO `media_wiki` VALUES (1,'wiki1','pss1'),(2,'wiki2','pss2'),(3,'wiki3','pss1');
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
) ENGINE=InnoDB AUTO_INCREMENT=857 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `models`
--

LOCK TABLES `models` WRITE;
/*!40000 ALTER TABLE `models` DISABLE KEYS */;
INSERT INTO `models` VALUES (838,'D522-1 PSS','Facebook,123;','1',7,0,'1,',1,NULL,NULL,0,1484301475293,NULL,0),(839,'Morris Ground 1','Facebook,123;','1',8,0,'1,',1,NULL,NULL,0,1484301489363,NULL,0),(841,'D522-2 PSS','Facebook,123;','2',14,0,'2,3,',1,NULL,NULL,0,1484301522995,NULL,0),(842,'Austin Basket','Facebook,12345;','2',15,0,'2,',1,NULL,NULL,0,1484301569427,NULL,0),(843,'Austin Soccer','Facebook,123;','2',16,0,'3,',1,NULL,NULL,0,1484301583115,NULL,0),(845,'D341-1 PSS','Facebook,123;','3',14,0,'4,5,6,',1,NULL,NULL,0,1484301611723,NULL,0),(846,'Morris Sea 1000','Facebook,123;','3',16,0,'4,',1,NULL,NULL,0,1484301624795,NULL,0),(847,'Morris Sea 2099','Facebook,123;','3',15,0,'5,',1,NULL,NULL,0,1484301640811,NULL,0),(848,'Morris Wind','Facebook,123;','3',27,0,'6,',1,NULL,NULL,0,1484301686803,NULL,0),(849,'D231-1 PSS','Facebook,123;','4',13,0,'7,8,',1,NULL,NULL,0,1484301700603,NULL,0),(852,'Austin Polo','Facebook,123;','4',17,0,'7,',1,NULL,NULL,0,1484301825434,NULL,0),(853,'Austin Cricket','Facebook,123;','4',23,0,'8,',1,NULL,NULL,0,1484301837315,NULL,0),(854,'D231-2 PSS','Facebook,123;','5',23,0,'9,10,',1,NULL,NULL,0,1484301854939,NULL,0),(855,'Austin XC','Facebook,123;','5',23,0,'9,',1,NULL,NULL,0,1484301867787,NULL,0),(856,'Austin Base','Facebook,123;','5',24,0,'10,',1,NULL,NULL,0,1484301878570,NULL,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `snapshots`
--

LOCK TABLES `snapshots` WRITE;
/*!40000 ALTER TABLE `snapshots` DISABLE KEYS */;
INSERT INTO `snapshots` VALUES (1,'asf',126,'sad','as','sad',12,NULL),(2,'asf',126,'sad','as','sad',12,NULL),(3,'asf',126,'sad','as','sad',12,NULL),(4,'asf',126,'sad','as','sad',12,NULL),(5,'asf',126,'sad','as','sad',12,NULL),(6,'testing',1485475200000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"FEB\",\"Value\":76.77499999999999,\"Variance\":6.275000000000006},{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999}]]','prediction',12,NULL),(7,'testing',1485475200000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"FEB\",\"Value\":76.77499999999999,\"Variance\":6.275000000000006},{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999}]]','prediction',12,NULL),(8,'testing',1485475200000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"FEB\",\"Value\":76.77499999999999,\"Variance\":6.275000000000006},{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999}]]','prediction',12,NULL),(9,'testing',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"FEB\",\"Value\":76.77499999999999,\"Variance\":6.275000000000006},{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999}]]','prediction',12,NULL),(10,'testing',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"FEB\",\"Value\":76.77499999999999,\"Variance\":6.275000000000006},{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999}]]','prediction',6,NULL),(11,'testing',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"FEB\",\"Value\":76.77499999999999,\"Variance\":6.275000000000006},{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999}]]','prediction',6,NULL),(12,'testing',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"FEB\",\"Value\":76.77499999999999,\"Variance\":6.275000000000006},{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999}]]','prediction',6,NULL),(13,'testing',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999},{\"Month\":\"FEB\",\"Value\":48.964999999999996,\"Variance\":19.585}]]','prediction',6,NULL),(14,'testing',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999},{\"Month\":\"FEB\",\"Value\":48.964999999999996,\"Variance\":19.585}]]','prediction',6,NULL),(15,'testing',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999},{\"Month\":\"FEB\",\"Value\":48.964999999999996,\"Variance\":19.585}]]','prediction',6,NULL),(16,'blabla',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999},{\"Month\":\"FEB\",\"Value\":48.964999999999996,\"Variance\":19.585}]]','prediction',6,NULL),(17,'blabla',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999},{\"Month\":\"FEB\",\"Value\":48.964999999999996,\"Variance\":19.585}]]','prediction',6,NULL),(18,'abc',1485907200000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"MAR\",\"Value\":67.205,\"Variance\":3.1950000000000074},{\"Month\":\"APR\",\"Value\":57.69,\"Variance\":11.670000000000002},{\"Month\":\"MAY\",\"Value\":54.004999999999995,\"Variance\":14.704999999999998},{\"Month\":\"JUN\",\"Value\":49.949999999999996,\"Variance\":18.470000000000006},{\"Month\":\"JUL\",\"Value\":47.449999999999996,\"Variance\":21.09000000000001},{\"Month\":\"AUG\",\"Value\":48.94499999999999,\"Variance\":19.625},{\"Month\":\"SEP\",\"Value\":50.964999999999996,\"Variance\":17.145000000000003},{\"Month\":\"OCT\",\"Value\":54.654999999999994,\"Variance\":13.785000000000004},{\"Month\":\"NOV\",\"Value\":56.830000000000005,\"Variance\":11.829999999999991},{\"Month\":\"DEC\",\"Value\":56.535000000000004,\"Variance\":12.145000000000003},{\"Month\":\"JAN\",\"Value\":53.68000000000001,\"Variance\":15.219999999999999},{\"Month\":\"FEB\",\"Value\":48.964999999999996,\"Variance\":19.585}]]','prediction',12,NULL),(19,'test',1485561600000,'test','[{\"Op\":\"Error\",\"Message\":\"You\'re not allowed to be here. What were you expecting to find?\"}]','prediction',6,NULL),(20,'test121',1485561600000,'test','[{\"Op\":\"Prediction\"},[{\"Month\":\"APR\",\"Value\":56.11,\"Variance\":2},{\"Month\":\"MAY\",\"Value\":52.285,\"Variance\":2},{\"Month\":\"JUN\",\"Value\":48.394999999999996,\"Variance\":3},{\"Month\":\"JUL\",\"Value\":47.58,\"Variance\":3},{\"Month\":\"AUG\",\"Value\":49.595,\"Variance\":3},{\"Month\":\"SEP\",\"Value\":52.67,\"Variance\":2},{\"Month\":\"OCT\",\"Value\":56.03,\"Variance\":2},{\"Month\":\"NOV\",\"Value\":56.99,\"Variance\":2},{\"Month\":\"DEC\",\"Value\":55.75,\"Variance\":2},{\"Month\":\"JAN\",\"Value\":51.11,\"Variance\":2},{\"Month\":\"FEB\",\"Value\":49.375,\"Variance\":3},{\"Month\":\"MAR\",\"Value\":0,\"Variance\":0}]]','prediction',6,NULL),(21,'francisco',1485561600000,'test','[{\"Op\":\"Error\",\"Message\":\"You\'re not allowed to be here. What were you expecting to find?\"}]','all',6,NULL),(22,'francisco',1485561600000,'test','[{\"Op\":\"Error\",\"Message\":\"You\'re not allowed to be here. What were you expecting to find?\"}]','location',6,NULL),(23,'francisco',1485561600000,'test','[{\"Op\":\"Error\",\"Message\":\"You\'re not allowed to be here. What were you expecting to find?\"}]','gender',6,NULL),(24,'francisco',1485561600000,'test','[{\"Op\":\"Error\",\"Message\":\"You\'re not allowed to be here. What were you expecting to find?\"}]','age',6,NULL),(25,'francisco',1485561600000,'test','[{\"Op\":\"Error\",\"Message\":\"You\'re not allowed to be here. What were you expecting to find?\"}]','product',6,NULL);
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
INSERT INTO `sources` VALUES ('Facebook','123',0,'1'),('Facebook','123',0,'2'),('Facebook','123',0,'3'),('Facebook','123',0,'4'),('Facebook','123',0,'5'),('Facebook','12345',0,'2'),('Facebook','abcd',0,'1'),('Facebook','teste',0,'1'),('Facebook','teste2',123123,'1'),('Facebook','teste2',0,'3');
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

-- Dump completed on 2017-03-29 14:33:36
