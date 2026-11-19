package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.urntt.hiteffect.effect.HurtFlash;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets the first-person arm, including its sleeve, flash while the local player is hurt. Vanilla draws it with
 * {@code NO_OVERLAY}; its render type already samples the overlay, so only the overlay changes. Items held in first
 * person stay vanilla.
 */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {
	@ModifyExpressionValue(
			method = "renderHand",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
	private int hiteffect$flashArm(final int overlayCoords) {
		return HurtFlash.firstPersonArmOverlay(overlayCoords);
	}
}
