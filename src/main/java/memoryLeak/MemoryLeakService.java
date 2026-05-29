package memoryLeak;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryLeakService {

    private static final List<byte[]> memoryLeak = new ArrayList<>();

    // [#2] feat : 정적 변수 메모리 누수 테스트 Service
    public void scenario1() {
        memoryLeak.add(new byte[1024 * 1024]);
    }
}
