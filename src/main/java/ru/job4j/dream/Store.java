package ru.job4j.dream;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Store {
    Collection<Post> findAllPosts();
    Collection<Candidate> findAllCandidates();
    Map<Long, String> findAllPhotos();
    Map<Candidate, String> findPhotoForAllCandidate(Collection<Candidate> list);

    void savePost(Post post);
    void saveCandidate(Candidate candidate);

    Post findPostById(int id);
    Candidate findCandidateById(int id);

    String findPhotoByCandidateId(String parseInt);
    void savePhotoPath(int candidate_id, String photo);
}
