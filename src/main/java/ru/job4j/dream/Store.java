package ru.job4j.dream;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;
import java.util.Collection;

import java.util.Map;

public interface Store {
    Collection<Post> findAllPosts();
    Collection<Candidate> findAllCandidates();
    Collection<User> findAllUsers();
    Map<Candidate, String> findPhotoForAllCandidate(Collection<Candidate> list);
    Collection<City> findAllCities();

    void savePost(Post post);
    void saveCandidate(Candidate candidate);
    User saveUser(User user);
    void saveCity(String city);
    void updateUserName(int id, String name);
    void updateUserEmail(int id, String email);
    void updateUserPassword(int id, String password);

    Post findPostById(int id);
    Candidate findCandidateById(int id);
    User findUserById(int id);
    User findUserByEmail(String email);
    Boolean existsUser(String email);
    City findCityById(String id);
    City findCityByName(String name);

    String findPhotoByCandidateId(String parseInt);
    void savePhotoPath(int candidate_id, String photo);
}
