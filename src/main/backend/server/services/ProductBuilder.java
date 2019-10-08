package server.services;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Log
@Service
public class ProductBuilder {
    public void parseDBFile(MultipartFile excelFile) {
        log.info(excelFile.getOriginalFilename());


    }
}
