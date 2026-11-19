package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.urntt.hiteffect.config.FlashPart;
import com.urntt.hiteffect.effect.HurtFlash;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets the item a fox holds in its mouth flash with the hurt fox. Only the overlay changes; the item's render type
 * stays vanilla.
 */
@Mixin(FoxHeldItemLayer.class)
public abstract class FoxHeldItemLayerMixin {
	@ModifyExpressionValue(
			method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/FoxRenderState;FF)V",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
	private int hiteffect$flashWithEntity(final int overlayCoords, @Local(argsOnly = true) final FoxRenderState state) {
		return HurtFlash.itemOverlay(overlayCoords, state, FlashPart.FOX_ITEMS);
	}
}
