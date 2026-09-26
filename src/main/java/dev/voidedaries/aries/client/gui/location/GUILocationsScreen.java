package dev.voidedaries.aries.client.gui.location;

import dev.voidedaries.aries.client.AriesConfig;
import dev.voidedaries.aries.client.feature.AriesFeature;
import dev.voidedaries.aries.client.gui.AriesScreen;
import dev.voidedaries.aries.client.gui.ScreenHelper;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GUILocationsScreen extends Screen {

    private final Screen parent;

    private HudRenderable selectedHud;
    private HudRenderable draggableHud;
    private int dragOffsetX;
    private int dragOffsetY;

    private final Map<HudRenderable, HudBounds> hudBounds = new HashMap<>();

    // tips
    private enum ResizeHandle {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    private static final int TIP_DURATION_TICKS = 20 * SharedConstants.TICKS_PER_SECOND;

    private static final List<Component> HUD_TIPS = List.of(
        Component.translatable("gui.menu.hud.drag_tip"),
        Component.translatable("gui.menu.hud.disable_snap_tip"),
        Component.translatable("gui.menu.hud.arrow_key_tip"),
        Component.translatable("gui.menu.hud.arrow_key_shift_tip"),
        Component.translatable("gui.menu.hud.scroll_keyboard_tip"),
        Component.translatable("gui.menu.hud.scroll_mouse_tip")
    );

    private int tipTicks;
    private int currentTip;

    // context Menu
    private HudRenderable contextHud;
    private int contextMenuX;
    private int contextMenuY;

    private enum ContextMenuAction {
        CONFIGURE_FEATURE, TOGGLE_HUD, RESET_POSITION, RESET_SCALE
    }

    private ContextMenuAction hoveredContextAction;

    private static final int CONTEXT_MENU_WIDTH = 100;
    private static final int CONTEXT_MENU_ITEM_HEIGHT = (int) (AriesScreen.PADDING * 1.75);

    // resizing
    private HudRenderable resizingHud;
    private ResizeHandle resizeHandle;
    private float resizeStartScale;
    private HudBounds resizeStartBounds;

    private static final int RESIZE_HANDLE_SIZE = 4;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;
    private static final float SCALE_STEP = 0.1f;

    private static final int HUD_MOVE_STEP = 1;

    public static final int SNAP_DISTANCE = 8;

    public GUILocationsScreen(Screen parent) {
        super(Component.translatable("gui.locations_screen"));
        this.parent = parent;
    }

    @Override
    public void tick() {
        super.tick();

        tipTicks++;

        if (tipTicks >= TIP_DURATION_TICKS) {
            tipTicks = 0;
            currentTip = (currentTip + 1) % HUD_TIPS.size();
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.fill(0, 0, width, height, 0x11000000);

        renderTips(graphics);
        renderRegions(graphics);
        renderHudElements(graphics);

        if (contextHud != null) {
            renderContextMenu(graphics);
        }
    }

    private void renderTips(GuiGraphicsExtractor graphics) {
        Component tip = HUD_TIPS.get(currentTip);

        Minecraft minecraft = Minecraft.getInstance();

        int tipWidth = minecraft.font.width(tip);
        int tipX = (width - tipWidth) / 2;
        int tipY = height - (AriesScreen.PADDING * 4);

        graphics.text(minecraft.font, tip, tipX, tipY, 0xFFFFFFFF);
    }

    private void renderRegions(GuiGraphicsExtractor graphics) {
        int thirdWidth = width / 3;
        int thirdHeight = height / 3;

        int lineColor = 0xAAFFFFFF;

        graphics.fill(thirdWidth, 0, thirdWidth + 1, height, lineColor);

        graphics.fill(thirdWidth * 2, 0, thirdWidth * 2 + 1, height, lineColor);

        graphics.fill(0, thirdHeight, width, thirdHeight + 1, lineColor);

        graphics.fill(0, thirdHeight * 2, width, thirdHeight * 2 + 1, lineColor);
    }

    private void renderHudElements(GuiGraphicsExtractor graphics) {
        hudBounds.clear();

        for (HudRenderable hud : AriesHudManager.getHudElements()) {
            if (!hud.isHudEnabled()) {
                continue;
            }

            HudBounds bounds = AriesHudManager.renderHud(graphics, hud);
            hudBounds.put(hud, bounds);
        }

        if (selectedHud != null) {
            HudBounds bounds = hudBounds.get(selectedHud);

            if (bounds != null) {
                graphics.fill(
                    bounds.x(), bounds.y(),
                    bounds.x() + bounds.width(),
                    bounds.y() + bounds.height(),
                    0x33FFFFFF
                );

                renderResizeHandles(graphics, bounds);
                renderSelectedHudInfo(graphics, bounds);
            }
        }

    }

    private void renderResizeHandles(
        GuiGraphicsExtractor graphics,
        HudBounds bounds
    ) {
        int halfSize = RESIZE_HANDLE_SIZE / 2;

        int left = bounds.x();
        int right = bounds.x() + bounds.width();
        int top = bounds.y();
        int bottom = bounds.y() + bounds.height();

        graphics.fill(
            left - halfSize,
            top - halfSize,
            left + halfSize,
            top + halfSize,
            0xFFFFFFFF
        );

        graphics.fill(
            right - halfSize,
            top - halfSize,
            right + halfSize,
            top + halfSize,
            0xFFFFFFFF
        );

        graphics.fill(
            left - halfSize,
            bottom - halfSize,
            left + halfSize,
            bottom + halfSize,
            0xFFFFFFFF
        );

        graphics.fill(
            right - halfSize,
            bottom - halfSize,
            right + halfSize,
            bottom + halfSize,
            0xFFFFFFFF
        );
    }

    private void renderSelectedHudInfo(GuiGraphicsExtractor graphics, HudBounds bounds) {
        GuiRegions region = getHudRegion(bounds);

        if (region == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        String info = String.format(
            "x:%d, y:%d, scale:%.1f, %s",
            bounds.x(), bounds.y(),
            selectedHud.getHudPosition().getScale(),
            region.name().toLowerCase(Locale.ROOT)
        );

        int infoWidth = minecraft.font.width(info);
        int textHeight = minecraft.font.lineHeight;

        int textX;

        switch (region) {
            case TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT -> textX = bounds.x();
            case TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT -> textX = bounds.x() + bounds.width() - infoWidth;
            default -> textX = bounds.x() + (bounds.width() - infoWidth) / 2;
        }

        int textY;
        int gap = (int) (AriesScreen.PADDING / 2.5);

        if (bounds.y() - gap - textHeight >= 0) {
            textY = bounds.y() - gap - textHeight;
        } else  {
            textY = bounds.y() + bounds.height() + gap;
        }

        graphics.text(minecraft.font, info, textX, textY, 0xFFFFFFFF);

    }

    private GuiRegions getHudRegion(HudBounds bounds) {
        double x = bounds.x();
        double y = bounds.y();

        for (GuiRegions region : GuiRegions.values()) {
            GuiBounds screenBounds = region.getRegion().toScreenBounds(width, height);

            if (screenBounds.contains(x, y)) {
                return region;
            }
        }

        return null;
    }

    private boolean isShiftDown() {
        long window = Minecraft.getInstance().getWindow().handle();

        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    private boolean isControlDown() {
        long window = Minecraft.getInstance().getWindow().handle();

        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }

    private void moveSelectedHud(int deltaX, int deltaY) {
        if (selectedHud == null) {
            return;
        }

        HudPosition position = selectedHud.getHudPosition();
        HudBounds bounds = hudBounds.get(selectedHud);

        if (bounds == null) {
            return;
        }

        int step = isShiftDown() ? HUD_MOVE_STEP * 10 : HUD_MOVE_STEP;

        int newX = position.getX() + deltaX * step;
        int newY = position.getY() + deltaY * step;

        newX = Math.clamp(newX, 0, width - bounds.width());
        newY = Math.clamp(newY, 0, height - bounds.height());

        position.setPosition(newX, newY);
    }

    private void setHudScale(HudRenderable hud, float scale) {
        float newScale = Math.clamp(scale, MIN_SCALE, MAX_SCALE);
        newScale = Math.round(newScale / SCALE_STEP) * SCALE_STEP;
        newScale = Math.clamp(newScale, MIN_SCALE, MAX_SCALE);

        hud.getHudPosition().setScale(newScale);
    }

    private void scaleSelectedHud(int direction) {
        if (selectedHud == null) {
            return;
        }

        HudPosition position = selectedHud.getHudPosition();

        setHudScale(selectedHud, position.getScale() + direction * SCALE_STEP);
    }

    private ResizeHandle getResizeHandle(
        double mouseX,
        double mouseY,
        HudBounds bounds
    ) {
        int halfSize = RESIZE_HANDLE_SIZE / 2;

        int left = bounds.x();
        int right = bounds.x() + bounds.width();
        int top = bounds.y();
        int bottom = bounds.y() + bounds.height();

        if (isInsideHandle(mouseX, mouseY, left, top, halfSize)) {
            return ResizeHandle.TOP_LEFT;
        }

        if (isInsideHandle(mouseX, mouseY, right, top, halfSize)) {
            return ResizeHandle.TOP_RIGHT;
        }

        if (isInsideHandle(mouseX, mouseY, left, bottom, halfSize)) {
            return ResizeHandle.BOTTOM_LEFT;
        }

        if (isInsideHandle(mouseX, mouseY, right, bottom, halfSize)) {
            return ResizeHandle.BOTTOM_RIGHT;
        }

        return null;
    }

    private void resizeHud(double mouseX, double mouseY) {
        HudPosition position = resizingHud.getHudPosition();

        double scaleChange;

        switch (resizeHandle) {
            case TOP_LEFT -> {
                double deltaX = resizeStartBounds.x() - mouseX;
                double deltaY = resizeStartBounds.y() - mouseY;

                scaleChange = Math.max(deltaX, deltaY);
            }

            case TOP_RIGHT -> {
                double deltaX = mouseX - (resizeStartBounds.x() + resizeStartBounds.width());
                double deltaY = resizeStartBounds.y() - mouseY;

                scaleChange = Math.max(deltaX, deltaY);
            }

            case BOTTOM_LEFT -> {
                double deltaX = resizeStartBounds.x() - mouseX;
                double deltaY = mouseY - (resizeStartBounds.y() + resizeStartBounds.height());

                scaleChange = Math.max(deltaX, deltaY);
            }

            case BOTTOM_RIGHT -> {
                double deltaX = mouseX - (resizeStartBounds.x() + resizeStartBounds.width());
                double deltaY = mouseY - (resizeStartBounds.y() + resizeStartBounds.height());

                scaleChange = Math.max(deltaX, deltaY);
            }

            default -> {
                return;
            }
        }

        double baseSize = Math.max(
            resizeStartBounds.width(),
            resizeStartBounds.height()
        );

        if (baseSize <= 0) {
            return;
        }

        float newScale = resizeStartScale + (float) (scaleChange / baseSize);

        setHudScale(resizingHud, newScale);

        newScale = position.getScale();

        double scaleRatio = newScale / resizeStartScale;

        int newWidth = (int) Math.round(resizeStartBounds.width() * scaleRatio);
        int newHeight = (int) Math.round(resizeStartBounds.height() * scaleRatio);

        int newX = resizeStartBounds.x();
        int newY = resizeStartBounds.y();

        switch (resizeHandle) {
            case TOP_LEFT -> {
                newX = resizeStartBounds.x() + resizeStartBounds.width() - newWidth;
                newY = resizeStartBounds.y() + resizeStartBounds.height() - newHeight;
            }

            case TOP_RIGHT -> newY = resizeStartBounds.y() + resizeStartBounds.height() - newHeight;

            case BOTTOM_LEFT -> newX = resizeStartBounds.x() + resizeStartBounds.width() - newWidth;

            case BOTTOM_RIGHT -> {
                // Top-left remains anchored.
            }
        }

        position.setPosition(
            Math.clamp(newX, 0, width - newWidth),
            Math.clamp(newY, 0, height - newHeight)
        );
    }

    private void selectHud(HudRenderable hud) {
        if (selectedHud != hud) {
            if (selectedHud != null) {
                AriesConfig.save();
            }

            selectedHud = hud;
        }
    }

    private void deselectHud() {
        if (selectedHud == null) {
            return;
        }

        AriesConfig.save();
        selectedHud = null;
    }

    private void openContextMenu(HudRenderable hud, int x, int y) {
        contextHud = hud;

        contextMenuX = Math.clamp(x, 0, width - CONTEXT_MENU_WIDTH);
        contextMenuY = Math.clamp(y, 0, height - getContextMenuHeight());
        hoveredContextAction = getContextMenuAction(x, y);
    }

    private void renderContextMenu(GuiGraphicsExtractor graphics) {
        int menuHeight = getContextMenuHeight();

        graphics.fill(
            contextMenuX, contextMenuY,
            contextMenuX + CONTEXT_MENU_WIDTH, contextMenuY + menuHeight,
            0xFF222933
        );

        int y = contextMenuY;

        for (ContextMenuAction action : ContextMenuAction.values()) {
            if (action == hoveredContextAction) {
                graphics.fill(
                    contextMenuX, y,
                    contextMenuX + CONTEXT_MENU_WIDTH, y + CONTEXT_MENU_ITEM_HEIGHT,
                    0xFF151A21
                );
            }

            graphics.text(
                font,
                getContextMenuText(action),
                contextMenuX + (AriesScreen.PADDING / 2),
                y + (AriesScreen.PADDING / 2),
                0xFFFFFFFF
            );

            y += CONTEXT_MENU_ITEM_HEIGHT;
        }
    }

    private void handleContextMenuAction(ContextMenuAction action) {
        if (contextHud == null) {
            return;
        }

        switch (action) {
            case CONFIGURE_FEATURE -> configureFeature(contextHud);

            case TOGGLE_HUD -> toggleHud(contextHud);

            case RESET_POSITION -> {
                contextHud.getHudPosition().setPosition(0, 0);
                contextHud = null;
                AriesConfig.save();
            }

            case RESET_SCALE -> {
                contextHud.getHudPosition().setScale(1.0f);
                contextHud = null;
                AriesConfig.save();
            }

        }
    }

    private void configureFeature(HudRenderable hud) {
        if (!(hud instanceof AriesFeature feature)) {
            return;
        }

        contextHud = null;
        hoveredContextAction = null;
        draggableHud = null;
        resizingHud = null;
        resizeHandle = null;
        resizeStartBounds = null;

        Minecraft.getInstance().setScreenAndShow(new AriesScreen(feature, this));
    }

    private void toggleHud(HudRenderable hud) {
        boolean enabled = !hud.isHudEnabled();

        hud.setHudEnabled(enabled);

        if (!enabled && selectedHud == hud) {
            selectedHud = null;
        }

        contextHud = null;
        AriesConfig.save();
    }

    private Component getContextMenuText(ContextMenuAction action) {
        return switch (action) {
            case CONFIGURE_FEATURE -> Component.translatable("gui.menu.hud.context.configure_feature");

            case TOGGLE_HUD -> contextHud.isHudEnabled()
                ? Component.translatable("gui.menu.hud.context.disable")
                : Component.translatable("gui.menu.hud.context.enable");

            case RESET_POSITION -> Component.translatable("gui.menu.hud.context.reset_position");

            case RESET_SCALE -> Component.translatable("gui.menu.hud.context.reset_scale");
        };
    }

    private ContextMenuAction getContextMenuAction(double mouseX, double mouseY) {
        if (contextHud == null) {
            return null;
        }

        if (mouseX < contextMenuX || mouseX >= contextMenuX + CONTEXT_MENU_WIDTH ||
            mouseY < contextMenuY || mouseY >= contextMenuY + getContextMenuHeight()) {
            return null;
        }

        int index =
            (int) ((mouseY - contextMenuY) / CONTEXT_MENU_ITEM_HEIGHT);

        ContextMenuAction[] actions = ContextMenuAction.values();

        if (index < 0 || index >= actions.length) {
            return null;
        }

        return actions[index];
    }

    private int getContextMenuHeight() {
        return ContextMenuAction.values().length * CONTEXT_MENU_ITEM_HEIGHT;
    }

    private boolean isInsideHandle(
        double mouseX,
        double mouseY,
        int handleX,
        int handleY,
        int halfSize
    ) {
        return mouseX >= handleX - halfSize
            && mouseX <= handleX + halfSize
            && mouseY >= handleY - halfSize
            && mouseY <= handleY + halfSize;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (selectedHud == null) {
            return super.keyPressed(event);
        }

        switch (event.key()) {
            case GLFW.GLFW_KEY_LEFT -> {
                moveSelectedHud(-1, 0);
                return true;
            }

            case GLFW.GLFW_KEY_RIGHT -> {
                moveSelectedHud(1, 0);
                return true;
            }

            case GLFW.GLFW_KEY_UP -> {
                moveSelectedHud(0, -1);
                return true;
            }

            case GLFW.GLFW_KEY_DOWN -> {
                moveSelectedHud(0, 1);
                return true;
            }

            default -> {
                return super.keyPressed(event);
            }
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (contextHud != null) {
            hoveredContextAction = getContextMenuAction(mouseX, mouseY);
        } else {
            hoveredContextAction = null;
        }

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_2) {
            for (HudRenderable hud : AriesHudManager.getHudElements()) {
                if (!hud.isHudEnabled()) {
                    continue;
                }

                HudBounds bounds = hudBounds.get(hud);

                if (bounds == null) {
                    continue;
                }

                if (ScreenHelper.isHovered(
                    event.x(), event.y(),
                    bounds.x(), bounds.y(),
                    bounds.width(), bounds.height()
                )) {
                    selectHud(hud);
                    openContextMenu(hud, (int) event.x(), (int) event.y());

                    return true;
                }
            }

            contextHud = null;
            return true;
        }

        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_1) {
            return super.mouseClicked(event, doubleClick);
        }

        if (contextHud != null) {
            ContextMenuAction action = getContextMenuAction(event.x(), event.y());

            if (action != null) {
                handleContextMenuAction(action);
                return true;
            }

            contextHud = null;
            return true;
        }

        for (HudRenderable hud : AriesHudManager.getHudElements()) {
            if (!hud.isHudEnabled()) {
                continue;
            }

            HudBounds bounds = hudBounds.get(hud);

            if (bounds == null) {
                continue;
            }

            ResizeHandle handle = getResizeHandle(event.x(), event.y(), bounds);

            if (handle != null) {
                selectHud(hud);

                resizingHud = hud;
                resizeHandle = handle;
                resizeStartScale = hud.getHudPosition().getScale();
                resizeStartBounds = bounds;

                return true;
            }

            HudPosition position = hud.getHudPosition();

            if (ScreenHelper.isHovered(
                event.x(), event.y(),
                bounds.x(), bounds.y(),
                bounds.width(), bounds.height()
            )) {
                selectHud(hud);
                draggableHud = hud;

                dragOffsetX = (int) event.x() - position.getX();
                dragOffsetY = (int) event.y() - position.getY();

                return true;
            }
        }

        deselectHud();

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        if (resizingHud != null && event.button() == 0) {
            resizeHud(event.x(), event.y());

            return true;
        }

        if (draggableHud == null || event.button() != 0) {
            return super.mouseDragged(event, dragX, dragY);
        }

        HudPosition position = draggableHud.getHudPosition();
        HudBounds bounds = hudBounds.get(draggableHud);

        if (bounds == null) {
            return false;
        }

        int newX = (int) event.x() - dragOffsetX;
        int newY = (int) event.y() - dragOffsetY;

        if (!isShiftDown()) {
            newX = GuiSnapHelper.snapX(newX, bounds.width(), width, SNAP_DISTANCE);
            newY = GuiSnapHelper.snapY(newY, bounds.height(), height, SNAP_DISTANCE);
        }

        newX = Math.clamp(newX, 0, width - bounds.width());
        newY = Math.clamp(newY, 0, height - bounds.height());

        position.setPosition(newX, newY);

        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            if (resizingHud != null) {
                resizingHud = null;
                resizeHandle = null;
                resizeStartBounds = null;

                return true;
            }

            if (draggableHud != null) {
                draggableHud = null;

                return true;
            }
        }

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selectedHud == null || !isControlDown()) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        if (verticalAmount > 0) {
            scaleSelectedHud(1);
        } else if (verticalAmount < 0) {
            scaleSelectedHud(-1);
        }

        return true;
    }

    @Override
    public void onClose() {
        AriesConfig.save();

        //? if 26.2
        minecraft.gui.setScreen(parent);
        //? if 26.1.2
        //minecraft.setScreen(parent);
    }
}
