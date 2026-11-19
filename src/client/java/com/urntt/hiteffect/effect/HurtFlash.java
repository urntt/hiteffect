package com.urntt.hiteffect.effect;

import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.config.FlashPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
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

	/** Whether {@code part} of the entity drawn with {@code state} flashes. */
	public static boolean flashes(final Object state, final FlashPart part) {
		return state instanceof LivingEntityRenderState living && living.hasRedOverlay && HitEffectClient.settings().flashes(part);
	}

	/** The part an equipment layer belongs to. */
	public static FlashPart equipmentPart(final EquipmentClientInfo.LayerType layerType) {
		return switch (layerType) {
			case HUMANOID, HUMANOID_LEGGINGS, HUMANOID_BABY -> FlashPart.ARMOR;
			case WINGS -> FlashPart.ELYTRA;
			case HORSE_BODY -> FlashPart.HORSE_ARMOR;
			case WOLF_BODY -> FlashPart.WOLF_ARMOR;
			case PIG_SADDLE, STRIDER_SADDLE, CAMEL_SADDLE, CAMEL_HUSK_SADDLE, HORSE_SADDLE, DONKEY_SADDLE, MULE_SADDLE,
					ZOMBIE_HORSE_SADDLE, SKELETON_HORSE_SADDLE, NAUTILUS_SADDLE -> FlashPart.SADDLES;
			case LLAMA_BODY, HAPPY_GHAST_BODY, NAUTILUS_BODY -> FlashPart.OTHER_EQUIPMENT;
		};
	}

	/** The overlay for {@code part}, an item held or worn by the entity drawn with {@code state}. */
	public static int itemOverlay(final int vanillaOverlay, final LivingEntityRenderState state, final FlashPart part) {
		return flashes(state, part) ? HURT_OVERLAY : vanillaOverlay;
	}

	/**
	 * Draws a skull worn on the head of the entity drawn with {@code state}. Vanilla passes no overlay to the skull
	 * renderer, so {@link #wornSkullOverlay} tells it whether the wearer is hurt.
	 */
	public static void submitWornSkull(final LivingEntityRenderState state, final Runnable submit) {
		wornSkullHurt = flashes(state, FlashPart.HEAD_ITEMS);
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
		firstPersonHurt = state != null && flashes(state, FlashPart.FIRST_PERSON_ARM);
	}

	/** Called after the first-person hands are drawn. */
	public static void endFirstPerson() {
		firstPersonHurt = false;
	}

	/** The overlay for the first-person arm. */
	public static int firstPersonArmOverlay(final int vanillaOverlay) {
		return firstPersonHurt ? HURT_OVERLAY : vanillaOverlay;
	}
}
