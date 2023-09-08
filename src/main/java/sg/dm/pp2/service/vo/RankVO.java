package sg.dm.pp2.service.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RankVO {
    private int rankUid;
    private int univUid;
    private String studentIdPivot;
    private int memberCount;
}
