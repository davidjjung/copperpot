package com.davigj.copperpot.common.blocks;

import com.davigj.copperpot.core.CopperPotMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MeringueBlock extends BreakableBlock {
    protected static final VoxelShape SHAPES = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 10.0D, 15.0D);

    Logger LOGGER = LogManager.getLogger(CopperPotMod.MOD_ID);

    public MeringueBlock(Properties properties) {
        super(properties);
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES;
    }

    private static boolean hasSlideEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TNTEntity || entity instanceof BoatEntity;
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (this.isRiding(pos, entityIn)) {
            this.Ride(entityIn);
        }
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    private boolean isRiding(BlockPos pos, Entity entity) {
        Vector3d vector3d = entity.getMotion();
        if (Math.abs(vector3d.y) > 0.1) {
            return true;
        }
//        if (entity.isOnGround()) {
//            return false;
//        } else if (entity.getPosY() > (double)pos.getY() + 0.9375D - 1.0E-7D) {
//            return false;
//        } else if (entity.getMotion().y >= -0.08D) {
//            return false;
//        } else {
//            double d0 = Math.abs((double)pos.getX() + 0.5D - entity.getPosX());
//            double d1 = Math.abs((double)pos.getZ() + 0.5D - entity.getPosZ());
//            double d2 = 0.4375D + (double)(entity.getWidth() / 2.0F);
//            return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
//        }
        return false;
    }

    private void Ride(Entity entity) {
        Vector3d vector3d = entity.getMotion();
        double d0 = -0.15D / vector3d.y;
        if (entity instanceof LivingEntity) {
            if (((LivingEntity) entity).isJumping && vector3d.y > 0) {
                double amp = 1;
                if (entity instanceof PlayerEntity) {
                    EffectInstance effect = ((PlayerEntity) entity).getActivePotionEffect(Effects.SPEED);
                    EffectInstance effect2 = ((PlayerEntity) entity).getActivePotionEffect(Effects.JUMP_BOOST);
                    if (effect != null) {
                        amp = effect.getAmplifier() + 1;
                    }
                    if (effect2 != null) {
                        amp = amp + (effect2.getAmplifier() + 1);
                    }
                }
                if (Math.abs(vector3d.y) < 0.3 + (amp * 0.1D)) {
                    entity.setMotion(new Vector3d(vector3d.x * d0 + 0.1, vector3d.y + (amp * 0.07D), vector3d.z * d0 + 0.1));
                } else {
                    entity.setMotion(new Vector3d(vector3d.x * d0, vector3d.y, vector3d.z * d0));
                }
                if (Math.random() > 0.99) {
                    entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 0.4F, 1.1F);
                }
            } else if (vector3d.y < 0D) {
                entity.setMotion(new Vector3d(vector3d.x * d0, -0.1D, vector3d.z * d0));
                if (Math.random() > 0.99) {
                    entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 0.4F, 1.1F);
                }
            }
            entity.fallDistance = 0.0F;
        }
    }

//    private void slideEffects(World world, Entity entity) {
//        if (hasSlideEffects(entity)) {
//            if (world.rand.nextInt(5) == 0) {
//                entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
//            }
//            if (!world.isRemote && world.rand.nextInt(5) == 0) {
//                world.setEntityState(entity, (byte)53);
//            }
//        }
//    }

    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        entityIn.playSound(SoundEvents.BLOCK_SLIME_BLOCK_FALL, 1.0F, 1.0F);
        if (!worldIn.isRemote) {
            worldIn.setEntityState(entityIn, (byte) 54);
        }

        if (entityIn.onLivingFall(fallDistance, 0.3F)) {
            entityIn.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }
    }
}
