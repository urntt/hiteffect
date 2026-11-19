package com.urntt.hiteffect.config;

import java.util.Locale;
import java.util.Optional;
import net.minecraft.util.Mth;

/**
 * Hurt flash color math, shared by the overlay texture, the configuration screen and the OneConfig integration.
 */
public final class HurtColors {
	/** Vanilla's hurt flash color, {@code 0xRRGGBB}. */
	public static final int VANILLA_COLOR = 0xFF0000;

	/** Vanilla's hurt flash strength in percent: its overlay alpha {@code 0xB2} leaves 70 % of the original color. */
	public static final int VANILLA_STRENGTH = 30;

	public static final int MIN_STRENGTH = 0;
	public static final int MAX_STRENGTH = 100;

	/** How long the rainbow takes to cycle through all hues. */
	public static final long RAINBOW_PERIOD_MILLIS = 2000L;

	/** Colors offered as presets on the configuration screen, {@code 0xRRGGBB}, starting with vanilla red. */
	public static final int[] PRESETS = {
			VANILLA_COLOR, 0xFF8000, 0xFFFF00, 0x00FF00, 0x00FFFF, 0x0000FF, 0x8000FF, 0xFFFFFF,
	};

	private static final int RGB_MASK = 0xFFFFFF;
	private static final int HEX_DIGITS = 6;

	private HurtColors() {
	}

	/**
	 * Returns the ARGB value of the overlay texture's hurt rows. The entity shaders compute
	 * {@code mix(overlay.rgb, color.rgb, overlay.a)}, so the alpha is the share of the original color that remains.
	 */
	public static int overlayArgb(final int rgb, final int strength) {
		int clamped = Mth.clamp(strength, MIN_STRENGTH, MAX_STRENGTH);
		int alpha = 255 - Math.round(clamped * 255 / (float) MAX_STRENGTH);
		return alpha << 24 | rgb & RGB_MASK;
	}

	/** Returns the rainbow color for the given time, {@code 0xRRGGBB}. */
	public static int rainbow(final long millis) {
		float hue = (float) Math.floorMod(millis, RAINBOW_PERIOD_MILLIS) / RAINBOW_PERIOD_MILLIS;
		return Mth.hsvToRgb(hue, 1.0F, 1.0F) & RGB_MASK;
	}

	/** Formats a color as {@code #RRGGBB}. */
	public static String toHex(final int rgb) {
		return String.format(Locale.ROOT, "#%06X", rgb & RGB_MASK);
	}

	/** Parses {@code #RRGGBB} or {@code RRGGBB}. */
	public static Optional<Integer> parseHex(final String text) {
		String digits = text.startsWith("#") ? text.substring(1) : text;
		if (digits.length() != HEX_DIGITS) {
			return Optional.empty();
		}
		try {
			return Optional.of(Integer.parseInt(digits, 16));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	/** A color channel of {@code 0xRRGGBB}, given by its bit offset. */
	public enum Channel {
		RED(16),
		GREEN(8),
		BLUE(0);

		/** The largest value of a channel. */
		public static final int MAX = 0xFF;

		private final int shift;

		Channel(final int shift) {
			this.shift = shift;
		}

		public int get(final int rgb) {
			return rgb >> this.shift & MAX;
		}

		/** Returns {@code rgb} with this channel set to {@code value}. */
		public int with(final int rgb, final int value) {
			return rgb & ~(MAX << this.shift) | (value & MAX) << this.shift;
		}
	}
}
