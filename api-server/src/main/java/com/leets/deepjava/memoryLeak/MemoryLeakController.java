package com.leets.deepjava.memoryLeak;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemoryLeakController {

    private final MemoryLeakService memoryLeakService;
    private final MemoryLeakSolutionService memoryLeakSolutionService;

    // [#2] feat : 정적 변수 메모리 누수 테스트 API
    @GetMapping("/leak/static")
    public String scenario1() {
        memoryLeakService.scenario1();
        return "scenario1 - static leak";
    }

    // [#2] feat : 컬렉션 변수 메모리 누수 테스트 API
    @GetMapping("/leak/collection")
    public String scenario2() {
        memoryLeakService.scenario2();
        return "scenario2 - collection leak";
    }

    // [#2] feat : 다른 객체 참고 누수 테스트 API
    @GetMapping("/leak/reference")
    public String scenario3() {
        String id = UUID.randomUUID().toString();
        memoryLeakService.scenario3(id);
        return "scenario3 - reference leak";
    }

    // [#2] feat : 정적 변수 메모리 누수 해결 테스트 API
    @GetMapping("/fix/static")
    public String scenario1Fixed() {
        memoryLeakSolutionService.scenario1Fixed();
        return "scenario1 - fixed";
    }

    // [#2] feat : 컬렉션 변수 메모리 누수 해결 테스트 API
    @GetMapping("/fix/collection")
    public String scenario2Fixed() {
        memoryLeakSolutionService.scenario2Fixed();
        return "scenario2 - fixed";
    }

    // [#2] feat : 참조 객체 변수 메모리 누수 해결 테스트 API
    @GetMapping("/fix/reference")
    public String scenario3Fixed() {
        String id = UUID.randomUUID().toString();
        memoryLeakSolutionService.scenario3Fixed(id);
        return "scenario3 - fixed";
    }

    // GC 튜닝 실험용 API
    @GetMapping("/gc-stress")
    public String gcStress() {
        memoryLeakSolutionService.gcStress();
        return "gc-stress ok";
    }

    // [#4] feat : OOM 발생 API
    @GetMapping("/oom")
    public String triggerOOM() {
        memoryLeakService.triggerOOM();
        return "oom triggered";
    }

    // Least Connection 실습용: 지정된 ms 동안 연결을 점유
    @GetMapping("/slow")
    public String slow(@RequestParam(defaultValue = "5000") long ms) throws InterruptedException {
        Thread.sleep(ms);
        return "slow ok (" + ms + "ms)";
    }
}
