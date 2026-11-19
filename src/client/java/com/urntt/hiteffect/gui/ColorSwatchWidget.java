package com.urntt.hiteffect.gui;

import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

/**
 * Shows a color. It cannot be clicked.
 */
public final class ColorSwatchWidget extends AbstractWidget {
	static final int BORDER_COLOR = 0xFFA0A0A0;
	static final int INACTIVE_SHADE = 0xA0000000;

	private final IntSupplier rgb;

	/**
	 * @param rgb supplies the color to show, {@code 0xRRGGBB}, read every frame
	 */
	public ColorSwatchWidget(final Component message, final IntSupplier rgb) {
		super(0, 0, 150, 20, message);
		this.rgb = rgb;
	}

	@Override
	protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		drawSwatch(graphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.rgb.getAsInt(), this.active, false);
	}

	@Override
	public boolean isActive() {
		// Only displays a value, so it never takes clicks or focus.
		return false;
	}

	@Override
	protected void updateWidgetNarration(final NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, this.getMessage());
	}

	static void drawSwatch(
			final GuiGraphicsExtractor graphics,
			final int x,
			final int y,
			final int width,
			final int height,
			final int rgb,
			final boolean active,
			final boolean highlighted) {
		graphics.fill(x, y, x + width, y + height, highlighted ? 0xFFFFFFFF : BORDER_COLOR);
		graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, ARGB.opaque(rgb));
		if (!active) {
			graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, INACTIVE_SHADE);
		}
	}
}
