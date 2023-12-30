package com.minehaptics.haptics;


import com.google.common.base.Charsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Utils {

    public static InputStream getAssetAsStream(String name, boolean required) throws IOException {
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation("tact", name));

        return resource.get().open();
    }

    public static byte[] loadAsset(String name, boolean required) throws IOException {
        InputStream inputstream = getAssetAsStream(name, required);
        byte[] abyte = IOUtils.toByteArray(inputstream);
        inputstream.close();
        return abyte;
    }

    public static String loadAssetAsString(String name, boolean required) throws IOException {
        byte[] abyte = loadAsset(name, required);
        return abyte == null ? null : new String(abyte, Charsets.UTF_8);
    }

    public static String getTactString(String tactFile) {
        String content;
        try {
            content = loadAssetAsString(tactFile + ".tact", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public static Boolean isSnowingOnPlayer(Player player) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;
        Biome biome = (Biome) level.getBiome(player.blockPosition()).value();
        return level.isRaining()
                && level.canSeeSky(player.blockPosition())
                && !(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, player.blockPosition()).getY() > player.blockPosition().getY())
                && biome.getPrecipitationAt(player.blockPosition()) == Biome.Precipitation.SNOW;
    }
}
