# MobPlugin 

[![CircleCI](https://circleci.com/gh/PikyCZ/MobPlugin/tree/master.svg?style=shield&circle-token=)](https://circleci.com/gh/PikyCZ/MobPlugin/tree/master)

![mobplugin](https://github.com/PikyCZ/MobPlugin/blob/master/images/MobPlugin.png)

**Config**
```yml
# general settings
entities:
  mob-ai: true
  auto-spawn-tick: 300 
  spawn-animals: true
  spawn-mobs: true
  worlds-spawn-disabled:

# max spawn settings
max-spawns:
  bat: 0
  blaze: 0
  cave-spider: 0
  chicken: 2
  cow: 1
  creeper: 0
  donkey: 0
  enderman: 0
  ghast: 0
  horse: 0
  husk: 0 
  iron-golem: 0
  mooshroom: 0
  mule: 0
  ocelot: 1
  pig: 2
  pig-zombie: 0
  rabbit: 1
  stray: 1
  silverfish: 0
  sheep: 2
  skeleton: 1
  skeleton-horse: 0
  snow-golem: 0
  spider: 0
  wolf: 1
  zombie: 1
  zombie-horse: 0
  zombie-villager: 0
```

# Commands
| Command | Usage | Description |
| ------- |  ----- | ----------- |
| `/mob` | `/mob` | Display list of commands|
| `/mob spawn <mob_name>` | `/mob spawn <mob_name>` | Spawn mob (mob_name write always with a capital letter)
| `/mob removemobs` | `/mob removemobs` | Remove all living mobs|
| `/mob removeitems` | `/mob removeitems` | Remove all items from all levels (ground)|

# Permissions
```yml
 mob-plugin.mob:
    default: op
  mob.plugin.removemobs:  
    default: op
  mob-plugin.removeitems:
    default: op  
  ```

# Download

**If you want this plugin, please download from Circle CI!**

__[Jar Download at Circle CI](https://circleci.com/gh/PikyCZ/MobPlugin/tree/master/)__ (**login required**)

# Contributed code since 2016
* [PikyCZ](//github.com/PikyCZ)
* [Creeperface01](//github.com/Creeperface01)
* [woofie](//https://github.com/woofie)
* [caspervanneck](//https://github.com/caspervanneck)
