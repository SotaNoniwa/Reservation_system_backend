drop database if exists reservation_system;
create database reservation_system;
use reservation_system;

create table users (
	id int not null auto_increment,
    username varchar(50) not null,
    email varchar(120) not null,
    phone varchar(30) not null,
    `password` varchar(50) not null,
    enabled tinyint not null,
    
    primary key(id)
) engine=InnoDB default charset=utf8mb4;

create table authorities (
	user_id int not null,
    authority varchar(50) not null,
    
    unique key authorities_idx_1 (user_id, authority),
    
    constraint authorities_fk_1
    foreign key (user_id)
    references users(id)
) engine=InnoDB default charset=utf8mb4;

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
    references users(id),
    
	constraint reservation_fk_2
    foreign key (table_id)
    references `table`(id),
    
    constraint reservation_fk_3
    foreign key (course_id)
    references course(id)
) engine=InnoDB default charset=utf8mb4;

insert `table` values (
	1, 1, 1
);

insert `table` values (
	2, 2, 2
);

insert `table` values (
	3, 3, 4
);

insert users values (
	1, 'john', 'john@mail.com', '00000000000', '{noop}test', 1
);

insert course values (
	1, 'Special', 5000
);