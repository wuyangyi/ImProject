package net.utils;

public class DataUtil {
    public static String getMaxUserId(String userId, String peerId) {
        if (userId == null) {
            return peerId;
        } else if (peerId == null) {
            return userId;
        }
        return userId.hashCode() > peerId.hashCode() ? userId : peerId;
    }

    public static String getMinUserId(String userId, String peerId) {
        if (userId == null) {
            return peerId;
        } else if (peerId == null) {
            return userId;
        }
        return userId.hashCode() < peerId.hashCode() ? userId : peerId;
    }
}
