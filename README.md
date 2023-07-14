# NBT Food Effects
A minecraft mod that lets you add CustomPotionEffects to food items, like with potions. Will also show a tooltip with all the effects on an item, also like with potions.  

Examples:
```
/give @p beef{CustomPotionEffects:[{Effect:"minecraft:levitation", Duration:300, Chance: 0.5}]}
/give @p beef{CustomPotionEffects:[{"forge:id":"minecraft:levitation", Duration:300, Chance: 0.5}]}
```
Will each give you a raw beef that has a 50% chance to give you levitation for 300 ticks (=15 seconds).
The chance mechanic is only available for food items mind you, since I added that cause it seemed useful.

```
/give @p cookie{CustomPotionEffects:[{Id:25}]}
```
This cookie will have levitation for 0 ticks. I'd by the way recommend using `Effect` instead of `Id`, but if you want you can find a list of the vanilla effect id's here: https://minecraft.fandom.com/wiki/Java_Edition_data_values#Effects

```
/give @p cooked_chicken{CustomPotionEffects:[{Id:25, Effect:"mod:mod_effect", "forge:id":"minecraft:haste"}]}
```
Will always give a cooked chicken with a the `mod_effect` from the mod `mod`. If that does not exist will give it haste. Only if neither `Effect` nor `forge:id` are valid will the effct from `Id` be applied.

And finally here is an example with all relevant options that exist:
```
/give @p bread{CustomPotionEffects:[{
  Id:25, Effect:  "mod:mod_effect", 
  "forge:id":     "minecraft:haste",
  Effect:         "mod:mod_effect",
  Chance:         0.5,   
  Amplifier       1,
  Duration        60,
  Ambient         0b,
  ShowParticles   0b,
  ShowIcon        1b
}]}
```

But you can also mostly just look at this: https://minecraft.fandom.com/wiki/Player.dat_format#Potion_Effects

Or give yourself an effect and then use `/data get`. This will show you what an effect will end up looking like on you.
```
/effect give @p minecraft:darkness 600
/data get entity @p ActiveEffects
```
