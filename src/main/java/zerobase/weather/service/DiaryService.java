package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }



    public void createDiary(LocalDate date, String text) {

        logger.info("start createDairy");

        //  날씨 데이터가져오기 (API 에서 가져오기 or DB 에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        //  파싱된 데이터 + 일기 값 우리 db에 넣기
        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText(text);


        diaryRepository.save(diary);

        logger.info("end createDairy");
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        logger.debug("read Diary");
        return diaryRepository.findAllByDate(date);
    }


    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary diary = diaryRepository.getFirstByDate(date);

        //수정
        diary.setText(text);

        diaryRepository.save(diary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }


    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        logger.info("오늘도 날씨데이터 가져오기 완료");
        dateWeatherRepository.save(getWeatherFromApi());
    }



    private String getWeatherString() {

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream())) ;
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return  response.toString();

        } catch (Exception e) {
            return  "failed to get response";
        }

    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        }catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("temp", mainData.get("temp"));
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }

    private DateWeather getWeatherFromApi(){
        //  open weather map 에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        //  받아온 날씨 json 파싱하기
        Map<String, Object> parseWeather = parseWeather(weatherData);

        DateWeather dateWeather = DateWeather.builder()
                                    .date(LocalDate.now())
                                    .weather(parseWeather.get("main").toString())
                                    .icon(parseWeather.get("icon").toString())
                                    .temperature((double) parseWeather.get("temp"))
                                    .build();
        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date){
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findByDate(date);

        if (dateWeatherListFromDB.isEmpty()) {
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

}
