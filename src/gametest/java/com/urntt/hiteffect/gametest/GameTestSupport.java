package com.urntt.hiteffect.gametest;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.util.ARGB;

/**
 * Helpers shared by the game tests.
 */
final class GameTestSupport {
	/** A pixel counts as "flash blue" when blue clearly dominates; see {@link #countFlashPixels}. */
	private static final int MIN_BLUE = 40;
	private static final int MAX_RED_GREEN = 16;

	private GameTestSupport() {
	}

	static void check(final boolean condition, final String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

	/**
	 * Counts the pixels of a screenshot, within the given fractions of its width and height, that show the test's
	 * pure blue hurt color. At full strength the hurt overlay replaces an entity's texture color, so only lighting
	 * darkens it; nothing else in the test scenes is this blue (the sky has plenty of red and green).
	 */
	static int countFlashPixels(final Path screenshot, final double left, final double top, final double right, final double bottom) {
		try (InputStream input = Files.newInputStream(screenshot); NativeImage image = NativeImage.read(input)) {
			int count = 0;
			for (int y = (int) (top * image.getHeight()); y < (int) (bottom * image.getHeight()); y++) {
				for (int x = (int) (left * image.getWidth()); x < (int) (right * image.getWidth()); x++) {
					int pixel = image.getPixel(x, y);
					if (ARGB.blue(pixel) >= MIN_BLUE && ARGB.red(pixel) <= MAX_RED_GREEN && ARGB.green(pixel) <= MAX_RED_GREEN) {
						count++;
					}
				}
			}
			return count;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
