## 语言

[跳转中文](https://github.com/deng-rui/Command-Extension/blob/master/README-zh_CN.md)  
[TO EN](https://github.com/deng-rui/Command-Extension/blob/master/README.md)  


## 鸣谢

感谢wzyzer和linglan512572354对项目所提供的帮助

wzyzer:https://github.com/way-zer  
linglan512572354:https://github.com/linglan512572354  

## 前言

请注意本项目阶段性的时效性  
这个项目是开放的，如果有任何短缺，请给我发电子邮件或提交一个问题  
这个项目是一个集中的项目。如果您需要一些模块，请在成品完成后等待分割，或者您可以自己完成  
默认情况下，Google Translator不应将源时间设置为0，否则将禁止通过ip访问Google的API  
提交的mast默认已通过编译，您可以自行编译并尝试使用它们，请不要用于生产环境:)  
需要生产环境请下载发布的文件  

## 构建配置

| 配置        | CPU                               | RAM   | Hard disk | Java      | Gradle    |
|:---       |:---                               |:---   |:---       |:---       |:---       |
| 当前配置  | BCM2711                           | 4G    | 500G HHD  | Java 11   | 6.2       |
| 建议配置  | Intel I3-6100+                    | 4G    | 500G HHD  | Java 8+   | 6.2       |

## 命令列表

| Command       | Parameter                             | Description                                           |
|:---           |:---                                   |:---                                                   |
| info          |                                       | info me                                               |
| status        |                                       | View server status                                    |
| getpos        |                                       | View the current coordinates                          |
| tp            |&lt;player name&gt;                    | Teleport to other players                             |
| tpp           |&lt;XYZ&gt;                            | Transfer to specified coordinates                     |
| suicide       |                                       | Kill yourself.                                        |
| getpos        |                                       | Get your current position info                        |
| team          |                                       | Replacement team.                                     |
| difficulty    |&lt;mode&gt;                           | Set server difficulty                                 |
| gameover      |                                       | KEnd the game                                         |
| host          |&lt;mapsname&gt; [gamemode]            | Start a new game                                      |
| runwave       |                                       | Runwave                                               |
| time          |                                       | View the current time of the server                   |
| tr            |                                       | Google translation(Use - instead of spaces in text)   |

### 当前进度

- [ ] Baidu翻译支持
- [ ] Vote
- [ ] 插件分割
    - [ ] Google翻译
    - [ ] 语言过滤
    - [ ] Vote
- [ ] 资源文件外置 便于动态刷新

### 插件使用的目录及文件

标记 \* 均为后续加入目录及文件

```
config
└───mods
    └───GA                  //插件使用主目录
        │   Authority.json  //权限配置
        │   Data.db         //玩家数据
        │   setting.json    //设置
        └───Lib             //插件使用jar-外置目录
        └───resources       //插件使用资源外置目录   \*
           └───bundles      //语言文件              \*
           └───other        //屏蔽词文件            \*
```

### 如何安装

只需将下载的jar放在服务器的“config/mods”目录中，然后重新启动服务器。
通过运行“mods”命令列出当前安装的插件。

### 个人广告:)
(很遗憾，为了维持稳定性 服务器暂时不对外开放)  
如有必要，您可以自己尝试更改本地化参数
