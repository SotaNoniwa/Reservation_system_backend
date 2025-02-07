drop database if exists reservation_system;
create database reservation_system;
use reservation_system;

SET foreign_key_checks = 0;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `role`;
SET foreign_key_checks = 1;

create table `user` (
	id int not null auto_increment,
    username varchar(50) not null,
    email varchar(120) not null,
    phone varchar(30) not null,
    `password` char(80) not null,
    enabled tinyint not null,
    
    primary key(id)
) engine=InnoDB default charset=utf8mb4;

INSERT INTO `user` (`username`, email, phone, `password`,`enabled`)
VALUES 
('john', 'john@mail.com', 'xxx-xxxx-xxxx', '$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K',1),
('mary', 'mary@mail.com', 'xxx-xxxx-xxxx', '$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K',1),
('susan','susan@mail.com', 'xxx-xxxx-xxxx', '$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K',1);

CREATE TABLE `role` (
	id int not null auto_increment,
    `name` varchar(50) not null,
    
    primary key (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

INSERT INTO `role` (name)
VALUES 
('ROLE_USER'),('ROLE_ADMIN');

--
-- Table structure for table `users_roles`
--

DROP TABLE IF EXISTS `users_roles`;

CREATE TABLE `users_roles` (
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  
  PRIMARY KEY (`user_id`,`role_id`),
  
  KEY `FK_ROLE_idx` (`role_id`),
  
  CONSTRAINT `FK_USER` FOREIGN KEY (`user_id`) 
  REFERENCES `user` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION,
  
  CONSTRAINT `FK_ROLE` FOREIGN KEY (`role_id`) 
  REFERENCES `role` (`id`) 
  ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

--
-- Dumping data for table `users_roles`
--

INSERT INTO `users_roles` (user_id,role_id)
VALUES 
(1, 1),
(2, 1),
(2, 2),
(3, 1),
(3, 2);

-- create table authorities (
-- 	user_id int not null,
--     authority varchar(50) not null,
--     
--     unique key authorities_idx_1 (user_id, authority),
--     
--     constraint authorities_fk_1
--     foreign key (user_id)
--     references users(id)
-- ) engine=InnoDB default charset=utf8mb4;

create table `table` (
	id int not null auto_increment,
    table_number int not null,
    capacity int not null default 1,
    
    primary key (id)
) engine=InnoDB default charset=utf8mb4;

create table available_time_slot (
	id int not null auto_increment,
    start_time datetime not null,
    end_time datetime not null,
    table_id int not null,
    
    primary key (id),
    
    constraint available_time_slot_fk_1
    foreign key (table_id)
    references `table` (id)
) engine=InnoDB default charset=utf8mb4;

create table course (
	id int not null auto_increment,
    `name` varchar(100) not null,
    price int not null default 0,
    
    primary key (id)
);

create table reservation (
	id int not null auto_increment,
    start_time datetime not null,
    end_time datetime not null,
    number_of_people int not null,
    note varchar(200) not null,
    user_id int not null,
    table_id int not null,
    course_id int not null,
    
    primary key (id),
    
    constraint reservation_fk_1
    foreign key (user_id)
    references `user`(id),
    
	constraint reservation_fk_2
    foreign key (table_id)
    references `table`(id),
    
    constraint reservation_fk_3
    foreign key (course_id)
    references course(id)
) engine=InnoDB default charset=utf8mb4;

insert `table` values 
(1, 1, 1), 
(2, 2, 2),
(3, 3, 4);

insert course values 
(1, 'Special', 5000),
(2, 'Pasta', 2500);
