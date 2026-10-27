package dev.voidedaries.aries.client.resourcepack;

import dev.voidedaries.aries.ModConstants;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.function.Consumer;

public class PersistentHypixelPackSource implements RepositorySource {
    private static final String PACK_ID = ModConstants.MOD_ID + ":hypixel_server_pack";

    @Override
    public void loadPacks(@NonNull Consumer<Pack> result) {
        PersistentHypixelPack.Entry entry = PersistentHypixelPack.getPackForRepository();

        if (entry == null || !ServerResourcePackCache.isCached(entry.id(), entry.hash())) {
            return;
        }

        PackLocationInfo location = new PackLocationInfo(
            PACK_ID,
            Component.literal("Hypixel Server Resource Pack"),
            PackSource.DEFAULT,
            Optional.empty()
        );

        Pack.ResourcesSupplier resources = new FilePackResources.FileResourcesSupplier(entry.path());

        Pack.Metadata metadata = Pack.readPackMetadata(
            location,
            resources,
            SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES),
            PackType.CLIENT_RESOURCES
        );

        if (metadata == null) {
            return;
        }

        Pack pack = new Pack(
            location,
            resources,
            metadata,
            new PackSelectionConfig(true, Pack.Position.TOP, true)
        );

        PersistentHypixelPack.markPendingPackOffered(entry.id(), entry.hash());
        result.accept(pack);
    }
}
