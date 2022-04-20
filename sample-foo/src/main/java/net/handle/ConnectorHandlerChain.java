package net.handle;

/**
 * 责任链默认结构封装
 * @param <Model>
 */
public abstract class ConnectorHandlerChain<Model> {
    // 当前解点所持有的小一份节点
    private volatile ConnectorHandlerChain<Model> next;
    private boolean isGroup; // 群责任链可以无限添加
    protected String groupId; //群id

    /**
     * 添加一个新的解点到当前链式结构的末尾
     * @param newChain 新的节点
     * @return 返回新的节点
     */
    public ConnectorHandlerChain<Model> appendLast(ConnectorHandlerChain<Model> newChain) {
        if (newChain == this || (this.getClass().equals(newChain.getClass()) && !newChain.isGroup)) {
            return this;
        }

        synchronized (this) {
            if (next == null) {
                next = newChain;
                return newChain;
            }
        }
        return next.appendLast(newChain);
    }

    /**
     * PS1: 移除节点中的某一个节点及其之后节点
     * PS2: 移除某节点时，如果其具有后续的节点，则把后续节点接到当前节点上；实现可以移除中间某个节点
     * @param clx 待移除节点的Class信息
     * @return 是否移除成功
     */
    public synchronized boolean remove(Class<? extends ConnectorHandlerChain<Model>> clx, String groupId) {
        // 自己不能移除自己，因为自己未持有上一个链接的引用
        if (this.getClass().equals(clx)) {
            return false;
        }

        boolean isGroup = groupId != null && !groupId.isEmpty();

        synchronized (this) {
            if (next == null) {
                // 当前无下一个节点存在，无法判断
                return false;
            } else if (next.getClass().equals(clx) && (!isGroup || (next.isGroup && next.groupId.equalsIgnoreCase(groupId)))) {
                // 移除next节点
                next = next.next;
                return true;
            } else {
                // 交给next进行移除操作
                return next.remove(clx, groupId);
            }
        }
    }

    synchronized boolean handle(ConnectorHandler handler, Model model) {
        ConnectorHandlerChain<Model> next = this.next;

        // 自己消费
        if (consume(handler, model)) {
            return true;
        }

        boolean consumed = next != null && next.handle(handler, model);

        if (consumed) {
            return true;
        }
        return consumeAgain(handler, model);
    }

    protected abstract boolean consume(ConnectorHandler handler, Model model);

    protected boolean consumeAgain(ConnectorHandler handler, Model model) {
        return false;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
}
