package flynnlies.nbt_food_effects;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Mod(NBTFoodEffects.MODID)
public class NBTFoodEffects {
    public static final String MODID = "nbt_food_effects";
    public static final String NBTID = "CustomPotionEffects";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

    private static final List<Tuple<MobEffectInstance, Float>> EMPTY = new ArrayList<>();

    public static List<Tuple<MobEffectInstance, Float>> getEffects(ListTag food_effects){
        if (food_effects.getElementType() != 10 || food_effects.isEmpty()) return EMPTY;

        List<Tuple<MobEffectInstance, Float>> effects = new ArrayList<>(food_effects.size());
        for (int i = 0; i < food_effects.size(); i ++) {
            CompoundTag tag = food_effects.getCompound(i);
            MobEffectInstance effect = load(tag);
            if (effect != null){
                effects.add(new Tuple<>(
                        new MobEffectInstance(effect), // maybe avoid the timer getting stuck at 0:0
                        getChance(food_effects, i)
                ));
            } else {
                NBTFoodEffects.LOGGER.warn("Invalid nbt '" + food_effects.getCompound(i) +  "', ignoring");
            }
        }

        return effects;
    }

    private static float getChance(ListTag food_effects, int i){
        CompoundTag tag = food_effects.getCompound(i);
        return switch (tag.getTagType("Chance") ) {
            case 1 ->  (float)tag.getByte("Chance");
            case 2 ->  tag.getShort("Chance");
            case 3 ->  tag.getInt("Chance");
            case 4 ->  (float) tag.getLong("Chance");
            case 5 ->  tag.getFloat("Chance");
            case 6 ->  (float) tag.getDouble("Chance");
            default->  1.0f;
        };
    }

    private static MobEffectInstance load(CompoundTag p_19561_) {
        int i = p_19561_.getByte("Id") & 0xFF;
        MobEffect mobeffect = net.minecraftforge.common.ForgeHooks.loadMobEffect(p_19561_, "Effect", null);
        if (mobeffect == null){
            mobeffect = net.minecraftforge.common.ForgeHooks.loadMobEffect(p_19561_, "forge:id", null);
            if (mobeffect == null){
                mobeffect = MobEffect.byId(i);
            }
        }

        return mobeffect == null ? null : MobEffectInstance.loadSpecifiedEffect(mobeffect, p_19561_);
    }

    public static void addEffectTooltip(ItemStack stack, List<Component> p_43557_, float p_43558_) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(NBTID, 9)) return;
        ListTag listtag = tag.getList(NBTID, 10);

        List<Tuple<MobEffectInstance, Float>> list = getEffects(listtag);
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (list != null && !list.isEmpty()) {
            for(int i = 0; i<list.size(); i++) {
                MobEffectInstance mobeffectinstance = list.get(i).getA();
                float effectchance = list.get(i).getB();

                MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
                MobEffect mobeffect = mobeffectinstance.getEffect();
                Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(mobeffectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (mobeffectinstance.getAmplifier() > 0) {
                    mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
                }

                if (mobeffectinstance.getDuration() > 20) {
                    mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, p_43558_));
                }

                if (effectchance < 0.001) {
                    mutablecomponent.append(" <0.1%");
                } else if (effectchance < 0.1) {
                    double percentage = effectchance * 100;
                    if (percentage % 1.0 > 0.0) {
                        mutablecomponent.append(String.format(" %.1f%%", percentage));
                    } else {
                        mutablecomponent.append(String.format(" %.0f%%", percentage));
                    }
                } else if (effectchance < 1) {
                    mutablecomponent.append(" " + (int) (effectchance * 100) + "%");
                }


                p_43557_.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
            }
        }


        if (!list1.isEmpty()) {
            p_43557_.add(CommonComponents.EMPTY);
            p_43557_.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

            for(Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    p_43557_.add(Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    p_43557_.add(Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }

    }
}

