package com.codingrecipe.board.service;

import com.codingrecipe.board.entity.Board;
import com.codingrecipe.board.repository.BoardRepositoryImpl;
import com.codingrecipe.board.repository.CommentRepositoryImpl;
import com.codingrecipe.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    @Autowired
    BoardRepositoryImpl boardRepository;
    @Autowired
    CommentRepositoryImpl commentRepository;
    @Autowired
    FirebaseService firebaseService;

    static final String IMAGE_PATH = "board_images/";

    @Override
    public void save(Board board, List<MultipartFile> files) {
        try{
            board.setImageUrl(firebaseService.uploadAll(files, IMAGE_PATH));
            boardRepository.save(board);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Board board, List<MultipartFile> files) {
        try {
            List<String> originalImage = board.getImageUrl();
            firebaseService.deleteAll(originalImage);
            board.setImageUrl(firebaseService.uploadAll(files, IMAGE_PATH));
            boardRepository.update(board);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(Long id){
        try{
            Optional<Board> boardOptional = boardRepository.findById(id);
            if(boardOptional.isPresent()) {
                List<String> urlList = boardOptional.get().getImageUrl();
                firebaseService.deleteAll(urlList);
                commentRepository.deleteByBoardId(id);
                //likeRepository.deleteByBoardId(id);
                boardRepository.delete(id);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Board> findById(Long id) {
        try{
            return boardRepository.findById(id);
        } catch (Exception e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Board> findAll() {
        try{
            return boardRepository.findAll();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Board> findByMemberName(String name){
        try {
            return boardRepository.findByMemberName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateLike(Board board, String email) {
        try {
            boardRepository.updateLike(board, email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}