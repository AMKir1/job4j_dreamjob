package ru.job4j.dream;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
        //Добавляем новые вакансии
        store.savePost(new Post(0, "Java Job"));
        store.savePost(new Post(0, "Java Job"));
        store.savePost(new Post(0, "Java Job"));
        //Изменяем вакансии
        store.savePost(new Post(1, "Java Job"));
        store.savePost(new Post(2, "Java Job 2"));
        //Добавляем новых кандидатов
        store.saveCandidate(new Candidate(0, "Andrew"));
        store.saveCandidate(new Candidate(0, "Andrew"));
        store.saveCandidate(new Candidate(0, "Andrew"));
        //Изменяем кандидатов
        store.saveCandidate(new Candidate(1, "Andrew"));
        store.saveCandidate(new Candidate(2, "Andrew 2"));

        //Получаем все посты из базы
        System.out.println("All Post");
        for (Post post : store.findAllPosts()) {
            System.out.println(post.getId() + " " + post.getName());
        }
        //Получаем всех кандидатов из базы
        System.out.println("All Candidates");
        for (Candidate can : store.findAllCandidates()) {
            System.out.println(can.getId() + " " + can.getName());
        }
        //Ищем вакансию по id
        System.out.println("find post by id");
        System.out.println(store.findPostById(1).getName());
        //Ищем кандидата по id
        System.out.println("find Candidate by id");
        System.out.println(store.findCandidateById(2).getName());

    }
}
