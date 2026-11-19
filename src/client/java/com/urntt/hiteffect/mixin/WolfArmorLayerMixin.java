package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.urntt.hiteffect.config.FlashPart;
import com.urntt.hiteffect.effect.HurtFlash;
import com.urntt.hiteffect.render.HitEffectRenderTypes;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets the cracks of damaged wolf armor flash with the armor. Vanilla draws them separately from the armor itself
 * (see {@link EquipmentLayerRendererMixin}), with {@code NO_OVERLAY} and a pipeline that ignores the overlay.
 */
@Mixin(WolfArmorLayer.class)
public abstract class WolfArmorLayerMixin {
	@WrapOperation(
			method = "maybeRenderCracks",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;III)V"))
	private void hiteffect$flashWithWolf(
			final OrderedSubmitNodeCollector collector,
			final Model<?> model,
			final Object state,
			final PoseStack poseStack,
			final RenderType renderType,
			final int lightCoords,
			final int overlayCoords,
			final int outlineColor,
			final Operation<Void> original) {
		if (HurtFlash.flashes(state, FlashPart.WOLF_ARMOR)) {
			original.call(collector, model, state, poseStack, HitEffectRenderTypes.withOverlay(renderType), lightCoords, HurtFlash.HURT_OVERLAY, outlineColor);
		} else {
			original.call(collector, model, state, poseStack, renderType, lightCoords, overlayCoords, outlineColor);
		}
	}
}
