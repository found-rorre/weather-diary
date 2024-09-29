package zerobase.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Diary API", description = "Diary management APIs")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }
    //  summary = value , description = note
    @Operation(summary = "일기 데이터 저장", description = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장합니다.")
    @PostMapping("/create/diary")
    //  description = value
    void createDiary(@Parameter(description = "일기 작성한 날", example = "2023-09-27")
                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text) {

        diaryService.createDiary(date, text);
    }
    @Operation(summary = "특정일 일기 데이터 조회", description = "선택한 날씨의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@Parameter(description = "조회할 날", example = "2023-09-27")
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

         return diaryService.readDiary(date);
    }

    @Operation(summary = "특정 기간 일기 데이터 조회", description = "선택한 기간중의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@Parameter(description = "조회할 기간의 첫번째 날", example = "2023-09-27")
                            @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @Parameter(description = "조회할 기간의 마지막 날", example = "2023-09-30")
                            @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return diaryService.readDiaries(startDate, endDate);
    }

    @Operation(summary = "일기 데이터 수정", description = "일기 데이터의 텍스트를 수정합니다.")
    @PutMapping("/update/diary")
    void updateDiary(@Parameter(description = "텍스트를 수정할 일기 날짜", example = "2023-09-27")
                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text) {

        diaryService.updateDiary(date, text);
    }

    @Operation(summary = "일기 데이터 삭제", description = "일기 데이터를 DB 에서 삭제합니다.")
    @DeleteMapping("/delete/diary")
    void deleteDiary(@Parameter(description = "삭제할 일기 날짜", example = "2023-09-27")
                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        diaryService.deleteDiary(date);
    }


}
