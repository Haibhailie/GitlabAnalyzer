package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitDto;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
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
        return null;
    }

    @PostMapping("/api/project/{projectId}/file/{mergerequestid}/{filepath}/{score}")
    public String changeMRFileScore(@CookieValue(value = "sessionId") String jwt,
                                    HttpServletResponse response,
                                    @PathVariable int projectId,
                                    @PathVariable String mergerequestid,
                                    @PathVariable String filepath,
                                    @PathVariable double score) {
        return null;
    }
}
