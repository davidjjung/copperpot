package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.CopperPotMod;
import com.davigj.copperpot.core.utils.TextUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// import com.minecraftabnormals.savageandravage.core.registry.SRAttributes;

public class CreepingYogurt extends Item {

    public CreepingYogurt(Item.Properties properties) {
        super(properties);
    }

    Logger LOGGER = LogManager.getLogger(CopperPotMod.MOD_ID);

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        if (!worldIn.isClientSide) {
            // TODO: Check how slabfish_snacks works--does it emulate the slabfish actually *using* the item
            pewpew(entityLiving, worldIn);
        }
        if (stack.isEmpty()) {
            return new ItemStack(Items.BOWL);
        } else {
            ItemStack itemstack = new ItemStack(Items.BOWL);
            PlayerEntity playerentity = (PlayerEntity) entityLiving;
            if (!playerentity.inventory.add(itemstack)) {
                playerentity.drop(itemstack, false);
            }
        }
        return stack;
    }

    public void pewpew(LivingEntity player, World worldIn) {
        BlockPos pos = player.blockPosition();
        Iterator effects = player.getActiveEffects().iterator();
        Direction direction = player.getDirection();
        ArrayList<EffectInstance> storedEffects = new ArrayList<>();
        while (effects.hasNext()) {
            EffectInstance effect = (EffectInstance) effects.next();
            if (effect.getDuration() > 10) {
                storedEffects.add(effect);
            }
        }
        int xpMult = 0;
        AreaEffectCloudEntity dummy = new AreaEffectCloudEntity(worldIn, pos.getX(), pos.getY(), pos.getZ());
        for (EffectInstance storedEffect : storedEffects) {
            AreaEffectCloudEntity steam = new AreaEffectCloudEntity(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            steam.setDuration(200);
            steam.setRadius(0.2F);
            steam.addEffect(new EffectInstance(storedEffect.getEffect(), (int) ((storedEffect.getDuration()) * 0.6), storedEffect.getAmplifier()));
            for (LivingEntity living : steam.level.getEntitiesOfClass(LivingEntity.class, steam.getBoundingBox().inflate(2.0D, 1.0D, 2.0D))) {
                living.addEffect(new EffectInstance(storedEffect.getEffect(), (int) ((storedEffect.getDuration()) * 0.6), storedEffect.getAmplifier()));
            }
            worldIn.addFreshEntity(steam);
            xpMult++;
        }
        double resist = 0;
//         TODO: Find out how to implement SR explosive damage reduction
//        if (ModList.get().isLoaded("savageandravage")) {
//            resist = (double)player.getAttributeValue(SRAttributes.EXPLOSIVE_DAMAGE_REDUCTION.get());
//            LOGGER.debug(resist);
//        }
        resist = Math.max(resist, player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

        player.push((1.8 * (1.0D - resist)) * direction.getStepX() + (0.2 * xpMult),
                0.8, (1.8 * (1.0D - resist)) * direction.getStepZ() + (0.2 * xpMult));
        worldIn.explode(dummy, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.6F, false, Explosion.Mode.NONE);
        player.removeAllEffects();
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.CREEPER_PRIMED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IFormattableTextComponent tip = TextUtils.getTranslation("tooltip.creeping_yogurt.tip");
        tooltip.add(tip.withStyle(TextFormatting.GREEN));
    }
}
