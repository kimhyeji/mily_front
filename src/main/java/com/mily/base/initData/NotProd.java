package com.mily.base.initData;

import com.mily.article.milyx.MilyXService;
import com.mily.article.milyx.category.CategoryService;
import com.mily.article.milyx.category.entity.FirstCategory;
import com.mily.article.milyx.category.entity.SecondCategory;
import com.mily.estimate.Estimate;
import com.mily.payment.PaymentService;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserRepository;
import com.mily.user.MilyUserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

@Configuration
@AllArgsConstructor
@Profile("!prod")
public class NotProd {
    private final MilyUserService milyUserService;
    private final MilyUserRepository milyUserRepository;
    private final CategoryService categoryService;
    private final MilyXService milyXService;
    private final PaymentService paymentService;
    private FirstCategory fc;
    @Bean
    public ApplicationRunner init() {
        return args -> {
            Optional<MilyUser> mu = milyUserService.findByUserLoginId("admin999");
            if (mu.isEmpty()) {
                MilyUser milyUser1 = milyUserService.userSignup("admin999", "qweasdzxc", "administrator", "admin999@admin.com", "01099999999", "1975-01-21").getData();
                milyUser1.setRole("admin");
                milyUserRepository.save(milyUser1);
                MilyUser milyUser2 = milyUserService.userSignup("leewowns1005", "a7586898", "이재준", "leewowns1005@naver.com", "01020105481", "1996-10-05").getData();
                MilyUser milyUser3 = milyUserService.userSignup("oizill5481", "a7586898", "이재준", "oizill5481@icloud.com", "01045702579", "1996-10-05").getData();
                MilyUser milyUser4 = milyUserService.userSignup("test1111", "test1111", "한문철", "test1111@email.com", "01011111111", "1996-01-01").getData();
                milyUserService.lawyerSignup("교통사고/범죄", "교통 사고, 상해 전문 승소율 97% 이상 / 처리까지 최저 수임료로", "법무법인 아로", "1111-1111-1111", "대전", milyUser4, "https://i.postimg.cc/J7sVTCpC/Vudz-Wb-LYPYgc-OBwt32ap-Na-ZBHt5b8-AXA.webp");
                MilyUser milyUser5 = milyUserService.userSignup("test2222", "test2222", "박추어", "test2222@email.com", "01022222222", "1995-01-01").getData();
                milyUserService.lawyerSignup("민사 절차", "<1,400건 이상의 후기> 검증된 변호사 / 합리적 수임료", "법무법인 새긴", "2222-2222-2222", "대전", milyUser5, "https://i.postimg.cc/63sjzFqt/SE-8dd832e9-2fa4-4cb4-9868-f1ebc.jpg");
                MilyUser milyUser6 = milyUserService.userSignup("test3333", "test3333", "이정건", "test3333@email.com", "01033333333", "1995-01-01").getData();
                milyUserService.lawyerSignup("성 범죄", "[성매매/성범죄] 초기 대응부터 확실하게", "법률사무소 M&Y", "3333-3333-3333", "대전", milyUser6, "https://i.postimg.cc/0QkzDyvS/Kakao-Talk-20231125-230349223.jpg");
                MilyUser milyUser7 = milyUserService.userSignup("test4444", "test4444", "이재영", "test4444@email.com", "01044444444", "1995-01-01").getData();
                milyUserService.lawyerSignup("가족", "TV출연/대형 로펌 출신/이혼 관련 상담 2000건 이상", "MILY L&C", "4444-4444-4444", "대전", milyUser7, "https://i.postimg.cc/dtYHGRCT/Kakao-Talk-20231125-222200034.jpg");
                MilyUser milyUser8 = milyUserService.userSignup("test5555", "test5555", "최진우", "test5555@email.com", "01055555555", "1995-01-01").getData();
                milyUserService.lawyerSignup("교통사고/범죄", "교통 사고, 상해 전문 승소율 90% 이상 / 처리까지 최저 수임료로", "MILY L&C", "5555-5555-5555", "대전", milyUser8, "https://i.postimg.cc/Kc90KKbn/IMG-1638.jpg");

                milyUser4.setRole("approve");
                milyUser5.setRole("approve");
                milyUser6.setRole("approve");
                milyUser7.setRole("approve");
                milyUserRepository.save(milyUser4);
                milyUserRepository.save(milyUser5);
                milyUserRepository.save(milyUser6);
                milyUserRepository.save(milyUser7);

                paymentService.dummyPayment("161251211", milyUser2, 300, "밀리 포인트 300", (long) 4800);
                paymentService.dummyPayment("161251212", milyUser3, 50, "밀리 포인트 50", (long) 900);
                paymentService.dummyPayment("161251213", milyUser3, 100, "밀리 포인트 100", (long) 1700);

                categoryService.addFC("성 범죄");
                categoryService.addFC("재산 범죄");
                categoryService.addFC("교통사고/범죄");
                categoryService.addFC("폭행/협박");
                categoryService.addFC("명예훼손/모욕");
                categoryService.addFC("기타 형사범죄");
                categoryService.addFC("부동산/임대차");
                categoryService.addFC("금전/계약 문제");
                categoryService.addFC("민사 절차");
                categoryService.addFC("기타 민사 문제");
                categoryService.addFC("가족");
                categoryService.addFC("회사");
                categoryService.addFC("의료/세금/행정");
                categoryService.addFC("IT/지식재산/금융");

                fc = categoryService.findByFTitle("성 범죄");

                categoryService.addSC("성매매", fc);
                categoryService.addSC("성폭력/강제추행 등", fc);
                categoryService.addSC("미성년 대상 성범죄", fc);
                categoryService.addSC("디지털 성범죄", fc);

                fc = categoryService.findByFTitle("재산 범죄");

                categoryService.addSC("횡령/배임", fc);
                categoryService.addSC("사기/공갈", fc);
                categoryService.addSC("기타 재산범죄", fc);

                fc = categoryService.findByFTitle("교통사고/범죄");

                categoryService.addSC("교통사고/도주", fc);
                categoryService.addSC("음주/무면허", fc);

                fc = categoryService.findByFTitle("폭행/협박");

                categoryService.addSC("폭행/협박/상해 일반", fc);

                fc = categoryService.findByFTitle("명예훼손/모욕");

                categoryService.addSC("명예훼손/모욕 일반", fc);
                categoryService.addSC("사이버 명예훼손/모욕", fc);

                fc = categoryService.findByFTitle("기타 형사범죄");

                categoryService.addSC("마약/도박", fc);
                categoryService.addSC("소년범죄/학교폭력", fc);
                categoryService.addSC("형사일반/기타범죄", fc);

                fc = categoryService.findByFTitle("부동산/임대차");

                categoryService.addSC("건축/부동산 일반", fc);
                categoryService.addSC("재개발/재건축", fc);
                categoryService.addSC("매매/소유권 등", fc);
                categoryService.addSC("임대차", fc);

                fc = categoryService.findByFTitle("금전/계약 문제");

                categoryService.addSC("손해배상", fc);
                categoryService.addSC("대여금/채권추심", fc);
                categoryService.addSC("계약일반/매매", fc);

                fc = categoryService.findByFTitle("민사 절차");

                categoryService.addSC("소송/집행절차", fc);
                categoryService.addSC("가압류/가처분", fc);
                categoryService.addSC("회생/파산", fc);

                fc = categoryService.findByFTitle("기타 민사 문제");

                categoryService.addSC("공증/내용증명/조합/국제문제 등", fc);

                fc = categoryService.findByFTitle("가족");

                categoryService.addSC("이혼", fc);
                categoryService.addSC("상속", fc);
                categoryService.addSC("가사 일반", fc);

                fc = categoryService.findByFTitle("회사");

                categoryService.addSC("기업법무", fc);
                categoryService.addSC("노동/인사", fc);

                fc = categoryService.findByFTitle("의료/세금/행정");

                categoryService.addSC("세금/행정/헌법", fc);
                categoryService.addSC("의료/식품의약", fc);
                categoryService.addSC("병역/군형법", fc);

                fc = categoryService.findByFTitle("IT/지식재산/금융");

                categoryService.addSC("소비자/공정거래", fc);
                categoryService.addSC("IT/개인정보", fc);
                categoryService.addSC("지식재산권/엔터", fc);
                categoryService.addSC("금융/보험", fc);

                FirstCategory firstCategory = categoryService.findByFTitle("성 범죄");
                SecondCategory secondCategory = categoryService.findBySTitle("성폭력/강제추행 등");
                milyXService.dummyCreate(milyUser1, firstCategory, secondCategory, "대학교 후배와 성관계 이후 상대방이 고소를 진행할 예정이랍니다.", "올해 초에 처음 관계를 맺었으며, 관계 이후에도 연락을 주고 받으면서 지냈습니다. 친하게 지냈음에도 불구하고, 5월에 상대방이 교수님과 상담하며 본인과 관계한 것에 대해 자궁에 상처가 났고, 성폭력을 당한 것처럼 주장했습니다. 처음 연락을 주고받은 시점부터 연락한 내용은 남아있습니다. 좋게 지내다가 돌연 사이가 소원해지면서 이전의 성관계에 대해 증거가 없다 생각해서 저러는 것 같은데, 어떻게 대처해야할까요? 무죄를 입증할만한 증거가 있다면 무죄 판정 받은 후에 무고죄도 성립될까요?", 50);
                Estimate estimate1 = milyUserService.sevenCreateEstimate(firstCategory, secondCategory, "대전", "내용", milyUser2);

                firstCategory = categoryService.findByFTitle("폭행/협박");
                secondCategory = categoryService.findBySTitle("폭행/협박/상해 일반");
                milyXService.dummyCreate(milyUser3, firstCategory, secondCategory, "술집에서 싸움에 휘말려 양방 폭행이 나왔습니다.", "사건 일시 : 2023년 10월 24일\n사건 경위 : 모르는 남성이 옆 테이블에 앉은 여성 일행에게 번호를 물었고, 일행이 완강하게 거부 의사를 밝혔으나 돌아가지 않고 계속 머뭇거려서 '자리로 돌아가세요 싫다잖아요'라고 한 마디 하자마자 얼굴을 한 대 맞았습니다.\n이후 저는 하지 말라는 의사를 두 차례 전달하였음에도 불구하고 밀치는 등의 행위를 계속 취해 와서 바닥에 넘어뜨렸는데 쌍방이랍니다.", 50);
                Estimate estimate2 = milyUserService.sevenCreateEstimate(firstCategory, secondCategory, "대전", "내용", milyUser2);

                firstCategory = categoryService.findByFTitle("명예훼손/모욕");
                secondCategory = categoryService.findBySTitle("사이버 명예훼손/모욕");
                milyXService.dummyCreate(milyUser2, firstCategory, secondCategory, "게임하다가 심한 욕설을 들었습니다.", "사건 발생 : 2023.09.10 11:35 a.m.\n본인 챔피언 : 카이사\n가해 챔피언 : 리 신\n내용 : 리그오브레전드 일반 (팀)게임을 하면서 '만년브론즈희생폿(본인)아, 니 엄마는 너를 낳지 말았어야 한다', '이게 피임의 중요성이다 시', '발아 ㅋㅋ'라고 하며 저를 능욕했고, 수치심과 모욕감에 한참을 진정하지 못 했습니다.\n본인 챔피언인 카이사를 특정하며 말을 해서 특정성과 팀 게임 특성상 공연성은 성립될 거 같은데, 모욕성까지 해서 모욕죄 성립 요건 3가지를 충족했으니 고소할 수 있을까요?", 50);
                Estimate estimate3 = milyUserService.sevenCreateEstimate(firstCategory, secondCategory, "대전", "내용", milyUser2);
            }
        };
    }
}