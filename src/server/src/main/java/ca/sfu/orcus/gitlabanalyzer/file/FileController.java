package ca.sfu.orcus.gitlabanalyzer.file;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static javax.servlet.http.HttpServletResponse.*;

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
                                        @PathVariable("commitid") String commitId,
                                        @PathVariable String filepath,
                                        @PathVariable double score) {
        FileDto file = fileService.changeCommitFileScore(jwt, projectId, commitId, filepath, score);
        response.setStatus(file == null ? SC_UNAUTHORIZED : SC_OK);
        Gson gson = new Gson();
        return gson.toJson(file);
    }

    @PostMapping("/api/project/{projectId}/file/{commitid}/{filepath}/{score}/true")
    public String changeFileIgnoreTrue(@CookieValue(value = "sessionId") String jwt,
                                        HttpServletResponse response,
                                        @PathVariable int projectId,
                                        @PathVariable("commitid") String commitId,
                                        @PathVariable String filepath,
                                        @PathVariable double score) {
        FileDto file = fileService.changeFileIgnoreTrue(jwt, projectId, commitId, filepath, score);
        response.setStatus(file == null ? SC_UNAUTHORIZED : SC_OK);
        Gson gson = new Gson();
        return gson.toJson(file);
    }

    @PostMapping("/api/project/{projectId}/file/{commitid}/{filepath}/{score}/false")
    public String changeFileIgnoreFalse(@CookieValue(value = "sessionId") String jwt,
                                       HttpServletResponse response,
                                       @PathVariable int projectId,
                                       @PathVariable("commitid") String commitId,
                                       @PathVariable String filepath,
                                       @PathVariable double score) {
        FileDto file = fileService.changeFileIgnoreFalse(jwt, projectId, commitId, filepath, score);
        response.setStatus(file == null ? SC_UNAUTHORIZED : SC_OK);
        Gson gson = new Gson();
        return gson.toJson(file);
    }
}
