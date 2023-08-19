package com.codingrecipe.board.service;

import com.codingrecipe.board.entity.Board;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BoardService {

    void save(Board board, List<MultipartFile> files);

    void update(Board board, List<MultipartFile> files);

    Optional<Board> findById(Long id);

    List<Board> findAll();

    boolean delete(Long postId);

    List<Board> findByMemberName(String name);
}