package org.cobra.api.cars.data;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.cobra.api.cars.entity.CarEntity;
import org.cobra.api.cars.storage.CarFuelStorage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public abstract class CarDefinitionDataGenerator {

    public final RegistryWrapper.WrapperLookup registries;
    public final BiConsumer<Identifier, CarDefinition> exporter;

    public CarDefinitionDataGenerator(RegistryWrapper.WrapperLookup registries, BiConsumer<Identifier, CarDefinition> exporter) {
        this.registries = registries;
        this.exporter = exporter;
    }

    public abstract void generate();

        public static abstract class Provider implements DataProvider {
    private final FabricDataOutput output;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

    private final Map<Identifier, JsonObject> cars = new HashMap<>();

    public Provider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    protected abstract CarDefinitionDataGenerator generateCarDefinition(RegistryWrapper.WrapperLookup registries, BiConsumer<Identifier, CarDefinition> exporter);

    public static void registerCarDefinition(EntityType<? extends CarEntity<? extends Number, ? extends CarFuelStorage<? extends Number>>> entity, CarDefinition definition, BiConsumer<Identifier, CarDefinition> exporter) {
        Identifier id = Registries.ENTITY_TYPE.getId(entity);
        exporter.accept(id, definition);
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registriesFuture.thenCompose(wrapperLookup -> {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            CarDefinitionDataGenerator generator = generateCarDefinition(wrapperLookup, (id, definition) -> {
                    DataOutput.PathResolver resolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "car_definition/");
                    Path path = resolver.resolve(Identifier.of(output.getModId(), id.getPath()), "json");
                    futures.add(DataProvider.writeCodecToPath(writer, CarDefinition.CODEC, definition, path));
                });

            generator.generate();
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Cars Definitions";
        }
    }
}
