## [Server]

服务器默认语言
Server_Language=en_US
## [Login]

是否启用 会产生IO
Login=on/off
玩家登陆时获取ip信息? on为yes off则注册时获取
Login_IP=on/off
玩家登录保持时间 单位 分钟
Login_Time
激进模式! 不登录不出生!
Login_Radical=on/off
## [Authority]

是否启用权限传递 (后文会讲)
Permission_Passing=on/off
总权限等级 仅限数字
Privilege_Level=0,1,2,3.....254
每个权限可使用的命令 如果开启传递 则只用配置阶段命令即可
例如
Privilege.1=info,tr
Privilege.2=vote,..
那么2可直接获取1的权限内容
注意 若关闭Login 则权限系统依然会生效
普通用户等级默认为1
管理员等级默认为2
请注意权限配置
管理默认权限等级
Admin.Authority
## [Vote]

是否允许新进玩家参与投票
Vote.New_Player=on/off
## [Building restriction -TEAM PVP]
建筑限制 - 按队伍 仅限PVP
是否启用
Building_Restriction=on/off
警告量 未启用则留空或不管
Building_Wan_Construction
停止生成量
Building_MAX_Construction
## [Soldier restriction -TEAM PVP]
兵力限制 - 按队伍 仅限PVP
是否启用
Building_Restriction=on/off
警告量 未启用则留空或不管
Soldier_Wan_Construction
停止生成量
Soldier_MAX_Construction
## [Mail]

Mail
是否启用
Mail_Use=on/off
邮件SMTP地址
Mail_SMTP.IP=
邮件SMTP端口
Mail_SMTP.Port=
发件人账户名
Mail_SMTP.User=
邮件密码 QQ则是授权码
Mail_SMTP.Passwd=
是否启用定时上报状态
Regular_Reporting=on/off
定时上报时间 (单位分钟) 不建议过短 否则会被邮件商AI拦截
Regular_Reporting_Time=
定时上报收件人
Regular_Reporting_ToMail=