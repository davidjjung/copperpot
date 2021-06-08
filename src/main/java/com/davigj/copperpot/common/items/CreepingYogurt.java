package com.davigj.copperpot.common.items;

import com.davigj.copperpot.core.CopperPotMod;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class CreepingYogurt extends Item{

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
        return stack;
    }

    public void pewpew(LivingEntity player, World worldIn) {
        BlockPos pos = player.getPosition();
        Iterator effects = player.getActivePotionEffects().iterator();
        Direction direction = player.getHorizontalFacing();
        ArrayList<EffectInstance> storedEffects = new ArrayList<>();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            if (effect.getDuration() > 10) {
                storedEffects.add(effect);
            }
        }
        int xpMult = 0;
        AreaEffectCloudEntity dummy = new AreaEffectCloudEntity(worldIn, pos.getX(), pos.getY(), pos.getZ());
        for(int kablooie = 0; kablooie < storedEffects.size(); kablooie++) {
            AreaEffectCloudEntity steam = new AreaEffectCloudEntity(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            steam.setDuration(100);
            steam.setRadius(0.2F);
            steam.addEffect(new EffectInstance(storedEffects.get(kablooie).getPotion(), (int) ((storedEffects.get(kablooie).getDuration()) * 0.6), storedEffects.get(kablooie).getAmplifier()));
            for (LivingEntity living : steam.world.getEntitiesWithinAABB(LivingEntity.class, steam.getBoundingBox().grow(2.0D, 1.0D, 2.0D))) {
                living.addPotionEffect(new EffectInstance(storedEffects.get(kablooie).getPotion(), (int) ((storedEffects.get(kablooie).getDuration()) * 0.6), storedEffects.get(kablooie).getAmplifier()));
            }
            worldIn.addEntity(steam);
            xpMult++;
        }
        player.addVelocity(1.8 * direction.getXOffset() + (0.2 * xpMult)* (1.0D - player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)),
                0.8, 1.8 * direction.getZOffset() + (0.2 * xpMult)* (1.0D - player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
        worldIn.createExplosion(dummy, (DamageSource) null, (ExplosionContext) null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.5F, false, Explosion.Mode.NONE);
        player.clearActivePotions();
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_CREEPER_PRIMED;
    }
}
