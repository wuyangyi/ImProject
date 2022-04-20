package net.im_server;

import net.box.StringReceivePacket;
import net.handle.ConnectorHandler;
import net.handle.ConnectorStringPacketChain;

public class ServerStatistics {
    long receiveSize;
    long sendSize;

    ConnectorStringPacketChain statisticsChain() {
        return new StatisticConnectorStringPacketChain();
    }

    class StatisticConnectorStringPacketChain extends ConnectorStringPacketChain {

        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket stringReceivePacket) {
            // 接收数据量自增
            receiveSize++;
            return false;
        }
    }

}
