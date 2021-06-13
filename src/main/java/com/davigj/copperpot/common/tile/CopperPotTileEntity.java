package com.davigj.copperpot.common.tile;

import com.davigj.copperpot.common.blocks.CopperPotBlock;
import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import com.davigj.copperpot.common.tile.container.CopperPotContainer;
import com.davigj.copperpot.common.tile.inventory.CopperPotItemHandler;
import com.davigj.copperpot.core.registry.CopperPotTileEntityTypes;
import com.davigj.copperpot.core.utils.TextUtils;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.utils.tags.ModTags;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopperPotTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity, INameable {
    public static final int MEAL_DISPLAY_SLOT = 3;
    public static final int CONTAINER_SLOT = 4;
    public static final int OUTPUT_SLOT = 5;
    public static final int INVENTORY_SIZE = 6;
    private ItemStackHandler itemHandler;
    private LazyOptional<IItemHandler> handlerInput;
    private LazyOptional<IItemHandler> handlerOutput;
    private ITextComponent customName;
    private int cookTime;
    private int cookTimeTotal;
    private ItemStack container;
    private boolean effectTrue;

    protected final IIntArray copperPotData;
    protected final IRecipeType<? extends CopperPotRecipe> recipeType;

    public CopperPotTileEntity(TileEntityType<?> tileEntityTypeIn, IRecipeType<? extends CopperPotRecipe> recipeTypeIn) {
        super(tileEntityTypeIn);
        this.itemHandler = this.createHandler();
        this.handlerInput = LazyOptional.of(() -> {
            return new CopperPotItemHandler(this.itemHandler, Direction.UP);
        });
        this.handlerOutput = LazyOptional.of(() -> {
            return new CopperPotItemHandler(this.itemHandler, Direction.DOWN);
        });
        this.copperPotData = new IIntArray() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return CopperPotTileEntity.this.cookTime;
                    case 1:
                        return CopperPotTileEntity.this.cookTimeTotal;
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        CopperPotTileEntity.this.cookTime = value;
                        break;
                    case 1:
                        CopperPotTileEntity.this.cookTimeTotal = value;
                }
            }

            public int size() {
                return 2;
            }
        };
        this.recipeType = recipeTypeIn;
        this.container = ItemStack.EMPTY;

    }

    public CopperPotTileEntity() {
        this((TileEntityType) CopperPotTileEntityTypes.COPPER_POT_TILE.get(), CopperPotRecipe.TYPE);
    }

    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
    }

    public CompoundNBT getUpdateTag() {
        return this.writeItems(new CompoundNBT());
    }

    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(this.getBlockState(), pkt.getNbtCompound());
    }

    private void inventoryChanged() {
        super.markDirty();
        this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
    }

    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.itemHandler.deserializeNBT(compound.getCompound("Inventory"));
        this.cookTime = compound.getInt("CookTime");
        this.cookTimeTotal = compound.getInt("CookTimeTotal");
        this.container = ItemStack.read(compound.getCompound("Container"));
        this.effectTrue = compound.getBoolean("EffectTrue");
        if (compound.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.getComponentFromJson(compound.getString("CustomName"));
        }

    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("CookTime", this.cookTime);
        compound.putInt("CookTimeTotal", this.cookTimeTotal);
        compound.put("Container", this.container.serializeNBT());
        compound.putBoolean("EffectTrue", this.effectTrue);
        if (this.customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        compound.put("Inventory", this.itemHandler.serializeNBT());
        return compound;
    }

    private CompoundNBT writeItems(CompoundNBT compound) {
        super.write(compound);
        compound.put("Container", this.container.serializeNBT());
        compound.put("Inventory", this.itemHandler.serializeNBT());
        return compound;
    }

    public CompoundNBT writeMeal(CompoundNBT compound) {
        if (this.getMeal().isEmpty()) {
            return compound;
        } else {
            ItemStackHandler drops = new ItemStackHandler(6);
            for (int i = 0; i < 6; ++i) {
                drops.setStackInSlot(i, i == 3 ? this.itemHandler.getStackInSlot(i) : ItemStack.EMPTY);
            }
            if (this.customName != null) {
                compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
            }
            compound.put("Container", this.container.serializeNBT());
            compound.put("Inventory", drops.serializeNBT());
            return compound;
        }
    }

    private static Supplier<Effect> getCookEffect(String modid, ResourceLocation effect) {
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.POTIONS.getValue(effect) : () -> null);
    }

    private void effectCloud(World worldIn, BlockPos pos) {
        AreaEffectCloudEntity steam = new AreaEffectCloudEntity(worldIn, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D);
        steam.setDuration(15);
        steam.setRadius(0.1F);
        String effect = this.getEffect();
        int effectDuration = this.getEffectDuration();
        int effectAmplifier = this.getEffectAmplifier();
        String[] effectName = effect.split(":", 2);
        EffectInstance effectInstance = new EffectInstance(getCookEffect(effectName[0], new ResourceLocation(effectName[0], effectName[1])).get(), effectDuration, effectAmplifier, false, true);
        for (LivingEntity living : steam.world.getEntitiesWithinAABB(LivingEntity.class, steam.getBoundingBox().grow(3.0D, 2.0D, 3.0D))) {
            living.addPotionEffect(effectInstance);
        }
        steam.addEffect(effectInstance);
        worldIn.addEntity(steam);
    }

    public void tick() {
        boolean isHeated = this.isAboveLitHeatSource();
        boolean dirty = false;
        World worldIn = this.world;
        BlockPos pos = this.pos;
        if (!this.world.isRemote) {
            if (isHeated && this.hasInput() && this.getBlockState().get(CopperPotBlock.ENABLED)) {
                CopperPotRecipe irecipe = (CopperPotRecipe) this.world.getRecipeManager().getRecipe(this.recipeType, new RecipeWrapper(this.itemHandler), this.world).orElse(null);
                if (this.canCook(irecipe)) {
                    ++this.cookTime;
                    if ((this.cookTime % 30 == 0) && this.isEffectTrue()) {
                        this.effectCloud(worldIn, pos);
                    }
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTime();
                        this.cook(irecipe);
                        dirty = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
            ItemStack meal = this.getMeal();
            if (!meal.isEmpty()) {
                if (!this.doesMealHaveContainer(meal)) {
                    this.moveMealToOutput();
                    dirty = true;
                } else if (!this.itemHandler.getStackInSlot(4).isEmpty()) {
                    this.useStoredContainersOnMeal();
                    dirty = true;
                }
            }
        } else if (isHeated) {
            this.animate();
        }
        if (dirty) {
            this.inventoryChanged();
        }
    }

    protected int getCookTime() {
        return (Integer) this.world.getRecipeManager().getRecipe(this.recipeType, new RecipeWrapper(this.itemHandler), this.world).map(CopperPotRecipe::getCookTime).orElse(100);
    }

    protected ItemStack getRecipeContainer() {
        return (ItemStack) this.world.getRecipeManager().getRecipe(this.recipeType, new RecipeWrapper(this.itemHandler), this.world).map(CopperPotRecipe::getOutputContainer).orElse(ItemStack.EMPTY);
    }

    public ItemStack getContainer() {
        return !this.container.isEmpty() ? this.container : this.getMeal().getContainerItem();
    }

    public boolean isEffectTrue() {
        return this.world.getRecipeManager().getRecipe(this.recipeType, new RecipeWrapper(this.itemHandler), this.world).map(CopperPotRecipe::getEffectTrue).orElse(false);
    }

    public String getEffect() {
        return this.world.getRecipeManager().getRecipe(this.recipeType, new RecipeWrapper(this.itemHandler), this.world).map(CopperPotRecipe::getEffect).orElse("");
    }

    public int getEffectDuration() {
        return this.world.getRecipeManager().getRecipe(this.recipeType, new RecipeWrapper(this.itemHandler), this.world).map(CopperPotRecipe::getEffectDuration).orElse(100);
    }

    public int getEffectAmplifier() {
        return this.world.getRecipeManager().getRecipe(this.recipeType, new RecipeWrapper(this.itemHandler), this.world).map(CopperPotRecipe::getEffectAmplifier).orElse(0);
    }

    private boolean hasInput() {
        // originally i < 6
        for (int i = 0; i < 3; ++i) {
            if (!this.itemHandler.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    protected boolean canCook(@Nullable IRecipe<?> recipeIn) {
        if (this.hasInput() && recipeIn != null) {
            ItemStack recipeOutput = recipeIn.getRecipeOutput();
            if (recipeOutput.isEmpty()) {
                return false;
            } else {
                ItemStack currentOutput = this.itemHandler.getStackInSlot(3);
                if (currentOutput.isEmpty()) {
                    return true;
                } else if (!currentOutput.isItemEqual(recipeOutput)) {
                    return false;
                } else if (currentOutput.getCount() + recipeOutput.getCount() <= Math.min(16, this.itemHandler.getSlotLimit(3))) {
                    return true;
                } else {
                    return currentOutput.getCount() + recipeOutput.getCount() <= Math.min(16, recipeOutput.getMaxStackSize());
                }
            }
        } else {
            return false;
        }
    }

    private void cook(@Nullable IRecipe<?> recipe) {
        if (recipe != null && this.canCook(recipe)) {
            this.container = this.getRecipeContainer();
            ItemStack recipeOutput = recipe.getRecipeOutput();
            ItemStack currentOutput = this.itemHandler.getStackInSlot(3);
            if (currentOutput.isEmpty()) {
                this.itemHandler.setStackInSlot(3, recipeOutput.copy());
            } else if (currentOutput.getItem() == recipeOutput.getItem()) {
                currentOutput.grow(recipeOutput.getCount());
            }
        }

        for (int i = 0; i < 3; ++i) {
            if (this.itemHandler.getStackInSlot(i).hasContainerItem()) {
                Direction direction = ((Direction) this.getBlockState().get(CopperPotBlock.HORIZONTAL_FACING)).rotateYCCW();
                double dropX = (double) this.pos.getX() + 0.5D + (double) direction.getXOffset() * 0.25D;
                double dropY = (double) this.pos.getY() + 0.7D;
                double dropZ = (double) this.pos.getZ() + 0.5D + (double) direction.getZOffset() * 0.25D;
                ItemEntity entity = new ItemEntity(this.world, dropX, dropY, dropZ, this.itemHandler.getStackInSlot(i).getContainerItem());
                entity.setMotion((double) ((float) direction.getXOffset() * 0.08F), 0.25D, (double) ((float) direction.getZOffset() * 0.08F));
                this.world.addEntity(entity);
            }

            if (!this.itemHandler.getStackInSlot(i).isEmpty()) {
                this.itemHandler.getStackInSlot(i).shrink(1);
            }
        }
    }

    private void animate() {
        World world = this.getWorld();
        if (world != null) {
            BlockPos blockpos = this.getPos();
            Random random = world.rand;
            double baseX;
            double baseY;
            double baseZ;
            if (random.nextFloat() < 0.07F) {
                baseX = (double) blockpos.getX() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
                baseY = (double) blockpos.getY() + 0.4D;
                baseZ = (double) blockpos.getZ() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
                // pick your particles, come pick yourself some particles
                world.addParticle(ParticleTypes.CRIT, baseX, baseY, baseZ, 0.0D, 0.0D, 0.0D);
            }

            if (random.nextFloat() < 0.03F) {
                baseX = (double) blockpos.getX() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                baseY = (double) blockpos.getY() + 0.4D;
                baseZ = (double) blockpos.getZ() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                world.addParticle(ParticleTypes.EFFECT, baseX, baseY, baseZ, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    public ItemStack getMeal() {
        return this.itemHandler.getStackInSlot(3);
    }

    public boolean isAboveLitHeatSource() {
        if (this.world == null) {
            return false;
        } else {
            BlockState checkState = this.world.getBlockState(this.pos.down());
            if (ModTags.HEAT_SOURCES.contains(checkState.getBlock())) {
                return checkState.hasProperty(BlockStateProperties.LIT) ? (Boolean) checkState.get(BlockStateProperties.LIT) : true;
            } else {
                return false;
            }
        }
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < 6; ++i) {
            drops.add(i == 3 ? ItemStack.EMPTY : this.itemHandler.getStackInSlot(i));
        }
        return drops;
    }

    private void moveMealToOutput() {
        ItemStack mealDisplay = this.itemHandler.getStackInSlot(3);
        ItemStack finalOutput = this.itemHandler.getStackInSlot(5);
        int mealCount = Math.min(mealDisplay.getCount(), mealDisplay.getMaxStackSize() - finalOutput.getCount());
        if (finalOutput.isEmpty()) {
            this.itemHandler.setStackInSlot(5, mealDisplay.split(mealCount));
        } else if (finalOutput.getItem() == mealDisplay.getItem()) {
            mealDisplay.shrink(mealCount);
            finalOutput.grow(mealCount);
        }

    }

    private void useStoredContainersOnMeal() {
        ItemStack mealDisplay = this.itemHandler.getStackInSlot(3);
        ItemStack containerInput = this.itemHandler.getStackInSlot(4);
        ItemStack finalOutput = this.itemHandler.getStackInSlot(5);
        if (this.isContainerValid(containerInput) && finalOutput.getCount() < finalOutput.getMaxStackSize()) {
            int smallerStack = Math.min(mealDisplay.getCount(), containerInput.getCount());
            int mealCount = Math.min(smallerStack, mealDisplay.getMaxStackSize() - finalOutput.getCount());
            if (finalOutput.isEmpty()) {
                containerInput.shrink(mealCount);
                this.itemHandler.setStackInSlot(5, mealDisplay.split(mealCount));
            } else if (finalOutput.getItem() == mealDisplay.getItem()) {
                mealDisplay.shrink(mealCount);
                containerInput.shrink(mealCount);
                finalOutput.grow(mealCount);
            }
        }

    }

    public ItemStack useHeldItemOnMeal(ItemStack container) {
        if (this.isContainerValid(container) && !this.getMeal().isEmpty()) {
            container.shrink(1);
            return this.getMeal().split(1);
        } else {
            return ItemStack.EMPTY;
        }
    }

    private boolean doesMealHaveContainer(ItemStack meal) {
        return !this.container.isEmpty() || meal.hasContainerItem();
    }

    public boolean isContainerValid(ItemStack containerItem) {
        if (containerItem.isEmpty()) {
            return false;
        } else {
            return !this.container.isEmpty() ? this.container.isItemEqual(containerItem) : this.getMeal().getContainerItem().isItemEqual(containerItem);
        }
    }

    public ItemStackHandler getInventory() {
        return this.itemHandler;
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }

    public ITextComponent getName() {
        return (ITextComponent) (this.customName != null ? this.customName : TextUtils.getTranslation("container.copper_pot", new Object[0]));
    }

    public ITextComponent getDisplayName() {
        return this.getName();
    }

    @Nullable
    public ITextComponent getCustomName() {
        return this.customName;
    }

    public Container createMenu(int id, PlayerInventory player, PlayerEntity entity) {
        return new CopperPotContainer(id, player, this, this.copperPotData);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            protected void onContentsChanged(int slot) {
                if (slot >= 0 && slot < 3) {
                    CopperPotTileEntity.this.cookTimeTotal = CopperPotTileEntity.this.getCookTime();
                    CopperPotTileEntity.this.inventoryChanged();
                }
            }
        };
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return side != null && !side.equals(Direction.UP) ? this.handlerOutput.cast() : this.handlerInput.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    public void remove() {
        super.remove();
        this.handlerInput.invalidate();
        this.handlerOutput.invalidate();
    }
}
