/*电话区域查询表*/
create table phone_location (_id integer primary key,location varchar(32) not null);
/*白名单*/
drop TABLE whitelist;
create table whitelist(phone_number text primary key, phone_enable integer, sms_enable integer);
/*黑名单*/
drop TABLE blacklist;
create table blacklist(phone_number text primary key, phone_enable integer, sms_enable integer);
/*拦截电话日志*/
drop TABLE blocker_phone_log;
create table blocker_phone_log(no integer primary key, phone_number text, time integer);
/*拦截短信日志*/
drop TABLE blocker_sms_log;
create table blocker_sms_log(no integer primary key, phone_number text, time integer, content text, isread integer);
/*系统信息*/
drop TABLE system_infomation;
create table system_infomation(key text primary key, value text);
insert into system_infomation values("firewallstatus", "0");
