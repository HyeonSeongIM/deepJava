package com.leets.deepjava.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Map;

@Slf4j
@Component
public class HeapMemoryMonitor {

    @Value("${alert.slack.webhook-url:}")
    private String slackWebhookUrl;

    @Value("${alert.heap.threshold:80}")
    private int thresholdPercent;

    private final RestTemplate restTemplate = new RestTemplate();

    // 연속 알람 방지: 임계값 초과 시 true, 회복 시 false
    private boolean alertSent = false;

    @Scheduled(fixedDelayString = "${alert.heap.check-interval-ms:10000}")
    public void checkHeap() {
        if (slackWebhookUrl.isBlank()) {
            return;
        }

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        long usedMb  = heap.getUsed()  / 1024 / 1024;
        long maxMb   = heap.getMax()   / 1024 / 1024;
        int  percent = (int) (heap.getUsed() * 100 / heap.getMax());

        if (percent >= thresholdPercent && !alertSent) {
            sendSlack(String.format(
                ":rotating_light: *힙 메모리 경고* — `%s`\n" +
                "> 사용률: *%d%%* (%dMB / %dMB)\n" +
                "> 임계값: %d%%  →  OOM 위험!",
                appInstanceId(), percent, usedMb, maxMb, thresholdPercent
            ));
            alertSent = true;
            log.warn("[HeapAlert] 힙 {}% 초과 — Slack 전송", percent);
        }

        // 임계값 - 10% 아래로 내려오면 회복 알람 + 재활성화
        if (percent < thresholdPercent - 10 && alertSent) {
            sendSlack(String.format(
                ":white_check_mark: *힙 메모리 회복* — `%s`\n" +
                "> 사용률: *%d%%* (%dMB / %dMB)",
                appInstanceId(), percent, usedMb, maxMb
            ));
            alertSent = false;
            log.info("[HeapAlert] 힙 {}%로 회복", percent);
        }
    }

    private void sendSlack(String text) {
        try {
            restTemplate.postForEntity(slackWebhookUrl, Map.of("text", text), String.class);
        } catch (Exception e) {
            log.error("[HeapAlert] Slack 전송 실패: {}", e.getMessage());
        }
    }

    private String appInstanceId() {
        String id = System.getenv("APP_INSTANCE_ID");
        return id != null ? id : "unknown";
    }
}
