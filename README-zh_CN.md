## 语言

[跳转中文](https://github.com/deng-rui/Command-Extension/blob/master/README-zh_CN.md)  
[TO EN](https://github.com/deng-rui/Command-Extension/blob/master/README.md)  


## 鸣谢

感谢wzyzer和linglan512572354对项目所提供的帮助

wzyzer:https://github.com/way-zer  
linglan512572354:https://github.com/linglan512572354  

## 前言

这个项目是开放的，如果有任何问题，请给我发电子邮件或提交一个问题  
这个项目是一个集中的项目。如果您需要一些模块，请在成品完成后等待分割，或者您可以自己完成  
默认情况下，Google Translator不应将源时间设置为0，否则谷歌将拉黑您的ip于12小时  
需要生产环境请下载发布的文件  

## 运行配置

| 配置 		| CPU             | 内存 	| 系统 			| 硬盘大小 	| Java      |
|:--- 		|:---             |:---     |:---           |:---       |:---       |
| 当前配置 	| BCM2711         | 4G      | Ubuntu 19.10  | 500G HHD  | Java 11   |
| 建议配置 	| Intel I3-6100+  | 4G      | ubuntu 16.04+ | 500G HHD  | Java 8+   |

## 构建配置

| 配置 		| CPU             | 内存 	| 系统 			| 硬盘大小 	| Java      | Gradle    |
|:--- 		|:---             |:--- 	|:--- 			|:---      	|:---       |:---       |
| 当前配置 	| BCM2711         | 4G 		| Ubuntu 19.10 	| 500G HHD 	| Java 11   | 6.2.2     |

## 服务器命令列表

| 命令 					 | 参数 												 | 信息 									 |
|:--- 					 |:--- 												 |:--- 									 |
| reloadmaps(覆盖) 		 | 													 | 重载地图(便于重新读取模式) 				 |

## 游戏命令列表

| 命令 			| 参数 												 | 信息 										 |
|:---           |:--- 												 |:--- 										 |
| info          | 													 | 查看我的信息 								 |
| status        | 													 | 查看服务器当前状态 						 |
| getpos        | 													 | 查看当前的坐标 							 |
| tp            |&lt;玩家名称&gt; 									 | TP到指定人身边 							 |
| tpp           |&lt;XYZ坐标&gt; 									 | TP到指定坐标 								 |
| suicide       | 													 | 自杀 										 |
| team          | 													 | 更换队伍 									 |
| difficulty    |&lt;难度&gt; 										 | 设置难度 									 |
| gameover      | 													 | 结束游戏 									 |
| host          |&lt;地图名&gt; [游戏模式] 							 | 更换地图 									 |
| runwave       | 													 | 下一波 									 |
| time          | 													 | 查看服务器当前时间(UTC 后续将动态玩家时间) 	 |
| tr            | 													 | 谷歌翻译! 请使用-代替文本的空格 			 |
| maps          |[页码] [指定查看模式-游戏模式-简写] 					 | 查看服务器当前地图 						 |
| vote          |[gameover/kick/skipwave/host] [玩家名/地图序号] 		 | 投票 										 |


### 当前进度

- [ ] Baidu翻译支持
- [x] Vote
    - [x] 地图模式识别(WZY)
- [ ] 动态难度
- [ ] PVP前期限制
- [ ] 插件分割
    - [ ] Google翻译
    - [ ] 语言过滤
    - [ ] Vote
- [x] 资源文件外置 便于动态刷新
- [ ] 多语言支持
    - [x] 玩家
    - [ ] 命令

### 插件使用的目录及文件

标记 \* 均为后续加入目录及文件

```
config
└───mods
    └───GA                  //插件使用主目录
        │   Authority.json  //权限配置
        │   Data.db         //玩家数据
        │   Setting.json    //设置
        └───lib             //插件使用jar-外置目录
        └───resources       //插件使用资源外置目录   
           └───bundles      //语言文件              
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
