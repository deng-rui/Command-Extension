## 语言  

[跳转中文](https://github.com/deng-rui/Command-Extension/blob/master/README-zh_CN.md)  
[TO EN](https://github.com/deng-rui/Command-Extension/blob/master/README.md)  


## 鸣谢  

感谢wzyzer和linglan512572354对项目所提供的帮助

wzyzer:https://github.com/way-zer  
linglan512572354:https://github.com/linglan512572354  

## 前言  

警告: 当前项目处于不稳定状态 数据库 数据将不会做迁移操作 请谨慎使用 :)  

  
这个项目是开放的，如果有任何问题，请给我发电子邮件或提交一个问题  
默认情况下，Google Translator不应将源时间设置为0，否则谷歌将拉黑您的ip  

请使用custom模式地图 自带地图将会对模式识别产生干扰 (/shuffle custom)  

最低为JAVA8! JAVA7将无法运行

## 安全

默认已开放Web-Api控制 Port=8080  
如需使用 为了更高的安全性 建议您将Bot与Server在一个网段下 防火墙关闭对外的Web-Port
如果不需要 您可以在Config.ini内关闭(还未完成)  
如果需要高强度验证 请自行编译RSA4096模式    

## 插件可实现功能

PVP :   
1.限制每一局队伍生成单位的数量  
2.限制每一局队伍累计建筑数量  
3.保存本局玩家队伍-适合玩家留存时间较短

通用功能  
1.Login无法关闭 请须知! 默认已关闭激进登陆  
2.半自动识别地图游戏模式 可自动改变模式  
3.Vote  
4.状态定时上报 (Mail)  
5.屏蔽关键词 (DFA)  
6.昼夜变换 将会覆盖地图默认设置  

## 运行配置  

| 配置 		| CPU             | 内存 	| 系统 			| 硬盘大小 	| Java      |
|:--- 		|:---             |:---     |:---           |:---       |:---       |
| 当前配置 	| BCM2711         | 4G      | Ubuntu 19.10  | 500G HHD  | Java 14   |
| 建议配置 	| Intel I3-6100+  | 4G      | ubuntu 16.04+ | 500G HHD  | Java 8+   |

## 构建配置  

| 配置 		| CPU             | 内存 	| 系统 			| 硬盘大小 	| Java      | Gradle    |
|:--- 		|:---             |:--- 	|:--- 			|:---      	|:---       |:---       |
| 当前配置 	| BCM2711         | 4G 		| Ubuntu 19.10 	| 500G HHD 	| Java 8    | 6.2.2     |

## 服务器命令列表  

| 命令 					 | 参数 																						 | 信息 									 |
|:--- 					 |:--- 																						 |:--- 									 |
| gameover(覆盖) 		 |                                                  										 | 强制结束游戏(防止原gameover恢复) 		 |
| reloadconfig           |                                                  										 | 热重载配置文件 						 |
| reloadmaps(覆盖) 		 |                                                  										 | 重载地图(便于重新读取模式) 				 |
| toadmin                | &lt;UUID&gt; &lt;权限级&gt; 																 | 在线设置玩家权限级       		         |
| exit(覆盖) 			 | 																							 | 关闭服务器(结束内置计时器) 				 |
| newkey 				 | &lt;Key长度&gt; &lt;权限级&gt; &lt;对于用户延长时间&gt; &lt;过期时间&gt; [最多可供多少人] 	 | 新建Key               				 |
| keys          		 |                                                  										 | 查看服务器已建立的Key              	 |
| rmkeys          		 |                                                  										 | 删除全部Key               			 |
| rmkey          		 | &lt;Key&gt;                               	   											 | 删除指定Key               			 |


## 游戏命令列表  

| 命令 			| 参数 												 | 信息 										 |
|:---           |:--- 												 |:--- 										 |
| register      |&lt;用户名&gt; &lt;密码&gt; &lt;重复密码&gt;	[Mail]   | 注册账号 									 |
| login         |&lt;用户名&gt; &lt;密码&gt;						 	 | 登录账号 									 |
| ftpasswd      |&lt;用户名/Mail&gt; [邮件内的验证码] 				 | 忘记密码 									 |
| info          | 													 | 查看我的信息 								 |
| status        | 													 | 查看服务器当前状态 						 |
| tp            |&lt;玩家名称&gt; 									 | TP到指定人身边 							 |
| tpp           |&lt;XYZ坐标&gt; 									 | TP到指定坐标 								 |
| suicide       | 													 | 自杀 										 |
| team          | 													 | 更换队伍 									 |
| difficulty    |&lt;难度&gt; 										 | 设置难度 									 |
| gameover      | 													 | 结束游戏 									 |
| host          |&lt;地图名&gt; [游戏模式] 							 | 更换地图 									 |
| runwave       | 													 | 下一波 									 |
| time          | 													 | 查看服务器当前时间(UTC 后续将动态玩家时间) 	 |
| tr            |&lt;目标语言&gt; &lt;TEXT&gt; 						 | 谷歌翻译! 请使用-代替文本的空格 			 |
| maps          |[页码] [指定查看模式-游戏模式-简写] 					 | 查看服务器当前地图 						 |
| vote          |[gameover/kick/skipwave/host] [玩家名/地图序号] 		 | 投票 										 |
| ukey          |&lt;key&gt; 								 		 | 使用Key									 |


### 当前进度  

- [ ] Config
    - [ ] Log
- [ ] 动态难度
- [ ] PVP前期限制
- [ ] 插件分割
    - [ ] Google翻译
    - [ ] 语言过滤
    - [ ] Vote
- [ ] 权限
    - [x] Help支持权限显示
    - [ ] 尝试更精细的权限控制
    - [x] KEY
        - [x] KEY - 有效时间
        - [x] KEY - 可多次使用
    - [x] 权限时间
- [ ] 优化
    - [ ] 内存占用
    - [ ] 逻辑处理
- [ ] 可选性
    - [ ] 数据库
- [ ] WEB-API
    - [ ] QQ-Bot
    - [ ] WEB
- [ ] 个人
    - [ ] 测试命令 (java/core/testmode)

### 插件使用的目录及文件  

标记 \* 均为后续加入目录及文件

```
config
└───mods
    └───GA                  //插件使用主目录
        │   Data.db         //玩家数据
        │   Config.ini      //配置文件
        └───lib             //插件使用jar-外置目录
        └───resources       //插件使用资源外置目录   
           └───bundles      //语言文件              -已去除 不便于更新文件
           └───other        //其他文件            
        └───log             //插件使用log目录       *
            Error.txt       //错误log              *
            bans.txt        //投票ban 记录          *
```

### 如何安装  

只需将下载的jar放在服务器的“config/mods”目录中，然后重新启动服务器。  
通过运行“mods”命令列出当前安装的插件。  

### 个人广告:)  

(很遗憾，为了维持稳定性 服务器暂时不对外开放)  
如有必要，您可以自己尝试更改本地化参数  

### 许可证 

The Unlicense-公共领域  
:) 
