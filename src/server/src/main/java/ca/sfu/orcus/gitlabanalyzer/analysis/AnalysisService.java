package ca.sfu.orcus.gitlabanalyzer.analysis;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.*;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.commit.CommitScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigDto;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberUtils;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestScoreCalculator;
import org.bson.types.ObjectId;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.util.*;

@Service
public class AnalysisService {
    private final AnalysisRepository analysisRepository;
    private final GitLabApiWrapper gitLabApiWrapper;
    private final ConfigService configService;

    @Autowired
    public AnalysisService(AnalysisRepository analysisRepository, GitLabApiWrapper gitLabApiWrapper, ConfigService configService) {
        this.analysisRepository = analysisRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
        this.configService = configService;
    }

    public void analyzeProject(String jwt, int projectId) throws GitLabApiException, NullPointerException, NotAuthorizedException {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            throw new NotAuthorizedException("Current user unauthorized");
        }

        analyzeProject(jwt, gitLabApi, projectId);
    }

    private void analyzeProject(String jwt, GitLabApi gitLabApi, Integer projectId) throws GitLabApiException, NullPointerException {
        Map<String, CommitterDtoDb> committerToCommitterDtoMap = new HashMap<>(); // committerEmail -> committerDto
        Map<Integer, MemberDtoDb> memberToMemberDtoMap = initializeMemberDtos(gitLabApi, projectId); // memberId -> memberDto
        Map<Integer, MergeRequestDtoDb> mrIdToMrDtoMap = new HashMap<>();

        ConfigDto currentConfig = configService.getCurrentConfig(jwt)
                .orElseThrow(() -> new NotFoundException("Current config not found"));

        for (MergeRequest mr : getAllMergeRequests(gitLabApi, projectId)) {
            MergeRequestDtoDb mergeRequestDto = generateMergeRequestDto(gitLabApi, mr, committerToCommitterDtoMap, currentConfig);
            mrIdToMrDtoMap.put(mr.getIid(), mergeRequestDto);

            addMergeRequestNotesToMemberDtos(gitLabApi, memberToMemberDtoMap, mr);
        }

        addIssueNotesToMemberDtos(gitLabApi, memberToMemberDtoMap, projectId);

        ProjectDtoDb projectDto = generateProjectDto(gitLabApi, projectId, new ArrayList<>(committerToCommitterDtoMap.values()));

        cache(projectDto, mrIdToMrDtoMap, memberToMemberDtoMap);
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

    private MergeRequestDtoDb generateMergeRequestDto(GitLabApi gitLabApi,
                                                      MergeRequest mergeRequest,
                                                      Map<String, CommitterDtoDb> committerToCommitterDtoMap,
                                                      ConfigDto currentConfig)
            throws GitLabApiException {
        List<CommitDtoDb> commitDtos = new ArrayList<>();
        Set<String> committers = new HashSet<>();

        double sumOfCommitsScore = 0;

        Integer projectId = mergeRequest.getProjectId();
        Integer mergeRequestId = mergeRequest.getIid();

        for (Commit c : getMergeRequestCommits(gitLabApi, mergeRequest)) {
            Commit detailedCommit = getDetailedCommit(gitLabApi, projectId, c);

            committers.add(detailedCommit.getAuthorEmail());
            addCommitIdAndMrIdToCommitterDto(committerToCommitterDtoMap, detailedCommit, mergeRequestId);

            CommitDtoDb commitDto = generateCommitDto(gitLabApi, projectId, detailedCommit, currentConfig);
            sumOfCommitsScore += commitDto.getScore();

            commitDtos.add(commitDto);
        }

        MergeRequestScoreCalculator scoreCalculator = new MergeRequestScoreCalculator(currentConfig);
        MergeRequest mrChanges = gitLabApi.getMergeRequestApi().getMergeRequestChanges(projectId, mergeRequestId);
        return new MergeRequestDtoDb(mergeRequest, commitDtos, committers, mrChanges, sumOfCommitsScore, scoreCalculator);
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
                            new HashSet<>(Collections.singletonList(commit.getId())),
                            new HashSet<>(Collections.singletonList(mergeRequestId)),
                            new MemberDtoDb()));
        }
    }

    private CommitDtoDb generateCommitDto(GitLabApi gitLabApi,
                                          Integer projectId,
                                          Commit detailedCommit,
                                          ConfigDto currentConfig)
            throws GitLabApiException {
        CommitScoreCalculator scoreCalculator = new CommitScoreCalculator(currentConfig);
        List<Diff> diffList = gitLabApi.getCommitsApi().getDiff(projectId, detailedCommit.getId());
        return new CommitDtoDb(detailedCommit, MemberUtils.EmptyMember.getId(), diffList, scoreCalculator);
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

    private ProjectDtoDb generateProjectDto(GitLabApi gitLabApi,
                                            Integer projectId,
                                            List<CommitterDtoDb> committers) throws GitLabApiException {
        Project project = gitLabApi.getProjectApi().getProject(projectId);
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
            Integer memberId = mrIdToMrDtoMap.get(mrId).getUserId();
            memberToMemberDtoMap.get(memberId).addMergeRequestDocId(mrIdToDocId.getSecond());
        }
    }

    private void cache(ProjectDtoDb projectDto,
                       Map<Integer, MergeRequestDtoDb> mrIdToMrDtoMap,
                       Map<Integer, MemberDtoDb> memberToMemberDtoMap) {
        analysisRepository.cacheProjectDto(projectDto);
        analysisRepository.cacheMergeRequestDtos(projectDto.getWebUrl(), new ArrayList<>(mrIdToMrDtoMap.values()));
        analysisRepository.cacheMemberDtos(projectDto.getWebUrl(), new ArrayList<>(memberToMemberDtoMap.values()));
    }
}