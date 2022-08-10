-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: localhost    Database: cems
-- ------------------------------------------------------
-- Server version	8.0.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `CName` varchar(100) DEFAULT NULL,
  `CourseCode` varchar(2) NOT NULL,
  `SubjectCode` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`CourseCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES ('Algebra','01','01'),('Mishdip','02','01'),('Malam','03','02'),('Matam','04','02');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `done_exam`
--

DROP TABLE IF EXISTS `done_exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `done_exam` (
  `ExamID` varchar(6) NOT NULL,
  `StudentID` varchar(10) NOT NULL,
  `Date` varchar(50) DEFAULT NULL,
  `OriginalDuration` int DEFAULT NULL,
  `ActualDuration` int DEFAULT NULL,
  `isFinishSuccessful` int DEFAULT NULL,
  `Grade` int DEFAULT NULL,
  `SysGrade` int DEFAULT NULL,
  `GradeStatus` int DEFAULT NULL,
  `GradeChangeRationale` varchar(300) DEFAULT NULL,
  `TeacherComments` varchar(300) DEFAULT NULL,
  `EQanswers` varchar(480) DEFAULT NULL,
  `copycatSuspecious` int DEFAULT NULL,
  `FileName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ExamID`,`StudentID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `done_exam`
--

LOCK TABLES `done_exam` WRITE;
/*!40000 ALTER TABLE `done_exam` DISABLE KEYS */;
INSERT INTO `done_exam` VALUES ('010100','111','14-06-2021 14:10:45',90,1,1,60,60,1,NULL,' ','1,3,1',NULL,NULL),('010100','113','14-06-2021 14:13:58',90,0,1,60,60,1,NULL,' ','1,2,1',NULL,NULL),('010200','111','14-06-2021 14:11:23',120,0,1,60,60,1,NULL,' ','1,3,4,1,1',NULL,NULL),('020300','111','14-06-2021 14:11:56',120,0,1,93,93,1,'change grade','change grade','3,1,1,1,1',NULL,NULL),('020400','111','14-06-2021 14:12:14',120,0,1,60,60,1,'yes',' ','3,1,1,1,1',NULL,NULL),('020400','113','14-06-2021 14:14:11',120,0,1,100,100,1,NULL,' ','1,1,1,1,1',NULL,NULL);
/*!40000 ALTER TABLE `done_exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam` (
  `ExamID` varchar(6) NOT NULL,
  `Duration` int DEFAULT NULL,
  `Comments4Teacher` varchar(100) DEFAULT NULL,
  `Comments4Students` varchar(100) DEFAULT NULL,
  `TeacherID` varchar(10) DEFAULT NULL,
  `ExamType` int DEFAULT NULL,
  `FileName` varchar(45) DEFAULT NULL,
  `EditMode` varchar(1) DEFAULT NULL,
  `ExecutionCode` varchar(4) NOT NULL,
  `LockStatus` int unsigned DEFAULT NULL,
  `AlteredByTeacher` int DEFAULT NULL,
  `ExamQuestionIDs` varchar(360) DEFAULT NULL,
  `ExamQuestionScores` varchar(240) DEFAULT NULL,
  `isOngoing` int unsigned DEFAULT NULL,
  `newDur` int DEFAULT NULL,
  `durChangeRationale` varchar(300) DEFAULT NULL,
  `newDurOK` int DEFAULT NULL,
  PRIMARY KEY (`ExamID`,`ExecutionCode`),
  UNIQUE KEY `ExecutionCode_UNIQUE` (`ExecutionCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam`
--

LOCK TABLES `exam` WRITE;
/*!40000 ALTER TABLE `exam` DISABLE KEYS */;
INSERT INTO `exam` VALUES ('010100',90,'tea ','stu ','121',1,NULL,NULL,'E36X',0,NULL,'01000,01003,01004','20,40,40',0,NULL,NULL,0),('010101',120,'comments for teachers ','comments for students ','121',1,NULL,NULL,'N1os',NULL,NULL,'01000,01001,01002,01003,01004','20,20,20,20,20',0,NULL,NULL,NULL),('010102',120,NULL,NULL,'121',0,'window flow.docx',NULL,'mv3g',0,NULL,NULL,NULL,0,NULL,NULL,NULL),('010200',120,'tea ','st ','121',1,NULL,NULL,'b0q6',0,NULL,'01000,01001,01002,01006,01005','20,20,20,20,20',0,NULL,NULL,0),('010201',90,'teachers ','students ','121',1,NULL,NULL,'Q17V',NULL,NULL,'01000,01005,01006','20,40,40',0,NULL,NULL,NULL),('010202',90,NULL,NULL,'121',0,'MyServerLoginTest - Test Case.docx',NULL,'zQ0T',0,NULL,NULL,NULL,0,NULL,NULL,NULL),('020300',120,'tea ','stu ','121',1,NULL,NULL,'Lucn',0,NULL,'02000,02001,02006,02004,02005','20,20,20,20,20',0,NULL,NULL,0),('020301',90,' ',' ','121',1,NULL,NULL,'hM92',NULL,NULL,'02000,02001,02004','40,40,20',0,NULL,NULL,NULL),('020302',120,NULL,NULL,'121',0,'G6_Answers.Ass.2.docx',NULL,'COCf',0,NULL,NULL,NULL,0,NULL,NULL,NULL),('020400',120,'tea ','st ','121',1,NULL,NULL,'hPpX',0,NULL,'02002,02004,02003,02005,02006','20,20,20,20,20',0,NULL,NULL,0),('020401',90,' ',' ','121',1,NULL,NULL,'t6Io',0,NULL,'02002,02004,02003','40,20,40',0,NULL,NULL,NULL),('020402',120,NULL,NULL,'121',0,'window flow.docx',NULL,'XAmT',0,NULL,NULL,NULL,0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_log`
--

DROP TABLE IF EXISTS `exam_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_log` (
  `ExamID` varchar(6) NOT NULL,
  `numStarted` int DEFAULT NULL,
  `numFinishSuccessful` int DEFAULT NULL,
  `numFinishUnsuccessful` int DEFAULT NULL,
  `median` int DEFAULT NULL,
  `average` int DEFAULT NULL,
  `0_9` int DEFAULT NULL,
  `10_19` int DEFAULT NULL,
  `20_29` int DEFAULT NULL,
  `30_39` int DEFAULT NULL,
  `40_49` int DEFAULT NULL,
  `50_59` int DEFAULT NULL,
  `60_69` int DEFAULT NULL,
  `70_79` int DEFAULT NULL,
  `80_89` int DEFAULT NULL,
  `90_100` int DEFAULT NULL,
  PRIMARY KEY (`ExamID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_log`
--

LOCK TABLES `exam_log` WRITE;
/*!40000 ALTER TABLE `exam_log` DISABLE KEYS */;
INSERT INTO `exam_log` VALUES ('010100',2,2,0,60,60,0,0,0,0,0,0,2,0,0,0),('010200',1,1,0,60,60,0,0,0,0,0,0,1,0,0,0),('020300',1,1,0,93,93,0,0,0,0,0,0,0,0,0,1),('020400',2,2,0,80,80,0,0,0,0,0,0,1,0,0,1);
/*!40000 ALTER TABLE `exam_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `external_data`
--

DROP TABLE IF EXISTS `external_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `external_data` (
  `ID` varchar(10) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Surname` varchar(50) DEFAULT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `Affiliation` varchar(20) DEFAULT NULL,
  `RelevantSubjects` varchar(200) DEFAULT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `external_data`
--

LOCK TABLES `external_data` WRITE;
/*!40000 ALTER TABLE `external_data` DISABLE KEYS */;
INSERT INTO `external_data` VALUES ('﻿100','Avi','Dor','avi@gmail.com','Principal',NULL,'avid','2468'),('111','Moshe','Levi','moshe@gmail.com','Student',NULL,'moshe','1234'),('112','Dafna','Ahron','dafna@gmail.com','Student',NULL,'dafnah','1234'),('113','Motti','Shmueli','motti@gmail.com','Student',NULL,'mottis','1234'),('121','Yuval','Cohen','yuvalc@gmail.com','Teacher','01,02','yuvalc','4321'),('122','Anat','Kobi','anatk@gmail.com','Teacher','01,03','anatk','4321');
/*!40000 ALTER TABLE `external_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person`
--

DROP TABLE IF EXISTS `person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person` (
  `ID` varchar(10) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Surname` varchar(50) DEFAULT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `Affiliation` varchar(20) DEFAULT NULL,
  `Status` int DEFAULT NULL,
  `RelevantSubjects` varchar(200) DEFAULT NULL,
  `ExamBanksID` varchar(200) DEFAULT NULL,
  `QuestionBanksID` varchar(200) DEFAULT NULL,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`,`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES ('﻿100','Avi','Dor','avi@gmail.com','Principal',0,NULL,NULL,NULL,'avid','2468'),('111','Moshe','Levi','moshe@gmail.com','Student',0,NULL,NULL,NULL,'moshe','1234'),('112','Dafna','Ahron','dafna@gmail.com','Student',0,NULL,NULL,NULL,'dafnah','1234'),('113','Motti','Shmueli','motti@gmail.com','Student',0,NULL,NULL,NULL,'mottis','1234'),('121','Yuval','Cohen','yuvalc@gmail.com','Teacher',0,'01,02','01,02','01,02','yuvalc','4321'),('122','Anat','Kobi','anatk@gmail.com','Teacher',0,'01,03',NULL,NULL,'anatk','4321');
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `AuthorID` varchar(50) DEFAULT NULL,
  `QuestionID` varchar(45) NOT NULL,
  `Question` varchar(300) DEFAULT NULL,
  `Answer1` varchar(300) DEFAULT NULL,
  `Answer2` varchar(300) DEFAULT NULL,
  `Answer3` varchar(300) DEFAULT NULL,
  `Answer4` varchar(300) DEFAULT NULL,
  `RightAnswer` int DEFAULT NULL,
  `BankID` varchar(2) DEFAULT NULL,
  `QNum` varchar(3) DEFAULT NULL,
  `CourseIDs` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`QuestionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES ('121','01000','alg and mish #1','1','2','3','4',1,'01','000','01,02'),('121','01001','alg mish #2','1','2','3','4',1,'01','001','01,02'),('121','01002','alg mish #3','1','2','3','4',1,'01','002','01,02'),('121','01003','alg #1','1','2','3','4',1,'01','003','01'),('121','01004','alg #2','1','2','3','4',1,'01','004','01'),('121','01005','mish #1','1','2','3','4',1,'01','005','02'),('121','01006','mish #2','1','2','3','4',1,'01','006','02'),('121','02000','malam #1','1','2','3','4',1,'02','000','03'),('121','02001','malam #2','1','2','3','4',1,'02','001','03'),('121','02002','matam #1','1','2','3','4',1,'02','002','04'),('121','02003','matam #2','1','2','3','4',1,'02','003','04'),('121','02004','L & T #1','1','2','3','4',1,'02','004','03,04'),('121','02005','L & T #2','1','2','3','4',1,'02','005','03,04'),('121','02006','L & T #3','1','2','3','4',1,'02','006','03,04');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistic_data`
--

DROP TABLE IF EXISTS `statistic_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistic_data` (
  `Averege` varchar(3) DEFAULT NULL,
  `Median` varchar(3) DEFAULT NULL,
  `TeacherID` varchar(10) DEFAULT NULL,
  `StudentID` varchar(10) NOT NULL,
  `ExamID` varchar(6) NOT NULL,
  PRIMARY KEY (`ExamID`,`StudentID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistic_data`
--

LOCK TABLES `statistic_data` WRITE;
/*!40000 ALTER TABLE `statistic_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistic_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject` (
  `name` varchar(100) NOT NULL,
  `id` varchar(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES ('Math','01'),('Software','02'),('Art','03');
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test` (
  `ExamId` varchar(10) NOT NULL,
  `Subject` varchar(50) DEFAULT NULL,
  `Course` varchar(50) DEFAULT NULL,
  `Duration` int DEFAULT NULL,
  `Scores` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`ExamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
INSERT INTO `test` VALUES ('123456','math','algebra',45,'55'),('123457','math','algebra',60,'33'),('123656','algo','games',60,'22');
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-14 14:17:34
