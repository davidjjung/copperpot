package com.davigj.copperpot.common.blocks;

import com.davigj.copperpot.common.tile.CopperPotTileEntity;
import com.davigj.copperpot.core.CopperPotMod;
import com.davigj.copperpot.core.registry.CopperPotTileEntityTypes;
import com.davigj.copperpot.core.utils.TextUtils;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vectorwing.farmersdelight.registry.ModSounds;
import vectorwing.farmersdelight.utils.MathUtils;
import vectorwing.farmersdelight.utils.tags.ModTags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CopperPotBlock extends HorizontalBlock implements IWaterLoggable {
    public static final BooleanProperty SUPPORTED = BlockStateProperties.DOWN;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 5.0D, 14.0D);
    protected static final VoxelShape SHAPE_SUPPORTED = VoxelShapes.or(SHAPE, Block.makeCuboidShape(0.0D, -1.0D, 0.0D, 16.0D, 0.0D, 16.0D));

    Logger LOGGER = LogManager.getLogger(CopperPotMod.MOD_ID);

    public CopperPotBlock(Properties builder) {
        super(builder);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(
                HORIZONTAL_FACING, Direction.NORTH)).with(SUPPORTED, false)).with(WATERLOGGED, false).with(ENABLED, Boolean.valueOf(true)));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return (Boolean)state.get(SUPPORTED) ? SHAPE_SUPPORTED : SHAPE;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        World world = context.getWorld();
        FluidState ifluidstate = world.getFluidState(context.getPos());
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing()
                .getOpposite())).with(SUPPORTED, this.needsTrayForHeatSource(world.getBlockState(blockpos.down())))).with(
                        WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER).with(ENABLED, Boolean.valueOf(true));
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if ((Boolean)stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return facing == Direction.DOWN ? (BlockState)stateIn.with(SUPPORTED, this.needsTrayForHeatSource(facingState)) : stateIn;
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.isIn(state.getBlock())) {
            this.updateState(worldIn, pos, state);
        }
    }

    private boolean needsTrayForHeatSource(BlockState state) {
        return state.getBlock().isIn(ModTags.TRAY_HEAT_SOURCES);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult result) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof CopperPotTileEntity) {
                ItemStack serving = ((CopperPotTileEntity)tile).useHeldItemOnMeal(player.getHeldItem(handIn));
                if (serving != ItemStack.EMPTY) {
                    if (!player.inventory.addItemStackToInventory(serving)) {
                        player.dropItem(serving, false);
                    }
                    worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS, 1.0F, 1.0F);
                } else {
                    NetworkHooks.openGui((ServerPlayerEntity)player, (CopperPotTileEntity)tile, pos);
                }
            }
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.SUCCESS;
        }
    }

    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        ItemStack itemstack = super.getItem(worldIn, pos, state);
        CopperPotTileEntity tile = (CopperPotTileEntity)worldIn.getTileEntity(pos);
        CompoundNBT compoundnbt = tile.writeMeal(new CompoundNBT());
        if (!compoundnbt.isEmpty()) {
            itemstack.setTagInfo("BlockEntityTag", compoundnbt);
        }
        if (tile.hasCustomName()) {
            itemstack.setDisplayName(tile.getCustomName());
        }
        return itemstack;
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        this.updateState(worldIn, pos, state);
    }

    private void updateState(World worldIn, BlockPos pos, BlockState state) {
        boolean flag = !worldIn.isBlockPowered(pos);
        if (flag != state.get(ENABLED)) {
            worldIn.setBlockState(pos, state.with(ENABLED, Boolean.valueOf(flag)), 4);
        }
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof CopperPotTileEntity) {
                InventoryHelper.dropItems(worldIn, pos, ((CopperPotTileEntity)tileentity).getDroppableInventory());
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
        if (compoundnbt != null) {
//            LOGGER.debug("compoundnbt ain't null");
            CompoundNBT inventoryTag = compoundnbt.getCompound("Inventory");
            if (inventoryTag.contains("Items", 9)) {
//                LOGGER.debug("there are contain items type 9 yes good very");
                ItemStackHandler handler = new ItemStackHandler();
                handler.deserializeNBT(inventoryTag);
                ItemStack meal = handler.getStackInSlot(3);
                if (!meal.isEmpty()) {
                    IFormattableTextComponent servingsOf = meal.getCount() == 1 ? TextUtils.getTranslation("tooltip.copper_pot.single_serving", new Object[0]) : TextUtils.getTranslation("tooltip.copper_pot.many_servings", new Object[]{meal.getCount()});
                    tooltip.add(servingsOf.mergeStyle(TextFormatting.GRAY));
                    IFormattableTextComponent mealName = meal.getDisplayName().deepCopy();
                    tooltip.add(mealName.mergeStyle(meal.getRarity().color));
                }
            }
        } else {
            IFormattableTextComponent empty = TextUtils.getTranslation("tooltip.copper_pot.empty", new Object[0]);
            tooltip.add(empty.mergeStyle(TextFormatting.GRAY));
        }

    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(new Property[]{HORIZONTAL_FACING, SUPPORTED, WATERLOGGED, ENABLED});
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof CopperPotTileEntity) {
                ((CopperPotTileEntity)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    // controls the sound
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof CopperPotTileEntity && ((CopperPotTileEntity)tileentity).isAboveLitHeatSource()) {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY();
            double d2 = (double)pos.getZ() + 0.5D;
            if (rand.nextInt(10) == 0) {
                worldIn.playSound(d0, d1, d2, (SoundEvent) ModSounds.BLOCK_COOKING_POT_BOIL.get(), SoundCategory.BLOCKS, 0.4F, rand.nextFloat() * 0.2F + 0.9F, false);
            }
        }
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof CopperPotTileEntity) {
            ItemStackHandler inventory = ((CopperPotTileEntity)tile).getInventory();
            return MathUtils.calcRedstoneFromItemHandler(inventory);
        } else {
            return 0;
        }
    }

    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ((TileEntityType) CopperPotTileEntityTypes.COPPER_POT_TILE.get()).create();
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
}
