import okio.BufferedSink;

import java.io.IOException;

public class SlowInterceptor implements Interceptor {
    @Override public boolean serverToClient(byte[] bytes, BufferedSink sink) throws IOException {
        return true;
    }

    @Override public boolean clientToServer(byte[] bytes, BufferedSink sink) throws IOException {
        return true;
    }
}
