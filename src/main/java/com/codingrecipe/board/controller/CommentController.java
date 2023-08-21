package com.codingrecipe.board.controller;

import com.codingrecipe.board.dto.CommentUpdateDto;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable("id") Long id,
                                           @RequestBody CommentUpdateDto commentUpdateDto,
                                           HttpServletRequest request) {
        try {
            Optional<Comment> commentOptional = commentService.findById(id);
            if(commentOptional.isPresent()) {
                String name = JwtUtil.getName(request);
                Comment origin = commentOptional.get();
                if(origin.getWriter().equals(name)) {
                    Comment comment = new Comment(origin, commentUpdateDto);
                    commentService.update(comment);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
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
    public ResponseEntity<?> findComments(@RequestParam("boardId") Long boardId) {  // 바디에 넣을 값이 안정해졌을 때 {?}를 하면 된다
        try{
            if(boardService.findById(boardId).isPresent()) {  // 보드아이디에 해당하는 보드가 있다묜~
                List<Comment> list = commentService.findByBoardId(boardId);  // 댓글 리스트 보내주는거
                if(list.isEmpty()) {  // 댓글 리스트가 비어있다면
                    return ResponseEntity.ok().body(false);
                }
                else {
                    return ResponseEntity.ok().body(list);  // 댓글 리스트를 프론트로 보내주는거
                }
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
