## Pinned plan

- [ ] Kickall - Kick all players.
  - [ ] Exclude who entered the command
- [ ] Teleport
  - [ ] tp playername playername - Teleport from Player to Player
  - [x] tp playername - Teleport to Player.
- [ ] spawnmob mob amount - Spawn enemies or mob.
- [ ] status - Show server status.
- [ ] realname player - Show real name.
- [x] me msg - Special chat format
- [ ] firework player - Show Firework with players.
- [x] motd - Show server motd.
- [x] getpos - Show current position.
- [ ] tempban player time - Timer ban.
- [ ] info - Show player info.
  - [x] Player name and UUID
  - [x] Show IP and GeoLocation
  - [ ] Show destroy/placed block count
  - [ ] Show destroy enemies count
  - [ ] Show dead count
  - [ ] Rank system
- [ ] difficulty - Set difficulty.
- [ ] effect - Make effect.
- [ ] gamerule - Set gamerule.
- [ ] vote - Map vote.
  - [ ] Map list
- [x] suicide - self-destruct.
- [x] kill - Kill other player.
- [ ] save - Map save.
- [ ] say - Server chat.
- [ ] nick - Set nickname.
- [x] time - Show server time.
- [x] team - Set PvP team.
- [ ] banlist - Show server ban list.
- [x] host - host.
- [ ] gameover - vote....

### Building a Jar

`gradlew jar` / `./gradlew jar`

Output jar should be in `build/libs`.


### Installing

Simply place the output jar from the step above in your server's `config/plugins` directory and restart the server.
List your currently installed plugins by running the `plugins` command.