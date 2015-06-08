/*电话区域查询表*/
create table  if not exists phone_location (_id integer primary key,location varchar(32) not null);
/*白名单*/
create table  if not exists whitelist(phone_number text primary key, name text, phone_enable integer, sms_enable integer);
/*黑名单*/
create table  if not exists blacklist(phone_number text primary key, name text, phone_enable integer, sms_enable integer);
/*短信关键字白名单*/
create table  if not exists sms_keyword_whitelist(keyword text, phone_enable integer, sms_enable integer);
/*短信关键字黑名单*/
create table  if not exists sms_keyword_blacklist(keyword text, phone_enable integer, sms_enable integer);
/*拦截电话日志*/
create table  if not exists blocker_phone_log(no integer primary key, phone_number text, time integer);
/*拦截短信日志*/
create table  if not exists blocker_sms_log(no integer primary key, phone_number text, time integer, content text, isread integer);
/*系统信息*/
create table  if not exists system_infomation(key text primary key, value text);
insert into system_infomation values("firewallstatus", "0");
