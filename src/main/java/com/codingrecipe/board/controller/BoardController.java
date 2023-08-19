package com.codingrecipe.board.controller;

import com.codingrecipe.board.entity.Board;
import com.codingrecipe.board.dto.BoardRequestDto;
import com.codingrecipe.board.service.BoardServiceImpl;
import com.codingrecipe.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    BoardServiceImpl boardService;

    @PostMapping("") //  프론트에서 작성할 게시글의 정보와 토큰을 보내주면 게시글을 저장
    public ResponseEntity<?> createBoard(@RequestPart(value = "data") BoardRequestDto boardRequestDto,
                                  @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                  HttpServletRequest request) {
        try{
            //JwtUtil에서 유효성 검사 후 닉네임 받아옴
            String name = JwtUtil.getName(request);

            Board board = new Board(boardRequestDto, name);
            boardService.save(board, files);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long id,
                                    @RequestPart(value = "data") BoardRequestDto boardRequestDto,
                                    @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                    HttpServletRequest request) {
        try {
            Optional<Board> boardOptional = boardService.findById(id);
            if(boardOptional.isPresent()) {
                String name = JwtUtil.getName(request);
                Board board = boardOptional.get();
                if(board.getWriter().equals(name)) {
                    board.setTitle(boardRequestDto.getBoardTitle());
                    board.setContents(boardRequestDto.getBoardContents());
                    boardService.update(board, files);
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
    public ResponseEntity<?> deleteBoard(@PathVariable("id") Long id, HttpServletRequest request) {
        try{
            Optional<Board> boardOptional = boardService.findById(id);
            if(boardOptional.isPresent()) {
                String name = JwtUtil.getName(request);
                if(boardOptional.get().getWriter().equals(name)) {
                    boardService.delete(id);
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

    @GetMapping("")   //  프론트로 모든 게시글 리스트 보냄
    public ResponseEntity<List<Board>> findAll() {
        try{
            List<Board> list = boardService.findAll();

            return ResponseEntity.ok().body(list);
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{id}")  //  프론트에서 게시글의 아이디를 보내주면 그 아이디를 가진 게시글 찾아서 정보 리턴
    public ResponseEntity<Board> findOne(@PathVariable("id") Long id) {
        try{
            Optional<Board> boardOptional = boardService.findById(id);
            //boardOptional에 값이 있으면 BOARD로 바꿔서 리턴, 없으면 null 리턴
            return boardOptional.map(board -> ResponseEntity.ok().body(board)).orElseGet(() -> ResponseEntity.badRequest().body(null));
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Board>> findMyPosts(HttpServletRequest request){
        try {
            String name = JwtUtil.getName(request);
            return ResponseEntity.ok().body(boardService.findByMemberName(name));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
