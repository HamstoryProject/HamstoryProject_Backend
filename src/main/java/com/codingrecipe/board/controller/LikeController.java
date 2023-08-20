package com.codingrecipe.board.controller;

import com.codingrecipe.board.entity.Like;
import com.codingrecipe.board.service.BoardServiceImpl;
import com.codingrecipe.board.service.LikeServiceImpl;
import com.codingrecipe.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    LikeServiceImpl likeService;

    @Autowired
    BoardServiceImpl boardService;

    @RequestMapping("")
    public ResponseEntity<?> like(@RequestParam("boardId") Long boardId , HttpServletRequest request) {
        try{
            String name = JwtUtil.getName(request);

            if(boardService.findById(boardId).isPresent()) {
                likeService.update(new Like(boardId, name));
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
