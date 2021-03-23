package ca.sfu.orcus.gitlabanalyzer.file;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/api/project/{projectId}/file/{commitid}/{filepath}/{score}")
    public String changeCommitFileScore(@CookieValue(value = "sessionId") String jwt,
                                        HttpServletResponse response,
                                        @PathVariable int projectId,
                                        @PathVariable String commitid,
                                        @PathVariable String filepath,
                                        @PathVariable double score) {
        FileDto file = fileService.changeCommitFileScore(jwt, projectId, commitid, filepath, score);
        response.setStatus(file == null ? 401 : 200);
        Gson gson = new Gson();
        return gson.toJson(file);
    }
}
