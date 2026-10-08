package com.heroacademy.common.network;

import com.heroacademy.HeroAcademy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = HeroAcademy.MODID)
public class ModNetworking {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(HeroAcademy.MODID).versioned("1.0.0");

        registrar.playToServer(
                KeybindCastPayload.TYPE,
                KeybindCastPayload.STREAM_CODEC,
                ServerPayloadHandler::handleKeybindCast
        );

        registrar.playToServer(
                AllocateStatPayload.TYPE,
                AllocateStatPayload.STREAM_CODEC,
                ServerPayloadHandler::handleAllocateStat
        );

        registrar.playToServer(
                AllocateUniqueRoutePayload.TYPE,
                AllocateUniqueRoutePayload.STREAM_CODEC,
                ServerPayloadHandler::handleAllocateUniqueRoute
        );

        registrar.playToServer(
                EquipSkillPayload.TYPE,
                EquipSkillPayload.STREAM_CODEC,
                ServerPayloadHandler::handleEquipSkill
        );

        registrar.playToServer(
                SwitchSkillBarPayload.TYPE,
                SwitchSkillBarPayload.STREAM_CODEC,
                ServerPayloadHandler::handleSwitchSkillBar
        );

        registrar.playToServer(
                AllocateClassPointPayload.TYPE,
                AllocateClassPointPayload.STREAM_CODEC,
                ServerPayloadHandler::handleAllocateClassPoint
        );

        registrar.playToServer(
                ImperialTransitPayload.TYPE,
                ImperialTransitPayload.STREAM_CODEC,
                ServerPayloadHandler::handleImperialTransit
        );

        registrar.playToClient(
                SyncHeroDataPayload.TYPE,
                SyncHeroDataPayload.STREAM_CODEC,
                ClientPayloadHandler::handleSyncHeroData
        );

        registrar.playToClient(
                OpenStudentIDPayload.TYPE,
                OpenStudentIDPayload.STREAM_CODEC,
                ClientPayloadHandler::handleOpenStudentID
        );

        registrar.playToClient(
                OpenTransitTerminalPayload.TYPE,
                OpenTransitTerminalPayload.STREAM_CODEC,
                ClientPayloadHandler::handleOpenTransitTerminal
        );

        registrar.playToClient(
                OpenCampusNavigationPayload.TYPE,
                OpenCampusNavigationPayload.STREAM_CODEC,
                ClientPayloadHandler::handleOpenCampusNavigation
        );
    }
}
