package com.heroacademy.common.item;

import com.heroacademy.HeroAcademy;
import com.heroacademy.common.block.ModBlocks;
import com.heroacademy.common.item.weapon.KatanaItem;
import com.heroacademy.common.item.weapon.ScytheItem;
import com.heroacademy.common.item.weapon.SpearItem;
import com.heroacademy.common.item.weapon.TonfaItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(HeroAcademy.MODID);

    // Itens Acadêmicos
    public static final DeferredItem<Item> STUDENT_ID_CARD = ITEMS.register(
            "student_id_card",
            () -> new StudentIDCardItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE))
    );

    public static final DeferredItem<Item> MAGIC_TOME = ITEMS.register(
            "magic_tome",
            () -> new MagicTomeItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
    );

    public static final DeferredItem<BlockItem> CLASSROOM_DESK_ITEM = ITEMS.register(
            "classroom_desk",
            () -> new BlockItem(ModBlocks.CLASSROOM_DESK.get(), new Item.Properties())
    );

    public static final DeferredItem<Item> IMPERIAL_CREDIT = ITEMS.register(
            "imperial_credit",
            () -> new ImperialCreditItem(new Item.Properties())
    );

    public static final DeferredItem<Item> CAMPUS_COMPASS = ITEMS.register(
            "campus_compass",
            () -> new CampusCompassItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE))
    );

    public static final DeferredItem<BlockItem> IMPERIAL_TRANSIT_TERMINAL_ITEM = ITEMS.register(
            "imperial_transit_terminal",
            () -> new BlockItem(ModBlocks.IMPERIAL_TRANSIT_TERMINAL.get(), new Item.Properties().rarity(Rarity.RARE))
    );

    // ==========================================
    // ARMAS DE HASTE (LANÇAS)
    // ==========================================
    public static final DeferredItem<Item> TRAINING_SPEAR = ITEMS.register("training_spear",
            () -> new SpearItem(Tiers.WOOD, new Item.Properties()));
    public static final DeferredItem<Item> IRON_SPEAR = ITEMS.register("iron_spear",
            () -> new SpearItem(Tiers.IRON, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_SPEAR = ITEMS.register("diamond_spear",
            () -> new SpearItem(Tiers.DIAMOND, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_SPEAR = ITEMS.register("netherite_spear",
            () -> new SpearItem(Tiers.NETHERITE, new Item.Properties().fireResistant()));

    // ==========================================
    // ARMAS ORIENTAIS (KATANAS)
    // ==========================================
    public static final DeferredItem<Item> TRAINING_KATANA = ITEMS.register("training_katana",
            () -> new KatanaItem(Tiers.WOOD, new Item.Properties()));
    public static final DeferredItem<Item> IRON_KATANA = ITEMS.register("iron_katana",
            () -> new KatanaItem(Tiers.IRON, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_KATANA = ITEMS.register("diamond_katana",
            () -> new KatanaItem(Tiers.DIAMOND, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_KATANA = ITEMS.register("netherite_katana",
            () -> new KatanaItem(Tiers.NETHERITE, new Item.Properties().fireResistant()));

    // ==========================================
    // ARMAS ORIENTAIS (TONFAS)
    // ==========================================
    public static final DeferredItem<Item> TRAINING_TONFA = ITEMS.register("training_tonfa",
            () -> new TonfaItem(Tiers.WOOD, new Item.Properties()));
    public static final DeferredItem<Item> IRON_TONFA = ITEMS.register("iron_tonfa",
            () -> new TonfaItem(Tiers.IRON, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_TONFA = ITEMS.register("diamond_tonfa",
            () -> new TonfaItem(Tiers.DIAMOND, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_TONFA = ITEMS.register("netherite_tonfa",
            () -> new TonfaItem(Tiers.NETHERITE, new Item.Properties().fireResistant()));

    // ==========================================
    // ARMAS EXÓTICAS (FOICES)
    // ==========================================
    public static final DeferredItem<Item> TRAINING_SCYTHE = ITEMS.register("training_scythe",
            () -> new ScytheItem(Tiers.WOOD, new Item.Properties()));
    public static final DeferredItem<Item> IRON_SCYTHE = ITEMS.register("iron_scythe",
            () -> new ScytheItem(Tiers.IRON, new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_SCYTHE = ITEMS.register("diamond_scythe",
            () -> new ScytheItem(Tiers.DIAMOND, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_SCYTHE = ITEMS.register("netherite_scythe",
            () -> new ScytheItem(Tiers.NETHERITE, new Item.Properties().fireResistant()));
}
