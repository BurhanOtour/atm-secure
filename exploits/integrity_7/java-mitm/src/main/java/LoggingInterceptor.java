import okio.Buffer;
import okio.BufferedSink;

import java.io.IOException;

public class LoggingInterceptor implements Interceptor {

    private final ThreadLocal<Buffer> bufferThreadLocal = ThreadLocal.withInitial(Buffer::new);

    @Override public boolean serverToClient(byte[] bytes, BufferedSink sink) throws IOException {
        Buffer buffer = bufferThreadLocal.get();
        buffer.clear();
        buffer.write(bytes);
        String md5 = buffer.md5().hex();

        System.err.println("Sending " + bytes.length + " bytes to client. (MD5=" + md5 + ")");
        sink.write(bytes);
        sink.flush();
        return true;
    }

    @Override public boolean clientToServer(byte[] bytes, BufferedSink sink) throws IOException {
        Buffer buffer = bufferThreadLocal.get();
        buffer.clear();
        buffer.write(bytes);
        String md5 = buffer.md5().hex();

        System.err.println("Sending " + bytes.length + " bytes to server. (MD5=" + md5 + ")");
        sink.write(bytes);
        sink.flush();
        return true;
    }

    @Override public boolean shouldAcceptNextSocket() {
        return true;
    }
}
