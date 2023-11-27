package com.mily.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByRelTypeCodeAndRelIdOrderByTypeCodeAscType2CodeAscFileNoAsc(String relTypeCode, Long relId);

    Optional<Image> findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(String relTypeCode, long relId, String typeCode, String type2Code, long fileNo);

    List<Image> findByRelTypeCodeAndRelIdInOrderByTypeCodeAscType2CodeAscFileNoAsc(String relTypeCode, long[] relIds);

    List<Image> findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeOrderByFileNoAsc(String relTypeCode, long relId, String typeCode, String type2Code);

    List<Image> findByRelTypeCodeAndRelId(String relTypeCode, long relId);

    Optional<Image> findTop1ByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeOrderByFileNoDesc(String relTypeCode, Long relId, String typeCode, String type2Code);

    List<Image> findByRelTypeCodeAndTypeCodeAndType2Code(String relTypeCode, String typeCode, String type2Code);

    List<Image> findByRelTypeCode(String relTypeCode);

    List<Image> findByRelTypeCodeAndCreateDateBefore(String relTypeCode, LocalDateTime dateTime);
}
