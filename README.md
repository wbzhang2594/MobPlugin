# MobPlugin 

Development: **[PikyCZ](https://github.com/PikyCZ)**

[![CircleCI](https://circleci.com/gh/PikyCZ/MobPlugin/tree/master.svg?style=shield&circle-token=)](https://circleci.com/gh/PikyCZ/MobPlugin/tree/master)

![mobplugin](https://github.com/PikyCZ/MobPlugin/blob/master/images/MobPlugin.png)

MobPlugin is a plugin that implements the mob entities for MCPE including movement, aggression etc.

## Notice
This plug-in is in development.

# Credits
Credits go to Team-SW! They have a nice plugin already made. I used it and adapt it for 1.1.x

# Plugin Example configuration
Place this plugin jar file to your Nukkit's home directory "${NUKKIT_HOME}/plugin".
#### Example:
  /usr/share/nukkit/plugins/MobPlugin-1.1.0-SNAPSHOT.jar
  
Then you have to create a folder in plugin folder with the name of the plugin and place the config.yml there ("${NUKKIT_HOME}/plugin/MobPlugin").
#### Example:
  /usr/share/nukkit/plugins/MobPlugin/config.yml

When Nukkit Server starts up and the plugin is activated, the config.yml is read and evaluated by the plugin.

## config.yml example

The following configuration sets mobs AI enabled and the auto spawn task will be triggered all 300 ticks.

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
  chicken: 0
  cow: 0
  creeper: 0
  donkey: 0
  enderman: 0
  ghast: 0
  horse: 0
  husk: 0 
  iron-golem: 0
  mooshroom: 0
  mule: 0
  ocelot: 0
  pig: 0
  pig-zombie: 0
  rabbit: 0
  stray: 0
  silverfish: 0
  sheep: 0
  skeleton: 0
  skeleton-horse: 0
  snow-golem: 0
  spider: 0
  wolf: 1
  zombie: 0
  zombie-horse: 0
  zombie-villager: 0
```

# Commands
| Command | Usage | Description |
| ------- |  ----- | ----------- |
| `/mob` | `/mob` | Display list of commands|
| `/mob spawn <mob_name>` | `/mob spawn <mob_name>` | Spawn mob (mob_name write always with a capital letter.. For example: Pig,Zombie)
| `/mob removemobs` | `/mob removemobs` | Remove all living mobs|
| `/mob removeitems` | `/mob removeitems` | Remove all items from all levels (ground)|

# Permissions
```yml
 mob-plugin.mob:
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
