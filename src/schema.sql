CREATE TABLE IF NOT EXISTS `users` (
  `id` VARCHAR(36) PRIMARY KEY,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `first_name` VARCHAR(50),
  `last_name` VARCHAR(50),
  `created_at` TIMESTAMP NOT NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `updated_at` TIMESTAMP DEFAULT NULL,
  `updated_by` VARCHAR(50) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` VARCHAR(36) PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `balance` DECIMAL(15,2) NOT NULL,
  `type` VARCHAR(50) NOT NULL,
  `user_id` VARCHAR(36) NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

CREATE TABLE IF NOT EXISTS `budgets` (
  `id` VARCHAR(36) PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `total_planned` DECIMAL(15,2) NOT NULL,
  `total_income` DECIMAL(15,2) NOT NULL,
  `total_expense` DECIMAL(15,2) NOT NULL,
  `current_balance` DECIMAL(15,2) NOT NULL,
  `user_id` VARCHAR(36) NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

CREATE TABLE IF NOT EXISTS `categories` (
  `id` VARCHAR(36) PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `type` VARCHAR(50) NOT NULL,
  `user_id` VARCHAR(36) NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

CREATE TABLE IF NOT EXISTS `budget_categories` (
  `id` VARCHAR(36) PRIMARY KEY,
  `allocated_amount` DECIMAL(15,2) NOT NULL,
  `budget_id` VARCHAR(36) NOT NULL,
  `category_id` VARCHAR(36) NOT NULL,
  FOREIGN KEY (`budget_id`) REFERENCES `budgets`(`id`),
  FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
);

CREATE TABLE IF NOT EXISTS `transactions` (
  `id` VARCHAR(36) PRIMARY KEY,
  `amount` DECIMAL(15,2) NOT NULL,
  `date` DATE NOT NULL,
  `description` TEXT,
  `type` VARCHAR(50) NOT NULL,
  `account_id` VARCHAR(36) NOT NULL,
  `category_id` VARCHAR(36) NOT NULL,
  `user_id` VARCHAR(36) NOT NULL,
  `budget_id` VARCHAR(36),
  FOREIGN KEY (`account_id`) REFERENCES `accounts`(`id`),
  FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`budget_id`) REFERENCES `budgets`(`id`)
);
