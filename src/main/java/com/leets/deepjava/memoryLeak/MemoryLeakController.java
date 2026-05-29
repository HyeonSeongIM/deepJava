package com.leets.deepjava.memoryLeak;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/fix/static")
    public String scenario1Fixed() {
        memoryLeakSolutionService.scenario1Fixed();
        return "scenario1 - fixed";
    }

    @GetMapping("/fix/collection")
    public String scenario2Fixed() {
        memoryLeakSolutionService.scenario2Fixed();
        return "scenario2 - fixed";
    }

    @GetMapping("/fix/reference")
    public String scenario3Fixed() {
        String id = UUID.randomUUID().toString();
        memoryLeakSolutionService.scenario3Fixed(id);
        return "scenario3 - fixed";
    }
}
