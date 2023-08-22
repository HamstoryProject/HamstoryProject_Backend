package com.codingrecipe.board.repository;

import com.codingrecipe.board.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    void save(Comment comment);

    void update(Comment comment);

    void delete(Long id);

    void deleteByBoardId(Long boardId);

    List<Comment> findByBoardId(Long boardId);

    List<Comment> findAll();

    Optional<Comment> findById(Long id);
    void addLikers(Comment comment, String commentLiker);

}
