
-- drop table jfileserver_user;
create table jfileserver_user
(
	uid int not null,
    pwd char(32) default 0 not null,
    state int default 0 not null,
    createtime datetime default null,
    primary key (uid)
	
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件服务器访问授权';



