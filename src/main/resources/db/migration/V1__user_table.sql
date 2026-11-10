CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_type` VARCHAR(45) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(150) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `employee_id` VARCHAR(45) NULL,
  `loyalty_points` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
