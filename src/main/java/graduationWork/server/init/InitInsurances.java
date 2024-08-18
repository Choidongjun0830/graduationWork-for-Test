package graduationWork.server.init;

import graduationWork.server.domain.Insurance;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class InitInsurances {

    private final InitInsuranceService initInsuranceService;

    @PostConstruct
    public void init() {
        initInsuranceService.domesticInit1();
        initInsuranceService.domesticInit2();
        initInsuranceService.overseaInit1();
        initInsuranceService.overseaInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitInsuranceService {

        private final EntityManager em;

        public void domesticInit1() {
            Insurance insurance = new Insurance();
            insurance.setName("국내 여행-Basic");
            insurance.setPremium(5000);
            insurance.setFormattedPremium("5,000원");
            insurance.setInsuranceType(InsuranceType.DOMESTIC);
            insurance.setCoverageDetails(Arrays.asList(
                    "국내의료비_상해 (급여) - 3000만원",
                    "국내의료비_상해 (비급여) - 3000만원",
                    "국내의료비_질병 (급여) - 3000만원",
                    "국내의료비_질병 (비급여) - 3000만원",
                    "국내의료비_3대 비급여 - 350만원",
                    "항공기 및 수하물 지연 보상 - 7만원"
            ));
            em.persist(insurance);
        }

        public void domesticInit2() {
            Insurance insurance = new Insurance();
            insurance.setName("국내 여행-Premium");
            insurance.setPremium(8000);
            insurance.setFormattedPremium("8,000원");
            insurance.setInsuranceType(InsuranceType.DOMESTIC);
            insurance.setCoverageDetails(Arrays.asList(
                    "국내의료비_상해 (급여) - 5000만원",
                    "국내의료비_상해 (비급여) - 5000만원",
                    "국내의료비_질병 (급여) - 5000만원",
                    "국내의료비_질병 (비급여) - 5000만원",
                    "국내의료비_3대 비급여 - 500만원",
                    "항공기 및 수하물 지연 보상 - 10만원"
            ));
            em.persist(insurance);
        }

        public void overseaInit1() {
            Insurance insurance = new Insurance();
            insurance.setName("해외 여행-Basic");
            insurance.setPremium(6000);
            insurance.setFormattedPremium("6,000원");
            insurance.setInsuranceType(InsuranceType.OVERSEAS);
            insurance.setCoverageDetails(Arrays.asList(
                    "해외여행 중 상해,사망,후유장해 - 9000만원",
                    "해외 의료비_상해 - 5000만원",
                    "해외여행 중 질병,사망 및 80% 이상 후유장해 - 3000만원",
                    "해외 의료비_질병 - 5000만원",
                    "해외여행 중 배상 책임 - 3000만원",
                    "해외여행 중 휴대품 손해 (분실 제외) - 60만원",
                    "해외여행 중 중대 사고 구조,송환 비용(7일이상) - 5000만원",
                    "해외여행 중 항공기 납치 - 140만원",
                    "해외여행 중 중대 사고로 인한 여행 중단 추가 비용 - 30만원",
                    "항공기 및 수하물 지연 보상 - 10만원",
                    "해외여행 중 여권 분실,재발급 비용 - 6만원",
                    "해외여행 중 식중독 입원 일당 - 2만원",
                    "해외여행 중 특정 전염병 치료비 - 20만원"
            ));
            em.persist(insurance);
        }

        public void overseaInit2() {
            Insurance insurance = new Insurance();
            insurance.setName("해외 여행-Premium");
            insurance.setPremium(10000);
            insurance.setFormattedPremium("10,000원");
            insurance.setInsuranceType(InsuranceType.OVERSEAS);
            insurance.setCoverageDetails(Arrays.asList(
                    "해외여행 중 상해,사망,후유장해 - 9500만원",
                    "해외 의료비_상해 - 6000만원",
                    "해외여행 중 질병,사망 및 80% 이상 후유장해 - 5000만원",
                    "해외의료비_질병 - 7000만원",
                    "해외여행 중 배상 책임 - 5000만원",
                    "해외여행 중 휴대품 손해(분실제외) - 80만원",
                    "해외여행 중 중대 사고 구조,송환 비용(7일이상) - 6000만원",
                    "해외여행 중 항공기 납치 - 180만원",
                    "해외여행 중 중대 사고로 인한 여행 중단 추가 비용 - 50만원",
                    "항공기 및 수하물 지연 보상 - 15만원",
                    "해외여행 중 여권 분실,재발급 비용 - 6만원",
                    "해외여행 중 식중독 입원 일당 - 2만원",
                    "해외여행 중 특정 전염병 치료비 - 25만원"
            ));
            em.persist(insurance);
        }
    }

}
