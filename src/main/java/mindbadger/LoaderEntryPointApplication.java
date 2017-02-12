package mindbadger;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.persistence.config.BatchWriting;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.logging.SessionLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

@SpringBootApplication(scanBasePackages="mindbadger")
public class LoaderEntryPointApplication extends JpaBaseConfiguration {


	@Autowired
	IntegrationTest integrationTest;
	
	protected LoaderEntryPointApplication(DataSource dataSource, JpaProperties properties,
			ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider) {
		super(dataSource, properties, jtaTransactionManagerProvider);
		// TODO Auto-generated constructor stub
	}

	private static final Logger log = LoggerFactory.getLogger(LoaderEntryPointApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(LoaderEntryPointApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			if (args.length > 0) {
				String command = args[0];
				
				if ("INT_TEST".equals(command)) {
					integrationTest.runIntegrationTest();
				} else {
					System.out.println("*** SORRY, I DON'T RECOGNISE COMMAND " + command + " ***");
				}
				
			} else {
				System.out.println("*** PLEASE SPECIFY A COMMAND TO RUN ***");
			}
			
		};
	}

	@Override
	protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
		return adapter;
	}

	@Override
	protected Map<String, Object> getVendorProperties() {
		HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(PersistenceUnitProperties.BATCH_WRITING, BatchWriting.JDBC);
        map.put(PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.FINE_LABEL);
		return map;
	}
}
