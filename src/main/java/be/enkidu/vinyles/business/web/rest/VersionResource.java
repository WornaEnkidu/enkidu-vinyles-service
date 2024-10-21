package be.enkidu.vinyles.business.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pub-api/version")
public class VersionResource {

    private static final Logger LOG = LoggerFactory.getLogger(VersionResource.class);

    @Value("${application.version}")
    private String version;

    @GetMapping
    public ResponseEntity<String> getVersion() {
        LOG.info("get version");
        return new ResponseEntity<>(version, HttpStatus.OK);
    }
}
