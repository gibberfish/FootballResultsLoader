package mindbadger.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan ("mindbadger")
@EnableAutoConfiguration
public class FootballLoaderConfiguration {
	public FootballLoaderConfiguration () {
	}
}
