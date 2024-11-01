package be.enkidu.vinyles;

import be.enkidu.vinyles.base.EnkiduVinylesServiceApp;
import be.enkidu.vinyles.base.config.AsyncSyncConfiguration;
import be.enkidu.vinyles.base.config.EmbeddedSQL;
import be.enkidu.vinyles.base.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { EnkiduVinylesServiceApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedSQL
public @interface IntegrationTest {
}
