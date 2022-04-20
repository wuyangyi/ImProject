package net.bean.db;

public class UpdateTagBean {

    private long id;
    private String tag_id;
    private int tag_type;
    private String receiver_id;
    private String attach;
    private long create_time;
    private long update_time;

    public UpdateTagBean(){}

    public UpdateTagBean(String tag_id, int tag_type, String receiver_id, String attach) {
        this.tag_id = tag_id;
        this.tag_type = tag_type;
        this.receiver_id = receiver_id;
        this.attach = attach;
        this.create_time = System.currentTimeMillis() / 1000;
        this.update_time = System.currentTimeMillis() / 1000;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public int getTag_type() {
        return tag_type;
    }

    public void setTag_type(int tag_type) {
        this.tag_type = tag_type;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}
