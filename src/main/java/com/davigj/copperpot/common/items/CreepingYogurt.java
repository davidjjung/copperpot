package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.CopperPotMod;
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
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

// import com.minecraftabnormals.savageandravage.core.registry.SRAttributes;

public class CreepingYogurt extends Item {

    public CreepingYogurt(Item.Properties properties) {
        super(properties);
    }

    Logger LOGGER = LogManager.getLogger(CopperPotMod.MOD_ID);

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (!worldIn.isRemote) {
            pewpew(entityLiving, worldIn);
        }
        if (stack.isEmpty()) {
            return new ItemStack(Items.BOWL);
        } else {
            ItemStack itemstack = new ItemStack(Items.BOWL);
            PlayerEntity playerentity = (PlayerEntity) entityLiving;
            if (!playerentity.inventory.addItemStackToInventory(itemstack)) {
                playerentity.dropItem(itemstack, false);
            }
        }
        return stack;
    }

    public void pewpew(LivingEntity player, World worldIn) {
        BlockPos pos = player.getPosition();
        Iterator effects = player.getActivePotionEffects().iterator();
        Direction direction = player.getHorizontalFacing();
        ArrayList<EffectInstance> storedEffects = new ArrayList<>();
        while (effects.hasNext()) {
            EffectInstance effect = (EffectInstance) effects.next();
            if (effect.getDuration() > 10) {
                storedEffects.add(effect);
            }
        }
        int xpMult = 0;
        AreaEffectCloudEntity dummy = new AreaEffectCloudEntity(worldIn, pos.getX(), pos.getY(), pos.getZ());
        for (int kablooie = 0; kablooie < storedEffects.size(); kablooie++) {
            AreaEffectCloudEntity steam = new AreaEffectCloudEntity(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            steam.setDuration(200);
            steam.setRadius(0.2F);
            steam.addEffect(new EffectInstance(storedEffects.get(kablooie).getPotion(), (int) ((storedEffects.get(kablooie).getDuration()) * 0.6), storedEffects.get(kablooie).getAmplifier()));
            for (LivingEntity living : steam.world.getEntitiesWithinAABB(LivingEntity.class, steam.getBoundingBox().grow(2.0D, 1.0D, 2.0D))) {
                living.addPotionEffect(new EffectInstance(storedEffects.get(kablooie).getPotion(), (int) ((storedEffects.get(kablooie).getDuration()) * 0.6), storedEffects.get(kablooie).getAmplifier()));
            }
            worldIn.addEntity(steam);
            xpMult++;
        }
        double resist = 0;
//         TODO: Find out how to implement SR explosive damage reduction
        if (ModList.get().isLoaded("savageandravage")) {
//            resist = (double)player.getAttributeValue(SRAttributes.EXPLOSIVE_DAMAGE_REDUCTION.get());
//            LOGGER.debug(resist);
        }
        resist = Math.max(resist, player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

        player.addVelocity((1.8 * (1.0D - resist)) * direction.getXOffset() + (0.2 * xpMult),
                0.8, (1.8 * (1.0D - resist)) * direction.getZOffset() + (0.2 * xpMult));
        worldIn.createExplosion(dummy, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.6F, false, Explosion.Mode.NONE);
        player.clearActivePotions();
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_CREEPER_PRIMED;
    }
}
