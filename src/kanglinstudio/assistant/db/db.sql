/*电话区域查询表*/
create table  if not exists phone_location (_id integer primary key,location varchar(32) not null);
/*白名单*/
create table  if not exists whitelist(phone_number text primary key, name text default "", phone_enable integer default 1, sms_enable integer default 1);
/*黑名单*/
create table  if not exists blacklist(phone_number text primary key, name text default "", phone_enable integer default 1, sms_enable integer default 1);
/*短信关键字白名单*/
create table  if not exists sms_keyword_whitelist(no integer primary key, keyword text not null, phone_number text default "", enable integer default 1);
/*短信关键字黑名单*/
create table  if not exists sms_keyword_blacklist(no integer primary key, keyword text not null, phone_number text default "", enable integer default 1);
/*拦截电话日志*/
create table  if not exists blocker_phone_log(no integer primary key, phone_number text not null, time integer, isread integer default 0);
/*拦截短信日志*/
create table  if not exists blocker_sms_log(no integer primary key, phone_number text not null, time integer, content text, isread integer default 0);
/*系统信息*/
create table  if not exists system_infomation(key text primary key not null, value text not null);
insert into system_infomation values("firewallstatus", "0");
/*建立gps位置表*/
create table if not exists position (no integer primary key, userid text, deviceid text, systime long, gpstime long, accuracy float, bearing float, speed float, latitude double, longitude double, altitude double, satellite_number integer, state integer);
create index if not exists position_time on position(gpstime);
create index if not exists position_user_device on position(userid, deviceid);