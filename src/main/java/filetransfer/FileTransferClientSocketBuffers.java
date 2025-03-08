package filetransfer;

public enum FileTransferClientSocketBuffers {
    MEDIUM(4 * 1024);

    private final int size;

    FileTransferClientSocketBuffers(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
