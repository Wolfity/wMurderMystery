# wMurderMystery
A Murder Mystery minigame

### **Introduction**
Murder Mystery is a game where you will be randomly assigned to one of three roles;
Murderer, Detective, or Innocent. This version of murder mystery also includes 
a random potion effect shop, for 2 gold you can either get lucky or unlucky!

Murderer
If you have this role, your goal is to kill all the players before the timer runs out without getting killed. 
You will be spawned with a sword.

Detective
As a detective, your goal is to identify the murderer and kill him.
 You get spawned with a bow, every 20 seconds (configurable), you get a new arrow. 

Innocent
As an innocent, your main goal is to stay alive. 
You can do this by either hiding or trying to kill the murderer. 
For every 10 gold you collect, you get a bow and 1 arrow. 

### **How to set it up**
**World Spawn**\
Setting up is straightforward. First, you have to set a world spawn. 
You can do that by typing the command [/mm setworldspawn]. 

**Lobby Spawn**\
After you’ve set the world spawn, you can set a waiting room spawn. 
The command for that is [/mm setlobby arenaName]. 

**Player Spawns**\
Next up is setting the player spawns. 
You can set as many as you want by using the command [/mm setspawn arenName].
 You must always have the same amount or more spawns than there are players.

You can delete a player spawn by standing in the exact location of the spawn point and typing
 [/mm deletespawn arenaName]

**Gold Generator Spawns**\
To create gold generator spawns, you simply use the command [/mm setgoldspot arenaName].

To delete a gold generator, you need to stand in the exact spot you placed it.
After you stand on the same spot, you execute the command [/mm deletegoldspot arenaName]

**Gold Shop NPC’s**\n
You can create gold shop NPC’s by typing the command [/mm setnpcshop arenaName], t
hat’s it! To delete a shop NPC, you stand in the same spot as the NPC you would like to delete and 
type [/mm deletenpcshop arenaName]

### **Gold Generators**
Gold Generators generate gold every x seconds (configurable). 
Innocents and Detectives can collect this gold to get extra arrows and a bow. 

### **Shop NPC's**
By right-clicking a shop NPC, you can get a random potion effect; this effect can be either a positive effect or a negative effect. Using this feature costs two gold.


### **Commands**
`/mm createarena <arenaName>` Creates a new arena\
`/mm deletearena <arenaName>` Deletes an existing arena\
`/mm join <arenaName>` Join an arena\
`/mm leave` Leaves an arena\
`/mm setlobby <arenaName>` Sets the lobby spawn point for an arena\
`/mm setspawn <arenaName>` Sets a game spawn point for an arena\
`/mm setworldspawn` Sets the world spawn, the spawn where players go to after the game ends, or they leave\
`/mm help` Displays the help message\
`/mm admin` Displays the admin message\
`/mm setgoldspot <arenaName>` Sets a gold generator\
`/mm setnpcshop <arenaName>` Sets a shop NPC location\
`/mm deletegoldspot <arenaName>` Deletes a gold generator\
`/mm deletespawn <arenaName>` Deletes a spawn location\
`/mm tp <arenaName>` Teleports you to an arena\
`/mm deletenpcshop <arenaName>` Deletes a gold shop