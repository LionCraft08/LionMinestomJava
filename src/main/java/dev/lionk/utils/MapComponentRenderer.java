package dev.lionk.utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MapComponentRenderer {

    /**
     * Draws an Adventure Component onto a Minestom LargeGraphics2DFramebuffer.
     *
     * @param framebuffer The Minestom framebuffer to draw on.
     * @param component   The component to render.
     * @param x           The starting X coordinate (left).
     * @param y           The baseline Y coordinate (bottom of the text).
     */
    public static void drawComponent(LargeGraphics2DFramebuffer framebuffer, Component component, int x, int y) {
        // 1. Get the AWT Graphics2D object from the framebuffer
        Graphics2D g2d = framebuffer.getRenderer();

        // 2. Save the original state to restore later (good practice)
        Color originalColor = g2d.getColor();
        Font originalFont = g2d.getFont();

        // 3. Begin recursive rendering
        // We use an AtomicInteger for X so it updates across the recursive stack
        drawNode(g2d, component, Style.empty(), new AtomicInteger(x), y);

        // 4. Restore original state
        g2d.setColor(originalColor);
        g2d.setFont(originalFont);
    }

    private static void drawNode(Graphics2D g, Component component, Style parentStyle, AtomicInteger cursorX, int y) {
        // Merge the parent style into the current component's style.
        // Strategy.IF_ABSENT_ON_TARGET ensures children inherit parent attributes
        // (e.g., if parent is Red and child has no color, child becomes Red).
        Style effectiveStyle = component.style().merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);

        // Render the text content if this is a TextComponent
        if (component instanceof TextComponent textComponent) {
            String content = textComponent.content();

            if (!content.isEmpty()) {
                applyStyleToGraphics(g, effectiveStyle);

                // Draw the string at the current cursor position
                g.drawString(content, cursorX.get(), y);

                // Advance the cursor by the width of the drawn text
                FontMetrics metrics = g.getFontMetrics();
                cursorX.addAndGet(metrics.stringWidth(content));
            }
        }

        // Recursively draw children
        for (Component child : component.children()) {
            drawNode(g, child, effectiveStyle, cursorX, y);
        }
    }

    private static void applyStyleToGraphics(Graphics2D g, Style style) {
        // 1. Handle Color
        if (style.color() != null) {
            TextColor adventureColor = style.color();
            g.setColor(new Color(adventureColor.value()));
        }

        // 2. Handle Font Styles (Bold/Italic)
        int fontStyle = Font.PLAIN;
        if (style.hasDecoration(TextDecoration.BOLD)) {
            fontStyle |= Font.BOLD;
        }
        if (style.hasDecoration(TextDecoration.ITALIC)) {
            fontStyle |= Font.ITALIC;
        }

        // 3. Create the base font with Bold/Italic
        // We derive from the current font to keep size/family consistent
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(fontStyle);

        // 4. Handle Advanced Decorations (Underline/Strikethrough) via Attributes
        Map<TextAttribute, Object> attributes = new HashMap<>(newFont.getAttributes());

        if (style.hasDecoration(TextDecoration.UNDERLINED)) {
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }
        if (style.hasDecoration(TextDecoration.STRIKETHROUGH)) {
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }

        // Apply final font
        g.setFont(newFont.deriveFont(attributes));
    }
}
