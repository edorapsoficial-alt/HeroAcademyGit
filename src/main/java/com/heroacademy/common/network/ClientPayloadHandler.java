package com.heroacademy.common.network;

import com.heroacademy.client.ClientHeroData;
import com.heroacademy.client.gui.StudentIDScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    public static void handleSyncHeroData(final SyncHeroDataPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientHeroData.update(payload);
        });
    }

    public static void handleOpenStudentID(final OpenStudentIDPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new StudentIDScreen());
        });
    }

    public static void handleOpenTransitTerminal(final OpenTransitTerminalPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new com.heroacademy.client.gui.ImperialTransitScreen());
        });
    }

    public static void handleOpenCampusNavigation(final OpenCampusNavigationPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new com.heroacademy.client.gui.CampusNavigationScreen());
        });
    }
}
