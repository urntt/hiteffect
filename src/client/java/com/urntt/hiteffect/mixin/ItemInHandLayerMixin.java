package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.urntt.hiteffect.effect.HurtFlash;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets items held in the hands flash with the hurt entity. Only the overlay changes; the item's render type stays
 * vanilla.
 */
@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {
	@ModifyExpressionValue(
			method = "submitArmWithItem",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
	private int hiteffect$flashWithEntity(final int overlayCoords, @Local(argsOnly = true) final ArmedEntityRenderState state) {
		return HurtFlash.itemOverlay(overlayCoords, state);
	}
}
