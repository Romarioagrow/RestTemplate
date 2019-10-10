package beans;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.LinkedHashMap;
import java.util.Map;

@ComponentScan
public class ProductsAlias {

    /*public Map<String, String> aliases() {
        @Value("#{${aliases}}")
        static Map<String,String> aliases;

        return new LinkedHashMap<>();
    }*/
}
