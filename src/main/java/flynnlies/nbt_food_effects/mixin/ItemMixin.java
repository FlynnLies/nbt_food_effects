package flynnlies.nbt_food_effects.mixin;

import flynnlies.nbt_food_effects.NBTFoodEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", at = @At("HEAD"))
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag, CallbackInfo ci) {
        if (stack.isEdible() && stack.hasTag()){

            CompoundTag tag = stack.getTag();
            assert tag != null;

            if (tag.contains("CustomPotionEffects") && !tag.getList("CustomPotionEffects", 10).isEmpty()){
                NBTFoodEffects.addEffectTooltip(stack, list, 1.0F);
            }
        }
    }
}
