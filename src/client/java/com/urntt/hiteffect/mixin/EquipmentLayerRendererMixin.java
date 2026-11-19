package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.urntt.hiteffect.effect.HurtFlash;
import com.urntt.hiteffect.render.HitEffectRenderTypes;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets armor and every other equipment layer (elytra, horse and wolf armor, saddles, harnesses) flash with the hurt
 * entity, each as its {@link com.urntt.hiteffect.config.FlashPart} is set. Vanilla submits equipment with
 * {@code NO_OVERLAY} and pipelines that ignore the overlay. While the entity shows its hurt overlay, this submits
 * overlay-capable copies of those render types with the hurt overlay instead. Equipment of entities that are not hurt,
 * parts that are switched off, and glint-only layers stay vanilla.
 */
@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {
	@WrapOperation(
			method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/UvMapping;I)V"))
	private void hiteffect$flashWithEntity(
			final OrderedSubmitNodeCollector collector,
			final Model<?> model,
			final Object state,
			final PoseStack poseStack,
			final RenderType renderType,
			final int lightCoords,
			final int overlayCoords,
			final int color,
			final @Nullable UvMapping uvMapping,
			final int outlineColor,
			final Operation<Void> original,
			@Local(argsOnly = true) final EquipmentClientInfo.LayerType layerType) {
		boolean flashes = HurtFlash.flashes(state, HurtFlash.equipmentPart(layerType));
		RenderType flashing = flashes ? HitEffectRenderTypes.withOverlay(renderType) : renderType;
		if (flashing == renderType) {
			original.call(collector, model, state, poseStack, renderType, lightCoords, overlayCoords, color, uvMapping, outlineColor);
		} else {
			original.call(collector, model, state, poseStack, flashing, lightCoords, HurtFlash.HURT_OVERLAY, color, uvMapping, outlineColor);
		}
	}
}
