package com.heroacademy.common.attachment;

import com.heroacademy.HeroAcademy;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HeroAcademy.MODID);

    public static final Supplier<AttachmentType<HeroData>> HERO_DATA =
            ATTACHMENT_TYPES.register("hero_data", () -> AttachmentType.serializable(HeroData::new)
                    .copyOnDeath()
                    .build());
}
