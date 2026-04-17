package com.circulation.circulation_networks.container;

import com.circulation.circulation_networks.CirculationFlowNetworks;
import com.circulation.circulation_networks.packets.ContainerProgressBar;
import com.circulation.circulation_networks.packets.ContainerValueConfig;
import com.circulation.circulation_networks.utils.GuiSyncManager;
import com.circulation.circulation_networks.utils.SyncSender;
import net.minecraft.server.level.ServerPlayer;

final class ContainerSyncCompat {

    private ContainerSyncCompat() {
    }

    static void detectAndSendChanges(CFNBaseContainer container, GuiSyncManager guiSyncManager) {
        guiSyncManager.detectAndSendChanges(createSyncSender(container));
    }

    private static SyncSender createSyncSender(CFNBaseContainer container) {
        return new SyncSender() {
            @Override
            public void sendInt(int channel, int value) {
                sendProgress(channel, value);
            }

            @Override
            public void sendLong(int channel, long value) {
                sendProgress(channel, value);
            }

            @Override
            public void sendByte(int channel, byte value) {
                sendProgress(channel, value);
            }

            @Override
            public void sendShort(int channel, short value) {
                sendProgress(channel, value);
            }

            @Override
            public void sendString(int channel, String value) {
                if (container.player instanceof ServerPlayer player) {
                    CirculationFlowNetworks.sendToPlayer(
                        new ContainerValueConfig((short) channel, value),
                        player
                    );
                }
            }

            @Override
            public void sendBytes(int channel, byte[] value) {
                if (container.player instanceof ServerPlayer player) {
                    CirculationFlowNetworks.sendToPlayer(
                        new ContainerValueConfig((short) channel, value),
                        player
                    );
                }
            }

            private void sendProgress(int channel, long value) {
                if (container.player instanceof ServerPlayer player) {
                    CirculationFlowNetworks.sendToPlayer(
                        new ContainerProgressBar((short) channel, value),
                        player
                    );
                }
            }
        };
    }
}
