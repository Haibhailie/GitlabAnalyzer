package ca.sfu.orcus.gitlabanalyzer.note;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NoteController {
    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/api/project/{projectId}/members/{memberId}/notes")
    public String getNotesByMemberId(@CookieValue(value = "sessionId") String jwt,
                                         HttpServletResponse response,
                                         @PathVariable int projectId,
                                         @PathVariable int memberId) {
        List<NoteDto> allNotesByMemberId = noteService.getNoteDtosByMemberId(jwt, projectId, memberId);
        response.setStatus(allNotesByMemberId == null ? SC_UNAUTHORIZED : SC_OK);
        Gson gson = new Gson();
        return gson.toJson(allNotesByMemberId);
    }

}
