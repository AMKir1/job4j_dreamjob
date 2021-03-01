package ru.job4j.dream;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class PsqlStore implements Store {

    private final static Logger log = LoggerFactory.getLogger(PsqlStore.class);
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("H:/projects/job4j_dreamjob/db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            log.error("Failed to get db.properties. {}", e.getMessage());
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            log.error("Failed to get Property jdbc.driver. {}", e.getMessage());
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
            log.error("Failed to find All Posts. {}", e.getMessage());
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
            log.error("Failed to find All Candidates. {}", e.getMessage());
        }
        return candidates;
    }

    @Override
    public Collection<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM users")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    users.add(new User(it.getInt("id"), it.getString("name"), it.getString("email"), it.getString("password")));
                }
            }
        } catch (Exception e) {
            log.error("Failed to find All Users. {}", e.getMessage());
        }
        return users;
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
    public void saveUser(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
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
            log.error("Failed to find Post By Id. {}", e.getMessage());
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
            log.error("Failed to find Candidate By Id. {}", e.getMessage());
        }
        return can;
    }

    @Override
    public User findUserById(int id) {
        User user = null;
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("SELECT id, name, email, password FROM users WHERE id = (?)")
        ) {
            ps.setLong(1, id);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    user = new User(it.getInt("id"), it.getString("name"), it.getString("email"), it.getString("password"));
                }
            }
        } catch (Exception e) {
            log.error("Failed to find User By Id. {}", e.getMessage());
        }
        return user;
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
                log.error("Failed to find Photo By Candidate Id. {}", e.getMessage());
            }
            return photo;
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
            log.error("Failed to create Post. {}", e.getMessage());
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
            log.error("Failed to create Candidate. {}", e.getMessage());
        }
        return can;
    }

    private User create(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO users(name, email, password) VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            log.error("Failed to create User. {}", e.getMessage());
        }
        return user;
    }

    private void update(Candidate can) {
        try(Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("UPDATE candidate SET name = (?) WHERE id = (?)")
        ) {
            ps.setString(1, can.getName());
            ps.setLong(2, can.getId());
            ps.execute();
        } catch (Exception e) {
            log.error("Failed to update Candidate. {}", e.getMessage());
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
            log.error("Failed to update Post. {}", e.getMessage());
        }
    }

    private void update(User user) {
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("UPDATE users SET name = (?), email = (?), password = (?) WHERE id = (?)")
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
        } catch (Exception e) {
            log.error("Failed to update User. {}", e.getMessage());
        }
    }

    @Override
    public void updateUserName(int id, String name) {
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("UPDATE users SET name = (?) WHERE id = (?)")
        ) {
            ps.setString(1, name);
            ps.setString(2, String.valueOf(id));
            ps.execute();
        } catch (Exception e) {
            log.error("Failed to update User's Name. {}", e.getMessage());
        }
    }

    @Override
    public void updateUserEmail(int id, String email) {
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("UPDATE users SET email = (?) WHERE id = (?)")
        ) {
            ps.setString(1, email);
            ps.setString(2, String.valueOf(id));
            ps.execute();
        } catch (Exception e) {
            log.error("Failed to update User's Email. {}", e.getMessage());
        }
    }

    @Override
    public void updateUserPassword(int id, String password) {
        try(Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("UPDATE users SET password = (?) WHERE id = (?)")
        ) {
            ps.setString(1, password);
            ps.setString(2, String.valueOf(id));
            ps.execute();
        } catch (Exception e) {
            log.error("Failed to update User's Password. {}", e.getMessage());
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
            log.error("Failed to Save Photo Path. {}", e.getMessage());
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
            log.error("Failed to combine the picture with the candidate. {}", e.getMessage());
        }
    }

}
