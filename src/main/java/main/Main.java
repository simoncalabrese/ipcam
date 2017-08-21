package main;

import app.Watch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

/**
 * Created by simon.calabrese on 21/08/2017.
 */
@SpringBootApplication
@ComponentScan
public class Main {
    public static void main(String[] args) {
        try {
            new Watch().process();
        } catch (IOException e) {
            return;
        }
    }
}
