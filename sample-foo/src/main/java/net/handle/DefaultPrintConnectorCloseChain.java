package net.handle;

import net.core.Connector;

/**
 * 关闭链接链式结构
 */
public class DefaultPrintConnectorCloseChain extends ConnectorCloseChain {
    @Override
    protected boolean consume(ConnectorHandler handler, Connector connector) {
        System.out.println(handler.getClientInfo() + ":Exit!!，Key:" + handler.getKey().toString());
        return false;
    }
}
