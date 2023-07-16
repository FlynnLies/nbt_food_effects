package flynnlies.nbt_food_effects.mixin;

import flynnlies.nbt_food_effects.NBTFoodEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.datafixers.util.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

// /give @p beef{CustomPotionEffects:[{Effect:"minecraft:levitation", Duration:300, Chance: 0.5}]}

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect p_21125_);

    @Inject(method = "addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    protected void addEffectFromNBT(ItemStack stack, Level level, LivingEntity entity, CallbackInfo ci){
        if (!stack.isEdible()) return;
        if (stack.hasTag()){
            List<Tuple<MobEffectInstance, Float>> effects = NBTFoodEffects.getEffects(stack, false);

            RandomSource source = entity.getRandom();
            for (Tuple<MobEffectInstance, Float> effectTuple : effects) {
                MobEffectInstance effectInstance = effectTuple.getA();
                float chance = effectTuple.getB();

                if (!level.isClientSide && source.nextFloat() % 1 < chance){
                    MobEffectInstance effect = new MobEffectInstance(effectInstance);
                    //TODO: remove effect or shorten duration from FoodProperties if duration<=0
                    entity.addEffect(effect);
                }
            }


        }
    }

    @Redirect(method = "addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getFoodProperties(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"))
    public FoodProperties modifyFoodPropertiesEffect(ItemStack stack, LivingEntity entity){

        FoodProperties properties = stack.getFoodProperties(entity);
        if (properties == null) {return null;} // cannot happen in LivingEntity.addEatEffect

        List<Tuple<MobEffectInstance, Float>> NBTEffects = NBTFoodEffects.getEffects(stack, true);
        List<Pair<MobEffectInstance, Float>> Effects = properties.getEffects();

        if (NBTEffects.isEmpty() || Effects.isEmpty()) return properties;

        FoodProperties.Builder builder =  new FoodProperties.Builder();

        // see if NBTEffects contains any effect that is the same as in foodEffects
        // if it has a duration == 0, this effect gets removed
        // if it has a duration < 0, this effect get replaced by the new effect with a Duration of (old.Duration +new.Duration)
        for (Pair<MobEffectInstance, Float> pair: Effects) {
            MobEffectInstance effect = pair.getFirst();
            boolean set_default = true;

            for (Tuple<MobEffectInstance, Float> tuple: NBTEffects) {

                MobEffectInstance nbt_effect = tuple.getA();
                if (nbt_effect.getEffect().equals(effect.getEffect())) {
                    if (nbt_effect.getDuration() == 0) {
                        set_default = false;
                        break;
                    } else if (nbt_effect.getDuration() < 0) {
                        CompoundTag save = nbt_effect.save(new CompoundTag());
                        save.putInt("Duration", effect.getDuration() + nbt_effect.getDuration());
                        builder.effect(() -> MobEffectInstance.load(save), tuple.getB());
                        set_default = false;
                        break;
                    }
                }
            }

            if (set_default){
                builder.effect(pair::getFirst, pair.getSecond());
            }
        }

        return builder.build();
    }

    @Redirect(method = "curePotionEffects(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;isCurativeItem(Lnet/minecraft/world/item/ItemStack;)Z"), remap = false)
    public boolean isCurativeItem(MobEffectInstance instance, ItemStack stack) {
        return instance.getCurativeItems().stream().anyMatch(e -> {
            CompoundTag tag = e.getTag();
            return e.sameItem(stack) && (tag == null || tag.equals(stack.getTag())); // TODO: Make it so that you can match any value, greater, lesser, ...
        });
    }

}
