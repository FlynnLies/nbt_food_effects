package flynnlies.nbt_food_effects.mixin;

import flynnlies.nbt_food_effects.NBTFoodEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// /give @p beef{CustomPotionEffects:[{Effect:"minecraft:levitation", Duration:300, Chance: 0.5}]}

@Mixin(LivingEntity.class)
public abstract class FoodDataMixin {

    @Inject(method = "addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    protected void applyEffectFromNBT(ItemStack stack, Level level, LivingEntity entity, CallbackInfo ci){
        if (!stack.isEdible()) return;
        if (stack.hasTag()){
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(NBTFoodEffects.NBTID, 9)){
                RandomSource source = entity.getRandom();

                ListTag foodEffectsTag = tag.getList(NBTFoodEffects.NBTID, 10);
                List<Tuple<MobEffectInstance, Float>> effects = NBTFoodEffects.getEffects(foodEffectsTag);

                for (Tuple<MobEffectInstance, Float> effectTuple : effects) {
                    MobEffectInstance effectInstance = effectTuple.getA();
                    float chance = effectTuple.getB();

                    if (source.nextFloat()%1 < chance){
                        entity.addEffect(effectInstance);

                    }
                }

            }
        }


    }



}
