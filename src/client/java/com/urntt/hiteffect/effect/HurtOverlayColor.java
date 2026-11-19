package com.urntt.hiteffect.effect;

import com.mojang.blaze3d.platform.NativeImage;
import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.config.EffectSettings;
import com.urntt.hiteffect.config.HurtColors;
import com.urntt.hiteffect.mixin.OverlayTextureAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Util;

/**
 * Applies the hurt flash color by rewriting the hurt rows of the game's {@link OverlayTexture}. Every entity shader
 * samples those rows while an entity shows its hurt overlay, so the color applies to all entities at once.
 */
public final class HurtOverlayColor {
	/** The overlay texture is 16 pixels wide. */
	private static final int TEXTURE_SIZE = 16;

	/** Rows {@code 0..7} hold the hurt color; {@link OverlayTexture#RED_OVERLAY_V} points into them. */
	private static final int HURT_ROWS = 8;

	private static int appliedArgb;
	private static boolean applied;

	private HurtOverlayColor() {
	}

	/** The ARGB value the hurt rows should hold for {@code settings} at the current time. */
	public static int targetArgb(final EffectSettings settings) {
		int rgb = settings.rainbow() ? HurtColors.rainbow(Util.getMillis()) : settings.hurtColor();
		return HurtColors.overlayArgb(rgb, settings.hurtStrength());
	}

	/** Rewrites the hurt rows if the settings, or the rainbow, changed since the last update. */
	public static void update(final Minecraft minecraft) {
		int argb = targetArgb(HitEffectClient.settings());
		if (applied && argb == appliedArgb) {
			return;
		}

		DynamicTexture texture = ((OverlayTextureAccessor) minecraft.gameRenderer.overlayTexture()).hiteffect$getTexture();
		NativeImage pixels = texture.getPixels();
		for (int y = 0; y < HURT_ROWS; y++) {
			for (int x = 0; x < TEXTURE_SIZE; x++) {
				pixels.setPixel(x, y, argb);
			}
		}
		texture.upload();
		appliedArgb = argb;
		applied = true;
	}

	/** The ARGB value the hurt rows currently hold, read back from the texture. */
	public static int currentArgb(final Minecraft minecraft) {
		DynamicTexture texture = ((OverlayTextureAccessor) minecraft.gameRenderer.overlayTexture()).hiteffect$getTexture();
		return texture.getPixels().getPixel(0, 0);
	}
}
