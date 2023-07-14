package flynnlies.nbt_food_effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V", shift = At.Shift.AFTER))
    protected void applyEffectFromNBT(Item p_38713_, ItemStack p_38714_, LivingEntity entity, CallbackInfo ci){
        String effect_id = "minecraft:levitation";
        MobEffect effect = Registry.MOB_EFFECT.get(new ResourceLocation(effect_id));
        if (effect != null) {
            entity.addEffect(new MobEffectInstance(effect, 100));
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Effect '" + effect_id + "' does not exist.");
        }

    }



}
