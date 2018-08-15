import okio.BufferedSink;

import java.io.IOException;

public interface Interceptor {

    /**
     * @return Keep socket open?
     */
    default boolean serverToClient(byte[] bytes, BufferedSink sink) throws IOException {
        sink.write(bytes);
        sink.flush();
        return true;
    }

    /**
     * @return Keep socket open?
     */
    default boolean clientToServer(byte[] bytes, BufferedSink sink) throws IOException {
        sink.write(bytes);
        sink.flush();
        return true;
    }

    default boolean shouldAcceptNextSocket() {
        return true;
    }

    default void onAccepted() {
    }

    default void onSocketClosed() {
    }

    default void onProxyClosed() {
    }

    default void onBeforeNextAccept() {
    }
}

