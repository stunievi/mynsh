package bin.leblanc.message.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@Slf4j
public class MessageConfig implements CommandLineRunner{
    @Override
    public void run(String... strings) throws Exception {
    }
}
