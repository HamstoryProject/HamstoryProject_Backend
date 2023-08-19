package com.codingrecipe.board.repository;

import com.codingrecipe.board.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    void save(Board board);

    void update(Board board);

    void delete(Long id);

    Optional<Board> findById(Long id);

    List<Board> findAll();

    List<Board> findByMemberName(String name);
}
