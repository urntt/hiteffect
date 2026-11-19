package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.urntt.hiteffect.effect.HurtFlash;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets a skull worn by a hurt entity flash with it. The skull renderer has no overlay parameter, so
 * {@link CustomHeadLayerMixin} marks worn skulls of hurt entities; skull blocks and items stay vanilla.
 */
@Mixin(SkullBlockRenderer.class)
public abstract class SkullBlockRendererMixin {
	@ModifyExpressionValue(
			method = "submitSkull",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
	private static int hiteffect$flashWornSkull(final int overlayCoords) {
		return HurtFlash.wornSkullOverlay(overlayCoords);
	}
}
