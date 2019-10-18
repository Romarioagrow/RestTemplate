package server.config;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashMap;

@Component
public class AliasConfig {
    @Bean
    public LinkedHashMap<String, String> aliasesMap() throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("D:\\Projects\\Rest\\src\\main\\resources\\aliases.json"));
        return gson.fromJson(reader, LinkedHashMap.class);
    }
}
