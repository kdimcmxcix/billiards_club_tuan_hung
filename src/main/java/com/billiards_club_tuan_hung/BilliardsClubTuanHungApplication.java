package com.billiards_club_tuan_hung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class BilliardsClubTuanHungApplication {

    public static void main(String[] args) {
        SpringApplication.run(BilliardsClubTuanHungApplication.class, args);
    }

    // Thiết lập múi giờ mặc định cho toàn bộ ứng dụng là Asia/Ho_Chi_Minh
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }
}
