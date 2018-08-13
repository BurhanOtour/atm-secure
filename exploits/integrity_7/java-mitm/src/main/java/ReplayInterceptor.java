import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.function.Supplier;

public class ReplayInterceptor extends RecordingInterceptor {
    private final Supplier<Socket> socketSupplier;
    private final List<Integer> replayMessages;
    private final int acceptMessageCount;
    private final int replayAfterMessageCount;
    private int accepted = 0;

    public ReplayInterceptor(Supplier<Socket> socketSupplier, List<Integer> replayMessages, int acceptMessageCount, int replayAfterMessageCount) {
        this.socketSupplier = socketSupplier;
        this.replayMessages = replayMessages;
        this.acceptMessageCount = acceptMessageCount;
        this.replayAfterMessageCount = replayAfterMessageCount;
    }

    @Override public void onAccepted() {
        accepted++;
    }

    @Override public boolean shouldAcceptNextSocket() {
        return accepted < acceptMessageCount;
    }

    @Override public void onProxyClosed() {

    }

    @Override public void onBeforeNextAccept() {
        if (accepted != replayAfterMessageCount) {
            return;
        }

        try {
            for (int replayMessage : replayMessages) {
                System.err.println("Replaying message " + replayMessage);

                Socket forwardSocket = socketSupplier.get();
                try (
                    BufferedSource forwardIn = Okio.buffer(Okio.source(forwardSocket));
                    BufferedSink forwardOut = Okio.buffer(Okio.sink(forwardSocket))) {
                    forwardOut.write(getClientToServerBytes().get(replayMessage));
                    forwardOut.flush();
                    forwardIn.readByteArray();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
