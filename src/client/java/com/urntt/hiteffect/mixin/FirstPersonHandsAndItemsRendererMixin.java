package com.urntt.hiteffect.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.urntt.hiteffect.effect.HurtFlash;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Tells {@link AvatarRendererMixin} whether the local player is hurt while the first-person hands are drawn. The
 * hands are drawn from the player's extracted render state, whose {@code hasRedOverlay} vanilla sets as for any
 * other living entity.
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonHandsAndItemsRendererMixin {
	@WrapMethod(method = "submitHandsWithItems")
	private void hiteffect$markHurt(
			final float partialTicks,
			final PoseStack poseStack,
			final SubmitNodeCollector submitNodeCollector,
			final PlayerRenderState playerState,
			final FirstPersonHandsAndItemsRenderState state,
			final Operation<Void> original) {
		HurtFlash.beginFirstPerson(playerState.avatarRenderState);
		try {
			original.call(partialTicks, poseStack, submitNodeCollector, playerState, state);
		} finally {
			HurtFlash.endFirstPerson();
		}
	}
}
