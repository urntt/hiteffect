package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.urntt.hiteffect.config.FlashPart;
import com.urntt.hiteffect.effect.HurtFlash;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets an item a player holds to the eye (a spyglass) flash with the hurt player. Only the overlay changes; the
 * item's render type stays vanilla.
 */
@Mixin(PlayerItemInHandLayer.class)
public abstract class PlayerItemInHandLayerMixin {
	@ModifyExpressionValue(
			method = "renderItemHeldToEye",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
	private int hiteffect$flashWithEntity(final int overlayCoords, @Local(argsOnly = true) final AvatarRenderState state) {
		return HurtFlash.itemOverlay(overlayCoords, state, FlashPart.HELD_ITEMS);
	}
}
