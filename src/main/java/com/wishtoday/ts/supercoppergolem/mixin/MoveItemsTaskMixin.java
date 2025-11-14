package com.wishtoday.ts.supercoppergolem.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MoveItemsTask.class)
public class MoveItemsTaskMixin {
    @ModifyExpressionValue(method = "hasExistingStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private static boolean areItemsEqual(boolean original
            , @Local(ordinal = 0) ItemStack item1
            , @Local(ordinal = 1) ItemStack item2) {
        return areItemsEqual(item1, item2);
    }

    @Unique
    private static boolean areItemsEqual(ItemStack left, ItemStack right) {
        if (left.isIn(ItemTags.SHULKER_BOXES) ||  right.isIn(ItemTags.SHULKER_BOXES)) return stackAreEqual(left, right);
        return left.isOf(right.getItem());
    }

    @Unique
    private static boolean stackAreEqual(ItemStack item1, ItemStack item2) {
        Item item = getOnlyItemFromShulker(item1);
        Item item3 = getOnlyItemFromShulker(item2);
        if (item == null || item3 == null) return false;
        return item == item3;
    }

    @Unique
    @Nullable
    private static Item getOnlyItemFromShulker(ItemStack shulker) {
        if (!shulker.isIn(ItemTags.SHULKER_BOXES)) return shulker.getItem();
        ContainerComponent component = shulker.get(DataComponentTypes.CONTAINER);
        if (component == null) return null;
        Item item = null;

        for (ItemStack itemStack : (Iterable<ItemStack>) component.streamNonEmpty()::iterator) {
            if (item == null) item = itemStack.getItem();
            if (item != itemStack.getItem()) return null;
        }
        return item;
    }

    @ModifyArg(method = "extractStack", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), index = 1)
    private static int min(int i, int j) {
        return j * 4;
    }
}
