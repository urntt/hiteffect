package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.urntt.hiteffect.effect.HurtFlash;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.client.renderer.entity.state.DolphinRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets the item a dolphin carries flash with the hurt dolphin. Only the overlay changes; the item's render type
 * stays vanilla.
 */
@Mixin(DolphinCarryingItemLayer.class)
public abstract class DolphinCarryingItemLayerMixin {
	@ModifyExpressionValue(
			method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/DolphinRenderState;FF)V",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
	private int hiteffect$flashWithEntity(final int overlayCoords, @Local(argsOnly = true) final DolphinRenderState state) {
		return HurtFlash.itemOverlay(overlayCoords, state);
	}
}
