## Language  

[跳转中文](https://github.com/deng-rui/Command-Extension/blob/master/README-zh_CN.md)  
[TO Engilsh](https://github.com/deng-rui/Command-Extension/blob/master/README.md)  


## Acknowledgement  

Thank wzyzer and linglan512572354 for their help in this project  

wzyzer:https://github.com/way-zer  
linglan512572354:https://github.com/linglan512572354  

## Premise

Warning: database data will not be migrated when the current project is in an unstable state. Please use with caution:)
  
  
This project is open. If you have any questions, please email me or submit a question    
By default, Google Translate should not set the source time to 0, otherwise Google will disable your IP  

Please use custom mode map, all mode(/shuffle all) will interfere with pattern recognition (/shuffle custom)  

At least java8! Java7 will not run!!!  


## Security

Default open web API control  Force HTTPS!! port = 8443  
If you need to use for higher security, it is recommended that you close the external web port by firewall under the same network segment between Bot and server  
If you don't need it, you can close it in config.ini (not finished yet)  

## Plug in can realize functions
PVP:  
1.Limit the number of team generating units  
2.Limit the accumulated building quantity of the team  
3.Save the player team of this game - suitable for players with short retention time

General function  
1.Semi automatic recognition map game mode can be changed automatically  
2.Vote  
3.Regular status reporting (Mail)  
4.Shielding keywords (DFA)  

## Required configuration  

| configure     | CPU             | RAM   | System        | Hard disk | Java      |
|:---           |:---             |:---   |:---           |:---       |:---       |
| Current use   | BCM2711         | 4G    | ubuntu 19.10  | 500G HHD  | Java 14   |
| Recommended   | Intel I3-6100+  | 4G    | ubuntu 16.04+ | 500G HHD  | Java 8+   |

## build configuration  

| configure     | CPU             | RAM   | System        | Hard disk | Java      | Gradle    |
|:---           |:---             |:---   |:---           |:---       |:---       |:---       |
| Current use   | BCM2711         | 4G    | ubuntu 19.10  | 500G HHD  | Java 8    | 6.2.2     |

## Server commands  

| Command               | Parameter                                                                                                     | Description                                           |
|:---                   |:---                                                                                                           |:---                                                   |
| gameover(replace)     |                                                                                                               | Force end of game (prevent original gameover recovery)|
| reloadconfig          |                                                                                                               | Reload the this plugins config.ini                    |
| reloadmaps(replace)   |                                                                                                               | Reload the map (easy to re-read the mode)             |
| toadmin               | &lt;UUID&gt; &lt;Privilege level&gt;                                                                          | Set player permission level online                    |
| exit(replace)         |                                                                                                               | Shut down the server (end built-in timer)             |
| newkey                | &lt;Key length&gt; &lt;Privilege level&gt; &lt;Available_time(min)&gt; &lt;Expiration_date(min)&gt; [Total]   | new Key                                               |
| keys                  |                                                                                                               | View the established key of the server                |
| rmkeys                |                                                                                                               | Delete all keys                                       |
| rmkey                 | &lt;Key&gt;                                                                                                   | Delete specified key                                  |

## Game command  

| Command       | Parameter                                                    | Description                                           |
|:---           |:---                                                          |:---                                                   |
| register      |&lt;New Username&gt; &lt;passwd&gt; &lt;repasswd&gt; [Mail]   | register user                                         |
| login         |&lt;Username&gt; &lt;passwd&gt;                               | login user                                            |
| ftpasswd      |&lt;Username/Mail&gt; [Verification code sent by mail]        | Forget password                                       |
| info          |                                                              | info me                                               |
| status        |                                                              | View server status                                    |
| tp            |&lt;player name&gt;                                           | Teleport to other players                             |
| tpp           |&lt;XYZ&gt;                                                   | Transfer to specified coordinates                     |
| suicide       |                                                              | Kill yourself.                                        |
| team          |                                                              | Replacement team.                                     |
| difficulty    |&lt;mode&gt;                                                  | Set server difficulty                                 |
| gameover      |                                                              | KEnd the game                                         |
| host          |&lt;mapsname&gt; [gamemode]                                   | Start a new game                                      |
| runwave       |                                                              | Runwave                                               |
| time          |                                                              | View the current time of the server                   |
| tr            |&lt;target language&gt; &lt;TEXT&gt;                          | Google translation(Use - instead of spaces in text)   |
| maps          |[page] [mode(1)]                                              | View the map currently available to the server        |
| vote          |&lt;gameover/kick/skipwave/host&gt; [name/number]             | VOTE                                                  |
| ukey          |&lt;key&gt;                                                   | Use key                                               |

Notes:
1: You need to view the schema abbreviation of the map for the specified schema

### Current progress  

- [ ] Config
    - [ ] Log
- [ ] Dynamic difficulty
- [ ] PVP pre limit
- [ ] plug in segmentation
    - [ ] Google translation
    - [ ] language filtering
    - [ ] Vote
- [ ] Authority
    - [x] Help supports permission display
    - [ ] Try finer authority control
    - [x] KEY
        - [x] KEY - Effective time
        - [x] KEY - Reuse
    - [x] Permission time
- [ ] Optimization
    - [ ] Memory usage
    - [ ] Logical processing
- [ ] Optional
    - [ ] DB
- [ ] WEB-API
    - [ ] QQ-Bot
    - [ ] WEB
- [ ] No tab
    - [ ] Test comm (java/core/testmode)

### Directories and files used by plug-ins  

The marks \* are all subsequent added directories and files

```
config
└───mods
    └───GA                  //Plug in uses home directory
        │   Data.db         //Player Data
        │   Config.ini      //Plugin Config
        └───lib             //Plug in uses jar external directory
        └───resources       //Plug in using resource external directory   
           └───bundles      //Language file                               -Removed files that are not easy to update
           └───other        //Other file                             
        └───log             //Plug in log (within ten days)               *
            Error.txt       //Error file                                  *
            bans.txt        //bans file                                   *
```

### Installing  

Simply place the output jar from the step above in your server's `config/mods` directory and restart the server.
List your currently installed plugins by running the `mods` command.

### NOT TAB  
(unfortunately, in order to maintain stability, the server is temporarily closed)  
If necessary, you can try to change the localization parameters yourself  


### Licenses  
The Unlicense  
:) 
