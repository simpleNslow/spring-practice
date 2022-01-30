create table users (
	id varchar(10) primary key,	
	name varchar(20) not null,
	password varchar(10) not null,
	email varchar(50),
	level smallint not null,
	login int not null,
	recommend int not null
)