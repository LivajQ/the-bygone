package com.jamiedev.bygone.common.weather.weather_types;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class HauntingsEvent extends WeatherType {
    private static final String TIME = "time";
    public HauntingsEvent(ResourceLocation id, @Nullable ServerLevel level) {
        super(id, level);

        this.registerProperty(WeatherProperties::ofInt, TIME, 0).setSync(false);
    }

    private static final int HAUNTING_CYCLE = 1000;
    private static final int HAUNTING_DURATION = 1000;

    @Override
    public void tick() {
        assert level != null;
        WeatherProperties.WeatherProperty<Integer> time = this.getProperty(TIME);
        time.setValue((time.getValue() + 1) % (HAUNTING_CYCLE + HAUNTING_DURATION));

        super.tick();
    }

    @Override public boolean isActive() {
        return ((int) this.getProperty(TIME).getValue() > HAUNTING_CYCLE);
    }

    @Override
    public void startWeather() {
        this.getProperty(TIME).setValue(HAUNTING_CYCLE);
    }

    @Override
    public void clearWeather() {
        this.getProperty(TIME).setValue(0);
    }
}
