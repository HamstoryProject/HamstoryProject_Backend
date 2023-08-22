package com.codingrecipe.board.controller;

import com.codingrecipe.board.dto.BoardResponseDto;
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
import java.util.stream.Collectors;

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

            String category = boardRequestDto.getBoardCategory();
            if(category.equals("자유게시판") || category.equals("앨범게시판")) {
                if(category.equals("앨범게시판")) {
                    if(files == null || files.get(0).isEmpty()) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    files = files.subList(0, 1);
                }
                Board board = new Board(boardRequestDto, name);
                boardService.save(board, files);
                return new ResponseEntity<>(HttpStatus.OK);  // 정상적인 응답
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {  // 예외처리 1. 토큰 유효성 검사 결과가 유효하지 않을 때
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e){  // 예외처리 2. 코드 에러 났을 때
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long id,  // 게시물 수정
                                    @RequestPart(value = "data") BoardRequestDto boardRequestDto,
                                    @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                    HttpServletRequest request) {
        try {
            Optional<Board> boardOptional = boardService.findById(id);  // 해당 아이디의 게시물을 받아옴
            if(boardOptional.isPresent()) {  // boardOptional이 null 값일 때(아이디 잘못 보냈을때)
                String name = JwtUtil.getName(request);
                Board board = boardOptional.get();
                if(board.getWriter().equals(name)) {  // 게시물 게시자 닉네임 받아와서 작성자 닉네임과 동일한지 확인
                    board.setTitle(boardRequestDto.getBoardTitle());  // 제목 수정
                    board.setContents(boardRequestDto.getBoardContents());  // 내용 수정
                    boardService.update(board, files);  // 보드 수정한거 업데이트와 이미지 수정
                    return new ResponseEntity<>(HttpStatus.OK);  //
                }
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 다른 사람이면 게시물 수정 권한이 없음
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
    public ResponseEntity<List<BoardResponseDto>> findAll() {
        try{
            List<Board> list = boardService.findAll();

            return ResponseEntity.ok().body(list.stream().map(BoardResponseDto::new).collect(Collectors.toList()));
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{id}")  //  프론트에서 게시글의 아이디를 보내주면 그 아이디를 가진 게시글 찾아서 정보 리턴
    public ResponseEntity<BoardResponseDto> findOne(@PathVariable("id") Long id) {
        try{
            Optional<Board> boardOptional = boardService.findById(id);
            return boardOptional.map(board -> ResponseEntity.ok().body(new BoardResponseDto(board))).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            //return boardOptional.map(board -> ResponseEntity.ok().body(board)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/my")
    public ResponseEntity<List<BoardResponseDto>> findMyPosts(HttpServletRequest request){
        try {
            String name = JwtUtil.getName(request);
            return ResponseEntity.ok().body(boardService.findByMemberName(name).stream().map(BoardResponseDto::new).collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeBoard(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            String email = JwtUtil.getEmail(request);
            Optional<Board> boardOptional = boardService.findById(id);
            if(boardOptional.isPresent()) {
                boardService.updateLike(boardOptional.get(), email);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
        } return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
