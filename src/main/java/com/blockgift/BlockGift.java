package com.blockgift;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Random;
import java.util.Set;

/** Cada vez que un jugador rompe un bloque, recibe un item aleatorio. */
public class BlockGift implements ModInitializer {

    // Items que NO queremos regalar (solo de creativo / rompen el juego)
    private static final Set<String> BLOCKED = Set.of(
            "air", "barrier", "light", "command_block", "chain_command_block",
            "repeating_command_block", "command_block_minecart", "structure_block",
            "structure_void", "jigsaw", "debug_stick", "knowledge_book", "test_block",
            "test_instance_block"
    );

    private final Random rng = new Random();
    private List<Item> pool;

    @Override
    public void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide()) return;

            ItemStack stack = new ItemStack(randomItem());
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false); // inventario lleno: lo tira al piso
            }
        });
    }

    private Item randomItem() {
        if (pool == null) {
            pool = BuiltInRegistries.ITEM.stream()
                    .filter(i -> i != Items.AIR)
                    .filter(i -> {
                        String path = BuiltInRegistries.ITEM.getKey(i).getPath();
                        return !BLOCKED.contains(path) && !path.endsWith("_spawn_egg");
                    })
                    .toList();
        }
        return pool.get(rng.nextInt(pool.size()));
    }
}
