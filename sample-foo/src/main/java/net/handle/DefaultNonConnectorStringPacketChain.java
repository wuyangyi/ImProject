package net.handle;

import net.box.StringReceivePacket;

/**
 * 默认String接收解点，不做任何事情
 */
public class DefaultNonConnectorStringPacketChain extends ConnectorStringPacketChain {
    @Override
    protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
        return false;
    }
}
