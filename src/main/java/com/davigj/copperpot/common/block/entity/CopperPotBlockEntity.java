package com.davigj.copperpot.common.block.entity;

import com.davigj.copperpot.CopperPotConfig;
import com.davigj.copperpot.common.block.CopperPotBlock;
import com.davigj.copperpot.common.block.entity.container.CopperPotMenu;
import com.davigj.copperpot.common.block.entity.inventory.CopperPotItemHandler;
import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import com.davigj.copperpot.core.registry.CPBlockEntityTypes;
import com.davigj.copperpot.core.registry.CPItems;
import com.davigj.copperpot.core.registry.CPRecipeTypes;
import com.davigj.copperpot.core.tags.CPBlockTags;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class CopperPotBlockEntity extends SyncedBlockEntity implements MenuProvider, HeatableBlockEntity, Nameable, RecipeHolder {
    public static final int MEAL_DISPLAY_SLOT = 3;
    public static final int CONTAINER_SLOT = 4;
    public static final int OUTPUT_SLOT = 5;
    public static final int INVENTORY_SIZE = 6;
    public static final Map<Item, Item> INGREDIENT_REMAINDER_OVERRIDES;
    private final ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> inputHandler;
    private final LazyOptional<IItemHandler> outputHandler;
    private int cookTime;
    private int cookTimeTotal;
    private ItemStack mealContainerStack;
    private Component customName;
    protected final ContainerData copperPotMenu;
    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;
    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;
    private boolean hasEffect;

    protected final RecipeType<? extends CopperPotRecipe> recipeType;

    public CopperPotBlockEntity(BlockPos pos, BlockState state) {
        super(CPBlockEntityTypes.COPPER_POT.get(), pos, state);
        this.mealContainerStack = ItemStack.EMPTY;
        this.copperPotMenu = this.createIntArray();
        this.usedRecipeTracker = new Object2IntOpenHashMap();
        this.checkNewRecipe = true;
        this.recipeType = CPRecipeTypes.COPPER_POT.get();
        this.inventory = createHandler();

        this.inputHandler = LazyOptional.of(() -> new CopperPotItemHandler(this.inventory, Direction.UP));
        this.outputHandler = LazyOptional.of(() -> new CopperPotItemHandler(this.inventory, Direction.DOWN));
    }

    public static ItemStack getMealFromItem(ItemStack copperPotStack) {
        if (!copperPotStack.is(CPItems.COPPER_POT.get())) {
            return ItemStack.EMPTY;
        } else {
            CompoundTag compound = copperPotStack.getTagElement("BlockEntityTag");
            if (compound != null) {
                CompoundTag inventoryTag = compound.getCompound("Inventory");
                if (inventoryTag.contains("Items", 9)) {
                    ItemStackHandler handler = new ItemStackHandler();
                    handler.deserializeNBT(inventoryTag);
                    return handler.getStackInSlot(3);
                }
            }

            return ItemStack.EMPTY;
        }
    }

    public static void takeServingFromItem(ItemStack copperPotStack) {
        if (copperPotStack.is(CPItems.COPPER_POT.get())) {
            CompoundTag compound = copperPotStack.getTagElement("BlockEntityTag");
            if (compound != null) {
                CompoundTag inventoryTag = compound.getCompound("Inventory");
                if (inventoryTag.contains("Items", 6)) {
                    ItemStackHandler handler = new ItemStackHandler();
                    handler.deserializeNBT(inventoryTag);
                    ItemStack newMealStack = handler.getStackInSlot(3);
                    newMealStack.shrink(1);
                    compound.remove("Inventory");
                    compound.put("Inventory", handler.serializeNBT());
                }
            }

        }
    }

    public static ItemStack getContainerFromItem(ItemStack copperPotStack) {
        if (!copperPotStack.is(CPItems.COPPER_POT.get())) {
            return ItemStack.EMPTY;
        } else {
            CompoundTag compound = copperPotStack.getTagElement("BlockEntityTag");
            return compound != null ? ItemStack.of(compound.getCompound("Container")) : ItemStack.EMPTY;
        }
    }

    public void load(CompoundTag compound) {
        super.load(compound);
        this.inventory.deserializeNBT(compound.getCompound("Inventory"));
        this.cookTime = compound.getInt("CookTime");
        this.cookTimeTotal = compound.getInt("CookTimeTotal");
        this.mealContainerStack = ItemStack.of(compound.getCompound("Container"));
        if (compound.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
        this.hasEffect = compound.getBoolean("HasEffect");

        CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
        Iterator var3 = compoundRecipes.getAllKeys().iterator();

        while (var3.hasNext()) {
            String key = (String) var3.next();
            this.usedRecipeTracker.put(new ResourceLocation(key), compoundRecipes.getInt(key));
        }

    }

    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("CookTime", this.cookTime);
        compound.putInt("CookTimeTotal", this.cookTimeTotal);
        compound.put("Container", this.mealContainerStack.serializeNBT());
        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
        compound.putBoolean("HasEffect", this.hasEffect);

        compound.put("Inventory", this.inventory.serializeNBT());
        CompoundTag compoundRecipes = new CompoundTag();
        this.usedRecipeTracker.forEach((recipeId, craftedAmount) -> {
            compoundRecipes.putInt(recipeId.toString(), craftedAmount);
        });
        compound.put("RecipesUsed", compoundRecipes);
    }

    private CompoundTag writeItems(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Container", this.mealContainerStack.serializeNBT());
        compound.put("Inventory", this.inventory.serializeNBT());
        return compound;
    }

    public CompoundTag writeMeal(CompoundTag compound) {
        if (this.getMeal().isEmpty()) {
            return compound;
        } else {
            ItemStackHandler drops = new ItemStackHandler(6);

            for (int i = 0; i < 6; ++i) {
                drops.setStackInSlot(i, i == 3 ? this.inventory.getStackInSlot(i) : ItemStack.EMPTY);
            }

            if (this.customName != null) {
                compound.putString("CustomName", Component.Serializer.toJson(this.customName));
            }

            compound.put("Container", this.mealContainerStack.serializeNBT());
            compound.put("Inventory", drops.serializeNBT());
            return compound;
        }
    }

    private void effectCloud(Level level, BlockPos pos) {
        String effect = this.getEffect();
        if (effect != null) {
            MobEffectInstance effectInstance = createEffectInstance(effect);
            double radius = fumesRadius(level, pos);
            for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(radius, 2.0D, radius))) {
                living.addEffect(effectInstance);
            }
        }
    }

    private MobEffectInstance createEffectInstance(String effect) {
        String[] effectName = effect.split(":", 2);
        int effectDuration = this.getEffectDuration();
        int effectAmplifier = this.getEffectAmplifier();
        return new MobEffectInstance(getCookEffect(effectName[0], new ResourceLocation(effectName[0], effectName[1])).get(), effectDuration, effectAmplifier, false, true);
    }

    private double fumesRadius(Level worldIn, BlockPos pos) {
        Iterator<BlockPos> var8 = BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 3, 1)).iterator();
        double inhibited = CopperPotConfig.COMMON.copperFumeRadius.get();
        while (var8.hasNext()) {
            BlockPos neighborPos = var8.next();
            BlockState neighborState = worldIn.getBlockState(neighborPos);
            if (neighborState.is(CPBlockTags.PARTIAL_FUME_INHIBITORS)) {
                inhibited = 1.0D;
            }
            if (neighborState.is(CPBlockTags.FUME_INHIBITORS)) {
                return 0.0D;
            }
        }
        return inhibited;
    }

    public static void cookingTick(Level level, BlockPos pos, BlockState state, CopperPotBlockEntity copperPot) {
        boolean isHeated = copperPot.isHeated(level, pos);
        boolean didInventoryChange = false;
        if (isHeated && copperPot.hasInput() && state.getValue(CopperPotBlock.ENABLED)) {
            Optional<? extends CopperPotRecipe> recipe = copperPot.getMatchingRecipe(new RecipeWrapper(copperPot.inventory));
            if (recipe.isPresent() && copperPot.canCook(recipe.get())) {
                didInventoryChange = copperPot.processCooking(recipe.get(), copperPot);
            } else {
                copperPot.cookTime = 0;
            }
        } else if (copperPot.cookTime > 0) {
            copperPot.cookTime = Mth.clamp(copperPot.cookTime - 2, 0, copperPot.cookTimeTotal);
        }

        ItemStack mealStack = copperPot.getMeal();
        if (!mealStack.isEmpty()) {
            if (!copperPot.doesMealHaveContainer(mealStack)) {
                copperPot.moveMealToOutput();
                didInventoryChange = true;
            } else if (!copperPot.inventory.getStackInSlot(3).isEmpty()) {
                copperPot.useStoredContainersOnMeal();
                didInventoryChange = true;
            }
        }

        if (didInventoryChange) {
            copperPot.inventoryChanged();
        }

    }


    protected List<CopperPotRecipe> getAllRecipes() {
        List<CopperPotRecipe> copperPot = this.level.getRecipeManager().getAllRecipesFor(CPRecipeTypes.COPPER_POT.get()).stream().toList();
        List<ItemStack> results = copperPot.stream().map((e) -> e.getResultItem(null)).toList();

        if (CopperPotConfig.COMMON.recipeReg.get()) {
            List<CopperPotRecipe> cookingPot = this.level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COOKING.get()).stream()
                    .filter(recipe -> recipe.getIngredients().size() < 4)
                    .filter(recipe -> results.stream().noneMatch((e) -> e.is(recipe.getResultItem(null).getItem())))
                    .map(CopperPotRecipe::fromRecipe)
                    .toList();

            return List.of(copperPot, cookingPot).stream().flatMap(List::stream).toList();
        }
        return Stream.of(copperPot).flatMap(List::stream).toList();
    }

    private static Supplier<MobEffect> getCookEffect(String modid, ResourceLocation effect) {
        return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
    }

    private Optional<CopperPotRecipe> getAllRecipesFor(RecipeWrapper pInventory, Level pLevel) {
        return getAllRecipes().stream().filter((p_220266_) -> {
            return p_220266_.matches(pInventory, pLevel);
        }).findFirst();
    }

    protected ItemStack getRecipeContainer() {
        return getAllRecipesFor(new RecipeWrapper(this.inventory), this.level).map(CopperPotRecipe::getOutputContainer).orElse(ItemStack.EMPTY);
    }

    public boolean hasEffect() {
        return getAllRecipesFor(new RecipeWrapper(this.inventory), this.level).map(CopperPotRecipe::hasEffect).orElse(false);
    }

    public String getEffect() {
        return getAllRecipesFor(new RecipeWrapper(this.inventory), this.level).map(CopperPotRecipe::getEffect).orElse("");
    }

    public int getEffectDuration() {
        return getAllRecipesFor(new RecipeWrapper(this.inventory), this.level).map(CopperPotRecipe::getEffectDuration).orElse(100);
    }

    public int getEffectAmplifier() {
        return getAllRecipesFor(new RecipeWrapper(this.inventory), this.level).map(CopperPotRecipe::getEffectAmplifier).orElse(0);
    }

    public static void animationTick(Level level, BlockPos pos, BlockState state, CopperPotBlockEntity copperPot) {
        if (copperPot.isHeated(level, pos)) {
            RandomSource random = level.getRandom();
            double x;
            double y;
            double z;
            if (random.nextFloat() < 0.08F) {
                x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                y = (double) pos.getY() + 0.4D;
                z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
                Optional<? extends CopperPotRecipe> recipe = copperPot.getMatchingRecipe(new RecipeWrapper(copperPot.inventory));
                if (!copperPot.hasEffect() || !(recipe.isPresent() && copperPot.canCook(recipe.get()))) {
                    if (random.nextBoolean()) {
                        level.addParticle(ParticleTypes.EFFECT, x, y, z, 0.0D, 0.0D, 0.0D);
                    }
                } else {
                    String effect = copperPot.getEffect();
                    MobEffectInstance instance = copperPot.createEffectInstance(effect);
                    int i = instance.getEffect().getColor();
                    double d0 = (double)(i >> 16 & 255) / 255.0D;
                    double d1 = (double)(i >> 8 & 255) / 255.0D;
                    double d2 = (double)(i & 255) / 255.0D;
                    level.addParticle(ParticleTypes.ENTITY_EFFECT, x, y, z, d0, d1, d2);
                }
            }

        }

    }

    private Optional<? extends CopperPotRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (this.level == null) {
            return Optional.empty();
        } else {
            if (lastRecipeID != null) {
                // get recipe by id
                Optional<? extends Recipe<?>> recipe = getAllRecipes().stream().filter(aef -> aef.getId() == lastRecipeID).findFirst();
                if (recipe.isPresent())
                    if (recipe.get() instanceof CopperPotRecipe copPot) {
                        if (copPot.matches(inventoryWrapper, level)) {
                            return Optional.of(copPot);
                        }
                        if (ItemStack.isSameItem(copPot.getResultItem(this.level.registryAccess()), getMeal())) {
                            return Optional.empty();
                        }
                    }
            }

            if (this.checkNewRecipe) {
                Optional<? extends CopperPotRecipe> recipe = getAllRecipesFor(inventoryWrapper, this.level);
                if (recipe.isPresent()) {
                    ResourceLocation newRecipeID = (recipe.get()).getId();
                    if (this.lastRecipeID != null && !this.lastRecipeID.equals(newRecipeID)) {
                        this.cookTime = 0;
                    }

                    this.lastRecipeID = newRecipeID;
                    return recipe;
                }
            }

            this.checkNewRecipe = false;
            return Optional.empty();
        }
    }

    public ItemStack getContainer() {
        ItemStack mealStack = this.getMeal();
        return !mealStack.isEmpty() && !this.mealContainerStack.isEmpty() ? this.mealContainerStack : mealStack.getCraftingRemainingItem();
    }

    private boolean hasInput() {
        for (int i = 0; i < 2; ++i) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    protected boolean canCook(@Nullable Recipe<RecipeWrapper> recipe) {
        if (this.hasInput() && recipe != null) {
            ItemStack recipeOutput = recipe.getResultItem(this.level.registryAccess());
            if (recipeOutput.isEmpty()) {
                return false;
            } else {
                ItemStack currentOutput = this.inventory.getStackInSlot(3);
                if (currentOutput.isEmpty()) {
                    return true;
                } else if (!currentOutput.is(recipeOutput.getItem())) {
                    return false;
                } else if (currentOutput.getCount() + recipeOutput.getCount() <= Math.min(16, this.inventory.getSlotLimit(3))) {
                    return true;
                } else {
                    return currentOutput.getCount() + recipeOutput.getCount() <= Math.min(16, recipeOutput.getMaxStackSize());
                }
            }
        } else {
            return false;
        }
    }

    private boolean processCooking(CopperPotRecipe recipe, CopperPotBlockEntity cookingPot) {
        if (this.level == null) {
            return false;
        } else {
            ++this.cookTime;
            if ((this.cookTime % 30 == 0) && this.hasEffect()) {
                this.effectCloud(getLevel(), worldPosition);
            }
            this.cookTimeTotal = recipe.getCookTime();
            if (this.cookTime < this.cookTimeTotal) {
                return false;
            } else {
                this.cookTime = 0;
                this.mealContainerStack = recipe.getOutputContainer();
                ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());
                ItemStack storedMealStack = this.inventory.getStackInSlot(3);
                if (storedMealStack.isEmpty()) {
                    this.inventory.setStackInSlot(3, resultStack.copy());
                } else if (ItemStack.isSameItem(storedMealStack, resultStack)) {
                    storedMealStack.grow(resultStack.getCount());
                }

                cookingPot.setRecipeUsed(recipe);

                for (int i = 0; i < 3; ++i) {
                    ItemStack slotStack = this.inventory.getStackInSlot(i);
                    if (slotStack.hasCraftingRemainingItem()) {
                        this.ejectIngredientRemainder(slotStack.getCraftingRemainingItem());
                    } else if (INGREDIENT_REMAINDER_OVERRIDES.containsKey(slotStack.getItem())) {
                        this.ejectIngredientRemainder(((Item) INGREDIENT_REMAINDER_OVERRIDES.get(slotStack.getItem())).getDefaultInstance());
                    }

                    if (!slotStack.isEmpty()) {
                        slotStack.shrink(1);
                    }
                }

                return true;
            }
        }
    }

    protected void ejectIngredientRemainder(ItemStack remainderStack) {
        Direction direction = ((Direction) this.getBlockState().getValue(CopperPotBlock.FACING)).getCounterClockWise();
        double x = (double) this.worldPosition.getX() + 0.5 + (double) direction.getStepX() * 0.25;
        double y = (double) this.worldPosition.getY() + 0.7;
        double z = (double) this.worldPosition.getZ() + 0.5 + (double) direction.getStepZ() * 0.25;
        ItemUtils.spawnItemEntity(this.level, remainderStack, x, y, z, (double) ((float) direction.getStepX() * 0.08F), 0.25, (double) ((float) direction.getStepZ() * 0.08F));
    }

    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.getId();
            this.usedRecipeTracker.addTo(recipeID, 1);
        }

    }

    @Nullable
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    public void awardUsedRecipes(Player player, List<ItemStack> items) {
        List<Recipe<?>> usedRecipes = this.getUsedRecipesAndPopExperience(player.level(), player.position());
        player.awardRecipes(usedRecipes);
        this.usedRecipeTracker.clear();
    }

    public List<Recipe<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
        List<Recipe<?>> list = Lists.newArrayList();
        ObjectIterator var4 = this.usedRecipeTracker.object2IntEntrySet().iterator();

        while (var4.hasNext()) {
            Object2IntMap.Entry<ResourceLocation> entry = (Object2IntMap.Entry) var4.next();
            getAllRecipes().stream().filter(aef -> aef.getId() == entry.getKey()).findFirst().ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerLevel) level, pos, entry.getIntValue(), ((CopperPotRecipe) recipe).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        ExperienceOrb.award(level, pos, expTotal);
    }

    public boolean isHeated() {
        return this.level == null ? false : this.isHeated(this.level, this.worldPosition);
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ItemStack getMeal() {
        return this.inventory.getStackInSlot(3);
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();

        for (int i = 0; i < 6; ++i) {
            if (i != 3) {
                drops.add(this.inventory.getStackInSlot(i));
            }
        }

        return drops;
    }

    private void moveMealToOutput() {
        ItemStack mealStack = this.inventory.getStackInSlot(3);
        ItemStack outputStack = this.inventory.getStackInSlot(5);
        int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxStackSize() - outputStack.getCount());
        if (outputStack.isEmpty()) {
            this.inventory.setStackInSlot(5, mealStack.split(mealCount));
        } else if (outputStack.getItem() == mealStack.getItem()) {
            mealStack.shrink(mealCount);
            outputStack.grow(mealCount);
        }

    }

    private void useStoredContainersOnMeal() {
        ItemStack mealStack = this.inventory.getStackInSlot(3);
        ItemStack containerInputStack = this.inventory.getStackInSlot(4);
        ItemStack outputStack = this.inventory.getStackInSlot(5);
        if (this.isContainerValid(containerInputStack) && outputStack.getCount() < outputStack.getMaxStackSize()) {
            int smallerStackCount = Math.min(mealStack.getCount(), containerInputStack.getCount());
            int mealCount = Math.min(smallerStackCount, mealStack.getMaxStackSize() - outputStack.getCount());
            if (outputStack.isEmpty()) {
                containerInputStack.shrink(mealCount);
                this.inventory.setStackInSlot(5, mealStack.split(mealCount));
            } else if (outputStack.getItem() == mealStack.getItem()) {
                mealStack.shrink(mealCount);
                containerInputStack.shrink(mealCount);
                outputStack.grow(mealCount);
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
        return !this.mealContainerStack.isEmpty() || meal.hasCraftingRemainingItem();
    }

    public boolean isContainerValid(ItemStack containerItem) {
        if (containerItem.isEmpty()) {
            return false;
        } else {
            return !this.mealContainerStack.isEmpty() ? ItemStack.isSameItem(this.mealContainerStack, containerItem) : ItemStack.isSameItem(this.getMeal(), containerItem);
        }
    }

    public Component getName() {
        return (Component) (this.customName != null ? this.customName : Component.translatable("copperpot.container.copper_pot"));
    }

    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
        return new CopperPotMenu(id, player, this, this.copperPotMenu);
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            return side != null && !side.equals(Direction.UP) ? this.outputHandler.cast() : this.inputHandler.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    public void setRemoved() {
        super.setRemoved();
        this.inputHandler.invalidate();
        this.outputHandler.invalidate();
    }

    public CompoundTag getUpdateTag() {
        return this.writeItems(new CompoundTag());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(6) {
            protected void onContentsChanged(int slot) {
                if (slot >= 0 && slot < 3) {
                    checkNewRecipe = true;
                }

                inventoryChanged();
            }
        };
    }

    private ContainerData createIntArray() {
        return new ContainerData() {
            public int get(int index) {
                int var10000;
                switch (index) {
                    case 0:
                        var10000 = cookTime;
                        break;
                    case 1:
                        var10000 = cookTimeTotal;
                        break;
                    default:
                        var10000 = 0;
                }

                return var10000;
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        cookTime = value;
                        break;
                    case 1:
                        cookTimeTotal = value;
                }

            }

            public int getCount() {
                return 2;
            }
        };
    }

    static {
        INGREDIENT_REMAINDER_OVERRIDES = Map.ofEntries(Map.entry(Items.POWDER_SNOW_BUCKET, Items.BUCKET), Map.entry(Items.AXOLOTL_BUCKET, Items.BUCKET), Map.entry(Items.COD_BUCKET, Items.BUCKET), Map.entry(Items.PUFFERFISH_BUCKET, Items.BUCKET), Map.entry(Items.SALMON_BUCKET, Items.BUCKET), Map.entry(Items.TROPICAL_FISH_BUCKET, Items.BUCKET), Map.entry(Items.SUSPICIOUS_STEW, Items.BOWL), Map.entry(Items.MUSHROOM_STEW, Items.BOWL), Map.entry(Items.RABBIT_STEW, Items.BOWL), Map.entry(Items.BEETROOT_SOUP, Items.BOWL), Map.entry(Items.POTION, Items.GLASS_BOTTLE), Map.entry(Items.SPLASH_POTION, Items.GLASS_BOTTLE), Map.entry(Items.LINGERING_POTION, Items.GLASS_BOTTLE), Map.entry(Items.EXPERIENCE_BOTTLE, Items.GLASS_BOTTLE));
    }
}
