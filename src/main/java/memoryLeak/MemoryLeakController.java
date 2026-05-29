package memoryLeak;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemoryLeakController {

    private final MemoryLeakService memoryLeakService;

    // [#2] feat : 정적 변수 메모리 누수 테스트 API
    @GetMapping("/leak/static")
    public String scenario1() {
        memoryLeakService.scenario1();
        return "scenario1 - static leak";
    }
}
