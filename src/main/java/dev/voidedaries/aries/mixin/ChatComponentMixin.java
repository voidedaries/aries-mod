package dev.voidedaries.aries.mixin;

import dev.voidedaries.aries.client.feature.AriesFeatures;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    public abstract void rescaleChat();

    // Stores base text of a compacted message with its current count
    private record CompactEntry(String baseText, int count) {}

    // Tracks components that are created, so a player typing " (2)" cant match a compacted message
    @Unique
    private final Map<Component, CompactEntry> compactedMessages = new IdentityHashMap<>();

    @Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
    private void compactChat(
        Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci
    ) {
        if (!AriesFeatures.COMPACT_CHAT.isEnabled() || this.allMessages.isEmpty()) {
            return;
        }

        String incomingText = contents.getString();

        Minecraft minecraft = Minecraft.getInstance();
        int currentTick = minecraft.gui.hud.getGuiTicks();
        int compactTime = AriesFeatures.COMPACT_CHAT_TIME.getCompactTimeSeconds() * SharedConstants.TICKS_PER_SECOND;

        // delete compactedByMod of entries no longer in allMessages
        Set<Component> activeMessages = Collections.newSetFromMap(new IdentityHashMap<>());
        for (GuiMessage msg : this.allMessages) {
            activeMessages.add(msg.content());
        }

        compactedMessages.keySet().retainAll(activeMessages);

        // searches recent messages within the time frame for a duplicate
        for (int time = 0; time < this.allMessages.size(); time++) {
            GuiMessage existingMessage = this.allMessages.get(time);

            // stop if over compact time
            if (currentTick - existingMessage.addedTime() > compactTime) {
                break;
            }

            Component existingComponent = existingMessage.content();
            CompactEntry messageEntry = compactedMessages.get(existingComponent);

            String baseText = messageEntry != null ? messageEntry.baseText() : existingComponent.getString();

            if (!incomingText.equals(baseText)) {
                continue;
            }

            int newCount = messageEntry != null ? messageEntry.count() + 1 : 2;

            Component compacted = Component.empty()
                .append(contents.copy())
                .append(
                    Component.literal(" (" + newCount + ")")
                        .withStyle(s -> s
                            .withColor(ChatFormatting.GRAY)
                            .withBold(false)
                            .withItalic(false))
                );

            compactedMessages.remove(existingComponent);
            compactedMessages.put(compacted, new CompactEntry(incomingText, newCount));

            this.allMessages.remove(time);
            this.allMessages.addFirst(
                new GuiMessage(
                    minecraft.gui.hud.getGuiTicks(),
                    compacted,
                    existingMessage.signature(),
                    existingMessage.source(),
                    existingMessage.tag()
                )
            );

            this.rescaleChat();
            ci.cancel();
            return;
        }
        
    }

}
