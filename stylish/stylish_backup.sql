-- MySQL dump 10.13  Distrib 8.3.0, for macos14.2 (arm64)
--
-- Host: localhost    Database: stylish
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `campaigns`
--

DROP TABLE IF EXISTS `campaigns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campaigns` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `picture` varchar(255) NOT NULL,
  `story` text,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `campaigns_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaigns`
--

LOCK TABLES `campaigns` WRITE;
/*!40000 ALTER TABLE `campaigns` DISABLE KEYS */;
INSERT INTO `campaigns` VALUES (1,1,'/uploads/564e8d7956a640e1.jpg','這個貓咪有那麼大\n真的有那麼大\n不要不相信我有那麼大。\n印象《超級大貓》'),(2,2,'/uploads/24c377f4d516467e.jpg','這個貓咪有那麼長\n真的有夠長\n你沒辦法想像有多長。\n不朽《與自己和解》'),(10,73,'/uploads/811c96b1a5af475e.jpg','你夠酷嗎？\r\n這是一件超級酷的外套\r\n真的酷爛\r\n復古《酷到沒朋友》');
/*!40000 ALTER TABLE `campaigns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotproducts`
--

DROP TABLE IF EXISTS `hotproducts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotproducts` (
  `hot_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`hot_id`,`product_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `hotproducts_ibfk_1` FOREIGN KEY (`hot_id`) REFERENCES `hots` (`id`),
  CONSTRAINT `hotproducts_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotproducts`
--

LOCK TABLES `hotproducts` WRITE;
/*!40000 ALTER TABLE `hotproducts` DISABLE KEYS */;
/*!40000 ALTER TABLE `hotproducts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hots`
--

DROP TABLE IF EXISTS `hots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hots` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hots`
--

LOCK TABLES `hots` WRITE;
/*!40000 ALTER TABLE `hots` DISABLE KEYS */;
/*!40000 ALTER TABLE `hots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderitems`
--

DROP TABLE IF EXISTS `orderitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderitems` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `color_code` varchar(7) NOT NULL,
  `color_name` varchar(50) NOT NULL,
  `size` varchar(10) NOT NULL,
  `quantity` int NOT NULL,
  `stock_at_time` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `orderitems_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `orderitems_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderitems`
--

LOCK TABLES `orderitems` WRITE;
/*!40000 ALTER TABLE `orderitems` DISABLE KEYS */;
INSERT INTO `orderitems` VALUES (4,4,1,'優雅連衣裙',26.00,'#00FF00','綠色','S',1,999),(5,5,2,'時尚外套',61.00,'#000000','黑色','M',1,16),(6,5,1,'優雅連衣裙',26.00,'#00FF00','綠色','S',2,998);
/*!40000 ALTER TABLE `orderitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `shipping` varchar(50) NOT NULL,
  `payment` varchar(50) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `freight` decimal(10,2) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `recipient_name` varchar(255) NOT NULL,
  `recipient_phone` varchar(20) NOT NULL,
  `recipient_email` varchar(255) NOT NULL,
  `recipient_address` text NOT NULL,
  `recipient_time` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('unpaid','paid','cancelled') NOT NULL DEFAULT 'unpaid',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `order_number` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (4,13,'delivery','credit_card',26.00,14.00,40.00,'Luke','0987654321','luke@gmail.com','市政府站','morning','2024-08-01 08:52:51','paid','2024-08-01 08:52:51','2c8bd14f7b3642e6'),(5,13,'delivery','credit_card',113.00,14.00,127.00,'Luke','0987654321','luke@gmail.com','市政府站','morning','2024-08-01 08:56:43','paid','2024-08-01 08:56:44','441f78e0cb8e4e1d');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productcolors`
--

DROP TABLE IF EXISTS `productcolors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productcolors` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `code` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `productcolors_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productcolors`
--

LOCK TABLES `productcolors` WRITE;
/*!40000 ALTER TABLE `productcolors` DISABLE KEYS */;
INSERT INTO `productcolors` VALUES (1,1,'綠色','#00FF00'),(2,1,'黑色','#000000'),(3,1,'藍色','#0000FF'),(4,1,'紅色','#FF0000'),(5,2,'黃色','#FFFF00'),(6,2,'黑色','#000000'),(68,27,'藍色','#0000FF'),(69,27,'綠色','#00FF00'),(70,28,'黃色','#FFFF00'),(71,28,'紅色','#FF0000'),(72,28,'黑色','#000000'),(73,28,'藍色','#0000FF'),(74,28,'綠色','#00FF00'),(75,29,'藍色','#0000FF'),(76,29,'黃色','#FFFF00'),(77,29,'黑色','#000000'),(78,29,'綠色','#00FF00'),(79,30,'綠色','#00FF00'),(80,30,'藍色','#0000FF'),(81,31,'綠色','#00FF00'),(82,31,'紅色','#FF0000'),(83,31,'藍色','#0000FF'),(84,32,'黑色','#000000'),(85,32,'綠色','#00FF00'),(86,32,'藍色','#0000FF'),(87,32,'紅色','#FF0000'),(88,33,'紅色','#FF0000'),(89,33,'藍色','#0000FF'),(90,33,'黃色','#FFFF00'),(91,34,'黃色','#FFFF00'),(92,34,'紅色','#FF0000'),(93,34,'綠色','#00FF00'),(94,35,'黃色','#FFFF00'),(95,35,'黑色','#000000'),(96,35,'綠色','#00FF00'),(97,36,'紅色','#FF0000'),(98,36,'綠色','#00FF00'),(99,36,'黑色','#000000'),(100,37,'綠色','#00FF00'),(101,38,'紅色','#FF0000'),(102,38,'黑色','#000000'),(103,38,'藍色','#0000FF'),(104,39,'紅色','#FF0000'),(105,39,'綠色','#00FF00'),(106,39,'黑色','#000000'),(107,40,'黃色','#FFFF00'),(108,41,'黑色','#000000'),(109,41,'綠色','#00FF00'),(110,41,'黃色','#FFFF00'),(111,41,'藍色','#0000FF'),(112,41,'紅色','#FF0000'),(113,42,'黑色','#000000'),(114,43,'藍色','#0000FF'),(115,44,'黃色','#FFFF00'),(116,44,'紅色','#FF0000'),(117,44,'黑色','#000000'),(118,44,'藍色','#0000FF'),(119,44,'綠色','#00FF00'),(120,45,'藍色','#0000FF'),(121,45,'紅色','#FF0000'),(122,46,'黑色','#000000'),(123,46,'綠色','#00FF00'),(124,46,'黃色','#FFFF00'),(125,47,'紅色','#FF0000'),(126,48,'黑色','#000000'),(127,48,'綠色','#00FF00'),(128,48,'藍色','#0000FF'),(129,48,'黃色','#FFFF00'),(130,48,'紅色','#FF0000'),(131,49,'紅色','#FF0000'),(132,49,'藍色','#0000FF'),(133,49,'黃色','#FFFF00'),(134,50,'黃色','#FFFF00'),(135,51,'紅色','#FF0000'),(136,51,'黑色','#000000'),(137,51,'黃色','#FFFF00'),(138,52,'黑色','#000000'),(139,52,'黃色','#FFFF00'),(140,52,'綠色','#00FF00'),(141,53,'綠色','#00FF00'),(142,53,'紅色','#FF0000'),(143,54,'黃色','#FFFF00'),(144,55,'黃色','#FFFF00'),(145,55,'黑色','#000000'),(146,56,'紅色','#FF0000'),(147,57,'綠色','#00FF00'),(148,57,'黃色','#FFFF00'),(149,57,'紅色','#FF0000'),(150,57,'藍色','#0000FF'),(151,57,'黑色','#000000'),(152,58,'黑色','#000000'),(153,58,'藍色','#0000FF'),(154,58,'黃色','#FFFF00'),(156,60,'藍色','#0000FF'),(157,61,'紅色','#FF0000'),(158,62,'黑色','#000000'),(159,63,'灰色','#808080'),(160,64,'綠色','#00FF00'),(161,65,'黃色','#FFFF00'),(162,66,'藍色','#0000FF'),(163,67,'棕色','#8B4513'),(164,68,'灰色','#808080'),(165,69,'白色','#FFFFFF'),(166,70,'黑色','#000000'),(167,71,'粉紅色','#FFC0CB'),(168,72,'紫色','#800080'),(169,73,'綠色','#00FF00');
/*!40000 ALTER TABLE `productcolors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productimages`
--

DROP TABLE IF EXISTS `productimages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productimages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `image_url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `productimages_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productimages`
--

LOCK TABLES `productimages` WRITE;
/*!40000 ALTER TABLE `productimages` DISABLE KEYS */;
INSERT INTO `productimages` VALUES (1,1,'/uploads/8d4be13772544d2d.jpg'),(2,1,'/uploads/f3b0d70716d54ab9.jpg'),(3,2,'/uploads/ffc8d7d8f715414d.jpg'),(4,2,'/uploads/27845ab6aded46a1.jpg'),(5,2,'/uploads/10bee7c90fc4424e.jpg'),(6,2,'/uploads/a86c6478eab4410c.jpg'),(69,27,'/uploads/4b2168e1d84249e7.jpg'),(70,27,'/uploads/1ba6483a333244ce.jpg'),(71,27,'/uploads/5cfe847747414b8f.jpg'),(72,28,'/uploads/9ed60d608443495d.jpg'),(73,28,'/uploads/cd6ea7d99bc24707.jpg'),(74,29,'/uploads/75fcb4c21a7d4001.jpg'),(75,29,'/uploads/c45e82649e6944a0.jpg'),(76,29,'/uploads/faf9e1435bd24e6a.jpg'),(77,29,'/uploads/500e9151fa3149f7.jpg'),(78,29,'/uploads/2e72bacbb44f4510.jpg'),(79,30,'/uploads/184ba5e94d74416f.jpg'),(80,30,'/uploads/62552107036c4979.jpg'),(81,31,'/uploads/bac8d96cc29e4b96.jpg'),(82,31,'/uploads/86168629486b4d86.jpg'),(83,31,'/uploads/a1b8c95e016d4692.jpg'),(84,32,'/uploads/e6af782f1e464e40.jpg'),(85,32,'/uploads/f676743820f24334.jpg'),(86,32,'/uploads/487aff50608c4a1b.jpg'),(87,32,'/uploads/659805faf0bd47e2.jpg'),(88,33,'/uploads/b54ff6299266416b.jpg'),(89,33,'/uploads/7efc6fba8017432f.jpg'),(90,33,'/uploads/14e95f0b44024e39.jpg'),(91,33,'/uploads/8b79213dface4772.jpg'),(92,34,'/uploads/bcb90138d29446f9.jpg'),(93,35,'/uploads/37d935baec6e4d83.jpg'),(94,35,'/uploads/de705e3940904fb8.jpg'),(95,35,'/uploads/ee5cb5b65f014b5d.jpg'),(96,36,'/uploads/6c7a4cf9c3664beb.jpg'),(97,37,'/uploads/9e7761af77054baa.jpg'),(98,37,'/uploads/b6d9c51d3aad492a.jpg'),(99,37,'/uploads/94047a74d33c4293.jpg'),(100,37,'/uploads/d4b72d9cfeba4575.jpg'),(101,38,'/uploads/2453e74f7854451d.jpg'),(102,39,'/uploads/be0c2ae9ffd24409.jpg'),(103,39,'/uploads/fcb3b5177aa44fa7.jpg'),(104,40,'/uploads/b730a5f5f3db44f5.jpg'),(105,40,'/uploads/a4d5c9213e4c40a7.jpg'),(106,41,'/uploads/2cf1ae3dfa094f0b.jpg'),(107,41,'/uploads/c9659f6e12204496.jpg'),(108,41,'/uploads/4950ce3431ca43c2.jpg'),(109,41,'/uploads/88620d9c1ffc46dd.jpg'),(110,41,'/uploads/1bd53e9146ee499d.jpg'),(111,42,'/uploads/96267a35bbfa4b77.jpg'),(112,42,'/uploads/e5d46bba17664d75.jpg'),(113,42,'/uploads/6819af418d75406f.jpg'),(114,43,'/uploads/ec960dfc11f545df.jpg'),(115,43,'/uploads/d2297bf661c141ef.jpg'),(116,43,'/uploads/445dc25407944afa.jpg'),(117,43,'/uploads/956047a82bfd4532.jpg'),(118,44,'/uploads/b8f75482d19c4a90.jpg'),(119,45,'/uploads/2f1869f4144946e7.jpg'),(120,46,'/uploads/bf1c1a1d6f16472b.jpg'),(121,46,'/uploads/66783c1ff0ff4719.jpg'),(122,46,'/uploads/6e0a53d539784f25.jpg'),(123,46,'/uploads/f0751fbb43dd4163.jpg'),(124,47,'/uploads/1e9cb92978ae4158.jpg'),(125,48,'/uploads/6149115650544e2c.jpg'),(126,48,'/uploads/c0ebac43871b4bb2.jpg'),(127,49,'/uploads/b3d2ff4230034ff9.jpg'),(128,50,'/uploads/197529e93882443d.jpg'),(129,50,'/uploads/822b1cd2a6b24352.jpg'),(130,50,'/uploads/3a192c7941544c37.jpg'),(131,51,'/uploads/781978cad47946f1.jpg'),(132,51,'/uploads/ecb1075f647945af.jpg'),(133,51,'/uploads/3053e27da38344b3.jpg'),(134,51,'/uploads/a87e436ba7374133.jpg'),(135,51,'/uploads/8aa73400ec004fda.jpg'),(136,52,'/uploads/9c0cd706bfda45bf.jpg'),(137,52,'/uploads/21c8a67e264248df.jpg'),(138,53,'/uploads/47b4940255994265.jpg'),(139,53,'/uploads/96d0a1337468488f.jpg'),(140,53,'/uploads/2dd95c532d60423d.jpg'),(141,53,'/uploads/b1c57b9d99b24afa.jpg'),(142,54,'/uploads/517bd26d3f1f42f2.jpg'),(143,54,'/uploads/2029ab13d3214346.jpg'),(144,55,'/uploads/e696fb0058bc46eb.jpg'),(145,56,'/uploads/8775fdd1549c426d.jpg'),(146,56,'/uploads/5719415583624ba8.jpg'),(147,57,'/uploads/15ca709c36724d69.jpg'),(148,57,'/uploads/17353b2485bf4fe4.jpg'),(149,58,'/uploads/3044102998274391.jpg'),(150,58,'/uploads/7ea1c56e0b93458e.jpg'),(151,58,'/uploads/b58fffab820a4fd9.jpg'),(152,58,'/uploads/417515d49bee4d0a.jpg'),(153,58,'/uploads/09027044863e42ce.jpg'),(155,60,'/uploads/052bda7f721c4fa5.jpg'),(156,60,'/uploads/67060679a1b5427d.jpg'),(157,61,'/uploads/aaf5c492f48b44a3.jpg'),(158,62,'/uploads/91853785871e41c0.jpg'),(159,63,'/uploads/6192a5220f494c1a.jpg'),(160,64,'/uploads/bc93e129a1974586.jpg'),(161,65,'/uploads/2ef19f618fcf4472.jpg'),(162,66,'/uploads/9180216e0bf345f5.jpg'),(163,67,'/uploads/3fba6e8eff274428.jpg'),(164,68,'/uploads/5ffdae8e7d3a4f4f.jpg'),(165,69,'/uploads/806507cd59304eaa.jpg'),(166,70,'/uploads/d2a15ee221af48f7.jpg'),(167,71,'/uploads/a2c58af90eac4674.jpg'),(168,72,'/uploads/2b7e9b48bf914e12.jpg'),(169,73,'/uploads/195e53997ef14668.jpg');
/*!40000 ALTER TABLE `productimages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` enum('men','women','accessories') NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `price` decimal(10,2) NOT NULL,
  `texture` varchar(255) DEFAULT NULL,
  `wash` varchar(255) DEFAULT NULL,
  `place` varchar(255) DEFAULT NULL,
  `note` text,
  `story` text,
  `main_image` varchar(255) DEFAULT NULL,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'women','優雅連衣裙','這條連衣裙優雅迷人，適合各種場合穿著。',26.00,'95%棉, 5%彈性纖維','不可以烘乾','印度','不易褪色','時尚潮流','/uploads/f0492dccc1c4467f.jpg','2024-07-24 06:44:17'),(2,'women','時尚外套','這件外套時尚前衛，是秋冬季節的完美選擇。',61.00,'60%聚酯纖維, 40%棉','手洗','中國','柔軟舒適','冬季溫暖','/uploads/f7717bbc172648ca.jpg','2024-07-24 06:44:18'),(27,'men','優質T恤','這件T恤簡約大方，是夏季的好選擇。',62.00,'100%亞麻','手洗','中國','不易褪色','冬季溫暖','/uploads/ca6ed168860d4cfe.jpg','2024-07-24 06:44:22'),(28,'men','精緻夾克','這款夾克設計精緻，適合秋冬季節。',38.00,'100%亞麻','機洗','印度','透氣性佳','經典設計','/uploads/f81893f714344fb2.jpg','2024-07-24 06:44:22'),(29,'men','休閒短褲','這條短褲舒適休閒，是夏季必備單品。',46.00,'60%聚酯纖維, 40%棉','乾洗','越南','耐磨損','時尚潮流','/uploads/eebf5cbdf269468d.jpg','2024-07-24 06:44:22'),(30,'men','時尚西裝','這套西裝現代時尚，適合正式場合。',31.00,'60%聚酯纖維, 40%棉','乾洗','中國','透氣性佳','夏季必備','/uploads/b3ed03fcba44404d.jpg','2024-07-24 06:44:23'),(31,'men','經典長褲','這條長褲經典耐磨，適合日常穿著。',18.00,'100%羊毛','不可以烘乾','越南','耐磨損','經典設計','/uploads/d361d5fdca2d435a.jpg','2024-07-24 06:44:23'),(32,'men','時尚外套','這件外套設計前衛，是秋冬季節的好選擇。',67.00,'60%聚酯纖維, 40%棉','乾洗','泰國','不易褪色','時尚潮流','/uploads/4fb42aadc7f14e23.jpg','2024-07-24 06:44:23'),(33,'men','舒適襯衫','這件襯衫舒適合身，適合多種場合。',63.00,'100%羊毛','機洗','印度','柔軟舒適','時尚潮流','/uploads/fcf92d462f5e4f3f.jpg','2024-07-24 06:44:23'),(34,'men','舒適內搭褲','這條內搭褲舒適自然，適合打底穿著。',82.00,'100%羊毛','乾洗','印度','耐磨損','經典設計','/uploads/df841a8b84be4b2a.jpg','2024-07-24 06:44:23'),(35,'men','優質針織衫','這款針織衫柔軟舒適，是日常穿搭的理想選擇。',74.00,'60%聚酯纖維, 40%棉','乾洗','越南','不易褪色','夏季必備','/uploads/173c4e20f6954204.jpg','2024-07-24 06:44:23'),(36,'men','經典風衣','這件風衣設計獨特，適合秋冬季節。',52.00,'95%棉, 5%彈性纖維','機洗','印度','不易褪色','夏季必備','/uploads/be427ce655444bf9.jpg','2024-07-24 06:44:24'),(37,'men','時尚運動褲','這條運動褲設計簡約，是運動時的最佳選擇。',26.00,'95%棉, 5%彈性纖維','機洗','泰國','透氣性佳','冬季溫暖','/uploads/035e9bd0c37e440c.jpg','2024-07-24 06:44:24'),(38,'men','時尚背心','這款背心時尚舒適，適合夏季穿著。',12.00,'100%羊毛','乾洗','印度','柔軟舒適','經典設計','/uploads/cac97c234ee34640.jpg','2024-07-24 06:44:24'),(39,'men','舒適T恤','這件T恤舒適耐穿，適合日常穿著。',67.00,'100%羊毛','不可以烘乾','印度','透氣性佳','夏季必備','/uploads/1357c1e4c0174b99.jpg','2024-07-24 06:44:24'),(40,'accessories','時尚手鏈','這條手鏈設計時尚，增添你的魅力。',100.00,'100%羊毛','機洗','泰國','耐磨損','時尚潮流','/uploads/2b93646e37e844d2.jpg','2024-07-24 06:44:24'),(41,'accessories','經典項鍊','這款項鍊經典優雅，適合多種場合。',91.00,'100%羊毛','乾洗','中國','柔軟舒適','經典設計','/uploads/cf547f8cdf994c71.jpg','2024-07-24 06:44:24'),(42,'accessories','華麗耳環','這對耳環華麗獨特，是晚宴的完美配件。',15.00,'100%亞麻','不可以烘乾','中國','柔軟舒適','時尚潮流','/uploads/b1a2eecdce604424.jpg','2024-07-24 06:44:25'),(43,'accessories','精緻手錶','這款手錶設計精緻，是實用與美觀的結合。',61.00,'95%棉, 5%彈性纖維','手洗','中國','耐磨損','時尚潮流','/uploads/c8729dc6d7a14dc7.jpg','2024-07-24 06:44:25'),(44,'accessories','流行帽子','這頂帽子流行時尚，能夠增加你的個人風格。',18.00,'60%聚酯纖維, 40%棉','機洗','越南','耐磨損','經典設計','/uploads/d039b39d99fb4b97.jpg','2024-07-24 06:44:25'),(45,'accessories','優雅圍巾','這條圍巾設計優雅，保暖舒適。',90.00,'100%羊毛','不可以烘乾','中國','不易褪色','夏季必備','/uploads/976dcbf23ab7441a.jpg','2024-07-24 06:44:25'),(46,'accessories','時尚腰帶','這條腰帶設計經典，能夠提升你的整體造型。',78.00,'60%聚酯纖維, 40%棉','不可以烘乾','中國','柔軟舒適','經典設計','/uploads/e5ba0fbd71554af0.jpg','2024-07-24 06:44:25'),(47,'accessories','精美戒指','這枚戒指精緻獨特，是增添個人風格的好選擇。',80.00,'100%羊毛','機洗','中國','不易褪色','夏季必備','/uploads/3f3a0122f02c4b99.jpg','2024-07-24 06:44:25'),(48,'accessories','舒適手套','這雙手套舒適合身，是寒冷天氣的理想選擇。',37.00,'95%棉, 5%彈性纖維','手洗','泰國','耐磨損','經典設計','/uploads/3ec7a9a356964179.jpg','2024-07-24 06:44:25'),(49,'accessories','經典領帶','這條領帶設計簡約，適合正式場合。',20.00,'60%聚酯纖維, 40%棉','不可以烘乾','泰國','耐磨損','冬季溫暖','/uploads/d14a35fbce764a44.jpg','2024-07-24 06:44:26'),(50,'accessories','流行手袋','這個手袋設計時尚，是出門的必備單品。',56.00,'60%聚酯纖維, 40%棉','手洗','泰國','透氣性佳','時尚潮流','/uploads/eb6e69c5a6c042bb.jpg','2024-07-24 06:44:26'),(51,'accessories','經典眼鏡','這副眼鏡設計獨特，增添你的魅力。',73.00,'60%聚酯纖維, 40%棉','機洗','越南','柔軟舒適','經典設計','/uploads/0a8e45fcd635428b.jpg','2024-07-24 06:44:26'),(52,'accessories','時尚腕帶','這款腕帶設計時尚，適合多種場合佩戴。',82.00,'95%棉, 5%彈性纖維','機洗','泰國','柔軟舒適','冬季溫暖','/uploads/33051039da57490e.jpg','2024-07-24 06:44:26'),(53,'accessories','精緻胸針','這枚胸針設計精美，是衣物上的亮點。',82.00,'100%亞麻','手洗','中國','不易褪色','時尚潮流','/uploads/dccf3acf7f88442e.jpg','2024-07-24 06:44:26'),(54,'accessories','華麗手環','這條手環華麗獨特，適合各種場合佩戴。',15.00,'60%聚酯纖維, 40%棉','機洗','印度','柔軟舒適','夏季必備','/uploads/11765f9c8de64305.jpg','2024-07-24 06:44:26'),(55,'accessories','時尚吊墜','這個吊墜設計獨特，是增添風格的好選擇。',37.00,'100%羊毛','手洗','越南','柔軟舒適','時尚潮流','/uploads/f5b83d1be74d44d6.jpg','2024-07-24 06:44:26'),(56,'accessories','經典襪子','這雙襪子設計經典，增添舒適感。',71.00,'100%亞麻','不可以烘乾','印度','柔軟舒適','經典設計','/uploads/58f2be18eb9c4c44.jpg','2024-07-24 06:44:27'),(57,'accessories','優雅圍巾','這條圍巾優雅時尚，是秋冬季節的好選擇。',62.00,'60%聚酯纖維, 40%棉','不可以烘乾','印度','不易褪色','時尚潮流','/uploads/4e4eeec094d74ba9.jpg','2024-07-24 06:44:27'),(58,'accessories','獨特耳環','這對耳環設計獨特，是提升風格的好配件。',91.00,'60%聚酯纖維, 40%棉','機洗','中國','不易褪色','冬季溫暖','/uploads/eb047d7e129c4f55.jpg','2024-07-24 06:44:27'),(60,'women','夏日清新洋裝','這款夏日洋裝輕盈舒適，適合炎熱的天氣穿著。',149.00,'100%棉','機洗','貝里斯','柔軟舒適','夏日必備','/uploads/9ec40a150837418d.jpg','2024-07-24 09:45:26'),(61,'women','優雅長洋裝','這件長洋裝優雅高貴，適合各種正式場合穿著。',199.00,'70%聚酯纖維, 30%棉','手洗','教廷','不易皺','高貴優雅','/uploads/fb9e9047e90947af.jpg','2024-07-24 09:45:26'),(62,'women','波點洋裝','這款波點洋裝趣味十足，展現活力青春的風格。',129.00,'100%聚酯纖維','機洗','史瓦帝尼','耐磨耐洗','青春活力','/uploads/ccabcbb5504a44b0.jpg','2024-07-24 09:45:26'),(63,'women','格紋短洋裝','這款格紋短洋裝具有經典魅力，適合日常穿搭。',139.00,'80%棉, 20%亞麻','手洗','吐瓦魯國','輕便舒適','經典格紋','/uploads/d715572b51184513.jpg','2024-07-24 09:45:26'),(64,'women','印花中長洋裝','這款印花中長洋裝充滿藝術感，讓您在任何場合都能引人注目。',159.00,'90%棉, 10%聚酯纖維','機洗','帛琉','舒適透氣','藝術印花','/uploads/ec8504d3cf264184.jpg','2024-07-24 09:45:26'),(65,'women','花卉短洋裝','這款花卉短洋裝充滿浪漫氣息，適合春夏季節穿著。',119.00,'100%聚酯纖維','手洗','馬爾紹群島','清爽透氣','浪漫花卉','/uploads/46233fdc6fd0456f.jpg','2024-07-24 09:45:26'),(66,'women','夏季印花洋裝','這款夏季印花洋裝清新亮眼，是夏季的完美選擇。',139.00,'85%棉, 15%聚酯纖維','機洗','英國','顏色鮮艷','清新印花','/uploads/182cb3f9396f410a.jpg','2024-07-24 09:45:26'),(67,'women','秋冬長洋裝','這款秋冬長洋裝保暖舒適，是寒冷季節的最佳選擇。',179.00,'60%羊毛, 40%聚酯纖維','手洗','香港','保暖舒適','秋冬必備','/uploads/643ea592f66541a4.jpg','2024-07-24 09:45:26'),(68,'women','休閒短洋裝','這款休閒短洋裝適合日常穿搭，輕鬆自在。',109.00,'100%棉','機洗','巴爾幹半島','透氣舒適','日常休閒','/uploads/5ed732a55f16426a.jpg','2024-07-24 09:45:26'),(69,'women','蕾絲洋裝','這款蕾絲洋裝具有精緻的蕾絲細節，展現優雅氣質。',199.00,'70%聚酯纖維, 30%尼龍','手洗','瓜地馬拉','精緻細緻','優雅蕾絲','/uploads/e040efc8a1e54994.jpg','2024-07-24 09:45:27'),(70,'women','經典洋裝','這款經典洋裝展現優雅氣質，適合各種場合穿著。',159.00,'100%聚酯纖維','機洗','日本','舒適耐穿','經典設計','/uploads/f4279f21f79b4583.jpg','2024-07-24 09:45:27'),(71,'women','時尚修身洋裝','這款修身洋裝展現您的完美身材，展現時尚風格。',179.00,'80%棉, 20%聚酯纖維','手洗','台中','修身設計','時尚修身','/uploads/f68d8f90d43d4a2b.jpg','2024-07-24 09:45:27'),(72,'women','輕盈雪紡洋裝','這款雪紡洋裝輕盈舒適，適合炎熱夏季穿著。',149.00,'100%雪紡','機洗','台南','透氣涼爽','輕盈雪紡','/uploads/25a7224ad7e1495c.jpg','2024-07-24 09:45:27'),(73,'men','酷酷外套','超級酷酷',99.00,'100%酷','手洗','台北','不夠酷不要穿','你夠酷嗎？\r\n這是一件超級酷的外套','/uploads/811c96b1a5af475e.jpg','2024-08-01 07:33:45');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productsizes`
--

DROP TABLE IF EXISTS `productsizes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productsizes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `size` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `productsizes_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=199 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productsizes`
--

LOCK TABLES `productsizes` WRITE;
/*!40000 ALTER TABLE `productsizes` DISABLE KEYS */;
INSERT INTO `productsizes` VALUES (1,1,'S'),(2,1,'M'),(3,1,'L'),(4,1,'XXL'),(5,1,'XL'),(6,2,'L'),(7,2,'M'),(8,2,'XL'),(83,27,'XXL'),(84,27,'XL'),(85,28,'S'),(86,28,'M'),(87,28,'L'),(88,28,'XL'),(89,28,'XXL'),(90,29,'XL'),(91,29,'M'),(92,30,'XXL'),(93,30,'M'),(94,30,'L'),(95,30,'S'),(96,31,'XL'),(97,31,'XXL'),(98,31,'M'),(99,31,'L'),(100,31,'S'),(101,32,'XL'),(102,32,'M'),(103,33,'XL'),(104,33,'XXL'),(105,33,'S'),(106,34,'L'),(107,34,'XXL'),(108,34,'M'),(109,34,'S'),(110,35,'XL'),(111,35,'L'),(112,35,'XXL'),(113,35,'S'),(114,36,'L'),(115,36,'S'),(116,36,'XXL'),(117,36,'XL'),(118,36,'M'),(119,37,'XXL'),(120,37,'XL'),(121,38,'XXL'),(122,38,'L'),(123,39,'XXL'),(124,39,'S'),(125,39,'M'),(126,39,'L'),(127,40,'XXL'),(128,41,'L'),(129,41,'M'),(130,41,'S'),(131,41,'XXL'),(132,41,'XL'),(133,42,'XXL'),(134,43,'S'),(135,44,'XL'),(136,44,'XXL'),(137,44,'M'),(138,44,'S'),(139,45,'L'),(140,45,'M'),(141,45,'XL'),(142,45,'XXL'),(143,46,'L'),(144,47,'S'),(145,48,'XXL'),(146,48,'XL'),(147,48,'L'),(148,49,'XXL'),(149,49,'XL'),(150,49,'S'),(151,50,'XL'),(152,50,'XXL'),(153,50,'L'),(154,51,'M'),(155,52,'S'),(156,52,'M'),(157,53,'XXL'),(158,53,'M'),(159,53,'L'),(160,53,'XL'),(161,53,'S'),(162,54,'XXL'),(163,54,'M'),(164,54,'S'),(165,54,'L'),(166,54,'XL'),(167,55,'XL'),(168,55,'XXL'),(169,55,'M'),(170,55,'L'),(171,55,'S'),(172,56,'L'),(173,56,'S'),(174,56,'XL'),(175,56,'M'),(176,57,'S'),(177,57,'XXL'),(178,57,'M'),(179,58,'M'),(180,58,'XL'),(181,58,'S'),(182,58,'L'),(183,58,'XXL'),(185,60,'S'),(186,61,'M'),(187,62,'L'),(188,63,'S'),(189,64,'XL'),(190,65,'M'),(191,66,'XXL'),(192,67,'L'),(193,68,'S'),(194,69,'M'),(195,70,'S'),(196,71,'L'),(197,72,'XXL'),(198,73,'M');
/*!40000 ALTER TABLE `productsizes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productvariants`
--

DROP TABLE IF EXISTS `productvariants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productvariants` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `color_code` varchar(30) NOT NULL,
  `size` varchar(255) NOT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `productvariants_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=199 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productvariants`
--

LOCK TABLES `productvariants` WRITE;
/*!40000 ALTER TABLE `productvariants` DISABLE KEYS */;
INSERT INTO `productvariants` VALUES (1,1,'#00FF00','S',996),(2,1,'#000000','M',25),(3,1,'#0000FF','L',30),(4,1,'#FF0000','XXL',22),(5,1,'#00FF00','XL',14),(6,2,'#FFFF00','L',15),(7,2,'#000000','M',15),(8,2,'#FFFF00','XL',13),(83,27,'#0000FF','XXL',21),(84,27,'#00FF00','XL',23),(85,28,'#FFFF00','S',24),(86,28,'#FF0000','M',30),(87,28,'#000000','L',30),(88,28,'#0000FF','XL',18),(89,28,'#00FF00','XXL',27),(90,29,'#0000FF','XL',17),(91,29,'#FFFF00','M',21),(92,30,'#00FF00','XXL',28),(93,30,'#0000FF','M',12),(94,30,'#00FF00','L',15),(95,30,'#0000FF','S',20),(96,31,'#00FF00','XL',21),(97,31,'#FF0000','XXL',17),(98,31,'#0000FF','M',19),(99,31,'#00FF00','L',24),(100,31,'#FF0000','S',21),(101,32,'#000000','XL',25),(102,32,'#00FF00','M',21),(103,33,'#FF0000','XL',14),(104,33,'#0000FF','XXL',17),(105,33,'#FFFF00','S',17),(106,34,'#FFFF00','L',11),(107,34,'#FF0000','XXL',22),(108,34,'#00FF00','M',14),(109,34,'#FFFF00','S',27),(110,35,'#FFFF00','XL',15),(111,35,'#000000','L',13),(112,35,'#00FF00','XXL',18),(113,35,'#FFFF00','S',10),(114,36,'#FF0000','L',29),(115,36,'#00FF00','S',11),(116,36,'#000000','XXL',16),(117,36,'#FF0000','XL',18),(118,36,'#00FF00','M',26),(119,37,'#00FF00','XXL',18),(120,37,'#00FF00','XL',21),(121,38,'#FF0000','XXL',16),(122,38,'#000000','L',10),(123,39,'#FF0000','XXL',10),(124,39,'#00FF00','S',29),(125,39,'#000000','M',17),(126,39,'#FF0000','L',25),(127,40,'#FFFF00','XXL',30),(128,41,'#000000','L',16),(129,41,'#00FF00','M',14),(130,41,'#FFFF00','S',15),(131,41,'#0000FF','XXL',22),(132,41,'#FF0000','XL',29),(133,42,'#000000','XXL',15),(134,43,'#0000FF','S',16),(135,44,'#FFFF00','XL',20),(136,44,'#FF0000','XXL',14),(137,44,'#000000','M',24),(138,44,'#0000FF','S',19),(139,45,'#0000FF','L',18),(140,45,'#FF0000','M',23),(141,45,'#0000FF','XL',15),(142,45,'#FF0000','XXL',30),(143,46,'#000000','L',13),(144,47,'#FF0000','S',29),(145,48,'#000000','XXL',17),(146,48,'#00FF00','XL',28),(147,48,'#0000FF','L',18),(148,49,'#FF0000','XXL',17),(149,49,'#0000FF','XL',24),(150,49,'#FFFF00','S',23),(151,50,'#FFFF00','XL',23),(152,50,'#FFFF00','XXL',17),(153,50,'#FFFF00','L',22),(154,51,'#FF0000','M',11),(155,52,'#000000','S',14),(156,52,'#FFFF00','M',24),(157,53,'#00FF00','XXL',13),(158,53,'#FF0000','M',23),(159,53,'#00FF00','L',13),(160,53,'#FF0000','XL',21),(161,53,'#00FF00','S',25),(162,54,'#FFFF00','XXL',15),(163,54,'#FFFF00','M',25),(164,54,'#FFFF00','S',22),(165,54,'#FFFF00','L',21),(166,54,'#FFFF00','XL',14),(167,55,'#FFFF00','XL',29),(168,55,'#000000','XXL',17),(169,55,'#FFFF00','M',28),(170,55,'#000000','L',24),(171,55,'#FFFF00','S',21),(172,56,'#FF0000','L',13),(173,56,'#FF0000','S',30),(174,56,'#FF0000','XL',25),(175,56,'#FF0000','M',24),(176,57,'#00FF00','S',20),(177,57,'#FFFF00','XXL',30),(178,57,'#FF0000','M',25),(179,58,'#000000','M',13),(180,58,'#0000FF','XL',21),(181,58,'#FFFF00','S',21),(182,58,'#000000','L',22),(183,58,'#0000FF','XXL',25),(185,60,'#0000FF','S',20),(186,61,'#FF0000','M',15),(187,62,'#000000','L',25),(188,63,'#808080','S',18),(189,64,'#00FF00','XL',22),(190,65,'#FFFF00','M',30),(191,66,'#0000FF','XXL',17),(192,67,'#8B4513','L',20),(193,68,'#808080','S',25),(194,69,'#FFFFFF','M',10),(195,70,'#000000','S',25),(196,71,'#FFC0CB','L',12),(197,72,'#800080','XXL',15),(198,73,'#00FF00','M',999);
/*!40000 ALTER TABLE `productvariants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'admin'),(3,'engineer'),(1,'user');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (14,2);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `provider` enum('native','facebook') NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `picture` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'native','1','1@g','$2a$10$x/9iKF3dgk0guZeMfP7WA.8gU0usy7XFfDCuiYxSkBeK4Fnt/a8G2',' ','2024-07-25 16:51:19'),(2,'native','stylishtest','stylishtest_12345678@test.com','$2a$10$McCeUQ6MdDbVP.HVT/5D0.rpTxyCSNYWI82Au/QwiOlY19Gv3KWKq',' ','2024-07-27 06:48:10'),(3,'native','stylishtest','stylishtest_12345679@test.com','$2a$10$fXYvtahgsKu0/asUlCKUheYeWtqSx3EBkGAP4FMD4PPqRaGC9lppS',' ','2024-07-27 07:50:27'),(4,'native','stylishtest','stylishtest_12345670@test.com','$2a$10$wCcFnjjzYJ9hxESO4MWYPOgGauFAN/ioyl6VgT2q19radMa9dg0Q.',' ','2024-07-27 07:53:15'),(5,'native','stylishtest','stylishtest_12345671@test.com','$2a$10$BgQSpbCsOs6wZ.rqmuWznuCDdVw.PEl8e2guh5rFYHEXlN.BXi9nS',' ','2024-07-27 08:03:49'),(6,'native','stylishtest','stylishtest_12345672@test.com','$2a$10$2l6uBc504N9EQ16ZdkRnD.04CKGokcW047SPt8zI5/.UGydR9LMwa',' ','2024-07-27 08:04:30'),(7,'native','stylishtest','stylishtest_12345673@test.com','$2a$10$R6S5JfsDwHbPbavPIoxVpuz78QmLQC/dmUjrkapYgqw8qtGZTThBG',' ','2024-07-27 08:15:04'),(8,'native','stylishtest','stylishtest_12345674@test.com','$2a$10$MVytiPTMbFt/s/.salZsXO8DNeszCG31RvZaO7eLFipvPvLgEplHC',' ','2024-07-27 08:17:48'),(9,'native','stylishtest','stylishtest_12345675@test.com','$2a$10$.u1GUEYF1sCzGWVJeetG/eHc.MJZYH9A67GtfIpN8ehnstoffxYGG',' ','2024-07-27 08:20:08'),(10,'facebook','Terry Cheng','cat@is.cute',NULL,' ','2024-07-28 03:58:10'),(13,'native','a','a@a','$2a$10$UA0lxUmMxf6aDMdvgwXKpOjEicSTTbvM/.qBQclx2FfbtZKPw1Gqe',' ','2024-08-01 08:41:18'),(14,'native','1','1@1','$2a$10$CIG.4CsATt4x0QBkk3GdseQpfJprs/bOVLEmdvdmykOkUXlg7p486',' ','2024-08-07 18:34:23'),(15,'native','2','2@2','$2a$10$BnaoKCaAVUthMSH9iiZTce2BRktn0xiO/Xog0/tRW6QAEYqk0Ts.y',' ','2024-08-08 07:30:23');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-09  0:41:40
