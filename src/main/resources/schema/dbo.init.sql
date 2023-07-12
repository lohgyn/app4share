-- init an admin user with username 'adminuser' and password 'adminuser1$'

insert into role (created_by,created_date,last_modified_by,last_modified_date,name) values ('system',getdate(),'system',getdate(),'ROLE_ADMIN');

insert into user_account (account_non_locked,created_by,created_date,last_modified_by,last_modified_date,password,username) values (1,'system',getdate(),'system',getdate(),'$2a$10$o5g9VMfpsI06tZoSrY4WmulOcawwDH/rYq7VLS9QMRvmVoY.x3BQ6','adminuser')

insert into user_role (user_account_id, role_id) values ((select id from user_account where username ='adminuser'), (select id from role where name = 'ROLE_ADMIN'))