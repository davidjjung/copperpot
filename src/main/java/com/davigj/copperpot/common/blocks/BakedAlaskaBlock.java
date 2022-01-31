package com.davigj.copperpot.common.blocks;

import com.davigj.copperpot.core.registry.CopperPotBlocks;
import com.davigj.copperpot.core.registry.CopperPotItems;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.registry.ModItems;
import vectorwing.farmersdelight.utils.tags.ModTags;

import java.util.function.Supplier;

public class BakedAlaskaBlock extends Block {
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
    public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 5.0D, 12.0D);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    ;

//    public static final IntegerProperty BITES = BlockStateProperties.BITES_0_6;
//    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(
//            1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(
//                    3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(
//                            5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(
//                                    7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(
//                                            9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(
//                                                    11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(
//                                                            13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};

//    protected static final VoxelShape[] NORTH_SHAPES = new VoxelShape[]{Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 12.0D), Block.makeCuboidShape(6.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 12.0D), Block.makeCuboidShape(8.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 12.0D), Block.makeCuboidShape(10.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 12.0D)};
//
//    protected static final VoxelShape[] EAST_SHAPES = new VoxelShape[]{Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 12.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            6.0D, 12.0D, 5.0D, 12.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            8.0D, 12.0D, 5.0D, 12.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            10.0D, 12.0D, 5.0D, 12.0D)};
//
//    protected static final VoxelShape[] SOUTH_SHAPES = new VoxelShape[]{Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 12.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 10.0D, 5.0D, 12.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 8.0D, 5.0D, 12.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 6.0D, 5.0D, 12.0D)};
//
//    protected static final VoxelShape[] WEST_SHAPES = new VoxelShape[]{Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 12.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 10.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 8.0D), Block.makeCuboidShape(4.0D, 0.0D,
//            4.0D, 12.0D, 5.0D, 6.0D)};

    public BakedAlaskaBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isClientSide) {
            ItemStack itemstack = player.getItemInHand(handIn);
            if (this.eatSlice(worldIn, pos, state, player).consumesAction()) {
                return ActionResultType.SUCCESS;
            }
            if (itemstack.isEmpty()) {
                return ActionResultType.CONSUME;
            }
        }

        return this.eatSlice(worldIn, pos, state, player);
    }

    private ActionResultType eatSlice(IWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!player.canEat(false)) {
            return ActionResultType.PASS;
        } else {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(3, 0.2F);
            int i = state.getValue(BITES);
            if (i < 3) {
                world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i + 1)), 3);
            } else {
                world.removeBlock(pos, false);
            }
            if (!world.isClientSide()) {
                double random = Math.random();
                if (random < 0.33) {
                    player.addEffect(new EffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "sugar_rush")).get(), 200, 2));
                } else if (random < 0.66 && random > 0.33) {
                    player.addEffect(new EffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "vanilla_scent")).get(), 100));
                } else if (random > 0.66 && random < 0.77) {
                    player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, 1));
                } else {
                    player.heal(4.0F);
                }
            }
            return ActionResultType.SUCCESS;
        }
    }

    private static Supplier<Effect> getCompatEffect(String modid, ResourceLocation effect) {
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.POTIONS.getValue(effect) : () -> null);
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.below()).getMaterial().isSolid();
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, BITES});
    }


    public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        return (4 - blockState.getValue(BITES)) * 2;
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }
}