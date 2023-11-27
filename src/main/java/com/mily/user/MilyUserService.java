package com.mily.user;

import com.mily.Email.EmailService;
import com.mily.article.milyx.category.entity.FirstCategory;
import com.mily.article.milyx.category.entity.SecondCategory;
import com.mily.base.rsData.RsData;
import com.mily.estimate.Estimate;
import com.mily.estimate.EstimateRepository;
import com.mily.image.AppConfig;
import com.mily.image.Image;
import com.mily.image.ImageService;
import com.mily.standard.util.Ut;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MilyUserService {
    private final EmailService emailService;
    private final MilyUserRepository milyUserRepository;
    private final LawyerUserRepository lawyerUserRepository;
    private final EstimateRepository estimateRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @Transactional
    public RsData<MilyUser> userSignup(String userLoginId, String userPassword, String userName, String userEmail, String userPhoneNumber, String userDateOfBirth) {
        if (findByUserLoginId(userLoginId).isPresent()) {
            return RsData.of("F-1", "%s은(는) 이미 사용 중인 아이디입니다.".formatted(userLoginId));
        }
        if (findByUserEmail(userEmail).isPresent()) {
            return RsData.of("F-1", "%s은(는) 이미 인증 된 이메일입니다.".formatted(userEmail));
        }
        if (findByUserPhoneNumber(userPhoneNumber).isPresent()) {
            return RsData.of("F-1", "%s은(는) 이미 인증 된 전화번호입니다.".formatted(userPhoneNumber));
        }

        LocalDateTime now = LocalDateTime.now();

        MilyUser mu = MilyUser
                .builder()
                .userLoginId(userLoginId)
                .userPassword(passwordEncoder.encode(userPassword))
                .userName(userName)
                .userEmail(userEmail)
                .userPhoneNumber(userPhoneNumber)
                .userDateOfBirth(userDateOfBirth)
                .userCreateDate(now)
                .build();

        mu = milyUserRepository.save(mu);

        return RsData.of("S-1", "MILY 회원이 되신 것을 환영합니다!", mu);
    }

    /* 암호화 된 비밀 번호를 확인함 */
    public boolean checkPassword (MilyUser user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getUserPassword());
    }

    @Transactional
    public RsData<LawyerUser> lawyerSignup(String major, String introduce, String officeAddress, String licenseNumber, String area, MilyUser milyUser, String profileImgFilePath) {
        milyUser.setRole("waiting");
        milyUser =  milyUserRepository.saveAndFlush(milyUser);

        if (profileImgFilePath != null) saveProfileImg(milyUser, profileImgFilePath);

        LawyerUser lu = LawyerUser
                .builder()
                .major(major)
                .introduce(introduce)
                .officeAddress(officeAddress)
                .profileImgFilePath(profileImgFilePath)
                .licenseNumber(licenseNumber)
                .area(area)
                .milyUser(milyUser)
                .build();

        lu = lawyerUserRepository.save(lu);

        return RsData.of("S-1", "변호사 가입 신청을 완료 했습니다.", lu);
    }

    public RsData<LawyerUser> lawyerSignup(String major, String introduce, String officeAddress, String licenseNumber, String area, MilyUser milyUser, MultipartFile profileImg) {
        String profileImgFilePath = Ut.file.toFile(profileImg, AppConfig.getTempDirPath());
        return lawyerSignup(major, introduce, officeAddress, licenseNumber, area, milyUser, profileImgFilePath);
    }

    public Optional<MilyUser> findByUserLoginId(String userLoginId) {
        return milyUserRepository.findByUserLoginId(userLoginId);
    }

    public Optional<MilyUser> findByUserEmail(String userEmail) {
        return milyUserRepository.findByUserEmail(userEmail);
    }

    public Optional<MilyUser> findByUserPhoneNumber(String userPhoneNumber) {
        return milyUserRepository.findByUserPhoneNumber(userPhoneNumber);
    }

    public Optional<MilyUser> findById(long id) {
        return milyUserRepository.findById(id);
    }

    public RsData checkUserLoginIdDup (String userLoginId) {
        if ( findByUserLoginId(userLoginId).isPresent() ) return RsData.of("F-1", "%s(은)는 이미 사용 중인 아이디입니다.".formatted(userLoginId));

        return RsData.of("S-1", "%s(은)는 사용 가능한 아이디입니다.".formatted(userLoginId));
    }

    public RsData checkUserEmailDup (String userEmail) {
        if ( findByUserEmail(userEmail).isPresent() ) return RsData.of("F-1", "%s(은)는 이미 인증 된 이메일입니다.".formatted(userEmail));

        return RsData.of("S-1", "%s(은)는 사용 가능한 이메일입니다.".formatted(userEmail));
    }

    public RsData checkUserPhoneNumberDup (String userPhoneNumber) {
        if ( findByUserPhoneNumber(userPhoneNumber).isPresent() ) return RsData.of("F-1", "%s(은)는 이미 인증 된 전화번호입니다.".formatted(userPhoneNumber));

        return RsData.of("S-1", "%s(은)는 사용 가능한 전화번호입니다.".formatted(userPhoneNumber));
    }

    @Transactional
    public Estimate sendEstimate(FirstCategory firstCategory, SecondCategory secondCategory, String area, String body, MilyUser milyUser) {
        Estimate estimate = new Estimate();
        estimate.setFirstCategory(firstCategory);
        estimate.setSecondCategory(secondCategory);
        estimate.setBody(body);
        estimate.setArea(area);
        estimate.setMilyUser(milyUser);
        estimate.setCreateDate(LocalDateTime.now());
        return estimateRepository.save(estimate);
    }

    @Transactional
    public Estimate sevenCreateEstimate(FirstCategory firstCategory, SecondCategory secondCategory, String area, String body, MilyUser milyUser) {
        Estimate estimate = new Estimate();
        estimate.setFirstCategory(firstCategory);
        estimate.setSecondCategory(secondCategory);
        estimate.setArea(milyUser.getUserName());
        estimate.setBody(body);
        estimate.setMilyUser(milyUser);
        estimate.setCreateDate(LocalDateTime.now().minusDays(7));
        return estimateRepository.save(estimate);
    }

    public MilyUser getUser(String userName) {
        Optional<MilyUser> milyUser = milyUserRepository.findByUserName(userName);
        if (milyUser.isPresent()) {
            return milyUser.get();
        } else {
            throw new Ut.DataNotFoundException("유저 정보가 없습니다.");
        }
    }

    public boolean isAdmin(String userLoginId) {
        return milyUserRepository.findByUserLoginId(userLoginId)
                .map(MilyUser::getUserLoginId)
                .filter(loginId -> loginId.equals("admin999"))
                .isPresent();
    }

    public List<MilyUser> getWaitingLawyerList() {
        List<MilyUser> lawyerUsers = milyUserRepository.findByRole("waiting");
        if (lawyerUsers.isEmpty()) {
            throw new Ut.DataNotFoundException("승인 대기중인 변호사 목록이 없습니다.");
        }
        return lawyerUsers;
    }

    @Transactional
    public void approveLawyer(long id, String userLoginId) {
        if (!isAdmin(userLoginId)) {
            throw new Ut.UnauthorizedException("승인 권한이 없습니다.");
        }

        Optional<MilyUser> optionalLawyer = milyUserRepository.findById(id);

        if (optionalLawyer.isPresent()) {
            MilyUser lawyer = optionalLawyer.get();
            if ("waiting".equals(lawyer.getRole())) {
                lawyer.setRole("approve");
                milyUserRepository.save(lawyer);
            } else {
                throw new Ut.InvalidDataException("선택된 변호사는 대기 중인 상태가 아닙니다.");
            }
        } else {
            throw new Ut.DataNotFoundException("변호사를 찾을 수 없습니다.");
        }
    }

    public MilyUser findUserLoginIdByEmail(String userEmail) {
        return milyUserRepository.findUserLoginIdByEmail(userEmail);
    }

    public Optional<MilyUser> findByuserLoginIdAndEmail(String userLoginId, String email) {
        return milyUserRepository.findByUserLoginIdAndUserEmail(userLoginId, email);
    }

    @Transactional
    public void sendTempPasswordToEmail(MilyUser member) {
        // 임시 비밀번호 생성
        String tempPassword = getTempPassword();

        // 사용자 이메일로 임시 비밀번호 전송
        emailService.send(member.getUserEmail(), "임시 비밀번호 입니다.", "임시 비밀 번호 : " + tempPassword);

        // 데이터베이스에서 사용자의 비밀번호를 임시 비밀번호로 업데이트
        updateUserPassword(member, tempPassword);
    }

    @Transactional
    public void updateUserPassword(MilyUser user, String tempPassword) {
        // 데이터베이스에서 특정 사용자의 비밀번호를 주어진 임시 비밀번호로 업데이트
        user.setUserPassword(passwordEncoder.encode(tempPassword));
        milyUserRepository.save(user);
    }

    public String getTempPassword(){
        // 임시 비밀번호를 위한 문자들 정렬
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        StringBuilder str = new StringBuilder();

        // 10자리 임시 비밀번호 생성
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
//            str += charSet[idx];
            str.append(charSet[idx]);
        }
        return str.toString();
    }

    public MilyUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User user) {
            return milyUserRepository.findByUserLoginId(user.getUsername()).orElse(null);
        }
        return null;
    }

    @Transactional
    public RsData<MilyUser> getPoint(MilyUser isLoginedUser, String orderName) {
        // isLoginedUser 의 milyPoint 값을 가져온다.
        int milyPoint = isLoginedUser.getMilyPoint();

        // 가져온 milyPoint 값에 orderName을 더한다.
        milyPoint += Integer.parseInt(orderName.substring(7));

        // repository에 저장한다.
        isLoginedUser.setMilyPoint(milyPoint);
        milyUserRepository.save(isLoginedUser);

        return RsData.of("S-1", "포인트 지급", null);
    }

    public MilyUser getLawyer(String UserLoginId, String role) {
        Optional<MilyUser> lawyerUser = milyUserRepository.findByUserLoginIdAndRole(UserLoginId, role);
        if (lawyerUser.isPresent()) {
            return lawyerUser.get();
        } else {
            throw new Ut.DataNotFoundException("변호사 정보가 없습니다.");
        }
    }

    public MilyUser getLawyer(Long id) {
        Optional<MilyUser> lawyerUser = milyUserRepository.findById(id);
        if(lawyerUser.isPresent()) {
            return lawyerUser.get();
        }
        else {
            throw new Ut.DataNotFoundException("변호사 정보가 없습니다.");
        }
    }

    public LawyerUser isLawyer(MilyUser milyUser) {
        if(milyUser.getRole().equals("approve")) {
            return milyUser.lawyerUser;
        } else {
            throw new Ut.DataNotFoundException("변호사 정보가 없습니다.");
        }
    }

    public List<Estimate> getEstimate(LocalDateTime localDateTime, FirstCategory firstCategory, String area) {
        List<Estimate> estimate = findDataWithin7DaysByLocationAndCategory(localDateTime, area, firstCategory);
        if (!estimate.isEmpty()) {
            return estimate;
        } else {
            List<Estimate> estimateArea = findDataWithin7DaysByLocation(area);
            if (!estimateArea.isEmpty()) {
                return estimateArea;
            } else {
                throw new Ut.DataNotFoundException("견적서에 해당되는 변호사가 없습니다.");
            }
        }
    }

    public List<Estimate> findDataWithin7DaysByLocationAndCategory(LocalDateTime localDateTime, String area, FirstCategory firstCategory) {
        LocalDateTime sevenDaysAgo = localDateTime.minusDays(7);
        return estimateRepository.findByCreateDateGreaterThanEqualAndAreaAndFirstCategory(sevenDaysAgo, area, firstCategory);
    }

    public List<Estimate> findDataWithin7DaysByLocation(String area) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return estimateRepository.findByCreateDateGreaterThanEqualAndArea(sevenDaysAgo, area);
    }

    public String maskEmail (String email) {
        int atIndex = email.indexOf("@");

        if (atIndex == -1) {
            return email;
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        int dotIndex = domainPart.indexOf(".");

        if (localPart.length() > 4) {
            localPart = localPart.substring(0, localPart.length() - 4) + "****";
        }

        if (dotIndex > 2) {
            domainPart = domainPart.substring(0, 2) + "***" + domainPart.substring(dotIndex);
        }

        return localPart + "@" + domainPart;
    }

    private void saveProfileImg(MilyUser milyUser, MultipartFile profileImg) {
        if (profileImg == null) return;
        if (profileImg.isEmpty()) return;

        String profileImgFilePath = Ut.file.toFile(profileImg, AppConfig.getTempDirPath());

        saveProfileImg(milyUser, profileImgFilePath);
    }

    private void saveProfileImg(MilyUser milyUser, String profileImgFilePath) {
        if (Ut.str.isBlank(profileImgFilePath)) return;
        imageService.save(milyUser.getUserLoginId(), milyUser.getId(), "common", "profileImg", 1, profileImgFilePath);
    }

    public void editPassword(MilyUser isLoginedUser, String passwordConfirm) {
        isLoginedUser.setUserPassword(passwordEncoder.encode(passwordConfirm));
        milyUserRepository.save(isLoginedUser);
    }

    public void withdraw(MilyUser isLoginedUser) {
        milyUserRepository.delete(isLoginedUser);
    }

    public MilyUser editInformation(MilyUser isLoginedUser, String email, String phoneNum) {
        isLoginedUser.setUserEmail(email);
        isLoginedUser.setUserPhoneNumber(phoneNum);

        milyUserRepository.save(isLoginedUser);

        return isLoginedUser;
    }

    public String getProfileImgUrl(LawyerUser lawyerUser) {
        return Optional.ofNullable(lawyerUser)
                .flatMap(this::findProfileImgUrl)
                .orElse("https://placehold.co/30x30?text=UU");
    }

    public Optional<String> findProfileImgUrl(LawyerUser lawyerUser) {
        return imageService.findBy(
                        lawyerUser.milyUser.getUserLoginId(), lawyerUser.milyUser.getId(), "common", "profileImg", 1
                )
                .map(Image::getUrl);
    }

    public List<MilyUser> findAll() { return milyUserRepository.findAll(); }

    public List<MilyUser> findAllApproveLawyer(String role) {
        return milyUserRepository.findByRole(role);
    }

    // area랑 role 참조해서 List 반환
    public List<LawyerUser> findByAreaAndRole(String role, String area) {
        return lawyerUserRepository.findByAreaAndMilyUserRole(area, role);
    }
}