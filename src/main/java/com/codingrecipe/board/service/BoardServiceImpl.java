package com.codingrecipe.board.service;

import com.codingrecipe.board.entity.Board;
import com.codingrecipe.board.repository.BoardRepositoryImpl;
import com.codingrecipe.board.repository.CommentRepositoryImpl;
import com.codingrecipe.board.repository.LikeRepositoryImpl;
import com.codingrecipe.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    @Autowired
    BoardRepositoryImpl boardRepository;
    @Autowired
    CommentRepositoryImpl commentRepository;
    @Autowired
    LikeRepositoryImpl likeRepository;
    @Autowired
    FirebaseService firebaseService;

    public static final String COLLECTION_NAME = "BOARD";

    @Override
    public void save(Board board, List<MultipartFile> files) {
        try{
            List<String> urlList = new ArrayList<>();
            for(MultipartFile file : files) {
                String url = firebaseService.uploadFile(file, "board_images/" + UUID.randomUUID());
                if(url != null) {
                    urlList.add(url);
                }
            }

            board.setImageUrl(urlList);
            boardRepository.save(board);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(String id){
        try{
            Optional<Board> boardOptional = boardRepository.findById(Long.valueOf(id));
            if(boardOptional.isPresent()) {
                List<String> urlList = boardOptional.get().getImageUrl();
                for(String url : urlList) {
                    firebaseService.deleteFile(url);
                }
                commentRepository.deleteByBoardId(Long.valueOf(id));
                likeRepository.deleteByBoardId(Long.valueOf(id));
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
}