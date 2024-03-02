package com.hack.journal;

import com.hack.journal.dto.AutoEmojiCountMetric;
import com.hack.journal.repository.MetricRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.List;

@EnableAsync
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Shopping API"))
@EnableMethodSecurity
@SpringBootApplication
public class JournalApplication implements CommandLineRunner {
    @Autowired
    private MetricRepository metricRepository;

    public static void main(String[] args) {
        SpringApplication.run(JournalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("hi");

        List<AutoEmojiCountMetric> metrics = metricRepository.getListOfAutoEmojiCountMetric(1, null, null);

        System.out.println(metrics);


    }
}
