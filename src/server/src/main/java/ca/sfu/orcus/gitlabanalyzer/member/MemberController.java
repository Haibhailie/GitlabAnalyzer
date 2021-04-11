package ca.sfu.orcus.gitlabanalyzer.member;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.MemberDtoDb;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MemberController {
    private final MemberServiceCached memberService;
    private static final Gson gson = new Gson();

    @Autowired
    public MemberController(MemberServiceCached memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/project/{projectId}/members")
    public String getMembers(@CookieValue(value = "sessionId") String jwt,
                             HttpServletResponse response,
                             @PathVariable int projectId) {
        List<MemberDtoDb> members = memberService.getAllMembers(jwt, projectId);
        response.setStatus(getResponseCode(members));
        return gson.toJson(members);
    }

    private int getResponseCode(List<MemberDtoDb> list) {
        if (list == null) {
            return SC_UNAUTHORIZED;
        } else if (list.isEmpty()) {
            return SC_INTERNAL_SERVER_ERROR;
        } else {
            return SC_OK;
        }
    }
}