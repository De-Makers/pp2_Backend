package sg.dm.pp2.service.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UnivEmailDomainDetailVO {
    private int domainUid;
    private String domain;
}
