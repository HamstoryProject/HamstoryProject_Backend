package com.codingrecipe.board.service;

import com.codingrecipe.board.entity.Comment;
import com.codingrecipe.board.repository.CommentRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service

public class CommentServiceImpl implements CommentService{

    @Autowired
    CommentRepositoryImpl commentRepository;

    @Override
    public void save(Comment comment) {
        try{
            commentRepository.save(comment);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Comment comment) {
        try {
            commentRepository.update(comment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id){
        try{
            commentRepository.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Comment> findByBoardId(Long boardId) {
        try{
            return commentRepository.findByBoardId(boardId);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Comment> findAll() {
        try{
            return commentRepository.findAll();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        try {
            return commentRepository.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
