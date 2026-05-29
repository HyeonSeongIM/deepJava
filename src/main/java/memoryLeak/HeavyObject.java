package memoryLeak;

import lombok.Getter;

@Getter
public class HeavyObject {
    private final byte[] data;
    private final String id;

    public HeavyObject(String id) {
        this.id = id;
        this.data = new byte[1024 * 1024]; // 1MB
    }
}