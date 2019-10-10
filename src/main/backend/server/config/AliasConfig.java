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
        JsonReader reader = new JsonReader(new FileReader("D:\\Projects\\Rest\\src\\main\\resources\\test.json"));
        return gson.fromJson(reader, LinkedHashMap.class);
    }

    /*!!! ЧЕРЕЗ SPRING CONTEXT*/
    /*
     * BEAN Matcher
     * COLLECTION Map<String, String[]> aliases
     * ЗНАЧЕНИЯ ДЛЯ КАЖДОЙ ENTRY В PROPERTIES
     * 1элемент - Синоним, 2.group, 3.coeff, 4.singleName, 5.category
     * В итерации для каждого originalProduct из таблицы Поставщиков
     * for entry : aliases {
     *   if(entry.key.startWith(originalProduct.getType)) {
     *           match new Product()
     *       }
     * }*/

    /*@Bean
    public Map<String, String> aliases() {
        @Value("#{${aliases}}")
        public Map<String,String> aliases;

        return new LinkedHashMap<>();
    }*/
}
