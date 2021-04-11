package ca.sfu.orcus.gitlabanalyzer.analysis;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.*;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.member.MemberUtils;
import org.bson.types.ObjectId;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotAuthorizedException;
import java.util.*;

@Service
public class AnalysisService {
    private final AnalysisRepository analysisRepository;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public AnalysisService(AnalysisRepository analysisRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.analysisRepository = analysisRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public void analyzeProject(String jwt, int projectId) throws GitLabApiException, NullPointerException, NotAuthorizedException {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            throw new NotAuthorizedException("Current user unauthorized");
        }

        analyzeProject(gitLabApi, projectId);
    }

    private void analyzeProject(GitLabApi gitLabApi, Integer projectId) throws GitLabApiException, NullPointerException {
        Map<String, CommitterDtoDb> committerToCommitterDtoMap = new HashMap<>(); // committerEmail -> committerDto
        Map<Integer, MemberDtoDb> memberToMemberDtoMap = initializeMemberDtos(gitLabApi, projectId); // memberId -> memberDto
        Map<Integer, MergeRequestDtoDb> mrIdToMrDtoMap = new HashMap<>();

        for (MergeRequest mr : getAllMergeRequests(gitLabApi, projectId)) {
            MergeRequestDtoDb mergeRequestDto = getMergeRequestDto(gitLabApi, mr, committerToCommitterDtoMap);
            mrIdToMrDtoMap.put(mr.getIid(), mergeRequestDto);

            addMergeRequestNotesToMemberDtos(gitLabApi, memberToMemberDtoMap, mr);
        }

        addIssueNotesToMemberDtos(gitLabApi, memberToMemberDtoMap, projectId);

        Project project = gitLabApi.getProjectApi().getProject(projectId);
        ProjectDtoDb projectDto = getProjectDto(gitLabApi, project, new ArrayList<>(committerToCommitterDtoMap.values()));

        // TODO: cacheProjectDto(projectDto) (key: projectUrl + projectId)
        //          - cacheCommitterDtos() inside projectDto
        analysisRepository.cacheProjectDto(projectDto);

        analysisRepository.cacheMemberDtos(project.getWebUrl(), new ArrayList<>(memberToMemberDtoMap.values()));

        analysisRepository.cacheMergeRequestDtos(project.getWebUrl(), new ArrayList<>(mrIdToMrDtoMap.values())); //(key: projectUrl + mergeRequestId)
        /*List<Pair<Integer, ObjectId>> mrIdsToDocIds =
                analysisRepository.cacheMergeRequestsDtos(project.getWebUrl(), new ArrayList<>(mrIdToMrDtoMap.values()));
        addMergeRequestDocumentIdsToMemberDtos(memberToMemberDtoMap, mrIdToMrDtoMap, mrIdsToDocIds);
    }

    private Map<Integer, MemberDtoDb> initializeMemberDtos(GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        Map<Integer, MemberDtoDb> memberDtos = new HashMap<>();

        List<Member> members = gitLabApi.getProjectApi().getAllMembers(projectId);
        for (Member m : members) {
            memberDtos.put(m.getId(), new MemberDtoDb(m));
        }

        return memberDtos;
    }

    private List<MergeRequest> getAllMergeRequests(GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        return gitLabApi.getMergeRequestApi().getMergeRequests(projectId, Constants.MergeRequestState.MERGED);
    }

    private MergeRequestDtoDb getMergeRequestDto(GitLabApi gitLabApi,
                                                 MergeRequest mergeRequest,
                                                 Map<String, CommitterDtoDb> committerToCommitterDtoMap) throws GitLabApiException {
        List<CommitDtoDb> commitDtos = new ArrayList<>();
        Set<String> committers = new HashSet<>();

        double sumOfCommitsScore = 0;

        Integer projectId = mergeRequest.getProjectId();
        Integer mergeRequestId = mergeRequest.getIid();

        for (Commit c : getMergeRequestCommits(gitLabApi, mergeRequest)) {
            Commit detailedCommit = getDetailedCommit(gitLabApi, projectId, c);

            committers.add(detailedCommit.getAuthorEmail());
            addCommitIdAndMrIdToCommitterDto(committerToCommitterDtoMap, detailedCommit, mergeRequestId);

            CommitDtoDb commitDto = getCommitDto(gitLabApi, projectId, detailedCommit);
            sumOfCommitsScore += commitDto.getScore();

            commitDtos.add(commitDto);
        }

        MergeRequest mrChanges = gitLabApi.getMergeRequestApi().getMergeRequestChanges(projectId, mergeRequestId);
        return new MergeRequestDtoDb(mergeRequest, commitDtos, committers, mrChanges, sumOfCommitsScore);
    }

    private List<Commit> getMergeRequestCommits(GitLabApi gitLabApi, MergeRequest mergeRequest)
            throws GitLabApiException {
        return gitLabApi.getMergeRequestApi().getCommits(mergeRequest.getProjectId(), mergeRequest.getIid());
    }

    private Commit getDetailedCommit(GitLabApi gitLabApi, Integer projectId, Commit c)
            throws GitLabApiException {
        return gitLabApi.getCommitsApi().getCommit(projectId, c.getShortId());
    }

    private void addCommitIdAndMrIdToCommitterDto(Map<String, CommitterDtoDb> committerDtos,
                                                  Commit commit,
                                                  Integer mergeRequestId) {
        String authorEmail = commit.getAuthorEmail();
        String authorName = commit.getAuthorName();

        if (committerDtos.containsKey(authorEmail)) {
            CommitterDtoDb committerDto = committerDtos.get(authorEmail);
            committerDto.addCommitId(commit.getId());
            committerDto.addMergeRequestId(mergeRequestId);
        } else {
            committerDtos.put(
                    authorEmail,
                    new CommitterDtoDb(
                            authorEmail,
                            authorName,
                            Collections.singleton(commit.getId()),
                            Collections.singleton(mergeRequestId)));
        }
    }

    private CommitDtoDb getCommitDto(GitLabApi gitLabApi, Integer projectId, Commit detailedCommit)
            throws GitLabApiException {
        List<Diff> diffList = gitLabApi.getCommitsApi().getDiff(projectId, detailedCommit.getId());
        return new CommitDtoDb(detailedCommit, diffList);
    }

    private void addMergeRequestNotesToMemberDtos(GitLabApi gitLabApi,
                                                  Map<Integer, MemberDtoDb> memberDtos,
                                                  MergeRequest mr) throws GitLabApiException {
        List<Note> mergeRequestNotes = gitLabApi.getNotesApi().getMergeRequestNotes(mr.getProjectId(), mr.getIid());
        addNotesToMemberDtos(memberDtos, mergeRequestNotes, mr.getWebUrl(), mr.getAuthor());
    }

    private void addNotesToMemberDtos(Map<Integer, MemberDtoDb> memberDtos, List<Note> notes, String webUrl, Author parentAuthor) {
        for (Note n : notes) {
            if (!n.getSystem()) {
                Integer authorId = n.getAuthor().getId();
                NoteDtoDb noteDto = new NoteDtoDb(n, webUrl,
                        (parentAuthor.getId().equals(authorId) ? "Self" : parentAuthor.getName()));
                memberDtos.get(authorId).addNote(noteDto);
            }
        }
    }

    private void addIssueNotesToMemberDtos(GitLabApi gitLabApi,
                                           Map<Integer, MemberDtoDb> memberDtos,
                                           Integer projectId) throws GitLabApiException {
        List<Issue> issues = gitLabApi.getIssuesApi().getIssues(projectId);
        for (Issue i : issues) {
            List<Note> issueNotes = gitLabApi.getNotesApi().getIssueNotes(projectId, i.getIid());
            addNotesToMemberDtos(memberDtos, issueNotes, i.getWebUrl(), i.getAuthor());
        }
    }

    private ProjectDtoDb getProjectDto(GitLabApi gitLabApi, Project project, List<CommitterDtoDb> committers)
            throws GitLabApiException {
        String role = getAuthenticatedMembersRoleInProject(gitLabApi, project.getId());
        return new ProjectDtoDb(project, role, new Date().getTime(), committers);
    }

    private String getAuthenticatedMembersRoleInProject(GitLabApi gitLabApi, int projectId) throws GitLabApiException {
        int currentUserId = gitLabApi.getUserApi().getCurrentUser().getId();
        Member currentMember = gitLabApi.getProjectApi().getMember(projectId, currentUserId);
        int currentAccessLevel = currentMember.getAccessLevel().value;
        return MemberUtils.getMemberRoleFromAccessLevel(currentAccessLevel);
    }

    private void addMergeRequestDocumentIdsToMemberDtos(Map<Integer, MemberDtoDb> memberToMemberDtoMap,
                                                        Map<Integer, MergeRequestDtoDb> mrIdToMrDtoMap,
                                                        List<Pair<Integer, ObjectId>> mrIdsToDocIds) {
        for (Pair<Integer, ObjectId> mrIdToDocId : mrIdsToDocIds) {
            Integer mrId = mrIdToDocId.getFirst();
            Integer memberId = mrIdToMrDtoMap.get(mrId).getAuthorId();
            memberToMemberDtoMap.get(memberId).addMergeRequestDocId(mrIdToDocId.getSecond());
        }
    }
}