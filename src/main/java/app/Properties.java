package app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by simon.calabrese on 21/08/2017.
 */
@Component(value = "Properties")
@ConfigurationProperties
public class Properties extends AbstractProperties{
    @Value("${files.srcPath}")
    private String src;

    @Value("${files.destPath}")
    private String dest;

    public String getSrc() {
        return src;
    }

    public String getDest() {
        return dest;
    }
}
