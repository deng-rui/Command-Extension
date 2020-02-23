## Language

[跳转中文](https://github.com/deng-rui/Command-Extension/blob/master/README-zh_CN.md)  
[TO EN](https://github.com/deng-rui/Command-Extension/blob/master/README.md)  


## Acknowledgement

Thank wzyzer and linglan512572354 for their help in this project  

wzyzer:https://github.com/way-zer  
linglan512572354:https://github.com/linglan512572354  

## Premise

Please pay attention to timeliness in the beginning of reconstruction of this project.
This project is open and only based on individuals If there is any shortage, please send me an email or submit a question.
This project is a centralized project. If you need some modules, please wait for the division after the completion of the finished product or you can finish it by yourself.
Published files have been compiled and you can compile and try to use them yourself. :)
By default, Google Translator should not set the source time to 0, otherwise it will be forbidden to access Google's API by ip.

## build configuration

| configure     | CPU             | RAM   | Hard disk | Java      | Gradle    |
|:---           |:---             |:---   |:---       |:---       |:---       |
| Current use   | BCM2711         | 4G    | 500G HHD  | Java 11   | 6.2       |
| Recommended   | Intel I3-6100+  | 4G    | 500G HHD  | Java 8+   | 6.2       |

## Client commands

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

### Current progress

- [ ] Baidu translation support
- [ ] Vote
- [ ] plug in segmentation
    - [ ] Google translation
    - [ ] language filtering
    - [ ] Vote
- [ ] External resource file for dynamic refresh

### Directories and files used by plug-ins

The marks \* are all subsequent added directories and files

```
config
└───mods
    └───GA                  //Plug in uses home directory
        │   Authority.json  //Authority Data
        │   Data.db         //Player Data
        │   setting.json    //Setting
        └───Lib             //Plug in uses jar external directory
           └───resources    //Plug in using resource external directory   *
           └───bundles      //Language file                               *
           └───other        //Block word file                             *
```

### Installing

Simply place the output jar from the step above in your server's `config/mods` directory and restart the server.
List your currently installed plugins by running the `mods` command.

### NOT TAB
(unfortunately, in order to maintain stability, the server is temporarily closed)  
If necessary, you can try to change the localization parameters yourself  
