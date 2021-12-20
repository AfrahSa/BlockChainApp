package blockchain;

import ui.App;
import actor.Manager;
import java.security.MessageDigest;


public class Block {
    private String data;
    private String hash;
    private String previousHash;
    private long timeStamp;
    private long index;

    public Block(String data, String previousHash, long index) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis() - Manager.startTime;
        this.index = index;

        this.hash = calculateBlockHash();
    }

    public String calculateBlockHash() {
        String dataToHash = previousHash
            + Long.toString(timeStamp)
            + Long.toString(index)
            + data;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
         return this.hash.substring(0, 4);
      //  return this.data;
    }

    public String getData() {
        return this.data;
    }

    public String getHash() {
        return this.hash;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public long getIndex() {
        return this.index;
    }
}
