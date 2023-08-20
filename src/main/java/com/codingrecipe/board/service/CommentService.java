package com.codingrecipe.board.service;

import com.codingrecipe.board.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    void save(Comment comment);

    void delete(Long id);

    List<Comment> findByBoardId(Long boardId);

    List<Comment> findAll();

    Optional<Comment> findById(Long id);

}
