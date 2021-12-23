package blockchain;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.util.LinkedList;

public class Ledger {
    private LinkedList<Block> ledger;

    public Ledger() {
        this.ledger = new LinkedList<Block>();
    }

    public Ledger(LinkedList<Block> ledger) {
        this.ledger = ledger;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer("");
        for (Block b : ledger) {
            str.append("\t - " + b.getHash() + " :\n"
                     + "\t\t" + b.getData() + "\n");
        }
        return str.toString();
    }

    public boolean addBlockToLedger(Block block) {
        if (getLastBlockHash().equals(block.getPreviousHash())
                && block.getHash().equals(block.calculateBlockHash())) {
            ledger.add(block);
            return true;
        }
        return false;
    }

    public LinkedList<Block> getLedger() {
        return this.ledger;
    }

    public int getSize() {
        return ledger.size();
    }

    public String getLastBlockHash() {
        return ledger.size() == 0 ? "" : ledger.getLast().getHash();
    }

    public Block getLastBlock() {
        return ledger.size() == 0 ? null : ledger.getLast();
    }
    public String toJson() {
        StringBuffer str = new StringBuffer("[\n");
        for (Block b : ledger) {
            JSONArray a = (JSONArray) JSONValue.parse(b.getData());
            str.append("{ \n\t \"Hash\": " + b.getHash() + " \n"
                     + "\t \"Previous Hash\": " + b.getPreviousHash() + "\n"
                     + "\t \"Time\": " + b.getTimeStamp() + "\n"
                     + "\t \"Data\": " + b.getData() + "\n},\n");
        }
        str.deleteCharAt( str.length() - 1 );
        str.deleteCharAt( str.length() - 1 );
        str.append("\n]");
        return str.toString();
    }
}
