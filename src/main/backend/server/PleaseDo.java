package server;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Map;

@SpringBootApplication
public class PleaseDo {
    public static void main(String[] args) {
        SpringApplication.run(PleaseDo.class, args);

        /*ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String[], String[]> carMap = mapper.readValue(new File("result.json"), new TypeReference<Map<String[], String[]>>() {
            });

            System.out.println("Car : " + carMap.get("car"));
            System.out.println("Price : " + carMap.get("price"));
            System.out.println("Model : " + carMap.get("model"));
            System.out.println("Colors : " + carMap.get("colors"));

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
