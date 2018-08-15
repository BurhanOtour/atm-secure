import okio.BufferedSink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordingInterceptor extends LoggingInterceptor {

    private final List<byte[]> serverToClientBytes = Collections.synchronizedList(new ArrayList<>());
    private final List<byte[]> clientToServerBytes = Collections.synchronizedList(new ArrayList<>());

    @Override public boolean serverToClient(byte[] bytes, BufferedSink sink) throws IOException {
        serverToClientBytes.add(bytes.clone());
        return super.serverToClient(bytes, sink);
    }

    @Override public boolean clientToServer(byte[] bytes, BufferedSink sink) throws IOException {
        clientToServerBytes.add(bytes.clone());
        return super.clientToServer(bytes, sink);
    }

    public List<byte[]> getServerToClientBytes() {
        return Collections.unmodifiableList(serverToClientBytes);
    }

    public List<byte[]> getClientToServerBytes() {
        return Collections.unmodifiableList(clientToServerBytes);
    }
}
