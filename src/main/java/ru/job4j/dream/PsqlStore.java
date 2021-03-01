package ru.job4j.dream;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class PsqlStore implements Store {
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("H:/projects/job4j_dreamjob/db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM candidate")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("id"), it.getString("name"), it.getInt("photo_id")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return candidates;
    }

    @Override
    public Map<Long, String> findAllPhotos() {
        Map<Long, String> photos = new HashMap<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM photo")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    photos.put(it.getLong("id"), it.getString("name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photos;
    }

    @Override
    public Map<Candidate, String> findPhotoForAllCandidate(Collection<Candidate> clist) {
        Map<Candidate, String> candidates = new HashMap<>();
        clist.forEach(c -> candidates.put(c, findPhotoByCandidateId(String.valueOf(c.getId()))));
        return candidates;
    }

    @Override
    public void savePost(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public void saveCandidate(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    @Override
    public Post findPostById(int id) {
        Post post = null;
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("SELECT id, name FROM post WHERE id = (?)")
        ) {
            ps.setLong(1, id);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    post = new Post(it.getInt("id"), it.getString("name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public Candidate findCandidateById(int id) {
        Candidate can = null;
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("SELECT id, name FROM candidate WHERE id = (?)")
        ) {
            ps.setLong(1, id);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    can = new Candidate(it.getInt("id"), it.getString("name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return can;
    }

    @Override
    public String findPhotoByCandidateId(String id) {
        if(id != null) {
            String photo = null;
            try (Connection cn = pool.getConnection();
                 PreparedStatement ps = cn.prepareStatement("Select p.name  AS photo FROM candidate AS c JOIN photo AS p ON c.photo_id = p.id WHERE c.id = (?)")
            ) {
                ps.setLong(1, Integer.parseInt(id));
                try (ResultSet it = ps.executeQuery()) {
                    while (it.next()) {
                        photo = new File(it.getString("photo")).getName();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("findPhotoByCandidateId: " + photo + " FOR ID: " + id);
            return photo != null ? photo : null;
        }
        return null;
    }

    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO post(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    private Candidate create(Candidate can) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO candidate(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, can.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    can.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return can;
    }

    private void update(Candidate can) {
        try(Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("UPDATE candidate SET name = (?) WHERE id = (?)")
        ) {
            ps.setString(1, can.getName());
            ps.setLong(2, can.getId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update(Post post) {
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("UPDATE post SET name = (?) WHERE id = (?)")
        ) {
            ps.setString(1, post.getName());
            ps.setLong(2, post.getId());
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePhotoPath(int cand_id, String photo) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO photo(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, photo);
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    joinCandidateAndPhoto(cand_id, id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinCandidateAndPhoto(int c_id, int p_id) {
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("UPDATE candidate SET photo_id = (?) WHERE id = (?)")
        ) {
            ps.setLong(1, p_id);
            ps.setLong(2, c_id);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
