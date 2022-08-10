-- MySQL dump 10.13  Distrib 8.0.24, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cems
-- ------------------------------------------------------
-- Server version	8.0.24

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
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-09 14:06:40
