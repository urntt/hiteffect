package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.urntt.hiteffect.effect.HurtFlash;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets items worn on the head (carved pumpkins, blocks, skulls) flash with the hurt entity. Only the overlay changes;
 * the render types stay vanilla. Skulls are drawn by {@code SkullBlockRenderer}, see {@link SkullBlockRendererMixin}.
 */
@Mixin(CustomHeadLayer.class)
public abstract class CustomHeadLayerMixin {
	private static final String SUBMIT = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V";

	@ModifyExpressionValue(method = SUBMIT, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
	private int hiteffect$flashWithEntity(final int overlayCoords, @Local(argsOnly = true) final LivingEntityRenderState state) {
		return HurtFlash.itemOverlay(overlayCoords, state);
	}

	@WrapOperation(
			method = SUBMIT,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;submitSkull(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/model/object/skull/SkullModelBase;Lnet/minecraft/client/renderer/rendertype/RenderType;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
	private void hiteffect$flashSkullWithEntity(
			final float animationValue,
			final PoseStack poseStack,
			final SubmitNodeCollector submitNodeCollector,
			final int lightCoords,
			final SkullModelBase model,
			final RenderType renderType,
			final int outlineColor,
			final ModelFeatureRenderer.CrumblingOverlay breakProgress,
			final Operation<Void> original,
			@Local(argsOnly = true) final LivingEntityRenderState state) {
		HurtFlash.submitWornSkull(state, () -> original.call(
				animationValue, poseStack, submitNodeCollector, lightCoords, model, renderType, outlineColor, breakProgress));
	}
}
