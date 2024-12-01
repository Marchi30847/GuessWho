package data;

public interface ClientCallBack {
    void onInvalidHostName();
    void onInvalidHostPort();
    void onMessageReceived(StringBuilder command, StringBuilder sender, StringBuilder message);
}
