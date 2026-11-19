package com.urntt.hiteffect.effect;

import com.urntt.hiteffect.HitEffectClient;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.Nullable;

/**
 * Decides which parts of a hurt entity flash with it. Vanilla draws only the entity's own model with the hurt
 * overlay; equipment, items and the first-person arm are drawn with {@link OverlayTexture#NO_OVERLAY}.
 */
public final class HurtFlash {
	/** The overlay coordinates of the hurt flash, without the white flash some entities use. */
	public static final int HURT_OVERLAY = OverlayTexture.pack(0.0F, true);

	private static boolean firstPersonHurt;
	private static boolean wornSkullHurt;

	private HurtFlash() {
	}

	/** Whether the equipment of the entity drawn with {@code state} should flash. */
	public static boolean flashesEquipment(final Object state) {
		return state instanceof LivingEntityRenderState living && living.hasRedOverlay && HitEffectClient.settings().armorFlash();
	}

	/** The overlay for an item held or worn by the entity drawn with {@code state}. */
	public static int itemOverlay(final int vanillaOverlay, final LivingEntityRenderState state) {
		return state.hasRedOverlay && HitEffectClient.settings().itemFlash() ? HURT_OVERLAY : vanillaOverlay;
	}

	/**
	 * Draws a skull worn on the head of the entity drawn with {@code state}. Vanilla passes no overlay to the skull
	 * renderer, so {@link #wornSkullOverlay} tells it whether the wearer is hurt.
	 */
	public static void submitWornSkull(final LivingEntityRenderState state, final Runnable submit) {
		wornSkullHurt = state.hasRedOverlay && HitEffectClient.settings().itemFlash();
		try {
			submit.run();
		} finally {
			wornSkullHurt = false;
		}
	}

	/** The overlay for a skull; only skulls worn by a hurt entity flash. */
	public static int wornSkullOverlay(final int vanillaOverlay) {
		return wornSkullHurt ? HURT_OVERLAY : vanillaOverlay;
	}

	/** Called before the first-person hands are drawn from the local player's render state. */
	public static void beginFirstPerson(final @Nullable AvatarRenderState state) {
		firstPersonHurt = state != null && state.hasRedOverlay;
	}

	/** Called after the first-person hands are drawn. */
	public static void endFirstPerson() {
		firstPersonHurt = false;
	}

	/** The overlay for the first-person arm. */
	public static int firstPersonArmOverlay(final int vanillaOverlay) {
		return firstPersonHurt && HitEffectClient.settings().firstPersonArmFlash() ? HURT_OVERLAY : vanillaOverlay;
	}
}
