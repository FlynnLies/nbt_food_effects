# NBT Food Effects
A forge mod that lets you add CustomPotionEffects to food items, like with potions. Will also show a tooltip with all the effects on an item, also like with potions.  

Basics
----------

```
/give @p beef{CustomPotionEffects:[{
  Effect:"minecraft:levitation", 
  Duration:300, 
  Chance: 0.5}]}
```
```
/give @p beef{CustomPotionEffects:[{
  "forge:id":"minecraft:levitation", 
  Duration:300, 
  Chance: 0.5
}]}
```

Will each give you a raw beef that has a 50% chance to give you levitation for 300 ticks (=15 seconds).
The chance mechanic is **only available for food items** not potions mind you, since I added that cause it seemed useful just like `Effect`. For potions you'd need to use the numeric id for `Id` or `forge:id`.

```
/give @p cookie{CustomPotionEffects:[{Id:25}]}
```
This cookie will have levitation for 0 ticks. I'd by the way recommend using `Effect` instead of `Id`, but if you want you can find a list of the vanilla effect id's here: https://minecraft.fandom.com/wiki/Java_Edition_data_values#Effects


You can add multiple effects like follows, just make sure to put commas in the correct places and so on.

```
/give @p beef{CustomPotionEffects:[
  {Effect:"minecraft:levitation", Duration:300},
  {Effect:"minecraft:blindness", Duration:60, Chance: 0.1}
]}
```

You probably want to write the command outside of minecraft first and just copy it in with multiple effects, since that can get tedious very fast if you make a mistake.


Remove Effect From Item
---

Currently not possible, but is a planned feature. I will have to re-write some code though.

The idea is if you have let's say a pufferfish, you might want to remove the nausea it naturally gives you or shorten it's duration.

On the other hand if you wanted to *make it longer*, just give it a higher `Duration` than it normally has and yours will replace the normal effect. That already works.

Fallback Order
----------

```
/give @p cooked_chicken{CustomPotionEffects:[{
  Id:25, 
  Effect:"mod:mod_effect", 
  "forge:id":"minecraft:haste", Duration: 200
}]}
```
This cooked chicken will have the effect `mod_effect` from the mod `mod`. However if that does not exist will have `minecraft:haste`. 

If neither `Effect` nor `forge:id` were valid effect resource locations, the effect from `Id` be applied.

Setting Curative Items
---

```
/give @p cooked_chicken{CustomPotionEffects: [{Effect: "minecraft:blindness", Duration: 600, CurativeItems: [{id: "minecraft:cod", Count: 1b}]}]}
```
The blindness that this chicken gives you can only be cleared by eating a raw cod. 

**Important:** the make sure to use lower letters for this `id` tag. Unlike the other it is not capitalized.

Note that the eaten cod **must not** have any nbt tags. This will become obvious in the next example.

```
/give @p apple{CustomPotionEffects: [
  { Effect: "minecraft:blindness",  
    Duration: 200,
    CurativeItems: [
      {id: "minecraft:potion", Count: 1b, tag: {Potion: "minecraft:water"}},
      {id: "minecraft:cod", Count: 1b}
    ] }
]}
```
Here you get an apple that gives you blindness which can be cured by drinking a bottle of water or eating a raw cod. Drinking any other potion will not cure the effect.

If you want to add an item to `CurativeItems`, you can simply hold the item in your hand and run 

```
/data get entity @p SelectedItem
```

This will give you what the item that you are holding looks like as NBT data and you can simply copy it in.

Note that the items must have **exactly** the same value as what you set, meaning they **have to be able to stack**. You can get around this limitation somehwat by explicitly adding things that don't.

So for example if you wanted to be able to drink any nightvision potion to remove the effect of the apple from above, you'd have to look add an entry for **every variation** that exists. This also goes for different amounts of `Duration` and any other tag that might be set.

This example needs to be **run in a command block** since it's too long for the normal console:

```
/give @p apple{CustomPotionEffects: [
  { Effect: "minecraft:blindness",  
    Duration: 2000,
    CurativeItems: [
      {
        id: "minecraft:potion", Count: 1b, 
        tag: {CustomPotionEffects:[{"forge:id":"minecraft:night_vision"}]} 
      },
      {
        id: "minecraft:potion", Count: 1b, 
        tag: {CustomPotionEffects:[{"forge:id":"minecraft:night_vision", Duration:600}]}
      }
    ]
  }
]}
```

Try the following two potions.

These will clear the effect:
```
/give @p minecraft:potion{CustomPotionEffects:[{"forge:id":"minecraft:night_vision", Duration:600}]}

/give @p minecraft:potion{CustomPotionEffects:[{"forge:id":"minecraft:night_vision"}]}
```

And these will not:
```
/give @p minecraft:potion{CustomPotionEffects:[{"forge:id":"minecraft:night_vision", Duration:500}]}

/give @p minecraft:potion{CustomPotionEffects:[{"forge:id":"minecraft:night_vision", Duration:0}]}
```

Summary
---------
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
  ShowIcon        1b,
  CurativeItems: [
    {id: "minecraft:potion", Count: 1b, tag: {Potion: "minecraft:water"}},
    {id: "minecraft:milk_bucket", Count: 1b}
  ]
}]}
```

But you can also mostly just look at this: https://minecraft.fandom.com/wiki/Player.dat_format#Potion_Effects

Or give yourself an effect and then use `/data get`. This will show you what an effect should look like as NBT. You can also use it for debugging for NBT data.
```
/effect give @p minecraft:darkness 600
/data get entity @p ActiveEffects
```
