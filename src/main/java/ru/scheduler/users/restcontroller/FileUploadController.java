package ru.scheduler.users.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.scheduler.users.model.entity.User;
import ru.scheduler.users.service.JwtService;
import ru.scheduler.users.service.StorageService;
import ru.scheduler.users.service.UserService;

import java.io.IOException;
import java.util.Calendar;

@RestController
@RequestMapping(value = "/images")
public class FileUploadController {

    private final String IMAGE_PATH = "no-avatar.png";

    private final StorageService storageService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Resource> serveStdFile() {
        Resource file = storageService.loadAsResource(IMAGE_PATH);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @RequestMapping(value = "/",method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) throws IOException {
        Long time = Calendar.getInstance().getTimeInMillis();
        User user = jwtService.getUser(token);
        if(user == null){
            return IMAGE_PATH;
        }
        String extension = file.getOriginalFilename().split("\\.")[1];
        String fileName = time.toString() + "." + extension;
        if(!user.getImagePath().equals(IMAGE_PATH)){
            storageService.delete(user.getImagePath());
        }
        user.setImagePath(fileName);
        userService.save(user);
        storageService.store(file, fileName);
        return fileName;
    }

}
