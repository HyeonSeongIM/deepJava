package com.leets.deepjava.memoryLeak;

import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemoryLeakSolutionService {

    private static final int MAX_SIZE = 100;
    private static final List<byte[]> memoryLeak = new ArrayList<>();
    private final Map<String, HeavyObject> objectHolder = new HashMap<>();

    // [#2] feat : 정적 변수 메모리 누수 해결 테스트 Service
    public void scenario1Fixed() {
        if (memoryLeak.size() >= MAX_SIZE) {
            memoryLeak.clear();
        }
        memoryLeak.add(new byte[1024]);
    }

    // [#2] feat : 컬렉션 변수 메모리 누수 해결 테스트 Service
    public void scenario2Fixed() {
        List<byte[]> requestCache = new ArrayList<>();
        requestCache.add(new byte[1024]);
    }

    // [#2] feat : 참조 객체 변수 메모리 누수 해결 테스트 Service
    public void scenario3Fixed(String id) {
        HeavyObject obj = new HeavyObject(id);
        objectHolder.put(id, obj);
        objectHolder.remove(id);
    }

    // GC 튜닝 실험용: 요청당 단기 객체 생성 후 즉시 GC 대상화
    public int gcStress() {
        List<byte[]> shortLived = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            shortLived.add(new byte[5 * 1024]); // 5KB × 200 = 1MB per request
        }
        return shortLived.stream().mapToInt(b -> b.length).sum();
    }

}
