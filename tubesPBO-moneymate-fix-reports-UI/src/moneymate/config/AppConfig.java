package moneymate.config;

import moneymate.controller.TransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration Class
 *
 * OOP Concept: Singleton Pattern via Spring Bean
 */
@Configuration
public class AppConfig {

    /**
     * Create a single shared TransactionManager instance
     * Spring ensures this is a Singleton across the application
     */
    @Bean
    public TransactionManager transactionManager() {
        return new TransactionManager();
    }
}
