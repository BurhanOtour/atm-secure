public class InterceptionResult {
    private final boolean keepSocketOpen;
    private final byte[] newBytes;

    public InterceptionResult(boolean keepSocketOpen, byte[] newBytes) {
        this.keepSocketOpen = keepSocketOpen;
        this.newBytes = newBytes;
    }

    public boolean keepSocketOpen() {
        return keepSocketOpen;
    }

    public byte[] getNewBytes() {
        return newBytes;
    }
}
