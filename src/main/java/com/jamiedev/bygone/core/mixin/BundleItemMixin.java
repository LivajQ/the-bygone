package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.common.item.CustomizableBundleItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin {
    
    //Reject insertion outright if the item doesn't pass the bundle's filter
    @Inject(
            method = "add(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)I",
            at = @At("HEAD"), cancellable = true
    )
    private static void bygone$rejectDisallowed(ItemStack pBundleStack, ItemStack pInsertedStack, CallbackInfoReturnable<Integer> cir) {
        if (pBundleStack.getItem() instanceof CustomizableBundleItem bundle && !bundle.acceptsStack(pInsertedStack)) {
            cir.setReturnValue(0);
        }
    }
    
    //Swap the hardcoded 64-weight cap inside add(...) for the bundle's custom max weight
    @ModifyConstant(
            method = "add(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)I",
            constant = @Constant(intValue = 64)
    )
    private static int bygone$modifyAddCapacity(int original, ItemStack pBundleStack, ItemStack pInsertedStack) {
        if (pBundleStack.getItem() instanceof CustomizableBundleItem bundle) {
            return bundle.getMaxWeight();
        }
        return original;
    }
    
    //Same swap for the hover text capacity display
    @ModifyConstant(method = "appendHoverText", constant = @Constant(intValue = 64))
    private int bygone$modifyHoverCapacity(int original) {
        if ((Object) this instanceof CustomizableBundleItem bundle) {
            return bundle.getMaxWeight();
        }
        return original;
    }
    
    @WrapOperation(
            method = "add(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BundleItem;getWeight(Lnet/minecraft/world/item/ItemStack;)I")
    )
    private static int bygone$wrapGetWeightInAdd(ItemStack stack, Operation<Integer> original, ItemStack pBundleStack, ItemStack pInsertedStack) {
        if (pBundleStack.getItem() instanceof CustomizableBundleItem bundle) {
            return bundle.getStackWeight(stack);
        }
        return original.call(stack);
    }
}