package com.codingrecipe.board.controller;

import com.codingrecipe.board.entity.Comment;
import com.codingrecipe.board.dto.CommentRequestDto;
import com.codingrecipe.board.service.BoardServiceImpl;
import com.codingrecipe.board.service.CommentServiceImpl;
import com.codingrecipe.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    CommentServiceImpl commentService;

    @Autowired
    BoardServiceImpl boardService;

    @PostMapping("")  // 프론트에서 댓글 내용을 받아오면 작성자, 댓글 쓴 글, 작성시간, 댓글 좋아요 수, 내용을 설정해서 반환해줌
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        try{
            String name = JwtUtil.getName(request);

            if(boardService.findById(Long.valueOf(commentRequestDto.getBoardId())).isPresent()) {
                commentService.save(new Comment(commentRequestDto, name));
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id, HttpServletRequest request) {
        try{
            Optional<Comment> commentOptional = commentService.findById(id);
            if(commentOptional.isPresent()) {
                Comment comment = commentOptional.get();
                String name = JwtUtil.getName(request);
                if(comment.getWriter().equals(name)) {
                    commentService.delete(id);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch(Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("")   //  프론트로 해당 게시글의 댓글 리스트 보냄
    public ResponseEntity<List<Comment>> findComments(@RequestParam("boardId") Long boardId) {
        try{
            if(boardService.findById(boardId).isPresent()) {
                List<Comment> list = commentService.findByBoardId(boardId);
                return ResponseEntity.ok().body(list);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Comment>> findAll() {
        try{
            List<Comment> list = commentService.findAll();

            return ResponseEntity.ok().body(list);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
