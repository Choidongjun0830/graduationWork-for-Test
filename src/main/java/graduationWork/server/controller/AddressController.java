package graduationWork.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AddressController {

    @GetMapping("/juso")
    public @ResponseBody String dfd(String roadFullAddr){
        System.out.println("테스트 : "+ roadFullAddr);
        return "ok";
    }

    @PostMapping("/juso")
    public @ResponseBody String dfddsa(String roadFullAddr){
        System.out.println("테스트 : "+ roadFullAddr);
        return "ok";
    }

    @RequestMapping(value="/sample/getAddrApi.do")
    public void getAddrApi(HttpServletRequest req, ModelMap model, HttpServletResponse response) throws Exception {
        // 요청변수 설정
        String currentPage = req.getParameter("currentPage");    //요청 변수 설정 (현재 페이지. currentPage : n > 0)
        String countPerPage = req.getParameter("countPerPage");  //요청 변수 설정 (페이지당 출력 개수. countPerPage 범위 : 0 < n <= 100)
        String resultType = req.getParameter("resultType");      //요청 변수 설정 (검색결과형식 설정, json)
        String confmKey = req.getParameter("confmKey");          //요청 변수 설정 (승인키)
        String keyword = req.getParameter("keyword");            //요청 변수 설정 (키워드)
        // OPEN API 호출 URL 정보 설정
        String apiUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do?currentPage="+currentPage+"&countPerPage="+countPerPage+"&keyword="+URLEncoder.encode(keyword,"UTF-8")+"&confmKey="+confmKey+"&resultType="+resultType;
        URL url = new URL(apiUrl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
        StringBuffer sb = new StringBuffer();
        String tempStr = null;

        while(true){
            tempStr = br.readLine();
            if(tempStr == null) break;
            sb.append(tempStr);								// 응답결과 JSON 저장
        }
        br.close();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        response.getWriter().write(sb.toString());			// 응답결과 반환
    }


    @GetMapping("/jusoPopup")
    public String jusoPopup() {
        return "address/jusoPopup";
    }

    @ResponseBody
    @GetMapping("/jusoApi")
    public ResponseEntity<String> getAddrApi(@RequestParam String currentPage,
                                     @RequestParam String countPerPage,
                                     @RequestParam String confmKey,
                                     @RequestParam String keyword) throws Exception {

        //OPEN API 호출 URL 정보 설정
        String apiUrl = "https://business.juso.go.kr/addrlink/addrLinkApi.do";
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("currentPage", currentPage)
                    .queryParam("countPerPage", countPerPage)
                    .queryParam("confmKey", confmKey)
                    .queryParam("keyword", keyword);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(uriBuilder.toUriString(), String.class);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러 발생");
        }
    }
}
