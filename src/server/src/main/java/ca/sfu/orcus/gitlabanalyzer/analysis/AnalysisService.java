package ca.sfu.orcus.gitlabanalyzer.analysis;

import ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos.*;
import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void analyzeProject(String jwt, int projectId) throws GitLabApiException, NullPointerException {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            throw new GitLabApiException("Could not get GitLabApi object");
        }

        analyzeProject(gitLabApi, projectId);
    }

    public void analyzeProject(GitLabApi gitLabApi, Integer projectId) throws GitLabApiException, NullPointerException {
        Map<String, CommitterDtoDb> committerDtos = new HashMap<>(); // committerEmail -> committerDto
        Map<Integer, MemberDtoDb> memberDtos = initializeMemberDtos(gitLabApi, projectId); // memberId -> memberDto

        List<MergeRequestDtoDb> mergeRequestDtos = new ArrayList<>();

        for (MergeRequest mr : getAllMergeRequests(gitLabApi, projectId)) {
            addMergeRequestIdToMemberDto(memberDtos, mr);

            MergeRequestDtoDb mergeRequestDto = getMergeRequestDto(gitLabApi, mr, committerDtos);
            mergeRequestDtos.add(mergeRequestDto);

            addMergeRequestNotesToMemberDtos(gitLabApi, memberDtos, mr);
        }

        addIssueNotesToMemberDtos(gitLabApi, memberDtos, projectId);

        // TODO: cacheCommitterDtos()
        // TODO: cacheMemberDtos()
        // TODO: cacheMergeRequestDtos()
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

    private void addMergeRequestIdToMemberDto(Map<Integer, MemberDtoDb> memberDtos, MergeRequest mr) {
        Integer authorId = mr.getAuthor().getId();
        MemberDtoDb memberDtoDb = memberDtos.get(authorId);
        memberDtoDb.addMergeRequestId(mr.getIid());
        // memberDtos.get(authorId).addMergeRequestId(mr.getIid());
    }

    private MergeRequestDtoDb getMergeRequestDto(GitLabApi gitLabApi,
                                                 MergeRequest mergeRequest,
                                                 Map<String, CommitterDtoDb> committerDtos) throws GitLabApiException {
        List<CommitDtoDb> commitDtos = new ArrayList<>();
        Set<String> committers = new HashSet<>();

        int numAdditions = 0;
        int numDeletions = 0;

        Integer projectId = mergeRequest.getProjectId();
        Integer mergeRequestId = mergeRequest.getIid();

        for (Commit c : getMergeRequestCommits(gitLabApi, mergeRequest)) {
            Commit detailedCommit = getDetailedCommit(gitLabApi, projectId, c);

            committers.add(detailedCommit.getAuthorEmail());
            addCommitIdAndMrIdToCommitterDto(committerDtos, detailedCommit, mergeRequestId);

            CommitDtoDb commitDto = getCommitDto(gitLabApi, projectId, detailedCommit);

            numAdditions += commitDto.getNumAdditions();
            numDeletions += commitDto.getNumDeletions();

            commitDtos.add(commitDto);
        }

        MergeRequest mrChanges = gitLabApi.getMergeRequestApi().getMergeRequestChanges(projectId, mergeRequestId);
        return new MergeRequestDtoDb(mergeRequest, numAdditions, numDeletions, commitDtos, committers, mrChanges);
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

        if (committerDtos.containsKey(authorEmail)) {
            CommitterDtoDb committerDto = committerDtos.get(authorEmail);
            committerDto.addCommitId(commit.getId());
            committerDto.addMergeRequestId(mergeRequestId);
        } else {
            committerDtos.put(
                    authorEmail,
                    new CommitterDtoDb(authorEmail,
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
        addNotesToMemberDtos(memberDtos, mergeRequestNotes, mr.getWebUrl());
    }

    private void addNotesToMemberDtos(Map<Integer, MemberDtoDb> memberDtos, List<Note> notes, String webUrl) {
        for (Note n : notes) {
            if (!n.getSystem()) {
                Integer authorId = n.getAuthor().getId();
                NoteDtoDb noteDto = new NoteDtoDb(n, webUrl);
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
            addNotesToMemberDtos(memberDtos, issueNotes, i.getWebUrl());
        }
    }
}