package app;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by simon.calabrese on 21/08/2017.
 */
public class Watch {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private static final Map<String,String> properties = new HashMap<>();
    static {
        Properties prop = new Properties();
        try {
            prop.load(Watch.class.getClassLoader().getResourceAsStream("ipcam.properties"));
        } catch (IOException e) {
            //
        }
        properties.putAll(prop.stringPropertyNames()
                .stream()
                .collect(Collectors.toMap(e -> e, prop::getProperty)));
    }

    public Watch() throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        walkAndRegisterDirectories(Paths.get(properties.get("srcPath")));
    }

    private void walkAndRegisterDirectories(final Path path) throws IOException {
        Files.walkFileTree(path,new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                keys.put(key, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void process() throws IOException {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                return;
            }
            Path dir = keys.get(key);
            for(WatchEvent event : key.pollEvents()) {
                Path name = ((WatchEvent<Path>) event).context();
                try {
                    /*if(!Files.exists(name)) {
                        Files.createDirectory(Paths.get("C:\\Users\\simon.calabrese\\Desktop\\conte1"));
                    }*/
                    Files.copy(name,Paths.get(properties.get("destPath")));
                } catch (IOException e) {
                    break;
                }
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }
}
