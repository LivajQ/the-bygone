package com.jamiedev.bygone.core.registry;

public class BGJukeboxSongs {
    /*
    static ResourceKey<JukeboxSong> SHUFFLE = create("shuffle");
    static ResourceKey<JukeboxSong> UNDER = create("under");
    SoundEvents ref;

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
        return JinxedRegistryHelper.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Bygone.MOD_ID, name, type);
    }

    private static ResourceKey<JukeboxSong> create(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, Bygone.id(name));
    }

    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Holder.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        context.register(key, new JukeboxSong(soundEvent, Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), (float) lengthInSeconds, comparatorOutput));
    }

    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Holder<SoundEvent> sound, float length, int output) {
        context.register(key, new JukeboxSong(sound, Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), length, output));
    }

    static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, SHUFFLE, BGSoundEvents.MUSIC_DISC_SHUFFLE, 178, 1);
        register(context, UNDER, BGSoundEvents.MUSIC_DISC_UNDER, 192, 2);
    }
     */
}
