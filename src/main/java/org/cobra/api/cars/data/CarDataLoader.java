package org.cobra.api.cars.data;

import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.cobra.api.cars.CarsAPI;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CarDataLoader implements SimpleSynchronousResourceReloadListener {
    public static final Map<Identifier, CarDefinition> CAR_DEFINITIONS = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return Identifier.of(CarsAPI.MOD_ID, "car_definition");
    }

    @Override
    public void reload(ResourceManager manager) {
        CAR_DEFINITIONS.clear();

        var resources = manager.findResources("car_definition", id -> id.getPath().endsWith(".json"));

        for (var entry : resources.entrySet()) {
            Identifier id = entry.getKey();
            try (var reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {
                var json = JsonParser.parseReader(reader);
                DataResult<CarDefinition> result = CarDefinition.CODEC.parse(JsonOps.INSTANCE, json);

                CarDefinition def = result.getOrThrow(Throwable::new);

                result.result().ifPresentOrElse(carDef -> {
                    String fileName = id.getPath().substring(id.getPath().lastIndexOf('/') + 1).replace(".json", "");
                    Identifier carId = Identifier.of(CarsAPI.MOD_ID, fileName);
                    CAR_DEFINITIONS.put(carId, def);
                    CarsAPI.LOGGER.info("Loaded car config: {}", carId);
                }, () -> result.error().ifPresent(error -> CarsAPI.LOGGER.error("Failed to load car config '{}'", id)));
            } catch (Exception e) {
                CarsAPI.LOGGER.error("Failed to load car config {}", entry.getKey(), e);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
