# About
MobStacker is a Bukkit/Spigot plugin for 1.15+ that stacks mobs in a highly efficient way to decrease server and FPS lag.

## Features
* Clusters of configurable mobs are removed and replaced with a single named entity
* Name can be customized and hidden when not directly looked at
* Damage sources can be configured to harm entire stacks
 * To prevent issues, stacks which die to `VOID` damage are always killed
* Stacks can drop loot for all entities when killed by a source which damages the entire stack
 * To prevent issues, stacks which die to `VOID` damage do not drop additional loot
* Stacked sheep and mooshrooms can be sheared
* Stacked animals can be bred
* Stacked chickens can be configured to drop stacked eggs
* Stacked creepers can be configured to explode more powerfully

# For Developers
## Compiling
MobStacker is compiled using Maven. Clone the repository and run `mvn clean install`.
## Maven and other dependency management systems [![](https://jitpack.io/v/Jikoo/MobStacker.svg)](https://jitpack.io/#Jikoo/MobStacker)
Should you wish to use MobStacker as a dependency, it is available via JitPack.
```
    <dependency>
      <groupId>com.github.Jikoo</groupId>
      <artifactId>MobStacker</artifactId>
      <version>-SNAPSHOT</version>
    </dependency>
```
Be sure to add the JitPack repository to your repositories section.
```
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
```
